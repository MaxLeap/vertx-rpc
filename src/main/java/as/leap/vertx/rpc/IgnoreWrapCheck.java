package as.leap.vertx.rpc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by stream.
 * rpc framework do not wrap your bean with this annotation on your class.
 * Default framework would check your class to determine whether wrap bean, since
 * the types of parameter and return would be Map Collection or primitive.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreWrapCheck {
}
