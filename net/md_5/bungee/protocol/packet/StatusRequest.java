package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class StatusRequest extends DefinedPacket {
  public String toString() {
    return "StatusRequest()";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof StatusRequest))
      return false; 
    StatusRequest other = (StatusRequest)o;
    return !!other.canEqual(this);
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof StatusRequest;
  }
  
  public int hashCode() {
    int result = 1;
    return 1;
  }
  
  public void read(ByteBuf buf) {}
  
  public void write(ByteBuf buf) {}
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
  
  public int expectedMaxLength(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    return 0;
  }
}
