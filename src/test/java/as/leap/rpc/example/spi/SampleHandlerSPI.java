package as.leap.rpc.example.spi;


import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;
import java.util.Map;


/**
 *
 */
public interface SampleHandlerSPI {

  //complex object
  void getDepartment(User user, Handler<AsyncResult<Department>> handler);

  //primitive
  void getDepartment(int userId, Integer anotherId, Handler<AsyncResult<Integer>> handler);

  //array
  void getBytes(byte[] args, Handler<AsyncResult<byte[]>> handler);

  //Collection
  void getDepartList(List<User> users, Handler<AsyncResult<List<Department>>> handler);

  //HashMap
  void getDepartMap(Map<String, User> userMap, Handler<AsyncResult<Map<String, Department>>> handler);

  //enum
  void getDayOfWeek(Weeks day, Handler<AsyncResult<Weeks>> handler);

  //exception and non-argument
  void someException(Handler<AsyncResult<User>> handler);

  //both arg and the result is null;
  void nullInvoke(User user, Handler<AsyncResult<User>> handler);
}
