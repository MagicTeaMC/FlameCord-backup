package com.mysql.cj.protocol;

import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.ExceptionInterceptor;

public interface AuthenticationProvider<M extends Message> {
  void init(Protocol<M> paramProtocol, PropertySet paramPropertySet, ExceptionInterceptor paramExceptionInterceptor);
  
  void connect(String paramString1, String paramString2, String paramString3);
  
  void changeUser(String paramString1, String paramString2, String paramString3);
}
