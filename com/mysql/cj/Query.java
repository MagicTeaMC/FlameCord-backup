package com.mysql.cj;

import com.mysql.cj.protocol.ProtocolEntityFactory;
import com.mysql.cj.protocol.Resultset;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public interface Query {
  int getId();
  
  void setCancelStatus(CancelStatus paramCancelStatus);
  
  void checkCancelTimeout();
  
  <T extends Resultset, M extends com.mysql.cj.protocol.Message> ProtocolEntityFactory<T, M> getResultSetFactory();
  
  Session getSession();
  
  Object getCancelTimeoutMutex();
  
  void resetCancelledState();
  
  void closeQuery();
  
  void addBatch(Object paramObject);
  
  List<Object> getBatchedArgs();
  
  void clearBatchedArgs();
  
  QueryAttributesBindings getQueryAttributesBindings();
  
  int getResultFetchSize();
  
  void setResultFetchSize(int paramInt);
  
  Resultset.Type getResultType();
  
  void setResultType(Resultset.Type paramType);
  
  int getTimeoutInMillis();
  
  void setTimeoutInMillis(int paramInt);
  
  void setExecuteTime(long paramLong);
  
  long getExecuteTime();
  
  CancelQueryTask startQueryTimer(Query paramQuery, int paramInt);
  
  AtomicBoolean getStatementExecuting();
  
  String getCurrentDatabase();
  
  void setCurrentDatabase(String paramString);
  
  boolean isClearWarningsCalled();
  
  void setClearWarningsCalled(boolean paramBoolean);
  
  void statementBegins();
  
  void stopQueryTimer(CancelQueryTask paramCancelQueryTask, boolean paramBoolean1, boolean paramBoolean2);
  
  public enum CancelStatus {
    NOT_CANCELED, CANCELED_BY_USER, CANCELED_BY_TIMEOUT;
  }
}
