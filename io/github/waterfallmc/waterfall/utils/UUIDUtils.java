package io.github.waterfallmc.waterfall.utils;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Longs;
import java.util.UUID;

public class UUIDUtils {
  public static String undash(String id) {
    return (new StringBuilder(32)).append(id, 0, 8).append(id, 9, 13).append(id, 14, 18).append(id, 19, 23).append(id, 24, 36).toString();
  }
  
  public static String toMojangString(UUID id) {
    Preconditions.checkNotNull(id, "Null id");
    return Hex.encodeString(toBytes(id));
  }
  
  public static UUID fromString(String s) {
    Preconditions.checkNotNull(s, "Null string");
    if (s.length() == 36) {
      s = undash(s);
    } else if (s.length() != 32) {
      throw new IllegalArgumentException("Invalid UUID: " + s);
    } 
    return fromMojangString0(s);
  }
  
  public static UUID fromMojangString(String s) {
    Preconditions.checkNotNull(s, "Null string");
    if (s.length() != 32)
      throw new IllegalArgumentException("UUID not in mojang format: " + s); 
    return fromMojangString0(s);
  }
  
  private static UUID fromMojangString0(String s) {
    assert s != null : "Null string";
    assert s.length() == 32 : "invalid length: " + s;
    try {
      return fromBytes(Hex.decode(s));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid UUID: " + s);
    } 
  }
  
  public static byte[] toBytes(UUID id) {
    Preconditions.checkNotNull(id, "Null id");
    byte[] result = new byte[16];
    long lsb = id.getLeastSignificantBits();
    for (int i = 15; i >= 8; i--) {
      result[i] = (byte)(int)(lsb & 0xFFL);
      lsb >>= 8L;
    } 
    long msb = id.getMostSignificantBits();
    for (int j = 7; j >= 0; j--) {
      result[j] = (byte)(int)(msb & 0xFFL);
      msb >>= 8L;
    } 
    return result;
  }
  
  public static UUID fromBytes(byte[] bytes) {
    Preconditions.checkNotNull(bytes, "Null bytes");
    if (bytes.length != 16)
      throw new IllegalArgumentException("Invalid length: " + bytes.length); 
    long msb = Longs.fromBytes(bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7]);
    long lsb = Longs.fromBytes(bytes[8], bytes[9], bytes[10], bytes[11], bytes[12], bytes[13], bytes[14], bytes[15]);
    return new UUID(msb, lsb);
  }
}
