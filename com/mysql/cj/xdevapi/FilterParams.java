package com.mysql.cj.xdevapi;

public interface FilterParams {
  Object getCollection();
  
  Object getOrder();
  
  void setOrder(String... paramVarArgs);
  
  Long getLimit();
  
  void setLimit(Long paramLong);
  
  Long getOffset();
  
  void setOffset(Long paramLong);
  
  boolean supportsOffset();
  
  Object getCriteria();
  
  void setCriteria(String paramString);
  
  Object getArgs();
  
  void addArg(String paramString, Object paramObject);
  
  void verifyAllArgsBound();
  
  void clearArgs();
  
  boolean isRelational();
  
  void setFields(String... paramVarArgs);
  
  Object getFields();
  
  void setGrouping(String... paramVarArgs);
  
  Object getGrouping();
  
  void setGroupingCriteria(String paramString);
  
  Object getGroupingCriteria();
  
  RowLock getLock();
  
  void setLock(RowLock paramRowLock);
  
  RowLockOptions getLockOption();
  
  void setLockOption(RowLockOptions paramRowLockOptions);
  
  public enum RowLock {
    SHARED_LOCK(1),
    EXCLUSIVE_LOCK(2);
    
    private int rowLock;
    
    RowLock(int rowLock) {
      this.rowLock = rowLock;
    }
    
    public int asNumber() {
      return this.rowLock;
    }
  }
  
  public enum RowLockOptions {
    NOWAIT(1),
    SKIP_LOCKED(2);
    
    private int rowLockOption;
    
    RowLockOptions(int rowLockOption) {
      this.rowLockOption = rowLockOption;
    }
    
    public int asNumber() {
      return this.rowLockOption;
    }
  }
}
