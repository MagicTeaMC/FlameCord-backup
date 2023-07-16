package com.mysql.cj.jdbc.ha;

import java.util.Properties;

public interface LoadBalanceExceptionChecker {
  void init(Properties paramProperties);
  
  void destroy();
  
  boolean shouldExceptionTriggerFailover(Throwable paramThrowable);
}
