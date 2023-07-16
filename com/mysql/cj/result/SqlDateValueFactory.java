package com.mysql.cj.result;

import com.mysql.cj.Messages;
import com.mysql.cj.WarningListener;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.DataReadException;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTime;
import com.mysql.cj.protocol.InternalTimestamp;
import com.mysql.cj.util.TimeUtil;
import java.sql.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class SqlDateValueFactory extends AbstractDateTimeValueFactory<Date> {
  private WarningListener warningListener;
  
  private Calendar cal;
  
  public SqlDateValueFactory(PropertySet pset, Calendar calendar, TimeZone tz) {
    super(pset);
    if (calendar != null) {
      this.cal = (Calendar)calendar.clone();
    } else {
      this.cal = Calendar.getInstance(tz, Locale.US);
      this.cal.set(14, 0);
      this.cal.setLenient(false);
    } 
  }
  
  public SqlDateValueFactory(PropertySet pset, Calendar calendar, TimeZone tz, WarningListener warningListener) {
    this(pset, calendar, tz);
    this.warningListener = warningListener;
  }
  
  public Date localCreateFromDate(InternalDate idate) {
    synchronized (this.cal) {
      if (idate.isZero())
        throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate")); 
      this.cal.clear();
      this.cal.set(idate.getYear(), idate.getMonth() - 1, idate.getDay());
      long ms = this.cal.getTimeInMillis();
      return new Date(ms);
    } 
  }
  
  public Date localCreateFromTime(InternalTime it) {
    if (this.warningListener != null)
      this.warningListener.warningEncountered(Messages.getString("ResultSet.ImplicitDatePartWarning", new Object[] { "java.sql.Date" })); 
    return Date.valueOf(TimeUtil.DEFAULT_DATE);
  }
  
  public Date localCreateFromTimestamp(InternalTimestamp its) {
    if (this.warningListener != null)
      this.warningListener.warningEncountered(Messages.getString("ResultSet.PrecisionLostWarning", new Object[] { "java.sql.Date" })); 
    return createFromDate((InternalDate)its);
  }
  
  public Date localCreateFromDatetime(InternalTimestamp its) {
    if (this.warningListener != null)
      this.warningListener.warningEncountered(Messages.getString("ResultSet.PrecisionLostWarning", new Object[] { "java.sql.Date" })); 
    return createFromDate((InternalDate)its);
  }
  
  public String getTargetTypeName() {
    return Date.class.getName();
  }
}
