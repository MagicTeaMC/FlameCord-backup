package gnu.trove.impl;

public final class HashFunctions {
  public static int hash(double value) {
    assert !Double.isNaN(value) : "Values of NaN are not supported.";
    long bits = Double.doubleToLongBits(value);
    return (int)(bits ^ bits >>> 32L);
  }
  
  public static int hash(float value) {
    assert !Float.isNaN(value) : "Values of NaN are not supported.";
    return Float.floatToIntBits(value * 6.6360896E8F);
  }
  
  public static int hash(int value) {
    return value;
  }
  
  public static int hash(long value) {
    return (int)(value ^ value >>> 32L);
  }
  
  public static int hash(Object object) {
    return (object == null) ? 0 : object.hashCode();
  }
}
