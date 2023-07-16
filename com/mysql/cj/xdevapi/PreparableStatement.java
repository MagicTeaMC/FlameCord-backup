package com.mysql.cj.xdevapi;

import com.mysql.cj.MysqlxSession;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.x.XMessage;
import com.mysql.cj.protocol.x.XMessageBuilder;
import com.mysql.cj.protocol.x.XProtocolError;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

public abstract class PreparableStatement<RES_T> {
  protected enum PreparedState {
    UNSUPPORTED, UNPREPARED, SUSPENDED, PREPARED, PREPARE, DEALLOCATE, REPREPARE;
  }
  
  protected int preparedStatementId = 0;
  
  protected PreparedState preparedState = PreparedState.UNPREPARED;
  
  protected MysqlxSession mysqlxSession;
  
  protected XMessageBuilder getMessageBuilder() {
    return (XMessageBuilder)this.mysqlxSession.getMessageBuilder();
  }
  
  protected void resetPrepareState() {
    if (this.preparedState == PreparedState.PREPARED || this.preparedState == PreparedState.REPREPARE) {
      this.preparedState = PreparedState.DEALLOCATE;
    } else if (this.preparedState == PreparedState.PREPARE) {
      this.preparedState = PreparedState.UNPREPARED;
    } 
  }
  
  protected void setReprepareState() {
    if (this.preparedState == PreparedState.PREPARED)
      this.preparedState = PreparedState.REPREPARE; 
  }
  
  public RES_T execute() {
    while (true) {
      RES_T result;
      switch (this.preparedState) {
        case UNSUPPORTED:
          return executeStatement();
        case UNPREPARED:
          result = executeStatement();
          this.preparedState = PreparedState.PREPARE;
          return result;
        case SUSPENDED:
          if (!this.mysqlxSession.supportsPreparedStatements()) {
            this.preparedState = PreparedState.UNSUPPORTED;
            continue;
          } 
          if (this.mysqlxSession.readyForPreparingStatements()) {
            this.preparedState = PreparedState.PREPARE;
            continue;
          } 
          return executeStatement();
        case PREPARE:
          this.preparedState = prepareStatement() ? PreparedState.PREPARED : PreparedState.SUSPENDED;
        case PREPARED:
          return executePreparedStatement();
        case DEALLOCATE:
          deallocatePrepared();
          this.preparedState = PreparedState.UNPREPARED;
        case REPREPARE:
          deallocatePrepared();
          this.preparedState = PreparedState.PREPARE;
      } 
    } 
  }
  
  protected abstract RES_T executeStatement();
  
  protected abstract XMessage getPrepareStatementXMessage();
  
  private boolean prepareStatement() {
    if (!this.mysqlxSession.supportsPreparedStatements())
      return false; 
    try {
      this.preparedStatementId = this.mysqlxSession.getNewPreparedStatementId(this);
      this.mysqlxSession.query((Message)getPrepareStatementXMessage(), new UpdateResultBuilder<>());
    } catch (XProtocolError e) {
      if (this.mysqlxSession.failedPreparingStatement(this.preparedStatementId, e)) {
        this.preparedStatementId = 0;
        return false;
      } 
      this.preparedStatementId = 0;
      throw e;
    } catch (Throwable t) {
      this.preparedStatementId = 0;
      throw t;
    } 
    return true;
  }
  
  protected abstract RES_T executePreparedStatement();
  
  protected void deallocatePrepared() {
    if (this.preparedState == PreparedState.PREPARED || this.preparedState == PreparedState.DEALLOCATE || this.preparedState == PreparedState.REPREPARE)
      try {
        this.mysqlxSession.query((Message)getMessageBuilder().buildPrepareDeallocate(this.preparedStatementId), new UpdateResultBuilder<>());
      } finally {
        this.mysqlxSession.freePreparedStatementId(this.preparedStatementId);
        this.preparedStatementId = 0;
      }  
  }
  
  public static class PreparableStatementFinalizer extends PhantomReference<PreparableStatement<?>> {
    int prepredStatementId;
    
    public PreparableStatementFinalizer(PreparableStatement<?> referent, ReferenceQueue<? super PreparableStatement<?>> q, int preparedStatementId) {
      super(referent, q);
      this.prepredStatementId = preparedStatementId;
    }
    
    public int getPreparedStatementId() {
      return this.prepredStatementId;
    }
  }
}
