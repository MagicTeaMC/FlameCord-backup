package com.mysql.cj.jdbc.result;

import com.mysql.cj.result.DefaultColumnDefinition;
import java.sql.ResultSetMetaData;

public class CachedResultSetMetaDataImpl extends DefaultColumnDefinition implements CachedResultSetMetaData {
  ResultSetMetaData metadata;
  
  public ResultSetMetaData getMetadata() {
    return this.metadata;
  }
  
  public void setMetadata(ResultSetMetaData metadata) {
    this.metadata = metadata;
  }
}
