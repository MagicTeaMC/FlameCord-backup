package com.mysql.cj.jdbc;

import com.mysql.cj.exceptions.ExceptionInterceptor;
import java.sql.NClob;

public class NClob extends Clob implements NClob {
  NClob(ExceptionInterceptor exceptionInterceptor) {
    super(exceptionInterceptor);
  }
  
  public NClob(String charDataInit, ExceptionInterceptor exceptionInterceptor) {
    super(charDataInit, exceptionInterceptor);
  }
}
