package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class TitleTimes extends DefinedPacket {
  private int fadeIn;
  
  private int stay;
  
  private int fadeOut;
  
  public void setFadeIn(int fadeIn) {
    this.fadeIn = fadeIn;
  }
  
  public void setStay(int stay) {
    this.stay = stay;
  }
  
  public void setFadeOut(int fadeOut) {
    this.fadeOut = fadeOut;
  }
  
  public String toString() {
    return "TitleTimes(fadeIn=" + getFadeIn() + ", stay=" + getStay() + ", fadeOut=" + getFadeOut() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof TitleTimes))
      return false; 
    TitleTimes other = (TitleTimes)o;
    return !other.canEqual(this) ? false : ((getFadeIn() != other.getFadeIn()) ? false : ((getStay() != other.getStay()) ? false : (!(getFadeOut() != other.getFadeOut()))));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof TitleTimes;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getFadeIn();
    result = result * 59 + getStay();
    return result * 59 + getFadeOut();
  }
  
  public int getFadeIn() {
    return this.fadeIn;
  }
  
  public int getStay() {
    return this.stay;
  }
  
  public int getFadeOut() {
    return this.fadeOut;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    this.fadeIn = buf.readInt();
    this.stay = buf.readInt();
    this.fadeOut = buf.readInt();
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    buf.writeInt(this.fadeIn);
    buf.writeInt(this.stay);
    buf.writeInt(this.fadeOut);
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
