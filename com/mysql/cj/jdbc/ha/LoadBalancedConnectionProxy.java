package com.mysql.cj.jdbc.ha;

import com.mysql.cj.Messages;
import com.mysql.cj.PingTarget;
import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.url.LoadBalanceConnectionUrl;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.interceptors.QueryInterceptor;
import com.mysql.cj.jdbc.ConnectionGroup;
import com.mysql.cj.jdbc.ConnectionGroupManager;
import com.mysql.cj.jdbc.ConnectionImpl;
import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.JdbcStatement;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.util.Util;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class LoadBalancedConnectionProxy extends MultiHostConnectionProxy implements PingTarget {
  private ConnectionGroup connectionGroup;
  
  private long connectionGroupProxyID;
  
  protected Map<String, ConnectionImpl> liveConnections;
  
  private Map<String, Integer> hostsToListIndexMap;
  
  private Map<ConnectionImpl, String> connectionsToHostsMap;
  
  private long totalPhysicalConnections;
  
  private long[] responseTimes;
  
  private int retriesAllDown;
  
  private BalanceStrategy balancer;
  
  private int globalBlocklistTimeout;
  
  private static Map<String, Long> globalBlocklist = new HashMap<>();
  
  private int hostRemovalGracePeriod;
  
  private Set<String> hostsToRemove;
  
  private boolean inTransaction;
  
  private long transactionStartTime;
  
  private long transactionCount;
  
  private LoadBalanceExceptionChecker exceptionChecker;
  
  private static Class<?>[] INTERFACES_TO_PROXY = new Class[] { LoadBalancedConnection.class, JdbcConnection.class };
  
  public static LoadBalancedConnection createProxyInstance(ConnectionUrl connectionUrl) throws SQLException {
    LoadBalancedConnectionProxy connProxy = new LoadBalancedConnectionProxy(connectionUrl);
    return (LoadBalancedConnection)Proxy.newProxyInstance(LoadBalancedConnection.class.getClassLoader(), INTERFACES_TO_PROXY, connProxy);
  }
  
  public LoadBalancedConnectionProxy(ConnectionUrl connectionUrl) throws SQLException {
    List<HostInfo> hosts;
    this.connectionGroup = null;
    this.connectionGroupProxyID = 0L;
    this.totalPhysicalConnections = 0L;
    this.globalBlocklistTimeout = 0;
    this.hostRemovalGracePeriod = 0;
    this.hostsToRemove = new HashSet<>();
    this.inTransaction = false;
    this.transactionStartTime = 0L;
    this.transactionCount = 0L;
    Properties props = connectionUrl.getConnectionArgumentsAsProperties();
    String group = props.getProperty(PropertyKey.loadBalanceConnectionGroup.getKeyName(), null);
    boolean enableJMX = false;
    String enableJMXAsString = props.getProperty(PropertyKey.ha_enableJMX.getKeyName(), "false");
    try {
      enableJMX = Boolean.parseBoolean(enableJMXAsString);
    } catch (Exception e) {
      throw SQLError.createSQLException(Messages.getString("MultihostConnection.badValueForHaEnableJMX", new Object[] { enableJMXAsString }), "S1009", null);
    } 
    if (!StringUtils.isNullOrEmpty(group) && LoadBalanceConnectionUrl.class.isAssignableFrom(connectionUrl.getClass())) {
      this.connectionGroup = ConnectionGroupManager.getConnectionGroupInstance(group);
      if (enableJMX)
        ConnectionGroupManager.registerJmx(); 
      this.connectionGroupProxyID = this.connectionGroup.registerConnectionProxy(this, ((LoadBalanceConnectionUrl)connectionUrl)
          .getHostInfoListAsHostPortPairs());
      hosts = ((LoadBalanceConnectionUrl)connectionUrl).getHostInfoListFromHostPortPairs(this.connectionGroup.getInitialHosts());
    } else {
      hosts = connectionUrl.getHostsList();
    } 
    int numHosts = initializeHostsSpecs(connectionUrl, hosts);
    this.liveConnections = new HashMap<>(numHosts);
    this.hostsToListIndexMap = new HashMap<>(numHosts);
    for (int i = 0; i < numHosts; i++)
      this.hostsToListIndexMap.put(((HostInfo)this.hostsList.get(i)).getHostPortPair(), Integer.valueOf(i)); 
    this.connectionsToHostsMap = new HashMap<>(numHosts);
    this.responseTimes = new long[numHosts];
    String retriesAllDownAsString = props.getProperty(PropertyKey.retriesAllDown.getKeyName(), "120");
    try {
      this.retriesAllDown = Integer.parseInt(retriesAllDownAsString);
    } catch (NumberFormatException nfe) {
      throw SQLError.createSQLException(
          Messages.getString("LoadBalancedConnectionProxy.badValueForRetriesAllDown", new Object[] { retriesAllDownAsString }), "S1009", null);
    } 
    String blocklistTimeoutAsString = props.getProperty(PropertyKey.loadBalanceBlocklistTimeout.getKeyName(), "0");
    try {
      this.globalBlocklistTimeout = Integer.parseInt(blocklistTimeoutAsString);
    } catch (NumberFormatException nfe) {
      throw SQLError.createSQLException(
          Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceBlocklistTimeout", new Object[] { blocklistTimeoutAsString }), "S1009", null);
    } 
    String hostRemovalGracePeriodAsString = props.getProperty(PropertyKey.loadBalanceHostRemovalGracePeriod.getKeyName(), "15000");
    try {
      this.hostRemovalGracePeriod = Integer.parseInt(hostRemovalGracePeriodAsString);
    } catch (NumberFormatException nfe) {
      throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceHostRemovalGracePeriod", new Object[] { hostRemovalGracePeriodAsString }), "S1009", null);
    } 
    String strategy = props.getProperty(PropertyKey.ha_loadBalanceStrategy.getKeyName(), "random");
    try {
      switch (strategy) {
        case "random":
          this.balancer = new RandomBalanceStrategy();
          break;
        case "bestResponseTime":
          this.balancer = new BestResponseTimeBalanceStrategy();
          break;
        case "serverAffinity":
          this.balancer = new ServerAffinityStrategy(props.getProperty(PropertyKey.serverAffinityOrder.getKeyName(), null));
          break;
        default:
          this.balancer = (BalanceStrategy)Util.getInstance(BalanceStrategy.class, strategy, null, null, null);
          break;
      } 
    } catch (Throwable t) {
      throw SQLError.createSQLException(Messages.getString("InvalidLoadBalanceStrategy", new Object[] { strategy }), "S1009", t, null);
    } 
    String autoCommitSwapThresholdAsString = props.getProperty(PropertyKey.loadBalanceAutoCommitStatementThreshold.getKeyName(), "0");
    try {
      Integer.parseInt(autoCommitSwapThresholdAsString);
    } catch (NumberFormatException nfe) {
      throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceAutoCommitStatementThreshold", new Object[] { autoCommitSwapThresholdAsString }), "S1009", null);
    } 
    String autoCommitSwapRegex = props.getProperty(PropertyKey.loadBalanceAutoCommitStatementRegex.getKeyName(), "");
    if (!"".equals(autoCommitSwapRegex))
      try {
        "".matches(autoCommitSwapRegex);
      } catch (Exception e) {
        throw SQLError.createSQLException(
            Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceAutoCommitStatementRegex", new Object[] { autoCommitSwapRegex }), "S1009", null);
      }  
    try {
      String lbExceptionChecker = props.getProperty(PropertyKey.loadBalanceExceptionChecker.getKeyName(), StandardLoadBalanceExceptionChecker.class
          .getName());
      this.exceptionChecker = (LoadBalanceExceptionChecker)Util.getInstance(LoadBalanceExceptionChecker.class, lbExceptionChecker, null, null, null);
      this.exceptionChecker.init(props);
    } catch (CJException e) {
      throw SQLExceptionsMapping.translateException(e, null);
    } 
    pickNewConnection();
  }
  
  JdbcConnection getNewWrapperForThisAsConnection() throws SQLException {
    return new LoadBalancedMySQLConnection(this);
  }
  
  protected void propagateProxyDown(JdbcConnection proxyConn) {
    for (JdbcConnection c : this.liveConnections.values())
      c.setProxy(proxyConn); 
  }
  
  @Deprecated
  public boolean shouldExceptionTriggerFailover(Throwable t) {
    return shouldExceptionTriggerConnectionSwitch(t);
  }
  
  boolean shouldExceptionTriggerConnectionSwitch(Throwable t) {
    return (t instanceof SQLException && this.exceptionChecker.shouldExceptionTriggerFailover(t));
  }
  
  boolean isSourceConnection() {
    return true;
  }
  
  synchronized void invalidateConnection(JdbcConnection conn) throws SQLException {
    super.invalidateConnection(conn);
    if (isGlobalBlocklistEnabled()) {
      String host = this.connectionsToHostsMap.get(conn);
      if (host != null)
        addToGlobalBlocklist(host); 
    } 
    this.liveConnections.remove(this.connectionsToHostsMap.get(conn));
    Object mappedHost = this.connectionsToHostsMap.remove(conn);
    if (mappedHost != null && this.hostsToListIndexMap.containsKey(mappedHost)) {
      int hostIndex = ((Integer)this.hostsToListIndexMap.get(mappedHost)).intValue();
      synchronized (this.responseTimes) {
        this.responseTimes[hostIndex] = 0L;
      } 
    } 
  }
  
  public synchronized void pickNewConnection() throws SQLException {
    if (this.isClosed && this.closedExplicitly)
      return; 
    List<String> hostPortList = Collections.unmodifiableList((List<? extends String>)this.hostsList.stream().map(hi -> hi.getHostPortPair()).collect(Collectors.toList()));
    if (this.currentConnection == null) {
      this.currentConnection = this.balancer.pickConnection(this, hostPortList, (Map)Collections.unmodifiableMap(this.liveConnections), (long[])this.responseTimes
          .clone(), this.retriesAllDown);
      return;
    } 
    if (this.currentConnection.isClosed())
      invalidateCurrentConnection(); 
    int pingTimeout = ((Integer)this.currentConnection.getPropertySet().getIntegerProperty(PropertyKey.loadBalancePingTimeout).getValue()).intValue();
    boolean pingBeforeReturn = ((Boolean)this.currentConnection.getPropertySet().getBooleanProperty(PropertyKey.loadBalanceValidateConnectionOnSwapServer).getValue()).booleanValue();
    for (int hostsTried = 0, hostsToTry = this.hostsList.size(); hostsTried < hostsToTry; hostsTried++) {
      ConnectionImpl newConn = null;
      try {
        newConn = (ConnectionImpl)this.balancer.pickConnection(this, hostPortList, (Map)Collections.unmodifiableMap(this.liveConnections), (long[])this.responseTimes
            .clone(), this.retriesAllDown);
        if (this.currentConnection != null) {
          if (pingBeforeReturn)
            newConn.pingInternal(true, pingTimeout); 
          syncSessionState(this.currentConnection, (JdbcConnection)newConn);
        } 
        this.currentConnection = (JdbcConnection)newConn;
        return;
      } catch (SQLException e) {
        if (shouldExceptionTriggerConnectionSwitch(e) && newConn != null)
          invalidateConnection((JdbcConnection)newConn); 
      } 
    } 
    this.isClosed = true;
    this.closedReason = "Connection closed after inability to pick valid new connection during load-balance.";
  }
  
  public synchronized ConnectionImpl createConnectionForHost(HostInfo hostInfo) throws SQLException {
    ConnectionImpl conn = super.createConnectionForHost(hostInfo);
    this.liveConnections.put(hostInfo.getHostPortPair(), conn);
    this.connectionsToHostsMap.put(conn, hostInfo.getHostPortPair());
    removeFromGlobalBlocklist(hostInfo.getHostPortPair());
    this.totalPhysicalConnections++;
    for (QueryInterceptor stmtInterceptor : conn.getQueryInterceptorsInstances()) {
      if (stmtInterceptor instanceof LoadBalancedAutoCommitInterceptor) {
        ((LoadBalancedAutoCommitInterceptor)stmtInterceptor).resumeCounters();
        break;
      } 
    } 
    return conn;
  }
  
  void syncSessionState(JdbcConnection source, JdbcConnection target, boolean readOnly) throws SQLException {
    LoadBalancedAutoCommitInterceptor lbAutoCommitStmtInterceptor = null;
    for (QueryInterceptor stmtInterceptor : target.getQueryInterceptorsInstances()) {
      if (stmtInterceptor instanceof LoadBalancedAutoCommitInterceptor) {
        lbAutoCommitStmtInterceptor = (LoadBalancedAutoCommitInterceptor)stmtInterceptor;
        lbAutoCommitStmtInterceptor.pauseCounters();
        break;
      } 
    } 
    super.syncSessionState(source, target, readOnly);
    if (lbAutoCommitStmtInterceptor != null)
      lbAutoCommitStmtInterceptor.resumeCounters(); 
  }
  
  public synchronized ConnectionImpl createConnectionForHost(String hostPortPair) throws SQLException {
    for (HostInfo hi : this.hostsList) {
      if (hi.getHostPortPair().equals(hostPortPair))
        return createConnectionForHost(hi); 
    } 
    return null;
  }
  
  private synchronized void closeAllConnections() {
    for (Connection c : this.liveConnections.values()) {
      try {
        c.close();
      } catch (SQLException sQLException) {}
    } 
    if (!this.isClosed && 
      this.connectionGroup != null)
      this.connectionGroup.closeConnectionProxy(this); 
    this.liveConnections.clear();
    this.connectionsToHostsMap.clear();
  }
  
  synchronized void doClose() {
    closeAllConnections();
  }
  
  synchronized void doAbortInternal() {
    for (JdbcConnection c : this.liveConnections.values()) {
      try {
        c.abortInternal();
      } catch (SQLException sQLException) {}
    } 
    if (!this.isClosed && 
      this.connectionGroup != null)
      this.connectionGroup.closeConnectionProxy(this); 
    this.liveConnections.clear();
    this.connectionsToHostsMap.clear();
  }
  
  synchronized void doAbort(Executor executor) {
    for (Connection c : this.liveConnections.values()) {
      try {
        c.abort(executor);
      } catch (SQLException sQLException) {}
    } 
    if (!this.isClosed && 
      this.connectionGroup != null)
      this.connectionGroup.closeConnectionProxy(this); 
    this.liveConnections.clear();
    this.connectionsToHostsMap.clear();
  }
  
  Object invokeMore(Object proxy, Method method, Object[] args) throws Throwable {
    String methodName = method.getName();
    if (this.isClosed && !allowedOnClosedConnection(method) && (method.getExceptionTypes()).length > 0)
      if (this.autoReconnect && !this.closedExplicitly) {
        this.currentConnection = null;
        pickNewConnection();
        this.isClosed = false;
        this.closedReason = null;
      } else {
        String reason = "No operations allowed after connection closed.";
        if (this.closedReason != null)
          reason = reason + " " + this.closedReason; 
        for (Class<?> excls : method.getExceptionTypes()) {
          if (SQLException.class.isAssignableFrom(excls))
            throw SQLError.createSQLException(reason, "08003", null); 
        } 
        throw (CJCommunicationsException)ExceptionFactory.createException(CJCommunicationsException.class, reason);
      }  
    if (!this.inTransaction) {
      this.inTransaction = true;
      this.transactionStartTime = System.nanoTime();
      this.transactionCount++;
    } 
    Object result = null;
    try {
      result = method.invoke(this.thisAsConnection, args);
      if (result != null) {
        if (result instanceof JdbcStatement)
          ((JdbcStatement)result).setPingTarget(this); 
        result = proxyIfReturnTypeIsJdbcInterface(method.getReturnType(), result);
      } 
    } catch (InvocationTargetException e) {
      dealWithInvocationException(e);
    } finally {
      if ("commit".equals(methodName) || "rollback".equals(methodName)) {
        this.inTransaction = false;
        String host = this.connectionsToHostsMap.get(this.currentConnection);
        if (host != null)
          synchronized (this.responseTimes) {
            Integer hostIndex = this.hostsToListIndexMap.get(host);
            if (hostIndex != null && hostIndex.intValue() < this.responseTimes.length)
              this.responseTimes[hostIndex.intValue()] = System.nanoTime() - this.transactionStartTime; 
          }  
        pickNewConnection();
      } 
    } 
    return result;
  }
  
  public synchronized void doPing() throws SQLException {
    SQLException se = null;
    boolean foundHost = false;
    int pingTimeout = ((Integer)this.currentConnection.getPropertySet().getIntegerProperty(PropertyKey.loadBalancePingTimeout).getValue()).intValue();
    synchronized (this) {
      for (HostInfo hi : this.hostsList) {
        String host = hi.getHostPortPair();
        ConnectionImpl conn = this.liveConnections.get(host);
        if (conn == null)
          continue; 
        try {
          if (pingTimeout == 0) {
            conn.ping();
          } else {
            conn.pingInternal(true, pingTimeout);
          } 
          foundHost = true;
        } catch (SQLException e) {
          if (host.equals(this.connectionsToHostsMap.get(this.currentConnection))) {
            closeAllConnections();
            this.isClosed = true;
            this.closedReason = "Connection closed because ping of current connection failed.";
            throw e;
          } 
          if (e.getMessage().equals(Messages.getString("Connection.exceededConnectionLifetime"))) {
            if (se == null)
              se = e; 
          } else {
            se = e;
            if (isGlobalBlocklistEnabled())
              addToGlobalBlocklist(host); 
          } 
          this.liveConnections.remove(this.connectionsToHostsMap.get(conn));
        } 
      } 
    } 
    if (!foundHost) {
      closeAllConnections();
      this.isClosed = true;
      this.closedReason = "Connection closed due to inability to ping any active connections.";
      if (se != null)
        throw se; 
      ((ConnectionImpl)this.currentConnection).throwConnectionClosedException();
    } 
  }
  
  public void addToGlobalBlocklist(String host, long timeout) {
    if (isGlobalBlocklistEnabled())
      synchronized (globalBlocklist) {
        globalBlocklist.put(host, Long.valueOf(timeout));
      }  
  }
  
  public void removeFromGlobalBlocklist(String host) {
    if (isGlobalBlocklistEnabled() && globalBlocklist.containsKey(host))
      synchronized (globalBlocklist) {
        globalBlocklist.remove(host);
      }  
  }
  
  @Deprecated
  public void removeFromGlobalBlacklist(String host) {
    removeFromGlobalBlocklist(host);
  }
  
  @Deprecated
  public void addToGlobalBlacklist(String host, long timeout) {
    addToGlobalBlocklist(host, timeout);
  }
  
  public void addToGlobalBlocklist(String host) {
    addToGlobalBlocklist(host, System.currentTimeMillis() + this.globalBlocklistTimeout);
  }
  
  @Deprecated
  public void addToGlobalBlacklist(String host) {
    addToGlobalBlocklist(host);
  }
  
  public boolean isGlobalBlocklistEnabled() {
    return (this.globalBlocklistTimeout > 0);
  }
  
  @Deprecated
  public boolean isGlobalBlacklistEnabled() {
    return isGlobalBlocklistEnabled();
  }
  
  public synchronized Map<String, Long> getGlobalBlocklist() {
    if (!isGlobalBlocklistEnabled()) {
      if (this.hostsToRemove.isEmpty())
        return new HashMap<>(1); 
      HashMap<String, Long> fakedBlocklist = new HashMap<>();
      for (String h : this.hostsToRemove)
        fakedBlocklist.put(h, Long.valueOf(System.currentTimeMillis() + 5000L)); 
      return fakedBlocklist;
    } 
    Map<String, Long> blocklistClone = new HashMap<>(globalBlocklist.size());
    synchronized (globalBlocklist) {
      blocklistClone.putAll(globalBlocklist);
    } 
    Set<String> keys = blocklistClone.keySet();
    keys.retainAll((Collection)this.hostsList.stream().map(hi -> hi.getHostPortPair()).collect(Collectors.toList()));
    for (Iterator<String> i = keys.iterator(); i.hasNext(); ) {
      String host = i.next();
      Long timeout = globalBlocklist.get(host);
      if (timeout != null && timeout.longValue() < System.currentTimeMillis()) {
        synchronized (globalBlocklist) {
          globalBlocklist.remove(host);
        } 
        i.remove();
      } 
    } 
    if (keys.size() == this.hostsList.size())
      return new HashMap<>(1); 
    return blocklistClone;
  }
  
  @Deprecated
  public synchronized Map<String, Long> getGlobalBlacklist() {
    return getGlobalBlocklist();
  }
  
  public void removeHostWhenNotInUse(String hostPortPair) throws SQLException {
    if (this.hostRemovalGracePeriod <= 0) {
      removeHost(hostPortPair);
      return;
    } 
    int timeBetweenChecks = (this.hostRemovalGracePeriod > 1000) ? 1000 : this.hostRemovalGracePeriod;
    synchronized (this) {
      addToGlobalBlocklist(hostPortPair, System.currentTimeMillis() + this.hostRemovalGracePeriod + timeBetweenChecks);
      long cur = System.currentTimeMillis();
      while (System.currentTimeMillis() < cur + this.hostRemovalGracePeriod) {
        this.hostsToRemove.add(hostPortPair);
        if (!hostPortPair.equals(this.currentConnection.getHostPortPair())) {
          removeHost(hostPortPair);
          return;
        } 
        try {
          Thread.sleep(timeBetweenChecks);
        } catch (InterruptedException interruptedException) {}
      } 
    } 
    removeHost(hostPortPair);
  }
  
  public synchronized void removeHost(String hostPortPair) throws SQLException {
    if (this.connectionGroup != null && 
      this.connectionGroup.getInitialHosts().size() == 1 && this.connectionGroup.getInitialHosts().contains(hostPortPair))
      throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.0"), null); 
    this.hostsToRemove.add(hostPortPair);
    this.connectionsToHostsMap.remove(this.liveConnections.remove(hostPortPair));
    if (this.hostsToListIndexMap.remove(hostPortPair) != null) {
      long[] newResponseTimes = new long[this.responseTimes.length - 1];
      int newIdx = 0;
      for (HostInfo hostInfo : this.hostsList) {
        String host = hostInfo.getHostPortPair();
        if (!this.hostsToRemove.contains(host)) {
          Integer idx = this.hostsToListIndexMap.get(host);
          if (idx != null && idx.intValue() < this.responseTimes.length)
            newResponseTimes[newIdx] = this.responseTimes[idx.intValue()]; 
          this.hostsToListIndexMap.put(host, Integer.valueOf(newIdx++));
        } 
      } 
      this.responseTimes = newResponseTimes;
    } 
    if (hostPortPair.equals(this.currentConnection.getHostPortPair())) {
      invalidateConnection(this.currentConnection);
      pickNewConnection();
    } 
  }
  
  public synchronized boolean addHost(String hostPortPair) {
    if (this.hostsToListIndexMap.containsKey(hostPortPair))
      return false; 
    long[] newResponseTimes = new long[this.responseTimes.length + 1];
    System.arraycopy(this.responseTimes, 0, newResponseTimes, 0, this.responseTimes.length);
    this.responseTimes = newResponseTimes;
    if (this.hostsList.stream().noneMatch(hi -> hostPortPair.equals(hi.getHostPortPair())))
      this.hostsList.add(this.connectionUrl.getHostOrSpawnIsolated(hostPortPair)); 
    this.hostsToListIndexMap.put(hostPortPair, Integer.valueOf(this.responseTimes.length - 1));
    this.hostsToRemove.remove(hostPortPair);
    return true;
  }
  
  public synchronized boolean inTransaction() {
    return this.inTransaction;
  }
  
  public synchronized long getTransactionCount() {
    return this.transactionCount;
  }
  
  public synchronized long getActivePhysicalConnectionCount() {
    return this.liveConnections.size();
  }
  
  public synchronized long getTotalPhysicalConnectionCount() {
    return this.totalPhysicalConnections;
  }
  
  public synchronized long getConnectionGroupProxyID() {
    return this.connectionGroupProxyID;
  }
  
  public synchronized String getCurrentActiveHost() {
    JdbcConnection c = this.currentConnection;
    if (c != null) {
      Object o = this.connectionsToHostsMap.get(c);
      if (o != null)
        return o.toString(); 
    } 
    return null;
  }
  
  public synchronized long getCurrentTransactionDuration() {
    if (this.inTransaction && this.transactionStartTime > 0L)
      return System.nanoTime() - this.transactionStartTime; 
    return 0L;
  }
  
  private static class NullLoadBalancedConnectionProxy implements InvocationHandler {
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      SQLException exceptionToThrow = SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.unusableConnection"), "25000", 1000001, true, null);
      Class<?>[] declaredException = method.getExceptionTypes();
      for (Class<?> declEx : declaredException) {
        if (declEx.isAssignableFrom(exceptionToThrow.getClass()))
          throw exceptionToThrow; 
      } 
      throw new IllegalStateException(exceptionToThrow.getMessage(), exceptionToThrow);
    }
  }
  
  private static LoadBalancedConnection nullLBConnectionInstance = null;
  
  static synchronized LoadBalancedConnection getNullLoadBalancedConnectionInstance() {
    if (nullLBConnectionInstance == null)
      nullLBConnectionInstance = (LoadBalancedConnection)Proxy.newProxyInstance(LoadBalancedConnection.class.getClassLoader(), INTERFACES_TO_PROXY, new NullLoadBalancedConnectionProxy()); 
    return nullLBConnectionInstance;
  }
}
