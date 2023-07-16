package com.mysql.cj.result;

import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTime;
import com.mysql.cj.protocol.InternalTimestamp;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface ValueFactory<T> {
  void setPropertySet(PropertySet paramPropertySet);
  
  T createFromDate(InternalDate paramInternalDate);
  
  T createFromTime(InternalTime paramInternalTime);
  
  T createFromTimestamp(InternalTimestamp paramInternalTimestamp);
  
  T createFromDatetime(InternalTimestamp paramInternalTimestamp);
  
  T createFromLong(long paramLong);
  
  T createFromBigInteger(BigInteger paramBigInteger);
  
  T createFromDouble(double paramDouble);
  
  T createFromBigDecimal(BigDecimal paramBigDecimal);
  
  T createFromBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, Field paramField);
  
  T createFromBit(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  T createFromYear(long paramLong);
  
  T createFromNull();
  
  String getTargetTypeName();
}
