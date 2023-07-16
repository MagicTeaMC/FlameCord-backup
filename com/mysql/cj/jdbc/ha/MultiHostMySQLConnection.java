package com.mysql.cj.jdbc.ha;

import com.mysql.cj.Messages;
import com.mysql.cj.ServerVersion;
import com.mysql.cj.Session;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.interceptors.QueryInterceptor;
import com.mysql.cj.jdbc.ClientInfoProvider;
import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.JdbcPreparedStatement;
import com.mysql.cj.jdbc.JdbcPropertySet;
import com.mysql.cj.jdbc.JdbcStatement;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.jdbc.result.CachedResultSetMetaData;
import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
import com.mysql.cj.protocol.ServerSessionStateController;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class MultiHostMySQLConnection implements JdbcConnection {
  protected MultiHostConnectionProxy thisAsProxy;
  
  public MultiHostMySQLConnection(MultiHostConnectionProxy proxy) {
    this.thisAsProxy = proxy;
  }
  
  public MultiHostConnectionProxy getThisAsProxy() {
    return this.thisAsProxy;
  }
  
  public JdbcConnection getActiveMySQLConnection() {
    synchronized (this.thisAsProxy) {
      return this.thisAsProxy.currentConnection;
    } 
  }
  
  public void abortInternal() throws SQLException {
    try {
      getActiveMySQLConnection().abortInternal();
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void changeUser(String userName, String newPassword) throws SQLException {
    try {
      getActiveMySQLConnection().changeUser(userName, newPassword);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void checkClosed() {
    getActiveMySQLConnection().checkClosed();
  }
  
  @Deprecated
  public void clearHasTriedMaster() {
    getActiveMySQLConnection().clearHasTriedMaster();
  }
  
  public void clearWarnings() throws SQLException {
    try {
      getActiveMySQLConnection().clearWarnings();
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    try {
      return getActiveMySQLConnection().clientPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    try {
      return getActiveMySQLConnection().clientPrepareStatement(sql, resultSetType, resultSetConcurrency);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
    try {
      return getActiveMySQLConnection().clientPrepareStatement(sql, autoGenKeyIndex);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
    try {
      return getActiveMySQLConnection().clientPrepareStatement(sql, autoGenKeyIndexes);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
    try {
      return getActiveMySQLConnection().clientPrepareStatement(sql, autoGenKeyColNames);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement clientPrepareStatement(String sql) throws SQLException {
    try {
      return getActiveMySQLConnection().clientPrepareStatement(sql);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void close() throws SQLException {
    try {
      getActiveMySQLConnection().close();
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void commit() throws SQLException {
    try {
      getActiveMySQLConnection().commit();
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void createNewIO(boolean isForReconnect) {
    getActiveMySQLConnection().createNewIO(isForReconnect);
  }
  
  public Statement createStatement() throws SQLException {
    try {
      return getActiveMySQLConnection().createStatement();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    try {
      return getActiveMySQLConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
    try {
      return getActiveMySQLConnection().createStatement(resultSetType, resultSetConcurrency);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getActiveStatementCount() {
    return getActiveMySQLConnection().getActiveStatementCount();
  }
  
  public boolean getAutoCommit() throws SQLException {
    try {
      return getActiveMySQLConnection().getAutoCommit();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getAutoIncrementIncrement() {
    return getActiveMySQLConnection().getAutoIncrementIncrement();
  }
  
  public CachedResultSetMetaData getCachedMetaData(String sql) {
    return getActiveMySQLConnection().getCachedMetaData(sql);
  }
  
  public String getCatalog() throws SQLException {
    try {
      return getActiveMySQLConnection().getCatalog();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getCharacterSetMetadata() {
    return getActiveMySQLConnection().getCharacterSetMetadata();
  }
  
  public ExceptionInterceptor getExceptionInterceptor() {
    return getActiveMySQLConnection().getExceptionInterceptor();
  }
  
  public int getHoldability() throws SQLException {
    try {
      return getActiveMySQLConnection().getHoldability();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getHost() {
    return getActiveMySQLConnection().getHost();
  }
  
  public long getId() {
    return getActiveMySQLConnection().getId();
  }
  
  public long getIdleFor() {
    return getActiveMySQLConnection().getIdleFor();
  }
  
  public JdbcConnection getMultiHostSafeProxy() {
    return getThisAsProxy().getProxy();
  }
  
  public JdbcConnection getMultiHostParentProxy() {
    return getThisAsProxy().getParentProxy();
  }
  
  public DatabaseMetaData getMetaData() throws SQLException {
    try {
      return getActiveMySQLConnection().getMetaData();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Statement getMetadataSafeStatement() throws SQLException {
    try {
      return getActiveMySQLConnection().getMetadataSafeStatement();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Properties getProperties() {
    return getActiveMySQLConnection().getProperties();
  }
  
  public ServerVersion getServerVersion() {
    return getActiveMySQLConnection().getServerVersion();
  }
  
  public Session getSession() {
    return getActiveMySQLConnection().getSession();
  }
  
  public String getStatementComment() {
    return getActiveMySQLConnection().getStatementComment();
  }
  
  public List<QueryInterceptor> getQueryInterceptorsInstances() {
    return getActiveMySQLConnection().getQueryInterceptorsInstances();
  }
  
  public int getTransactionIsolation() throws SQLException {
    try {
      return getActiveMySQLConnection().getTransactionIsolation();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Map<String, Class<?>> getTypeMap() throws SQLException {
    try {
      return getActiveMySQLConnection().getTypeMap();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getURL() {
    return getActiveMySQLConnection().getURL();
  }
  
  public String getUser() {
    return getActiveMySQLConnection().getUser();
  }
  
  public SQLWarning getWarnings() throws SQLException {
    try {
      return getActiveMySQLConnection().getWarnings();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean hasSameProperties(JdbcConnection c) {
    return getActiveMySQLConnection().hasSameProperties(c);
  }
  
  @Deprecated
  public boolean hasTriedMaster() {
    return getActiveMySQLConnection().hasTriedMaster();
  }
  
  public void initializeResultsMetadataFromCache(String sql, CachedResultSetMetaData cachedMetaData, ResultSetInternalMethods resultSet) throws SQLException {
    try {
      getActiveMySQLConnection().initializeResultsMetadataFromCache(sql, cachedMetaData, resultSet);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void initializeSafeQueryInterceptors() throws SQLException {
    try {
      getActiveMySQLConnection().initializeSafeQueryInterceptors();
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isInGlobalTx() {
    return getActiveMySQLConnection().isInGlobalTx();
  }
  
  public boolean isSourceConnection() {
    return getThisAsProxy().isSourceConnection();
  }
  
  public boolean isReadOnly() throws SQLException {
    try {
      return getActiveMySQLConnection().isReadOnly();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isReadOnly(boolean useSessionStatus) throws SQLException {
    try {
      return getActiveMySQLConnection().isReadOnly(useSessionStatus);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isSameResource(JdbcConnection otherConnection) {
    return getActiveMySQLConnection().isSameResource(otherConnection);
  }
  
  public boolean lowerCaseTableNames() {
    return getActiveMySQLConnection().lowerCaseTableNames();
  }
  
  public String nativeSQL(String sql) throws SQLException {
    try {
      return getActiveMySQLConnection().nativeSQL(sql);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void ping() throws SQLException {
    try {
      getActiveMySQLConnection().ping();
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void pingInternal(boolean checkForClosedConnection, int timeoutMillis) throws SQLException {
    try {
      getActiveMySQLConnection().pingInternal(checkForClosedConnection, timeoutMillis);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    try {
      return getActiveMySQLConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    try {
      return getActiveMySQLConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public CallableStatement prepareCall(String sql) throws SQLException {
    try {
      return getActiveMySQLConnection().prepareCall(sql);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    try {
      return getActiveMySQLConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    try {
      return getActiveMySQLConnection().prepareStatement(sql, resultSetType, resultSetConcurrency);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement prepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
    try {
      return getActiveMySQLConnection().prepareStatement(sql, autoGenKeyIndex);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement prepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
    try {
      return getActiveMySQLConnection().prepareStatement(sql, autoGenKeyIndexes);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement prepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
    try {
      return getActiveMySQLConnection().prepareStatement(sql, autoGenKeyColNames);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement prepareStatement(String sql) throws SQLException {
    try {
      return getActiveMySQLConnection().prepareStatement(sql);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void realClose(boolean calledExplicitly, boolean issueRollback, boolean skipLocalTeardown, Throwable reason) throws SQLException {
    try {
      getActiveMySQLConnection().realClose(calledExplicitly, issueRollback, skipLocalTeardown, reason);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void recachePreparedStatement(JdbcPreparedStatement pstmt) throws SQLException {
    try {
      getActiveMySQLConnection().recachePreparedStatement(pstmt);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void decachePreparedStatement(JdbcPreparedStatement pstmt) throws SQLException {
    try {
      getActiveMySQLConnection().decachePreparedStatement(pstmt);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void registerStatement(JdbcStatement stmt) {
    getActiveMySQLConnection().registerStatement(stmt);
  }
  
  public void releaseSavepoint(Savepoint arg0) throws SQLException {
    try {
      getActiveMySQLConnection().releaseSavepoint(arg0);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void resetServerState() throws SQLException {
    try {
      getActiveMySQLConnection().resetServerState();
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void rollback() throws SQLException {
    try {
      getActiveMySQLConnection().rollback();
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void rollback(Savepoint savepoint) throws SQLException {
    try {
      getActiveMySQLConnection().rollback(savepoint);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    try {
      return getActiveMySQLConnection().serverPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    try {
      return getActiveMySQLConnection().serverPrepareStatement(sql, resultSetType, resultSetConcurrency);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
    try {
      return getActiveMySQLConnection().serverPrepareStatement(sql, autoGenKeyIndex);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
    try {
      return getActiveMySQLConnection().serverPrepareStatement(sql, autoGenKeyIndexes);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
    try {
      return getActiveMySQLConnection().serverPrepareStatement(sql, autoGenKeyColNames);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement serverPrepareStatement(String sql) throws SQLException {
    try {
      return getActiveMySQLConnection().serverPrepareStatement(sql);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setAutoCommit(boolean autoCommitFlag) throws SQLException {
    try {
      getActiveMySQLConnection().setAutoCommit(autoCommitFlag);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setDatabase(String dbName) throws SQLException {
    try {
      getActiveMySQLConnection().setDatabase(dbName);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getDatabase() throws SQLException {
    try {
      return getActiveMySQLConnection().getDatabase();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setCatalog(String catalog) throws SQLException {
    try {
      getActiveMySQLConnection().setCatalog(catalog);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setFailedOver(boolean flag) {
    getActiveMySQLConnection().setFailedOver(flag);
  }
  
  public void setHoldability(int arg0) throws SQLException {
    try {
      getActiveMySQLConnection().setHoldability(arg0);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setInGlobalTx(boolean flag) {
    getActiveMySQLConnection().setInGlobalTx(flag);
  }
  
  public void setProxy(JdbcConnection proxy) {
    getThisAsProxy().setProxy(proxy);
  }
  
  public void setReadOnly(boolean readOnlyFlag) throws SQLException {
    try {
      getActiveMySQLConnection().setReadOnly(readOnlyFlag);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setReadOnlyInternal(boolean readOnlyFlag) throws SQLException {
    try {
      getActiveMySQLConnection().setReadOnlyInternal(readOnlyFlag);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Savepoint setSavepoint() throws SQLException {
    try {
      return getActiveMySQLConnection().setSavepoint();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Savepoint setSavepoint(String name) throws SQLException {
    try {
      return getActiveMySQLConnection().setSavepoint(name);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setStatementComment(String comment) {
    getActiveMySQLConnection().setStatementComment(comment);
  }
  
  public void setTransactionIsolation(int level) throws SQLException {
    try {
      getActiveMySQLConnection().setTransactionIsolation(level);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void shutdownServer() throws SQLException {
    try {
      getActiveMySQLConnection().shutdownServer();
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean storesLowerCaseTableName() {
    return getActiveMySQLConnection().storesLowerCaseTableName();
  }
  
  public void throwConnectionClosedException() throws SQLException {
    try {
      getActiveMySQLConnection().throwConnectionClosedException();
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void transactionBegun() {
    getActiveMySQLConnection().transactionBegun();
  }
  
  public void transactionCompleted() {
    getActiveMySQLConnection().transactionCompleted();
  }
  
  public void unregisterStatement(JdbcStatement stmt) {
    getActiveMySQLConnection().unregisterStatement(stmt);
  }
  
  public void unSafeQueryInterceptors() throws SQLException {
    try {
      getActiveMySQLConnection().unSafeQueryInterceptors();
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isClosed() throws SQLException {
    try {
      return (getThisAsProxy()).isClosed;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isProxySet() {
    return getActiveMySQLConnection().isProxySet();
  }
  
  public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
    try {
      getActiveMySQLConnection().setTypeMap(map);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isServerLocal() throws SQLException {
    try {
      return getActiveMySQLConnection().isServerLocal();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setSchema(String schema) throws SQLException {
    try {
      getActiveMySQLConnection().setSchema(schema);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getSchema() throws SQLException {
    try {
      return getActiveMySQLConnection().getSchema();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void abort(Executor executor) throws SQLException {
    try {
      getActiveMySQLConnection().abort(executor);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
    try {
      getActiveMySQLConnection().setNetworkTimeout(executor, milliseconds);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getNetworkTimeout() throws SQLException {
    try {
      return getActiveMySQLConnection().getNetworkTimeout();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Object getConnectionMutex() {
    return getActiveMySQLConnection().getConnectionMutex();
  }
  
  public int getSessionMaxRows() {
    return getActiveMySQLConnection().getSessionMaxRows();
  }
  
  public void setSessionMaxRows(int max) throws SQLException {
    try {
      getActiveMySQLConnection().setSessionMaxRows(max);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public SQLXML createSQLXML() throws SQLException {
    try {
      return getActiveMySQLConnection().createSQLXML();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
    try {
      return getActiveMySQLConnection().createArrayOf(typeName, elements);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
    try {
      return getActiveMySQLConnection().createStruct(typeName, attributes);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Properties getClientInfo() throws SQLException {
    try {
      return getActiveMySQLConnection().getClientInfo();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getClientInfo(String name) throws SQLException {
    try {
      return getActiveMySQLConnection().getClientInfo(name);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isValid(int timeout) throws SQLException {
    try {
      return getActiveMySQLConnection().isValid(timeout);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setClientInfo(Properties properties) throws SQLClientInfoException {
    getActiveMySQLConnection().setClientInfo(properties);
  }
  
  public void setClientInfo(String name, String value) throws SQLClientInfoException {
    getActiveMySQLConnection().setClientInfo(name, value);
  }
  
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    try {
      return iface.isInstance(this);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public <T> T unwrap(Class<T> iface) throws SQLException {
    try {
      try {
        return iface.cast(this);
      } catch (ClassCastException cce) {
        throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[] { iface.toString() }), "S1009", 
            getExceptionInterceptor());
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Blob createBlob() throws SQLException {
    try {
      return getActiveMySQLConnection().createBlob();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Clob createClob() throws SQLException {
    try {
      return getActiveMySQLConnection().createClob();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public NClob createNClob() throws SQLException {
    try {
      return getActiveMySQLConnection().createNClob();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ClientInfoProvider getClientInfoProviderImpl() throws SQLException {
    try {
      synchronized (getThisAsProxy()) {
        return getActiveMySQLConnection().getClientInfoProviderImpl();
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public JdbcPropertySet getPropertySet() {
    return getActiveMySQLConnection().getPropertySet();
  }
  
  public String getHostPortPair() {
    return getActiveMySQLConnection().getHostPortPair();
  }
  
  public void normalClose() {
    getActiveMySQLConnection().normalClose();
  }
  
  public void cleanup(Throwable whyCleanedUp) {
    getActiveMySQLConnection().cleanup(whyCleanedUp);
  }
  
  public ServerSessionStateController getServerSessionStateController() {
    return getActiveMySQLConnection().getServerSessionStateController();
  }
}
