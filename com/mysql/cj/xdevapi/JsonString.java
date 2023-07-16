package com.mysql.cj.xdevapi;

import java.util.HashMap;

public class JsonString implements JsonValue {
  static HashMap<Character, String> escapeChars = new HashMap<>();
  
  static {
    for (JsonParser.EscapeChar ec : JsonParser.EscapeChar.values()) {
      if (ec.NEEDS_ESCAPING)
        escapeChars.put(Character.valueOf(ec.CHAR), ec.ESCAPED); 
    } 
  }
  
  private String val = "";
  
  public String getString() {
    return this.val;
  }
  
  public JsonString setValue(String value) {
    this.val = value;
    return this;
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder("\"");
    for (int i = 0; i < this.val.length(); i++) {
      if (escapeChars.containsKey(Character.valueOf(this.val.charAt(i)))) {
        sb.append(escapeChars.get(Character.valueOf(this.val.charAt(i))));
      } else {
        sb.append(this.val.charAt(i));
      } 
    } 
    sb.append("\"");
    return sb.toString();
  }
}
