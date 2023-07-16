package org.apache.logging.log4j.util;

import java.util.Collection;
import java.util.Objects;
import java.util.Properties;

public class SystemPropertiesPropertySource implements PropertySource {
  private static final int DEFAULT_PRIORITY = 0;
  
  private static final String PREFIX = "log4j2.";
  
  public static String getSystemProperty(String key, String defaultValue) {
    try {
      return System.getProperty(key, defaultValue);
    } catch (SecurityException e) {
      return defaultValue;
    } 
  }
  
  public int getPriority() {
    return 0;
  }
  
  public void forEach(BiConsumer<String, String> action) {
    Properties properties;
    Object[] keySet;
    try {
      properties = System.getProperties();
    } catch (SecurityException e) {
      return;
    } 
    synchronized (properties) {
      keySet = properties.keySet().toArray();
    } 
    for (Object key : keySet) {
      String keyStr = Objects.toString(key, null);
      action.accept(keyStr, properties.getProperty(keyStr));
    } 
  }
  
  public CharSequence getNormalForm(Iterable<? extends CharSequence> tokens) {
    return "log4j2." + PropertySource.Util.joinAsCamelCase(tokens);
  }
  
  public Collection<String> getPropertyNames() {
    try {
      return System.getProperties().stringPropertyNames();
    } catch (SecurityException e) {
      return super.getPropertyNames();
    } 
  }
  
  public String getProperty(String key) {
    try {
      return System.getProperty(key);
    } catch (SecurityException e) {
      return super.getProperty(key);
    } 
  }
  
  public boolean containsProperty(String key) {
    return (getProperty(key) != null);
  }
}
