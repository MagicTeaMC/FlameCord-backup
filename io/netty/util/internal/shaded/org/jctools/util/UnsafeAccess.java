package io.netty.util.internal.shaded.org.jctools.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

public class UnsafeAccess {
  public static final boolean SUPPORTS_GET_AND_SET_REF;
  
  public static final boolean SUPPORTS_GET_AND_ADD_LONG;
  
  public static final Unsafe UNSAFE = getUnsafe();
  
  static {
    SUPPORTS_GET_AND_SET_REF = hasGetAndSetSupport();
    SUPPORTS_GET_AND_ADD_LONG = hasGetAndAddLongSupport();
  }
  
  private static Unsafe getUnsafe() {
    Unsafe instance;
    try {
      Field field = Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      instance = (Unsafe)field.get(null);
    } catch (Exception ignored) {
      try {
        Constructor<Unsafe> c = Unsafe.class.getDeclaredConstructor(new Class[0]);
        c.setAccessible(true);
        instance = c.newInstance(new Object[0]);
      } catch (Exception e) {
        throw new RuntimeException(e);
      } 
    } 
    return instance;
  }
  
  private static boolean hasGetAndSetSupport() {
    try {
      Unsafe.class.getMethod("getAndSetObject", new Class[] { Object.class, long.class, Object.class });
      return true;
    } catch (Exception exception) {
      return false;
    } 
  }
  
  private static boolean hasGetAndAddLongSupport() {
    try {
      Unsafe.class.getMethod("getAndAddLong", new Class[] { Object.class, long.class, long.class });
      return true;
    } catch (Exception exception) {
      return false;
    } 
  }
  
  public static long fieldOffset(Class clz, String fieldName) throws RuntimeException {
    try {
      return UNSAFE.objectFieldOffset(clz.getDeclaredField(fieldName));
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    } 
  }
}
