package com.mysql.cj.jdbc.ha;

import com.mysql.cj.Messages;
import com.mysql.cj.PingTarget;
import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.HostsListView;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.url.LoadBalanceConnectionUrl;
import com.mysql.cj.conf.url.ReplicationConnectionUrl;
import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.JdbcStatement;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.util.StringUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

public class ReplicationConnectionProxy extends MultiHostConnectionProxy implements PingTarget {
  private ReplicationConnection thisAsReplicationConnection;
  
  protected boolean enableJMX = false;
  
  protected boolean allowSourceDownConnections = false;
  
  protected boolean allowReplicaDownConnections = false;
  
  protected boolean readFromSourceWhenNoReplicas = false;
  
  protected boolean readFromSourceWhenNoReplicasOriginal = false;
  
  protected boolean readOnly = false;
  
  ReplicationConnectionGroup connectionGroup;
  
  private long connectionGroupID = -1L;
  
  private List<HostInfo> sourceHosts;
  
  protected LoadBalancedConnection sourceConnection;
  
  private List<HostInfo> replicaHosts;
  
  protected LoadBalancedConnection replicasConnection;
  
  public static ReplicationConnection createProxyInstance(ConnectionUrl connectionUrl) throws SQLException {
    ReplicationConnectionProxy connProxy = new ReplicationConnectionProxy(connectionUrl);
    return (ReplicationConnection)Proxy.newProxyInstance(ReplicationConnection.class.getClassLoader(), new Class[] { ReplicationConnection.class, JdbcConnection.class }, connProxy);
  }
  
  private ReplicationConnectionProxy(ConnectionUrl connectionUrl) throws SQLException {
    Properties props = connectionUrl.getConnectionArgumentsAsProperties();
    this.thisAsReplicationConnection = (ReplicationConnection)this.thisAsConnection;
    this.connectionUrl = connectionUrl;
    String enableJMXAsString = props.getProperty(PropertyKey.ha_enableJMX.getKeyName(), "false");
    try {
      this.enableJMX = Boolean.parseBoolean(enableJMXAsString);
    } catch (Exception e) {
      throw SQLError.createSQLException(Messages.getString("MultihostConnection.badValueForHaEnableJMX", new Object[] { enableJMXAsString }), "S1009", null);
    } 
    String allowSourceDownConnectionsAsString = props.getProperty(PropertyKey.allowSourceDownConnections.getKeyName(), "false");
    try {
      this.allowSourceDownConnections = Boolean.parseBoolean(allowSourceDownConnectionsAsString);
    } catch (Exception e) {
      throw SQLError.createSQLException(
          Messages.getString("ReplicationConnectionProxy.badValueForAllowSourceDownConnections", new Object[] { enableJMXAsString }), "S1009", null);
    } 
    String allowReplicaDownConnectionsAsString = props.getProperty(PropertyKey.allowReplicaDownConnections.getKeyName(), "false");
    try {
      this.allowReplicaDownConnections = Boolean.parseBoolean(allowReplicaDownConnectionsAsString);
    } catch (Exception e) {
      throw SQLError.createSQLException(Messages.getString("ReplicationConnectionProxy.badValueForAllowReplicaDownConnections", new Object[] { allowReplicaDownConnectionsAsString }), "S1009", null);
    } 
    String readFromSourceWhenNoReplicasAsString = props.getProperty(PropertyKey.readFromSourceWhenNoReplicas.getKeyName());
    try {
      this.readFromSourceWhenNoReplicasOriginal = Boolean.parseBoolean(readFromSourceWhenNoReplicasAsString);
    } catch (Exception e) {
      throw SQLError.createSQLException(Messages.getString("ReplicationConnectionProxy.badValueForReadFromSourceWhenNoReplicas", new Object[] { readFromSourceWhenNoReplicasAsString }), "S1009", null);
    } 
    String group = props.getProperty(PropertyKey.replicationConnectionGroup.getKeyName(), null);
    if (!StringUtils.isNullOrEmpty(group) && ReplicationConnectionUrl.class.isAssignableFrom(connectionUrl.getClass())) {
      this.connectionGroup = ReplicationConnectionGroupManager.getConnectionGroupInstance(group);
      if (this.enableJMX)
        ReplicationConnectionGroupManager.registerJmx(); 
      this.connectionGroupID = this.connectionGroup.registerReplicationConnection(this.thisAsReplicationConnection, ((ReplicationConnectionUrl)connectionUrl)
          .getSourcesListAsHostPortPairs(), ((ReplicationConnectionUrl)connectionUrl)
          .getReplicasListAsHostPortPairs());
      this.sourceHosts = ((ReplicationConnectionUrl)connectionUrl).getSourceHostsListFromHostPortPairs(this.connectionGroup.getSourceHosts());
      this.replicaHosts = ((ReplicationConnectionUrl)connectionUrl).getReplicaHostsListFromHostPortPairs(this.connectionGroup.getReplicaHosts());
    } else {
      this.sourceHosts = new ArrayList<>(connectionUrl.getHostsList(HostsListView.SOURCES));
      this.replicaHosts = new ArrayList<>(connectionUrl.getHostsList(HostsListView.REPLICAS));
    } 
    resetReadFromSourceWhenNoReplicas();
    try {
      initializeReplicasConnection();
    } catch (SQLException e) {
      if (!this.allowReplicaDownConnections) {
        if (this.connectionGroup != null)
          this.connectionGroup.handleCloseConnection(this.thisAsReplicationConnection); 
        throw e;
      } 
    } 
    SQLException exCaught = null;
    try {
      this.currentConnection = initializeSourceConnection();
    } catch (SQLException e) {
      exCaught = e;
    } 
    if (this.currentConnection == null)
      if (this.allowSourceDownConnections && this.replicasConnection != null) {
        this.readOnly = true;
        this.currentConnection = this.replicasConnection;
      } else {
        if (this.connectionGroup != null)
          this.connectionGroup.handleCloseConnection(this.thisAsReplicationConnection); 
        if (exCaught != null)
          throw exCaught; 
        throw SQLError.createSQLException(Messages.getString("ReplicationConnectionProxy.initializationWithEmptyHostsLists"), "S1009", null);
      }  
  }
  
  JdbcConnection getNewWrapperForThisAsConnection() throws SQLException {
    return new ReplicationMySQLConnection(this);
  }
  
  protected void propagateProxyDown(JdbcConnection proxyConn) {
    if (this.sourceConnection != null)
      this.sourceConnection.setProxy(proxyConn); 
    if (this.replicasConnection != null)
      this.replicasConnection.setProxy(proxyConn); 
  }
  
  boolean shouldExceptionTriggerConnectionSwitch(Throwable t) {
    return false;
  }
  
  public boolean isSourceConnection() {
    return (this.currentConnection != null && this.currentConnection == this.sourceConnection);
  }
  
  public boolean isReplicasConnection() {
    return (this.currentConnection != null && this.currentConnection == this.replicasConnection);
  }
  
  @Deprecated
  public boolean isSlavesConnection() {
    return isReplicasConnection();
  }
  
  void pickNewConnection() throws SQLException {}
  
  void syncSessionState(JdbcConnection source, JdbcConnection target, boolean readonly) throws SQLException {
    try {
      super.syncSessionState(source, target, readonly);
    } catch (SQLException e1) {
      try {
        super.syncSessionState(source, target, readonly);
      } catch (SQLException sQLException) {}
    } 
  }
  
  void doClose() throws SQLException {
    if (this.sourceConnection != null)
      this.sourceConnection.close(); 
    if (this.replicasConnection != null)
      this.replicasConnection.close(); 
    if (this.connectionGroup != null)
      this.connectionGroup.handleCloseConnection(this.thisAsReplicationConnection); 
  }
  
  void doAbortInternal() throws SQLException {
    this.sourceConnection.abortInternal();
    this.replicasConnection.abortInternal();
    if (this.connectionGroup != null)
      this.connectionGroup.handleCloseConnection(this.thisAsReplicationConnection); 
  }
  
  void doAbort(Executor executor) throws SQLException {
    this.sourceConnection.abort(executor);
    this.replicasConnection.abort(executor);
    if (this.connectionGroup != null)
      this.connectionGroup.handleCloseConnection(this.thisAsReplicationConnection); 
  }
  
  Object invokeMore(Object proxy, Method method, Object[] args) throws Throwable {
    checkConnectionCapabilityForMethod(method);
    boolean invokeAgain = false;
    while (true) {
      try {
        Object result = method.invoke(this.thisAsConnection, args);
        if (result != null && result instanceof JdbcStatement)
          ((JdbcStatement)result).setPingTarget(this); 
        return result;
      } catch (InvocationTargetException e) {
        if (invokeAgain) {
          invokeAgain = false;
        } else if (e.getCause() != null && e.getCause() instanceof SQLException && ((SQLException)e
          .getCause()).getSQLState() == "25000" && ((SQLException)e
          .getCause()).getErrorCode() == 1000001) {
          try {
            setReadOnly(this.readOnly);
            invokeAgain = true;
          } catch (SQLException sQLException) {}
        } 
        if (!invokeAgain)
          throw e; 
      } 
    } 
  }
  
  private void checkConnectionCapabilityForMethod(Method method) throws Throwable {
    if (this.sourceHosts.isEmpty() && this.replicaHosts.isEmpty() && !ReplicationConnection.class.isAssignableFrom(method.getDeclaringClass()))
      throw SQLError.createSQLException(Messages.getString("ReplicationConnectionProxy.noHostsInconsistentState"), "25000", 1000002, true, null); 
  }
  
  public void doPing() throws SQLException {
    boolean isSourceConn = isSourceConnection();
    SQLException sourcesPingException = null;
    SQLException replicasPingException = null;
    if (this.sourceConnection != null) {
      try {
        this.sourceConnection.ping();
      } catch (SQLException e) {
        sourcesPingException = e;
      } 
    } else {
      initializeSourceConnection();
    } 
    if (this.replicasConnection != null) {
      try {
        this.replicasConnection.ping();
      } catch (SQLException e) {
        replicasPingException = e;
      } 
    } else {
      try {
        initializeReplicasConnection();
        if (switchToReplicasConnectionIfNecessary())
          isSourceConn = false; 
      } catch (SQLException e) {
        if (this.sourceConnection == null || !this.readFromSourceWhenNoReplicas)
          throw e; 
      } 
    } 
    if (isSourceConn && sourcesPingException != null) {
      if (this.replicasConnection != null && replicasPingException == null) {
        this.sourceConnection = null;
        this.currentConnection = this.replicasConnection;
        this.readOnly = true;
      } 
      throw sourcesPingException;
    } 
    if (!isSourceConn && (replicasPingException != null || this.replicasConnection == null)) {
      if (this.sourceConnection != null && this.readFromSourceWhenNoReplicas && sourcesPingException == null) {
        this.replicasConnection = null;
        this.currentConnection = this.sourceConnection;
        this.readOnly = true;
        this.currentConnection.setReadOnly(true);
      } 
      if (replicasPingException != null)
        throw replicasPingException; 
    } 
  }
  
  private JdbcConnection initializeSourceConnection() throws SQLException {
    this.sourceConnection = null;
    if (this.sourceHosts.size() == 0)
      return null; 
    LoadBalancedConnection newSourceConn = LoadBalancedConnectionProxy.createProxyInstance((ConnectionUrl)new LoadBalanceConnectionUrl(this.sourceHosts, this.connectionUrl.getOriginalProperties()));
    newSourceConn.setProxy(getProxy());
    this.sourceConnection = newSourceConn;
    return this.sourceConnection;
  }
  
  private JdbcConnection initializeReplicasConnection() throws SQLException {
    this.replicasConnection = null;
    if (this.replicaHosts.size() == 0)
      return null; 
    LoadBalancedConnection newReplicasConn = LoadBalancedConnectionProxy.createProxyInstance((ConnectionUrl)new LoadBalanceConnectionUrl(this.replicaHosts, this.connectionUrl.getOriginalProperties()));
    newReplicasConn.setProxy(getProxy());
    newReplicasConn.setReadOnly(true);
    this.replicasConnection = newReplicasConn;
    return this.replicasConnection;
  }
  
  private synchronized boolean switchToSourceConnection() throws SQLException {
    if (this.sourceConnection == null || this.sourceConnection.isClosed())
      try {
        if (initializeSourceConnection() == null)
          return false; 
      } catch (SQLException e) {
        this.currentConnection = null;
        throw e;
      }  
    if (!isSourceConnection() && this.sourceConnection != null) {
      syncSessionState(this.currentConnection, this.sourceConnection, false);
      this.currentConnection = this.sourceConnection;
    } 
    return true;
  }
  
  private synchronized boolean switchToReplicasConnection() throws SQLException {
    if (this.replicasConnection == null || this.replicasConnection.isClosed())
      try {
        if (initializeReplicasConnection() == null)
          return false; 
      } catch (SQLException e) {
        this.currentConnection = null;
        throw e;
      }  
    if (!isReplicasConnection() && this.replicasConnection != null) {
      syncSessionState(this.currentConnection, this.replicasConnection, true);
      this.currentConnection = this.replicasConnection;
    } 
    return true;
  }
  
  private boolean switchToReplicasConnectionIfNecessary() throws SQLException {
    if (this.currentConnection == null || (isSourceConnection() && (this.readOnly || (this.sourceHosts.isEmpty() && this.currentConnection.isClosed()))) || (
      !isSourceConnection() && this.currentConnection.isClosed()))
      return switchToReplicasConnection(); 
    return false;
  }
  
  public synchronized JdbcConnection getCurrentConnection() {
    return (this.currentConnection == null) ? LoadBalancedConnectionProxy.getNullLoadBalancedConnectionInstance() : this.currentConnection;
  }
  
  public long getConnectionGroupId() {
    return this.connectionGroupID;
  }
  
  public synchronized JdbcConnection getSourceConnection() {
    return this.sourceConnection;
  }
  
  @Deprecated
  public synchronized JdbcConnection getMasterConnection() {
    return getSourceConnection();
  }
  
  public synchronized void promoteReplicaToSource(String hostPortPair) throws SQLException {
    HostInfo host = getReplicaHost(hostPortPair);
    if (host == null)
      return; 
    this.sourceHosts.add(host);
    removeReplica(hostPortPair);
    if (this.sourceConnection != null)
      this.sourceConnection.addHost(hostPortPair); 
    if (!this.readOnly && !isSourceConnection())
      switchToSourceConnection(); 
  }
  
  @Deprecated
  public synchronized void promoteSlaveToMaster(String hostPortPair) throws SQLException {
    promoteReplicaToSource(hostPortPair);
  }
  
  public synchronized void removeSourceHost(String hostPortPair) throws SQLException {
    removeSourceHost(hostPortPair, true);
  }
  
  @Deprecated
  public synchronized void removeMasterHost(String hostPortPair) throws SQLException {
    removeSourceHost(hostPortPair);
  }
  
  public synchronized void removeSourceHost(String hostPortPair, boolean waitUntilNotInUse) throws SQLException {
    removeSourceHost(hostPortPair, waitUntilNotInUse, false);
  }
  
  @Deprecated
  public synchronized void removeMasterHost(String hostPortPair, boolean waitUntilNotInUse) throws SQLException {
    removeSourceHost(hostPortPair, waitUntilNotInUse);
  }
  
  public synchronized void removeSourceHost(String hostPortPair, boolean waitUntilNotInUse, boolean isNowReplica) throws SQLException {
    HostInfo host = getSourceHost(hostPortPair);
    if (host == null)
      return; 
    if (isNowReplica) {
      this.replicaHosts.add(host);
      resetReadFromSourceWhenNoReplicas();
    } 
    this.sourceHosts.remove(host);
    if (this.sourceConnection == null || this.sourceConnection.isClosed()) {
      this.sourceConnection = null;
      return;
    } 
    if (waitUntilNotInUse) {
      this.sourceConnection.removeHostWhenNotInUse(hostPortPair);
    } else {
      this.sourceConnection.removeHost(hostPortPair);
    } 
    if (this.sourceHosts.isEmpty()) {
      this.sourceConnection.close();
      this.sourceConnection = null;
      switchToReplicasConnectionIfNecessary();
    } 
  }
  
  @Deprecated
  public synchronized void removeMasterHost(String hostPortPair, boolean waitUntilNotInUse, boolean isNowReplica) throws SQLException {
    removeSourceHost(hostPortPair, waitUntilNotInUse, isNowReplica);
  }
  
  public boolean isHostSource(String hostPortPair) {
    if (hostPortPair == null)
      return false; 
    return this.sourceHosts.stream().anyMatch(hi -> hostPortPair.equalsIgnoreCase(hi.getHostPortPair()));
  }
  
  @Deprecated
  public boolean isHostMaster(String hostPortPair) {
    return isHostSource(hostPortPair);
  }
  
  public synchronized JdbcConnection getReplicasConnection() {
    return this.replicasConnection;
  }
  
  @Deprecated
  public synchronized JdbcConnection getSlavesConnection() {
    return getReplicasConnection();
  }
  
  public synchronized void addReplicaHost(String hostPortPair) throws SQLException {
    if (isHostReplica(hostPortPair))
      return; 
    this.replicaHosts.add(getConnectionUrl().getReplicaHostOrSpawnIsolated(hostPortPair));
    resetReadFromSourceWhenNoReplicas();
    if (this.replicasConnection == null) {
      initializeReplicasConnection();
      switchToReplicasConnectionIfNecessary();
    } else {
      this.replicasConnection.addHost(hostPortPair);
    } 
  }
  
  @Deprecated
  public synchronized void addSlaveHost(String hostPortPair) throws SQLException {
    addReplicaHost(hostPortPair);
  }
  
  public synchronized void removeReplica(String hostPortPair) throws SQLException {
    removeReplica(hostPortPair, true);
  }
  
  @Deprecated
  public synchronized void removeSlave(String hostPortPair) throws SQLException {
    removeReplica(hostPortPair);
  }
  
  public synchronized void removeReplica(String hostPortPair, boolean closeGently) throws SQLException {
    HostInfo host = getReplicaHost(hostPortPair);
    if (host == null)
      return; 
    this.replicaHosts.remove(host);
    resetReadFromSourceWhenNoReplicas();
    if (this.replicasConnection == null || this.replicasConnection.isClosed()) {
      this.replicasConnection = null;
      return;
    } 
    if (closeGently) {
      this.replicasConnection.removeHostWhenNotInUse(hostPortPair);
    } else {
      this.replicasConnection.removeHost(hostPortPair);
    } 
    if (this.replicaHosts.isEmpty()) {
      this.replicasConnection.close();
      this.replicasConnection = null;
      switchToSourceConnection();
      if (isSourceConnection())
        this.currentConnection.setReadOnly(this.readOnly); 
    } 
  }
  
  @Deprecated
  public synchronized void removeSlave(String hostPortPair, boolean closeGently) throws SQLException {
    removeReplica(hostPortPair, closeGently);
  }
  
  public boolean isHostReplica(String hostPortPair) {
    if (hostPortPair == null)
      return false; 
    return this.replicaHosts.stream().anyMatch(hi -> hostPortPair.equalsIgnoreCase(hi.getHostPortPair()));
  }
  
  @Deprecated
  public boolean isHostSlave(String hostPortPair) {
    return isHostReplica(hostPortPair);
  }
  
  public synchronized void setReadOnly(boolean readOnly) throws SQLException {
    if (readOnly) {
      if (!isReplicasConnection() || this.currentConnection.isClosed()) {
        boolean switched = true;
        SQLException exceptionCaught = null;
        try {
          switched = switchToReplicasConnection();
        } catch (SQLException e) {
          switched = false;
          exceptionCaught = e;
        } 
        if (!switched && this.readFromSourceWhenNoReplicas && switchToSourceConnection())
          exceptionCaught = null; 
        if (exceptionCaught != null)
          throw exceptionCaught; 
      } 
    } else if (!isSourceConnection() || this.currentConnection.isClosed()) {
      boolean switched = true;
      SQLException exceptionCaught = null;
      try {
        switched = switchToSourceConnection();
      } catch (SQLException e) {
        switched = false;
        exceptionCaught = e;
      } 
      if (!switched && switchToReplicasConnectionIfNecessary())
        exceptionCaught = null; 
      if (exceptionCaught != null)
        throw exceptionCaught; 
    } 
    this.readOnly = readOnly;
    if (this.readFromSourceWhenNoReplicas && isSourceConnection())
      this.currentConnection.setReadOnly(this.readOnly); 
  }
  
  public boolean isReadOnly() throws SQLException {
    return (!isSourceConnection() || this.readOnly);
  }
  
  private void resetReadFromSourceWhenNoReplicas() {
    this.readFromSourceWhenNoReplicas = (this.replicaHosts.isEmpty() || this.readFromSourceWhenNoReplicasOriginal);
  }
  
  private HostInfo getSourceHost(String hostPortPair) {
    return this.sourceHosts.stream().filter(hi -> hostPortPair.equalsIgnoreCase(hi.getHostPortPair())).findFirst().orElse(null);
  }
  
  private HostInfo getReplicaHost(String hostPortPair) {
    return this.replicaHosts.stream().filter(hi -> hostPortPair.equalsIgnoreCase(hi.getHostPortPair())).findFirst().orElse(null);
  }
  
  private ReplicationConnectionUrl getConnectionUrl() {
    return (ReplicationConnectionUrl)this.connectionUrl;
  }
}
