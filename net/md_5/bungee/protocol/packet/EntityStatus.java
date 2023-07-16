package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class EntityStatus extends DefinedPacket {
  public static final byte DEBUG_INFO_REDUCED = 22;
  
  public static final byte DEBUG_INFO_NORMAL = 23;
  
  private int entityId;
  
  private byte status;
  
  public void setEntityId(int entityId) {
    this.entityId = entityId;
  }
  
  public void setStatus(byte status) {
    this.status = status;
  }
  
  public String toString() {
    return "EntityStatus(entityId=" + getEntityId() + ", status=" + getStatus() + ")";
  }
  
  public EntityStatus() {}
  
  public EntityStatus(int entityId, byte status) {
    this.entityId = entityId;
    this.status = status;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof EntityStatus))
      return false; 
    EntityStatus other = (EntityStatus)o;
    return !other.canEqual(this) ? false : ((getEntityId() != other.getEntityId()) ? false : (!(getStatus() != other.getStatus())));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof EntityStatus;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getEntityId();
    return result * 59 + getStatus();
  }
  
  public int getEntityId() {
    return this.entityId;
  }
  
  public byte getStatus() {
    return this.status;
  }
  
  public void read(ByteBuf buf) {
    this.entityId = buf.readInt();
    this.status = buf.readByte();
  }
  
  public void write(ByteBuf buf) {
    buf.writeInt(this.entityId);
    buf.writeByte(this.status);
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
