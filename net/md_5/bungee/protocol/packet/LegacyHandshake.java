package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class LegacyHandshake extends DefinedPacket {
  public String toString() {
    return "LegacyHandshake()";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof LegacyHandshake))
      return false; 
    LegacyHandshake other = (LegacyHandshake)o;
    return !!other.canEqual(this);
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof LegacyHandshake;
  }
  
  public int hashCode() {
    int result = 1;
    return 1;
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
