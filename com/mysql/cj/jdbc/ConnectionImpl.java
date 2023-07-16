package com.mysql.cj.jdbc;

import com.mysql.cj.CacheAdapter;
import com.mysql.cj.CacheAdapterFactory;
import com.mysql.cj.LicenseConfiguration;
import com.mysql.cj.Messages;
import com.mysql.cj.NativeSession;
import com.mysql.cj.NoSubInterceptorWrapper;
import com.mysql.cj.PreparedQuery;
import com.mysql.cj.QueryInfo;
import com.mysql.cj.ServerVersion;
import com.mysql.cj.Session;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.ExceptionInterceptorChain;
import com.mysql.cj.exceptions.UnableToConnectException;
import com.mysql.cj.interceptors.QueryInterceptor;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.jdbc.ha.MultiHostMySQLConnection;
import com.mysql.cj.jdbc.interceptors.ConnectionLifecycleInterceptor;
import com.mysql.cj.jdbc.result.CachedResultSetMetaData;
import com.mysql.cj.jdbc.result.CachedResultSetMetaDataImpl;
import com.mysql.cj.jdbc.result.ResultSetFactory;
import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
import com.mysql.cj.jdbc.result.UpdatableResultSet;
import com.mysql.cj.log.StandardLogger;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.ProtocolEntityFactory;
import com.mysql.cj.protocol.ServerSessionStateController;
import com.mysql.cj.protocol.SocksProxySocketFactory;
import com.mysql.cj.protocol.a.NativeProtocol;
import com.mysql.cj.util.LRUCache;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.util.Util;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLPermission;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ConnectionImpl implements JdbcConnection, Session.SessionEventListener, Serializable {
  private static final long serialVersionUID = 4009476458425101761L;
  
  private static final SQLPermission SET_NETWORK_TIMEOUT_PERM = new SQLPermission("setNetworkTimeout");
  
  private static final SQLPermission ABORT_PERM = new SQLPermission("abort");
  
  public String getHost() {
    return this.session.getHostInfo().getHost();
  }
  
  private JdbcConnection parentProxy = null;
  
  private JdbcConnection topProxy = null;
  
  private InvocationHandler realProxy = null;
  
  public boolean isProxySet() {
    return (this.topProxy != null);
  }
  
  public void setProxy(JdbcConnection proxy) {
    if (this.parentProxy == null)
      this.parentProxy = proxy; 
    this.topProxy = proxy;
    this.realProxy = (this.topProxy instanceof MultiHostMySQLConnection) ? (InvocationHandler)((MultiHostMySQLConnection)proxy).getThisAsProxy() : null;
  }
  
  private JdbcConnection getProxy() {
    return (this.topProxy != null) ? this.topProxy : this;
  }
  
  public JdbcConnection getMultiHostSafeProxy() {
    return getProxy();
  }
  
  public JdbcConnection getMultiHostParentProxy() {
    return this.parentProxy;
  }
  
  public JdbcConnection getActiveMySQLConnection() {
    return this;
  }
  
  public Object getConnectionMutex() {
    return (this.realProxy != null) ? this.realProxy : getProxy();
  }
  
  static class CompoundCacheKey {
    final String componentOne;
    
    final String componentTwo;
    
    final int hashCode;
    
    CompoundCacheKey(String partOne, String partTwo) {
      this.componentOne = partOne;
      this.componentTwo = partTwo;
      int hc = 17;
      hc = 31 * hc + ((this.componentOne != null) ? this.componentOne.hashCode() : 0);
      hc = 31 * hc + ((this.componentTwo != null) ? this.componentTwo.hashCode() : 0);
      this.hashCode = hc;
    }
    
    public boolean equals(Object obj) {
      if (this == obj)
        return true; 
      if (obj != null && CompoundCacheKey.class.isAssignableFrom(obj.getClass())) {
        CompoundCacheKey another = (CompoundCacheKey)obj;
        if ((this.componentOne == null) ? (another.componentOne == null) : this.componentOne.equals(another.componentOne))
          return (this.componentTwo == null) ? ((another.componentTwo == null)) : this.componentTwo.equals(another.componentTwo); 
      } 
      return false;
    }
    
    public int hashCode() {
      return this.hashCode;
    }
  }
  
  protected static final String DEFAULT_LOGGER_CLASS = StandardLogger.class.getName();
  
  private static Map<String, Integer> mapTransIsolationNameToValue = null;
  
  protected static Map<?, ?> roundRobinStatsMap;
  
  private List<ConnectionLifecycleInterceptor> connectionLifecycleInterceptors;
  
  private static final int DEFAULT_RESULT_SET_TYPE = 1003;
  
  private static final int DEFAULT_RESULT_SET_CONCURRENCY = 1007;
  
  private static final Random random;
  
  private CacheAdapter<String, QueryInfo> queryInfoCache;
  
  static {
    mapTransIsolationNameToValue = new HashMap<>(8);
    mapTransIsolationNameToValue.put("READ-UNCOMMITED", Integer.valueOf(1));
    mapTransIsolationNameToValue.put("READ-UNCOMMITTED", Integer.valueOf(1));
    mapTransIsolationNameToValue.put("READ-COMMITTED", Integer.valueOf(2));
    mapTransIsolationNameToValue.put("REPEATABLE-READ", Integer.valueOf(4));
    mapTransIsolationNameToValue.put("SERIALIZABLE", Integer.valueOf(8));
    random = new Random();
  }
  
  public static JdbcConnection getInstance(HostInfo hostInfo) throws SQLException {
    return new ConnectionImpl(hostInfo);
  }
  
  protected static synchronized int getNextRoundRobinHostIndex(String url, List<?> hostList) {
    int indexRange = hostList.size();
    int index = random.nextInt(indexRange);
    return index;
  }
  
  private static boolean nullSafeCompare(String s1, String s2) {
    if (s1 == null && s2 == null)
      return true; 
    if (s1 == null && s2 != null)
      return false; 
    return (s1 != null && s1.equals(s2));
  }
  
  private String database = null;
  
  private DatabaseMetaData dbmd = null;
  
  private NativeSession session = null;
  
  private boolean isInGlobalTx = false;
  
  private int isolationLevel = 2;
  
  private final CopyOnWriteArrayList<JdbcStatement> openStatements = new CopyOnWriteArrayList<>();
  
  private LRUCache<CompoundCacheKey, CallableStatement.CallableStatementParamInfo> parsedCallableStatementCache;
  
  private String password = null;
  
  protected Properties props = null;
  
  private boolean readOnly = false;
  
  protected LRUCache<String, CachedResultSetMetaData> resultSetMetadataCache;
  
  private Map<String, Class<?>> typeMap;
  
  private String user = null;
  
  private LRUCache<String, Boolean> serverSideStatementCheckCache;
  
  private LRUCache<CompoundCacheKey, ServerPreparedStatement> serverSideStatementCache;
  
  private HostInfo origHostInfo;
  
  private String origHostToConnectTo;
  
  private int origPortToConnectTo;
  
  private boolean hasTriedSourceFlag = false;
  
  private List<QueryInterceptor> queryInterceptors;
  
  protected JdbcPropertySet propertySet;
  
  private RuntimeProperty<Boolean> autoReconnectForPools;
  
  private RuntimeProperty<Boolean> cachePrepStmts;
  
  private RuntimeProperty<Boolean> autoReconnect;
  
  private RuntimeProperty<Boolean> useUsageAdvisor;
  
  private RuntimeProperty<Boolean> reconnectAtTxEnd;
  
  private RuntimeProperty<Boolean> emulateUnsupportedPstmts;
  
  private RuntimeProperty<Boolean> ignoreNonTxTables;
  
  private RuntimeProperty<Boolean> pedantic;
  
  private RuntimeProperty<Integer> prepStmtCacheSqlLimit;
  
  private RuntimeProperty<Boolean> useLocalSessionState;
  
  private RuntimeProperty<Boolean> useServerPrepStmts;
  
  private RuntimeProperty<Boolean> processEscapeCodesForPrepStmts;
  
  private RuntimeProperty<Boolean> useLocalTransactionState;
  
  private RuntimeProperty<Boolean> disconnectOnExpiredPasswords;
  
  private RuntimeProperty<Boolean> readOnlyPropagatesToServer;
  
  protected ResultSetFactory nullStatementResultSetFactory;
  
  private int autoIncrementIncrement;
  
  private ExceptionInterceptor exceptionInterceptor;
  
  private ClientInfoProvider infoProvider;
  
  public JdbcPropertySet getPropertySet() {
    return this.propertySet;
  }
  
  public void unSafeQueryInterceptors() throws SQLException {
    try {
      this
        .queryInterceptors = (List<QueryInterceptor>)this.queryInterceptors.stream().map(NoSubInterceptorWrapper.class::cast).map(NoSubInterceptorWrapper::getUnderlyingInterceptor).collect(Collectors.toCollection(java.util.LinkedList::new));
      if (this.session != null)
        this.session.setQueryInterceptors(this.queryInterceptors); 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void initializeSafeQueryInterceptors() throws SQLException {
    try {
      this
        
        .queryInterceptors = (List<QueryInterceptor>)Util.loadClasses(QueryInterceptor.class, this.propertySet.getStringProperty(PropertyKey.queryInterceptors).getStringValue(), "MysqlIo.BadQueryInterceptor", getExceptionInterceptor()).stream().map(o -> new NoSubInterceptorWrapper(o.init(this, this.props, this.session.getLog()))).collect(Collectors.toCollection(java.util.LinkedList::new));
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public List<QueryInterceptor> getQueryInterceptorsInstances() {
    return this.queryInterceptors;
  }
  
  private boolean canHandleAsServerPreparedStatement(String sql) throws SQLException {
    if (sql == null || sql.length() == 0)
      return true; 
    if (!((Boolean)this.useServerPrepStmts.getValue()).booleanValue())
      return false; 
    boolean allowMultiQueries = ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.allowMultiQueries).getValue()).booleanValue();
    if (((Boolean)this.cachePrepStmts.getValue()).booleanValue())
      synchronized (this.serverSideStatementCheckCache) {
        Boolean flag = (Boolean)this.serverSideStatementCheckCache.get(sql);
        if (flag != null)
          return flag.booleanValue(); 
        boolean canHandle = StringUtils.canHandleAsServerPreparedStatementNoCache(sql, getServerVersion(), allowMultiQueries, this.session
            .getServerSession().isNoBackslashEscapesSet(), this.session.getServerSession().useAnsiQuotedIdentifiers());
        if (sql.length() < ((Integer)this.prepStmtCacheSqlLimit.getValue()).intValue())
          this.serverSideStatementCheckCache.put(sql, canHandle ? Boolean.TRUE : Boolean.FALSE); 
        return canHandle;
      }  
    return StringUtils.canHandleAsServerPreparedStatementNoCache(sql, getServerVersion(), allowMultiQueries, this.session
        .getServerSession().isNoBackslashEscapesSet(), this.session.getServerSession().useAnsiQuotedIdentifiers());
  }
  
  public void changeUser(String userName, String newPassword) throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        checkClosed();
        if (userName == null || userName.equals(""))
          userName = ""; 
        if (newPassword == null)
          newPassword = ""; 
        try {
          this.session.changeUser(userName, newPassword, this.database);
        } catch (CJException ex) {
          if ("28000".equals(ex.getSQLState()))
            cleanup((Throwable)ex); 
          throw ex;
        } 
        this.user = userName;
        this.password = newPassword;
        this.session.getServerSession().getCharsetSettings().configurePostHandshake(true);
        this.session.setSessionVariables();
        setupServerForTruncationChecks();
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void checkClosed() {
    this.session.checkClosed();
  }
  
  public void throwConnectionClosedException() throws SQLException {
    try {
      SQLException ex = SQLError.createSQLException(Messages.getString("Connection.2"), "08003", 
          getExceptionInterceptor());
      if (this.session.getForceClosedReason() != null)
        ex.initCause(this.session.getForceClosedReason()); 
      throw ex;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private void checkTransactionIsolationLevel() {
    String s = this.session.getServerSession().getServerVariable("transaction_isolation");
    if (s == null)
      s = this.session.getServerSession().getServerVariable("tx_isolation"); 
    if (s != null) {
      Integer intTI = mapTransIsolationNameToValue.get(s);
      if (intTI != null)
        this.isolationLevel = intTI.intValue(); 
    } 
  }
  
  public void abortInternal() throws SQLException {
    try {
      this.session.forceClose();
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void cleanup(Throwable whyCleanedUp) {
    try {
      if (this.session != null)
        if (isClosed()) {
          this.session.forceClose();
        } else {
          realClose(false, false, false, whyCleanedUp);
        }  
    } catch (SQLException|CJException sQLException) {}
  }
  
  @Deprecated
  public void clearHasTriedMaster() {
    this.hasTriedSourceFlag = false;
  }
  
  public void clearWarnings() throws SQLException {
    try {
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement clientPrepareStatement(String sql) throws SQLException {
    try {
      return clientPrepareStatement(sql, 1003, 1007);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
    try {
      PreparedStatement pStmt = clientPrepareStatement(sql);
      ((ClientPreparedStatement)pStmt).setRetrieveGeneratedKeys((autoGenKeyIndex == 1));
      return pStmt;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    try {
      return clientPrepareStatement(sql, resultSetType, resultSetConcurrency, true);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, boolean processEscapeCodesIfNeeded) throws SQLException {
    try {
      checkClosed();
      String nativeSql = (processEscapeCodesIfNeeded && ((Boolean)this.processEscapeCodesForPrepStmts.getValue()).booleanValue()) ? nativeSQL(sql) : sql;
      ClientPreparedStatement pStmt = null;
      if (((Boolean)this.cachePrepStmts.getValue()).booleanValue()) {
        QueryInfo pStmtInfo = (QueryInfo)this.queryInfoCache.get(nativeSql);
        if (pStmtInfo == null) {
          pStmt = ClientPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, this.database);
          this.queryInfoCache.put(nativeSql, pStmt.getQueryInfo());
        } else {
          pStmt = ClientPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, this.database, pStmtInfo);
        } 
      } else {
        pStmt = ClientPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, this.database);
      } 
      pStmt.setResultSetType(resultSetType);
      pStmt.setResultSetConcurrency(resultSetConcurrency);
      return pStmt;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
    try {
      ClientPreparedStatement pStmt = (ClientPreparedStatement)clientPrepareStatement(sql);
      pStmt.setRetrieveGeneratedKeys((autoGenKeyIndexes != null && autoGenKeyIndexes.length > 0));
      return pStmt;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
    try {
      ClientPreparedStatement pStmt = (ClientPreparedStatement)clientPrepareStatement(sql);
      pStmt.setRetrieveGeneratedKeys((autoGenKeyColNames != null && autoGenKeyColNames.length > 0));
      return pStmt;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    try {
      return clientPrepareStatement(sql, resultSetType, resultSetConcurrency, true);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void close() throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        if (this.connectionLifecycleInterceptors != null)
          for (ConnectionLifecycleInterceptor cli : this.connectionLifecycleInterceptors)
            cli.close();  
        realClose(true, true, false, null);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void normalClose() {
    try {
      close();
    } catch (SQLException e) {
      ExceptionFactory.createException(e.getMessage(), e);
    } 
  }
  
  private void closeAllOpenStatements() throws SQLException {
    SQLException postponedException = null;
    for (JdbcStatement stmt : this.openStatements) {
      try {
        ((StatementImpl)stmt).realClose(false, true);
      } catch (SQLException sqlEx) {
        postponedException = sqlEx;
      } 
    } 
    if (postponedException != null)
      throw postponedException; 
  }
  
  private void closeStatement(Statement stmt) {
    if (stmt != null) {
      try {
        stmt.close();
      } catch (SQLException sQLException) {}
      stmt = null;
    } 
  }
  
  public void commit() throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        checkClosed();
        try {
          if (this.connectionLifecycleInterceptors != null) {
            IterateBlock<ConnectionLifecycleInterceptor> iter = new IterateBlock<ConnectionLifecycleInterceptor>(this.connectionLifecycleInterceptors.iterator()) {
                void forEach(ConnectionLifecycleInterceptor each) throws SQLException {
                  if (!each.commit())
                    this.stopIterating = true; 
                }
              };
            iter.doForAll();
            if (!iter.fullIteration())
              return; 
          } 
          if (this.session.getServerSession().isAutoCommit())
            throw SQLError.createSQLException(Messages.getString("Connection.3"), getExceptionInterceptor()); 
          if (((Boolean)this.useLocalTransactionState.getValue()).booleanValue() && 
            !this.session.getServerSession().inTransactionOnServer())
            return; 
          this.session.execSQL(null, "commit", -1, null, false, (ProtocolEntityFactory)this.nullStatementResultSetFactory, null, false);
        } catch (SQLException sqlException) {
          if ("08S01".equals(sqlException.getSQLState()))
            throw SQLError.createSQLException(Messages.getString("Connection.4"), "08007", 
                getExceptionInterceptor()); 
          throw sqlException;
        } finally {
          this.session.setNeedsPing(((Boolean)this.reconnectAtTxEnd.getValue()).booleanValue());
        } 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void createNewIO(boolean isForReconnect) {
    try {
      synchronized (getConnectionMutex()) {
        try {
          if (!((Boolean)this.autoReconnect.getValue()).booleanValue()) {
            connectOneTryOnly(isForReconnect);
            return;
          } 
          connectWithRetries(isForReconnect);
        } catch (SQLException ex) {
          throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, ex.getMessage(), ex);
        } 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private void connectWithRetries(boolean isForReconnect) throws SQLException {
    double timeout = ((Integer)this.propertySet.getIntegerProperty(PropertyKey.initialTimeout).getValue()).intValue();
    boolean connectionGood = false;
    Exception connectionException = null;
    int attemptCount = 0;
    for (; attemptCount < ((Integer)this.propertySet.getIntegerProperty(PropertyKey.maxReconnects).getValue()).intValue() && !connectionGood; attemptCount++) {
      try {
        boolean oldAutoCommit;
        int oldIsolationLevel;
        boolean oldReadOnly;
        String oldDb;
        this.session.forceClose();
        JdbcConnection c = getProxy();
        this.session.connect(this.origHostInfo, this.user, this.password, this.database, getLoginTimeout(), c);
        pingInternal(false, 0);
        synchronized (getConnectionMutex()) {
          oldAutoCommit = getAutoCommit();
          oldIsolationLevel = this.isolationLevel;
          oldReadOnly = isReadOnly(false);
          oldDb = getDatabase();
          this.session.setQueryInterceptors(this.queryInterceptors);
        } 
        initializePropsFromServer();
        if (isForReconnect) {
          setAutoCommit(oldAutoCommit);
          setTransactionIsolation(oldIsolationLevel);
          setDatabase(oldDb);
          setReadOnly(oldReadOnly);
        } 
        connectionGood = true;
        break;
      } catch (UnableToConnectException rejEx) {
        close();
        this.session.getProtocol().getSocketConnection().forceClose();
      } catch (Exception e) {
        connectionException = e;
        connectionGood = false;
      } 
      if (connectionGood)
        break; 
      if (attemptCount > 0)
        try {
          Thread.sleep((long)timeout * 1000L);
        } catch (InterruptedException interruptedException) {} 
    } 
    if (!connectionGood) {
      SQLException chainedEx = SQLError.createSQLException(
          Messages.getString("Connection.UnableToConnectWithRetries", new Object[] { this.propertySet.getIntegerProperty(PropertyKey.maxReconnects).getValue() }), "08001", connectionException, getExceptionInterceptor());
      throw chainedEx;
    } 
    if (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.paranoid).getValue()).booleanValue() && !((Boolean)this.autoReconnect.getValue()).booleanValue()) {
      this.password = null;
      this.user = null;
    } 
    if (isForReconnect) {
      Iterator<JdbcStatement> statementIter = this.openStatements.iterator();
      Stack<JdbcStatement> serverPreparedStatements = null;
      while (statementIter.hasNext()) {
        JdbcStatement statementObj = statementIter.next();
        if (statementObj instanceof ServerPreparedStatement) {
          if (serverPreparedStatements == null)
            serverPreparedStatements = new Stack<>(); 
          serverPreparedStatements.add(statementObj);
        } 
      } 
      if (serverPreparedStatements != null)
        while (!serverPreparedStatements.isEmpty())
          ((ServerPreparedStatement)serverPreparedStatements.pop()).rePrepare();  
    } 
  }
  
  private void connectOneTryOnly(boolean isForReconnect) throws SQLException {
    Exception connectionNotEstablishedBecause = null;
    try {
      JdbcConnection c = getProxy();
      this.session.connect(this.origHostInfo, this.user, this.password, this.database, getLoginTimeout(), c);
      boolean oldAutoCommit = getAutoCommit();
      int oldIsolationLevel = this.isolationLevel;
      boolean oldReadOnly = isReadOnly(false);
      String oldDb = getDatabase();
      this.session.setQueryInterceptors(this.queryInterceptors);
      initializePropsFromServer();
      if (isForReconnect) {
        setAutoCommit(oldAutoCommit);
        setTransactionIsolation(oldIsolationLevel);
        setDatabase(oldDb);
        setReadOnly(oldReadOnly);
      } 
      return;
    } catch (UnableToConnectException rejEx) {
      close();
      NativeProtocol protocol = this.session.getProtocol();
      if (protocol != null)
        protocol.getSocketConnection().forceClose(); 
      throw rejEx;
    } catch (Exception e) {
      if ((e instanceof com.mysql.cj.exceptions.PasswordExpiredException || (e instanceof SQLException && ((SQLException)e)
        .getErrorCode() == 1820)) && 
        !((Boolean)this.disconnectOnExpiredPasswords.getValue()).booleanValue())
        return; 
      if (this.session != null)
        this.session.forceClose(); 
      connectionNotEstablishedBecause = e;
      if (e instanceof SQLException)
        throw (SQLException)e; 
      if (e.getCause() != null && e.getCause() instanceof SQLException)
        throw (SQLException)e.getCause(); 
      if (e instanceof CJException)
        throw (CJException)e; 
      SQLException chainedEx = SQLError.createSQLException(Messages.getString("Connection.UnableToConnect"), "08001", 
          getExceptionInterceptor());
      chainedEx.initCause(connectionNotEstablishedBecause);
      throw chainedEx;
    } 
  }
  
  private int getLoginTimeout() {
    int loginTimeoutSecs = DriverManager.getLoginTimeout();
    if (loginTimeoutSecs <= 0)
      return 0; 
    return loginTimeoutSecs * 1000;
  }
  
  private void createPreparedStatementCaches() throws SQLException {
    synchronized (getConnectionMutex()) {
      int cacheSize = ((Integer)this.propertySet.getIntegerProperty(PropertyKey.prepStmtCacheSize).getValue()).intValue();
      String queryInfoCacheFactory = (String)this.propertySet.getStringProperty(PropertyKey.queryInfoCacheFactory).getValue();
      CacheAdapterFactory<String, QueryInfo> cacheFactory = (CacheAdapterFactory<String, QueryInfo>)Util.getInstance(CacheAdapterFactory.class, queryInfoCacheFactory, null, null, 
          getExceptionInterceptor());
      this.queryInfoCache = cacheFactory.getInstance(this, this.origHostInfo.getDatabaseUrl(), cacheSize, ((Integer)this.prepStmtCacheSqlLimit.getValue()).intValue());
      if (((Boolean)this.useServerPrepStmts.getValue()).booleanValue()) {
        this.serverSideStatementCheckCache = new LRUCache(cacheSize);
        this.serverSideStatementCache = new LRUCache<CompoundCacheKey, ServerPreparedStatement>(cacheSize) {
            private static final long serialVersionUID = 7692318650375988114L;
            
            protected boolean removeEldestEntry(Map.Entry<ConnectionImpl.CompoundCacheKey, ServerPreparedStatement> eldest) {
              if (this.maxElements <= 1)
                return false; 
              boolean removeIt = super.removeEldestEntry(eldest);
              if (removeIt) {
                ServerPreparedStatement ps = eldest.getValue();
                ps.isCached = false;
                ps.setClosed(false);
                try {
                  ps.realClose(true, true);
                } catch (SQLException sQLException) {}
              } 
              return removeIt;
            }
          };
      } 
    } 
  }
  
  public Statement createStatement() throws SQLException {
    try {
      return createStatement(1003, 1007);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
    try {
      checkClosed();
      StatementImpl stmt = new StatementImpl(getMultiHostSafeProxy(), this.database);
      stmt.setResultSetType(resultSetType);
      stmt.setResultSetConcurrency(resultSetConcurrency);
      return stmt;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    try {
      if (((Boolean)this.pedantic.getValue()).booleanValue() && 
        resultSetHoldability != 1)
        throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009", 
            getExceptionInterceptor()); 
      return createStatement(resultSetType, resultSetConcurrency);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getActiveStatementCount() {
    return this.openStatements.size();
  }
  
  public boolean getAutoCommit() throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        return this.session.getServerSession().isAutoCommit();
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getCatalog() throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        return (this.propertySet.getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? null : this.database;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getCharacterSetMetadata() {
    synchronized (getConnectionMutex()) {
      return this.session.getServerSession().getCharsetSettings().getMetadataEncoding();
    } 
  }
  
  public int getHoldability() throws SQLException {
    try {
      return 2;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public long getId() {
    return this.session.getThreadId();
  }
  
  public long getIdleFor() {
    synchronized (getConnectionMutex()) {
      return this.session.getIdleFor();
    } 
  }
  
  public DatabaseMetaData getMetaData() throws SQLException {
    try {
      return getMetaData(true, true);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private DatabaseMetaData getMetaData(boolean checkClosed, boolean checkForInfoSchema) throws SQLException {
    try {
      if (checkClosed)
        checkClosed(); 
      DatabaseMetaData dbmeta = DatabaseMetaData.getInstance(getMultiHostSafeProxy(), this.database, checkForInfoSchema, this.nullStatementResultSetFactory);
      if (getSession() != null && getSession().getProtocol() != null) {
        dbmeta.setMetadataEncoding(getSession().getServerSession().getCharsetSettings().getMetadataEncoding());
        dbmeta.setMetadataCollationIndex(getSession().getServerSession().getCharsetSettings().getMetadataCollationIndex());
      } 
      return dbmeta;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Statement getMetadataSafeStatement() throws SQLException {
    try {
      return getMetadataSafeStatement(0);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Statement getMetadataSafeStatement(int maxRows) throws SQLException {
    Statement stmt = createStatement();
    stmt.setMaxRows((maxRows == -1) ? 0 : maxRows);
    stmt.setEscapeProcessing(false);
    if (stmt.getFetchSize() != 0)
      stmt.setFetchSize(0); 
    return stmt;
  }
  
  public ServerVersion getServerVersion() {
    return this.session.getServerSession().getServerVersion();
  }
  
  public int getTransactionIsolation() throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        if (!((Boolean)this.useLocalSessionState.getValue()).booleanValue()) {
          String s = this.session.queryServerVariable((
              versionMeetsMinimum(8, 0, 3) || (versionMeetsMinimum(5, 7, 20) && !versionMeetsMinimum(8, 0, 0))) ? "@@session.transaction_isolation" : "@@session.tx_isolation");
          if (s != null) {
            Integer intTI = mapTransIsolationNameToValue.get(s);
            if (intTI != null) {
              this.isolationLevel = intTI.intValue();
              return this.isolationLevel;
            } 
            throw SQLError.createSQLException(Messages.getString("Connection.12", new Object[] { s }), "S1000", 
                getExceptionInterceptor());
          } 
          throw SQLError.createSQLException(Messages.getString("Connection.13"), "S1000", getExceptionInterceptor());
        } 
        return this.isolationLevel;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Map<String, Class<?>> getTypeMap() throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        if (this.typeMap == null)
          this.typeMap = new HashMap<>(); 
        return this.typeMap;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getURL() {
    return this.origHostInfo.getDatabaseUrl();
  }
  
  public String getUser() {
    return this.user;
  }
  
  public SQLWarning getWarnings() throws SQLException {
    try {
      return null;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean hasSameProperties(JdbcConnection c) {
    return this.props.equals(c.getProperties());
  }
  
  public Properties getProperties() {
    return this.props;
  }
  
  @Deprecated
  public boolean hasTriedMaster() {
    return this.hasTriedSourceFlag;
  }
  
  private void initializePropsFromServer() throws SQLException {
    String connectionInterceptorClasses = this.propertySet.getStringProperty(PropertyKey.connectionLifecycleInterceptors).getStringValue();
    this.connectionLifecycleInterceptors = null;
    if (connectionInterceptorClasses != null)
      try {
        this
          
          .connectionLifecycleInterceptors = (List<ConnectionLifecycleInterceptor>)Util.loadClasses(ConnectionLifecycleInterceptor.class, connectionInterceptorClasses, "Connection.badLifecycleInterceptor", getExceptionInterceptor()).stream().map(i -> i.init(this, this.props, this.session.getLog())).collect(Collectors.toCollection(java.util.LinkedList::new));
      } catch (CJException e) {
        throw SQLExceptionsMapping.translateException(e, getExceptionInterceptor());
      }  
    this.session.setSessionVariables();
    this.session.loadServerVariables(getConnectionMutex(), this.dbmd.getDriverVersion());
    this.autoIncrementIncrement = this.session.getServerSession().getServerVariable("auto_increment_increment", 1);
    try {
      LicenseConfiguration.checkLicenseType(this.session.getServerSession().getServerVariables());
    } catch (CJException e) {
      throw SQLError.createSQLException(e.getMessage(), "08001", getExceptionInterceptor());
    } 
    this.session.getProtocol().initServerSession();
    checkTransactionIsolationLevel();
    handleAutoCommitDefaults();
    ((DatabaseMetaData)this.dbmd).setMetadataEncoding(this.session.getServerSession().getCharsetSettings().getMetadataEncoding());
    ((DatabaseMetaData)this.dbmd)
      .setMetadataCollationIndex(this.session.getServerSession().getCharsetSettings().getMetadataCollationIndex());
    setupServerForTruncationChecks();
  }
  
  private void handleAutoCommitDefaults() throws SQLException {
    try {
      boolean resetAutoCommitDefault = false;
      String initConnectValue = this.session.getServerSession().getServerVariable("init_connect");
      if (initConnectValue != null && initConnectValue.length() > 0) {
        String s = this.session.queryServerVariable("@@session.autocommit");
        if (s != null) {
          this.session.getServerSession().setAutoCommit(Boolean.parseBoolean(s));
          if (!this.session.getServerSession().isAutoCommit())
            resetAutoCommitDefault = true; 
        } 
      } else {
        resetAutoCommitDefault = true;
      } 
      if (resetAutoCommitDefault)
        try {
          setAutoCommit(true);
        } catch (SQLException ex) {
          if (ex.getErrorCode() != 1820 || ((Boolean)this.disconnectOnExpiredPasswords.getValue()).booleanValue())
            throw ex; 
        }  
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isClosed() {
    try {
      return this.session.isClosed();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isInGlobalTx() {
    return this.isInGlobalTx;
  }
  
  public boolean isSourceConnection() {
    return false;
  }
  
  public boolean isReadOnly() throws SQLException {
    try {
      return isReadOnly(true);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isReadOnly(boolean useSessionStatus) throws SQLException {
    try {
      if (useSessionStatus && !this.session.isClosed() && versionMeetsMinimum(5, 6, 5) && !((Boolean)this.useLocalSessionState.getValue()).booleanValue() && ((Boolean)this.readOnlyPropagatesToServer
        .getValue()).booleanValue()) {
        String s = this.session.queryServerVariable((
            versionMeetsMinimum(8, 0, 3) || (versionMeetsMinimum(5, 7, 20) && !versionMeetsMinimum(8, 0, 0))) ? "@@session.transaction_read_only" : "@@session.tx_read_only");
        if (s != null)
          return (Integer.parseInt(s) != 0); 
      } 
      return this.readOnly;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isSameResource(JdbcConnection otherConnection) {
    synchronized (getConnectionMutex()) {
      if (otherConnection == null)
        return false; 
      boolean directCompare = true;
      String otherHost = ((ConnectionImpl)otherConnection).origHostToConnectTo;
      String otherOrigDatabase = ((ConnectionImpl)otherConnection).origHostInfo.getDatabase();
      String otherCurrentDb = ((ConnectionImpl)otherConnection).database;
      if (!nullSafeCompare(otherHost, this.origHostToConnectTo)) {
        directCompare = false;
      } else if (otherHost != null && otherHost.indexOf(',') == -1 && otherHost.indexOf(':') == -1) {
        directCompare = (((ConnectionImpl)otherConnection).origPortToConnectTo == this.origPortToConnectTo);
      } 
      if (directCompare && (
        !nullSafeCompare(otherOrigDatabase, this.origHostInfo.getDatabase()) || !nullSafeCompare(otherCurrentDb, this.database)))
        directCompare = false; 
      if (directCompare)
        return true; 
      String otherResourceId = (String)((ConnectionImpl)otherConnection).getPropertySet().getStringProperty(PropertyKey.resourceId).getValue();
      String myResourceId = (String)this.propertySet.getStringProperty(PropertyKey.resourceId).getValue();
      if (otherResourceId != null || myResourceId != null) {
        directCompare = nullSafeCompare(otherResourceId, myResourceId);
        if (directCompare)
          return true; 
      } 
      return false;
    } 
  }
  
  protected ConnectionImpl() {
    this.autoIncrementIncrement = 0;
  }
  
  public ConnectionImpl(HostInfo hostInfo) throws SQLException {
    this.autoIncrementIncrement = 0;
    try {
      this.origHostInfo = hostInfo;
      this.origHostToConnectTo = hostInfo.getHost();
      this.origPortToConnectTo = hostInfo.getPort();
      this.database = hostInfo.getDatabase();
      this.user = hostInfo.getUser();
      this.password = hostInfo.getPassword();
      this.props = hostInfo.exposeAsProperties();
      this.propertySet = new JdbcPropertySetImpl();
      this.propertySet.initializeProperties(this.props);
      this.nullStatementResultSetFactory = new ResultSetFactory(this, null);
      this.session = new NativeSession(hostInfo, this.propertySet);
      this.session.addListener(this);
      this.autoReconnectForPools = this.propertySet.getBooleanProperty(PropertyKey.autoReconnectForPools);
      this.cachePrepStmts = this.propertySet.getBooleanProperty(PropertyKey.cachePrepStmts);
      this.autoReconnect = this.propertySet.getBooleanProperty(PropertyKey.autoReconnect);
      this.useUsageAdvisor = this.propertySet.getBooleanProperty(PropertyKey.useUsageAdvisor);
      this.reconnectAtTxEnd = this.propertySet.getBooleanProperty(PropertyKey.reconnectAtTxEnd);
      this.emulateUnsupportedPstmts = this.propertySet.getBooleanProperty(PropertyKey.emulateUnsupportedPstmts);
      this.ignoreNonTxTables = this.propertySet.getBooleanProperty(PropertyKey.ignoreNonTxTables);
      this.pedantic = this.propertySet.getBooleanProperty(PropertyKey.pedantic);
      this.prepStmtCacheSqlLimit = this.propertySet.getIntegerProperty(PropertyKey.prepStmtCacheSqlLimit);
      this.useLocalSessionState = this.propertySet.getBooleanProperty(PropertyKey.useLocalSessionState);
      this.useServerPrepStmts = this.propertySet.getBooleanProperty(PropertyKey.useServerPrepStmts);
      this.processEscapeCodesForPrepStmts = this.propertySet.getBooleanProperty(PropertyKey.processEscapeCodesForPrepStmts);
      this.useLocalTransactionState = this.propertySet.getBooleanProperty(PropertyKey.useLocalTransactionState);
      this.disconnectOnExpiredPasswords = this.propertySet.getBooleanProperty(PropertyKey.disconnectOnExpiredPasswords);
      this.readOnlyPropagatesToServer = this.propertySet.getBooleanProperty(PropertyKey.readOnlyPropagatesToServer);
      String exceptionInterceptorClasses = this.propertySet.getStringProperty(PropertyKey.exceptionInterceptors).getStringValue();
      if (exceptionInterceptorClasses != null && !"".equals(exceptionInterceptorClasses))
        this.exceptionInterceptor = (ExceptionInterceptor)new ExceptionInterceptorChain(exceptionInterceptorClasses, this.props, this.session.getLog()); 
      if (((Boolean)this.cachePrepStmts.getValue()).booleanValue())
        createPreparedStatementCaches(); 
      if (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.cacheCallableStmts).getValue()).booleanValue())
        this.parsedCallableStatementCache = new LRUCache(((Integer)this.propertySet.getIntegerProperty(PropertyKey.callableStmtCacheSize).getValue()).intValue()); 
      if (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.allowMultiQueries).getValue()).booleanValue())
        this.propertySet.getProperty(PropertyKey.cacheResultSetMetadata).setValue(Boolean.valueOf(false)); 
      if (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.cacheResultSetMetadata).getValue()).booleanValue())
        this.resultSetMetadataCache = new LRUCache(((Integer)this.propertySet.getIntegerProperty(PropertyKey.metadataCacheSize).getValue()).intValue()); 
      if (this.propertySet.getStringProperty(PropertyKey.socksProxyHost).getStringValue() != null)
        this.propertySet.getProperty(PropertyKey.socketFactory).setValue(SocksProxySocketFactory.class.getName()); 
      this.dbmd = getMetaData(false, false);
      initializeSafeQueryInterceptors();
    } catch (CJException e) {
      throw SQLExceptionsMapping.translateException(e, getExceptionInterceptor());
    } 
    try {
      createNewIO(false);
      unSafeQueryInterceptors();
      AbandonedConnectionCleanupThread.trackConnection(this, getSession().getNetworkResources());
    } catch (SQLException ex) {
      cleanup(ex);
      throw ex;
    } catch (Exception ex) {
      cleanup(ex);
      throw SQLError.createSQLException(((Boolean)this.propertySet.getBooleanProperty(PropertyKey.paranoid).getValue()).booleanValue() ? Messages.getString("Connection.0") : Messages.getString("Connection.1", new Object[] { this.session.getHostInfo().getHost(), Integer.valueOf(this.session.getHostInfo().getPort()) }), "08S01", ex, getExceptionInterceptor());
    } 
  }
  
  public int getAutoIncrementIncrement() {
    return this.autoIncrementIncrement;
  }
  
  public boolean lowerCaseTableNames() {
    return this.session.getServerSession().isLowerCaseTableNames();
  }
  
  public String nativeSQL(String sql) throws SQLException {
    try {
      if (sql == null)
        return null; 
      Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, getMultiHostSafeProxy().getSession().getServerSession().getSessionTimeZone(), 
          getMultiHostSafeProxy().getSession().getServerSession().getCapabilities().serverSupportsFracSecs(), 
          getMultiHostSafeProxy().getSession().getServerSession().isServerTruncatesFracSecs(), getExceptionInterceptor());
      if (escapedSqlResult instanceof String)
        return (String)escapedSqlResult; 
      return ((EscapeProcessorResult)escapedSqlResult).escapedSql;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private CallableStatement parseCallableStatement(String sql) throws SQLException {
    Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, getMultiHostSafeProxy().getSession().getServerSession().getSessionTimeZone(), 
        getMultiHostSafeProxy().getSession().getServerSession().getCapabilities().serverSupportsFracSecs(), 
        getMultiHostSafeProxy().getSession().getServerSession().isServerTruncatesFracSecs(), getExceptionInterceptor());
    boolean isFunctionCall = false;
    String parsedSql = null;
    if (escapedSqlResult instanceof EscapeProcessorResult) {
      parsedSql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
      isFunctionCall = ((EscapeProcessorResult)escapedSqlResult).callingStoredFunction;
    } else {
      parsedSql = (String)escapedSqlResult;
      isFunctionCall = false;
    } 
    return CallableStatement.getInstance(getMultiHostSafeProxy(), parsedSql, this.database, isFunctionCall);
  }
  
  public void ping() throws SQLException {
    try {
      pingInternal(true, 0);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void pingInternal(boolean checkForClosedConnection, int timeoutMillis) throws SQLException {
    try {
      this.session.ping(checkForClosedConnection, timeoutMillis);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public CallableStatement prepareCall(String sql) throws SQLException {
    try {
      return prepareCall(sql, 1003, 1007);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    try {
      CallableStatement cStmt = null;
      if (!((Boolean)this.propertySet.getBooleanProperty(PropertyKey.cacheCallableStmts).getValue()).booleanValue()) {
        cStmt = parseCallableStatement(sql);
      } else {
        synchronized (this.parsedCallableStatementCache) {
          CompoundCacheKey key = new CompoundCacheKey(getDatabase(), sql);
          CallableStatement.CallableStatementParamInfo cachedParamInfo = (CallableStatement.CallableStatementParamInfo)this.parsedCallableStatementCache.get(key);
          if (cachedParamInfo != null) {
            cStmt = CallableStatement.getInstance(getMultiHostSafeProxy(), cachedParamInfo);
          } else {
            cStmt = parseCallableStatement(sql);
            synchronized (cStmt) {
              cachedParamInfo = cStmt.paramInfo;
            } 
            this.parsedCallableStatementCache.put(key, cachedParamInfo);
          } 
        } 
      } 
      cStmt.setResultSetType(resultSetType);
      cStmt.setResultSetConcurrency(resultSetConcurrency);
      return cStmt;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    try {
      if (((Boolean)this.pedantic.getValue()).booleanValue() && 
        resultSetHoldability != 1)
        throw SQLError.createSQLException(Messages.getString("Connection.17"), "S1009", getExceptionInterceptor()); 
      CallableStatement cStmt = (CallableStatement)prepareCall(sql, resultSetType, resultSetConcurrency);
      return cStmt;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement prepareStatement(String sql) throws SQLException {
    try {
      return prepareStatement(sql, 1003, 1007);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement prepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
    try {
      PreparedStatement pStmt = prepareStatement(sql);
      ((ClientPreparedStatement)pStmt).setRetrieveGeneratedKeys((autoGenKeyIndex == 1));
      return pStmt;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        checkClosed();
        ClientPreparedStatement pStmt = null;
        boolean canServerPrepare = true;
        String nativeSql = ((Boolean)this.processEscapeCodesForPrepStmts.getValue()).booleanValue() ? nativeSQL(sql) : sql;
        if (((Boolean)this.useServerPrepStmts.getValue()).booleanValue() && ((Boolean)this.emulateUnsupportedPstmts.getValue()).booleanValue())
          canServerPrepare = canHandleAsServerPreparedStatement(nativeSql); 
        if (((Boolean)this.useServerPrepStmts.getValue()).booleanValue() && canServerPrepare) {
          if (((Boolean)this.cachePrepStmts.getValue()).booleanValue()) {
            synchronized (this.serverSideStatementCache) {
              pStmt = (ClientPreparedStatement)this.serverSideStatementCache.remove(new CompoundCacheKey(this.database, sql));
              if (pStmt != null) {
                ((ServerPreparedStatement)pStmt).setClosed(false);
                pStmt.clearParameters();
                pStmt.setResultSetType(resultSetType);
                pStmt.setResultSetConcurrency(resultSetConcurrency);
              } 
              if (pStmt == null)
                try {
                  pStmt = ServerPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, this.database, resultSetType, resultSetConcurrency);
                  if (sql.length() < ((Integer)this.prepStmtCacheSqlLimit.getValue()).intValue())
                    ((ServerPreparedStatement)pStmt).isCacheable = true; 
                  pStmt.setResultSetType(resultSetType);
                  pStmt.setResultSetConcurrency(resultSetConcurrency);
                } catch (SQLException sqlEx) {
                  if (((Boolean)this.emulateUnsupportedPstmts.getValue()).booleanValue()) {
                    pStmt = (ClientPreparedStatement)clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
                    if (sql.length() < ((Integer)this.prepStmtCacheSqlLimit.getValue()).intValue())
                      this.serverSideStatementCheckCache.put(sql, Boolean.FALSE); 
                  } else {
                    throw sqlEx;
                  } 
                }  
            } 
          } else {
            try {
              pStmt = ServerPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, this.database, resultSetType, resultSetConcurrency);
              pStmt.setResultSetType(resultSetType);
              pStmt.setResultSetConcurrency(resultSetConcurrency);
            } catch (SQLException sqlEx) {
              if (((Boolean)this.emulateUnsupportedPstmts.getValue()).booleanValue()) {
                pStmt = (ClientPreparedStatement)clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
              } else {
                throw sqlEx;
              } 
            } 
          } 
        } else {
          pStmt = (ClientPreparedStatement)clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
        } 
        return pStmt;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    try {
      if (((Boolean)this.pedantic.getValue()).booleanValue() && 
        resultSetHoldability != 1)
        throw SQLError.createSQLException(Messages.getString("Connection.17"), "S1009", getExceptionInterceptor()); 
      return prepareStatement(sql, resultSetType, resultSetConcurrency);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement prepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
    try {
      PreparedStatement pStmt = prepareStatement(sql);
      ((ClientPreparedStatement)pStmt).setRetrieveGeneratedKeys((autoGenKeyIndexes != null && autoGenKeyIndexes.length > 0));
      return pStmt;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement prepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
    try {
      PreparedStatement pStmt = prepareStatement(sql);
      ((ClientPreparedStatement)pStmt).setRetrieveGeneratedKeys((autoGenKeyColNames != null && autoGenKeyColNames.length > 0));
      return pStmt;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void realClose(boolean calledExplicitly, boolean issueRollback, boolean skipLocalTeardown, Throwable reason) throws SQLException {
    try {
      SQLException sqlEx = null;
      if (isClosed())
        return; 
      this.session.setForceClosedReason(reason);
      try {
        if (!skipLocalTeardown) {
          if (!getAutoCommit() && issueRollback)
            try {
              rollback();
            } catch (SQLException ex) {
              sqlEx = ex;
            }  
          if (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.gatherPerfMetrics).getValue()).booleanValue())
            this.session.getProtocol().getMetricsHolder().reportMetrics(this.session.getLog()); 
          if (((Boolean)this.useUsageAdvisor.getValue()).booleanValue()) {
            if (!calledExplicitly)
              this.session.getProfilerEventHandler().processEvent((byte)0, (Session)this.session, null, null, 0L, new Throwable(), 
                  Messages.getString("Connection.18")); 
            if (System.currentTimeMillis() - this.session.getConnectionCreationTimeMillis() < 500L)
              this.session.getProfilerEventHandler().processEvent((byte)0, (Session)this.session, null, null, 0L, new Throwable(), 
                  Messages.getString("Connection.19")); 
          } 
          try {
            closeAllOpenStatements();
          } catch (SQLException ex) {
            sqlEx = ex;
          } 
          this.session.quit();
        } else {
          this.session.forceClose();
        } 
        if (this.queryInterceptors != null)
          this.queryInterceptors.forEach(QueryInterceptor::destroy); 
        if (this.exceptionInterceptor != null)
          this.exceptionInterceptor.destroy(); 
      } finally {
        this.openStatements.clear();
        this.queryInterceptors = null;
        this.exceptionInterceptor = null;
        this.nullStatementResultSetFactory = null;
      } 
      if (sqlEx != null)
        throw sqlEx; 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void recachePreparedStatement(JdbcPreparedStatement pstmt) throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        if (((Boolean)this.cachePrepStmts.getValue()).booleanValue() && pstmt.isPoolable())
          synchronized (this.serverSideStatementCache) {
            Object oldServerPrepStmt = this.serverSideStatementCache.put(new CompoundCacheKey(pstmt
                  .getCurrentDatabase(), ((PreparedQuery)pstmt.getQuery()).getOriginalSql()), pstmt);
            if (oldServerPrepStmt != null && oldServerPrepStmt != pstmt) {
              ((ServerPreparedStatement)oldServerPrepStmt).isCached = false;
              ((ServerPreparedStatement)oldServerPrepStmt).setClosed(false);
              ((ServerPreparedStatement)oldServerPrepStmt).realClose(true, true);
            } 
          }  
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void decachePreparedStatement(JdbcPreparedStatement pstmt) throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        if (((Boolean)this.cachePrepStmts.getValue()).booleanValue())
          synchronized (this.serverSideStatementCache) {
            this.serverSideStatementCache.remove(new CompoundCacheKey(pstmt.getCurrentDatabase(), ((PreparedQuery)pstmt.getQuery()).getOriginalSql()));
          }  
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void registerStatement(JdbcStatement stmt) {
    this.openStatements.addIfAbsent(stmt);
  }
  
  public void releaseSavepoint(Savepoint arg0) throws SQLException {
    try {
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void resetServerState() throws SQLException {
    try {
      if (!((Boolean)this.propertySet.getBooleanProperty(PropertyKey.paranoid).getValue()).booleanValue() && this.session != null)
        changeUser(this.user, this.password); 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void rollback() throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        checkClosed();
        try {
          if (this.connectionLifecycleInterceptors != null) {
            IterateBlock<ConnectionLifecycleInterceptor> iter = new IterateBlock<ConnectionLifecycleInterceptor>(this.connectionLifecycleInterceptors.iterator()) {
                void forEach(ConnectionLifecycleInterceptor each) throws SQLException {
                  if (!each.rollback())
                    this.stopIterating = true; 
                }
              };
            iter.doForAll();
            if (!iter.fullIteration())
              return; 
          } 
          if (this.session.getServerSession().isAutoCommit())
            throw SQLError.createSQLException(Messages.getString("Connection.20"), "08003", 
                getExceptionInterceptor()); 
          try {
            rollbackNoChecks();
          } catch (SQLException sqlEx) {
            if (((Boolean)this.ignoreNonTxTables.getInitialValue()).booleanValue() && sqlEx.getErrorCode() == 1196)
              return; 
            throw sqlEx;
          } 
        } catch (SQLException sqlException) {
          if ("08S01".equals(sqlException.getSQLState()))
            throw SQLError.createSQLException(Messages.getString("Connection.21"), "08007", 
                getExceptionInterceptor()); 
          throw sqlException;
        } finally {
          this.session.setNeedsPing(((Boolean)this.reconnectAtTxEnd.getValue()).booleanValue());
        } 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void rollback(final Savepoint savepoint) throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        checkClosed();
        try {
          if (this.connectionLifecycleInterceptors != null) {
            IterateBlock<ConnectionLifecycleInterceptor> iter = new IterateBlock<ConnectionLifecycleInterceptor>(this.connectionLifecycleInterceptors.iterator()) {
                void forEach(ConnectionLifecycleInterceptor each) throws SQLException {
                  if (!each.rollback(savepoint))
                    this.stopIterating = true; 
                }
              };
            iter.doForAll();
            if (!iter.fullIteration())
              return; 
          } 
          StringBuilder rollbackQuery = new StringBuilder("ROLLBACK TO SAVEPOINT ");
          rollbackQuery.append('`');
          rollbackQuery.append(savepoint.getSavepointName());
          rollbackQuery.append('`');
          Statement stmt = null;
          try {
            stmt = getMetadataSafeStatement();
            stmt.executeUpdate(rollbackQuery.toString());
          } catch (SQLException sqlEx) {
            int errno = sqlEx.getErrorCode();
            if (errno == 1181) {
              String msg = sqlEx.getMessage();
              if (msg != null) {
                int indexOfError153 = msg.indexOf("153");
                if (indexOfError153 != -1)
                  throw SQLError.createSQLException(Messages.getString("Connection.22", new Object[] { savepoint.getSavepointName() }), "S1009", errno, 
                      getExceptionInterceptor()); 
              } 
            } 
            if (((Boolean)this.ignoreNonTxTables.getValue()).booleanValue() && sqlEx.getErrorCode() != 1196)
              throw sqlEx; 
            if ("08S01".equals(sqlEx.getSQLState()))
              throw SQLError.createSQLException(Messages.getString("Connection.23"), "08007", 
                  getExceptionInterceptor()); 
            throw sqlEx;
          } finally {
            closeStatement(stmt);
          } 
        } finally {
          this.session.setNeedsPing(((Boolean)this.reconnectAtTxEnd.getValue()).booleanValue());
        } 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private void rollbackNoChecks() throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        if (((Boolean)this.useLocalTransactionState.getValue()).booleanValue() && 
          !this.session.getServerSession().inTransactionOnServer())
          return; 
        this.session.execSQL(null, "rollback", -1, null, false, (ProtocolEntityFactory)this.nullStatementResultSetFactory, null, false);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement serverPrepareStatement(String sql) throws SQLException {
    try {
      String nativeSql = ((Boolean)this.processEscapeCodesForPrepStmts.getValue()).booleanValue() ? nativeSQL(sql) : sql;
      return ServerPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, getDatabase(), 1003, 1007);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
    try {
      String nativeSql = ((Boolean)this.processEscapeCodesForPrepStmts.getValue()).booleanValue() ? nativeSQL(sql) : sql;
      ClientPreparedStatement pStmt = ServerPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, getDatabase(), 1003, 1007);
      pStmt.setRetrieveGeneratedKeys((autoGenKeyIndex == 1));
      return pStmt;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    try {
      String nativeSql = ((Boolean)this.processEscapeCodesForPrepStmts.getValue()).booleanValue() ? nativeSQL(sql) : sql;
      return ServerPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, getDatabase(), resultSetType, resultSetConcurrency);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    try {
      if (((Boolean)this.pedantic.getValue()).booleanValue() && 
        resultSetHoldability != 1)
        throw SQLError.createSQLException(Messages.getString("Connection.17"), "S1009", getExceptionInterceptor()); 
      return serverPrepareStatement(sql, resultSetType, resultSetConcurrency);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
    try {
      ClientPreparedStatement pStmt = (ClientPreparedStatement)serverPrepareStatement(sql);
      pStmt.setRetrieveGeneratedKeys((autoGenKeyIndexes != null && autoGenKeyIndexes.length > 0));
      return pStmt;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
    try {
      ClientPreparedStatement pStmt = (ClientPreparedStatement)serverPrepareStatement(sql);
      pStmt.setRetrieveGeneratedKeys((autoGenKeyColNames != null && autoGenKeyColNames.length > 0));
      return pStmt;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setAutoCommit(final boolean autoCommitFlag) throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        checkClosed();
        if (this.connectionLifecycleInterceptors != null) {
          IterateBlock<ConnectionLifecycleInterceptor> iter = new IterateBlock<ConnectionLifecycleInterceptor>(this.connectionLifecycleInterceptors.iterator()) {
              void forEach(ConnectionLifecycleInterceptor each) throws SQLException {
                if (!each.setAutoCommit(autoCommitFlag))
                  this.stopIterating = true; 
              }
            };
          iter.doForAll();
          if (!iter.fullIteration())
            return; 
        } 
        if (((Boolean)this.autoReconnectForPools.getValue()).booleanValue())
          this.autoReconnect.setValue(Boolean.valueOf(true)); 
        boolean isAutoCommit = this.session.getServerSession().isAutoCommit();
        try {
          boolean needsSetOnServer = true;
          if (((Boolean)this.useLocalSessionState.getValue()).booleanValue() && isAutoCommit == autoCommitFlag) {
            needsSetOnServer = false;
          } else if (!((Boolean)this.autoReconnect.getValue()).booleanValue()) {
            needsSetOnServer = getSession().isSetNeededForAutoCommitMode(autoCommitFlag);
          } 
          this.session.getServerSession().setAutoCommit(autoCommitFlag);
          if (needsSetOnServer)
            this.session.execSQL(null, autoCommitFlag ? "SET autocommit=1" : "SET autocommit=0", -1, null, false, (ProtocolEntityFactory)this.nullStatementResultSetFactory, null, false); 
        } catch (CJCommunicationsException e) {
          throw e;
        } catch (CJException e) {
          this.session.getServerSession().setAutoCommit(isAutoCommit);
          throw SQLError.createSQLException(e.getMessage(), e.getSQLState(), e.getVendorCode(), e.isTransient(), e, getExceptionInterceptor());
        } finally {
          if (((Boolean)this.autoReconnectForPools.getValue()).booleanValue())
            this.autoReconnect.setValue(Boolean.valueOf(false)); 
        } 
        return;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setCatalog(String catalog) throws SQLException {
    try {
      if (this.propertySet.getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.CATALOG)
        setDatabase(catalog); 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setDatabase(final String db) throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        checkClosed();
        if (db == null)
          throw SQLError.createSQLException("Database can not be null", "S1009", getExceptionInterceptor()); 
        if (this.connectionLifecycleInterceptors != null) {
          IterateBlock<ConnectionLifecycleInterceptor> iter = new IterateBlock<ConnectionLifecycleInterceptor>(this.connectionLifecycleInterceptors.iterator()) {
              void forEach(ConnectionLifecycleInterceptor each) throws SQLException {
                if (!each.setDatabase(db))
                  this.stopIterating = true; 
              }
            };
          iter.doForAll();
          if (!iter.fullIteration())
            return; 
        } 
        if (((Boolean)this.useLocalSessionState.getValue()).booleanValue())
          if (this.session.getServerSession().isLowerCaseTableNames()) {
            if (this.database.equalsIgnoreCase(db))
              return; 
          } else if (this.database.equals(db)) {
            return;
          }  
        String quotedId = this.session.getIdentifierQuoteString();
        if (quotedId == null || quotedId.equals(" "))
          quotedId = ""; 
        StringBuilder query = new StringBuilder("USE ");
        query.append(StringUtils.quoteIdentifier(db, quotedId, ((Boolean)this.pedantic.getValue()).booleanValue()));
        this.session.execSQL(null, query.toString(), -1, null, false, (ProtocolEntityFactory)this.nullStatementResultSetFactory, null, false);
        this.database = db;
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getDatabase() throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        return this.database;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setFailedOver(boolean flag) {}
  
  public void setHoldability(int arg0) throws SQLException {
    try {
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setInGlobalTx(boolean flag) {
    this.isInGlobalTx = flag;
  }
  
  public void setReadOnly(boolean readOnlyFlag) throws SQLException {
    try {
      checkClosed();
      setReadOnlyInternal(readOnlyFlag);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setReadOnlyInternal(boolean readOnlyFlag) throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        if (((Boolean)this.readOnlyPropagatesToServer.getValue()).booleanValue() && versionMeetsMinimum(5, 6, 5) && (
          !((Boolean)this.useLocalSessionState.getValue()).booleanValue() || readOnlyFlag != this.readOnly))
          this.session.execSQL(null, "set session transaction " + (readOnlyFlag ? "read only" : "read write"), -1, null, false, (ProtocolEntityFactory)this.nullStatementResultSetFactory, null, false); 
        this.readOnly = readOnlyFlag;
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Savepoint setSavepoint() throws SQLException {
    try {
      MysqlSavepoint savepoint = new MysqlSavepoint(getExceptionInterceptor());
      setSavepoint(savepoint);
      return savepoint;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private void setSavepoint(MysqlSavepoint savepoint) throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        checkClosed();
        StringBuilder savePointQuery = new StringBuilder("SAVEPOINT ");
        savePointQuery.append('`');
        savePointQuery.append(savepoint.getSavepointName());
        savePointQuery.append('`');
        Statement stmt = null;
        try {
          stmt = getMetadataSafeStatement();
          stmt.executeUpdate(savePointQuery.toString());
        } finally {
          closeStatement(stmt);
        } 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Savepoint setSavepoint(String name) throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        MysqlSavepoint savepoint = new MysqlSavepoint(name, getExceptionInterceptor());
        setSavepoint(savepoint);
        return savepoint;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setTransactionIsolation(int level) throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        checkClosed();
        String sql = null;
        boolean shouldSendSet = false;
        if (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.alwaysSendSetIsolation).getValue()).booleanValue()) {
          shouldSendSet = true;
        } else if (level != this.isolationLevel) {
          shouldSendSet = true;
        } 
        if (((Boolean)this.useLocalSessionState.getValue()).booleanValue())
          shouldSendSet = (this.isolationLevel != level); 
        if (shouldSendSet) {
          switch (level) {
            case 0:
              throw SQLError.createSQLException(Messages.getString("Connection.24"), getExceptionInterceptor());
            case 2:
              sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED";
              break;
            case 1:
              sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED";
              break;
            case 4:
              sql = "SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ";
              break;
            case 8:
              sql = "SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE";
              break;
            default:
              throw SQLError.createSQLException(Messages.getString("Connection.25", new Object[] { Integer.valueOf(level) }), "S1C00", 
                  getExceptionInterceptor());
          } 
          this.session.execSQL(null, sql, -1, null, false, (ProtocolEntityFactory)this.nullStatementResultSetFactory, null, false);
          this.isolationLevel = level;
        } 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        this.typeMap = map;
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private void setupServerForTruncationChecks() throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        RuntimeProperty<Boolean> jdbcCompliantTruncation = this.propertySet.getProperty(PropertyKey.jdbcCompliantTruncation);
        if (((Boolean)jdbcCompliantTruncation.getValue()).booleanValue()) {
          String currentSqlMode = this.session.getServerSession().getServerVariable("sql_mode");
          boolean strictTransTablesIsSet = (StringUtils.indexOfIgnoreCase(currentSqlMode, "STRICT_TRANS_TABLES") != -1);
          if (currentSqlMode == null || currentSqlMode.length() == 0 || !strictTransTablesIsSet) {
            StringBuilder commandBuf = new StringBuilder("SET sql_mode='");
            if (currentSqlMode != null && currentSqlMode.length() > 0) {
              commandBuf.append(currentSqlMode);
              commandBuf.append(",");
            } 
            commandBuf.append("STRICT_TRANS_TABLES'");
            this.session.execSQL(null, commandBuf.toString(), -1, null, false, (ProtocolEntityFactory)this.nullStatementResultSetFactory, null, false);
            jdbcCompliantTruncation.setValue(Boolean.valueOf(false));
          } else if (strictTransTablesIsSet) {
            jdbcCompliantTruncation.setValue(Boolean.valueOf(false));
          } 
        } 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void shutdownServer() throws SQLException {
    try {
      try {
        this.session.shutdownServer();
      } catch (CJException ex) {
        SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.UnhandledExceptionDuringShutdown"), "S1000", 
            getExceptionInterceptor());
        sqlEx.initCause((Throwable)ex);
        throw sqlEx;
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void unregisterStatement(JdbcStatement stmt) {
    this.openStatements.remove(stmt);
  }
  
  public boolean versionMeetsMinimum(int major, int minor, int subminor) {
    try {
      checkClosed();
      return this.session.versionMeetsMinimum(major, minor, subminor);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public CachedResultSetMetaData getCachedMetaData(String sql) {
    if (this.resultSetMetadataCache != null)
      synchronized (this.resultSetMetadataCache) {
        return (CachedResultSetMetaData)this.resultSetMetadataCache.get(sql);
      }  
    return null;
  }
  
  public void initializeResultsMetadataFromCache(String sql, CachedResultSetMetaData cachedMetaData, ResultSetInternalMethods resultSet) throws SQLException {
    try {
      CachedResultSetMetaDataImpl cachedResultSetMetaDataImpl;
      if (cachedMetaData == null) {
        cachedResultSetMetaDataImpl = new CachedResultSetMetaDataImpl();
        resultSet.getColumnDefinition().buildIndexMapping();
        resultSet.initializeWithMetadata();
        if (resultSet instanceof UpdatableResultSet)
          ((UpdatableResultSet)resultSet).checkUpdatability(); 
        resultSet.populateCachedMetaData((CachedResultSetMetaData)cachedResultSetMetaDataImpl);
        this.resultSetMetadataCache.put(sql, cachedResultSetMetaDataImpl);
      } else {
        resultSet.getColumnDefinition().initializeFrom((ColumnDefinition)cachedResultSetMetaDataImpl);
        resultSet.initializeWithMetadata();
        if (resultSet instanceof UpdatableResultSet)
          ((UpdatableResultSet)resultSet).checkUpdatability(); 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getStatementComment() {
    return this.session.getProtocol().getQueryComment();
  }
  
  public void setStatementComment(String comment) {
    this.session.getProtocol().setQueryComment(comment);
  }
  
  public void transactionBegun() {
    synchronized (getConnectionMutex()) {
      if (this.connectionLifecycleInterceptors != null)
        this.connectionLifecycleInterceptors.stream().forEach(ConnectionLifecycleInterceptor::transactionBegun); 
    } 
  }
  
  public void transactionCompleted() {
    synchronized (getConnectionMutex()) {
      if (this.connectionLifecycleInterceptors != null)
        this.connectionLifecycleInterceptors.stream().forEach(ConnectionLifecycleInterceptor::transactionCompleted); 
    } 
  }
  
  public boolean storesLowerCaseTableName() {
    return this.session.getServerSession().storesLowerCaseTableNames();
  }
  
  public ExceptionInterceptor getExceptionInterceptor() {
    return this.exceptionInterceptor;
  }
  
  public boolean isServerLocal() throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        return this.session.isServerLocal((Session)getSession());
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getSessionMaxRows() {
    synchronized (getConnectionMutex()) {
      return this.session.getSessionMaxRows();
    } 
  }
  
  public void setSessionMaxRows(int max) throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        checkClosed();
        if (this.session.getSessionMaxRows() != max) {
          this.session.setSessionMaxRows(max);
          this.session.execSQL(null, "SET SQL_SELECT_LIMIT=" + ((this.session.getSessionMaxRows() == -1) ? "DEFAULT" : (String)Integer.valueOf(this.session.getSessionMaxRows())), -1, null, false, (ProtocolEntityFactory)this.nullStatementResultSetFactory, null, false);
        } 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setSchema(String schema) throws SQLException {
    try {
      checkClosed();
      if (this.propertySet.getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA)
        setDatabase(schema); 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getSchema() throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        checkClosed();
        return (this.propertySet.getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? this.database : null;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void abort(Executor executor) throws SQLException {
    try {
      SecurityManager sec = System.getSecurityManager();
      if (sec != null)
        sec.checkPermission(ABORT_PERM); 
      if (executor == null)
        throw SQLError.createSQLException(Messages.getString("Connection.26"), "S1009", getExceptionInterceptor()); 
      executor.execute(new Runnable() {
            public void run() {
              try {
                ConnectionImpl.this.abortInternal();
              } catch (SQLException e) {
                throw new RuntimeException(e);
              } 
            }
          });
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        SecurityManager sec = System.getSecurityManager();
        if (sec != null)
          sec.checkPermission(SET_NETWORK_TIMEOUT_PERM); 
        if (executor == null)
          throw SQLError.createSQLException(Messages.getString("Connection.26"), "S1009", getExceptionInterceptor()); 
        checkClosed();
        executor.execute(new NetworkTimeoutSetter(this, milliseconds));
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private static class NetworkTimeoutSetter implements Runnable {
    private final WeakReference<JdbcConnection> connRef;
    
    private final int milliseconds;
    
    public NetworkTimeoutSetter(JdbcConnection conn, int milliseconds) {
      this.connRef = new WeakReference<>(conn);
      this.milliseconds = milliseconds;
    }
    
    public void run() {
      JdbcConnection conn = this.connRef.get();
      if (conn != null)
        synchronized (conn.getConnectionMutex()) {
          ((NativeSession)conn.getSession()).setSocketTimeout(this.milliseconds);
        }  
    }
  }
  
  public int getNetworkTimeout() throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        checkClosed();
        return this.session.getSocketTimeout();
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Clob createClob() {
    try {
      return new Clob(getExceptionInterceptor());
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Blob createBlob() {
    try {
      return new Blob(getExceptionInterceptor());
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public NClob createNClob() {
    try {
      return new NClob(getExceptionInterceptor());
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public SQLXML createSQLXML() throws SQLException {
    try {
      return new MysqlSQLXML(getExceptionInterceptor());
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isValid(int timeout) throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        if (isClosed())
          return false; 
        try {
          try {
            pingInternal(false, timeout * 1000);
          } catch (Throwable t) {
            try {
              abortInternal();
            } catch (Throwable throwable) {}
            return false;
          } 
        } catch (Throwable t) {
          return false;
        } 
        return true;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ClientInfoProvider getClientInfoProviderImpl() throws SQLException {
    try {
      synchronized (getConnectionMutex()) {
        if (this.infoProvider == null) {
          String clientInfoProvider = this.propertySet.getStringProperty(PropertyKey.clientInfoProvider).getStringValue();
          try {
            this.infoProvider = (ClientInfoProvider)Util.getInstance(ClientInfoProvider.class, clientInfoProvider, null, null, getExceptionInterceptor());
          } catch (CJException e1) {
            if (ClassNotFoundException.class.isInstance(e1.getCause())) {
              try {
                this.infoProvider = (ClientInfoProvider)Util.getInstance(ClientInfoProvider.class, "com.mysql.cj.jdbc." + clientInfoProvider, null, null, 
                    getExceptionInterceptor());
              } catch (CJException e2) {
                throw SQLExceptionsMapping.translateException(e1, getExceptionInterceptor());
              } 
            } else {
              throw SQLExceptionsMapping.translateException(e1, getExceptionInterceptor());
            } 
          } 
          this.infoProvider.initialize(this, this.props);
        } 
        return this.infoProvider;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setClientInfo(String name, String value) throws SQLClientInfoException {
    try {
      getClientInfoProviderImpl().setClientInfo(this, name, value);
    } catch (SQLClientInfoException ciEx) {
      throw ciEx;
    } catch (SQLException|CJException sqlEx) {
      SQLClientInfoException clientInfoEx = new SQLClientInfoException();
      clientInfoEx.initCause(sqlEx);
      throw clientInfoEx;
    } 
  }
  
  public void setClientInfo(Properties properties) throws SQLClientInfoException {
    try {
      getClientInfoProviderImpl().setClientInfo(this, properties);
    } catch (SQLClientInfoException ciEx) {
      throw ciEx;
    } catch (SQLException|CJException sqlEx) {
      SQLClientInfoException clientInfoEx = new SQLClientInfoException();
      clientInfoEx.initCause(sqlEx);
      throw clientInfoEx;
    } 
  }
  
  public String getClientInfo(String name) throws SQLException {
    try {
      return getClientInfoProviderImpl().getClientInfo(this, name);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Properties getClientInfo() throws SQLException {
    try {
      return getClientInfoProviderImpl().getClientInfo(this);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
    try {
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
    try {
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public <T> T unwrap(Class<T> iface) throws SQLException {
    try {
      try {
        return iface.cast(this);
      } catch (ClassCastException cce) {
        throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", 
            getExceptionInterceptor());
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    try {
      checkClosed();
      return iface.isInstance(this);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public NativeSession getSession() {
    return this.session;
  }
  
  public String getHostPortPair() {
    return this.origHostInfo.getHostPortPair();
  }
  
  public void handleNormalClose() {
    try {
      close();
    } catch (SQLException e) {
      ExceptionFactory.createException(e.getMessage(), e);
    } 
  }
  
  public void handleReconnect() {
    createNewIO(true);
  }
  
  public void handleCleanup(Throwable whyCleanedUp) {
    cleanup(whyCleanedUp);
  }
  
  public ServerSessionStateController getServerSessionStateController() {
    return this.session.getServerSession().getServerSessionStateController();
  }
}
