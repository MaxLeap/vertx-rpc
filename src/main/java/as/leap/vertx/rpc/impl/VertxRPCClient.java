package as.leap.vertx.rpc.impl;

import as.leap.vertx.rpc.RPCClient;
import as.leap.vertx.rpc.RPCHook;
import as.leap.vertx.rpc.RequestProp;
import as.leap.vertx.rpc.VertxRPCException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;
import io.vertx.core.http.CaseInsensitiveHeaders;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public class VertxRPCClient<T> extends RPCBase implements InvocationHandler, RPCClient<T> {
  private Class<T> service;
  private Vertx vertx;
  private RPCClientOptions options;
  private String serviceAddress;
  private long timeout;

  public VertxRPCClient(RPCClientOptions<T> options) {
    super(options.getWireProtocol());
    this.options = options;
    this.vertx = options.getVertx();
    this.timeout = options.getTimeout();
    this.serviceAddress = options.getBusAddress();
    this.service = options.getServiceClass();
    checkBusAddress(serviceAddress);
    Objects.requireNonNull(service, "service's interface can not be null.");
  }

  public T bindService() {
    return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service}, this);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    String serviceName = service.getCanonicalName();
    //args
    RPCRequest request = new RPCRequest();
    request.setServiceName(serviceName);
    request.setMethodName(method.getName());
    List<Class<?>> argsClass = Stream.of(method.getParameterTypes())
        .filter(argClass -> !argClass.isAssignableFrom(Handler.class))
        .collect(Collectors.toList());

    List<String> argsClassName = argsClass.stream().map(clazz -> {
      if (isWrapType(clazz)) {
        return WrapperType.class.getName();
      } else {
        return clazz.getName();
      }
    }).collect(Collectors.toList());

    List<Object> argList = new ArrayList<>();
    for (int index = 0; index < argsClass.size(); index++) {
      Optional<Object> argOptional = Optional.ofNullable(args[index]);
      byte[] argBytes;
      if (argOptional.isPresent()) {
        argList.add(argsClassName.get(index));
        Class<?> argClass = argsClass.get(index);
        argBytes = asBytes(argOptional.get(), argClass);
      } else {
        //the argument is null, so we have to wrap it.
        argList.add(WrapperType.class.getName());
        argBytes = asBytes(new WrapperType(null, argsClass.get(index)), WrapperType.class);
      }
      argList.add(argBytes);
    }
    request.setArgs(argList);

    CallbackType callbackType = getCallbackType(method.getReturnType());
    RequestProperties requestProperties = extractRequestProp(method);
    switch (callbackType) {
      case REACTIVE:
        return Observable.create(new ReactiveHandler<Object>() {
          @Override
          void execute() throws Exception {
            invoke(request, args, requestProperties, callbackType, this);
          }
        });
      case ASYNC_HANDLER:
        Handler<AsyncResult<Object>> handler = (Handler<AsyncResult<Object>>) args[args.length - 1];
        invoke(request, args, requestProperties, callbackType, handler);
        return null;
      case COMPLETABLE_FUTURE:
        CompletableFutureHandler<Object> futureHandler = new CompletableFutureHandler<>();
        invoke(request, args, requestProperties, callbackType, futureHandler);
        return futureHandler.future;
      default:
        throw new VertxRPCException("unKnow the type of callback");
    }
  }

  private CallbackType getCallbackType(Class<?> returnType) {
    if (Observable.class.isAssignableFrom(returnType)) {
      return CallbackType.REACTIVE;
    } else if (CompletableFuture.class.isAssignableFrom(returnType)) {
      return CallbackType.COMPLETABLE_FUTURE;
    } else if (void.class.equals(returnType)) {
      return CallbackType.ASYNC_HANDLER;
    } else {
      throw new VertxRPCException("unKnow the type of callback, for now, we just support Obserable CompletableFuture and Handler of vert.x");
    }
  }

  private static abstract class ReactiveHandler<T> implements Observable.OnSubscribe<T>, Handler<AsyncResult<T>> {
    private Observer<? super T> observer;

    @Override
    public void handle(AsyncResult<T> event) {
      if (event.succeeded()) {
        fireNext(event.result());
      } else {
        fireError(event.cause());
      }
    }

    protected void fireNext(T next) {
      if (observer != null) observer.onNext(next);
    }

    protected void fireError(Throwable t) {
      if (observer != null) observer.onError(t);
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
      this.observer = subscriber;
      try {
        execute();
      } catch (Exception e) {
        fireError(e);
      }
    }

    abstract void execute() throws Exception;
  }

  private static class CompletableFutureHandler<T> implements Handler<AsyncResult<T>> {
    private CompletableFuture<T> future = new CompletableFuture<>();

    @Override
    public void handle(AsyncResult<T> event) {
      if (event.succeeded()) {
        future.complete(event.result());
      } else {
        future.completeExceptionally(event.cause());
      }
    }
  }

  private <E> void invoke(RPCRequest request, Object[] args, RequestProperties requestProp, CallbackType callBackType, Handler<AsyncResult<E>> responseHandler) throws Exception {
    final DeliveryOptions deliveryOptions = new DeliveryOptions();
    deliveryOptions.setSendTimeout(requestProp.getTimeout());
    deliveryOptions.setHeaders(new CaseInsensitiveHeaders());
    //
    byte[] requestBytes = asBytes(request);

    //execute hook before send message
    vertx.executeBlocking(future -> {
      options.getRpcHook().beforeHandler(request.getServiceName(), request.getMethodName(), args, deliveryOptions.getHeaders());
      future.complete();
    }, false, event -> {
      deliveryOptions.addHeader(CALLBACK_TYPE, callBackType.name());
      ReplyHandler<E> replyHandler = new ReplyHandler<>(requestProp.getRetryTimes(), 0, requestBytes, deliveryOptions, responseHandler);
      vertx.eventBus().send(serviceAddress, requestBytes, deliveryOptions, replyHandler);
    });
  }

  /**
   * EventBus reply Handler
   *
   * @param <E>
   */
  private class ReplyHandler<E> implements Handler<AsyncResult<Message<byte[]>>> {
    private int retryTimes;
    private int currentRetryTimes;
    private byte[] requestBytes;
    private DeliveryOptions deliveryOptions;
    private Handler<AsyncResult<E>> responseHandler;

    public ReplyHandler(int retryTimes, int currentRetryTimes, byte[] requestBytes, DeliveryOptions deliveryOptions, Handler<AsyncResult<E>> responseHandler) {
      this.retryTimes = retryTimes;
      this.currentRetryTimes = currentRetryTimes;
      this.requestBytes = requestBytes;
      this.deliveryOptions = deliveryOptions;
      this.responseHandler = responseHandler;
    }

    @Override
    public void handle(AsyncResult<Message<byte[]>> message) {
      //get hook and remove key of callback type.
      RPCHook RPCHook = options.getRpcHook();
      //for retry
      final String callBackType = deliveryOptions.getHeaders().get(CALLBACK_TYPE);
      deliveryOptions.getHeaders().remove(CALLBACK_TYPE);

      if (message.succeeded()) {
        try {
          RPCResponse response = asObject(message.result().body(), RPCResponse.class);
          String responseTypeName = response.getResponseTypeName();
          byte[] responseBytes = response.getResponse();
          Object result = asObject(responseBytes, (Class<E>) Class.forName(responseTypeName));
          E realResult = (E) (result instanceof WrapperType ? ((WrapperType) result).getValue() : result);
          //execute hook after handler message
          responseHandler.handle(Future.succeededFuture(realResult));
          vertx.runOnContext(aVoid -> vertx.executeBlocking(future -> {
            RPCHook.afterHandler(realResult, deliveryOptions.getHeaders());
            future.complete();
          }, false, null));
        } catch (Exception e) {
          responseHandler.handle(Future.failedFuture(new VertxRPCException(e)));
        }
      } else {
        //filter timeout exception
        Throwable throwable = message.cause();
        if (throwable instanceof ReplyException && ((ReplyException) throwable).failureType() == ReplyFailure.TIMEOUT && currentRetryTimes < retryTimes) {
          this.currentRetryTimes++;
          deliveryOptions.addHeader(CALLBACK_TYPE, callBackType);
          vertx.eventBus().send(serviceAddress, requestBytes, deliveryOptions, this);
        } else if (throwable instanceof ReplyException && ((ReplyException) throwable).failureType() == ReplyFailure.RECIPIENT_FAILURE) {
          Exception t = getThrowable(throwable.getMessage());
          responseHandler.handle(Future.failedFuture(t));
          vertx.runOnContext(aVoid -> vertx.executeBlocking(future -> {
            RPCHook.afterHandler(t, deliveryOptions.getHeaders());
            future.complete();
          }, false, null));
        } else {
          responseHandler.handle(Future.failedFuture(throwable));
          vertx.runOnContext(aVoid -> vertx.executeBlocking(future -> {
            RPCHook.afterHandler(throwable, deliveryOptions.getHeaders());
            future.complete();
          }, false, null));
        }
      }
    }
  }

  private <EX extends Exception> EX getThrowable(String messExceptionString) {
    String[] messages = messExceptionString.split("\\|", 2);
    String exceptionClass = messages[0];
    String message = messages[1];
    try {
      Class<EX> clazz = (Class<EX>) Class.forName(exceptionClass);
      return clazz.getConstructor(String.class).newInstance(message);
    } catch (NoSuchMethodException | ClassNotFoundException e) {
      return (EX) new VertxRPCException("Invoke method " + message + " throw exception " + exceptionClass);
    } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
      throw new VertxRPCException(e);
    }
  }

  private RequestProperties extractRequestProp(Method method) {
    return Optional.ofNullable(method.getAnnotation(RequestProp.class))
        .map(requestProp -> {
          RequestProperties requestProperties = new RequestProperties();
          requestProperties.setTimeout(requestProp.timeout() == 0 ? timeout : requestProp.timeUnit().toMillis(requestProp.timeout()));
          requestProperties.setRetryTimes(requestProp.retry());
          return requestProperties;
        }).orElse(new RequestProperties(timeout));
  }

  private static class RequestProperties {
    private long timeout;
    private int retryTimes = 0;

    public RequestProperties() {
    }

    public RequestProperties(long timeout) {
      this.timeout = timeout;
    }

    public void setTimeout(long timeout) {
      this.timeout = timeout;
    }

    public void setRetryTimes(int retryTimes) {
      this.retryTimes = retryTimes;
    }

    public long getTimeout() {
      return timeout;
    }

    public int getRetryTimes() {
      return retryTimes;
    }
  }
}
