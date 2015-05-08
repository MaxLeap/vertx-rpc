package as.leap.rpc.example.spi;

import rx.Observable;

import java.util.List;

/**
 *
 */
public interface ExampleObserableSPI {

  //complex object
  Observable<Department> getDepartment(User user);

  //primitive
  Observable<Integer> getDepartment(int userId, Integer anotherId);

  //array
  Observable<byte[]> getBytes(byte[] args);

  //Collection
  Observable<List<Department>> getDepartList(List<User> users);

  //enum
  Observable<Weeks> getDayOfWeek(Weeks day);

  //exception and non-argument
  Observable<User> someException();

  //both arg and the result is null;
  Observable<User> nullInvoke(User user);
}
