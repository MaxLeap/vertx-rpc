package as.leap.vertx.rpc;

import io.vertx.core.shareddata.Shareable;

/**
 * Created by stream.
 */
class SharedWrapper<T> implements Shareable {
  private T value;

  public SharedWrapper(T value) {
    this.value = value;
  }

  public T getValue() {
    return value;
  }
}
