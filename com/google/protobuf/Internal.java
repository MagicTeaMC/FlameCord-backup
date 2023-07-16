package com.google.protobuf;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;

public final class Internal {
  static final Charset US_ASCII = Charset.forName("US-ASCII");
  
  static final Charset UTF_8 = Charset.forName("UTF-8");
  
  static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
  
  private static final int DEFAULT_BUFFER_SIZE = 4096;
  
  static <T> T checkNotNull(T obj) {
    if (obj == null)
      throw new NullPointerException(); 
    return obj;
  }
  
  static <T> T checkNotNull(T obj, String message) {
    if (obj == null)
      throw new NullPointerException(message); 
    return obj;
  }
  
  public static String stringDefaultValue(String bytes) {
    return new String(bytes.getBytes(ISO_8859_1), UTF_8);
  }
  
  public static ByteString bytesDefaultValue(String bytes) {
    return ByteString.copyFrom(bytes.getBytes(ISO_8859_1));
  }
  
  public static byte[] byteArrayDefaultValue(String bytes) {
    return bytes.getBytes(ISO_8859_1);
  }
  
  public static ByteBuffer byteBufferDefaultValue(String bytes) {
    return ByteBuffer.wrap(byteArrayDefaultValue(bytes));
  }
  
  public static ByteBuffer copyByteBuffer(ByteBuffer source) {
    ByteBuffer temp = source.duplicate();
    temp.clear();
    ByteBuffer result = ByteBuffer.allocate(temp.capacity());
    result.put(temp);
    result.clear();
    return result;
  }
  
  public static boolean isValidUtf8(ByteString byteString) {
    return byteString.isValidUtf8();
  }
  
  public static boolean isValidUtf8(byte[] byteArray) {
    return Utf8.isValidUtf8(byteArray);
  }
  
  public static byte[] toByteArray(String value) {
    return value.getBytes(UTF_8);
  }
  
  public static String toStringUtf8(byte[] bytes) {
    return new String(bytes, UTF_8);
  }
  
  public static int hashLong(long n) {
    return (int)(n ^ n >>> 32L);
  }
  
  public static int hashBoolean(boolean b) {
    return b ? 1231 : 1237;
  }
  
  public static int hashEnum(EnumLite e) {
    return e.getNumber();
  }
  
  public static int hashEnumList(List<? extends EnumLite> list) {
    int hash = 1;
    for (EnumLite e : list)
      hash = 31 * hash + hashEnum(e); 
    return hash;
  }
  
  public static boolean equals(List<byte[]> a, List<byte[]> b) {
    if (a.size() != b.size())
      return false; 
    for (int i = 0; i < a.size(); i++) {
      if (!Arrays.equals(a.get(i), b.get(i)))
        return false; 
    } 
    return true;
  }
  
  public static int hashCode(List<byte[]> list) {
    int hash = 1;
    for (byte[] bytes : list)
      hash = 31 * hash + hashCode(bytes); 
    return hash;
  }
  
  public static int hashCode(byte[] bytes) {
    return hashCode(bytes, 0, bytes.length);
  }
  
  static int hashCode(byte[] bytes, int offset, int length) {
    int h = partialHash(length, bytes, offset, length);
    return (h == 0) ? 1 : h;
  }
  
  static int partialHash(int h, byte[] bytes, int offset, int length) {
    for (int i = offset; i < offset + length; i++)
      h = h * 31 + bytes[i]; 
    return h;
  }
  
  public static boolean equalsByteBuffer(ByteBuffer a, ByteBuffer b) {
    if (a.capacity() != b.capacity())
      return false; 
    return a.duplicate().clear().equals(b.duplicate().clear());
  }
  
  public static boolean equalsByteBuffer(List<ByteBuffer> a, List<ByteBuffer> b) {
    if (a.size() != b.size())
      return false; 
    for (int i = 0; i < a.size(); i++) {
      if (!equalsByteBuffer(a.get(i), b.get(i)))
        return false; 
    } 
    return true;
  }
  
  public static int hashCodeByteBuffer(List<ByteBuffer> list) {
    int hash = 1;
    for (ByteBuffer bytes : list)
      hash = 31 * hash + hashCodeByteBuffer(bytes); 
    return hash;
  }
  
  public static int hashCodeByteBuffer(ByteBuffer bytes) {
    if (bytes.hasArray()) {
      int i = partialHash(bytes.capacity(), bytes.array(), bytes.arrayOffset(), bytes.capacity());
      return (i == 0) ? 1 : i;
    } 
    int bufferSize = (bytes.capacity() > 4096) ? 4096 : bytes.capacity();
    byte[] buffer = new byte[bufferSize];
    ByteBuffer duplicated = bytes.duplicate();
    duplicated.clear();
    int h = bytes.capacity();
    while (duplicated.remaining() > 0) {
      int length = (duplicated.remaining() <= bufferSize) ? duplicated.remaining() : bufferSize;
      duplicated.get(buffer, 0, length);
      h = partialHash(h, buffer, 0, length);
    } 
    return (h == 0) ? 1 : h;
  }
  
  public static <T extends MessageLite> T getDefaultInstance(Class<T> clazz) {
    try {
      Method method = clazz.getMethod("getDefaultInstance", new Class[0]);
      return (T)method.invoke(method, new Object[0]);
    } catch (Exception e) {
      throw new RuntimeException("Failed to get default instance for " + clazz, e);
    } 
  }
  
  public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
  
  public static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.wrap(EMPTY_BYTE_ARRAY);
  
  public static final CodedInputStream EMPTY_CODED_INPUT_STREAM = CodedInputStream.newInstance(EMPTY_BYTE_ARRAY);
  
  static Object mergeMessage(Object destination, Object source) {
    return ((MessageLite)destination).toBuilder().mergeFrom((MessageLite)source).buildPartial();
  }
  
  public static interface EnumLite {
    int getNumber();
  }
  
  public static interface EnumLiteMap<T extends EnumLite> {
    T findValueByNumber(int param1Int);
  }
  
  public static interface EnumVerifier {
    boolean isInRange(int param1Int);
  }
  
  public static class ListAdapter<F, T> extends AbstractList<T> {
    private final List<F> fromList;
    
    private final Converter<F, T> converter;
    
    public ListAdapter(List<F> fromList, Converter<F, T> converter) {
      this.fromList = fromList;
      this.converter = converter;
    }
    
    public T get(int index) {
      return this.converter.convert(this.fromList.get(index));
    }
    
    public int size() {
      return this.fromList.size();
    }
    
    public static interface Converter<F, T> {
      T convert(F param2F);
    }
  }
  
  public static class MapAdapter<K, V, RealValue> extends AbstractMap<K, V> {
    private final Map<K, RealValue> realMap;
    
    private final Converter<RealValue, V> valueConverter;
    
    public static <T extends Internal.EnumLite> Converter<Integer, T> newEnumConverter(final Internal.EnumLiteMap<T> enumMap, final T unrecognizedValue) {
      return new Converter<Integer, T>() {
          public T doForward(Integer value) {
            T result = (T)enumMap.findValueByNumber(value.intValue());
            return (result == null) ? (T)unrecognizedValue : result;
          }
          
          public Integer doBackward(T value) {
            return Integer.valueOf(value.getNumber());
          }
        };
    }
    
    public MapAdapter(Map<K, RealValue> realMap, Converter<RealValue, V> valueConverter) {
      this.realMap = realMap;
      this.valueConverter = valueConverter;
    }
    
    public V get(Object key) {
      RealValue result = this.realMap.get(key);
      if (result == null)
        return null; 
      return this.valueConverter.doForward(result);
    }
    
    public V put(K key, V value) {
      RealValue oldValue = this.realMap.put(key, this.valueConverter.doBackward(value));
      if (oldValue == null)
        return null; 
      return this.valueConverter.doForward(oldValue);
    }
    
    public Set<Map.Entry<K, V>> entrySet() {
      return new SetAdapter(this.realMap.entrySet());
    }
    
    public static interface Converter<A, B> {
      B doForward(A param2A);
      
      A doBackward(B param2B);
    }
    
    private class SetAdapter extends AbstractSet<Map.Entry<K, V>> {
      private final Set<Map.Entry<K, RealValue>> realSet;
      
      public SetAdapter(Set<Map.Entry<K, RealValue>> realSet) {
        this.realSet = realSet;
      }
      
      public Iterator<Map.Entry<K, V>> iterator() {
        return new Internal.MapAdapter.IteratorAdapter(this.realSet.iterator());
      }
      
      public int size() {
        return this.realSet.size();
      }
    }
    
    private class IteratorAdapter implements Iterator<Map.Entry<K, V>> {
      private final Iterator<Map.Entry<K, RealValue>> realIterator;
      
      public IteratorAdapter(Iterator<Map.Entry<K, RealValue>> realIterator) {
        this.realIterator = realIterator;
      }
      
      public boolean hasNext() {
        return this.realIterator.hasNext();
      }
      
      public Map.Entry<K, V> next() {
        return new Internal.MapAdapter.EntryAdapter(this.realIterator.next());
      }
      
      public void remove() {
        this.realIterator.remove();
      }
    }
    
    private class EntryAdapter implements Map.Entry<K, V> {
      private final Map.Entry<K, RealValue> realEntry;
      
      public EntryAdapter(Map.Entry<K, RealValue> realEntry) {
        this.realEntry = realEntry;
      }
      
      public K getKey() {
        return this.realEntry.getKey();
      }
      
      public V getValue() {
        return (V)Internal.MapAdapter.this.valueConverter.doForward(this.realEntry.getValue());
      }
      
      public V setValue(V value) {
        RealValue oldValue = this.realEntry.setValue((RealValue)Internal.MapAdapter.this.valueConverter.doBackward(value));
        if (oldValue == null)
          return null; 
        return (V)Internal.MapAdapter.this.valueConverter.doForward(oldValue);
      }
      
      public boolean equals(Object o) {
        if (o == this)
          return true; 
        if (!(o instanceof Map.Entry))
          return false; 
        Map.Entry<?, ?> other = (Map.Entry<?, ?>)o;
        return (getKey().equals(other.getKey()) && getValue().equals(getValue()));
      }
      
      public int hashCode() {
        return this.realEntry.hashCode();
      }
    }
  }
  
  public static interface ProtobufList<E> extends List<E>, RandomAccess {
    void makeImmutable();
    
    boolean isModifiable();
    
    ProtobufList<E> mutableCopyWithCapacity(int param1Int);
  }
  
  public static interface IntList extends ProtobufList<Integer> {
    int getInt(int param1Int);
    
    void addInt(int param1Int);
    
    int setInt(int param1Int1, int param1Int2);
    
    IntList mutableCopyWithCapacity(int param1Int);
  }
  
  public static interface BooleanList extends ProtobufList<Boolean> {
    boolean getBoolean(int param1Int);
    
    void addBoolean(boolean param1Boolean);
    
    boolean setBoolean(int param1Int, boolean param1Boolean);
    
    BooleanList mutableCopyWithCapacity(int param1Int);
  }
  
  public static interface LongList extends ProtobufList<Long> {
    long getLong(int param1Int);
    
    void addLong(long param1Long);
    
    long setLong(int param1Int, long param1Long);
    
    LongList mutableCopyWithCapacity(int param1Int);
  }
  
  public static interface DoubleList extends ProtobufList<Double> {
    double getDouble(int param1Int);
    
    void addDouble(double param1Double);
    
    double setDouble(int param1Int, double param1Double);
    
    DoubleList mutableCopyWithCapacity(int param1Int);
  }
  
  public static interface FloatList extends ProtobufList<Float> {
    float getFloat(int param1Int);
    
    void addFloat(float param1Float);
    
    float setFloat(int param1Int, float param1Float);
    
    FloatList mutableCopyWithCapacity(int param1Int);
  }
}
