package net.md_5.bungee.forge;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.packet.PluginMessage;

public enum ForgeServerHandshakeState implements IForgeServerPacketHandler<ForgeServerHandshakeState> {
  START {
    public ForgeServerHandshakeState handle(PluginMessage message, ChannelWrapper ch) {
      ForgeLogger.logServer(ForgeLogger.LogDirection.RECEIVED, name(), message);
      ch.write(message);
      return this;
    }
    
    public ForgeServerHandshakeState send(PluginMessage message, UserConnection con) {
      return HELLO;
    }
  },
  HELLO {
    public ForgeServerHandshakeState handle(PluginMessage message, ChannelWrapper ch) {
      ForgeLogger.logServer(ForgeLogger.LogDirection.RECEIVED, name(), message);
      if (message.getData()[0] == 1)
        ch.write(message); 
      if (message.getData()[0] == 2)
        ch.write(message); 
      return this;
    }
    
    public ForgeServerHandshakeState send(PluginMessage message, UserConnection con) {
      return WAITINGCACK;
    }
  },
  WAITINGCACK {
    public ForgeServerHandshakeState handle(PluginMessage message, ChannelWrapper ch) {
      ForgeLogger.logServer(ForgeLogger.LogDirection.RECEIVED, name(), message);
      ch.write(message);
      return this;
    }
    
    public ForgeServerHandshakeState send(PluginMessage message, UserConnection con) {
      if (message.getData()[0] == 3 && message.getTag().equals("FML|HS")) {
        con.getForgeClientHandler().setServerIdList(message);
        return this;
      } 
      if (message.getData()[0] == -1 && message.getTag().equals("FML|HS"))
        return this; 
      if (message.getTag().equals("FORGE"))
        return COMPLETE; 
      return this;
    }
  },
  COMPLETE {
    public ForgeServerHandshakeState handle(PluginMessage message, ChannelWrapper ch) {
      ForgeLogger.logServer(ForgeLogger.LogDirection.RECEIVED, name(), message);
      ch.write(message);
      return this;
    }
    
    public ForgeServerHandshakeState send(PluginMessage message, UserConnection con) {
      return DONE;
    }
  },
  DONE {
    public ForgeServerHandshakeState handle(PluginMessage message, ChannelWrapper ch) {
      ForgeLogger.logServer(ForgeLogger.LogDirection.RECEIVED, name(), message);
      ch.write(message);
      return this;
    }
    
    public ForgeServerHandshakeState send(PluginMessage message, UserConnection con) {
      return this;
    }
  };
}
