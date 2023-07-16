package com.mysql.cj.xdevapi;

public interface DeleteStatement extends Statement<DeleteStatement, Result> {
  DeleteStatement where(String paramString);
  
  DeleteStatement orderBy(String... paramVarArgs);
  
  DeleteStatement limit(long paramLong);
}
