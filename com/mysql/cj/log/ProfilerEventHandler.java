package com.mysql.cj.log;

import com.mysql.cj.Query;
import com.mysql.cj.Session;
import com.mysql.cj.protocol.Resultset;

public interface ProfilerEventHandler {
  void init(Log paramLog);
  
  void destroy();
  
  void consumeEvent(ProfilerEvent paramProfilerEvent);
  
  void processEvent(byte paramByte, Session paramSession, Query paramQuery, Resultset paramResultset, long paramLong, Throwable paramThrowable, String paramString);
}
