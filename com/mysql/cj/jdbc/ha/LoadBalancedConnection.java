package com.mysql.cj.jdbc.ha;

import com.mysql.cj.jdbc.JdbcConnection;
import java.sql.SQLException;

public interface LoadBalancedConnection extends JdbcConnection {
  boolean addHost(String paramString) throws SQLException;
  
  void removeHost(String paramString) throws SQLException;
  
  void removeHostWhenNotInUse(String paramString) throws SQLException;
  
  void ping(boolean paramBoolean) throws SQLException;
}
