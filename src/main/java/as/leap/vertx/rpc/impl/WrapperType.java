package as.leap.vertx.rpc.impl;

/**
 *
 */
class WrapperType<T> {
  private Class<T> clazz;
  private T value;

  public WrapperType() {
  }

  public WrapperType(T value, Class<T> clazz) {
    this.value = value;
    this.clazz = clazz;
  }

  public Class<T> getClazz() {
    return clazz;
  }

  public void setClazz(Class<T> clazz) {
    this.clazz = clazz;
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }
}
