package org.codehaus.plexus.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ReflectionUtils {
  public static Field getFieldByNameIncludingSuperclasses(String fieldName, Class<?> clazz) {
    Field retValue = null;
    try {
      retValue = clazz.getDeclaredField(fieldName);
    } catch (NoSuchFieldException e) {
      Class<?> superclass = clazz.getSuperclass();
      if (superclass != null)
        retValue = getFieldByNameIncludingSuperclasses(fieldName, superclass); 
    } 
    return retValue;
  }
  
  public static List<Field> getFieldsIncludingSuperclasses(Class<?> clazz) {
    List<Field> fields = new ArrayList<Field>(Arrays.asList(clazz.getDeclaredFields()));
    Class<?> superclass = clazz.getSuperclass();
    if (superclass != null)
      fields.addAll(getFieldsIncludingSuperclasses(superclass)); 
    return fields;
  }
  
  public static Method getSetter(String fieldName, Class<?> clazz) {
    Method[] methods = clazz.getMethods();
    fieldName = "set" + StringUtils.capitalizeFirstLetter(fieldName);
    for (Method method : methods) {
      if (method.getName().equals(fieldName) && isSetter(method))
        return method; 
    } 
    return null;
  }
  
  public static List<Method> getSetters(Class<?> clazz) {
    Method[] methods = clazz.getMethods();
    List<Method> list = new ArrayList<Method>();
    for (Method method : methods) {
      if (isSetter(method))
        list.add(method); 
    } 
    return list;
  }
  
  public static Class<?> getSetterType(Method method) {
    if (!isSetter(method))
      throw new RuntimeException("The method " + method.getDeclaringClass().getName() + "." + method.getName() + " is not a setter."); 
    return method.getParameterTypes()[0];
  }
  
  public static void setVariableValueInObject(Object object, String variable, Object value) throws IllegalAccessException {
    Field field = getFieldByNameIncludingSuperclasses(variable, object.getClass());
    field.setAccessible(true);
    field.set(object, value);
  }
  
  public static Object getValueIncludingSuperclasses(String variable, Object object) throws IllegalAccessException {
    Field field = getFieldByNameIncludingSuperclasses(variable, object.getClass());
    field.setAccessible(true);
    return field.get(object);
  }
  
  public static Map<String, Object> getVariablesAndValuesIncludingSuperclasses(Object object) throws IllegalAccessException {
    Map<String, Object> map = new HashMap<String, Object>();
    gatherVariablesAndValuesIncludingSuperclasses(object, map);
    return map;
  }
  
  public static boolean isSetter(Method method) {
    return (method.getReturnType().equals(void.class) && !Modifier.isStatic(method.getModifiers()) && (method.getParameterTypes()).length == 1);
  }
  
  private static void gatherVariablesAndValuesIncludingSuperclasses(Object object, Map<String, Object> map) throws IllegalAccessException {
    Class<?> clazz = object.getClass();
    Field[] fields = clazz.getDeclaredFields();
    AccessibleObject.setAccessible((AccessibleObject[])fields, true);
    for (Field field : fields)
      map.put(field.getName(), field.get(object)); 
    Class<?> superclass = clazz.getSuperclass();
    if (!Object.class.equals(superclass))
      gatherVariablesAndValuesIncludingSuperclasses(superclass, map); 
  }
}
