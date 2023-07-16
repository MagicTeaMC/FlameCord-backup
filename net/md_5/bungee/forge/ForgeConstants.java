package net.md_5.bungee.forge;

import java.util.regex.Pattern;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class ForgeConstants {
  public static final String FORGE_REGISTER = "FORGE";
  
  public static final String FML_TAG = "FML";
  
  public static final String FML_HANDSHAKE_TAG = "FML|HS";
  
  public static final String FML_REGISTER = "REGISTER";
  
  public static final String FML_LOGIN_PROFILE = "forgeClient";
  
  public static final String EXTRA_DATA = "extraData";
  
  public static final String FML_HANDSHAKE_TOKEN = "\000FML\000";
  
  public static final PluginMessage FML_RESET_HANDSHAKE = new PluginMessage("FML|HS", new byte[] { -2, 0 }, false);
  
  public static final PluginMessage FML_ACK = new PluginMessage("FML|HS", new byte[] { -1, 0 }, false);
  
  public static final PluginMessage FML_START_CLIENT_HANDSHAKE = new PluginMessage("FML|HS", new byte[] { 0, 1 }, false);
  
  public static final PluginMessage FML_START_SERVER_HANDSHAKE = new PluginMessage("FML|HS", new byte[] { 1, 1 }, false);
  
  public static final PluginMessage FML_EMPTY_MOD_LIST = new PluginMessage("FML|HS", new byte[] { 2, 0 }, false);
  
  public static final int FML_MIN_BUILD_VERSION = 1209;
  
  public static final Pattern FML_HANDSHAKE_VERSION_REGEX = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)");
}
