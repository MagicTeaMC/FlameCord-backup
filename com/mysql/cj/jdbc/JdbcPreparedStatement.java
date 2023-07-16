package com.mysql.cj.jdbc;

import com.mysql.cj.MysqlType;
import com.mysql.cj.QueryBindings;
import com.mysql.cj.QueryInfo;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface JdbcPreparedStatement extends PreparedStatement, JdbcStatement {
  void realClose(boolean paramBoolean1, boolean paramBoolean2) throws SQLException;
  
  QueryBindings getQueryBindings();
  
  byte[] getBytesRepresentation(int paramInt) throws SQLException;
  
  QueryInfo getQueryInfo();
  
  boolean isNull(int paramInt) throws SQLException;
  
  String getPreparedSql();
  
  void setBytes(int paramInt, byte[] paramArrayOfbyte, boolean paramBoolean) throws SQLException;
  
  void setBigInteger(int paramInt, BigInteger paramBigInteger) throws SQLException;
  
  void setNull(int paramInt, MysqlType paramMysqlType) throws SQLException;
  
  ParameterBindings getParameterBindings() throws SQLException;
}
