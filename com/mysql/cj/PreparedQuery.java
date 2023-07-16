package com.mysql.cj;

public interface PreparedQuery extends Query {
  QueryInfo getQueryInfo();
  
  void setQueryInfo(QueryInfo paramQueryInfo);
  
  void checkNullOrEmptyQuery(String paramString);
  
  String getOriginalSql();
  
  void setOriginalSql(String paramString);
  
  int getParameterCount();
  
  void setParameterCount(int paramInt);
  
  QueryBindings getQueryBindings();
  
  void setQueryBindings(QueryBindings paramQueryBindings);
  
  int computeBatchSize(int paramInt);
  
  int getBatchCommandIndex();
  
  void setBatchCommandIndex(int paramInt);
  
  String asSql();
  
  <M extends com.mysql.cj.protocol.Message> M fillSendPacket(QueryBindings paramQueryBindings);
}
