package com.mysql.cj.protocol.a;

import com.mysql.cj.BindValue;
import com.mysql.cj.Messages;
import com.mysql.cj.MysqlType;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.util.StringUtils;
import java.math.BigDecimal;

public class NumberValueEncoder extends AbstractValueEncoder {
  public String getString(BindValue binding) {
    Number x = (binding.getValue() instanceof BigDecimal) ? getScaled((BigDecimal)binding.getValue(), binding.getScaleOrLength()) : (Number)binding.getValue();
    switch (binding.getMysqlType()) {
      case NULL:
        return "null";
      case BOOLEAN:
        return String.valueOf((x.longValue() != 0L));
      case BIT:
      case TINYINT:
      case TINYINT_UNSIGNED:
      case SMALLINT:
      case SMALLINT_UNSIGNED:
      case MEDIUMINT:
      case MEDIUMINT_UNSIGNED:
      case INT:
      case YEAR:
        return String.valueOf(x.intValue());
      case INT_UNSIGNED:
      case BIGINT:
      case BIGINT_UNSIGNED:
        return String.valueOf(x.longValue());
      case FLOAT:
      case FLOAT_UNSIGNED:
        return StringUtils.fixDecimalExponent(Float.toString(x.floatValue()));
      case DOUBLE:
      case DOUBLE_UNSIGNED:
        return StringUtils.fixDecimalExponent(Double.toString(x.doubleValue()));
      case DECIMAL:
      case DECIMAL_UNSIGNED:
      case CHAR:
      case VARCHAR:
      case TINYTEXT:
      case TEXT:
      case MEDIUMTEXT:
      case LONGTEXT:
      case BINARY:
      case VARBINARY:
      case TINYBLOB:
      case BLOB:
      case MEDIUMBLOB:
      case LONGBLOB:
        return (x instanceof BigDecimal) ? ((BigDecimal)x).toPlainString() : StringUtils.fixDecimalExponent(x.toString());
    } 
    throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
        Messages.getString("PreparedStatement.67", new Object[] { binding.getValue().getClass().getName(), binding.getMysqlType().toString() }), this.exceptionInterceptor);
  }
  
  public void encodeAsBinary(Message msg, BindValue binding) {
    Number x = (binding.getValue() instanceof BigDecimal) ? getScaled((BigDecimal)binding.getValue(), binding.getScaleOrLength()) : (Number)binding.getValue();
    NativePacketPayload intoPacket = (NativePacketPayload)msg;
    switch (binding.getMysqlType()) {
      case BOOLEAN:
      case BIT:
      case TINYINT:
      case TINYINT_UNSIGNED:
        intoPacket.writeInteger(NativeConstants.IntegerDataType.INT1, x.longValue());
        return;
      case SMALLINT:
      case SMALLINT_UNSIGNED:
      case MEDIUMINT:
      case MEDIUMINT_UNSIGNED:
        intoPacket.writeInteger(NativeConstants.IntegerDataType.INT2, x.longValue());
        return;
      case INT:
      case YEAR:
      case INT_UNSIGNED:
        intoPacket.writeInteger(NativeConstants.IntegerDataType.INT4, x.longValue());
        return;
      case BIGINT:
      case BIGINT_UNSIGNED:
        intoPacket.writeInteger(NativeConstants.IntegerDataType.INT8, x.longValue());
        return;
      case FLOAT:
      case FLOAT_UNSIGNED:
        intoPacket.writeInteger(NativeConstants.IntegerDataType.INT4, Float.floatToIntBits(x.floatValue()));
        return;
      case DOUBLE:
      case DOUBLE_UNSIGNED:
        intoPacket.writeInteger(NativeConstants.IntegerDataType.INT8, Double.doubleToLongBits(x.doubleValue()));
        return;
      case DECIMAL:
      case DECIMAL_UNSIGNED:
      case CHAR:
      case VARCHAR:
      case TINYTEXT:
      case TEXT:
      case MEDIUMTEXT:
      case LONGTEXT:
      case BINARY:
      case VARBINARY:
      case TINYBLOB:
      case BLOB:
      case MEDIUMBLOB:
      case LONGBLOB:
        intoPacket.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, 
            StringUtils.getBytes((x instanceof BigDecimal) ? ((BigDecimal)x).toPlainString() : x.toString(), (String)this.charEncoding.getValue()));
        return;
    } 
    throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
        Messages.getString("PreparedStatement.67", new Object[] { binding.getValue().getClass().getName(), binding.getMysqlType().toString() }), this.exceptionInterceptor);
  }
  
  public void encodeAsQueryAttribute(Message msg, BindValue binding) {
    encodeAsBinary(msg, binding);
  }
}
