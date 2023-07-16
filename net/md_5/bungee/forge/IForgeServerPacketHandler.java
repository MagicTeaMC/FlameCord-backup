package net.md_5.bungee.forge;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.packet.PluginMessage;

public interface IForgeServerPacketHandler<S> {
  S handle(PluginMessage paramPluginMessage, ChannelWrapper paramChannelWrapper);
  
  S send(PluginMessage paramPluginMessage, UserConnection paramUserConnection);
}
