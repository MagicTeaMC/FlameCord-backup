package com.mysql.cj.result;

import com.mysql.cj.Constants;
import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.NumberOutOfRange;
import com.mysql.cj.util.DataTypeUtil;
import java.math.BigDecimal;
import java.math.BigInteger;

public class ShortValueFactory extends AbstractNumericValueFactory<Short> {
  public ShortValueFactory(PropertySet pset) {
    super(pset);
  }
  
  public Short createFromBigInteger(BigInteger i) {
    if (this.jdbcCompliantTruncationForReads && (i
      .compareTo(Constants.BIG_INTEGER_MIN_SHORT_VALUE) < 0 || i.compareTo(Constants.BIG_INTEGER_MAX_SHORT_VALUE) > 0))
      throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { i, getTargetTypeName() })); 
    return Short.valueOf((short)i.intValue());
  }
  
  public Short createFromLong(long l) {
    if (this.jdbcCompliantTruncationForReads && (l < -32768L || l > 32767L))
      throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Long.valueOf(l).toString(), getTargetTypeName() })); 
    return Short.valueOf((short)(int)l);
  }
  
  public Short createFromBigDecimal(BigDecimal d) {
    if (this.jdbcCompliantTruncationForReads && (d
      .compareTo(Constants.BIG_DECIMAL_MIN_SHORT_VALUE) < 0 || d.compareTo(Constants.BIG_DECIMAL_MAX_SHORT_VALUE) > 0))
      throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { d, getTargetTypeName() })); 
    return Short.valueOf((short)(int)d.longValue());
  }
  
  public Short createFromDouble(double d) {
    if (this.jdbcCompliantTruncationForReads && (d < -32768.0D || d > 32767.0D))
      throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Double.valueOf(d), getTargetTypeName() })); 
    return Short.valueOf((short)(int)d);
  }
  
  public Short createFromBit(byte[] bytes, int offset, int length) {
    long l = DataTypeUtil.bitToLong(bytes, offset, length);
    if (this.jdbcCompliantTruncationForReads && l >> 16L != 0L)
      throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Long.valueOf(l).toString(), getTargetTypeName() })); 
    return Short.valueOf((short)(int)l);
  }
  
  public String getTargetTypeName() {
    return Short.class.getName();
  }
}
