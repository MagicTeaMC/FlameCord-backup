package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class ClearTitles extends DefinedPacket {
  private boolean reset;
  
  public void setReset(boolean reset) {
    this.reset = reset;
  }
  
  public String toString() {
    return "ClearTitles(reset=" + isReset() + ")";
  }
  
  public ClearTitles() {}
  
  public ClearTitles(boolean reset) {
    this.reset = reset;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ClearTitles))
      return false; 
    ClearTitles other = (ClearTitles)o;
    return !other.canEqual(this) ? false : (!(isReset() != other.isReset()));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ClearTitles;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    return result * 59 + (isReset() ? 79 : 97);
  }
  
  public boolean isReset() {
    return this.reset;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    this.reset = buf.readBoolean();
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    buf.writeBoolean(this.reset);
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
