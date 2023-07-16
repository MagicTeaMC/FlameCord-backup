package com.mysql.cj.protocol.a;

import com.mysql.cj.BindValue;
import com.mysql.cj.exceptions.ExceptionFactory;
import java.sql.Clob;

public class ClobValueEncoder extends ReaderValueEncoder {
  public byte[] getBytes(BindValue binding) {
    try {
      return readBytes(((Clob)binding.getValue()).getCharacterStream(), binding);
    } catch (Throwable t) {
      throw ExceptionFactory.createException(t.getMessage(), t, this.exceptionInterceptor);
    } 
  }
}
