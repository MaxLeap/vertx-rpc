package as.leap.vertx.rpc.impl;

import java.util.List;

/**
 *
 */
class RPCRequest {

  private String serviceName;
  private String methodName;
  //[String className, byte[] valueBytes]
  private List<Object> args;

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public List<Object> getArgs() {
    return args;
  }

  public void setArgs(List<Object> args) {
    this.args = args;
  }
}
