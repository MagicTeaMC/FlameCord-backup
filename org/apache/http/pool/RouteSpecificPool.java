package org.apache.http.pool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Future;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

abstract class RouteSpecificPool<T, C, E extends PoolEntry<T, C>> {
  private final T route;
  
  private final Set<E> leased;
  
  private final LinkedList<E> available;
  
  private final LinkedList<Future<E>> pending;
  
  RouteSpecificPool(T route) {
    this.route = route;
    this.leased = new HashSet<E>();
    this.available = new LinkedList<E>();
    this.pending = new LinkedList<Future<E>>();
  }
  
  protected abstract E createEntry(C paramC);
  
  public final T getRoute() {
    return this.route;
  }
  
  public int getLeasedCount() {
    return this.leased.size();
  }
  
  public int getPendingCount() {
    return this.pending.size();
  }
  
  public int getAvailableCount() {
    return this.available.size();
  }
  
  public int getAllocatedCount() {
    return this.available.size() + this.leased.size();
  }
  
  public E getFree(Object state) {
    if (!this.available.isEmpty()) {
      if (state != null) {
        Iterator<E> iterator = this.available.iterator();
        while (iterator.hasNext()) {
          PoolEntry poolEntry = (PoolEntry)iterator.next();
          if (state.equals(poolEntry.getState())) {
            iterator.remove();
            this.leased.add((E)poolEntry);
            return (E)poolEntry;
          } 
        } 
      } 
      Iterator<E> it = this.available.iterator();
      while (it.hasNext()) {
        PoolEntry poolEntry = (PoolEntry)it.next();
        if (poolEntry.getState() == null) {
          it.remove();
          this.leased.add((E)poolEntry);
          return (E)poolEntry;
        } 
      } 
    } 
    return null;
  }
  
  public E getLastUsed() {
    return this.available.isEmpty() ? null : this.available.getLast();
  }
  
  public boolean remove(E entry) {
    Args.notNull(entry, "Pool entry");
    if (!this.available.remove(entry) && 
      !this.leased.remove(entry))
      return false; 
    return true;
  }
  
  public void free(E entry, boolean reusable) {
    Args.notNull(entry, "Pool entry");
    boolean found = this.leased.remove(entry);
    Asserts.check(found, "Entry %s has not been leased from this pool", entry);
    if (reusable)
      this.available.addFirst(entry); 
  }
  
  public E add(C conn) {
    E entry = createEntry(conn);
    this.leased.add(entry);
    return entry;
  }
  
  public void queue(Future<E> future) {
    if (future == null)
      return; 
    this.pending.add(future);
  }
  
  public Future<E> nextPending() {
    return this.pending.poll();
  }
  
  public void unqueue(Future<E> future) {
    if (future == null)
      return; 
    this.pending.remove(future);
  }
  
  public void shutdown() {
    for (Future<E> future : this.pending)
      future.cancel(true); 
    this.pending.clear();
    for (PoolEntry poolEntry : this.available)
      poolEntry.close(); 
    this.available.clear();
    for (PoolEntry poolEntry : this.leased)
      poolEntry.close(); 
    this.leased.clear();
  }
  
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    buffer.append("[route: ");
    buffer.append(this.route);
    buffer.append("][leased: ");
    buffer.append(this.leased.size());
    buffer.append("][available: ");
    buffer.append(this.available.size());
    buffer.append("][pending: ");
    buffer.append(this.pending.size());
    buffer.append("]");
    return buffer.toString();
  }
}
