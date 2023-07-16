package com.mysql.cj.xdevapi;

import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.ProtocolEntity;
import com.mysql.cj.protocol.ResultBuilder;
import com.mysql.cj.protocol.x.StatementExecuteOkBuilder;

public class UpdateResultBuilder<T extends Result> implements ResultBuilder<T> {
  protected StatementExecuteOkBuilder statementExecuteOkBuilder = new StatementExecuteOkBuilder();
  
  public boolean addProtocolEntity(ProtocolEntity entity) {
    if (entity instanceof com.mysql.cj.protocol.x.Notice) {
      this.statementExecuteOkBuilder.addProtocolEntity(entity);
      return false;
    } 
    if (entity instanceof com.mysql.cj.protocol.x.StatementExecuteOk)
      return true; 
    if (entity instanceof com.mysql.cj.protocol.x.Ok)
      return true; 
    throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Unexpected protocol entity " + entity);
  }
  
  public T build() {
    return (T)new UpdateResult(this.statementExecuteOkBuilder.build());
  }
}
