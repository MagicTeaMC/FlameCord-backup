package com.mysql.cj.exceptions;

import com.mysql.cj.log.Log;
import java.util.Properties;

public interface ExceptionInterceptor {
  ExceptionInterceptor init(Properties paramProperties, Log paramLog);
  
  void destroy();
  
  Exception interceptException(Exception paramException);
}
