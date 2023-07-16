package com.mysql.cj.protocol;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class InternalTimestamp extends InternalDate {
  private int hours = 0;
  
  private int minutes = 0;
  
  private int seconds = 0;
  
  private int nanos = 0;
  
  private int scale = 0;
  
  private int offset = 0;
  
  public static InternalTimestamp from(LocalDate x) {
    return new InternalTimestamp(x.getYear(), x.getMonthValue(), x.getDayOfMonth(), 0, 0, 0, 0, -1);
  }
  
  public static InternalTimestamp from(LocalDateTime x) {
    return new InternalTimestamp(x.getYear(), x.getMonthValue(), x.getDayOfMonth(), x.getHour(), x.getMinute(), x.getSecond(), x.getNano(), -1);
  }
  
  public static InternalTimestamp from(OffsetDateTime x) {
    InternalTimestamp internalTimestamp = new InternalTimestamp(x.getYear(), x.getMonthValue(), x.getDayOfMonth(), x.getHour(), x.getMinute(), x.getSecond(), x.getNano(), -1);
    internalTimestamp.setOffset((int)TimeUnit.SECONDS.toMinutes(x.getOffset().getTotalSeconds()));
    return internalTimestamp;
  }
  
  public static InternalTimestamp from(ZonedDateTime x) {
    InternalTimestamp internalTimestamp = new InternalTimestamp(x.getYear(), x.getMonthValue(), x.getDayOfMonth(), x.getHour(), x.getMinute(), x.getSecond(), x.getNano(), -1);
    internalTimestamp.setOffset((int)TimeUnit.SECONDS.toMinutes(x.getOffset().getTotalSeconds()));
    return internalTimestamp;
  }
  
  public static InternalTimestamp from(Calendar x, int nanos) {
    return new InternalTimestamp(x.get(1), x.get(2) + 1, x.get(5), x.get(11), x
        .get(12), x.get(13), nanos, -1);
  }
  
  public InternalTimestamp() {}
  
  public InternalTimestamp(int year, int month, int day, int hours, int minutes, int seconds, int nanos, int scale) {
    this.year = year;
    this.month = month;
    this.day = day;
    this.hours = hours;
    this.minutes = minutes;
    this.seconds = seconds;
    this.nanos = nanos;
    this.scale = scale;
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
  
  public int getScale() {
    return this.scale;
  }
  
  public void setScale(int scale) {
    this.scale = scale;
  }
  
  public int getOffset() {
    return this.offset;
  }
  
  public void setOffset(int offset) {
    this.offset = offset;
  }
  
  public boolean isZero() {
    return (super.isZero() && this.hours == 0 && this.minutes == 0 && this.seconds == 0 && this.nanos == 0);
  }
}
