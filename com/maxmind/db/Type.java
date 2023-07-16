package com.maxmind.db;

enum Type {
  EXTENDED, POINTER, UTF8_STRING, DOUBLE, BYTES, UINT16, UINT32, MAP, INT32, UINT64, UINT128, ARRAY, CONTAINER, END_MARKER, BOOLEAN, FLOAT;
  
  static final Type[] values;
  
  static {
    values = values();
  }
  
  static Type get(int i) throws InvalidDatabaseException {
    if (i >= values.length)
      throw new InvalidDatabaseException("The MaxMind DB file's data section contains bad data"); 
    return values[i];
  }
  
  private static Type get(byte b) throws InvalidDatabaseException {
    return get(b & 0xFF);
  }
  
  static Type fromControlByte(int b) throws InvalidDatabaseException {
    return get((byte)((0xFF & b) >>> 5));
  }
}
