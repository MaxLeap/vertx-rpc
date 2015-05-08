package as.leap.vertx.rpc;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

/**
 *
 */
final class Codec {

  static boolean isWrapType(Class clazz) {
    return clazz.isPrimitive() || Collection.class.isAssignableFrom(clazz) || clazz.isArray()
        || Map.class.isAssignableFrom(clazz) || clazz.isEnum()
        || Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers());
  }

  private static <T> byte[] getWrapTypeBytes(Object object, Class<T> clazz) throws Exception {
    LinkedBuffer buffer = LinkedBuffer.allocate();
    try {
      WrapperType<T> wrapperType = new WrapperType(object, clazz);
      Schema<WrapperType> schema = RuntimeSchema.getSchema(WrapperType.class);
      return ProtobufIOUtil.toByteArray(wrapperType, schema, buffer);
    } finally {
      buffer.clear();
    }
  }

  static <T> byte[] asBytes(T object) throws Exception {
    return asBytes(object, object.getClass());
  }

  static byte[] asBytes(Object object, Class clazz) throws Exception {
    LinkedBuffer buffer = LinkedBuffer.allocate();
    try {
      if (isWrapType(clazz)) {
        return getWrapTypeBytes(object, clazz);
      } else {
        Schema<Object> schema = RuntimeSchema.getSchema((Class<Object>) clazz);
        return ProtobufIOUtil.toByteArray(object, schema, buffer);
      }
    } finally {
      buffer.clear();
    }
  }

  static <T> T asObject(byte[] bytes, Class<T> clazz) {
    Schema<T> schema = RuntimeSchema.getSchema(clazz);
    T object = schema.newMessage();
    ProtobufIOUtil.mergeFrom(bytes, object, schema);
    return object;
  }
}
