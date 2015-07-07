package as.leap.rpc.example.impl;

import as.leap.rpc.example.spi.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.futures.AsyncCompletionStage;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by stream.
 */
public class SampleSyncSPIImpl implements SampleSyncSPI {

  private SampleFutureSPI sampleFutureSPI;
  private Logger log = LoggerFactory.getLogger(SampleSyncSPIImpl.class);

  public SampleSyncSPIImpl(SampleFutureSPI sampleFutureSPI) {
    this.sampleFutureSPI = sampleFutureSPI;
  }

  @Override
  public Department getDepartment(User user) {
    try {
      return AsyncCompletionStage.get(sampleFutureSPI.getDepartment(user));
    } catch (ExecutionException e) {
      throw new RuntimeException(e.getCause());
    } catch (InterruptedException | SuspendExecution e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Integer getDepartment(int userId, Integer anotherId) {
    try {
      return AsyncCompletionStage.get(sampleFutureSPI.getDepartment(userId, anotherId));
    } catch (ExecutionException e) {
      throw new RuntimeException(e.getCause());
    } catch (InterruptedException | SuspendExecution e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }

  @Override
  public byte[] getBytes(byte[] args) {
    try {
      return AsyncCompletionStage.get(sampleFutureSPI.getBytes(args));
    } catch (ExecutionException e) {
      throw new RuntimeException(e.getCause());
    } catch (InterruptedException | SuspendExecution e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }

  @Override
  public List<Department> getDepartList(List<User> users) {
    try {
      return AsyncCompletionStage.get(sampleFutureSPI.getDepartList(users));
    } catch (ExecutionException e) {
      throw new RuntimeException(e.getCause());
    } catch (InterruptedException | SuspendExecution e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Map<String, Department> getDepartMap(Map<String, User> userMap) {
    try {
      return AsyncCompletionStage.get(sampleFutureSPI.getDepartMap(userMap));
    } catch (ExecutionException e) {
      throw new RuntimeException(e.getCause());
    } catch (InterruptedException | SuspendExecution e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Weeks getDayOfWeek(Weeks day) {
    try {
      return AsyncCompletionStage.get(sampleFutureSPI.getDayOfWeek(day));
    } catch (ExecutionException e) {
      throw new RuntimeException(e.getCause());
    } catch (InterruptedException | SuspendExecution e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public User someException() {
    try {
      return AsyncCompletionStage.get(sampleFutureSPI.someException());
    } catch (ExecutionException e) {
      throw new RuntimeException(e.getCause());
    } catch (InterruptedException | SuspendExecution e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public User nullInvoke(User user) {
    try {
      return AsyncCompletionStage.get(sampleFutureSPI.nullInvoke(user));
    } catch (ExecutionException e) {
      throw new RuntimeException(e.getCause());
    } catch (InterruptedException | SuspendExecution e) {
      e.printStackTrace();
    }
    return null;
  }
}
