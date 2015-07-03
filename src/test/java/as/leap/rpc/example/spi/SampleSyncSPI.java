package as.leap.rpc.example.spi;

import java.util.List;
import java.util.Map;

/**
 * Created by stream.
 */
public interface SampleSyncSPI {

  Department getDepartment(User user);

  Integer getDepartment(int userId, Integer anotherId);

  //array
  byte[] getBytes(byte[] args);

  //Collection
  List<Department> getDepartList(List<User> users);

  //HashMap
  Map<String, Department> getDepartMap(Map<String, User> userMap);

  //enum
  Weeks getDayOfWeek(Weeks day);

  //exception and non-argument
  User someException();

  //both arg and the result is null;
  User nullInvoke(User user);

}
