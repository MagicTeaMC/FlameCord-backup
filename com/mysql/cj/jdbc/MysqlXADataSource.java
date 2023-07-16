package com.mysql.cj.jdbc;

import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.XAConnection;
import javax.sql.XADataSource;

public class MysqlXADataSource extends MysqlDataSource implements XADataSource {
  static final long serialVersionUID = 7911390333152247455L;
  
  public XAConnection getXAConnection() throws SQLException {
    try {
      Connection conn = getConnection();
      return wrapConnection(conn);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  public XAConnection getXAConnection(String u, String p) throws SQLException {
    try {
      Connection conn = getConnection(u, p);
      return wrapConnection(conn);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  private XAConnection wrapConnection(Connection conn) throws SQLException {
    if (((Boolean)getBooleanProperty(PropertyKey.pinGlobalTxToPhysicalConnection).getValue()).booleanValue() || ((Boolean)((JdbcConnection)conn)
      .getPropertySet().getBooleanProperty(PropertyKey.pinGlobalTxToPhysicalConnection).getValue()).booleanValue())
      return SuspendableXAConnection.getInstance((JdbcConnection)conn); 
    return MysqlXAConnection.getInstance((JdbcConnection)conn, ((Boolean)getBooleanProperty(PropertyKey.logXaCommands).getValue()).booleanValue());
  }
}
