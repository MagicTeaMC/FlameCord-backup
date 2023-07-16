package com.mysql.cj.xdevapi;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlxSession;
import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.DefaultPropertySet;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.x.XMessageBuilder;
import com.mysql.cj.protocol.x.XProtocol;
import com.mysql.cj.protocol.x.XProtocolError;
import com.mysql.cj.result.Row;
import com.mysql.cj.result.StringValueFactory;
import com.mysql.cj.result.ValueFactory;
import com.mysql.cj.util.StringUtils;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SessionImpl implements Session {
  protected MysqlxSession session;
  
  protected String defaultSchemaName;
  
  private XMessageBuilder xbuilder;
  
  public SessionImpl(HostInfo hostInfo) {
    DefaultPropertySet defaultPropertySet = new DefaultPropertySet();
    defaultPropertySet.initializeProperties(hostInfo.exposeAsProperties());
    this.session = new MysqlxSession(hostInfo, (PropertySet)defaultPropertySet);
    this.defaultSchemaName = hostInfo.getDatabase();
    this.xbuilder = (XMessageBuilder)this.session.getMessageBuilder();
  }
  
  public SessionImpl(XProtocol prot) {
    this.session = new MysqlxSession(prot);
    this.defaultSchemaName = prot.defaultSchemaName;
    this.xbuilder = (XMessageBuilder)this.session.getMessageBuilder();
  }
  
  protected SessionImpl() {}
  
  public List<Schema> getSchemas() {
    Function<Row, String> rowToName = r -> (String)r.getValue(0, (ValueFactory)new StringValueFactory(this.session.getPropertySet()));
    Function<Row, Schema> rowToSchema = rowToName.andThen(n -> new SchemaImpl(this.session, this, n));
    return (List<Schema>)this.session.query((Message)this.xbuilder.buildSqlStatement("select schema_name from information_schema.schemata"), null, rowToSchema, 
        Collectors.toList());
  }
  
  public Schema getSchema(String schemaName) {
    return new SchemaImpl(this.session, this, schemaName);
  }
  
  public String getDefaultSchemaName() {
    return this.defaultSchemaName;
  }
  
  public Schema getDefaultSchema() {
    if (this.defaultSchemaName == null || this.defaultSchemaName.length() == 0)
      return null; 
    return new SchemaImpl(this.session, this, this.defaultSchemaName);
  }
  
  public Schema createSchema(String schemaName) {
    StringBuilder stmtString = new StringBuilder("CREATE DATABASE ");
    stmtString.append(StringUtils.quoteIdentifier(schemaName, true));
    this.session.query((Message)this.xbuilder.buildSqlStatement(stmtString.toString()), new UpdateResultBuilder<>());
    return getSchema(schemaName);
  }
  
  public Schema createSchema(String schemaName, boolean reuseExistingObject) {
    try {
      return createSchema(schemaName);
    } catch (XProtocolError ex) {
      if (ex.getErrorCode() == 1007)
        return getSchema(schemaName); 
      throw ex;
    } 
  }
  
  public void dropSchema(String schemaName) {
    StringBuilder stmtString = new StringBuilder("DROP DATABASE ");
    stmtString.append(StringUtils.quoteIdentifier(schemaName, true));
    this.session.query((Message)this.xbuilder.buildSqlStatement(stmtString.toString()), new UpdateResultBuilder<>());
  }
  
  public void startTransaction() {
    this.session.query((Message)this.xbuilder.buildSqlStatement("START TRANSACTION"), new UpdateResultBuilder<>());
  }
  
  public void commit() {
    this.session.query((Message)this.xbuilder.buildSqlStatement("COMMIT"), new UpdateResultBuilder<>());
  }
  
  public void rollback() {
    this.session.query((Message)this.xbuilder.buildSqlStatement("ROLLBACK"), new UpdateResultBuilder<>());
  }
  
  public String setSavepoint() {
    return setSavepoint(StringUtils.getUniqueSavepointId());
  }
  
  public String setSavepoint(String name) {
    if (name == null || name.trim().length() == 0)
      throw new XDevAPIError(Messages.getString("XSession.0", new String[] { "name" })); 
    this.session.query((Message)this.xbuilder.buildSqlStatement("SAVEPOINT " + StringUtils.quoteIdentifier(name, true)), new UpdateResultBuilder<>());
    return name;
  }
  
  public void rollbackTo(String name) {
    if (name == null || name.trim().length() == 0)
      throw new XDevAPIError(Messages.getString("XSession.0", new String[] { "name" })); 
    this.session.query((Message)this.xbuilder.buildSqlStatement("ROLLBACK TO " + StringUtils.quoteIdentifier(name, true)), new UpdateResultBuilder<>());
  }
  
  public void releaseSavepoint(String name) {
    if (name == null || name.trim().length() == 0)
      throw new XDevAPIError(Messages.getString("XSession.0", new String[] { "name" })); 
    this.session.query((Message)this.xbuilder.buildSqlStatement("RELEASE SAVEPOINT " + StringUtils.quoteIdentifier(name, true)), new UpdateResultBuilder<>());
  }
  
  public String getUri() {
    PropertySet pset = this.session.getPropertySet();
    StringBuilder sb = new StringBuilder(ConnectionUrl.Type.XDEVAPI_SESSION.getScheme());
    sb.append("//").append(this.session.getProcessHost()).append(":").append(this.session.getPort()).append("/").append(this.defaultSchemaName).append("?");
    boolean isFirstParam = true;
    for (PropertyKey propKey : PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.keySet()) {
      RuntimeProperty<?> propToGet = pset.getProperty(propKey);
      if (propToGet.isExplicitlySet()) {
        String propValue = propToGet.getStringValue();
        Object defaultValue = propToGet.getPropertyDefinition().getDefaultValue();
        if ((defaultValue == null && !StringUtils.isNullOrEmpty(propValue)) || (defaultValue != null && propValue == null) || (defaultValue != null && propValue != null && 
          !propValue.equals(defaultValue.toString()))) {
          if (isFirstParam) {
            isFirstParam = false;
          } else {
            sb.append("&");
          } 
          sb.append(propKey.getKeyName());
          sb.append("=");
          sb.append(propValue);
        } 
      } 
    } 
    return sb.toString();
  }
  
  public boolean isOpen() {
    return !this.session.isClosed();
  }
  
  public void close() {
    this.session.quit();
  }
  
  public SqlStatementImpl sql(String sql) {
    return new SqlStatementImpl(this.session, sql);
  }
  
  public MysqlxSession getSession() {
    return this.session;
  }
}
