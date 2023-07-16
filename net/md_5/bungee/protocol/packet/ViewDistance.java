package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class ViewDistance extends DefinedPacket {
  private int distance;
  
  public void setDistance(int distance) {
    this.distance = distance;
  }
  
  public String toString() {
    return "ViewDistance(distance=" + getDistance() + ")";
  }
  
  public ViewDistance() {}
  
  public ViewDistance(int distance) {
    this.distance = distance;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ViewDistance))
      return false; 
    ViewDistance other = (ViewDistance)o;
    return !other.canEqual(this) ? false : (!(getDistance() != other.getDistance()));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ViewDistance;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    return result * 59 + getDistance();
  }
  
  public int getDistance() {
    return this.distance;
  }
  
  public void read(ByteBuf buf) {
    this.distance = DefinedPacket.readVarInt(buf);
  }
  
  public void write(ByteBuf buf) {
    DefinedPacket.writeVarInt(this.distance, buf);
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
