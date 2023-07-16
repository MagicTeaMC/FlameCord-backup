package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TFloatByteHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TFloatByteIterator;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.map.TFloatByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TFloatByteProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TFloatByteHashMap extends TFloatByteHash implements TFloatByteMap, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected transient byte[] _values;
  
  public TFloatByteHashMap() {}
  
  public TFloatByteHashMap(int initialCapacity) {
    super(initialCapacity);
  }
  
  public TFloatByteHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }
  
  public TFloatByteHashMap(int initialCapacity, float loadFactor, float noEntryKey, byte noEntryValue) {
    super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
  }
  
  public TFloatByteHashMap(float[] keys, byte[] values) {
    super(Math.max(keys.length, values.length));
    int size = Math.min(keys.length, values.length);
    for (int i = 0; i < size; i++)
      put(keys[i], values[i]); 
  }
  
  public TFloatByteHashMap(TFloatByteMap map) {
    super(map.size());
    if (map instanceof TFloatByteHashMap) {
      TFloatByteHashMap hashmap = (TFloatByteHashMap)map;
      this._loadFactor = Math.abs(hashmap._loadFactor);
      this.no_entry_key = hashmap.no_entry_key;
      this.no_entry_value = hashmap.no_entry_value;
      if (this.no_entry_key != 0.0F)
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
    float[] oldKeys = this._set;
    byte[] oldVals = this._values;
    byte[] oldStates = this._states;
    this._set = new float[newCapacity];
    this._values = new byte[newCapacity];
    this._states = new byte[newCapacity];
    for (int i = oldCapacity; i-- > 0;) {
      if (oldStates[i] == 1) {
        float o = oldKeys[i];
        int index = insertKey(o);
        this._values[index] = oldVals[i];
      } 
    } 
  }
  
  public byte put(float key, byte value) {
    int index = insertKey(key);
    return doPut(key, value, index);
  }
  
  public byte putIfAbsent(float key, byte value) {
    int index = insertKey(key);
    if (index < 0)
      return this._values[-index - 1]; 
    return doPut(key, value, index);
  }
  
  private byte doPut(float key, byte value, int index) {
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
  
  public void putAll(Map<? extends Float, ? extends Byte> map) {
    ensureCapacity(map.size());
    for (Map.Entry<? extends Float, ? extends Byte> entry : map.entrySet())
      put(((Float)entry.getKey()).floatValue(), ((Byte)entry.getValue()).byteValue()); 
  }
  
  public void putAll(TFloatByteMap map) {
    ensureCapacity(map.size());
    TFloatByteIterator iter = map.iterator();
    while (iter.hasNext()) {
      iter.advance();
      put(iter.key(), iter.value());
    } 
  }
  
  public byte get(float key) {
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
  
  public byte remove(float key) {
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
  
  public TFloatSet keySet() {
    return new TKeyView();
  }
  
  public float[] keys() {
    float[] keys = new float[size()];
    if (keys.length == 0)
      return keys; 
    float[] k = this._set;
    byte[] states = this._states;
    for (int i = k.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        keys[j++] = k[i]; 
    } 
    return keys;
  }
  
  public float[] keys(float[] array) {
    int size = size();
    if (size == 0)
      return array; 
    if (array.length < size)
      array = new float[size]; 
    float[] keys = this._set;
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
  
  public boolean containsKey(float key) {
    return contains(key);
  }
  
  public TFloatByteIterator iterator() {
    return new TFloatByteHashIterator(this);
  }
  
  public boolean forEachKey(TFloatProcedure procedure) {
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
  
  public boolean forEachEntry(TFloatByteProcedure procedure) {
    byte[] states = this._states;
    float[] keys = this._set;
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
  
  public boolean retainEntries(TFloatByteProcedure procedure) {
    boolean modified = false;
    byte[] states = this._states;
    float[] keys = this._set;
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
  
  public boolean increment(float key) {
    return adjustValue(key, (byte)1);
  }
  
  public boolean adjustValue(float key, byte amount) {
    int index = index(key);
    if (index < 0)
      return false; 
    this._values[index] = (byte)(this._values[index] + amount);
    return true;
  }
  
  public byte adjustOrPutValue(float key, byte adjust_amount, byte put_amount) {
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
  
  protected class TKeyView implements TFloatSet {
    public TFloatIterator iterator() {
      return new TFloatByteHashMap.TFloatByteKeyHashIterator((TPrimitiveHash)TFloatByteHashMap.this);
    }
    
    public float getNoEntryValue() {
      return TFloatByteHashMap.this.no_entry_key;
    }
    
    public int size() {
      return TFloatByteHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TFloatByteHashMap.this._size);
    }
    
    public boolean contains(float entry) {
      return TFloatByteHashMap.this.contains(entry);
    }
    
    public float[] toArray() {
      return TFloatByteHashMap.this.keys();
    }
    
    public float[] toArray(float[] dest) {
      return TFloatByteHashMap.this.keys(dest);
    }
    
    public boolean add(float entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(float entry) {
      return (TFloatByteHashMap.this.no_entry_value != TFloatByteHashMap.this.remove(entry));
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Float) {
          float ele = ((Float)element).floatValue();
          if (!TFloatByteHashMap.this.containsKey(ele))
            return false; 
          continue;
        } 
        return false;
      } 
      return true;
    }
    
    public boolean containsAll(TFloatCollection collection) {
      TFloatIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!TFloatByteHashMap.this.containsKey(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(float[] array) {
      for (float element : array) {
        if (!TFloatByteHashMap.this.contains(element))
          return false; 
      } 
      return true;
    }
    
    public boolean addAll(Collection<? extends Float> collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(TFloatCollection collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(float[] array) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> collection) {
      boolean modified = false;
      TFloatIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(Float.valueOf(iter.next()))) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(TFloatCollection collection) {
      if (this == collection)
        return false; 
      boolean modified = false;
      TFloatIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(iter.next())) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(float[] array) {
      boolean changed = false;
      Arrays.sort(array);
      float[] set = TFloatByteHashMap.this._set;
      byte[] states = TFloatByteHashMap.this._states;
      for (int i = set.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
          TFloatByteHashMap.this.removeAt(i);
          changed = true;
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(Collection<?> collection) {
      boolean changed = false;
      for (Object element : collection) {
        if (element instanceof Float) {
          float c = ((Float)element).floatValue();
          if (remove(c))
            changed = true; 
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(TFloatCollection collection) {
      if (this == collection) {
        clear();
        return true;
      } 
      boolean changed = false;
      TFloatIterator iter = collection.iterator();
      while (iter.hasNext()) {
        float element = iter.next();
        if (remove(element))
          changed = true; 
      } 
      return changed;
    }
    
    public boolean removeAll(float[] array) {
      boolean changed = false;
      for (int i = array.length; i-- > 0;) {
        if (remove(array[i]))
          changed = true; 
      } 
      return changed;
    }
    
    public void clear() {
      TFloatByteHashMap.this.clear();
    }
    
    public boolean forEach(TFloatProcedure procedure) {
      return TFloatByteHashMap.this.forEachKey(procedure);
    }
    
    public boolean equals(Object other) {
      if (!(other instanceof TFloatSet))
        return false; 
      TFloatSet that = (TFloatSet)other;
      if (that.size() != size())
        return false; 
      for (int i = TFloatByteHashMap.this._states.length; i-- > 0;) {
        if (TFloatByteHashMap.this._states[i] == 1 && 
          !that.contains(TFloatByteHashMap.this._set[i]))
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      int hashcode = 0;
      for (int i = TFloatByteHashMap.this._states.length; i-- > 0;) {
        if (TFloatByteHashMap.this._states[i] == 1)
          hashcode += HashFunctions.hash(TFloatByteHashMap.this._set[i]); 
      } 
      return hashcode;
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TFloatByteHashMap.this.forEachKey(new TFloatProcedure() {
            private boolean first = true;
            
            public boolean execute(float key) {
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
      return new TFloatByteHashMap.TFloatByteValueHashIterator((TPrimitiveHash)TFloatByteHashMap.this);
    }
    
    public byte getNoEntryValue() {
      return TFloatByteHashMap.this.no_entry_value;
    }
    
    public int size() {
      return TFloatByteHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TFloatByteHashMap.this._size);
    }
    
    public boolean contains(byte entry) {
      return TFloatByteHashMap.this.containsValue(entry);
    }
    
    public byte[] toArray() {
      return TFloatByteHashMap.this.values();
    }
    
    public byte[] toArray(byte[] dest) {
      return TFloatByteHashMap.this.values(dest);
    }
    
    public boolean add(byte entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(byte entry) {
      byte[] values = TFloatByteHashMap.this._values;
      byte[] states = TFloatByteHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
          TFloatByteHashMap.this.removeAt(i);
          return true;
        } 
      } 
      return false;
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Byte) {
          byte ele = ((Byte)element).byteValue();
          if (!TFloatByteHashMap.this.containsValue(ele))
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
        if (!TFloatByteHashMap.this.containsValue(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(byte[] array) {
      for (byte element : array) {
        if (!TFloatByteHashMap.this.containsValue(element))
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
      byte[] values = TFloatByteHashMap.this._values;
      byte[] states = TFloatByteHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
          TFloatByteHashMap.this.removeAt(i);
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
      TFloatByteHashMap.this.clear();
    }
    
    public boolean forEach(TByteProcedure procedure) {
      return TFloatByteHashMap.this.forEachValue(procedure);
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TFloatByteHashMap.this.forEachValue(new TByteProcedure() {
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
  
  class TFloatByteKeyHashIterator extends THashPrimitiveIterator implements TFloatIterator {
    TFloatByteKeyHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public float next() {
      moveToNextIndex();
      return TFloatByteHashMap.this._set[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TFloatByteHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TFloatByteValueHashIterator extends THashPrimitiveIterator implements TByteIterator {
    TFloatByteValueHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public byte next() {
      moveToNextIndex();
      return TFloatByteHashMap.this._values[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TFloatByteHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TFloatByteHashIterator extends THashPrimitiveIterator implements TFloatByteIterator {
    TFloatByteHashIterator(TFloatByteHashMap map) {
      super((TPrimitiveHash)map);
    }
    
    public void advance() {
      moveToNextIndex();
    }
    
    public float key() {
      return TFloatByteHashMap.this._set[this._index];
    }
    
    public byte value() {
      return TFloatByteHashMap.this._values[this._index];
    }
    
    public byte setValue(byte val) {
      byte old = value();
      TFloatByteHashMap.this._values[this._index] = val;
      return old;
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TFloatByteHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  public boolean equals(Object other) {
    if (!(other instanceof TFloatByteMap))
      return false; 
    TFloatByteMap that = (TFloatByteMap)other;
    if (that.size() != size())
      return false; 
    byte[] values = this._values;
    byte[] states = this._states;
    byte this_no_entry_value = getNoEntryValue();
    byte that_no_entry_value = that.getNoEntryValue();
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1) {
        float key = this._set[i];
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
    forEachEntry(new TFloatByteProcedure() {
          private boolean first = true;
          
          public boolean execute(float key, byte value) {
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
        out.writeFloat(this._set[i]);
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
      float key = in.readFloat();
      byte val = in.readByte();
      put(key, val);
    } 
  }
}
