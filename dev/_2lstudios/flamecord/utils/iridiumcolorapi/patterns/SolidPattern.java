package dev._2lstudios.flamecord.utils.iridiumcolorapi.patterns;

import dev._2lstudios.flamecord.utils.iridiumcolorapi.IridiumColorAPI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SolidPattern implements Pattern {
  Pattern pattern = Pattern.compile("<SOLID:([0-9A-Fa-f]{6})>|#\\{([0-9A-Fa-f]{6})}");
  
  public String process(String string, int protocol) {
    Matcher matcher = this.pattern.matcher(string);
    while (matcher.find()) {
      String color = matcher.group(1);
      if (color == null)
        color = matcher.group(2); 
      string = string.replace(matcher.group(), IridiumColorAPI.getColor(color, protocol) + "");
    } 
    return string;
  }
}
