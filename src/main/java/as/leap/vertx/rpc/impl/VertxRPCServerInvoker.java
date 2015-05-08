package as.leap.vertx.rpc.impl;


import as.leap.vertx.rpc.RPCServer;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.shareddata.LocalMap;
import rx.Observable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class VertxRPCServerInvoker implements RPCServer {
  private final LocalMap<String, SharedWrapper> serviceMapping;
  private final MessageConsumer<byte[]> consumer;

  public VertxRPCServerInvoker(RPCServerOptions options) {
    this.serviceMapping = options.getServiceMapping();
    this.consumer = options.getVertx().eventBus().consumer(options.getBusAddress());
    this.consumer.setMaxBufferedMessages(options.getMaxBufferedMessages());
    registryService();
  }

  private void registryService() {
    consumer.handler(message -> {
      try {
        RPCRequest request = Codec.asObject(message.body(), RPCRequest.class);
        VertxRPCServerInvoker.this.call(request, message);
      } catch (Exception e) {
        replyFail(e, message);
      }
    });
  }

  private <T> void call(RPCRequest request, Message<byte[]> message) {
    try {
      Object service = serviceMapping.get(request.getServiceName()).getValue();
      Class<?>[] argClasses = {};
      Object[] args = {};
      //args
      if (request.getArgs() != null && request.getArgs().size() > 0) {
        List<Object> argList = request.getArgs();
        int argCount = argList.size() / 2;
        args = new Object[argCount];
        argClasses = new Class[argCount];

        for (int i = 0; i < argList.size(); i += 2) {
          int index = i / 2;
          String argClassName = (String) argList.get(i);
          byte[] argBytes = (byte[]) argList.get(i + 1);

          Class<?> argClass = Class.forName(argClassName);
          Object arg = Codec.asObject(argBytes, argClass);
          //check type for get real class and real value
          if (arg instanceof WrapperType) {
            argClasses[index] = ((WrapperType) arg).getClazz();
            args[index] = ((WrapperType) arg).getValue();
          } else {
            argClasses[index] = arg.getClass();
            args[index] = arg;
          }
        }
      }
      try {
        //try obserable first
        Method method = service.getClass().getMethod(request.getMethodName(), argClasses);
        Observable<?> observable = (Observable) method.invoke(service, args);
        observable.subscribe(result -> replySuccess(result, message), ex -> replyFail(ex, message));
      } catch (NoSuchMethodException e) {
        //try handler
        argClasses = Arrays.copyOf(argClasses, argClasses.length + 1);
        argClasses[argClasses.length - 1] = Handler.class;
        args = Arrays.copyOf(args, args.length + 1);
        args[args.length - 1] = (Handler<AsyncResult<T>>) event -> {
          if (event.succeeded()) replySuccess(event.result(), message);
          else replyFail(event.cause(), message);
        };
        Method method = service.getClass().getMethod(request.getMethodName(), argClasses);
        method.invoke(service, args);
      }
    } catch (Exception e) {
      replyFail(e, message);
    }
  }

  private <T> void replySuccess(T result, Message<byte[]> message) {
    String resultClassName;
    byte[] resultBytes = {};
    try {
      if (Optional.ofNullable(result).isPresent()) {
        Class<?> resultClass = result.getClass();
        resultClassName = Codec.isWrapType(resultClass) ? WrapperType.class.getName() : resultClass.getName();
        resultBytes = Codec.asBytes(result);
      } else {
        // result is null, so we have wrap it.
        resultClassName = WrapperType.class.getName();
      }
      RPCResponse response = new RPCResponse(resultClassName, resultBytes);
      byte[] responseBytes = Codec.asBytes(response);
      message.reply(responseBytes);
    } catch (Exception e) {
      replyFail(e, message);
    }
  }

  private void replyFail(Throwable ex, Message<byte[]> message) {
    message.fail(500, ex.getMessage());
  }

  @Override
  public void shutdown(Handler<AsyncResult<Void>> handler) {
    if (Optional.ofNullable(handler).isPresent()) {
      consumer.unregister(handler);
    } else {
      consumer.unregister();
    }
  }
}
