package com.mysql.cj.jdbc.result;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlConnection;
import com.mysql.cj.MysqlType;
import com.mysql.cj.NativeSession;
import com.mysql.cj.Query;
import com.mysql.cj.Session;
import com.mysql.cj.WarningListener;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.jdbc.Blob;
import com.mysql.cj.jdbc.BlobFromLocator;
import com.mysql.cj.jdbc.Clob;
import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.JdbcPreparedStatement;
import com.mysql.cj.jdbc.JdbcPropertySet;
import com.mysql.cj.jdbc.JdbcStatement;
import com.mysql.cj.jdbc.MysqlSQLXML;
import com.mysql.cj.jdbc.NClob;
import com.mysql.cj.jdbc.StatementImpl;
import com.mysql.cj.jdbc.exceptions.NotUpdatable;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.log.ProfilerEventHandler;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.ResultsetRows;
import com.mysql.cj.protocol.a.result.NativeResultset;
import com.mysql.cj.protocol.a.result.OkPacket;
import com.mysql.cj.result.BigDecimalValueFactory;
import com.mysql.cj.result.BinaryStreamValueFactory;
import com.mysql.cj.result.BooleanValueFactory;
import com.mysql.cj.result.ByteValueFactory;
import com.mysql.cj.result.DoubleValueFactory;
import com.mysql.cj.result.DurationValueFactory;
import com.mysql.cj.result.Field;
import com.mysql.cj.result.FloatValueFactory;
import com.mysql.cj.result.IntegerValueFactory;
import com.mysql.cj.result.LocalDateTimeValueFactory;
import com.mysql.cj.result.LocalDateValueFactory;
import com.mysql.cj.result.LocalTimeValueFactory;
import com.mysql.cj.result.LongValueFactory;
import com.mysql.cj.result.OffsetDateTimeValueFactory;
import com.mysql.cj.result.OffsetTimeValueFactory;
import com.mysql.cj.result.Row;
import com.mysql.cj.result.ShortValueFactory;
import com.mysql.cj.result.SqlDateValueFactory;
import com.mysql.cj.result.SqlTimeValueFactory;
import com.mysql.cj.result.SqlTimestampValueFactory;
import com.mysql.cj.result.StringValueFactory;
import com.mysql.cj.result.UtilCalendarValueFactory;
import com.mysql.cj.result.ValueFactory;
import com.mysql.cj.result.ZonedDateTimeValueFactory;
import com.mysql.cj.util.LogUtils;
import com.mysql.cj.util.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ResultSetImpl extends NativeResultset implements ResultSetInternalMethods, WarningListener {
  static int resultCounter = 1;
  
  protected String db = null;
  
  protected boolean[] columnUsed = null;
  
  protected volatile JdbcConnection connection;
  
  protected NativeSession session = null;
  
  protected int currentRow = -1;
  
  protected ProfilerEventHandler eventSink = null;
  
  Calendar fastDefaultCal = null;
  
  Calendar fastClientCal = null;
  
  protected int fetchDirection = 1000;
  
  protected int fetchSize = 0;
  
  protected char firstCharOfQuery;
  
  protected boolean isClosed = false;
  
  private StatementImpl owningStatement;
  
  private String pointOfOrigin;
  
  protected int resultSetConcurrency = 0;
  
  protected int resultSetType = 0;
  
  JdbcPreparedStatement statementUsedForFetchingRows;
  
  protected boolean useUsageAdvisor = false;
  
  protected boolean gatherPerfMetrics = false;
  
  protected boolean scrollTolerant = false;
  
  protected SQLWarning warningChain = null;
  
  protected Statement wrapperStatement;
  
  private boolean padCharsWithSpace = false;
  
  private boolean useColumnNamesInFindColumn;
  
  private ExceptionInterceptor exceptionInterceptor;
  
  private ValueFactory<Boolean> booleanValueFactory;
  
  private ValueFactory<Byte> byteValueFactory;
  
  private ValueFactory<Short> shortValueFactory;
  
  private ValueFactory<Integer> integerValueFactory;
  
  private ValueFactory<Long> longValueFactory;
  
  private ValueFactory<Float> floatValueFactory;
  
  private ValueFactory<Double> doubleValueFactory;
  
  private ValueFactory<BigDecimal> bigDecimalValueFactory;
  
  private ValueFactory<InputStream> binaryStreamValueFactory;
  
  private ValueFactory<Time> defaultTimeValueFactory;
  
  private ValueFactory<Timestamp> defaultTimestampValueFactory;
  
  private ValueFactory<Calendar> defaultUtilCalendarValueFactory;
  
  private ValueFactory<LocalDate> defaultLocalDateValueFactory;
  
  private ValueFactory<LocalDateTime> defaultLocalDateTimeValueFactory;
  
  private ValueFactory<LocalTime> defaultLocalTimeValueFactory;
  
  private ValueFactory<OffsetTime> defaultOffsetTimeValueFactory;
  
  private ValueFactory<OffsetDateTime> defaultOffsetDateTimeValueFactory;
  
  private ValueFactory<ZonedDateTime> defaultZonedDateTimeValueFactory;
  
  protected RuntimeProperty<Boolean> emulateLocators;
  
  protected boolean yearIsDateType = true;
  
  private boolean onValidRow;
  
  private String invalidRowReason;
  
  public ResultSetImpl(OkPacket ok, JdbcConnection conn, StatementImpl creatorStmt) {
    super(ok);
    this.onValidRow = false;
    this.invalidRowReason = null;
    this.connection = conn;
    this.owningStatement = creatorStmt;
    if (this.connection != null) {
      this.session = (NativeSession)conn.getSession();
      this.exceptionInterceptor = this.connection.getExceptionInterceptor();
      this.padCharsWithSpace = ((Boolean)this.connection.getPropertySet().getBooleanProperty(PropertyKey.padCharsWithSpace).getValue()).booleanValue();
    } 
  }
  
  public void initializeWithMetadata() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        initRowsWithMetadata();
        if (this.useUsageAdvisor) {
          this.columnUsed = new boolean[(this.columnDefinition.getFields()).length];
          this.pointOfOrigin = LogUtils.findCallingClassAndMethod(new Throwable());
          this.resultId = resultCounter++;
          this.eventSink = this.session.getProfilerEventHandler();
        } 
        if (this.gatherPerfMetrics) {
          this.session.getProtocol().getMetricsHolder().incrementNumberOfResultSetsCreated();
          Set<String> tableNamesSet = new HashSet<>();
          for (int i = 0; i < (this.columnDefinition.getFields()).length; i++) {
            Field f = this.columnDefinition.getFields()[i];
            String tableName = f.getOriginalTableName();
            if (tableName == null)
              tableName = f.getTableName(); 
            if (tableName != null) {
              if (this.connection.lowerCaseTableNames())
                tableName = tableName.toLowerCase(); 
              tableNamesSet.add(tableName);
            } 
          } 
          this.session.getProtocol().getMetricsHolder().reportNumberOfTablesAccessed(tableNamesSet.size());
        } 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean absolute(int row) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        boolean b;
        if (!hasRows())
          throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", getExceptionInterceptor()); 
        if (isStrictlyForwardOnly())
          throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly")); 
        if (this.rowData.size() == 0) {
          b = false;
        } else if (row == 0) {
          beforeFirst();
          b = false;
        } else if (row == 1) {
          b = first();
        } else if (row == -1) {
          b = last();
        } else if (row > this.rowData.size()) {
          afterLast();
          b = false;
        } else if (row < 0) {
          int newRowPosition = this.rowData.size() + row + 1;
          if (newRowPosition <= 0) {
            beforeFirst();
            b = false;
          } else {
            b = absolute(newRowPosition);
          } 
        } else {
          row--;
          this.rowData.setCurrentRow(row);
          this.thisRow = this.rowData.get(row);
          b = true;
        } 
        setRowPositionValidity();
        return b;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void afterLast() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (!hasRows())
          throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", getExceptionInterceptor()); 
        if (isStrictlyForwardOnly())
          throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly")); 
        if (this.rowData.size() != 0) {
          this.rowData.afterLast();
          this.thisRow = null;
        } 
        setRowPositionValidity();
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void beforeFirst() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (!hasRows())
          throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", getExceptionInterceptor()); 
        if (isStrictlyForwardOnly())
          throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly")); 
        if (this.rowData.size() == 0)
          return; 
        this.rowData.beforeFirst();
        this.thisRow = null;
        setRowPositionValidity();
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void cancelRowUpdates() throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected final JdbcConnection checkClosed() throws SQLException {
    JdbcConnection c = this.connection;
    if (c == null)
      throw SQLError.createSQLException(Messages.getString("ResultSet.Operation_not_allowed_after_ResultSet_closed_144"), "S1000", getExceptionInterceptor()); 
    return c;
  }
  
  protected final void checkColumnBounds(int columnIndex) throws SQLException {
    synchronized (checkClosed().getConnectionMutex()) {
      if (columnIndex < 1)
        throw SQLError.createSQLException(Messages.getString("ResultSet.Column_Index_out_of_range_low", new Object[] { Integer.valueOf(columnIndex), Integer.valueOf((this.columnDefinition.getFields()).length) }), "S1009", getExceptionInterceptor()); 
      if (columnIndex > (this.columnDefinition.getFields()).length)
        throw SQLError.createSQLException(Messages.getString("ResultSet.Column_Index_out_of_range_high", new Object[] { Integer.valueOf(columnIndex), Integer.valueOf((this.columnDefinition.getFields()).length) }), "S1009", getExceptionInterceptor()); 
      if (this.useUsageAdvisor)
        this.columnUsed[columnIndex - 1] = true; 
    } 
  }
  
  protected void checkRowPos() throws SQLException {
    checkClosed();
    if (!this.onValidRow)
      throw SQLError.createSQLException(this.invalidRowReason, "S1000", getExceptionInterceptor()); 
  }
  
  public ResultSetImpl(ResultsetRows tuples, JdbcConnection conn, StatementImpl creatorStmt) throws SQLException {
    this.onValidRow = false;
    this.invalidRowReason = null;
    this.connection = conn;
    this.session = (NativeSession)conn.getSession();
    this.db = (creatorStmt != null) ? creatorStmt.getCurrentDatabase() : conn.getDatabase();
    this.owningStatement = creatorStmt;
    this.exceptionInterceptor = this.connection.getExceptionInterceptor();
    JdbcPropertySet jdbcPropertySet = this.connection.getPropertySet();
    this.emulateLocators = jdbcPropertySet.getBooleanProperty(PropertyKey.emulateLocators);
    this.padCharsWithSpace = ((Boolean)jdbcPropertySet.getBooleanProperty(PropertyKey.padCharsWithSpace).getValue()).booleanValue();
    this.yearIsDateType = ((Boolean)jdbcPropertySet.getBooleanProperty(PropertyKey.yearIsDateType).getValue()).booleanValue();
    this.useUsageAdvisor = ((Boolean)jdbcPropertySet.getBooleanProperty(PropertyKey.useUsageAdvisor).getValue()).booleanValue();
    this.gatherPerfMetrics = ((Boolean)jdbcPropertySet.getBooleanProperty(PropertyKey.gatherPerfMetrics).getValue()).booleanValue();
    this.scrollTolerant = ((Boolean)jdbcPropertySet.getBooleanProperty(PropertyKey.scrollTolerantForwardOnly).getValue()).booleanValue();
    this.booleanValueFactory = (ValueFactory<Boolean>)new BooleanValueFactory((PropertySet)jdbcPropertySet);
    this.byteValueFactory = (ValueFactory<Byte>)new ByteValueFactory((PropertySet)jdbcPropertySet);
    this.shortValueFactory = (ValueFactory<Short>)new ShortValueFactory((PropertySet)jdbcPropertySet);
    this.integerValueFactory = (ValueFactory<Integer>)new IntegerValueFactory((PropertySet)jdbcPropertySet);
    this.longValueFactory = (ValueFactory<Long>)new LongValueFactory((PropertySet)jdbcPropertySet);
    this.floatValueFactory = (ValueFactory<Float>)new FloatValueFactory((PropertySet)jdbcPropertySet);
    this.doubleValueFactory = (ValueFactory<Double>)new DoubleValueFactory((PropertySet)jdbcPropertySet);
    this.bigDecimalValueFactory = (ValueFactory<BigDecimal>)new BigDecimalValueFactory((PropertySet)jdbcPropertySet);
    this.binaryStreamValueFactory = (ValueFactory<InputStream>)new BinaryStreamValueFactory((PropertySet)jdbcPropertySet);
    this.defaultTimeValueFactory = (ValueFactory<Time>)new SqlTimeValueFactory((PropertySet)jdbcPropertySet, null, this.session.getServerSession().getDefaultTimeZone(), this);
    this.defaultTimestampValueFactory = (ValueFactory<Timestamp>)new SqlTimestampValueFactory((PropertySet)jdbcPropertySet, null, this.session.getServerSession().getDefaultTimeZone(), this.session.getServerSession().getSessionTimeZone());
    this.defaultUtilCalendarValueFactory = (ValueFactory<Calendar>)new UtilCalendarValueFactory((PropertySet)jdbcPropertySet, this.session.getServerSession().getDefaultTimeZone(), this.session.getServerSession().getSessionTimeZone());
    this.defaultLocalDateValueFactory = (ValueFactory<LocalDate>)new LocalDateValueFactory((PropertySet)jdbcPropertySet, this);
    this.defaultLocalTimeValueFactory = (ValueFactory<LocalTime>)new LocalTimeValueFactory((PropertySet)jdbcPropertySet, this);
    this.defaultLocalDateTimeValueFactory = (ValueFactory<LocalDateTime>)new LocalDateTimeValueFactory((PropertySet)jdbcPropertySet);
    this.defaultOffsetTimeValueFactory = (ValueFactory<OffsetTime>)new OffsetTimeValueFactory((PropertySet)jdbcPropertySet, this.session.getProtocol().getServerSession().getDefaultTimeZone());
    this.defaultOffsetDateTimeValueFactory = (ValueFactory<OffsetDateTime>)new OffsetDateTimeValueFactory((PropertySet)jdbcPropertySet, this.session.getProtocol().getServerSession().getDefaultTimeZone(), this.session.getProtocol().getServerSession().getSessionTimeZone());
    this.defaultZonedDateTimeValueFactory = (ValueFactory<ZonedDateTime>)new ZonedDateTimeValueFactory((PropertySet)jdbcPropertySet, this.session.getProtocol().getServerSession().getDefaultTimeZone(), this.session.getProtocol().getServerSession().getSessionTimeZone());
    this.columnDefinition = tuples.getMetadata();
    this.rowData = tuples;
    this.updateCount = this.rowData.size();
    if (this.rowData.size() > 0) {
      if (this.updateCount == 1L && this.thisRow == null) {
        this.rowData.close();
        this.updateCount = -1L;
      } 
    } else {
      this.thisRow = null;
    } 
    this.rowData.setOwner(this);
    if (this.columnDefinition.getFields() != null)
      initializeWithMetadata(); 
    this.useColumnNamesInFindColumn = ((Boolean)jdbcPropertySet.getBooleanProperty(PropertyKey.useColumnNamesInFindColumn).getValue()).booleanValue();
    setRowPositionValidity();
  }
  
  private void setRowPositionValidity() throws SQLException {
    if (!this.rowData.isDynamic() && this.rowData.size() == 0) {
      this.invalidRowReason = Messages.getString("ResultSet.Illegal_operation_on_empty_result_set");
      this.onValidRow = false;
    } else if (this.rowData.isBeforeFirst()) {
      this.invalidRowReason = Messages.getString("ResultSet.Before_start_of_result_set_146");
      this.onValidRow = false;
    } else if (this.rowData.isAfterLast()) {
      this.invalidRowReason = Messages.getString("ResultSet.After_end_of_result_set_148");
      this.onValidRow = false;
    } else {
      this.onValidRow = true;
      this.invalidRowReason = null;
    } 
  }
  
  public void clearWarnings() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        this.warningChain = null;
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void close() throws SQLException {
    try {
      realClose(true);
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void populateCachedMetaData(CachedResultSetMetaData cachedMetaData) throws SQLException {
    try {
      this.columnDefinition.exportTo(cachedMetaData);
      cachedMetaData.setMetadata(getMetaData());
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void deleteRow() throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int findColumn(String columnName) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        int index = this.columnDefinition.findColumn(columnName, this.useColumnNamesInFindColumn, 1);
        if (index == -1)
          throw SQLError.createSQLException(
              Messages.getString("ResultSet.Column____112") + columnName + Messages.getString("ResultSet.___not_found._113"), "S0022", 
              getExceptionInterceptor()); 
        return index;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean first() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (!hasRows())
          throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", 
              getExceptionInterceptor()); 
        if (isStrictlyForwardOnly())
          throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly")); 
        boolean b = true;
        if (this.rowData.isEmpty()) {
          b = false;
        } else {
          this.rowData.beforeFirst();
          this.thisRow = (Row)this.rowData.next();
        } 
        setRowPositionValidity();
        return b;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Array getArray(int columnIndex) throws SQLException {
    try {
      checkRowPos();
      checkColumnBounds(columnIndex);
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Array getArray(String colName) throws SQLException {
    try {
      return getArray(findColumn(colName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public InputStream getAsciiStream(int columnIndex) throws SQLException {
    try {
      checkRowPos();
      checkColumnBounds(columnIndex);
      return getBinaryStream(columnIndex);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public InputStream getAsciiStream(String columnName) throws SQLException {
    try {
      return getAsciiStream(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
    try {
      checkRowPos();
      checkColumnBounds(columnIndex);
      return (BigDecimal)this.thisRow.getValue(columnIndex - 1, this.bigDecimalValueFactory);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  @Deprecated
  public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
    try {
      checkRowPos();
      checkColumnBounds(columnIndex);
      BigDecimalValueFactory bigDecimalValueFactory = new BigDecimalValueFactory(this.session.getPropertySet(), scale);
      bigDecimalValueFactory.setPropertySet((PropertySet)this.connection.getPropertySet());
      return (BigDecimal)this.thisRow.getValue(columnIndex - 1, (ValueFactory)bigDecimalValueFactory);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public BigDecimal getBigDecimal(String columnName) throws SQLException {
    try {
      return getBigDecimal(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  @Deprecated
  public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
    try {
      return getBigDecimal(findColumn(columnName), scale);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public InputStream getBinaryStream(int columnIndex) throws SQLException {
    try {
      checkRowPos();
      checkColumnBounds(columnIndex);
      return (InputStream)this.thisRow.getValue(columnIndex - 1, this.binaryStreamValueFactory);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public InputStream getBinaryStream(String columnName) throws SQLException {
    try {
      return getBinaryStream(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Blob getBlob(int columnIndex) throws SQLException {
    try {
      checkRowPos();
      checkColumnBounds(columnIndex);
      if (this.thisRow.getNull(columnIndex - 1))
        return null; 
      if (!((Boolean)this.emulateLocators.getValue()).booleanValue())
        return (Blob)new Blob(this.thisRow.getBytes(columnIndex - 1), getExceptionInterceptor()); 
      return (Blob)new BlobFromLocator(this, columnIndex, getExceptionInterceptor());
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Blob getBlob(String colName) throws SQLException {
    try {
      return getBlob(findColumn(colName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean getBoolean(int columnIndex) throws SQLException {
    try {
      Boolean res = getObject(columnIndex, (Class)boolean.class);
      return (res == null) ? false : res.booleanValue();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean getBoolean(String columnName) throws SQLException {
    try {
      return getBoolean(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public byte getByte(int columnIndex) throws SQLException {
    try {
      Byte res = getObject(columnIndex, (Class)byte.class);
      return (res == null) ? 0 : res.byteValue();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public byte getByte(String columnName) throws SQLException {
    try {
      return getByte(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public byte[] getBytes(int columnIndex) throws SQLException {
    try {
      checkRowPos();
      checkColumnBounds(columnIndex);
      return this.thisRow.getBytes(columnIndex - 1);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public byte[] getBytes(String columnName) throws SQLException {
    try {
      return getBytes(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Reader getCharacterStream(int columnIndex) throws SQLException {
    try {
      checkRowPos();
      checkColumnBounds(columnIndex);
      InputStream stream = getBinaryStream(columnIndex);
      if (stream == null)
        return null; 
      Field f = this.columnDefinition.getFields()[columnIndex - 1];
      try {
        return new InputStreamReader(stream, f.getEncoding());
      } catch (UnsupportedEncodingException e) {
        SQLException sqlEx = SQLError.createSQLException("Cannot read value with encoding: " + f.getEncoding(), this.exceptionInterceptor);
        sqlEx.initCause(e);
        throw sqlEx;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Reader getCharacterStream(String columnName) throws SQLException {
    try {
      return getCharacterStream(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Clob getClob(int columnIndex) throws SQLException {
    try {
      String asString = getStringForClob(columnIndex);
      if (asString == null)
        return null; 
      return (Clob)new Clob(asString, getExceptionInterceptor());
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Clob getClob(String colName) throws SQLException {
    try {
      return getClob(findColumn(colName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Date getDate(int columnIndex) throws SQLException {
    try {
      checkRowPos();
      checkColumnBounds(columnIndex);
      return (Date)this.thisRow.getValue(columnIndex - 1, (ValueFactory)new SqlDateValueFactory(this.session
            .getPropertySet(), null, this.session.getServerSession().getDefaultTimeZone(), this));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Date getDate(int columnIndex, Calendar cal) throws SQLException {
    try {
      checkRowPos();
      checkColumnBounds(columnIndex);
      return (Date)this.thisRow.getValue(columnIndex - 1, (ValueFactory)new SqlDateValueFactory(this.session.getPropertySet(), cal, (cal != null) ? cal
            .getTimeZone() : this.session.getServerSession().getDefaultTimeZone(), this));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Date getDate(String columnName) throws SQLException {
    try {
      return getDate(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Date getDate(String columnName, Calendar cal) throws SQLException {
    try {
      return getDate(findColumn(columnName), cal);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public double getDouble(int columnIndex) throws SQLException {
    try {
      Double res = getObject(columnIndex, (Class)double.class);
      return (res == null) ? 0.0D : res.doubleValue();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public double getDouble(String columnName) throws SQLException {
    try {
      return getDouble(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public float getFloat(int columnIndex) throws SQLException {
    try {
      Float res = getObject(columnIndex, (Class)float.class);
      return (res == null) ? 0.0F : res.floatValue();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public float getFloat(String columnName) throws SQLException {
    try {
      return getFloat(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getInt(int columnIndex) throws SQLException {
    try {
      Integer res = getObject(columnIndex, (Class)int.class);
      return (res == null) ? 0 : res.intValue();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public BigInteger getBigInteger(int columnIndex) throws SQLException {
    try {
      String stringVal = getString(columnIndex);
      if (stringVal == null)
        return null; 
      try {
        return new BigInteger(stringVal);
      } catch (NumberFormatException nfe) {
        throw SQLError.createSQLException(
            Messages.getString("ResultSet.Bad_format_for_BigInteger", new Object[] { Integer.valueOf(columnIndex), stringVal }), "S1009", 
            getExceptionInterceptor());
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getInt(String columnName) throws SQLException {
    try {
      return getInt(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public long getLong(int columnIndex) throws SQLException {
    try {
      Long res = getObject(columnIndex, (Class)long.class);
      return (res == null) ? 0L : res.longValue();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public long getLong(String columnName) throws SQLException {
    try {
      return getLong(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public short getShort(int columnIndex) throws SQLException {
    try {
      Short res = getObject(columnIndex, (Class)short.class);
      return (res == null) ? 0 : res.shortValue();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public short getShort(String columnName) throws SQLException {
    try {
      return getShort(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getString(int columnIndex) throws SQLException {
    try {
      checkRowPos();
      checkColumnBounds(columnIndex);
      Field f = this.columnDefinition.getFields()[columnIndex - 1];
      StringValueFactory stringValueFactory = new StringValueFactory(this.session.getPropertySet());
      String stringVal = (String)this.thisRow.getValue(columnIndex - 1, (ValueFactory)stringValueFactory);
      if (this.padCharsWithSpace && stringVal != null && f.getMysqlTypeId() == 254) {
        int maxBytesPerChar = this.session.getServerSession().getCharsetSettings().getMaxBytesPerChar(Integer.valueOf(f.getCollationIndex()), f.getEncoding());
        int fieldLength = (int)f.getLength() / maxBytesPerChar;
        return StringUtils.padString(stringVal, fieldLength);
      } 
      return stringVal;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getString(String columnName) throws SQLException {
    try {
      return getString(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private String getStringForClob(int columnIndex) throws SQLException {
    String asString = null;
    String forcedEncoding = this.connection.getPropertySet().getStringProperty(PropertyKey.clobCharacterEncoding).getStringValue();
    if (forcedEncoding == null) {
      asString = getString(columnIndex);
    } else {
      byte[] asBytes = getBytes(columnIndex);
      if (asBytes != null)
        asString = StringUtils.toString(asBytes, forcedEncoding); 
    } 
    return asString;
  }
  
  public Time getTime(int columnIndex) throws SQLException {
    try {
      checkRowPos();
      checkColumnBounds(columnIndex);
      return (Time)this.thisRow.getValue(columnIndex - 1, this.defaultTimeValueFactory);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Time getTime(int columnIndex, Calendar cal) throws SQLException {
    try {
      checkRowPos();
      checkColumnBounds(columnIndex);
      SqlTimeValueFactory sqlTimeValueFactory = new SqlTimeValueFactory(this.session.getPropertySet(), cal, (cal != null) ? cal.getTimeZone() : this.session.getServerSession().getDefaultTimeZone());
      return (Time)this.thisRow.getValue(columnIndex - 1, (ValueFactory)sqlTimeValueFactory);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Time getTime(String columnName) throws SQLException {
    try {
      return getTime(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Time getTime(String columnName, Calendar cal) throws SQLException {
    try {
      return getTime(findColumn(columnName), cal);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Timestamp getTimestamp(int columnIndex) throws SQLException {
    try {
      checkRowPos();
      checkColumnBounds(columnIndex);
      return (Timestamp)this.thisRow.getValue(columnIndex - 1, this.defaultTimestampValueFactory);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public LocalDate getLocalDate(int columnIndex) throws SQLException {
    checkRowPos();
    checkColumnBounds(columnIndex);
    return (LocalDate)this.thisRow.getValue(columnIndex - 1, this.defaultLocalDateValueFactory);
  }
  
  public LocalDateTime getLocalDateTime(int columnIndex) throws SQLException {
    checkRowPos();
    checkColumnBounds(columnIndex);
    return (LocalDateTime)this.thisRow.getValue(columnIndex - 1, this.defaultLocalDateTimeValueFactory);
  }
  
  public LocalTime getLocalTime(int columnIndex) throws SQLException {
    checkRowPos();
    checkColumnBounds(columnIndex);
    return (LocalTime)this.thisRow.getValue(columnIndex - 1, this.defaultLocalTimeValueFactory);
  }
  
  public Calendar getUtilCalendar(int columnIndex) throws SQLException {
    checkRowPos();
    checkColumnBounds(columnIndex);
    return (Calendar)this.thisRow.getValue(columnIndex - 1, this.defaultUtilCalendarValueFactory);
  }
  
  public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
    try {
      checkRowPos();
      checkColumnBounds(columnIndex);
      SqlTimestampValueFactory sqlTimestampValueFactory = new SqlTimestampValueFactory(this.session.getPropertySet(), cal, this.session.getServerSession().getDefaultTimeZone(), this.session.getServerSession().getSessionTimeZone());
      return (Timestamp)this.thisRow.getValue(columnIndex - 1, (ValueFactory)sqlTimestampValueFactory);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Timestamp getTimestamp(String columnName) throws SQLException {
    try {
      return getTimestamp(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
    try {
      return getTimestamp(findColumn(columnName), cal);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Reader getNCharacterStream(int columnIndex) throws SQLException {
    try {
      checkRowPos();
      checkColumnBounds(columnIndex);
      String fieldEncoding = this.columnDefinition.getFields()[columnIndex - 1].getEncoding();
      if (fieldEncoding == null || !fieldEncoding.equals("UTF-8"))
        throw new SQLException("Can not call getNCharacterStream() when field's charset isn't UTF-8"); 
      return getCharacterStream(columnIndex);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Reader getNCharacterStream(String columnName) throws SQLException {
    try {
      return getNCharacterStream(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public NClob getNClob(int columnIndex) throws SQLException {
    try {
      checkRowPos();
      checkColumnBounds(columnIndex);
      String fieldEncoding = this.columnDefinition.getFields()[columnIndex - 1].getEncoding();
      if (fieldEncoding == null || !fieldEncoding.equals("UTF-8"))
        throw new SQLException("Can not call getNClob() when field's charset isn't UTF-8"); 
      String asString = getStringForNClob(columnIndex);
      if (asString == null)
        return null; 
      return (NClob)new NClob(asString, getExceptionInterceptor());
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public NClob getNClob(String columnName) throws SQLException {
    try {
      return getNClob(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  private String getStringForNClob(int columnIndex) throws SQLException {
    String asString = null;
    String forcedEncoding = "UTF-8";
    try {
      byte[] asBytes = getBytes(columnIndex);
      if (asBytes != null)
        asString = new String(asBytes, forcedEncoding); 
    } catch (UnsupportedEncodingException uee) {
      throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", 
          getExceptionInterceptor());
    } 
    return asString;
  }
  
  public String getNString(int columnIndex) throws SQLException {
    try {
      checkRowPos();
      checkColumnBounds(columnIndex);
      String fieldEncoding = this.columnDefinition.getFields()[columnIndex - 1].getEncoding();
      if (fieldEncoding == null || !fieldEncoding.equals("UTF-8"))
        throw new SQLException("Can not call getNString() when field's charset isn't UTF-8"); 
      return getString(columnIndex);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getNString(String columnName) throws SQLException {
    try {
      return getNString(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getConcurrency() throws SQLException {
    try {
      return 1007;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public String getCursorName() throws SQLException {
    try {
      throw SQLError.createSQLException(Messages.getString("ResultSet.Positioned_Update_not_supported"), "S1C00", 
          getExceptionInterceptor());
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getFetchDirection() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        return this.fetchDirection;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getFetchSize() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        return this.fetchSize;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public char getFirstCharOfQuery() {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        return this.firstCharOfQuery;
      } 
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public ResultSetMetaData getMetaData() throws SQLException {
    try {
      checkClosed();
      return new ResultSetMetaData((Session)this.session, this.columnDefinition.getFields(), ((Boolean)this.session
          .getPropertySet().getBooleanProperty(PropertyKey.useOldAliasMetadataBehavior).getValue()).booleanValue(), this.yearIsDateType, 
          getExceptionInterceptor());
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Object getObject(int columnIndex) throws SQLException {
    try {
      String stringVal;
      checkRowPos();
      checkColumnBounds(columnIndex);
      int columnIndexMinusOne = columnIndex - 1;
      if (this.thisRow.getNull(columnIndexMinusOne))
        return null; 
      Field field = this.columnDefinition.getFields()[columnIndexMinusOne];
      switch (field.getMysqlType()) {
        case BIT:
          if (field.isBinary() || field.isBlob()) {
            byte[] data = getBytes(columnIndex);
            if (((Boolean)this.connection.getPropertySet().getBooleanProperty(PropertyKey.autoDeserialize).getValue()).booleanValue()) {
              Object obj = data;
              if (data != null && data.length >= 2)
                if (data[0] == -84 && data[1] == -19) {
                  try {
                    ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
                    ObjectInputStream objIn = new ObjectInputStream(bytesIn);
                    obj = objIn.readObject();
                    objIn.close();
                    bytesIn.close();
                  } catch (ClassNotFoundException cnfe) {
                    throw SQLError.createSQLException(Messages.getString("ResultSet.Class_not_found___91") + cnfe.toString() + 
                        Messages.getString("ResultSet._while_reading_serialized_object_92"), getExceptionInterceptor());
                  } catch (IOException ex) {
                    obj = data;
                  } 
                } else {
                  return getString(columnIndex);
                }  
              return obj;
            } 
            return data;
          } 
          return field.isSingleBit() ? Boolean.valueOf(getBoolean(columnIndex)) : getBytes(columnIndex);
        case BOOLEAN:
          return Boolean.valueOf(getBoolean(columnIndex));
        case TINYINT:
          return Integer.valueOf(getByte(columnIndex));
        case TINYINT_UNSIGNED:
        case SMALLINT:
        case SMALLINT_UNSIGNED:
        case MEDIUMINT:
        case MEDIUMINT_UNSIGNED:
        case INT:
          return Integer.valueOf(getInt(columnIndex));
        case INT_UNSIGNED:
        case BIGINT:
          return Long.valueOf(getLong(columnIndex));
        case BIGINT_UNSIGNED:
          return getBigInteger(columnIndex);
        case DECIMAL:
        case DECIMAL_UNSIGNED:
          stringVal = getString(columnIndex);
          if (stringVal != null) {
            if (stringVal.length() == 0)
              return new BigDecimal(0); 
            try {
              return new BigDecimal(stringVal);
            } catch (NumberFormatException ex) {
              throw SQLError.createSQLException(
                  Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", 
                  getExceptionInterceptor());
            } 
          } 
          return null;
        case FLOAT:
        case FLOAT_UNSIGNED:
          return new Float(getFloat(columnIndex));
        case DOUBLE:
        case DOUBLE_UNSIGNED:
          return new Double(getDouble(columnIndex));
        case CHAR:
        case ENUM:
        case SET:
        case VARCHAR:
        case TINYTEXT:
          return getString(columnIndex);
        case TEXT:
        case MEDIUMTEXT:
        case LONGTEXT:
        case JSON:
          return getStringForClob(columnIndex);
        case GEOMETRY:
          return getBytes(columnIndex);
        case BINARY:
        case VARBINARY:
        case TINYBLOB:
        case MEDIUMBLOB:
        case LONGBLOB:
        case BLOB:
          if (field.isBinary() || field.isBlob()) {
            byte[] data = getBytes(columnIndex);
            if (((Boolean)this.connection.getPropertySet().getBooleanProperty(PropertyKey.autoDeserialize).getValue()).booleanValue()) {
              Object obj = data;
              if (data != null && data.length >= 2)
                if (data[0] == -84 && data[1] == -19) {
                  try {
                    ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
                    ObjectInputStream objIn = new ObjectInputStream(bytesIn);
                    obj = objIn.readObject();
                    objIn.close();
                    bytesIn.close();
                  } catch (ClassNotFoundException cnfe) {
                    throw SQLError.createSQLException(Messages.getString("ResultSet.Class_not_found___91") + cnfe.toString() + 
                        Messages.getString("ResultSet._while_reading_serialized_object_92"), getExceptionInterceptor());
                  } catch (IOException ex) {
                    obj = data;
                  } 
                } else {
                  return getString(columnIndex);
                }  
              return obj;
            } 
            return data;
          } 
          return getBytes(columnIndex);
        case YEAR:
          return this.yearIsDateType ? getDate(columnIndex) : Short.valueOf(getShort(columnIndex));
        case DATE:
          return getDate(columnIndex);
        case TIME:
          return getTime(columnIndex);
        case TIMESTAMP:
          return getTimestamp(columnIndex);
        case DATETIME:
          return getLocalDateTime(columnIndex);
      } 
      return getString(columnIndex);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
    try {
      if (type == null)
        throw SQLError.createSQLException("Type parameter can not be null", "S1009", getExceptionInterceptor()); 
      synchronized (checkClosed().getConnectionMutex()) {
        if (type.equals(String.class))
          return (T)getString(columnIndex); 
        if (type.equals(BigDecimal.class))
          return (T)getBigDecimal(columnIndex); 
        if (type.equals(BigInteger.class))
          return (T)getBigInteger(columnIndex); 
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
          checkRowPos();
          checkColumnBounds(columnIndex);
          return (T)this.thisRow.getValue(columnIndex - 1, this.booleanValueFactory);
        } 
        if (type.equals(Byte.class) || type.equals(byte.class)) {
          checkRowPos();
          checkColumnBounds(columnIndex);
          return (T)this.thisRow.getValue(columnIndex - 1, this.byteValueFactory);
        } 
        if (type.equals(Short.class) || type.equals(short.class)) {
          checkRowPos();
          checkColumnBounds(columnIndex);
          return (T)this.thisRow.getValue(columnIndex - 1, this.shortValueFactory);
        } 
        if (type.equals(Integer.class) || type.equals(int.class)) {
          checkRowPos();
          checkColumnBounds(columnIndex);
          return (T)this.thisRow.getValue(columnIndex - 1, this.integerValueFactory);
        } 
        if (type.equals(Long.class) || type.equals(long.class)) {
          checkRowPos();
          checkColumnBounds(columnIndex);
          return (T)this.thisRow.getValue(columnIndex - 1, this.longValueFactory);
        } 
        if (type.equals(Float.class) || type.equals(float.class)) {
          checkRowPos();
          checkColumnBounds(columnIndex);
          return (T)this.thisRow.getValue(columnIndex - 1, this.floatValueFactory);
        } 
        if (type.equals(Double.class) || type.equals(double.class)) {
          checkRowPos();
          checkColumnBounds(columnIndex);
          return (T)this.thisRow.getValue(columnIndex - 1, this.doubleValueFactory);
        } 
        if (type.equals(byte[].class))
          return (T)getBytes(columnIndex); 
        if (type.equals(Date.class))
          return (T)getDate(columnIndex); 
        if (type.equals(Time.class))
          return (T)getTime(columnIndex); 
        if (type.equals(Timestamp.class))
          return (T)getTimestamp(columnIndex); 
        if (type.equals(Date.class)) {
          Timestamp ts = getTimestamp(columnIndex);
          return (ts == null) ? null : (T)Date.from(ts.toInstant());
        } 
        if (type.equals(Calendar.class))
          return (T)getUtilCalendar(columnIndex); 
        if (type.equals(Clob.class))
          return (T)getClob(columnIndex); 
        if (type.equals(Blob.class))
          return (T)getBlob(columnIndex); 
        if (type.equals(Array.class))
          return (T)getArray(columnIndex); 
        if (type.equals(Ref.class))
          return (T)getRef(columnIndex); 
        if (type.equals(URL.class))
          return (T)getURL(columnIndex); 
        if (type.equals(Struct.class))
          throw new SQLFeatureNotSupportedException(); 
        if (type.equals(RowId.class))
          return (T)getRowId(columnIndex); 
        if (type.equals(NClob.class))
          return (T)getNClob(columnIndex); 
        if (type.equals(SQLXML.class))
          return (T)getSQLXML(columnIndex); 
        if (type.equals(LocalDate.class))
          return (T)getLocalDate(columnIndex); 
        if (type.equals(LocalDateTime.class))
          return (T)getLocalDateTime(columnIndex); 
        if (type.equals(LocalTime.class))
          return (T)getLocalTime(columnIndex); 
        if (type.equals(OffsetDateTime.class)) {
          checkRowPos();
          checkColumnBounds(columnIndex);
          return (T)this.thisRow.getValue(columnIndex - 1, this.defaultOffsetDateTimeValueFactory);
        } 
        if (type.equals(OffsetTime.class)) {
          checkRowPos();
          checkColumnBounds(columnIndex);
          return (T)this.thisRow.getValue(columnIndex - 1, this.defaultOffsetTimeValueFactory);
        } 
        if (type.equals(ZonedDateTime.class)) {
          checkRowPos();
          checkColumnBounds(columnIndex);
          return (T)this.thisRow.getValue(columnIndex - 1, this.defaultZonedDateTimeValueFactory);
        } 
        if (type.equals(Duration.class)) {
          checkRowPos();
          checkColumnBounds(columnIndex);
          return (T)this.thisRow.getValue(columnIndex - 1, (ValueFactory)new DurationValueFactory(this.session.getPropertySet()));
        } 
        if (((Boolean)this.connection.getPropertySet().getBooleanProperty(PropertyKey.autoDeserialize).getValue()).booleanValue())
          try {
            return (T)getObject(columnIndex);
          } catch (ClassCastException cce) {
            SQLException sqlEx = SQLError.createSQLException("Conversion not supported for type " + type.getName(), "S1009", 
                getExceptionInterceptor());
            sqlEx.initCause(cce);
            throw sqlEx;
          }  
        throw SQLError.createSQLException("Conversion not supported for type " + type.getName(), "S1009", 
            getExceptionInterceptor());
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
    try {
      return getObject(findColumn(columnLabel), type);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
    try {
      return getObject(i);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Object getObject(String columnName) throws SQLException {
    try {
      return getObject(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Object getObject(String colName, Map<String, Class<?>> map) throws SQLException {
    try {
      return getObject(findColumn(colName), map);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Object getObjectStoredProc(int columnIndex, int desiredSqlType) throws SQLException {
    try {
      String stringVal;
      checkRowPos();
      checkColumnBounds(columnIndex);
      Object value = this.thisRow.getBytes(columnIndex - 1);
      if (value == null)
        return null; 
      Field field = this.columnDefinition.getFields()[columnIndex - 1];
      MysqlType desiredMysqlType = MysqlType.getByJdbcType(desiredSqlType);
      switch (desiredMysqlType) {
        case BIT:
        case BOOLEAN:
          return Boolean.valueOf(getBoolean(columnIndex));
        case TINYINT:
        case TINYINT_UNSIGNED:
          return Integer.valueOf(getInt(columnIndex));
        case SMALLINT:
        case SMALLINT_UNSIGNED:
          return Integer.valueOf(getInt(columnIndex));
        case MEDIUMINT:
        case MEDIUMINT_UNSIGNED:
        case INT:
        case INT_UNSIGNED:
          if (!field.isUnsigned() || field.getMysqlTypeId() == 9)
            return Integer.valueOf(getInt(columnIndex)); 
          return Long.valueOf(getLong(columnIndex));
        case BIGINT:
          return Long.valueOf(getLong(columnIndex));
        case BIGINT_UNSIGNED:
          return getBigInteger(columnIndex);
        case DECIMAL:
        case DECIMAL_UNSIGNED:
          stringVal = getString(columnIndex);
          if (stringVal != null) {
            BigDecimal val;
            if (stringVal.length() == 0) {
              val = new BigDecimal(0);
              return val;
            } 
            try {
              val = new BigDecimal(stringVal);
            } catch (NumberFormatException ex) {
              throw SQLError.createSQLException(
                  Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", 
                  getExceptionInterceptor());
            } 
            return val;
          } 
          return null;
        case FLOAT:
        case FLOAT_UNSIGNED:
          return new Float(getFloat(columnIndex));
        case DOUBLE:
        case DOUBLE_UNSIGNED:
          return new Double(getDouble(columnIndex));
        case CHAR:
        case ENUM:
        case SET:
        case VARCHAR:
        case TINYTEXT:
          return getString(columnIndex);
        case TEXT:
        case MEDIUMTEXT:
        case LONGTEXT:
        case JSON:
          return getStringForClob(columnIndex);
        case GEOMETRY:
        case BINARY:
        case VARBINARY:
        case TINYBLOB:
        case MEDIUMBLOB:
        case LONGBLOB:
        case BLOB:
          return getBytes(columnIndex);
        case YEAR:
        case DATE:
          if (field.getMysqlType() == MysqlType.YEAR && !this.yearIsDateType)
            return Short.valueOf(getShort(columnIndex)); 
          return getDate(columnIndex);
        case TIME:
          return getTime(columnIndex);
        case TIMESTAMP:
          return getTimestamp(columnIndex);
      } 
      return getString(columnIndex);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Object getObjectStoredProc(int i, Map<Object, Object> map, int desiredSqlType) throws SQLException {
    try {
      return getObjectStoredProc(i, desiredSqlType);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Object getObjectStoredProc(String columnName, int desiredSqlType) throws SQLException {
    try {
      return getObjectStoredProc(findColumn(columnName), desiredSqlType);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Object getObjectStoredProc(String colName, Map<Object, Object> map, int desiredSqlType) throws SQLException {
    try {
      return getObjectStoredProc(findColumn(colName), map, desiredSqlType);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Ref getRef(int i) throws SQLException {
    try {
      checkColumnBounds(i);
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Ref getRef(String colName) throws SQLException {
    try {
      return getRef(findColumn(colName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getRow() throws SQLException {
    try {
      checkClosed();
      if (!hasRows())
        throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", 
            getExceptionInterceptor()); 
      int currentRowNumber = this.rowData.getPosition();
      int row = 0;
      if (!this.rowData.isDynamic()) {
        if (currentRowNumber < 0 || this.rowData.isAfterLast() || this.rowData.isEmpty()) {
          row = 0;
        } else {
          row = currentRowNumber + 1;
        } 
      } else {
        row = currentRowNumber + 1;
      } 
      return row;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public Statement getStatement() throws SQLException {
    try {
      try {
        synchronized (checkClosed().getConnectionMutex()) {
          if (this.wrapperStatement != null)
            return this.wrapperStatement; 
          return (Statement)this.owningStatement;
        } 
      } catch (SQLException sqlEx) {
        throw SQLError.createSQLException("Operation not allowed on closed ResultSet.", "S1000", 
            getExceptionInterceptor());
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public int getType() throws SQLException {
    try {
      return this.resultSetType;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  @Deprecated
  public InputStream getUnicodeStream(int columnIndex) throws SQLException {
    try {
      checkRowPos();
      return getBinaryStream(columnIndex);
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  @Deprecated
  public InputStream getUnicodeStream(String columnName) throws SQLException {
    try {
      return getUnicodeStream(findColumn(columnName));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public URL getURL(int colIndex) throws SQLException {
    try {
      String val = getString(colIndex);
      if (val == null)
        return null; 
      try {
        return new URL(val);
      } catch (MalformedURLException mfe) {
        throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____104") + val + "'", "S1009", 
            getExceptionInterceptor());
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public URL getURL(String colName) throws SQLException {
    try {
      String val = getString(colName);
      if (val == null)
        return null; 
      try {
        return new URL(val);
      } catch (MalformedURLException mfe) {
        throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____107") + val + "'", "S1009", 
            getExceptionInterceptor());
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public SQLWarning getWarnings() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        return this.warningChain;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void insertRow() throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isAfterLast() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (!hasRows())
          throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", 
              getExceptionInterceptor()); 
        return this.rowData.isAfterLast();
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isBeforeFirst() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (!hasRows())
          throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", 
              getExceptionInterceptor()); 
        return this.rowData.isBeforeFirst();
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isFirst() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (!hasRows())
          throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", 
              getExceptionInterceptor()); 
        return this.rowData.isFirst();
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isLast() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (!hasRows())
          throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", 
              getExceptionInterceptor()); 
        return this.rowData.isLast();
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected boolean isStrictlyForwardOnly() {
    return (this.resultSetType == 1003 && !this.scrollTolerant);
  }
  
  public boolean last() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (!hasRows())
          throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", 
              getExceptionInterceptor()); 
        if (isStrictlyForwardOnly())
          throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly")); 
        boolean b = true;
        if (this.rowData.size() == 0) {
          b = false;
        } else {
          this.rowData.beforeLast();
          this.thisRow = (Row)this.rowData.next();
        } 
        setRowPositionValidity();
        return b;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void moveToCurrentRow() throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void moveToInsertRow() throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean next() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        boolean b;
        if (!hasRows())
          throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", 
              getExceptionInterceptor()); 
        if (this.rowData.size() == 0) {
          b = false;
        } else {
          this.thisRow = (Row)this.rowData.next();
          if (this.thisRow == null) {
            b = false;
          } else {
            clearWarnings();
            b = true;
          } 
        } 
        setRowPositionValidity();
        return b;
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean prev() throws SQLException {
    synchronized (checkClosed().getConnectionMutex()) {
      int rowIndex = this.rowData.getPosition();
      boolean b = true;
      if (rowIndex - 1 >= 0) {
        rowIndex--;
        this.rowData.setCurrentRow(rowIndex);
        this.thisRow = this.rowData.get(rowIndex);
        b = true;
      } else if (rowIndex - 1 == -1) {
        rowIndex--;
        this.rowData.setCurrentRow(rowIndex);
        this.thisRow = null;
        b = false;
      } else {
        b = false;
      } 
      setRowPositionValidity();
      return b;
    } 
  }
  
  public boolean previous() throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (!hasRows())
          throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", 
              getExceptionInterceptor()); 
        if (isStrictlyForwardOnly())
          throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly")); 
        return prev();
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void realClose(boolean calledExplicitly) throws SQLException {
    try {
      JdbcConnection locallyScopedConn = this.connection;
      if (locallyScopedConn == null)
        return; 
      synchronized (locallyScopedConn.getConnectionMutex()) {
        if (this.isClosed)
          return; 
        try {
          if (this.useUsageAdvisor) {
            if (!calledExplicitly)
              this.eventSink.processEvent((byte)0, (Session)this.session, (Query)this.owningStatement, this, 0L, new Throwable(), 
                  Messages.getString("ResultSet.ResultSet_implicitly_closed_by_driver")); 
            int resultSetSizeThreshold = ((Integer)locallyScopedConn.getPropertySet().getIntegerProperty(PropertyKey.resultSetSizeThreshold).getValue()).intValue();
            if (this.rowData.size() > resultSetSizeThreshold)
              this.eventSink.processEvent((byte)0, (Session)this.session, (Query)this.owningStatement, this, 0L, new Throwable(), 
                  Messages.getString("ResultSet.Too_Large_Result_Set", new Object[] { Integer.valueOf(this.rowData.size()), Integer.valueOf(resultSetSizeThreshold) })); 
            if (!isLast() && !isAfterLast() && this.rowData.size() != 0)
              this.eventSink.processEvent((byte)0, (Session)this.session, (Query)this.owningStatement, this, 0L, new Throwable(), 
                  Messages.getString("ResultSet.Possible_incomplete_traversal_of_result_set", new Object[] { Integer.valueOf(getRow()), Integer.valueOf(this.rowData.size()) })); 
            if (this.columnUsed.length > 0 && !this.rowData.wasEmpty()) {
              StringBuilder buf = new StringBuilder();
              for (int i = 0; i < this.columnUsed.length; i++) {
                if (!this.columnUsed[i]) {
                  if (buf.length() > 0)
                    buf.append(", "); 
                  buf.append(this.columnDefinition.getFields()[i].getFullName());
                } 
              } 
              if (buf.length() > 0)
                this.eventSink.processEvent((byte)0, (Session)this.session, (Query)this.owningStatement, this, 0L, new Throwable(), 
                    Messages.getString("ResultSet.The_following_columns_were_never_referenced", (Object[])new String[] { buf.toString() })); 
            } 
          } 
        } finally {
          if (this.owningStatement != null && calledExplicitly)
            this.owningStatement.removeOpenResultSet(this); 
          SQLException exceptionDuringClose = null;
          if (this.rowData != null)
            try {
              this.rowData.close();
            } catch (CJException sqlEx) {
              exceptionDuringClose = SQLExceptionsMapping.translateException((Throwable)sqlEx);
            }  
          if (this.statementUsedForFetchingRows != null)
            try {
              this.statementUsedForFetchingRows.realClose(true, false);
            } catch (SQLException sqlEx) {
              if (exceptionDuringClose != null) {
                exceptionDuringClose.setNextException(sqlEx);
              } else {
                exceptionDuringClose = sqlEx;
              } 
            }  
          this.rowData = null;
          this.columnDefinition = null;
          this.eventSink = null;
          this.warningChain = null;
          this.owningStatement = null;
          this.db = null;
          this.serverInfo = null;
          this.thisRow = null;
          this.fastDefaultCal = null;
          this.fastClientCal = null;
          this.connection = null;
          this.session = null;
          this.isClosed = true;
          if (exceptionDuringClose != null)
            throw exceptionDuringClose; 
        } 
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean isClosed() throws SQLException {
    try {
      return this.isClosed;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void refreshRow() throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean relative(int rows) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (!hasRows())
          throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", 
              getExceptionInterceptor()); 
        if (isStrictlyForwardOnly())
          throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly")); 
        if (this.rowData.size() == 0) {
          setRowPositionValidity();
          return false;
        } 
        this.rowData.moveRowRelative(rows);
        this.thisRow = this.rowData.get(this.rowData.getPosition());
        setRowPositionValidity();
        return (!this.rowData.isAfterLast() && !this.rowData.isBeforeFirst());
      } 
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean rowDeleted() throws SQLException {
    try {
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean rowInserted() throws SQLException {
    try {
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean rowUpdated() throws SQLException {
    try {
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setFetchDirection(int direction) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (direction != 1000 && direction != 1001 && direction != 1002)
          throw SQLError.createSQLException(Messages.getString("ResultSet.Illegal_value_for_fetch_direction_64"), "S1009", 
              getExceptionInterceptor()); 
        if (isStrictlyForwardOnly() && direction != 1000) {
          String constName = (direction == 1001) ? "ResultSet.FETCH_REVERSE" : "ResultSet.FETCH_UNKNOWN";
          throw ExceptionFactory.createException(Messages.getString("ResultSet.Unacceptable_value_for_fetch_direction", new Object[] { constName }));
        } 
        this.fetchDirection = direction;
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setFetchSize(int rows) throws SQLException {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        if (rows < 0)
          throw SQLError.createSQLException(Messages.getString("ResultSet.Value_must_be_between_0_and_getMaxRows()_66"), "S1009", 
              getExceptionInterceptor()); 
        this.fetchSize = rows;
      } 
      return;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void setFirstCharOfQuery(char c) {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        this.firstCharOfQuery = c;
      } 
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public void setOwningStatement(JdbcStatement owningStatement) {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        this.owningStatement = (StatementImpl)owningStatement;
      } 
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public synchronized void setResultSetConcurrency(int concurrencyFlag) {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        this.resultSetConcurrency = concurrencyFlag;
      } 
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public synchronized void setResultSetType(int typeFlag) {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        this.resultSetType = typeFlag;
      } 
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public void setServerInfo(String info) {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        this.serverInfo = info;
      } 
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public synchronized void setStatementUsedForFetchingRows(JdbcPreparedStatement stmt) {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        this.statementUsedForFetchingRows = stmt;
      } 
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public synchronized void setWrapperStatement(Statement wrapperStatement) {
    try {
      synchronized (checkClosed().getConnectionMutex()) {
        this.wrapperStatement = wrapperStatement;
      } 
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public String toString() {
    return hasRows() ? super.toString() : ("Result set representing update count of " + this.updateCount);
  }
  
  public void updateArray(int columnIndex, Array arg1) throws SQLException {
    try {
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateArray(String columnLabel, Array arg1) throws SQLException {
    try {
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateBlob(int columnIndex, Blob arg1) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateBlob(String columnLabel, Blob arg1) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateBoolean(int columnIndex, boolean x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateBoolean(String columnName, boolean x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateByte(int columnIndex, byte x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateByte(String columnName, byte x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateBytes(int columnIndex, byte[] x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateBytes(String columnName, byte[] x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateClob(int columnIndex, Clob arg1) throws SQLException {
    try {
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateClob(String columnName, Clob clob) throws SQLException {
    try {
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateClob(int columnIndex, Reader reader) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateClob(String columnLabel, Reader reader) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateDate(int columnIndex, Date x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateDate(String columnName, Date x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateDouble(int columnIndex, double x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateDouble(String columnName, double x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateFloat(int columnIndex, float x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateFloat(String columnName, float x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateInt(int columnIndex, int x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateInt(String columnName, int x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateLong(int columnIndex, long x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateLong(String columnName, long x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateNClob(String columnName, NClob nClob) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateNClob(int columnIndex, Reader reader) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateNClob(String columnLabel, Reader reader) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateNull(int columnIndex) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateNull(String columnName) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateNString(int columnIndex, String nString) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateNString(String columnLabel, String nString) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateObject(int columnIndex, Object x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateObject(String columnName, Object x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateObject(String columnName, Object x, int scale) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateRef(int columnIndex, Ref arg1) throws SQLException {
    try {
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateRef(String columnLabel, Ref arg1) throws SQLException {
    try {
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateRow() throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateRowId(int columnIndex, RowId x) throws SQLException {
    try {
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateRowId(String columnName, RowId x) throws SQLException {
    try {
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateShort(int columnIndex, short x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateShort(String columnName, short x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateString(int columnIndex, String x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateString(String columnName, String x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateTime(int columnIndex, Time x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateTime(String columnName, Time x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
    try {
      throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public boolean wasNull() throws SQLException {
    try {
      return this.thisRow.wasNull();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  protected ExceptionInterceptor getExceptionInterceptor() {
    return this.exceptionInterceptor;
  }
  
  public int getHoldability() throws SQLException {
    try {
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public RowId getRowId(int columnIndex) throws SQLException {
    try {
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public RowId getRowId(String columnLabel) throws SQLException {
    try {
      throw SQLError.createSQLFeatureNotSupportedException();
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public SQLXML getSQLXML(int columnIndex) throws SQLException {
    try {
      checkColumnBounds(columnIndex);
      return (SQLXML)new MysqlSQLXML(this, columnIndex, getExceptionInterceptor());
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
    } 
  }
  
  public SQLXML getSQLXML(String columnLabel) throws SQLException {
    try {
      return getSQLXML(findColumn(columnLabel));
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
  
  public synchronized void warningEncountered(String warning) {
    SQLWarning w = new SQLWarning(warning);
    if (this.warningChain == null) {
      this.warningChain = w;
    } else {
      this.warningChain.setNextWarning(w);
    } 
  }
  
  public ColumnDefinition getMetadata() {
    return this.columnDefinition;
  }
  
  public StatementImpl getOwningStatement() {
    return this.owningStatement;
  }
  
  public void closeOwner(boolean calledExplicitly) {
    try {
      realClose(calledExplicitly);
    } catch (SQLException e) {
      throw ExceptionFactory.createException(e.getMessage(), e);
    } 
  }
  
  public JdbcConnection getConnection() {
    return this.connection;
  }
  
  public Session getSession() {
    return (this.connection != null) ? this.connection.getSession() : null;
  }
  
  public String getPointOfOrigin() {
    return this.pointOfOrigin;
  }
  
  public int getOwnerFetchSize() {
    try {
      return getFetchSize();
    } catch (SQLException e) {
      throw ExceptionFactory.createException(e.getMessage(), e);
    } 
  }
  
  public Query getOwningQuery() {
    return (Query)this.owningStatement;
  }
  
  public int getOwningStatementMaxRows() {
    return (this.owningStatement == null) ? -1 : this.owningStatement.maxRows;
  }
  
  public int getOwningStatementFetchSize() {
    try {
      return (this.owningStatement == null) ? 0 : this.owningStatement.getFetchSize();
    } catch (SQLException e) {
      throw ExceptionFactory.createException(e.getMessage(), e);
    } 
  }
  
  public long getOwningStatementServerId() {
    return (this.owningStatement == null) ? 0L : this.owningStatement.getServerStatementId();
  }
  
  public Object getSyncMutex() {
    return (this.connection != null) ? this.connection.getConnectionMutex() : null;
  }
}
