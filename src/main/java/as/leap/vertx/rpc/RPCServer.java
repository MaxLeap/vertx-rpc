package as.leap.vertx.rpc;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Created by stream.
 */
public interface RPCServer {

  default void shutdown() {
    shutdown(null);
  }

  void shutdown(Handler<AsyncResult<Void>> handler);

}
