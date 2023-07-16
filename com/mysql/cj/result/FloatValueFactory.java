package com.mysql.cj.result;

import com.mysql.cj.Constants;
import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.NumberOutOfRange;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class FloatValueFactory extends AbstractNumericValueFactory<Float> {
  public FloatValueFactory(PropertySet pset) {
    super(pset);
  }
  
  public Float createFromBigInteger(BigInteger i) {
    if (this.jdbcCompliantTruncationForReads && ((new BigDecimal(i)).compareTo(Constants.BIG_DECIMAL_MAX_NEGATIVE_FLOAT_VALUE) < 0 || (new BigDecimal(i))
      .compareTo(Constants.BIG_DECIMAL_MAX_FLOAT_VALUE) > 0))
      throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { i, getTargetTypeName() })); 
    return Float.valueOf((float)i.doubleValue());
  }
  
  public Float createFromLong(long l) {
    if (this.jdbcCompliantTruncationForReads && ((float)l < -3.4028235E38F || (float)l > Float.MAX_VALUE))
      throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Long.valueOf(l), getTargetTypeName() })); 
    return Float.valueOf((float)l);
  }
  
  public Float createFromBigDecimal(BigDecimal d) {
    if (this.jdbcCompliantTruncationForReads && (d
      .compareTo(Constants.BIG_DECIMAL_MAX_NEGATIVE_FLOAT_VALUE) < 0 || d.compareTo(Constants.BIG_DECIMAL_MAX_FLOAT_VALUE) > 0))
      throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { d, getTargetTypeName() })); 
    return Float.valueOf((float)d.doubleValue());
  }
  
  public Float createFromDouble(double d) {
    if (this.jdbcCompliantTruncationForReads && (d < -3.4028234663852886E38D || d > 3.4028234663852886E38D))
      throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Double.valueOf(d), getTargetTypeName() })); 
    return Float.valueOf((float)d);
  }
  
  public Float createFromBit(byte[] bytes, int offset, int length) {
    return Float.valueOf((new BigInteger(ByteBuffer.allocate(length + 1).put((byte)0).put(bytes, offset, length).array())).floatValue());
  }
  
  public String getTargetTypeName() {
    return Float.class.getName();
  }
}
