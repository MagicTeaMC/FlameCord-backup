package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

public class DefaultDnsCache implements DnsCache {
  private final Cache<DefaultDnsCacheEntry> resolveCache = new Cache<DefaultDnsCacheEntry>() {
      protected boolean shouldReplaceAll(DefaultDnsCache.DefaultDnsCacheEntry entry) {
        return (entry.cause() != null);
      }
      
      protected boolean equals(DefaultDnsCache.DefaultDnsCacheEntry entry, DefaultDnsCache.DefaultDnsCacheEntry otherEntry) {
        if (entry.address() != null)
          return entry.address().equals(otherEntry.address()); 
        if (otherEntry.address() != null)
          return false; 
        return entry.cause().equals(otherEntry.cause());
      }
    };
  
  private final int minTtl;
  
  private final int maxTtl;
  
  private final int negativeTtl;
  
  public DefaultDnsCache() {
    this(0, Cache.MAX_SUPPORTED_TTL_SECS, 0);
  }
  
  public DefaultDnsCache(int minTtl, int maxTtl, int negativeTtl) {
    this.minTtl = Math.min(Cache.MAX_SUPPORTED_TTL_SECS, ObjectUtil.checkPositiveOrZero(minTtl, "minTtl"));
    this.maxTtl = Math.min(Cache.MAX_SUPPORTED_TTL_SECS, ObjectUtil.checkPositiveOrZero(maxTtl, "maxTtl"));
    if (minTtl > maxTtl)
      throw new IllegalArgumentException("minTtl: " + minTtl + ", maxTtl: " + maxTtl + " (expected: 0 <= minTtl <= maxTtl)"); 
    this.negativeTtl = ObjectUtil.checkPositiveOrZero(negativeTtl, "negativeTtl");
  }
  
  public int minTtl() {
    return this.minTtl;
  }
  
  public int maxTtl() {
    return this.maxTtl;
  }
  
  public int negativeTtl() {
    return this.negativeTtl;
  }
  
  public void clear() {
    this.resolveCache.clear();
  }
  
  public boolean clear(String hostname) {
    ObjectUtil.checkNotNull(hostname, "hostname");
    return this.resolveCache.clear(appendDot(hostname));
  }
  
  private static boolean emptyAdditionals(DnsRecord[] additionals) {
    return (additionals == null || additionals.length == 0);
  }
  
  public List<? extends DnsCacheEntry> get(String hostname, DnsRecord[] additionals) {
    ObjectUtil.checkNotNull(hostname, "hostname");
    if (!emptyAdditionals(additionals))
      return Collections.emptyList(); 
    List<? extends DnsCacheEntry> entries = this.resolveCache.get(appendDot(hostname));
    if (entries == null || entries.isEmpty())
      return entries; 
    return new DnsCacheEntryList(entries);
  }
  
  public DnsCacheEntry cache(String hostname, DnsRecord[] additionals, InetAddress address, long originalTtl, EventLoop loop) {
    ObjectUtil.checkNotNull(hostname, "hostname");
    ObjectUtil.checkNotNull(address, "address");
    ObjectUtil.checkNotNull(loop, "loop");
    DefaultDnsCacheEntry e = new DefaultDnsCacheEntry(hostname, address);
    if (this.maxTtl == 0 || !emptyAdditionals(additionals))
      return e; 
    this.resolveCache.cache(appendDot(hostname), e, Math.max(this.minTtl, (int)Math.min(this.maxTtl, originalTtl)), loop);
    return e;
  }
  
  public DnsCacheEntry cache(String hostname, DnsRecord[] additionals, Throwable cause, EventLoop loop) {
    ObjectUtil.checkNotNull(hostname, "hostname");
    ObjectUtil.checkNotNull(cause, "cause");
    ObjectUtil.checkNotNull(loop, "loop");
    DefaultDnsCacheEntry e = new DefaultDnsCacheEntry(hostname, cause);
    if (this.negativeTtl == 0 || !emptyAdditionals(additionals))
      return e; 
    this.resolveCache.cache(appendDot(hostname), e, this.negativeTtl, loop);
    return e;
  }
  
  public String toString() {
    return "DefaultDnsCache(minTtl=" + 
      this.minTtl + 
      ", maxTtl=" + this.maxTtl + 
      ", negativeTtl=" + this.negativeTtl + 
      ", cached resolved hostname=" + this.resolveCache
      .size() + ')';
  }
  
  private static final class DefaultDnsCacheEntry implements DnsCacheEntry {
    private final String hostname;
    
    private final InetAddress address;
    
    private final Throwable cause;
    
    private final int hash;
    
    DefaultDnsCacheEntry(String hostname, InetAddress address) {
      this.hostname = hostname;
      this.address = address;
      this.cause = null;
      this.hash = System.identityHashCode(this);
    }
    
    DefaultDnsCacheEntry(String hostname, Throwable cause) {
      this.hostname = hostname;
      this.cause = cause;
      this.address = null;
      this.hash = System.identityHashCode(this);
    }
    
    private DefaultDnsCacheEntry(DefaultDnsCacheEntry entry) {
      this.hostname = entry.hostname;
      if (entry.cause == null) {
        this.address = entry.address;
        this.cause = null;
      } else {
        this.address = null;
        this.cause = DefaultDnsCache.copyThrowable(entry.cause);
      } 
      this.hash = entry.hash;
    }
    
    public InetAddress address() {
      return this.address;
    }
    
    public Throwable cause() {
      return this.cause;
    }
    
    String hostname() {
      return this.hostname;
    }
    
    public String toString() {
      if (this.cause != null)
        return this.hostname + '/' + this.cause; 
      return this.address.toString();
    }
    
    public int hashCode() {
      return this.hash;
    }
    
    public boolean equals(Object obj) {
      return (obj instanceof DefaultDnsCacheEntry && ((DefaultDnsCacheEntry)obj).hash == this.hash);
    }
    
    DnsCacheEntry copyIfNeeded() {
      if (this.cause == null)
        return this; 
      return new DefaultDnsCacheEntry(this);
    }
  }
  
  private static String appendDot(String hostname) {
    return StringUtil.endsWith(hostname, '.') ? hostname : (hostname + '.');
  }
  
  private static Throwable copyThrowable(Throwable error) {
    if (error.getClass() == UnknownHostException.class) {
      UnknownHostException copy = new UnknownHostException(error.getMessage()) {
          public Throwable fillInStackTrace() {
            return this;
          }
        };
      copy.initCause(error.getCause());
      copy.setStackTrace(error.getStackTrace());
      return copy;
    } 
    ObjectOutputStream oos = null;
    ObjectInputStream ois = null;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      oos = new ObjectOutputStream(baos);
      oos.writeObject(error);
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      ois = new ObjectInputStream(bais);
      return (Throwable)ois.readObject();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    } finally {
      if (oos != null)
        try {
          oos.close();
        } catch (IOException iOException) {} 
      if (ois != null)
        try {
          ois.close();
        } catch (IOException iOException) {} 
    } 
  }
  
  private static final class DnsCacheEntryList extends AbstractList<DnsCacheEntry> {
    private final List<? extends DnsCacheEntry> entries;
    
    DnsCacheEntryList(List<? extends DnsCacheEntry> entries) {
      this.entries = entries;
    }
    
    public DnsCacheEntry get(int index) {
      DefaultDnsCache.DefaultDnsCacheEntry entry = (DefaultDnsCache.DefaultDnsCacheEntry)this.entries.get(index);
      return entry.copyIfNeeded();
    }
    
    public int size() {
      return this.entries.size();
    }
    
    public int hashCode() {
      return super.hashCode();
    }
    
    public boolean equals(Object o) {
      if (o instanceof DnsCacheEntryList)
        return this.entries.equals(((DnsCacheEntryList)o).entries); 
      return super.equals(o);
    }
  }
}
