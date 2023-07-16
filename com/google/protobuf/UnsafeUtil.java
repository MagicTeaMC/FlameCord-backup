package com.google.protobuf;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;

final class UnsafeUtil {
  private static final Unsafe UNSAFE = getUnsafe();
  
  private static final Class<?> MEMORY_CLASS = Android.getMemoryClass();
  
  private static final boolean IS_ANDROID_64 = determineAndroidSupportByAddressSize(long.class);
  
  private static final boolean IS_ANDROID_32 = determineAndroidSupportByAddressSize(int.class);
  
  private static final MemoryAccessor MEMORY_ACCESSOR = getMemoryAccessor();
  
  private static final boolean HAS_UNSAFE_BYTEBUFFER_OPERATIONS = supportsUnsafeByteBufferOperations();
  
  private static final boolean HAS_UNSAFE_ARRAY_OPERATIONS = supportsUnsafeArrayOperations();
  
  static final long BYTE_ARRAY_BASE_OFFSET = arrayBaseOffset(byte[].class);
  
  private static final long BOOLEAN_ARRAY_BASE_OFFSET = arrayBaseOffset(boolean[].class);
  
  private static final long BOOLEAN_ARRAY_INDEX_SCALE = arrayIndexScale(boolean[].class);
  
  private static final long INT_ARRAY_BASE_OFFSET = arrayBaseOffset(int[].class);
  
  private static final long INT_ARRAY_INDEX_SCALE = arrayIndexScale(int[].class);
  
  private static final long LONG_ARRAY_BASE_OFFSET = arrayBaseOffset(long[].class);
  
  private static final long LONG_ARRAY_INDEX_SCALE = arrayIndexScale(long[].class);
  
  private static final long FLOAT_ARRAY_BASE_OFFSET = arrayBaseOffset(float[].class);
  
  private static final long FLOAT_ARRAY_INDEX_SCALE = arrayIndexScale(float[].class);
  
  private static final long DOUBLE_ARRAY_BASE_OFFSET = arrayBaseOffset(double[].class);
  
  private static final long DOUBLE_ARRAY_INDEX_SCALE = arrayIndexScale(double[].class);
  
  private static final long OBJECT_ARRAY_BASE_OFFSET = arrayBaseOffset(Object[].class);
  
  private static final long OBJECT_ARRAY_INDEX_SCALE = arrayIndexScale(Object[].class);
  
  private static final long BUFFER_ADDRESS_OFFSET = fieldOffset(bufferAddressField());
  
  private static final int STRIDE = 8;
  
  private static final int STRIDE_ALIGNMENT_MASK = 7;
  
  private static final int BYTE_ARRAY_ALIGNMENT = (int)(BYTE_ARRAY_BASE_OFFSET & 0x7L);
  
  static final boolean IS_BIG_ENDIAN = (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN);
  
  static boolean hasUnsafeArrayOperations() {
    return HAS_UNSAFE_ARRAY_OPERATIONS;
  }
  
  static boolean hasUnsafeByteBufferOperations() {
    return HAS_UNSAFE_BYTEBUFFER_OPERATIONS;
  }
  
  static boolean isAndroid64() {
    return IS_ANDROID_64;
  }
  
  static <T> T allocateInstance(Class<T> clazz) {
    try {
      return (T)UNSAFE.allocateInstance(clazz);
    } catch (InstantiationException e) {
      throw new IllegalStateException(e);
    } 
  }
  
  static long objectFieldOffset(Field field) {
    return MEMORY_ACCESSOR.objectFieldOffset(field);
  }
  
  private static int arrayBaseOffset(Class<?> clazz) {
    return HAS_UNSAFE_ARRAY_OPERATIONS ? MEMORY_ACCESSOR.arrayBaseOffset(clazz) : -1;
  }
  
  private static int arrayIndexScale(Class<?> clazz) {
    return HAS_UNSAFE_ARRAY_OPERATIONS ? MEMORY_ACCESSOR.arrayIndexScale(clazz) : -1;
  }
  
  static byte getByte(Object target, long offset) {
    return MEMORY_ACCESSOR.getByte(target, offset);
  }
  
  static void putByte(Object target, long offset, byte value) {
    MEMORY_ACCESSOR.putByte(target, offset, value);
  }
  
  static int getInt(Object target, long offset) {
    return MEMORY_ACCESSOR.getInt(target, offset);
  }
  
  static void putInt(Object target, long offset, int value) {
    MEMORY_ACCESSOR.putInt(target, offset, value);
  }
  
  static long getLong(Object target, long offset) {
    return MEMORY_ACCESSOR.getLong(target, offset);
  }
  
  static void putLong(Object target, long offset, long value) {
    MEMORY_ACCESSOR.putLong(target, offset, value);
  }
  
  static boolean getBoolean(Object target, long offset) {
    return MEMORY_ACCESSOR.getBoolean(target, offset);
  }
  
  static void putBoolean(Object target, long offset, boolean value) {
    MEMORY_ACCESSOR.putBoolean(target, offset, value);
  }
  
  static float getFloat(Object target, long offset) {
    return MEMORY_ACCESSOR.getFloat(target, offset);
  }
  
  static void putFloat(Object target, long offset, float value) {
    MEMORY_ACCESSOR.putFloat(target, offset, value);
  }
  
  static double getDouble(Object target, long offset) {
    return MEMORY_ACCESSOR.getDouble(target, offset);
  }
  
  static void putDouble(Object target, long offset, double value) {
    MEMORY_ACCESSOR.putDouble(target, offset, value);
  }
  
  static Object getObject(Object target, long offset) {
    return MEMORY_ACCESSOR.getObject(target, offset);
  }
  
  static void putObject(Object target, long offset, Object value) {
    MEMORY_ACCESSOR.putObject(target, offset, value);
  }
  
  static byte getByte(byte[] target, long index) {
    return MEMORY_ACCESSOR.getByte(target, BYTE_ARRAY_BASE_OFFSET + index);
  }
  
  static void putByte(byte[] target, long index, byte value) {
    MEMORY_ACCESSOR.putByte(target, BYTE_ARRAY_BASE_OFFSET + index, value);
  }
  
  static int getInt(int[] target, long index) {
    return MEMORY_ACCESSOR.getInt(target, INT_ARRAY_BASE_OFFSET + index * INT_ARRAY_INDEX_SCALE);
  }
  
  static void putInt(int[] target, long index, int value) {
    MEMORY_ACCESSOR.putInt(target, INT_ARRAY_BASE_OFFSET + index * INT_ARRAY_INDEX_SCALE, value);
  }
  
  static long getLong(long[] target, long index) {
    return MEMORY_ACCESSOR.getLong(target, LONG_ARRAY_BASE_OFFSET + index * LONG_ARRAY_INDEX_SCALE);
  }
  
  static void putLong(long[] target, long index, long value) {
    MEMORY_ACCESSOR.putLong(target, LONG_ARRAY_BASE_OFFSET + index * LONG_ARRAY_INDEX_SCALE, value);
  }
  
  static boolean getBoolean(boolean[] target, long index) {
    return MEMORY_ACCESSOR.getBoolean(target, BOOLEAN_ARRAY_BASE_OFFSET + index * BOOLEAN_ARRAY_INDEX_SCALE);
  }
  
  static void putBoolean(boolean[] target, long index, boolean value) {
    MEMORY_ACCESSOR.putBoolean(target, BOOLEAN_ARRAY_BASE_OFFSET + index * BOOLEAN_ARRAY_INDEX_SCALE, value);
  }
  
  static float getFloat(float[] target, long index) {
    return MEMORY_ACCESSOR.getFloat(target, FLOAT_ARRAY_BASE_OFFSET + index * FLOAT_ARRAY_INDEX_SCALE);
  }
  
  static void putFloat(float[] target, long index, float value) {
    MEMORY_ACCESSOR.putFloat(target, FLOAT_ARRAY_BASE_OFFSET + index * FLOAT_ARRAY_INDEX_SCALE, value);
  }
  
  static double getDouble(double[] target, long index) {
    return MEMORY_ACCESSOR.getDouble(target, DOUBLE_ARRAY_BASE_OFFSET + index * DOUBLE_ARRAY_INDEX_SCALE);
  }
  
  static void putDouble(double[] target, long index, double value) {
    MEMORY_ACCESSOR.putDouble(target, DOUBLE_ARRAY_BASE_OFFSET + index * DOUBLE_ARRAY_INDEX_SCALE, value);
  }
  
  static Object getObject(Object[] target, long index) {
    return MEMORY_ACCESSOR.getObject(target, OBJECT_ARRAY_BASE_OFFSET + index * OBJECT_ARRAY_INDEX_SCALE);
  }
  
  static void putObject(Object[] target, long index, Object value) {
    MEMORY_ACCESSOR.putObject(target, OBJECT_ARRAY_BASE_OFFSET + index * OBJECT_ARRAY_INDEX_SCALE, value);
  }
  
  static void copyMemory(byte[] src, long srcIndex, long targetOffset, long length) {
    MEMORY_ACCESSOR.copyMemory(src, srcIndex, targetOffset, length);
  }
  
  static void copyMemory(long srcOffset, byte[] target, long targetIndex, long length) {
    MEMORY_ACCESSOR.copyMemory(srcOffset, target, targetIndex, length);
  }
  
  static void copyMemory(byte[] src, long srcIndex, byte[] target, long targetIndex, long length) {
    System.arraycopy(src, (int)srcIndex, target, (int)targetIndex, (int)length);
  }
  
  static byte getByte(long address) {
    return MEMORY_ACCESSOR.getByte(address);
  }
  
  static void putByte(long address, byte value) {
    MEMORY_ACCESSOR.putByte(address, value);
  }
  
  static int getInt(long address) {
    return MEMORY_ACCESSOR.getInt(address);
  }
  
  static void putInt(long address, int value) {
    MEMORY_ACCESSOR.putInt(address, value);
  }
  
  static long getLong(long address) {
    return MEMORY_ACCESSOR.getLong(address);
  }
  
  static void putLong(long address, long value) {
    MEMORY_ACCESSOR.putLong(address, value);
  }
  
  static long addressOffset(ByteBuffer buffer) {
    return MEMORY_ACCESSOR.getLong(buffer, BUFFER_ADDRESS_OFFSET);
  }
  
  static Object getStaticObject(Field field) {
    return MEMORY_ACCESSOR.getStaticObject(field);
  }
  
  static Unsafe getUnsafe() {
    Unsafe unsafe = null;
    try {
      unsafe = AccessController.<Unsafe>doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
            public Unsafe run() throws Exception {
              Class<Unsafe> k = Unsafe.class;
              for (Field f : k.getDeclaredFields()) {
                f.setAccessible(true);
                Object x = f.get(null);
                if (k.isInstance(x))
                  return k.cast(x); 
              } 
              return null;
            }
          });
    } catch (Throwable throwable) {}
    return unsafe;
  }
  
  private static MemoryAccessor getMemoryAccessor() {
    if (UNSAFE == null)
      return null; 
    if (Android.isOnAndroidDevice()) {
      if (IS_ANDROID_64)
        return new Android64MemoryAccessor(UNSAFE); 
      if (IS_ANDROID_32)
        return new Android32MemoryAccessor(UNSAFE); 
      return null;
    } 
    return new JvmMemoryAccessor(UNSAFE);
  }
  
  private static boolean supportsUnsafeArrayOperations() {
    if (MEMORY_ACCESSOR == null)
      return false; 
    return MEMORY_ACCESSOR.supportsUnsafeArrayOperations();
  }
  
  private static boolean supportsUnsafeByteBufferOperations() {
    if (MEMORY_ACCESSOR == null)
      return false; 
    return MEMORY_ACCESSOR.supportsUnsafeByteBufferOperations();
  }
  
  static boolean determineAndroidSupportByAddressSize(Class<?> addressClass) {
    if (!Android.isOnAndroidDevice())
      return false; 
    try {
      Class<?> clazz = MEMORY_CLASS;
      clazz.getMethod("peekLong", new Class[] { addressClass, boolean.class });
      clazz.getMethod("pokeLong", new Class[] { addressClass, long.class, boolean.class });
      clazz.getMethod("pokeInt", new Class[] { addressClass, int.class, boolean.class });
      clazz.getMethod("peekInt", new Class[] { addressClass, boolean.class });
      clazz.getMethod("pokeByte", new Class[] { addressClass, byte.class });
      clazz.getMethod("peekByte", new Class[] { addressClass });
      clazz.getMethod("pokeByteArray", new Class[] { addressClass, byte[].class, int.class, int.class });
      clazz.getMethod("peekByteArray", new Class[] { addressClass, byte[].class, int.class, int.class });
      return true;
    } catch (Throwable t) {
      return false;
    } 
  }
  
  private static Field bufferAddressField() {
    if (Android.isOnAndroidDevice()) {
      Field field1 = field(Buffer.class, "effectiveDirectAddress");
      if (field1 != null)
        return field1; 
    } 
    Field field = field(Buffer.class, "address");
    return (field != null && field.getType() == long.class) ? field : null;
  }
  
  private static int firstDifferingByteIndexNativeEndian(long left, long right) {
    int n = IS_BIG_ENDIAN ? Long.numberOfLeadingZeros(left ^ right) : Long.numberOfTrailingZeros(left ^ right);
    return n >> 3;
  }
  
  static int mismatch(byte[] left, int leftOff, byte[] right, int rightOff, int length) {
    if (leftOff < 0 || rightOff < 0 || length < 0 || leftOff + length > left.length || rightOff + length > right.length)
      throw new IndexOutOfBoundsException(); 
    int index = 0;
    if (HAS_UNSAFE_ARRAY_OPERATIONS) {
      int leftAlignment = BYTE_ARRAY_ALIGNMENT + leftOff & 0x7;
      for (; index < length && (leftAlignment & 0x7) != 0; 
        index++, leftAlignment++) {
        if (left[leftOff + index] != right[rightOff + index])
          return index; 
      } 
      int strideLength = (length - index & 0xFFFFFFF8) + index;
      for (; index < strideLength; index += 8) {
        long leftLongWord = getLong(left, BYTE_ARRAY_BASE_OFFSET + leftOff + index);
        long rightLongWord = getLong(right, BYTE_ARRAY_BASE_OFFSET + rightOff + index);
        if (leftLongWord != rightLongWord)
          return index + firstDifferingByteIndexNativeEndian(leftLongWord, rightLongWord); 
      } 
    } 
    for (; index < length; index++) {
      if (left[leftOff + index] != right[rightOff + index])
        return index; 
    } 
    return -1;
  }
  
  private static long fieldOffset(Field field) {
    return (field == null || MEMORY_ACCESSOR == null) ? -1L : MEMORY_ACCESSOR.objectFieldOffset(field);
  }
  
  private static Field field(Class<?> clazz, String fieldName) {
    Field field;
    try {
      field = clazz.getDeclaredField(fieldName);
    } catch (Throwable t) {
      field = null;
    } 
    return field;
  }
  
  private static abstract class MemoryAccessor {
    Unsafe unsafe;
    
    MemoryAccessor(Unsafe unsafe) {
      this.unsafe = unsafe;
    }
    
    public final long objectFieldOffset(Field field) {
      return this.unsafe.objectFieldOffset(field);
    }
    
    public final int arrayBaseOffset(Class<?> clazz) {
      return this.unsafe.arrayBaseOffset(clazz);
    }
    
    public final int arrayIndexScale(Class<?> clazz) {
      return this.unsafe.arrayIndexScale(clazz);
    }
    
    public abstract Object getStaticObject(Field param1Field);
    
    public boolean supportsUnsafeArrayOperations() {
      if (this.unsafe == null)
        return false; 
      try {
        Class<?> clazz = this.unsafe.getClass();
        clazz.getMethod("objectFieldOffset", new Class[] { Field.class });
        clazz.getMethod("arrayBaseOffset", new Class[] { Class.class });
        clazz.getMethod("arrayIndexScale", new Class[] { Class.class });
        clazz.getMethod("getInt", new Class[] { Object.class, long.class });
        clazz.getMethod("putInt", new Class[] { Object.class, long.class, int.class });
        clazz.getMethod("getLong", new Class[] { Object.class, long.class });
        clazz.getMethod("putLong", new Class[] { Object.class, long.class, long.class });
        clazz.getMethod("getObject", new Class[] { Object.class, long.class });
        clazz.getMethod("putObject", new Class[] { Object.class, long.class, Object.class });
        return true;
      } catch (Throwable e) {
        UnsafeUtil.logMissingMethod(e);
        return false;
      } 
    }
    
    public abstract byte getByte(Object param1Object, long param1Long);
    
    public abstract void putByte(Object param1Object, long param1Long, byte param1Byte);
    
    public final int getInt(Object target, long offset) {
      return this.unsafe.getInt(target, offset);
    }
    
    public final void putInt(Object target, long offset, int value) {
      this.unsafe.putInt(target, offset, value);
    }
    
    public final long getLong(Object target, long offset) {
      return this.unsafe.getLong(target, offset);
    }
    
    public final void putLong(Object target, long offset, long value) {
      this.unsafe.putLong(target, offset, value);
    }
    
    public abstract boolean getBoolean(Object param1Object, long param1Long);
    
    public abstract void putBoolean(Object param1Object, long param1Long, boolean param1Boolean);
    
    public abstract float getFloat(Object param1Object, long param1Long);
    
    public abstract void putFloat(Object param1Object, long param1Long, float param1Float);
    
    public abstract double getDouble(Object param1Object, long param1Long);
    
    public abstract void putDouble(Object param1Object, long param1Long, double param1Double);
    
    public final Object getObject(Object target, long offset) {
      return this.unsafe.getObject(target, offset);
    }
    
    public final void putObject(Object target, long offset, Object value) {
      this.unsafe.putObject(target, offset, value);
    }
    
    public boolean supportsUnsafeByteBufferOperations() {
      if (this.unsafe == null)
        return false; 
      try {
        Class<?> clazz = this.unsafe.getClass();
        clazz.getMethod("objectFieldOffset", new Class[] { Field.class });
        clazz.getMethod("getLong", new Class[] { Object.class, long.class });
        if (UnsafeUtil.bufferAddressField() == null)
          return false; 
        return true;
      } catch (Throwable e) {
        UnsafeUtil.logMissingMethod(e);
        return false;
      } 
    }
    
    public abstract byte getByte(long param1Long);
    
    public abstract void putByte(long param1Long, byte param1Byte);
    
    public abstract int getInt(long param1Long);
    
    public abstract void putInt(long param1Long, int param1Int);
    
    public abstract long getLong(long param1Long);
    
    public abstract void putLong(long param1Long1, long param1Long2);
    
    public abstract void copyMemory(long param1Long1, byte[] param1ArrayOfbyte, long param1Long2, long param1Long3);
    
    public abstract void copyMemory(byte[] param1ArrayOfbyte, long param1Long1, long param1Long2, long param1Long3);
  }
  
  private static final class JvmMemoryAccessor extends MemoryAccessor {
    JvmMemoryAccessor(Unsafe unsafe) {
      super(unsafe);
    }
    
    public Object getStaticObject(Field field) {
      return getObject(this.unsafe.staticFieldBase(field), this.unsafe.staticFieldOffset(field));
    }
    
    public boolean supportsUnsafeArrayOperations() {
      if (!super.supportsUnsafeArrayOperations())
        return false; 
      try {
        Class<?> clazz = this.unsafe.getClass();
        clazz.getMethod("getByte", new Class[] { Object.class, long.class });
        clazz.getMethod("putByte", new Class[] { Object.class, long.class, byte.class });
        clazz.getMethod("getBoolean", new Class[] { Object.class, long.class });
        clazz.getMethod("putBoolean", new Class[] { Object.class, long.class, boolean.class });
        clazz.getMethod("getFloat", new Class[] { Object.class, long.class });
        clazz.getMethod("putFloat", new Class[] { Object.class, long.class, float.class });
        clazz.getMethod("getDouble", new Class[] { Object.class, long.class });
        clazz.getMethod("putDouble", new Class[] { Object.class, long.class, double.class });
        return true;
      } catch (Throwable e) {
        UnsafeUtil.logMissingMethod(e);
        return false;
      } 
    }
    
    public byte getByte(Object target, long offset) {
      return this.unsafe.getByte(target, offset);
    }
    
    public void putByte(Object target, long offset, byte value) {
      this.unsafe.putByte(target, offset, value);
    }
    
    public boolean getBoolean(Object target, long offset) {
      return this.unsafe.getBoolean(target, offset);
    }
    
    public void putBoolean(Object target, long offset, boolean value) {
      this.unsafe.putBoolean(target, offset, value);
    }
    
    public float getFloat(Object target, long offset) {
      return this.unsafe.getFloat(target, offset);
    }
    
    public void putFloat(Object target, long offset, float value) {
      this.unsafe.putFloat(target, offset, value);
    }
    
    public double getDouble(Object target, long offset) {
      return this.unsafe.getDouble(target, offset);
    }
    
    public void putDouble(Object target, long offset, double value) {
      this.unsafe.putDouble(target, offset, value);
    }
    
    public boolean supportsUnsafeByteBufferOperations() {
      if (!super.supportsUnsafeByteBufferOperations())
        return false; 
      try {
        Class<?> clazz = this.unsafe.getClass();
        clazz.getMethod("getByte", new Class[] { long.class });
        clazz.getMethod("putByte", new Class[] { long.class, byte.class });
        clazz.getMethod("getInt", new Class[] { long.class });
        clazz.getMethod("putInt", new Class[] { long.class, int.class });
        clazz.getMethod("getLong", new Class[] { long.class });
        clazz.getMethod("putLong", new Class[] { long.class, long.class });
        clazz.getMethod("copyMemory", new Class[] { long.class, long.class, long.class });
        clazz.getMethod("copyMemory", new Class[] { Object.class, long.class, Object.class, long.class, long.class });
        return true;
      } catch (Throwable e) {
        UnsafeUtil.logMissingMethod(e);
        return false;
      } 
    }
    
    public byte getByte(long address) {
      return this.unsafe.getByte(address);
    }
    
    public void putByte(long address, byte value) {
      this.unsafe.putByte(address, value);
    }
    
    public int getInt(long address) {
      return this.unsafe.getInt(address);
    }
    
    public void putInt(long address, int value) {
      this.unsafe.putInt(address, value);
    }
    
    public long getLong(long address) {
      return this.unsafe.getLong(address);
    }
    
    public void putLong(long address, long value) {
      this.unsafe.putLong(address, value);
    }
    
    public void copyMemory(long srcOffset, byte[] target, long targetIndex, long length) {
      this.unsafe.copyMemory(null, srcOffset, target, UnsafeUtil.BYTE_ARRAY_BASE_OFFSET + targetIndex, length);
    }
    
    public void copyMemory(byte[] src, long srcIndex, long targetOffset, long length) {
      this.unsafe.copyMemory(src, UnsafeUtil.BYTE_ARRAY_BASE_OFFSET + srcIndex, null, targetOffset, length);
    }
  }
  
  private static final class Android64MemoryAccessor extends MemoryAccessor {
    Android64MemoryAccessor(Unsafe unsafe) {
      super(unsafe);
    }
    
    public Object getStaticObject(Field field) {
      try {
        return field.get(null);
      } catch (IllegalAccessException e) {
        return null;
      } 
    }
    
    public byte getByte(Object target, long offset) {
      if (UnsafeUtil.IS_BIG_ENDIAN)
        return UnsafeUtil.getByteBigEndian(target, offset); 
      return UnsafeUtil.getByteLittleEndian(target, offset);
    }
    
    public void putByte(Object target, long offset, byte value) {
      if (UnsafeUtil.IS_BIG_ENDIAN) {
        UnsafeUtil.putByteBigEndian(target, offset, value);
      } else {
        UnsafeUtil.putByteLittleEndian(target, offset, value);
      } 
    }
    
    public boolean getBoolean(Object target, long offset) {
      if (UnsafeUtil.IS_BIG_ENDIAN)
        return UnsafeUtil.getBooleanBigEndian(target, offset); 
      return UnsafeUtil.getBooleanLittleEndian(target, offset);
    }
    
    public void putBoolean(Object target, long offset, boolean value) {
      if (UnsafeUtil.IS_BIG_ENDIAN) {
        UnsafeUtil.putBooleanBigEndian(target, offset, value);
      } else {
        UnsafeUtil.putBooleanLittleEndian(target, offset, value);
      } 
    }
    
    public float getFloat(Object target, long offset) {
      return Float.intBitsToFloat(getInt(target, offset));
    }
    
    public void putFloat(Object target, long offset, float value) {
      putInt(target, offset, Float.floatToIntBits(value));
    }
    
    public double getDouble(Object target, long offset) {
      return Double.longBitsToDouble(getLong(target, offset));
    }
    
    public void putDouble(Object target, long offset, double value) {
      putLong(target, offset, Double.doubleToLongBits(value));
    }
    
    public boolean supportsUnsafeByteBufferOperations() {
      return false;
    }
    
    public byte getByte(long address) {
      throw new UnsupportedOperationException();
    }
    
    public void putByte(long address, byte value) {
      throw new UnsupportedOperationException();
    }
    
    public int getInt(long address) {
      throw new UnsupportedOperationException();
    }
    
    public void putInt(long address, int value) {
      throw new UnsupportedOperationException();
    }
    
    public long getLong(long address) {
      throw new UnsupportedOperationException();
    }
    
    public void putLong(long address, long value) {
      throw new UnsupportedOperationException();
    }
    
    public void copyMemory(long srcOffset, byte[] target, long targetIndex, long length) {
      throw new UnsupportedOperationException();
    }
    
    public void copyMemory(byte[] src, long srcIndex, long targetOffset, long length) {
      throw new UnsupportedOperationException();
    }
  }
  
  private static final class Android32MemoryAccessor extends MemoryAccessor {
    private static final long SMALL_ADDRESS_MASK = -1L;
    
    private static int smallAddress(long address) {
      return (int)(0xFFFFFFFFFFFFFFFFL & address);
    }
    
    Android32MemoryAccessor(Unsafe unsafe) {
      super(unsafe);
    }
    
    public Object getStaticObject(Field field) {
      try {
        return field.get(null);
      } catch (IllegalAccessException e) {
        return null;
      } 
    }
    
    public byte getByte(Object target, long offset) {
      if (UnsafeUtil.IS_BIG_ENDIAN)
        return UnsafeUtil.getByteBigEndian(target, offset); 
      return UnsafeUtil.getByteLittleEndian(target, offset);
    }
    
    public void putByte(Object target, long offset, byte value) {
      if (UnsafeUtil.IS_BIG_ENDIAN) {
        UnsafeUtil.putByteBigEndian(target, offset, value);
      } else {
        UnsafeUtil.putByteLittleEndian(target, offset, value);
      } 
    }
    
    public boolean getBoolean(Object target, long offset) {
      if (UnsafeUtil.IS_BIG_ENDIAN)
        return UnsafeUtil.getBooleanBigEndian(target, offset); 
      return UnsafeUtil.getBooleanLittleEndian(target, offset);
    }
    
    public void putBoolean(Object target, long offset, boolean value) {
      if (UnsafeUtil.IS_BIG_ENDIAN) {
        UnsafeUtil.putBooleanBigEndian(target, offset, value);
      } else {
        UnsafeUtil.putBooleanLittleEndian(target, offset, value);
      } 
    }
    
    public float getFloat(Object target, long offset) {
      return Float.intBitsToFloat(getInt(target, offset));
    }
    
    public void putFloat(Object target, long offset, float value) {
      putInt(target, offset, Float.floatToIntBits(value));
    }
    
    public double getDouble(Object target, long offset) {
      return Double.longBitsToDouble(getLong(target, offset));
    }
    
    public void putDouble(Object target, long offset, double value) {
      putLong(target, offset, Double.doubleToLongBits(value));
    }
    
    public boolean supportsUnsafeByteBufferOperations() {
      return false;
    }
    
    public byte getByte(long address) {
      throw new UnsupportedOperationException();
    }
    
    public void putByte(long address, byte value) {
      throw new UnsupportedOperationException();
    }
    
    public int getInt(long address) {
      throw new UnsupportedOperationException();
    }
    
    public void putInt(long address, int value) {
      throw new UnsupportedOperationException();
    }
    
    public long getLong(long address) {
      throw new UnsupportedOperationException();
    }
    
    public void putLong(long address, long value) {
      throw new UnsupportedOperationException();
    }
    
    public void copyMemory(long srcOffset, byte[] target, long targetIndex, long length) {
      throw new UnsupportedOperationException();
    }
    
    public void copyMemory(byte[] src, long srcIndex, long targetOffset, long length) {
      throw new UnsupportedOperationException();
    }
  }
  
  private static byte getByteBigEndian(Object target, long offset) {
    return (byte)(getInt(target, offset & 0xFFFFFFFFFFFFFFFCL) >>> (int)(((offset ^ 0xFFFFFFFFFFFFFFFFL) & 0x3L) << 3L) & 0xFF);
  }
  
  private static byte getByteLittleEndian(Object target, long offset) {
    return (byte)(getInt(target, offset & 0xFFFFFFFFFFFFFFFCL) >>> (int)((offset & 0x3L) << 3L) & 0xFF);
  }
  
  private static void putByteBigEndian(Object target, long offset, byte value) {
    int intValue = getInt(target, offset & 0xFFFFFFFFFFFFFFFCL);
    int shift = (((int)offset ^ 0xFFFFFFFF) & 0x3) << 3;
    int output = intValue & (255 << shift ^ 0xFFFFFFFF) | (0xFF & value) << shift;
    putInt(target, offset & 0xFFFFFFFFFFFFFFFCL, output);
  }
  
  private static void putByteLittleEndian(Object target, long offset, byte value) {
    int intValue = getInt(target, offset & 0xFFFFFFFFFFFFFFFCL);
    int shift = ((int)offset & 0x3) << 3;
    int output = intValue & (255 << shift ^ 0xFFFFFFFF) | (0xFF & value) << shift;
    putInt(target, offset & 0xFFFFFFFFFFFFFFFCL, output);
  }
  
  private static boolean getBooleanBigEndian(Object target, long offset) {
    return (getByteBigEndian(target, offset) != 0);
  }
  
  private static boolean getBooleanLittleEndian(Object target, long offset) {
    return (getByteLittleEndian(target, offset) != 0);
  }
  
  private static void putBooleanBigEndian(Object target, long offset, boolean value) {
    putByteBigEndian(target, offset, (byte)(value ? 1 : 0));
  }
  
  private static void putBooleanLittleEndian(Object target, long offset, boolean value) {
    putByteLittleEndian(target, offset, (byte)(value ? 1 : 0));
  }
  
  private static void logMissingMethod(Throwable e) {
    Logger.getLogger(UnsafeUtil.class.getName())
      .log(Level.WARNING, "platform method missing - proto runtime falling back to safer methods: " + e);
  }
}
