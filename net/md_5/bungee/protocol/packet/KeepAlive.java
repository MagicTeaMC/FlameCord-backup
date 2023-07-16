package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class KeepAlive extends DefinedPacket {
  private long randomId;
  
  public void setRandomId(long randomId) {
    this.randomId = randomId;
  }
  
  public String toString() {
    return "KeepAlive(randomId=" + getRandomId() + ")";
  }
  
  public KeepAlive() {}
  
  public KeepAlive(long randomId) {
    this.randomId = randomId;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof KeepAlive))
      return false; 
    KeepAlive other = (KeepAlive)o;
    return !other.canEqual(this) ? false : (!(getRandomId() != other.getRandomId()));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof KeepAlive;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    long $randomId = getRandomId();
    return result * 59 + (int)($randomId >>> 32L ^ $randomId);
  }
  
  public long getRandomId() {
    return this.randomId;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (ProtocolConstants.isBeforeOrEq(protocolVersion, 5)) {
      this.randomId = buf.readInt();
      return;
    } 
    this.randomId = (protocolVersion >= 340) ? buf.readLong() : readVarInt(buf);
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (ProtocolConstants.isBeforeOrEq(protocolVersion, 5)) {
      buf.writeInt((int)this.randomId);
      return;
    } 
    if (protocolVersion >= 340) {
      buf.writeLong(this.randomId);
    } else {
      writeVarInt((int)this.randomId, buf);
    } 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
