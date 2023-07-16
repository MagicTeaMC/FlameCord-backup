package dev._2lstudios.flamecord.utils;

import dev._2lstudios.flamecord.utils.iridiumcolorapi.IridiumColorAPI;
import java.util.List;
import net.md_5.bungee.api.ChatColor;

public class ColorUtil {
  public static String color(String text) {
    return ChatColor.translateAlternateColorCodes('&', text);
  }
  
  public static List<String> color(List<String> texts) {
    for (int i = 0; i < texts.size(); i++)
      texts.set(i, color(texts.get(i))); 
    return texts;
  }
  
  public static String hex(String text, int protocol) {
    if (text == null)
      return text; 
    return IridiumColorAPI.process(text, protocol);
  }
  
  public static String hexColor(String text, int protocol) {
    if (text != null) {
      text = color(text);
      if (text != null)
        text = hex(text, protocol); 
    } 
    return text;
  }
  
  public static List<String> hexColor(List<String> texts, int protocol) {
    for (int i = 0; i < texts.size(); i++)
      texts.set(i, hexColor(texts.get(i), protocol)); 
    return texts;
  }
}
