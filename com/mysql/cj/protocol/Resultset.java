package com.mysql.cj.protocol;

public interface Resultset extends ProtocolEntity {
  void setColumnDefinition(ColumnDefinition paramColumnDefinition);
  
  ColumnDefinition getColumnDefinition();
  
  boolean hasRows();
  
  ResultsetRows getRows();
  
  void initRowsWithMetadata();
  
  int getResultId();
  
  void setNextResultset(Resultset paramResultset);
  
  Resultset getNextResultset();
  
  void clearNextResultset();
  
  long getUpdateCount();
  
  long getUpdateID();
  
  String getServerInfo();
  
  public enum Concurrency {
    READ_ONLY(1007),
    UPDATABLE(1008);
    
    private int value;
    
    Concurrency(int jdbcRsConcur) {
      this.value = jdbcRsConcur;
    }
    
    public int getIntValue() {
      return this.value;
    }
    
    public static Concurrency fromValue(int concurMode, Concurrency backupValue) {
      for (Concurrency c : values()) {
        if (c.getIntValue() == concurMode)
          return c; 
      } 
      return backupValue;
    }
  }
  
  public enum Type {
    FORWARD_ONLY(1003),
    SCROLL_INSENSITIVE(1004),
    SCROLL_SENSITIVE(1005);
    
    private int value;
    
    Type(int jdbcRsType) {
      this.value = jdbcRsType;
    }
    
    public int getIntValue() {
      return this.value;
    }
    
    public static Type fromValue(int rsType, Type backupValue) {
      for (Type t : values()) {
        if (t.getIntValue() == rsType)
          return t; 
      } 
      return backupValue;
    }
  }
}
