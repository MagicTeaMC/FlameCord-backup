package org.codehaus.plexus.util.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class Reflector {
  private static final String CONSTRUCTOR_METHOD_NAME = "$$CONSTRUCTOR$$";
  
  private static final String GET_INSTANCE_METHOD_NAME = "getInstance";
  
  private Map<String, Map<String, Map<String, Method>>> classMaps = new HashMap<String, Map<String, Map<String, Method>>>();
  
  public <T> T newInstance(Class<T> theClass, Object[] params) throws ReflectorException {
    if (params == null)
      params = new Object[0]; 
    Class[] paramTypes = new Class[params.length];
    for (int i = 0, len = params.length; i < len; i++)
      paramTypes[i] = params[i].getClass(); 
    try {
      Constructor<T> con = getConstructor(theClass, paramTypes);
      if (con == null) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Constructor not found for class: ");
        buffer.append(theClass.getName());
        buffer.append(" with specified or ancestor parameter classes: ");
        for (Class paramType : paramTypes) {
          buffer.append(paramType.getName());
          buffer.append(',');
        } 
        buffer.setLength(buffer.length() - 1);
        throw new ReflectorException(buffer.toString());
      } 
      return con.newInstance(params);
    } catch (InstantiationException ex) {
      throw new ReflectorException(ex);
    } catch (InvocationTargetException ex) {
      throw new ReflectorException(ex);
    } catch (IllegalAccessException ex) {
      throw new ReflectorException(ex);
    } 
  }
  
  public <T> T getSingleton(Class<T> theClass, Object[] initParams) throws ReflectorException {
    Class[] paramTypes = new Class[initParams.length];
    for (int i = 0, len = initParams.length; i < len; i++)
      paramTypes[i] = initParams[i].getClass(); 
    try {
      Method method = getMethod(theClass, "getInstance", paramTypes);
      return (T)method.invoke(null, initParams);
    } catch (InvocationTargetException ex) {
      throw new ReflectorException(ex);
    } catch (IllegalAccessException ex) {
      throw new ReflectorException(ex);
    } 
  }
  
  public Object invoke(Object target, String methodName, Object[] params) throws ReflectorException {
    if (params == null)
      params = new Object[0]; 
    Class[] paramTypes = new Class[params.length];
    for (int i = 0, len = params.length; i < len; i++)
      paramTypes[i] = params[i].getClass(); 
    try {
      Method method = getMethod(target.getClass(), methodName, paramTypes);
      if (method == null) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Singleton-producing method named '").append(methodName).append("' not found with specified parameter classes: ");
        for (Class paramType : paramTypes) {
          buffer.append(paramType.getName());
          buffer.append(',');
        } 
        buffer.setLength(buffer.length() - 1);
        throw new ReflectorException(buffer.toString());
      } 
      return method.invoke(target, params);
    } catch (InvocationTargetException ex) {
      throw new ReflectorException(ex);
    } catch (IllegalAccessException ex) {
      throw new ReflectorException(ex);
    } 
  }
  
  public Object getStaticField(Class targetClass, String fieldName) throws ReflectorException {
    try {
      Field field = targetClass.getField(fieldName);
      return field.get((Object)null);
    } catch (SecurityException e) {
      throw new ReflectorException(e);
    } catch (NoSuchFieldException e) {
      throw new ReflectorException(e);
    } catch (IllegalArgumentException e) {
      throw new ReflectorException(e);
    } catch (IllegalAccessException e) {
      throw new ReflectorException(e);
    } 
  }
  
  public Object getField(Object target, String fieldName) throws ReflectorException {
    return getField(target, fieldName, false);
  }
  
  public Object getField(Object target, String fieldName, boolean breakAccessibility) throws ReflectorException {
    Class<?> targetClass = target.getClass();
    while (targetClass != null) {
      try {
        Field field = targetClass.getDeclaredField(fieldName);
        boolean accessibilityBroken = false;
        if (!field.isAccessible() && breakAccessibility) {
          field.setAccessible(true);
          accessibilityBroken = true;
        } 
        Object result = field.get(target);
        if (accessibilityBroken)
          field.setAccessible(false); 
        return result;
      } catch (SecurityException e) {
        throw new ReflectorException(e);
      } catch (NoSuchFieldException e) {
        if (targetClass == Object.class)
          throw new ReflectorException(e); 
        targetClass = targetClass.getSuperclass();
      } catch (IllegalAccessException e) {
        throw new ReflectorException(e);
      } 
    } 
    return null;
  }
  
  public Object invokeStatic(Class targetClass, String methodName, Object[] params) throws ReflectorException {
    if (params == null)
      params = new Object[0]; 
    Class[] paramTypes = new Class[params.length];
    for (int i = 0, len = params.length; i < len; i++)
      paramTypes[i] = params[i].getClass(); 
    try {
      Method method = getMethod(targetClass, methodName, paramTypes);
      if (method == null) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Singleton-producing method named '").append(methodName).append("' not found with specified parameter classes: ");
        for (Class paramType : paramTypes) {
          buffer.append(paramType.getName());
          buffer.append(',');
        } 
        buffer.setLength(buffer.length() - 1);
        throw new ReflectorException(buffer.toString());
      } 
      return method.invoke(null, params);
    } catch (InvocationTargetException ex) {
      throw new ReflectorException(ex);
    } catch (IllegalAccessException ex) {
      throw new ReflectorException(ex);
    } 
  }
  
  public <T> Constructor<T> getConstructor(Class<T> targetClass, Class[] params) throws ReflectorException {
    Constructor<T> constructor;
    Map<String, Constructor<T>> constructorMap = getConstructorMap(targetClass);
    StringBuilder key = new StringBuilder(200);
    key.append("(");
    for (Class param : params) {
      key.append(param.getName());
      key.append(",");
    } 
    if (params.length > 0)
      key.setLength(key.length() - 1); 
    key.append(")");
    String paramKey = key.toString();
    synchronized (paramKey.intern()) {
      constructor = constructorMap.get(paramKey);
      if (constructor == null) {
        Constructor[] arrayOfConstructor = (Constructor[])targetClass.getConstructors();
        for (Constructor<T> cand : arrayOfConstructor) {
          Class[] types = cand.getParameterTypes();
          if (params.length == types.length) {
            for (int j = 0, len2 = params.length; j < len2; j++) {
              if (!types[j].isAssignableFrom(params[j]));
            } 
            constructor = cand;
            constructorMap.put(paramKey, constructor);
          } 
        } 
      } 
    } 
    if (constructor == null)
      throw new ReflectorException("Error retrieving constructor object for: " + targetClass.getName() + paramKey); 
    return constructor;
  }
  
  public Object getObjectProperty(Object target, String propertyName) throws ReflectorException {
    Object returnValue;
    if (propertyName == null || propertyName.trim().length() < 1)
      throw new ReflectorException("Cannot retrieve value for empty property."); 
    String beanAccessor = "get" + Character.toUpperCase(propertyName.charAt(0));
    if (propertyName.trim().length() > 1)
      beanAccessor = beanAccessor + propertyName.substring(1).trim(); 
    Class<?> targetClass = target.getClass();
    Class[] emptyParams = new Class[0];
    Method method = _getMethod(targetClass, beanAccessor, emptyParams);
    if (method == null)
      method = _getMethod(targetClass, propertyName, emptyParams); 
    if (method != null)
      try {
        returnValue = method.invoke(target, new Object[0]);
      } catch (IllegalAccessException e) {
        throw new ReflectorException("Error retrieving property '" + propertyName + "' from '" + targetClass + "'", e);
      } catch (InvocationTargetException e) {
        throw new ReflectorException("Error retrieving property '" + propertyName + "' from '" + targetClass + "'", e);
      }  
    if (method != null) {
      try {
        returnValue = method.invoke(target, new Object[0]);
      } catch (IllegalAccessException e) {
        throw new ReflectorException("Error retrieving property '" + propertyName + "' from '" + targetClass + "'", e);
      } catch (InvocationTargetException e) {
        throw new ReflectorException("Error retrieving property '" + propertyName + "' from '" + targetClass + "'", e);
      } 
    } else {
      returnValue = getField(target, propertyName, true);
      if (returnValue == null)
        throw new ReflectorException("Neither method: '" + propertyName + "' nor bean accessor: '" + beanAccessor + "' can be found for class: '" + targetClass + "', and retrieval of field: '" + propertyName + "' returned null as value."); 
    } 
    return returnValue;
  }
  
  public Method getMethod(Class targetClass, String methodName, Class[] params) throws ReflectorException {
    Method method = _getMethod(targetClass, methodName, params);
    if (method == null)
      throw new ReflectorException("Method: '" + methodName + "' not found in class: '" + targetClass + "'"); 
    return method;
  }
  
  private Method _getMethod(Class targetClass, String methodName, Class[] params) throws ReflectorException {
    Method method;
    Map<String, Method> methodMap = (Map)getMethodMap(targetClass, methodName);
    StringBuilder key = new StringBuilder(200);
    key.append("(");
    for (Class param : params) {
      key.append(param.getName());
      key.append(",");
    } 
    key.append(")");
    String paramKey = key.toString();
    synchronized (paramKey.intern()) {
      method = methodMap.get(paramKey);
      if (method == null) {
        Method[] cands = targetClass.getMethods();
        for (Method cand : cands) {
          String name = cand.getName();
          if (methodName.equals(name)) {
            Class[] types = cand.getParameterTypes();
            if (params.length == types.length) {
              for (int j = 0, len2 = params.length; j < len2; j++) {
                if (!types[j].isAssignableFrom(params[j]));
              } 
              method = cand;
              methodMap.put(paramKey, method);
            } 
          } 
        } 
      } 
    } 
    return method;
  }
  
  private <T> Map<String, Constructor<T>> getConstructorMap(Class<T> theClass) throws ReflectorException {
    return (Map)getMethodMap(theClass, "$$CONSTRUCTOR$$");
  }
  
  private Map<String, ?> getMethodMap(Class theClass, String methodName) throws ReflectorException {
    Map<String, Method> methodMap;
    if (theClass == null)
      return null; 
    String className = theClass.getName();
    synchronized (className.intern()) {
      Map<String, Map<String, Method>> classMethods = this.classMaps.get(className);
      if (classMethods == null) {
        classMethods = new HashMap<String, Map<String, Method>>();
        methodMap = new HashMap<String, Method>();
        classMethods.put(methodName, methodMap);
        this.classMaps.put(className, classMethods);
      } else {
        String key = className + "::" + methodName;
        synchronized (key.intern()) {
          methodMap = classMethods.get(methodName);
          if (methodMap == null) {
            methodMap = new HashMap<String, Method>();
            classMethods.put(methodName, methodMap);
          } 
        } 
      } 
    } 
    return methodMap;
  }
}
