package org.codehaus.plexus.interpolation.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Hashtable;
import java.util.Map;

public class ClassMap {
  private static final class CacheMiss {
    private CacheMiss() {}
  }
  
  private static final CacheMiss CACHE_MISS = new CacheMiss();
  
  private static final Object OBJECT = new Object();
  
  private Class<?> clazz;
  
  private Map<String, Object> methodCache = new Hashtable<String, Object>();
  
  private MethodMap methodMap = new MethodMap();
  
  public ClassMap(Class<?> clazz) {
    this.clazz = clazz;
    populateMethodCache();
  }
  
  Class<?> getCachedClass() {
    return this.clazz;
  }
  
  public Method findMethod(String name, Object[] params) throws MethodMap.AmbiguousException {
    String methodKey = makeMethodKey(name, params);
    Object cacheEntry = this.methodCache.get(methodKey);
    if (cacheEntry == CACHE_MISS)
      return null; 
    if (cacheEntry == null) {
      try {
        cacheEntry = this.methodMap.find(name, params);
      } catch (AmbiguousException ae) {
        this.methodCache.put(methodKey, CACHE_MISS);
        throw ae;
      } 
      if (cacheEntry == null) {
        this.methodCache.put(methodKey, CACHE_MISS);
      } else {
        this.methodCache.put(methodKey, cacheEntry);
      } 
    } 
    return (Method)cacheEntry;
  }
  
  private void populateMethodCache() {
    Method[] methods = getAccessibleMethods(this.clazz);
    for (Method method : methods) {
      Method publicMethod = getPublicMethod(method);
      if (publicMethod != null) {
        this.methodMap.add(publicMethod);
        this.methodCache.put(makeMethodKey(publicMethod), publicMethod);
      } 
    } 
  }
  
  private String makeMethodKey(Method method) {
    Class<?>[] parameterTypes = method.getParameterTypes();
    StringBuilder methodKey = new StringBuilder(method.getName());
    for (Class<?> parameterType : parameterTypes) {
      if (parameterType.isPrimitive()) {
        if (parameterType.equals(boolean.class)) {
          methodKey.append("java.lang.Boolean");
        } else if (parameterType.equals(byte.class)) {
          methodKey.append("java.lang.Byte");
        } else if (parameterType.equals(char.class)) {
          methodKey.append("java.lang.Character");
        } else if (parameterType.equals(double.class)) {
          methodKey.append("java.lang.Double");
        } else if (parameterType.equals(float.class)) {
          methodKey.append("java.lang.Float");
        } else if (parameterType.equals(int.class)) {
          methodKey.append("java.lang.Integer");
        } else if (parameterType.equals(long.class)) {
          methodKey.append("java.lang.Long");
        } else if (parameterType.equals(short.class)) {
          methodKey.append("java.lang.Short");
        } 
      } else {
        methodKey.append(parameterType.getName());
      } 
    } 
    return methodKey.toString();
  }
  
  private static String makeMethodKey(String method, Object[] params) {
    if (params.length == 0)
      return method; 
    StringBuilder methodKey = (new StringBuilder()).append(method);
    for (Object arg : params) {
      if (arg == null)
        arg = OBJECT; 
      methodKey.append(arg.getClass().getName());
    } 
    return methodKey.toString();
  }
  
  private static Method[] getAccessibleMethods(Class<?> clazz) {
    Method[] methods = clazz.getMethods();
    if (Modifier.isPublic(clazz.getModifiers()))
      return methods; 
    MethodInfo[] methodInfos = new MethodInfo[methods.length];
    for (int i = methods.length; i-- > 0;)
      methodInfos[i] = new MethodInfo(methods[i]); 
    int upcastCount = getAccessibleMethods(clazz, methodInfos, 0);
    if (upcastCount < methods.length)
      methods = new Method[upcastCount]; 
    int j = 0;
    for (MethodInfo methodInfo : methodInfos) {
      if (methodInfo.upcast)
        methods[j++] = methodInfo.method; 
    } 
    return methods;
  }
  
  private static int getAccessibleMethods(Class<?> clazz, MethodInfo[] methodInfos, int upcastCount) {
    int l = methodInfos.length;
    if (Modifier.isPublic(clazz.getModifiers())) {
      for (int j = 0; j < l && upcastCount < l; j++) {
        try {
          MethodInfo methodInfo = methodInfos[j];
          if (!methodInfo.upcast) {
            methodInfo.tryUpcasting(clazz);
            upcastCount++;
          } 
        } catch (NoSuchMethodException noSuchMethodException) {}
      } 
      if (upcastCount == l)
        return upcastCount; 
    } 
    Class<?> superclazz = clazz.getSuperclass();
    if (superclazz != null) {
      upcastCount = getAccessibleMethods(superclazz, methodInfos, upcastCount);
      if (upcastCount == l)
        return upcastCount; 
    } 
    Class<?>[] interfaces = clazz.getInterfaces();
    for (int i = interfaces.length; i-- > 0; ) {
      upcastCount = getAccessibleMethods(interfaces[i], methodInfos, upcastCount);
      if (upcastCount == l)
        return upcastCount; 
    } 
    return upcastCount;
  }
  
  public static Method getPublicMethod(Method method) {
    Class<?> clazz = method.getDeclaringClass();
    if ((clazz.getModifiers() & 0x1) != 0)
      return method; 
    return getPublicMethod(clazz, method.getName(), method.getParameterTypes());
  }
  
  private static Method getPublicMethod(Class<?> clazz, String name, Class<?>[] paramTypes) {
    if ((clazz.getModifiers() & 0x1) != 0)
      try {
        return clazz.getMethod(name, paramTypes);
      } catch (NoSuchMethodException e) {
        return null;
      }  
    Class<?> superclazz = clazz.getSuperclass();
    if (superclazz != null) {
      Method superclazzMethod = getPublicMethod(superclazz, name, paramTypes);
      if (superclazzMethod != null)
        return superclazzMethod; 
    } 
    for (Class<?> interface_ : clazz.getInterfaces()) {
      Method interfaceMethod = getPublicMethod(interface_, name, paramTypes);
      if (interfaceMethod != null)
        return interfaceMethod; 
    } 
    return null;
  }
  
  private static final class MethodInfo {
    Method method;
    
    String name;
    
    Class<?>[] parameterTypes;
    
    boolean upcast;
    
    MethodInfo(Method method) {
      this.method = null;
      this.name = method.getName();
      this.parameterTypes = method.getParameterTypes();
      this.upcast = false;
    }
    
    void tryUpcasting(Class<?> clazz) throws NoSuchMethodException {
      this.method = clazz.getMethod(this.name, this.parameterTypes);
      this.name = null;
      this.parameterTypes = null;
      this.upcast = true;
    }
  }
}
