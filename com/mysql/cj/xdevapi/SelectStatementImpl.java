package com.mysql.cj.xdevapi;

import com.mysql.cj.MysqlxSession;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.x.XMessage;
import java.util.concurrent.CompletableFuture;

public class SelectStatementImpl extends FilterableStatement<SelectStatement, RowResult> implements SelectStatement {
  SelectStatementImpl(MysqlxSession mysqlxSession, String schema, String table, String... projection) {
    super(new TableFilterParams(schema, table));
    this.mysqlxSession = mysqlxSession;
    if (projection != null && projection.length > 0)
      this.filterParams.setFields(projection); 
  }
  
  protected RowResult executeStatement() {
    return (RowResult)this.mysqlxSession.query((Message)getMessageBuilder().buildFind(this.filterParams), new StreamingRowResultBuilder(this.mysqlxSession));
  }
  
  protected XMessage getPrepareStatementXMessage() {
    return getMessageBuilder().buildPrepareFind(this.preparedStatementId, this.filterParams);
  }
  
  protected RowResult executePreparedStatement() {
    return (RowResult)this.mysqlxSession.query((Message)getMessageBuilder().buildPrepareExecute(this.preparedStatementId, this.filterParams), new StreamingRowResultBuilder(this.mysqlxSession));
  }
  
  public CompletableFuture<RowResult> executeAsync() {
    return this.mysqlxSession.queryAsync((Message)getMessageBuilder().buildFind(this.filterParams), new RowResultBuilder(this.mysqlxSession));
  }
  
  public SelectStatement groupBy(String... groupBy) {
    resetPrepareState();
    this.filterParams.setGrouping(groupBy);
    return this;
  }
  
  public SelectStatement having(String having) {
    resetPrepareState();
    this.filterParams.setGroupingCriteria(having);
    return this;
  }
  
  public FilterParams getFilterParams() {
    return this.filterParams;
  }
  
  public SelectStatement lockShared() {
    return lockShared(Statement.LockContention.DEFAULT);
  }
  
  public SelectStatement lockShared(Statement.LockContention lockContention) {
    resetPrepareState();
    this.filterParams.setLock(FilterParams.RowLock.SHARED_LOCK);
    switch (lockContention) {
      case NOWAIT:
        this.filterParams.setLockOption(FilterParams.RowLockOptions.NOWAIT);
        break;
      case SKIP_LOCKED:
        this.filterParams.setLockOption(FilterParams.RowLockOptions.SKIP_LOCKED);
        break;
    } 
    return this;
  }
  
  public SelectStatement lockExclusive() {
    return lockExclusive(Statement.LockContention.DEFAULT);
  }
  
  public SelectStatement lockExclusive(Statement.LockContention lockContention) {
    resetPrepareState();
    this.filterParams.setLock(FilterParams.RowLock.EXCLUSIVE_LOCK);
    switch (lockContention) {
      case NOWAIT:
        this.filterParams.setLockOption(FilterParams.RowLockOptions.NOWAIT);
        break;
      case SKIP_LOCKED:
        this.filterParams.setLockOption(FilterParams.RowLockOptions.SKIP_LOCKED);
        break;
    } 
    return this;
  }
}
