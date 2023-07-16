package net.md_5.bungee;

import com.google.common.base.Joiner;
import com.google.common.primitives.Ints;
import com.google.common.primitives.UnsignedLongs;
import io.github.waterfallmc.waterfall.utils.Hex;
import io.netty.channel.unix.DomainSocketAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.UUID;

public class Util {
  public static final int DEFAULT_PORT = 25565;
  
  public static SocketAddress getAddr(String hostline) {
    URI uri = null;
    try {
      uri = new URI(hostline);
    } catch (URISyntaxException uRISyntaxException) {}
    if (uri != null && "unix".equals(uri.getScheme()))
      return (SocketAddress)new DomainSocketAddress(uri.getPath()); 
    if (uri == null || uri.getHost() == null)
      try {
        uri = new URI("tcp://" + hostline);
      } catch (URISyntaxException ex) {
        throw new IllegalArgumentException("Bad hostline: " + hostline, ex);
      }  
    if (uri.getHost() == null)
      throw new IllegalArgumentException("Invalid host/address: " + hostline); 
    return new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 25565 : uri.getPort());
  }
  
  public static String hex(int i) {
    return Hex.encodeString(Ints.toByteArray(i));
  }
  
  public static String unicode(char c) {
    return "\\u" + String.format("%04x", new Object[] { Integer.valueOf(c) }).toUpperCase(Locale.ROOT);
  }
  
  public static String exception(Throwable t) {
    return exception(t, true);
  }
  
  public static String exception(Throwable t, boolean includeLineNumbers) {
    StackTraceElement[] trace = t.getStackTrace();
    return t.getClass().getSimpleName() + " : " + t.getMessage() + ((includeLineNumbers && trace.length > 0) ? (" @ " + t
      .getStackTrace()[0].getClassName() + ":" + t.getStackTrace()[0].getLineNumber()) : "");
  }
  
  public static String csv(Iterable<?> objects) {
    return format(objects, ", ");
  }
  
  @Deprecated
  public static String format(Iterable<?> objects, String separators) {
    return Joiner.on(separators).join(objects);
  }
  
  public static UUID getUUID(String uuid) {
    return new UUID(UnsignedLongs.parseUnsignedLong(uuid.substring(0, 16), 16), UnsignedLongs.parseUnsignedLong(uuid.substring(16), 16));
  }
}
