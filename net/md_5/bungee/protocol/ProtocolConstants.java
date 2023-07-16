package net.md_5.bungee.protocol;

import com.google.common.collect.ImmutableList;
import java.util.List;

public class ProtocolConstants {
  private static final boolean SNAPSHOT_SUPPORT = Boolean.getBoolean("net.md_5.bungee.protocol.snapshot");
  
  public static final int MINECRAFT_1_7_2 = 4;
  
  public static final int MINECRAFT_1_7_6 = 5;
  
  public static final int MINECRAFT_1_8 = 47;
  
  public static final int MINECRAFT_1_9 = 107;
  
  public static final int MINECRAFT_1_9_1 = 108;
  
  public static final int MINECRAFT_1_9_2 = 109;
  
  public static final int MINECRAFT_1_9_4 = 110;
  
  public static final int MINECRAFT_1_10 = 210;
  
  public static final int MINECRAFT_1_11 = 315;
  
  public static final int MINECRAFT_1_11_1 = 316;
  
  public static final int MINECRAFT_1_12 = 335;
  
  public static final int MINECRAFT_1_12_1 = 338;
  
  public static final int MINECRAFT_1_12_2 = 340;
  
  public static final int MINECRAFT_1_13 = 393;
  
  public static final int MINECRAFT_1_13_1 = 401;
  
  public static final int MINECRAFT_1_13_2 = 404;
  
  public static final int MINECRAFT_1_14 = 477;
  
  public static final int MINECRAFT_1_14_1 = 480;
  
  public static final int MINECRAFT_1_14_2 = 485;
  
  public static final int MINECRAFT_1_14_3 = 490;
  
  public static final int MINECRAFT_1_14_4 = 498;
  
  public static final int MINECRAFT_1_15 = 573;
  
  public static final int MINECRAFT_1_15_1 = 575;
  
  public static final int MINECRAFT_1_15_2 = 578;
  
  public static final int MINECRAFT_1_16 = 735;
  
  public static final int MINECRAFT_1_16_1 = 736;
  
  public static final int MINECRAFT_1_16_2 = 751;
  
  public static final int MINECRAFT_1_16_3 = 753;
  
  public static final int MINECRAFT_1_16_4 = 754;
  
  public static final int MINECRAFT_1_17 = 755;
  
  public static final int MINECRAFT_1_17_1 = 756;
  
  public static final int MINECRAFT_1_18 = 757;
  
  public static final int MINECRAFT_1_18_2 = 758;
  
  public static final int MINECRAFT_1_19 = 759;
  
  public static final int MINECRAFT_1_19_1 = 760;
  
  public static final int MINECRAFT_1_19_3 = 761;
  
  public static final int MINECRAFT_1_19_4 = 762;
  
  public static final int MINECRAFT_1_20 = 763;
  
  public static final List<String> SUPPORTED_VERSIONS;
  
  public static final List<Integer> SUPPORTED_VERSION_IDS;
  
  static {
    ImmutableList.Builder<String> supportedVersions = ImmutableList.builder().add((Object[])new String[] { 
          "1.7.x", "1.8.x", "1.9.x", "1.10.x", "1.11.x", "1.12.x", "1.13.x", "1.14.x", "1.15.x", "1.16.x", 
          "1.17.x", "1.18.x", "1.19.x", "1.20.x" });
    ImmutableList.Builder<Integer> supportedVersionIds = ImmutableList.builder().add((Object[])new Integer[] { 
          Integer.valueOf(4), 
          Integer.valueOf(5), 
          
          Integer.valueOf(47), 
          Integer.valueOf(107), 
          Integer.valueOf(108), 
          Integer.valueOf(109), 
          Integer.valueOf(110), 
          Integer.valueOf(210), 
          Integer.valueOf(315), 
          Integer.valueOf(316), 
          Integer.valueOf(335), 
          Integer.valueOf(338), 
          Integer.valueOf(340), 
          Integer.valueOf(393), 
          Integer.valueOf(401), 
          Integer.valueOf(404), 
          Integer.valueOf(477), 
          Integer.valueOf(480), 
          Integer.valueOf(485), 
          Integer.valueOf(490), 
          Integer.valueOf(498), 
          Integer.valueOf(573), 
          Integer.valueOf(575), 
          Integer.valueOf(578), 
          Integer.valueOf(735), 
          Integer.valueOf(736), 
          Integer.valueOf(751), 
          Integer.valueOf(753), 
          Integer.valueOf(754), 
          Integer.valueOf(755), 
          Integer.valueOf(756), 
          Integer.valueOf(757), 
          Integer.valueOf(758), 
          Integer.valueOf(759), 
          Integer.valueOf(760), 
          Integer.valueOf(761), 
          Integer.valueOf(762), 
          Integer.valueOf(763) });
    if (SNAPSHOT_SUPPORT);
    SUPPORTED_VERSIONS = (List<String>)supportedVersions.build();
    SUPPORTED_VERSION_IDS = (List<Integer>)supportedVersionIds.build();
  }
  
  public static final boolean isBeforeOrEq(int before, int other) {
    return (before <= other);
  }
  
  public static final boolean isAfterOrEq(int after, int other) {
    return (after >= other);
  }
  
  public enum Direction {
    TO_CLIENT, TO_SERVER;
  }
}
