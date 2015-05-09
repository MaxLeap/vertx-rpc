package as.leap.rpc.example.impl;

import as.leap.rpc.example.spi.Department;
import as.leap.rpc.example.spi.SampleTimeoutRetrySPI;
import as.leap.rpc.example.spi.User;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.junit.Assert;

/**
 * Created by stream.
 */
public class SampleTimeoutRetryServiceImpl implements SampleTimeoutRetrySPI {

  private int requestCount = 0;
  private long startTime = System.currentTimeMillis();
  @Override
  public void getDepartment(User user, Handler<AsyncResult<Department>> handler) {
    System.out.println("request count " + requestCount);
    int oldRequestCount = requestCount++;
    Assert.assertEquals(1, user.getId());
    Assert.assertEquals("name", user.getName());
    Assert.assertTrue(requestCount - oldRequestCount == 1);

    if (requestCount > 1) {
      Assert.assertTrue(System.currentTimeMillis() - startTime >= 1000);
      startTime = System.currentTimeMillis();
    }

    if (requestCount == 3) {
      Department department = new Department();
      department.setId(1);
      department.setName("research");
      handler.handle(Future.succeededFuture(department));
    }
  }
}
