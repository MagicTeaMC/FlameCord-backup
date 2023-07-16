package com.mysql.cj.jdbc;

import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

public class MysqlConnectionPoolDataSource extends MysqlDataSource implements ConnectionPoolDataSource {
  static final long serialVersionUID = -7767325445592304961L;
  
  public synchronized PooledConnection getPooledConnection() throws SQLException {
    try {
      Connection connection = getConnection();
      MysqlPooledConnection mysqlPooledConnection = MysqlPooledConnection.getInstance((JdbcConnection)connection);
      return mysqlPooledConnection;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  public synchronized PooledConnection getPooledConnection(String u, String p) throws SQLException {
    try {
      Connection connection = getConnection(u, p);
      MysqlPooledConnection mysqlPooledConnection = MysqlPooledConnection.getInstance((JdbcConnection)connection);
      return mysqlPooledConnection;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
}
