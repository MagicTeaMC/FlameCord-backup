package org.eclipse.sisu.bean;

public interface BeanManager {
  boolean manage(Class<?> paramClass);
  
  PropertyBinding manage(BeanProperty<?> paramBeanProperty);
  
  boolean manage(Object paramObject);
  
  boolean unmanage(Object paramObject);
  
  boolean unmanage();
}
