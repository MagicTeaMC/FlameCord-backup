package com.mysql.cj.protocol;

import com.mysql.cj.util.TimeUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.util.Calendar;

public class InternalTime {
  private boolean negative = false;
  
  private int hours = 0;
  
  private int minutes = 0;
  
  private int seconds = 0;
  
  private int nanos = 0;
  
  private int scale = 0;
  
  public static InternalTime from(LocalTime x) {
    return new InternalTime(x.getHour(), x.getMinute(), x.getSecond(), x.getNano(), -1);
  }
  
  public static InternalTime from(LocalDateTime x) {
    return new InternalTime(x.getHour(), x.getMinute(), x.getSecond(), x.getNano(), -1);
  }
  
  public static InternalTime from(OffsetTime x) {
    return new InternalTime(x.getHour(), x.getMinute(), x.getSecond(), x.getNano(), -1);
  }
  
  public static InternalTime from(Duration x) {
    Duration durationAbs = x.abs();
    long fullSeconds = durationAbs.getSeconds();
    long fullMinutes = fullSeconds / 60L;
    long fullHours = fullMinutes / 60L;
    InternalTime internalTime = new InternalTime((int)fullHours, (int)(fullMinutes % 60L), (int)(fullSeconds % 60L), durationAbs.getNano(), -1);
    internalTime.setNegative(x.isNegative());
    return internalTime;
  }
  
  public static InternalTime from(Calendar x, int nanos) {
    return new InternalTime(x.get(11), x.get(12), x.get(13), nanos, -1);
  }
  
  public InternalTime() {}
  
  public InternalTime(int hours, int minutes, int seconds, int nanos, int scale) {
    this.hours = hours;
    this.minutes = minutes;
    this.seconds = seconds;
    this.nanos = nanos;
    this.scale = scale;
  }
  
  public boolean isNegative() {
    return this.negative;
  }
  
  public void setNegative(boolean negative) {
    this.negative = negative;
  }
  
  public int getHours() {
    return this.hours;
  }
  
  public void setHours(int hours) {
    this.hours = hours;
  }
  
  public int getMinutes() {
    return this.minutes;
  }
  
  public void setMinutes(int minutes) {
    this.minutes = minutes;
  }
  
  public int getSeconds() {
    return this.seconds;
  }
  
  public void setSeconds(int seconds) {
    this.seconds = seconds;
  }
  
  public int getNanos() {
    return this.nanos;
  }
  
  public void setNanos(int nanos) {
    this.nanos = nanos;
  }
  
  public boolean isZero() {
    return (this.hours == 0 && this.minutes == 0 && this.seconds == 0 && this.nanos == 0);
  }
  
  public int getScale() {
    return this.scale;
  }
  
  public void setScale(int scale) {
    this.scale = scale;
  }
  
  public String toString() {
    if (this.nanos > 0)
      return String.format("%02d:%02d:%02d.%s", new Object[] { Integer.valueOf(this.hours), Integer.valueOf(this.minutes), Integer.valueOf(this.seconds), TimeUtil.formatNanos(this.nanos, this.scale, false) }); 
    return String.format("%02d:%02d:%02d", new Object[] { Integer.valueOf(this.hours), Integer.valueOf(this.minutes), Integer.valueOf(this.seconds) });
  }
}
