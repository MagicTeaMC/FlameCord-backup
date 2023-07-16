package com.mysql.cj.protocol.a;

import com.mysql.cj.BindValue;
import com.mysql.cj.Messages;
import com.mysql.cj.MysqlType;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.util.StringUtils;
import java.math.BigDecimal;

public class BooleanValueEncoder extends AbstractValueEncoder {
  public String getString(BindValue binding) {
    boolean b = ((Boolean)binding.getValue()).booleanValue();
    switch (binding.getMysqlType()) {
      case NULL:
        return "null";
      case CHAR:
      case VARCHAR:
      case TINYTEXT:
      case TEXT:
      case MEDIUMTEXT:
      case LONGTEXT:
        return String.valueOf(b);
      case BIT:
      case BOOLEAN:
      case TINYINT:
      case TINYINT_UNSIGNED:
      case SMALLINT:
      case SMALLINT_UNSIGNED:
      case MEDIUMINT:
      case MEDIUMINT_UNSIGNED:
      case INT:
      case INT_UNSIGNED:
      case YEAR:
        return String.valueOf(b ? 1 : 0);
      case BIGINT:
      case BIGINT_UNSIGNED:
        return String.valueOf(b ? 1L : 0L);
      case FLOAT:
      case FLOAT_UNSIGNED:
        return StringUtils.fixDecimalExponent(Float.toString(b ? 1.0F : 0.0F));
      case DOUBLE:
      case DOUBLE_UNSIGNED:
        return StringUtils.fixDecimalExponent(Double.toString(b ? 1.0D : 0.0D));
      case DECIMAL:
      case DECIMAL_UNSIGNED:
        return (new BigDecimal(b ? 1.0D : 0.0D)).toPlainString();
    } 
    throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
        Messages.getString("PreparedStatement.67", new Object[] { binding.getValue().getClass().getName(), binding.getMysqlType().toString() }), this.exceptionInterceptor);
  }
  
  public void encodeAsBinary(Message msg, BindValue binding) {
    boolean b = ((Boolean)binding.getValue()).booleanValue();
    NativePacketPayload intoPacket = (NativePacketPayload)msg;
    switch (binding.getMysqlType()) {
      case BIT:
      case BOOLEAN:
      case TINYINT:
      case TINYINT_UNSIGNED:
        intoPacket.writeInteger(NativeConstants.IntegerDataType.INT1, b ? 1L : 0L);
        return;
      case CHAR:
      case VARCHAR:
      case TINYTEXT:
      case TEXT:
      case MEDIUMTEXT:
      case LONGTEXT:
        intoPacket.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, StringUtils.getBytes(String.valueOf(b), (String)this.charEncoding.getValue()));
        return;
      case SMALLINT:
      case SMALLINT_UNSIGNED:
      case MEDIUMINT:
      case MEDIUMINT_UNSIGNED:
        intoPacket.writeInteger(NativeConstants.IntegerDataType.INT2, b ? 1L : 0L);
        return;
      case INT:
      case INT_UNSIGNED:
      case YEAR:
        intoPacket.writeInteger(NativeConstants.IntegerDataType.INT4, ((Long)binding.getValue()).longValue());
        return;
      case BIGINT:
      case BIGINT_UNSIGNED:
        intoPacket.writeInteger(NativeConstants.IntegerDataType.INT8, b ? 1L : 0L);
        return;
      case FLOAT:
      case FLOAT_UNSIGNED:
        intoPacket.writeInteger(NativeConstants.IntegerDataType.INT4, Float.floatToIntBits(b ? 1.0F : 0.0F));
        return;
      case DOUBLE:
      case DOUBLE_UNSIGNED:
        intoPacket.writeInteger(NativeConstants.IntegerDataType.INT8, Double.doubleToLongBits(b ? 1.0D : 0.0D));
        return;
      case DECIMAL:
      case DECIMAL_UNSIGNED:
        intoPacket.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, 
            StringUtils.getBytes((new BigDecimal(b ? 1.0D : 0.0D)).toPlainString(), (String)this.charEncoding.getValue()));
        return;
    } 
    throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
        Messages.getString("PreparedStatement.67", new Object[] { binding.getValue().getClass().getName(), binding.getMysqlType().toString() }), this.exceptionInterceptor);
  }
  
  public void encodeAsQueryAttribute(Message msg, BindValue binding) {
    boolean b = ((Boolean)binding.getValue()).booleanValue();
    NativePacketPayload intoPacket = (NativePacketPayload)msg;
    intoPacket.writeInteger(NativeConstants.IntegerDataType.INT1, b ? 1L : 0L);
  }
}
