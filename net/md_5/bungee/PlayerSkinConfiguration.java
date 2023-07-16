package net.md_5.bungee;

import net.md_5.bungee.api.SkinConfiguration;

public class PlayerSkinConfiguration implements SkinConfiguration {
  public PlayerSkinConfiguration(byte bitmask) {
    this.bitmask = bitmask;
  }
  
  static final SkinConfiguration SKIN_SHOW_ALL = new PlayerSkinConfiguration(127);
  
  private final byte bitmask;
  
  public boolean hasCape() {
    return ((this.bitmask >> 0 & 0x1) == 1);
  }
  
  public boolean hasJacket() {
    return ((this.bitmask >> 1 & 0x1) == 1);
  }
  
  public boolean hasLeftSleeve() {
    return ((this.bitmask >> 2 & 0x1) == 1);
  }
  
  public boolean hasRightSleeve() {
    return ((this.bitmask >> 3 & 0x1) == 1);
  }
  
  public boolean hasLeftPants() {
    return ((this.bitmask >> 4 & 0x1) == 1);
  }
  
  public boolean hasRightPants() {
    return ((this.bitmask >> 5 & 0x1) == 1);
  }
  
  public boolean hasHat() {
    return ((this.bitmask >> 6 & 0x1) == 1);
  }
}
