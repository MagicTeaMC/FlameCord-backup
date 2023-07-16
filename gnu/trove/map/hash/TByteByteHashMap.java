package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteByteHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteByteIterator;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.map.TByteByteMap;
import gnu.trove.procedure.TByteByteProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.set.TByteSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TByteByteHashMap extends TByteByteHash implements TByteByteMap, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected transient byte[] _values;
  
  public TByteByteHashMap() {}
  
  public TByteByteHashMap(int initialCapacity) {
    super(initialCapacity);
  }
  
  public TByteByteHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }
  
  public TByteByteHashMap(int initialCapacity, float loadFactor, byte noEntryKey, byte noEntryValue) {
    super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
  }
  
  public TByteByteHashMap(byte[] keys, byte[] values) {
    super(Math.max(keys.length, values.length));
    int size = Math.min(keys.length, values.length);
    for (int i = 0; i < size; i++)
      put(keys[i], values[i]); 
  }
  
  public TByteByteHashMap(TByteByteMap map) {
    super(map.size());
    if (map instanceof TByteByteHashMap) {
      TByteByteHashMap hashmap = (TByteByteHashMap)map;
      this._loadFactor = Math.abs(hashmap._loadFactor);
      this.no_entry_key = hashmap.no_entry_key;
      this.no_entry_value = hashmap.no_entry_value;
      if (this.no_entry_key != 0)
        Arrays.fill(this._set, this.no_entry_key); 
      if (this.no_entry_value != 0)
        Arrays.fill(this._values, this.no_entry_value); 
      setUp(saturatedCast(fastCeil(10.0D / this._loadFactor)));
    } 
    putAll(map);
  }
  
  protected int setUp(int initialCapacity) {
    int capacity = super.setUp(initialCapacity);
    this._values = new byte[capacity];
    return capacity;
  }
  
  protected void rehash(int newCapacity) {
    int oldCapacity = this._set.length;
    byte[] oldKeys = this._set;
    byte[] oldVals = this._values;
    byte[] oldStates = this._states;
    this._set = new byte[newCapacity];
    this._values = new byte[newCapacity];
    this._states = new byte[newCapacity];
    for (int i = oldCapacity; i-- > 0;) {
      if (oldStates[i] == 1) {
        byte o = oldKeys[i];
        int index = insertKey(o);
        this._values[index] = oldVals[i];
      } 
    } 
  }
  
  public byte put(byte key, byte value) {
    int index = insertKey(key);
    return doPut(key, value, index);
  }
  
  public byte putIfAbsent(byte key, byte value) {
    int index = insertKey(key);
    if (index < 0)
      return this._values[-index - 1]; 
    return doPut(key, value, index);
  }
  
  private byte doPut(byte key, byte value, int index) {
    byte previous = this.no_entry_value;
    boolean isNewMapping = true;
    if (index < 0) {
      index = -index - 1;
      previous = this._values[index];
      isNewMapping = false;
    } 
    this._values[index] = value;
    if (isNewMapping)
      postInsertHook(this.consumeFreeSlot); 
    return previous;
  }
  
  public void putAll(Map<? extends Byte, ? extends Byte> map) {
    ensureCapacity(map.size());
    for (Map.Entry<? extends Byte, ? extends Byte> entry : map.entrySet())
      put(((Byte)entry.getKey()).byteValue(), ((Byte)entry.getValue()).byteValue()); 
  }
  
  public void putAll(TByteByteMap map) {
    ensureCapacity(map.size());
    TByteByteIterator iter = map.iterator();
    while (iter.hasNext()) {
      iter.advance();
      put(iter.key(), iter.value());
    } 
  }
  
  public byte get(byte key) {
    int index = index(key);
    return (index < 0) ? this.no_entry_value : this._values[index];
  }
  
  public void clear() {
    super.clear();
    Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
    Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
    Arrays.fill(this._states, 0, this._states.length, (byte)0);
  }
  
  public boolean isEmpty() {
    return (0 == this._size);
  }
  
  public byte remove(byte key) {
    byte prev = this.no_entry_value;
    int index = index(key);
    if (index >= 0) {
      prev = this._values[index];
      removeAt(index);
    } 
    return prev;
  }
  
  protected void removeAt(int index) {
    this._values[index] = this.no_entry_value;
    super.removeAt(index);
  }
  
  public TByteSet keySet() {
    return new TKeyView();
  }
  
  public byte[] keys() {
    byte[] keys = new byte[size()];
    if (keys.length == 0)
      return keys; 
    byte[] k = this._set;
    byte[] states = this._states;
    for (int i = k.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        keys[j++] = k[i]; 
    } 
    return keys;
  }
  
  public byte[] keys(byte[] array) {
    int size = size();
    if (size == 0)
      return array; 
    if (array.length < size)
      array = new byte[size]; 
    byte[] keys = this._set;
    byte[] states = this._states;
    for (int i = keys.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        array[j++] = keys[i]; 
    } 
    return array;
  }
  
  public TByteCollection valueCollection() {
    return new TValueView();
  }
  
  public byte[] values() {
    byte[] vals = new byte[size()];
    if (vals.length == 0)
      return vals; 
    byte[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        vals[j++] = v[i]; 
    } 
    return vals;
  }
  
  public byte[] values(byte[] array) {
    int size = size();
    if (size == 0)
      return array; 
    if (array.length < size)
      array = new byte[size]; 
    byte[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        array[j++] = v[i]; 
    } 
    return array;
  }
  
  public boolean containsValue(byte val) {
    byte[] states = this._states;
    byte[] vals = this._values;
    for (int i = vals.length; i-- > 0;) {
      if (states[i] == 1 && val == vals[i])
        return true; 
    } 
    return false;
  }
  
  public boolean containsKey(byte key) {
    return contains(key);
  }
  
  public TByteByteIterator iterator() {
    return new TByteByteHashIterator(this);
  }
  
  public boolean forEachKey(TByteProcedure procedure) {
    return forEach(procedure);
  }
  
  public boolean forEachValue(TByteProcedure procedure) {
    byte[] states = this._states;
    byte[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(values[i]))
        return false; 
    } 
    return true;
  }
  
  public boolean forEachEntry(TByteByteProcedure procedure) {
    byte[] states = this._states;
    byte[] keys = this._set;
    byte[] values = this._values;
    for (int i = keys.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(keys[i], values[i]))
        return false; 
    } 
    return true;
  }
  
  public void transformValues(TByteFunction function) {
    byte[] states = this._states;
    byte[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1)
        values[i] = function.execute(values[i]); 
    } 
  }
  
  public boolean retainEntries(TByteByteProcedure procedure) {
    boolean modified = false;
    byte[] states = this._states;
    byte[] keys = this._set;
    byte[] values = this._values;
    tempDisableAutoCompaction();
    try {
      for (int i = keys.length; i-- > 0;) {
        if (states[i] == 1 && !procedure.execute(keys[i], values[i])) {
          removeAt(i);
          modified = true;
        } 
      } 
    } finally {
      reenableAutoCompaction(true);
    } 
    return modified;
  }
  
  public boolean increment(byte key) {
    return adjustValue(key, (byte)1);
  }
  
  public boolean adjustValue(byte key, byte amount) {
    int index = index(key);
    if (index < 0)
      return false; 
    this._values[index] = (byte)(this._values[index] + amount);
    return true;
  }
  
  public byte adjustOrPutValue(byte key, byte adjust_amount, byte put_amount) {
    boolean isNewMapping;
    byte newValue;
    int index = insertKey(key);
    if (index < 0) {
      index = -index - 1;
      newValue = this._values[index] = (byte)(this._values[index] + adjust_amount);
      isNewMapping = false;
    } else {
      newValue = this._values[index] = put_amount;
      isNewMapping = true;
    } 
    byte previousState = this._states[index];
    if (isNewMapping)
      postInsertHook(this.consumeFreeSlot); 
    return newValue;
  }
  
  protected class TKeyView implements TByteSet {
    public TByteIterator iterator() {
      return new TByteByteHashMap.TByteByteKeyHashIterator((TPrimitiveHash)TByteByteHashMap.this);
    }
    
    public byte getNoEntryValue() {
      return TByteByteHashMap.this.no_entry_key;
    }
    
    public int size() {
      return TByteByteHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TByteByteHashMap.this._size);
    }
    
    public boolean contains(byte entry) {
      return TByteByteHashMap.this.contains(entry);
    }
    
    public byte[] toArray() {
      return TByteByteHashMap.this.keys();
    }
    
    public byte[] toArray(byte[] dest) {
      return TByteByteHashMap.this.keys(dest);
    }
    
    public boolean add(byte entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(byte entry) {
      return (TByteByteHashMap.this.no_entry_value != TByteByteHashMap.this.remove(entry));
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Byte) {
          byte ele = ((Byte)element).byteValue();
          if (!TByteByteHashMap.this.containsKey(ele))
            return false; 
          continue;
        } 
        return false;
      } 
      return true;
    }
    
    public boolean containsAll(TByteCollection collection) {
      TByteIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!TByteByteHashMap.this.containsKey(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(byte[] array) {
      for (byte element : array) {
        if (!TByteByteHashMap.this.contains(element))
          return false; 
      } 
      return true;
    }
    
    public boolean addAll(Collection<? extends Byte> collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(TByteCollection collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(byte[] array) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> collection) {
      boolean modified = false;
      TByteIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(Byte.valueOf(iter.next()))) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(TByteCollection collection) {
      if (this == collection)
        return false; 
      boolean modified = false;
      TByteIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(iter.next())) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(byte[] array) {
      boolean changed = false;
      Arrays.sort(array);
      byte[] set = TByteByteHashMap.this._set;
      byte[] states = TByteByteHashMap.this._states;
      for (int i = set.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
          TByteByteHashMap.this.removeAt(i);
          changed = true;
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(Collection<?> collection) {
      boolean changed = false;
      for (Object element : collection) {
        if (element instanceof Byte) {
          byte c = ((Byte)element).byteValue();
          if (remove(c))
            changed = true; 
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(TByteCollection collection) {
      if (this == collection) {
        clear();
        return true;
      } 
      boolean changed = false;
      TByteIterator iter = collection.iterator();
      while (iter.hasNext()) {
        byte element = iter.next();
        if (remove(element))
          changed = true; 
      } 
      return changed;
    }
    
    public boolean removeAll(byte[] array) {
      boolean changed = false;
      for (int i = array.length; i-- > 0;) {
        if (remove(array[i]))
          changed = true; 
      } 
      return changed;
    }
    
    public void clear() {
      TByteByteHashMap.this.clear();
    }
    
    public boolean forEach(TByteProcedure procedure) {
      return TByteByteHashMap.this.forEachKey(procedure);
    }
    
    public boolean equals(Object other) {
      if (!(other instanceof TByteSet))
        return false; 
      TByteSet that = (TByteSet)other;
      if (that.size() != size())
        return false; 
      for (int i = TByteByteHashMap.this._states.length; i-- > 0;) {
        if (TByteByteHashMap.this._states[i] == 1 && 
          !that.contains(TByteByteHashMap.this._set[i]))
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      int hashcode = 0;
      for (int i = TByteByteHashMap.this._states.length; i-- > 0;) {
        if (TByteByteHashMap.this._states[i] == 1)
          hashcode += HashFunctions.hash(TByteByteHashMap.this._set[i]); 
      } 
      return hashcode;
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TByteByteHashMap.this.forEachKey(new TByteProcedure() {
            private boolean first = true;
            
            public boolean execute(byte key) {
              if (this.first) {
                this.first = false;
              } else {
                buf.append(", ");
              } 
              buf.append(key);
              return true;
            }
          });
      buf.append("}");
      return buf.toString();
    }
  }
  
  protected class TValueView implements TByteCollection {
    public TByteIterator iterator() {
      return new TByteByteHashMap.TByteByteValueHashIterator((TPrimitiveHash)TByteByteHashMap.this);
    }
    
    public byte getNoEntryValue() {
      return TByteByteHashMap.this.no_entry_value;
    }
    
    public int size() {
      return TByteByteHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TByteByteHashMap.this._size);
    }
    
    public boolean contains(byte entry) {
      return TByteByteHashMap.this.containsValue(entry);
    }
    
    public byte[] toArray() {
      return TByteByteHashMap.this.values();
    }
    
    public byte[] toArray(byte[] dest) {
      return TByteByteHashMap.this.values(dest);
    }
    
    public boolean add(byte entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(byte entry) {
      byte[] values = TByteByteHashMap.this._values;
      byte[] states = TByteByteHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
          TByteByteHashMap.this.removeAt(i);
          return true;
        } 
      } 
      return false;
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Byte) {
          byte ele = ((Byte)element).byteValue();
          if (!TByteByteHashMap.this.containsValue(ele))
            return false; 
          continue;
        } 
        return false;
      } 
      return true;
    }
    
    public boolean containsAll(TByteCollection collection) {
      TByteIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!TByteByteHashMap.this.containsValue(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(byte[] array) {
      for (byte element : array) {
        if (!TByteByteHashMap.this.containsValue(element))
          return false; 
      } 
      return true;
    }
    
    public boolean addAll(Collection<? extends Byte> collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(TByteCollection collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(byte[] array) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> collection) {
      boolean modified = false;
      TByteIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(Byte.valueOf(iter.next()))) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(TByteCollection collection) {
      if (this == collection)
        return false; 
      boolean modified = false;
      TByteIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(iter.next())) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(byte[] array) {
      boolean changed = false;
      Arrays.sort(array);
      byte[] values = TByteByteHashMap.this._values;
      byte[] states = TByteByteHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
          TByteByteHashMap.this.removeAt(i);
          changed = true;
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(Collection<?> collection) {
      boolean changed = false;
      for (Object element : collection) {
        if (element instanceof Byte) {
          byte c = ((Byte)element).byteValue();
          if (remove(c))
            changed = true; 
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(TByteCollection collection) {
      if (this == collection) {
        clear();
        return true;
      } 
      boolean changed = false;
      TByteIterator iter = collection.iterator();
      while (iter.hasNext()) {
        byte element = iter.next();
        if (remove(element))
          changed = true; 
      } 
      return changed;
    }
    
    public boolean removeAll(byte[] array) {
      boolean changed = false;
      for (int i = array.length; i-- > 0;) {
        if (remove(array[i]))
          changed = true; 
      } 
      return changed;
    }
    
    public void clear() {
      TByteByteHashMap.this.clear();
    }
    
    public boolean forEach(TByteProcedure procedure) {
      return TByteByteHashMap.this.forEachValue(procedure);
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TByteByteHashMap.this.forEachValue(new TByteProcedure() {
            private boolean first = true;
            
            public boolean execute(byte value) {
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
  }
  
  class TByteByteKeyHashIterator extends THashPrimitiveIterator implements TByteIterator {
    TByteByteKeyHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public byte next() {
      moveToNextIndex();
      return TByteByteHashMap.this._set[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TByteByteHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TByteByteValueHashIterator extends THashPrimitiveIterator implements TByteIterator {
    TByteByteValueHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public byte next() {
      moveToNextIndex();
      return TByteByteHashMap.this._values[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TByteByteHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TByteByteHashIterator extends THashPrimitiveIterator implements TByteByteIterator {
    TByteByteHashIterator(TByteByteHashMap map) {
      super((TPrimitiveHash)map);
    }
    
    public void advance() {
      moveToNextIndex();
    }
    
    public byte key() {
      return TByteByteHashMap.this._set[this._index];
    }
    
    public byte value() {
      return TByteByteHashMap.this._values[this._index];
    }
    
    public byte setValue(byte val) {
      byte old = value();
      TByteByteHashMap.this._values[this._index] = val;
      return old;
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TByteByteHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  public boolean equals(Object other) {
    if (!(other instanceof TByteByteMap))
      return false; 
    TByteByteMap that = (TByteByteMap)other;
    if (that.size() != size())
      return false; 
    byte[] values = this._values;
    byte[] states = this._states;
    byte this_no_entry_value = getNoEntryValue();
    byte that_no_entry_value = that.getNoEntryValue();
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1) {
        byte key = this._set[i];
        if (!that.containsKey(key))
          return false; 
        byte that_value = that.get(key);
        byte this_value = values[i];
        if (this_value != that_value && (this_value != this_no_entry_value || that_value != that_no_entry_value))
          return false; 
      } 
    } 
    return true;
  }
  
  public int hashCode() {
    int hashcode = 0;
    byte[] states = this._states;
    for (int i = this._values.length; i-- > 0;) {
      if (states[i] == 1)
        hashcode += HashFunctions.hash(this._set[i]) ^ 
          HashFunctions.hash(this._values[i]); 
    } 
    return hashcode;
  }
  
  public String toString() {
    final StringBuilder buf = new StringBuilder("{");
    forEachEntry(new TByteByteProcedure() {
          private boolean first = true;
          
          public boolean execute(byte key, byte value) {
            if (this.first) {
              this.first = false;
            } else {
              buf.append(", ");
            } 
            buf.append(key);
            buf.append("=");
            buf.append(value);
            return true;
          }
        });
    buf.append("}");
    return buf.toString();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    super.writeExternal(out);
    out.writeInt(this._size);
    for (int i = this._states.length; i-- > 0;) {
      if (this._states[i] == 1) {
        out.writeByte(this._set[i]);
        out.writeByte(this._values[i]);
      } 
    } 
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    super.readExternal(in);
    int size = in.readInt();
    setUp(size);
    while (size-- > 0) {
      byte key = in.readByte();
      byte val = in.readByte();
      put(key, val);
    } 
  }
}
