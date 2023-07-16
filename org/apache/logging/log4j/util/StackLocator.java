package org.apache.logging.log4j.util;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Predicate;

public final class StackLocator {
  static final int JDK_7U25_OFFSET;
  
  private static final Method GET_CALLER_CLASS_METHOD;
  
  private static final StackLocator INSTANCE;
  
  private static final Class<?> DEFAULT_CALLER_CLASS = null;
  
  static {
    Method getCallerClassMethod;
  }
  
  static {
    int java7u25CompensationOffset = 0;
    try {
      Class<?> sunReflectionClass = LoaderUtil.loadClass("sun.reflect.Reflection");
      getCallerClassMethod = sunReflectionClass.getDeclaredMethod("getCallerClass", new Class[] { int.class });
      Object o = getCallerClassMethod.invoke(null, new Object[] { Integer.valueOf(0) });
      getCallerClassMethod.invoke(null, new Object[] { Integer.valueOf(0) });
      if (o == null || o != sunReflectionClass) {
        getCallerClassMethod = null;
        java7u25CompensationOffset = -1;
      } else {
        o = getCallerClassMethod.invoke(null, new Object[] { Integer.valueOf(1) });
        if (o == sunReflectionClass) {
          System.out.println("WARNING: Unexpected result from sun.reflect.Reflection.getCallerClass(int), adjusting offset for future calls.");
          java7u25CompensationOffset = 1;
        } 
      } 
    } catch (Exception|LinkageError e) {
      System.out.println("WARNING: sun.reflect.Reflection.getCallerClass is not supported. This will impact performance.");
      getCallerClassMethod = null;
      java7u25CompensationOffset = -1;
    } 
    GET_CALLER_CLASS_METHOD = getCallerClassMethod;
    JDK_7U25_OFFSET = java7u25CompensationOffset;
    INSTANCE = new StackLocator();
  }
  
  public static StackLocator getInstance() {
    return INSTANCE;
  }
  
  @PerformanceSensitive
  public Class<?> getCallerClass(Class<?> sentinelClass, Predicate<Class<?>> callerPredicate) {
    if (sentinelClass == null)
      throw new IllegalArgumentException("sentinelClass cannot be null"); 
    if (callerPredicate == null)
      throw new IllegalArgumentException("callerPredicate cannot be null"); 
    boolean foundSentinel = false;
    Class<?> clazz;
    for (int i = 2; null != (clazz = getCallerClass(i)); i++) {
      if (sentinelClass.equals(clazz)) {
        foundSentinel = true;
      } else if (foundSentinel && callerPredicate.test(clazz)) {
        return clazz;
      } 
    } 
    return DEFAULT_CALLER_CLASS;
  }
  
  @PerformanceSensitive
  public Class<?> getCallerClass(int depth) {
    if (depth < 0)
      throw new IndexOutOfBoundsException(Integer.toString(depth)); 
    if (GET_CALLER_CLASS_METHOD == null)
      return DEFAULT_CALLER_CLASS; 
    try {
      return (Class)GET_CALLER_CLASS_METHOD.invoke(null, new Object[] { Integer.valueOf(depth + 1 + JDK_7U25_OFFSET) });
    } catch (Exception e) {
      return DEFAULT_CALLER_CLASS;
    } 
  }
  
  @PerformanceSensitive
  public Class<?> getCallerClass(String fqcn, String pkg) {
    boolean next = false;
    Class<?> clazz;
    for (int i = 2; null != (clazz = getCallerClass(i)); i++) {
      if (fqcn.equals(clazz.getName())) {
        next = true;
      } else if (next && clazz.getName().startsWith(pkg)) {
        return clazz;
      } 
    } 
    return DEFAULT_CALLER_CLASS;
  }
  
  @PerformanceSensitive
  public Class<?> getCallerClass(Class<?> anchor) {
    boolean next = false;
    Class<?> clazz;
    for (int i = 2; null != (clazz = getCallerClass(i)); i++) {
      if (anchor.equals(clazz)) {
        next = true;
      } else if (next) {
        return clazz;
      } 
    } 
    return Object.class;
  }
  
  @PerformanceSensitive
  public Deque<Class<?>> getCurrentStackTrace() {
    if (PrivateSecurityManagerStackTraceUtil.isEnabled())
      return PrivateSecurityManagerStackTraceUtil.getCurrentStackTrace(); 
    Deque<Class<?>> classes = new ArrayDeque<>();
    Class<?> clazz;
    for (int i = 1; null != (clazz = getCallerClass(i)); i++)
      classes.addLast(clazz); 
    return classes;
  }
  
  public StackTraceElement calcLocation(String fqcnOfLogger) {
    if (fqcnOfLogger == null)
      return null; 
    StackTraceElement[] stackTrace = (new Throwable()).getStackTrace();
    boolean found = false;
    for (int i = 0; i < stackTrace.length; i++) {
      String className = stackTrace[i].getClassName();
      if (fqcnOfLogger.equals(className)) {
        found = true;
      } else if (found && !fqcnOfLogger.equals(className)) {
        return stackTrace[i];
      } 
    } 
    return null;
  }
  
  public StackTraceElement getStackTraceElement(int depth) {
    int i = 0;
    for (StackTraceElement element : (new Throwable()).getStackTrace()) {
      if (isValid(element)) {
        if (i == depth)
          return element; 
        i++;
      } 
    } 
    throw new IndexOutOfBoundsException(Integer.toString(depth));
  }
  
  private boolean isValid(StackTraceElement element) {
    if (element.isNativeMethod())
      return false; 
    String cn = element.getClassName();
    if (cn.startsWith("sun.reflect."))
      return false; 
    String mn = element.getMethodName();
    if (cn.startsWith("java.lang.reflect.") && (mn.equals("invoke") || mn.equals("newInstance")))
      return false; 
    if (cn.startsWith("jdk.internal.reflect."))
      return false; 
    if (cn.equals("java.lang.Class") && mn.equals("newInstance"))
      return false; 
    if (cn.equals("java.lang.invoke.MethodHandle") && mn.startsWith("invoke"))
      return false; 
    return true;
  }
}
