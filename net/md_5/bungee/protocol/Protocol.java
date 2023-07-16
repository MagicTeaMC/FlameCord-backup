package net.md_5.bungee.protocol;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Supplier;
import net.md_5.bungee.protocol.packet.BossBar;
import net.md_5.bungee.protocol.packet.Chat;
import net.md_5.bungee.protocol.packet.ClearTitles;
import net.md_5.bungee.protocol.packet.ClientChat;
import net.md_5.bungee.protocol.packet.ClientCommand;
import net.md_5.bungee.protocol.packet.ClientSettings;
import net.md_5.bungee.protocol.packet.Commands;
import net.md_5.bungee.protocol.packet.EncryptionRequest;
import net.md_5.bungee.protocol.packet.EncryptionResponse;
import net.md_5.bungee.protocol.packet.EntityStatus;
import net.md_5.bungee.protocol.packet.GameState;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.Login;
import net.md_5.bungee.protocol.packet.LoginPayloadRequest;
import net.md_5.bungee.protocol.packet.LoginPayloadResponse;
import net.md_5.bungee.protocol.packet.LoginRequest;
import net.md_5.bungee.protocol.packet.LoginSuccess;
import net.md_5.bungee.protocol.packet.PingPacket;
import net.md_5.bungee.protocol.packet.PlayerListHeaderFooter;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import net.md_5.bungee.protocol.packet.PlayerListItemRemove;
import net.md_5.bungee.protocol.packet.PlayerListItemUpdate;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.Respawn;
import net.md_5.bungee.protocol.packet.ScoreboardDisplay;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;
import net.md_5.bungee.protocol.packet.ScoreboardScore;
import net.md_5.bungee.protocol.packet.ServerData;
import net.md_5.bungee.protocol.packet.SetCompression;
import net.md_5.bungee.protocol.packet.StatusRequest;
import net.md_5.bungee.protocol.packet.StatusResponse;
import net.md_5.bungee.protocol.packet.Subtitle;
import net.md_5.bungee.protocol.packet.SystemChat;
import net.md_5.bungee.protocol.packet.TabCompleteRequest;
import net.md_5.bungee.protocol.packet.TabCompleteResponse;
import net.md_5.bungee.protocol.packet.Team;
import net.md_5.bungee.protocol.packet.Title;
import net.md_5.bungee.protocol.packet.TitleTimes;
import net.md_5.bungee.protocol.packet.ViewDistance;

public enum Protocol {
  HANDSHAKE {
    Protocol() {
      this.TO_SERVER.registerPacket((Class)Handshake.class, Handshake::new, new ProtocolMapping[] { access$100(4, 0) });
    }
  },
  GAME {
    Protocol() {
      this.TO_CLIENT.registerPacket((Class)KeepAlive.class, KeepAlive::new, new ProtocolMapping[] { 
            access$100(4, 0), 
            access$100(107, 31), 
            access$100(393, 33), 
            access$100(477, 32), 
            access$100(573, 33), 
            access$100(735, 32), 
            access$100(751, 31), 
            access$100(755, 33), 
            access$100(759, 30), 
            access$100(760, 32), 
            access$100(761, 31), 
            access$100(762, 35) });
      this.TO_CLIENT.registerPacket((Class)Login.class, Login::new, new ProtocolMapping[] { 
            access$100(4, 1), 
            access$100(107, 35), 
            access$100(393, 37), 
            access$100(573, 38), 
            access$100(735, 37), 
            access$100(751, 36), 
            access$100(755, 38), 
            access$100(759, 35), 
            access$100(760, 37), 
            access$100(761, 36), 
            access$100(762, 40) });
      this.TO_CLIENT.registerPacket((Class)Chat.class, Chat::new, new ProtocolMapping[] { access$100(4, 2), 
            access$100(107, 15), 
            access$100(393, 14), 
            access$100(573, 15), 
            access$100(735, 14), 
            access$100(755, 15), 
            access$100(759, -1) });
      this.TO_CLIENT.registerPacket((Class)Respawn.class, Respawn::new, new ProtocolMapping[] { 
            access$100(4, 7), 
            access$100(107, 51), 
            access$100(335, 52), 
            access$100(338, 53), 
            access$100(393, 56), 
            access$100(477, 58), 
            access$100(573, 59), 
            access$100(735, 58), 
            access$100(751, 57), 
            access$100(755, 61), 
            access$100(759, 59), 
            access$100(760, 62), 
            access$100(761, 61), 
            access$100(762, 65) });
      this.TO_CLIENT.registerPacket((Class)BossBar.class, BossBar::new, new ProtocolMapping[] { access$100(107, 12), 
            access$100(573, 13), 
            access$100(735, 12), 
            access$100(755, 13), 
            access$100(759, 10), 
            access$100(762, 11) });
      this.TO_CLIENT.registerPacket((Class)PlayerListItem.class, PlayerListItem::new, new ProtocolMapping[] { 
            access$100(4, 56), 
            access$100(107, 45), 
            access$100(338, 46), 
            access$100(393, 48), 
            access$100(477, 51), 
            access$100(573, 52), 
            access$100(735, 51), 
            access$100(751, 50), 
            access$100(755, 54), 
            access$100(759, 52), 
            access$100(760, 55), 
            access$100(761, -1) });
      this.TO_CLIENT.registerPacket((Class)TabCompleteResponse.class, TabCompleteResponse::new, new ProtocolMapping[] { access$100(4, 58), 
            access$100(107, 14), 
            access$100(393, 16), 
            access$100(573, 17), 
            access$100(735, 16), 
            access$100(751, 15), 
            access$100(755, 17), 
            access$100(759, 14), 
            access$100(761, 13), 
            access$100(762, 15) });
      this.TO_CLIENT.registerPacket((Class)ScoreboardObjective.class, ScoreboardObjective::new, new ProtocolMapping[] { 
            access$100(4, 59), 
            access$100(107, 63), 
            access$100(335, 65), 
            access$100(338, 66), 
            access$100(393, 69), 
            access$100(477, 73), 
            access$100(573, 74), 
            access$100(755, 83), 
            access$100(760, 86), 
            access$100(761, 84), 
            access$100(762, 88) });
      this.TO_CLIENT.registerPacket((Class)ScoreboardScore.class, ScoreboardScore::new, new ProtocolMapping[] { 
            access$100(4, 60), 
            access$100(107, 66), 
            access$100(335, 68), 
            access$100(338, 69), 
            access$100(393, 72), 
            access$100(477, 76), 
            access$100(573, 77), 
            access$100(755, 86), 
            access$100(760, 89), 
            access$100(761, 87), 
            access$100(762, 91) });
      this.TO_CLIENT.registerPacket((Class)ScoreboardDisplay.class, ScoreboardDisplay::new, new ProtocolMapping[] { 
            access$100(4, 61), 
            access$100(107, 56), 
            access$100(335, 58), 
            access$100(338, 59), 
            access$100(393, 62), 
            access$100(477, 66), 
            access$100(573, 67), 
            access$100(755, 76), 
            access$100(760, 79), 
            access$100(761, 77), 
            access$100(762, 81) });
      this.TO_CLIENT.registerPacket((Class)Team.class, Team::new, new ProtocolMapping[] { 
            access$100(4, 62), 
            access$100(107, 65), 
            access$100(335, 67), 
            access$100(338, 68), 
            access$100(393, 71), 
            access$100(477, 75), 
            access$100(573, 76), 
            access$100(755, 85), 
            access$100(760, 88), 
            access$100(761, 86), 
            access$100(762, 90) });
      this.TO_CLIENT.registerPacket((Class)PluginMessage.class, PluginMessage::new, new ProtocolMapping[] { 
            access$100(4, 63), 
            access$100(107, 24), 
            access$100(393, 25), 
            access$100(477, 24), 
            access$100(573, 25), 
            access$100(735, 24), 
            access$100(751, 23), 
            access$100(755, 24), 
            access$100(759, 21), 
            access$100(760, 22), 
            access$100(761, 21), 
            access$100(762, 23) });
      this.TO_CLIENT.registerPacket((Class)Kick.class, Kick::new, new ProtocolMapping[] { 
            access$100(4, 64), 
            access$100(107, 26), 
            access$100(393, 27), 
            access$100(477, 26), 
            access$100(573, 27), 
            access$100(735, 26), 
            access$100(751, 25), 
            access$100(755, 26), 
            access$100(759, 23), 
            access$100(760, 25), 
            access$100(761, 23), 
            access$100(762, 26) });
      this.TO_CLIENT.registerPacket((Class)Title.class, Title::new, new ProtocolMapping[] { 
            access$100(4, 69), 
            access$100(335, 71), 
            access$100(338, 72), 
            access$100(393, 75), 
            access$100(477, 79), 
            access$100(573, 80), 
            access$100(735, 79), 
            access$100(755, 89), 
            access$100(757, 90), 
            access$100(760, 93), 
            access$100(761, 91), 
            access$100(762, 95) });
      this.TO_CLIENT.registerPacket((Class)ClearTitles.class, ClearTitles::new, new ProtocolMapping[] { access$100(755, 16), 
            access$100(759, 13), 
            access$100(761, 12), 
            access$100(762, 14) });
      this.TO_CLIENT.registerPacket((Class)Subtitle.class, Subtitle::new, new ProtocolMapping[] { access$100(755, 87), 
            access$100(757, 88), 
            access$100(760, 91), 
            access$100(761, 89), 
            access$100(762, 93) });
      this.TO_CLIENT.registerPacket((Class)TitleTimes.class, TitleTimes::new, new ProtocolMapping[] { access$100(755, 90), 
            access$100(757, 91), 
            access$100(760, 94), 
            access$100(761, 92), 
            access$100(762, 96) });
      this.TO_CLIENT.registerPacket((Class)SystemChat.class, SystemChat::new, new ProtocolMapping[] { access$100(759, 95), 
            access$100(760, 98), 
            access$100(761, 96), 
            access$100(762, 100) });
      this.TO_CLIENT.registerPacket((Class)PlayerListHeaderFooter.class, PlayerListHeaderFooter::new, new ProtocolMapping[] { 
            access$100(4, 71), 
            access$100(107, 72), 
            access$100(110, 71), 
            access$100(335, 73), 
            access$100(338, 74), 
            access$100(393, 78), 
            access$100(477, 83), 
            access$100(573, 84), 
            access$100(735, 83), 
            access$100(755, 94), 
            access$100(757, 95), 
            access$100(759, 96), 
            access$100(760, 99), 
            access$100(761, 97), 
            access$100(762, 101) });
      this.TO_CLIENT.registerPacket((Class)EntityStatus.class, EntityStatus::new, new ProtocolMapping[] { 
            access$100(4, 26), 
            access$100(107, 27), 
            access$100(393, 28), 
            access$100(477, 27), 
            access$100(573, 28), 
            access$100(735, 27), 
            access$100(751, 26), 
            access$100(755, 27), 
            access$100(759, 24), 
            access$100(760, 26), 
            access$100(761, 25), 
            access$100(762, 28) });
      this.TO_CLIENT.registerPacket((Class)Commands.class, Commands::new, new ProtocolMapping[] { access$100(393, 17), 
            access$100(573, 18), 
            access$100(735, 17), 
            access$100(751, 16), 
            access$100(755, 18), 
            access$100(759, 15), 
            access$100(761, 14), 
            access$100(762, 16) });
      this.TO_CLIENT.registerPacket((Class)GameState.class, GameState::new, new ProtocolMapping[] { access$100(573, 31), 
            access$100(735, 30), 
            access$100(751, 29), 
            access$100(755, 30), 
            access$100(759, 27), 
            access$100(760, 29), 
            access$100(761, 28), 
            access$100(762, 31) });
      this.TO_CLIENT.registerPacket((Class)ViewDistance.class, ViewDistance::new, new ProtocolMapping[] { access$100(477, 65), 
            access$100(573, 66), 
            access$100(735, 65), 
            access$100(755, 74), 
            access$100(759, 73), 
            access$100(760, 76), 
            access$100(761, 75), 
            access$100(762, 79) });
      this.TO_CLIENT.registerPacket((Class)ServerData.class, ServerData::new, new ProtocolMapping[] { access$100(759, 63), 
            access$100(760, 66), 
            access$100(761, 65), 
            access$100(762, 69) });
      this.TO_CLIENT.registerPacket((Class)PlayerListItemRemove.class, PlayerListItemRemove::new, new ProtocolMapping[] { access$100(761, 53), 
            access$100(762, 57) });
      this.TO_CLIENT.registerPacket((Class)PlayerListItemUpdate.class, PlayerListItemUpdate::new, new ProtocolMapping[] { access$100(761, 54), 
            access$100(762, 58) });
      this.TO_SERVER.registerPacket((Class)KeepAlive.class, KeepAlive::new, new ProtocolMapping[] { 
            access$100(4, 0), 
            access$100(107, 11), 
            access$100(335, 12), 
            access$100(338, 11), 
            access$100(393, 14), 
            access$100(477, 15), 
            access$100(735, 16), 
            access$100(755, 15), 
            access$100(759, 17), 
            access$100(760, 18), 
            access$100(761, 17), 
            access$100(762, 18) });
      this.TO_SERVER.registerPacket((Class)Chat.class, Chat::new, new ProtocolMapping[] { access$100(4, 1), 
            access$100(107, 2), 
            access$100(335, 3), 
            access$100(338, 2), 
            access$100(477, 3), 
            access$100(759, -1) });
      this.TO_SERVER.registerPacket((Class)ClientCommand.class, ClientCommand::new, new ProtocolMapping[] { access$100(759, 3), 
            access$100(760, 4) });
      this.TO_SERVER.registerPacket((Class)ClientChat.class, ClientChat::new, new ProtocolMapping[] { access$100(759, 4), 
            access$100(760, 5) });
      this.TO_SERVER.registerPacket((Class)TabCompleteRequest.class, TabCompleteRequest::new, new ProtocolMapping[] { access$100(4, 20), 
            access$100(107, 1), 
            access$100(335, 2), 
            access$100(338, 1), 
            access$100(393, 5), 
            access$100(477, 6), 
            access$100(759, 8), 
            access$100(760, 9), 
            access$100(761, 8), 
            access$100(762, 9) });
      this.TO_SERVER.registerPacket((Class)ClientSettings.class, ClientSettings::new, new ProtocolMapping[] { access$100(4, 21), 
            access$100(107, 4), 
            access$100(335, 5), 
            access$100(338, 4), 
            access$100(477, 5), 
            access$100(759, 7), 
            access$100(760, 8), 
            access$100(761, 7), 
            access$100(762, 8) });
      this.TO_SERVER.registerPacket((Class)PluginMessage.class, PluginMessage::new, new ProtocolMapping[] { 
            access$100(4, 23), 
            access$100(107, 9), 
            access$100(335, 10), 
            access$100(338, 9), 
            access$100(393, 10), 
            access$100(477, 11), 
            access$100(755, 10), 
            access$100(759, 12), 
            access$100(760, 13), 
            access$100(761, 12), 
            access$100(762, 13) });
    }
  },
  STATUS {
    Protocol() {
      this.TO_CLIENT.registerPacket((Class)StatusResponse.class, StatusResponse::new, new ProtocolMapping[] { access$100(4, 0) });
      this.TO_CLIENT.registerPacket((Class)PingPacket.class, PingPacket::new, new ProtocolMapping[] { access$100(4, 1) });
      this.TO_SERVER.registerPacket((Class)StatusRequest.class, StatusRequest::new, new ProtocolMapping[] { access$100(4, 0) });
      this.TO_SERVER.registerPacket((Class)PingPacket.class, PingPacket::new, new ProtocolMapping[] { access$100(4, 1) });
    }
  },
  LOGIN {
    Protocol() {
      this.TO_CLIENT.registerPacket((Class)Kick.class, Kick::new, new ProtocolMapping[] { access$100(4, 0) });
      this.TO_CLIENT.registerPacket((Class)EncryptionRequest.class, EncryptionRequest::new, new ProtocolMapping[] { access$100(4, 1) });
      this.TO_CLIENT.registerPacket((Class)LoginSuccess.class, LoginSuccess::new, new ProtocolMapping[] { access$100(4, 2) });
      this.TO_CLIENT.registerPacket((Class)SetCompression.class, SetCompression::new, new ProtocolMapping[] { access$100(4, 3) });
      this.TO_CLIENT.registerPacket((Class)LoginPayloadRequest.class, LoginPayloadRequest::new, new ProtocolMapping[] { access$100(393, 4) });
      this.TO_SERVER.registerPacket((Class)LoginRequest.class, LoginRequest::new, new ProtocolMapping[] { access$100(4, 0) });
      this.TO_SERVER.registerPacket((Class)EncryptionResponse.class, EncryptionResponse::new, new ProtocolMapping[] { access$100(4, 1) });
      this.TO_SERVER.registerPacket((Class)LoginPayloadResponse.class, LoginPayloadResponse::new, new ProtocolMapping[] { access$100(393, 2) });
    }
  };
  
  Protocol() {
    this.TO_SERVER = new DirectionData(this, ProtocolConstants.Direction.TO_SERVER);
    this.TO_CLIENT = new DirectionData(this, ProtocolConstants.Direction.TO_CLIENT);
  }
  
  public static final int MAX_PACKET_ID = 255;
  
  final DirectionData TO_SERVER;
  
  final DirectionData TO_CLIENT;
  
  public static void main(String[] args) {
    for (Iterator<Integer> iterator = ProtocolConstants.SUPPORTED_VERSION_IDS.iterator(); iterator.hasNext(); ) {
      int version = ((Integer)iterator.next()).intValue();
      dump(version);
    } 
  }
  
  private static void dump(int version) {
    for (Protocol protocol : values())
      dump(version, protocol); 
  }
  
  private static void dump(int version, Protocol protocol) {
    dump(version, protocol.TO_CLIENT);
    dump(version, protocol.TO_SERVER);
  }
  
  private static void dump(int version, DirectionData data) {
    for (int id = 0; id < 255; id++) {
      DefinedPacket packet = data.createPacket(id, version);
      if (packet != null)
        System.out.println(version + " " + data.protocolPhase + " " + data.direction + " " + id + " " + packet.getClass().getSimpleName()); 
    } 
  }
  
  private static class ProtocolData {
    private final int protocolVersion;
    
    private final TObjectIntMap<Class<? extends DefinedPacket>> packetMap;
    
    private final Supplier<? extends DefinedPacket>[] packetConstructors;
    
    public ProtocolData(int protocolVersion) {
      this.packetMap = (TObjectIntMap<Class<? extends DefinedPacket>>)new TObjectIntHashMap(255);
      this.packetConstructors = (Supplier<? extends DefinedPacket>[])new Supplier[255];
      this.protocolVersion = protocolVersion;
    }
    
    public boolean equals(Object o) {
      if (o == this)
        return true; 
      if (!(o instanceof ProtocolData))
        return false; 
      ProtocolData other = (ProtocolData)o;
      if (!other.canEqual(this))
        return false; 
      if (getProtocolVersion() != other.getProtocolVersion())
        return false; 
      Object<Class<? extends DefinedPacket>> this$packetMap = (Object<Class<? extends DefinedPacket>>)getPacketMap(), other$packetMap = (Object<Class<? extends DefinedPacket>>)other.getPacketMap();
      return ((this$packetMap == null) ? (other$packetMap != null) : !this$packetMap.equals(other$packetMap)) ? false : (!!Arrays.deepEquals((Object[])getPacketConstructors(), (Object[])other.getPacketConstructors()));
    }
    
    protected boolean canEqual(Object other) {
      return other instanceof ProtocolData;
    }
    
    public int hashCode() {
      int PRIME = 59;
      result = 1;
      result = result * 59 + getProtocolVersion();
      Object<Class<? extends DefinedPacket>> $packetMap = (Object<Class<? extends DefinedPacket>>)getPacketMap();
      result = result * 59 + (($packetMap == null) ? 43 : $packetMap.hashCode());
      return result * 59 + Arrays.deepHashCode((Object[])getPacketConstructors());
    }
    
    public String toString() {
      return "Protocol.ProtocolData(protocolVersion=" + getProtocolVersion() + ", packetMap=" + getPacketMap() + ", packetConstructors=" + Arrays.deepToString((Object[])getPacketConstructors()) + ")";
    }
    
    public int getProtocolVersion() {
      return this.protocolVersion;
    }
    
    public TObjectIntMap<Class<? extends DefinedPacket>> getPacketMap() {
      return this.packetMap;
    }
    
    public Supplier<? extends DefinedPacket>[] getPacketConstructors() {
      return this.packetConstructors;
    }
  }
  
  private static class ProtocolMapping {
    private final int protocolVersion;
    
    private final int packetID;
    
    public ProtocolMapping(int protocolVersion, int packetID) {
      this.protocolVersion = protocolVersion;
      this.packetID = packetID;
    }
    
    public boolean equals(Object o) {
      if (o == this)
        return true; 
      if (!(o instanceof ProtocolMapping))
        return false; 
      ProtocolMapping other = (ProtocolMapping)o;
      return !other.canEqual(this) ? false : ((getProtocolVersion() != other.getProtocolVersion()) ? false : (!(getPacketID() != other.getPacketID())));
    }
    
    protected boolean canEqual(Object other) {
      return other instanceof ProtocolMapping;
    }
    
    public int hashCode() {
      int PRIME = 59;
      result = 1;
      result = result * 59 + getProtocolVersion();
      return result * 59 + getPacketID();
    }
    
    public String toString() {
      return "Protocol.ProtocolMapping(protocolVersion=" + getProtocolVersion() + ", packetID=" + getPacketID() + ")";
    }
    
    public int getProtocolVersion() {
      return this.protocolVersion;
    }
    
    public int getPacketID() {
      return this.packetID;
    }
  }
  
  private static ProtocolMapping map(int protocol, int id) {
    return new ProtocolMapping(protocol, id);
  }
  
  static final class DirectionData {
    private final TIntObjectMap<Protocol.ProtocolData> protocols = (TIntObjectMap<Protocol.ProtocolData>)new TIntObjectHashMap();
    
    private final Protocol protocolPhase;
    
    private final ProtocolConstants.Direction direction;
    
    public ProtocolConstants.Direction getDirection() {
      return this.direction;
    }
    
    public DirectionData(Protocol protocolPhase, ProtocolConstants.Direction direction) {
      this.protocolPhase = protocolPhase;
      this.direction = direction;
      for (Iterator<Integer> iterator = ProtocolConstants.SUPPORTED_VERSION_IDS.iterator(); iterator.hasNext(); ) {
        int protocol = ((Integer)iterator.next()).intValue();
        this.protocols.put(protocol, new Protocol.ProtocolData(protocol));
      } 
    }
    
    private Protocol.ProtocolData getProtocolData(int version) {
      Protocol.ProtocolData protocol = (Protocol.ProtocolData)this.protocols.get(version);
      if (protocol == null && this.protocolPhase != Protocol.GAME)
        protocol = (Protocol.ProtocolData)Iterables.getFirst(this.protocols.valueCollection(), null); 
      return protocol;
    }
    
    public boolean hasPacket(int i, boolean supportsForge) {
      return (supportsForge || (i >= 0 && i <= 255));
    }
    
    public final DefinedPacket createPacket(int id, int version) {
      return createPacket(id, version, true);
    }
    
    public final DefinedPacket createPacket(int id, int version, boolean supportsForge) {
      Protocol.ProtocolData protocolData = getProtocolData(version);
      if (protocolData == null)
        throw new BadPacketException("Unsupported protocol version " + version); 
      if (!hasPacket(id, supportsForge))
        throw new BadPacketException("Packet with id " + id + " outside of range"); 
      Supplier<? extends DefinedPacket> constructor = protocolData.packetConstructors[id];
      return (constructor == null) ? null : constructor.get();
    }
    
    private void registerPacket(Class<? extends DefinedPacket> packetClass, Supplier<? extends DefinedPacket> constructor, Protocol.ProtocolMapping... mappings) {
      int mappingIndex = 0;
      Protocol.ProtocolMapping mapping = mappings[mappingIndex];
      for (Iterator<Integer> iterator = ProtocolConstants.SUPPORTED_VERSION_IDS.iterator(); iterator.hasNext(); ) {
        int protocol = ((Integer)iterator.next()).intValue();
        if (protocol < mapping.protocolVersion)
          continue; 
        if (mapping.protocolVersion < protocol && mappingIndex + 1 < mappings.length) {
          Protocol.ProtocolMapping nextMapping = mappings[mappingIndex + 1];
          if (nextMapping.protocolVersion == protocol) {
            Preconditions.checkState((nextMapping.packetID != mapping.packetID), "Duplicate packet mapping (%s, %s)", mapping.protocolVersion, nextMapping.protocolVersion);
            mapping = nextMapping;
            mappingIndex++;
          } 
        } 
        if (mapping.packetID < 0)
          break; 
        Protocol.ProtocolData data = (Protocol.ProtocolData)this.protocols.get(protocol);
        data.packetMap.put(packetClass, mapping.packetID);
        data.packetConstructors[mapping.packetID] = constructor;
      } 
    }
    
    final int getId(Class<? extends DefinedPacket> packet, int version) {
      Protocol.ProtocolData protocolData = getProtocolData(version);
      if (protocolData == null)
        throw new BadPacketException("Unsupported protocol version"); 
      int packetId = protocolData.packetMap.get(packet);
      Preconditions.checkArgument((packetId >= 0), "Cannot get ID for packet %s in phase %s with direction %s for protocol version %s", packet, this.protocolPhase, this.direction, Integer.valueOf(version));
      return packetId;
    }
  }
  
  public DirectionData getToServer() {
    return this.TO_SERVER;
  }
}
