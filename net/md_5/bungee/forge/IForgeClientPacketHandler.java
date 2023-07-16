package net.md_5.bungee.forge;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.protocol.packet.PluginMessage;

public interface IForgeClientPacketHandler<S> {
  S handle(PluginMessage paramPluginMessage, UserConnection paramUserConnection);
  
  S send(PluginMessage paramPluginMessage, UserConnection paramUserConnection);
}
