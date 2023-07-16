package com.mysql.cj.jdbc;

import com.mysql.cj.Messages;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.HashMap;

public class StatementWrapper extends WrapperBase implements Statement {
  protected Statement wrappedStmt;
  
  protected ConnectionWrapper wrappedConn;
  
  protected static StatementWrapper getInstance(ConnectionWrapper c, MysqlPooledConnection conn, Statement toWrap) throws SQLException {
    return new StatementWrapper(c, conn, toWrap);
  }
  
  public StatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, Statement toWrap) {
    super(conn);
    this.wrappedStmt = toWrap;
    this.wrappedConn = c;
  }
  
  public Connection getConnection() throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedConn; 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return null;
    } 
  }
  
  public void setCursorName(String name) throws SQLException {
    try {
      if (this.wrappedStmt != null) {
        this.wrappedStmt.setCursorName(name);
      } else {
        throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
      } 
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
    } 
  }
  
  public void setEscapeProcessing(boolean enable) throws SQLException {
    try {
      if (this.wrappedStmt != null) {
        this.wrappedStmt.setEscapeProcessing(enable);
      } else {
        throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
      } 
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
    } 
  }
  
  public void setFetchDirection(int direction) throws SQLException {
    try {
      if (this.wrappedStmt != null) {
        this.wrappedStmt.setFetchDirection(direction);
      } else {
        throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
      } 
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
    } 
  }
  
  public int getFetchDirection() throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.getFetchDirection(); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return 1000;
    } 
  }
  
  public void setFetchSize(int rows) throws SQLException {
    try {
      if (this.wrappedStmt != null) {
        this.wrappedStmt.setFetchSize(rows);
      } else {
        throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
      } 
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
    } 
  }
  
  public int getFetchSize() throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.getFetchSize(); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return 0;
    } 
  }
  
  public ResultSet getGeneratedKeys() throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.getGeneratedKeys(); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return null;
    } 
  }
  
  public void setMaxFieldSize(int max) throws SQLException {
    try {
      if (this.wrappedStmt != null) {
        this.wrappedStmt.setMaxFieldSize(max);
      } else {
        throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
      } 
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
    } 
  }
  
  public int getMaxFieldSize() throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.getMaxFieldSize(); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return 0;
    } 
  }
  
  public void setMaxRows(int max) throws SQLException {
    try {
      if (this.wrappedStmt != null) {
        this.wrappedStmt.setMaxRows(max);
      } else {
        throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
      } 
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
    } 
  }
  
  public int getMaxRows() throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.getMaxRows(); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return 0;
    } 
  }
  
  public boolean getMoreResults() throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.getMoreResults(); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return false;
    } 
  }
  
  public boolean getMoreResults(int current) throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.getMoreResults(current); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return false;
    } 
  }
  
  public void setQueryTimeout(int seconds) throws SQLException {
    try {
      if (this.wrappedStmt != null) {
        this.wrappedStmt.setQueryTimeout(seconds);
      } else {
        throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
      } 
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
    } 
  }
  
  public int getQueryTimeout() throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.getQueryTimeout(); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return 0;
    } 
  }
  
  public ResultSet getResultSet() throws SQLException {
    try {
      if (this.wrappedStmt != null) {
        ResultSet rs = this.wrappedStmt.getResultSet();
        if (rs != null)
          ((ResultSetInternalMethods)rs).setWrapperStatement(this); 
        return rs;
      } 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return null;
    } 
  }
  
  public int getResultSetConcurrency() throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.getResultSetConcurrency(); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return 0;
    } 
  }
  
  public int getResultSetHoldability() throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.getResultSetHoldability(); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return 1;
    } 
  }
  
  public int getResultSetType() throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.getResultSetType(); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return 1003;
    } 
  }
  
  public int getUpdateCount() throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.getUpdateCount(); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return -1;
    } 
  }
  
  public SQLWarning getWarnings() throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.getWarnings(); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return null;
    } 
  }
  
  public void addBatch(String sql) throws SQLException {
    try {
      if (this.wrappedStmt != null) {
        this.wrappedStmt.addBatch(sql);
      } else {
        throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
      } 
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
    } 
  }
  
  public void cancel() throws SQLException {
    try {
      if (this.wrappedStmt != null) {
        this.wrappedStmt.cancel();
      } else {
        throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
      } 
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
    } 
  }
  
  public void clearBatch() throws SQLException {
    try {
      if (this.wrappedStmt != null) {
        this.wrappedStmt.clearBatch();
      } else {
        throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
      } 
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
    } 
  }
  
  public void clearWarnings() throws SQLException {
    try {
      if (this.wrappedStmt != null) {
        this.wrappedStmt.clearWarnings();
      } else {
        throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
      } 
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
    } 
  }
  
  public void close() throws SQLException {
    try {
      if (this.wrappedStmt != null)
        this.wrappedStmt.close(); 
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
    } finally {
      this.wrappedStmt = null;
      this.pooledConnection = null;
      this.unwrappedInterfaces = null;
    } 
  }
  
  public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.execute(sql, autoGeneratedKeys); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return false;
    } 
  }
  
  public boolean execute(String sql, int[] columnIndexes) throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.execute(sql, columnIndexes); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return false;
    } 
  }
  
  public boolean execute(String sql, String[] columnNames) throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.execute(sql, columnNames); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return false;
    } 
  }
  
  public boolean execute(String sql) throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.execute(sql); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return false;
    } 
  }
  
  public int[] executeBatch() throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.executeBatch(); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return null;
    } 
  }
  
  public ResultSet executeQuery(String sql) throws SQLException {
    ResultSet rs = null;
    try {
      if (this.wrappedStmt == null)
        throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor); 
      rs = this.wrappedStmt.executeQuery(sql);
      ((ResultSetInternalMethods)rs).setWrapperStatement(this);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
    } 
    return rs;
  }
  
  public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.executeUpdate(sql, autoGeneratedKeys); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return -1;
    } 
  }
  
  public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.executeUpdate(sql, columnIndexes); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return -1;
    } 
  }
  
  public int executeUpdate(String sql, String[] columnNames) throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.executeUpdate(sql, columnNames); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return -1;
    } 
  }
  
  public int executeUpdate(String sql) throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.executeUpdate(sql); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return -1;
    } 
  }
  
  public void enableStreamingResults() throws SQLException {
    try {
      if (this.wrappedStmt != null) {
        ((JdbcStatement)this.wrappedStmt).enableStreamingResults();
      } else {
        throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
      } 
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
    } 
  }
  
  public synchronized <T> T unwrap(Class<T> iface) throws SQLException {
    try {
      if ("java.sql.Statement".equals(iface.getName()) || "java.sql.Wrapper.class".equals(iface.getName()))
        return iface.cast(this); 
      if (this.unwrappedInterfaces == null)
        this.unwrappedInterfaces = new HashMap<>(); 
      Object cachedUnwrapped = this.unwrappedInterfaces.get(iface);
      if (cachedUnwrapped == null) {
        cachedUnwrapped = Proxy.newProxyInstance(this.wrappedStmt.getClass().getClassLoader(), new Class[] { iface }, new WrapperBase.ConnectionErrorFiringInvocationHandler(this, this.wrappedStmt));
        this.unwrappedInterfaces.put(iface, cachedUnwrapped);
      } 
      return iface.cast(cachedUnwrapped);
    } catch (ClassCastException cce) {
      throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[] { iface.toString() }), "S1009", this.exceptionInterceptor);
    } 
  }
  
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    boolean isInstance = iface.isInstance(this);
    if (isInstance)
      return true; 
    String interfaceClassName = iface.getName();
    return (interfaceClassName.equals("com.mysql.cj.jdbc.Statement") || interfaceClassName.equals("java.sql.Statement") || interfaceClassName
      .equals("java.sql.Wrapper"));
  }
  
  public boolean isClosed() throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.isClosed(); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return false;
    } 
  }
  
  public void setPoolable(boolean poolable) throws SQLException {
    try {
      if (this.wrappedStmt != null) {
        this.wrappedStmt.setPoolable(poolable);
      } else {
        throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
      } 
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
    } 
  }
  
  public boolean isPoolable() throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return this.wrappedStmt.isPoolable(); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return false;
    } 
  }
  
  public void closeOnCompletion() throws SQLException {
    if (this.wrappedStmt == null)
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor); 
  }
  
  public boolean isCloseOnCompletion() throws SQLException {
    if (this.wrappedStmt == null)
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor); 
    return false;
  }
  
  public long[] executeLargeBatch() throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return ((StatementImpl)this.wrappedStmt).executeLargeBatch(); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return null;
    } 
  }
  
  public long executeLargeUpdate(String sql) throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return ((StatementImpl)this.wrappedStmt).executeLargeUpdate(sql); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return -1L;
    } 
  }
  
  public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return ((StatementImpl)this.wrappedStmt).executeLargeUpdate(sql, autoGeneratedKeys); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return -1L;
    } 
  }
  
  public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return ((StatementImpl)this.wrappedStmt).executeLargeUpdate(sql, columnIndexes); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return -1L;
    } 
  }
  
  public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return ((StatementImpl)this.wrappedStmt).executeLargeUpdate(sql, columnNames); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return -1L;
    } 
  }
  
  public long getLargeMaxRows() throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return ((StatementImpl)this.wrappedStmt).getLargeMaxRows(); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return 0L;
    } 
  }
  
  public long getLargeUpdateCount() throws SQLException {
    try {
      if (this.wrappedStmt != null)
        return ((StatementImpl)this.wrappedStmt).getLargeUpdateCount(); 
      throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
      return -1L;
    } 
  }
  
  public void setLargeMaxRows(long max) throws SQLException {
    try {
      if (this.wrappedStmt != null) {
        ((StatementImpl)this.wrappedStmt).setLargeMaxRows(max);
      } else {
        throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
      } 
    } catch (SQLException sqlEx) {
      checkAndFireConnectionError(sqlEx);
    } 
  }
}
