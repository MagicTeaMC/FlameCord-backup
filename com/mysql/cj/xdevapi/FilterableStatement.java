package com.mysql.cj.xdevapi;

public abstract class FilterableStatement<STMT_T, RES_T> extends PreparableStatement<RES_T> implements Statement<STMT_T, RES_T> {
  protected FilterParams filterParams;
  
  public FilterableStatement(FilterParams filterParams) {
    this.filterParams = filterParams;
  }
  
  public STMT_T where(String searchCondition) {
    resetPrepareState();
    this.filterParams.setCriteria(searchCondition);
    return (STMT_T)this;
  }
  
  public STMT_T sort(String... sortFields) {
    return orderBy(sortFields);
  }
  
  public STMT_T orderBy(String... sortFields) {
    resetPrepareState();
    this.filterParams.setOrder(sortFields);
    return (STMT_T)this;
  }
  
  public STMT_T limit(long numberOfRows) {
    if (this.filterParams.getLimit() == null)
      setReprepareState(); 
    this.filterParams.setLimit(Long.valueOf(numberOfRows));
    return (STMT_T)this;
  }
  
  public STMT_T offset(long limitOffset) {
    this.filterParams.setOffset(Long.valueOf(limitOffset));
    return (STMT_T)this;
  }
  
  public boolean isRelational() {
    return this.filterParams.isRelational();
  }
  
  public STMT_T clearBindings() {
    this.filterParams.clearArgs();
    return (STMT_T)this;
  }
  
  public STMT_T bind(String argName, Object value) {
    this.filterParams.addArg(argName, value);
    return (STMT_T)this;
  }
}
