package com.mysql.cj;

public interface CancelQueryTask {
  boolean cancel();
  
  Throwable getCaughtWhileCancelling();
  
  void setCaughtWhileCancelling(Throwable paramThrowable);
  
  Query getQueryToCancel();
  
  void setQueryToCancel(Query paramQuery);
}
