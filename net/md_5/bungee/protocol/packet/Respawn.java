package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Location;
import net.md_5.bungee.protocol.ProtocolConstants;
import se.llbit.nbt.Tag;

public class Respawn extends DefinedPacket {
  private Object dimension;
  
  private String worldName;
  
  private long seed;
  
  private short difficulty;
  
  private short gameMode;
  
  private short previousGameMode;
  
  private String levelType;
  
  private boolean debug;
  
  private boolean flat;
  
  private boolean copyMeta;
  
  private Location deathLocation;
  
  private int portalCooldown;
  
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
  
  public void setGameMode(short gameMode) {
    this.gameMode = gameMode;
  }
  
  public void setPreviousGameMode(short previousGameMode) {
    this.previousGameMode = previousGameMode;
  }
  
  public void setLevelType(String levelType) {
    this.levelType = levelType;
  }
  
  public void setDebug(boolean debug) {
    this.debug = debug;
  }
  
  public void setFlat(boolean flat) {
    this.flat = flat;
  }
  
  public void setCopyMeta(boolean copyMeta) {
    this.copyMeta = copyMeta;
  }
  
  public void setDeathLocation(Location deathLocation) {
    this.deathLocation = deathLocation;
  }
  
  public void setPortalCooldown(int portalCooldown) {
    this.portalCooldown = portalCooldown;
  }
  
  public String toString() {
    return "Respawn(dimension=" + getDimension() + ", worldName=" + getWorldName() + ", seed=" + getSeed() + ", difficulty=" + getDifficulty() + ", gameMode=" + getGameMode() + ", previousGameMode=" + getPreviousGameMode() + ", levelType=" + getLevelType() + ", debug=" + isDebug() + ", flat=" + isFlat() + ", copyMeta=" + isCopyMeta() + ", deathLocation=" + getDeathLocation() + ", portalCooldown=" + getPortalCooldown() + ")";
  }
  
  public Respawn() {}
  
  public Respawn(Object dimension, String worldName, long seed, short difficulty, short gameMode, short previousGameMode, String levelType, boolean debug, boolean flat, boolean copyMeta, Location deathLocation, int portalCooldown) {
    this.dimension = dimension;
    this.worldName = worldName;
    this.seed = seed;
    this.difficulty = difficulty;
    this.gameMode = gameMode;
    this.previousGameMode = previousGameMode;
    this.levelType = levelType;
    this.debug = debug;
    this.flat = flat;
    this.copyMeta = copyMeta;
    this.deathLocation = deathLocation;
    this.portalCooldown = portalCooldown;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Respawn))
      return false; 
    Respawn other = (Respawn)o;
    if (!other.canEqual(this))
      return false; 
    if (getSeed() != other.getSeed())
      return false; 
    if (getDifficulty() != other.getDifficulty())
      return false; 
    if (getGameMode() != other.getGameMode())
      return false; 
    if (getPreviousGameMode() != other.getPreviousGameMode())
      return false; 
    if (isDebug() != other.isDebug())
      return false; 
    if (isFlat() != other.isFlat())
      return false; 
    if (isCopyMeta() != other.isCopyMeta())
      return false; 
    if (getPortalCooldown() != other.getPortalCooldown())
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
    return other instanceof Respawn;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    long $seed = getSeed();
    result = result * 59 + (int)($seed >>> 32L ^ $seed);
    result = result * 59 + getDifficulty();
    result = result * 59 + getGameMode();
    result = result * 59 + getPreviousGameMode();
    result = result * 59 + (isDebug() ? 79 : 97);
    result = result * 59 + (isFlat() ? 79 : 97);
    result = result * 59 + (isCopyMeta() ? 79 : 97);
    result = result * 59 + getPortalCooldown();
    Object $dimension = getDimension();
    result = result * 59 + (($dimension == null) ? 43 : $dimension.hashCode());
    Object $worldName = getWorldName();
    result = result * 59 + (($worldName == null) ? 43 : $worldName.hashCode());
    Object $levelType = getLevelType();
    result = result * 59 + (($levelType == null) ? 43 : $levelType.hashCode());
    Object $deathLocation = getDeathLocation();
    return result * 59 + (($deathLocation == null) ? 43 : $deathLocation.hashCode());
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
  
  public short getGameMode() {
    return this.gameMode;
  }
  
  public short getPreviousGameMode() {
    return this.previousGameMode;
  }
  
  public String getLevelType() {
    return this.levelType;
  }
  
  public boolean isDebug() {
    return this.debug;
  }
  
  public boolean isFlat() {
    return this.flat;
  }
  
  public boolean isCopyMeta() {
    return this.copyMeta;
  }
  
  public Location getDeathLocation() {
    return this.deathLocation;
  }
  
  public int getPortalCooldown() {
    return this.portalCooldown;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (protocolVersion >= 735) {
      if (protocolVersion >= 751 && protocolVersion < 759) {
        this.dimension = readTag(buf);
      } else {
        this.dimension = readString(buf);
      } 
      this.worldName = readString(buf);
    } else {
      this.dimension = Integer.valueOf(buf.readInt());
    } 
    if (protocolVersion >= 573)
      this.seed = buf.readLong(); 
    if (protocolVersion < 477)
      this.difficulty = buf.readUnsignedByte(); 
    this.gameMode = buf.readUnsignedByte();
    if (protocolVersion >= 735) {
      this.previousGameMode = buf.readUnsignedByte();
      this.debug = buf.readBoolean();
      this.flat = buf.readBoolean();
      this.copyMeta = buf.readBoolean();
    } else {
      this.levelType = readString(buf);
    } 
    if (protocolVersion >= 759)
      if (buf.readBoolean())
        this.deathLocation = new Location(readString(buf), buf.readLong());  
    if (protocolVersion >= 763)
      this.portalCooldown = readVarInt(buf); 
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (protocolVersion >= 735) {
      if (protocolVersion >= 751 && protocolVersion < 759) {
        writeTag((Tag)this.dimension, buf);
      } else {
        writeString((String)this.dimension, buf);
      } 
      writeString(this.worldName, buf);
    } else {
      buf.writeInt(((Integer)this.dimension).intValue());
    } 
    if (protocolVersion >= 573)
      buf.writeLong(this.seed); 
    if (protocolVersion < 477)
      buf.writeByte(this.difficulty); 
    buf.writeByte(this.gameMode);
    if (protocolVersion >= 735) {
      buf.writeByte(this.previousGameMode);
      buf.writeBoolean(this.debug);
      buf.writeBoolean(this.flat);
      buf.writeBoolean(this.copyMeta);
    } else {
      writeString(this.levelType, buf);
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
