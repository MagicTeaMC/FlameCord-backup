package com.mysql.cj.conf;

import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.WrongArgumentException;

public class LongProperty extends AbstractRuntimeProperty<Long> {
  private static final long serialVersionUID = 1814429804634837665L;
  
  protected LongProperty(PropertyDefinition<Long> propertyDefinition) {
    super(propertyDefinition);
  }
  
  protected void checkRange(Long val, String valueAsString, ExceptionInterceptor exceptionInterceptor) {
    if (val.longValue() < getPropertyDefinition().getLowerBound() || val.longValue() > getPropertyDefinition().getUpperBound())
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "The connection property '" + 
          getPropertyDefinition().getName() + "' only accepts long integer values in the range of " + 
          getPropertyDefinition().getLowerBound() + " - " + getPropertyDefinition().getUpperBound() + ", the value '" + ((valueAsString == null) ? 
          Long.valueOf(val.longValue()) : valueAsString) + "' exceeds this range.", exceptionInterceptor); 
  }
}
