package com.mysql.cj.protocol;

import com.mysql.cj.ServerVersion;

public interface ServerCapabilities {
  int getCapabilityFlags();
  
  void setCapabilityFlags(int paramInt);
  
  ServerVersion getServerVersion();
  
  long getThreadId();
  
  void setThreadId(long paramLong);
  
  boolean serverSupportsFracSecs();
  
  int getServerDefaultCollationIndex();
}
