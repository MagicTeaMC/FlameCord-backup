package net.md_5.bungee.forge;

import java.util.Map;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.PluginMessage;

enum ForgeClientHandshakeState implements IForgeClientPacketHandler<ForgeClientHandshakeState> {
  START {
    public ForgeClientHandshakeState handle(PluginMessage message, UserConnection con) {
      ForgeLogger.logClient(ForgeLogger.LogDirection.RECEIVED, name(), message);
      con.unsafe().sendPacket((DefinedPacket)message);
      con.getForgeClientHandler().setState(HELLO);
      return HELLO;
    }
    
    public ForgeClientHandshakeState send(PluginMessage message, UserConnection con) {
      return HELLO;
    }
  },
  HELLO {
    public ForgeClientHandshakeState handle(PluginMessage message, UserConnection con) {
      ForgeLogger.logClient(ForgeLogger.LogDirection.RECEIVED, name(), message);
      if (message.getData()[0] == 0)
        con.unsafe().sendPacket((DefinedPacket)message); 
      return this;
    }
    
    public ForgeClientHandshakeState send(PluginMessage message, UserConnection con) {
      if (message.getData()[0] == 1)
        return this; 
      if (message.getData()[0] == 2) {
        if (con.getForgeClientHandler().getClientModList() == null) {
          Map<String, String> clientModList = ForgeUtils.readModList(message);
          con.getForgeClientHandler().setClientModList(clientModList);
          if (ProtocolConstants.isBeforeOrEq(con.getPendingConnection().getVersion(), 5)) {
            int buildNumber = ForgeUtils.getFmlBuildNumber(clientModList);
            if (buildNumber < 1209 && buildNumber != 0)
              con.getForgeClientHandler().setForgeOutdated(true); 
          } 
        } 
        return WAITINGSERVERDATA;
      } 
      return this;
    }
  },
  WAITINGSERVERDATA {
    public ForgeClientHandshakeState handle(PluginMessage message, UserConnection con) {
      ForgeLogger.logClient(ForgeLogger.LogDirection.RECEIVED, name(), message);
      if (message.getData()[0] == 2)
        con.unsafe().sendPacket((DefinedPacket)message); 
      return this;
    }
    
    public ForgeClientHandshakeState send(PluginMessage message, UserConnection con) {
      return WAITINGSERVERCOMPLETE;
    }
  },
  WAITINGSERVERCOMPLETE {
    public ForgeClientHandshakeState handle(PluginMessage message, UserConnection con) {
      ForgeLogger.logClient(ForgeLogger.LogDirection.RECEIVED, name(), message);
      if (message.getData()[0] == 3) {
        con.unsafe().sendPacket((DefinedPacket)message);
        return this;
      } 
      con.unsafe().sendPacket((DefinedPacket)message);
      return this;
    }
    
    public ForgeClientHandshakeState send(PluginMessage message, UserConnection con) {
      return PENDINGCOMPLETE;
    }
  },
  PENDINGCOMPLETE {
    public ForgeClientHandshakeState handle(PluginMessage message, UserConnection con) {
      if (message.getData()[0] == -1) {
        ForgeLogger.logClient(ForgeLogger.LogDirection.RECEIVED, name(), message);
        con.unsafe().sendPacket((DefinedPacket)message);
      } 
      return this;
    }
    
    public ForgeClientHandshakeState send(PluginMessage message, UserConnection con) {
      return COMPLETE;
    }
  },
  COMPLETE {
    public ForgeClientHandshakeState handle(PluginMessage message, UserConnection con) {
      if ((message.getTag().equals("FML|HS") && message.getData()[0] == -1) || message
        .getTag().equals("FORGE")) {
        ForgeLogger.logClient(ForgeLogger.LogDirection.RECEIVED, name(), message);
        con.unsafe().sendPacket((DefinedPacket)message);
      } 
      return this;
    }
    
    public ForgeClientHandshakeState send(PluginMessage message, UserConnection con) {
      return DONE;
    }
  },
  DONE {
    public ForgeClientHandshakeState handle(PluginMessage message, UserConnection con) {
      ForgeLogger.logClient(ForgeLogger.LogDirection.RECEIVED, name(), message);
      if (message.getTag().equals("FORGE"))
        con.unsafe().sendPacket((DefinedPacket)message); 
      return this;
    }
    
    public ForgeClientHandshakeState send(PluginMessage message, UserConnection con) {
      return this;
    }
  };
}
