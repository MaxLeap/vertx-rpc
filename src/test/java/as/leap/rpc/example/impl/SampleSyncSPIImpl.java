package as.leap.rpc.example.impl;

import as.leap.rpc.example.spi.*;
import co.paralleluniverse.fibers.Suspendable;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stream.
 */
public class SampleSyncSPIImpl implements SampleSyncSPI {

  @Suspendable
  @Override
  public Department getDepartment(User user) {
    Assert.assertEquals(1, user.getId());
    Assert.assertEquals("name", user.getName());

    Department department = new Department();
    department.setId(1);
    department.setName("research");
    return department;
  }

  @Suspendable
  @Override
  public Integer getDepartment(int userId, Integer anotherId) {
    Assert.assertEquals(1, userId);
    Assert.assertEquals(2, anotherId.intValue());
    return 1;
  }

  @Suspendable
  @Override
  public byte[] getBytes(byte[] args) {
    Assert.assertArrayEquals("name".getBytes(), args);

    return args;
  }

  @Suspendable
  @Override
  public List<Department> getDepartList(List<User> users) {
    Assert.assertEquals(1, users.get(0).getId());

    List<Department> departments = new ArrayList<>();
    Department department = new Department();
    department.setId(1);
    department.setName("research");
    departments.add(department);

    return departments;
  }

  @Suspendable
  @Override
  public Map<String, Department> getDepartMap(Map<String, User> userMap) {
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

    return departmentMap;
  }

  @Suspendable
  @Override
  public Weeks getDayOfWeek(Weeks day) {
    Assert.assertEquals(Weeks.SUNDAY, day);

    return Weeks.FRIDAY;
  }

  @Suspendable
  @Override
  public User someException() {
    throw new MyException("illegalArguments");
  }

  @Suspendable
  @Override
  public User nullInvoke(User user) {
    Assert.assertNull(user);
    return null;
  }
}
