package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class ScoreboardDisplay extends DefinedPacket {
  private byte position;
  
  private String name;
  
  public void setPosition(byte position) {
    this.position = position;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String toString() {
    return "ScoreboardDisplay(position=" + getPosition() + ", name=" + getName() + ")";
  }
  
  public ScoreboardDisplay() {}
  
  public ScoreboardDisplay(byte position, String name) {
    this.position = position;
    this.name = name;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ScoreboardDisplay))
      return false; 
    ScoreboardDisplay other = (ScoreboardDisplay)o;
    if (!other.canEqual(this))
      return false; 
    if (getPosition() != other.getPosition())
      return false; 
    Object this$name = getName(), other$name = other.getName();
    return !((this$name == null) ? (other$name != null) : !this$name.equals(other$name));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ScoreboardDisplay;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getPosition();
    Object $name = getName();
    return result * 59 + (($name == null) ? 43 : $name.hashCode());
  }
  
  public byte getPosition() {
    return this.position;
  }
  
  public String getName() {
    return this.name;
  }
  
  public void read(ByteBuf buf) {
    this.position = buf.readByte();
    this.name = readString(buf);
  }
  
  public void write(ByteBuf buf) {
    buf.writeByte(this.position);
    writeString(this.name, buf);
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
