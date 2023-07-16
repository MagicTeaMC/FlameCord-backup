package com.mysql.cj.protocol;

import com.mysql.cj.BindValue;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.ExceptionInterceptor;

public interface ValueEncoder {
  void init(PropertySet paramPropertySet, ServerSession paramServerSession, ExceptionInterceptor paramExceptionInterceptor);
  
  byte[] getBytes(BindValue paramBindValue);
  
  String getString(BindValue paramBindValue);
  
  long getTextLength(BindValue paramBindValue);
  
  long getBinaryLength(BindValue paramBindValue);
  
  void encodeAsText(Message paramMessage, BindValue paramBindValue);
  
  void encodeAsBinary(Message paramMessage, BindValue paramBindValue);
  
  void encodeAsQueryAttribute(Message paramMessage, BindValue paramBindValue);
}
