package com.google.protobuf;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

class SmallSortedMap<K extends Comparable<K>, V> extends AbstractMap<K, V> {
  private final int maxArraySize;
  
  private List<Entry> entryList;
  
  private Map<K, V> overflowEntries;
  
  private boolean isImmutable;
  
  private volatile EntrySet lazyEntrySet;
  
  private Map<K, V> overflowEntriesDescending;
  
  private volatile DescendingEntrySet lazyDescendingEntrySet;
  
  static <FieldDescriptorType extends FieldSet.FieldDescriptorLite<FieldDescriptorType>> SmallSortedMap<FieldDescriptorType, Object> newFieldMap(int arraySize) {
    return new SmallSortedMap<FieldDescriptorType, Object>(arraySize) {
        public void makeImmutable() {
          if (!isImmutable()) {
            for (int i = 0; i < getNumArrayEntries(); i++) {
              Map.Entry<FieldDescriptorType, Object> entry = getArrayEntryAt(i);
              if (((FieldSet.FieldDescriptorLite)entry.getKey()).isRepeated()) {
                List<?> value = (List)entry.getValue();
                entry.setValue(Collections.unmodifiableList(value));
              } 
            } 
            for (Map.Entry<FieldDescriptorType, Object> entry : getOverflowEntries()) {
              if (((FieldSet.FieldDescriptorLite)entry.getKey()).isRepeated()) {
                List<?> value = (List)entry.getValue();
                entry.setValue(Collections.unmodifiableList(value));
              } 
            } 
          } 
          super.makeImmutable();
        }
      };
  }
  
  static <K extends Comparable<K>, V> SmallSortedMap<K, V> newInstanceForTest(int arraySize) {
    return new SmallSortedMap<>(arraySize);
  }
  
  private SmallSortedMap(int arraySize) {
    this.maxArraySize = arraySize;
    this.entryList = Collections.emptyList();
    this.overflowEntries = Collections.emptyMap();
    this.overflowEntriesDescending = Collections.emptyMap();
  }
  
  public void makeImmutable() {
    if (!this.isImmutable) {
      this
        
        .overflowEntries = this.overflowEntries.isEmpty() ? Collections.<K, V>emptyMap() : Collections.<K, V>unmodifiableMap(this.overflowEntries);
      this
        
        .overflowEntriesDescending = this.overflowEntriesDescending.isEmpty() ? Collections.<K, V>emptyMap() : Collections.<K, V>unmodifiableMap(this.overflowEntriesDescending);
      this.isImmutable = true;
    } 
  }
  
  public boolean isImmutable() {
    return this.isImmutable;
  }
  
  public int getNumArrayEntries() {
    return this.entryList.size();
  }
  
  public Map.Entry<K, V> getArrayEntryAt(int index) {
    return this.entryList.get(index);
  }
  
  public int getNumOverflowEntries() {
    return this.overflowEntries.size();
  }
  
  public Iterable<Map.Entry<K, V>> getOverflowEntries() {
    return this.overflowEntries.isEmpty() ? 
      EmptySet.<Map.Entry<K, V>>iterable() : this.overflowEntries
      .entrySet();
  }
  
  Iterable<Map.Entry<K, V>> getOverflowEntriesDescending() {
    return this.overflowEntriesDescending.isEmpty() ? 
      EmptySet.<Map.Entry<K, V>>iterable() : this.overflowEntriesDescending
      .entrySet();
  }
  
  public int size() {
    return this.entryList.size() + this.overflowEntries.size();
  }
  
  public boolean containsKey(Object o) {
    Comparable comparable = (Comparable)o;
    return (binarySearchInArray((K)comparable) >= 0 || this.overflowEntries.containsKey(comparable));
  }
  
  public V get(Object o) {
    Comparable comparable = (Comparable)o;
    int index = binarySearchInArray((K)comparable);
    if (index >= 0)
      return ((Entry)this.entryList.get(index)).getValue(); 
    return this.overflowEntries.get(comparable);
  }
  
  public V put(K key, V value) {
    checkMutable();
    int index = binarySearchInArray(key);
    if (index >= 0)
      return ((Entry)this.entryList.get(index)).setValue(value); 
    ensureEntryArrayMutable();
    int insertionPoint = -(index + 1);
    if (insertionPoint >= this.maxArraySize)
      return getOverflowEntriesMutable().put(key, value); 
    if (this.entryList.size() == this.maxArraySize) {
      Entry lastEntryInArray = this.entryList.remove(this.maxArraySize - 1);
      getOverflowEntriesMutable().put(lastEntryInArray.getKey(), lastEntryInArray.getValue());
    } 
    this.entryList.add(insertionPoint, new Entry(key, value));
    return null;
  }
  
  public void clear() {
    checkMutable();
    if (!this.entryList.isEmpty())
      this.entryList.clear(); 
    if (!this.overflowEntries.isEmpty())
      this.overflowEntries.clear(); 
  }
  
  public V remove(Object o) {
    checkMutable();
    Comparable comparable = (Comparable)o;
    int index = binarySearchInArray((K)comparable);
    if (index >= 0)
      return removeArrayEntryAt(index); 
    if (this.overflowEntries.isEmpty())
      return null; 
    return this.overflowEntries.remove(comparable);
  }
  
  private V removeArrayEntryAt(int index) {
    checkMutable();
    V removed = ((Entry)this.entryList.remove(index)).getValue();
    if (!this.overflowEntries.isEmpty()) {
      Iterator<Map.Entry<K, V>> iterator = getOverflowEntriesMutable().entrySet().iterator();
      this.entryList.add(new Entry(iterator.next()));
      iterator.remove();
    } 
    return removed;
  }
  
  private int binarySearchInArray(K key) {
    int left = 0;
    int right = this.entryList.size() - 1;
    if (right >= 0) {
      int cmp = key.compareTo(((Entry)this.entryList.get(right)).getKey());
      if (cmp > 0)
        return -(right + 2); 
      if (cmp == 0)
        return right; 
    } 
    while (left <= right) {
      int mid = (left + right) / 2;
      int cmp = key.compareTo(((Entry)this.entryList.get(mid)).getKey());
      if (cmp < 0) {
        right = mid - 1;
        continue;
      } 
      if (cmp > 0) {
        left = mid + 1;
        continue;
      } 
      return mid;
    } 
    return -(left + 1);
  }
  
  public Set<Map.Entry<K, V>> entrySet() {
    if (this.lazyEntrySet == null)
      this.lazyEntrySet = new EntrySet(); 
    return this.lazyEntrySet;
  }
  
  Set<Map.Entry<K, V>> descendingEntrySet() {
    if (this.lazyDescendingEntrySet == null)
      this.lazyDescendingEntrySet = new DescendingEntrySet(); 
    return this.lazyDescendingEntrySet;
  }
  
  private void checkMutable() {
    if (this.isImmutable)
      throw new UnsupportedOperationException(); 
  }
  
  private SortedMap<K, V> getOverflowEntriesMutable() {
    checkMutable();
    if (this.overflowEntries.isEmpty() && !(this.overflowEntries instanceof TreeMap)) {
      this.overflowEntries = new TreeMap<>();
      this.overflowEntriesDescending = ((TreeMap<K, V>)this.overflowEntries).descendingMap();
    } 
    return (SortedMap<K, V>)this.overflowEntries;
  }
  
  private void ensureEntryArrayMutable() {
    checkMutable();
    if (this.entryList.isEmpty() && !(this.entryList instanceof ArrayList))
      this.entryList = new ArrayList<>(this.maxArraySize); 
  }
  
  private class Entry implements Map.Entry<K, V>, Comparable<Entry> {
    private final K key;
    
    private V value;
    
    Entry(Map.Entry<K, V> copy) {
      this(copy.getKey(), copy.getValue());
    }
    
    Entry(K key, V value) {
      this.key = key;
      this.value = value;
    }
    
    public K getKey() {
      return this.key;
    }
    
    public V getValue() {
      return this.value;
    }
    
    public int compareTo(Entry other) {
      return getKey().compareTo(other.getKey());
    }
    
    public V setValue(V newValue) {
      SmallSortedMap.this.checkMutable();
      V oldValue = this.value;
      this.value = newValue;
      return oldValue;
    }
    
    public boolean equals(Object o) {
      if (o == this)
        return true; 
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<?, ?> other = (Map.Entry<?, ?>)o;
      return (equals(this.key, other.getKey()) && equals(this.value, other.getValue()));
    }
    
    public int hashCode() {
      return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
    }
    
    public String toString() {
      return (new StringBuilder()).append(this.key).append("=").append(this.value).toString();
    }
    
    private boolean equals(Object o1, Object o2) {
      return (o1 == null) ? ((o2 == null)) : o1.equals(o2);
    }
  }
  
  private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
    private EntrySet() {}
    
    public Iterator<Map.Entry<K, V>> iterator() {
      return new SmallSortedMap.EntryIterator();
    }
    
    public int size() {
      return SmallSortedMap.this.size();
    }
    
    public boolean contains(Object o) {
      Map.Entry<K, V> entry = (Map.Entry<K, V>)o;
      V existing = (V)SmallSortedMap.this.get(entry.getKey());
      V value = entry.getValue();
      return (existing == value || (existing != null && existing.equals(value)));
    }
    
    public boolean add(Map.Entry<K, V> entry) {
      if (!contains(entry)) {
        SmallSortedMap.this.put((Comparable)entry.getKey(), entry.getValue());
        return true;
      } 
      return false;
    }
    
    public boolean remove(Object o) {
      Map.Entry<K, V> entry = (Map.Entry<K, V>)o;
      if (contains(entry)) {
        SmallSortedMap.this.remove(entry.getKey());
        return true;
      } 
      return false;
    }
    
    public void clear() {
      SmallSortedMap.this.clear();
    }
  }
  
  private class DescendingEntrySet extends EntrySet {
    private DescendingEntrySet() {}
    
    public Iterator<Map.Entry<K, V>> iterator() {
      return new SmallSortedMap.DescendingEntryIterator();
    }
  }
  
  private class EntryIterator implements Iterator<Map.Entry<K, V>> {
    private int pos = -1;
    
    private boolean nextCalledBeforeRemove;
    
    private Iterator<Map.Entry<K, V>> lazyOverflowIterator;
    
    public boolean hasNext() {
      return (this.pos + 1 < SmallSortedMap.access$600(SmallSortedMap.this).size() || (
        !SmallSortedMap.this.overflowEntries.isEmpty() && getOverflowIterator().hasNext()));
    }
    
    public Map.Entry<K, V> next() {
      this.nextCalledBeforeRemove = true;
      if (++this.pos < SmallSortedMap.access$600(SmallSortedMap.this).size())
        return SmallSortedMap.access$600(SmallSortedMap.this).get(this.pos); 
      return getOverflowIterator().next();
    }
    
    public void remove() {
      if (!this.nextCalledBeforeRemove)
        throw new IllegalStateException("remove() was called before next()"); 
      this.nextCalledBeforeRemove = false;
      SmallSortedMap.this.checkMutable();
      if (this.pos < SmallSortedMap.access$600(SmallSortedMap.this).size()) {
        SmallSortedMap.this.removeArrayEntryAt(this.pos--);
      } else {
        getOverflowIterator().remove();
      } 
    }
    
    private Iterator<Map.Entry<K, V>> getOverflowIterator() {
      if (this.lazyOverflowIterator == null)
        this.lazyOverflowIterator = SmallSortedMap.this.overflowEntries.entrySet().iterator(); 
      return this.lazyOverflowIterator;
    }
    
    private EntryIterator() {}
  }
  
  private class DescendingEntryIterator implements Iterator<Map.Entry<K, V>> {
    private int pos = SmallSortedMap.access$600(SmallSortedMap.this).size();
    
    private Iterator<Map.Entry<K, V>> lazyOverflowIterator;
    
    public boolean hasNext() {
      return ((this.pos > 0 && this.pos <= SmallSortedMap.access$600(SmallSortedMap.this).size()) || getOverflowIterator().hasNext());
    }
    
    public Map.Entry<K, V> next() {
      if (getOverflowIterator().hasNext())
        return getOverflowIterator().next(); 
      return SmallSortedMap.access$600(SmallSortedMap.this).get(--this.pos);
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
    
    private Iterator<Map.Entry<K, V>> getOverflowIterator() {
      if (this.lazyOverflowIterator == null)
        this.lazyOverflowIterator = SmallSortedMap.this.overflowEntriesDescending.entrySet().iterator(); 
      return this.lazyOverflowIterator;
    }
    
    private DescendingEntryIterator() {}
  }
  
  private static class EmptySet {
    private static final Iterator<Object> ITERATOR = new Iterator() {
        public boolean hasNext() {
          return false;
        }
        
        public Object next() {
          throw new NoSuchElementException();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
    
    private static final Iterable<Object> ITERABLE = new Iterable() {
        public Iterator<Object> iterator() {
          return SmallSortedMap.EmptySet.ITERATOR;
        }
      };
    
    static <T> Iterable<T> iterable() {
      return (Iterable)ITERABLE;
    }
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (!(o instanceof SmallSortedMap))
      return super.equals(o); 
    SmallSortedMap<?, ?> other = (SmallSortedMap<?, ?>)o;
    int size = size();
    if (size != other.size())
      return false; 
    int numArrayEntries = getNumArrayEntries();
    if (numArrayEntries != other.getNumArrayEntries())
      return entrySet().equals(other.entrySet()); 
    for (int i = 0; i < numArrayEntries; i++) {
      if (!getArrayEntryAt(i).equals(other.getArrayEntryAt(i)))
        return false; 
    } 
    if (numArrayEntries != size)
      return this.overflowEntries.equals(other.overflowEntries); 
    return true;
  }
  
  public int hashCode() {
    int h = 0;
    int listSize = getNumArrayEntries();
    for (int i = 0; i < listSize; i++)
      h += ((Entry)this.entryList.get(i)).hashCode(); 
    if (getNumOverflowEntries() > 0)
      h += this.overflowEntries.hashCode(); 
    return h;
  }
}
