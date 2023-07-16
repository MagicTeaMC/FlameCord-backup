package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.UUID;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PlayerPublicKey;
import net.md_5.bungee.protocol.Property;
import net.md_5.bungee.protocol.ProtocolConstants;

public class PlayerListItem extends DefinedPacket {
  private Action action;
  
  private Item[] items;
  
  public void setAction(Action action) {
    this.action = action;
  }
  
  public void setItems(Item[] items) {
    this.items = items;
  }
  
  public String toString() {
    return "PlayerListItem(action=" + getAction() + ", items=" + Arrays.deepToString((Object[])getItems()) + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof PlayerListItem))
      return false; 
    PlayerListItem other = (PlayerListItem)o;
    if (!other.canEqual(this))
      return false; 
    Object this$action = getAction(), other$action = other.getAction();
    return ((this$action == null) ? (other$action != null) : !this$action.equals(other$action)) ? false : (!!Arrays.deepEquals((Object[])getItems(), (Object[])other.getItems()));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof PlayerListItem;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $action = getAction();
    result = result * 59 + (($action == null) ? 43 : $action.hashCode());
    return result * 59 + Arrays.deepHashCode((Object[])getItems());
  }
  
  public Action getAction() {
    return this.action;
  }
  
  public Item[] getItems() {
    return this.items;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (ProtocolConstants.isBeforeOrEq(protocolVersion, 5)) {
      this.items = new Item[1];
      Item item = this.items[0] = new Item();
      item.displayName = item.username = readString(buf);
      this.action = !buf.readBoolean() ? Action.REMOVE_PLAYER : Action.ADD_PLAYER;
      item.ping = Integer.valueOf(buf.readShort());
      return;
    } 
    this.action = Action.values()[DefinedPacket.readVarInt(buf)];
    this.items = new Item[DefinedPacket.readVarInt(buf)];
    for (int i = 0; i < this.items.length; i++) {
      Item item = this.items[i] = new Item();
      item.setUuid(DefinedPacket.readUUID(buf));
      switch (this.action) {
        case ADD_PLAYER:
          item.username = DefinedPacket.readString(buf);
          item.properties = DefinedPacket.readProperties(buf);
          item.gamemode = Integer.valueOf(DefinedPacket.readVarInt(buf));
          item.ping = Integer.valueOf(DefinedPacket.readVarInt(buf));
          if (buf.readBoolean())
            item.displayName = DefinedPacket.readString(buf); 
          if (protocolVersion >= 759)
            item.publicKey = readPublicKey(buf); 
          break;
        case UPDATE_GAMEMODE:
          item.gamemode = Integer.valueOf(DefinedPacket.readVarInt(buf));
          break;
        case UPDATE_LATENCY:
          item.ping = Integer.valueOf(DefinedPacket.readVarInt(buf));
          break;
        case UPDATE_DISPLAY_NAME:
          if (buf.readBoolean())
            item.displayName = DefinedPacket.readString(buf); 
          break;
      } 
    } 
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (ProtocolConstants.isBeforeOrEq(protocolVersion, 5)) {
      Item item = this.items[0];
      writeString(item.displayName, buf);
      buf.writeBoolean((this.action != Action.REMOVE_PLAYER));
      buf.writeShort(item.ping.intValue());
      return;
    } 
    DefinedPacket.writeVarInt(this.action.ordinal(), buf);
    DefinedPacket.writeVarInt(this.items.length, buf);
    for (Item item : this.items) {
      DefinedPacket.writeUUID(item.uuid, buf);
      switch (this.action) {
        case ADD_PLAYER:
          DefinedPacket.writeString(item.username, buf);
          DefinedPacket.writeProperties(item.properties, buf);
          DefinedPacket.writeVarInt(item.gamemode.intValue(), buf);
          DefinedPacket.writeVarInt(item.ping.intValue(), buf);
          buf.writeBoolean((item.displayName != null));
          if (item.displayName != null)
            DefinedPacket.writeString(item.displayName, buf); 
          if (protocolVersion >= 759)
            writePublicKey(item.publicKey, buf); 
          break;
        case UPDATE_GAMEMODE:
          DefinedPacket.writeVarInt(item.gamemode.intValue(), buf);
          break;
        case UPDATE_LATENCY:
          DefinedPacket.writeVarInt(item.ping.intValue(), buf);
          break;
        case UPDATE_DISPLAY_NAME:
          buf.writeBoolean((item.displayName != null));
          if (item.displayName != null)
            DefinedPacket.writeString(item.displayName, buf); 
          break;
      } 
    } 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
  
  public enum Action {
    ADD_PLAYER, UPDATE_GAMEMODE, UPDATE_LATENCY, UPDATE_DISPLAY_NAME, REMOVE_PLAYER;
  }
  
  public static class Item {
    UUID uuid;
    
    String username;
    
    Property[] properties;
    
    UUID chatSessionId;
    
    PlayerPublicKey publicKey;
    
    Boolean listed;
    
    Integer gamemode;
    
    Integer ping;
    
    String displayName;
    
    public void setUuid(UUID uuid) {
      this.uuid = uuid;
    }
    
    public void setUsername(String username) {
      this.username = username;
    }
    
    public void setProperties(Property[] properties) {
      this.properties = properties;
    }
    
    public void setChatSessionId(UUID chatSessionId) {
      this.chatSessionId = chatSessionId;
    }
    
    public void setPublicKey(PlayerPublicKey publicKey) {
      this.publicKey = publicKey;
    }
    
    public void setListed(Boolean listed) {
      this.listed = listed;
    }
    
    public void setGamemode(Integer gamemode) {
      this.gamemode = gamemode;
    }
    
    public void setPing(Integer ping) {
      this.ping = ping;
    }
    
    public void setDisplayName(String displayName) {
      this.displayName = displayName;
    }
    
    public boolean equals(Object o) {
      if (o == this)
        return true; 
      if (!(o instanceof Item))
        return false; 
      Item other = (Item)o;
      if (!other.canEqual(this))
        return false; 
      Object this$listed = getListed(), other$listed = other.getListed();
      if ((this$listed == null) ? (other$listed != null) : !this$listed.equals(other$listed))
        return false; 
      Object this$gamemode = getGamemode(), other$gamemode = other.getGamemode();
      if ((this$gamemode == null) ? (other$gamemode != null) : !this$gamemode.equals(other$gamemode))
        return false; 
      Object this$ping = getPing(), other$ping = other.getPing();
      if ((this$ping == null) ? (other$ping != null) : !this$ping.equals(other$ping))
        return false; 
      Object this$uuid = getUuid(), other$uuid = other.getUuid();
      if ((this$uuid == null) ? (other$uuid != null) : !this$uuid.equals(other$uuid))
        return false; 
      Object this$username = getUsername(), other$username = other.getUsername();
      if ((this$username == null) ? (other$username != null) : !this$username.equals(other$username))
        return false; 
      if (!Arrays.deepEquals((Object[])getProperties(), (Object[])other.getProperties()))
        return false; 
      Object this$chatSessionId = getChatSessionId(), other$chatSessionId = other.getChatSessionId();
      if ((this$chatSessionId == null) ? (other$chatSessionId != null) : !this$chatSessionId.equals(other$chatSessionId))
        return false; 
      Object this$publicKey = getPublicKey(), other$publicKey = other.getPublicKey();
      if ((this$publicKey == null) ? (other$publicKey != null) : !this$publicKey.equals(other$publicKey))
        return false; 
      Object this$displayName = getDisplayName(), other$displayName = other.getDisplayName();
      return !((this$displayName == null) ? (other$displayName != null) : !this$displayName.equals(other$displayName));
    }
    
    protected boolean canEqual(Object other) {
      return other instanceof Item;
    }
    
    public int hashCode() {
      int PRIME = 59;
      result = 1;
      Object $listed = getListed();
      result = result * 59 + (($listed == null) ? 43 : $listed.hashCode());
      Object $gamemode = getGamemode();
      result = result * 59 + (($gamemode == null) ? 43 : $gamemode.hashCode());
      Object $ping = getPing();
      result = result * 59 + (($ping == null) ? 43 : $ping.hashCode());
      Object $uuid = getUuid();
      result = result * 59 + (($uuid == null) ? 43 : $uuid.hashCode());
      Object $username = getUsername();
      result = result * 59 + (($username == null) ? 43 : $username.hashCode());
      result = result * 59 + Arrays.deepHashCode((Object[])getProperties());
      Object $chatSessionId = getChatSessionId();
      result = result * 59 + (($chatSessionId == null) ? 43 : $chatSessionId.hashCode());
      Object $publicKey = getPublicKey();
      result = result * 59 + (($publicKey == null) ? 43 : $publicKey.hashCode());
      Object $displayName = getDisplayName();
      return result * 59 + (($displayName == null) ? 43 : $displayName.hashCode());
    }
    
    public String toString() {
      return "PlayerListItem.Item(uuid=" + getUuid() + ", username=" + getUsername() + ", properties=" + Arrays.deepToString((Object[])getProperties()) + ", chatSessionId=" + getChatSessionId() + ", publicKey=" + getPublicKey() + ", listed=" + getListed() + ", gamemode=" + getGamemode() + ", ping=" + getPing() + ", displayName=" + getDisplayName() + ")";
    }
    
    public UUID getUuid() {
      return this.uuid;
    }
    
    public String getUsername() {
      return this.username;
    }
    
    public Property[] getProperties() {
      return this.properties;
    }
    
    public UUID getChatSessionId() {
      return this.chatSessionId;
    }
    
    public PlayerPublicKey getPublicKey() {
      return this.publicKey;
    }
    
    public Boolean getListed() {
      return this.listed;
    }
    
    public Integer getGamemode() {
      return this.gamemode;
    }
    
    public Integer getPing() {
      return this.ping;
    }
    
    public String getDisplayName() {
      return this.displayName;
    }
  }
}
