package com.mysql.cj.util;

import com.mysql.cj.Constants;
import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.WrongArgumentException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Util {
  private static int jvmVersion = 8;
  
  private static int jvmUpdateNumber = -1;
  
  static {
    int startPos = Constants.JVM_VERSION.indexOf('.');
    int endPos = startPos + 1;
    if (startPos != -1)
      while (Character.isDigit(Constants.JVM_VERSION.charAt(endPos)) && ++endPos < Constants.JVM_VERSION.length()); 
    startPos++;
    if (endPos > startPos)
      jvmVersion = Integer.parseInt(Constants.JVM_VERSION.substring(startPos, endPos)); 
    startPos = Constants.JVM_VERSION.indexOf("_");
    endPos = startPos + 1;
    if (startPos != -1)
      while (Character.isDigit(Constants.JVM_VERSION.charAt(endPos)) && ++endPos < Constants.JVM_VERSION.length()); 
    startPos++;
    if (endPos > startPos)
      jvmUpdateNumber = Integer.parseInt(Constants.JVM_VERSION.substring(startPos, endPos)); 
  }
  
  public static int getJVMVersion() {
    return jvmVersion;
  }
  
  public static boolean jvmMeetsMinimum(int version, int updateNumber) {
    return (getJVMVersion() > version || (getJVMVersion() == version && getJVMUpdateNumber() >= updateNumber));
  }
  
  public static int getJVMUpdateNumber() {
    return jvmUpdateNumber;
  }
  
  public static boolean isCommunityEdition(String serverVersion) {
    return !isEnterpriseEdition(serverVersion);
  }
  
  public static boolean isEnterpriseEdition(String serverVersion) {
    return (serverVersion.contains("enterprise") || serverVersion.contains("commercial") || serverVersion.contains("advanced"));
  }
  
  public static String stackTraceToString(Throwable ex) {
    StringBuilder traceBuf = new StringBuilder();
    traceBuf.append(Messages.getString("Util.1"));
    if (ex != null) {
      traceBuf.append(ex.getClass().getName());
      String message = ex.getMessage();
      if (message != null) {
        traceBuf.append(Messages.getString("Util.2"));
        traceBuf.append(message);
      } 
      StringWriter out = new StringWriter();
      PrintWriter printOut = new PrintWriter(out);
      ex.printStackTrace(printOut);
      traceBuf.append(Messages.getString("Util.3"));
      traceBuf.append(out.toString());
    } 
    traceBuf.append(Messages.getString("Util.4"));
    return traceBuf.toString();
  }
  
  public static <T> T getInstance(Class<T> returnType, String className, Class<?>[] argTypes, Object[] args, ExceptionInterceptor exceptionInterceptor) {
    try {
      Class<?> clazz = Class.forName(className, false, Util.class.getClassLoader());
      if (!returnType.isAssignableFrom(clazz))
        throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
            Messages.getString("Util.WrongImplementation", new Object[] { className, returnType.getName() }), exceptionInterceptor); 
      return handleNewInstance((Constructor)clazz.getConstructor(argTypes), args, exceptionInterceptor);
    } catch (ClassNotFoundException|NoSuchMethodException|SecurityException e) {
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Util.FailLoadClass", new Object[] { className }), e, exceptionInterceptor);
    } 
  }
  
  public static <T> T handleNewInstance(Constructor<T> ctor, Object[] args, ExceptionInterceptor exceptionInterceptor) {
    try {
      return ctor.newInstance(args);
    } catch (IllegalArgumentException|InstantiationException|IllegalAccessException e) {
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
          Messages.getString("Util.FailCreateInstance", new Object[] { ctor.getDeclaringClass().getName() }), e, exceptionInterceptor);
    } catch (InvocationTargetException e) {
      Throwable target = e.getCause();
      if (target instanceof ExceptionInInitializerError) {
        target = target.getCause();
      } else if (target instanceof CJException) {
        throw (CJException)target;
      } 
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, target.getMessage(), target, exceptionInterceptor);
    } 
  }
  
  public static Map<Object, Object> calculateDifferences(Map<?, ?> map1, Map<?, ?> map2) {
    Map<Object, Object> diffMap = new HashMap<>();
    for (Map.Entry<?, ?> entry : map1.entrySet()) {
      Object key = entry.getKey();
      Number value1 = null;
      Number value2 = null;
      if (entry.getValue() instanceof Number) {
        value1 = (Number)entry.getValue();
        value2 = (Number)map2.get(key);
      } else {
        try {
          value1 = new Double(entry.getValue().toString());
          value2 = new Double(map2.get(key).toString());
        } catch (NumberFormatException nfe) {
          continue;
        } 
      } 
      if (value1.equals(value2))
        continue; 
      if (value1 instanceof Byte) {
        diffMap.put(key, Byte.valueOf((byte)(((Byte)value2).byteValue() - ((Byte)value1).byteValue())));
        continue;
      } 
      if (value1 instanceof Short) {
        diffMap.put(key, Short.valueOf((short)(((Short)value2).shortValue() - ((Short)value1).shortValue())));
        continue;
      } 
      if (value1 instanceof Integer) {
        diffMap.put(key, Integer.valueOf(((Integer)value2).intValue() - ((Integer)value1).intValue()));
        continue;
      } 
      if (value1 instanceof Long) {
        diffMap.put(key, Long.valueOf(((Long)value2).longValue() - ((Long)value1).longValue()));
        continue;
      } 
      if (value1 instanceof Float) {
        diffMap.put(key, Float.valueOf(((Float)value2).floatValue() - ((Float)value1).floatValue()));
        continue;
      } 
      if (value1 instanceof Double) {
        diffMap.put(key, Double.valueOf((((Double)value2).shortValue() - ((Double)value1).shortValue())));
        continue;
      } 
      if (value1 instanceof BigDecimal) {
        diffMap.put(key, ((BigDecimal)value2).subtract((BigDecimal)value1));
        continue;
      } 
      if (value1 instanceof BigInteger)
        diffMap.put(key, ((BigInteger)value2).subtract((BigInteger)value1)); 
    } 
    return diffMap;
  }
  
  public static <T> List<T> loadClasses(Class<T> instancesType, String extensionClassNames, String errorMessageKey, ExceptionInterceptor exceptionInterceptor) {
    try {
      return (List<T>)StringUtils.split(extensionClassNames, ",", true).stream().filter(s -> !s.isEmpty())
        .map(c -> getInstance(instancesType, c, null, null, exceptionInterceptor)).collect(Collectors.toCollection(java.util.LinkedList::new));
    } catch (Throwable t) {
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString(errorMessageKey), t, exceptionInterceptor);
    } 
  }
  
  private static final ConcurrentMap<Class<?>, Boolean> isJdbcInterfaceCache = new ConcurrentHashMap<>();
  
  public static boolean isJdbcInterface(Class<?> clazz) {
    if (isJdbcInterfaceCache.containsKey(clazz))
      return ((Boolean)isJdbcInterfaceCache.get(clazz)).booleanValue(); 
    if (clazz.isInterface())
      try {
        if (isJdbcPackage(clazz.getPackage().getName())) {
          isJdbcInterfaceCache.putIfAbsent(clazz, Boolean.valueOf(true));
          return true;
        } 
      } catch (Exception exception) {} 
    for (Class<?> iface : clazz.getInterfaces()) {
      if (isJdbcInterface(iface)) {
        isJdbcInterfaceCache.putIfAbsent(clazz, Boolean.valueOf(true));
        return true;
      } 
    } 
    if (clazz.getSuperclass() != null && isJdbcInterface(clazz.getSuperclass())) {
      isJdbcInterfaceCache.putIfAbsent(clazz, Boolean.valueOf(true));
      return true;
    } 
    isJdbcInterfaceCache.putIfAbsent(clazz, Boolean.valueOf(false));
    return false;
  }
  
  public static boolean isJdbcPackage(String packageName) {
    return (packageName != null && (packageName
      .startsWith("java.sql") || packageName.startsWith("javax.sql") || packageName.startsWith("com.mysql.cj.jdbc")));
  }
  
  private static final ConcurrentMap<Class<?>, Class<?>[]> implementedInterfacesCache = (ConcurrentMap)new ConcurrentHashMap<>();
  
  public static Class<?>[] getImplementedInterfaces(Class<?> clazz) {
    Class<?>[] implementedInterfaces = implementedInterfacesCache.get(clazz);
    if (implementedInterfaces != null)
      return implementedInterfaces; 
    Set<Class<?>> interfaces = new LinkedHashSet<>();
    Class<?> superClass = clazz;
    do {
      Collections.addAll(interfaces, superClass.getInterfaces());
    } while ((superClass = superClass.getSuperclass()) != null);
    implementedInterfaces = (Class[])interfaces.<Class<?>[]>toArray((Class<?>[][])new Class[interfaces.size()]);
    Class<?>[] oldValue = implementedInterfacesCache.putIfAbsent(clazz, implementedInterfaces);
    if (oldValue != null)
      implementedInterfaces = oldValue; 
    return implementedInterfaces;
  }
  
  public static long secondsSinceMillis(long timeInMillis) {
    return (System.currentTimeMillis() - timeInMillis) / 1000L;
  }
  
  public static int truncateAndConvertToInt(long longValue) {
    return (longValue > 2147483647L) ? Integer.MAX_VALUE : ((longValue < -2147483648L) ? Integer.MIN_VALUE : (int)longValue);
  }
  
  public static int[] truncateAndConvertToInt(long[] longArray) {
    int[] intArray = new int[longArray.length];
    for (int i = 0; i < longArray.length; i++)
      intArray[i] = (longArray[i] > 2147483647L) ? Integer.MAX_VALUE : ((longArray[i] < -2147483648L) ? Integer.MIN_VALUE : (int)longArray[i]); 
    return intArray;
  }
  
  public static String getPackageName(Class<?> clazz) {
    String fqcn = clazz.getName();
    int classNameStartsAt = fqcn.lastIndexOf('.');
    if (classNameStartsAt > 0)
      return fqcn.substring(0, classNameStartsAt); 
    return "";
  }
  
  public static boolean isRunningOnWindows() {
    return (StringUtils.indexOfIgnoreCase(Constants.OS_NAME, "WINDOWS") != -1);
  }
  
  public static int readFully(Reader reader, char[] buf, int length) throws IOException {
    int numCharsRead = 0;
    while (numCharsRead < length) {
      int count = reader.read(buf, numCharsRead, length - numCharsRead);
      if (count < 0)
        break; 
      numCharsRead += count;
    } 
    return numCharsRead;
  }
  
  public static final int readBlock(InputStream i, byte[] b, ExceptionInterceptor exceptionInterceptor) {
    try {
      return i.read(b);
    } catch (Throwable ex) {
      throw ExceptionFactory.createException(Messages.getString("Util.5") + ex.getClass().getName(), exceptionInterceptor);
    } 
  }
  
  public static final int readBlock(InputStream i, byte[] b, int length, ExceptionInterceptor exceptionInterceptor) {
    try {
      int lengthToRead = length;
      if (lengthToRead > b.length)
        lengthToRead = b.length; 
      return i.read(b, 0, lengthToRead);
    } catch (Throwable ex) {
      throw ExceptionFactory.createException(Messages.getString("Util.5") + ex.getClass().getName(), exceptionInterceptor);
    } 
  }
}
