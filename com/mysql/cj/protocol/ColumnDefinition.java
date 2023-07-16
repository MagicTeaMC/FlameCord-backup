package com.mysql.cj.protocol;

import com.mysql.cj.result.Field;
import java.util.Map;

public interface ColumnDefinition extends ProtocolEntity {
  Field[] getFields();
  
  void setFields(Field[] paramArrayOfField);
  
  void buildIndexMapping();
  
  boolean hasBuiltIndexMapping();
  
  Map<String, Integer> getColumnLabelToIndex();
  
  void setColumnLabelToIndex(Map<String, Integer> paramMap);
  
  Map<String, Integer> getFullColumnNameToIndex();
  
  void setFullColumnNameToIndex(Map<String, Integer> paramMap);
  
  Map<String, Integer> getColumnNameToIndex();
  
  void setColumnNameToIndex(Map<String, Integer> paramMap);
  
  Map<String, Integer> getColumnToIndexCache();
  
  void setColumnToIndexCache(Map<String, Integer> paramMap);
  
  void initializeFrom(ColumnDefinition paramColumnDefinition);
  
  void exportTo(ColumnDefinition paramColumnDefinition);
  
  int findColumn(String paramString, boolean paramBoolean, int paramInt);
  
  boolean hasLargeFields();
}
