package as.leap.rpc.example.spi;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by stream.
 */
public interface SampleCompletableFutureSPI {

  //complex object
  CompletableFuture<Department> getDepartment(User user);

  //primitive
  CompletableFuture<Integer> getDepartment(int userId, Integer anotherId);

  //array
  CompletableFuture<byte[]> getBytes(byte[] args);

  //Collection
  CompletableFuture<List<Department>> getDepartList(List<User> users);

  //HashMap
  CompletableFuture<Map<String, Department>> getDepartMap(Map<String, User> userMap);

  //enum
  CompletableFuture<Weeks> getDayOfWeek(Weeks day);

  //exception and non-argument
  CompletableFuture<User> someException();

  //both arg and the result is null;
  CompletableFuture<User> nullInvoke(User user);

}
