Vertx-RPC
=====

Wrap eventBus of vert.x 3 as trasport layer for RPC invoking, and arguments of method could be POJO or primitive.
You just have to define `interface` of service, and then implements it with service end.
Expose interface as client for invoker, say separation of interface and implementation.

How to using
=======

1.Define service interface.

    public interface MyService {
		void hello(String what, Handler<AsyncResult<String>> handler)
    }

2.Implements service with interface.

    public ExampleService implements MyService {
    	public void hello(String what, Handler<AsyncResult<String>> handler) {
			handler.handle(Future.succeededFuture("echo " + what));
        }
    }

3.Startup service on the server end.

    RPCServerOptions serverOption = new RPCServerOptions(vertx).setBusAddress("Address")
    .addService(new ExampleService());

    RPCServer rpcServer = new VertxRPCServer(serverOption);

4.Invoke service from client.

	RPCClientOptions<MyService> rpcClientOptions = new RPCClientOptions<MyService>(vertx)
        .setBusAddress("Address").setServiceClass(MyService.class);

    MyService myService = new VertxRPCClient(rpcClientOptions).bindService();

    //invoking service
    myService.hello("world", result -> {
		
    });

full example could be found [here]().

The more detail
=========

We only dependency `Protostuff` as Codec, default we using protobuf, you can also specify JSON as wire protocol both client and server.

    new RPCServerOptions(vertx).setWireProtocol(WireProtocol.JSON))
    new RPCClientOptions<MyService>(vertx).setWireProtocol(WireProtocol.JSON))	

We also support `Reactive` as return type, so you can define your interface as

`Observable<String> hello(String what)` instead of `void hello(String what, Handler<AsyncResult<String>> handler)`

or CompletableFuture

`CompletableFuture<String> hello(String what)` instead of `void hello(String what, Handler<AsyncResult<String>> handler)`

One more thing that about `timeout and retry`.
you can make annotation on your interface to define timeout for specify method and retry times.

    @RequestProp(timeout = 1, timeUnit = TimeUnit.SECONDS, retry = 2)
    void hello(String what, Handler<AsyncResult<String>> handler);

or

	@RequestProp(timeout = 1000) //default timeUnit is milliseconds
    void hello(String what, Handler<AsyncResult<String>> handler)

You can specify default timeout parameter in RPCClientOptions, if there are no RequestProp annotation on method, vertx-rpc will using
default timeout, annotation @RequestProp have highest priority.

`retry = 2` meaning that will repeat request at most 2 times after found Timeout Exception,
this is not include original request, so this would be throw Timeout Exception after try 3 times.

