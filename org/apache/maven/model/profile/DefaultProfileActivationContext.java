package org.apache.maven.model.profile;

import java.io.File;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DefaultProfileActivationContext implements ProfileActivationContext {
  private List<String> activeProfileIds = Collections.emptyList();
  
  private List<String> inactiveProfileIds = Collections.emptyList();
  
  private Map<String, String> systemProperties = Collections.emptyMap();
  
  private Map<String, String> userProperties = Collections.emptyMap();
  
  private Map<String, String> projectProperties = Collections.emptyMap();
  
  private File projectDirectory;
  
  public List<String> getActiveProfileIds() {
    return this.activeProfileIds;
  }
  
  public DefaultProfileActivationContext setActiveProfileIds(List<String> activeProfileIds) {
    if (activeProfileIds != null) {
      this.activeProfileIds = Collections.unmodifiableList(activeProfileIds);
    } else {
      this.activeProfileIds = Collections.emptyList();
    } 
    return this;
  }
  
  public List<String> getInactiveProfileIds() {
    return this.inactiveProfileIds;
  }
  
  public DefaultProfileActivationContext setInactiveProfileIds(List<String> inactiveProfileIds) {
    if (inactiveProfileIds != null) {
      this.inactiveProfileIds = Collections.unmodifiableList(inactiveProfileIds);
    } else {
      this.inactiveProfileIds = Collections.emptyList();
    } 
    return this;
  }
  
  public Map<String, String> getSystemProperties() {
    return this.systemProperties;
  }
  
  public DefaultProfileActivationContext setSystemProperties(Properties systemProperties) {
    if (systemProperties != null) {
      this.systemProperties = Collections.unmodifiableMap(systemProperties);
    } else {
      this.systemProperties = Collections.emptyMap();
    } 
    return this;
  }
  
  public DefaultProfileActivationContext setSystemProperties(Map<String, String> systemProperties) {
    if (systemProperties != null) {
      this.systemProperties = Collections.unmodifiableMap(systemProperties);
    } else {
      this.systemProperties = Collections.emptyMap();
    } 
    return this;
  }
  
  public Map<String, String> getUserProperties() {
    return this.userProperties;
  }
  
  public DefaultProfileActivationContext setUserProperties(Properties userProperties) {
    if (userProperties != null) {
      this.userProperties = Collections.unmodifiableMap(userProperties);
    } else {
      this.userProperties = Collections.emptyMap();
    } 
    return this;
  }
  
  public DefaultProfileActivationContext setUserProperties(Map<String, String> userProperties) {
    if (userProperties != null) {
      this.userProperties = Collections.unmodifiableMap(userProperties);
    } else {
      this.userProperties = Collections.emptyMap();
    } 
    return this;
  }
  
  public File getProjectDirectory() {
    return this.projectDirectory;
  }
  
  public DefaultProfileActivationContext setProjectDirectory(File projectDirectory) {
    this.projectDirectory = projectDirectory;
    return this;
  }
  
  public Map<String, String> getProjectProperties() {
    return this.projectProperties;
  }
  
  public DefaultProfileActivationContext setProjectProperties(Properties projectProperties) {
    if (projectProperties != null) {
      this.projectProperties = Collections.unmodifiableMap(toMap(projectProperties));
    } else {
      this.projectProperties = Collections.emptyMap();
    } 
    return this;
  }
  
  private Map<String, String> toMap(Properties properties) {
    if (properties == null)
      return Collections.emptyMap(); 
    Map<String, String> map = new HashMap<>();
    Enumeration<Object> keys = properties.keys();
    while (keys.hasMoreElements()) {
      String key = (String)keys.nextElement();
      map.put(key, properties.getProperty(key));
    } 
    return map;
  }
}
