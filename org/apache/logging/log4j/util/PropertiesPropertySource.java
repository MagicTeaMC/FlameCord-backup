package org.apache.logging.log4j.util;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

public class PropertiesPropertySource implements PropertySource {
  private static final int DEFAULT_PRIORITY = 200;
  
  private static final String PREFIX = "log4j2.";
  
  private final Properties properties;
  
  private final int priority;
  
  public PropertiesPropertySource(Properties properties) {
    this(properties, 200);
  }
  
  public PropertiesPropertySource(Properties properties, int priority) {
    this.properties = properties;
    this.priority = priority;
  }
  
  public int getPriority() {
    return this.priority;
  }
  
  public void forEach(BiConsumer<String, String> action) {
    for (Map.Entry<Object, Object> entry : this.properties.entrySet())
      action.accept((String)entry.getKey(), (String)entry.getValue()); 
  }
  
  public CharSequence getNormalForm(Iterable<? extends CharSequence> tokens) {
    CharSequence camelCase = PropertySource.Util.joinAsCamelCase(tokens);
    return (camelCase.length() > 0) ? ("log4j2." + camelCase) : null;
  }
  
  public Collection<String> getPropertyNames() {
    return this.properties.stringPropertyNames();
  }
  
  public String getProperty(String key) {
    return this.properties.getProperty(key);
  }
  
  public boolean containsProperty(String key) {
    return (getProperty(key) != null);
  }
}
