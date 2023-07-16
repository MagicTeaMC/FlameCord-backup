package com.mysql.cj.protocol.a;

import com.mysql.cj.BindValue;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.CJOperationNotSupportedException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.util.Util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamValueEncoder extends AbstractValueEncoder {
  private byte[] streamConvertBuf = null;
  
  public byte[] getBytes(BindValue binding) {
    return streamToBytes((InputStream)binding.getValue(), binding.getScaleOrLength(), (NativePacketPayload)null);
  }
  
  public String getString(BindValue binding) {
    return "'** STREAM DATA **'";
  }
  
  public void encodeAsText(Message msg, BindValue binding) {
    NativePacketPayload intoPacket = (NativePacketPayload)msg;
    streamToBytes((InputStream)binding.getValue(), binding.getScaleOrLength(), intoPacket);
  }
  
  public void encodeAsBinary(Message msg, BindValue binding) {
    throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
  }
  
  protected byte[] streamToBytes(InputStream in, long length, NativePacketPayload packet) {
    boolean useLength = (length == -1L) ? false : ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.useStreamLengthsInPrepStmts).getValue()).booleanValue();
    in.mark(2147483647);
    try {
      if (this.streamConvertBuf == null)
        this.streamConvertBuf = new byte[4096]; 
      int bcnt = useLength ? Util.readBlock(in, this.streamConvertBuf, (int)length, this.exceptionInterceptor) : Util.readBlock(in, this.streamConvertBuf, this.exceptionInterceptor);
      int lengthLeftToRead = (int)(length - bcnt);
      ByteArrayOutputStream bytesOut = null;
      boolean hexEscape = false;
      if (packet == null) {
        bytesOut = new ByteArrayOutputStream();
      } else {
        hexEscape = this.serverSession.isNoBackslashEscapesSet();
        packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, StringUtils.getBytes(hexEscape ? "x" : "_binary"));
        packet.writeInteger(NativeConstants.IntegerDataType.INT1, 39L);
      } 
      while (bcnt > 0) {
        if (packet == null) {
          bytesOut.write(this.streamConvertBuf, 0, bcnt);
        } else if (hexEscape) {
          StringUtils.hexEscapeBlock(this.streamConvertBuf, bcnt, (lowBits, highBits) -> {
                packet.writeInteger(NativeConstants.IntegerDataType.INT1, lowBits.byteValue());
                packet.writeInteger(NativeConstants.IntegerDataType.INT1, highBits.byteValue());
              });
        } else {
          escapeblockFast(this.streamConvertBuf, packet, bcnt);
        } 
        if (useLength) {
          bcnt = Util.readBlock(in, this.streamConvertBuf, lengthLeftToRead, this.exceptionInterceptor);
          if (bcnt > 0)
            lengthLeftToRead -= bcnt; 
          continue;
        } 
        bcnt = Util.readBlock(in, this.streamConvertBuf, this.exceptionInterceptor);
      } 
      if (packet == null)
        return bytesOut.toByteArray(); 
      packet.writeInteger(NativeConstants.IntegerDataType.INT1, 39L);
      return null;
    } finally {
      try {
        in.reset();
      } catch (IOException iOException) {}
      if (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.autoClosePStmtStreams).getValue()).booleanValue()) {
        try {
          in.close();
        } catch (IOException iOException) {}
        in = null;
      } 
    } 
  }
  
  private final void escapeblockFast(byte[] buf, NativePacketPayload packet, int size) {
    int lastwritten = 0;
    for (int i = 0; i < size; i++) {
      byte b = buf[i];
      if (b == 0) {
        if (i > lastwritten)
          packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, buf, lastwritten, i - lastwritten); 
        packet.writeInteger(NativeConstants.IntegerDataType.INT1, 92L);
        packet.writeInteger(NativeConstants.IntegerDataType.INT1, 48L);
        lastwritten = i + 1;
      } else if (b == 92 || b == 39) {
        if (i > lastwritten)
          packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, buf, lastwritten, i - lastwritten); 
        packet.writeInteger(NativeConstants.IntegerDataType.INT1, b);
        lastwritten = i;
      } 
    } 
    if (lastwritten < size)
      packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, buf, lastwritten, size - lastwritten); 
  }
}
