package com.mysql.cj.result;

import com.mysql.cj.Constants;
import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.NumberOutOfRange;
import com.mysql.cj.util.DataTypeUtil;
import java.math.BigDecimal;
import java.math.BigInteger;

public class IntegerValueFactory extends AbstractNumericValueFactory<Integer> {
  public IntegerValueFactory(PropertySet pset) {
    super(pset);
  }
  
  public Integer createFromBigInteger(BigInteger i) {
    if (this.jdbcCompliantTruncationForReads && (i
      .compareTo(Constants.BIG_INTEGER_MIN_INTEGER_VALUE) < 0 || i.compareTo(Constants.BIG_INTEGER_MAX_INTEGER_VALUE) > 0))
      throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { i, getTargetTypeName() })); 
    return Integer.valueOf(i.intValue());
  }
  
  public Integer createFromLong(long l) {
    if (this.jdbcCompliantTruncationForReads && (l < -2147483648L || l > 2147483647L))
      throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Long.valueOf(l).toString(), getTargetTypeName() })); 
    return Integer.valueOf((int)l);
  }
  
  public Integer createFromBigDecimal(BigDecimal d) {
    if (this.jdbcCompliantTruncationForReads && (d
      .compareTo(Constants.BIG_DECIMAL_MIN_INTEGER_VALUE) < 0 || d.compareTo(Constants.BIG_DECIMAL_MAX_INTEGER_VALUE) > 0))
      throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { d, getTargetTypeName() })); 
    return Integer.valueOf((int)d.longValue());
  }
  
  public Integer createFromDouble(double d) {
    if (this.jdbcCompliantTruncationForReads && (d < -2.147483648E9D || d > 2.147483647E9D))
      throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Double.valueOf(d), getTargetTypeName() })); 
    return Integer.valueOf((int)d);
  }
  
  public Integer createFromBit(byte[] bytes, int offset, int length) {
    long l = DataTypeUtil.bitToLong(bytes, offset, length);
    if (this.jdbcCompliantTruncationForReads && l >> 32L != 0L)
      throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Long.valueOf(l).toString(), getTargetTypeName() })); 
    return Integer.valueOf((int)l);
  }
  
  public String getTargetTypeName() {
    return Integer.class.getName();
  }
}
