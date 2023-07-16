package org.codehaus.plexus.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FastMap<K, V> implements Map<K, V>, Cloneable, Serializable {
  private transient EntryImpl[] _entries;
  
  private transient int _capacity;
  
  private transient int _mask;
  
  private transient EntryImpl _poolFirst;
  
  private transient EntryImpl _mapFirst;
  
  private transient EntryImpl _mapLast;
  
  private transient int _size;
  
  private transient Values _values;
  
  private transient EntrySet _entrySet;
  
  private transient KeySet _keySet;
  
  public FastMap() {
    initialize(256);
  }
  
  public FastMap(Map<? extends K, ? extends V> map) {
    int capacity = (map instanceof FastMap) ? ((FastMap)map).capacity() : map.size();
    initialize(capacity);
    putAll(map);
  }
  
  public FastMap(int capacity) {
    initialize(capacity);
  }
  
  public int size() {
    return this._size;
  }
  
  public int capacity() {
    return this._capacity;
  }
  
  public boolean isEmpty() {
    return (this._size == 0);
  }
  
  public boolean containsKey(Object key) {
    EntryImpl entry = this._entries[keyHash(key) & this._mask];
    while (entry != null) {
      if (key.equals(entry._key))
        return true; 
      entry = entry._next;
    } 
    return false;
  }
  
  public boolean containsValue(Object value) {
    EntryImpl entry = this._mapFirst;
    while (entry != null) {
      if (value.equals(entry._value))
        return true; 
      entry = entry._after;
    } 
    return false;
  }
  
  public V get(Object key) {
    EntryImpl<K, V> entry = this._entries[keyHash(key) & this._mask];
    while (entry != null) {
      if (key.equals(entry._key))
        return entry._value; 
      entry = entry._next;
    } 
    return null;
  }
  
  public Map.Entry getEntry(Object key) {
    EntryImpl entry = this._entries[keyHash(key) & this._mask];
    while (entry != null) {
      if (key.equals(entry._key))
        return entry; 
      entry = entry._next;
    } 
    return null;
  }
  
  public Object put(Object key, Object value) {
    EntryImpl entry = this._entries[keyHash(key) & this._mask];
    while (entry != null) {
      if (key.equals(entry._key)) {
        Object prevValue = entry._value;
        entry._value = (V)value;
        return prevValue;
      } 
      entry = entry._next;
    } 
    addEntry(key, value);
    return null;
  }
  
  public void putAll(Map<? extends K, ? extends V> map) {
    for (Map.Entry<? extends K, ? extends V> entry : map.entrySet())
      addEntry(entry.getKey(), entry.getValue()); 
  }
  
  public V remove(Object key) {
    EntryImpl<K, V> entry = this._entries[keyHash(key) & this._mask];
    while (entry != null) {
      if (key.equals(entry._key)) {
        V prevValue = entry._value;
        removeEntry(entry);
        return prevValue;
      } 
      entry = entry._next;
    } 
    return null;
  }
  
  public void clear() {
    for (EntryImpl entry = this._mapFirst; entry != null; entry = entry._after) {
      entry._key = null;
      entry._value = null;
      entry._before = null;
      entry._next = null;
      if (entry._previous == null) {
        this._entries[entry._index] = null;
      } else {
        entry._previous = null;
      } 
    } 
    if (this._mapLast != null) {
      this._mapLast._after = this._poolFirst;
      this._poolFirst = this._mapFirst;
      this._mapFirst = null;
      this._mapLast = null;
      this._size = 0;
      sizeChanged();
    } 
  }
  
  public void setCapacity(int newCapacity) {
    if (newCapacity > this._capacity) {
      for (int i = this._capacity; i < newCapacity; i++) {
        EntryImpl<Object, Object> entry = new EntryImpl<Object, Object>();
        entry._after = this._poolFirst;
        this._poolFirst = entry;
      } 
    } else if (newCapacity < this._capacity) {
      for (int i = newCapacity; i < this._capacity && this._poolFirst != null; i++) {
        EntryImpl entry = this._poolFirst;
        this._poolFirst = entry._after;
        entry._after = null;
      } 
    } 
    int tableLength = 16;
    while (tableLength < newCapacity)
      tableLength <<= 1; 
    if (this._entries.length != tableLength) {
      this._entries = new EntryImpl[tableLength];
      this._mask = tableLength - 1;
      EntryImpl entry = this._mapFirst;
      while (entry != null) {
        int index = keyHash(entry._key) & this._mask;
        entry._index = index;
        entry._previous = null;
        EntryImpl next = this._entries[index];
        entry._next = next;
        if (next != null)
          next._previous = entry; 
        this._entries[index] = entry;
        entry = entry._after;
      } 
    } 
    this._capacity = newCapacity;
  }
  
  public Object clone() {
    try {
      FastMap<K, V> clone = (FastMap)super.clone();
      clone.initialize(this._capacity);
      clone.putAll(this);
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new InternalError();
    } 
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (obj instanceof Map) {
      Map that = (Map)obj;
      if (size() == that.size()) {
        EntryImpl entry = this._mapFirst;
        while (entry != null) {
          if (!that.entrySet().contains(entry))
            return false; 
          entry = entry._after;
        } 
        return true;
      } 
      return false;
    } 
    return false;
  }
  
  public int hashCode() {
    int code = 0;
    EntryImpl entry = this._mapFirst;
    while (entry != null) {
      code += entry.hashCode();
      entry = entry._after;
    } 
    return code;
  }
  
  public String toString() {
    return entrySet().toString();
  }
  
  public Collection values() {
    return this._values;
  }
  
  private class Values extends AbstractCollection {
    private Values() {}
    
    public Iterator iterator() {
      return new Iterator() {
          FastMap.EntryImpl after = FastMap.this._mapFirst;
          
          FastMap.EntryImpl before;
          
          public void remove() {
            FastMap.this.removeEntry(this.before);
          }
          
          public boolean hasNext() {
            return (this.after != null);
          }
          
          public Object next() {
            this.before = this.after;
            this.after = this.after._after;
            return this.before._value;
          }
        };
    }
    
    public int size() {
      return FastMap.this._size;
    }
    
    public boolean contains(Object o) {
      return FastMap.this.containsValue(o);
    }
    
    public void clear() {
      FastMap.this.clear();
    }
  }
  
  public Set entrySet() {
    return this._entrySet;
  }
  
  private class EntrySet extends AbstractSet {
    private EntrySet() {}
    
    public Iterator iterator() {
      return new Iterator() {
          FastMap.EntryImpl after = FastMap.this._mapFirst;
          
          FastMap.EntryImpl before;
          
          public void remove() {
            FastMap.this.removeEntry(this.before);
          }
          
          public boolean hasNext() {
            return (this.after != null);
          }
          
          public Object next() {
            this.before = this.after;
            this.after = this.after._after;
            return this.before;
          }
        };
    }
    
    public int size() {
      return FastMap.this._size;
    }
    
    public boolean contains(Object obj) {
      if (obj instanceof Map.Entry) {
        Map.Entry entry = (Map.Entry)obj;
        Map.Entry mapEntry = FastMap.this.getEntry(entry.getKey());
        return entry.equals(mapEntry);
      } 
      return false;
    }
    
    public boolean remove(Object obj) {
      if (obj instanceof Map.Entry) {
        Map.Entry entry = (Map.Entry)obj;
        FastMap.EntryImpl mapEntry = (FastMap.EntryImpl)FastMap.this.getEntry(entry.getKey());
        if (mapEntry != null && entry.getValue().equals(mapEntry._value)) {
          FastMap.this.removeEntry(mapEntry);
          return true;
        } 
      } 
      return false;
    }
  }
  
  public Set keySet() {
    return this._keySet;
  }
  
  private class KeySet extends AbstractSet {
    private KeySet() {}
    
    public Iterator iterator() {
      return new Iterator() {
          FastMap.EntryImpl after = FastMap.this._mapFirst;
          
          FastMap.EntryImpl before;
          
          public void remove() {
            FastMap.this.removeEntry(this.before);
          }
          
          public boolean hasNext() {
            return (this.after != null);
          }
          
          public Object next() {
            this.before = this.after;
            this.after = this.after._after;
            return this.before._key;
          }
        };
    }
    
    public int size() {
      return FastMap.this._size;
    }
    
    public boolean contains(Object obj) {
      return FastMap.this.containsKey(obj);
    }
    
    public boolean remove(Object obj) {
      return (FastMap.this.remove(obj) != null);
    }
    
    public void clear() {
      FastMap.this.clear();
    }
  }
  
  protected void sizeChanged() {
    if (size() > capacity())
      setCapacity(capacity() * 2); 
  }
  
  private static int keyHash(Object key) {
    int hashCode = key.hashCode();
    hashCode += hashCode << 9 ^ 0xFFFFFFFF;
    hashCode ^= hashCode >>> 14;
    hashCode += hashCode << 4;
    hashCode ^= hashCode >>> 10;
    return hashCode;
  }
  
  private void addEntry(Object key, Object value) {
    EntryImpl<Object, Object> entry = this._poolFirst;
    if (entry != null) {
      this._poolFirst = entry._after;
      entry._after = null;
    } else {
      entry = new EntryImpl<Object, Object>();
    } 
    entry._key = (K)key;
    entry._value = (V)value;
    int index = keyHash(key) & this._mask;
    entry._index = index;
    EntryImpl next = this._entries[index];
    entry._next = next;
    if (next != null)
      next._previous = entry; 
    this._entries[index] = entry;
    if (this._mapLast != null) {
      entry._before = this._mapLast;
      this._mapLast._after = entry;
    } else {
      this._mapFirst = entry;
    } 
    this._mapLast = entry;
    this._size++;
    sizeChanged();
  }
  
  private void removeEntry(EntryImpl entry) {
    EntryImpl previous = entry._previous;
    EntryImpl next = entry._next;
    if (previous != null) {
      previous._next = next;
      entry._previous = null;
    } else {
      this._entries[entry._index] = next;
    } 
    if (next != null) {
      next._previous = previous;
      entry._next = null;
    } 
    EntryImpl before = entry._before;
    EntryImpl after = entry._after;
    if (before != null) {
      before._after = after;
      entry._before = null;
    } else {
      this._mapFirst = after;
    } 
    if (after != null) {
      after._before = before;
    } else {
      this._mapLast = before;
    } 
    entry._key = null;
    entry._value = null;
    entry._after = this._poolFirst;
    this._poolFirst = entry;
    this._size--;
    sizeChanged();
  }
  
  private void initialize(int capacity) {
    int tableLength = 16;
    while (tableLength < capacity)
      tableLength <<= 1; 
    this._entries = new EntryImpl[tableLength];
    this._mask = tableLength - 1;
    this._capacity = capacity;
    this._size = 0;
    this._values = new Values();
    this._entrySet = new EntrySet();
    this._keySet = new KeySet();
    this._poolFirst = null;
    this._mapFirst = null;
    this._mapLast = null;
    for (int i = 0; i < capacity; i++) {
      EntryImpl<Object, Object> entry = new EntryImpl<Object, Object>();
      entry._after = this._poolFirst;
      this._poolFirst = entry;
    } 
  }
  
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    int capacity = stream.readInt();
    initialize(capacity);
    int size = stream.readInt();
    for (int i = 0; i < size; i++) {
      Object key = stream.readObject();
      Object value = stream.readObject();
      addEntry(key, value);
    } 
  }
  
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.writeInt(this._capacity);
    stream.writeInt(this._size);
    int count = 0;
    EntryImpl entry = this._mapFirst;
    while (entry != null) {
      stream.writeObject(entry._key);
      stream.writeObject(entry._value);
      count++;
      entry = entry._after;
    } 
    if (count != this._size)
      throw new IOException("FastMap Corrupted"); 
  }
  
  private static final class EntryImpl<K, V> implements Map.Entry<K, V> {
    private K _key;
    
    private V _value;
    
    private int _index;
    
    private EntryImpl _previous;
    
    private EntryImpl _next;
    
    private EntryImpl _before;
    
    private EntryImpl _after;
    
    private EntryImpl() {}
    
    public K getKey() {
      return this._key;
    }
    
    public V getValue() {
      return this._value;
    }
    
    public V setValue(V value) {
      V old = this._value;
      this._value = value;
      return old;
    }
    
    public boolean equals(Object that) {
      if (that instanceof Map.Entry) {
        Map.Entry entry = (Map.Entry)that;
        return (this._key.equals(entry.getKey()) && ((this._value != null) ? this._value.equals(entry.getValue()) : (entry.getValue() == null)));
      } 
      return false;
    }
    
    public int hashCode() {
      return this._key.hashCode() ^ ((this._value != null) ? this._value.hashCode() : 0);
    }
    
    public String toString() {
      return (new StringBuilder()).append(this._key).append("=").append(this._value).toString();
    }
  }
}
