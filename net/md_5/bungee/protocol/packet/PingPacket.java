package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class PingPacket extends DefinedPacket {
  private long time;
  
  public void setTime(long time) {
    this.time = time;
  }
  
  public String toString() {
    return "PingPacket(time=" + getTime() + ")";
  }
  
  public PingPacket() {}
  
  public PingPacket(long time) {
    this.time = time;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof PingPacket))
      return false; 
    PingPacket other = (PingPacket)o;
    return !other.canEqual(this) ? false : (!(getTime() != other.getTime()));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof PingPacket;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    long $time = getTime();
    return result * 59 + (int)($time >>> 32L ^ $time);
  }
  
  public long getTime() {
    return this.time;
  }
  
  public void read(ByteBuf buf) {
    this.time = buf.readLong();
  }
  
  public void write(ByteBuf buf) {
    buf.writeLong(this.time);
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
  
  public int expectedMaxLength(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    return 8;
  }
  
  public int expectedMinLength(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    return 8;
  }
}
