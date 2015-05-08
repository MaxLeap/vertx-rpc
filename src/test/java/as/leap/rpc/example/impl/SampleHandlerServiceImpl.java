package as.leap.rpc.example.impl;

import as.leap.rpc.example.spi.Department;
import as.leap.rpc.example.spi.SampleHandlerSPI;
import as.leap.rpc.example.spi.User;
import as.leap.rpc.example.spi.Weeks;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class SampleHandlerServiceImpl implements SampleHandlerSPI {

  @Override
  public void getDepartment(User user, Handler<AsyncResult<Department>> handler) {
    Assert.assertEquals(1, user.getId());
    Assert.assertEquals("name", user.getName());

    Department department = new Department();
    department.setId(1);
    department.setName("research");
    handler.handle(Future.succeededFuture(department));
  }

  @Override
  public void getDepartment(int userId, Integer anotherId, Handler<AsyncResult<Integer>> handler) {
    Assert.assertEquals(1, userId);
    Assert.assertEquals(2, anotherId.intValue());
    handler.handle(Future.succeededFuture(1));
  }

  @Override
  public void getBytes(byte[] args, Handler<AsyncResult<byte[]>> handler) {
    Assert.assertArrayEquals("name".getBytes(), args);
    handler.handle(Future.succeededFuture("name".getBytes()));
  }

  @Override
  public void getDepartList(List<User> users, Handler<AsyncResult<List<Department>>> handler) {
    Assert.assertEquals(1, users.get(0).getId());

    List<Department> departments = new ArrayList<>();
    Department department = new Department();
    department.setId(1);
    department.setName("research");
    departments.add(department);

    handler.handle(Future.succeededFuture(departments));
  }

  @Override
  public void getDayOfWeek(Weeks day, Handler<AsyncResult<Weeks>> handler) {
    Assert.assertEquals(Weeks.SUNDAY, day);

    handler.handle(Future.succeededFuture(Weeks.FRIDAY));
  }

  @Override
  public void someException(Handler<AsyncResult<User>> handler) {
    handler.handle(Future.failedFuture(new IllegalArgumentException("illegalArguments")));
  }

  @Override
  public void nullInvoke(User user, Handler<AsyncResult<User>> handler) {
    Assert.assertNull(user);
    handler.handle(Future.succeededFuture(null));
  }

}
