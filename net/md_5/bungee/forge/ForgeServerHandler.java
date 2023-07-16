package net.md_5.bungee.forge;

import java.util.ArrayDeque;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class ForgeServerHandler {
  private final UserConnection con;
  
  private final ChannelWrapper ch;
  
  private final ServerInfo serverInfo;
  
  private ForgeServerHandshakeState state;
  
  private boolean serverForge;
  
  private final ArrayDeque<PluginMessage> packetQueue;
  
  public ForgeServerHandler(UserConnection con, ChannelWrapper ch, ServerInfo serverInfo) {
    this.state = ForgeServerHandshakeState.START;
    this.serverForge = false;
    this.packetQueue = new ArrayDeque<>();
    this.con = con;
    this.ch = ch;
    this.serverInfo = serverInfo;
  }
  
  public ChannelWrapper getCh() {
    return this.ch;
  }
  
  ServerInfo getServerInfo() {
    return this.serverInfo;
  }
  
  public ForgeServerHandshakeState getState() {
    return this.state;
  }
  
  public boolean isServerForge() {
    return this.serverForge;
  }
  
  public void handle(PluginMessage message) throws IllegalArgumentException {
    if (!message.getTag().equalsIgnoreCase("FML|HS") && !message.getTag().equalsIgnoreCase("FORGE"))
      throw new IllegalArgumentException("Expecting a Forge REGISTER or FML Handshake packet."); 
    message.setAllowExtendedPacket(true);
    ForgeServerHandshakeState prevState = this.state;
    this.packetQueue.add(message);
    this.state = this.state.send(message, this.con);
    if (this.state == ForgeServerHandshakeState.DONE || this.state != prevState)
      synchronized (this.packetQueue) {
        while (!this.packetQueue.isEmpty()) {
          ForgeLogger.logServer(ForgeLogger.LogDirection.SENDING, prevState.name(), this.packetQueue.getFirst());
          this.con.getForgeClientHandler().receive(this.packetQueue.removeFirst());
        } 
      }  
  }
  
  public void receive(PluginMessage message) throws IllegalArgumentException {
    this.state = this.state.handle(message, this.ch);
  }
  
  public void setServerAsForgeServer() {
    this.serverForge = true;
  }
}
