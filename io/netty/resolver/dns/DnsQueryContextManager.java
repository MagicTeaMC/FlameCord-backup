package io.netty.resolver.dns;

import io.netty.util.NetUtil;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.internal.PlatformDependent;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

final class DnsQueryContextManager {
  private final Map<InetSocketAddress, DnsQueryContextMap> map = new HashMap<InetSocketAddress, DnsQueryContextMap>();
  
  int add(InetSocketAddress nameServerAddr, DnsQueryContext qCtx) {
    DnsQueryContextMap contexts = getOrCreateContextMap(nameServerAddr);
    return contexts.add(qCtx);
  }
  
  DnsQueryContext get(InetSocketAddress nameServerAddr, int id) {
    DnsQueryContextMap contexts = getContextMap(nameServerAddr);
    if (contexts == null)
      return null; 
    return contexts.get(id);
  }
  
  DnsQueryContext remove(InetSocketAddress nameServerAddr, int id) {
    DnsQueryContextMap contexts = getContextMap(nameServerAddr);
    if (contexts == null)
      return null; 
    return contexts.remove(id);
  }
  
  private DnsQueryContextMap getContextMap(InetSocketAddress nameServerAddr) {
    synchronized (this.map) {
      return this.map.get(nameServerAddr);
    } 
  }
  
  private DnsQueryContextMap getOrCreateContextMap(InetSocketAddress nameServerAddr) {
    synchronized (this.map) {
      DnsQueryContextMap contexts = this.map.get(nameServerAddr);
      if (contexts != null)
        return contexts; 
      DnsQueryContextMap newContexts = new DnsQueryContextMap();
      InetAddress a = nameServerAddr.getAddress();
      int port = nameServerAddr.getPort();
      DnsQueryContextMap old = this.map.put(nameServerAddr, newContexts);
      assert old == null : "DnsQueryContextMap already exists for " + nameServerAddr;
      InetSocketAddress extraAddress = null;
      if (a instanceof Inet4Address) {
        Inet4Address a4 = (Inet4Address)a;
        if (a4.isLoopbackAddress()) {
          extraAddress = new InetSocketAddress(NetUtil.LOCALHOST6, port);
        } else {
          extraAddress = new InetSocketAddress(toCompactAddress(a4), port);
        } 
      } else if (a instanceof Inet6Address) {
        Inet6Address a6 = (Inet6Address)a;
        if (a6.isLoopbackAddress()) {
          extraAddress = new InetSocketAddress(NetUtil.LOCALHOST4, port);
        } else if (a6.isIPv4CompatibleAddress()) {
          extraAddress = new InetSocketAddress(toIPv4Address(a6), port);
        } 
      } 
      if (extraAddress != null) {
        old = this.map.put(extraAddress, newContexts);
        assert old == null : "DnsQueryContextMap already exists for " + extraAddress;
      } 
      return newContexts;
    } 
  }
  
  private static Inet6Address toCompactAddress(Inet4Address a4) {
    byte[] b4 = a4.getAddress();
    byte[] b6 = { 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, b4[0], b4[1], b4[2], b4[3] };
    try {
      return (Inet6Address)InetAddress.getByAddress(b6);
    } catch (UnknownHostException e) {
      throw new Error(e);
    } 
  }
  
  private static Inet4Address toIPv4Address(Inet6Address a6) {
    assert a6.isIPv4CompatibleAddress();
    byte[] b6 = a6.getAddress();
    byte[] b4 = { b6[12], b6[13], b6[14], b6[15] };
    try {
      return (Inet4Address)InetAddress.getByAddress(b4);
    } catch (UnknownHostException e) {
      throw new Error(e);
    } 
  }
  
  private static final class DnsQueryContextMap {
    private static final int MAX_ID = 65535;
    
    private static final int MAX_TRIES = 131070;
    
    private final IntObjectMap<DnsQueryContext> map = (IntObjectMap<DnsQueryContext>)new IntObjectHashMap();
    
    synchronized int add(DnsQueryContext ctx) {
      int tries = 0;
      int id = PlatformDependent.threadLocalRandom().nextInt(65534) + 1;
      while (true) {
        DnsQueryContext oldCtx = (DnsQueryContext)this.map.put(id, ctx);
        if (oldCtx == null)
          return id; 
        this.map.put(id, oldCtx);
        id = id + 1 & 0xFFFF;
        if (++tries >= 131070)
          throw new IllegalStateException("query ID space exhausted after 131070: " + ctx
              .question()); 
      } 
    }
    
    synchronized DnsQueryContext get(int id) {
      return (DnsQueryContext)this.map.get(id);
    }
    
    synchronized DnsQueryContext remove(int id) {
      return (DnsQueryContext)this.map.remove(id);
    }
    
    private DnsQueryContextMap() {}
  }
}