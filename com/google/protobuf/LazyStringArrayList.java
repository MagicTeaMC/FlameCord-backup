package com.google.protobuf;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

public class LazyStringArrayList extends AbstractProtobufList<String> implements LazyStringList, RandomAccess {
  private static final LazyStringArrayList EMPTY_LIST = new LazyStringArrayList();
  
  static {
    EMPTY_LIST.makeImmutable();
  }
  
  static LazyStringArrayList emptyList() {
    return EMPTY_LIST;
  }
  
  public static final LazyStringList EMPTY = EMPTY_LIST;
  
  private final List<Object> list;
  
  public LazyStringArrayList() {
    this(10);
  }
  
  public LazyStringArrayList(int initialCapacity) {
    this(new ArrayList(initialCapacity));
  }
  
  public LazyStringArrayList(LazyStringList from) {
    this.list = new ArrayList(from.size());
    addAll(from);
  }
  
  public LazyStringArrayList(List<String> from) {
    this(new ArrayList(from));
  }
  
  private LazyStringArrayList(ArrayList<Object> list) {
    this.list = list;
  }
  
  public LazyStringArrayList mutableCopyWithCapacity(int capacity) {
    if (capacity < size())
      throw new IllegalArgumentException(); 
    ArrayList<Object> newList = new ArrayList(capacity);
    newList.addAll(this.list);
    return new LazyStringArrayList(newList);
  }
  
  public String get(int index) {
    Object o = this.list.get(index);
    if (o instanceof String)
      return (String)o; 
    if (o instanceof ByteString) {
      ByteString bs = (ByteString)o;
      String str = bs.toStringUtf8();
      if (bs.isValidUtf8())
        this.list.set(index, str); 
      return str;
    } 
    byte[] ba = (byte[])o;
    String s = Internal.toStringUtf8(ba);
    if (Internal.isValidUtf8(ba))
      this.list.set(index, s); 
    return s;
  }
  
  public int size() {
    return this.list.size();
  }
  
  public String set(int index, String s) {
    ensureIsMutable();
    Object o = this.list.set(index, s);
    return asString(o);
  }
  
  public void add(int index, String element) {
    ensureIsMutable();
    this.list.add(index, element);
    this.modCount++;
  }
  
  private void add(int index, ByteString element) {
    ensureIsMutable();
    this.list.add(index, element);
    this.modCount++;
  }
  
  private void add(int index, byte[] element) {
    ensureIsMutable();
    this.list.add(index, element);
    this.modCount++;
  }
  
  public boolean addAll(Collection<? extends String> c) {
    return addAll(size(), c);
  }
  
  public boolean addAll(int index, Collection<? extends String> c) {
    ensureIsMutable();
    Collection<?> collection = (c instanceof LazyStringList) ? ((LazyStringList)c).getUnderlyingElements() : c;
    boolean ret = this.list.addAll(index, collection);
    this.modCount++;
    return ret;
  }
  
  public boolean addAllByteString(Collection<? extends ByteString> values) {
    ensureIsMutable();
    boolean ret = this.list.addAll(values);
    this.modCount++;
    return ret;
  }
  
  public boolean addAllByteArray(Collection<byte[]> c) {
    ensureIsMutable();
    boolean ret = this.list.addAll(c);
    this.modCount++;
    return ret;
  }
  
  public String remove(int index) {
    ensureIsMutable();
    Object o = this.list.remove(index);
    this.modCount++;
    return asString(o);
  }
  
  public void clear() {
    ensureIsMutable();
    this.list.clear();
    this.modCount++;
  }
  
  public void add(ByteString element) {
    ensureIsMutable();
    this.list.add(element);
    this.modCount++;
  }
  
  public void add(byte[] element) {
    ensureIsMutable();
    this.list.add(element);
    this.modCount++;
  }
  
  public Object getRaw(int index) {
    return this.list.get(index);
  }
  
  public ByteString getByteString(int index) {
    Object o = this.list.get(index);
    ByteString b = asByteString(o);
    if (b != o)
      this.list.set(index, b); 
    return b;
  }
  
  public byte[] getByteArray(int index) {
    Object o = this.list.get(index);
    byte[] b = asByteArray(o);
    if (b != o)
      this.list.set(index, b); 
    return b;
  }
  
  public void set(int index, ByteString s) {
    setAndReturn(index, s);
  }
  
  private Object setAndReturn(int index, ByteString s) {
    ensureIsMutable();
    return this.list.set(index, s);
  }
  
  public void set(int index, byte[] s) {
    setAndReturn(index, s);
  }
  
  private Object setAndReturn(int index, byte[] s) {
    ensureIsMutable();
    return this.list.set(index, s);
  }
  
  private static String asString(Object o) {
    if (o instanceof String)
      return (String)o; 
    if (o instanceof ByteString)
      return ((ByteString)o).toStringUtf8(); 
    return Internal.toStringUtf8((byte[])o);
  }
  
  private static ByteString asByteString(Object o) {
    if (o instanceof ByteString)
      return (ByteString)o; 
    if (o instanceof String)
      return ByteString.copyFromUtf8((String)o); 
    return ByteString.copyFrom((byte[])o);
  }
  
  private static byte[] asByteArray(Object o) {
    if (o instanceof byte[])
      return (byte[])o; 
    if (o instanceof String)
      return Internal.toByteArray((String)o); 
    return ((ByteString)o).toByteArray();
  }
  
  public List<?> getUnderlyingElements() {
    return Collections.unmodifiableList(this.list);
  }
  
  public void mergeFrom(LazyStringList other) {
    ensureIsMutable();
    for (Object o : other.getUnderlyingElements()) {
      if (o instanceof byte[]) {
        byte[] b = (byte[])o;
        this.list.add(Arrays.copyOf(b, b.length));
        continue;
      } 
      this.list.add(o);
    } 
  }
  
  private static class ByteArrayListView extends AbstractList<byte[]> implements RandomAccess {
    private final LazyStringArrayList list;
    
    ByteArrayListView(LazyStringArrayList list) {
      this.list = list;
    }
    
    public byte[] get(int index) {
      return this.list.getByteArray(index);
    }
    
    public int size() {
      return this.list.size();
    }
    
    public byte[] set(int index, byte[] s) {
      Object o = this.list.setAndReturn(index, s);
      this.modCount++;
      return LazyStringArrayList.asByteArray(o);
    }
    
    public void add(int index, byte[] s) {
      this.list.add(index, s);
      this.modCount++;
    }
    
    public byte[] remove(int index) {
      Object o = this.list.remove(index);
      this.modCount++;
      return LazyStringArrayList.asByteArray(o);
    }
  }
  
  public List<byte[]> asByteArrayList() {
    return new ByteArrayListView(this);
  }
  
  private static class ByteStringListView extends AbstractList<ByteString> implements RandomAccess {
    private final LazyStringArrayList list;
    
    ByteStringListView(LazyStringArrayList list) {
      this.list = list;
    }
    
    public ByteString get(int index) {
      return this.list.getByteString(index);
    }
    
    public int size() {
      return this.list.size();
    }
    
    public ByteString set(int index, ByteString s) {
      Object o = this.list.setAndReturn(index, s);
      this.modCount++;
      return LazyStringArrayList.asByteString(o);
    }
    
    public void add(int index, ByteString s) {
      this.list.add(index, s);
      this.modCount++;
    }
    
    public ByteString remove(int index) {
      Object o = this.list.remove(index);
      this.modCount++;
      return LazyStringArrayList.asByteString(o);
    }
  }
  
  public List<ByteString> asByteStringList() {
    return new ByteStringListView(this);
  }
  
  public LazyStringList getUnmodifiableView() {
    if (isModifiable())
      return new UnmodifiableLazyStringList(this); 
    return this;
  }
}
