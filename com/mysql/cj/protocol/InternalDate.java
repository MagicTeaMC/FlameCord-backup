package com.mysql.cj.protocol;

import java.time.LocalDate;
import java.util.Calendar;

public class InternalDate {
  protected int year = 0;
  
  protected int month = 0;
  
  protected int day = 0;
  
  public static InternalDate from(LocalDate x) {
    return new InternalDate(x.getYear(), x.getMonthValue(), x.getDayOfMonth());
  }
  
  public static InternalDate from(Calendar x) {
    return new InternalDate(x.get(1), x.get(2) + 1, x.get(5));
  }
  
  public InternalDate() {}
  
  public InternalDate(int year, int month, int day) {
    this.year = year;
    this.month = month;
    this.day = day;
  }
  
  public int getYear() {
    return this.year;
  }
  
  public void setYear(int year) {
    this.year = year;
  }
  
  public int getMonth() {
    return this.month;
  }
  
  public void setMonth(int month) {
    this.month = month;
  }
  
  public int getDay() {
    return this.day;
  }
  
  public void setDay(int day) {
    this.day = day;
  }
  
  public boolean isZero() {
    return (this.year == 0 && this.month == 0 && this.day == 0);
  }
}
