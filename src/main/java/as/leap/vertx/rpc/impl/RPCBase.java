package as.leap.vertx.rpc.impl;

import as.leap.vertx.rpc.IgnoreWrapCheck;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Created by stream.
 */
abstract class RPCBase {
  static final String CALLBACK_TYPE = "callbackType";

  void checkBusAddress(String address) {
    Objects.requireNonNull(address, "service's event bus address can not be null.");
  }

  boolean isWrapType(Class clazz) {
    if (clazz.getAnnotation(IgnoreWrapCheck.class) != null) {
      return false;
    } else {
      return clazz.isPrimitive() || clazz.isArray() || clazz.isEnum()
        || Collection.class.isAssignableFrom(clazz)
        || Map.class.isAssignableFrom(clazz)
        || Modifier.isAbstract(clazz.getModifiers())
        || clazz.isInterface();
    }
  }

  private <T> byte[] toBytes(Schema<T> schema, T object) throws Exception {
    LinkedBuffer buffer = LinkedBuffer.allocate();
    byte[] bytes = new byte[0];
    try {
      bytes = ProtobufIOUtil.toByteArray(object, schema, buffer);
    } finally {
      buffer.clear();
    }
    return bytes;
  }

  <T> byte[] getWrapTypeBytes(Object object, Class<T> clazz) throws Exception {
    WrapperType<T> wrapperType = new WrapperType(object, clazz);
    Schema<WrapperType> schema = RuntimeSchema.getSchema(WrapperType.class);
    return toBytes(schema, wrapperType);
  }

  <T> byte[] asBytes(T object) throws Exception {
    return asBytes(object, object.getClass());
  }

  byte[] asBytes(Object object, Class clazz) throws Exception {
    if (isWrapType(clazz)) {
      return getWrapTypeBytes(object, clazz);
    } else {
      Schema<Object> schema = RuntimeSchema.getSchema((Class<Object>) clazz);
      return toBytes(schema, object);
    }
  }

  <T> T asObject(byte[] bytes, Class<T> clazz) throws IOException {
    Schema<T> schema = RuntimeSchema.getSchema(clazz);
    T object = schema.newMessage();
    ProtobufIOUtil.mergeFrom(bytes, object, schema);
    return object;
  }

  protected enum CallbackType {
    ASYNC_HANDLER, FUTURE, REACTIVE, COMPLETABLE_FUTURE
  }
}
