package com.mysql.cj.xdevapi;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlxSession;
import com.mysql.cj.exceptions.AssertionFailedException;
import com.mysql.cj.exceptions.FeatureNotAvailableException;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.x.XMessageBuilder;
import com.mysql.cj.protocol.x.XProtocolError;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

public class CollectionImpl implements Collection {
  private MysqlxSession mysqlxSession;
  
  private XMessageBuilder xbuilder;
  
  private SchemaImpl schema;
  
  private String name;
  
  CollectionImpl(MysqlxSession mysqlxSession, SchemaImpl schema, String name) {
    this.mysqlxSession = mysqlxSession;
    this.schema = schema;
    this.name = name;
    this.xbuilder = (XMessageBuilder)this.mysqlxSession.getMessageBuilder();
  }
  
  public Session getSession() {
    return this.schema.getSession();
  }
  
  public Schema getSchema() {
    return this.schema;
  }
  
  public String getName() {
    return this.name;
  }
  
  public DatabaseObject.DbObjectStatus existsInDatabase() {
    if (this.mysqlxSession.getDataStoreMetadata().tableExists(this.schema.getName(), this.name))
      return DatabaseObject.DbObjectStatus.EXISTS; 
    return DatabaseObject.DbObjectStatus.NOT_EXISTS;
  }
  
  public AddStatement add(Map<String, ?> doc) {
    throw new FeatureNotAvailableException("TODO: ");
  }
  
  public AddStatement add(String... jsonString) {
    try {
      DbDoc[] docs = new DbDoc[jsonString.length];
      for (int i = 0; i < jsonString.length; i++)
        docs[i] = JsonParser.parseDoc(new StringReader(jsonString[i])); 
      return add(docs);
    } catch (IOException ex) {
      throw AssertionFailedException.shouldNotHappen(ex);
    } 
  }
  
  public AddStatement add(DbDoc doc) {
    return new AddStatementImpl(this.mysqlxSession, this.schema.getName(), this.name, doc);
  }
  
  public AddStatement add(DbDoc... docs) {
    return new AddStatementImpl(this.mysqlxSession, this.schema.getName(), this.name, docs);
  }
  
  public FindStatement find() {
    return find(null);
  }
  
  public FindStatement find(String searchCondition) {
    return new FindStatementImpl(this.mysqlxSession, this.schema.getName(), this.name, searchCondition);
  }
  
  public ModifyStatement modify(String searchCondition) {
    return new ModifyStatementImpl(this.mysqlxSession, this.schema.getName(), this.name, searchCondition);
  }
  
  public RemoveStatement remove(String searchCondition) {
    return new RemoveStatementImpl(this.mysqlxSession, this.schema.getName(), this.name, searchCondition);
  }
  
  public Result createIndex(String indexName, DbDoc indexDefinition) {
    return (Result)this.mysqlxSession.query((Message)this.xbuilder
        .buildCreateCollectionIndex(this.schema.getName(), this.name, new CreateIndexParams(indexName, indexDefinition)), new UpdateResultBuilder<>());
  }
  
  public Result createIndex(String indexName, String jsonIndexDefinition) {
    return (Result)this.mysqlxSession.query((Message)this.xbuilder
        .buildCreateCollectionIndex(this.schema.getName(), this.name, new CreateIndexParams(indexName, jsonIndexDefinition)), new UpdateResultBuilder<>());
  }
  
  public void dropIndex(String indexName) {
    try {
      this.mysqlxSession.query((Message)this.xbuilder.buildDropCollectionIndex(this.schema.getName(), this.name, indexName), new UpdateResultBuilder<>());
    } catch (XProtocolError e) {
      if (e.getErrorCode() != 1091)
        throw e; 
    } 
  }
  
  public long count() {
    try {
      return this.mysqlxSession.getDataStoreMetadata().getTableRowCount(this.schema.getName(), this.name);
    } catch (XProtocolError e) {
      if (e.getErrorCode() == 1146)
        throw new XProtocolError("Collection '" + this.name + "' does not exist in schema '" + this.schema.getName() + "'", e); 
      throw e;
    } 
  }
  
  public DbDoc newDoc() {
    return new DbDocImpl();
  }
  
  public boolean equals(Object other) {
    return (other != null && other.getClass() == CollectionImpl.class && ((CollectionImpl)other).schema.equals(this.schema) && ((CollectionImpl)other).mysqlxSession == this.mysqlxSession && this.name
      .equals(((CollectionImpl)other).name));
  }
  
  public int hashCode() {
    assert false : "hashCode not designed";
    return 0;
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder("Collection(");
    sb.append(ExprUnparser.quoteIdentifier(this.schema.getName()));
    sb.append(".");
    sb.append(ExprUnparser.quoteIdentifier(this.name));
    sb.append(")");
    return sb.toString();
  }
  
  public Result replaceOne(String id, DbDoc doc) {
    if (id == null)
      throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[] { "id" })); 
    if (doc == null)
      throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[] { "doc" })); 
    JsonValue docId = doc.get("_id");
    if (docId != null && (!JsonString.class.isInstance(docId) || !id.equals(((JsonString)docId).getString())))
      throw new XDevAPIError(Messages.getString("Collection.DocIdMismatch")); 
    return modify("_id = :id").set("$", doc).bind("id", id).execute();
  }
  
  public Result replaceOne(String id, String jsonString) {
    if (id == null)
      throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[] { "id" })); 
    if (jsonString == null)
      throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[] { "jsonString" })); 
    try {
      return replaceOne(id, JsonParser.parseDoc(new StringReader(jsonString)));
    } catch (IOException e) {
      throw AssertionFailedException.shouldNotHappen(e);
    } 
  }
  
  public Result addOrReplaceOne(String id, DbDoc doc) {
    if (id == null)
      throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[] { "id" })); 
    if (doc == null)
      throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[] { "doc" })); 
    JsonValue docId = doc.get("_id");
    if (docId == null) {
      doc.add("_id", (new JsonString()).setValue(id));
    } else if (!JsonString.class.isInstance(docId) || !id.equals(((JsonString)docId).getString())) {
      throw new XDevAPIError(Messages.getString("Collection.DocIdMismatch"));
    } 
    return add(doc).setUpsert(true).execute();
  }
  
  public Result addOrReplaceOne(String id, String jsonString) {
    if (id == null)
      throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[] { "id" })); 
    if (jsonString == null)
      throw new XDevAPIError(Messages.getString("CreateTableStatement.0", new String[] { "jsonString" })); 
    try {
      return addOrReplaceOne(id, JsonParser.parseDoc(new StringReader(jsonString)));
    } catch (IOException e) {
      throw AssertionFailedException.shouldNotHappen(e);
    } 
  }
  
  public DbDoc getOne(String id) {
    return find("_id = :id").bind("id", id).execute().fetchOne();
  }
  
  public Result removeOne(String id) {
    return remove("_id = :id").bind("id", id).execute();
  }
}
