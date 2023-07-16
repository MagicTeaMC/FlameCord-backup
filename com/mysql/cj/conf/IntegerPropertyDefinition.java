package com.mysql.cj.conf;

import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.WrongArgumentException;

public class IntegerPropertyDefinition extends AbstractPropertyDefinition<Integer> {
  private static final long serialVersionUID = 4151893695173946081L;
  
  protected int multiplier = 1;
  
  public IntegerPropertyDefinition(PropertyKey key, int defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory) {
    super(key, Integer.valueOf(defaultValue), isRuntimeModifiable, description, sinceVersion, category, orderInCategory);
  }
  
  public IntegerPropertyDefinition(PropertyKey key, int defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory, int lowerBound, int upperBound) {
    super(key, Integer.valueOf(defaultValue), isRuntimeModifiable, description, sinceVersion, category, orderInCategory, lowerBound, upperBound);
  }
  
  public boolean isRangeBased() {
    return (getUpperBound() != getLowerBound());
  }
  
  public Integer parseObject(String value, ExceptionInterceptor exceptionInterceptor) {
    return integerFrom(getName(), value, this.multiplier, exceptionInterceptor);
  }
  
  public RuntimeProperty<Integer> createRuntimeProperty() {
    return new IntegerProperty(this);
  }
  
  public static Integer integerFrom(String name, String value, int multiplier, ExceptionInterceptor exceptionInterceptor) {
    try {
      int intValue = (int)(Double.parseDouble(value) * multiplier);
      return Integer.valueOf(intValue);
    } catch (NumberFormatException nfe) {
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "The connection property '" + name + "' only accepts integer values. The value '" + value + "' can not be converted to an integer.", exceptionInterceptor);
    } 
  }
}
