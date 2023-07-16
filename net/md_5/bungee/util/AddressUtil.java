package net.md_5.bungee.util;

import com.google.common.base.Preconditions;
import java.net.InetSocketAddress;

public class AddressUtil {
  public static String sanitizeAddress(InetSocketAddress addr) {
    Preconditions.checkArgument(!addr.isUnresolved(), "Unresolved address");
    String string = addr.getAddress().getHostAddress();
    if (addr.getAddress() instanceof java.net.Inet6Address) {
      int strip = string.indexOf('%');
      return (strip == -1) ? string : string.substring(0, strip);
    } 
    return string;
  }
}
