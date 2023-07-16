package com.mysql.cj.conf;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.PropertyNotModifiableException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.naming.RefAddr;
import javax.naming.Reference;

public abstract class AbstractRuntimeProperty<T> implements RuntimeProperty<T>, Serializable {
  private static final long serialVersionUID = -3424722534876438236L;
  
  private PropertyDefinition<T> propertyDefinition;
  
  protected T value;
  
  protected T initialValue;
  
  protected boolean wasExplicitlySet = false;
  
  private List<WeakReference<RuntimeProperty.RuntimePropertyListener>> listeners;
  
  public AbstractRuntimeProperty() {}
  
  protected AbstractRuntimeProperty(PropertyDefinition<T> propertyDefinition) {
    this.propertyDefinition = propertyDefinition;
    this.value = propertyDefinition.getDefaultValue();
    this.initialValue = propertyDefinition.getDefaultValue();
  }
  
  public PropertyDefinition<T> getPropertyDefinition() {
    return this.propertyDefinition;
  }
  
  public void initializeFrom(Properties extractFrom, ExceptionInterceptor exceptionInterceptor) {
    String name = getPropertyDefinition().getName();
    String alias = getPropertyDefinition().getCcAlias();
    if (extractFrom.containsKey(name)) {
      String extractedValue = (String)extractFrom.remove(name);
      if (extractedValue != null) {
        setValueInternal(extractedValue, exceptionInterceptor);
        this.initialValue = this.value;
      } 
    } else if (alias != null && extractFrom.containsKey(alias)) {
      String extractedValue = (String)extractFrom.remove(alias);
      if (extractedValue != null) {
        setValueInternal(extractedValue, exceptionInterceptor);
        this.initialValue = this.value;
      } 
    } 
  }
  
  public void initializeFrom(Reference ref, ExceptionInterceptor exceptionInterceptor) {
    RefAddr refAddr = ref.get(getPropertyDefinition().getName());
    if (refAddr != null) {
      String refContentAsString = (String)refAddr.getContent();
      if (refContentAsString != null) {
        setValueInternal(refContentAsString, exceptionInterceptor);
        this.initialValue = this.value;
      } 
    } 
  }
  
  public void resetValue() {
    this.value = this.initialValue;
    invokeListeners();
  }
  
  public boolean isExplicitlySet() {
    return this.wasExplicitlySet;
  }
  
  public void addListener(RuntimeProperty.RuntimePropertyListener l) {
    if (this.listeners == null)
      this.listeners = new ArrayList<>(); 
    boolean found = false;
    for (WeakReference<RuntimeProperty.RuntimePropertyListener> weakReference : this.listeners) {
      if (l.equals(weakReference.get())) {
        found = true;
        break;
      } 
    } 
    if (!found)
      this.listeners.add(new WeakReference<>(l)); 
  }
  
  public void removeListener(RuntimeProperty.RuntimePropertyListener listener) {
    if (this.listeners != null)
      for (WeakReference<RuntimeProperty.RuntimePropertyListener> wr : this.listeners) {
        RuntimeProperty.RuntimePropertyListener l = wr.get();
        if (l.equals(listener)) {
          this.listeners.remove(wr);
          break;
        } 
      }  
  }
  
  protected void invokeListeners() {
    if (this.listeners != null)
      for (WeakReference<RuntimeProperty.RuntimePropertyListener> wr : this.listeners) {
        RuntimeProperty.RuntimePropertyListener l = wr.get();
        if (l != null) {
          l.handlePropertyChange(this);
          continue;
        } 
        this.listeners.remove(wr);
      }  
  }
  
  public T getValue() {
    return this.value;
  }
  
  public T getInitialValue() {
    return this.initialValue;
  }
  
  public String getStringValue() {
    return (this.value == null) ? null : this.value.toString();
  }
  
  public void setValueInternal(String value, ExceptionInterceptor exceptionInterceptor) {
    setValueInternal(getPropertyDefinition().parseObject(value, exceptionInterceptor), value, exceptionInterceptor);
  }
  
  public void setValueInternal(T value, String valueAsString, ExceptionInterceptor exceptionInterceptor) {
    if (getPropertyDefinition().isRangeBased())
      checkRange(value, valueAsString, exceptionInterceptor); 
    this.value = value;
    this.wasExplicitlySet = true;
  }
  
  protected void checkRange(T val, String valueAsString, ExceptionInterceptor exceptionInterceptor) {}
  
  public void setValue(T value) {
    setValue(value, null);
  }
  
  public void setValue(T value, ExceptionInterceptor exceptionInterceptor) {
    if (getPropertyDefinition().isRuntimeModifiable()) {
      setValueInternal(value, null, exceptionInterceptor);
      invokeListeners();
    } else {
      throw (PropertyNotModifiableException)ExceptionFactory.createException(PropertyNotModifiableException.class, 
          Messages.getString("ConnectionProperties.dynamicChangeIsNotAllowed", new Object[] { "'" + getPropertyDefinition().getName() + "'" }));
    } 
  }
}
