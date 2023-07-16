package org.apache.http.conn.util;

public class DnsUtils {
  private static boolean isUpper(char c) {
    return (c >= 'A' && c <= 'Z');
  }
  
  public static String normalize(String s) {
    if (s == null)
      return null; 
    int pos = 0;
    int remaining = s.length();
    while (remaining > 0 && 
      !isUpper(s.charAt(pos))) {
      pos++;
      remaining--;
    } 
    if (remaining > 0) {
      StringBuilder buf = new StringBuilder(s.length());
      buf.append(s, 0, pos);
      while (remaining > 0) {
        char c = s.charAt(pos);
        if (isUpper(c)) {
          buf.append((char)(c + 32));
        } else {
          buf.append(c);
        } 
        pos++;
        remaining--;
      } 
      return buf.toString();
    } 
    return s;
  }
}
