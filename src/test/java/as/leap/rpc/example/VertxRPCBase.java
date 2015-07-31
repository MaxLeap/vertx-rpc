package as.leap.rpc.example;

import as.leap.rpc.example.spi.Department;
import as.leap.rpc.example.spi.MyException;
import as.leap.rpc.example.spi.User;
import as.leap.rpc.example.spi.Weeks;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import java.util.List;
import java.util.Map;

/**
 * Created by stream.
 */
abstract class VertxRPCBase {

  protected void assertOne(Department department, TestContext testContext, Async async) {
    testContext.assertEquals(1, department.getId());
    testContext.assertEquals("research", department.getName());
    async.complete();
  }

  protected void assertTwo(Integer departmentId, TestContext testContext, Async async) {
    testContext.assertEquals(1, departmentId);
    async.complete();
  }

  protected void assertThree(byte[] result, TestContext testContext, Async async) {
    testContext.assertEquals("name", new String(result));
    async.complete();
  }

  protected void assertFour(List<Department> result, TestContext testContext, Async async) {
    testContext.assertEquals(1, result.get(0).getId());
    async.complete();
  }

  protected void assertFive(Weeks day, TestContext testContext, Async async) {
    testContext.assertEquals(Weeks.FRIDAY, day);
    async.complete();
  }

  protected void assertSix(Throwable ex, TestContext testContext, Async async) {
    testContext.assertTrue(ex instanceof MyException);
    testContext.assertEquals(400, ((MyException) ex).getCode());
    testContext.assertEquals("illegalArguments", ex.getMessage());
    async.complete();
  }

  protected void assertSeven(User user, TestContext testContext, Async async) {
    testContext.assertNull(user);
    async.complete();
  }

  protected void assertEight(Map<String, Department> departmentMap, TestContext testContext, Async async) {
    testContext.assertNotNull(departmentMap);
    testContext.assertEquals(1, departmentMap.size());
    testContext.assertEquals("research", departmentMap.get("research").getName());
    async.complete();
  }
}
