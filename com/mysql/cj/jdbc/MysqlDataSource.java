package com.mysql.cj.jdbc;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.AbstractRuntimeProperty;
import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.util.StringUtils;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.DataSource;

public class MysqlDataSource extends JdbcPropertySetImpl implements DataSource, Referenceable, Serializable, JdbcPropertySet {
  static final long serialVersionUID = -5515846944416881264L;
  
  protected static final NonRegisteringDriver mysqlDriver;
  
  static {
    try {
      mysqlDriver = new NonRegisteringDriver();
    } catch (Exception E) {
      throw new RuntimeException(Messages.getString("MysqlDataSource.0"));
    } 
  }
  
  protected transient PrintWriter logWriter = null;
  
  protected String databaseName = null;
  
  protected String encoding = null;
  
  protected String url = null;
  
  protected boolean explicitUrl = false;
  
  protected String hostName = null;
  
  protected int port = 3306;
  
  protected boolean explicitPort = false;
  
  protected String user = null;
  
  protected String password = null;
  
  protected String profileSQLString = "false";
  
  protected String description = "MySQL Connector/J Data Source";
  
  public Connection getConnection() throws SQLException {
    try {
      return getConnection(this.user, this.password);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  public Connection getConnection(String userID, String pass) throws SQLException {
    try {
      Properties props = exposeAsProperties();
      if (userID != null)
        props.setProperty(PropertyKey.USER.getKeyName(), userID); 
      if (pass != null)
        props.setProperty(PropertyKey.PASSWORD.getKeyName(), pass); 
      return getConnection(props);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  public String getDescription() {
    return this.description;
  }
  
  public void setDescription(String value) {
    this.description = value;
  }
  
  public void setDatabaseName(String dbName) {
    this.databaseName = dbName;
  }
  
  public String getDatabaseName() {
    return (this.databaseName != null) ? this.databaseName : "";
  }
  
  public void setLogWriter(PrintWriter output) throws SQLException {
    try {
      this.logWriter = output;
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  public PrintWriter getLogWriter() {
    try {
      return this.logWriter;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  public void setLoginTimeout(int seconds) throws SQLException {
    try {
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  public int getLoginTimeout() {
    try {
      return 0;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  public void setPassword(String pass) {
    this.password = pass;
  }
  
  public String getPassword() {
    return this.password;
  }
  
  public void setPort(int p) {
    this.port = p;
    this.explicitPort = true;
  }
  
  public int getPort() {
    return this.port;
  }
  
  public void setPortNumber(int p) {
    setPort(p);
  }
  
  public int getPortNumber() {
    return getPort();
  }
  
  public void setPropertiesViaRef(Reference ref) throws SQLException {
    for (PropertyKey propKey : PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.keySet()) {
      RuntimeProperty<?> propToSet = getProperty(propKey);
      if (ref != null)
        propToSet.initializeFrom(ref, null); 
    } 
    postInitialization();
  }
  
  public Reference getReference() throws NamingException {
    String factoryName = MysqlDataSourceFactory.class.getName();
    Reference ref = new Reference(getClass().getName(), factoryName, null);
    ref.add(new StringRefAddr(PropertyKey.USER.getKeyName(), getUser()));
    ref.add(new StringRefAddr(PropertyKey.PASSWORD.getKeyName(), this.password));
    ref.add(new StringRefAddr("serverName", getServerName()));
    ref.add(new StringRefAddr("port", "" + getPort()));
    ref.add(new StringRefAddr("explicitPort", String.valueOf(this.explicitPort)));
    ref.add(new StringRefAddr("databaseName", getDatabaseName()));
    ref.add(new StringRefAddr("url", getUrl()));
    ref.add(new StringRefAddr("explicitUrl", String.valueOf(this.explicitUrl)));
    for (PropertyKey propKey : PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.keySet()) {
      RuntimeProperty<?> propToStore = getProperty(propKey);
      String val = propToStore.getStringValue();
      if (val != null)
        ref.add(new StringRefAddr(propToStore.getPropertyDefinition().getName(), val)); 
    } 
    return ref;
  }
  
  public void setServerName(String serverName) {
    this.hostName = serverName;
  }
  
  public String getServerName() {
    return (this.hostName != null) ? this.hostName : "";
  }
  
  public void setURL(String url) {
    setUrl(url);
  }
  
  public String getURL() {
    return getUrl();
  }
  
  public void setUrl(String url) {
    this.url = url;
    this.explicitUrl = true;
  }
  
  public String getUrl() {
    if (!this.explicitUrl) {
      StringBuilder sbUrl = new StringBuilder(ConnectionUrl.Type.SINGLE_CONNECTION.getScheme());
      sbUrl.append("//").append(StringUtils.urlEncode(getServerName()));
      try {
        if (this.explicitPort || !getBooleanRuntimeProperty(PropertyKey.dnsSrv.getKeyName()))
          sbUrl.append(":").append(getPort()); 
      } catch (SQLException e) {
        sbUrl.append(":").append(getPort());
      } 
      sbUrl.append("/").append(StringUtils.urlEncode(getDatabaseName()));
      return sbUrl.toString();
    } 
    return this.url;
  }
  
  public void setUser(String userID) {
    this.user = userID;
  }
  
  public String getUser() {
    return this.user;
  }
  
  protected Connection getConnection(Properties props) throws SQLException {
    String jdbcUrlToUse = this.explicitUrl ? this.url : getUrl();
    ConnectionUrl connUrl = ConnectionUrl.getConnectionUrlInstance(jdbcUrlToUse, null);
    Properties urlProps = connUrl.getConnectionArgumentsAsProperties();
    urlProps.remove(PropertyKey.HOST.getKeyName());
    urlProps.remove(PropertyKey.PORT.getKeyName());
    urlProps.remove(PropertyKey.DBNAME.getKeyName());
    urlProps.stringPropertyNames().stream().forEach(k -> props.setProperty(k, urlProps.getProperty(k)));
    return mysqlDriver.connect(jdbcUrlToUse, props);
  }
  
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return null;
  }
  
  public <T> T unwrap(Class<T> iface) throws SQLException {
    try {
      return null;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    try {
      return false;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  protected String getStringRuntimeProperty(String name) throws SQLException {
    try {
      return (String)getStringProperty(name).getValue();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  protected void setStringRuntimeProperty(String name, String value) throws SQLException {
    try {
      ((AbstractRuntimeProperty)getStringProperty(name)).setValueInternal(value, null, null);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  protected boolean getBooleanRuntimeProperty(String name) throws SQLException {
    try {
      return ((Boolean)getBooleanProperty(name).getValue()).booleanValue();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  protected void setBooleanRuntimeProperty(String name, boolean value) throws SQLException {
    try {
      ((AbstractRuntimeProperty)getBooleanProperty(name)).setValueInternal(Boolean.valueOf(value), null, null);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  protected int getIntegerRuntimeProperty(String name) throws SQLException {
    try {
      return ((Integer)getIntegerProperty(name).getValue()).intValue();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  protected void setIntegerRuntimeProperty(String name, int value) throws SQLException {
    try {
      ((AbstractRuntimeProperty)getIntegerProperty(name)).setValueInternal(Integer.valueOf(value), null, null);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  protected long getLongRuntimeProperty(String name) throws SQLException {
    try {
      return ((Long)getLongProperty(name).getValue()).longValue();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  protected void setLongRuntimeProperty(String name, long value) throws SQLException {
    try {
      ((AbstractRuntimeProperty)getLongProperty(name)).setValueInternal(Long.valueOf(value), null, null);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  protected int getMemorySizeRuntimeProperty(String name) throws SQLException {
    try {
      return ((Integer)getMemorySizeProperty(name).getValue()).intValue();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  protected void setMemorySizeRuntimeProperty(String name, int value) throws SQLException {
    try {
      ((AbstractRuntimeProperty)getMemorySizeProperty(name)).setValueInternal(Integer.valueOf(value), null, null);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  protected String getEnumRuntimeProperty(String name) throws SQLException {
    try {
      return getEnumProperty(name).getStringValue();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  protected void setEnumRuntimeProperty(String name, String value) throws SQLException {
    try {
      ((AbstractRuntimeProperty)getEnumProperty(name)).setValueInternal(value, null);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  public Properties exposeAsProperties() {
    Properties props = new Properties();
    for (PropertyKey propKey : PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.keySet()) {
      RuntimeProperty<?> propToGet = getProperty(propKey);
      String propValue = propToGet.getStringValue();
      if (propValue != null && propToGet.isExplicitlySet())
        props.setProperty(propToGet.getPropertyDefinition().getName(), propValue); 
    } 
    return props;
  }
  
  public int getTcpTrafficClass() throws SQLException {
    return getIntegerRuntimeProperty("tcpTrafficClass");
  }
  
  public void setTcpTrafficClass(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("tcpTrafficClass", paramInt);
  }
  
  public boolean getAllowMultiQueries() throws SQLException {
    return getBooleanRuntimeProperty("allowMultiQueries");
  }
  
  public void setAllowMultiQueries(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("allowMultiQueries", paramBoolean);
  }
  
  public boolean getUseReadAheadInput() throws SQLException {
    return getBooleanRuntimeProperty("useReadAheadInput");
  }
  
  public void setUseReadAheadInput(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("useReadAheadInput", paramBoolean);
  }
  
  public boolean getCacheServerConfiguration() throws SQLException {
    return getBooleanRuntimeProperty("cacheServerConfiguration");
  }
  
  public void setCacheServerConfiguration(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("cacheServerConfiguration", paramBoolean);
  }
  
  public boolean getCompensateOnDuplicateKeyUpdateCounts() throws SQLException {
    return getBooleanRuntimeProperty("compensateOnDuplicateKeyUpdateCounts");
  }
  
  public void setCompensateOnDuplicateKeyUpdateCounts(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("compensateOnDuplicateKeyUpdateCounts", paramBoolean);
  }
  
  public boolean getUseUsageAdvisor() throws SQLException {
    return getBooleanRuntimeProperty("useUsageAdvisor");
  }
  
  public void setUseUsageAdvisor(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("useUsageAdvisor", paramBoolean);
  }
  
  public String getAllowLoadLocalInfileInPath() throws SQLException {
    return getStringRuntimeProperty("allowLoadLocalInfileInPath");
  }
  
  public void setAllowLoadLocalInfileInPath(String paramString) throws SQLException {
    setStringRuntimeProperty("allowLoadLocalInfileInPath", paramString);
  }
  
  public boolean getElideSetAutoCommits() throws SQLException {
    return getBooleanRuntimeProperty("elideSetAutoCommits");
  }
  
  public void setElideSetAutoCommits(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("elideSetAutoCommits", paramBoolean);
  }
  
  public boolean getUseAffectedRows() throws SQLException {
    return getBooleanRuntimeProperty("useAffectedRows");
  }
  
  public void setUseAffectedRows(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("useAffectedRows", paramBoolean);
  }
  
  public String getZeroDateTimeBehavior() throws SQLException {
    return getEnumRuntimeProperty("zeroDateTimeBehavior");
  }
  
  public void setZeroDateTimeBehavior(String paramString) throws SQLException {
    setEnumRuntimeProperty("zeroDateTimeBehavior", paramString);
  }
  
  public int getTcpRcvBuf() throws SQLException {
    return getIntegerRuntimeProperty("tcpRcvBuf");
  }
  
  public void setTcpRcvBuf(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("tcpRcvBuf", paramInt);
  }
  
  public String getAuthenticationPlugins() throws SQLException {
    return getStringRuntimeProperty("authenticationPlugins");
  }
  
  public void setAuthenticationPlugins(String paramString) throws SQLException {
    setStringRuntimeProperty("authenticationPlugins", paramString);
  }
  
  public String getOciConfigFile() throws SQLException {
    return getStringRuntimeProperty("ociConfigFile");
  }
  
  public void setOciConfigFile(String paramString) throws SQLException {
    setStringRuntimeProperty("ociConfigFile", paramString);
  }
  
  public String getClobCharacterEncoding() throws SQLException {
    return getStringRuntimeProperty("clobCharacterEncoding");
  }
  
  public void setClobCharacterEncoding(String paramString) throws SQLException {
    setStringRuntimeProperty("clobCharacterEncoding", paramString);
  }
  
  public String getSslMode() throws SQLException {
    return getEnumRuntimeProperty("sslMode");
  }
  
  public void setSslMode(String paramString) throws SQLException {
    setEnumRuntimeProperty("sslMode", paramString);
  }
  
  public boolean getReadOnlyPropagatesToServer() throws SQLException {
    return getBooleanRuntimeProperty("readOnlyPropagatesToServer");
  }
  
  public void setReadOnlyPropagatesToServer(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("readOnlyPropagatesToServer", paramBoolean);
  }
  
  public boolean getTcpKeepAlive() throws SQLException {
    return getBooleanRuntimeProperty("tcpKeepAlive");
  }
  
  public void setTcpKeepAlive(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("tcpKeepAlive", paramBoolean);
  }
  
  public int getSelfDestructOnPingSecondsLifetime() throws SQLException {
    return getIntegerRuntimeProperty("selfDestructOnPingSecondsLifetime");
  }
  
  public void setSelfDestructOnPingSecondsLifetime(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("selfDestructOnPingSecondsLifetime", paramInt);
  }
  
  public boolean getIncludeThreadNamesAsStatementComment() throws SQLException {
    return getBooleanRuntimeProperty("includeThreadNamesAsStatementComment");
  }
  
  public void setIncludeThreadNamesAsStatementComment(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("includeThreadNamesAsStatementComment", paramBoolean);
  }
  
  public String getUseConfigs() throws SQLException {
    return getStringRuntimeProperty("useConfigs");
  }
  
  public void setUseConfigs(String paramString) throws SQLException {
    setStringRuntimeProperty("useConfigs", paramString);
  }
  
  public boolean getFallbackToSystemTrustStore() throws SQLException {
    return getBooleanRuntimeProperty("fallbackToSystemTrustStore");
  }
  
  public void setFallbackToSystemTrustStore(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("fallbackToSystemTrustStore", paramBoolean);
  }
  
  public boolean getAllowLoadLocalInfile() throws SQLException {
    return getBooleanRuntimeProperty("allowLoadLocalInfile");
  }
  
  public void setAllowLoadLocalInfile(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("allowLoadLocalInfile", paramBoolean);
  }
  
  public boolean getUseLocalSessionState() throws SQLException {
    return getBooleanRuntimeProperty("useLocalSessionState");
  }
  
  public void setUseLocalSessionState(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("useLocalSessionState", paramBoolean);
  }
  
  public String getTrustCertificateKeyStoreType() throws SQLException {
    return getStringRuntimeProperty("trustCertificateKeyStoreType");
  }
  
  public void setTrustCertificateKeyStoreType(String paramString) throws SQLException {
    setStringRuntimeProperty("trustCertificateKeyStoreType", paramString);
  }
  
  public int getBlobSendChunkSize() throws SQLException {
    return getIntegerRuntimeProperty("blobSendChunkSize");
  }
  
  public void setBlobSendChunkSize(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("blobSendChunkSize", paramInt);
  }
  
  public boolean getUseColumnNamesInFindColumn() throws SQLException {
    return getBooleanRuntimeProperty("useColumnNamesInFindColumn");
  }
  
  public void setUseColumnNamesInFindColumn(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("useColumnNamesInFindColumn", paramBoolean);
  }
  
  public boolean getTraceProtocol() throws SQLException {
    return getBooleanRuntimeProperty("traceProtocol");
  }
  
  public void setTraceProtocol(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("traceProtocol", paramBoolean);
  }
  
  public String getCharacterEncoding() throws SQLException {
    return getStringRuntimeProperty("characterEncoding");
  }
  
  public void setCharacterEncoding(String paramString) throws SQLException {
    setStringRuntimeProperty("characterEncoding", paramString);
  }
  
  public boolean getAllowNanAndInf() throws SQLException {
    return getBooleanRuntimeProperty("allowNanAndInf");
  }
  
  public void setAllowNanAndInf(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("allowNanAndInf", paramBoolean);
  }
  
  public boolean getAutoReconnectForPools() throws SQLException {
    return getBooleanRuntimeProperty("autoReconnectForPools");
  }
  
  public void setAutoReconnectForPools(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("autoReconnectForPools", paramBoolean);
  }
  
  public boolean getProcessEscapeCodesForPrepStmts() throws SQLException {
    return getBooleanRuntimeProperty("processEscapeCodesForPrepStmts");
  }
  
  public void setProcessEscapeCodesForPrepStmts(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("processEscapeCodesForPrepStmts", paramBoolean);
  }
  
  public int getLoadBalancePingTimeout() throws SQLException {
    return getIntegerRuntimeProperty("loadBalancePingTimeout");
  }
  
  public void setLoadBalancePingTimeout(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("loadBalancePingTimeout", paramInt);
  }
  
  public boolean getAllowMasterDownConnections() throws SQLException {
    return getBooleanRuntimeProperty("allowMasterDownConnections");
  }
  
  public void setAllowMasterDownConnections(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("allowMasterDownConnections", paramBoolean);
  }
  
  public boolean getDetectCustomCollations() throws SQLException {
    return getBooleanRuntimeProperty("detectCustomCollations");
  }
  
  public void setDetectCustomCollations(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("detectCustomCollations", paramBoolean);
  }
  
  public int getNetTimeoutForStreamingResults() throws SQLException {
    return getIntegerRuntimeProperty("netTimeoutForStreamingResults");
  }
  
  public void setNetTimeoutForStreamingResults(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("netTimeoutForStreamingResults", paramInt);
  }
  
  public boolean getUseUnbufferedInput() throws SQLException {
    return getBooleanRuntimeProperty("useUnbufferedInput");
  }
  
  public void setUseUnbufferedInput(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("useUnbufferedInput", paramBoolean);
  }
  
  public String getCustomCharsetMapping() throws SQLException {
    return getStringRuntimeProperty("customCharsetMapping");
  }
  
  public void setCustomCharsetMapping(String paramString) throws SQLException {
    setStringRuntimeProperty("customCharsetMapping", paramString);
  }
  
  public int getLoadBalanceBlacklistTimeout() throws SQLException {
    return getIntegerRuntimeProperty("loadBalanceBlacklistTimeout");
  }
  
  public void setLoadBalanceBlacklistTimeout(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("loadBalanceBlacklistTimeout", paramInt);
  }
  
  public String getLoadBalanceAutoCommitStatementRegex() throws SQLException {
    return getStringRuntimeProperty("loadBalanceAutoCommitStatementRegex");
  }
  
  public void setLoadBalanceAutoCommitStatementRegex(String paramString) throws SQLException {
    setStringRuntimeProperty("loadBalanceAutoCommitStatementRegex", paramString);
  }
  
  public String getSocksProxyHost() throws SQLException {
    return getStringRuntimeProperty("socksProxyHost");
  }
  
  public void setSocksProxyHost(String paramString) throws SQLException {
    setStringRuntimeProperty("socksProxyHost", paramString);
  }
  
  public boolean getIgnoreNonTxTables() throws SQLException {
    return getBooleanRuntimeProperty("ignoreNonTxTables");
  }
  
  public void setIgnoreNonTxTables(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("ignoreNonTxTables", paramBoolean);
  }
  
  public String getCharacterSetResults() throws SQLException {
    return getStringRuntimeProperty("characterSetResults");
  }
  
  public void setCharacterSetResults(String paramString) throws SQLException {
    setStringRuntimeProperty("characterSetResults", paramString);
  }
  
  public boolean getReconnectAtTxEnd() throws SQLException {
    return getBooleanRuntimeProperty("reconnectAtTxEnd");
  }
  
  public void setReconnectAtTxEnd(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("reconnectAtTxEnd", paramBoolean);
  }
  
  public int getMaxByteArrayAsHex() throws SQLException {
    return getIntegerRuntimeProperty("maxByteArrayAsHex");
  }
  
  public void setMaxByteArrayAsHex(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("maxByteArrayAsHex", paramInt);
  }
  
  public boolean getCachePrepStmts() throws SQLException {
    return getBooleanRuntimeProperty("cachePrepStmts");
  }
  
  public void setCachePrepStmts(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("cachePrepStmts", paramBoolean);
  }
  
  public boolean getLogSlowQueries() throws SQLException {
    return getBooleanRuntimeProperty("logSlowQueries");
  }
  
  public void setLogSlowQueries(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("logSlowQueries", paramBoolean);
  }
  
  public boolean getNullCatalogMeansCurrent() throws SQLException {
    return getBooleanRuntimeProperty("nullCatalogMeansCurrent");
  }
  
  public void setNullCatalogMeansCurrent(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("nullCatalogMeansCurrent", paramBoolean);
  }
  
  public String getClientCertificateKeyStorePassword() throws SQLException {
    return getStringRuntimeProperty("clientCertificateKeyStorePassword");
  }
  
  public void setClientCertificateKeyStorePassword(String paramString) throws SQLException {
    setStringRuntimeProperty("clientCertificateKeyStorePassword", paramString);
  }
  
  public String getClientCertificateKeyStoreUrl() throws SQLException {
    return getStringRuntimeProperty("clientCertificateKeyStoreUrl");
  }
  
  public void setClientCertificateKeyStoreUrl(String paramString) throws SQLException {
    setStringRuntimeProperty("clientCertificateKeyStoreUrl", paramString);
  }
  
  public String getClientCertificateKeyStoreType() throws SQLException {
    return getStringRuntimeProperty("clientCertificateKeyStoreType");
  }
  
  public void setClientCertificateKeyStoreType(String paramString) throws SQLException {
    setStringRuntimeProperty("clientCertificateKeyStoreType", paramString);
  }
  
  public boolean getEmptyStringsConvertToZero() throws SQLException {
    return getBooleanRuntimeProperty("emptyStringsConvertToZero");
  }
  
  public void setEmptyStringsConvertToZero(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("emptyStringsConvertToZero", paramBoolean);
  }
  
  public int getMaxRows() throws SQLException {
    return getIntegerRuntimeProperty("maxRows");
  }
  
  public void setMaxRows(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("maxRows", paramInt);
  }
  
  public int getPrepStmtCacheSize() throws SQLException {
    return getIntegerRuntimeProperty("prepStmtCacheSize");
  }
  
  public void setPrepStmtCacheSize(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("prepStmtCacheSize", paramInt);
  }
  
  public String getConnectionAttributes() throws SQLException {
    return getStringRuntimeProperty("connectionAttributes");
  }
  
  public void setConnectionAttributes(String paramString) throws SQLException {
    setStringRuntimeProperty("connectionAttributes", paramString);
  }
  
  public boolean getUltraDevHack() throws SQLException {
    return getBooleanRuntimeProperty("ultraDevHack");
  }
  
  public void setUltraDevHack(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("ultraDevHack", paramBoolean);
  }
  
  public String getReplicationConnectionGroup() throws SQLException {
    return getStringRuntimeProperty("replicationConnectionGroup");
  }
  
  public void setReplicationConnectionGroup(String paramString) throws SQLException {
    setStringRuntimeProperty("replicationConnectionGroup", paramString);
  }
  
  public String getLogger() throws SQLException {
    return getStringRuntimeProperty("logger");
  }
  
  public void setLogger(String paramString) throws SQLException {
    setStringRuntimeProperty("logger", paramString);
  }
  
  public int getSelfDestructOnPingMaxOperations() throws SQLException {
    return getIntegerRuntimeProperty("selfDestructOnPingMaxOperations");
  }
  
  public void setSelfDestructOnPingMaxOperations(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("selfDestructOnPingMaxOperations", paramInt);
  }
  
  public boolean getDisconnectOnExpiredPasswords() throws SQLException {
    return getBooleanRuntimeProperty("disconnectOnExpiredPasswords");
  }
  
  public void setDisconnectOnExpiredPasswords(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("disconnectOnExpiredPasswords", paramBoolean);
  }
  
  public int getReportMetricsIntervalMillis() throws SQLException {
    return getIntegerRuntimeProperty("reportMetricsIntervalMillis");
  }
  
  public void setReportMetricsIntervalMillis(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("reportMetricsIntervalMillis", paramInt);
  }
  
  public String getPassword2() throws SQLException {
    return getStringRuntimeProperty("password2");
  }
  
  public void setPassword2(String paramString) throws SQLException {
    setStringRuntimeProperty("password2", paramString);
  }
  
  public boolean getFallbackToSystemKeyStore() throws SQLException {
    return getBooleanRuntimeProperty("fallbackToSystemKeyStore");
  }
  
  public void setFallbackToSystemKeyStore(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("fallbackToSystemKeyStore", paramBoolean);
  }
  
  public boolean getDontCheckOnDuplicateKeyUpdateInSQL() throws SQLException {
    return getBooleanRuntimeProperty("dontCheckOnDuplicateKeyUpdateInSQL");
  }
  
  public void setDontCheckOnDuplicateKeyUpdateInSQL(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("dontCheckOnDuplicateKeyUpdateInSQL", paramBoolean);
  }
  
  public boolean getUseNanosForElapsedTime() throws SQLException {
    return getBooleanRuntimeProperty("useNanosForElapsedTime");
  }
  
  public void setUseNanosForElapsedTime(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("useNanosForElapsedTime", paramBoolean);
  }
  
  public String getConnectionCollation() throws SQLException {
    return getStringRuntimeProperty("connectionCollation");
  }
  
  public void setConnectionCollation(String paramString) throws SQLException {
    setStringRuntimeProperty("connectionCollation", paramString);
  }
  
  public int getSecondsBeforeRetryMaster() throws SQLException {
    return getIntegerRuntimeProperty("secondsBeforeRetryMaster");
  }
  
  public void setSecondsBeforeRetryMaster(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("secondsBeforeRetryMaster", paramInt);
  }
  
  public int getQueriesBeforeRetryMaster() throws SQLException {
    return getIntegerRuntimeProperty("queriesBeforeRetryMaster");
  }
  
  public void setQueriesBeforeRetryMaster(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("queriesBeforeRetryMaster", paramInt);
  }
  
  public boolean getPinGlobalTxToPhysicalConnection() throws SQLException {
    return getBooleanRuntimeProperty("pinGlobalTxToPhysicalConnection");
  }
  
  public void setPinGlobalTxToPhysicalConnection(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("pinGlobalTxToPhysicalConnection", paramBoolean);
  }
  
  public int getCallableStmtCacheSize() throws SQLException {
    return getIntegerRuntimeProperty("callableStmtCacheSize");
  }
  
  public void setCallableStmtCacheSize(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("callableStmtCacheSize", paramInt);
  }
  
  public boolean getEnableQueryTimeouts() throws SQLException {
    return getBooleanRuntimeProperty("enableQueryTimeouts");
  }
  
  public void setEnableQueryTimeouts(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("enableQueryTimeouts", paramBoolean);
  }
  
  public String getAuthenticationFidoCallbackHandler() throws SQLException {
    return getStringRuntimeProperty("authenticationFidoCallbackHandler");
  }
  
  public void setAuthenticationFidoCallbackHandler(String paramString) throws SQLException {
    setStringRuntimeProperty("authenticationFidoCallbackHandler", paramString);
  }
  
  public int getMaxAllowedPacket() throws SQLException {
    return getIntegerRuntimeProperty("maxAllowedPacket");
  }
  
  public void setMaxAllowedPacket(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("maxAllowedPacket", paramInt);
  }
  
  public String getEnabledTLSProtocols() throws SQLException {
    return getStringRuntimeProperty("enabledTLSProtocols");
  }
  
  public void setEnabledTLSProtocols(String paramString) throws SQLException {
    setStringRuntimeProperty("enabledTLSProtocols", paramString);
  }
  
  public int getConnectTimeout() throws SQLException {
    return getIntegerRuntimeProperty("connectTimeout");
  }
  
  public void setConnectTimeout(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("connectTimeout", paramInt);
  }
  
  public boolean getUseOldAliasMetadataBehavior() throws SQLException {
    return getBooleanRuntimeProperty("useOldAliasMetadataBehavior");
  }
  
  public void setUseOldAliasMetadataBehavior(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("useOldAliasMetadataBehavior", paramBoolean);
  }
  
  public boolean getTrackSessionState() throws SQLException {
    return getBooleanRuntimeProperty("trackSessionState");
  }
  
  public void setTrackSessionState(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("trackSessionState", paramBoolean);
  }
  
  public String getLocalSocketAddress() throws SQLException {
    return getStringRuntimeProperty("localSocketAddress");
  }
  
  public void setLocalSocketAddress(String paramString) throws SQLException {
    setStringRuntimeProperty("localSocketAddress", paramString);
  }
  
  public boolean getPreserveInstants() throws SQLException {
    return getBooleanRuntimeProperty("preserveInstants");
  }
  
  public void setPreserveInstants(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("preserveInstants", paramBoolean);
  }
  
  public boolean getDontTrackOpenResources() throws SQLException {
    return getBooleanRuntimeProperty("dontTrackOpenResources");
  }
  
  public void setDontTrackOpenResources(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("dontTrackOpenResources", paramBoolean);
  }
  
  public boolean getBlobsAreStrings() throws SQLException {
    return getBooleanRuntimeProperty("blobsAreStrings");
  }
  
  public void setBlobsAreStrings(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("blobsAreStrings", paramBoolean);
  }
  
  public boolean getPopulateInsertRowWithDefaultValues() throws SQLException {
    return getBooleanRuntimeProperty("populateInsertRowWithDefaultValues");
  }
  
  public void setPopulateInsertRowWithDefaultValues(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("populateInsertRowWithDefaultValues", paramBoolean);
  }
  
  public boolean getRequireSSL() throws SQLException {
    return getBooleanRuntimeProperty("requireSSL");
  }
  
  public void setRequireSSL(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("requireSSL", paramBoolean);
  }
  
  public String getOciConfigProfile() throws SQLException {
    return getStringRuntimeProperty("ociConfigProfile");
  }
  
  public void setOciConfigProfile(String paramString) throws SQLException {
    setStringRuntimeProperty("ociConfigProfile", paramString);
  }
  
  public boolean getTinyInt1isBit() throws SQLException {
    return getBooleanRuntimeProperty("tinyInt1isBit");
  }
  
  public void setTinyInt1isBit(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("tinyInt1isBit", paramBoolean);
  }
  
  public boolean getCacheCallableStmts() throws SQLException {
    return getBooleanRuntimeProperty("cacheCallableStmts");
  }
  
  public void setCacheCallableStmts(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("cacheCallableStmts", paramBoolean);
  }
  
  public String getEnabledSSLCipherSuites() throws SQLException {
    return getStringRuntimeProperty("enabledSSLCipherSuites");
  }
  
  public void setEnabledSSLCipherSuites(String paramString) throws SQLException {
    setStringRuntimeProperty("enabledSSLCipherSuites", paramString);
  }
  
  public boolean getAutoSlowLog() throws SQLException {
    return getBooleanRuntimeProperty("autoSlowLog");
  }
  
  public void setAutoSlowLog(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("autoSlowLog", paramBoolean);
  }
  
  public int getMaxReconnects() throws SQLException {
    return getIntegerRuntimeProperty("maxReconnects");
  }
  
  public void setMaxReconnects(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("maxReconnects", paramInt);
  }
  
  public long getSlowQueryThresholdNanos() throws SQLException {
    return getLongRuntimeProperty("slowQueryThresholdNanos");
  }
  
  public void setSlowQueryThresholdNanos(long paramLong) throws SQLException {
    setLongRuntimeProperty("slowQueryThresholdNanos", paramLong);
  }
  
  public boolean getEnablePacketDebug() throws SQLException {
    return getBooleanRuntimeProperty("enablePacketDebug");
  }
  
  public void setEnablePacketDebug(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("enablePacketDebug", paramBoolean);
  }
  
  public boolean getScrollTolerantForwardOnly() throws SQLException {
    return getBooleanRuntimeProperty("scrollTolerantForwardOnly");
  }
  
  public void setScrollTolerantForwardOnly(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("scrollTolerantForwardOnly", paramBoolean);
  }
  
  public boolean getUseHostsInPrivileges() throws SQLException {
    return getBooleanRuntimeProperty("useHostsInPrivileges");
  }
  
  public void setUseHostsInPrivileges(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("useHostsInPrivileges", paramBoolean);
  }
  
  public boolean getDumpQueriesOnException() throws SQLException {
    return getBooleanRuntimeProperty("dumpQueriesOnException");
  }
  
  public void setDumpQueriesOnException(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("dumpQueriesOnException", paramBoolean);
  }
  
  public String getConnectionLifecycleInterceptors() throws SQLException {
    return getStringRuntimeProperty("connectionLifecycleInterceptors");
  }
  
  public void setConnectionLifecycleInterceptors(String paramString) throws SQLException {
    setStringRuntimeProperty("connectionLifecycleInterceptors", paramString);
  }
  
  public boolean getLoadBalanceValidateConnectionOnSwapServer() throws SQLException {
    return getBooleanRuntimeProperty("loadBalanceValidateConnectionOnSwapServer");
  }
  
  public void setLoadBalanceValidateConnectionOnSwapServer(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("loadBalanceValidateConnectionOnSwapServer", paramBoolean);
  }
  
  public String getHaLoadBalanceStrategy() throws SQLException {
    return getStringRuntimeProperty("haLoadBalanceStrategy");
  }
  
  public void setHaLoadBalanceStrategy(String paramString) throws SQLException {
    setStringRuntimeProperty("haLoadBalanceStrategy", paramString);
  }
  
  public boolean getTreatUtilDateAsTimestamp() throws SQLException {
    return getBooleanRuntimeProperty("treatUtilDateAsTimestamp");
  }
  
  public void setTreatUtilDateAsTimestamp(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("treatUtilDateAsTimestamp", paramBoolean);
  }
  
  public boolean getEmulateUnsupportedPstmts() throws SQLException {
    return getBooleanRuntimeProperty("emulateUnsupportedPstmts");
  }
  
  public void setEmulateUnsupportedPstmts(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("emulateUnsupportedPstmts", paramBoolean);
  }
  
  public boolean getUseCompression() throws SQLException {
    return getBooleanRuntimeProperty("useCompression");
  }
  
  public void setUseCompression(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("useCompression", paramBoolean);
  }
  
  public boolean getUseSSL() throws SQLException {
    return getBooleanRuntimeProperty("useSSL");
  }
  
  public void setUseSSL(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("useSSL", paramBoolean);
  }
  
  public boolean getJdbcCompliantTruncation() throws SQLException {
    return getBooleanRuntimeProperty("jdbcCompliantTruncation");
  }
  
  public void setJdbcCompliantTruncation(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("jdbcCompliantTruncation", paramBoolean);
  }
  
  public boolean getClobberStreamingResults() throws SQLException {
    return getBooleanRuntimeProperty("clobberStreamingResults");
  }
  
  public void setClobberStreamingResults(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("clobberStreamingResults", paramBoolean);
  }
  
  public boolean getCreateDatabaseIfNotExist() throws SQLException {
    return getBooleanRuntimeProperty("createDatabaseIfNotExist");
  }
  
  public void setCreateDatabaseIfNotExist(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("createDatabaseIfNotExist", paramBoolean);
  }
  
  public int getMaxQuerySizeToLog() throws SQLException {
    return getIntegerRuntimeProperty("maxQuerySizeToLog");
  }
  
  public void setMaxQuerySizeToLog(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("maxQuerySizeToLog", paramInt);
  }
  
  public String getTrustCertificateKeyStoreUrl() throws SQLException {
    return getStringRuntimeProperty("trustCertificateKeyStoreUrl");
  }
  
  public void setTrustCertificateKeyStoreUrl(String paramString) throws SQLException {
    setStringRuntimeProperty("trustCertificateKeyStoreUrl", paramString);
  }
  
  public boolean getDnsSrv() throws SQLException {
    return getBooleanRuntimeProperty("dnsSrv");
  }
  
  public void setDnsSrv(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("dnsSrv", paramBoolean);
  }
  
  public boolean getEmulateLocators() throws SQLException {
    return getBooleanRuntimeProperty("emulateLocators");
  }
  
  public void setEmulateLocators(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("emulateLocators", paramBoolean);
  }
  
  public String getSessionVariables() throws SQLException {
    return getStringRuntimeProperty("sessionVariables");
  }
  
  public void setSessionVariables(String paramString) throws SQLException {
    setStringRuntimeProperty("sessionVariables", paramString);
  }
  
  public boolean getAutoReconnect() throws SQLException {
    return getBooleanRuntimeProperty("autoReconnect");
  }
  
  public void setAutoReconnect(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("autoReconnect", paramBoolean);
  }
  
  public boolean getStrictUpdates() throws SQLException {
    return getBooleanRuntimeProperty("strictUpdates");
  }
  
  public void setStrictUpdates(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("strictUpdates", paramBoolean);
  }
  
  public int getLoadBalanceHostRemovalGracePeriod() throws SQLException {
    return getIntegerRuntimeProperty("loadBalanceHostRemovalGracePeriod");
  }
  
  public void setLoadBalanceHostRemovalGracePeriod(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("loadBalanceHostRemovalGracePeriod", paramInt);
  }
  
  public String getTrustCertificateKeyStorePassword() throws SQLException {
    return getStringRuntimeProperty("trustCertificateKeyStorePassword");
  }
  
  public void setTrustCertificateKeyStorePassword(String paramString) throws SQLException {
    setStringRuntimeProperty("trustCertificateKeyStorePassword", paramString);
  }
  
  public boolean getUseServerPrepStmts() throws SQLException {
    return getBooleanRuntimeProperty("useServerPrepStmts");
  }
  
  public void setUseServerPrepStmts(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("useServerPrepStmts", paramBoolean);
  }
  
  public boolean getNoAccessToProcedureBodies() throws SQLException {
    return getBooleanRuntimeProperty("noAccessToProcedureBodies");
  }
  
  public void setNoAccessToProcedureBodies(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("noAccessToProcedureBodies", paramBoolean);
  }
  
  public boolean getCacheDefaultTimezone() throws SQLException {
    return getBooleanRuntimeProperty("cacheDefaultTimezone");
  }
  
  public void setCacheDefaultTimezone(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("cacheDefaultTimezone", paramBoolean);
  }
  
  public boolean getSendFractionalSecondsForTime() throws SQLException {
    return getBooleanRuntimeProperty("sendFractionalSecondsForTime");
  }
  
  public void setSendFractionalSecondsForTime(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("sendFractionalSecondsForTime", paramBoolean);
  }
  
  public boolean getProfileSQL() throws SQLException {
    return getBooleanRuntimeProperty("profileSQL");
  }
  
  public void setProfileSQL(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("profileSQL", paramBoolean);
  }
  
  public String getPasswordCharacterEncoding() throws SQLException {
    return getStringRuntimeProperty("passwordCharacterEncoding");
  }
  
  public void setPasswordCharacterEncoding(String paramString) throws SQLException {
    setStringRuntimeProperty("passwordCharacterEncoding", paramString);
  }
  
  public boolean getReadFromMasterWhenNoSlaves() throws SQLException {
    return getBooleanRuntimeProperty("readFromMasterWhenNoSlaves");
  }
  
  public void setReadFromMasterWhenNoSlaves(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("readFromMasterWhenNoSlaves", paramBoolean);
  }
  
  public boolean getAutoGenerateTestcaseScript() throws SQLException {
    return getBooleanRuntimeProperty("autoGenerateTestcaseScript");
  }
  
  public void setAutoGenerateTestcaseScript(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("autoGenerateTestcaseScript", paramBoolean);
  }
  
  public String getDisabledAuthenticationPlugins() throws SQLException {
    return getStringRuntimeProperty("disabledAuthenticationPlugins");
  }
  
  public void setDisabledAuthenticationPlugins(String paramString) throws SQLException {
    setStringRuntimeProperty("disabledAuthenticationPlugins", paramString);
  }
  
  public boolean getHaEnableJMX() throws SQLException {
    return getBooleanRuntimeProperty("haEnableJMX");
  }
  
  public void setHaEnableJMX(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("haEnableJMX", paramBoolean);
  }
  
  public String getExceptionInterceptors() throws SQLException {
    return getStringRuntimeProperty("exceptionInterceptors");
  }
  
  public void setExceptionInterceptors(String paramString) throws SQLException {
    setStringRuntimeProperty("exceptionInterceptors", paramString);
  }
  
  public boolean getMaintainTimeStats() throws SQLException {
    return getBooleanRuntimeProperty("maintainTimeStats");
  }
  
  public void setMaintainTimeStats(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("maintainTimeStats", paramBoolean);
  }
  
  public String getServerAffinityOrder() throws SQLException {
    return getStringRuntimeProperty("serverAffinityOrder");
  }
  
  public void setServerAffinityOrder(String paramString) throws SQLException {
    setStringRuntimeProperty("serverAffinityOrder", paramString);
  }
  
  public boolean getUseCursorFetch() throws SQLException {
    return getBooleanRuntimeProperty("useCursorFetch");
  }
  
  public void setUseCursorFetch(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("useCursorFetch", paramBoolean);
  }
  
  public boolean getOverrideSupportsIntegrityEnhancementFacility() throws SQLException {
    return getBooleanRuntimeProperty("overrideSupportsIntegrityEnhancementFacility");
  }
  
  public void setOverrideSupportsIntegrityEnhancementFacility(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("overrideSupportsIntegrityEnhancementFacility", paramBoolean);
  }
  
  public boolean getUseInformationSchema() throws SQLException {
    return getBooleanRuntimeProperty("useInformationSchema");
  }
  
  public void setUseInformationSchema(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("useInformationSchema", paramBoolean);
  }
  
  public int getInitialTimeout() throws SQLException {
    return getIntegerRuntimeProperty("initialTimeout");
  }
  
  public void setInitialTimeout(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("initialTimeout", paramInt);
  }
  
  public boolean getCacheResultSetMetadata() throws SQLException {
    return getBooleanRuntimeProperty("cacheResultSetMetadata");
  }
  
  public void setCacheResultSetMetadata(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("cacheResultSetMetadata", paramBoolean);
  }
  
  public int getSlowQueryThresholdMillis() throws SQLException {
    return getIntegerRuntimeProperty("slowQueryThresholdMillis");
  }
  
  public void setSlowQueryThresholdMillis(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("slowQueryThresholdMillis", paramInt);
  }
  
  public boolean getNoDatetimeStringSync() throws SQLException {
    return getBooleanRuntimeProperty("noDatetimeStringSync");
  }
  
  public void setNoDatetimeStringSync(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("noDatetimeStringSync", paramBoolean);
  }
  
  public boolean getAllowSlaveDownConnections() throws SQLException {
    return getBooleanRuntimeProperty("allowSlaveDownConnections");
  }
  
  public void setAllowSlaveDownConnections(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("allowSlaveDownConnections", paramBoolean);
  }
  
  public boolean getFunctionsNeverReturnBlobs() throws SQLException {
    return getBooleanRuntimeProperty("functionsNeverReturnBlobs");
  }
  
  public void setFunctionsNeverReturnBlobs(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("functionsNeverReturnBlobs", paramBoolean);
  }
  
  public String getResourceId() throws SQLException {
    return getStringRuntimeProperty("resourceId");
  }
  
  public void setResourceId(String paramString) throws SQLException {
    setStringRuntimeProperty("resourceId", paramString);
  }
  
  public boolean getParanoid() throws SQLException {
    return getBooleanRuntimeProperty("paranoid");
  }
  
  public void setParanoid(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("paranoid", paramBoolean);
  }
  
  public boolean getRollbackOnPooledClose() throws SQLException {
    return getBooleanRuntimeProperty("rollbackOnPooledClose");
  }
  
  public void setRollbackOnPooledClose(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("rollbackOnPooledClose", paramBoolean);
  }
  
  public int getDefaultFetchSize() throws SQLException {
    return getIntegerRuntimeProperty("defaultFetchSize");
  }
  
  public void setDefaultFetchSize(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("defaultFetchSize", paramInt);
  }
  
  public boolean getGenerateSimpleParameterMetadata() throws SQLException {
    return getBooleanRuntimeProperty("generateSimpleParameterMetadata");
  }
  
  public void setGenerateSimpleParameterMetadata(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("generateSimpleParameterMetadata", paramBoolean);
  }
  
  public boolean getHoldResultsOpenOverStatementClose() throws SQLException {
    return getBooleanRuntimeProperty("holdResultsOpenOverStatementClose");
  }
  
  public void setHoldResultsOpenOverStatementClose(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("holdResultsOpenOverStatementClose", paramBoolean);
  }
  
  public boolean getAutoClosePStmtStreams() throws SQLException {
    return getBooleanRuntimeProperty("autoClosePStmtStreams");
  }
  
  public void setAutoClosePStmtStreams(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("autoClosePStmtStreams", paramBoolean);
  }
  
  public int getLoadBalanceAutoCommitStatementThreshold() throws SQLException {
    return getIntegerRuntimeProperty("loadBalanceAutoCommitStatementThreshold");
  }
  
  public void setLoadBalanceAutoCommitStatementThreshold(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("loadBalanceAutoCommitStatementThreshold", paramInt);
  }
  
  public String getPassword3() throws SQLException {
    return getStringRuntimeProperty("password3");
  }
  
  public void setPassword3(String paramString) throws SQLException {
    setStringRuntimeProperty("password3", paramString);
  }
  
  public String getLoadBalanceSQLExceptionSubclassFailover() throws SQLException {
    return getStringRuntimeProperty("loadBalanceSQLExceptionSubclassFailover");
  }
  
  public void setLoadBalanceSQLExceptionSubclassFailover(String paramString) throws SQLException {
    setStringRuntimeProperty("loadBalanceSQLExceptionSubclassFailover", paramString);
  }
  
  public String getServerRSAPublicKeyFile() throws SQLException {
    return getStringRuntimeProperty("serverRSAPublicKeyFile");
  }
  
  public void setServerRSAPublicKeyFile(String paramString) throws SQLException {
    setStringRuntimeProperty("serverRSAPublicKeyFile", paramString);
  }
  
  public boolean getFailOverReadOnly() throws SQLException {
    return getBooleanRuntimeProperty("failOverReadOnly");
  }
  
  public void setFailOverReadOnly(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("failOverReadOnly", paramBoolean);
  }
  
  public boolean getVerifyServerCertificate() throws SQLException {
    return getBooleanRuntimeProperty("verifyServerCertificate");
  }
  
  public void setVerifyServerCertificate(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("verifyServerCertificate", paramBoolean);
  }
  
  public boolean getGetProceduresReturnsFunctions() throws SQLException {
    return getBooleanRuntimeProperty("getProceduresReturnsFunctions");
  }
  
  public void setGetProceduresReturnsFunctions(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("getProceduresReturnsFunctions", paramBoolean);
  }
  
  public int getLocatorFetchBufferSize() throws SQLException {
    return getIntegerRuntimeProperty("locatorFetchBufferSize");
  }
  
  public void setLocatorFetchBufferSize(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("locatorFetchBufferSize", paramInt);
  }
  
  public boolean getTcpNoDelay() throws SQLException {
    return getBooleanRuntimeProperty("tcpNoDelay");
  }
  
  public void setTcpNoDelay(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("tcpNoDelay", paramBoolean);
  }
  
  public String getClientInfoProvider() throws SQLException {
    return getStringRuntimeProperty("clientInfoProvider");
  }
  
  public void setClientInfoProvider(String paramString) throws SQLException {
    setStringRuntimeProperty("clientInfoProvider", paramString);
  }
  
  public String getProfilerEventHandler() throws SQLException {
    return getStringRuntimeProperty("profilerEventHandler");
  }
  
  public void setProfilerEventHandler(String paramString) throws SQLException {
    setStringRuntimeProperty("profilerEventHandler", paramString);
  }
  
  public boolean getSendFractionalSeconds() throws SQLException {
    return getBooleanRuntimeProperty("sendFractionalSeconds");
  }
  
  public void setSendFractionalSeconds(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("sendFractionalSeconds", paramBoolean);
  }
  
  public boolean getIncludeInnodbStatusInDeadlockExceptions() throws SQLException {
    return getBooleanRuntimeProperty("includeInnodbStatusInDeadlockExceptions");
  }
  
  public void setIncludeInnodbStatusInDeadlockExceptions(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("includeInnodbStatusInDeadlockExceptions", paramBoolean);
  }
  
  public boolean getAllowUrlInLocalInfile() throws SQLException {
    return getBooleanRuntimeProperty("allowUrlInLocalInfile");
  }
  
  public void setAllowUrlInLocalInfile(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("allowUrlInLocalInfile", paramBoolean);
  }
  
  public boolean getPadCharsWithSpace() throws SQLException {
    return getBooleanRuntimeProperty("padCharsWithSpace");
  }
  
  public void setPadCharsWithSpace(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("padCharsWithSpace", paramBoolean);
  }
  
  public boolean getLogXaCommands() throws SQLException {
    return getBooleanRuntimeProperty("logXaCommands");
  }
  
  public void setLogXaCommands(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("logXaCommands", paramBoolean);
  }
  
  public String getQueryInterceptors() throws SQLException {
    return getStringRuntimeProperty("queryInterceptors");
  }
  
  public void setQueryInterceptors(String paramString) throws SQLException {
    setStringRuntimeProperty("queryInterceptors", paramString);
  }
  
  public boolean getUseLocalTransactionState() throws SQLException {
    return getBooleanRuntimeProperty("useLocalTransactionState");
  }
  
  public void setUseLocalTransactionState(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("useLocalTransactionState", paramBoolean);
  }
  
  public int getSocketTimeout() throws SQLException {
    return getIntegerRuntimeProperty("socketTimeout");
  }
  
  public void setSocketTimeout(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("socketTimeout", paramInt);
  }
  
  public boolean getContinueBatchOnError() throws SQLException {
    return getBooleanRuntimeProperty("continueBatchOnError");
  }
  
  public void setContinueBatchOnError(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("continueBatchOnError", paramBoolean);
  }
  
  public boolean getPedantic() throws SQLException {
    return getBooleanRuntimeProperty("pedantic");
  }
  
  public void setPedantic(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("pedantic", paramBoolean);
  }
  
  public String getParseInfoCacheFactory() throws SQLException {
    return getStringRuntimeProperty("parseInfoCacheFactory");
  }
  
  public void setParseInfoCacheFactory(String paramString) throws SQLException {
    setStringRuntimeProperty("parseInfoCacheFactory", paramString);
  }
  
  public boolean getGatherPerfMetrics() throws SQLException {
    return getBooleanRuntimeProperty("gatherPerfMetrics");
  }
  
  public void setGatherPerfMetrics(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("gatherPerfMetrics", paramBoolean);
  }
  
  public int getSocksProxyPort() throws SQLException {
    return getIntegerRuntimeProperty("socksProxyPort");
  }
  
  public void setSocksProxyPort(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("socksProxyPort", paramInt);
  }
  
  public String getLoadBalanceConnectionGroup() throws SQLException {
    return getStringRuntimeProperty("loadBalanceConnectionGroup");
  }
  
  public void setLoadBalanceConnectionGroup(String paramString) throws SQLException {
    setStringRuntimeProperty("loadBalanceConnectionGroup", paramString);
  }
  
  public String getServerTimezone() throws SQLException {
    return getStringRuntimeProperty("serverTimezone");
  }
  
  public void setServerTimezone(String paramString) throws SQLException {
    setStringRuntimeProperty("serverTimezone", paramString);
  }
  
  public String getLoadBalanceSQLStateFailover() throws SQLException {
    return getStringRuntimeProperty("loadBalanceSQLStateFailover");
  }
  
  public void setLoadBalanceSQLStateFailover(String paramString) throws SQLException {
    setStringRuntimeProperty("loadBalanceSQLStateFailover", paramString);
  }
  
  public boolean getUseOnlyServerErrorMessages() throws SQLException {
    return getBooleanRuntimeProperty("useOnlyServerErrorMessages");
  }
  
  public void setUseOnlyServerErrorMessages(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("useOnlyServerErrorMessages", paramBoolean);
  }
  
  public int getRetriesAllDown() throws SQLException {
    return getIntegerRuntimeProperty("retriesAllDown");
  }
  
  public void setRetriesAllDown(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("retriesAllDown", paramInt);
  }
  
  public boolean getYearIsDateType() throws SQLException {
    return getBooleanRuntimeProperty("yearIsDateType");
  }
  
  public void setYearIsDateType(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("yearIsDateType", paramBoolean);
  }
  
  public String getLdapServerHostname() throws SQLException {
    return getStringRuntimeProperty("ldapServerHostname");
  }
  
  public void setLdapServerHostname(String paramString) throws SQLException {
    setStringRuntimeProperty("ldapServerHostname", paramString);
  }
  
  public int getPacketDebugBufferSize() throws SQLException {
    return getIntegerRuntimeProperty("packetDebugBufferSize");
  }
  
  public void setPacketDebugBufferSize(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("packetDebugBufferSize", paramInt);
  }
  
  public int getLargeRowSizeThreshold() throws SQLException {
    return getIntegerRuntimeProperty("largeRowSizeThreshold");
  }
  
  public void setLargeRowSizeThreshold(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("largeRowSizeThreshold", paramInt);
  }
  
  public boolean getEnableEscapeProcessing() throws SQLException {
    return getBooleanRuntimeProperty("enableEscapeProcessing");
  }
  
  public void setEnableEscapeProcessing(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("enableEscapeProcessing", paramBoolean);
  }
  
  public String getServerConfigCacheFactory() throws SQLException {
    return getStringRuntimeProperty("serverConfigCacheFactory");
  }
  
  public void setServerConfigCacheFactory(String paramString) throws SQLException {
    setStringRuntimeProperty("serverConfigCacheFactory", paramString);
  }
  
  public boolean getUseStreamLengthsInPrepStmts() throws SQLException {
    return getBooleanRuntimeProperty("useStreamLengthsInPrepStmts");
  }
  
  public void setUseStreamLengthsInPrepStmts(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("useStreamLengthsInPrepStmts", paramBoolean);
  }
  
  public int getTcpSndBuf() throws SQLException {
    return getIntegerRuntimeProperty("tcpSndBuf");
  }
  
  public void setTcpSndBuf(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("tcpSndBuf", paramInt);
  }
  
  public String getDefaultAuthenticationPlugin() throws SQLException {
    return getStringRuntimeProperty("defaultAuthenticationPlugin");
  }
  
  public void setDefaultAuthenticationPlugin(String paramString) throws SQLException {
    setStringRuntimeProperty("defaultAuthenticationPlugin", paramString);
  }
  
  public boolean getTransformedBitIsBoolean() throws SQLException {
    return getBooleanRuntimeProperty("transformedBitIsBoolean");
  }
  
  public void setTransformedBitIsBoolean(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("transformedBitIsBoolean", paramBoolean);
  }
  
  public String getLoadBalanceExceptionChecker() throws SQLException {
    return getStringRuntimeProperty("loadBalanceExceptionChecker");
  }
  
  public void setLoadBalanceExceptionChecker(String paramString) throws SQLException {
    setStringRuntimeProperty("loadBalanceExceptionChecker", paramString);
  }
  
  public int getResultSetSizeThreshold() throws SQLException {
    return getIntegerRuntimeProperty("resultSetSizeThreshold");
  }
  
  public void setResultSetSizeThreshold(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("resultSetSizeThreshold", paramInt);
  }
  
  public String getPassword1() throws SQLException {
    return getStringRuntimeProperty("password1");
  }
  
  public void setPassword1(String paramString) throws SQLException {
    setStringRuntimeProperty("password1", paramString);
  }
  
  public boolean getSocksProxyRemoteDns() throws SQLException {
    return getBooleanRuntimeProperty("socksProxyRemoteDns");
  }
  
  public void setSocksProxyRemoteDns(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("socksProxyRemoteDns", paramBoolean);
  }
  
  public String getDatabaseTerm() throws SQLException {
    return getEnumRuntimeProperty("databaseTerm");
  }
  
  public void setDatabaseTerm(String paramString) throws SQLException {
    setEnumRuntimeProperty("databaseTerm", paramString);
  }
  
  public boolean getAlwaysSendSetIsolation() throws SQLException {
    return getBooleanRuntimeProperty("alwaysSendSetIsolation");
  }
  
  public void setAlwaysSendSetIsolation(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("alwaysSendSetIsolation", paramBoolean);
  }
  
  public int getMetadataCacheSize() throws SQLException {
    return getIntegerRuntimeProperty("metadataCacheSize");
  }
  
  public void setMetadataCacheSize(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("metadataCacheSize", paramInt);
  }
  
  public String getPropertiesTransform() throws SQLException {
    return getStringRuntimeProperty("propertiesTransform");
  }
  
  public void setPropertiesTransform(String paramString) throws SQLException {
    setStringRuntimeProperty("propertiesTransform", paramString);
  }
  
  public boolean getIncludeThreadDumpInDeadlockExceptions() throws SQLException {
    return getBooleanRuntimeProperty("includeThreadDumpInDeadlockExceptions");
  }
  
  public void setIncludeThreadDumpInDeadlockExceptions(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("includeThreadDumpInDeadlockExceptions", paramBoolean);
  }
  
  public boolean getInteractiveClient() throws SQLException {
    return getBooleanRuntimeProperty("interactiveClient");
  }
  
  public void setInteractiveClient(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("interactiveClient", paramBoolean);
  }
  
  public boolean getAllowPublicKeyRetrieval() throws SQLException {
    return getBooleanRuntimeProperty("allowPublicKeyRetrieval");
  }
  
  public void setAllowPublicKeyRetrieval(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("allowPublicKeyRetrieval", paramBoolean);
  }
  
  public boolean getAutoDeserialize() throws SQLException {
    return getBooleanRuntimeProperty("autoDeserialize");
  }
  
  public void setAutoDeserialize(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("autoDeserialize", paramBoolean);
  }
  
  public boolean getRewriteBatchedStatements() throws SQLException {
    return getBooleanRuntimeProperty("rewriteBatchedStatements");
  }
  
  public void setRewriteBatchedStatements(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("rewriteBatchedStatements", paramBoolean);
  }
  
  public int getPrepStmtCacheSqlLimit() throws SQLException {
    return getIntegerRuntimeProperty("prepStmtCacheSqlLimit");
  }
  
  public void setPrepStmtCacheSqlLimit(int paramInt) throws SQLException {
    setIntegerRuntimeProperty("prepStmtCacheSqlLimit", paramInt);
  }
  
  public String getSocketFactory() throws SQLException {
    return getStringRuntimeProperty("socketFactory");
  }
  
  public void setSocketFactory(String paramString) throws SQLException {
    setStringRuntimeProperty("socketFactory", paramString);
  }
  
  public boolean getForceConnectionTimeZoneToSession() throws SQLException {
    return getBooleanRuntimeProperty("forceConnectionTimeZoneToSession");
  }
  
  public void setForceConnectionTimeZoneToSession(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("forceConnectionTimeZoneToSession", paramBoolean);
  }
  
  public boolean getExplainSlowQueries() throws SQLException {
    return getBooleanRuntimeProperty("explainSlowQueries");
  }
  
  public void setExplainSlowQueries(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("explainSlowQueries", paramBoolean);
  }
  
  public boolean getQueryTimeoutKillsConnection() throws SQLException {
    return getBooleanRuntimeProperty("queryTimeoutKillsConnection");
  }
  
  public void setQueryTimeoutKillsConnection(boolean paramBoolean) throws SQLException {
    setBooleanRuntimeProperty("queryTimeoutKillsConnection", paramBoolean);
  }
}
