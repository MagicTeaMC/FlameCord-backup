package com.mysql.cj.xdevapi;

import com.mysql.cj.MysqlxSession;
import com.mysql.cj.exceptions.AssertionFailedException;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.x.StatementExecuteOk;
import com.mysql.cj.protocol.x.XMessageBuilder;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AddStatementImpl implements AddStatement {
  private MysqlxSession mysqlxSession;
  
  private String schemaName;
  
  private String collectionName;
  
  private List<DbDoc> newDocs;
  
  private boolean upsert = false;
  
  AddStatementImpl(MysqlxSession mysqlxSession, String schema, String collection, DbDoc newDoc) {
    this.mysqlxSession = mysqlxSession;
    this.schemaName = schema;
    this.collectionName = collection;
    this.newDocs = new ArrayList<>();
    this.newDocs.add(newDoc);
  }
  
  AddStatementImpl(MysqlxSession mysqlxSession, String schema, String collection, DbDoc[] newDocs) {
    this.mysqlxSession = mysqlxSession;
    this.schemaName = schema;
    this.collectionName = collection;
    this.newDocs = new ArrayList<>();
    this.newDocs.addAll(Arrays.asList(newDocs));
  }
  
  public AddStatement add(String jsonString) {
    try {
      DbDoc doc = JsonParser.parseDoc(new StringReader(jsonString));
      return add(new DbDoc[] { doc });
    } catch (IOException ex) {
      throw AssertionFailedException.shouldNotHappen(ex);
    } 
  }
  
  public AddStatement add(DbDoc... docs) {
    this.newDocs.addAll(Arrays.asList(docs));
    return this;
  }
  
  private List<String> serializeDocs() {
    return (List<String>)this.newDocs.stream().map(Object::toString).collect(Collectors.toList());
  }
  
  public AddResult execute() {
    if (this.newDocs.size() == 0) {
      StatementExecuteOk ok = new StatementExecuteOk(0L, null, Collections.emptyList(), Collections.emptyList());
      return new AddResultImpl(ok);
    } 
    return (AddResult)this.mysqlxSession.query((Message)((XMessageBuilder)this.mysqlxSession.getMessageBuilder()).buildDocInsert(this.schemaName, this.collectionName, 
          serializeDocs(), this.upsert), new AddResultBuilder());
  }
  
  public CompletableFuture<AddResult> executeAsync() {
    if (this.newDocs.size() == 0) {
      StatementExecuteOk ok = new StatementExecuteOk(0L, null, Collections.emptyList(), Collections.emptyList());
      return CompletableFuture.completedFuture(new AddResultImpl(ok));
    } 
    return this.mysqlxSession.queryAsync((Message)((XMessageBuilder)this.mysqlxSession.getMessageBuilder()).buildDocInsert(this.schemaName, this.collectionName, 
          serializeDocs(), this.upsert), new AddResultBuilder());
  }
  
  public boolean isUpsert() {
    return this.upsert;
  }
  
  public AddStatement setUpsert(boolean upsert) {
    this.upsert = upsert;
    return this;
  }
}
