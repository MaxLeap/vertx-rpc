package as.leap.vertx.rpc;

import io.vertx.core.Vertx;

/**
 * Created by stream.
 */
public class RPCClientOptions<T> {

  private Vertx vertx;
  private String busAddress;
  private Class<T> serviceClass;
  private long timeout = 20 * 1000L;

  public RPCClientOptions(Vertx vertx) {
    this.vertx = vertx;
  }

  public RPCClientOptions(Vertx vertx, RPCClientOptions<T> other) {
    this.vertx = vertx;
    this.busAddress = other.getBusAddress();
    this.timeout = other.getTimeout();
    this.serviceClass = other.getServiceClass();
  }

  public RPCClientOptions<T> setBusAddress(String busAddress) {
    this.busAddress = busAddress;
    return this;
  }

  public RPCClientOptions<T> setTimeout(long timeout) {
    this.timeout = timeout;
    return this;
  }

  public RPCClientOptions<T> setServiceClass(Class<T> serviceClass) {
    this.serviceClass = serviceClass;
    return this;
  }

  public Vertx getVertx() {
    return vertx;
  }

  public String getBusAddress() {
    return busAddress;
  }

  public long getTimeout() {
    return timeout;
  }

  public Class<T> getServiceClass() {
    return serviceClass;
  }
}
