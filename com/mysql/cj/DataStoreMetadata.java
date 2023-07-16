package com.mysql.cj;

public interface DataStoreMetadata {
  boolean schemaExists(String paramString);
  
  boolean tableExists(String paramString1, String paramString2);
  
  long getTableRowCount(String paramString1, String paramString2);
}
