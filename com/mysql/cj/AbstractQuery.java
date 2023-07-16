package com.mysql.cj;

import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.CJTimeoutException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.OperationCancelledException;
import com.mysql.cj.protocol.ProtocolEntityFactory;
import com.mysql.cj.protocol.Resultset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractQuery implements Query {
  static int statementCounter = 1;
  
  public NativeSession session = null;
  
  protected int statementId;
  
  protected RuntimeProperty<Integer> maxAllowedPacket;
  
  protected String charEncoding = null;
  
  protected Object cancelTimeoutMutex = new Object();
  
  private Query.CancelStatus cancelStatus = Query.CancelStatus.NOT_CANCELED;
  
  protected int timeoutInMillis = 0;
  
  protected List<Object> batchedArgs;
  
  protected Resultset.Type resultSetType = Resultset.Type.FORWARD_ONLY;
  
  protected int fetchSize = 0;
  
  protected final AtomicBoolean statementExecuting = new AtomicBoolean(false);
  
  protected String currentDb = null;
  
  protected boolean clearWarningsCalled = false;
  
  private long executeTime = -1L;
  
  protected QueryAttributesBindings queryAttributesBindings;
  
  public AbstractQuery(NativeSession sess) {
    statementCounter++;
    this.session = sess;
    this.maxAllowedPacket = sess.getPropertySet().getIntegerProperty(PropertyKey.maxAllowedPacket);
    this.charEncoding = (String)sess.getPropertySet().getStringProperty(PropertyKey.characterEncoding).getValue();
    this.queryAttributesBindings = new NativeQueryAttributesBindings(sess);
  }
  
  public int getId() {
    return this.statementId;
  }
  
  public void setCancelStatus(Query.CancelStatus cs) {
    this.cancelStatus = cs;
  }
  
  public long getExecuteTime() {
    return this.executeTime;
  }
  
  public void setExecuteTime(long executeTime) {
    this.executeTime = executeTime;
  }
  
  public void checkCancelTimeout() {
    synchronized (this.cancelTimeoutMutex) {
      if (this.cancelStatus != Query.CancelStatus.NOT_CANCELED) {
        CJException cause = (this.cancelStatus == Query.CancelStatus.CANCELED_BY_TIMEOUT) ? (CJException)new CJTimeoutException() : (CJException)new OperationCancelledException();
        resetCancelledState();
        throw cause;
      } 
    } 
  }
  
  public void resetCancelledState() {
    synchronized (this.cancelTimeoutMutex) {
      this.cancelStatus = Query.CancelStatus.NOT_CANCELED;
    } 
  }
  
  public <T extends Resultset, M extends com.mysql.cj.protocol.Message> ProtocolEntityFactory<T, M> getResultSetFactory() {
    return null;
  }
  
  public NativeSession getSession() {
    return this.session;
  }
  
  public Object getCancelTimeoutMutex() {
    return this.cancelTimeoutMutex;
  }
  
  public void closeQuery() {
    this.queryAttributesBindings = null;
    this.session = null;
  }
  
  public void addBatch(Object batch) {
    if (this.batchedArgs == null)
      this.batchedArgs = new ArrayList(); 
    this.batchedArgs.add(batch);
  }
  
  public List<Object> getBatchedArgs() {
    return (this.batchedArgs == null) ? null : Collections.<Object>unmodifiableList(this.batchedArgs);
  }
  
  public void clearBatchedArgs() {
    if (this.batchedArgs != null)
      this.batchedArgs.clear(); 
  }
  
  public QueryAttributesBindings getQueryAttributesBindings() {
    return this.queryAttributesBindings;
  }
  
  public int getResultFetchSize() {
    return this.fetchSize;
  }
  
  public void setResultFetchSize(int fetchSize) {
    this.fetchSize = fetchSize;
  }
  
  public Resultset.Type getResultType() {
    return this.resultSetType;
  }
  
  public void setResultType(Resultset.Type resultSetType) {
    this.resultSetType = resultSetType;
  }
  
  public int getTimeoutInMillis() {
    return this.timeoutInMillis;
  }
  
  public void setTimeoutInMillis(int timeoutInMillis) {
    this.timeoutInMillis = timeoutInMillis;
  }
  
  public CancelQueryTask startQueryTimer(Query stmtToCancel, int timeout) {
    if (((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.enableQueryTimeouts).getValue()).booleanValue() && timeout != 0) {
      CancelQueryTaskImpl timeoutTask = new CancelQueryTaskImpl(stmtToCancel);
      this.session.getCancelTimer().schedule(timeoutTask, timeout);
      return timeoutTask;
    } 
    return null;
  }
  
  public void stopQueryTimer(CancelQueryTask timeoutTask, boolean rethrowCancelReason, boolean checkCancelTimeout) {
    if (timeoutTask != null) {
      timeoutTask.cancel();
      if (rethrowCancelReason && timeoutTask.getCaughtWhileCancelling() != null) {
        Throwable t = timeoutTask.getCaughtWhileCancelling();
        throw ExceptionFactory.createException(t.getMessage(), t);
      } 
      this.session.getCancelTimer().purge();
      if (checkCancelTimeout)
        checkCancelTimeout(); 
    } 
  }
  
  public AtomicBoolean getStatementExecuting() {
    return this.statementExecuting;
  }
  
  public String getCurrentDatabase() {
    return this.currentDb;
  }
  
  public void setCurrentDatabase(String currentDb) {
    this.currentDb = currentDb;
  }
  
  public boolean isClearWarningsCalled() {
    return this.clearWarningsCalled;
  }
  
  public void setClearWarningsCalled(boolean clearWarningsCalled) {
    this.clearWarningsCalled = clearWarningsCalled;
  }
  
  public void statementBegins() {
    this.clearWarningsCalled = false;
    this.statementExecuting.set(true);
  }
}
