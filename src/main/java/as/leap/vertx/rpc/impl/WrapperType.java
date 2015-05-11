package as.leap.vertx.rpc.impl;

/**
 *
 */
class WrapperType<T> {
  private Class<T> clazz;
  private T value;

  public WrapperType(T value, Class<T> clazz) {
    this.value = value;
    this.clazz = clazz;
  }

  public Class<T> getClazz() {
    return clazz;
  }

  public T getValue() {
    return value;
  }
}
