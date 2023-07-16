package gnu.trove.impl;

public class Constants {
  private static final boolean VERBOSE;
  
  public static final int DEFAULT_CAPACITY = 10;
  
  public static final float DEFAULT_LOAD_FACTOR = 0.5F;
  
  public static final byte DEFAULT_BYTE_NO_ENTRY_VALUE;
  
  public static final short DEFAULT_SHORT_NO_ENTRY_VALUE;
  
  public static final char DEFAULT_CHAR_NO_ENTRY_VALUE;
  
  public static final int DEFAULT_INT_NO_ENTRY_VALUE;
  
  public static final long DEFAULT_LONG_NO_ENTRY_VALUE;
  
  public static final float DEFAULT_FLOAT_NO_ENTRY_VALUE;
  
  public static final double DEFAULT_DOUBLE_NO_ENTRY_VALUE;
  
  static {
    short s;
    int i;
    long l;
    float f;
    double value;
    boolean verbose = false;
    try {
      verbose = (System.getProperty("gnu.trove.verbose", null) != null);
    } catch (SecurityException securityException) {}
    VERBOSE = verbose;
    String property = "0";
    try {
      property = System.getProperty("gnu.trove.no_entry.byte", property);
    } catch (SecurityException securityException) {}
    if ("MAX_VALUE".equalsIgnoreCase(property)) {
      s = 127;
    } else if ("MIN_VALUE".equalsIgnoreCase(property)) {
      s = -128;
    } else {
      s = Byte.valueOf(property).byteValue();
    } 
    if (s > 127) {
      s = 127;
    } else if (s < -128) {
      s = -128;
    } 
    DEFAULT_BYTE_NO_ENTRY_VALUE = s;
    if (VERBOSE)
      System.out.println("DEFAULT_BYTE_NO_ENTRY_VALUE: " + DEFAULT_BYTE_NO_ENTRY_VALUE); 
    property = "0";
    try {
      property = System.getProperty("gnu.trove.no_entry.short", property);
    } catch (SecurityException securityException) {}
    if ("MAX_VALUE".equalsIgnoreCase(property)) {
      short s1 = Short.MAX_VALUE;
    } else if ("MIN_VALUE".equalsIgnoreCase(property)) {
      short s1 = Short.MIN_VALUE;
    } else {
      s = Short.valueOf(property).shortValue();
    } 
    if (s > Short.MAX_VALUE) {
      s = Short.MAX_VALUE;
    } else if (s < Short.MIN_VALUE) {
      s = Short.MIN_VALUE;
    } 
    DEFAULT_SHORT_NO_ENTRY_VALUE = s;
    if (VERBOSE)
      System.out.println("DEFAULT_SHORT_NO_ENTRY_VALUE: " + DEFAULT_SHORT_NO_ENTRY_VALUE); 
    property = "\000";
    try {
      property = System.getProperty("gnu.trove.no_entry.char", property);
    } catch (SecurityException securityException) {}
    if ("MAX_VALUE".equalsIgnoreCase(property)) {
      i = 65535;
    } else if ("MIN_VALUE".equalsIgnoreCase(property)) {
      i = 0;
    } else {
      i = property.toCharArray()[0];
    } 
    if (i > 65535) {
      i = 65535;
    } else if (i < 0) {
      i = 0;
    } 
    DEFAULT_CHAR_NO_ENTRY_VALUE = i;
    if (VERBOSE)
      System.out.println("DEFAULT_CHAR_NO_ENTRY_VALUE: " + 
          Integer.valueOf(i)); 
    property = "0";
    try {
      property = System.getProperty("gnu.trove.no_entry.int", property);
    } catch (SecurityException securityException) {}
    if ("MAX_VALUE".equalsIgnoreCase(property)) {
      int j = Integer.MAX_VALUE;
    } else if ("MIN_VALUE".equalsIgnoreCase(property)) {
      int j = Integer.MIN_VALUE;
    } else {
      i = Integer.valueOf(property).intValue();
    } 
    DEFAULT_INT_NO_ENTRY_VALUE = i;
    if (VERBOSE)
      System.out.println("DEFAULT_INT_NO_ENTRY_VALUE: " + DEFAULT_INT_NO_ENTRY_VALUE); 
    String str1 = "0";
    try {
      str1 = System.getProperty("gnu.trove.no_entry.long", str1);
    } catch (SecurityException securityException) {}
    if ("MAX_VALUE".equalsIgnoreCase(str1)) {
      l = Long.MAX_VALUE;
    } else if ("MIN_VALUE".equalsIgnoreCase(str1)) {
      l = Long.MIN_VALUE;
    } else {
      l = Long.valueOf(str1).longValue();
    } 
    DEFAULT_LONG_NO_ENTRY_VALUE = l;
    if (VERBOSE)
      System.out.println("DEFAULT_LONG_NO_ENTRY_VALUE: " + DEFAULT_LONG_NO_ENTRY_VALUE); 
    property = "0";
    try {
      property = System.getProperty("gnu.trove.no_entry.float", property);
    } catch (SecurityException securityException) {}
    if ("MAX_VALUE".equalsIgnoreCase(property)) {
      f = Float.MAX_VALUE;
    } else if ("MIN_VALUE".equalsIgnoreCase(property)) {
      f = Float.MIN_VALUE;
    } else if ("MIN_NORMAL".equalsIgnoreCase(property)) {
      f = 1.17549435E-38F;
    } else if ("NEGATIVE_INFINITY".equalsIgnoreCase(property)) {
      f = Float.NEGATIVE_INFINITY;
    } else if ("POSITIVE_INFINITY".equalsIgnoreCase(property)) {
      f = Float.POSITIVE_INFINITY;
    } else {
      f = Float.valueOf(property).floatValue();
    } 
    DEFAULT_FLOAT_NO_ENTRY_VALUE = f;
    if (VERBOSE)
      System.out.println("DEFAULT_FLOAT_NO_ENTRY_VALUE: " + DEFAULT_FLOAT_NO_ENTRY_VALUE); 
    str1 = "0";
    try {
      str1 = System.getProperty("gnu.trove.no_entry.double", str1);
    } catch (SecurityException securityException) {}
    if ("MAX_VALUE".equalsIgnoreCase(str1)) {
      value = Double.MAX_VALUE;
    } else if ("MIN_VALUE".equalsIgnoreCase(str1)) {
      value = Double.MIN_VALUE;
    } else if ("MIN_NORMAL".equalsIgnoreCase(str1)) {
      value = 2.2250738585072014E-308D;
    } else if ("NEGATIVE_INFINITY".equalsIgnoreCase(str1)) {
      value = Double.NEGATIVE_INFINITY;
    } else if ("POSITIVE_INFINITY".equalsIgnoreCase(str1)) {
      value = Double.POSITIVE_INFINITY;
    } else {
      value = Double.valueOf(str1).doubleValue();
    } 
    DEFAULT_DOUBLE_NO_ENTRY_VALUE = value;
    if (VERBOSE)
      System.out.println("DEFAULT_DOUBLE_NO_ENTRY_VALUE: " + DEFAULT_DOUBLE_NO_ENTRY_VALUE); 
  }
}
