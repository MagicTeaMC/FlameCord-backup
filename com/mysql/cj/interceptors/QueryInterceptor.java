package com.mysql.cj.interceptors;

import com.mysql.cj.MysqlConnection;
import com.mysql.cj.Query;
import com.mysql.cj.log.Log;
import com.mysql.cj.protocol.ServerSession;
import java.util.Properties;
import java.util.function.Supplier;

public interface QueryInterceptor {
  QueryInterceptor init(MysqlConnection paramMysqlConnection, Properties paramProperties, Log paramLog);
  
  <T extends com.mysql.cj.protocol.Resultset> T preProcess(Supplier<String> paramSupplier, Query paramQuery);
  
  default <M extends com.mysql.cj.protocol.Message> M preProcess(M queryPacket) {
    return null;
  }
  
  boolean executeTopLevelOnly();
  
  void destroy();
  
  <T extends com.mysql.cj.protocol.Resultset> T postProcess(Supplier<String> paramSupplier, Query paramQuery, T paramT, ServerSession paramServerSession);
  
  default <M extends com.mysql.cj.protocol.Message> M postProcess(M queryPacket, M originalResponsePacket) {
    return null;
  }
}
