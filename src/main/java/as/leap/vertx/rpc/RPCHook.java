package as.leap.vertx.rpc;

import io.vertx.core.MultiMap;

/**
 * All the hook will be running with worker thread.
 * Since we assume your code would be execute some blocking method, eg: logger, generate UUID and
 * get timeStamp from System.
 * Created by stream.
 */
public interface RPCHook {

  /**
   * @param interfaceName target interface
   * @param methodName    method name
   * @param args          args
   * @param header        header for deliverOption
   */
  void beforeHandler(String interfaceName, String methodName, Object[] args, MultiMap header);

  /**
   * @param response return result
   * @param header   header for deliverOption in request
   */
  void afterHandler(Object response, MultiMap header);

  /**
   * @param throwable throwable
   * @param header    header for deliverOption in request
   */
  void afterHandler(Throwable throwable, MultiMap header);

}
