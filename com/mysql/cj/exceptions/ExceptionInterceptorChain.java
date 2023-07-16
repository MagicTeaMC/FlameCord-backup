package com.mysql.cj.exceptions;

import com.mysql.cj.log.Log;
import com.mysql.cj.util.Util;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ExceptionInterceptorChain implements ExceptionInterceptor {
  private List<ExceptionInterceptor> interceptors;
  
  public ExceptionInterceptorChain(String interceptorClasses, Properties props, Log log) {
    this
      .interceptors = (List<ExceptionInterceptor>)Util.loadClasses(ExceptionInterceptor.class, interceptorClasses, "Connection.BadExceptionInterceptor", null).stream().map(i -> i.init(props, log)).collect(Collectors.toCollection(java.util.LinkedList::new));
  }
  
  public void addRingZero(ExceptionInterceptor interceptor) {
    this.interceptors.add(0, interceptor);
  }
  
  public Exception interceptException(Exception sqlEx) {
    for (ExceptionInterceptor ie : this.interceptors)
      sqlEx = ie.interceptException(sqlEx); 
    return sqlEx;
  }
  
  public void destroy() {
    this.interceptors.forEach(ExceptionInterceptor::destroy);
  }
  
  public ExceptionInterceptor init(Properties properties, Log log) {
    this.interceptors = (List<ExceptionInterceptor>)this.interceptors.stream().map(i -> i.init(properties, log)).collect(Collectors.toCollection(java.util.LinkedList::new));
    return this;
  }
  
  public List<ExceptionInterceptor> getInterceptors() {
    return this.interceptors;
  }
}
