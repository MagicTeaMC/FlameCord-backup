package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class StatusResponse extends DefinedPacket {
  private String response;
  
  public void setResponse(String response) {
    this.response = response;
  }
  
  public String toString() {
    return "StatusResponse(response=" + getResponse() + ")";
  }
  
  public StatusResponse() {}
  
  public StatusResponse(String response) {
    this.response = response;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof StatusResponse))
      return false; 
    StatusResponse other = (StatusResponse)o;
    if (!other.canEqual(this))
      return false; 
    Object this$response = getResponse(), other$response = other.getResponse();
    return !((this$response == null) ? (other$response != null) : !this$response.equals(other$response));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof StatusResponse;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $response = getResponse();
    return result * 59 + (($response == null) ? 43 : $response.hashCode());
  }
  
  public String getResponse() {
    return this.response;
  }
  
  public void read(ByteBuf buf) {
    this.response = readString(buf);
  }
  
  public void write(ByteBuf buf) {
    writeString(this.response, buf);
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
