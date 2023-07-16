package com.google.protobuf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapField<K, V> implements MutabilityOracle {
  private volatile boolean isMutable;
  
  private volatile StorageMode mode;
  
  private MutabilityAwareMap<K, V> mapData;
  
  private List<Message> listData;
  
  private final Converter<K, V> converter;
  
  private enum StorageMode {
    MAP, LIST, BOTH;
  }
  
  private static interface Converter<K, V> {
    Message convertKeyAndValueToMessage(K param1K, V param1V);
    
    void convertMessageToKeyAndValue(Message param1Message, Map<K, V> param1Map);
    
    Message getMessageDefaultInstance();
  }
  
  private static class ImmutableMessageConverter<K, V> implements Converter<K, V> {
    private final MapEntry<K, V> defaultEntry;
    
    public ImmutableMessageConverter(MapEntry<K, V> defaultEntry) {
      this.defaultEntry = defaultEntry;
    }
    
    public Message convertKeyAndValueToMessage(K key, V value) {
      return this.defaultEntry.newBuilderForType().setKey(key).setValue(value).buildPartial();
    }
    
    public void convertMessageToKeyAndValue(Message message, Map<K, V> map) {
      MapEntry<K, V> entry = (MapEntry<K, V>)message;
      map.put(entry.getKey(), entry.getValue());
    }
    
    public Message getMessageDefaultInstance() {
      return this.defaultEntry;
    }
  }
  
  private MapField(Converter<K, V> converter, StorageMode mode, Map<K, V> mapData) {
    this.converter = converter;
    this.isMutable = true;
    this.mode = mode;
    this.mapData = new MutabilityAwareMap<>(this, mapData);
    this.listData = null;
  }
  
  private MapField(MapEntry<K, V> defaultEntry, StorageMode mode, Map<K, V> mapData) {
    this(new ImmutableMessageConverter<>(defaultEntry), mode, mapData);
  }
  
  public static <K, V> MapField<K, V> emptyMapField(MapEntry<K, V> defaultEntry) {
    return new MapField<>(defaultEntry, StorageMode.MAP, Collections.emptyMap());
  }
  
  public static <K, V> MapField<K, V> newMapField(MapEntry<K, V> defaultEntry) {
    return new MapField<>(defaultEntry, StorageMode.MAP, new LinkedHashMap<>());
  }
  
  private Message convertKeyAndValueToMessage(K key, V value) {
    return this.converter.convertKeyAndValueToMessage(key, value);
  }
  
  private void convertMessageToKeyAndValue(Message message, Map<K, V> map) {
    this.converter.convertMessageToKeyAndValue(message, map);
  }
  
  private List<Message> convertMapToList(MutabilityAwareMap<K, V> mapData) {
    List<Message> listData = new ArrayList<>();
    for (Map.Entry<K, V> entry : mapData.entrySet())
      listData.add(convertKeyAndValueToMessage(entry.getKey(), entry.getValue())); 
    return listData;
  }
  
  private MutabilityAwareMap<K, V> convertListToMap(List<Message> listData) {
    Map<K, V> mapData = new LinkedHashMap<>();
    for (Message item : listData)
      convertMessageToKeyAndValue(item, mapData); 
    return new MutabilityAwareMap<>(this, mapData);
  }
  
  public Map<K, V> getMap() {
    if (this.mode == StorageMode.LIST)
      synchronized (this) {
        if (this.mode == StorageMode.LIST) {
          this.mapData = convertListToMap(this.listData);
          this.mode = StorageMode.BOTH;
        } 
      }  
    return Collections.unmodifiableMap(this.mapData);
  }
  
  public Map<K, V> getMutableMap() {
    if (this.mode != StorageMode.MAP) {
      if (this.mode == StorageMode.LIST)
        this.mapData = convertListToMap(this.listData); 
      this.listData = null;
      this.mode = StorageMode.MAP;
    } 
    return this.mapData;
  }
  
  public void mergeFrom(MapField<K, V> other) {
    getMutableMap().putAll(MapFieldLite.copy(other.getMap()));
  }
  
  public void clear() {
    this.mapData = new MutabilityAwareMap<>(this, new LinkedHashMap<>());
    this.mode = StorageMode.MAP;
  }
  
  public boolean equals(Object object) {
    if (!(object instanceof MapField))
      return false; 
    MapField<K, V> other = (MapField<K, V>)object;
    return MapFieldLite.equals(getMap(), other.getMap());
  }
  
  public int hashCode() {
    return MapFieldLite.calculateHashCodeForMap(getMap());
  }
  
  public MapField<K, V> copy() {
    return new MapField(this.converter, StorageMode.MAP, MapFieldLite.copy(getMap()));
  }
  
  List<Message> getList() {
    if (this.mode == StorageMode.MAP)
      synchronized (this) {
        if (this.mode == StorageMode.MAP) {
          this.listData = convertMapToList(this.mapData);
          this.mode = StorageMode.BOTH;
        } 
      }  
    return Collections.unmodifiableList(this.listData);
  }
  
  List<Message> getMutableList() {
    if (this.mode != StorageMode.LIST) {
      if (this.mode == StorageMode.MAP)
        this.listData = convertMapToList(this.mapData); 
      this.mapData = null;
      this.mode = StorageMode.LIST;
    } 
    return this.listData;
  }
  
  Message getMapEntryMessageDefaultInstance() {
    return this.converter.getMessageDefaultInstance();
  }
  
  public void makeImmutable() {
    this.isMutable = false;
  }
  
  public boolean isMutable() {
    return this.isMutable;
  }
  
  public void ensureMutable() {
    if (!isMutable())
      throw new UnsupportedOperationException(); 
  }
  
  private static class MutabilityAwareMap<K, V> implements Map<K, V> {
    private final MutabilityOracle mutabilityOracle;
    
    private final Map<K, V> delegate;
    
    MutabilityAwareMap(MutabilityOracle mutabilityOracle, Map<K, V> delegate) {
      this.mutabilityOracle = mutabilityOracle;
      this.delegate = delegate;
    }
    
    public int size() {
      return this.delegate.size();
    }
    
    public boolean isEmpty() {
      return this.delegate.isEmpty();
    }
    
    public boolean containsKey(Object key) {
      return this.delegate.containsKey(key);
    }
    
    public boolean containsValue(Object value) {
      return this.delegate.containsValue(value);
    }
    
    public V get(Object key) {
      return this.delegate.get(key);
    }
    
    public V put(K key, V value) {
      this.mutabilityOracle.ensureMutable();
      Internal.checkNotNull(key);
      Internal.checkNotNull(value);
      return this.delegate.put(key, value);
    }
    
    public V remove(Object key) {
      this.mutabilityOracle.ensureMutable();
      return this.delegate.remove(key);
    }
    
    public void putAll(Map<? extends K, ? extends V> m) {
      this.mutabilityOracle.ensureMutable();
      for (K key : m.keySet()) {
        Internal.checkNotNull(key);
        Internal.checkNotNull(m.get(key));
      } 
      this.delegate.putAll(m);
    }
    
    public void clear() {
      this.mutabilityOracle.ensureMutable();
      this.delegate.clear();
    }
    
    public Set<K> keySet() {
      return new MutabilityAwareSet<>(this.mutabilityOracle, this.delegate.keySet());
    }
    
    public Collection<V> values() {
      return new MutabilityAwareCollection<>(this.mutabilityOracle, this.delegate.values());
    }
    
    public Set<Map.Entry<K, V>> entrySet() {
      return new MutabilityAwareSet<>(this.mutabilityOracle, this.delegate.entrySet());
    }
    
    public boolean equals(Object o) {
      return this.delegate.equals(o);
    }
    
    public int hashCode() {
      return this.delegate.hashCode();
    }
    
    public String toString() {
      return this.delegate.toString();
    }
    
    private static class MutabilityAwareCollection<E> implements Collection<E> {
      private final MutabilityOracle mutabilityOracle;
      
      private final Collection<E> delegate;
      
      MutabilityAwareCollection(MutabilityOracle mutabilityOracle, Collection<E> delegate) {
        this.mutabilityOracle = mutabilityOracle;
        this.delegate = delegate;
      }
      
      public int size() {
        return this.delegate.size();
      }
      
      public boolean isEmpty() {
        return this.delegate.isEmpty();
      }
      
      public boolean contains(Object o) {
        return this.delegate.contains(o);
      }
      
      public Iterator<E> iterator() {
        return new MapField.MutabilityAwareMap.MutabilityAwareIterator<>(this.mutabilityOracle, this.delegate.iterator());
      }
      
      public Object[] toArray() {
        return this.delegate.toArray();
      }
      
      public <T> T[] toArray(T[] a) {
        return this.delegate.toArray(a);
      }
      
      public boolean add(E e) {
        throw new UnsupportedOperationException();
      }
      
      public boolean remove(Object o) {
        this.mutabilityOracle.ensureMutable();
        return this.delegate.remove(o);
      }
      
      public boolean containsAll(Collection<?> c) {
        return this.delegate.containsAll(c);
      }
      
      public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
      }
      
      public boolean removeAll(Collection<?> c) {
        this.mutabilityOracle.ensureMutable();
        return this.delegate.removeAll(c);
      }
      
      public boolean retainAll(Collection<?> c) {
        this.mutabilityOracle.ensureMutable();
        return this.delegate.retainAll(c);
      }
      
      public void clear() {
        this.mutabilityOracle.ensureMutable();
        this.delegate.clear();
      }
      
      public boolean equals(Object o) {
        return this.delegate.equals(o);
      }
      
      public int hashCode() {
        return this.delegate.hashCode();
      }
      
      public String toString() {
        return this.delegate.toString();
      }
    }
    
    private static class MutabilityAwareSet<E> implements Set<E> {
      private final MutabilityOracle mutabilityOracle;
      
      private final Set<E> delegate;
      
      MutabilityAwareSet(MutabilityOracle mutabilityOracle, Set<E> delegate) {
        this.mutabilityOracle = mutabilityOracle;
        this.delegate = delegate;
      }
      
      public int size() {
        return this.delegate.size();
      }
      
      public boolean isEmpty() {
        return this.delegate.isEmpty();
      }
      
      public boolean contains(Object o) {
        return this.delegate.contains(o);
      }
      
      public Iterator<E> iterator() {
        return new MapField.MutabilityAwareMap.MutabilityAwareIterator<>(this.mutabilityOracle, this.delegate.iterator());
      }
      
      public Object[] toArray() {
        return this.delegate.toArray();
      }
      
      public <T> T[] toArray(T[] a) {
        return this.delegate.toArray(a);
      }
      
      public boolean add(E e) {
        this.mutabilityOracle.ensureMutable();
        return this.delegate.add(e);
      }
      
      public boolean remove(Object o) {
        this.mutabilityOracle.ensureMutable();
        return this.delegate.remove(o);
      }
      
      public boolean containsAll(Collection<?> c) {
        return this.delegate.containsAll(c);
      }
      
      public boolean addAll(Collection<? extends E> c) {
        this.mutabilityOracle.ensureMutable();
        return this.delegate.addAll(c);
      }
      
      public boolean retainAll(Collection<?> c) {
        this.mutabilityOracle.ensureMutable();
        return this.delegate.retainAll(c);
      }
      
      public boolean removeAll(Collection<?> c) {
        this.mutabilityOracle.ensureMutable();
        return this.delegate.removeAll(c);
      }
      
      public void clear() {
        this.mutabilityOracle.ensureMutable();
        this.delegate.clear();
      }
      
      public boolean equals(Object o) {
        return this.delegate.equals(o);
      }
      
      public int hashCode() {
        return this.delegate.hashCode();
      }
      
      public String toString() {
        return this.delegate.toString();
      }
    }
    
    private static class MutabilityAwareIterator<E> implements Iterator<E> {
      private final MutabilityOracle mutabilityOracle;
      
      private final Iterator<E> delegate;
      
      MutabilityAwareIterator(MutabilityOracle mutabilityOracle, Iterator<E> delegate) {
        this.mutabilityOracle = mutabilityOracle;
        this.delegate = delegate;
      }
      
      public boolean hasNext() {
        return this.delegate.hasNext();
      }
      
      public E next() {
        return this.delegate.next();
      }
      
      public void remove() {
        this.mutabilityOracle.ensureMutable();
        this.delegate.remove();
      }
      
      public boolean equals(Object obj) {
        return this.delegate.equals(obj);
      }
      
      public int hashCode() {
        return this.delegate.hashCode();
      }
      
      public String toString() {
        return this.delegate.toString();
      }
    }
  }
}
