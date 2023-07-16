package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharFloatHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharFloatIterator;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.map.TCharFloatMap;
import gnu.trove.procedure.TCharFloatProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TCharSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TCharFloatHashMap extends TCharFloatHash implements TCharFloatMap, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected transient float[] _values;
  
  public TCharFloatHashMap() {}
  
  public TCharFloatHashMap(int initialCapacity) {
    super(initialCapacity);
  }
  
  public TCharFloatHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }
  
  public TCharFloatHashMap(int initialCapacity, float loadFactor, char noEntryKey, float noEntryValue) {
    super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
  }
  
  public TCharFloatHashMap(char[] keys, float[] values) {
    super(Math.max(keys.length, values.length));
    int size = Math.min(keys.length, values.length);
    for (int i = 0; i < size; i++)
      put(keys[i], values[i]); 
  }
  
  public TCharFloatHashMap(TCharFloatMap map) {
    super(map.size());
    if (map instanceof TCharFloatHashMap) {
      TCharFloatHashMap hashmap = (TCharFloatHashMap)map;
      this._loadFactor = Math.abs(hashmap._loadFactor);
      this.no_entry_key = hashmap.no_entry_key;
      this.no_entry_value = hashmap.no_entry_value;
      if (this.no_entry_key != '\000')
        Arrays.fill(this._set, this.no_entry_key); 
      if (this.no_entry_value != 0.0F)
        Arrays.fill(this._values, this.no_entry_value); 
      setUp(saturatedCast(fastCeil(10.0D / this._loadFactor)));
    } 
    putAll(map);
  }
  
  protected int setUp(int initialCapacity) {
    int capacity = super.setUp(initialCapacity);
    this._values = new float[capacity];
    return capacity;
  }
  
  protected void rehash(int newCapacity) {
    int oldCapacity = this._set.length;
    char[] oldKeys = this._set;
    float[] oldVals = this._values;
    byte[] oldStates = this._states;
    this._set = new char[newCapacity];
    this._values = new float[newCapacity];
    this._states = new byte[newCapacity];
    for (int i = oldCapacity; i-- > 0;) {
      if (oldStates[i] == 1) {
        char o = oldKeys[i];
        int index = insertKey(o);
        this._values[index] = oldVals[i];
      } 
    } 
  }
  
  public float put(char key, float value) {
    int index = insertKey(key);
    return doPut(key, value, index);
  }
  
  public float putIfAbsent(char key, float value) {
    int index = insertKey(key);
    if (index < 0)
      return this._values[-index - 1]; 
    return doPut(key, value, index);
  }
  
  private float doPut(char key, float value, int index) {
    float previous = this.no_entry_value;
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
  
  public void putAll(Map<? extends Character, ? extends Float> map) {
    ensureCapacity(map.size());
    for (Map.Entry<? extends Character, ? extends Float> entry : map.entrySet())
      put(((Character)entry.getKey()).charValue(), ((Float)entry.getValue()).floatValue()); 
  }
  
  public void putAll(TCharFloatMap map) {
    ensureCapacity(map.size());
    TCharFloatIterator iter = map.iterator();
    while (iter.hasNext()) {
      iter.advance();
      put(iter.key(), iter.value());
    } 
  }
  
  public float get(char key) {
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
  
  public float remove(char key) {
    float prev = this.no_entry_value;
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
  
  public TCharSet keySet() {
    return new TKeyView();
  }
  
  public char[] keys() {
    char[] keys = new char[size()];
    if (keys.length == 0)
      return keys; 
    char[] k = this._set;
    byte[] states = this._states;
    for (int i = k.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        keys[j++] = k[i]; 
    } 
    return keys;
  }
  
  public char[] keys(char[] array) {
    int size = size();
    if (size == 0)
      return array; 
    if (array.length < size)
      array = new char[size]; 
    char[] keys = this._set;
    byte[] states = this._states;
    for (int i = keys.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        array[j++] = keys[i]; 
    } 
    return array;
  }
  
  public TFloatCollection valueCollection() {
    return new TValueView();
  }
  
  public float[] values() {
    float[] vals = new float[size()];
    if (vals.length == 0)
      return vals; 
    float[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        vals[j++] = v[i]; 
    } 
    return vals;
  }
  
  public float[] values(float[] array) {
    int size = size();
    if (size == 0)
      return array; 
    if (array.length < size)
      array = new float[size]; 
    float[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        array[j++] = v[i]; 
    } 
    return array;
  }
  
  public boolean containsValue(float val) {
    byte[] states = this._states;
    float[] vals = this._values;
    for (int i = vals.length; i-- > 0;) {
      if (states[i] == 1 && val == vals[i])
        return true; 
    } 
    return false;
  }
  
  public boolean containsKey(char key) {
    return contains(key);
  }
  
  public TCharFloatIterator iterator() {
    return new TCharFloatHashIterator(this);
  }
  
  public boolean forEachKey(TCharProcedure procedure) {
    return forEach(procedure);
  }
  
  public boolean forEachValue(TFloatProcedure procedure) {
    byte[] states = this._states;
    float[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(values[i]))
        return false; 
    } 
    return true;
  }
  
  public boolean forEachEntry(TCharFloatProcedure procedure) {
    byte[] states = this._states;
    char[] keys = this._set;
    float[] values = this._values;
    for (int i = keys.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(keys[i], values[i]))
        return false; 
    } 
    return true;
  }
  
  public void transformValues(TFloatFunction function) {
    byte[] states = this._states;
    float[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1)
        values[i] = function.execute(values[i]); 
    } 
  }
  
  public boolean retainEntries(TCharFloatProcedure procedure) {
    boolean modified = false;
    byte[] states = this._states;
    char[] keys = this._set;
    float[] values = this._values;
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
  
  public boolean increment(char key) {
    return adjustValue(key, 1.0F);
  }
  
  public boolean adjustValue(char key, float amount) {
    int index = index(key);
    if (index < 0)
      return false; 
    this._values[index] = this._values[index] + amount;
    return true;
  }
  
  public float adjustOrPutValue(char key, float adjust_amount, float put_amount) {
    boolean isNewMapping;
    float newValue;
    int index = insertKey(key);
    if (index < 0) {
      index = -index - 1;
      newValue = this._values[index] = this._values[index] + adjust_amount;
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
  
  protected class TKeyView implements TCharSet {
    public TCharIterator iterator() {
      return new TCharFloatHashMap.TCharFloatKeyHashIterator((TPrimitiveHash)TCharFloatHashMap.this);
    }
    
    public char getNoEntryValue() {
      return TCharFloatHashMap.this.no_entry_key;
    }
    
    public int size() {
      return TCharFloatHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TCharFloatHashMap.this._size);
    }
    
    public boolean contains(char entry) {
      return TCharFloatHashMap.this.contains(entry);
    }
    
    public char[] toArray() {
      return TCharFloatHashMap.this.keys();
    }
    
    public char[] toArray(char[] dest) {
      return TCharFloatHashMap.this.keys(dest);
    }
    
    public boolean add(char entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(char entry) {
      return (TCharFloatHashMap.this.no_entry_value != TCharFloatHashMap.this.remove(entry));
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Character) {
          char ele = ((Character)element).charValue();
          if (!TCharFloatHashMap.this.containsKey(ele))
            return false; 
          continue;
        } 
        return false;
      } 
      return true;
    }
    
    public boolean containsAll(TCharCollection collection) {
      TCharIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!TCharFloatHashMap.this.containsKey(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(char[] array) {
      for (char element : array) {
        if (!TCharFloatHashMap.this.contains(element))
          return false; 
      } 
      return true;
    }
    
    public boolean addAll(Collection<? extends Character> collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(TCharCollection collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(char[] array) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> collection) {
      boolean modified = false;
      TCharIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(Character.valueOf(iter.next()))) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(TCharCollection collection) {
      if (this == collection)
        return false; 
      boolean modified = false;
      TCharIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(iter.next())) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(char[] array) {
      boolean changed = false;
      Arrays.sort(array);
      char[] set = TCharFloatHashMap.this._set;
      byte[] states = TCharFloatHashMap.this._states;
      for (int i = set.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
          TCharFloatHashMap.this.removeAt(i);
          changed = true;
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(Collection<?> collection) {
      boolean changed = false;
      for (Object element : collection) {
        if (element instanceof Character) {
          char c = ((Character)element).charValue();
          if (remove(c))
            changed = true; 
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(TCharCollection collection) {
      if (this == collection) {
        clear();
        return true;
      } 
      boolean changed = false;
      TCharIterator iter = collection.iterator();
      while (iter.hasNext()) {
        char element = iter.next();
        if (remove(element))
          changed = true; 
      } 
      return changed;
    }
    
    public boolean removeAll(char[] array) {
      boolean changed = false;
      for (int i = array.length; i-- > 0;) {
        if (remove(array[i]))
          changed = true; 
      } 
      return changed;
    }
    
    public void clear() {
      TCharFloatHashMap.this.clear();
    }
    
    public boolean forEach(TCharProcedure procedure) {
      return TCharFloatHashMap.this.forEachKey(procedure);
    }
    
    public boolean equals(Object other) {
      if (!(other instanceof TCharSet))
        return false; 
      TCharSet that = (TCharSet)other;
      if (that.size() != size())
        return false; 
      for (int i = TCharFloatHashMap.this._states.length; i-- > 0;) {
        if (TCharFloatHashMap.this._states[i] == 1 && 
          !that.contains(TCharFloatHashMap.this._set[i]))
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      int hashcode = 0;
      for (int i = TCharFloatHashMap.this._states.length; i-- > 0;) {
        if (TCharFloatHashMap.this._states[i] == 1)
          hashcode += HashFunctions.hash(TCharFloatHashMap.this._set[i]); 
      } 
      return hashcode;
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TCharFloatHashMap.this.forEachKey(new TCharProcedure() {
            private boolean first = true;
            
            public boolean execute(char key) {
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
  
  protected class TValueView implements TFloatCollection {
    public TFloatIterator iterator() {
      return new TCharFloatHashMap.TCharFloatValueHashIterator((TPrimitiveHash)TCharFloatHashMap.this);
    }
    
    public float getNoEntryValue() {
      return TCharFloatHashMap.this.no_entry_value;
    }
    
    public int size() {
      return TCharFloatHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TCharFloatHashMap.this._size);
    }
    
    public boolean contains(float entry) {
      return TCharFloatHashMap.this.containsValue(entry);
    }
    
    public float[] toArray() {
      return TCharFloatHashMap.this.values();
    }
    
    public float[] toArray(float[] dest) {
      return TCharFloatHashMap.this.values(dest);
    }
    
    public boolean add(float entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(float entry) {
      float[] values = TCharFloatHashMap.this._values;
      byte[] states = TCharFloatHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
          TCharFloatHashMap.this.removeAt(i);
          return true;
        } 
      } 
      return false;
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Float) {
          float ele = ((Float)element).floatValue();
          if (!TCharFloatHashMap.this.containsValue(ele))
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
        if (!TCharFloatHashMap.this.containsValue(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(float[] array) {
      for (float element : array) {
        if (!TCharFloatHashMap.this.containsValue(element))
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
      float[] values = TCharFloatHashMap.this._values;
      byte[] states = TCharFloatHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
          TCharFloatHashMap.this.removeAt(i);
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
      TCharFloatHashMap.this.clear();
    }
    
    public boolean forEach(TFloatProcedure procedure) {
      return TCharFloatHashMap.this.forEachValue(procedure);
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TCharFloatHashMap.this.forEachValue(new TFloatProcedure() {
            private boolean first = true;
            
            public boolean execute(float value) {
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
  
  class TCharFloatKeyHashIterator extends THashPrimitiveIterator implements TCharIterator {
    TCharFloatKeyHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public char next() {
      moveToNextIndex();
      return TCharFloatHashMap.this._set[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TCharFloatHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TCharFloatValueHashIterator extends THashPrimitiveIterator implements TFloatIterator {
    TCharFloatValueHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public float next() {
      moveToNextIndex();
      return TCharFloatHashMap.this._values[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TCharFloatHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TCharFloatHashIterator extends THashPrimitiveIterator implements TCharFloatIterator {
    TCharFloatHashIterator(TCharFloatHashMap map) {
      super((TPrimitiveHash)map);
    }
    
    public void advance() {
      moveToNextIndex();
    }
    
    public char key() {
      return TCharFloatHashMap.this._set[this._index];
    }
    
    public float value() {
      return TCharFloatHashMap.this._values[this._index];
    }
    
    public float setValue(float val) {
      float old = value();
      TCharFloatHashMap.this._values[this._index] = val;
      return old;
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TCharFloatHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  public boolean equals(Object other) {
    if (!(other instanceof TCharFloatMap))
      return false; 
    TCharFloatMap that = (TCharFloatMap)other;
    if (that.size() != size())
      return false; 
    float[] values = this._values;
    byte[] states = this._states;
    float this_no_entry_value = getNoEntryValue();
    float that_no_entry_value = that.getNoEntryValue();
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1) {
        char key = this._set[i];
        if (!that.containsKey(key))
          return false; 
        float that_value = that.get(key);
        float this_value = values[i];
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
    forEachEntry(new TCharFloatProcedure() {
          private boolean first = true;
          
          public boolean execute(char key, float value) {
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
        out.writeChar(this._set[i]);
        out.writeFloat(this._values[i]);
      } 
    } 
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    super.readExternal(in);
    int size = in.readInt();
    setUp(size);
    while (size-- > 0) {
      char key = in.readChar();
      float val = in.readFloat();
      put(key, val);
    } 
  }
}
