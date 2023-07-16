package com.mysql.cj.jdbc.interceptors;

import com.mysql.cj.MysqlConnection;
import com.mysql.cj.log.Log;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Properties;

public interface ConnectionLifecycleInterceptor {
  ConnectionLifecycleInterceptor init(MysqlConnection paramMysqlConnection, Properties paramProperties, Log paramLog);
  
  void destroy();
  
  void close() throws SQLException;
  
  boolean commit() throws SQLException;
  
  boolean rollback() throws SQLException;
  
  boolean rollback(Savepoint paramSavepoint) throws SQLException;
  
  boolean setAutoCommit(boolean paramBoolean) throws SQLException;
  
  boolean setDatabase(String paramString) throws SQLException;
  
  boolean transactionBegun();
  
  boolean transactionCompleted();
}
