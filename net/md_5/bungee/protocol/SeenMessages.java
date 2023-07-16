package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import java.util.BitSet;

public class SeenMessages extends DefinedPacket {
  private int offset;
  
  private BitSet acknowledged;
  
  public void setOffset(int offset) {
    this.offset = offset;
  }
  
  public void setAcknowledged(BitSet acknowledged) {
    this.acknowledged = acknowledged;
  }
  
  public String toString() {
    return "SeenMessages(offset=" + getOffset() + ", acknowledged=" + getAcknowledged() + ")";
  }
  
  public SeenMessages() {}
  
  public SeenMessages(int offset, BitSet acknowledged) {
    this.offset = offset;
    this.acknowledged = acknowledged;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof SeenMessages))
      return false; 
    SeenMessages other = (SeenMessages)o;
    if (!other.canEqual(this))
      return false; 
    if (getOffset() != other.getOffset())
      return false; 
    Object this$acknowledged = getAcknowledged(), other$acknowledged = other.getAcknowledged();
    return !((this$acknowledged == null) ? (other$acknowledged != null) : !this$acknowledged.equals(other$acknowledged));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof SeenMessages;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getOffset();
    Object $acknowledged = getAcknowledged();
    return result * 59 + (($acknowledged == null) ? 43 : $acknowledged.hashCode());
  }
  
  public int getOffset() {
    return this.offset;
  }
  
  public BitSet getAcknowledged() {
    return this.acknowledged;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    this.offset = DefinedPacket.readVarInt(buf);
    this.acknowledged = DefinedPacket.readFixedBitSet(20, buf);
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    DefinedPacket.writeVarInt(this.offset, buf);
    DefinedPacket.writeFixedBitSet(this.acknowledged, 20, buf);
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    throw new UnsupportedOperationException("Not supported.");
  }
}
