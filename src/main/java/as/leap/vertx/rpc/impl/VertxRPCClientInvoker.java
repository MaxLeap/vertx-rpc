package as.leap.vertx.rpc.impl;

import as.leap.vertx.rpc.RPCClient;
import as.leap.vertx.rpc.VertxRPCException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public class VertxRPCClientInvoker<T> implements InvocationHandler, RPCClient<T> {
  private Class<T> service;
  private Vertx vertx;
  private String serviceAddress;
  private long timeout;

  public VertxRPCClientInvoker(RPCClientOptions<T> options) {
    this.vertx = options.getVertx();
    this.timeout = options.getTimeout();
    this.serviceAddress = options.getBusAddress();
    this.service = options.getServiceClass();
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
      if (Codec.isWrapType(clazz)) {
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
        argBytes = Codec.asBytes(argOptional.get(), argClass);
      } else {
        //the argument is null, so we have to wrap it.
        argList.add(WrapperType.class.getName());
        argBytes = Codec.asBytes(new WrapperType(null, argsClass.get(index)), WrapperType.class);
      }
      argList.add(argBytes);
    }

    request.setArgs(argList);
    if (method.getReturnType().equals(void.class)) {
      Handler<AsyncResult<Object>> handler = (Handler<AsyncResult<Object>>) args[args.length - 1];
      invoke(request, handler);
      return null;
    } else {
      return Observable.create(new ResponseHandler<Object>() {
        @Override
        void execute() throws Exception {
          invoke(request, this);
        }
      });
    }
  }

  private static abstract class ResponseHandler<T> implements Observable.OnSubscribe<T>, Handler<AsyncResult<T>> {
    private Observer<? super T> observer;

    @Override
    public void handle(AsyncResult<T> event) {
      if (event.succeeded()) {
        fireNext(event.result());
      } else {
        fireError(new VertxRPCException(event.cause()));
      }
    }

    protected void fireNext(T next) {
      if (observer != null) observer.onNext(next);
    }

    protected void fireError(Throwable t) {
      if (observer != null) observer.onError(t.getCause());
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

  private <E> void invoke(RPCRequest request, Handler<AsyncResult<E>> responseHandler) throws Exception {
    Handler<AsyncResult<Message<byte[]>>> messageHandler = message -> {
      if (message.succeeded()) {
        try {
          RPCResponse response = Codec.asObject(message.result().body(), RPCResponse.class);
          String responseTypeName = response.getResponseTypeName();
          byte[] responseBytes = response.getResponse();
          Object result = Codec.asObject(responseBytes, (Class<E>) Class.forName(responseTypeName));
          E realResult = (E) (result instanceof WrapperType ? ((WrapperType) result).getValue() : result);
          responseHandler.handle(Future.succeededFuture(realResult));
        } catch (Exception e) {
          responseHandler.handle(Future.failedFuture(new VertxRPCException(e)));
        }
      } else {
        responseHandler.handle(Future.failedFuture(message.cause()));
      }
    };
    DeliveryOptions deliveryOptions = new DeliveryOptions();
    deliveryOptions.setSendTimeout(timeout);
    byte[] requestBytes = Codec.asBytes(request);
    vertx.eventBus().send(serviceAddress, requestBytes, deliveryOptions, messageHandler);
  }
}
