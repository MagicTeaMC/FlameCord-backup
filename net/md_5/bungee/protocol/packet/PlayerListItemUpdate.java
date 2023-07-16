package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.EnumSet;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PlayerPublicKey;
import net.md_5.bungee.protocol.ProtocolConstants;

public class PlayerListItemUpdate extends DefinedPacket {
  private EnumSet<Action> actions;
  
  private PlayerListItem.Item[] items;
  
  public void setActions(EnumSet<Action> actions) {
    this.actions = actions;
  }
  
  public void setItems(PlayerListItem.Item[] items) {
    this.items = items;
  }
  
  public String toString() {
    return "PlayerListItemUpdate(actions=" + getActions() + ", items=" + Arrays.deepToString((Object[])getItems()) + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof PlayerListItemUpdate))
      return false; 
    PlayerListItemUpdate other = (PlayerListItemUpdate)o;
    if (!other.canEqual(this))
      return false; 
    Object<Action> this$actions = (Object<Action>)getActions(), other$actions = (Object<Action>)other.getActions();
    return ((this$actions == null) ? (other$actions != null) : !this$actions.equals(other$actions)) ? false : (!!Arrays.deepEquals((Object[])getItems(), (Object[])other.getItems()));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof PlayerListItemUpdate;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object<Action> $actions = (Object<Action>)getActions();
    result = result * 59 + (($actions == null) ? 43 : $actions.hashCode());
    return result * 59 + Arrays.deepHashCode((Object[])getItems());
  }
  
  public EnumSet<Action> getActions() {
    return this.actions;
  }
  
  public PlayerListItem.Item[] getItems() {
    return this.items;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    this.actions = readEnumSet(Action.class, buf);
    this.items = new PlayerListItem.Item[DefinedPacket.readVarInt(buf)];
    for (int i = 0; i < this.items.length; i++) {
      PlayerListItem.Item item = this.items[i] = new PlayerListItem.Item();
      item.setUuid(DefinedPacket.readUUID(buf));
      for (Action action : this.actions) {
        switch (action) {
          case ADD_PLAYER:
            item.username = DefinedPacket.readString(buf);
            item.properties = DefinedPacket.readProperties(buf);
          case INITIALIZE_CHAT:
            if (buf.readBoolean()) {
              item.chatSessionId = readUUID(buf);
              item.publicKey = new PlayerPublicKey(buf.readLong(), readArray(buf, 512), readArray(buf, 4096));
            } 
          case UPDATE_GAMEMODE:
            item.gamemode = Integer.valueOf(DefinedPacket.readVarInt(buf));
          case UPDATE_LISTED:
            item.listed = Boolean.valueOf(buf.readBoolean());
          case UPDATE_LATENCY:
            item.ping = Integer.valueOf(DefinedPacket.readVarInt(buf));
          case UPDATE_DISPLAY_NAME:
            if (buf.readBoolean())
              item.displayName = DefinedPacket.readString(buf); 
        } 
      } 
    } 
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    DefinedPacket.writeEnumSet(this.actions, Action.class, buf);
    DefinedPacket.writeVarInt(this.items.length, buf);
    for (PlayerListItem.Item item : this.items) {
      DefinedPacket.writeUUID(item.uuid, buf);
      for (Action action : this.actions) {
        switch (action) {
          case ADD_PLAYER:
            DefinedPacket.writeString(item.username, buf);
            DefinedPacket.writeProperties(item.properties, buf);
          case INITIALIZE_CHAT:
            buf.writeBoolean((item.chatSessionId != null));
            if (item.chatSessionId != null) {
              writeUUID(item.chatSessionId, buf);
              buf.writeLong(item.publicKey.getExpiry());
              writeArray(item.publicKey.getKey(), buf);
              writeArray(item.publicKey.getSignature(), buf);
            } 
          case UPDATE_GAMEMODE:
            DefinedPacket.writeVarInt(item.gamemode.intValue(), buf);
          case UPDATE_LISTED:
            buf.writeBoolean(item.listed.booleanValue());
          case UPDATE_LATENCY:
            DefinedPacket.writeVarInt(item.ping.intValue(), buf);
          case UPDATE_DISPLAY_NAME:
            buf.writeBoolean((item.displayName != null));
            if (item.displayName != null)
              DefinedPacket.writeString(item.displayName, buf); 
        } 
      } 
    } 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
  
  public enum Action {
    ADD_PLAYER, INITIALIZE_CHAT, UPDATE_GAMEMODE, UPDATE_LISTED, UPDATE_LATENCY, UPDATE_DISPLAY_NAME;
  }
}
