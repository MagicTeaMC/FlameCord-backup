package gnu.trove.set.hash;

import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.array.ToObjectArrayProceedure;
import gnu.trove.strategy.HashingStrategy;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class TCustomHashSet<E> extends TCustomObjectHash<E> implements Set<E>, Iterable<E>, Externalizable {
  static final long serialVersionUID = 1L;
  
  public TCustomHashSet() {}
  
  public TCustomHashSet(HashingStrategy<? super E> strategy) {
    super(strategy);
  }
  
  public TCustomHashSet(HashingStrategy<? super E> strategy, int initialCapacity) {
    super(strategy, initialCapacity);
  }
  
  public TCustomHashSet(HashingStrategy<? super E> strategy, int initialCapacity, float loadFactor) {
    super(strategy, initialCapacity, loadFactor);
  }
  
  public TCustomHashSet(HashingStrategy<? super E> strategy, Collection<? extends E> collection) {
    this(strategy, collection.size());
    addAll(collection);
  }
  
  public boolean add(E obj) {
    int index = insertKey(obj);
    if (index < 0)
      return false; 
    postInsertHook(this.consumeFreeSlot);
    return true;
  }
  
  public boolean equals(Object other) {
    if (!(other instanceof Set))
      return false; 
    Set<?> that = (Set)other;
    if (that.size() != size())
      return false; 
    return containsAll(that);
  }
  
  public int hashCode() {
    HashProcedure p = new HashProcedure();
    forEach(p);
    return p.getHashCode();
  }
  
  private final class HashProcedure implements TObjectProcedure<E> {
    private int h = 0;
    
    public int getHashCode() {
      return this.h;
    }
    
    public final boolean execute(E key) {
      this.h += HashFunctions.hash(key);
      return true;
    }
    
    private HashProcedure() {}
  }
  
  protected void rehash(int newCapacity) {
    int oldCapacity = this._set.length;
    int oldSize = size();
    Object[] oldSet = this._set;
    this._set = new Object[newCapacity];
    Arrays.fill(this._set, FREE);
    for (int i = oldCapacity; i-- > 0; ) {
      E o = (E)oldSet[i];
      if (o != FREE && o != REMOVED) {
        int index = insertKey(o);
        if (index < 0)
          throwObjectContractViolation(this._set[-index - 1], o, size(), oldSize, oldSet); 
      } 
    } 
  }
  
  public Object[] toArray() {
    Object[] result = new Object[size()];
    forEach((TObjectProcedure)new ToObjectArrayProceedure(result));
    return result;
  }
  
  public <T> T[] toArray(T[] a) {
    int size = size();
    if (a.length < size)
      a = (T[])Array.newInstance(a.getClass().getComponentType(), size); 
    forEach((TObjectProcedure)new ToObjectArrayProceedure((Object[])a));
    if (a.length > size)
      a[size] = null; 
    return a;
  }
  
  public void clear() {
    super.clear();
    Arrays.fill(this._set, 0, this._set.length, FREE);
  }
  
  public boolean remove(Object obj) {
    int index = index(obj);
    if (index >= 0) {
      removeAt(index);
      return true;
    } 
    return false;
  }
  
  public TObjectHashIterator<E> iterator() {
    return new TObjectHashIterator((TObjectHash)this);
  }
  
  public boolean containsAll(Collection<?> collection) {
    for (Iterator<?> i = collection.iterator(); i.hasNext();) {
      if (!contains(i.next()))
        return false; 
    } 
    return true;
  }
  
  public boolean addAll(Collection<? extends E> collection) {
    boolean changed = false;
    int size = collection.size();
    ensureCapacity(size);
    Iterator<? extends E> it = collection.iterator();
    while (size-- > 0) {
      if (add(it.next()))
        changed = true; 
    } 
    return changed;
  }
  
  public boolean removeAll(Collection<?> collection) {
    boolean changed = false;
    int size = collection.size();
    Iterator<?> it = collection.iterator();
    while (size-- > 0) {
      if (remove(it.next()))
        changed = true; 
    } 
    return changed;
  }
  
  public boolean retainAll(Collection<?> collection) {
    boolean changed = false;
    int size = size();
    TObjectHashIterator<E> tObjectHashIterator = iterator();
    while (size-- > 0) {
      if (!collection.contains(tObjectHashIterator.next())) {
        tObjectHashIterator.remove();
        changed = true;
      } 
    } 
    return changed;
  }
  
  public String toString() {
    final StringBuilder buf = new StringBuilder("{");
    forEach(new TObjectProcedure<E>() {
          private boolean first = true;
          
          public boolean execute(Object value) {
            if (this.first) {
              this.first = false;
            } else {
              buf.append(", ");
            } 
            buf.append(value);
            return true;
          }
        });
    buf.append("}");
    return buf.toString();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(1);
    super.writeExternal(out);
    out.writeInt(this._size);
    for (int i = this._set.length; i-- > 0;) {
      if (this._set[i] != REMOVED && this._set[i] != FREE)
        out.writeObject(this._set[i]); 
    } 
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    byte version = in.readByte();
    if (version != 0)
      super.readExternal(in); 
    int size = in.readInt();
    setUp(size);
    while (size-- > 0) {
      E val = (E)in.readObject();
      add(val);
    } 
  }
}
