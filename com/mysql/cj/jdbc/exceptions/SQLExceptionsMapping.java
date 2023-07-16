package com.mysql.cj.jdbc.exceptions;

import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.DataTruncationException;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import java.sql.SQLException;

public class SQLExceptionsMapping {
  public static SQLException translateException(Throwable ex, ExceptionInterceptor interceptor) {
    if (ex instanceof SQLException)
      return (SQLException)ex; 
    if (ex.getCause() != null && ex.getCause() instanceof SQLException)
      return (SQLException)ex.getCause(); 
    if (ex instanceof com.mysql.cj.exceptions.CJCommunicationsException)
      return SQLError.createCommunicationsException(ex.getMessage(), ex, interceptor); 
    if (ex instanceof com.mysql.cj.exceptions.CJConnectionFeatureNotAvailableException)
      return new ConnectionFeatureNotAvailableException(ex.getMessage(), ex); 
    if (ex instanceof com.mysql.cj.exceptions.SSLParamsException)
      return SQLError.createSQLException(ex.getMessage(), "08000", 0, false, ex, interceptor); 
    if (ex instanceof com.mysql.cj.exceptions.ConnectionIsClosedException)
      return SQLError.createSQLException(ex.getMessage(), "08003", ex, interceptor); 
    if (ex instanceof com.mysql.cj.exceptions.InvalidConnectionAttributeException)
      return SQLError.createSQLException(ex.getMessage(), "01S00", ex, interceptor); 
    if (ex instanceof com.mysql.cj.exceptions.UnableToConnectException)
      return SQLError.createSQLException(ex.getMessage(), "08001", ex, interceptor); 
    if (ex instanceof com.mysql.cj.exceptions.StatementIsClosedException)
      return SQLError.createSQLException(ex.getMessage(), "S1009", ex, interceptor); 
    if (ex instanceof com.mysql.cj.exceptions.WrongArgumentException)
      return SQLError.createSQLException(ex.getMessage(), "S1009", ex, interceptor); 
    if (ex instanceof StringIndexOutOfBoundsException)
      return SQLError.createSQLException(ex.getMessage(), "S1009", ex, interceptor); 
    if (ex instanceof com.mysql.cj.exceptions.NumberOutOfRange)
      return SQLError.createSQLException(ex.getMessage(), "22003", ex, interceptor); 
    if (ex instanceof com.mysql.cj.exceptions.DataConversionException)
      return SQLError.createSQLException(ex.getMessage(), "22018", ex, interceptor); 
    if (ex instanceof com.mysql.cj.exceptions.DataReadException)
      return SQLError.createSQLException(ex.getMessage(), "S1009", ex, interceptor); 
    if (ex instanceof DataTruncationException)
      return new MysqlDataTruncation(((DataTruncationException)ex).getMessage(), ((DataTruncationException)ex).getIndex(), ((DataTruncationException)ex)
          .isParameter(), ((DataTruncationException)ex).isRead(), ((DataTruncationException)ex).getDataSize(), ((DataTruncationException)ex)
          .getTransferSize(), ((DataTruncationException)ex).getVendorCode()); 
    if (ex instanceof com.mysql.cj.exceptions.CJPacketTooBigException)
      return new PacketTooBigException(ex.getMessage()); 
    if (ex instanceof com.mysql.cj.exceptions.OperationCancelledException)
      return new MySQLStatementCancelledException(ex.getMessage()); 
    if (ex instanceof com.mysql.cj.exceptions.CJTimeoutException)
      return new MySQLTimeoutException(ex.getMessage()); 
    if (ex instanceof com.mysql.cj.exceptions.CJOperationNotSupportedException)
      return new OperationNotSupportedException(ex.getMessage()); 
    if (ex instanceof UnsupportedOperationException)
      return new OperationNotSupportedException(ex.getMessage()); 
    if (ex instanceof CJException)
      return SQLError.createSQLException(ex.getMessage(), ((CJException)ex).getSQLState(), ((CJException)ex).getVendorCode(), ((CJException)ex)
          .isTransient(), ex.getCause(), interceptor); 
    return SQLError.createSQLException(ex.getMessage(), "S1000", ex, interceptor);
  }
  
  public static SQLException translateException(Throwable ex) {
    return translateException(ex, null);
  }
}
