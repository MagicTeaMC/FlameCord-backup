package net.md_5.bungee.util;

public final class AllowedCharacters {
  public static boolean isChatAllowedCharacter(char character) {
    return (character != '§' && character >= ' ' && character != '');
  }
  
  private static boolean isNameAllowedCharacter(char c, boolean onlineMode) {
    if (onlineMode)
      return ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || c == '.' || c == '-'); 
    return (isChatAllowedCharacter(c) && c != ' ' && c != '.');
  }
  
  public static boolean isValidName(String name, boolean onlineMode) {
    for (int index = 0, len = name.length(); index < len; index++) {
      if (!isNameAllowedCharacter(name.charAt(index), onlineMode))
        return false; 
    } 
    return true;
  }
}
