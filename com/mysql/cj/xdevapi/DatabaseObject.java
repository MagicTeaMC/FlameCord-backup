package com.mysql.cj.xdevapi;

public interface DatabaseObject {
  Session getSession();
  
  Schema getSchema();
  
  String getName();
  
  DbObjectStatus existsInDatabase();
  
  public enum DbObjectType {
    COLLECTION, TABLE, VIEW, COLLECTION_VIEW;
  }
  
  public enum DbObjectStatus {
    EXISTS, NOT_EXISTS, UNKNOWN;
  }
}
