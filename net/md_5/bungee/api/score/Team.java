package net.md_5.bungee.api.score;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;

public class Team {
  @NonNull
  private final String name;
  
  private String displayName;
  
  private String prefix;
  
  private String suffix;
  
  private byte friendlyFire;
  
  private String nameTagVisibility;
  
  private String collisionRule;
  
  private int color;
  
  public Team(@NonNull String name) {
    if (name == null)
      throw new NullPointerException("name is marked non-null but is null"); 
    this.name = name;
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
  
  public void setFriendlyFire(byte friendlyFire) {
    this.friendlyFire = friendlyFire;
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
  
  public void setPlayers(Set<String> players) {
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
    if (getFriendlyFire() != other.getFriendlyFire())
      return false; 
    if (getColor() != other.getColor())
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
    if ((this$collisionRule == null) ? (other$collisionRule != null) : !this$collisionRule.equals(other$collisionRule))
      return false; 
    Object<String> this$players = (Object<String>)getPlayers(), other$players = (Object<String>)other.getPlayers();
    return !((this$players == null) ? (other$players != null) : !this$players.equals(other$players));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Team;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getFriendlyFire();
    result = result * 59 + getColor();
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
    Object<String> $players = (Object<String>)getPlayers();
    return result * 59 + (($players == null) ? 43 : $players.hashCode());
  }
  
  public String toString() {
    return "Team(name=" + getName() + ", displayName=" + getDisplayName() + ", prefix=" + getPrefix() + ", suffix=" + getSuffix() + ", friendlyFire=" + getFriendlyFire() + ", nameTagVisibility=" + getNameTagVisibility() + ", collisionRule=" + getCollisionRule() + ", color=" + getColor() + ", players=" + getPlayers() + ")";
  }
  
  @NonNull
  public String getName() {
    return this.name;
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
  
  public byte getFriendlyFire() {
    return this.friendlyFire;
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
  
  private Set<String> players = new HashSet<>();
  
  public Collection<String> getPlayers() {
    return Collections.unmodifiableSet(this.players);
  }
  
  public void addPlayer(String name) {
    this.players.add(name);
  }
  
  public void removePlayer(String name) {
    this.players.remove(name);
  }
}
