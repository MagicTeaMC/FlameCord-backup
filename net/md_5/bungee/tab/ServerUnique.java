package net.md_5.bungee.tab;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import net.md_5.bungee.protocol.packet.PlayerListItemRemove;
import net.md_5.bungee.protocol.packet.PlayerListItemUpdate;

public class ServerUnique extends TabList {
  private final Collection<UUID> uuids = new HashSet<>();
  
  private final Collection<String> usernames = new HashSet<>();
  
  public ServerUnique(ProxiedPlayer player) {
    super(player);
  }
  
  public void onUpdate(PlayerListItem playerListItem) {
    for (PlayerListItem.Item item : playerListItem.getItems()) {
      if (playerListItem.getAction() == PlayerListItem.Action.ADD_PLAYER) {
        if (item.getUuid() != null) {
          this.uuids.add(item.getUuid());
        } else {
          this.usernames.add(item.getUsername());
        } 
      } else if (playerListItem.getAction() == PlayerListItem.Action.REMOVE_PLAYER) {
        if (item.getUuid() != null) {
          this.uuids.remove(item.getUuid());
        } else {
          this.usernames.remove(item.getUsername());
        } 
      } 
    } 
    this.player.unsafe().sendPacket((DefinedPacket)playerListItem);
  }
  
  public void onUpdate(PlayerListItemRemove playerListItem) {
    for (UUID uuid : playerListItem.getUuids())
      this.uuids.remove(uuid); 
    this.player.unsafe().sendPacket((DefinedPacket)playerListItem);
  }
  
  public void onUpdate(PlayerListItemUpdate playerListItem) {
    for (PlayerListItem.Item item : playerListItem.getItems()) {
      for (PlayerListItemUpdate.Action action : playerListItem.getActions()) {
        if (action == PlayerListItemUpdate.Action.ADD_PLAYER)
          this.uuids.add(item.getUuid()); 
      } 
    } 
    this.player.unsafe().sendPacket((DefinedPacket)playerListItem);
  }
  
  public void onPingChange(int ping) {}
  
  public void onServerChange() {
    if (this.player.getPendingConnection().getVersion() >= 761) {
      PlayerListItemRemove packet = new PlayerListItemRemove();
      packet.setUuids((UUID[])this.uuids.stream().toArray(x$0 -> new UUID[x$0]));
      this.player.unsafe().sendPacket((DefinedPacket)packet);
    } else {
      PlayerListItem packet = new PlayerListItem();
      packet.setAction(PlayerListItem.Action.REMOVE_PLAYER);
      PlayerListItem.Item[] items = new PlayerListItem.Item[this.uuids.size() + this.usernames.size()];
      int i = 0;
      for (String username : this.usernames) {
        PlayerListItem.Item item = items[i++] = new PlayerListItem.Item();
        item.setUsername(username);
        item.setDisplayName(username);
        item.setPing(Integer.valueOf(0));
      } 
      for (UUID uuid : this.uuids) {
        PlayerListItem.Item item = items[i++] = new PlayerListItem.Item();
        item.setUuid(uuid);
      } 
      packet.setItems(items);
      if (ProtocolConstants.isAfterOrEq(this.player.getPendingConnection().getVersion(), 47)) {
        this.player.unsafe().sendPacket((DefinedPacket)packet);
      } else {
        for (PlayerListItem.Item item : packet.getItems()) {
          PlayerListItem p2 = new PlayerListItem();
          p2.setAction(packet.getAction());
          p2.setItems(new PlayerListItem.Item[] { item });
          this.player.unsafe().sendPacket((DefinedPacket)p2);
        } 
      } 
    } 
    this.uuids.clear();
    this.usernames.clear();
  }
  
  public void onConnect() {}
  
  public void onDisconnect() {}
}
