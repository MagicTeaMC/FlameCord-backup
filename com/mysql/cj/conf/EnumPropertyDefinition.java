package com.mysql.cj.conf;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.util.StringUtils;
import java.util.Arrays;

public class EnumPropertyDefinition<T extends Enum<T>> extends AbstractPropertyDefinition<T> {
  private static final long serialVersionUID = -3297521968759540444L;
  
  private Class<T> enumType;
  
  public EnumPropertyDefinition(PropertyKey key, T defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory) {
    super(key, defaultValue, isRuntimeModifiable, description, sinceVersion, category, orderInCategory);
    if (defaultValue == null)
      throw ExceptionFactory.createException("Enum property '" + key.getKeyName() + "' cannot be initialized with null."); 
    this.enumType = defaultValue.getDeclaringClass();
  }
  
  public String[] getAllowableValues() {
    return (String[])Arrays.stream((Object[])this.enumType.getEnumConstants()).map(Enum::toString).sorted().toArray(x$0 -> new String[x$0]);
  }
  
  public T parseObject(String value, ExceptionInterceptor exceptionInterceptor) {
    try {
      return Enum.valueOf(this.enumType, value.toUpperCase());
    } catch (Exception e) {
      throw ExceptionFactory.createException(
          Messages.getString("PropertyDefinition.1", new Object[] { getName(), StringUtils.stringArrayToString(getAllowableValues(), "'", "', '", "' or '", "'"), value }), e, exceptionInterceptor);
    } 
  }
  
  public RuntimeProperty<T> createRuntimeProperty() {
    return new EnumProperty<>(this);
  }
}
