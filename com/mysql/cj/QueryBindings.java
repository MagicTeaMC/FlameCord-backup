package com.mysql.cj;

import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.result.Field;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;

public interface QueryBindings {
  QueryBindings clone();
  
  void setColumnDefinition(ColumnDefinition paramColumnDefinition);
  
  BindValue[] getBindValues();
  
  void setBindValues(BindValue[] paramArrayOfBindValue);
  
  boolean clearBindValues();
  
  void checkParameterSet(int paramInt);
  
  void checkAllParametersSet();
  
  int getNumberOfExecutions();
  
  void setNumberOfExecutions(int paramInt);
  
  boolean isLongParameterSwitchDetected();
  
  void setLongParameterSwitchDetected(boolean paramBoolean);
  
  AtomicBoolean getSendTypesToServer();
  
  BindValue getBinding(int paramInt, boolean paramBoolean);
  
  void setFromBindValue(int paramInt, BindValue paramBindValue);
  
  void setAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2);
  
  void setBigDecimal(int paramInt, BigDecimal paramBigDecimal);
  
  void setBigInteger(int paramInt, BigInteger paramBigInteger);
  
  void setBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2);
  
  void setBlob(int paramInt, Blob paramBlob);
  
  void setBoolean(int paramInt, boolean paramBoolean);
  
  void setByte(int paramInt, byte paramByte);
  
  void setBytes(int paramInt, byte[] paramArrayOfbyte, boolean paramBoolean);
  
  void setCharacterStream(int paramInt1, Reader paramReader, int paramInt2);
  
  void setClob(int paramInt, Clob paramClob);
  
  void setDate(int paramInt, Date paramDate, Calendar paramCalendar);
  
  void setDouble(int paramInt, double paramDouble);
  
  void setFloat(int paramInt, float paramFloat);
  
  void setInt(int paramInt1, int paramInt2);
  
  void setLong(int paramInt, long paramLong);
  
  void setNCharacterStream(int paramInt, Reader paramReader, long paramLong);
  
  void setNClob(int paramInt, NClob paramNClob);
  
  void setNString(int paramInt, String paramString);
  
  void setNull(int paramInt);
  
  boolean isNull(int paramInt);
  
  void setObject(int paramInt, Object paramObject);
  
  void setObject(int paramInt1, Object paramObject, MysqlType paramMysqlType, int paramInt2);
  
  void setShort(int paramInt, short paramShort);
  
  void setString(int paramInt, String paramString);
  
  void setTime(int paramInt, Time paramTime, Calendar paramCalendar);
  
  void setTimestamp(int paramInt, Timestamp paramTimestamp, Calendar paramCalendar, Field paramField, MysqlType paramMysqlType);
  
  byte[] getBytesRepresentation(int paramInt);
}
