package as.leap.rpc.example.spi;

import io.vertx.core.Future;

import java.util.List;
import java.util.Map;

/**
 * Created by stream.
 */
public interface SampleFutureSPI {

  //complex object
  Future<Department> getDepartment(User user);

  //primitive
  Future<Integer> getDepartment(int userId, Integer anotherId);

  //array
  Future<byte[]> getBytes(byte[] args);

  //Collection
  Future<List<Department>> getDepartList(List<User> users);

  //HashMap
  Future<Map<String, Department>> getDepartMap(Map<String, User> userMap);

  //enum
  Future<Weeks> getDayOfWeek(Weeks day);

  //exception and non-argument
  Future<User> someException();

  //both arg and the result is null;
  Future<User> nullInvoke(User user);

}
