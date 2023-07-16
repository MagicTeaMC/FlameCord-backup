package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.UUID;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class PlayerListItemRemove extends DefinedPacket {
  private UUID[] uuids;
  
  public void setUuids(UUID[] uuids) {
    this.uuids = uuids;
  }
  
  public String toString() {
    return "PlayerListItemRemove(uuids=" + Arrays.deepToString((Object[])getUuids()) + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof PlayerListItemRemove))
      return false; 
    PlayerListItemRemove other = (PlayerListItemRemove)o;
    return !other.canEqual(this) ? false : (!!Arrays.deepEquals((Object[])getUuids(), (Object[])other.getUuids()));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof PlayerListItemRemove;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    return result * 59 + Arrays.deepHashCode((Object[])getUuids());
  }
  
  public UUID[] getUuids() {
    return this.uuids;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    this.uuids = new UUID[DefinedPacket.readVarInt(buf)];
    for (int i = 0; i < this.uuids.length; i++)
      this.uuids[i] = DefinedPacket.readUUID(buf); 
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    DefinedPacket.writeVarInt(this.uuids.length, buf);
    for (UUID uuid : this.uuids)
      DefinedPacket.writeUUID(uuid, buf); 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
