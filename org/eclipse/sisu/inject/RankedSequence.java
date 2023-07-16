package org.eclipse.sisu.inject;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

final class RankedSequence<T> extends AtomicReference<RankedSequence.Content> implements Iterable<T> {
  private static final long serialVersionUID = 1L;
  
  RankedSequence() {}
  
  RankedSequence(RankedSequence<T> sequence) {
    if (sequence != null)
      set(sequence.get()); 
  }
  
  public void insert(T element, int rank) {
    Content o;
    Content n;
    do {
      n = ((o = get()) != null) ? o.insert(element, rank) : new Content(element, rank);
    } while (!compareAndSet(o, n));
  }
  
  public T peek() {
    Content content = get();
    return (content != null) ? (T)content.objs[0] : null;
  }
  
  public boolean contains(Object element) {
    Content content = get();
    return (content != null && content.indexOf(element) >= 0);
  }
  
  public boolean containsThis(Object element) {
    Content content = get();
    return (content != null && content.indexOfThis(element) >= 0);
  }
  
  public T remove(Object element) {
    Content o;
    Content n;
    int index;
    do {
      if ((o = get()) == null || (index = o.indexOf(element)) < 0)
        return null; 
      n = o.remove(index);
    } while (!compareAndSet(o, n));
    return (T)o.objs[index];
  }
  
  public boolean removeThis(T element) {
    Content o;
    Content n;
    do {
      int index;
      if ((o = get()) == null || (index = o.indexOfThis(element)) < 0)
        return false; 
      n = o.remove(index);
    } while (!compareAndSet(o, n));
    return true;
  }
  
  public Iterable<T> snapshot() {
    Content content = get();
    return (content != null) ? Arrays.<T>asList((T[])content.objs) : Collections.EMPTY_SET;
  }
  
  public void clear() {
    set(null);
  }
  
  public boolean isEmpty() {
    return (get() == null);
  }
  
  public int size() {
    Content content = get();
    return (content != null) ? content.objs.length : 0;
  }
  
  public Itr iterator() {
    return new Itr();
  }
  
  static long rank2uid(int rank, int uniq) {
    return (rank ^ 0xFFFFFFFF) << 32L | 0xFFFFFFFFL & uniq;
  }
  
  static int uid2rank(long uid) {
    return (int)((uid ^ 0xFFFFFFFFFFFFFFFFL) >>> 32L);
  }
  
  static int safeBinarySearch(long[] uids, long uid) {
    if (uid < uids[0])
      return 0; 
    int min = 0;
    int max = uids.length - 1;
    while (min < max) {
      int m = min + max >>> 1;
      if (uid <= uids[m]) {
        max = m;
        continue;
      } 
      min = m + 1;
    } 
    if (min == uids.length - 1 && uids[min] < uid)
      min++; 
    return min;
  }
  
  static final class Content {
    final Object[] objs;
    
    final long[] uids;
    
    final int uniq;
    
    Content(Object element, int rank) {
      this.objs = new Object[] { element };
      this.uids = new long[] { RankedSequence.rank2uid(rank, 0) };
      this.uniq = 1;
    }
    
    Content(Object[] objs, long[] uids, int uniq) {
      this.objs = objs;
      this.uids = uids;
      this.uniq = uniq;
    }
    
    public int indexOf(Object element) {
      if (element == null)
        return indexOfThis(null); 
      for (int i = 0; i < this.objs.length; i++) {
        if (element.equals(this.objs[i]))
          return i; 
      } 
      return -1;
    }
    
    public int indexOfThis(Object element) {
      for (int i = 0; i < this.objs.length; i++) {
        if (element == this.objs[i])
          return i; 
      } 
      return -1;
    }
    
    public Content insert(Object element, int rank) {
      int size = this.objs.length + 1;
      Object[] newObjs = new Object[size];
      long[] newUIDs = new long[size];
      long uid = RankedSequence.rank2uid(rank, this.uniq);
      int index = RankedSequence.safeBinarySearch(this.uids, uid);
      if (index > 0) {
        System.arraycopy(this.objs, 0, newObjs, 0, index);
        System.arraycopy(this.uids, 0, newUIDs, 0, index);
      } 
      newObjs[index] = element;
      newUIDs[index] = uid;
      int destPos = index + 1, len = size - destPos;
      if (len > 0) {
        System.arraycopy(this.objs, index, newObjs, destPos, len);
        System.arraycopy(this.uids, index, newUIDs, destPos, len);
      } 
      return new Content(newObjs, newUIDs, this.uniq + 1);
    }
    
    public Content remove(int index) {
      if (this.objs.length == 1)
        return null; 
      int size = this.objs.length - 1;
      Object[] newObjs = new Object[size];
      long[] newUIDs = new long[size];
      if (index > 0) {
        System.arraycopy(this.objs, 0, newObjs, 0, index);
        System.arraycopy(this.uids, 0, newUIDs, 0, index);
      } 
      int srcPos = index + 1, len = size - index;
      if (len > 0) {
        System.arraycopy(this.objs, srcPos, newObjs, index, len);
        System.arraycopy(this.uids, srcPos, newUIDs, index, len);
      } 
      return new Content(newObjs, newUIDs, this.uniq);
    }
  }
  
  final class Itr implements Iterator<T> {
    private RankedSequence.Content content;
    
    private T nextObj;
    
    private long nextUID;
    
    private int index;
    
    Itr() {
      this.nextUID = Long.MIN_VALUE;
      this.index = -1;
    }
    
    public boolean hasNext() {
      if (this.nextObj != null)
        return true; 
      RankedSequence.Content newContent = RankedSequence.this.get();
      if (this.content != newContent) {
        this.index = (newContent != null) ? RankedSequence.safeBinarySearch(newContent.uids, this.nextUID) : -1;
        this.content = newContent;
      } 
      if (this.index >= 0 && this.index < this.content.objs.length) {
        this.nextObj = (T)this.content.objs[this.index];
        this.nextUID = this.content.uids[this.index];
        return true;
      } 
      return false;
    }
    
    public boolean hasNext(int rank) {
      if (this.nextObj != null)
        return (RankedSequence.uid2rank(this.nextUID) >= rank); 
      RankedSequence.Content newContent = RankedSequence.this.get();
      if (this.content != newContent) {
        this.index = (newContent != null) ? RankedSequence.safeBinarySearch(newContent.uids, this.nextUID) : -1;
        this.content = newContent;
      } 
      if (this.index >= 0 && this.index < this.content.uids.length)
        return (RankedSequence.uid2rank(this.content.uids[this.index]) >= rank); 
      return false;
    }
    
    public T next() {
      if (hasNext()) {
        this.nextUID++;
        this.index++;
        T element = this.nextObj;
        this.nextObj = null;
        return element;
      } 
      throw new NoSuchElementException();
    }
    
    public int rank() {
      return RankedSequence.uid2rank(this.nextUID);
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
