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
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SqlTimestampValueEncoder extends AbstractValueEncoder {
  private SimpleDateFormat tsdf = null;
  
  public String getString(BindValue binding) {
    StringBuffer buf;
    Calendar cal;
    StringBuilder sb;
    Timestamp x = adjustTimestamp((Timestamp)((Timestamp)binding.getValue()).clone(), binding.getField(), binding.keepOrigNanos());
    switch (binding.getMysqlType()) {
      case NULL:
        return "null";
      case DATE:
        return (binding.getCalendar() != null) ? 
          TimeUtil.getSimpleDateFormat("''yyyy-MM-dd''", binding.getCalendar())
          .format(new Date(((Date)binding.getValue()).getTime())) : 
          TimeUtil.getSimpleDateFormat(null, "''yyyy-MM-dd''", this.serverSession.getDefaultTimeZone())
          .format(new Date(((Date)binding.getValue()).getTime()));
      case DATETIME:
      case TIMESTAMP:
      case CHAR:
      case VARCHAR:
      case TINYTEXT:
      case TEXT:
      case MEDIUMTEXT:
      case LONGTEXT:
        buf = new StringBuffer();
        if (binding.getCalendar() != null) {
          buf.append(TimeUtil.getSimpleDateFormat("''yyyy-MM-dd HH:mm:ss", binding.getCalendar()).format(x));
        } else {
          this.tsdf = TimeUtil.getSimpleDateFormat(this.tsdf, "''yyyy-MM-dd HH:mm:ss", (binding
              .getMysqlType() == MysqlType.TIMESTAMP && ((Boolean)this.preserveInstants.getValue()).booleanValue()) ? this.serverSession.getSessionTimeZone() : this.serverSession
              .getDefaultTimeZone());
          buf.append(this.tsdf.format(x));
        } 
        if (this.serverSession.getCapabilities().serverSupportsFracSecs() && x.getNanos() > 0) {
          buf.append('.');
          buf.append(TimeUtil.formatNanos(x.getNanos(), 6));
        } 
        buf.append('\'');
        return buf.toString();
      case YEAR:
        cal = Calendar.getInstance();
        cal.setTime((Date)binding.getValue());
        return String.valueOf(cal.get(1));
      case TIME:
        sb = new StringBuilder("'");
        sb.append(adjustLocalTime(((Timestamp)binding.getValue()).toLocalDateTime().toLocalTime(), binding.getField())
            .format(TimeUtil.TIME_FORMATTER_WITH_OPTIONAL_MICROS));
        sb.append("'");
        return sb.toString();
    } 
    throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
        Messages.getString("PreparedStatement.67", new Object[] { binding.getValue().getClass().getName(), binding.getMysqlType().toString() }), this.exceptionInterceptor);
  }
  
  public void encodeAsBinary(Message msg, BindValue binding) {
    Calendar cal;
    Time t;
    StringBuffer buf;
    NativePacketPayload intoPacket = (NativePacketPayload)msg;
    Timestamp x = adjustTimestamp((Timestamp)((Timestamp)binding.getValue()).clone(), binding.getField(), binding.keepOrigNanos());
    Calendar calendar = binding.getCalendar();
    switch (binding.getMysqlType()) {
      case DATE:
        if (calendar == null)
          calendar = Calendar.getInstance(this.serverSession.getDefaultTimeZone(), Locale.US); 
        calendar.setTime((Date)binding.getValue());
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        writeDate(msg, InternalDate.from(calendar));
        return;
      case DATETIME:
      case TIMESTAMP:
        if (calendar == null)
          calendar = Calendar.getInstance((binding
              .getMysqlType() == MysqlType.TIMESTAMP && ((Boolean)this.preserveInstants.getValue()).booleanValue()) ? this.serverSession.getSessionTimeZone() : this.serverSession
              .getDefaultTimeZone(), Locale.US); 
        calendar.setTime(x);
        writeDateTime(msg, InternalTimestamp.from(calendar, x.getNanos()));
        return;
      case YEAR:
        cal = Calendar.getInstance();
        cal.setTime((Date)binding.getValue());
        intoPacket.writeInteger(NativeConstants.IntegerDataType.INT4, cal.get(1));
        return;
      case TIME:
        t = adjustTime(new Time(x.getTime()));
        if (calendar == null)
          calendar = Calendar.getInstance(this.serverSession.getDefaultTimeZone(), Locale.US); 
        calendar.setTime(t);
        writeTime(msg, InternalTime.from(calendar, 
              adjustTimestamp((Timestamp)((Timestamp)binding.getValue()).clone(), binding.getField(), binding.keepOrigNanos()).getNanos()));
        return;
      case CHAR:
      case VARCHAR:
      case TINYTEXT:
      case TEXT:
      case MEDIUMTEXT:
      case LONGTEXT:
        buf = new StringBuffer();
        if (binding.getCalendar() != null) {
          buf.append(TimeUtil.getSimpleDateFormat("yyyy-MM-dd HH:mm:ss", binding.getCalendar()).format(x));
        } else {
          this.tsdf = TimeUtil.getSimpleDateFormat(this.tsdf, "yyyy-MM-dd HH:mm:ss", (binding
              .getMysqlType() == MysqlType.TIMESTAMP && ((Boolean)this.preserveInstants.getValue()).booleanValue()) ? this.serverSession.getSessionTimeZone() : this.serverSession
              .getDefaultTimeZone());
          buf.append(this.tsdf.format(x));
        } 
        if (this.serverSession.getCapabilities().serverSupportsFracSecs() && x.getNanos() > 0) {
          buf.append('.');
          buf.append(TimeUtil.formatNanos(x.getNanos(), 6));
        } 
        intoPacket.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, StringUtils.getBytes(buf.toString(), (String)this.charEncoding.getValue()));
        return;
    } 
    throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
        Messages.getString("PreparedStatement.67", new Object[] { binding.getValue().getClass().getName(), binding.getMysqlType().toString() }), this.exceptionInterceptor);
  }
  
  public void encodeAsQueryAttribute(Message msg, BindValue binding) {
    Timestamp x = (Timestamp)binding.getValue();
    Calendar cal = Calendar.getInstance(this.serverSession.getDefaultTimeZone(), Locale.US);
    cal.setTime(x);
    InternalTimestamp internalTimestamp = InternalTimestamp.from(cal, x.getNanos());
    internalTimestamp.setOffset((int)TimeUnit.MILLISECONDS.toMinutes(cal.getTimeZone().getOffset(cal.getTimeInMillis())));
    writeDateTimeWithOffset(msg, internalTimestamp);
  }
}
