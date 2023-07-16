package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class ClientStatus extends DefinedPacket {
  private byte payload;
  
  public void setPayload(byte payload) {
    this.payload = payload;
  }
  
  public String toString() {
    return "ClientStatus(payload=" + getPayload() + ")";
  }
  
  public ClientStatus() {}
  
  public ClientStatus(byte payload) {
    this.payload = payload;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ClientStatus))
      return false; 
    ClientStatus other = (ClientStatus)o;
    return !other.canEqual(this) ? false : (!(getPayload() != other.getPayload()));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ClientStatus;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    return result * 59 + getPayload();
  }
  
  public byte getPayload() {
    return this.payload;
  }
  
  public void read(ByteBuf buf) {
    this.payload = buf.readByte();
  }
  
  public void write(ByteBuf buf) {
    buf.writeByte(this.payload);
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
