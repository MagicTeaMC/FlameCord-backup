package org.apache.commons.logging.impl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

public class SLF4JLogFactory extends LogFactory {
  ConcurrentMap<String, Log> loggerMap = new ConcurrentHashMap<String, Log>();
  
  public static final String LOG_PROPERTY = "org.apache.commons.logging.Log";
  
  protected Hashtable attributes = new Hashtable<Object, Object>();
  
  public Object getAttribute(String name) {
    return this.attributes.get(name);
  }
  
  public String[] getAttributeNames() {
    List<String> names = new ArrayList<String>();
    Enumeration<String> keys = this.attributes.keys();
    while (keys.hasMoreElements())
      names.add(keys.nextElement()); 
    String[] results = new String[names.size()];
    for (int i = 0; i < results.length; i++)
      results[i] = names.get(i); 
    return results;
  }
  
  public Log getInstance(Class clazz) throws LogConfigurationException {
    return getInstance(clazz.getName());
  }
  
  public Log getInstance(String name) throws LogConfigurationException {
    Log newInstance, instance = this.loggerMap.get(name);
    if (instance != null)
      return instance; 
    Logger slf4jLogger = LoggerFactory.getLogger(name);
    if (slf4jLogger instanceof LocationAwareLogger) {
      newInstance = new SLF4JLocationAwareLog((LocationAwareLogger)slf4jLogger);
    } else {
      newInstance = new SLF4JLog(slf4jLogger);
    } 
    Log oldInstance = this.loggerMap.putIfAbsent(name, newInstance);
    return (oldInstance == null) ? newInstance : oldInstance;
  }
  
  public void release() {
    System.out.println("WARN: The method " + SLF4JLogFactory.class + "#release() was invoked.");
    System.out.println("WARN: Please see http://www.slf4j.org/codes.html#release for an explanation.");
    System.out.flush();
  }
  
  public void removeAttribute(String name) {
    this.attributes.remove(name);
  }
  
  public void setAttribute(String name, Object value) {
    if (value == null) {
      this.attributes.remove(name);
    } else {
      this.attributes.put(name, value);
    } 
  }
}
