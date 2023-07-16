package com.mysql.cj.protocol.a;

import com.mysql.cj.BindValue;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.protocol.Message;
import java.sql.Blob;

public class BlobValueEncoder extends InputStreamValueEncoder {
  public byte[] getBytes(BindValue binding) {
    try {
      return streamToBytes(((Blob)binding.getValue()).getBinaryStream(), binding.getScaleOrLength(), (NativePacketPayload)null);
    } catch (Throwable t) {
      throw ExceptionFactory.createException(t.getMessage(), t, this.exceptionInterceptor);
    } 
  }
  
  public void encodeAsText(Message msg, BindValue binding) {
    try {
      streamToBytes(((Blob)binding.getValue()).getBinaryStream(), binding.getScaleOrLength(), (NativePacketPayload)msg);
    } catch (Throwable t) {
      throw ExceptionFactory.createException(t.getMessage(), t, this.exceptionInterceptor);
    } 
  }
}
