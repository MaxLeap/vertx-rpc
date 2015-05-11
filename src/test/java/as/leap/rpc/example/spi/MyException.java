package as.leap.rpc.example.spi;

/**
 * Created by stream.
 */
public class MyException extends RuntimeException {

  public MyException(String message) {
    super(message);
  }

  public MyException(String message, Throwable cause) {
    super(message, cause);
  }

  public MyException(Throwable cause) {
    super(cause);
  }
}
