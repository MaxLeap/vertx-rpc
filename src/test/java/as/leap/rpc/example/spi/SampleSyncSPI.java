package as.leap.rpc.example.spi;

import java.util.List;
import java.util.Map;

/**
 * Created by stream.
 */
public interface SampleSyncSPI {

  Department getDepartment(User user);

  Integer getDepartment(int userId, Integer anotherId);

  byte[] getBytes(byte[] args);

  List<Department> getDepartList(List<User> users);

  Map<String, Department> getDepartMap(Map<String, User> userMap);

  Weeks getDayOfWeek(Weeks day);

  User someException();

  User nullInvoke(User user);

}
