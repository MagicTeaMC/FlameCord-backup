package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class GameState extends DefinedPacket {
  public static final short IMMEDIATE_RESPAWN = 11;
  
  private short state;
  
  private float value;
  
  public void setState(short state) {
    this.state = state;
  }
  
  public void setValue(float value) {
    this.value = value;
  }
  
  public String toString() {
    return "GameState(state=" + getState() + ", value=" + getValue() + ")";
  }
  
  public GameState() {}
  
  public GameState(short state, float value) {
    this.state = state;
    this.value = value;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof GameState))
      return false; 
    GameState other = (GameState)o;
    return !other.canEqual(this) ? false : ((getState() != other.getState()) ? false : (!(Float.compare(getValue(), other.getValue()) != 0)));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof GameState;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getState();
    return result * 59 + Float.floatToIntBits(getValue());
  }
  
  public short getState() {
    return this.state;
  }
  
  public float getValue() {
    return this.value;
  }
  
  public void read(ByteBuf buf) {
    this.state = buf.readUnsignedByte();
    this.value = buf.readFloat();
  }
  
  public void write(ByteBuf buf) {
    buf.writeByte(this.state);
    buf.writeFloat(this.value);
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
