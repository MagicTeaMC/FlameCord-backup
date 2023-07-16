package org.eclipse.aether.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.aether.RepositorySystemSession;

public final class ConfigUtils {
  public static Object getObject(Map<?, ?> properties, Object defaultValue, String... keys) {
    for (String key : keys) {
      Object value = properties.get(key);
      if (value != null)
        return value; 
    } 
    return defaultValue;
  }
  
  public static Object getObject(RepositorySystemSession session, Object defaultValue, String... keys) {
    return getObject(session.getConfigProperties(), defaultValue, keys);
  }
  
  public static String getString(Map<?, ?> properties, String defaultValue, String... keys) {
    for (String key : keys) {
      Object value = properties.get(key);
      if (value instanceof String)
        return (String)value; 
    } 
    return defaultValue;
  }
  
  public static String getString(RepositorySystemSession session, String defaultValue, String... keys) {
    return getString(session.getConfigProperties(), defaultValue, keys);
  }
  
  public static int getInteger(Map<?, ?> properties, int defaultValue, String... keys) {
    for (String key : keys) {
      Object value = properties.get(key);
      if (value instanceof Number)
        return ((Number)value).intValue(); 
      try {
        return Integer.parseInt((String)value);
      } catch (Exception exception) {}
    } 
    return defaultValue;
  }
  
  public static int getInteger(RepositorySystemSession session, int defaultValue, String... keys) {
    return getInteger(session.getConfigProperties(), defaultValue, keys);
  }
  
  public static long getLong(Map<?, ?> properties, long defaultValue, String... keys) {
    for (String key : keys) {
      Object value = properties.get(key);
      if (value instanceof Number)
        return ((Number)value).longValue(); 
      try {
        return Long.parseLong((String)value);
      } catch (Exception exception) {}
    } 
    return defaultValue;
  }
  
  public static long getLong(RepositorySystemSession session, long defaultValue, String... keys) {
    return getLong(session.getConfigProperties(), defaultValue, keys);
  }
  
  public static float getFloat(Map<?, ?> properties, float defaultValue, String... keys) {
    for (String key : keys) {
      Object value = properties.get(key);
      if (value instanceof Number)
        return ((Number)value).floatValue(); 
      try {
        return Float.parseFloat((String)value);
      } catch (Exception exception) {}
    } 
    return defaultValue;
  }
  
  public static float getFloat(RepositorySystemSession session, float defaultValue, String... keys) {
    return getFloat(session.getConfigProperties(), defaultValue, keys);
  }
  
  public static boolean getBoolean(Map<?, ?> properties, boolean defaultValue, String... keys) {
    for (String key : keys) {
      Object value = properties.get(key);
      if (value instanceof Boolean)
        return ((Boolean)value).booleanValue(); 
      if (value instanceof String)
        return Boolean.parseBoolean((String)value); 
    } 
    return defaultValue;
  }
  
  public static boolean getBoolean(RepositorySystemSession session, boolean defaultValue, String... keys) {
    return getBoolean(session.getConfigProperties(), defaultValue, keys);
  }
  
  public static List<?> getList(Map<?, ?> properties, List<?> defaultValue, String... keys) {
    for (String key : keys) {
      Object value = properties.get(key);
      if (value instanceof List)
        return (List)value; 
      if (value instanceof Collection)
        return Collections.unmodifiableList(new ArrayList((Collection)value)); 
    } 
    return defaultValue;
  }
  
  public static List<?> getList(RepositorySystemSession session, List<?> defaultValue, String... keys) {
    return getList(session.getConfigProperties(), defaultValue, keys);
  }
  
  public static Map<?, ?> getMap(Map<?, ?> properties, Map<?, ?> defaultValue, String... keys) {
    for (String key : keys) {
      Object value = properties.get(key);
      if (value instanceof Map)
        return (Map<?, ?>)value; 
    } 
    return defaultValue;
  }
  
  public static Map<?, ?> getMap(RepositorySystemSession session, Map<?, ?> defaultValue, String... keys) {
    return getMap(session.getConfigProperties(), defaultValue, keys);
  }
}
