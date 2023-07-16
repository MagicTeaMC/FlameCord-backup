package com.mysql.cj.jdbc.result;

import com.mysql.cj.jdbc.JdbcPreparedStatement;
import com.mysql.cj.jdbc.JdbcStatement;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.ResultsetRowsOwner;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public interface ResultSetInternalMethods extends ResultSet, ResultsetRowsOwner, Resultset {
  Object getObjectStoredProc(int paramInt1, int paramInt2) throws SQLException;
  
  Object getObjectStoredProc(int paramInt1, Map<Object, Object> paramMap, int paramInt2) throws SQLException;
  
  Object getObjectStoredProc(String paramString, int paramInt) throws SQLException;
  
  Object getObjectStoredProc(String paramString, Map<Object, Object> paramMap, int paramInt) throws SQLException;
  
  void realClose(boolean paramBoolean) throws SQLException;
  
  void setFirstCharOfQuery(char paramChar);
  
  void setOwningStatement(JdbcStatement paramJdbcStatement);
  
  char getFirstCharOfQuery();
  
  void setStatementUsedForFetchingRows(JdbcPreparedStatement paramJdbcPreparedStatement);
  
  void setWrapperStatement(Statement paramStatement);
  
  void initializeWithMetadata() throws SQLException;
  
  void populateCachedMetaData(CachedResultSetMetaData paramCachedResultSetMetaData) throws SQLException;
  
  BigInteger getBigInteger(int paramInt) throws SQLException;
}
