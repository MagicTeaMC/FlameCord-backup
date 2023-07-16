package org.eclipse.sisu.inject;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

final class MildElements<T> extends AbstractCollection<T> {
  private final ReferenceQueue<T> queue = new ReferenceQueue<T>();
  
  final List<Reference<T>> list;
  
  private final boolean soft;
  
  MildElements(List<Reference<T>> list, boolean soft) {
    this.list = list;
    this.soft = soft;
  }
  
  public boolean add(T element) {
    compact();
    return this.list.add(this.soft ? new Soft<T>(element, this.queue, this.list.size()) : new Weak<T>(element, this.queue, this.list.size()));
  }
  
  public int size() {
    compact();
    return this.list.size();
  }
  
  public Iterator<T> iterator() {
    compact();
    return new Itr();
  }
  
  private void compact() {
    Reference<? extends T> ref;
    while ((ref = this.queue.poll()) != null)
      evict(ref); 
  }
  
  void evict(Reference<? extends T> ref) {
    int index = ((Indexable)ref).index(-1);
    if (index >= 0) {
      Reference<T> last = this.list.remove(this.list.size() - 1);
      if (ref != last) {
        ((Indexable)last).index(index);
        this.list.set(index, last);
      } 
    } 
  }
  
  private static interface Indexable {
    int index(int param1Int);
  }
  
  final class Itr implements Iterator<T> {
    private int index;
    
    private T nextElement;
    
    private boolean haveElement;
    
    public boolean hasNext() {
      while (this.nextElement == null && this.index < MildElements.this.list.size())
        this.nextElement = ((Reference<T>)MildElements.this.list.get(this.index++)).get(); 
      return (this.nextElement != null);
    }
    
    public T next() {
      this.haveElement = hasNext();
      if (this.haveElement) {
        T element = this.nextElement;
        this.nextElement = null;
        return element;
      } 
      throw new NoSuchElementException();
    }
    
    public void remove() {
      if (this.haveElement) {
        MildElements.this.evict(MildElements.this.list.get(--this.index));
        this.haveElement = false;
      } else {
        throw new IllegalStateException();
      } 
    }
  }
  
  private static final class Soft<T> extends SoftReference<T> implements Indexable {
    private int index;
    
    Soft(T value, ReferenceQueue<T> queue, int index) {
      super(value, queue);
      this.index = index;
    }
    
    public int index(int newIndex) {
      int oldIndex = this.index;
      this.index = newIndex;
      return oldIndex;
    }
  }
  
  private static final class Weak<T> extends WeakReference<T> implements Indexable {
    private int index;
    
    Weak(T value, ReferenceQueue<T> queue, int index) {
      super(value, queue);
      this.index = index;
    }
    
    public int index(int newIndex) {
      int oldIndex = this.index;
      this.index = newIndex;
      return oldIndex;
    }
  }
}
