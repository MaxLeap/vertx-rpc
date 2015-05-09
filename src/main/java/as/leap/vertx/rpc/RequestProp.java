package as.leap.vertx.rpc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

/**
 * Created by stream.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestProp {
  //define timeout to the method
  int timeout() default 0;

  TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

  //retry times for method
  int retry() default 0;
}
