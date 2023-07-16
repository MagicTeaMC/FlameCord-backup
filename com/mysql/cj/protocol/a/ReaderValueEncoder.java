package com.mysql.cj.protocol.a;

import com.mysql.cj.BindValue;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.CJOperationNotSupportedException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.util.Util;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class ReaderValueEncoder extends AbstractValueEncoder {
  public byte[] getBytes(BindValue binding) {
    return readBytes((Reader)binding.getValue(), binding);
  }
  
  public String getString(BindValue binding) {
    return "'** STREAM DATA **'";
  }
  
  public void encodeAsBinary(Message msg, BindValue binding) {
    throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
  }
  
  protected byte[] readBytes(Reader reader, BindValue binding) {
    try {
      byte[] bytes;
      char[] c = null;
      int len = 0;
      boolean useLength = ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.useStreamLengthsInPrepStmts).getValue()).booleanValue();
      String clobEncoding = binding.isNational() ? null : this.propertySet.getStringProperty(PropertyKey.clobCharacterEncoding).getStringValue();
      if (clobEncoding == null)
        clobEncoding = this.charEncoding.getStringValue(); 
      long scaleOrLength = binding.getScaleOrLength();
      if (useLength && scaleOrLength != -1L) {
        c = new char[(int)scaleOrLength];
        int numCharsRead = Util.readFully(reader, c, (int)scaleOrLength);
        bytes = StringUtils.getBytes(new String(c, 0, numCharsRead), clobEncoding);
      } else {
        c = new char[4096];
        StringBuilder buf = new StringBuilder();
        while ((len = reader.read(c)) != -1)
          buf.append(c, 0, len); 
        bytes = StringUtils.getBytes(buf.toString(), clobEncoding);
      } 
      return escapeBytesIfNeeded(bytes);
    } catch (UnsupportedEncodingException uec) {
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, uec.toString(), uec, this.exceptionInterceptor);
    } catch (IOException ioEx) {
      throw ExceptionFactory.createException(ioEx.toString(), ioEx, this.exceptionInterceptor);
    } 
  }
}
