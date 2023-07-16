package org.apache.commons.codec.binary;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

public class Hex implements BinaryEncoder, BinaryDecoder {
  public static final Charset DEFAULT_CHARSET = Charsets.UTF_8;
  
  public static final String DEFAULT_CHARSET_NAME = "UTF-8";
  
  private static final char[] DIGITS_LOWER = new char[] { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'a', 'b', 'c', 'd', 'e', 'f' };
  
  private static final char[] DIGITS_UPPER = new char[] { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'A', 'B', 'C', 'D', 'E', 'F' };
  
  private final Charset charset;
  
  public static byte[] decodeHex(String data) throws DecoderException {
    return decodeHex(data.toCharArray());
  }
  
  public static byte[] decodeHex(char[] data) throws DecoderException {
    int len = data.length;
    if ((len & 0x1) != 0)
      throw new DecoderException("Odd number of characters."); 
    byte[] out = new byte[len >> 1];
    for (int i = 0, j = 0; j < len; i++) {
      int f = toDigit(data[j], j) << 4;
      j++;
      f |= toDigit(data[j], j);
      j++;
      out[i] = (byte)(f & 0xFF);
    } 
    return out;
  }
  
  public static char[] encodeHex(byte[] data) {
    return encodeHex(data, true);
  }
  
  public static char[] encodeHex(ByteBuffer data) {
    return encodeHex(data, true);
  }
  
  public static char[] encodeHex(byte[] data, boolean toLowerCase) {
    return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
  }
  
  public static char[] encodeHex(ByteBuffer data, boolean toLowerCase) {
    return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
  }
  
  protected static char[] encodeHex(byte[] data, char[] toDigits) {
    int l = data.length;
    char[] out = new char[l << 1];
    for (int i = 0, j = 0; i < l; i++) {
      out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
      out[j++] = toDigits[0xF & data[i]];
    } 
    return out;
  }
  
  protected static char[] encodeHex(ByteBuffer data, char[] toDigits) {
    return encodeHex(data.array(), toDigits);
  }
  
  public static String encodeHexString(byte[] data) {
    return new String(encodeHex(data));
  }
  
  public static String encodeHexString(byte[] data, boolean toLowerCase) {
    return new String(encodeHex(data, toLowerCase));
  }
  
  public static String encodeHexString(ByteBuffer data) {
    return new String(encodeHex(data));
  }
  
  public static String encodeHexString(ByteBuffer data, boolean toLowerCase) {
    return new String(encodeHex(data, toLowerCase));
  }
  
  protected static int toDigit(char ch, int index) throws DecoderException {
    int digit = Character.digit(ch, 16);
    if (digit == -1)
      throw new DecoderException("Illegal hexadecimal character " + ch + " at index " + index); 
    return digit;
  }
  
  public Hex() {
    this.charset = DEFAULT_CHARSET;
  }
  
  public Hex(Charset charset) {
    this.charset = charset;
  }
  
  public Hex(String charsetName) {
    this(Charset.forName(charsetName));
  }
  
  public byte[] decode(byte[] array) throws DecoderException {
    return decodeHex((new String(array, getCharset())).toCharArray());
  }
  
  public byte[] decode(ByteBuffer buffer) throws DecoderException {
    return decodeHex((new String(buffer.array(), getCharset())).toCharArray());
  }
  
  public Object decode(Object object) throws DecoderException {
    if (object instanceof String)
      return decode(((String)object).toCharArray()); 
    if (object instanceof byte[])
      return decode((byte[])object); 
    if (object instanceof ByteBuffer)
      return decode((ByteBuffer)object); 
    try {
      return decodeHex((char[])object);
    } catch (ClassCastException e) {
      throw new DecoderException(e.getMessage(), e);
    } 
  }
  
  public byte[] encode(byte[] array) {
    return encodeHexString(array).getBytes(getCharset());
  }
  
  public byte[] encode(ByteBuffer array) {
    return encodeHexString(array).getBytes(getCharset());
  }
  
  public Object encode(Object object) throws EncoderException {
    byte[] byteArray;
    if (object instanceof String) {
      byteArray = ((String)object).getBytes(getCharset());
    } else if (object instanceof ByteBuffer) {
      byteArray = ((ByteBuffer)object).array();
    } else {
      try {
        byteArray = (byte[])object;
      } catch (ClassCastException e) {
        throw new EncoderException(e.getMessage(), e);
      } 
    } 
    return encodeHex(byteArray);
  }
  
  public Charset getCharset() {
    return this.charset;
  }
  
  public String getCharsetName() {
    return this.charset.name();
  }
  
  public String toString() {
    return super.toString() + "[charsetName=" + this.charset + "]";
  }
}
