package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.PlatformDependent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

abstract class Cache<E> {
  private static final AtomicReferenceFieldUpdater<Entries, ScheduledFuture> FUTURE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(Entries.class, ScheduledFuture.class, "expirationFuture");
  
  private static final ScheduledFuture<?> CANCELLED = new ScheduledFuture() {
      public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
      }
      
      public long getDelay(TimeUnit unit) {
        return Long.MIN_VALUE;
      }
      
      public int compareTo(Delayed o) {
        throw new UnsupportedOperationException();
      }
      
      public boolean isCancelled() {
        return true;
      }
      
      public boolean isDone() {
        return true;
      }
      
      public Object get() {
        throw new UnsupportedOperationException();
      }
      
      public Object get(long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException();
      }
    };
  
  static final int MAX_SUPPORTED_TTL_SECS = (int)TimeUnit.DAYS.toSeconds(730L);
  
  private final ConcurrentMap<String, Entries> resolveCache = PlatformDependent.newConcurrentHashMap();
  
  final void clear() {
    while (!this.resolveCache.isEmpty()) {
      for (Iterator<Map.Entry<String, Entries>> i = this.resolveCache.entrySet().iterator(); i.hasNext(); ) {
        Map.Entry<String, Entries> e = i.next();
        i.remove();
        ((Entries)e.getValue()).clearAndCancel();
      } 
    } 
  }
  
  final boolean clear(String hostname) {
    Entries entries = this.resolveCache.remove(hostname);
    return (entries != null && entries.clearAndCancel());
  }
  
  final List<? extends E> get(String hostname) {
    Entries entries = this.resolveCache.get(hostname);
    return (entries == null) ? null : entries.get();
  }
  
  final void cache(String hostname, E value, int ttl, EventLoop loop) {
    Entries entries = this.resolveCache.get(hostname);
    if (entries == null) {
      entries = new Entries(hostname);
      Entries oldEntries = this.resolveCache.putIfAbsent(hostname, entries);
      if (oldEntries != null)
        entries = oldEntries; 
    } 
    entries.add(value, ttl, loop);
  }
  
  final int size() {
    return this.resolveCache.size();
  }
  
  protected void sortEntries(String hostname, List<E> entries) {}
  
  protected abstract boolean shouldReplaceAll(E paramE);
  
  protected abstract boolean equals(E paramE1, E paramE2);
  
  private final class Entries extends AtomicReference<List<E>> implements Runnable {
    private final String hostname;
    
    volatile ScheduledFuture<?> expirationFuture;
    
    Entries(String hostname) {
      super(Collections.emptyList());
      this.hostname = hostname;
    }
    
    void add(E e, int ttl, EventLoop loop) {
      if (!Cache.this.shouldReplaceAll(e))
        while (true) {
          List<E> entries = get();
          if (!entries.isEmpty()) {
            E firstEntry = entries.get(0);
            if (Cache.this.shouldReplaceAll(firstEntry)) {
              assert entries.size() == 1;
              if (compareAndSet(entries, Collections.singletonList(e))) {
                scheduleCacheExpirationIfNeeded(ttl, loop);
                return;
              } 
              continue;
            } 
            List<E> newEntries = new ArrayList<E>(entries.size() + 1);
            int i = 0;
            E replacedEntry = null;
            do {
              E entry = entries.get(i);
              if (!Cache.this.equals(e, entry)) {
                newEntries.add(entry);
              } else {
                replacedEntry = entry;
                newEntries.add(e);
                i++;
                for (; i < entries.size(); i++)
                  newEntries.add(entries.get(i)); 
                break;
              } 
            } while (++i < entries.size());
            if (replacedEntry == null)
              newEntries.add(e); 
            Cache.this.sortEntries(this.hostname, newEntries);
            if (compareAndSet(entries, Collections.unmodifiableList(newEntries))) {
              scheduleCacheExpirationIfNeeded(ttl, loop);
              return;
            } 
            continue;
          } 
          if (compareAndSet(entries, Collections.singletonList(e))) {
            scheduleCacheExpirationIfNeeded(ttl, loop);
            return;
          } 
        }  
      set(Collections.singletonList(e));
      scheduleCacheExpirationIfNeeded(ttl, loop);
    }
    
    private void scheduleCacheExpirationIfNeeded(int ttl, EventLoop loop) {
      while (true) {
        ScheduledFuture<?> oldFuture = Cache.FUTURE_UPDATER.get(this);
        if (oldFuture == null || oldFuture.getDelay(TimeUnit.SECONDS) > ttl) {
          ScheduledFuture scheduledFuture = loop.schedule(this, ttl, TimeUnit.SECONDS);
          if (Cache.FUTURE_UPDATER.compareAndSet(this, oldFuture, scheduledFuture)) {
            if (oldFuture != null)
              oldFuture.cancel(true); 
            break;
          } 
          scheduledFuture.cancel(true);
          continue;
        } 
        break;
      } 
    }
    
    boolean clearAndCancel() {
      List<E> entries = getAndSet(Collections.emptyList());
      if (entries.isEmpty())
        return false; 
      ScheduledFuture<?> expirationFuture = Cache.FUTURE_UPDATER.getAndSet(this, Cache.CANCELLED);
      if (expirationFuture != null)
        expirationFuture.cancel(false); 
      return true;
    }
    
    public void run() {
      Cache.access$200(Cache.this).remove(this.hostname, this);
      clearAndCancel();
    }
  }
}
