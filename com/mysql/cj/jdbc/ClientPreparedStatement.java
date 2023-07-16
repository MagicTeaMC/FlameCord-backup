package com.mysql.cj.jdbc;

import com.mysql.cj.BindValue;
import com.mysql.cj.CancelQueryTask;
import com.mysql.cj.ClientPreparedQuery;
import com.mysql.cj.Messages;
import com.mysql.cj.MysqlType;
import com.mysql.cj.NativeQueryBindings;
import com.mysql.cj.NativeSession;
import com.mysql.cj.PreparedQuery;
import com.mysql.cj.Query;
import com.mysql.cj.QueryBindings;
import com.mysql.cj.QueryInfo;
import com.mysql.cj.QueryReturnType;
import com.mysql.cj.Session;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.FeatureNotAvailableException;
import com.mysql.cj.exceptions.StatementIsClosedException;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.jdbc.result.CachedResultSetMetaData;
import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.result.Row;
import com.mysql.cj.util.Util;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.function.Function;

public class ClientPreparedStatement extends StatementImpl implements JdbcPreparedStatement {
  protected boolean batchHasPlainStatements = false;
  
  protected MysqlParameterMetadata parameterMetaData;
  
  private ResultSetMetaData pstmtResultMetaData;
  
  protected String batchedValuesClause;
  
  private boolean doPingInstead;
  
  private boolean compensateForOnDuplicateKeyUpdate = false;
  
  protected int rewrittenBatchSize = 0;
  
  protected static ClientPreparedStatement getInstance(JdbcConnection conn, String sql, String db) throws SQLException {
    return new ClientPreparedStatement(conn, sql, db);
  }
  
  protected static ClientPreparedStatement getInstance(JdbcConnection conn, String sql, String db, QueryInfo cachedQueryInfo) throws SQLException {
    return new ClientPreparedStatement(conn, sql, db, cachedQueryInfo);
  }
  
  protected void initQuery() {
    this.query = (Query)new ClientPreparedQuery(this.session);
  }
  
  protected ClientPreparedStatement(JdbcConnection conn, String db) throws SQLException {
    super(conn, db);
    setPoolable(true);
    this.compensateForOnDuplicateKeyUpdate = ((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.compensateOnDuplicateKeyUpdateCounts).getValue()).booleanValue();
  }
  
  public ClientPreparedStatement(JdbcConnection conn, String sql, String db) throws SQLException {
    this(conn, sql, db, (QueryInfo)null);
  }
  
  public ClientPreparedStatement(JdbcConnection conn, String sql, String db, QueryInfo cachedQueryInfo) throws SQLException {
    this(conn, db);
    try {
      ((PreparedQuery)this.query).checkNullOrEmptyQuery(sql);
      ((PreparedQuery)this.query).setOriginalSql(sql);
      ((PreparedQuery)this.query).setQueryInfo((cachedQueryInfo != null) ? cachedQueryInfo : new QueryInfo(sql, (Session)this.session, this.charEncoding));
    } catch (CJException e) {
      throw SQLExceptionsMapping.translateException(e, this.exceptionInterceptor);
    } 
    this.doPingInstead = sql.startsWith("/* ping */");
    initializeFromQueryInfo();
  }
  
  public QueryBindings getQueryBindings() {
    return ((PreparedQuery)this.query).getQueryBindings();
  }
  
  public String toString() {
    try {
      StringBuilder buf = new StringBuilder();
      buf.append(getClass().getName());
      buf.append(": ");
      buf.append(((PreparedQuery)this.query).asSql());
      return buf.toString();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void addBatch() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        QueryBindings queryBindings = ((PreparedQuery)this.query).getQueryBindings();
        queryBindings.checkAllParametersSet();
        this.query.addBatch(queryBindings.clone());
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void addBatch(String sql) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        this.batchHasPlainStatements = true;
        super.addBatch(sql);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void clearBatch() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        this.batchHasPlainStatements = false;
        super.clearBatch();
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void clearParameters() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        for (BindValue bv : ((PreparedQuery)this.query).getQueryBindings().getBindValues())
          bv.reset(); 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected boolean checkReadOnlySafeStatement() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        return (QueryInfo.isReadOnlySafeQuery(((PreparedQuery)this.query).getOriginalSql(), this.session.getServerSession().isNoBackslashEscapesSet()) || 
          !this.connection.isReadOnly());
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean execute() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        JdbcConnection locallyScopedConn = this.connection;
        if (!this.doPingInstead && !checkReadOnlySafeStatement())
          throw SQLError.createSQLException(Messages.getString("PreparedStatement.20") + Messages.getString("PreparedStatement.21"), "S1009", this.exceptionInterceptor); 
        ResultSetInternalMethods rs = null;
        this.lastQueryIsOnDupKeyUpdate = false;
        if (this.retrieveGeneratedKeys)
          this.lastQueryIsOnDupKeyUpdate = containsOnDuplicateKeyUpdate(); 
        this.batchedGeneratedKeys = null;
        resetCancelledState();
        implicitlyCloseAllOpenResults();
        clearWarnings();
        if (this.doPingInstead) {
          doPingInstead();
          return true;
        } 
        setupStreamingTimeout(locallyScopedConn);
        Message sendPacket = ((PreparedQuery)this.query).fillSendPacket(((PreparedQuery)this.query).getQueryBindings());
        String oldDb = null;
        if (!locallyScopedConn.getDatabase().equals(getCurrentDatabase())) {
          oldDb = locallyScopedConn.getDatabase();
          locallyScopedConn.setDatabase(getCurrentDatabase());
        } 
        CachedResultSetMetaData cachedMetadata = null;
        boolean cacheResultSetMetadata = ((Boolean)locallyScopedConn.getPropertySet().getBooleanProperty(PropertyKey.cacheResultSetMetadata).getValue()).booleanValue();
        if (cacheResultSetMetadata)
          cachedMetadata = locallyScopedConn.getCachedMetaData(((PreparedQuery)this.query).getOriginalSql()); 
        locallyScopedConn.setSessionMaxRows((getQueryInfo().getFirstStmtChar() == 'S') ? this.maxRows : -1);
        rs = executeInternal(this.maxRows, sendPacket, createStreamingResultSet(), (getQueryInfo().getFirstStmtChar() == 'S'), (ColumnDefinition)cachedMetadata, false);
        if (cachedMetadata != null) {
          locallyScopedConn.initializeResultsMetadataFromCache(((PreparedQuery)this.query).getOriginalSql(), cachedMetadata, rs);
        } else if (rs.hasRows() && cacheResultSetMetadata) {
          locallyScopedConn.initializeResultsMetadataFromCache(((PreparedQuery)this.query).getOriginalSql(), (CachedResultSetMetaData)null, rs);
        } 
        if (this.retrieveGeneratedKeys)
          rs.setFirstCharOfQuery(getQueryInfo().getFirstStmtChar()); 
        if (oldDb != null)
          locallyScopedConn.setDatabase(oldDb); 
        if (rs != null) {
          this.lastInsertId = rs.getUpdateID();
          this.results = rs;
        } 
        return (rs != null && rs.hasRows());
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected long[] executeBatchInternal() throws SQLException {
    synchronized (checkClosed().getConnectionMutex()) {
      if (this.connection.isReadOnly())
        throw new SQLException(Messages.getString("PreparedStatement.25") + Messages.getString("PreparedStatement.26"), "S1009"); 
      if (this.query.getBatchedArgs() == null || this.query.getBatchedArgs().size() == 0)
        return new long[0]; 
      int batchTimeout = getTimeoutInMillis();
      setTimeoutInMillis(0);
      resetCancelledState();
      try {
        statementBegins();
        clearWarnings();
        if (!this.batchHasPlainStatements && ((Boolean)this.rewriteBatchedStatements.getValue()).booleanValue()) {
          if (getQueryInfo().isRewritableWithMultiValuesClause())
            return executeBatchWithMultiValuesClause(batchTimeout); 
          if (!this.batchHasPlainStatements && this.query.getBatchedArgs() != null && this.query
            .getBatchedArgs().size() > 3)
            return executePreparedBatchAsMultiStatement(batchTimeout); 
        } 
        return executeBatchSerially(batchTimeout);
      } finally {
        this.query.getStatementExecuting().set(false);
        clearBatch();
      } 
    } 
  }
  
  protected long[] executePreparedBatchAsMultiStatement(int batchTimeout) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (this.batchedValuesClause == null)
          this.batchedValuesClause = ((PreparedQuery)this.query).getOriginalSql() + ";"; 
        JdbcConnection locallyScopedConn = this.connection;
        boolean multiQueriesEnabled = ((Boolean)locallyScopedConn.getPropertySet().getBooleanProperty(PropertyKey.allowMultiQueries).getValue()).booleanValue();
        CancelQueryTask timeoutTask = null;
        try {
          clearWarnings();
          int numBatchedArgs = this.query.getBatchedArgs().size();
          if (this.retrieveGeneratedKeys)
            this.batchedGeneratedKeys = new ArrayList<>(numBatchedArgs); 
          int numValuesPerBatch = ((PreparedQuery)this.query).computeBatchSize(numBatchedArgs);
          if (numBatchedArgs < numValuesPerBatch)
            numValuesPerBatch = numBatchedArgs; 
          PreparedStatement batchedStatement = null;
          int batchedParamIndex = 1;
          int numberToExecuteAsMultiValue = 0;
          int batchCounter = 0;
          int updateCountCounter = 0;
        } finally {
          stopQueryTimer(timeoutTask, false, false);
          resetCancelledState();
          if (!multiQueriesEnabled)
            ((NativeSession)locallyScopedConn.getSession()).disableMultiQueries(); 
          clearBatch();
        } 
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected int setOneBatchedParameterSet(PreparedStatement batchedStatement, int batchedParamIndex, Object paramSet) throws SQLException {
    BindValue[] bindValues = ((QueryBindings)paramSet).getBindValues();
    QueryBindings batchedStatementBindings = ((PreparedQuery)((ClientPreparedStatement)batchedStatement).getQuery()).getQueryBindings();
    for (int j = 0; j < bindValues.length; j++)
      batchedStatementBindings.setFromBindValue(batchedParamIndex++ - 1, bindValues[j]); 
    return batchedParamIndex;
  }
  
  private String generateMultiStatementForBatch(int numBatches) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        String origSql = ((PreparedQuery)this.query).getOriginalSql();
        StringBuilder newStatementSql = new StringBuilder((origSql.length() + 1) * numBatches);
        newStatementSql.append(origSql);
        for (int i = 0; i < numBatches - 1; i++) {
          newStatementSql.append(';');
          newStatementSql.append(origSql);
        } 
        return newStatementSql.toString();
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected long[] executeBatchWithMultiValuesClause(int batchTimeout) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        JdbcConnection locallyScopedConn = this.connection;
        int numBatchedArgs = this.query.getBatchedArgs().size();
        if (this.retrieveGeneratedKeys)
          this.batchedGeneratedKeys = new ArrayList<>(numBatchedArgs); 
        int numValuesPerBatch = ((PreparedQuery)this.query).computeBatchSize(numBatchedArgs);
        if (numBatchedArgs < numValuesPerBatch)
          numValuesPerBatch = numBatchedArgs; 
        JdbcPreparedStatement batchedStatement = null;
        int batchedParamIndex = 1;
        long updateCountRunningTotal = 0L;
        int numberToExecuteAsMultiValue = 0;
        int batchCounter = 0;
        CancelQueryTask timeoutTask = null;
        SQLException sqlEx = null;
        long[] updateCounts = new long[numBatchedArgs];
        try {
        
        } finally {
          stopQueryTimer(timeoutTask, false, false);
          resetCancelledState();
        } 
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected long[] executeBatchSerially(int batchTimeout) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (this.connection == null)
          checkClosed(); 
        long[] updateCounts = null;
        if (this.query.getBatchedArgs() != null) {
          int nbrCommands = this.query.getBatchedArgs().size();
          updateCounts = new long[nbrCommands];
          for (int i = 0; i < nbrCommands; i++)
            updateCounts[i] = -3L; 
          SQLException sqlEx = null;
          CancelQueryTask timeoutTask = null;
          try {
            timeoutTask = startQueryTimer(this, batchTimeout);
            if (this.retrieveGeneratedKeys)
              this.batchedGeneratedKeys = new ArrayList<>(nbrCommands); 
            int batchCommandIndex = ((PreparedQuery)this.query).getBatchCommandIndex();
            for (batchCommandIndex = 0; batchCommandIndex < nbrCommands; batchCommandIndex++) {
              ((PreparedQuery)this.query).setBatchCommandIndex(batchCommandIndex);
              Object arg = this.query.getBatchedArgs().get(batchCommandIndex);
              try {
                if (arg instanceof String) {
                  updateCounts[batchCommandIndex] = executeUpdateInternal((String)arg, true, this.retrieveGeneratedKeys);
                  getBatchedGeneratedKeys((this.results.getFirstCharOfQuery() == 'I' && containsOnDuplicateKeyInString((String)arg)) ? 1 : 0);
                } else {
                  QueryBindings queryBindings = (QueryBindings)arg;
                  updateCounts[batchCommandIndex] = executeUpdateInternal(queryBindings, true);
                  getBatchedGeneratedKeys(containsOnDuplicateKeyUpdate() ? 1 : 0);
                } 
              } catch (SQLException ex) {
                updateCounts[batchCommandIndex] = -3L;
                if (this.continueBatchOnError && !(ex instanceof com.mysql.cj.jdbc.exceptions.MySQLTimeoutException) && !(ex instanceof com.mysql.cj.jdbc.exceptions.MySQLStatementCancelledException) && 
                  !hasDeadlockOrTimeoutRolledBackTx(ex)) {
                  sqlEx = ex;
                } else {
                  long[] newUpdateCounts = new long[batchCommandIndex];
                  System.arraycopy(updateCounts, 0, newUpdateCounts, 0, batchCommandIndex);
                  throw SQLError.createBatchUpdateException(ex, newUpdateCounts, this.exceptionInterceptor);
                } 
              } 
            } 
            if (sqlEx != null)
              throw SQLError.createBatchUpdateException(sqlEx, updateCounts, this.exceptionInterceptor); 
          } catch (NullPointerException npe) {
            try {
              checkClosed();
            } catch (StatementIsClosedException connectionClosedEx) {
              int batchCommandIndex = ((PreparedQuery)this.query).getBatchCommandIndex();
              updateCounts[batchCommandIndex] = -3L;
              long[] newUpdateCounts = new long[batchCommandIndex];
              System.arraycopy(updateCounts, 0, newUpdateCounts, 0, batchCommandIndex);
              throw SQLError.createBatchUpdateException(SQLExceptionsMapping.translateException(connectionClosedEx), newUpdateCounts, this.exceptionInterceptor);
            } 
            throw npe;
          } finally {
            ((PreparedQuery)this.query).setBatchCommandIndex(-1);
            stopQueryTimer(timeoutTask, false, false);
            resetCancelledState();
          } 
        } 
        return (updateCounts != null) ? updateCounts : new long[0];
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected <M extends Message> ResultSetInternalMethods executeInternal(int maxRowsToRetrieve, M sendPacket, boolean createStreamingResultSet, boolean queryIsSelectOnly, ColumnDefinition metadata, boolean isBatch) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ResultSetInternalMethods rs;
        JdbcConnection locallyScopedConnection = this.connection;
        ((PreparedQuery)this.query).getQueryBindings()
          .setNumberOfExecutions(((PreparedQuery)this.query).getQueryBindings().getNumberOfExecutions() + 1);
        CancelQueryTask timeoutTask = null;
        try {
          timeoutTask = startQueryTimer(this, getTimeoutInMillis());
          if (!isBatch)
            statementBegins(); 
          rs = (ResultSetInternalMethods)((NativeSession)locallyScopedConnection.getSession()).execSQL(this, null, maxRowsToRetrieve, (NativePacketPayload)sendPacket, createStreamingResultSet, 
              getResultSetFactory(), metadata, isBatch);
          if (timeoutTask != null) {
            stopQueryTimer(timeoutTask, true, true);
            timeoutTask = null;
          } 
        } finally {
          if (!isBatch)
            this.query.getStatementExecuting().set(false); 
          stopQueryTimer(timeoutTask, false, false);
        } 
        return rs;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ResultSet executeQuery() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        JdbcConnection locallyScopedConn = this.connection;
        if (!this.doPingInstead) {
          QueryReturnType queryReturnType = getQueryInfo().getQueryReturnType();
          if (queryReturnType != QueryReturnType.PRODUCES_RESULT_SET && queryReturnType != QueryReturnType.MAY_PRODUCE_RESULT_SET)
            throw SQLError.createSQLException(Messages.getString("Statement.57"), "S1009", 
                getExceptionInterceptor()); 
        } 
        this.batchedGeneratedKeys = null;
        resetCancelledState();
        implicitlyCloseAllOpenResults();
        clearWarnings();
        if (this.doPingInstead) {
          doPingInstead();
          return (ResultSet)this.results;
        } 
        setupStreamingTimeout(locallyScopedConn);
        Message sendPacket = ((PreparedQuery)this.query).fillSendPacket(((PreparedQuery)this.query).getQueryBindings());
        String oldDb = null;
        if (!locallyScopedConn.getDatabase().equals(getCurrentDatabase())) {
          oldDb = locallyScopedConn.getDatabase();
          locallyScopedConn.setDatabase(getCurrentDatabase());
        } 
        CachedResultSetMetaData cachedMetadata = null;
        boolean cacheResultSetMetadata = ((Boolean)locallyScopedConn.getPropertySet().getBooleanProperty(PropertyKey.cacheResultSetMetadata).getValue()).booleanValue();
        String origSql = ((PreparedQuery)this.query).getOriginalSql();
        if (cacheResultSetMetadata)
          cachedMetadata = locallyScopedConn.getCachedMetaData(origSql); 
        locallyScopedConn.setSessionMaxRows(this.maxRows);
        this.results = executeInternal(this.maxRows, sendPacket, createStreamingResultSet(), true, (ColumnDefinition)cachedMetadata, false);
        if (oldDb != null)
          locallyScopedConn.setDatabase(oldDb); 
        if (cachedMetadata != null) {
          locallyScopedConn.initializeResultsMetadataFromCache(origSql, cachedMetadata, this.results);
        } else if (cacheResultSetMetadata) {
          locallyScopedConn.initializeResultsMetadataFromCache(origSql, (CachedResultSetMetaData)null, this.results);
        } 
        this.lastInsertId = this.results.getUpdateID();
        return (ResultSet)this.results;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int executeUpdate() throws SQLException {
    try {
      return Util.truncateAndConvertToInt(executeLargeUpdate());
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected long executeUpdateInternal(boolean clearBatchedGeneratedKeysAndWarnings, boolean isBatch) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (clearBatchedGeneratedKeysAndWarnings) {
          clearWarnings();
          this.batchedGeneratedKeys = null;
        } 
        return executeUpdateInternal(((PreparedQuery)this.query).getQueryBindings(), isBatch);
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected long executeUpdateInternal(QueryBindings bindings, boolean isReallyBatch) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        JdbcConnection locallyScopedConn = this.connection;
        if (locallyScopedConn.isReadOnly(false))
          throw SQLError.createSQLException(Messages.getString("PreparedStatement.34") + Messages.getString("PreparedStatement.35"), "S1009", this.exceptionInterceptor); 
        if (!isNonResultSetProducingQuery())
          throw SQLError.createSQLException(Messages.getString("PreparedStatement.37"), "01S03", this.exceptionInterceptor); 
        resetCancelledState();
        implicitlyCloseAllOpenResults();
        ResultSetInternalMethods rs = null;
        Message sendPacket = ((PreparedQuery)this.query).fillSendPacket(bindings);
        String oldDb = null;
        if (!locallyScopedConn.getDatabase().equals(getCurrentDatabase())) {
          oldDb = locallyScopedConn.getDatabase();
          locallyScopedConn.setDatabase(getCurrentDatabase());
        } 
        locallyScopedConn.setSessionMaxRows(-1);
        rs = executeInternal(-1, sendPacket, false, false, (ColumnDefinition)null, isReallyBatch);
        if (this.retrieveGeneratedKeys)
          rs.setFirstCharOfQuery(getQueryInfo().getFirstStmtChar()); 
        if (oldDb != null)
          locallyScopedConn.setDatabase(oldDb); 
        this.results = rs;
        this.updateCount = rs.getUpdateCount();
        if (containsOnDuplicateKeyUpdate() && this.compensateForOnDuplicateKeyUpdate && (
          this.updateCount == 2L || this.updateCount == 0L))
          this.updateCount = 1L; 
        this.lastInsertId = rs.getUpdateID();
        return this.updateCount;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected boolean containsOnDuplicateKeyUpdate() {
    return getQueryInfo().containsOnDuplicateKeyUpdate();
  }
  
  protected ClientPreparedStatement prepareBatchedInsertSQL(JdbcConnection localConn, int numBatches) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ClientPreparedStatement pstmt = new ClientPreparedStatement(localConn, "Rewritten batch of: " + ((PreparedQuery)this.query).getOriginalSql(), getCurrentDatabase(), getQueryInfo().getQueryInfoForBatch(numBatches));
        pstmt.setRetrieveGeneratedKeys(this.retrieveGeneratedKeys);
        pstmt.rewrittenBatchSize = numBatches;
        getQueryAttributesBindings().runThroughAll(a -> pstmt.setAttribute(a.getName(), a.getValue()));
        return pstmt;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected void setRetrieveGeneratedKeys(boolean flag) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        this.retrieveGeneratedKeys = flag;
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public byte[] getBytesRepresentation(int parameterIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        return ((PreparedQuery)this.query).getQueryBindings().getBytesRepresentation(getCoreParameterIndex(parameterIndex));
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ResultSetMetaData getMetaData() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (!isResultSetProducingQuery())
          return null; 
        JdbcPreparedStatement mdStmt = null;
        ResultSet mdRs = null;
        if (this.pstmtResultMetaData == null)
          try {
            mdStmt = new ClientPreparedStatement(this.connection, ((PreparedQuery)this.query).getOriginalSql(), getCurrentDatabase(), getQueryInfo());
            mdStmt.setMaxRows(1);
            int paramCount = ((PreparedQuery)this.query).getParameterCount();
            for (int i = 1; i <= paramCount; i++)
              mdStmt.setString(i, (String)null); 
            boolean hadResults = mdStmt.execute();
            if (hadResults) {
              mdRs = mdStmt.getResultSet();
              this.pstmtResultMetaData = mdRs.getMetaData();
            } else {
              this
                
                .pstmtResultMetaData = (ResultSetMetaData)new ResultSetMetaData((Session)this.session, new com.mysql.cj.result.Field[0], ((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.useOldAliasMetadataBehavior).getValue()).booleanValue(), ((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.yearIsDateType).getValue()).booleanValue(), this.exceptionInterceptor);
            } 
          } finally {
            SQLException sqlExRethrow = null;
            if (mdRs != null) {
              try {
                mdRs.close();
              } catch (SQLException sqlEx) {
                sqlExRethrow = sqlEx;
              } 
              mdRs = null;
            } 
            if (mdStmt != null) {
              try {
                mdStmt.close();
              } catch (SQLException sqlEx) {
                sqlExRethrow = sqlEx;
              } 
              mdStmt = null;
            } 
            if (sqlExRethrow != null)
              throw sqlExRethrow; 
          }  
        return this.pstmtResultMetaData;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected boolean isResultSetProducingQuery() {
    QueryReturnType queryReturnType = getQueryInfo().getQueryReturnType();
    return (queryReturnType == QueryReturnType.PRODUCES_RESULT_SET || queryReturnType == QueryReturnType.MAY_PRODUCE_RESULT_SET);
  }
  
  private boolean isNonResultSetProducingQuery() {
    QueryReturnType queryReturnType = getQueryInfo().getQueryReturnType();
    return (queryReturnType == QueryReturnType.DOES_NOT_PRODUCE_RESULT_SET || queryReturnType == QueryReturnType.MAY_PRODUCE_RESULT_SET);
  }
  
  public ParameterMetaData getParameterMetaData() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (this.parameterMetaData == null)
          if (((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.generateSimpleParameterMetadata).getValue()).booleanValue()) {
            this.parameterMetaData = new MysqlParameterMetadata(((PreparedQuery)this.query).getParameterCount());
          } else {
            this.parameterMetaData = new MysqlParameterMetadata((Session)this.session, null, ((PreparedQuery)this.query).getParameterCount(), this.exceptionInterceptor);
          }  
        return this.parameterMetaData;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public QueryInfo getQueryInfo() {
    return ((PreparedQuery)this.query).getQueryInfo();
  }
  
  private void initializeFromQueryInfo() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        int parameterCount = (getQueryInfo().getStaticSqlParts()).length - 1;
        ((PreparedQuery)this.query).setParameterCount(parameterCount);
        ((PreparedQuery)this.query).setQueryBindings((QueryBindings)new NativeQueryBindings(parameterCount, (Session)this.session, com.mysql.cj.NativeQueryBindValue::new));
        clearParameters();
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isNull(int paramIndex) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        return ((PreparedQuery)this.query).getQueryBindings().getBindValues()[getCoreParameterIndex(paramIndex)].isNull();
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void realClose(boolean calledExplicitly, boolean closeOpenResults) throws SQLException {
    JdbcConnection locallyScopedConn = this.connection;
    if (locallyScopedConn == null)
      return; 
    synchronized (locallyScopedConn.getConnectionMutex()) {
      if (this.isClosed)
        return; 
      if (this.useUsageAdvisor) {
        QueryBindings qb = ((PreparedQuery)this.query).getQueryBindings();
        if (qb == null || qb.getNumberOfExecutions() <= 1)
          this.session.getProfilerEventHandler().processEvent((byte)0, (Session)this.session, this, null, 0L, new Throwable(), 
              Messages.getString("PreparedStatement.43")); 
      } 
      super.realClose(calledExplicitly, closeOpenResults);
      ((PreparedQuery)this.query).setOriginalSql(null);
      ((PreparedQuery)this.query).setQueryBindings(null);
    } 
  }
  
  public String getPreparedSql() {
    synchronized (checkClosed().getConnectionMutex()) {
      if (this.rewrittenBatchSize == 0)
        return ((PreparedQuery)this.query).getOriginalSql(); 
      return getQueryInfo().getSqlForBatch();
    } 
  }
  
  public int getUpdateCount() throws SQLException {
    try {
      int count = super.getUpdateCount();
      if (containsOnDuplicateKeyUpdate() && this.compensateForOnDuplicateKeyUpdate && (
        count == 2 || count == 0))
        count = 1; 
      return count;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public long executeLargeUpdate() throws SQLException {
    try {
      return executeUpdateInternal(true, false);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public ParameterBindings getParameterBindings() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        return new ParameterBindingsImpl((PreparedQuery)this.query, (Session)this.session, this.resultSetFactory);
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected int getParameterIndexOffset() {
    return 0;
  }
  
  protected void checkBounds(int paramIndex, int parameterIndexOffset) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (paramIndex < 1)
          throw SQLError.createSQLException(Messages.getString("PreparedStatement.49") + paramIndex + Messages.getString("PreparedStatement.50"), "S1009", this.exceptionInterceptor); 
        if (paramIndex > ((PreparedQuery)this.query).getParameterCount())
          throw SQLError.createSQLException(
              Messages.getString("PreparedStatement.51") + paramIndex + Messages.getString("PreparedStatement.52") + ((PreparedQuery)this.query)
              .getParameterCount() + Messages.getString("PreparedStatement.53"), "S1009", this.exceptionInterceptor); 
        if (parameterIndexOffset == -1 && paramIndex == 1)
          throw SQLError.createSQLException(Messages.getString("PreparedStatement.63"), "S1009", this.exceptionInterceptor); 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public final int getCoreParameterIndex(int paramIndex) throws SQLException {
    int parameterIndexOffset = getParameterIndexOffset();
    checkBounds(paramIndex, parameterIndexOffset);
    return paramIndex - 1 + parameterIndexOffset;
  }
  
  public void setArray(int i, Array x) throws SQLException {
    try {
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setAsciiStream(getCoreParameterIndex(parameterIndex), x, -1);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setAsciiStream(getCoreParameterIndex(parameterIndex), x, length);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setAsciiStream(getCoreParameterIndex(parameterIndex), x, (int)length);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setBigDecimal(getCoreParameterIndex(parameterIndex), x);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setBinaryStream(getCoreParameterIndex(parameterIndex), x, -1);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setBinaryStream(getCoreParameterIndex(parameterIndex), x, length);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setBinaryStream(getCoreParameterIndex(parameterIndex), x, (int)length);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setBlob(int i, Blob x) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setBlob(getCoreParameterIndex(i), x);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setBinaryStream(getCoreParameterIndex(parameterIndex), inputStream, -1);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setBinaryStream(getCoreParameterIndex(parameterIndex), inputStream, (int)length);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setBoolean(int parameterIndex, boolean x) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setBoolean(getCoreParameterIndex(parameterIndex), x);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setByte(int parameterIndex, byte x) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setByte(getCoreParameterIndex(parameterIndex), x);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setBytes(int parameterIndex, byte[] x) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setBytes(getCoreParameterIndex(parameterIndex), x, true);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setBytes(int parameterIndex, byte[] x, boolean escapeIfNeeded) throws SQLException {
    synchronized (checkClosed().getConnectionMutex()) {
      ((PreparedQuery)this.query).getQueryBindings().setBytes(getCoreParameterIndex(parameterIndex), x, escapeIfNeeded);
    } 
  }
  
  public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setCharacterStream(getCoreParameterIndex(parameterIndex), reader, -1);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setCharacterStream(getCoreParameterIndex(parameterIndex), reader, length);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setCharacterStream(getCoreParameterIndex(parameterIndex), reader, (int)length);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setClob(int parameterIndex, Reader reader) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setCharacterStream(getCoreParameterIndex(parameterIndex), reader, -1);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setCharacterStream(getCoreParameterIndex(parameterIndex), reader, (int)length);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setClob(int i, Clob x) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setClob(getCoreParameterIndex(i), x);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setDate(int parameterIndex, Date x) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setDate(getCoreParameterIndex(parameterIndex), x, null);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setDate(getCoreParameterIndex(parameterIndex), x, cal);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setDouble(int parameterIndex, double x) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setDouble(getCoreParameterIndex(parameterIndex), x);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setFloat(int parameterIndex, float x) throws SQLException {
    try {
      ((PreparedQuery)this.query).getQueryBindings().setFloat(getCoreParameterIndex(parameterIndex), x);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setInt(int parameterIndex, int x) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setInt(getCoreParameterIndex(parameterIndex), x);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setLong(int parameterIndex, long x) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setLong(getCoreParameterIndex(parameterIndex), x);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setBigInteger(int parameterIndex, BigInteger x) throws SQLException {
    synchronized (checkClosed().getConnectionMutex()) {
      ((PreparedQuery)this.query).getQueryBindings().setBigInteger(getCoreParameterIndex(parameterIndex), x);
    } 
  }
  
  public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setNCharacterStream(getCoreParameterIndex(parameterIndex), value, -1L);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setNCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setNCharacterStream(getCoreParameterIndex(parameterIndex), reader, length);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setNClob(int parameterIndex, Reader reader) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setNCharacterStream(getCoreParameterIndex(parameterIndex), reader, -1L);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setNCharacterStream(getCoreParameterIndex(parameterIndex), reader, length);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setNClob(int parameterIndex, NClob value) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setNClob(getCoreParameterIndex(parameterIndex), value);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setNString(int parameterIndex, String x) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setNString(getCoreParameterIndex(parameterIndex), x);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setNull(int parameterIndex, int sqlType) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setNull(getCoreParameterIndex(parameterIndex));
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setNull(getCoreParameterIndex(parameterIndex));
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setNull(int parameterIndex, MysqlType mysqlType) throws SQLException {
    setNull(parameterIndex, mysqlType.getJdbcType());
  }
  
  public void setObject(int parameterIndex, Object parameterObj) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setObject(getCoreParameterIndex(parameterIndex), parameterObj);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setObject(int parameterIndex, Object parameterObj, int targetSqlType) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        try {
          ((PreparedQuery)this.query).getQueryBindings().setObject(getCoreParameterIndex(parameterIndex), parameterObj, 
              MysqlType.getByJdbcType(targetSqlType), -1);
        } catch (FeatureNotAvailableException nae) {
          throw SQLError.createSQLFeatureNotSupportedException(Messages.getString("Statement.UnsupportedSQLType") + JDBCType.valueOf(targetSqlType), "S1C00", this.exceptionInterceptor);
        } 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setObject(int parameterIndex, Object parameterObj, SQLType targetSqlType) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (targetSqlType instanceof MysqlType) {
          ((PreparedQuery)this.query).getQueryBindings().setObject(getCoreParameterIndex(parameterIndex), parameterObj, (MysqlType)targetSqlType, -1);
        } else {
          setObject(parameterIndex, parameterObj, targetSqlType.getVendorTypeNumber().intValue());
        } 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setObject(int parameterIndex, Object parameterObj, int targetSqlType, int scale) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        try {
          ((PreparedQuery)this.query).getQueryBindings().setObject(getCoreParameterIndex(parameterIndex), parameterObj, 
              MysqlType.getByJdbcType(targetSqlType), scale);
        } catch (FeatureNotAvailableException nae) {
          throw SQLError.createSQLFeatureNotSupportedException(Messages.getString("Statement.UnsupportedSQLType") + JDBCType.valueOf(targetSqlType), "S1C00", this.exceptionInterceptor);
        } 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (targetSqlType instanceof MysqlType) {
          ((PreparedQuery)this.query).getQueryBindings().setObject(getCoreParameterIndex(parameterIndex), x, (MysqlType)targetSqlType, scaleOrLength);
        } else {
          setObject(parameterIndex, x, targetSqlType.getVendorTypeNumber().intValue(), scaleOrLength);
        } 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setRef(int i, Ref x) throws SQLException {
    try {
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setRowId(int parameterIndex, RowId x) throws SQLException {
    try {
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setShort(int parameterIndex, short x) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setShort(getCoreParameterIndex(parameterIndex), x);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
    try {
      if (xmlObject == null) {
        setNull(parameterIndex, MysqlType.VARCHAR);
      } else {
        setCharacterStream(parameterIndex, ((MysqlSQLXML)xmlObject).serializeAsCharacterStream());
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setString(int parameterIndex, String x) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setString(getCoreParameterIndex(parameterIndex), x);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setTime(int parameterIndex, Time x) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setTime(getCoreParameterIndex(parameterIndex), x, null);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setTime(getCoreParameterIndex(parameterIndex), x, cal);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setTimestamp(getCoreParameterIndex(parameterIndex), x, null, null, MysqlType.TIMESTAMP);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        ((PreparedQuery)this.query).getQueryBindings().setTimestamp(getCoreParameterIndex(parameterIndex), x, cal, null, MysqlType.TIMESTAMP);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  @Deprecated
  public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
    try {
      setBinaryStream(parameterIndex, x, length);
      ((PreparedQuery)this.query).getQueryBindings().getBindValues()[getCoreParameterIndex(parameterIndex)].setMysqlType(MysqlType.TEXT);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setURL(int parameterIndex, URL arg) throws SQLException {
    try {
      if (arg == null) {
        setNull(parameterIndex, MysqlType.VARCHAR);
      } else {
        setString(parameterIndex, arg.toString());
        ((PreparedQuery)this.query).getQueryBindings().getBindValues()[getCoreParameterIndex(parameterIndex)].setMysqlType(MysqlType.VARCHAR);
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
}
