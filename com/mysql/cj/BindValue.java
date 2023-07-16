package com.mysql.cj;

import com.mysql.cj.protocol.Message;
import com.mysql.cj.result.Field;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;

public interface BindValue {
  BindValue clone();
  
  void reset();
  
  boolean isNull();
  
  void setNull(boolean paramBoolean);
  
  boolean isStream();
  
  MysqlType getMysqlType();
  
  void setMysqlType(MysqlType paramMysqlType);
  
  byte[] getByteValue();
  
  boolean isSet();
  
  void setBinding(Object paramObject, MysqlType paramMysqlType, int paramInt, AtomicBoolean paramAtomicBoolean);
  
  Calendar getCalendar();
  
  void setCalendar(Calendar paramCalendar);
  
  boolean escapeBytesIfNeeded();
  
  void setEscapeBytesIfNeeded(boolean paramBoolean);
  
  Object getValue();
  
  boolean isNational();
  
  void setIsNational(boolean paramBoolean);
  
  int getFieldType();
  
  long getTextLength();
  
  long getBinaryLength();
  
  long getBoundBeforeExecutionNum();
  
  String getString();
  
  Field getField();
  
  void setField(Field paramField);
  
  boolean keepOrigNanos();
  
  void setKeepOrigNanos(boolean paramBoolean);
  
  void setScaleOrLength(long paramLong);
  
  long getScaleOrLength();
  
  String getName();
  
  void setName(String paramString);
  
  void writeAsText(Message paramMessage);
  
  void writeAsBinary(Message paramMessage);
  
  void writeAsQueryAttribute(Message paramMessage);
}
