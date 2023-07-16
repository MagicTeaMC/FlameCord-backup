package com.google.common.math;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.math.RoundingMode;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
abstract class ToDoubleRounder<X extends Number & Comparable<X>> {
  abstract double roundToDoubleArbitrarily(X paramX);
  
  abstract int sign(X paramX);
  
  abstract X toX(double paramDouble, RoundingMode paramRoundingMode);
  
  abstract X minus(X paramX1, X paramX2);
  
  final double roundToDouble(X x, RoundingMode mode) {
    X roundFloor;
    double roundFloorAsDouble;
    X roundCeiling;
    double roundCeilingAsDouble;
    X deltaToFloor, deltaToCeiling;
    int diff;
    Preconditions.checkNotNull(x, "x");
    Preconditions.checkNotNull(mode, "mode");
    double roundArbitrarily = roundToDoubleArbitrarily(x);
    if (Double.isInfinite(roundArbitrarily)) {
      String str;
      switch (mode) {
        case DOWN:
        case HALF_EVEN:
        case HALF_DOWN:
        case HALF_UP:
          return Double.MAX_VALUE * sign(x);
        case FLOOR:
          return (roundArbitrarily == Double.POSITIVE_INFINITY) ? 
            Double.MAX_VALUE : 
            Double.NEGATIVE_INFINITY;
        case CEILING:
          return (roundArbitrarily == Double.POSITIVE_INFINITY) ? 
            Double.POSITIVE_INFINITY : 
            -1.7976931348623157E308D;
        case UP:
          return roundArbitrarily;
        case UNNECESSARY:
          str = String.valueOf(x);
          throw new ArithmeticException((new StringBuilder(44 + String.valueOf(str).length())).append(str).append(" cannot be represented precisely as a double").toString());
      } 
    } 
    X roundArbitrarilyAsX = toX(roundArbitrarily, RoundingMode.UNNECESSARY);
    int cmpXToRoundArbitrarily = ((Comparable<X>)x).compareTo(roundArbitrarilyAsX);
    switch (mode) {
      case UNNECESSARY:
        MathPreconditions.checkRoundingUnnecessary((cmpXToRoundArbitrarily == 0));
        return roundArbitrarily;
      case FLOOR:
        return (cmpXToRoundArbitrarily >= 0) ? 
          roundArbitrarily : 
          DoubleUtils.nextDown(roundArbitrarily);
      case CEILING:
        return (cmpXToRoundArbitrarily <= 0) ? roundArbitrarily : Math.nextUp(roundArbitrarily);
      case DOWN:
        if (sign(x) >= 0)
          return (cmpXToRoundArbitrarily >= 0) ? 
            roundArbitrarily : 
            DoubleUtils.nextDown(roundArbitrarily); 
        return (cmpXToRoundArbitrarily <= 0) ? roundArbitrarily : Math.nextUp(roundArbitrarily);
      case UP:
        if (sign(x) >= 0)
          return (cmpXToRoundArbitrarily <= 0) ? roundArbitrarily : Math.nextUp(roundArbitrarily); 
        return (cmpXToRoundArbitrarily >= 0) ? 
          roundArbitrarily : 
          DoubleUtils.nextDown(roundArbitrarily);
      case HALF_EVEN:
      case HALF_DOWN:
      case HALF_UP:
        if (cmpXToRoundArbitrarily >= 0) {
          roundFloorAsDouble = roundArbitrarily;
          roundFloor = roundArbitrarilyAsX;
          roundCeilingAsDouble = Math.nextUp(roundArbitrarily);
          if (roundCeilingAsDouble == Double.POSITIVE_INFINITY)
            return roundFloorAsDouble; 
          roundCeiling = toX(roundCeilingAsDouble, RoundingMode.CEILING);
        } else {
          roundCeilingAsDouble = roundArbitrarily;
          roundCeiling = roundArbitrarilyAsX;
          roundFloorAsDouble = DoubleUtils.nextDown(roundArbitrarily);
          if (roundFloorAsDouble == Double.NEGATIVE_INFINITY)
            return roundCeilingAsDouble; 
          roundFloor = toX(roundFloorAsDouble, RoundingMode.FLOOR);
        } 
        deltaToFloor = minus(x, roundFloor);
        deltaToCeiling = minus(roundCeiling, x);
        diff = ((Comparable<X>)deltaToFloor).compareTo(deltaToCeiling);
        if (diff < 0)
          return roundFloorAsDouble; 
        if (diff > 0)
          return roundCeilingAsDouble; 
        switch (mode) {
          case HALF_EVEN:
            return ((Double.doubleToRawLongBits(roundFloorAsDouble) & 0x1L) == 0L) ? 
              roundFloorAsDouble : 
              roundCeilingAsDouble;
          case HALF_DOWN:
            return (sign(x) >= 0) ? roundFloorAsDouble : roundCeilingAsDouble;
          case HALF_UP:
            return (sign(x) >= 0) ? roundCeilingAsDouble : roundFloorAsDouble;
        } 
        throw new AssertionError("impossible");
    } 
    throw new AssertionError("impossible");
  }
}
