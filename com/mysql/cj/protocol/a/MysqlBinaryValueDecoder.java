package com.mysql.cj.protocol.a;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.DataReadException;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTime;
import com.mysql.cj.protocol.InternalTimestamp;
import com.mysql.cj.protocol.ValueDecoder;
import com.mysql.cj.result.Field;
import com.mysql.cj.result.ValueFactory;
import com.mysql.cj.util.StringUtils;
import java.math.BigDecimal;
import java.math.BigInteger;

public class MysqlBinaryValueDecoder implements ValueDecoder {
  public <T> T decodeTimestamp(byte[] bytes, int offset, int length, int scale, ValueFactory<T> vf) {
    if (length == 0)
      return (T)vf.createFromTimestamp(new InternalTimestamp()); 
    if (length != 4 && length != 11 && length != 7)
      throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "TIMESTAMP" })); 
    int year = 0;
    int month = 0;
    int day = 0;
    int hours = 0;
    int minutes = 0;
    int seconds = 0;
    int nanos = 0;
    year = bytes[offset + 0] & 0xFF | (bytes[offset + 1] & 0xFF) << 8;
    month = bytes[offset + 2];
    day = bytes[offset + 3];
    if (length > 4) {
      hours = bytes[offset + 4];
      minutes = bytes[offset + 5];
      seconds = bytes[offset + 6];
    } 
    if (length > 7)
      nanos = 1000 * (bytes[offset + 7] & 0xFF | (bytes[offset + 8] & 0xFF) << 8 | (bytes[offset + 9] & 0xFF) << 16 | (bytes[offset + 10] & 0xFF) << 24); 
    return (T)vf.createFromTimestamp(new InternalTimestamp(year, month, day, hours, minutes, seconds, nanos, scale));
  }
  
  public <T> T decodeDatetime(byte[] bytes, int offset, int length, int scale, ValueFactory<T> vf) {
    if (length == 0)
      return (T)vf.createFromTimestamp(new InternalTimestamp()); 
    if (length != 4 && length != 11 && length != 7)
      throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "TIMESTAMP" })); 
    int year = 0;
    int month = 0;
    int day = 0;
    int hours = 0;
    int minutes = 0;
    int seconds = 0;
    int nanos = 0;
    year = bytes[offset + 0] & 0xFF | (bytes[offset + 1] & 0xFF) << 8;
    month = bytes[offset + 2];
    day = bytes[offset + 3];
    if (length > 4) {
      hours = bytes[offset + 4];
      minutes = bytes[offset + 5];
      seconds = bytes[offset + 6];
    } 
    if (length > 7)
      nanos = 1000 * (bytes[offset + 7] & 0xFF | (bytes[offset + 8] & 0xFF) << 8 | (bytes[offset + 9] & 0xFF) << 16 | (bytes[offset + 10] & 0xFF) << 24); 
    return (T)vf.createFromDatetime(new InternalTimestamp(year, month, day, hours, minutes, seconds, nanos, scale));
  }
  
  public <T> T decodeTime(byte[] bytes, int offset, int length, int scale, ValueFactory<T> vf) {
    if (length == 0)
      return (T)vf.createFromTime(new InternalTime()); 
    if (length != 12 && length != 8)
      throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "TIME" })); 
    int days = 0;
    int hours = 0;
    int minutes = 0;
    int seconds = 0;
    int nanos = 0;
    boolean negative = (bytes[offset] == 1);
    days = bytes[offset + 1] & 0xFF | (bytes[offset + 2] & 0xFF) << 8 | (bytes[offset + 3] & 0xFF) << 16 | (bytes[offset + 4] & 0xFF) << 24;
    hours = bytes[offset + 5];
    minutes = bytes[offset + 6];
    seconds = bytes[offset + 7];
    if (negative)
      days *= -1; 
    if (length > 8)
      nanos = 1000 * (bytes[offset + 8] & 0xFF | (bytes[offset + 9] & 0xFF) << 8 | (bytes[offset + 10] & 0xFF) << 16 | (bytes[offset + 11] & 0xFF) << 24); 
    return (T)vf.createFromTime(new InternalTime(days * 24 + hours, minutes, seconds, nanos, scale));
  }
  
  public <T> T decodeDate(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
    if (length == 0)
      return (T)vf.createFromDate(new InternalDate()); 
    if (length != 4)
      throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "DATE" })); 
    int year = bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8;
    int month = bytes[offset + 2];
    int day = bytes[offset + 3];
    return (T)vf.createFromDate(new InternalDate(year, month, day));
  }
  
  public <T> T decodeUInt1(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
    if (length != 1)
      throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "BYTE" })); 
    return (T)vf.createFromLong((bytes[offset] & 0xFF));
  }
  
  public <T> T decodeInt1(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
    if (length != 1)
      throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "BYTE" })); 
    return (T)vf.createFromLong(bytes[offset]);
  }
  
  public <T> T decodeUInt2(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
    if (length != 2)
      throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "SHORT" })); 
    int asInt = bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8;
    return (T)vf.createFromLong(asInt);
  }
  
  public <T> T decodeInt2(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
    if (length != 2)
      throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "SHORT" })); 
    short asShort = (short)(bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8);
    return (T)vf.createFromLong(asShort);
  }
  
  public <T> T decodeUInt4(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
    if (length != 4)
      throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "INT" })); 
    long asLong = (bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8 | (bytes[offset + 2] & 0xFF) << 16) | (bytes[offset + 3] & 0xFF) << 24L;
    return (T)vf.createFromLong(asLong);
  }
  
  public <T> T decodeInt4(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
    if (length != 4)
      throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "SHORT" })); 
    int asInt = bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8 | (bytes[offset + 2] & 0xFF) << 16 | (bytes[offset + 3] & 0xFF) << 24;
    return (T)vf.createFromLong(asInt);
  }
  
  public <T> T decodeInt8(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
    if (length != 8)
      throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "LONG" })); 
    long asLong = (bytes[offset] & 0xFF) | (bytes[offset + 1] & 0xFF) << 8L | (bytes[offset + 2] & 0xFF) << 16L | (bytes[offset + 3] & 0xFF) << 24L | (bytes[offset + 4] & 0xFF) << 32L | (bytes[offset + 5] & 0xFF) << 40L | (bytes[offset + 6] & 0xFF) << 48L | (bytes[offset + 7] & 0xFF) << 56L;
    return (T)vf.createFromLong(asLong);
  }
  
  public <T> T decodeUInt8(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
    if (length != 8)
      throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "LONG" })); 
    if ((bytes[offset + 7] & 0x80) == 0)
      return decodeInt8(bytes, offset, length, vf); 
    byte[] bigEndian = { 0, bytes[offset + 7], bytes[offset + 6], bytes[offset + 5], bytes[offset + 4], bytes[offset + 3], bytes[offset + 2], bytes[offset + 1], bytes[offset] };
    BigInteger bigInt = new BigInteger(bigEndian);
    return (T)vf.createFromBigInteger(bigInt);
  }
  
  public <T> T decodeFloat(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
    if (length != 4)
      throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "FLOAT" })); 
    int asInt = bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8 | (bytes[offset + 2] & 0xFF) << 16 | (bytes[offset + 3] & 0xFF) << 24;
    return (T)vf.createFromDouble(Float.intBitsToFloat(asInt));
  }
  
  public <T> T decodeDouble(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
    if (length != 8)
      throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "DOUBLE" })); 
    long valueAsLong = (bytes[offset + 0] & 0xFF) | (bytes[offset + 1] & 0xFF) << 8L | (bytes[offset + 2] & 0xFF) << 16L | (bytes[offset + 3] & 0xFF) << 24L | (bytes[offset + 4] & 0xFF) << 32L | (bytes[offset + 5] & 0xFF) << 40L | (bytes[offset + 6] & 0xFF) << 48L | (bytes[offset + 7] & 0xFF) << 56L;
    return (T)vf.createFromDouble(Double.longBitsToDouble(valueAsLong));
  }
  
  public <T> T decodeDecimal(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
    BigDecimal d = new BigDecimal(StringUtils.toAsciiCharArray(bytes, offset, length));
    return (T)vf.createFromBigDecimal(d);
  }
  
  public <T> T decodeByteArray(byte[] bytes, int offset, int length, Field f, ValueFactory<T> vf) {
    return (T)vf.createFromBytes(bytes, offset, length, f);
  }
  
  public <T> T decodeBit(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
    return (T)vf.createFromBit(bytes, offset, length);
  }
  
  public <T> T decodeSet(byte[] bytes, int offset, int length, Field f, ValueFactory<T> vf) {
    return decodeByteArray(bytes, offset, length, f, vf);
  }
  
  public <T> T decodeYear(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
    if (length != 2)
      throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "YEAR" })); 
    short asShort = (short)(bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8);
    return (T)vf.createFromYear(asShort);
  }
}
