package as.leap.vertx.rpc;

/**
 * RPC Client
 *
 * Created by stream.
 */
public interface RPCClient<T> {

  /**
   * bind the interface of service
   *
   * @return interface of SPI
   */
  T bindService();
}
