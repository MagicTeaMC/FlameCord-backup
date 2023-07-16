package com.mysql.cj.xdevapi;

public interface SelectStatement extends Statement<SelectStatement, RowResult> {
  SelectStatement where(String paramString);
  
  SelectStatement groupBy(String... paramVarArgs);
  
  SelectStatement having(String paramString);
  
  SelectStatement orderBy(String... paramVarArgs);
  
  SelectStatement limit(long paramLong);
  
  SelectStatement offset(long paramLong);
  
  SelectStatement lockShared();
  
  SelectStatement lockShared(Statement.LockContention paramLockContention);
  
  SelectStatement lockExclusive();
  
  SelectStatement lockExclusive(Statement.LockContention paramLockContention);
  
  FilterParams getFilterParams();
}
