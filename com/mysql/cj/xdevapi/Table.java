package com.mysql.cj.xdevapi;

import java.util.Map;

public interface Table extends DatabaseObject {
  InsertStatement insert();
  
  InsertStatement insert(String... paramVarArgs);
  
  InsertStatement insert(Map<String, Object> paramMap);
  
  SelectStatement select(String... paramVarArgs);
  
  UpdateStatement update();
  
  DeleteStatement delete();
  
  long count();
  
  boolean isView();
}
