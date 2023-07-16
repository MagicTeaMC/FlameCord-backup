package com.mysql.cj.conf;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.util.StringUtils;
import java.util.Arrays;

public class BooleanPropertyDefinition extends AbstractPropertyDefinition<Boolean> {
  private static final long serialVersionUID = -7288366734350231540L;
  
  public enum AllowableValues {
    TRUE(true),
    FALSE(false),
    YES(true),
    NO(false);
    
    private boolean asBoolean;
    
    AllowableValues(boolean booleanValue) {
      this.asBoolean = booleanValue;
    }
    
    public boolean asBoolean() {
      return this.asBoolean;
    }
  }
  
  public BooleanPropertyDefinition(PropertyKey key, Boolean defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory) {
    super(key, defaultValue, isRuntimeModifiable, description, sinceVersion, category, orderInCategory);
  }
  
  public String[] getAllowableValues() {
    return getBooleanAllowableValues();
  }
  
  public Boolean parseObject(String value, ExceptionInterceptor exceptionInterceptor) {
    return booleanFrom(getName(), value, exceptionInterceptor);
  }
  
  public RuntimeProperty<Boolean> createRuntimeProperty() {
    return new BooleanProperty(this);
  }
  
  public static Boolean booleanFrom(String name, String value, ExceptionInterceptor exceptionInterceptor) {
    try {
      return Boolean.valueOf(AllowableValues.valueOf(value.toUpperCase()).asBoolean());
    } catch (Exception e) {
      throw ExceptionFactory.createException(
          Messages.getString("PropertyDefinition.1", new Object[] { name, StringUtils.stringArrayToString(getBooleanAllowableValues(), "'", "', '", "' or '", "'"), value }), e, exceptionInterceptor);
    } 
  }
  
  public static String[] getBooleanAllowableValues() {
    return (String[])Arrays.<AllowableValues>stream(AllowableValues.values()).map(Enum::toString).toArray(x$0 -> new String[x$0]);
  }
}
