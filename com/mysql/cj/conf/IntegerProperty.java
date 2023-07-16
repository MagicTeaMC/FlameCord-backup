package com.mysql.cj.conf;

import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.WrongArgumentException;

public class IntegerProperty extends AbstractRuntimeProperty<Integer> {
  private static final long serialVersionUID = 9208223182595760858L;
  
  public IntegerProperty(PropertyDefinition<Integer> propertyDefinition) {
    super(propertyDefinition);
  }
  
  protected void checkRange(Integer val, String valueAsString, ExceptionInterceptor exceptionInterceptor) {
    if (val.intValue() < getPropertyDefinition().getLowerBound() || val.intValue() > getPropertyDefinition().getUpperBound())
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "The connection property '" + 
          getPropertyDefinition().getName() + "' only accepts integer values in the range of " + 
          getPropertyDefinition().getLowerBound() + " - " + getPropertyDefinition().getUpperBound() + ", the value '" + ((valueAsString == null) ? 
          Integer.valueOf(val.intValue()) : valueAsString) + "' exceeds this range.", exceptionInterceptor); 
  }
}
