package com.mysql.cj.protocol.a;

import com.mysql.cj.BindValue;
import com.mysql.cj.Messages;
import com.mysql.cj.MysqlType;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTimestamp;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.util.TimeUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LocalDateValueEncoder extends AbstractValueEncoder {
  public String getString(BindValue binding) {
    StringBuilder sb;
    switch (binding.getMysqlType()) {
      case NULL:
        return "null";
      case DATE:
        sb = new StringBuilder("'");
        sb.append(((LocalDate)binding.getValue()).format(TimeUtil.DATE_FORMATTER));
        sb.append("'");
        return sb.toString();
      case DATETIME:
      case TIMESTAMP:
        sb = new StringBuilder("'");
        sb.append(LocalDateTime.of((LocalDate)binding.getValue(), TimeUtil.DEFAULT_TIME).format(TimeUtil.DATETIME_FORMATTER_WITH_OPTIONAL_MICROS));
        sb.append("'");
        return sb.toString();
      case YEAR:
        return String.valueOf(((LocalDate)binding.getValue()).getYear());
      case CHAR:
      case VARCHAR:
      case TINYTEXT:
      case TEXT:
      case MEDIUMTEXT:
      case LONGTEXT:
        sb = new StringBuilder("'");
        sb.append(binding.getValue().toString());
        sb.append("'");
        return sb.toString();
    } 
    throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
        Messages.getString("PreparedStatement.67", new Object[] { binding.getValue().getClass().getName(), binding.getMysqlType().toString() }), this.exceptionInterceptor);
  }
  
  public void encodeAsBinary(Message msg, BindValue binding) {
    NativePacketPayload intoPacket = (NativePacketPayload)msg;
    switch (binding.getMysqlType()) {
      case DATE:
        writeDate(msg, InternalDate.from((LocalDate)binding.getValue()));
        return;
      case DATETIME:
      case TIMESTAMP:
        writeDateTime(msg, InternalTimestamp.from(LocalDateTime.of((LocalDate)binding.getValue(), TimeUtil.DEFAULT_TIME)));
        return;
      case YEAR:
        intoPacket.writeInteger(NativeConstants.IntegerDataType.INT4, ((LocalDate)binding.getValue()).getYear());
        return;
      case CHAR:
      case VARCHAR:
      case TINYTEXT:
      case TEXT:
      case MEDIUMTEXT:
      case LONGTEXT:
        intoPacket.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, StringUtils.getBytes(binding.getValue().toString(), (String)this.charEncoding.getValue()));
        return;
    } 
    throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
        Messages.getString("PreparedStatement.67", new Object[] { binding.getValue().getClass().getName(), binding.getMysqlType().toString() }), this.exceptionInterceptor);
  }
  
  public void encodeAsQueryAttribute(Message msg, BindValue binding) {
    writeDate(msg, InternalDate.from((LocalDate)binding.getValue()));
  }
}
