package com.mysql.cj.result;

import com.mysql.cj.Constants;
import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.NumberOutOfRange;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class DoubleValueFactory extends AbstractNumericValueFactory<Double> {
  public DoubleValueFactory(PropertySet pset) {
    super(pset);
  }
  
  public Double createFromBigInteger(BigInteger i) {
    if (this.jdbcCompliantTruncationForReads && ((new BigDecimal(i)).compareTo(Constants.BIG_DECIMAL_MAX_NEGATIVE_DOUBLE_VALUE) < 0 || (new BigDecimal(i))
      .compareTo(Constants.BIG_DECIMAL_MAX_DOUBLE_VALUE) > 0))
      throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { i, getTargetTypeName() })); 
    return Double.valueOf(i.doubleValue());
  }
  
  public Double createFromLong(long l) {
    if (this.jdbcCompliantTruncationForReads && (l < -1.7976931348623157E308D || l > Double.MAX_VALUE))
      throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Long.valueOf(l), getTargetTypeName() })); 
    return Double.valueOf(l);
  }
  
  public Double createFromBigDecimal(BigDecimal d) {
    if (this.jdbcCompliantTruncationForReads && (d
      .compareTo(Constants.BIG_DECIMAL_MAX_NEGATIVE_DOUBLE_VALUE) < 0 || d.compareTo(Constants.BIG_DECIMAL_MAX_DOUBLE_VALUE) > 0))
      throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { d, getTargetTypeName() })); 
    return Double.valueOf(d.doubleValue());
  }
  
  public Double createFromDouble(double d) {
    if (this.jdbcCompliantTruncationForReads && (d < -1.7976931348623157E308D || d > Double.MAX_VALUE))
      throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Double.valueOf(d), getTargetTypeName() })); 
    return Double.valueOf(d);
  }
  
  public Double createFromBit(byte[] bytes, int offset, int length) {
    return Double.valueOf((new BigInteger(ByteBuffer.allocate(length + 1).put((byte)0).put(bytes, offset, length).array())).doubleValue());
  }
  
  public String getTargetTypeName() {
    return Double.class.getName();
  }
}
