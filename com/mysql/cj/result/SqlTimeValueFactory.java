package com.mysql.cj.result;

import com.mysql.cj.Messages;
import com.mysql.cj.WarningListener;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.DataReadException;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTime;
import com.mysql.cj.protocol.InternalTimestamp;
import java.sql.Time;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class SqlTimeValueFactory extends AbstractDateTimeValueFactory<Time> {
  private WarningListener warningListener;
  
  private Calendar cal;
  
  public SqlTimeValueFactory(PropertySet pset, Calendar calendar, TimeZone tz) {
    super(pset);
    if (calendar != null) {
      this.cal = (Calendar)calendar.clone();
    } else {
      this.cal = Calendar.getInstance(tz, Locale.US);
      this.cal.setLenient(false);
    } 
  }
  
  public SqlTimeValueFactory(PropertySet pset, Calendar calendar, TimeZone tz, WarningListener warningListener) {
    this(pset, calendar, tz);
    this.warningListener = warningListener;
  }
  
  Time localCreateFromDate(InternalDate idate) {
    synchronized (this.cal) {
      this.cal.clear();
      return new Time(this.cal.getTimeInMillis());
    } 
  }
  
  public Time localCreateFromTime(InternalTime it) {
    if (it.getHours() < 0 || it.getHours() >= 24)
      throw new DataReadException(Messages.getString("ResultSet.InvalidTimeValue", new Object[] { it.toString() })); 
    synchronized (this.cal) {
      this.cal.set(1970, 0, 1, it.getHours(), it.getMinutes(), it.getSeconds());
      this.cal.set(14, 0);
      long ms = (it.getNanos() / 1000000) + this.cal.getTimeInMillis();
      return new Time(ms);
    } 
  }
  
  public Time localCreateFromDatetime(InternalTimestamp its) {
    if (this.warningListener != null)
      this.warningListener.warningEncountered(Messages.getString("ResultSet.PrecisionLostWarning", new Object[] { "java.sql.Time" })); 
    return createFromTime(new InternalTime(its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos(), its.getScale()));
  }
  
  public Time localCreateFromTimestamp(InternalTimestamp its) {
    if (this.warningListener != null)
      this.warningListener.warningEncountered(Messages.getString("ResultSet.PrecisionLostWarning", new Object[] { "java.sql.Time" })); 
    return createFromTime(new InternalTime(its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos(), its.getScale()));
  }
  
  public String getTargetTypeName() {
    return Time.class.getName();
  }
}
