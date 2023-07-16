package io.netty.resolver.dns;

import io.netty.channel.socket.InternetProtocolFamily;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Comparator;

final class PreferredAddressTypeComparator implements Comparator<InetAddress> {
  private static final PreferredAddressTypeComparator IPv4 = new PreferredAddressTypeComparator((Class)Inet4Address.class);
  
  private static final PreferredAddressTypeComparator IPv6 = new PreferredAddressTypeComparator((Class)Inet6Address.class);
  
  private final Class<? extends InetAddress> preferredAddressType;
  
  static PreferredAddressTypeComparator comparator(InternetProtocolFamily family) {
    switch (family) {
      case IPv4:
        return IPv4;
      case IPv6:
        return IPv6;
    } 
    throw new IllegalArgumentException();
  }
  
  private PreferredAddressTypeComparator(Class<? extends InetAddress> preferredAddressType) {
    this.preferredAddressType = preferredAddressType;
  }
  
  public int compare(InetAddress o1, InetAddress o2) {
    if (o1.getClass() == o2.getClass())
      return 0; 
    return this.preferredAddressType.isAssignableFrom(o1.getClass()) ? -1 : 1;
  }
}
