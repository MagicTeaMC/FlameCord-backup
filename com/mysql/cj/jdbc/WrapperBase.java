package com.mysql.cj.jdbc;

import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.util.Util;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.Map;

abstract class WrapperBase {
  protected MysqlPooledConnection pooledConnection;
  
  protected void checkAndFireConnectionError(SQLException sqlEx) throws SQLException {
    if (this.pooledConnection != null && 
      "08S01".equals(sqlEx.getSQLState()))
      this.pooledConnection.callConnectionEventListeners(1, sqlEx); 
    throw sqlEx;
  }
  
  protected Map<Class<?>, Object> unwrappedInterfaces = null;
  
  protected ExceptionInterceptor exceptionInterceptor;
  
  protected WrapperBase(MysqlPooledConnection pooledConnection) {
    this.pooledConnection = pooledConnection;
    this.exceptionInterceptor = this.pooledConnection.getExceptionInterceptor();
  }
  
  protected class ConnectionErrorFiringInvocationHandler implements InvocationHandler {
    Object invokeOn = null;
    
    public ConnectionErrorFiringInvocationHandler(Object toInvokeOn) {
      this.invokeOn = toInvokeOn;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if ("equals".equals(method.getName()))
        return Boolean.valueOf(args[0].equals(this)); 
      Object result = null;
      try {
        result = method.invoke(this.invokeOn, args);
        if (result != null)
          result = proxyIfInterfaceIsJdbc(result, result.getClass()); 
      } catch (InvocationTargetException e) {
        if (e.getTargetException() instanceof SQLException) {
          WrapperBase.this.checkAndFireConnectionError((SQLException)e.getTargetException());
        } else {
          throw e;
        } 
      } 
      return result;
    }
    
    private Object proxyIfInterfaceIsJdbc(Object toProxy, Class<?> clazz) {
      Class<?>[] interfaces = clazz.getInterfaces();
      Class<?>[] arrayOfClass1 = interfaces;
      int i = arrayOfClass1.length;
      byte b = 0;
      if (b < i) {
        Class<?> iclass = arrayOfClass1[b];
        String packageName = Util.getPackageName(iclass);
        if ("java.sql".equals(packageName) || "javax.sql".equals(packageName))
          return Proxy.newProxyInstance(toProxy.getClass().getClassLoader(), interfaces, new ConnectionErrorFiringInvocationHandler(toProxy)); 
        return proxyIfInterfaceIsJdbc(toProxy, iclass);
      } 
      return toProxy;
    }
  }
}
