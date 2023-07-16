package net.md_5.bungee.forge;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class ForgeUtils {
  public static Set<String> readRegisteredChannels(PluginMessage pluginMessage) {
    String channels = new String(pluginMessage.getData(), Charsets.UTF_8);
    String[] split = channels.split("\000");
    return (Set<String>)ImmutableSet.copyOf((Object[])split);
  }
  
  public static Map<String, String> readModList(PluginMessage pluginMessage) {
    Map<String, String> modTags = new HashMap<>();
    ByteBuf payload = Unpooled.wrappedBuffer(pluginMessage.getData());
    try {
      byte discriminator = payload.readByte();
      if (discriminator == 2) {
        ByteBuf buffer = payload.slice();
        int modCount = DefinedPacket.readVarInt(buffer);
        for (int i = 0; i < modCount; i++)
          modTags.put(DefinedPacket.readString(buffer), DefinedPacket.readString(buffer)); 
      } 
    } finally {
      payload.release();
    } 
    return modTags;
  }
  
  public static int getFmlBuildNumber(Map<String, String> modList) {
    if (modList.containsKey("FML")) {
      String fmlVersion = modList.get("FML");
      if (fmlVersion.equals("7.10.99.99")) {
        Matcher matcher = ForgeConstants.FML_HANDSHAKE_VERSION_REGEX.matcher(modList.get("Forge"));
        if (matcher.find())
          return Integer.parseInt(matcher.group(4)); 
      } else {
        Matcher matcher = ForgeConstants.FML_HANDSHAKE_VERSION_REGEX.matcher(fmlVersion);
        if (matcher.find())
          return Integer.parseInt(matcher.group(4)); 
      } 
    } 
    return 0;
  }
}
