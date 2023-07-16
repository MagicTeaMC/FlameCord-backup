package gnu.trove.set.hash;

import gnu.trove.TShortCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.impl.hash.TShortHash;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.set.TShortSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;

public class TShortHashSet extends TShortHash implements TShortSet, Externalizable {
  static final long serialVersionUID = 1L;
  
  public TShortHashSet() {}
  
  public TShortHashSet(int initialCapacity) {
    super(initialCapacity);
  }
  
  public TShortHashSet(int initialCapacity, float load_factor) {
    super(initialCapacity, load_factor);
  }
  
  public TShortHashSet(int initial_capacity, float load_factor, short no_entry_value) {
    super(initial_capacity, load_factor, no_entry_value);
    if (no_entry_value != 0)
      Arrays.fill(this._set, no_entry_value); 
  }
  
  public TShortHashSet(Collection<? extends Short> collection) {
    this(Math.max(collection.size(), 10));
    addAll(collection);
  }
  
  public TShortHashSet(TShortCollection collection) {
    this(Math.max(collection.size(), 10));
    if (collection instanceof TShortHashSet) {
      TShortHashSet hashset = (TShortHashSet)collection;
      this._loadFactor = hashset._loadFactor;
      this.no_entry_value = hashset.no_entry_value;
      if (this.no_entry_value != 0)
        Arrays.fill(this._set, this.no_entry_value); 
      setUp(saturatedCast(fastCeil(10.0D / this._loadFactor)));
    } 
    addAll(collection);
  }
  
  public TShortHashSet(short[] array) {
    this(Math.max(array.length, 10));
    addAll(array);
  }
  
  public TShortIterator iterator() {
    return new TShortHashIterator(this);
  }
  
  public short[] toArray() {
    return toArray(new short[this._size]);
  }
  
  public short[] toArray(short[] dest) {
    if (dest.length < this._size)
      dest = new short[this._size]; 
    short[] set = this._set;
    byte[] states = this._states;
    for (int i = states.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        dest[j++] = set[i]; 
    } 
    if (dest.length > this._size)
      dest[this._size] = this.no_entry_value; 
    return dest;
  }
  
  public boolean add(short val) {
    int index = insertKey(val);
    if (index < 0)
      return false; 
    postInsertHook(this.consumeFreeSlot);
    return true;
  }
  
  public boolean remove(short val) {
    int index = index(val);
    if (index >= 0) {
      removeAt(index);
      return true;
    } 
    return false;
  }
  
  public boolean containsAll(Collection<?> collection) {
    for (Object element : collection) {
      if (element instanceof Short) {
        short c = ((Short)element).shortValue();
        if (!contains(c))
          return false; 
        continue;
      } 
      return false;
    } 
    return true;
  }
  
  public boolean containsAll(TShortCollection collection) {
    TShortIterator iter = collection.iterator();
    while (iter.hasNext()) {
      short element = iter.next();
      if (!contains(element))
        return false; 
    } 
    return true;
  }
  
  public boolean containsAll(short[] array) {
    for (int i = array.length; i-- > 0;) {
      if (!contains(array[i]))
        return false; 
    } 
    return true;
  }
  
  public boolean addAll(Collection<? extends Short> collection) {
    boolean changed = false;
    for (Short element : collection) {
      short e = element.shortValue();
      if (add(e))
        changed = true; 
    } 
    return changed;
  }
  
  public boolean addAll(TShortCollection collection) {
    boolean changed = false;
    TShortIterator iter = collection.iterator();
    while (iter.hasNext()) {
      short element = iter.next();
      if (add(element))
        changed = true; 
    } 
    return changed;
  }
  
  public boolean addAll(short[] array) {
    boolean changed = false;
    for (int i = array.length; i-- > 0;) {
      if (add(array[i]))
        changed = true; 
    } 
    return changed;
  }
  
  public boolean retainAll(Collection<?> collection) {
    boolean modified = false;
    TShortIterator iter = iterator();
    while (iter.hasNext()) {
      if (!collection.contains(Short.valueOf(iter.next()))) {
        iter.remove();
        modified = true;
      } 
    } 
    return modified;
  }
  
  public boolean retainAll(TShortCollection collection) {
    if (this == collection)
      return false; 
    boolean modified = false;
    TShortIterator iter = iterator();
    while (iter.hasNext()) {
      if (!collection.contains(iter.next())) {
        iter.remove();
        modified = true;
      } 
    } 
    return modified;
  }
  
  public boolean retainAll(short[] array) {
    boolean changed = false;
    Arrays.sort(array);
    short[] set = this._set;
    byte[] states = this._states;
    this._autoCompactTemporaryDisable = true;
    for (int i = set.length; i-- > 0;) {
      if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
        removeAt(i);
        changed = true;
      } 
    } 
    this._autoCompactTemporaryDisable = false;
    return changed;
  }
  
  public boolean removeAll(Collection<?> collection) {
    boolean changed = false;
    for (Object element : collection) {
      if (element instanceof Short) {
        short c = ((Short)element).shortValue();
        if (remove(c))
          changed = true; 
      } 
    } 
    return changed;
  }
  
  public boolean removeAll(TShortCollection collection) {
    boolean changed = false;
    TShortIterator iter = collection.iterator();
    while (iter.hasNext()) {
      short element = iter.next();
      if (remove(element))
        changed = true; 
    } 
    return changed;
  }
  
  public boolean removeAll(short[] array) {
    boolean changed = false;
    for (int i = array.length; i-- > 0;) {
      if (remove(array[i]))
        changed = true; 
    } 
    return changed;
  }
  
  public void clear() {
    super.clear();
    short[] set = this._set;
    byte[] states = this._states;
    for (int i = set.length; i-- > 0; ) {
      set[i] = this.no_entry_value;
      states[i] = 0;
    } 
  }
  
  protected void rehash(int newCapacity) {
    int oldCapacity = this._set.length;
    short[] oldSet = this._set;
    byte[] oldStates = this._states;
    this._set = new short[newCapacity];
    this._states = new byte[newCapacity];
    for (int i = oldCapacity; i-- > 0;) {
      if (oldStates[i] == 1) {
        short o = oldSet[i];
        int j = insertKey(o);
      } 
    } 
  }
  
  public boolean equals(Object other) {
    if (!(other instanceof TShortSet))
      return false; 
    TShortSet that = (TShortSet)other;
    if (that.size() != size())
      return false; 
    for (int i = this._states.length; i-- > 0;) {
      if (this._states[i] == 1 && 
        !that.contains(this._set[i]))
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    int hashcode = 0;
    for (int i = this._states.length; i-- > 0;) {
      if (this._states[i] == 1)
        hashcode += HashFunctions.hash(this._set[i]); 
    } 
    return hashcode;
  }
  
  public String toString() {
    StringBuilder buffy = new StringBuilder(this._size * 2 + 2);
    buffy.append("{");
    for (int i = this._states.length, j = 1; i-- > 0;) {
      if (this._states[i] == 1) {
        buffy.append(this._set[i]);
        if (j++ < this._size)
          buffy.append(","); 
      } 
    } 
    buffy.append("}");
    return buffy.toString();
  }
  
  class TShortHashIterator extends THashPrimitiveIterator implements TShortIterator {
    private final TShortHash _hash;
    
    public TShortHashIterator(TShortHash hash) {
      super((TPrimitiveHash)hash);
      this._hash = hash;
    }
    
    public short next() {
      moveToNextIndex();
      return this._hash._set[this._index];
    }
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(1);
    super.writeExternal(out);
    out.writeInt(this._size);
    out.writeFloat(this._loadFactor);
    out.writeShort(this.no_entry_value);
    for (int i = this._states.length; i-- > 0;) {
      if (this._states[i] == 1)
        out.writeShort(this._set[i]); 
    } 
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    int version = in.readByte();
    super.readExternal(in);
    int size = in.readInt();
    if (version >= 1) {
      this._loadFactor = in.readFloat();
      this.no_entry_value = in.readShort();
      if (this.no_entry_value != 0)
        Arrays.fill(this._set, this.no_entry_value); 
    } 
    setUp(size);
    while (size-- > 0) {
      short val = in.readShort();
      add(val);
    } 
  }
}