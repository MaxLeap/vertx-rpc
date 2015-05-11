package as.leap.rpc.example.spi;

import rx.Observable;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface SampleObserableSPI {

  //complex object
  Observable<Department> getDepartment(User user);

  //primitive
  Observable<Integer> getDepartment(int userId, Integer anotherId);

  //array
  Observable<byte[]> getBytes(byte[] args);

  //Collection
  Observable<List<Department>> getDepartList(List<User> users);

  //HashMap
  Observable<Map<String, Department>> getDepartMap(Map<String, User> userMap);

  //enum
  Observable<Weeks> getDayOfWeek(Weeks day);

  //exception and non-argument
  Observable<User> someException();

  //both arg and the result is null;
  Observable<User> nullInvoke(User user);
}
