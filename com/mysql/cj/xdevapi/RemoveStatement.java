package com.mysql.cj.xdevapi;

public interface RemoveStatement extends Statement<RemoveStatement, Result> {
  @Deprecated
  RemoveStatement orderBy(String... paramVarArgs);
  
  RemoveStatement sort(String... paramVarArgs);
  
  RemoveStatement limit(long paramLong);
}
