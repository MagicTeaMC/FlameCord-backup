package net.md_5.bungee.api;

import io.github.waterfallmc.waterfall.utils.UUIDUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ServerPing {
  private Protocol version;
  
  private Players players;
  
  private BaseComponent description;
  
  private Favicon favicon;
  
  private final ModInfo modinfo;
  
  public void setVersion(Protocol version) {
    this.version = version;
  }
  
  public void setPlayers(Players players) {
    this.players = players;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ServerPing))
      return false; 
    ServerPing other = (ServerPing)o;
    if (!other.canEqual(this))
      return false; 
    Object this$version = getVersion(), other$version = other.getVersion();
    if ((this$version == null) ? (other$version != null) : !this$version.equals(other$version))
      return false; 
    Object this$players = getPlayers(), other$players = other.getPlayers();
    if ((this$players == null) ? (other$players != null) : !this$players.equals(other$players))
      return false; 
    Object this$description = getDescription(), other$description = other.getDescription();
    if ((this$description == null) ? (other$description != null) : !this$description.equals(other$description))
      return false; 
    Object this$favicon = getFavicon(), other$favicon = other.getFavicon();
    if ((this$favicon == null) ? (other$favicon != null) : !this$favicon.equals(other$favicon))
      return false; 
    Object this$modinfo = getModinfo(), other$modinfo = other.getModinfo();
    return !((this$modinfo == null) ? (other$modinfo != null) : !this$modinfo.equals(other$modinfo));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ServerPing;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $version = getVersion();
    result = result * 59 + (($version == null) ? 43 : $version.hashCode());
    Object $players = getPlayers();
    result = result * 59 + (($players == null) ? 43 : $players.hashCode());
    Object $description = getDescription();
    result = result * 59 + (($description == null) ? 43 : $description.hashCode());
    Object $favicon = getFavicon();
    result = result * 59 + (($favicon == null) ? 43 : $favicon.hashCode());
    Object $modinfo = getModinfo();
    return result * 59 + (($modinfo == null) ? 43 : $modinfo.hashCode());
  }
  
  public String toString() {
    return "ServerPing(version=" + getVersion() + ", players=" + getPlayers() + ", description=" + getDescription() + ", modinfo=" + getModinfo() + ")";
  }
  
  public ServerPing() {
    this.modinfo = new ModInfo();
  }
  
  public ServerPing(Protocol version, Players players, BaseComponent description, Favicon favicon) {
    this.modinfo = new ModInfo();
    this.version = version;
    this.players = players;
    this.description = description;
    this.favicon = favicon;
  }
  
  public Protocol getVersion() {
    return this.version;
  }
  
  public static class Protocol {
    private String name;
    
    private int protocol;
    
    public void setName(String name) {
      this.name = name;
    }
    
    public void setProtocol(int protocol) {
      this.protocol = protocol;
    }
    
    public boolean equals(Object o) {
      if (o == this)
        return true; 
      if (!(o instanceof Protocol))
        return false; 
      Protocol other = (Protocol)o;
      if (!other.canEqual(this))
        return false; 
      if (getProtocol() != other.getProtocol())
        return false; 
      Object this$name = getName(), other$name = other.getName();
      return !((this$name == null) ? (other$name != null) : !this$name.equals(other$name));
    }
    
    protected boolean canEqual(Object other) {
      return other instanceof Protocol;
    }
    
    public int hashCode() {
      int PRIME = 59;
      result = 1;
      result = result * 59 + getProtocol();
      Object $name = getName();
      return result * 59 + (($name == null) ? 43 : $name.hashCode());
    }
    
    public String toString() {
      return "ServerPing.Protocol(name=" + getName() + ", protocol=" + getProtocol() + ")";
    }
    
    public Protocol(String name, int protocol) {
      this.name = name;
      this.protocol = protocol;
    }
    
    public String getName() {
      return this.name;
    }
    
    public int getProtocol() {
      return this.protocol;
    }
  }
  
  public Players getPlayers() {
    return this.players;
  }
  
  public static class Players {
    private int max;
    
    private int online;
    
    private ServerPing.PlayerInfo[] sample;
    
    public void setMax(int max) {
      this.max = max;
    }
    
    public void setOnline(int online) {
      this.online = online;
    }
    
    public void setSample(ServerPing.PlayerInfo[] sample) {
      this.sample = sample;
    }
    
    public boolean equals(Object o) {
      if (o == this)
        return true; 
      if (!(o instanceof Players))
        return false; 
      Players other = (Players)o;
      return !other.canEqual(this) ? false : ((getMax() != other.getMax()) ? false : ((getOnline() != other.getOnline()) ? false : (!!Arrays.deepEquals((Object[])getSample(), (Object[])other.getSample()))));
    }
    
    protected boolean canEqual(Object other) {
      return other instanceof Players;
    }
    
    public int hashCode() {
      int PRIME = 59;
      result = 1;
      result = result * 59 + getMax();
      result = result * 59 + getOnline();
      return result * 59 + Arrays.deepHashCode((Object[])getSample());
    }
    
    public String toString() {
      return "ServerPing.Players(max=" + getMax() + ", online=" + getOnline() + ", sample=" + Arrays.deepToString((Object[])getSample()) + ")";
    }
    
    public Players(int max, int online, ServerPing.PlayerInfo[] sample) {
      this.max = max;
      this.online = online;
      this.sample = sample;
    }
    
    public int getMax() {
      return this.max;
    }
    
    public int getOnline() {
      return this.online;
    }
    
    public ServerPing.PlayerInfo[] getSample() {
      return this.sample;
    }
  }
  
  public static class PlayerInfo {
    private String name;
    
    private UUID uniqueId;
    
    public void setName(String name) {
      this.name = name;
    }
    
    public void setUniqueId(UUID uniqueId) {
      this.uniqueId = uniqueId;
    }
    
    public boolean equals(Object o) {
      if (o == this)
        return true; 
      if (!(o instanceof PlayerInfo))
        return false; 
      PlayerInfo other = (PlayerInfo)o;
      if (!other.canEqual(this))
        return false; 
      Object this$name = getName(), other$name = other.getName();
      if ((this$name == null) ? (other$name != null) : !this$name.equals(other$name))
        return false; 
      Object this$uniqueId = getUniqueId(), other$uniqueId = other.getUniqueId();
      return !((this$uniqueId == null) ? (other$uniqueId != null) : !this$uniqueId.equals(other$uniqueId));
    }
    
    protected boolean canEqual(Object other) {
      return other instanceof PlayerInfo;
    }
    
    public int hashCode() {
      int PRIME = 59;
      result = 1;
      Object $name = getName();
      result = result * 59 + (($name == null) ? 43 : $name.hashCode());
      Object $uniqueId = getUniqueId();
      return result * 59 + (($uniqueId == null) ? 43 : $uniqueId.hashCode());
    }
    
    public String toString() {
      return "ServerPing.PlayerInfo(name=" + getName() + ", uniqueId=" + getUniqueId() + ")";
    }
    
    public PlayerInfo(String name, UUID uniqueId) {
      this.name = name;
      this.uniqueId = uniqueId;
    }
    
    public String getName() {
      return this.name;
    }
    
    public UUID getUniqueId() {
      return this.uniqueId;
    }
    
    private static final UUID md5UUID = Util.getUUID("af74a02d19cb445bb07f6866a861f783");
    
    public PlayerInfo(String name, String id) {
      setName(name);
      setId(id);
    }
    
    public void setId(String id) {
      try {
        this.uniqueId = Util.getUUID(id);
      } catch (Exception e) {
        this.uniqueId = md5UUID;
      } 
    }
    
    public String getId() {
      return UUIDUtils.undash(this.uniqueId.toString());
    }
  }
  
  public static class ModInfo {
    public void setType(String type) {
      this.type = type;
    }
    
    public void setModList(List<ServerPing.ModItem> modList) {
      this.modList = modList;
    }
    
    public boolean equals(Object o) {
      if (o == this)
        return true; 
      if (!(o instanceof ModInfo))
        return false; 
      ModInfo other = (ModInfo)o;
      if (!other.canEqual(this))
        return false; 
      Object this$type = getType(), other$type = other.getType();
      if ((this$type == null) ? (other$type != null) : !this$type.equals(other$type))
        return false; 
      Object<ServerPing.ModItem> this$modList = (Object<ServerPing.ModItem>)getModList(), other$modList = (Object<ServerPing.ModItem>)other.getModList();
      return !((this$modList == null) ? (other$modList != null) : !this$modList.equals(other$modList));
    }
    
    protected boolean canEqual(Object other) {
      return other instanceof ModInfo;
    }
    
    public int hashCode() {
      int PRIME = 59;
      result = 1;
      Object $type = getType();
      result = result * 59 + (($type == null) ? 43 : $type.hashCode());
      Object<ServerPing.ModItem> $modList = (Object<ServerPing.ModItem>)getModList();
      return result * 59 + (($modList == null) ? 43 : $modList.hashCode());
    }
    
    public String toString() {
      return "ServerPing.ModInfo(type=" + getType() + ", modList=" + getModList() + ")";
    }
    
    private String type = "FML";
    
    public String getType() {
      return this.type;
    }
    
    private List<ServerPing.ModItem> modList = new ArrayList<>();
    
    public List<ServerPing.ModItem> getModList() {
      return this.modList;
    }
  }
  
  public static class ModItem {
    private String modid;
    
    private String version;
    
    public void setModid(String modid) {
      this.modid = modid;
    }
    
    public void setVersion(String version) {
      this.version = version;
    }
    
    public boolean equals(Object o) {
      if (o == this)
        return true; 
      if (!(o instanceof ModItem))
        return false; 
      ModItem other = (ModItem)o;
      if (!other.canEqual(this))
        return false; 
      Object this$modid = getModid(), other$modid = other.getModid();
      if ((this$modid == null) ? (other$modid != null) : !this$modid.equals(other$modid))
        return false; 
      Object this$version = getVersion(), other$version = other.getVersion();
      return !((this$version == null) ? (other$version != null) : !this$version.equals(other$version));
    }
    
    protected boolean canEqual(Object other) {
      return other instanceof ModItem;
    }
    
    public int hashCode() {
      int PRIME = 59;
      result = 1;
      Object $modid = getModid();
      result = result * 59 + (($modid == null) ? 43 : $modid.hashCode());
      Object $version = getVersion();
      return result * 59 + (($version == null) ? 43 : $version.hashCode());
    }
    
    public String toString() {
      return "ServerPing.ModItem(modid=" + getModid() + ", version=" + getVersion() + ")";
    }
    
    public ModItem(String modid, String version) {
      this.modid = modid;
      this.version = version;
    }
    
    public String getModid() {
      return this.modid;
    }
    
    public String getVersion() {
      return this.version;
    }
  }
  
  public ModInfo getModinfo() {
    return this.modinfo;
  }
  
  @Deprecated
  public ServerPing(Protocol version, Players players, String description, String favicon) {
    this(version, players, (BaseComponent)new TextComponent(TextComponent.fromLegacyText(description)), (favicon == null) ? null : Favicon.create(favicon));
  }
  
  @Deprecated
  public ServerPing(Protocol version, Players players, String description, Favicon favicon) {
    this(version, players, (BaseComponent)new TextComponent(TextComponent.fromLegacyText(description)), favicon);
  }
  
  @Deprecated
  public String getFavicon() {
    return (getFaviconObject() == null) ? null : getFaviconObject().getEncoded();
  }
  
  public Favicon getFaviconObject() {
    return this.favicon;
  }
  
  @Deprecated
  public void setFavicon(String favicon) {
    setFavicon((favicon == null) ? null : Favicon.create(favicon));
  }
  
  public void setFavicon(Favicon favicon) {
    this.favicon = favicon;
  }
  
  @Deprecated
  public void setDescription(String description) {
    this.description = (BaseComponent)new TextComponent(TextComponent.fromLegacyText(description));
  }
  
  @Deprecated
  public String getDescription() {
    return BaseComponent.toLegacyText(new BaseComponent[] { this.description });
  }
  
  public void setDescriptionComponent(BaseComponent description) {
    this.description = description;
  }
  
  public BaseComponent getDescriptionComponent() {
    return this.description;
  }
}
