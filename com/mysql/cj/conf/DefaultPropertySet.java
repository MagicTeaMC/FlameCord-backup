package com.mysql.cj.conf;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.WrongArgumentException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DefaultPropertySet implements PropertySet, Serializable {
  private static final long serialVersionUID = -5156024634430650528L;
  
  private final Map<PropertyKey, RuntimeProperty<?>> PROPERTY_KEY_TO_RUNTIME_PROPERTY = new HashMap<>();
  
  private final Map<String, RuntimeProperty<?>> PROPERTY_NAME_TO_RUNTIME_PROPERTY = new HashMap<>();
  
  public DefaultPropertySet() {
    for (PropertyDefinition<?> pdef : PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.values())
      addProperty(pdef.createRuntimeProperty()); 
  }
  
  public void addProperty(RuntimeProperty<?> prop) {
    PropertyDefinition<?> def = prop.getPropertyDefinition();
    if (def.getPropertyKey() != null) {
      this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.put(def.getPropertyKey(), prop);
    } else {
      this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.put(def.getName(), prop);
      if (def.hasCcAlias())
        this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.put(def.getCcAlias(), prop); 
    } 
  }
  
  public void removeProperty(String name) {
    PropertyKey key = PropertyKey.fromValue(name);
    if (key != null) {
      this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.remove(key);
    } else {
      RuntimeProperty<?> prop = this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.remove(name);
      if (prop != null)
        if (!name.equals(prop.getPropertyDefinition().getName())) {
          this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.remove(prop.getPropertyDefinition().getName());
        } else if (prop.getPropertyDefinition().hasCcAlias()) {
          this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.remove(prop.getPropertyDefinition().getCcAlias());
        }  
    } 
  }
  
  public void removeProperty(PropertyKey key) {
    this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.remove(key);
  }
  
  public <T> RuntimeProperty<T> getProperty(String name) {
    try {
      PropertyKey key = PropertyKey.fromValue(name);
      if (key != null)
        return getProperty(key); 
      return (RuntimeProperty<T>)this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.get(name);
    } catch (ClassCastException ex) {
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, ex.getMessage(), ex);
    } 
  }
  
  public <T> RuntimeProperty<T> getProperty(PropertyKey key) {
    try {
      RuntimeProperty<T> prop = (RuntimeProperty<T>)this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.get(key);
      if (prop == null)
        prop = (RuntimeProperty<T>)this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.get(key.getKeyName()); 
      return prop;
    } catch (ClassCastException ex) {
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, ex.getMessage(), ex);
    } 
  }
  
  public RuntimeProperty<Boolean> getBooleanProperty(String name) {
    return getProperty(name);
  }
  
  public RuntimeProperty<Boolean> getBooleanProperty(PropertyKey key) {
    return getProperty(key);
  }
  
  public RuntimeProperty<Integer> getIntegerProperty(String name) {
    return getProperty(name);
  }
  
  public RuntimeProperty<Integer> getIntegerProperty(PropertyKey key) {
    return getProperty(key);
  }
  
  public RuntimeProperty<Long> getLongProperty(String name) {
    return getProperty(name);
  }
  
  public RuntimeProperty<Long> getLongProperty(PropertyKey key) {
    return getProperty(key);
  }
  
  public RuntimeProperty<Integer> getMemorySizeProperty(String name) {
    return getProperty(name);
  }
  
  public RuntimeProperty<Integer> getMemorySizeProperty(PropertyKey key) {
    return getProperty(key);
  }
  
  public RuntimeProperty<String> getStringProperty(String name) {
    return getProperty(name);
  }
  
  public RuntimeProperty<String> getStringProperty(PropertyKey key) {
    return getProperty(key);
  }
  
  public <T extends Enum<T>> RuntimeProperty<T> getEnumProperty(String name) {
    return getProperty(name);
  }
  
  public <T extends Enum<T>> RuntimeProperty<T> getEnumProperty(PropertyKey key) {
    return getProperty(key);
  }
  
  public void initializeProperties(Properties props) {
    if (props != null) {
      Properties infoCopy = (Properties)props.clone();
      infoCopy.remove(PropertyKey.HOST.getKeyName());
      infoCopy.remove(PropertyKey.PORT.getKeyName());
      infoCopy.remove(PropertyKey.USER.getKeyName());
      infoCopy.remove(PropertyKey.PASSWORD.getKeyName());
      infoCopy.remove(PropertyKey.DBNAME.getKeyName());
      for (PropertyKey propKey : PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.keySet()) {
        try {
          RuntimeProperty<?> propToSet = getProperty(propKey);
          propToSet.initializeFrom(infoCopy, (ExceptionInterceptor)null);
        } catch (CJException e) {
          throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, e.getMessage(), e);
        } 
      } 
      RuntimeProperty<PropertyDefinitions.SslMode> sslMode = getEnumProperty(PropertyKey.sslMode);
      if (!sslMode.isExplicitlySet()) {
        RuntimeProperty<Boolean> useSSL = getBooleanProperty(PropertyKey.useSSL);
        RuntimeProperty<Boolean> verifyServerCertificate = getBooleanProperty(PropertyKey.verifyServerCertificate);
        RuntimeProperty<Boolean> requireSSL = getBooleanProperty(PropertyKey.requireSSL);
        if (useSSL.isExplicitlySet() || verifyServerCertificate.isExplicitlySet() || requireSSL.isExplicitlySet())
          if (!((Boolean)useSSL.getValue()).booleanValue()) {
            sslMode.setValue(PropertyDefinitions.SslMode.DISABLED);
          } else if (((Boolean)verifyServerCertificate.getValue()).booleanValue()) {
            sslMode.setValue(PropertyDefinitions.SslMode.VERIFY_CA);
          } else if (((Boolean)requireSSL.getValue()).booleanValue()) {
            sslMode.setValue(PropertyDefinitions.SslMode.REQUIRED);
          }  
      } 
      for (Object key : infoCopy.keySet()) {
        String val = infoCopy.getProperty((String)key);
        PropertyDefinition<String> def = new StringPropertyDefinition((String)key, null, val, true, Messages.getString("ConnectionProperties.unknown"), "8.0.10", PropertyDefinitions.CATEGORY_USER_DEFINED, -2147483648);
        RuntimeProperty<String> p = new StringProperty(def);
        addProperty(p);
      } 
      postInitialization();
    } 
  }
  
  public void postInitialization() {}
  
  public Properties exposeAsProperties() {
    Properties props = new Properties();
    for (PropertyKey propKey : this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.keySet()) {
      if (!props.containsKey(propKey.getKeyName())) {
        RuntimeProperty<?> propToGet = getProperty(propKey);
        String propValue = propToGet.getStringValue();
        if (propValue != null)
          props.setProperty(propToGet.getPropertyDefinition().getName(), propValue); 
      } 
    } 
    for (String propName : this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.keySet()) {
      if (!props.containsKey(propName)) {
        RuntimeProperty<?> propToGet = getProperty(propName);
        String propValue = propToGet.getStringValue();
        if (propValue != null)
          props.setProperty(propToGet.getPropertyDefinition().getName(), propValue); 
      } 
    } 
    return props;
  }
  
  public void reset() {
    this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.values().forEach(p -> p.resetValue());
    this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.values().forEach(p -> p.resetValue());
    postInitialization();
  }
}
