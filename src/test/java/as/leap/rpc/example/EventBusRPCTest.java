package as.leap.rpc.example;

import as.leap.rpc.example.impl.ExampleHandlerServiceImpl;
import as.leap.rpc.example.impl.ExampleObserableServiceImpl;
import as.leap.rpc.example.spi.*;
import as.leap.vertx.rpc.impl.RPCClientOptions;
import as.leap.vertx.rpc.impl.RPCServerOptions;
import as.leap.vertx.rpc.impl.VertxRPCClientInvoker;
import as.leap.vertx.rpc.impl.VertxRPCServerInvoker;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxFactoryImpl;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(io.vertx.ext.unit.junit.VertxUnitRunner.class)
public class EventBusRPCTest {

  private static ExampleHandlerSPI exampleHandlerSPI;
  private static ExampleObserableSPI exampleObsSPI;

  @BeforeClass
  public static void beforeClass() {
    Vertx vertx = new VertxFactoryImpl().vertx();
    String busAddressHandler = "serviceAddressHandler";
    String busAddressObs = "serviceAddressObs";

    //Server
    new VertxRPCServerInvoker(new RPCServerOptions(vertx).setBusAddress(busAddressHandler).addService(new ExampleHandlerServiceImpl()));
    new VertxRPCServerInvoker(new RPCServerOptions(vertx).setBusAddress(busAddressObs).addService(new ExampleObserableServiceImpl()));

    //client
    RPCClientOptions<ExampleHandlerSPI> rpcClientHandlerOptions = new RPCClientOptions<ExampleHandlerSPI>(vertx).setBusAddress(busAddressHandler).setServiceClass(ExampleHandlerSPI.class);
    exampleHandlerSPI = new VertxRPCClientInvoker<>(rpcClientHandlerOptions).bindService();

    RPCClientOptions<ExampleObserableSPI> rpcClientObsOptions = new RPCClientOptions<ExampleObserableSPI>(vertx).setBusAddress(busAddressObs).setServiceClass(ExampleObserableSPI.class);
    exampleObsSPI = new VertxRPCClientInvoker<>(rpcClientObsOptions).bindService();
  }

  @Test
  public void handlerOne(TestContext testContext) {
    Async async = testContext.async();
    User user = new User(1, "name");
    exampleHandlerSPI.getDepartment(user, departmentAsyncResult -> {
      if (departmentAsyncResult.succeeded()) {
        assertOne(departmentAsyncResult.result(), testContext, async);
      } else {
        testContext.fail(departmentAsyncResult.cause());
      }
    });
  }

  @Test
  public void handlerTwo(TestContext testContext) {
    Async async = testContext.async();
    exampleHandlerSPI.getDepartment(1, 2, departmentAsyncResult -> {
      if (departmentAsyncResult.succeeded()) {
        assertTwo(departmentAsyncResult.result(), testContext, async);
      } else {
        testContext.fail(departmentAsyncResult.cause());
      }
    });
  }

  @Test
  public void handlerThree(TestContext testContext) {
    Async async = testContext.async();
    exampleHandlerSPI.getBytes("name".getBytes(), asyncResult -> {
      if (asyncResult.succeeded()) {
        assertThree(asyncResult.result(), testContext, async);
      } else {
        testContext.fail(asyncResult.cause());
      }
    });
  }

  @Test
  public void handlerFour(TestContext testContext) {
    Async async = testContext.async();
    List<User> users = new ArrayList<>();
    User user = new User();
    user.setId(1);
    users.add(user);

    exampleHandlerSPI.getDepartList(users, asyncResult -> {
      if (asyncResult.succeeded()) {
        assertFour(asyncResult.result(), testContext, async);
      } else {
        testContext.fail(asyncResult.cause());
      }
    });
  }

  @Test
  public void handlerFive(TestContext testContext) {
    Async async = testContext.async();
    exampleHandlerSPI.getDayOfWeek(Weeks.SUNDAY, asyncResult -> {
      if (asyncResult.succeeded()) {
        assertFive(asyncResult.result(), testContext, async);
      } else {
        testContext.fail(asyncResult.cause());
      }
    });
  }

  @Test
  public void handleSix(TestContext testContext) {
    Async async = testContext.async();
    exampleHandlerSPI.someException(asyncResult -> {
      if (asyncResult.succeeded()) {
        testContext.fail("should not be success.");
      } else {
        assertSix(asyncResult.cause(), testContext, async);
      }
    });
  }

  @Test
  public void handleSeven(TestContext testContext) {
    Async async = testContext.async();
    exampleHandlerSPI.nullInvoke(null, asyncResult -> {
      if (asyncResult.succeeded()) {
        assertSeven(asyncResult.result(), testContext, async);
      } else {
        testContext.fail(asyncResult.cause());
      }
    });
  }

  //--------------------------------------------------------------------------------------------------------------------

  @Test
  public void obsOne(TestContext testContext) {
    Async async = testContext.async();
    User user = new User(1, "name");
    exampleObsSPI.getDepartment(user)
        .subscribe(department -> assertOne(department, testContext, async), testContext::fail);
  }

  @Test
  public void obsTwo(TestContext testContext) {
    Async async = testContext.async();
    exampleObsSPI.getDepartment(1, 2)
        .subscribe(department -> assertTwo(department, testContext, async), testContext::fail);
  }

  @Test
  public void obsThree(TestContext testContext) {
    Async async = testContext.async();
    exampleObsSPI.getBytes("name".getBytes()).subscribe(result -> assertThree(result, testContext, async), testContext::fail);
  }

  @Test
  public void obsFour(TestContext testContext) {
    Async async = testContext.async();
    List<User> users = new ArrayList<>();
    User user = new User();
    user.setId(1);
    users.add(user);
    exampleObsSPI.getDepartList(users).subscribe(result -> assertFour(result, testContext, async), testContext::fail);
  }

  @Test
  public void obsFive(TestContext testContext) {
    Async async = testContext.async();
    exampleObsSPI.getDayOfWeek(Weeks.SUNDAY).subscribe(result -> assertFive(result, testContext, async), testContext::fail);
  }

  @Test
  public void obsSix(TestContext testContext) {
    Async async = testContext.async();
    exampleObsSPI.someException().subscribe(result -> testContext.fail(), ex -> assertSix(ex, testContext, async));
  }

  @Test
  public void obsSeven(TestContext testContext) {
    Async async = testContext.async();
    exampleObsSPI.nullInvoke(null).subscribe(result -> assertSeven(result, testContext, async), testContext::fail);
  }

  //--------------------------------------------------------------------------------------------------------------------

  private void assertOne(Department department, TestContext testContext, Async async) {
    testContext.assertEquals(1, department.getId());
    testContext.assertEquals("research", department.getName());
    async.complete();
  }

  private void assertTwo(Integer departmentId, TestContext testContext, Async async) {
    testContext.assertEquals(1, departmentId);
    async.complete();
  }

  private void assertThree(byte[] result, TestContext testContext, Async async) {
    testContext.assertTrue(result.length == "name".getBytes().length);
    async.complete();
  }

  private void assertFour(List<Department> result, TestContext testContext, Async async) {
    testContext.assertEquals(1, result.get(0).getId());
    async.complete();
  }

  private void assertFive(Weeks day, TestContext testContext, Async async) {
    testContext.assertEquals(Weeks.FRIDAY, day);
    async.complete();
  }

  private void assertSix(Throwable ex, TestContext testContext, Async async) {
    testContext.assertEquals("illegalArguments", ex.getMessage());
    async.complete();
  }

  private void assertSeven(User user, TestContext testContext, Async async) {
    testContext.assertNull(user);
    async.complete();
  }
}


