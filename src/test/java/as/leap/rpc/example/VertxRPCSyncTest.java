package as.leap.rpc.example;

import as.leap.rpc.example.impl.SampleFutureServiceImpl;
import as.leap.rpc.example.impl.SampleSyncSPIImpl;
import as.leap.rpc.example.spi.*;
import as.leap.vertx.rpc.impl.RPCClientOptions;
import as.leap.vertx.rpc.impl.RPCServerOptions;
import as.leap.vertx.rpc.impl.VertxRPCClient;
import as.leap.vertx.rpc.impl.VertxRPCServer;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxFactoryImpl;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stream.
 */

@RunWith(io.vertx.ext.unit.junit.VertxUnitRunner.class)
public class VertxRPCSyncTest extends VertxRPCBase {

  private static SampleSyncSPI sampleSyncSPISync;

  @BeforeClass
  public static void beforeClass() {
    Vertx vertx = new VertxFactoryImpl().vertx();
    //sync
    new VertxRPCServer(new RPCServerOptions(vertx).setBusAddress("syncServiceAddress").addService(new SampleSyncSPIImpl(new SampleFutureServiceImpl())));
    RPCClientOptions<SampleSyncSPI> rpcClientSyncOptions = new RPCClientOptions<SampleSyncSPI>(vertx)
        .setBusAddress("syncServiceAddress").setServiceClass(SampleSyncSPI.class);
    sampleSyncSPISync = new VertxRPCClient<>(rpcClientSyncOptions).bindService();
  }

  @Test
  public void syncOne(TestContext testContext) {
    Async async = testContext.async();
    User user = new User(1, "name");
    try {
      Department department = sampleSyncSPISync.getDepartment(user);
      assertOne(department, testContext, async);
    } catch (Exception e) {
      testContext.fail(e);
    }
  }

  @Test
  public void syncTwo(TestContext testContext) {
    Async async = testContext.async();
    assertTwo(sampleSyncSPISync.getDepartment(1, 2), testContext, async);
  }

  @Test
  public void syncThree(TestContext testContext) {
    Async async = testContext.async();
    assertThree(sampleSyncSPISync.getBytes("name".getBytes()), testContext, async);
  }

  @Test
  public void syncFour(TestContext testContext) {
    Async async = testContext.async();
    List<User> users = new ArrayList<>();
    User user = new User();
    user.setId(1);
    users.add(user);
    assertFour(sampleSyncSPISync.getDepartList(users), testContext, async);
  }

  @Test
  public void syncFive(TestContext testContext) {
    Async async = testContext.async();
    assertFive(sampleSyncSPISync.getDayOfWeek(Weeks.SUNDAY), testContext, async);
  }

  @Test
  public void syncSix(TestContext testContext) {
    Async async = testContext.async();
    try {
      sampleSyncSPISync.someException();
    } catch (MyException e) {
      assertSix(e, testContext, async);
    }
  }

  @Test
  public void syncSeven(TestContext testContext) {
    Async async = testContext.async();
    assertSeven(sampleSyncSPISync.nullInvoke(null), testContext, async);
  }

  @Test
  public void syncEight(TestContext testContext) {
    Async async = testContext.async();
    User user = new User(1, "name");
    Map<String, User> userMap = new HashMap<>();
    userMap.put("name", user);
    assertEight(sampleSyncSPISync.getDepartMap(userMap), testContext, async);
  }


}
