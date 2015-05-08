package as.leap.vertx.rpc;

/**
 *
 */
class RPCResponse {

  private String responseTypeName;
  private byte[] response;

  public RPCResponse(String responseTypeName, byte[] response) {
    this.responseTypeName = responseTypeName;
    this.response = response;
  }

  public RPCResponse() {

  }

  public String getResponseTypeName() {
    return responseTypeName;
  }

  public void setResponseTypeName(String responseTypeName) {
    this.responseTypeName = responseTypeName;
  }

  public byte[] getResponse() {
    return response;
  }

  public void setResponse(byte[] response) {
    this.response = response;
  }
}
