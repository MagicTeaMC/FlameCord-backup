package com.google.protobuf;

import java.util.Map;

public interface StructOrBuilder extends MessageOrBuilder {
  int getFieldsCount();
  
  boolean containsFields(String paramString);
  
  @Deprecated
  Map<String, Value> getFields();
  
  Map<String, Value> getFieldsMap();
  
  Value getFieldsOrDefault(String paramString, Value paramValue);
  
  Value getFieldsOrThrow(String paramString);
}
