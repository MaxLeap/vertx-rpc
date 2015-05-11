package as.leap.rpc.example.impl;

import as.leap.rpc.example.spi.Department;
import as.leap.rpc.example.spi.SampleObserableSPI;
import as.leap.rpc.example.spi.User;
import as.leap.rpc.example.spi.Weeks;
import org.junit.Assert;
import rx.Observable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SampleObserableServiceImpl implements SampleObserableSPI {
  @Override
  public Observable<Department> getDepartment(User user) {
    Assert.assertEquals(1, user.getId());
    Assert.assertEquals("name", user.getName());

    Department department = new Department();
    department.setId(1);
    department.setName("research");
    return Observable.just(department);
  }

  @Override
  public Observable<Integer> getDepartment(int userId, Integer anotherId) {
    Assert.assertEquals(1, userId);
    Assert.assertEquals(2, anotherId.intValue());
    return Observable.just(1);
  }

  @Override
  public Observable<byte[]> getBytes(byte[] args) {
    Assert.assertArrayEquals("name".getBytes(), args);
    return Observable.just(args);
  }

  @Override
  public Observable<List<Department>> getDepartList(List<User> users) {
    Assert.assertEquals(1, users.get(0).getId());

    List<Department> departments = new ArrayList<>();
    Department department = new Department();
    department.setId(1);
    department.setName("research");
    departments.add(department);

    return Observable.just(departments);
  }

  @Override
  public Observable<Map<String, Department>> getDepartMap(Map<String, User> userMap) {
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

    return Observable.just(departmentMap);
  }

  @Override
  public Observable<Weeks> getDayOfWeek(Weeks day) {
    Assert.assertEquals(Weeks.SUNDAY, day);
    return Observable.just(Weeks.FRIDAY);
  }

  @Override
  public Observable<User> someException() {
    return Observable.error(new IllegalArgumentException("illegalArguments"));
  }

  @Override
  public Observable<User> nullInvoke(User user) {
    Assert.assertNull(user);
    return Observable.just(null);
  }
}
