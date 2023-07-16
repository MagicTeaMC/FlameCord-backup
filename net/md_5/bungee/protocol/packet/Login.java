package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.HashSet;
import java.util.Set;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Location;
import net.md_5.bungee.protocol.ProtocolConstants;
import se.llbit.nbt.Tag;

public class Login extends DefinedPacket {
  private int entityId;
  
  private boolean hardcore;
  
  private short gameMode;
  
  private short previousGameMode;
  
  private Set<String> worldNames;
  
  private Tag dimensions;
  
  private Object dimension;
  
  private String worldName;
  
  private long seed;
  
  private short difficulty;
  
  private int maxPlayers;
  
  private String levelType;
  
  private int viewDistance;
  
  private int simulationDistance;
  
  private boolean reducedDebugInfo;
  
  private boolean normalRespawn;
  
  private boolean debug;
  
  private boolean flat;
  
  private Location deathLocation;
  
  private int portalCooldown;
  
  public void setEntityId(int entityId) {
    this.entityId = entityId;
  }
  
  public void setHardcore(boolean hardcore) {
    this.hardcore = hardcore;
  }
  
  public void setGameMode(short gameMode) {
    this.gameMode = gameMode;
  }
  
  public void setPreviousGameMode(short previousGameMode) {
    this.previousGameMode = previousGameMode;
  }
  
  public void setWorldNames(Set<String> worldNames) {
    this.worldNames = worldNames;
  }
  
  public void setDimensions(Tag dimensions) {
    this.dimensions = dimensions;
  }
  
  public void setDimension(Object dimension) {
    this.dimension = dimension;
  }
  
  public void setWorldName(String worldName) {
    this.worldName = worldName;
  }
  
  public void setSeed(long seed) {
    this.seed = seed;
  }
  
  public void setDifficulty(short difficulty) {
    this.difficulty = difficulty;
  }
  
  public void setMaxPlayers(int maxPlayers) {
    this.maxPlayers = maxPlayers;
  }
  
  public void setLevelType(String levelType) {
    this.levelType = levelType;
  }
  
  public void setViewDistance(int viewDistance) {
    this.viewDistance = viewDistance;
  }
  
  public void setSimulationDistance(int simulationDistance) {
    this.simulationDistance = simulationDistance;
  }
  
  public void setReducedDebugInfo(boolean reducedDebugInfo) {
    this.reducedDebugInfo = reducedDebugInfo;
  }
  
  public void setNormalRespawn(boolean normalRespawn) {
    this.normalRespawn = normalRespawn;
  }
  
  public void setDebug(boolean debug) {
    this.debug = debug;
  }
  
  public void setFlat(boolean flat) {
    this.flat = flat;
  }
  
  public void setDeathLocation(Location deathLocation) {
    this.deathLocation = deathLocation;
  }
  
  public void setPortalCooldown(int portalCooldown) {
    this.portalCooldown = portalCooldown;
  }
  
  public String toString() {
    return "Login(entityId=" + getEntityId() + ", hardcore=" + isHardcore() + ", gameMode=" + getGameMode() + ", previousGameMode=" + getPreviousGameMode() + ", worldNames=" + getWorldNames() + ", dimensions=" + getDimensions() + ", dimension=" + getDimension() + ", worldName=" + getWorldName() + ", seed=" + getSeed() + ", difficulty=" + getDifficulty() + ", maxPlayers=" + getMaxPlayers() + ", levelType=" + getLevelType() + ", viewDistance=" + getViewDistance() + ", simulationDistance=" + getSimulationDistance() + ", reducedDebugInfo=" + isReducedDebugInfo() + ", normalRespawn=" + isNormalRespawn() + ", debug=" + isDebug() + ", flat=" + isFlat() + ", deathLocation=" + getDeathLocation() + ", portalCooldown=" + getPortalCooldown() + ")";
  }
  
  public Login() {}
  
  public Login(int entityId, boolean hardcore, short gameMode, short previousGameMode, Set<String> worldNames, Tag dimensions, Object dimension, String worldName, long seed, short difficulty, int maxPlayers, String levelType, int viewDistance, int simulationDistance, boolean reducedDebugInfo, boolean normalRespawn, boolean debug, boolean flat, Location deathLocation, int portalCooldown) {
    this.entityId = entityId;
    this.hardcore = hardcore;
    this.gameMode = gameMode;
    this.previousGameMode = previousGameMode;
    this.worldNames = worldNames;
    this.dimensions = dimensions;
    this.dimension = dimension;
    this.worldName = worldName;
    this.seed = seed;
    this.difficulty = difficulty;
    this.maxPlayers = maxPlayers;
    this.levelType = levelType;
    this.viewDistance = viewDistance;
    this.simulationDistance = simulationDistance;
    this.reducedDebugInfo = reducedDebugInfo;
    this.normalRespawn = normalRespawn;
    this.debug = debug;
    this.flat = flat;
    this.deathLocation = deathLocation;
    this.portalCooldown = portalCooldown;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Login))
      return false; 
    Login other = (Login)o;
    if (!other.canEqual(this))
      return false; 
    if (getEntityId() != other.getEntityId())
      return false; 
    if (isHardcore() != other.isHardcore())
      return false; 
    if (getGameMode() != other.getGameMode())
      return false; 
    if (getPreviousGameMode() != other.getPreviousGameMode())
      return false; 
    if (getSeed() != other.getSeed())
      return false; 
    if (getDifficulty() != other.getDifficulty())
      return false; 
    if (getMaxPlayers() != other.getMaxPlayers())
      return false; 
    if (getViewDistance() != other.getViewDistance())
      return false; 
    if (getSimulationDistance() != other.getSimulationDistance())
      return false; 
    if (isReducedDebugInfo() != other.isReducedDebugInfo())
      return false; 
    if (isNormalRespawn() != other.isNormalRespawn())
      return false; 
    if (isDebug() != other.isDebug())
      return false; 
    if (isFlat() != other.isFlat())
      return false; 
    if (getPortalCooldown() != other.getPortalCooldown())
      return false; 
    Object<String> this$worldNames = (Object<String>)getWorldNames(), other$worldNames = (Object<String>)other.getWorldNames();
    if ((this$worldNames == null) ? (other$worldNames != null) : !this$worldNames.equals(other$worldNames))
      return false; 
    Object this$dimensions = getDimensions(), other$dimensions = other.getDimensions();
    if ((this$dimensions == null) ? (other$dimensions != null) : !this$dimensions.equals(other$dimensions))
      return false; 
    Object this$dimension = getDimension(), other$dimension = other.getDimension();
    if ((this$dimension == null) ? (other$dimension != null) : !this$dimension.equals(other$dimension))
      return false; 
    Object this$worldName = getWorldName(), other$worldName = other.getWorldName();
    if ((this$worldName == null) ? (other$worldName != null) : !this$worldName.equals(other$worldName))
      return false; 
    Object this$levelType = getLevelType(), other$levelType = other.getLevelType();
    if ((this$levelType == null) ? (other$levelType != null) : !this$levelType.equals(other$levelType))
      return false; 
    Object this$deathLocation = getDeathLocation(), other$deathLocation = other.getDeathLocation();
    return !((this$deathLocation == null) ? (other$deathLocation != null) : !this$deathLocation.equals(other$deathLocation));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Login;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getEntityId();
    result = result * 59 + (isHardcore() ? 79 : 97);
    result = result * 59 + getGameMode();
    result = result * 59 + getPreviousGameMode();
    long $seed = getSeed();
    result = result * 59 + (int)($seed >>> 32L ^ $seed);
    result = result * 59 + getDifficulty();
    result = result * 59 + getMaxPlayers();
    result = result * 59 + getViewDistance();
    result = result * 59 + getSimulationDistance();
    result = result * 59 + (isReducedDebugInfo() ? 79 : 97);
    result = result * 59 + (isNormalRespawn() ? 79 : 97);
    result = result * 59 + (isDebug() ? 79 : 97);
    result = result * 59 + (isFlat() ? 79 : 97);
    result = result * 59 + getPortalCooldown();
    Object<String> $worldNames = (Object<String>)getWorldNames();
    result = result * 59 + (($worldNames == null) ? 43 : $worldNames.hashCode());
    Object $dimensions = getDimensions();
    result = result * 59 + (($dimensions == null) ? 43 : $dimensions.hashCode());
    Object $dimension = getDimension();
    result = result * 59 + (($dimension == null) ? 43 : $dimension.hashCode());
    Object $worldName = getWorldName();
    result = result * 59 + (($worldName == null) ? 43 : $worldName.hashCode());
    Object $levelType = getLevelType();
    result = result * 59 + (($levelType == null) ? 43 : $levelType.hashCode());
    Object $deathLocation = getDeathLocation();
    return result * 59 + (($deathLocation == null) ? 43 : $deathLocation.hashCode());
  }
  
  public int getEntityId() {
    return this.entityId;
  }
  
  public boolean isHardcore() {
    return this.hardcore;
  }
  
  public short getGameMode() {
    return this.gameMode;
  }
  
  public short getPreviousGameMode() {
    return this.previousGameMode;
  }
  
  public Set<String> getWorldNames() {
    return this.worldNames;
  }
  
  public Tag getDimensions() {
    return this.dimensions;
  }
  
  public Object getDimension() {
    return this.dimension;
  }
  
  public String getWorldName() {
    return this.worldName;
  }
  
  public long getSeed() {
    return this.seed;
  }
  
  public short getDifficulty() {
    return this.difficulty;
  }
  
  public int getMaxPlayers() {
    return this.maxPlayers;
  }
  
  public String getLevelType() {
    return this.levelType;
  }
  
  public int getViewDistance() {
    return this.viewDistance;
  }
  
  public int getSimulationDistance() {
    return this.simulationDistance;
  }
  
  public boolean isReducedDebugInfo() {
    return this.reducedDebugInfo;
  }
  
  public boolean isNormalRespawn() {
    return this.normalRespawn;
  }
  
  public boolean isDebug() {
    return this.debug;
  }
  
  public boolean isFlat() {
    return this.flat;
  }
  
  public Location getDeathLocation() {
    return this.deathLocation;
  }
  
  public int getPortalCooldown() {
    return this.portalCooldown;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    this.entityId = buf.readInt();
    if (protocolVersion >= 751)
      this.hardcore = buf.readBoolean(); 
    this.gameMode = buf.readUnsignedByte();
    if (protocolVersion >= 735) {
      this.previousGameMode = buf.readUnsignedByte();
      this.worldNames = new HashSet<>();
      int worldCount = readVarInt(buf);
      for (int i = 0; i < worldCount; i++)
        this.worldNames.add(readString(buf)); 
      this.dimensions = readTag(buf);
    } 
    if (protocolVersion >= 735) {
      if (protocolVersion >= 751 && protocolVersion < 759) {
        this.dimension = readTag(buf);
      } else {
        this.dimension = readString(buf);
      } 
      this.worldName = readString(buf);
    } else if (protocolVersion > 107) {
      this.dimension = Integer.valueOf(buf.readInt());
    } else {
      this.dimension = Integer.valueOf(buf.readByte());
    } 
    if (protocolVersion >= 573)
      this.seed = buf.readLong(); 
    if (protocolVersion < 477)
      this.difficulty = buf.readUnsignedByte(); 
    if (protocolVersion >= 751) {
      this.maxPlayers = readVarInt(buf);
    } else {
      this.maxPlayers = buf.readUnsignedByte();
    } 
    if (protocolVersion < 735)
      this.levelType = readString(buf); 
    if (protocolVersion >= 477)
      this.viewDistance = readVarInt(buf); 
    if (protocolVersion >= 757)
      this.simulationDistance = readVarInt(buf); 
    if (protocolVersion >= 29)
      this.reducedDebugInfo = buf.readBoolean(); 
    if (protocolVersion >= 573)
      this.normalRespawn = buf.readBoolean(); 
    if (protocolVersion >= 735) {
      this.debug = buf.readBoolean();
      this.flat = buf.readBoolean();
    } 
    if (protocolVersion >= 759)
      if (buf.readBoolean())
        this.deathLocation = new Location(readString(buf), buf.readLong());  
    if (protocolVersion >= 763)
      this.portalCooldown = readVarInt(buf); 
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    buf.writeInt(this.entityId);
    if (protocolVersion >= 751)
      buf.writeBoolean(this.hardcore); 
    buf.writeByte(this.gameMode);
    if (protocolVersion >= 735) {
      buf.writeByte(this.previousGameMode);
      writeVarInt(this.worldNames.size(), buf);
      for (String world : this.worldNames)
        writeString(world, buf); 
      writeTag(this.dimensions, buf);
    } 
    if (protocolVersion >= 735) {
      if (protocolVersion >= 751 && protocolVersion < 759) {
        writeTag((Tag)this.dimension, buf);
      } else {
        writeString((String)this.dimension, buf);
      } 
      writeString(this.worldName, buf);
    } else if (protocolVersion > 107) {
      buf.writeInt(((Integer)this.dimension).intValue());
    } else {
      buf.writeByte(((Integer)this.dimension).intValue());
    } 
    if (protocolVersion >= 573)
      buf.writeLong(this.seed); 
    if (protocolVersion < 477)
      buf.writeByte(this.difficulty); 
    if (protocolVersion >= 751) {
      writeVarInt(this.maxPlayers, buf);
    } else {
      buf.writeByte(this.maxPlayers);
    } 
    if (protocolVersion < 735)
      writeString(this.levelType, buf); 
    if (protocolVersion >= 477)
      writeVarInt(this.viewDistance, buf); 
    if (protocolVersion >= 757)
      writeVarInt(this.simulationDistance, buf); 
    if (protocolVersion >= 29)
      buf.writeBoolean(this.reducedDebugInfo); 
    if (protocolVersion >= 573)
      buf.writeBoolean(this.normalRespawn); 
    if (protocolVersion >= 735) {
      buf.writeBoolean(this.debug);
      buf.writeBoolean(this.flat);
    } 
    if (protocolVersion >= 759)
      if (this.deathLocation != null) {
        buf.writeBoolean(true);
        writeString(this.deathLocation.getDimension(), buf);
        buf.writeLong(this.deathLocation.getPos());
      } else {
        buf.writeBoolean(false);
      }  
    if (protocolVersion >= 763)
      writeVarInt(this.portalCooldown, buf); 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
