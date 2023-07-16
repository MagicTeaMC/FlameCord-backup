package com.mysql.cj;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Constants {
  public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
  
  public static final String MILLIS_I18N = Messages.getString("Milliseconds");
  
  public static final byte[] SLASH_STAR_SPACE_AS_BYTES = new byte[] { 47, 42, 32 };
  
  public static final byte[] SPACE_STAR_SLASH_SPACE_AS_BYTES = new byte[] { 32, 42, 47, 32 };
  
  public static final String JVM_VENDOR = System.getProperty("java.vendor");
  
  public static final String JVM_VERSION = System.getProperty("java.version");
  
  public static final String OS_NAME = System.getProperty("os.name");
  
  public static final String OS_ARCH = System.getProperty("os.arch");
  
  public static final String OS_VERSION = System.getProperty("os.version");
  
  public static final String CJ_NAME = "MySQL Connector/J";
  
  public static final String CJ_FULL_NAME = "mysql-connector-j-8.0.33";
  
  public static final String CJ_REVISION = "7d6b0800528b6b25c68b52dc10d6c1c8429c100c";
  
  public static final String CJ_VERSION = "8.0.33";
  
  public static final String CJ_MAJOR_VERSION = "8";
  
  public static final String CJ_MINOR_VERSION = "0";
  
  public static final String CJ_LICENSE = "GPL";
  
  public static final BigInteger BIG_INTEGER_ZERO = BigInteger.valueOf(0L);
  
  public static final BigInteger BIG_INTEGER_ONE = BigInteger.valueOf(1L);
  
  public static final BigInteger BIG_INTEGER_NEGATIVE_ONE = BigInteger.valueOf(-1L);
  
  public static final BigInteger BIG_INTEGER_MIN_BYTE_VALUE = BigInteger.valueOf(-128L);
  
  public static final BigInteger BIG_INTEGER_MAX_BYTE_VALUE = BigInteger.valueOf(127L);
  
  public static final BigInteger BIG_INTEGER_MIN_SHORT_VALUE = BigInteger.valueOf(-32768L);
  
  public static final BigInteger BIG_INTEGER_MAX_SHORT_VALUE = BigInteger.valueOf(32767L);
  
  public static final BigInteger BIG_INTEGER_MIN_INTEGER_VALUE = BigInteger.valueOf(-2147483648L);
  
  public static final BigInteger BIG_INTEGER_MAX_INTEGER_VALUE = BigInteger.valueOf(2147483647L);
  
  public static final BigInteger BIG_INTEGER_MIN_LONG_VALUE = BigInteger.valueOf(Long.MIN_VALUE);
  
  public static final BigInteger BIG_INTEGER_MAX_LONG_VALUE = BigInteger.valueOf(Long.MAX_VALUE);
  
  public static final BigDecimal BIG_DECIMAL_ZERO = BigDecimal.valueOf(0L);
  
  public static final BigDecimal BIG_DECIMAL_ONE = BigDecimal.valueOf(1L);
  
  public static final BigDecimal BIG_DECIMAL_NEGATIVE_ONE = BigDecimal.valueOf(-1L);
  
  public static final BigDecimal BIG_DECIMAL_MIN_BYTE_VALUE = BigDecimal.valueOf(-128L);
  
  public static final BigDecimal BIG_DECIMAL_MAX_BYTE_VALUE = BigDecimal.valueOf(127L);
  
  public static final BigDecimal BIG_DECIMAL_MIN_SHORT_VALUE = BigDecimal.valueOf(-32768L);
  
  public static final BigDecimal BIG_DECIMAL_MAX_SHORT_VALUE = BigDecimal.valueOf(32767L);
  
  public static final BigDecimal BIG_DECIMAL_MIN_INTEGER_VALUE = BigDecimal.valueOf(-2147483648L);
  
  public static final BigDecimal BIG_DECIMAL_MAX_INTEGER_VALUE = BigDecimal.valueOf(2147483647L);
  
  public static final BigDecimal BIG_DECIMAL_MIN_LONG_VALUE = BigDecimal.valueOf(Long.MIN_VALUE);
  
  public static final BigDecimal BIG_DECIMAL_MAX_LONG_VALUE = BigDecimal.valueOf(Long.MAX_VALUE);
  
  public static final BigDecimal BIG_DECIMAL_MAX_DOUBLE_VALUE = BigDecimal.valueOf(Double.MAX_VALUE);
  
  public static final BigDecimal BIG_DECIMAL_MAX_NEGATIVE_DOUBLE_VALUE = BigDecimal.valueOf(-1.7976931348623157E308D);
  
  public static final BigDecimal BIG_DECIMAL_MAX_FLOAT_VALUE = BigDecimal.valueOf(3.4028234663852886E38D);
  
  public static final BigDecimal BIG_DECIMAL_MAX_NEGATIVE_FLOAT_VALUE = BigDecimal.valueOf(-3.4028234663852886E38D);
  
  public static final int UNSIGNED_BYTE_MAX_VALUE = 255;
}
