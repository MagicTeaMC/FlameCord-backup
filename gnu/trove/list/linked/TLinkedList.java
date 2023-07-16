package gnu.trove.list.linked;

import gnu.trove.list.TLinkable;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractSequentialList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class TLinkedList<T extends TLinkable<T>> extends AbstractSequentialList<T> implements Externalizable {
  static final long serialVersionUID = 1L;
  
  protected T _head;
  
  protected T _tail;
  
  protected int _size = 0;
  
  public ListIterator<T> listIterator(int index) {
    return new IteratorImpl(index);
  }
  
  public int size() {
    return this._size;
  }
  
  public void add(int index, T linkable) {
    if (index < 0 || index > size())
      throw new IndexOutOfBoundsException("index:" + index); 
    insert(index, linkable);
  }
  
  public boolean add(T linkable) {
    insert(this._size, linkable);
    return true;
  }
  
  public void addFirst(T linkable) {
    insert(0, linkable);
  }
  
  public void addLast(T linkable) {
    insert(size(), linkable);
  }
  
  public void clear() {
    if (null != this._head) {
      TLinkable<T> link = this._head.getNext();
      for (; link != null; 
        link = link.getNext()) {
        TLinkable<T> prev = link.getPrevious();
        prev.setNext(null);
        link.setPrevious(null);
      } 
      this._head = this._tail = null;
    } 
    this._size = 0;
  }
  
  public Object[] toArray() {
    Object[] o = new Object[this._size];
    int i = 0;
    for (T t = this._head; t != null; tLinkable = t.getNext()) {
      TLinkable tLinkable;
      o[i++] = t;
    } 
    return o;
  }
  
  public Object[] toUnlinkedArray() {
    Object[] o = new Object[this._size];
    int i = 0;
    for (T t = this._head; t != null; i++) {
      o[i] = t;
      T t1 = t;
      TLinkable tLinkable = t.getNext();
      t1.setNext(null);
      t1.setPrevious(null);
    } 
    this._size = 0;
    this._head = this._tail = null;
    return o;
  }
  
  public T[] toUnlinkedArray(T[] a) {
    TLinkable[] arrayOfTLinkable;
    int size = size();
    if (a.length < size)
      arrayOfTLinkable = (TLinkable[])Array.newInstance(a.getClass().getComponentType(), size); 
    int i = 0;
    for (T link = this._head; link != null; i++) {
      arrayOfTLinkable[i] = (TLinkable)link;
      T tmp = link;
      TLinkable tLinkable = link.getNext();
      tmp.setNext(null);
      tmp.setPrevious(null);
    } 
    this._size = 0;
    this._head = this._tail = null;
    return (T[])arrayOfTLinkable;
  }
  
  public boolean contains(Object o) {
    for (T t = this._head; t != null; tLinkable = t.getNext()) {
      TLinkable tLinkable;
      if (o.equals(t))
        return true; 
    } 
    return false;
  }
  
  public T get(int index) {
    TLinkable tLinkable;
    if (index < 0 || index >= this._size)
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this._size); 
    if (index > this._size >> 1) {
      int i = this._size - 1;
      T t = this._tail;
      while (i > index) {
        tLinkable = t.getPrevious();
        i--;
      } 
      return (T)tLinkable;
    } 
    int position = 0;
    T node = this._head;
    while (position < index) {
      tLinkable = node.getNext();
      position++;
    } 
    return (T)tLinkable;
  }
  
  public T getFirst() {
    return this._head;
  }
  
  public T getLast() {
    return this._tail;
  }
  
  public T getNext(T current) {
    return (T)current.getNext();
  }
  
  public T getPrevious(T current) {
    return (T)current.getPrevious();
  }
  
  public T removeFirst() {
    T o = this._head;
    if (o == null)
      return null; 
    TLinkable tLinkable = o.getNext();
    o.setNext(null);
    if (null != tLinkable)
      tLinkable.setPrevious(null); 
    this._head = (T)tLinkable;
    if (--this._size == 0)
      this._tail = null; 
    return o;
  }
  
  public T removeLast() {
    T o = this._tail;
    if (o == null)
      return null; 
    TLinkable tLinkable = o.getPrevious();
    o.setPrevious(null);
    if (null != tLinkable)
      tLinkable.setNext(null); 
    this._tail = (T)tLinkable;
    if (--this._size == 0)
      this._head = null; 
    return o;
  }
  
  protected void insert(int index, T linkable) {
    if (this._size == 0) {
      this._head = this._tail = linkable;
    } else if (index == 0) {
      linkable.setNext((TLinkable)this._head);
      this._head.setPrevious((TLinkable)linkable);
      this._head = linkable;
    } else if (index == this._size) {
      this._tail.setNext((TLinkable)linkable);
      linkable.setPrevious((TLinkable)this._tail);
      this._tail = linkable;
    } else {
      T node = get(index);
      TLinkable tLinkable = node.getPrevious();
      if (tLinkable != null)
        tLinkable.setNext((TLinkable)linkable); 
      linkable.setPrevious(tLinkable);
      linkable.setNext((TLinkable)node);
      node.setPrevious((TLinkable)linkable);
    } 
    this._size++;
  }
  
  public boolean remove(Object o) {
    if (o instanceof TLinkable) {
      TLinkable<T> link = (TLinkable<T>)o;
      TLinkable tLinkable1 = link.getPrevious();
      TLinkable tLinkable2 = link.getNext();
      if (tLinkable2 == null && tLinkable1 == null) {
        if (o != this._head)
          return false; 
        this._head = this._tail = null;
      } else if (tLinkable2 == null) {
        link.setPrevious(null);
        tLinkable1.setNext(null);
        this._tail = (T)tLinkable1;
      } else if (tLinkable1 == null) {
        link.setNext(null);
        tLinkable2.setPrevious(null);
        this._head = (T)tLinkable2;
      } else {
        tLinkable1.setNext(tLinkable2);
        tLinkable2.setPrevious(tLinkable1);
        link.setNext(null);
        link.setPrevious(null);
      } 
      this._size--;
      return true;
    } 
    return false;
  }
  
  public void addBefore(T current, T newElement) {
    if (current == this._head) {
      addFirst(newElement);
    } else if (current == null) {
      addLast(newElement);
    } else {
      TLinkable tLinkable = current.getPrevious();
      newElement.setNext((TLinkable)current);
      tLinkable.setNext((TLinkable)newElement);
      newElement.setPrevious(tLinkable);
      current.setPrevious((TLinkable)newElement);
      this._size++;
    } 
  }
  
  public void addAfter(T current, T newElement) {
    if (current == this._tail) {
      addLast(newElement);
    } else if (current == null) {
      addFirst(newElement);
    } else {
      TLinkable tLinkable = current.getNext();
      newElement.setPrevious((TLinkable)current);
      newElement.setNext(tLinkable);
      current.setNext((TLinkable)newElement);
      tLinkable.setPrevious((TLinkable)newElement);
      this._size++;
    } 
  }
  
  public boolean forEachValue(TObjectProcedure<T> procedure) {
    T node = this._head;
    while (node != null) {
      boolean keep_going = procedure.execute(node);
      if (!keep_going)
        return false; 
      TLinkable tLinkable = node.getNext();
    } 
    return true;
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeInt(this._size);
    out.writeObject(this._head);
    out.writeObject(this._tail);
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._size = in.readInt();
    this._head = (T)in.readObject();
    this._tail = (T)in.readObject();
  }
  
  protected final class IteratorImpl implements ListIterator<T> {
    private int _nextIndex = 0;
    
    private T _next;
    
    private T _lastReturned;
    
    IteratorImpl(int position) {
      if (position < 0 || position > TLinkedList.this._size)
        throw new IndexOutOfBoundsException(); 
      this._nextIndex = position;
      if (position == 0) {
        this._next = TLinkedList.this._head;
      } else if (position == TLinkedList.this._size) {
        this._next = null;
      } else if (position < TLinkedList.this._size >> 1) {
        int pos = 0;
        for (this._next = TLinkedList.this._head; pos < position; pos++)
          this._next = (T)this._next.getNext(); 
      } else {
        int pos = TLinkedList.this._size - 1;
        for (this._next = TLinkedList.this._tail; pos > position; pos--)
          this._next = (T)this._next.getPrevious(); 
      } 
    }
    
    public final void add(T linkable) {
      this._lastReturned = null;
      this._nextIndex++;
      if (TLinkedList.this._size == 0) {
        TLinkedList.this.add(linkable);
      } else {
        TLinkedList.this.addBefore(this._next, linkable);
      } 
    }
    
    public final boolean hasNext() {
      return (this._nextIndex != TLinkedList.this._size);
    }
    
    public final boolean hasPrevious() {
      return (this._nextIndex != 0);
    }
    
    public final T next() {
      if (this._nextIndex == TLinkedList.this._size)
        throw new NoSuchElementException(); 
      this._lastReturned = this._next;
      this._next = (T)this._next.getNext();
      this._nextIndex++;
      return this._lastReturned;
    }
    
    public final int nextIndex() {
      return this._nextIndex;
    }
    
    public final T previous() {
      if (this._nextIndex == 0)
        throw new NoSuchElementException(); 
      if (this._nextIndex == TLinkedList.this._size) {
        this._lastReturned = this._next = TLinkedList.this._tail;
      } else {
        this._lastReturned = this._next = (T)this._next.getPrevious();
      } 
      this._nextIndex--;
      return this._lastReturned;
    }
    
    public final int previousIndex() {
      return this._nextIndex - 1;
    }
    
    public final void remove() {
      if (this._lastReturned == null)
        throw new IllegalStateException("must invoke next or previous before invoking remove"); 
      if (this._lastReturned != this._next)
        this._nextIndex--; 
      this._next = (T)this._lastReturned.getNext();
      TLinkedList.this.remove(this._lastReturned);
      this._lastReturned = null;
    }
    
    public final void set(T linkable) {
      if (this._lastReturned == null)
        throw new IllegalStateException(); 
      swap(this._lastReturned, linkable);
      this._lastReturned = linkable;
    }
    
    private void swap(T from, T to) {
      TLinkable tLinkable1 = from.getPrevious();
      TLinkable tLinkable2 = from.getNext();
      TLinkable tLinkable3 = to.getPrevious();
      TLinkable tLinkable4 = to.getNext();
      if (tLinkable2 == to) {
        if (tLinkable1 != null)
          tLinkable1.setNext((TLinkable)to); 
        to.setPrevious(tLinkable1);
        to.setNext((TLinkable)from);
        from.setPrevious((TLinkable)to);
        from.setNext(tLinkable4);
        if (tLinkable4 != null)
          tLinkable4.setPrevious((TLinkable)from); 
      } else if (tLinkable4 == from) {
        if (tLinkable3 != null)
          tLinkable3.setNext((TLinkable)to); 
        to.setPrevious((TLinkable)from);
        to.setNext(tLinkable2);
        from.setPrevious(tLinkable3);
        from.setNext((TLinkable)to);
        if (tLinkable2 != null)
          tLinkable2.setPrevious((TLinkable)to); 
      } else {
        from.setNext(tLinkable4);
        from.setPrevious(tLinkable3);
        if (tLinkable3 != null)
          tLinkable3.setNext((TLinkable)from); 
        if (tLinkable4 != null)
          tLinkable4.setPrevious((TLinkable)from); 
        to.setNext(tLinkable2);
        to.setPrevious(tLinkable1);
        if (tLinkable1 != null)
          tLinkable1.setNext((TLinkable)to); 
        if (tLinkable2 != null)
          tLinkable2.setPrevious((TLinkable)to); 
      } 
      if (TLinkedList.this._head == from) {
        TLinkedList.this._head = to;
      } else if (TLinkedList.this._head == to) {
        TLinkedList.this._head = from;
      } 
      if (TLinkedList.this._tail == from) {
        TLinkedList.this._tail = to;
      } else if (TLinkedList.this._tail == to) {
        TLinkedList.this._tail = from;
      } 
      if (this._lastReturned == from) {
        this._lastReturned = to;
      } else if (this._lastReturned == to) {
        this._lastReturned = from;
      } 
      if (this._next == from) {
        this._next = to;
      } else if (this._next == to) {
        this._next = from;
      } 
    }
  }
}
