package com.mysql.cj.conf;

import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.WrongArgumentException;

public class LongPropertyDefinition extends AbstractPropertyDefinition<Long> {
  private static final long serialVersionUID = -5264490959206230852L;
  
  public LongPropertyDefinition(PropertyKey key, long defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory) {
    super(key, Long.valueOf(defaultValue), isRuntimeModifiable, description, sinceVersion, category, orderInCategory);
  }
  
  public LongPropertyDefinition(PropertyKey key, long defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory, long lowerBound, long upperBound) {
    super(key, Long.valueOf(defaultValue), isRuntimeModifiable, description, sinceVersion, category, orderInCategory, (int)lowerBound, (int)upperBound);
  }
  
  public Long parseObject(String value, ExceptionInterceptor exceptionInterceptor) {
    try {
      return Long.valueOf(Double.valueOf(value).longValue());
    } catch (NumberFormatException nfe) {
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "The connection property '" + getName() + "' only accepts long integer values. The value '" + value + "' can not be converted to a long integer.", exceptionInterceptor);
    } 
  }
  
  public boolean isRangeBased() {
    return (getUpperBound() != getLowerBound());
  }
  
  public RuntimeProperty<Long> createRuntimeProperty() {
    return new LongProperty(this);
  }
}
