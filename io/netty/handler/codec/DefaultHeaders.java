package io.netty.handler.codec;

import io.netty.util.HashingStrategy;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class DefaultHeaders<K, V, T extends Headers<K, V, T>> implements Headers<K, V, T> {
  static final int HASH_CODE_SEED = -1028477387;
  
  private final HeaderEntry<K, V>[] entries;
  
  protected final HeaderEntry<K, V> head;
  
  private final byte hashMask;
  
  private final ValueConverter<V> valueConverter;
  
  private final NameValidator<K> nameValidator;
  
  private final ValueValidator<V> valueValidator;
  
  private final HashingStrategy<K> hashingStrategy;
  
  int size;
  
  public static interface NameValidator<K> {
    public static final NameValidator NOT_NULL = new NameValidator() {
        public void validateName(Object name) {
          ObjectUtil.checkNotNull(name, "name");
        }
      };
    
    void validateName(K param1K);
  }
  
  public static interface ValueValidator<V> {
    public static final ValueValidator<?> NO_VALIDATION = new ValueValidator<Object>() {
        public void validate(Object value) {}
      };
    
    void validate(V param1V);
  }
  
  public DefaultHeaders(ValueConverter<V> valueConverter) {
    this(HashingStrategy.JAVA_HASHER, valueConverter);
  }
  
  public DefaultHeaders(ValueConverter<V> valueConverter, NameValidator<K> nameValidator) {
    this(HashingStrategy.JAVA_HASHER, valueConverter, nameValidator);
  }
  
  public DefaultHeaders(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter) {
    this(nameHashingStrategy, valueConverter, NameValidator.NOT_NULL);
  }
  
  public DefaultHeaders(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter, NameValidator<K> nameValidator) {
    this(nameHashingStrategy, valueConverter, nameValidator, 16);
  }
  
  public DefaultHeaders(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter, NameValidator<K> nameValidator, int arraySizeHint) {
    this(nameHashingStrategy, valueConverter, nameValidator, arraySizeHint, (ValueValidator)ValueValidator.NO_VALIDATION);
  }
  
  public DefaultHeaders(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter, NameValidator<K> nameValidator, int arraySizeHint, ValueValidator<V> valueValidator) {
    this.valueConverter = (ValueConverter<V>)ObjectUtil.checkNotNull(valueConverter, "valueConverter");
    this.nameValidator = (NameValidator<K>)ObjectUtil.checkNotNull(nameValidator, "nameValidator");
    this.hashingStrategy = (HashingStrategy<K>)ObjectUtil.checkNotNull(nameHashingStrategy, "nameHashingStrategy");
    this.valueValidator = (ValueValidator<V>)ObjectUtil.checkNotNull(valueValidator, "valueValidator");
    this.entries = (HeaderEntry<K, V>[])new HeaderEntry[MathUtil.findNextPositivePowerOfTwo(Math.max(2, Math.min(arraySizeHint, 128)))];
    this.hashMask = (byte)(this.entries.length - 1);
    this.head = new HeaderEntry<K, V>();
  }
  
  public V get(K name) {
    ObjectUtil.checkNotNull(name, "name");
    int h = this.hashingStrategy.hashCode(name);
    int i = index(h);
    HeaderEntry<K, V> e = this.entries[i];
    V value = null;
    while (e != null) {
      if (e.hash == h && this.hashingStrategy.equals(name, e.key))
        value = e.value; 
      e = e.next;
    } 
    return value;
  }
  
  public V get(K name, V defaultValue) {
    V value = get(name);
    if (value == null)
      return defaultValue; 
    return value;
  }
  
  public V getAndRemove(K name) {
    int h = this.hashingStrategy.hashCode(name);
    return remove0(h, index(h), (K)ObjectUtil.checkNotNull(name, "name"));
  }
  
  public V getAndRemove(K name, V defaultValue) {
    V value = getAndRemove(name);
    if (value == null)
      return defaultValue; 
    return value;
  }
  
  public List<V> getAll(K name) {
    ObjectUtil.checkNotNull(name, "name");
    LinkedList<V> values = new LinkedList<V>();
    int h = this.hashingStrategy.hashCode(name);
    int i = index(h);
    HeaderEntry<K, V> e = this.entries[i];
    while (e != null) {
      if (e.hash == h && this.hashingStrategy.equals(name, e.key))
        values.addFirst(e.getValue()); 
      e = e.next;
    } 
    return values;
  }
  
  public Iterator<V> valueIterator(K name) {
    return new ValueIterator(name);
  }
  
  public List<V> getAllAndRemove(K name) {
    List<V> all = getAll(name);
    remove(name);
    return all;
  }
  
  public boolean contains(K name) {
    return (get(name) != null);
  }
  
  public boolean containsObject(K name, Object value) {
    return contains(name, fromObject(name, value));
  }
  
  public boolean containsBoolean(K name, boolean value) {
    return contains(name, fromBoolean(name, value));
  }
  
  public boolean containsByte(K name, byte value) {
    return contains(name, fromByte(name, value));
  }
  
  public boolean containsChar(K name, char value) {
    return contains(name, fromChar(name, value));
  }
  
  public boolean containsShort(K name, short value) {
    return contains(name, fromShort(name, value));
  }
  
  public boolean containsInt(K name, int value) {
    return contains(name, fromInt(name, value));
  }
  
  public boolean containsLong(K name, long value) {
    return contains(name, fromLong(name, value));
  }
  
  public boolean containsFloat(K name, float value) {
    return contains(name, fromFloat(name, value));
  }
  
  public boolean containsDouble(K name, double value) {
    return contains(name, fromDouble(name, value));
  }
  
  public boolean containsTimeMillis(K name, long value) {
    return contains(name, fromTimeMillis(name, value));
  }
  
  public boolean contains(K name, V value) {
    return contains(name, value, HashingStrategy.JAVA_HASHER);
  }
  
  public final boolean contains(K name, V value, HashingStrategy<? super V> valueHashingStrategy) {
    ObjectUtil.checkNotNull(name, "name");
    int h = this.hashingStrategy.hashCode(name);
    int i = index(h);
    HeaderEntry<K, V> e = this.entries[i];
    while (e != null) {
      if (e.hash == h && this.hashingStrategy.equals(name, e.key) && valueHashingStrategy.equals(value, e.value))
        return true; 
      e = e.next;
    } 
    return false;
  }
  
  public int size() {
    return this.size;
  }
  
  public boolean isEmpty() {
    return (this.head == this.head.after);
  }
  
  public Set<K> names() {
    if (isEmpty())
      return Collections.emptySet(); 
    Set<K> names = new LinkedHashSet<K>(size());
    HeaderEntry<K, V> e = this.head.after;
    while (e != this.head) {
      names.add(e.getKey());
      e = e.after;
    } 
    return names;
  }
  
  public T add(K name, V value) {
    validateName(this.nameValidator, true, name);
    validateValue(this.valueValidator, name, value);
    ObjectUtil.checkNotNull(value, "value");
    int h = this.hashingStrategy.hashCode(name);
    int i = index(h);
    add0(h, i, name, value);
    return thisT();
  }
  
  public T add(K name, Iterable<? extends V> values) {
    validateName(this.nameValidator, true, name);
    int h = this.hashingStrategy.hashCode(name);
    int i = index(h);
    for (V v : values) {
      validateValue(this.valueValidator, name, v);
      add0(h, i, name, v);
    } 
    return thisT();
  }
  
  public T add(K name, V... values) {
    validateName(this.nameValidator, true, name);
    int h = this.hashingStrategy.hashCode(name);
    int i = index(h);
    for (V v : values) {
      validateValue(this.valueValidator, name, v);
      add0(h, i, name, v);
    } 
    return thisT();
  }
  
  public T addObject(K name, Object value) {
    return add(name, fromObject(name, value));
  }
  
  public T addObject(K name, Iterable<?> values) {
    for (Object value : values)
      addObject(name, value); 
    return thisT();
  }
  
  public T addObject(K name, Object... values) {
    for (Object value : values)
      addObject(name, value); 
    return thisT();
  }
  
  public T addInt(K name, int value) {
    return add(name, fromInt(name, value));
  }
  
  public T addLong(K name, long value) {
    return add(name, fromLong(name, value));
  }
  
  public T addDouble(K name, double value) {
    return add(name, fromDouble(name, value));
  }
  
  public T addTimeMillis(K name, long value) {
    return add(name, fromTimeMillis(name, value));
  }
  
  public T addChar(K name, char value) {
    return add(name, fromChar(name, value));
  }
  
  public T addBoolean(K name, boolean value) {
    return add(name, fromBoolean(name, value));
  }
  
  public T addFloat(K name, float value) {
    return add(name, fromFloat(name, value));
  }
  
  public T addByte(K name, byte value) {
    return add(name, fromByte(name, value));
  }
  
  public T addShort(K name, short value) {
    return add(name, fromShort(name, value));
  }
  
  public T add(Headers<? extends K, ? extends V, ?> headers) {
    if (headers == this)
      throw new IllegalArgumentException("can't add to itself."); 
    addImpl(headers);
    return thisT();
  }
  
  protected void addImpl(Headers<? extends K, ? extends V, ?> headers) {
    if (headers instanceof DefaultHeaders) {
      DefaultHeaders<? extends K, ? extends V, T> defaultHeaders = (DefaultHeaders)headers;
      HeaderEntry<? extends K, ? extends V> e = defaultHeaders.head.after;
      if (defaultHeaders.hashingStrategy == this.hashingStrategy && defaultHeaders.nameValidator == this.nameValidator) {
        while (e != defaultHeaders.head) {
          add0(e.hash, index(e.hash), e.key, e.value);
          e = e.after;
        } 
      } else {
        while (e != defaultHeaders.head) {
          add(e.key, e.value);
          e = e.after;
        } 
      } 
    } else {
      for (Map.Entry<? extends K, ? extends V> header : headers)
        add(header.getKey(), header.getValue()); 
    } 
  }
  
  public T set(K name, V value) {
    validateName(this.nameValidator, false, name);
    validateValue(this.valueValidator, name, value);
    ObjectUtil.checkNotNull(value, "value");
    int h = this.hashingStrategy.hashCode(name);
    int i = index(h);
    remove0(h, i, name);
    add0(h, i, name, value);
    return thisT();
  }
  
  public T set(K name, Iterable<? extends V> values) {
    validateName(this.nameValidator, false, name);
    ObjectUtil.checkNotNull(values, "values");
    int h = this.hashingStrategy.hashCode(name);
    int i = index(h);
    remove0(h, i, name);
    for (V v : values) {
      if (v == null)
        break; 
      validateValue(this.valueValidator, name, v);
      add0(h, i, name, v);
    } 
    return thisT();
  }
  
  public T set(K name, V... values) {
    validateName(this.nameValidator, false, name);
    ObjectUtil.checkNotNull(values, "values");
    int h = this.hashingStrategy.hashCode(name);
    int i = index(h);
    remove0(h, i, name);
    for (V v : values) {
      if (v == null)
        break; 
      validateValue(this.valueValidator, name, v);
      add0(h, i, name, v);
    } 
    return thisT();
  }
  
  public T setObject(K name, Object value) {
    V convertedValue = (V)ObjectUtil.checkNotNull(fromObject(name, value), "convertedValue");
    return set(name, convertedValue);
  }
  
  public T setObject(K name, Iterable<?> values) {
    validateName(this.nameValidator, false, name);
    int h = this.hashingStrategy.hashCode(name);
    int i = index(h);
    remove0(h, i, name);
    for (Object v : values) {
      if (v == null)
        break; 
      V converted = fromObject(name, v);
      validateValue(this.valueValidator, name, converted);
      add0(h, i, name, converted);
    } 
    return thisT();
  }
  
  public T setObject(K name, Object... values) {
    validateName(this.nameValidator, false, name);
    int h = this.hashingStrategy.hashCode(name);
    int i = index(h);
    remove0(h, i, name);
    for (Object v : values) {
      if (v == null)
        break; 
      V converted = fromObject(name, v);
      validateValue(this.valueValidator, name, converted);
      add0(h, i, name, converted);
    } 
    return thisT();
  }
  
  public T setInt(K name, int value) {
    return set(name, fromInt(name, value));
  }
  
  public T setLong(K name, long value) {
    return set(name, fromLong(name, value));
  }
  
  public T setDouble(K name, double value) {
    return set(name, fromDouble(name, value));
  }
  
  public T setTimeMillis(K name, long value) {
    return set(name, fromTimeMillis(name, value));
  }
  
  public T setFloat(K name, float value) {
    return set(name, fromFloat(name, value));
  }
  
  public T setChar(K name, char value) {
    return set(name, fromChar(name, value));
  }
  
  public T setBoolean(K name, boolean value) {
    return set(name, fromBoolean(name, value));
  }
  
  public T setByte(K name, byte value) {
    return set(name, fromByte(name, value));
  }
  
  public T setShort(K name, short value) {
    return set(name, fromShort(name, value));
  }
  
  public T set(Headers<? extends K, ? extends V, ?> headers) {
    if (headers != this) {
      clear();
      addImpl(headers);
    } 
    return thisT();
  }
  
  public T setAll(Headers<? extends K, ? extends V, ?> headers) {
    if (headers != this) {
      for (K key : headers.names())
        remove(key); 
      addImpl(headers);
    } 
    return thisT();
  }
  
  public boolean remove(K name) {
    return (getAndRemove(name) != null);
  }
  
  public T clear() {
    Arrays.fill((Object[])this.entries, (Object)null);
    this.head.before = this.head.after = this.head;
    this.size = 0;
    return thisT();
  }
  
  public Iterator<Map.Entry<K, V>> iterator() {
    return new HeaderIterator();
  }
  
  public Boolean getBoolean(K name) {
    V v = get(name);
    try {
      return (v != null) ? Boolean.valueOf(toBoolean(name, v)) : null;
    } catch (RuntimeException ignore) {
      return null;
    } 
  }
  
  public boolean getBoolean(K name, boolean defaultValue) {
    Boolean v = getBoolean(name);
    return (v != null) ? v.booleanValue() : defaultValue;
  }
  
  public Byte getByte(K name) {
    V v = get(name);
    try {
      return (v != null) ? Byte.valueOf(toByte(name, v)) : null;
    } catch (RuntimeException ignore) {
      return null;
    } 
  }
  
  public byte getByte(K name, byte defaultValue) {
    Byte v = getByte(name);
    return (v != null) ? v.byteValue() : defaultValue;
  }
  
  public Character getChar(K name) {
    V v = get(name);
    try {
      return (v != null) ? Character.valueOf(toChar(name, v)) : null;
    } catch (RuntimeException ignore) {
      return null;
    } 
  }
  
  public char getChar(K name, char defaultValue) {
    Character v = getChar(name);
    return (v != null) ? v.charValue() : defaultValue;
  }
  
  public Short getShort(K name) {
    V v = get(name);
    try {
      return (v != null) ? Short.valueOf(toShort(name, v)) : null;
    } catch (RuntimeException ignore) {
      return null;
    } 
  }
  
  public short getShort(K name, short defaultValue) {
    Short v = getShort(name);
    return (v != null) ? v.shortValue() : defaultValue;
  }
  
  public Integer getInt(K name) {
    V v = get(name);
    try {
      return (v != null) ? Integer.valueOf(toInt(name, v)) : null;
    } catch (RuntimeException ignore) {
      return null;
    } 
  }
  
  public int getInt(K name, int defaultValue) {
    Integer v = getInt(name);
    return (v != null) ? v.intValue() : defaultValue;
  }
  
  public Long getLong(K name) {
    V v = get(name);
    try {
      return (v != null) ? Long.valueOf(toLong(name, v)) : null;
    } catch (RuntimeException ignore) {
      return null;
    } 
  }
  
  public long getLong(K name, long defaultValue) {
    Long v = getLong(name);
    return (v != null) ? v.longValue() : defaultValue;
  }
  
  public Float getFloat(K name) {
    V v = get(name);
    try {
      return (v != null) ? Float.valueOf(toFloat(name, v)) : null;
    } catch (RuntimeException ignore) {
      return null;
    } 
  }
  
  public float getFloat(K name, float defaultValue) {
    Float v = getFloat(name);
    return (v != null) ? v.floatValue() : defaultValue;
  }
  
  public Double getDouble(K name) {
    V v = get(name);
    try {
      return (v != null) ? Double.valueOf(toDouble(name, v)) : null;
    } catch (RuntimeException ignore) {
      return null;
    } 
  }
  
  public double getDouble(K name, double defaultValue) {
    Double v = getDouble(name);
    return (v != null) ? v.doubleValue() : defaultValue;
  }
  
  public Long getTimeMillis(K name) {
    V v = get(name);
    try {
      return (v != null) ? Long.valueOf(toTimeMillis(name, v)) : null;
    } catch (RuntimeException ignore) {
      return null;
    } 
  }
  
  public long getTimeMillis(K name, long defaultValue) {
    Long v = getTimeMillis(name);
    return (v != null) ? v.longValue() : defaultValue;
  }
  
  public Boolean getBooleanAndRemove(K name) {
    V v = getAndRemove(name);
    try {
      return (v != null) ? Boolean.valueOf(toBoolean(name, v)) : null;
    } catch (RuntimeException ignore) {
      return null;
    } 
  }
  
  public boolean getBooleanAndRemove(K name, boolean defaultValue) {
    Boolean v = getBooleanAndRemove(name);
    return (v != null) ? v.booleanValue() : defaultValue;
  }
  
  public Byte getByteAndRemove(K name) {
    V v = getAndRemove(name);
    try {
      return (v != null) ? Byte.valueOf(toByte(name, v)) : null;
    } catch (RuntimeException ignore) {
      return null;
    } 
  }
  
  public byte getByteAndRemove(K name, byte defaultValue) {
    Byte v = getByteAndRemove(name);
    return (v != null) ? v.byteValue() : defaultValue;
  }
  
  public Character getCharAndRemove(K name) {
    V v = getAndRemove(name);
    try {
      return (v != null) ? Character.valueOf(toChar(name, v)) : null;
    } catch (RuntimeException ignore) {
      return null;
    } 
  }
  
  public char getCharAndRemove(K name, char defaultValue) {
    Character v = getCharAndRemove(name);
    return (v != null) ? v.charValue() : defaultValue;
  }
  
  public Short getShortAndRemove(K name) {
    V v = getAndRemove(name);
    try {
      return (v != null) ? Short.valueOf(toShort(name, v)) : null;
    } catch (RuntimeException ignore) {
      return null;
    } 
  }
  
  public short getShortAndRemove(K name, short defaultValue) {
    Short v = getShortAndRemove(name);
    return (v != null) ? v.shortValue() : defaultValue;
  }
  
  public Integer getIntAndRemove(K name) {
    V v = getAndRemove(name);
    try {
      return (v != null) ? Integer.valueOf(toInt(name, v)) : null;
    } catch (RuntimeException ignore) {
      return null;
    } 
  }
  
  public int getIntAndRemove(K name, int defaultValue) {
    Integer v = getIntAndRemove(name);
    return (v != null) ? v.intValue() : defaultValue;
  }
  
  public Long getLongAndRemove(K name) {
    V v = getAndRemove(name);
    try {
      return (v != null) ? Long.valueOf(toLong(name, v)) : null;
    } catch (RuntimeException ignore) {
      return null;
    } 
  }
  
  public long getLongAndRemove(K name, long defaultValue) {
    Long v = getLongAndRemove(name);
    return (v != null) ? v.longValue() : defaultValue;
  }
  
  public Float getFloatAndRemove(K name) {
    V v = getAndRemove(name);
    try {
      return (v != null) ? Float.valueOf(toFloat(name, v)) : null;
    } catch (RuntimeException ignore) {
      return null;
    } 
  }
  
  public float getFloatAndRemove(K name, float defaultValue) {
    Float v = getFloatAndRemove(name);
    return (v != null) ? v.floatValue() : defaultValue;
  }
  
  public Double getDoubleAndRemove(K name) {
    V v = getAndRemove(name);
    try {
      return (v != null) ? Double.valueOf(toDouble(name, v)) : null;
    } catch (RuntimeException ignore) {
      return null;
    } 
  }
  
  public double getDoubleAndRemove(K name, double defaultValue) {
    Double v = getDoubleAndRemove(name);
    return (v != null) ? v.doubleValue() : defaultValue;
  }
  
  public Long getTimeMillisAndRemove(K name) {
    V v = getAndRemove(name);
    try {
      return (v != null) ? Long.valueOf(toTimeMillis(name, v)) : null;
    } catch (RuntimeException ignore) {
      return null;
    } 
  }
  
  public long getTimeMillisAndRemove(K name, long defaultValue) {
    Long v = getTimeMillisAndRemove(name);
    return (v != null) ? v.longValue() : defaultValue;
  }
  
  public boolean equals(Object o) {
    if (!(o instanceof Headers))
      return false; 
    return equals((Headers<K, V, ?>)o, HashingStrategy.JAVA_HASHER);
  }
  
  public int hashCode() {
    return hashCode(HashingStrategy.JAVA_HASHER);
  }
  
  public final boolean equals(Headers<K, V, ?> h2, HashingStrategy<V> valueHashingStrategy) {
    if (h2.size() != size())
      return false; 
    if (this == h2)
      return true; 
    for (K name : names()) {
      List<V> otherValues = h2.getAll(name);
      List<V> values = getAll(name);
      if (otherValues.size() != values.size())
        return false; 
      for (int i = 0; i < otherValues.size(); i++) {
        if (!valueHashingStrategy.equals(otherValues.get(i), values.get(i)))
          return false; 
      } 
    } 
    return true;
  }
  
  public final int hashCode(HashingStrategy<V> valueHashingStrategy) {
    int result = -1028477387;
    for (K name : names()) {
      result = 31 * result + this.hashingStrategy.hashCode(name);
      List<V> values = getAll(name);
      for (int i = 0; i < values.size(); i++)
        result = 31 * result + valueHashingStrategy.hashCode(values.get(i)); 
    } 
    return result;
  }
  
  public String toString() {
    return HeadersUtils.toString(getClass(), iterator(), size());
  }
  
  protected void validateName(NameValidator<K> validator, boolean forAdd, K name) {
    validator.validateName(name);
  }
  
  protected void validateValue(ValueValidator<V> validator, K name, V value) {
    try {
      validator.validate(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Validation failed for header '" + name + "'", e);
    } 
  }
  
  protected HeaderEntry<K, V> newHeaderEntry(int h, K name, V value, HeaderEntry<K, V> next) {
    return new HeaderEntry<K, V>(h, name, value, next, this.head);
  }
  
  protected ValueConverter<V> valueConverter() {
    return this.valueConverter;
  }
  
  protected NameValidator<K> nameValidator() {
    return this.nameValidator;
  }
  
  protected ValueValidator<V> valueValidator() {
    return this.valueValidator;
  }
  
  private int index(int hash) {
    return hash & this.hashMask;
  }
  
  private void add0(int h, int i, K name, V value) {
    this.entries[i] = newHeaderEntry(h, name, value, this.entries[i]);
    this.size++;
  }
  
  private V remove0(int h, int i, K name) {
    HeaderEntry<K, V> e = this.entries[i];
    if (e == null)
      return null; 
    V value = null;
    HeaderEntry<K, V> next = e.next;
    while (next != null) {
      if (next.hash == h && this.hashingStrategy.equals(name, next.key)) {
        value = next.value;
        e.next = next.next;
        next.remove();
        this.size--;
      } else {
        e = next;
      } 
      next = e.next;
    } 
    e = this.entries[i];
    if (e.hash == h && this.hashingStrategy.equals(name, e.key)) {
      if (value == null)
        value = e.value; 
      this.entries[i] = e.next;
      e.remove();
      this.size--;
    } 
    return value;
  }
  
  HeaderEntry<K, V> remove0(HeaderEntry<K, V> entry, HeaderEntry<K, V> previous) {
    int i = index(entry.hash);
    HeaderEntry<K, V> firstEntry = this.entries[i];
    if (firstEntry == entry) {
      this.entries[i] = entry.next;
      previous = this.entries[i];
    } else if (previous == null) {
      previous = firstEntry;
      HeaderEntry<K, V> next = firstEntry.next;
      while (next != null && next != entry) {
        previous = next;
        next = next.next;
      } 
      assert next != null : "Entry not found in its hash bucket: " + entry;
      previous.next = entry.next;
    } else {
      previous.next = entry.next;
    } 
    entry.remove();
    this.size--;
    return previous;
  }
  
  private T thisT() {
    return (T)this;
  }
  
  private V fromObject(K name, Object value) {
    try {
      return this.valueConverter.convertObject(ObjectUtil.checkNotNull(value, "value"));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to convert object value for header '" + name + '\'', e);
    } 
  }
  
  private V fromBoolean(K name, boolean value) {
    try {
      return this.valueConverter.convertBoolean(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to convert boolean value for header '" + name + '\'', e);
    } 
  }
  
  private V fromByte(K name, byte value) {
    try {
      return this.valueConverter.convertByte(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to convert byte value for header '" + name + '\'', e);
    } 
  }
  
  private V fromChar(K name, char value) {
    try {
      return this.valueConverter.convertChar(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to convert char value for header '" + name + '\'', e);
    } 
  }
  
  private V fromShort(K name, short value) {
    try {
      return this.valueConverter.convertShort(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to convert short value for header '" + name + '\'', e);
    } 
  }
  
  private V fromInt(K name, int value) {
    try {
      return this.valueConverter.convertInt(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to convert int value for header '" + name + '\'', e);
    } 
  }
  
  private V fromLong(K name, long value) {
    try {
      return this.valueConverter.convertLong(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to convert long value for header '" + name + '\'', e);
    } 
  }
  
  private V fromFloat(K name, float value) {
    try {
      return this.valueConverter.convertFloat(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to convert float value for header '" + name + '\'', e);
    } 
  }
  
  private V fromDouble(K name, double value) {
    try {
      return this.valueConverter.convertDouble(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to convert double value for header '" + name + '\'', e);
    } 
  }
  
  private V fromTimeMillis(K name, long value) {
    try {
      return this.valueConverter.convertTimeMillis(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to convert millsecond value for header '" + name + '\'', e);
    } 
  }
  
  private boolean toBoolean(K name, V value) {
    try {
      return this.valueConverter.convertToBoolean(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to convert header value to boolean for header '" + name + '\'');
    } 
  }
  
  private byte toByte(K name, V value) {
    try {
      return this.valueConverter.convertToByte(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to convert header value to byte for header '" + name + '\'');
    } 
  }
  
  private char toChar(K name, V value) {
    try {
      return this.valueConverter.convertToChar(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to convert header value to char for header '" + name + '\'');
    } 
  }
  
  private short toShort(K name, V value) {
    try {
      return this.valueConverter.convertToShort(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to convert header value to short for header '" + name + '\'');
    } 
  }
  
  private int toInt(K name, V value) {
    try {
      return this.valueConverter.convertToInt(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to convert header value to int for header '" + name + '\'');
    } 
  }
  
  private long toLong(K name, V value) {
    try {
      return this.valueConverter.convertToLong(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to convert header value to long for header '" + name + '\'');
    } 
  }
  
  private float toFloat(K name, V value) {
    try {
      return this.valueConverter.convertToFloat(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to convert header value to float for header '" + name + '\'');
    } 
  }
  
  private double toDouble(K name, V value) {
    try {
      return this.valueConverter.convertToDouble(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to convert header value to double for header '" + name + '\'');
    } 
  }
  
  private long toTimeMillis(K name, V value) {
    try {
      return this.valueConverter.convertToTimeMillis(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to convert header value to millsecond for header '" + name + '\'');
    } 
  }
  
  public DefaultHeaders<K, V, T> copy() {
    DefaultHeaders<K, V, T> copy = new DefaultHeaders(this.hashingStrategy, this.valueConverter, this.nameValidator, this.entries.length);
    copy.addImpl(this);
    return copy;
  }
  
  private final class HeaderIterator implements Iterator<Map.Entry<K, V>> {
    private DefaultHeaders.HeaderEntry<K, V> current = DefaultHeaders.this.head;
    
    public boolean hasNext() {
      return (this.current.after != DefaultHeaders.this.head);
    }
    
    public Map.Entry<K, V> next() {
      this.current = this.current.after;
      if (this.current == DefaultHeaders.this.head)
        throw new NoSuchElementException(); 
      return this.current;
    }
    
    public void remove() {
      throw new UnsupportedOperationException("read only");
    }
    
    private HeaderIterator() {}
  }
  
  private final class ValueIterator implements Iterator<V> {
    private final K name;
    
    private final int hash;
    
    private DefaultHeaders.HeaderEntry<K, V> removalPrevious;
    
    private DefaultHeaders.HeaderEntry<K, V> previous;
    
    private DefaultHeaders.HeaderEntry<K, V> next;
    
    ValueIterator(K name) {
      this.name = (K)ObjectUtil.checkNotNull(name, "name");
      this.hash = DefaultHeaders.this.hashingStrategy.hashCode(name);
      calculateNext(DefaultHeaders.this.entries[DefaultHeaders.this.index(this.hash)]);
    }
    
    public boolean hasNext() {
      return (this.next != null);
    }
    
    public V next() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      if (this.previous != null)
        this.removalPrevious = this.previous; 
      this.previous = this.next;
      calculateNext(this.next.next);
      return this.previous.value;
    }
    
    public void remove() {
      if (this.previous == null)
        throw new IllegalStateException(); 
      this.removalPrevious = DefaultHeaders.this.remove0(this.previous, this.removalPrevious);
      this.previous = null;
    }
    
    private void calculateNext(DefaultHeaders.HeaderEntry<K, V> entry) {
      while (entry != null) {
        if (entry.hash == this.hash && DefaultHeaders.this.hashingStrategy.equals(this.name, entry.key)) {
          this.next = entry;
          return;
        } 
        entry = entry.next;
      } 
      this.next = null;
    }
  }
  
  protected static class HeaderEntry<K, V> implements Map.Entry<K, V> {
    protected final int hash;
    
    protected final K key;
    
    protected V value;
    
    protected HeaderEntry<K, V> next;
    
    protected HeaderEntry<K, V> before;
    
    protected HeaderEntry<K, V> after;
    
    protected HeaderEntry(int hash, K key) {
      this.hash = hash;
      this.key = key;
    }
    
    HeaderEntry(int hash, K key, V value, HeaderEntry<K, V> next, HeaderEntry<K, V> head) {
      this.hash = hash;
      this.key = key;
      this.value = value;
      this.next = next;
      this.after = head;
      this.before = head.before;
      pointNeighborsToThis();
    }
    
    HeaderEntry() {
      this.hash = -1;
      this.key = null;
      this.before = this.after = this;
    }
    
    protected final void pointNeighborsToThis() {
      this.before.after = this;
      this.after.before = this;
    }
    
    public final HeaderEntry<K, V> before() {
      return this.before;
    }
    
    public final HeaderEntry<K, V> after() {
      return this.after;
    }
    
    protected void remove() {
      this.before.after = this.after;
      this.after.before = this.before;
    }
    
    public final K getKey() {
      return this.key;
    }
    
    public final V getValue() {
      return this.value;
    }
    
    public final V setValue(V value) {
      ObjectUtil.checkNotNull(value, "value");
      V oldValue = this.value;
      this.value = value;
      return oldValue;
    }
    
    public final String toString() {
      return this.key.toString() + '=' + this.value.toString();
    }
    
    public boolean equals(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<?, ?> other = (Map.Entry<?, ?>)o;
      return (((getKey() == null) ? (other.getKey() == null) : getKey().equals(other.getKey())) && (
        (getValue() == null) ? (other.getValue() == null) : getValue().equals(other.getValue())));
    }
    
    public int hashCode() {
      return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
    }
  }
}
