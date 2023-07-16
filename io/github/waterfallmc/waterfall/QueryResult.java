package io.github.waterfallmc.waterfall;

import java.util.List;
import lombok.NonNull;

public class QueryResult {
  @NonNull
  private String motd;
  
  @NonNull
  private String gameType;
  
  @NonNull
  private String worldName;
  
  private int onlinePlayers;
  
  private int maxPlayers;
  
  private int port;
  
  @NonNull
  private String address;
  
  @NonNull
  private String gameId;
  
  @NonNull
  private final List<String> players;
  
  @NonNull
  private String version;
  
  public void setMotd(@NonNull String motd) {
    if (motd == null)
      throw new NullPointerException("motd is marked non-null but is null"); 
    this.motd = motd;
  }
  
  public void setGameType(@NonNull String gameType) {
    if (gameType == null)
      throw new NullPointerException("gameType is marked non-null but is null"); 
    this.gameType = gameType;
  }
  
  public void setWorldName(@NonNull String worldName) {
    if (worldName == null)
      throw new NullPointerException("worldName is marked non-null but is null"); 
    this.worldName = worldName;
  }
  
  public void setOnlinePlayers(int onlinePlayers) {
    this.onlinePlayers = onlinePlayers;
  }
  
  public void setMaxPlayers(int maxPlayers) {
    this.maxPlayers = maxPlayers;
  }
  
  public void setPort(int port) {
    this.port = port;
  }
  
  public void setAddress(@NonNull String address) {
    if (address == null)
      throw new NullPointerException("address is marked non-null but is null"); 
    this.address = address;
  }
  
  public void setGameId(@NonNull String gameId) {
    if (gameId == null)
      throw new NullPointerException("gameId is marked non-null but is null"); 
    this.gameId = gameId;
  }
  
  public void setVersion(@NonNull String version) {
    if (version == null)
      throw new NullPointerException("version is marked non-null but is null"); 
    this.version = version;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof QueryResult))
      return false; 
    QueryResult other = (QueryResult)o;
    if (!other.canEqual(this))
      return false; 
    if (getOnlinePlayers() != other.getOnlinePlayers())
      return false; 
    if (getMaxPlayers() != other.getMaxPlayers())
      return false; 
    if (getPort() != other.getPort())
      return false; 
    Object this$motd = getMotd(), other$motd = other.getMotd();
    if ((this$motd == null) ? (other$motd != null) : !this$motd.equals(other$motd))
      return false; 
    Object this$gameType = getGameType(), other$gameType = other.getGameType();
    if ((this$gameType == null) ? (other$gameType != null) : !this$gameType.equals(other$gameType))
      return false; 
    Object this$worldName = getWorldName(), other$worldName = other.getWorldName();
    if ((this$worldName == null) ? (other$worldName != null) : !this$worldName.equals(other$worldName))
      return false; 
    Object this$address = getAddress(), other$address = other.getAddress();
    if ((this$address == null) ? (other$address != null) : !this$address.equals(other$address))
      return false; 
    Object this$gameId = getGameId(), other$gameId = other.getGameId();
    if ((this$gameId == null) ? (other$gameId != null) : !this$gameId.equals(other$gameId))
      return false; 
    Object<String> this$players = (Object<String>)getPlayers(), other$players = (Object<String>)other.getPlayers();
    if ((this$players == null) ? (other$players != null) : !this$players.equals(other$players))
      return false; 
    Object this$version = getVersion(), other$version = other.getVersion();
    return !((this$version == null) ? (other$version != null) : !this$version.equals(other$version));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof QueryResult;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getOnlinePlayers();
    result = result * 59 + getMaxPlayers();
    result = result * 59 + getPort();
    Object $motd = getMotd();
    result = result * 59 + (($motd == null) ? 43 : $motd.hashCode());
    Object $gameType = getGameType();
    result = result * 59 + (($gameType == null) ? 43 : $gameType.hashCode());
    Object $worldName = getWorldName();
    result = result * 59 + (($worldName == null) ? 43 : $worldName.hashCode());
    Object $address = getAddress();
    result = result * 59 + (($address == null) ? 43 : $address.hashCode());
    Object $gameId = getGameId();
    result = result * 59 + (($gameId == null) ? 43 : $gameId.hashCode());
    Object<String> $players = (Object<String>)getPlayers();
    result = result * 59 + (($players == null) ? 43 : $players.hashCode());
    Object $version = getVersion();
    return result * 59 + (($version == null) ? 43 : $version.hashCode());
  }
  
  public String toString() {
    return "QueryResult(motd=" + getMotd() + ", gameType=" + getGameType() + ", worldName=" + getWorldName() + ", onlinePlayers=" + getOnlinePlayers() + ", maxPlayers=" + getMaxPlayers() + ", port=" + getPort() + ", address=" + getAddress() + ", gameId=" + getGameId() + ", players=" + getPlayers() + ", version=" + getVersion() + ")";
  }
  
  public QueryResult(@NonNull String motd, @NonNull String gameType, @NonNull String worldName, int onlinePlayers, int maxPlayers, int port, @NonNull String address, @NonNull String gameId, @NonNull List<String> players, @NonNull String version) {
    if (motd == null)
      throw new NullPointerException("motd is marked non-null but is null"); 
    if (gameType == null)
      throw new NullPointerException("gameType is marked non-null but is null"); 
    if (worldName == null)
      throw new NullPointerException("worldName is marked non-null but is null"); 
    if (address == null)
      throw new NullPointerException("address is marked non-null but is null"); 
    if (gameId == null)
      throw new NullPointerException("gameId is marked non-null but is null"); 
    if (players == null)
      throw new NullPointerException("players is marked non-null but is null"); 
    if (version == null)
      throw new NullPointerException("version is marked non-null but is null"); 
    this.motd = motd;
    this.gameType = gameType;
    this.worldName = worldName;
    this.onlinePlayers = onlinePlayers;
    this.maxPlayers = maxPlayers;
    this.port = port;
    this.address = address;
    this.gameId = gameId;
    this.players = players;
    this.version = version;
  }
  
  @NonNull
  public String getMotd() {
    return this.motd;
  }
  
  @NonNull
  public String getGameType() {
    return this.gameType;
  }
  
  @NonNull
  public String getWorldName() {
    return this.worldName;
  }
  
  public int getOnlinePlayers() {
    return this.onlinePlayers;
  }
  
  public int getMaxPlayers() {
    return this.maxPlayers;
  }
  
  public int getPort() {
    return this.port;
  }
  
  @NonNull
  public String getAddress() {
    return this.address;
  }
  
  @NonNull
  public String getGameId() {
    return this.gameId;
  }
  
  @NonNull
  public List<String> getPlayers() {
    return this.players;
  }
  
  @NonNull
  public String getVersion() {
    return this.version;
  }
}
