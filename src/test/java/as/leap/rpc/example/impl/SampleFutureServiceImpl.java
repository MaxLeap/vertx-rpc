package as.leap.rpc.example.impl;

import as.leap.rpc.example.spi.*;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by stream.
 */
public class SampleFutureServiceImpl implements SampleFutureSPI {
  @Override
  public CompletableFuture<Department> getDepartment(User user) {
    Assert.assertEquals(1, user.getId());
    Assert.assertEquals("name", user.getName());

    Department department = new Department();
    department.setId(1);
    department.setName("research");

    return CompletableFuture.completedFuture(department);
  }

  @Override
  public CompletableFuture<Integer> getDepartment(int userId, Integer anotherId) {
    Assert.assertEquals(1, userId);
    Assert.assertEquals(2, anotherId.intValue());
    return CompletableFuture.completedFuture(1);
  }

  @Override
  public CompletableFuture<byte[]> getBytes(byte[] args) {
    Assert.assertArrayEquals("name".getBytes(), args);
    return CompletableFuture.completedFuture(args);
  }

  @Override
  public CompletableFuture<List<Department>> getDepartList(List<User> users) {
    Assert.assertEquals(1, users.get(0).getId());

    List<Department> departments = new ArrayList<>();
    Department department = new Department();
    department.setId(1);
    department.setName("research");
    departments.add(department);

    return CompletableFuture.completedFuture(departments);
  }

  @Override
  public CompletableFuture<Map<String, Department>> getDepartMap(Map<String, User> userMap) {
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

    return CompletableFuture.completedFuture(departmentMap);
  }

  @Override
  public CompletableFuture<Weeks> getDayOfWeek(Weeks day) {
    Assert.assertEquals(Weeks.SUNDAY, day);
    return CompletableFuture.completedFuture(Weeks.FRIDAY);
  }

  @Override
  public CompletableFuture<User> someException() {
    CompletableFuture<User> completableFuture = new CompletableFuture<>();
    completableFuture.completeExceptionally(new MyException("illegalArguments"));
    return completableFuture;
  }

  @Override
  public CompletableFuture<User> nullInvoke(User user) {
    Assert.assertNull(user);
    return CompletableFuture.completedFuture(null);
  }
}
