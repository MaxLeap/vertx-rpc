package as.leap.rpc.example;

import as.leap.rpc.example.impl.SampleFutureServiceImpl;
import as.leap.rpc.example.impl.SampleHandlerServiceImpl;
import as.leap.rpc.example.impl.SampleObserableServiceImpl;
import as.leap.rpc.example.impl.SampleTimeoutRetryServiceImpl;
import as.leap.rpc.example.spi.*;
import as.leap.vertx.rpc.RPCHook;
import as.leap.vertx.rpc.WireProtocol;
import as.leap.vertx.rpc.impl.RPCClientOptions;
import as.leap.vertx.rpc.impl.RPCServerOptions;
import as.leap.vertx.rpc.impl.VertxRPCClient;
import as.leap.vertx.rpc.impl.VertxRPCServer;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxFactoryImpl;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

@RunWith(io.vertx.ext.unit.junit.VertxUnitRunner.class)
public class VertxRPCTest {
  private static final Logger logger = LoggerFactory.getLogger(VertxRPCTest.class);
  private static SampleHandlerSPI sampleHandlerSPI;
  private static SampleObserableSPI exampleObsSPI;
  private static SampleFutureSPI sampleFutureSPI;
  private static SampleTimeoutRetrySPI sampleTimeoutRetrySPI;

  @BeforeClass
  public static void beforeClass() {
    Vertx vertx = new VertxFactoryImpl().vertx();
    String busAddressHandler = "serviceAddressHandler";
    String busAddressForTimeout = "serviceAddressTimeout";
    String busAddressObs = "serviceAddressObs";
    String busAddressFuture = "serviceAddressFuture";

    //handler
    new VertxRPCServer(new RPCServerOptions(vertx).setBusAddress(busAddressHandler)
        .setRPCHook(new ServerServiceHook())
        .addService(new SampleHandlerServiceImpl()).setWireProtocol(WireProtocol.JSON));

    RPCClientOptions<SampleHandlerSPI> rpcClientHandlerOptions = new RPCClientOptions<SampleHandlerSPI>(vertx)
        .setRPCHook(new ClientServiceHook())
        .setBusAddress(busAddressHandler).setServiceClass(SampleHandlerSPI.class).setWireProtocol(WireProtocol.JSON);
    sampleHandlerSPI = new VertxRPCClient<>(rpcClientHandlerOptions).bindService();

    //reactive
    new VertxRPCServer(new RPCServerOptions(vertx)
        .setBusAddress(busAddressObs).addService(new SampleObserableServiceImpl()));

    RPCClientOptions<SampleObserableSPI> rpcClientObsOptions = new RPCClientOptions<SampleObserableSPI>(vertx)
        .setBusAddress(busAddressObs).setServiceClass(SampleObserableSPI.class);
    exampleObsSPI = new VertxRPCClient<>(rpcClientObsOptions).bindService();

    //completableFuture
    new VertxRPCServer(new RPCServerOptions(vertx)
        .setBusAddress(busAddressFuture).addService(new SampleFutureServiceImpl()));

    RPCClientOptions<SampleFutureSPI> rpcClientFutureOptions = new RPCClientOptions<SampleFutureSPI>(vertx)
        .setBusAddress(busAddressFuture).setServiceClass(SampleFutureSPI.class);
    sampleFutureSPI = new VertxRPCClient<>(rpcClientFutureOptions).bindService();

    //Timeout and retry
    new VertxRPCServer(new RPCServerOptions(vertx).setBusAddress(busAddressForTimeout).addService(new SampleTimeoutRetryServiceImpl()));

    RPCClientOptions<SampleTimeoutRetrySPI> rpcClientTimeoutOptions = new RPCClientOptions<SampleTimeoutRetrySPI>(vertx)
        .setBusAddress(busAddressForTimeout).setServiceClass(SampleTimeoutRetrySPI.class);
    sampleTimeoutRetrySPI = new VertxRPCClient<>(rpcClientTimeoutOptions).bindService();

  }


  @Test
  public void timeoutAndRetry(TestContext testContext) {
    Async async = testContext.async();
    User user = new User(1, "name");
    sampleTimeoutRetrySPI.getDepartment(user, departmentAsyncResult -> {
      if (departmentAsyncResult.succeeded()) {
        assertOne(departmentAsyncResult.result(), testContext, async);
      } else {
        testContext.fail(departmentAsyncResult.cause());
      }
    });
  }

  //--------------------------------------------------------------------------------------------------------------------

  @Test
  public void handlerOne(TestContext testContext) {
    Async async = testContext.async();
    User user = new User(1, "name");
    sampleHandlerSPI.getDepartment(user, departmentAsyncResult -> {
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
    sampleHandlerSPI.getDepartment(1, 2, departmentAsyncResult -> {
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
    sampleHandlerSPI.getBytes("name".getBytes(), asyncResult -> {
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

    sampleHandlerSPI.getDepartList(users, asyncResult -> {
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
    sampleHandlerSPI.getDayOfWeek(Weeks.SUNDAY, asyncResult -> {
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
    sampleHandlerSPI.someException(asyncResult -> {
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
    sampleHandlerSPI.nullInvoke(null, asyncResult -> {
      if (asyncResult.succeeded()) {
        assertSeven(asyncResult.result(), testContext, async);
      } else {
        testContext.fail(asyncResult.cause());
      }
    });
  }

  @Test
  public void handlerEight(TestContext testContext) {
    Async async = testContext.async();
    User user = new User(1, "name");
    Map<String, User> userMap = new HashMap<>();
    userMap.put("name", user);

    sampleHandlerSPI.getDepartMap(userMap, asyncResult -> {
      if (asyncResult.succeeded()) {
        Map<String, Department> departmentMap = asyncResult.result();
        assertEight(departmentMap, testContext, async);
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

  @Test
  public void obsEight(TestContext testContext) {
    Async async = testContext.async();
    User user = new User(1, "name");
    Map<String, User> userMap = new HashMap<>();
    userMap.put("name", user);
    exampleObsSPI.getDepartMap(userMap).subscribe(result -> assertEight(result, testContext, async), testContext::fail);
  }
  //--------------------------------------------------------------------------------------------------------------------

  @Test
  public void futureOne(TestContext testContext) {
    Async async = testContext.async();
    User user = new User(1, "name");
    sampleFutureSPI.getDepartment(user).whenComplete((department, throwable) -> {
      if (throwable != null) testContext.fail(throwable);
      assertOne(department, testContext, async);
    });

  }

  @Test
  public void futureTwo(TestContext testContext) {
    Async async = testContext.async();
    sampleFutureSPI.getDepartment(1, 2).whenComplete((department, throwable) -> {
      if (throwable != null) testContext.fail(throwable);
      assertTwo(department, testContext, async);
    });
  }

  @Test
  public void futureThree(TestContext testContext) {
    Async async = testContext.async();
    sampleFutureSPI.getBytes("name".getBytes()).whenComplete((result, throwable) -> {
      if (throwable != null) testContext.fail(throwable);
      assertThree(result, testContext, async);
    });
  }

  @Test
  public void futureFour(TestContext testContext) {
    Async async = testContext.async();
    List<User> users = new ArrayList<>();
    User user = new User();
    user.setId(1);
    users.add(user);
    sampleFutureSPI.getDepartList(users).whenComplete((result, throwable) -> {
      if (throwable != null) testContext.fail(throwable);
      assertFour(result, testContext, async);
    });
  }

  @Test
  public void futureFive(TestContext testContext) {
    Async async = testContext.async();
    sampleFutureSPI.getDayOfWeek(Weeks.SUNDAY).whenComplete((result, throwable) -> {
      if (throwable != null) testContext.fail(throwable);
      assertFive(result, testContext, async);
    });
  }

  @Test
  public void futureSix(TestContext testContext) {
    Async async = testContext.async();
    sampleFutureSPI.someException().whenComplete((result, throwable) -> {
      if (throwable != null) assertSix(throwable, testContext, async);
      else testContext.fail();
    });
  }

  @Test
  public void futureSeven(TestContext testContext) {
    Async async = testContext.async();
    sampleFutureSPI.nullInvoke(null).whenComplete((result, throwable) -> {
      if (throwable != null) testContext.fail(throwable);
      assertSeven(result, testContext, async);
    });
  }

  @Test
  public void futureEight(TestContext testContext) {
    Async async = testContext.async();
    User user = new User(1, "name");
    Map<String, User> userMap = new HashMap<>();
    userMap.put("name", user);
    sampleFutureSPI.getDepartMap(userMap).whenComplete((result, throwable) -> {
      if (throwable != null) testContext.fail(throwable);
      assertEight(result, testContext, async);
    });
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
    testContext.assertEquals("name", new String(result));
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
    testContext.assertTrue(ex instanceof MyException);
    testContext.assertEquals("illegalArguments", ex.getMessage());
    async.complete();
  }

  private void assertSeven(User user, TestContext testContext, Async async) {
    testContext.assertNull(user);
    async.complete();
  }

  private void assertEight(Map<String, Department> departmentMap, TestContext testContext, Async async) {
    testContext.assertNotNull(departmentMap);
    testContext.assertEquals(1, departmentMap.size());
    testContext.assertEquals("research", departmentMap.get("research").getName());
    async.complete();
  }

  private static class ClientServiceHook implements RPCHook {
    @Override
    public void beforeHandler(String interfaceName, String methodName, Object[] args, MultiMap header) {
      logger.info("client hook before.");
      header.add("reqId", UUID.randomUUID().toString());
      logger.info(String.format("interfaceName:%s, methodName:%s, objects:%s, header:%s", interfaceName, methodName, Arrays.toString(args), header));
    }

    @Override
    public void afterHandler(Object response, MultiMap header) {
      logger.info("client hook after.");
      logger.info(String.format("result: %s, header:%s", response, header));
    }

    @Override
    public void afterHandler(Throwable throwable, MultiMap header) {
      logger.info("client hook after.");
      logger.info(String.format("exception: %s, header:%s", throwable.getMessage(), header));
    }
  }

  private static class ServerServiceHook implements RPCHook {
    @Override
    public void beforeHandler(String interfaceName, String methodName, Object[] args, MultiMap header) {
      logger.info("server hook before.");
      logger.info(String.format("interfaceName:%s, methodName:%s, objects:%s, header:%s", interfaceName, methodName, Arrays.toString(args), header));
    }

    @Override
    public void afterHandler(Object response, MultiMap header) {
      logger.info("server hook after.");
      logger.info(String.format("result: %s, header:%s", response, header));
    }

    @Override
    public void afterHandler(Throwable throwable, MultiMap header) {
      logger.info("server hook after.");
      logger.info(String.format("exception: %s, header:%s", throwable.getMessage(), header));
    }
  }


}


