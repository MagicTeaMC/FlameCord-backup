package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class SetCompression extends DefinedPacket {
  private int threshold;
  
  public void setThreshold(int threshold) {
    this.threshold = threshold;
  }
  
  public String toString() {
    return "SetCompression(threshold=" + getThreshold() + ")";
  }
  
  public SetCompression() {}
  
  public SetCompression(int threshold) {
    this.threshold = threshold;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof SetCompression))
      return false; 
    SetCompression other = (SetCompression)o;
    return !other.canEqual(this) ? false : (!(getThreshold() != other.getThreshold()));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof SetCompression;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    return result * 59 + getThreshold();
  }
  
  public int getThreshold() {
    return this.threshold;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    this.threshold = DefinedPacket.readVarInt(buf);
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    DefinedPacket.writeVarInt(this.threshold, buf);
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
