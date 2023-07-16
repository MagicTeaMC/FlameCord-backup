package com.mysql.cj;

import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.result.Field;
import com.mysql.cj.util.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class NativeQueryBindings implements QueryBindings {
  private Session session;
  
  private BindValue[] bindValues;
  
  private int numberOfExecutions = 0;
  
  private boolean isLoadDataQuery = false;
  
  private ColumnDefinition columnDefinition;
  
  private AtomicBoolean sendTypesToServer = new AtomicBoolean(false);
  
  private Function<Session, BindValue> bindValueConstructor;
  
  private boolean longParameterSwitchDetected = false;
  
  public NativeQueryBindings(int parameterCount, Session sess, Function<Session, BindValue> bindValueConstructor) {
    this.session = sess;
    this.bindValueConstructor = bindValueConstructor;
    this.bindValues = new BindValue[parameterCount];
    for (int i = 0; i < parameterCount; i++)
      this.bindValues[i] = bindValueConstructor.apply(this.session); 
  }
  
  public QueryBindings clone() {
    NativeQueryBindings newBindings = new NativeQueryBindings(this.bindValues.length, this.session, this.bindValueConstructor);
    BindValue[] bvs = new BindValue[this.bindValues.length];
    for (int i = 0; i < this.bindValues.length; i++)
      bvs[i] = this.bindValues[i].clone(); 
    newBindings.setBindValues(bvs);
    newBindings.isLoadDataQuery = this.isLoadDataQuery;
    newBindings.sendTypesToServer.set(this.sendTypesToServer.get());
    newBindings.setLongParameterSwitchDetected(isLongParameterSwitchDetected());
    return newBindings;
  }
  
  public void setColumnDefinition(ColumnDefinition colDef) {
    this.columnDefinition = colDef;
  }
  
  public BindValue[] getBindValues() {
    return this.bindValues;
  }
  
  public void setBindValues(BindValue[] bindValues) {
    this.bindValues = bindValues;
  }
  
  public boolean clearBindValues() {
    boolean hadLongData = false;
    if (this.bindValues != null)
      for (int i = 0; i < this.bindValues.length; i++) {
        if (this.bindValues[i] != null && this.bindValues[i].isStream())
          hadLongData = true; 
        this.bindValues[i].reset();
      }  
    return hadLongData;
  }
  
  public void checkParameterSet(int columnIndex) {
    if (!this.bindValues[columnIndex].isSet())
      throw ExceptionFactory.createException(Messages.getString("PreparedStatement.40") + (columnIndex + 1), "07001", 0, true, null, this.session
          .getExceptionInterceptor()); 
  }
  
  public void checkAllParametersSet() {
    for (int i = 0; i < this.bindValues.length; i++)
      checkParameterSet(i); 
  }
  
  public int getNumberOfExecutions() {
    return this.numberOfExecutions;
  }
  
  public void setNumberOfExecutions(int numberOfExecutions) {
    this.numberOfExecutions = numberOfExecutions;
  }
  
  public boolean isLongParameterSwitchDetected() {
    return this.longParameterSwitchDetected;
  }
  
  public void setLongParameterSwitchDetected(boolean longParameterSwitchDetected) {
    this.longParameterSwitchDetected = longParameterSwitchDetected;
  }
  
  public AtomicBoolean getSendTypesToServer() {
    return this.sendTypesToServer;
  }
  
  public BindValue getBinding(int parameterIndex, boolean forLongData) {
    if (this.bindValues[parameterIndex] != null && this.bindValues[parameterIndex].isStream() && !forLongData)
      this.longParameterSwitchDetected = true; 
    return this.bindValues[parameterIndex];
  }
  
  public void setFromBindValue(int parameterIndex, BindValue bv) {
    BindValue binding = getBinding(parameterIndex, false);
    binding.setBinding(bv.getValue(), bv.getMysqlType(), this.numberOfExecutions, this.sendTypesToServer);
    binding.setKeepOrigNanos(bv.keepOrigNanos());
    binding.setCalendar(bv.getCalendar());
    binding.setEscapeBytesIfNeeded(bv.escapeBytesIfNeeded());
    binding.setIsNational(bv.isNational());
    binding.setField(bv.getField());
    binding.setScaleOrLength(bv.getScaleOrLength());
  }
  
  static Map<Class<?>, MysqlType> DEFAULT_MYSQL_TYPES = new HashMap<>();
  
  static {
    DEFAULT_MYSQL_TYPES.put(BigDecimal.class, MysqlType.DECIMAL);
    DEFAULT_MYSQL_TYPES.put(BigInteger.class, MysqlType.BIGINT);
    DEFAULT_MYSQL_TYPES.put(Blob.class, MysqlType.BLOB);
    DEFAULT_MYSQL_TYPES.put(Boolean.class, MysqlType.BOOLEAN);
    DEFAULT_MYSQL_TYPES.put(Byte.class, MysqlType.TINYINT);
    DEFAULT_MYSQL_TYPES.put(byte[].class, MysqlType.BINARY);
    DEFAULT_MYSQL_TYPES.put(Calendar.class, MysqlType.TIMESTAMP);
    DEFAULT_MYSQL_TYPES.put(Clob.class, MysqlType.TEXT);
    DEFAULT_MYSQL_TYPES.put(Date.class, MysqlType.DATE);
    DEFAULT_MYSQL_TYPES.put(Date.class, MysqlType.TIMESTAMP);
    DEFAULT_MYSQL_TYPES.put(Double.class, MysqlType.DOUBLE);
    DEFAULT_MYSQL_TYPES.put(Duration.class, MysqlType.TIME);
    DEFAULT_MYSQL_TYPES.put(Float.class, MysqlType.FLOAT);
    DEFAULT_MYSQL_TYPES.put(InputStream.class, MysqlType.BLOB);
    DEFAULT_MYSQL_TYPES.put(Instant.class, MysqlType.TIMESTAMP);
    DEFAULT_MYSQL_TYPES.put(Integer.class, MysqlType.INT);
    DEFAULT_MYSQL_TYPES.put(LocalDate.class, MysqlType.DATE);
    DEFAULT_MYSQL_TYPES.put(LocalDateTime.class, MysqlType.DATETIME);
    DEFAULT_MYSQL_TYPES.put(LocalTime.class, MysqlType.TIME);
    DEFAULT_MYSQL_TYPES.put(Long.class, MysqlType.BIGINT);
    DEFAULT_MYSQL_TYPES.put(OffsetDateTime.class, MysqlType.TIMESTAMP);
    DEFAULT_MYSQL_TYPES.put(OffsetTime.class, MysqlType.TIME);
    DEFAULT_MYSQL_TYPES.put(Reader.class, MysqlType.TEXT);
    DEFAULT_MYSQL_TYPES.put(Short.class, MysqlType.SMALLINT);
    DEFAULT_MYSQL_TYPES.put(String.class, MysqlType.VARCHAR);
    DEFAULT_MYSQL_TYPES.put(Time.class, MysqlType.TIME);
    DEFAULT_MYSQL_TYPES.put(Timestamp.class, MysqlType.TIMESTAMP);
    DEFAULT_MYSQL_TYPES.put(ZonedDateTime.class, MysqlType.TIMESTAMP);
  }
  
  public void setAsciiStream(int parameterIndex, InputStream x, int length) {
    if (x == null) {
      setNull(parameterIndex);
      return;
    } 
    BindValue binding = getBinding(parameterIndex, true);
    binding.setBinding(x, MysqlType.TEXT, this.numberOfExecutions, this.sendTypesToServer);
    binding.setScaleOrLength(length);
  }
  
  public void setBigDecimal(int parameterIndex, BigDecimal x) {
    if (x == null) {
      setNull(parameterIndex);
      return;
    } 
    getBinding(parameterIndex, false).setBinding(x, MysqlType.DECIMAL, this.numberOfExecutions, this.sendTypesToServer);
  }
  
  public void setBigInteger(int parameterIndex, BigInteger x) {
    if (x == null) {
      setNull(parameterIndex);
      return;
    } 
    getBinding(parameterIndex, false).setBinding(x, MysqlType.BIGINT_UNSIGNED, this.numberOfExecutions, this.sendTypesToServer);
  }
  
  public void setBinaryStream(int parameterIndex, InputStream x, int length) {
    if (x == null) {
      setNull(parameterIndex);
      return;
    } 
    BindValue binding = getBinding(parameterIndex, true);
    binding.setBinding(x, MysqlType.BLOB, this.numberOfExecutions, this.sendTypesToServer);
    binding.setScaleOrLength(length);
  }
  
  public void setBlob(int parameterIndex, Blob x) {
    if (x == null) {
      setNull(parameterIndex);
      return;
    } 
    BindValue binding = getBinding(parameterIndex, false);
    binding.setBinding(x, MysqlType.BLOB, this.numberOfExecutions, this.sendTypesToServer);
    binding.setScaleOrLength(-1L);
  }
  
  public void setBoolean(int parameterIndex, boolean x) {
    getBinding(parameterIndex, false).setBinding(Boolean.valueOf(x), MysqlType.BOOLEAN, this.numberOfExecutions, this.sendTypesToServer);
  }
  
  public void setByte(int parameterIndex, byte x) {
    getBinding(parameterIndex, false).setBinding(Byte.valueOf(x), MysqlType.TINYINT, this.numberOfExecutions, this.sendTypesToServer);
  }
  
  public void setBytes(int parameterIndex, byte[] x, boolean escapeIfNeeded) {
    if (x == null) {
      setNull(parameterIndex);
      return;
    } 
    BindValue binding = getBinding(parameterIndex, false);
    binding.setBinding(x, MysqlType.BINARY, this.numberOfExecutions, this.sendTypesToServer);
    binding.setEscapeBytesIfNeeded(escapeIfNeeded);
  }
  
  public void setCharacterStream(int parameterIndex, Reader reader, int length) {
    if (reader == null) {
      setNull(parameterIndex);
      return;
    } 
    BindValue binding = getBinding(parameterIndex, true);
    binding.setBinding(reader, MysqlType.TEXT, this.numberOfExecutions, this.sendTypesToServer);
    binding.setScaleOrLength(length);
  }
  
  public void setClob(int parameterIndex, Clob x) {
    if (x == null) {
      setNull(parameterIndex);
      return;
    } 
    BindValue binding = getBinding(parameterIndex, false);
    binding.setBinding(x, MysqlType.TEXT, this.numberOfExecutions, this.sendTypesToServer);
    binding.setScaleOrLength(-1L);
  }
  
  public void setDate(int parameterIndex, Date x, Calendar cal) {
    if (x == null) {
      setNull(parameterIndex);
      return;
    } 
    BindValue binding = getBinding(parameterIndex, false);
    binding.setBinding(x, MysqlType.DATE, this.numberOfExecutions, this.sendTypesToServer);
    binding.setCalendar((cal == null) ? null : (Calendar)cal.clone());
  }
  
  public void setDouble(int parameterIndex, double x) {
    if (!((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.allowNanAndInf).getValue()).booleanValue() && (x == Double.POSITIVE_INFINITY || x == Double.NEGATIVE_INFINITY || 
      Double.isNaN(x)))
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("PreparedStatement.64", new Object[] { Double.valueOf(x) }), this.session
          .getExceptionInterceptor()); 
    getBinding(parameterIndex, false).setBinding(Double.valueOf(x), MysqlType.DOUBLE, this.numberOfExecutions, this.sendTypesToServer);
  }
  
  public void setFloat(int parameterIndex, float x) {
    getBinding(parameterIndex, false).setBinding(Float.valueOf(x), MysqlType.FLOAT, this.numberOfExecutions, this.sendTypesToServer);
  }
  
  public void setInt(int parameterIndex, int x) {
    getBinding(parameterIndex, false).setBinding(Integer.valueOf(x), MysqlType.INT, this.numberOfExecutions, this.sendTypesToServer);
  }
  
  public void setLong(int parameterIndex, long x) {
    getBinding(parameterIndex, false).setBinding(Long.valueOf(x), MysqlType.BIGINT, this.numberOfExecutions, this.sendTypesToServer);
  }
  
  public void setNCharacterStream(int parameterIndex, Reader reader, long length) {
    if (reader == null) {
      setNull(parameterIndex);
      return;
    } 
    BindValue binding = getBinding(parameterIndex, true);
    binding.setBinding(reader, MysqlType.TEXT, this.numberOfExecutions, this.sendTypesToServer);
    binding.setScaleOrLength(length);
    binding.setIsNational(true);
  }
  
  public void setNClob(int parameterIndex, NClob value) {
    if (value == null) {
      setNull(parameterIndex);
      return;
    } 
    try {
      setNCharacterStream(parameterIndex, value.getCharacterStream(), value.length());
    } catch (Throwable t) {
      throw ExceptionFactory.createException(t.getMessage(), t, this.session.getExceptionInterceptor());
    } 
  }
  
  public void setNString(int parameterIndex, String x) {
    if (x == null) {
      setNull(parameterIndex);
      return;
    } 
    BindValue binding = getBinding(parameterIndex, false);
    binding.setBinding(x, MysqlType.VARCHAR, this.numberOfExecutions, this.sendTypesToServer);
    binding.setIsNational(true);
  }
  
  public synchronized void setNull(int parameterIndex) {
    BindValue binding = getBinding(parameterIndex, false);
    binding.setBinding(null, MysqlType.NULL, this.numberOfExecutions, this.sendTypesToServer);
  }
  
  public boolean isNull(int parameterIndex) {
    return this.bindValues[parameterIndex].isNull();
  }
  
  public void setShort(int parameterIndex, short x) {
    getBinding(parameterIndex, false).setBinding(Short.valueOf(x), MysqlType.SMALLINT, this.numberOfExecutions, this.sendTypesToServer);
  }
  
  public void setString(int parameterIndex, String x) {
    if (x == null) {
      setNull(parameterIndex);
      return;
    } 
    getBinding(parameterIndex, false).setBinding(x, MysqlType.VARCHAR, this.numberOfExecutions, this.sendTypesToServer);
  }
  
  public void setTime(int parameterIndex, Time x, Calendar cal) {
    if (x == null) {
      setNull(parameterIndex);
      return;
    } 
    BindValue binding = getBinding(parameterIndex, false);
    binding.setBinding(x, MysqlType.TIME, this.numberOfExecutions, this.sendTypesToServer);
    binding.setCalendar((cal == null) ? null : (Calendar)cal.clone());
  }
  
  public void setTimestamp(int parameterIndex, Timestamp x, Calendar targetCalendar, Field field, MysqlType targetMysqlType) {
    if (x == null) {
      setNull(parameterIndex);
      return;
    } 
    if (field == null && 
      this.columnDefinition != null && parameterIndex <= (this.columnDefinition.getFields()).length && parameterIndex >= 0 && this.columnDefinition
      .getFields()[parameterIndex].getDecimals() > 0)
      field = this.columnDefinition.getFields()[parameterIndex]; 
    BindValue binding = getBinding(parameterIndex, false);
    if (field == null)
      binding.setField(field); 
    binding.setBinding(x, targetMysqlType, this.numberOfExecutions, this.sendTypesToServer);
    binding.setCalendar((targetCalendar == null) ? null : (Calendar)targetCalendar.clone());
  }
  
  public void setObject(int parameterIndex, Object parameterObj) {
    if (parameterObj == null) {
      setNull(parameterIndex);
      return;
    } 
    MysqlType defaultMysqlType = DEFAULT_MYSQL_TYPES.get(parameterObj.getClass());
    if (defaultMysqlType == null) {
      Optional<MysqlType> mysqlType = DEFAULT_MYSQL_TYPES.entrySet().stream().filter(m -> ((Class)m.getKey()).isAssignableFrom(parameterObj.getClass())).map(m -> (MysqlType)m.getValue()).findFirst();
      if (mysqlType.isPresent())
        defaultMysqlType = mysqlType.get(); 
    } 
    setObject(parameterIndex, parameterObj, defaultMysqlType, -1);
  }
  
  public void setObject(int parameterIndex, Object parameterObj, MysqlType targetMysqlType, int scaleOrLength) {
    if (parameterObj == null) {
      setNull(parameterIndex);
      return;
    } 
    try {
      if (targetMysqlType == null || targetMysqlType == MysqlType.UNKNOWN || (parameterObj instanceof Date && 
        !((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.treatUtilDateAsTimestamp).getValue()).booleanValue())) {
        setSerializableObject(parameterIndex, parameterObj);
        return;
      } 
      BindValue binding = getBinding(parameterIndex, false);
      if (this.columnDefinition != null && parameterIndex <= (this.columnDefinition.getFields()).length && parameterIndex >= 0)
        binding.setField(this.columnDefinition.getFields()[parameterIndex]); 
      binding.setBinding(parameterObj, targetMysqlType, this.numberOfExecutions, this.sendTypesToServer);
      binding.setScaleOrLength(scaleOrLength);
    } catch (Exception ex) {
      throw ExceptionFactory.createException(
          Messages.getString("PreparedStatement.17") + parameterObj.getClass().toString() + Messages.getString("PreparedStatement.18") + ex
          .getClass().getName() + Messages.getString("PreparedStatement.19") + ex.getMessage(), ex, this.session
          .getExceptionInterceptor());
    } 
  }
  
  protected final void setSerializableObject(int parameterIndex, Object parameterObj) {
    try {
      ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
      ObjectOutputStream objectOut = new ObjectOutputStream(bytesOut);
      objectOut.writeObject(parameterObj);
      objectOut.flush();
      objectOut.close();
      bytesOut.flush();
      bytesOut.close();
      byte[] buf = bytesOut.toByteArray();
      ByteArrayInputStream bytesIn = new ByteArrayInputStream(buf);
      setBinaryStream(parameterIndex, bytesIn, buf.length);
      this.bindValues[parameterIndex].setMysqlType(MysqlType.BINARY);
    } catch (Exception ex) {
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("PreparedStatement.54") + ex.getClass().getName(), ex, this.session
          .getExceptionInterceptor());
    } 
  }
  
  public byte[] getBytesRepresentation(int parameterIndex) {
    byte[] parameterVal = this.bindValues[parameterIndex].getByteValue();
    return (parameterVal == null) ? null : (this.bindValues[parameterIndex].isStream() ? parameterVal : StringUtils.unquoteBytes(parameterVal));
  }
}
