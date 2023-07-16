package com.mysql.cj.protocol.a;

import com.mysql.cj.BindValue;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.ServerSession;
import com.mysql.cj.util.StringUtils;
import java.nio.charset.StandardCharsets;

public class ByteArrayValueEncoder extends AbstractValueEncoder {
  protected RuntimeProperty<Integer> maxByteArrayAsHex;
  
  public void init(PropertySet pset, ServerSession serverSess, ExceptionInterceptor excInterceptor) {
    super.init(pset, serverSess, excInterceptor);
    this.maxByteArrayAsHex = pset.getIntegerProperty(PropertyKey.maxByteArrayAsHex);
  }
  
  public byte[] getBytes(BindValue binding) {
    if (binding.escapeBytesIfNeeded())
      return escapeBytesIfNeeded((byte[])binding.getValue()); 
    return (byte[])binding.getValue();
  }
  
  public String getString(BindValue binding) {
    if (binding.escapeBytesIfNeeded() && binding.getBinaryLength() <= ((Integer)this.maxByteArrayAsHex.getValue()).intValue())
      return StringUtils.toString(escapeBytesIfNeeded((byte[])binding.getValue()), StandardCharsets.US_ASCII); 
    return "** BYTE ARRAY DATA **";
  }
  
  public void encodeAsBinary(Message msg, BindValue binding) {
    ((NativePacketPayload)msg).writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, (byte[])binding.getValue());
  }
}
