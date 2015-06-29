package as.leap.vertx.rpc.impl;


import as.leap.vertx.rpc.RPCHook;
import as.leap.vertx.rpc.WireProtocol;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;

import static as.leap.vertx.rpc.RPCHook.emptyRPCHook;

/**
 *
 */
public class RPCServerOptions {

  private Vertx vertx;
  private String busAddress;
  private int maxBufferedMessages;
  private static final String SERVICE_MAP_NAME = "VERTX_RPC_SERVICE";
  private RPCHook rpcHook = emptyRPCHook();
  private WireProtocol wireProtocol = WireProtocol.PROTOBUF;
  LocalMap<String, SharedWrapper> serviceMapping;

  public RPCServerOptions(Vertx vertx) {
    this.vertx = vertx;
    this.serviceMapping = vertx.sharedData().getLocalMap(SERVICE_MAP_NAME);
  }

  public RPCServerOptions(Vertx vertx, RPCServerOptions other) {
    this.vertx = vertx;
    this.busAddress = other.getBusAddress();
    this.wireProtocol = other.getWireProtocol();
    this.rpcHook = other.getRpcHook();
    this.serviceMapping = vertx.sharedData().getLocalMap(SERVICE_MAP_NAME);
  }

  LocalMap<String, SharedWrapper> getServiceMapping() {
    return serviceMapping;
  }

  public RPCServerOptions addService(Object service) {
    serviceMapping.put(service.getClass().getInterfaces()[0].getCanonicalName(), new SharedWrapper<>(service));
    return this;
  }

  public int getMaxBufferedMessages() {
    return maxBufferedMessages;
  }

  public RPCServerOptions setMaxBufferedMessages(int maxBufferedMessages) {
    this.maxBufferedMessages = maxBufferedMessages;
    return this;
  }

  public Vertx getVertx() {
    return vertx;
  }

  public RPCServerOptions setBusAddress(String busAddress) {
    this.busAddress = busAddress;
    return this;
  }

  public RPCHook getRpcHook() {
    return rpcHook;
  }

  public RPCServerOptions setRpcHook(RPCHook rpcHook) {
    this.rpcHook = rpcHook;
    return this;
  }

  public String getBusAddress() {
    return busAddress;
  }

  public WireProtocol getWireProtocol() {
    return wireProtocol;
  }

  public RPCServerOptions setWireProtocol(WireProtocol wireProtocol) {
    this.wireProtocol = wireProtocol;
    return this;
  }

}
