package as.leap.vertx.rpc;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * RPC Server
 * Created by stream.
 */
public interface RPCServer {

  /**
   * shutdown eventbus.
   */
  default void shutdown() {
    shutdown(null);
  }

  /**
   * shutdown with callback handler
   *
   * @param handler Handler
   */
  void shutdown(Handler<AsyncResult<Void>> handler);

}
