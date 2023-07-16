package com.mysql.cj.result;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.DataConversionException;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTime;
import com.mysql.cj.protocol.InternalTimestamp;
import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class DefaultValueFactory<T> implements ValueFactory<T> {
  protected boolean jdbcCompliantTruncationForReads = true;
  
  protected PropertySet pset;
  
  public DefaultValueFactory(PropertySet pset) {
    this.pset = null;
    this.pset = pset;
    this.jdbcCompliantTruncationForReads = ((Boolean)this.pset.getBooleanProperty(PropertyKey.jdbcCompliantTruncation).getInitialValue()).booleanValue();
  }
  
  public void setPropertySet(PropertySet pset) {
    this.pset = pset;
  }
  
  protected T unsupported(String sourceType) {
    throw new DataConversionException(Messages.getString("ResultSet.UnsupportedConversion", new Object[] { sourceType, getTargetTypeName() }));
  }
  
  public T createFromDate(InternalDate idate) {
    return unsupported("DATE");
  }
  
  public T createFromTime(InternalTime it) {
    return unsupported("TIME");
  }
  
  public T createFromTimestamp(InternalTimestamp its) {
    return unsupported("TIMESTAMP");
  }
  
  public T createFromDatetime(InternalTimestamp its) {
    return unsupported("DATETIME");
  }
  
  public T createFromLong(long l) {
    return unsupported("LONG");
  }
  
  public T createFromBigInteger(BigInteger i) {
    return unsupported("BIGINT");
  }
  
  public T createFromDouble(double d) {
    return unsupported("DOUBLE");
  }
  
  public T createFromBigDecimal(BigDecimal d) {
    return unsupported("DECIMAL");
  }
  
  public T createFromBit(byte[] bytes, int offset, int length) {
    return unsupported("BIT");
  }
  
  public T createFromYear(long l) {
    return unsupported("YEAR");
  }
  
  public T createFromNull() {
    return null;
  }
}
