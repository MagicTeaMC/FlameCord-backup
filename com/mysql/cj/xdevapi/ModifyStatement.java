package com.mysql.cj.xdevapi;

public interface ModifyStatement extends Statement<ModifyStatement, Result> {
  ModifyStatement sort(String... paramVarArgs);
  
  ModifyStatement limit(long paramLong);
  
  ModifyStatement set(String paramString, Object paramObject);
  
  ModifyStatement change(String paramString, Object paramObject);
  
  ModifyStatement unset(String... paramVarArgs);
  
  ModifyStatement patch(DbDoc paramDbDoc);
  
  ModifyStatement patch(String paramString);
  
  ModifyStatement arrayInsert(String paramString, Object paramObject);
  
  ModifyStatement arrayAppend(String paramString, Object paramObject);
}
