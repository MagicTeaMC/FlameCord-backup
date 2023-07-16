package com.mysql.cj.jdbc;

import java.sql.Connection;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.util.Properties;

public interface ClientInfoProvider {
  void initialize(Connection paramConnection, Properties paramProperties) throws SQLException;
  
  void destroy() throws SQLException;
  
  Properties getClientInfo(Connection paramConnection) throws SQLException;
  
  String getClientInfo(Connection paramConnection, String paramString) throws SQLException;
  
  void setClientInfo(Connection paramConnection, Properties paramProperties) throws SQLClientInfoException;
  
  void setClientInfo(Connection paramConnection, String paramString1, String paramString2) throws SQLClientInfoException;
}
