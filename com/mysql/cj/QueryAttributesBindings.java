package com.mysql.cj;

import java.util.function.Consumer;

public interface QueryAttributesBindings {
  void setAttribute(String paramString, Object paramObject);
  
  int getCount();
  
  BindValue getAttributeValue(int paramInt);
  
  void runThroughAll(Consumer<BindValue> paramConsumer);
  
  void clearAttributes();
}
