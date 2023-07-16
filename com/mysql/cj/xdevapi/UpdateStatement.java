package com.mysql.cj.xdevapi;

import java.util.Map;

public interface UpdateStatement extends Statement<UpdateStatement, Result> {
  UpdateStatement set(Map<String, Object> paramMap);
  
  UpdateStatement set(String paramString, Object paramObject);
  
  UpdateStatement where(String paramString);
  
  UpdateStatement orderBy(String... paramVarArgs);
  
  UpdateStatement limit(long paramLong);
}
