package as.leap.rpc.example.impl;

import as.leap.rpc.example.spi.*;
import io.vertx.core.Future;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stream.
 */
public class SampleFutureServiceImpl implements SampleFutureSPI {

  @Override
  public Future<Department> getDepartment(User user) {
    Assert.assertEquals(1, user.getId());
    Assert.assertEquals("name", user.getName());

    Department department = new Department();
    department.setId(1);
    department.setName("research");

    return Future.succeededFuture(department);
  }

  @Override
  public Future<Integer> getDepartment(int userId, Integer anotherId) {
    Assert.assertEquals(1, userId);
    Assert.assertEquals(2, anotherId.intValue());
    return Future.succeededFuture(1);
  }

  @Override
  public Future<byte[]> getBytes(byte[] args) {
    Assert.assertArrayEquals("name".getBytes(), args);
    return Future.succeededFuture(args);
  }

  @Override
  public Future<List<Department>> getDepartList(List<User> users) {
    Assert.assertEquals(1, users.get(0).getId());

    List<Department> departments = new ArrayList<>();
    Department department = new Department();
    department.setId(1);
    department.setName("research");
    departments.add(department);

    return Future.succeededFuture(departments);
  }

  @Override
  public Future<Map<String, Department>> getDepartMap(Map<String, User> userMap) {
    Assert.assertNotNull(userMap);
    Assert.assertEquals(1, userMap.size());
    User user = userMap.get("name");
    Assert.assertEquals(1, user.getId());
    Assert.assertEquals("name", user.getName());

    Map<String, Department> departmentMap = new HashMap<>();
    Department department = new Department();
    department.setId(1);
    department.setName("research");
    departmentMap.put("research", department);

    return Future.succeededFuture(departmentMap);
  }

  @Override
  public Future<Weeks> getDayOfWeek(Weeks day) {
    Assert.assertEquals(Weeks.SUNDAY, day);
    return Future.succeededFuture(Weeks.FRIDAY);
  }

  @Override
  public Future<User> someException() {
    return Future.failedFuture(new MyException("illegalArguments"));
  }

  @Override
  public Future<User> nullInvoke(User user) {
    Assert.assertNull(user);
    return Future.succeededFuture(null);
  }
}
