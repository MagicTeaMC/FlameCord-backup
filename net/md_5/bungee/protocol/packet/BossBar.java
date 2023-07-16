package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class BossBar extends DefinedPacket {
  private UUID uuid;
  
  private int action;
  
  private String title;
  
  private float health;
  
  private int color;
  
  private int division;
  
  private byte flags;
  
  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }
  
  public void setAction(int action) {
    this.action = action;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public void setHealth(float health) {
    this.health = health;
  }
  
  public void setColor(int color) {
    this.color = color;
  }
  
  public void setDivision(int division) {
    this.division = division;
  }
  
  public void setFlags(byte flags) {
    this.flags = flags;
  }
  
  public String toString() {
    return "BossBar(uuid=" + getUuid() + ", action=" + getAction() + ", title=" + getTitle() + ", health=" + getHealth() + ", color=" + getColor() + ", division=" + getDivision() + ", flags=" + getFlags() + ")";
  }
  
  public BossBar() {}
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof BossBar))
      return false; 
    BossBar other = (BossBar)o;
    if (!other.canEqual(this))
      return false; 
    if (getAction() != other.getAction())
      return false; 
    if (Float.compare(getHealth(), other.getHealth()) != 0)
      return false; 
    if (getColor() != other.getColor())
      return false; 
    if (getDivision() != other.getDivision())
      return false; 
    if (getFlags() != other.getFlags())
      return false; 
    Object this$uuid = getUuid(), other$uuid = other.getUuid();
    if ((this$uuid == null) ? (other$uuid != null) : !this$uuid.equals(other$uuid))
      return false; 
    Object this$title = getTitle(), other$title = other.getTitle();
    return !((this$title == null) ? (other$title != null) : !this$title.equals(other$title));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof BossBar;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getAction();
    result = result * 59 + Float.floatToIntBits(getHealth());
    result = result * 59 + getColor();
    result = result * 59 + getDivision();
    result = result * 59 + getFlags();
    Object $uuid = getUuid();
    result = result * 59 + (($uuid == null) ? 43 : $uuid.hashCode());
    Object $title = getTitle();
    return result * 59 + (($title == null) ? 43 : $title.hashCode());
  }
  
  public UUID getUuid() {
    return this.uuid;
  }
  
  public int getAction() {
    return this.action;
  }
  
  public String getTitle() {
    return this.title;
  }
  
  public float getHealth() {
    return this.health;
  }
  
  public int getColor() {
    return this.color;
  }
  
  public int getDivision() {
    return this.division;
  }
  
  public byte getFlags() {
    return this.flags;
  }
  
  public BossBar(UUID uuid, int action) {
    this.uuid = uuid;
    this.action = action;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    this.uuid = readUUID(buf);
    this.action = readVarInt(buf);
    switch (this.action) {
      case 0:
        this.title = readString(buf);
        this.health = buf.readFloat();
        this.color = readVarInt(buf);
        this.division = readVarInt(buf);
        this.flags = buf.readByte();
        break;
      case 2:
        this.health = buf.readFloat();
        break;
      case 3:
        this.title = readString(buf);
        break;
      case 4:
        this.color = readVarInt(buf);
        this.division = readVarInt(buf);
        break;
      case 5:
        this.flags = buf.readByte();
        break;
    } 
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    writeUUID(this.uuid, buf);
    writeVarInt(this.action, buf);
    switch (this.action) {
      case 0:
        writeString(this.title, buf);
        buf.writeFloat(this.health);
        writeVarInt(this.color, buf);
        writeVarInt(this.division, buf);
        buf.writeByte(this.flags);
        break;
      case 2:
        buf.writeFloat(this.health);
        break;
      case 3:
        writeString(this.title, buf);
        break;
      case 4:
        writeVarInt(this.color, buf);
        writeVarInt(this.division, buf);
        break;
      case 5:
        buf.writeByte(this.flags);
        break;
    } 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
