package org.eclipse.sisu.bean;

public interface PropertyBinder {
  public static final PropertyBinding LAST_BINDING = new PropertyBinding() {
      public <B> void injectProperty(B bean) {
        throw new UnsupportedOperationException("LAST_BINDING");
      }
    };
  
  <T> PropertyBinding bindProperty(BeanProperty<T> paramBeanProperty);
}
