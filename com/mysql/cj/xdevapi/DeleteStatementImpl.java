package com.mysql.cj.xdevapi;

import com.mysql.cj.MysqlxSession;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.x.XMessage;
import java.util.concurrent.CompletableFuture;

public class DeleteStatementImpl extends FilterableStatement<DeleteStatement, Result> implements DeleteStatement {
  DeleteStatementImpl(MysqlxSession mysqlxSession, String schema, String table) {
    super(new TableFilterParams(schema, table, false));
    this.mysqlxSession = mysqlxSession;
  }
  
  protected Result executeStatement() {
    return (Result)this.mysqlxSession.query((Message)getMessageBuilder().buildDelete(this.filterParams), new UpdateResultBuilder<>());
  }
  
  protected XMessage getPrepareStatementXMessage() {
    return getMessageBuilder().buildPrepareDelete(this.preparedStatementId, this.filterParams);
  }
  
  protected Result executePreparedStatement() {
    return (Result)this.mysqlxSession.query((Message)getMessageBuilder().buildPrepareExecute(this.preparedStatementId, this.filterParams), new UpdateResultBuilder<>());
  }
  
  public CompletableFuture<Result> executeAsync() {
    return this.mysqlxSession.queryAsync((Message)getMessageBuilder().buildDelete(this.filterParams), new UpdateResultBuilder<>());
  }
}
