package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class LegacyPing extends DefinedPacket {
  private final boolean v1_5;
  
  public String toString() {
    return "LegacyPing(v1_5=" + isV1_5() + ")";
  }
  
  public LegacyPing(boolean v1_5) {
    this.v1_5 = v1_5;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof LegacyPing))
      return false; 
    LegacyPing other = (LegacyPing)o;
    return !other.canEqual(this) ? false : (!(isV1_5() != other.isV1_5()));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof LegacyPing;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    return result * 59 + (isV1_5() ? 79 : 97);
  }
  
  public boolean isV1_5() {
    return this.v1_5;
  }
  
  public void read(ByteBuf buf) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void write(ByteBuf buf) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
