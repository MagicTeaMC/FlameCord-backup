package com.mysql.cj.protocol.x;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.mysql.cj.MessageBuilder;
import com.mysql.cj.Messages;
import com.mysql.cj.PreparedQuery;
import com.mysql.cj.QueryBindings;
import com.mysql.cj.Session;
import com.mysql.cj.exceptions.CJOperationNotSupportedException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.Security;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.x.protobuf.MysqlxConnection;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import com.mysql.cj.x.protobuf.MysqlxExpect;
import com.mysql.cj.x.protobuf.MysqlxExpr;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
import com.mysql.cj.x.protobuf.MysqlxSession;
import com.mysql.cj.x.protobuf.MysqlxSql;
import com.mysql.cj.xdevapi.CreateIndexParams;
import com.mysql.cj.xdevapi.ExprUtil;
import com.mysql.cj.xdevapi.FilterParams;
import com.mysql.cj.xdevapi.InsertParams;
import com.mysql.cj.xdevapi.Schema;
import com.mysql.cj.xdevapi.UpdateParams;
import com.mysql.cj.xdevapi.UpdateSpec;
import java.math.BigInteger;
import java.security.DigestException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

public class XMessageBuilder implements MessageBuilder<XMessage> {
  private static final String XPLUGIN_NAMESPACE = "mysqlx";
  
  public XMessage buildCapabilitiesGet() {
    return new XMessage((Message)MysqlxConnection.CapabilitiesGet.getDefaultInstance());
  }
  
  public XMessage buildCapabilitiesSet(Map<String, Object> keyValuePair) {
    MysqlxConnection.Capabilities.Builder capsB = MysqlxConnection.Capabilities.newBuilder();
    keyValuePair.forEach((k, v) -> {
          MysqlxDatatypes.Any val;
          if (XServerCapabilities.KEY_SESSION_CONNECT_ATTRS.equals(k) || XServerCapabilities.KEY_COMPRESSION.equals(k)) {
            MysqlxDatatypes.Object.Builder attrB = MysqlxDatatypes.Object.newBuilder();
            ((Map)v).forEach(());
            val = MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT).setObj(attrB).build();
          } else {
            val = ExprUtil.argObjectToScalarAny(v);
          } 
          MysqlxConnection.Capability cap = MysqlxConnection.Capability.newBuilder().setName(k).setValue(val).build();
          capsB.addCapabilities(cap);
        });
    return new XMessage((Message)MysqlxConnection.CapabilitiesSet.newBuilder().setCapabilities(capsB).build());
  }
  
  public XMessage buildDocInsert(String schemaName, String collectionName, List<String> json, boolean upsert) {
    MysqlxCrud.Insert.Builder builder = MysqlxCrud.Insert.newBuilder().setCollection(ExprUtil.buildCollection(schemaName, collectionName));
    if (upsert != builder.getUpsert())
      builder.setUpsert(upsert); 
    json.stream().map(str -> MysqlxCrud.Insert.TypedRow.newBuilder().addField(ExprUtil.argObjectToExpr(str, false)).build()).forEach(builder::addRow);
    return new XMessage((Message)builder.build());
  }
  
  private MysqlxCrud.Insert.Builder commonRowInsertBuilder(String schemaName, String tableName, InsertParams insertParams) {
    MysqlxCrud.Insert.Builder builder = MysqlxCrud.Insert.newBuilder().setDataModel(MysqlxCrud.DataModel.TABLE).setCollection(ExprUtil.buildCollection(schemaName, tableName));
    if (insertParams.getProjection() != null)
      builder.addAllProjection((List)insertParams.getProjection()); 
    return builder;
  }
  
  public XMessage buildRowInsert(String schemaName, String tableName, InsertParams insertParams) {
    MysqlxCrud.Insert.Builder builder = commonRowInsertBuilder(schemaName, tableName, insertParams);
    builder.addAllRow((List)insertParams.getRows());
    return new XMessage((Message)builder.build());
  }
  
  private MysqlxCrud.Update.Builder commonDocUpdateBuilder(FilterParams filterParams, List<UpdateSpec> updates) {
    MysqlxCrud.Update.Builder builder = MysqlxCrud.Update.newBuilder().setCollection((MysqlxCrud.Collection)filterParams.getCollection());
    updates.forEach(u -> {
          MysqlxCrud.UpdateOperation.Builder opBuilder = MysqlxCrud.UpdateOperation.newBuilder();
          opBuilder.setOperation((MysqlxCrud.UpdateOperation.UpdateType)u.getUpdateType());
          opBuilder.setSource((MysqlxExpr.ColumnIdentifier)u.getSource());
          if (u.getValue() != null)
            opBuilder.setValue((MysqlxExpr.Expr)u.getValue()); 
          builder.addOperation(opBuilder.build());
        });
    return builder;
  }
  
  public XMessage buildDocUpdate(FilterParams filterParams, List<UpdateSpec> updates) {
    MysqlxCrud.Update.Builder builder = commonDocUpdateBuilder(filterParams, updates);
    applyFilterParams(filterParams, builder::addAllOrder, builder::setLimit, builder::setCriteria, builder::addAllArgs);
    return new XMessage((Message)builder.build());
  }
  
  public XMessage buildPrepareDocUpdate(int preparedStatementId, FilterParams filterParams, List<UpdateSpec> updates) {
    MysqlxCrud.Update.Builder updateBuilder = commonDocUpdateBuilder(filterParams, updates);
    applyFilterParams(filterParams, updateBuilder::addAllOrder, updateBuilder::setLimitExpr, updateBuilder::setCriteria);
    MysqlxPrepare.Prepare.Builder builder = MysqlxPrepare.Prepare.newBuilder().setStmtId(preparedStatementId);
    builder.setStmt(MysqlxPrepare.Prepare.OneOfMessage.newBuilder().setType(MysqlxPrepare.Prepare.OneOfMessage.Type.UPDATE).setUpdate(updateBuilder.build()).build());
    return new XMessage((Message)builder.build());
  }
  
  private MysqlxCrud.Update.Builder commonRowUpdateBuilder(FilterParams filterParams, UpdateParams updateParams) {
    MysqlxCrud.Update.Builder builder = MysqlxCrud.Update.newBuilder().setDataModel(MysqlxCrud.DataModel.TABLE).setCollection((MysqlxCrud.Collection)filterParams.getCollection());
    ((Map)updateParams.getUpdates()).entrySet().stream()
      .map(e -> MysqlxCrud.UpdateOperation.newBuilder().setOperation(MysqlxCrud.UpdateOperation.UpdateType.SET).setSource((MysqlxExpr.ColumnIdentifier)e.getKey()).setValue((MysqlxExpr.Expr)e.getValue()).build())
      .forEach(builder::addOperation);
    return builder;
  }
  
  public XMessage buildRowUpdate(FilterParams filterParams, UpdateParams updateParams) {
    MysqlxCrud.Update.Builder builder = commonRowUpdateBuilder(filterParams, updateParams);
    applyFilterParams(filterParams, builder::addAllOrder, builder::setLimit, builder::setCriteria, builder::addAllArgs);
    return new XMessage((Message)builder.build());
  }
  
  public XMessage buildPrepareRowUpdate(int preparedStatementId, FilterParams filterParams, UpdateParams updateParams) {
    MysqlxCrud.Update.Builder updateBuilder = commonRowUpdateBuilder(filterParams, updateParams);
    applyFilterParams(filterParams, updateBuilder::addAllOrder, updateBuilder::setLimitExpr, updateBuilder::setCriteria);
    MysqlxPrepare.Prepare.Builder builder = MysqlxPrepare.Prepare.newBuilder().setStmtId(preparedStatementId);
    builder.setStmt(MysqlxPrepare.Prepare.OneOfMessage.newBuilder().setType(MysqlxPrepare.Prepare.OneOfMessage.Type.UPDATE).setUpdate(updateBuilder.build()).build());
    return new XMessage((Message)builder.build());
  }
  
  private MysqlxCrud.Find.Builder commonFindBuilder(FilterParams filterParams) {
    MysqlxCrud.Find.Builder builder = MysqlxCrud.Find.newBuilder().setCollection((MysqlxCrud.Collection)filterParams.getCollection());
    builder.setDataModel(filterParams.isRelational() ? MysqlxCrud.DataModel.TABLE : MysqlxCrud.DataModel.DOCUMENT);
    if (filterParams.getFields() != null)
      builder.addAllProjection((List)filterParams.getFields()); 
    if (filterParams.getGrouping() != null)
      builder.addAllGrouping((List)filterParams.getGrouping()); 
    if (filterParams.getGroupingCriteria() != null)
      builder.setGroupingCriteria((MysqlxExpr.Expr)filterParams.getGroupingCriteria()); 
    if (filterParams.getLock() != null)
      builder.setLocking(MysqlxCrud.Find.RowLock.forNumber(filterParams.getLock().asNumber())); 
    if (filterParams.getLockOption() != null)
      builder.setLockingOptions(MysqlxCrud.Find.RowLockOptions.forNumber(filterParams.getLockOption().asNumber())); 
    return builder;
  }
  
  public XMessage buildFind(FilterParams filterParams) {
    MysqlxCrud.Find.Builder builder = commonFindBuilder(filterParams);
    applyFilterParams(filterParams, builder::addAllOrder, builder::setLimit, builder::setCriteria, builder::addAllArgs);
    return new XMessage((Message)builder.build());
  }
  
  public XMessage buildPrepareFind(int preparedStatementId, FilterParams filterParams) {
    MysqlxCrud.Find.Builder findBuilder = commonFindBuilder(filterParams);
    applyFilterParams(filterParams, findBuilder::addAllOrder, findBuilder::setLimitExpr, findBuilder::setCriteria);
    MysqlxPrepare.Prepare.Builder builder = MysqlxPrepare.Prepare.newBuilder().setStmtId(preparedStatementId);
    builder.setStmt(MysqlxPrepare.Prepare.OneOfMessage.newBuilder().setType(MysqlxPrepare.Prepare.OneOfMessage.Type.FIND).setFind(findBuilder.build()).build());
    return new XMessage((Message)builder.build());
  }
  
  private MysqlxCrud.Delete.Builder commonDeleteBuilder(FilterParams filterParams) {
    MysqlxCrud.Delete.Builder builder = MysqlxCrud.Delete.newBuilder().setCollection((MysqlxCrud.Collection)filterParams.getCollection());
    return builder;
  }
  
  public XMessage buildDelete(FilterParams filterParams) {
    MysqlxCrud.Delete.Builder builder = commonDeleteBuilder(filterParams);
    applyFilterParams(filterParams, builder::addAllOrder, builder::setLimit, builder::setCriteria, builder::addAllArgs);
    return new XMessage((Message)builder.build());
  }
  
  public XMessage buildPrepareDelete(int preparedStatementId, FilterParams filterParams) {
    MysqlxCrud.Delete.Builder deleteBuilder = commonDeleteBuilder(filterParams);
    applyFilterParams(filterParams, deleteBuilder::addAllOrder, deleteBuilder::setLimitExpr, deleteBuilder::setCriteria);
    MysqlxPrepare.Prepare.Builder builder = MysqlxPrepare.Prepare.newBuilder().setStmtId(preparedStatementId);
    builder.setStmt(MysqlxPrepare.Prepare.OneOfMessage.newBuilder().setType(MysqlxPrepare.Prepare.OneOfMessage.Type.DELETE).setDelete(deleteBuilder.build()).build());
    return new XMessage((Message)builder.build());
  }
  
  private MysqlxSql.StmtExecute.Builder commonSqlStatementBuilder(String statement) {
    MysqlxSql.StmtExecute.Builder builder = MysqlxSql.StmtExecute.newBuilder();
    builder.setStmt(ByteString.copyFromUtf8(statement));
    return builder;
  }
  
  public XMessage buildSqlStatement(String statement) {
    return buildSqlStatement(statement, (List<Object>)null);
  }
  
  public XMessage buildSqlStatement(String statement, List<Object> args) {
    MysqlxSql.StmtExecute.Builder builder = commonSqlStatementBuilder(statement);
    if (args != null)
      builder.addAllArgs((Iterable)args.stream().map(ExprUtil::argObjectToScalarAny).collect(Collectors.toList())); 
    return new XMessage((Message)builder.build());
  }
  
  public XMessage buildPrepareSqlStatement(int preparedStatementId, String statement) {
    MysqlxSql.StmtExecute.Builder stmtExecBuilder = commonSqlStatementBuilder(statement);
    MysqlxPrepare.Prepare.Builder builder = MysqlxPrepare.Prepare.newBuilder().setStmtId(preparedStatementId);
    builder.setStmt(MysqlxPrepare.Prepare.OneOfMessage.newBuilder().setType(MysqlxPrepare.Prepare.OneOfMessage.Type.STMT).setStmtExecute(stmtExecBuilder.build()).build());
    return new XMessage((Message)builder.build());
  }
  
  private static void applyFilterParams(FilterParams filterParams, Consumer<List<MysqlxCrud.Order>> setOrder, Consumer<MysqlxCrud.Limit> setLimit, Consumer<MysqlxExpr.Expr> setCriteria, Consumer<List<MysqlxDatatypes.Scalar>> setArgs) {
    filterParams.verifyAllArgsBound();
    if (filterParams.getOrder() != null)
      setOrder.accept((List<MysqlxCrud.Order>)filterParams.getOrder()); 
    if (filterParams.getLimit() != null) {
      MysqlxCrud.Limit.Builder lb = MysqlxCrud.Limit.newBuilder().setRowCount(filterParams.getLimit().longValue());
      if (filterParams.getOffset() != null)
        lb.setOffset(filterParams.getOffset().longValue()); 
      setLimit.accept(lb.build());
    } 
    if (filterParams.getCriteria() != null)
      setCriteria.accept((MysqlxExpr.Expr)filterParams.getCriteria()); 
    if (filterParams.getArgs() != null)
      setArgs.accept((List<MysqlxDatatypes.Scalar>)filterParams.getArgs()); 
  }
  
  private static void applyFilterParams(FilterParams filterParams, Consumer<List<MysqlxCrud.Order>> setOrder, Consumer<MysqlxCrud.LimitExpr> setLimit, Consumer<MysqlxExpr.Expr> setCriteria) {
    if (filterParams.getOrder() != null)
      setOrder.accept((List<MysqlxCrud.Order>)filterParams.getOrder()); 
    Object argsList = filterParams.getArgs();
    int numberOfArgs = (argsList == null) ? 0 : ((List)argsList).size();
    if (filterParams.getLimit() != null) {
      MysqlxCrud.LimitExpr.Builder lb = MysqlxCrud.LimitExpr.newBuilder().setRowCount(ExprUtil.buildPlaceholderExpr(numberOfArgs));
      if (filterParams.supportsOffset())
        lb.setOffset(ExprUtil.buildPlaceholderExpr(numberOfArgs + 1)); 
      setLimit.accept(lb.build());
    } 
    if (filterParams.getCriteria() != null)
      setCriteria.accept((MysqlxExpr.Expr)filterParams.getCriteria()); 
  }
  
  public XMessage buildPrepareExecute(int preparedStatementId, FilterParams filterParams) {
    MysqlxPrepare.Execute.Builder builder = MysqlxPrepare.Execute.newBuilder().setStmtId(preparedStatementId);
    if (filterParams.getArgs() != null)
      builder.addAllArgs((Iterable)((List)filterParams.getArgs()).stream().map(s -> MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.SCALAR).setScalar(s).build())
          .collect(Collectors.toList())); 
    if (filterParams.getLimit() != null) {
      builder.addArgs(ExprUtil.anyOf(ExprUtil.scalarOf(filterParams.getLimit().longValue())));
      if (filterParams.supportsOffset())
        builder.addArgs(ExprUtil.anyOf(ExprUtil.scalarOf((filterParams.getOffset() != null) ? filterParams.getOffset().longValue() : 0L))); 
    } 
    return new XMessage((Message)builder.build());
  }
  
  public XMessage buildPrepareDeallocate(int preparedStatementId) {
    MysqlxPrepare.Deallocate.Builder builder = MysqlxPrepare.Deallocate.newBuilder().setStmtId(preparedStatementId);
    return new XMessage((Message)builder.build());
  }
  
  public XMessage buildCreateCollection(String schemaName, String collectionName, Schema.CreateCollectionOptions options) {
    if (schemaName == null)
      throw new XProtocolError(Messages.getString("CreateTableStatement.0", new String[] { "schemaName" })); 
    if (collectionName == null)
      throw new XProtocolError(Messages.getString("CreateTableStatement.0", new String[] { "collectionName" })); 
    MysqlxDatatypes.Object.Builder argsBuilder = MysqlxDatatypes.Object.newBuilder().addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("name").setValue(ExprUtil.buildAny(collectionName))).addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("schema").setValue(ExprUtil.buildAny(schemaName)));
    MysqlxDatatypes.Object.Builder optBuilder = MysqlxDatatypes.Object.newBuilder();
    boolean hasOptions = false;
    if (options.getReuseExisting() != null) {
      hasOptions = true;
      optBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("reuse_existing").setValue(ExprUtil.buildAny(options.getReuseExisting().booleanValue())));
    } 
    if (options.getValidation() != null) {
      hasOptions = true;
      MysqlxDatatypes.Object.Builder validationBuilder = MysqlxDatatypes.Object.newBuilder();
      if (options.getValidation().getSchema() != null)
        validationBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("schema").setValue(ExprUtil.buildAny(options.getValidation().getSchema()))); 
      if (options.getValidation().getLevel() != null)
        validationBuilder
          .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("level").setValue(ExprUtil.buildAny(options.getValidation().getLevel().name().toLowerCase()))); 
      optBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("validation").setValue(MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT).setObj(validationBuilder)));
    } 
    if (hasOptions)
      argsBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("options").setValue(MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT).setObj(optBuilder))); 
    return new XMessage((Message)buildXpluginCommand(XpluginStatementCommand.XPLUGIN_STMT_CREATE_COLLECTION, new MysqlxDatatypes.Any[] { MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT).setObj(argsBuilder).build() }));
  }
  
  public XMessage buildModifyCollectionOptions(String schemaName, String collectionName, Schema.ModifyCollectionOptions options) {
    if (schemaName == null)
      throw new XProtocolError(Messages.getString("CreateTableStatement.0", new String[] { "schemaName" })); 
    if (collectionName == null)
      throw new XProtocolError(Messages.getString("CreateTableStatement.0", new String[] { "collectionName" })); 
    MysqlxDatatypes.Object.Builder argsBuilder = MysqlxDatatypes.Object.newBuilder().addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("name").setValue(ExprUtil.buildAny(collectionName))).addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("schema").setValue(ExprUtil.buildAny(schemaName)));
    MysqlxDatatypes.Object.Builder optBuilder = MysqlxDatatypes.Object.newBuilder();
    if (options != null && options.getValidation() != null) {
      MysqlxDatatypes.Object.Builder validationBuilder = MysqlxDatatypes.Object.newBuilder();
      if (options.getValidation().getSchema() != null)
        validationBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("schema").setValue(ExprUtil.buildAny(options.getValidation().getSchema()))); 
      if (options.getValidation().getLevel() != null)
        validationBuilder
          .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("level").setValue(ExprUtil.buildAny(options.getValidation().getLevel().name().toLowerCase()))); 
      optBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("validation").setValue(MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT).setObj(validationBuilder)));
    } 
    argsBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("options").setValue(MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT).setObj(optBuilder)));
    return new XMessage((Message)buildXpluginCommand(XpluginStatementCommand.XPLUGIN_STMT_MODIFY_COLLECTION_OPTIONS, new MysqlxDatatypes.Any[] { MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT).setObj(argsBuilder).build() }));
  }
  
  public XMessage buildCreateCollection(String schemaName, String collectionName) {
    if (schemaName == null)
      throw new XProtocolError(Messages.getString("CreateTableStatement.0", new String[] { "schemaName" })); 
    if (collectionName == null)
      throw new XProtocolError(Messages.getString("CreateTableStatement.0", new String[] { "collectionName" })); 
    return new XMessage((Message)buildXpluginCommand(XpluginStatementCommand.XPLUGIN_STMT_CREATE_COLLECTION, new MysqlxDatatypes.Any[] { MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT)
            .setObj(MysqlxDatatypes.Object.newBuilder()
              .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("name").setValue(ExprUtil.buildAny(collectionName)))
              .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("schema").setValue(ExprUtil.buildAny(schemaName))))
            .build() }));
  }
  
  public XMessage buildDropCollection(String schemaName, String collectionName) {
    if (schemaName == null)
      throw new XProtocolError(Messages.getString("CreateTableStatement.0", new String[] { "schemaName" })); 
    if (collectionName == null)
      throw new XProtocolError(Messages.getString("CreateTableStatement.0", new String[] { "collectionName" })); 
    return new XMessage((Message)buildXpluginCommand(XpluginStatementCommand.XPLUGIN_STMT_DROP_COLLECTION, new MysqlxDatatypes.Any[] { MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT)
            .setObj(MysqlxDatatypes.Object.newBuilder()
              .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("name").setValue(ExprUtil.buildAny(collectionName)))
              .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("schema").setValue(ExprUtil.buildAny(schemaName))))
            .build() }));
  }
  
  public XMessage buildClose() {
    return new XMessage((Message)MysqlxSession.Close.getDefaultInstance());
  }
  
  public XMessage buildListObjects(String schemaName, String pattern) {
    if (schemaName == null)
      throw new XProtocolError(Messages.getString("CreateTableStatement.0", new String[] { "schemaName" })); 
    MysqlxDatatypes.Object.Builder obj = MysqlxDatatypes.Object.newBuilder().addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("schema").setValue(ExprUtil.buildAny(schemaName)));
    if (pattern != null)
      obj.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("pattern").setValue(ExprUtil.buildAny(pattern))); 
    return new XMessage((Message)
        buildXpluginCommand(XpluginStatementCommand.XPLUGIN_STMT_LIST_OBJECTS, new MysqlxDatatypes.Any[] { MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT).setObj(obj).build() }));
  }
  
  public XMessage buildEnableNotices(String... notices) {
    MysqlxDatatypes.Array.Builder abuilder = MysqlxDatatypes.Array.newBuilder();
    for (String notice : notices)
      abuilder.addValue(ExprUtil.buildAny(notice)); 
    return new XMessage((Message)buildXpluginCommand(XpluginStatementCommand.XPLUGIN_STMT_ENABLE_NOTICES, new MysqlxDatatypes.Any[] { MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT)
            .setObj(MysqlxDatatypes.Object.newBuilder()
              .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("notice").setValue(MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.ARRAY).setArray(abuilder))))
            .build() }));
  }
  
  public XMessage buildDisableNotices(String... notices) {
    MysqlxDatatypes.Array.Builder abuilder = MysqlxDatatypes.Array.newBuilder();
    for (String notice : notices)
      abuilder.addValue(ExprUtil.buildAny(notice)); 
    return new XMessage((Message)buildXpluginCommand(XpluginStatementCommand.XPLUGIN_STMT_DISABLE_NOTICES, new MysqlxDatatypes.Any[] { MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT)
            .setObj(MysqlxDatatypes.Object.newBuilder()
              .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("notice").setValue(MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.ARRAY).setArray(abuilder))))
            .build() }));
  }
  
  public XMessage buildListNotices() {
    return new XMessage((Message)buildXpluginCommand(XpluginStatementCommand.XPLUGIN_STMT_LIST_NOTICES, new MysqlxDatatypes.Any[0]));
  }
  
  public XMessage buildCreateCollectionIndex(String schemaName, String collectionName, CreateIndexParams params) {
    MysqlxDatatypes.Object.Builder builder = MysqlxDatatypes.Object.newBuilder();
    builder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("name").setValue(ExprUtil.buildAny(params.getIndexName())))
      .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("collection").setValue(ExprUtil.buildAny(collectionName)))
      .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("schema").setValue(ExprUtil.buildAny(schemaName)))
      .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("unique").setValue(ExprUtil.buildAny(false)));
    if (params.getIndexType() != null)
      builder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("type").setValue(ExprUtil.buildAny(params.getIndexType()))); 
    MysqlxDatatypes.Array.Builder aBuilder = MysqlxDatatypes.Array.newBuilder();
    for (CreateIndexParams.IndexField indexField : params.getFields()) {
      MysqlxDatatypes.Object.Builder fBuilder = MysqlxDatatypes.Object.newBuilder().addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("member").setValue(ExprUtil.buildAny(indexField.getField()))).addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("type").setValue(ExprUtil.buildAny(indexField.getType())));
      if (indexField.isRequired() != null)
        fBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("required").setValue(ExprUtil.buildAny(indexField.isRequired().booleanValue()))); 
      if (indexField.getOptions() != null)
        fBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("options").setValue(MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.SCALAR)
              .setScalar(MysqlxDatatypes.Scalar.newBuilder().setType(MysqlxDatatypes.Scalar.Type.V_UINT).setVUnsignedInt(indexField.getOptions().intValue())).build())); 
      if (indexField.getSrid() != null)
        fBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("srid").setValue(MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.SCALAR)
              .setScalar(MysqlxDatatypes.Scalar.newBuilder().setType(MysqlxDatatypes.Scalar.Type.V_UINT).setVUnsignedInt(indexField.getSrid().intValue())).build())); 
      if (indexField.isArray() != null)
        fBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("array").setValue(ExprUtil.buildAny(indexField.isArray().booleanValue()))); 
      aBuilder.addValue(MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT).setObj(fBuilder));
    } 
    builder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("constraint").setValue(MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.ARRAY).setArray(aBuilder)));
    return new XMessage((Message)buildXpluginCommand(XpluginStatementCommand.XPLUGIN_STMT_CREATE_COLLECTION_INDEX, new MysqlxDatatypes.Any[] { MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT).setObj(builder).build() }));
  }
  
  public XMessage buildDropCollectionIndex(String schemaName, String collectionName, String indexName) {
    return new XMessage((Message)buildXpluginCommand(XpluginStatementCommand.XPLUGIN_STMT_DROP_COLLECTION_INDEX, new MysqlxDatatypes.Any[] { MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT)
            .setObj(MysqlxDatatypes.Object.newBuilder()
              .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("name").setValue(ExprUtil.buildAny(indexName)))
              .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("collection").setValue(ExprUtil.buildAny(collectionName)))
              .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("schema").setValue(ExprUtil.buildAny(schemaName))))
            
            .build() }));
  }
  
  private MysqlxSql.StmtExecute buildXpluginCommand(XpluginStatementCommand command, MysqlxDatatypes.Any... args) {
    MysqlxSql.StmtExecute.Builder builder = MysqlxSql.StmtExecute.newBuilder();
    builder.setNamespace("mysqlx");
    builder.setStmt(ByteString.copyFromUtf8(command.commandName));
    Arrays.<MysqlxDatatypes.Any>stream(args).forEach(a -> builder.addArgs(a));
    return builder.build();
  }
  
  public XMessage buildSha256MemoryAuthStart() {
    return new XMessage((Message)MysqlxSession.AuthenticateStart.newBuilder().setMechName("SHA256_MEMORY").build());
  }
  
  public XMessage buildSha256MemoryAuthContinue(String user, String password, byte[] nonce, String database) {
    String encoding = "UTF8";
    byte[] databaseBytes = (database == null) ? new byte[0] : StringUtils.getBytes(database, encoding);
    byte[] userBytes = (user == null) ? new byte[0] : StringUtils.getBytes(user, encoding);
    byte[] passwordBytes = (password == null || password.length() == 0) ? new byte[0] : StringUtils.getBytes(password, encoding);
    byte[] hashedPassword = passwordBytes;
    try {
      hashedPassword = Security.scrambleCachingSha2(passwordBytes, nonce);
    } catch (DigestException e) {
      throw new RuntimeException(e);
    } 
    hashedPassword = StringUtils.toHexString(hashedPassword, hashedPassword.length).getBytes();
    byte[] reply = new byte[databaseBytes.length + userBytes.length + hashedPassword.length + 2];
    System.arraycopy(databaseBytes, 0, reply, 0, databaseBytes.length);
    int pos = databaseBytes.length;
    reply[pos++] = 0;
    System.arraycopy(userBytes, 0, reply, pos, userBytes.length);
    pos += userBytes.length;
    reply[pos++] = 0;
    System.arraycopy(hashedPassword, 0, reply, pos, hashedPassword.length);
    MysqlxSession.AuthenticateContinue.Builder builder = MysqlxSession.AuthenticateContinue.newBuilder();
    builder.setAuthData(ByteString.copyFrom(reply));
    return new XMessage((Message)builder.build());
  }
  
  public XMessage buildMysql41AuthStart() {
    return new XMessage((Message)MysqlxSession.AuthenticateStart.newBuilder().setMechName("MYSQL41").build());
  }
  
  public XMessage buildMysql41AuthContinue(String user, String password, byte[] salt, String database) {
    String encoding = "UTF8";
    byte[] userBytes = (user == null) ? new byte[0] : StringUtils.getBytes(user, encoding);
    byte[] passwordBytes = (password == null || password.length() == 0) ? new byte[0] : StringUtils.getBytes(password, encoding);
    byte[] databaseBytes = (database == null) ? new byte[0] : StringUtils.getBytes(database, encoding);
    byte[] hashedPassword = passwordBytes;
    if (password != null && password.length() > 0) {
      hashedPassword = Security.scramble411(passwordBytes, salt);
      hashedPassword = String.format("*%040x", new Object[] { new BigInteger(1, hashedPassword) }).getBytes();
    } 
    byte[] reply = new byte[databaseBytes.length + userBytes.length + hashedPassword.length + 2];
    System.arraycopy(databaseBytes, 0, reply, 0, databaseBytes.length);
    int pos = databaseBytes.length;
    reply[pos++] = 0;
    System.arraycopy(userBytes, 0, reply, pos, userBytes.length);
    pos += userBytes.length;
    reply[pos++] = 0;
    System.arraycopy(hashedPassword, 0, reply, pos, hashedPassword.length);
    MysqlxSession.AuthenticateContinue.Builder builder = MysqlxSession.AuthenticateContinue.newBuilder();
    builder.setAuthData(ByteString.copyFrom(reply));
    return new XMessage((Message)builder.build());
  }
  
  public XMessage buildPlainAuthStart(final String user, final String password, String database) {
    CallbackHandler callbackHandler = new CallbackHandler() {
        public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
          for (Callback c : callbacks) {
            if (NameCallback.class.isAssignableFrom(c.getClass())) {
              ((NameCallback)c).setName(user);
            } else if (PasswordCallback.class.isAssignableFrom(c.getClass())) {
              ((PasswordCallback)c).setPassword((password == null) ? new char[0] : password.toCharArray());
            } else {
              throw new UnsupportedCallbackException(c);
            } 
          } 
        }
      };
    try {
      String[] mechanisms = { "PLAIN" };
      String authorizationId = (database == null || database.trim().length() == 0) ? null : database;
      String protocol = "X Protocol";
      Map<String, ?> props = null;
      String serverName = "<unknown>";
      SaslClient saslClient = Sasl.createSaslClient(mechanisms, authorizationId, protocol, serverName, props, callbackHandler);
      MysqlxSession.AuthenticateStart.Builder authStartBuilder = MysqlxSession.AuthenticateStart.newBuilder();
      authStartBuilder.setMechName("PLAIN");
      authStartBuilder.setAuthData(ByteString.copyFrom(saslClient.evaluateChallenge(null)));
      return new XMessage((Message)authStartBuilder.build());
    } catch (SaslException ex) {
      throw new RuntimeException(ex);
    } 
  }
  
  public XMessage buildExternalAuthStart(String database) {
    CallbackHandler callbackHandler = new CallbackHandler() {
        public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
          Callback[] arrayOfCallback = callbacks;
          int i = arrayOfCallback.length;
          byte b = 0;
          if (b < i) {
            Callback c = arrayOfCallback[b];
            if (NameCallback.class.isAssignableFrom(c.getClass()))
              throw new UnsupportedCallbackException(c); 
            if (PasswordCallback.class.isAssignableFrom(c.getClass()))
              throw new UnsupportedCallbackException(c); 
            throw new UnsupportedCallbackException(c);
          } 
        }
      };
    try {
      String[] mechanisms = { "EXTERNAL" };
      String authorizationId = (database == null || database.trim().length() == 0) ? null : database;
      String protocol = "X Protocol";
      Map<String, ?> props = null;
      String serverName = "<unknown>";
      SaslClient saslClient = Sasl.createSaslClient(mechanisms, authorizationId, protocol, serverName, props, callbackHandler);
      MysqlxSession.AuthenticateStart.Builder authStartBuilder = MysqlxSession.AuthenticateStart.newBuilder();
      authStartBuilder.setMechName("EXTERNAL");
      authStartBuilder.setAuthData(ByteString.copyFrom(saslClient.evaluateChallenge(null)));
      return new XMessage((Message)authStartBuilder.build());
    } catch (SaslException ex) {
      throw new RuntimeException(ex);
    } 
  }
  
  public XMessage buildSessionResetAndClose() {
    return new XMessage((Message)MysqlxSession.Reset.newBuilder().build());
  }
  
  public XMessage buildSessionResetKeepOpen() {
    return new XMessage((Message)MysqlxSession.Reset.newBuilder().setKeepOpen(true).build());
  }
  
  public XMessage buildExpectOpen() {
    return new XMessage((Message)MysqlxExpect.Open.newBuilder().addCond(MysqlxExpect.Open.Condition.newBuilder()
          .setConditionKey(2).setConditionValue(ByteString.copyFromUtf8("6.1"))).build());
  }
  
  public XMessage buildComQuery(XMessage sharedPacket, Session sess, PreparedQuery preparedQuery, QueryBindings bindings, String characterEncoding) {
    throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
  }
}
