package com.mysql.cj.jdbc.ha;

import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.jdbc.ConnectionImpl;
import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.JdbcPropertySetImpl;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.util.Util;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.concurrent.Executor;

public class FailoverConnectionProxy extends MultiHostConnectionProxy {
  private static final String METHOD_SET_READ_ONLY = "setReadOnly";
  
  private static final String METHOD_SET_AUTO_COMMIT = "setAutoCommit";
  
  private static final String METHOD_COMMIT = "commit";
  
  private static final String METHOD_ROLLBACK = "rollback";
  
  private static final int NO_CONNECTION_INDEX = -1;
  
  private static final int DEFAULT_PRIMARY_HOST_INDEX = 0;
  
  private int secondsBeforeRetryPrimaryHost;
  
  private long queriesBeforeRetryPrimaryHost;
  
  private boolean failoverReadOnly;
  
  private int retriesAllDown;
  
  private int currentHostIndex = -1;
  
  private int primaryHostIndex = 0;
  
  private Boolean explicitlyReadOnly = null;
  
  private boolean explicitlyAutoCommit = true;
  
  private boolean enableFallBackToPrimaryHost = true;
  
  private long primaryHostFailTimeMillis = 0L;
  
  private long queriesIssuedSinceFailover = 0L;
  
  class FailoverJdbcInterfaceProxy extends MultiHostConnectionProxy.JdbcInterfaceProxy {
    FailoverJdbcInterfaceProxy(Object toInvokeOn) {
      super(toInvokeOn);
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      String methodName = method.getName();
      boolean isExecute = methodName.startsWith("execute");
      if (FailoverConnectionProxy.this.connectedToSecondaryHost() && isExecute)
        FailoverConnectionProxy.this.incrementQueriesIssuedSinceFailover(); 
      Object result = super.invoke(proxy, method, args);
      if (FailoverConnectionProxy.this.explicitlyAutoCommit && isExecute && FailoverConnectionProxy.this.readyToFallBackToPrimaryHost())
        FailoverConnectionProxy.this.fallBackToPrimaryIfAvailable(); 
      return result;
    }
  }
  
  public static JdbcConnection createProxyInstance(ConnectionUrl connectionUrl) throws SQLException {
    FailoverConnectionProxy connProxy = new FailoverConnectionProxy(connectionUrl);
    return (JdbcConnection)Proxy.newProxyInstance(JdbcConnection.class.getClassLoader(), new Class[] { JdbcConnection.class }, connProxy);
  }
  
  private FailoverConnectionProxy(ConnectionUrl connectionUrl) throws SQLException {
    super(connectionUrl);
    JdbcPropertySetImpl connProps = new JdbcPropertySetImpl();
    connProps.initializeProperties(connectionUrl.getConnectionArgumentsAsProperties());
    this.secondsBeforeRetryPrimaryHost = ((Integer)connProps.getIntegerProperty(PropertyKey.secondsBeforeRetrySource).getValue()).intValue();
    this.queriesBeforeRetryPrimaryHost = ((Integer)connProps.getIntegerProperty(PropertyKey.queriesBeforeRetrySource).getValue()).intValue();
    this.failoverReadOnly = ((Boolean)connProps.getBooleanProperty(PropertyKey.failOverReadOnly).getValue()).booleanValue();
    this.retriesAllDown = ((Integer)connProps.getIntegerProperty(PropertyKey.retriesAllDown).getValue()).intValue();
    this.enableFallBackToPrimaryHost = (this.secondsBeforeRetryPrimaryHost > 0 || this.queriesBeforeRetryPrimaryHost > 0L);
    pickNewConnection();
    this.explicitlyAutoCommit = this.currentConnection.getAutoCommit();
  }
  
  MultiHostConnectionProxy.JdbcInterfaceProxy getNewJdbcInterfaceProxy(Object toProxy) {
    return new FailoverJdbcInterfaceProxy(toProxy);
  }
  
  boolean shouldExceptionTriggerConnectionSwitch(Throwable t) {
    String sqlState = null;
    if (t instanceof com.mysql.cj.jdbc.exceptions.CommunicationsException || t instanceof com.mysql.cj.exceptions.CJCommunicationsException)
      return true; 
    if (t instanceof SQLException) {
      sqlState = ((SQLException)t).getSQLState();
    } else if (t instanceof CJException) {
      sqlState = ((CJException)t).getSQLState();
    } 
    if (sqlState != null && 
      sqlState.startsWith("08"))
      return true; 
    return false;
  }
  
  boolean isSourceConnection() {
    return connectedToPrimaryHost();
  }
  
  synchronized void pickNewConnection() throws SQLException {
    if (this.isClosed && this.closedExplicitly)
      return; 
    if (!isConnected() || readyToFallBackToPrimaryHost()) {
      try {
        connectTo(this.primaryHostIndex);
      } catch (SQLException e) {
        resetAutoFallBackCounters();
        failOver(this.primaryHostIndex);
      } 
    } else {
      failOver();
    } 
  }
  
  synchronized ConnectionImpl createConnectionForHostIndex(int hostIndex) throws SQLException {
    return createConnectionForHost(this.hostsList.get(hostIndex));
  }
  
  private synchronized void connectTo(int hostIndex) throws SQLException {
    try {
      switchCurrentConnectionTo(hostIndex, (JdbcConnection)createConnectionForHostIndex(hostIndex));
    } catch (SQLException e) {
      if (this.currentConnection != null) {
        StringBuilder msg = (new StringBuilder("Connection to ")).append(isPrimaryHostIndex(hostIndex) ? "primary" : "secondary").append(" host '").append(this.hostsList.get(hostIndex)).append("' failed");
        try {
          this.currentConnection.getSession().getLog().logWarn(msg.toString(), e);
        } catch (CJException ex) {
          throw SQLExceptionsMapping.translateException(e, this.currentConnection.getExceptionInterceptor());
        } 
      } 
      throw e;
    } 
  }
  
  private synchronized void switchCurrentConnectionTo(int hostIndex, JdbcConnection connection) throws SQLException {
    boolean readOnly;
    invalidateCurrentConnection();
    if (isPrimaryHostIndex(hostIndex)) {
      readOnly = (this.explicitlyReadOnly == null) ? false : this.explicitlyReadOnly.booleanValue();
    } else if (this.failoverReadOnly) {
      readOnly = true;
    } else if (this.explicitlyReadOnly != null) {
      readOnly = this.explicitlyReadOnly.booleanValue();
    } else if (this.currentConnection != null) {
      readOnly = this.currentConnection.isReadOnly();
    } else {
      readOnly = false;
    } 
    syncSessionState(this.currentConnection, connection, readOnly);
    this.currentConnection = connection;
    this.currentHostIndex = hostIndex;
  }
  
  private synchronized void failOver() throws SQLException {
    failOver(this.currentHostIndex);
  }
  
  private synchronized void failOver(int failedHostIdx) throws SQLException {
    int prevHostIndex = this.currentHostIndex;
    int nextHostIndex = nextHost(failedHostIdx, false);
    int firstHostIndexTried = nextHostIndex;
    SQLException lastExceptionCaught = null;
    int attempts = 0;
    boolean gotConnection = false;
    boolean firstConnOrPassedByPrimaryHost = (prevHostIndex == -1 || isPrimaryHostIndex(prevHostIndex));
    do {
      try {
        firstConnOrPassedByPrimaryHost = (firstConnOrPassedByPrimaryHost || isPrimaryHostIndex(nextHostIndex));
        connectTo(nextHostIndex);
        if (firstConnOrPassedByPrimaryHost && connectedToSecondaryHost())
          resetAutoFallBackCounters(); 
        gotConnection = true;
      } catch (SQLException e) {
        lastExceptionCaught = e;
        if (shouldExceptionTriggerConnectionSwitch(e)) {
          int newNextHostIndex = nextHost(nextHostIndex, (attempts > 0));
          if (newNextHostIndex == firstHostIndexTried && newNextHostIndex == (newNextHostIndex = nextHost(nextHostIndex, true))) {
            attempts++;
            try {
              Thread.sleep(250L);
            } catch (InterruptedException interruptedException) {}
          } 
          nextHostIndex = newNextHostIndex;
        } else {
          throw e;
        } 
      } 
    } while (attempts < this.retriesAllDown && !gotConnection);
    if (!gotConnection)
      throw lastExceptionCaught; 
  }
  
  synchronized void fallBackToPrimaryIfAvailable() {
    ConnectionImpl connectionImpl;
    JdbcConnection connection = null;
    try {
      connectionImpl = createConnectionForHostIndex(this.primaryHostIndex);
      switchCurrentConnectionTo(this.primaryHostIndex, (JdbcConnection)connectionImpl);
    } catch (SQLException e1) {
      if (connectionImpl != null)
        try {
          connectionImpl.close();
        } catch (SQLException sQLException) {} 
      resetAutoFallBackCounters();
    } 
  }
  
  private int nextHost(int currHostIdx, boolean vouchForPrimaryHost) {
    int nextHostIdx = (currHostIdx + 1) % this.hostsList.size();
    if (isPrimaryHostIndex(nextHostIdx) && isConnected() && !vouchForPrimaryHost && this.enableFallBackToPrimaryHost && !readyToFallBackToPrimaryHost())
      nextHostIdx = nextHost(nextHostIdx, vouchForPrimaryHost); 
    return nextHostIdx;
  }
  
  synchronized void incrementQueriesIssuedSinceFailover() {
    this.queriesIssuedSinceFailover++;
  }
  
  synchronized boolean readyToFallBackToPrimaryHost() {
    return (this.enableFallBackToPrimaryHost && connectedToSecondaryHost() && (secondsBeforeRetryPrimaryHostIsMet() || queriesBeforeRetryPrimaryHostIsMet()));
  }
  
  synchronized boolean isConnected() {
    return (this.currentHostIndex != -1);
  }
  
  synchronized boolean isPrimaryHostIndex(int hostIndex) {
    return (hostIndex == this.primaryHostIndex);
  }
  
  synchronized boolean connectedToPrimaryHost() {
    return isPrimaryHostIndex(this.currentHostIndex);
  }
  
  synchronized boolean connectedToSecondaryHost() {
    return (this.currentHostIndex >= 0 && !isPrimaryHostIndex(this.currentHostIndex));
  }
  
  private synchronized boolean secondsBeforeRetryPrimaryHostIsMet() {
    return (this.secondsBeforeRetryPrimaryHost > 0 && Util.secondsSinceMillis(this.primaryHostFailTimeMillis) >= this.secondsBeforeRetryPrimaryHost);
  }
  
  private synchronized boolean queriesBeforeRetryPrimaryHostIsMet() {
    return (this.queriesBeforeRetryPrimaryHost > 0L && this.queriesIssuedSinceFailover >= this.queriesBeforeRetryPrimaryHost);
  }
  
  private synchronized void resetAutoFallBackCounters() {
    this.primaryHostFailTimeMillis = System.currentTimeMillis();
    this.queriesIssuedSinceFailover = 0L;
  }
  
  synchronized void doClose() throws SQLException {
    this.currentConnection.close();
  }
  
  synchronized void doAbortInternal() throws SQLException {
    this.currentConnection.abortInternal();
  }
  
  synchronized void doAbort(Executor executor) throws SQLException {
    this.currentConnection.abort(executor);
  }
  
  public Object invokeMore(Object proxy, Method method, Object[] args) throws Throwable {
    String methodName = method.getName();
    if ("setReadOnly".equals(methodName)) {
      this.explicitlyReadOnly = (Boolean)args[0];
      if (this.failoverReadOnly && connectedToSecondaryHost())
        return null; 
    } 
    if (this.isClosed && !allowedOnClosedConnection(method))
      if (this.autoReconnect && !this.closedExplicitly) {
        this.currentHostIndex = -1;
        pickNewConnection();
        this.isClosed = false;
        this.closedReason = null;
      } else {
        String reason = "No operations allowed after connection closed.";
        if (this.closedReason != null)
          reason = reason + "  " + this.closedReason; 
        throw SQLError.createSQLException(reason, "08003", null);
      }  
    Object result = null;
    try {
      result = method.invoke(this.thisAsConnection, args);
      result = proxyIfReturnTypeIsJdbcInterface(method.getReturnType(), result);
    } catch (InvocationTargetException e) {
      dealWithInvocationException(e);
    } 
    if ("setAutoCommit".equals(methodName))
      this.explicitlyAutoCommit = ((Boolean)args[0]).booleanValue(); 
    if ((this.explicitlyAutoCommit || "commit".equals(methodName) || "rollback".equals(methodName)) && readyToFallBackToPrimaryHost())
      fallBackToPrimaryIfAvailable(); 
    return result;
  }
}
