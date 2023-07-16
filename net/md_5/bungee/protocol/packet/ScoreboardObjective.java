package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Locale;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class ScoreboardObjective extends DefinedPacket {
  private String name;
  
  private String value;
  
  private HealthDisplay type;
  
  private byte action;
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setValue(String value) {
    this.value = value;
  }
  
  public void setType(HealthDisplay type) {
    this.type = type;
  }
  
  public void setAction(byte action) {
    this.action = action;
  }
  
  public String toString() {
    return "ScoreboardObjective(name=" + getName() + ", value=" + getValue() + ", type=" + getType() + ", action=" + getAction() + ")";
  }
  
  public ScoreboardObjective() {}
  
  public ScoreboardObjective(String name, String value, HealthDisplay type, byte action) {
    this.name = name;
    this.value = value;
    this.type = type;
    this.action = action;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ScoreboardObjective))
      return false; 
    ScoreboardObjective other = (ScoreboardObjective)o;
    if (!other.canEqual(this))
      return false; 
    if (getAction() != other.getAction())
      return false; 
    Object this$name = getName(), other$name = other.getName();
    if ((this$name == null) ? (other$name != null) : !this$name.equals(other$name))
      return false; 
    Object this$value = getValue(), other$value = other.getValue();
    if ((this$value == null) ? (other$value != null) : !this$value.equals(other$value))
      return false; 
    Object this$type = getType(), other$type = other.getType();
    return !((this$type == null) ? (other$type != null) : !this$type.equals(other$type));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ScoreboardObjective;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getAction();
    Object $name = getName();
    result = result * 59 + (($name == null) ? 43 : $name.hashCode());
    Object $value = getValue();
    result = result * 59 + (($value == null) ? 43 : $value.hashCode());
    Object $type = getType();
    return result * 59 + (($type == null) ? 43 : $type.hashCode());
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public HealthDisplay getType() {
    return this.type;
  }
  
  public byte getAction() {
    return this.action;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (ProtocolConstants.isBeforeOrEq(protocolVersion, 5)) {
      this.name = readString(buf);
      this.value = readString(buf);
      this.action = buf.readByte();
      return;
    } 
    this.name = readString(buf);
    this.action = buf.readByte();
    if (this.action == 0 || this.action == 2) {
      this.value = readString(buf);
      if (protocolVersion >= 393) {
        this.type = HealthDisplay.values()[readVarInt(buf)];
      } else {
        this.type = HealthDisplay.fromString(readString(buf));
      } 
    } 
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (ProtocolConstants.isBeforeOrEq(protocolVersion, 5)) {
      writeString(this.name, buf);
      writeString(this.value, buf);
      buf.writeByte(this.action);
      return;
    } 
    writeString(this.name, buf);
    buf.writeByte(this.action);
    if (this.action == 0 || this.action == 2) {
      writeString(this.value, buf);
      if (protocolVersion >= 393) {
        writeVarInt(this.type.ordinal(), buf);
      } else {
        writeString(this.type.toString(), buf);
      } 
    } 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
  
  public enum HealthDisplay {
    INTEGER, HEARTS;
    
    public String toString() {
      return super.toString().toLowerCase(Locale.ROOT);
    }
    
    public static HealthDisplay fromString(String s) {
      return valueOf(s.toUpperCase(Locale.ROOT));
    }
  }
}
