package com.google.common.math;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.math.BigInteger;
import java.math.RoundingMode;

@ElementTypesAreNonnullByDefault
@GwtCompatible
@CanIgnoreReturnValue
final class MathPreconditions {
  static int checkPositive(String role, int x) {
    if (x <= 0)
      throw new IllegalArgumentException((new StringBuilder(26 + String.valueOf(role).length())).append(role).append(" (").append(x).append(") must be > 0").toString()); 
    return x;
  }
  
  static long checkPositive(String role, long x) {
    if (x <= 0L)
      throw new IllegalArgumentException((new StringBuilder(35 + String.valueOf(role).length())).append(role).append(" (").append(x).append(") must be > 0").toString()); 
    return x;
  }
  
  static BigInteger checkPositive(String role, BigInteger x) {
    if (x.signum() <= 0) {
      String str = String.valueOf(x);
      throw new IllegalArgumentException((new StringBuilder(15 + String.valueOf(role).length() + String.valueOf(str).length())).append(role).append(" (").append(str).append(") must be > 0").toString());
    } 
    return x;
  }
  
  static int checkNonNegative(String role, int x) {
    if (x < 0)
      throw new IllegalArgumentException((new StringBuilder(27 + String.valueOf(role).length())).append(role).append(" (").append(x).append(") must be >= 0").toString()); 
    return x;
  }
  
  static long checkNonNegative(String role, long x) {
    if (x < 0L)
      throw new IllegalArgumentException((new StringBuilder(36 + String.valueOf(role).length())).append(role).append(" (").append(x).append(") must be >= 0").toString()); 
    return x;
  }
  
  static BigInteger checkNonNegative(String role, BigInteger x) {
    if (x.signum() < 0) {
      String str = String.valueOf(x);
      throw new IllegalArgumentException((new StringBuilder(16 + String.valueOf(role).length() + String.valueOf(str).length())).append(role).append(" (").append(str).append(") must be >= 0").toString());
    } 
    return x;
  }
  
  static double checkNonNegative(String role, double x) {
    if (x < 0.0D)
      throw new IllegalArgumentException((new StringBuilder(40 + String.valueOf(role).length())).append(role).append(" (").append(x).append(") must be >= 0").toString()); 
    return x;
  }
  
  static void checkRoundingUnnecessary(boolean condition) {
    if (!condition)
      throw new ArithmeticException("mode was UNNECESSARY, but rounding was necessary"); 
  }
  
  static void checkInRangeForRoundingInputs(boolean condition, double input, RoundingMode mode) {
    if (!condition) {
      String str = String.valueOf(mode);
      throw new ArithmeticException((new StringBuilder(83 + String.valueOf(str).length())).append("rounded value is out of range for input ").append(input).append(" and rounding mode ").append(str).toString());
    } 
  }
  
  static void checkNoOverflow(boolean condition, String methodName, int a, int b) {
    if (!condition)
      throw new ArithmeticException((new StringBuilder(36 + String.valueOf(methodName).length())).append("overflow: ").append(methodName).append("(").append(a).append(", ").append(b).append(")").toString()); 
  }
  
  static void checkNoOverflow(boolean condition, String methodName, long a, long b) {
    if (!condition)
      throw new ArithmeticException((new StringBuilder(54 + String.valueOf(methodName).length())).append("overflow: ").append(methodName).append("(").append(a).append(", ").append(b).append(")").toString()); 
  }
}
