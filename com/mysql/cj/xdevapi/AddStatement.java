package com.mysql.cj.xdevapi;

public interface AddStatement extends Statement<AddStatement, AddResult> {
  AddStatement add(String paramString);
  
  AddStatement add(DbDoc... paramVarArgs);
  
  boolean isUpsert();
  
  AddStatement setUpsert(boolean paramBoolean);
}
