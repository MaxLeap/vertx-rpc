package as.leap.vertx.rpc;

/**
 *
 */
public class VertxRPCException extends RuntimeException {

  public VertxRPCException(String message) {
    super(message);
  }

  public VertxRPCException(String message, Throwable cause) {
    super(message, cause);
  }

  public VertxRPCException(Throwable cause) {
    super(cause);
  }

  protected VertxRPCException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
