package com.mysql.cj;

import java.util.List;

public interface MessageBuilder<M extends com.mysql.cj.protocol.Message> {
  M buildSqlStatement(String paramString);
  
  M buildSqlStatement(String paramString, List<Object> paramList);
  
  M buildClose();
  
  M buildComQuery(M paramM, Session paramSession, PreparedQuery paramPreparedQuery, QueryBindings paramQueryBindings, String paramString);
}
