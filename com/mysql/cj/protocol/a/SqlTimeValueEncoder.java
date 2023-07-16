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

public class SqlTimeValueEncoder extends AbstractValueEncoder {
  private SimpleDateFormat tdf;
  
  public String getString(BindValue binding) {
    Date d;
    String formatStr;
    Timestamp ts;
    StringBuffer buf;
    Calendar cal;
    if (binding.isNull())
      return "null"; 
    Time x = adjustTime((Time)binding.getValue());
    switch (binding.getMysqlType()) {
      case DATE:
        d = new Date(x.getTime());
        return (binding.getCalendar() != null) ? TimeUtil.getSimpleDateFormat("''yyyy-MM-dd''", binding.getCalendar()).format(d) : 
          TimeUtil.getSimpleDateFormat(null, "''yyyy-MM-dd''", this.serverSession.getDefaultTimeZone()).format(d);
      case TIME:
      case CHAR:
      case VARCHAR:
      case TINYTEXT:
      case TEXT:
      case MEDIUMTEXT:
      case LONGTEXT:
        formatStr = (this.serverSession.getCapabilities().serverSupportsFracSecs() && ((Boolean)this.sendFractionalSeconds.getValue()).booleanValue() && ((Boolean)this.sendFractionalSecondsForTime.getValue()).booleanValue() && TimeUtil.hasFractionalSeconds(x).booleanValue()) ? "''HH:mm:ss.SSS''" : "''HH:mm:ss''";
        return (binding.getCalendar() != null) ? TimeUtil.getSimpleDateFormat(formatStr, binding.getCalendar()).format(x) : 
          TimeUtil.getSimpleDateFormat(this.tdf, formatStr, this.serverSession.getDefaultTimeZone()).format(x);
      case DATETIME:
      case TIMESTAMP:
        ts = new Timestamp(x.getTime());
        if (!((Boolean)this.sendFractionalSecondsForTime.getValue()).booleanValue())
          ts = TimeUtil.truncateFractionalSeconds(ts); 
        buf = new StringBuffer();
        buf.append((binding.getCalendar() != null) ? TimeUtil.getSimpleDateFormat("''yyyy-MM-dd HH:mm:ss", binding.getCalendar()).format(x) : 
            TimeUtil.getSimpleDateFormat(null, "''yyyy-MM-dd HH:mm:ss", this.serverSession.getDefaultTimeZone()).format(x));
        if (this.serverSession.getCapabilities().serverSupportsFracSecs() && ts.getNanos() > 0) {
          buf.append('.');
          buf.append(TimeUtil.formatNanos(ts.getNanos(), 6));
        } 
        buf.append('\'');
        return buf.toString();
      case YEAR:
        cal = Calendar.getInstance();
        cal.setTime((Date)binding.getValue());
        return String.valueOf(cal.get(1));
    } 
    throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
        Messages.getString("PreparedStatement.67", new Object[] { binding.getValue().getClass().getName(), binding.getMysqlType().toString() }), this.exceptionInterceptor);
  }
  
  public void encodeAsBinary(Message msg, BindValue binding) {
    Timestamp ts;
    Calendar cal;
    Time x;
    String formatStr;
    NativePacketPayload intoPacket = (NativePacketPayload)msg;
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
        ts = new Timestamp(((Time)binding.getValue()).getTime());
        if (!this.serverSession.getCapabilities().serverSupportsFracSecs() || !((Boolean)this.sendFractionalSeconds.getValue()).booleanValue() || 
          !((Boolean)this.sendFractionalSecondsForTime.getValue()).booleanValue())
          ts = TimeUtil.truncateFractionalSeconds(ts); 
        if (calendar == null)
          calendar = Calendar.getInstance(this.serverSession.getDefaultTimeZone(), Locale.US); 
        calendar.setTime(ts);
        writeDateTime(msg, InternalTimestamp.from(calendar, ts.getNanos()));
        return;
      case YEAR:
        cal = Calendar.getInstance();
        cal.setTime((Date)binding.getValue());
        intoPacket.writeInteger(NativeConstants.IntegerDataType.INT4, cal.get(1));
        return;
      case TIME:
        x = adjustTime((Time)binding.getValue());
        if (calendar == null)
          calendar = Calendar.getInstance(this.serverSession.getDefaultTimeZone(), Locale.US); 
        calendar.setTime(x);
        writeTime(msg, InternalTime.from(calendar, (int)TimeUnit.MILLISECONDS.toNanos(calendar.get(14))));
        return;
      case CHAR:
      case VARCHAR:
      case TINYTEXT:
      case TEXT:
      case MEDIUMTEXT:
      case LONGTEXT:
        x = adjustTime((Time)binding.getValue());
        formatStr = (this.serverSession.getCapabilities().serverSupportsFracSecs() && ((Boolean)this.sendFractionalSeconds.getValue()).booleanValue() && ((Boolean)this.sendFractionalSecondsForTime.getValue()).booleanValue() && TimeUtil.hasFractionalSeconds(x).booleanValue()) ? "HH:mm:ss.SSS" : "HH:mm:ss";
        intoPacket.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, 
            StringUtils.getBytes(
              (binding.getCalendar() != null) ? TimeUtil.getSimpleDateFormat(formatStr, binding.getCalendar()).format(x) : 
              TimeUtil.getSimpleDateFormat(this.tdf, formatStr, this.serverSession.getDefaultTimeZone()).format(x), (String)this.charEncoding
              .getValue()));
        return;
    } 
    throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
        Messages.getString("PreparedStatement.67", new Object[] { binding.getValue().getClass().getName(), binding.getMysqlType().toString() }), this.exceptionInterceptor);
  }
  
  public void encodeAsQueryAttribute(Message msg, BindValue binding) {
    Time x = (Time)binding.getValue();
    Calendar calendar = Calendar.getInstance(this.serverSession.getDefaultTimeZone(), Locale.US);
    calendar.setTime(x);
    writeTime(msg, InternalTime.from(calendar, (int)TimeUnit.MILLISECONDS.toNanos(calendar.get(14))));
  }
}
