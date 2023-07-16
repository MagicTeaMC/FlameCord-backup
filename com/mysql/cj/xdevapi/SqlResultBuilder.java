package com.mysql.cj.xdevapi;

import com.mysql.cj.MysqlxSession;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.ProtocolEntity;
import com.mysql.cj.protocol.ResultBuilder;
import com.mysql.cj.protocol.x.StatementExecuteOkBuilder;
import com.mysql.cj.result.BufferedRowList;
import com.mysql.cj.result.DefaultColumnDefinition;
import com.mysql.cj.result.Field;
import com.mysql.cj.result.Row;
import com.mysql.cj.result.RowList;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class SqlResultBuilder implements ResultBuilder<SqlResult> {
  private ArrayList<Field> fields = new ArrayList<>();
  
  private ColumnDefinition metadata;
  
  private List<Row> rows = new ArrayList<>();
  
  TimeZone defaultTimeZone;
  
  PropertySet pset;
  
  boolean isRowResult = false;
  
  List<SqlSingleResult> resultSets = new ArrayList<>();
  
  private ProtocolEntity prevEntity = null;
  
  private StatementExecuteOkBuilder statementExecuteOkBuilder = new StatementExecuteOkBuilder();
  
  public SqlResultBuilder(TimeZone defaultTimeZone, PropertySet pset) {
    this.defaultTimeZone = defaultTimeZone;
    this.pset = pset;
  }
  
  public SqlResultBuilder(MysqlxSession sess) {
    this.defaultTimeZone = sess.getServerSession().getDefaultTimeZone();
    this.pset = sess.getPropertySet();
  }
  
  public boolean addProtocolEntity(ProtocolEntity entity) {
    if (entity instanceof Field) {
      this.fields.add((Field)entity);
      if (!this.isRowResult)
        this.isRowResult = true; 
      this.prevEntity = entity;
      return false;
    } 
    if (entity instanceof com.mysql.cj.protocol.x.Notice) {
      this.statementExecuteOkBuilder.addProtocolEntity(entity);
      return false;
    } 
    if (this.isRowResult && this.metadata == null)
      this.metadata = (ColumnDefinition)new DefaultColumnDefinition(this.fields.<Field>toArray(new Field[0])); 
    if (entity instanceof Row) {
      this.rows.add(((Row)entity).setMetadata(this.metadata));
    } else if (entity instanceof com.mysql.cj.protocol.x.FetchDoneMoreResults) {
      this.resultSets.add(new SqlSingleResult(this.metadata, this.defaultTimeZone, (RowList)new BufferedRowList(this.rows), () -> this.statementExecuteOkBuilder.build(), this.pset));
      this.fields = new ArrayList<>();
      this.metadata = null;
      this.rows = new ArrayList<>();
      this.statementExecuteOkBuilder = new StatementExecuteOkBuilder();
    } else if (entity instanceof com.mysql.cj.protocol.x.FetchDoneEntity) {
      if (!(this.prevEntity instanceof com.mysql.cj.protocol.x.FetchDoneMoreResults))
        this.resultSets.add(new SqlSingleResult(this.metadata, this.defaultTimeZone, (RowList)new BufferedRowList(this.rows), () -> this.statementExecuteOkBuilder.build(), this.pset)); 
    } else if (entity instanceof com.mysql.cj.protocol.x.StatementExecuteOk) {
      return true;
    } 
    this.prevEntity = entity;
    return false;
  }
  
  public SqlResult build() {
    return this.isRowResult ? new SqlMultiResult(() -> (this.resultSets.size() > 0) ? this.resultSets.remove(0) : null) : new SqlUpdateResult(this.statementExecuteOkBuilder
        
        .build());
  }
}
