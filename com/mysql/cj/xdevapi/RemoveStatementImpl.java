package com.mysql.cj.xdevapi;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlxSession;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.x.XMessage;
import com.mysql.cj.protocol.x.XMessageBuilder;
import java.util.concurrent.CompletableFuture;

public class RemoveStatementImpl extends FilterableStatement<RemoveStatement, Result> implements RemoveStatement {
  RemoveStatementImpl(MysqlxSession mysqlxSession, String schema, String collection, String criteria) {
    super(new DocFilterParams(schema, collection, false));
    this.mysqlxSession = mysqlxSession;
    if (criteria == null || criteria.trim().length() == 0)
      throw new XDevAPIError(Messages.getString("RemoveStatement.0", new String[] { "criteria" })); 
    this.filterParams.setCriteria(criteria);
  }
  
  @Deprecated
  public RemoveStatement orderBy(String... sortFields) {
    return super.orderBy(sortFields);
  }
  
  public Result executeStatement() {
    return (Result)this.mysqlxSession.query((Message)getMessageBuilder().buildDelete(this.filterParams), new UpdateResultBuilder<>());
  }
  
  protected XMessage getPrepareStatementXMessage() {
    return getMessageBuilder().buildPrepareDelete(this.preparedStatementId, this.filterParams);
  }
  
  protected Result executePreparedStatement() {
    return (Result)this.mysqlxSession.query((Message)getMessageBuilder().buildPrepareExecute(this.preparedStatementId, this.filterParams), new UpdateResultBuilder<>());
  }
  
  public CompletableFuture<Result> executeAsync() {
    return this.mysqlxSession.queryAsync((Message)((XMessageBuilder)this.mysqlxSession.getMessageBuilder()).buildDelete(this.filterParams), new UpdateResultBuilder<>());
  }
  
  @Deprecated
  public RemoveStatement where(String searchCondition) {
    return super.where(searchCondition);
  }
}
