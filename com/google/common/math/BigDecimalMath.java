package com.google.common.math;

import com.google.common.annotations.GwtIncompatible;
import java.math.BigDecimal;
import java.math.RoundingMode;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public class BigDecimalMath {
  public static double roundToDouble(BigDecimal x, RoundingMode mode) {
    return BigDecimalToDoubleRounder.INSTANCE.roundToDouble(x, mode);
  }
  
  private static class BigDecimalToDoubleRounder extends ToDoubleRounder<BigDecimal> {
    static final BigDecimalToDoubleRounder INSTANCE = new BigDecimalToDoubleRounder();
    
    double roundToDoubleArbitrarily(BigDecimal bigDecimal) {
      return bigDecimal.doubleValue();
    }
    
    int sign(BigDecimal bigDecimal) {
      return bigDecimal.signum();
    }
    
    BigDecimal toX(double d, RoundingMode mode) {
      return new BigDecimal(d);
    }
    
    BigDecimal minus(BigDecimal a, BigDecimal b) {
      return a.subtract(b);
    }
  }
}
