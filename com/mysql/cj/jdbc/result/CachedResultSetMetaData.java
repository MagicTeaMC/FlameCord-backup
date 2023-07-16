package com.mysql.cj.jdbc.result;

import com.mysql.cj.protocol.ColumnDefinition;
import java.sql.ResultSetMetaData;

public interface CachedResultSetMetaData extends ColumnDefinition {
  ResultSetMetaData getMetadata();
  
  void setMetadata(ResultSetMetaData paramResultSetMetaData);
}
