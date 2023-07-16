package com.mysql.cj.protocol.a;

import com.mysql.cj.BindValue;
import com.mysql.cj.Messages;
import com.mysql.cj.MysqlType;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTime;
import com.mysql.cj.protocol.InternalTimestamp;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.util.TimeUtil;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Locale;

public class OffsetDateTimeValueEncoder extends AbstractValueEncoder {
  public String getString(BindValue binding) {
    StringBuilder sb;
    Timestamp x;
    StringBuffer buf;
    OffsetDateTime odt = (OffsetDateTime)binding.getValue();
    switch (binding.getMysqlType()) {
      case NULL:
        return "null";
      case DATE:
        sb = new StringBuilder("'");
        sb.append(odt.atZoneSameInstant(this.serverSession.getDefaultTimeZone().toZoneId()).toLocalDate().format(TimeUtil.DATE_FORMATTER));
        sb.append("'");
        return sb.toString();
      case TIME:
        sb = new StringBuilder("'");
        sb.append(adjustLocalTime(((OffsetDateTime)binding
              .getValue()).atZoneSameInstant(this.serverSession.getDefaultTimeZone().toZoneId()).toLocalTime(), binding
              .getField()).format(TimeUtil.TIME_FORMATTER_WITH_OPTIONAL_MICROS));
        sb.append("'");
        return sb.toString();
      case DATETIME:
      case TIMESTAMP:
        x = adjustTimestamp(
            Timestamp.valueOf(((OffsetDateTime)binding
              .getValue()).atZoneSameInstant(this.serverSession.getDefaultTimeZone().toZoneId()).toLocalDateTime()), binding
            .getField(), binding.keepOrigNanos());
        buf = new StringBuffer();
        buf.append(TimeUtil.getSimpleDateFormat(null, "''yyyy-MM-dd HH:mm:ss", (binding
              .getMysqlType() == MysqlType.TIMESTAMP && ((Boolean)this.preserveInstants.getValue()).booleanValue()) ? this.serverSession.getSessionTimeZone() : this.serverSession
              .getDefaultTimeZone())
            .format(x));
        if (this.serverSession.getCapabilities().serverSupportsFracSecs() && x.getNanos() > 0) {
          buf.append('.');
          buf.append(TimeUtil.formatNanos(x.getNanos(), 6));
        } 
        buf.append('\'');
        return buf.toString();
      case YEAR:
        return String.valueOf(odt.atZoneSameInstant(this.serverSession.getDefaultTimeZone().toZoneId()).getYear());
      case CHAR:
      case VARCHAR:
      case TINYTEXT:
      case TEXT:
      case MEDIUMTEXT:
      case LONGTEXT:
        sb = new StringBuilder("'");
        sb.append(odt.format((((Boolean)this.sendFractionalSeconds.getValue()).booleanValue() && odt.getNano() > 0) ? TimeUtil.DATETIME_FORMATTER_WITH_NANOS_WITH_OFFSET : TimeUtil.DATETIME_FORMATTER_NO_FRACT_WITH_OFFSET));
        sb.append("'");
        return sb.toString();
    } 
    throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
        Messages.getString("PreparedStatement.67", new Object[] { binding.getValue().getClass().getName(), binding.getMysqlType().toString() }), this.exceptionInterceptor);
  }
  
  public void encodeAsBinary(Message msg, BindValue binding) {
    Timestamp ts;
    Calendar calendar;
    OffsetDateTime odt = (OffsetDateTime)binding.getValue();
    NativePacketPayload intoPacket = (NativePacketPayload)msg;
    switch (binding.getMysqlType()) {
      case DATE:
        writeDate(msg, InternalDate.from(odt.atZoneSameInstant(this.serverSession.getDefaultTimeZone().toZoneId()).toLocalDate()));
        return;
      case TIME:
        writeTime(msg, 
            InternalTime.from(adjustLocalTime(((OffsetDateTime)binding
                .getValue()).atZoneSameInstant(this.serverSession.getDefaultTimeZone().toZoneId()).toLocalTime(), binding
                .getField())));
        return;
      case DATETIME:
      case TIMESTAMP:
        ts = adjustTimestamp(
            Timestamp.valueOf(((OffsetDateTime)binding
              .getValue()).atZoneSameInstant(this.serverSession.getDefaultTimeZone().toZoneId()).toLocalDateTime()), binding
            .getField(), binding.keepOrigNanos());
        calendar = Calendar.getInstance((binding.getMysqlType() == MysqlType.TIMESTAMP && ((Boolean)this.preserveInstants.getValue()).booleanValue()) ? this.serverSession.getSessionTimeZone() : this.serverSession
            .getDefaultTimeZone(), Locale.US);
        calendar.setTime(ts);
        writeDateTime(msg, InternalTimestamp.from(calendar, ts.getNanos()));
        return;
      case YEAR:
        intoPacket.writeInteger(NativeConstants.IntegerDataType.INT4, odt.atZoneSameInstant(this.serverSession.getDefaultTimeZone().toZoneId()).getYear());
        return;
      case CHAR:
      case VARCHAR:
      case TINYTEXT:
      case TEXT:
      case MEDIUMTEXT:
      case LONGTEXT:
        intoPacket.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, 
            StringUtils.getBytes(odt
              .format((((Boolean)this.sendFractionalSeconds.getValue()).booleanValue() && odt.getNano() > 0) ? TimeUtil.DATETIME_FORMATTER_WITH_NANOS_WITH_OFFSET : TimeUtil.DATETIME_FORMATTER_NO_FRACT_WITH_OFFSET), (String)this.charEncoding
              
              .getValue()));
        return;
    } 
    throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
        Messages.getString("PreparedStatement.67", new Object[] { binding.getValue().getClass().getName(), binding.getMysqlType().toString() }), this.exceptionInterceptor);
  }
  
  public void encodeAsQueryAttribute(Message msg, BindValue binding) {
    writeDateTimeWithOffset(msg, InternalTimestamp.from((OffsetDateTime)binding.getValue()));
  }
}
