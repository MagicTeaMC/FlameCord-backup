package io.netty.handler.codec.http;

import io.netty.util.AsciiString;

public final class HttpHeaderValidationUtil {
  private static final long TOKEN_CHARS_HIGH;
  
  private static final long TOKEN_CHARS_LOW;
  
  public static boolean isConnectionHeader(CharSequence name, boolean ignoreTeHeader) {
    int len = name.length();
    switch (len) {
      case 2:
        return ignoreTeHeader ? false : AsciiString.contentEqualsIgnoreCase(name, (CharSequence)HttpHeaderNames.TE);
      case 7:
        return AsciiString.contentEqualsIgnoreCase(name, (CharSequence)HttpHeaderNames.UPGRADE);
      case 10:
        return (AsciiString.contentEqualsIgnoreCase(name, (CharSequence)HttpHeaderNames.CONNECTION) || 
          AsciiString.contentEqualsIgnoreCase(name, (CharSequence)HttpHeaderNames.KEEP_ALIVE));
      case 16:
        return AsciiString.contentEqualsIgnoreCase(name, (CharSequence)HttpHeaderNames.PROXY_CONNECTION);
      case 17:
        return AsciiString.contentEqualsIgnoreCase(name, (CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
    } 
    return false;
  }
  
  public static boolean isTeNotTrailers(CharSequence name, CharSequence value) {
    if (name.length() == 2)
      return (AsciiString.contentEqualsIgnoreCase(name, (CharSequence)HttpHeaderNames.TE) && 
        !AsciiString.contentEqualsIgnoreCase(value, (CharSequence)HttpHeaderValues.TRAILERS)); 
    return false;
  }
  
  public static int validateValidHeaderValue(CharSequence value) {
    int length = value.length();
    if (length == 0)
      return -1; 
    if (value instanceof AsciiString)
      return verifyValidHeaderValueAsciiString((AsciiString)value); 
    return verifyValidHeaderValueCharSequence(value);
  }
  
  private static int verifyValidHeaderValueAsciiString(AsciiString value) {
    byte[] array = value.array();
    int start = value.arrayOffset();
    int b = array[start] & 0xFF;
    if (b < 33 || b == 127)
      return 0; 
    int length = value.length();
    for (int i = start + 1; i < length; i++) {
      b = array[i] & 0xFF;
      if ((b < 32 && b != 9) || b == 127)
        return i - start; 
    } 
    return -1;
  }
  
  private static int verifyValidHeaderValueCharSequence(CharSequence value) {
    int b = value.charAt(0);
    if (b < 33 || b == 127)
      return 0; 
    int length = value.length();
    for (int i = 1; i < length; i++) {
      b = value.charAt(i);
      if ((b < 32 && b != 9) || b == 127)
        return i; 
    } 
    return -1;
  }
  
  public static int validateToken(CharSequence token) {
    if (token instanceof AsciiString)
      return validateAsciiStringToken((AsciiString)token); 
    return validateCharSequenceToken(token);
  }
  
  private static int validateAsciiStringToken(AsciiString token) {
    byte[] array = token.array();
    for (int i = token.arrayOffset(), len = token.arrayOffset() + token.length(); i < len; i++) {
      if (!BitSet128.contains(array[i], TOKEN_CHARS_HIGH, TOKEN_CHARS_LOW))
        return i - token.arrayOffset(); 
    } 
    return -1;
  }
  
  private static int validateCharSequenceToken(CharSequence token) {
    for (int i = 0, len = token.length(); i < len; i++) {
      byte value = (byte)token.charAt(i);
      if (!BitSet128.contains(value, TOKEN_CHARS_HIGH, TOKEN_CHARS_LOW))
        return i; 
    } 
    return -1;
  }
  
  static {
    BitSet128 tokenChars = (new BitSet128()).range('0', '9').range('a', 'z').range('A', 'Z').bits(new char[] { '-', '.', '_', '~' }).bits(new char[] { 
          '!', '#', '$', '%', '&', '\'', '*', '+', '^', '`', 
          '|' });
    TOKEN_CHARS_HIGH = tokenChars.high();
    TOKEN_CHARS_LOW = tokenChars.low();
  }
  
  private static final class BitSet128 {
    private long high;
    
    private long low;
    
    private BitSet128() {}
    
    BitSet128 range(char fromInc, char toInc) {
      for (int bit = fromInc; bit <= toInc; bit++) {
        if (bit < 64) {
          this.low |= 1L << bit;
        } else {
          this.high |= 1L << bit - 64;
        } 
      } 
      return this;
    }
    
    BitSet128 bits(char... bits) {
      for (char bit : bits) {
        if (bit < '@') {
          this.low |= 1L << bit;
        } else {
          this.high |= 1L << bit - 64;
        } 
      } 
      return this;
    }
    
    long high() {
      return this.high;
    }
    
    long low() {
      return this.low;
    }
    
    static boolean contains(byte bit, long high, long low) {
      if (bit < 0)
        return false; 
      if (bit < 64)
        return (0L != (low & 1L << bit)); 
      return (0L != (high & 1L << bit - 64));
    }
  }
}
