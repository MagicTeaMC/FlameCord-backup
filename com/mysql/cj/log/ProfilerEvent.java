package com.mysql.cj.log;

public interface ProfilerEvent {
  public static final byte TYPE_USAGE = 0;
  
  public static final byte TYPE_OBJECT_CREATION = 1;
  
  public static final byte TYPE_PREPARE = 2;
  
  public static final byte TYPE_QUERY = 3;
  
  public static final byte TYPE_EXECUTE = 4;
  
  public static final byte TYPE_FETCH = 5;
  
  public static final byte TYPE_SLOW_QUERY = 6;
  
  public static final byte NA = -1;
  
  byte getEventType();
  
  String getHostName();
  
  String getDatabase();
  
  long getConnectionId();
  
  int getStatementId();
  
  int getResultSetId();
  
  long getEventCreationTime();
  
  long getEventDuration();
  
  String getDurationUnits();
  
  String getEventCreationPointAsString();
  
  String getMessage();
  
  byte[] pack();
}
