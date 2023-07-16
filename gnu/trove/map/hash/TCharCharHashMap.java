package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharCharHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharCharIterator;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.map.TCharCharMap;
import gnu.trove.procedure.TCharCharProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.set.TCharSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TCharCharHashMap extends TCharCharHash implements TCharCharMap, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected transient char[] _values;
  
  public TCharCharHashMap() {}
  
  public TCharCharHashMap(int initialCapacity) {
    super(initialCapacity);
  }
  
  public TCharCharHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }
  
  public TCharCharHashMap(int initialCapacity, float loadFactor, char noEntryKey, char noEntryValue) {
    super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
  }
  
  public TCharCharHashMap(char[] keys, char[] values) {
    super(Math.max(keys.length, values.length));
    int size = Math.min(keys.length, values.length);
    for (int i = 0; i < size; i++)
      put(keys[i], values[i]); 
  }
  
  public TCharCharHashMap(TCharCharMap map) {
    super(map.size());
    if (map instanceof TCharCharHashMap) {
      TCharCharHashMap hashmap = (TCharCharHashMap)map;
      this._loadFactor = Math.abs(hashmap._loadFactor);
      this.no_entry_key = hashmap.no_entry_key;
      this.no_entry_value = hashmap.no_entry_value;
      if (this.no_entry_key != '\000')
        Arrays.fill(this._set, this.no_entry_key); 
      if (this.no_entry_value != '\000')
        Arrays.fill(this._values, this.no_entry_value); 
      setUp(saturatedCast(fastCeil(10.0D / this._loadFactor)));
    } 
    putAll(map);
  }
  
  protected int setUp(int initialCapacity) {
    int capacity = super.setUp(initialCapacity);
    this._values = new char[capacity];
    return capacity;
  }
  
  protected void rehash(int newCapacity) {
    int oldCapacity = this._set.length;
    char[] oldKeys = this._set;
    char[] oldVals = this._values;
    byte[] oldStates = this._states;
    this._set = new char[newCapacity];
    this._values = new char[newCapacity];
    this._states = new byte[newCapacity];
    for (int i = oldCapacity; i-- > 0;) {
      if (oldStates[i] == 1) {
        char o = oldKeys[i];
        int index = insertKey(o);
        this._values[index] = oldVals[i];
      } 
    } 
  }
  
  public char put(char key, char value) {
    int index = insertKey(key);
    return doPut(key, value, index);
  }
  
  public char putIfAbsent(char key, char value) {
    int index = insertKey(key);
    if (index < 0)
      return this._values[-index - 1]; 
    return doPut(key, value, index);
  }
  
  private char doPut(char key, char value, int index) {
    char previous = this.no_entry_value;
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
  
  public void putAll(Map<? extends Character, ? extends Character> map) {
    ensureCapacity(map.size());
    for (Map.Entry<? extends Character, ? extends Character> entry : map.entrySet())
      put(((Character)entry.getKey()).charValue(), ((Character)entry.getValue()).charValue()); 
  }
  
  public void putAll(TCharCharMap map) {
    ensureCapacity(map.size());
    TCharCharIterator iter = map.iterator();
    while (iter.hasNext()) {
      iter.advance();
      put(iter.key(), iter.value());
    } 
  }
  
  public char get(char key) {
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
  
  public char remove(char key) {
    char prev = this.no_entry_value;
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
  
  public TCharCollection valueCollection() {
    return new TValueView();
  }
  
  public char[] values() {
    char[] vals = new char[size()];
    if (vals.length == 0)
      return vals; 
    char[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        vals[j++] = v[i]; 
    } 
    return vals;
  }
  
  public char[] values(char[] array) {
    int size = size();
    if (size == 0)
      return array; 
    if (array.length < size)
      array = new char[size]; 
    char[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        array[j++] = v[i]; 
    } 
    return array;
  }
  
  public boolean containsValue(char val) {
    byte[] states = this._states;
    char[] vals = this._values;
    for (int i = vals.length; i-- > 0;) {
      if (states[i] == 1 && val == vals[i])
        return true; 
    } 
    return false;
  }
  
  public boolean containsKey(char key) {
    return contains(key);
  }
  
  public TCharCharIterator iterator() {
    return new TCharCharHashIterator(this);
  }
  
  public boolean forEachKey(TCharProcedure procedure) {
    return forEach(procedure);
  }
  
  public boolean forEachValue(TCharProcedure procedure) {
    byte[] states = this._states;
    char[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(values[i]))
        return false; 
    } 
    return true;
  }
  
  public boolean forEachEntry(TCharCharProcedure procedure) {
    byte[] states = this._states;
    char[] keys = this._set;
    char[] values = this._values;
    for (int i = keys.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(keys[i], values[i]))
        return false; 
    } 
    return true;
  }
  
  public void transformValues(TCharFunction function) {
    byte[] states = this._states;
    char[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1)
        values[i] = function.execute(values[i]); 
    } 
  }
  
  public boolean retainEntries(TCharCharProcedure procedure) {
    boolean modified = false;
    byte[] states = this._states;
    char[] keys = this._set;
    char[] values = this._values;
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
    return adjustValue(key, '\001');
  }
  
  public boolean adjustValue(char key, char amount) {
    int index = index(key);
    if (index < 0)
      return false; 
    this._values[index] = (char)(this._values[index] + amount);
    return true;
  }
  
  public char adjustOrPutValue(char key, char adjust_amount, char put_amount) {
    boolean isNewMapping;
    char newValue;
    int index = insertKey(key);
    if (index < 0) {
      index = -index - 1;
      newValue = this._values[index] = (char)(this._values[index] + adjust_amount);
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
      return new TCharCharHashMap.TCharCharKeyHashIterator((TPrimitiveHash)TCharCharHashMap.this);
    }
    
    public char getNoEntryValue() {
      return TCharCharHashMap.this.no_entry_key;
    }
    
    public int size() {
      return TCharCharHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TCharCharHashMap.this._size);
    }
    
    public boolean contains(char entry) {
      return TCharCharHashMap.this.contains(entry);
    }
    
    public char[] toArray() {
      return TCharCharHashMap.this.keys();
    }
    
    public char[] toArray(char[] dest) {
      return TCharCharHashMap.this.keys(dest);
    }
    
    public boolean add(char entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(char entry) {
      return (TCharCharHashMap.this.no_entry_value != TCharCharHashMap.this.remove(entry));
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Character) {
          char ele = ((Character)element).charValue();
          if (!TCharCharHashMap.this.containsKey(ele))
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
        if (!TCharCharHashMap.this.containsKey(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(char[] array) {
      for (char element : array) {
        if (!TCharCharHashMap.this.contains(element))
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
      char[] set = TCharCharHashMap.this._set;
      byte[] states = TCharCharHashMap.this._states;
      for (int i = set.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
          TCharCharHashMap.this.removeAt(i);
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
      TCharCharHashMap.this.clear();
    }
    
    public boolean forEach(TCharProcedure procedure) {
      return TCharCharHashMap.this.forEachKey(procedure);
    }
    
    public boolean equals(Object other) {
      if (!(other instanceof TCharSet))
        return false; 
      TCharSet that = (TCharSet)other;
      if (that.size() != size())
        return false; 
      for (int i = TCharCharHashMap.this._states.length; i-- > 0;) {
        if (TCharCharHashMap.this._states[i] == 1 && 
          !that.contains(TCharCharHashMap.this._set[i]))
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      int hashcode = 0;
      for (int i = TCharCharHashMap.this._states.length; i-- > 0;) {
        if (TCharCharHashMap.this._states[i] == 1)
          hashcode += HashFunctions.hash(TCharCharHashMap.this._set[i]); 
      } 
      return hashcode;
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TCharCharHashMap.this.forEachKey(new TCharProcedure() {
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
  
  protected class TValueView implements TCharCollection {
    public TCharIterator iterator() {
      return new TCharCharHashMap.TCharCharValueHashIterator((TPrimitiveHash)TCharCharHashMap.this);
    }
    
    public char getNoEntryValue() {
      return TCharCharHashMap.this.no_entry_value;
    }
    
    public int size() {
      return TCharCharHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TCharCharHashMap.this._size);
    }
    
    public boolean contains(char entry) {
      return TCharCharHashMap.this.containsValue(entry);
    }
    
    public char[] toArray() {
      return TCharCharHashMap.this.values();
    }
    
    public char[] toArray(char[] dest) {
      return TCharCharHashMap.this.values(dest);
    }
    
    public boolean add(char entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(char entry) {
      char[] values = TCharCharHashMap.this._values;
      byte[] states = TCharCharHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
          TCharCharHashMap.this.removeAt(i);
          return true;
        } 
      } 
      return false;
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Character) {
          char ele = ((Character)element).charValue();
          if (!TCharCharHashMap.this.containsValue(ele))
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
        if (!TCharCharHashMap.this.containsValue(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(char[] array) {
      for (char element : array) {
        if (!TCharCharHashMap.this.containsValue(element))
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
      char[] values = TCharCharHashMap.this._values;
      byte[] states = TCharCharHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
          TCharCharHashMap.this.removeAt(i);
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
      TCharCharHashMap.this.clear();
    }
    
    public boolean forEach(TCharProcedure procedure) {
      return TCharCharHashMap.this.forEachValue(procedure);
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TCharCharHashMap.this.forEachValue(new TCharProcedure() {
            private boolean first = true;
            
            public boolean execute(char value) {
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
  
  class TCharCharKeyHashIterator extends THashPrimitiveIterator implements TCharIterator {
    TCharCharKeyHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public char next() {
      moveToNextIndex();
      return TCharCharHashMap.this._set[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TCharCharHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TCharCharValueHashIterator extends THashPrimitiveIterator implements TCharIterator {
    TCharCharValueHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public char next() {
      moveToNextIndex();
      return TCharCharHashMap.this._values[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TCharCharHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TCharCharHashIterator extends THashPrimitiveIterator implements TCharCharIterator {
    TCharCharHashIterator(TCharCharHashMap map) {
      super((TPrimitiveHash)map);
    }
    
    public void advance() {
      moveToNextIndex();
    }
    
    public char key() {
      return TCharCharHashMap.this._set[this._index];
    }
    
    public char value() {
      return TCharCharHashMap.this._values[this._index];
    }
    
    public char setValue(char val) {
      char old = value();
      TCharCharHashMap.this._values[this._index] = val;
      return old;
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TCharCharHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  public boolean equals(Object other) {
    if (!(other instanceof TCharCharMap))
      return false; 
    TCharCharMap that = (TCharCharMap)other;
    if (that.size() != size())
      return false; 
    char[] values = this._values;
    byte[] states = this._states;
    char this_no_entry_value = getNoEntryValue();
    char that_no_entry_value = that.getNoEntryValue();
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1) {
        char key = this._set[i];
        if (!that.containsKey(key))
          return false; 
        char that_value = that.get(key);
        char this_value = values[i];
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
    forEachEntry(new TCharCharProcedure() {
          private boolean first = true;
          
          public boolean execute(char key, char value) {
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
        out.writeChar(this._values[i]);
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
      char val = in.readChar();
      put(key, val);
    } 
  }
}
