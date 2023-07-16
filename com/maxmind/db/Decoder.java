package com.maxmind.db;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class Decoder {
  private static final Charset UTF_8 = StandardCharsets.UTF_8;
  
  private static final int[] POINTER_VALUE_OFFSETS = new int[] { 0, 0, 2048, 526336, 0 };
  
  boolean POINTER_TEST_HACK = false;
  
  private final NodeCache cache;
  
  private final long pointerBase;
  
  private final CharsetDecoder utfDecoder = UTF_8.newDecoder();
  
  private final ByteBuffer buffer;
  
  private final ConcurrentHashMap<Class, CachedConstructor> constructors;
  
  private final NodeCache.Loader cacheLoader;
  
  Decoder(NodeCache cache, ByteBuffer buffer, long pointerBase) {
    this(cache, buffer, pointerBase, new ConcurrentHashMap<>());
  }
  
  Decoder(NodeCache cache, ByteBuffer buffer, long pointerBase, ConcurrentHashMap<Class<?>, CachedConstructor> constructors) {
    this.cacheLoader = this::decode;
    this.cache = cache;
    this.pointerBase = pointerBase;
    this.buffer = buffer;
    this.constructors = constructors;
  }
  
  public <T> T decode(int offset, Class<T> cls) throws IOException {
    if (offset >= this.buffer.capacity())
      throw new InvalidDatabaseException("The MaxMind DB file's data section contains bad data: pointer larger than the database."); 
    this.buffer.position(offset);
    return cls.cast(decode(cls, (Type)null).getValue());
  }
  
  private <T> DecodedValue decode(CacheKey<T> key) throws IOException {
    int offset = key.getOffset();
    if (offset >= this.buffer.capacity())
      throw new InvalidDatabaseException("The MaxMind DB file's data section contains bad data: pointer larger than the database."); 
    this.buffer.position(offset);
    Class<T> cls = key.getCls();
    return decode(cls, key.getType());
  }
  
  private <T> DecodedValue decode(Class<T> cls, Type genericType) throws IOException {
    int ctrlByte = 0xFF & this.buffer.get();
    Type type = Type.fromControlByte(ctrlByte);
    if (type.equals(Type.POINTER)) {
      int pointerSize = (ctrlByte >>> 3 & 0x3) + 1;
      int base = (pointerSize == 4) ? 0 : (byte)(ctrlByte & 0x7);
      int packed = decodeInteger(base, pointerSize);
      long pointer = packed + this.pointerBase + POINTER_VALUE_OFFSETS[pointerSize];
      if (this.POINTER_TEST_HACK)
        return new DecodedValue(Long.valueOf(pointer)); 
      int targetOffset = (int)pointer;
      int position = this.buffer.position();
      CacheKey<T> key = new CacheKey<>(targetOffset, cls, genericType);
      DecodedValue o = this.cache.get(key, this.cacheLoader);
      this.buffer.position(position);
      return o;
    } 
    if (type.equals(Type.EXTENDED)) {
      int nextByte = this.buffer.get();
      int typeNum = nextByte + 7;
      if (typeNum < 8)
        throw new InvalidDatabaseException("Something went horribly wrong in the decoder. An extended type resolved to a type number < 8 (" + typeNum + ")"); 
      type = Type.get(typeNum);
    } 
    int size = ctrlByte & 0x1F;
    if (size >= 29) {
      switch (size) {
        case 29:
          size = 29 + (0xFF & this.buffer.get());
          return new DecodedValue(decodeByType(type, size, cls, genericType));
        case 30:
          size = 285 + decodeInteger(2);
          return new DecodedValue(decodeByType(type, size, cls, genericType));
      } 
      size = 65821 + decodeInteger(3);
    } 
    return new DecodedValue(decodeByType(type, size, cls, genericType));
  }
  
  private <T> Object decodeByType(Type type, int size, Class<T> cls, Type genericType) throws IOException {
    Class<?> elementClass;
    switch (type) {
      case MAP:
        return decodeMap(size, cls, genericType);
      case ARRAY:
        elementClass = Object.class;
        if (genericType instanceof ParameterizedType) {
          ParameterizedType pType = (ParameterizedType)genericType;
          Type[] actualTypes = pType.getActualTypeArguments();
          if (actualTypes.length == 1)
            elementClass = (Class)actualTypes[0]; 
        } 
        return decodeArray(size, cls, elementClass);
      case BOOLEAN:
        return Boolean.valueOf(decodeBoolean(size));
      case UTF8_STRING:
        return decodeString(size);
      case DOUBLE:
        return Double.valueOf(decodeDouble(size));
      case FLOAT:
        return Float.valueOf(decodeFloat(size));
      case BYTES:
        return getByteArray(size);
      case UINT16:
        return Integer.valueOf(decodeUint16(size));
      case UINT32:
        return Long.valueOf(decodeUint32(size));
      case INT32:
        return Integer.valueOf(decodeInt32(size));
      case UINT64:
      case UINT128:
        return decodeBigInteger(size);
    } 
    throw new InvalidDatabaseException("Unknown or unexpected type: " + type
        .name());
  }
  
  private String decodeString(int size) throws CharacterCodingException {
    int oldLimit = this.buffer.limit();
    this.buffer.limit(this.buffer.position() + size);
    String s = this.utfDecoder.decode(this.buffer).toString();
    this.buffer.limit(oldLimit);
    return s;
  }
  
  private int decodeUint16(int size) {
    return decodeInteger(size);
  }
  
  private int decodeInt32(int size) {
    return decodeInteger(size);
  }
  
  private long decodeLong(int size) {
    long integer = 0L;
    for (int i = 0; i < size; i++)
      integer = integer << 8L | (this.buffer.get() & 0xFF); 
    return integer;
  }
  
  private long decodeUint32(int size) {
    return decodeLong(size);
  }
  
  private int decodeInteger(int size) {
    return decodeInteger(0, size);
  }
  
  private int decodeInteger(int base, int size) {
    return decodeInteger(this.buffer, base, size);
  }
  
  static int decodeInteger(ByteBuffer buffer, int base, int size) {
    int integer = base;
    for (int i = 0; i < size; i++)
      integer = integer << 8 | buffer.get() & 0xFF; 
    return integer;
  }
  
  private BigInteger decodeBigInteger(int size) {
    byte[] bytes = getByteArray(size);
    return new BigInteger(1, bytes);
  }
  
  private double decodeDouble(int size) throws InvalidDatabaseException {
    if (size != 8)
      throw new InvalidDatabaseException("The MaxMind DB file's data section contains bad data: invalid size of double."); 
    return this.buffer.getDouble();
  }
  
  private float decodeFloat(int size) throws InvalidDatabaseException {
    if (size != 4)
      throw new InvalidDatabaseException("The MaxMind DB file's data section contains bad data: invalid size of float."); 
    return this.buffer.getFloat();
  }
  
  private static boolean decodeBoolean(int size) throws InvalidDatabaseException {
    switch (size) {
      case 0:
        return false;
      case 1:
        return true;
    } 
    throw new InvalidDatabaseException("The MaxMind DB file's data section contains bad data: invalid size of boolean.");
  }
  
  private <T, V> List<V> decodeArray(int size, Class<T> cls, Class<V> elementClass) throws IOException {
    List<V> array;
    if (!List.class.isAssignableFrom(cls) && !cls.equals(Object.class))
      throw new DeserializationException("Unable to deserialize an array into an " + cls); 
    if (cls.equals(List.class) || cls.equals(Object.class)) {
      array = new ArrayList<>(size);
    } else {
      Constructor<T> constructor;
      try {
        constructor = cls.getConstructor(new Class[] { int.class });
      } catch (NoSuchMethodException e) {
        throw new DeserializationException("No constructor found for the List: " + e);
      } 
      Object[] parameters = { Integer.valueOf(size) };
      try {
        List<V> array2 = (List<V>)constructor.newInstance(parameters);
        array = array2;
      } catch (InstantiationException|IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
        throw new DeserializationException("Error creating list: " + e);
      } 
    } 
    for (int i = 0; i < size; i++) {
      Object e = decode(elementClass, (Type)null).getValue();
      array.add(elementClass.cast(e));
    } 
    return array;
  }
  
  private <T> Object decodeMap(int size, Class<T> cls, Type genericType) throws IOException {
    if (Map.class.isAssignableFrom(cls) || cls.equals(Object.class)) {
      Class<?> valueClass = Object.class;
      if (genericType instanceof ParameterizedType) {
        ParameterizedType pType = (ParameterizedType)genericType;
        Type[] actualTypes = pType.getActualTypeArguments();
        if (actualTypes.length == 2) {
          Class<?> keyClass = (Class)actualTypes[0];
          if (!keyClass.equals(String.class))
            throw new DeserializationException("Map keys must be strings."); 
          valueClass = (Class)actualTypes[1];
        } 
      } 
      return decodeMapIntoMap(cls, size, valueClass);
    } 
    return decodeMapIntoObject(size, cls);
  }
  
  private <T, V> Map<String, V> decodeMapIntoMap(Class<T> cls, int size, Class<V> valueClass) throws IOException {
    Map<String, V> map;
    if (cls.equals(Map.class) || cls.equals(Object.class)) {
      map = new HashMap<>(size);
    } else {
      Constructor<T> constructor;
      try {
        constructor = cls.getConstructor(new Class[] { int.class });
      } catch (NoSuchMethodException e) {
        throw new DeserializationException("No constructor found for the Map: " + e);
      } 
      Object[] parameters = { Integer.valueOf(size) };
      try {
        Map<String, V> map2 = (Map<String, V>)constructor.newInstance(parameters);
        map = map2;
      } catch (InstantiationException|IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
        throw new DeserializationException("Error creating map: " + e);
      } 
    } 
    for (int i = 0; i < size; i++) {
      String key = (String)decode(String.class, (Type)null).getValue();
      Object value = decode(valueClass, (Type)null).getValue();
      map.put(key, valueClass.cast(value));
    } 
    return map;
  }
  
  private <T> Object decodeMapIntoObject(int size, Class<T> cls) throws IOException {
    Constructor<T> constructor;
    Class<?>[] parameterTypes;
    Type[] parameterGenericTypes;
    Map<String, Integer> parameterIndexes;
    CachedConstructor<T> cachedConstructor = this.constructors.get(cls);
    if (cachedConstructor == null) {
      this;
      constructor = findConstructor(cls);
      parameterTypes = constructor.getParameterTypes();
      parameterGenericTypes = constructor.getGenericParameterTypes();
      parameterIndexes = new HashMap<>();
      Annotation[][] annotations = constructor.getParameterAnnotations();
      for (int j = 0; j < constructor.getParameterCount(); j++) {
        this;
        String parameterName = getParameterName(cls, j, annotations[j]);
        parameterIndexes.put(parameterName, Integer.valueOf(j));
      } 
      this.constructors.put(cls, new CachedConstructor<>(constructor, parameterTypes, parameterGenericTypes, parameterIndexes));
    } else {
      constructor = cachedConstructor.getConstructor();
      parameterTypes = cachedConstructor.getParameterTypes();
      parameterGenericTypes = cachedConstructor.getParameterGenericTypes();
      parameterIndexes = cachedConstructor.getParameterIndexes();
    } 
    Object[] parameters = new Object[parameterTypes.length];
    for (int i = 0; i < size; i++) {
      String key = (String)decode(String.class, (Type)null).getValue();
      Integer parameterIndex = parameterIndexes.get(key);
      if (parameterIndex == null) {
        int offset = nextValueOffset(this.buffer.position(), 1);
        this.buffer.position(offset);
      } else {
        parameters[parameterIndex.intValue()] = decode(parameterTypes[parameterIndex
              .intValue()], parameterGenericTypes[parameterIndex
              .intValue()])
          .getValue();
      } 
    } 
    try {
      return constructor.newInstance(parameters);
    } catch (InstantiationException|IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
      throw new DeserializationException("Error creating object: " + e);
    } 
  }
  
  private static <T> Constructor<T> findConstructor(Class<T> cls) throws ConstructorNotFoundException {
    Constructor[] arrayOfConstructor1 = (Constructor[])cls.getConstructors();
    Constructor[] arrayOfConstructor2;
    int i;
    byte b;
    for (arrayOfConstructor2 = arrayOfConstructor1, i = arrayOfConstructor2.length, b = 0; b < i; ) {
      Constructor<?> constructor = arrayOfConstructor2[b];
      if (constructor.getAnnotation(MaxMindDbConstructor.class) == null) {
        b++;
        continue;
      } 
      Constructor<T> constructor2 = (Constructor)constructor;
      return constructor2;
    } 
    throw new ConstructorNotFoundException("No constructor on class " + cls.getName() + " with the MaxMindDbConstructor annotation was found.");
  }
  
  private static <T> String getParameterName(Class<T> cls, int index, Annotation[] annotations) throws ParameterNotFoundException {
    Annotation[] arrayOfAnnotation;
    int i;
    byte b;
    for (arrayOfAnnotation = annotations, i = arrayOfAnnotation.length, b = 0; b < i; ) {
      Annotation annotation = arrayOfAnnotation[b];
      if (!annotation.annotationType().equals(MaxMindDbParameter.class)) {
        b++;
        continue;
      } 
      MaxMindDbParameter paramAnnotation = (MaxMindDbParameter)annotation;
      return paramAnnotation.name();
    } 
    throw new ParameterNotFoundException("Constructor parameter " + index + " on class " + cls.getName() + " is not annotated with MaxMindDbParameter.");
  }
  
  private int nextValueOffset(int offset, int numberToSkip) throws InvalidDatabaseException {
    int pointerSize;
    if (numberToSkip == 0)
      return offset; 
    CtrlData ctrlData = getCtrlData(offset);
    int ctrlByte = ctrlData.getCtrlByte();
    int size = ctrlData.getSize();
    offset = ctrlData.getOffset();
    Type type = ctrlData.getType();
    switch (type) {
      case POINTER:
        pointerSize = (ctrlByte >>> 3 & 0x3) + 1;
        offset += pointerSize;
      case MAP:
        numberToSkip += 2 * size;
      case ARRAY:
        numberToSkip += size;
      case BOOLEAN:
        return nextValueOffset(offset, numberToSkip - 1);
    } 
    offset += size;
  }
  
  private CtrlData getCtrlData(int offset) throws InvalidDatabaseException {
    if (offset >= this.buffer.capacity())
      throw new InvalidDatabaseException("The MaxMind DB file's data section contains bad data: pointer larger than the database."); 
    this.buffer.position(offset);
    int ctrlByte = 0xFF & this.buffer.get();
    offset++;
    Type type = Type.fromControlByte(ctrlByte);
    if (type.equals(Type.EXTENDED)) {
      int nextByte = this.buffer.get();
      int typeNum = nextByte + 7;
      if (typeNum < 8)
        throw new InvalidDatabaseException("Something went horribly wrong in the decoder. An extended type resolved to a type number < 8 (" + typeNum + ")"); 
      type = Type.get(typeNum);
      offset++;
    } 
    int size = ctrlByte & 0x1F;
    if (size >= 29) {
      int bytesToRead = size - 28;
      offset += bytesToRead;
      switch (size) {
        case 29:
          size = 29 + (0xFF & this.buffer.get());
          return new CtrlData(type, ctrlByte, offset, size);
        case 30:
          size = 285 + decodeInteger(2);
          return new CtrlData(type, ctrlByte, offset, size);
      } 
      size = 65821 + decodeInteger(3);
    } 
    return new CtrlData(type, ctrlByte, offset, size);
  }
  
  private byte[] getByteArray(int length) {
    return getByteArray(this.buffer, length);
  }
  
  private static byte[] getByteArray(ByteBuffer buffer, int length) {
    byte[] bytes = new byte[length];
    buffer.get(bytes);
    return bytes;
  }
}
