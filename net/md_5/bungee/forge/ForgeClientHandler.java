package net.md_5.bungee.forge;

import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.Map;
import lombok.NonNull;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class ForgeClientHandler {
  @NonNull
  private final UserConnection con;
  
  public ForgeClientHandler(@NonNull UserConnection con) {
    if (con == null)
      throw new NullPointerException("con is marked non-null but is null"); 
    this.con = con;
  }
  
  private boolean forgeOutdated = false;
  
  public boolean isForgeOutdated() {
    return this.forgeOutdated;
  }
  
  void setForgeOutdated(boolean forgeOutdated) {
    this.forgeOutdated = forgeOutdated;
  }
  
  private Map<String, String> clientModList = null;
  
  public Map<String, String> getClientModList() {
    return this.clientModList;
  }
  
  void setClientModList(Map<String, String> clientModList) {
    this.clientModList = clientModList;
  }
  
  private final ArrayDeque<PluginMessage> packetQueue = new ArrayDeque<>();
  
  @NonNull
  private ForgeClientHandshakeState state = ForgeClientHandshakeState.HELLO;
  
  void setState(@NonNull ForgeClientHandshakeState state) {
    if (state == null)
      throw new NullPointerException("state is marked non-null but is null"); 
    this.state = state;
  }
  
  private PluginMessage serverModList = null;
  
  private PluginMessage serverIdList = null;
  
  private boolean fmlTokenInHandshake = false;
  
  public boolean isFmlTokenInHandshake() {
    return this.fmlTokenInHandshake;
  }
  
  public void setFmlTokenInHandshake(boolean fmlTokenInHandshake) {
    this.fmlTokenInHandshake = fmlTokenInHandshake;
  }
  
  public void handle(PluginMessage message) throws IllegalArgumentException {
    if (!message.getTag().equalsIgnoreCase("FML|HS"))
      throw new IllegalArgumentException("Expecting a Forge Handshake packet."); 
    message.setAllowExtendedPacket(true);
    ForgeClientHandshakeState prevState = this.state;
    Preconditions.checkState((this.packetQueue.size() < 128), "Forge packet queue too big!");
    this.packetQueue.add(message);
    this.state = this.state.send(message, this.con);
    if (this.state != prevState)
      synchronized (this.packetQueue) {
        while (!this.packetQueue.isEmpty()) {
          ForgeLogger.logClient(ForgeLogger.LogDirection.SENDING, prevState.name(), this.packetQueue.getFirst());
          this.con.getForgeServerHandler().receive(this.packetQueue.removeFirst());
        } 
      }  
  }
  
  public void receive(PluginMessage message) throws IllegalArgumentException {
    this.state = this.state.handle(message, this.con);
  }
  
  public void resetHandshake() {
    this.state = ForgeClientHandshakeState.HELLO;
    if (this.con.getPendingConnection().getVersion() == 47)
      resetAllThePotions(this.con); 
    this.con.unsafe().sendPacket((DefinedPacket)ForgeConstants.FML_RESET_HANDSHAKE);
  }
  
  private void resetAllThePotions(UserConnection con) {
    con.getPotions().clear();
  }
  
  public void setServerModList(PluginMessage modList) throws IllegalArgumentException {
    if (!modList.getTag().equalsIgnoreCase("FML|HS") || modList.getData()[0] != 2)
      throw new IllegalArgumentException("modList"); 
    this.serverModList = modList;
  }
  
  public void setServerIdList(PluginMessage idList) throws IllegalArgumentException {
    if (!idList.getTag().equalsIgnoreCase("FML|HS") || idList.getData()[0] != 3)
      throw new IllegalArgumentException("idList"); 
    this.serverIdList = idList;
  }
  
  public boolean isHandshakeComplete() {
    return (this.state == ForgeClientHandshakeState.DONE);
  }
  
  public void setHandshakeComplete() {
    this.state = ForgeClientHandshakeState.DONE;
  }
  
  public boolean isForgeUser() {
    return (this.fmlTokenInHandshake || this.clientModList != null);
  }
  
  public boolean checkUserOutdated() {
    if (this.forgeOutdated)
      this.con.disconnect(BungeeCord.getInstance().getTranslation("connect_kick_outdated_forge", new Object[0])); 
    return this.forgeOutdated;
  }
}
