package dev._2lstudios.flamecord.utils.iridiumcolorapi.patterns;

import dev._2lstudios.flamecord.utils.iridiumcolorapi.IridiumColorAPI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RainbowPattern implements Pattern {
  Pattern pattern = Pattern.compile("<RAINBOW([0-9]{1,3})>(.*?)</RAINBOW>");
  
  public String process(String string, int protocol) {
    Matcher matcher = this.pattern.matcher(string);
    while (matcher.find()) {
      String saturation = matcher.group(1);
      String content = matcher.group(2);
      string = string.replace(matcher.group(), IridiumColorAPI.rainbow(content, Float.parseFloat(saturation), protocol));
    } 
    return string;
  }
}
