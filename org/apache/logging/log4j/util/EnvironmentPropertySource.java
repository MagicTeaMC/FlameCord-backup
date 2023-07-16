package org.apache.logging.log4j.util;

import java.util.Collection;
import java.util.Map;

public class EnvironmentPropertySource implements PropertySource {
  private static final String PREFIX = "LOG4J_";
  
  private static final int DEFAULT_PRIORITY = 100;
  
  public int getPriority() {
    return 100;
  }
  
  private void logException(SecurityException e) {
    LowLevelLogUtil.logException("The system environment variables are not available to Log4j due to security restrictions: " + e, e);
  }
  
  public void forEach(BiConsumer<String, String> action) {
    Map<String, String> getenv;
    try {
      getenv = System.getenv();
    } catch (SecurityException e) {
      logException(e);
      return;
    } 
    for (Map.Entry<String, String> entry : getenv.entrySet()) {
      String key = entry.getKey();
      if (key.startsWith("LOG4J_"))
        action.accept(key.substring("LOG4J_".length()), entry.getValue()); 
    } 
  }
  
  public CharSequence getNormalForm(Iterable<? extends CharSequence> tokens) {
    StringBuilder sb = new StringBuilder("LOG4J");
    boolean empty = true;
    for (CharSequence token : tokens) {
      empty = false;
      sb.append('_');
      for (int i = 0; i < token.length(); i++)
        sb.append(Character.toUpperCase(token.charAt(i))); 
    } 
    return empty ? null : sb.toString();
  }
  
  public Collection<String> getPropertyNames() {
    try {
      return System.getenv().keySet();
    } catch (SecurityException e) {
      logException(e);
      return super.getPropertyNames();
    } 
  }
  
  public String getProperty(String key) {
    try {
      return System.getenv(key);
    } catch (SecurityException e) {
      logException(e);
      return super.getProperty(key);
    } 
  }
  
  public boolean containsProperty(String key) {
    try {
      return System.getenv().containsKey(key);
    } catch (SecurityException e) {
      logException(e);
      return super.containsProperty(key);
    } 
  }
}
