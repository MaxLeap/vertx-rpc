package as.leap.rpc.example.spi;

import as.leap.vertx.rpc.RequestProp;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.concurrent.TimeUnit;

/**
 * Created by stream
 */
public interface SampleTimeoutRetrySPI {

  @RequestProp(timeout = 1, timeUnit = TimeUnit.SECONDS, retry = 2)
  void getDepartment(User user, Handler<AsyncResult<Department>> handler);



}
