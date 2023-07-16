package com.mysql.cj.xdevapi;

import com.mysql.cj.MysqlxSession;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.ProtocolEntity;
import com.mysql.cj.protocol.ResultBuilder;
import com.mysql.cj.protocol.x.Notice;
import com.mysql.cj.protocol.x.StatementExecuteOk;
import com.mysql.cj.protocol.x.StatementExecuteOkBuilder;
import com.mysql.cj.protocol.x.XProtocol;
import com.mysql.cj.protocol.x.XProtocolRowInputStream;
import com.mysql.cj.result.Field;
import com.mysql.cj.result.RowList;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class StreamingSqlResultBuilder implements ResultBuilder<SqlResult> {
  TimeZone defaultTimeZone;
  
  PropertySet pset;
  
  XProtocol protocol;
  
  StatementExecuteOkBuilder statementExecuteOkBuilder = new StatementExecuteOkBuilder();
  
  boolean isRowResult = false;
  
  ProtocolEntity lastEntity = null;
  
  List<SqlSingleResult> resultSets = new ArrayList<>();
  
  private SqlResult result;
  
  public StreamingSqlResultBuilder(MysqlxSession sess) {
    this.defaultTimeZone = sess.getServerSession().getDefaultTimeZone();
    this.pset = sess.getPropertySet();
    this.protocol = sess.getProtocol();
  }
  
  public boolean addProtocolEntity(ProtocolEntity entity) {
    if (entity instanceof Notice) {
      this.statementExecuteOkBuilder.addProtocolEntity(entity);
    } else {
      this.lastEntity = entity;
    } 
    AtomicBoolean readLastResult = new AtomicBoolean(false);
    Supplier<ProtocolEntity> okReader = () -> {
        if (readLastResult.get())
          throw new CJCommunicationsException("Invalid state attempting to read ok packet"); 
        if (this.protocol.hasMoreResults()) {
          StatementExecuteOk res = this.statementExecuteOkBuilder.build();
          this.statementExecuteOkBuilder = new StatementExecuteOkBuilder();
          return (ProtocolEntity)res;
        } 
        readLastResult.set(true);
        return (ProtocolEntity)this.protocol.readQueryResult((ResultBuilder)this.statementExecuteOkBuilder);
      };
    Supplier<SqlResult> resultStream = () -> {
        if (readLastResult.get())
          return null; 
        if ((this.lastEntity != null && this.lastEntity instanceof Field) || this.protocol.isSqlResultPending()) {
          ColumnDefinition cd;
          if (this.lastEntity != null && this.lastEntity instanceof Field) {
            cd = this.protocol.readMetadata((Field)this.lastEntity, ());
            this.lastEntity = null;
          } else {
            cd = this.protocol.readMetadata(this.statementExecuteOkBuilder::addProtocolEntity);
          } 
          return new SqlSingleResult(cd, this.protocol.getServerSession().getDefaultTimeZone(), (RowList)new XProtocolRowInputStream(cd, this.protocol, ()), okReader, this.pset);
        } 
        readLastResult.set(true);
        SqlResultBuilder rb = new SqlResultBuilder(this.defaultTimeZone, this.pset);
        rb.addProtocolEntity(entity);
        return (SqlResult)this.protocol.readQueryResult(rb);
      };
    this.result = new SqlMultiResult(resultStream);
    return true;
  }
  
  public SqlResult build() {
    return this.result;
  }
}
