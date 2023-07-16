package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class Team extends DefinedPacket {
  private String name;
  
  private byte mode;
  
  private String displayName;
  
  private String prefix;
  
  private String suffix;
  
  private String nameTagVisibility;
  
  private String collisionRule;
  
  private int color;
  
  private byte friendlyFire;
  
  private String[] players;
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setMode(byte mode) {
    this.mode = mode;
  }
  
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
  
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }
  
  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }
  
  public void setNameTagVisibility(String nameTagVisibility) {
    this.nameTagVisibility = nameTagVisibility;
  }
  
  public void setCollisionRule(String collisionRule) {
    this.collisionRule = collisionRule;
  }
  
  public void setColor(int color) {
    this.color = color;
  }
  
  public void setFriendlyFire(byte friendlyFire) {
    this.friendlyFire = friendlyFire;
  }
  
  public void setPlayers(String[] players) {
    this.players = players;
  }
  
  public String toString() {
    return "Team(name=" + getName() + ", mode=" + getMode() + ", displayName=" + getDisplayName() + ", prefix=" + getPrefix() + ", suffix=" + getSuffix() + ", nameTagVisibility=" + getNameTagVisibility() + ", collisionRule=" + getCollisionRule() + ", color=" + getColor() + ", friendlyFire=" + getFriendlyFire() + ", players=" + Arrays.deepToString((Object[])getPlayers()) + ")";
  }
  
  public Team() {}
  
  public Team(String name, byte mode, String displayName, String prefix, String suffix, String nameTagVisibility, String collisionRule, int color, byte friendlyFire, String[] players) {
    this.name = name;
    this.mode = mode;
    this.displayName = displayName;
    this.prefix = prefix;
    this.suffix = suffix;
    this.nameTagVisibility = nameTagVisibility;
    this.collisionRule = collisionRule;
    this.color = color;
    this.friendlyFire = friendlyFire;
    this.players = players;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Team))
      return false; 
    Team other = (Team)o;
    if (!other.canEqual(this))
      return false; 
    if (getMode() != other.getMode())
      return false; 
    if (getColor() != other.getColor())
      return false; 
    if (getFriendlyFire() != other.getFriendlyFire())
      return false; 
    Object this$name = getName(), other$name = other.getName();
    if ((this$name == null) ? (other$name != null) : !this$name.equals(other$name))
      return false; 
    Object this$displayName = getDisplayName(), other$displayName = other.getDisplayName();
    if ((this$displayName == null) ? (other$displayName != null) : !this$displayName.equals(other$displayName))
      return false; 
    Object this$prefix = getPrefix(), other$prefix = other.getPrefix();
    if ((this$prefix == null) ? (other$prefix != null) : !this$prefix.equals(other$prefix))
      return false; 
    Object this$suffix = getSuffix(), other$suffix = other.getSuffix();
    if ((this$suffix == null) ? (other$suffix != null) : !this$suffix.equals(other$suffix))
      return false; 
    Object this$nameTagVisibility = getNameTagVisibility(), other$nameTagVisibility = other.getNameTagVisibility();
    if ((this$nameTagVisibility == null) ? (other$nameTagVisibility != null) : !this$nameTagVisibility.equals(other$nameTagVisibility))
      return false; 
    Object this$collisionRule = getCollisionRule(), other$collisionRule = other.getCollisionRule();
    return ((this$collisionRule == null) ? (other$collisionRule != null) : !this$collisionRule.equals(other$collisionRule)) ? false : (!!Arrays.deepEquals((Object[])getPlayers(), (Object[])other.getPlayers()));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Team;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getMode();
    result = result * 59 + getColor();
    result = result * 59 + getFriendlyFire();
    Object $name = getName();
    result = result * 59 + (($name == null) ? 43 : $name.hashCode());
    Object $displayName = getDisplayName();
    result = result * 59 + (($displayName == null) ? 43 : $displayName.hashCode());
    Object $prefix = getPrefix();
    result = result * 59 + (($prefix == null) ? 43 : $prefix.hashCode());
    Object $suffix = getSuffix();
    result = result * 59 + (($suffix == null) ? 43 : $suffix.hashCode());
    Object $nameTagVisibility = getNameTagVisibility();
    result = result * 59 + (($nameTagVisibility == null) ? 43 : $nameTagVisibility.hashCode());
    Object $collisionRule = getCollisionRule();
    result = result * 59 + (($collisionRule == null) ? 43 : $collisionRule.hashCode());
    return result * 59 + Arrays.deepHashCode((Object[])getPlayers());
  }
  
  public String getName() {
    return this.name;
  }
  
  public byte getMode() {
    return this.mode;
  }
  
  public String getDisplayName() {
    return this.displayName;
  }
  
  public String getPrefix() {
    return this.prefix;
  }
  
  public String getSuffix() {
    return this.suffix;
  }
  
  public String getNameTagVisibility() {
    return this.nameTagVisibility;
  }
  
  public String getCollisionRule() {
    return this.collisionRule;
  }
  
  public int getColor() {
    return this.color;
  }
  
  public byte getFriendlyFire() {
    return this.friendlyFire;
  }
  
  public String[] getPlayers() {
    return this.players;
  }
  
  public Team(String name) {
    this.name = name;
    this.mode = 1;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    this.name = readString(buf);
    this.mode = buf.readByte();
    if (this.mode == 0 || this.mode == 2) {
      this.displayName = readString(buf);
      if (protocolVersion < 393) {
        this.prefix = readString(buf);
        this.suffix = readString(buf);
      } 
      this.friendlyFire = buf.readByte();
      if (protocolVersion >= 47)
        this.nameTagVisibility = readString(buf); 
      if (protocolVersion >= 107)
        this.collisionRule = readString(buf); 
      if (protocolVersion >= 47)
        this.color = (protocolVersion >= 393) ? readVarInt(buf) : buf.readByte(); 
      if (protocolVersion >= 393) {
        this.prefix = readString(buf);
        this.suffix = readString(buf);
      } 
    } 
    if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
      int len = (protocolVersion >= 47) ? readVarInt(buf) : buf.readShort();
      this.players = new String[len];
      for (int i = 0; i < len; i++)
        this.players[i] = readString(buf); 
    } 
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (ProtocolConstants.isBeforeOrEq(protocolVersion, 5)) {
      writeString(this.name, buf);
      buf.writeByte(this.mode);
      if (this.mode == 0 || this.mode == 2) {
        writeString(this.displayName, buf);
        writeString(this.prefix, buf);
        writeString(this.suffix, buf);
        buf.writeByte(this.friendlyFire);
      } 
      if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
        buf.writeShort(this.players.length);
        for (String player : this.players)
          writeString(player, buf); 
      } 
      return;
    } 
    writeString(this.name, buf);
    buf.writeByte(this.mode);
    if (this.mode == 0 || this.mode == 2) {
      writeString(this.displayName, buf);
      if (protocolVersion < 393) {
        writeString(this.prefix, buf);
        writeString(this.suffix, buf);
      } 
      buf.writeByte(this.friendlyFire);
      writeString(this.nameTagVisibility, buf);
      if (protocolVersion >= 107)
        writeString(this.collisionRule, buf); 
      if (protocolVersion >= 393) {
        writeVarInt(this.color, buf);
        writeString(this.prefix, buf);
        writeString(this.suffix, buf);
      } else {
        buf.writeByte(this.color);
      } 
    } 
    if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
      writeVarInt(this.players.length, buf);
      for (String player : this.players)
        writeString(player, buf); 
    } 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
