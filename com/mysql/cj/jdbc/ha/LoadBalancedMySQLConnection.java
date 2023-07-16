package com.mysql.cj.jdbc.ha;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import java.sql.SQLException;

public class LoadBalancedMySQLConnection extends MultiHostMySQLConnection implements LoadBalancedConnection {
  public LoadBalancedMySQLConnection(LoadBalancedConnectionProxy proxy) {
    super(proxy);
  }
  
  public LoadBalancedConnectionProxy getThisAsProxy() {
    return (LoadBalancedConnectionProxy)super.getThisAsProxy();
  }
  
  public void close() throws SQLException {
    try {
      getThisAsProxy().doClose();
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void ping() throws SQLException {
    try {
      ping(true);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void ping(boolean allConnections) throws SQLException {
    try {
      if (allConnections) {
        getThisAsProxy().doPing();
      } else {
        getActiveMySQLConnection().ping();
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean addHost(String host) throws SQLException {
    try {
      return getThisAsProxy().addHost(host);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void removeHost(String host) throws SQLException {
    try {
      getThisAsProxy().removeHost(host);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void removeHostWhenNotInUse(String host) throws SQLException {
    try {
      getThisAsProxy().removeHostWhenNotInUse(host);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    try {
      return iface.isInstance(this);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public <T> T unwrap(Class<T> iface) throws SQLException {
    try {
      try {
        return iface.cast(this);
      } catch (ClassCastException cce) {
        throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[] { iface.toString() }), "S1009", 
            getExceptionInterceptor());
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
}
