package org.codehaus.plexus.interpolation.util;

public class StringUtils {
  public static String replace(String text, String repl, String with) {
    return replace(text, repl, with, -1);
  }
  
  public static String replace(String text, String repl, String with, int max) {
    if (text == null || repl == null || with == null || repl.length() == 0)
      return text; 
    StringBuilder buf = new StringBuilder(text.length());
    int start = 0;
    int end;
    while ((end = text.indexOf(repl, start)) != -1) {
      buf.append(text, start, end).append(with);
      start = end + repl.length();
      if (--max == 0)
        break; 
    } 
    buf.append(text, start, text.length());
    return buf.toString();
  }
  
  public static String capitalizeFirstLetter(String data) {
    char firstChar = data.charAt(0);
    char titleCase = Character.toTitleCase(firstChar);
    if (firstChar == titleCase)
      return data; 
    StringBuilder result = new StringBuilder(data.length());
    result.append(titleCase);
    result.append(data, 1, data.length());
    return result.toString();
  }
}
