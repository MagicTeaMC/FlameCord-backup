package com.mysql.cj.protocol.a;

import com.mysql.cj.Constants;
import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.util.StringUtils;
import java.util.HashMap;
import java.util.Map;

public class NativePacketPayload implements Message {
  static final int NO_LENGTH_LIMIT = -1;
  
  public static final long NULL_LENGTH = -1L;
  
  public static final short TYPE_ID_ERROR = 255;
  
  public static final short TYPE_ID_EOF = 254;
  
  public static final short TYPE_ID_AUTH_SWITCH = 254;
  
  public static final short TYPE_ID_LOCAL_INFILE = 251;
  
  public static final short TYPE_ID_OK = 0;
  
  public static final short TYPE_ID_AUTH_MORE_DATA = 1;
  
  public static final short TYPE_ID_AUTH_NEXT_FACTOR = 2;
  
  private int payloadLength = 0;
  
  private byte[] byteBuffer;
  
  private int position = 0;
  
  static final int MAX_BYTES_TO_DUMP = 1024;
  
  private Map<String, Integer> tags = new HashMap<>();
  
  public String toString() {
    int numBytes = (this.position <= this.payloadLength) ? this.position : this.payloadLength;
    int numBytesToDump = (numBytes < 1024) ? numBytes : 1024;
    this.position = 0;
    String dumped = StringUtils.dumpAsHex(readBytes(NativeConstants.StringLengthDataType.STRING_FIXED, numBytesToDump), numBytesToDump);
    if (numBytesToDump < numBytes)
      return dumped + " ....(packet exceeds max. dump length)"; 
    return dumped;
  }
  
  public String toSuperString() {
    return super.toString();
  }
  
  public NativePacketPayload(byte[] buf) {
    this.byteBuffer = buf;
    this.payloadLength = buf.length;
  }
  
  public NativePacketPayload(int size) {
    this.byteBuffer = new byte[size];
    this.payloadLength = size;
  }
  
  public int getCapacity() {
    return this.byteBuffer.length;
  }
  
  public final void ensureCapacity(int additionalData) {
    if (this.position + additionalData > this.byteBuffer.length) {
      int newLength = (int)(this.byteBuffer.length * 1.25D);
      if (newLength < this.byteBuffer.length + additionalData)
        newLength = this.byteBuffer.length + (int)(additionalData * 1.25D); 
      if (newLength < this.byteBuffer.length)
        newLength = this.byteBuffer.length + additionalData; 
      byte[] newBytes = new byte[newLength];
      System.arraycopy(this.byteBuffer, 0, newBytes, 0, this.byteBuffer.length);
      this.byteBuffer = newBytes;
    } 
  }
  
  public byte[] getByteBuffer() {
    return this.byteBuffer;
  }
  
  public void setByteBuffer(byte[] byteBufferToSet) {
    this.byteBuffer = byteBufferToSet;
  }
  
  public int getPayloadLength() {
    return this.payloadLength;
  }
  
  public void setPayloadLength(int bufLengthToSet) {
    if (bufLengthToSet > this.byteBuffer.length)
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Buffer.0")); 
    this.payloadLength = bufLengthToSet;
  }
  
  private void adjustPayloadLength() {
    if (this.position > this.payloadLength)
      this.payloadLength = this.position; 
  }
  
  public int getPosition() {
    return this.position;
  }
  
  public void setPosition(int positionToSet) {
    this.position = positionToSet;
  }
  
  public boolean isErrorPacket() {
    return ((this.byteBuffer[0] & 0xFF) == 255);
  }
  
  public final boolean isEOFPacket() {
    return ((this.byteBuffer[0] & 0xFF) == 254 && this.payloadLength <= 5);
  }
  
  public final boolean isAuthMethodSwitchRequestPacket() {
    return ((this.byteBuffer[0] & 0xFF) == 254);
  }
  
  public final boolean isOKPacket() {
    return ((this.byteBuffer[0] & 0xFF) == 0);
  }
  
  public final boolean isResultSetOKPacket() {
    return ((this.byteBuffer[0] & 0xFF) == 254 && this.payloadLength > 5 && this.payloadLength < 16777215);
  }
  
  public final boolean isAuthMoreDataPacket() {
    return ((this.byteBuffer[0] & 0xFF) == 1);
  }
  
  public final boolean isAuthNextFactorPacket() {
    return ((this.byteBuffer[0] & 0xFF) == 2);
  }
  
  public void writeInteger(NativeConstants.IntegerDataType type, long l) {
    byte[] b;
    switch (type) {
      case STRING_FIXED:
        ensureCapacity(1);
        b = this.byteBuffer;
        b[this.position++] = (byte)(int)(l & 0xFFL);
        break;
      case STRING_VAR:
        ensureCapacity(2);
        b = this.byteBuffer;
        b[this.position++] = (byte)(int)(l & 0xFFL);
        b[this.position++] = (byte)(int)(l >>> 8L);
        break;
      case null:
        ensureCapacity(3);
        b = this.byteBuffer;
        b[this.position++] = (byte)(int)(l & 0xFFL);
        b[this.position++] = (byte)(int)(l >>> 8L);
        b[this.position++] = (byte)(int)(l >>> 16L);
        break;
      case null:
        ensureCapacity(4);
        b = this.byteBuffer;
        b[this.position++] = (byte)(int)(l & 0xFFL);
        b[this.position++] = (byte)(int)(l >>> 8L);
        b[this.position++] = (byte)(int)(l >>> 16L);
        b[this.position++] = (byte)(int)(l >>> 24L);
        break;
      case null:
        ensureCapacity(6);
        b = this.byteBuffer;
        b[this.position++] = (byte)(int)(l & 0xFFL);
        b[this.position++] = (byte)(int)(l >>> 8L);
        b[this.position++] = (byte)(int)(l >>> 16L);
        b[this.position++] = (byte)(int)(l >>> 24L);
        b[this.position++] = (byte)(int)(l >>> 32L);
        b[this.position++] = (byte)(int)(l >>> 40L);
        break;
      case null:
        ensureCapacity(8);
        b = this.byteBuffer;
        b[this.position++] = (byte)(int)(l & 0xFFL);
        b[this.position++] = (byte)(int)(l >>> 8L);
        b[this.position++] = (byte)(int)(l >>> 16L);
        b[this.position++] = (byte)(int)(l >>> 24L);
        b[this.position++] = (byte)(int)(l >>> 32L);
        b[this.position++] = (byte)(int)(l >>> 40L);
        b[this.position++] = (byte)(int)(l >>> 48L);
        b[this.position++] = (byte)(int)(l >>> 56L);
        break;
      case null:
        if (l < 251L) {
          ensureCapacity(1);
          writeInteger(NativeConstants.IntegerDataType.INT1, l);
          break;
        } 
        if (l < 65536L) {
          ensureCapacity(3);
          writeInteger(NativeConstants.IntegerDataType.INT1, 252L);
          writeInteger(NativeConstants.IntegerDataType.INT2, l);
          break;
        } 
        if (l < 16777216L) {
          ensureCapacity(4);
          writeInteger(NativeConstants.IntegerDataType.INT1, 253L);
          writeInteger(NativeConstants.IntegerDataType.INT3, l);
          break;
        } 
        ensureCapacity(9);
        writeInteger(NativeConstants.IntegerDataType.INT1, 254L);
        writeInteger(NativeConstants.IntegerDataType.INT8, l);
        break;
    } 
    adjustPayloadLength();
  }
  
  public final long readInteger(NativeConstants.IntegerDataType type) {
    int sw;
    byte[] b = this.byteBuffer;
    switch (type) {
      case STRING_FIXED:
        return (b[this.position++] & 0xFF);
      case STRING_VAR:
        return (b[this.position++] & 0xFF | (b[this.position++] & 0xFF) << 8);
      case null:
        return (b[this.position++] & 0xFF | (b[this.position++] & 0xFF) << 8 | (b[this.position++] & 0xFF) << 16);
      case null:
        return b[this.position++] & 0xFFL | (b[this.position++] & 0xFFL) << 8L | (b[this.position++] & 0xFF) << 16L | (b[this.position++] & 0xFF) << 24L;
      case null:
        return (b[this.position++] & 0xFF) | (b[this.position++] & 0xFF) << 8L | (b[this.position++] & 0xFF) << 16L | (b[this.position++] & 0xFF) << 24L | (b[this.position++] & 0xFF) << 32L | (b[this.position++] & 0xFF) << 40L;
      case null:
        return (b[this.position++] & 0xFF) | (b[this.position++] & 0xFF) << 8L | (b[this.position++] & 0xFF) << 16L | (b[this.position++] & 0xFF) << 24L | (b[this.position++] & 0xFF) << 32L | (b[this.position++] & 0xFF) << 40L | (b[this.position++] & 0xFF) << 48L | (b[this.position++] & 0xFF) << 56L;
      case null:
        sw = b[this.position++] & 0xFF;
        switch (sw) {
          case 251:
            return -1L;
          case 252:
            return readInteger(NativeConstants.IntegerDataType.INT2);
          case 253:
            return readInteger(NativeConstants.IntegerDataType.INT3);
          case 254:
            return readInteger(NativeConstants.IntegerDataType.INT8);
        } 
        return sw;
    } 
    return (b[this.position++] & 0xFF);
  }
  
  public final void writeBytes(NativeConstants.StringSelfDataType type, byte[] b) {
    writeBytes(type, b, 0, b.length);
  }
  
  public final void writeBytes(NativeConstants.StringLengthDataType type, byte[] b) {
    writeBytes(type, b, 0, b.length);
  }
  
  public void writeBytes(NativeConstants.StringSelfDataType type, byte[] b, int offset, int len) {
    switch (type) {
      case STRING_FIXED:
        writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, b, offset, len);
        break;
      case STRING_VAR:
        ensureCapacity(len + 1);
        writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, b, offset, len);
        this.byteBuffer[this.position++] = 0;
        break;
      case null:
        ensureCapacity(len + 9);
        writeInteger(NativeConstants.IntegerDataType.INT_LENENC, len);
        writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, b, offset, len);
        break;
    } 
    adjustPayloadLength();
  }
  
  public void writeBytes(NativeConstants.StringLengthDataType type, byte[] b, int offset, int len) {
    switch (type) {
      case STRING_FIXED:
      case STRING_VAR:
        ensureCapacity(len);
        System.arraycopy(b, offset, this.byteBuffer, this.position, len);
        this.position += len;
        break;
    } 
    adjustPayloadLength();
  }
  
  public byte[] readBytes(NativeConstants.StringSelfDataType type) {
    byte[] b;
    int i;
    long l;
    switch (type) {
      case STRING_VAR:
        i = this.position;
        while (i < this.payloadLength && this.byteBuffer[i] != 0)
          i++; 
        b = readBytes(NativeConstants.StringLengthDataType.STRING_FIXED, i - this.position);
        this.position++;
        return b;
      case null:
        l = readInteger(NativeConstants.IntegerDataType.INT_LENENC);
        return (l == -1L) ? null : ((l == 0L) ? Constants.EMPTY_BYTE_ARRAY : readBytes(NativeConstants.StringLengthDataType.STRING_FIXED, (int)l));
      case STRING_FIXED:
        return readBytes(NativeConstants.StringLengthDataType.STRING_FIXED, this.payloadLength - this.position);
    } 
    return null;
  }
  
  public void skipBytes(NativeConstants.StringSelfDataType type) {
    long len;
    switch (type) {
      case STRING_VAR:
        while (this.position < this.payloadLength && this.byteBuffer[this.position] != 0)
          this.position++; 
        this.position++;
        break;
      case null:
        len = readInteger(NativeConstants.IntegerDataType.INT_LENENC);
        if (len != -1L && len != 0L)
          this.position += (int)len; 
        break;
      case STRING_FIXED:
        this.position = this.payloadLength;
        break;
    } 
  }
  
  public byte[] readBytes(NativeConstants.StringLengthDataType type, int len) {
    byte[] b;
    switch (type) {
      case STRING_FIXED:
      case STRING_VAR:
        b = new byte[len];
        System.arraycopy(this.byteBuffer, this.position, b, 0, len);
        this.position += len;
        return b;
    } 
    return null;
  }
  
  public String readString(NativeConstants.StringSelfDataType type, String encoding) {
    int i;
    long l;
    String res = null;
    switch (type) {
      case STRING_VAR:
        i = this.position;
        while (i < this.payloadLength && this.byteBuffer[i] != 0)
          i++; 
        res = readString(NativeConstants.StringLengthDataType.STRING_FIXED, encoding, i - this.position);
        this.position++;
        break;
      case null:
        l = readInteger(NativeConstants.IntegerDataType.INT_LENENC);
        return (l == -1L) ? null : ((l == 0L) ? "" : readString(NativeConstants.StringLengthDataType.STRING_FIXED, encoding, (int)l));
      case STRING_FIXED:
        return readString(NativeConstants.StringLengthDataType.STRING_FIXED, encoding, this.payloadLength - this.position);
    } 
    return res;
  }
  
  public String readString(NativeConstants.StringLengthDataType type, String encoding, int len) {
    String res = null;
    switch (type) {
      case STRING_FIXED:
      case STRING_VAR:
        if (this.position + len > this.payloadLength)
          throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Buffer.1")); 
        res = StringUtils.toString(this.byteBuffer, this.position, len, encoding);
        this.position += len;
        break;
    } 
    return res;
  }
  
  public static String extractSqlFromPacket(String possibleSqlQuery, NativePacketPayload packet, int endOfQueryPacketPosition, int maxQuerySizeToLog) {
    String extractedSql = null;
    if (possibleSqlQuery != null)
      if (possibleSqlQuery.length() > maxQuerySizeToLog) {
        StringBuilder truncatedQueryBuf = new StringBuilder(possibleSqlQuery.substring(0, maxQuerySizeToLog));
        truncatedQueryBuf.append(Messages.getString("MysqlIO.25"));
        extractedSql = truncatedQueryBuf.toString();
      } else {
        extractedSql = possibleSqlQuery;
      }  
    if (extractedSql == null) {
      int extractPosition = endOfQueryPacketPosition;
      boolean truncated = false;
      if (endOfQueryPacketPosition > maxQuerySizeToLog) {
        extractPosition = maxQuerySizeToLog;
        truncated = true;
      } 
      extractedSql = StringUtils.toString(packet.getByteBuffer(), 1, extractPosition - 1);
      if (truncated)
        extractedSql = extractedSql + Messages.getString("MysqlIO.25"); 
    } 
    return extractedSql;
  }
  
  public int setTag(String key) {
    Integer pos = this.tags.put(key, Integer.valueOf(getPosition()));
    return (pos == null) ? -1 : pos.intValue();
  }
  
  public int getTag(String key) {
    Integer pos = this.tags.get(key);
    return (pos == null) ? -1 : pos.intValue();
  }
}
