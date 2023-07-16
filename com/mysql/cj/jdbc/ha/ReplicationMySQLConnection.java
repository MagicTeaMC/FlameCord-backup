package com.mysql.cj.jdbc.ha;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executor;

public class ReplicationMySQLConnection extends MultiHostMySQLConnection implements ReplicationConnection {
  public ReplicationMySQLConnection(MultiHostConnectionProxy proxy) {
    super(proxy);
  }
  
  public ReplicationConnectionProxy getThisAsProxy() {
    return (ReplicationConnectionProxy)super.getThisAsProxy();
  }
  
  public JdbcConnection getActiveMySQLConnection() {
    return getCurrentConnection();
  }
  
  public synchronized JdbcConnection getCurrentConnection() {
    return getThisAsProxy().getCurrentConnection();
  }
  
  public long getConnectionGroupId() {
    return getThisAsProxy().getConnectionGroupId();
  }
  
  public synchronized JdbcConnection getSourceConnection() {
    return getThisAsProxy().getSourceConnection();
  }
  
  private JdbcConnection getValidatedSourceConnection() {
    JdbcConnection conn = (getThisAsProxy()).sourceConnection;
    try {
      return (conn == null || conn.isClosed()) ? null : conn;
    } catch (SQLException e) {
      return null;
    } 
  }
  
  public void promoteReplicaToSource(String host) throws SQLException {
    try {
      getThisAsProxy().promoteReplicaToSource(host);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void removeSourceHost(String host) throws SQLException {
    try {
      getThisAsProxy().removeSourceHost(host);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void removeSourceHost(String host, boolean waitUntilNotInUse) throws SQLException {
    try {
      getThisAsProxy().removeSourceHost(host, waitUntilNotInUse);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isHostSource(String host) {
    return getThisAsProxy().isHostSource(host);
  }
  
  public synchronized JdbcConnection getReplicaConnection() {
    return getThisAsProxy().getReplicasConnection();
  }
  
  private JdbcConnection getValidatedReplicasConnection() {
    JdbcConnection conn = (getThisAsProxy()).replicasConnection;
    try {
      return (conn == null || conn.isClosed()) ? null : conn;
    } catch (SQLException e) {
      return null;
    } 
  }
  
  public void addReplicaHost(String host) throws SQLException {
    try {
      getThisAsProxy().addReplicaHost(host);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void removeReplica(String host) throws SQLException {
    try {
      getThisAsProxy().removeReplica(host);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void removeReplica(String host, boolean closeGently) throws SQLException {
    try {
      getThisAsProxy().removeReplica(host, closeGently);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isHostReplica(String host) {
    return getThisAsProxy().isHostReplica(host);
  }
  
  public void setReadOnly(boolean readOnlyFlag) throws SQLException {
    try {
      getThisAsProxy().setReadOnly(readOnlyFlag);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isReadOnly() throws SQLException {
    try {
      return getThisAsProxy().isReadOnly();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public synchronized void ping() throws SQLException {
    try {
      try {
        JdbcConnection conn;
        if ((conn = getValidatedSourceConnection()) != null)
          conn.ping(); 
      } catch (SQLException e) {
        if (isSourceConnection())
          throw e; 
      } 
      try {
        JdbcConnection conn;
        if ((conn = getValidatedReplicasConnection()) != null)
          conn.ping(); 
      } catch (SQLException e) {
        if (!isSourceConnection())
          throw e; 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public synchronized void changeUser(String userName, String newPassword) throws SQLException {
    try {
      JdbcConnection conn;
      if ((conn = getValidatedSourceConnection()) != null)
        conn.changeUser(userName, newPassword); 
      if ((conn = getValidatedReplicasConnection()) != null)
        conn.changeUser(userName, newPassword); 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public synchronized void setStatementComment(String comment) {
    JdbcConnection conn;
    if ((conn = getValidatedSourceConnection()) != null)
      conn.setStatementComment(comment); 
    if ((conn = getValidatedReplicasConnection()) != null)
      conn.setStatementComment(comment); 
  }
  
  public boolean hasSameProperties(JdbcConnection c) {
    JdbcConnection connM = getValidatedSourceConnection();
    JdbcConnection connS = getValidatedReplicasConnection();
    if (connM == null && connS == null)
      return false; 
    return ((connM == null || connM.hasSameProperties(c)) && (connS == null || connS.hasSameProperties(c)));
  }
  
  public Properties getProperties() {
    Properties props = new Properties();
    JdbcConnection conn;
    if ((conn = getValidatedSourceConnection()) != null)
      props.putAll(conn.getProperties()); 
    if ((conn = getValidatedReplicasConnection()) != null)
      props.putAll(conn.getProperties()); 
    return props;
  }
  
  public void abort(Executor executor) throws SQLException {
    try {
      getThisAsProxy().doAbort(executor);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void abortInternal() throws SQLException {
    try {
      getThisAsProxy().doAbortInternal();
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setProxy(JdbcConnection proxy) {
    getThisAsProxy().setProxy(proxy);
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
  
  @Deprecated
  public synchronized void clearHasTriedMaster() {
    (getThisAsProxy()).sourceConnection.clearHasTriedMaster();
    (getThisAsProxy()).replicasConnection.clearHasTriedMaster();
  }
}
