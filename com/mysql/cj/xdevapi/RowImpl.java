package com.mysql.cj.xdevapi;

import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.DataReadException;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.result.BigDecimalValueFactory;
import com.mysql.cj.result.BooleanValueFactory;
import com.mysql.cj.result.ByteValueFactory;
import com.mysql.cj.result.DoubleValueFactory;
import com.mysql.cj.result.IntegerValueFactory;
import com.mysql.cj.result.LongValueFactory;
import com.mysql.cj.result.Row;
import com.mysql.cj.result.SqlDateValueFactory;
import com.mysql.cj.result.SqlTimeValueFactory;
import com.mysql.cj.result.SqlTimestampValueFactory;
import com.mysql.cj.result.StringValueFactory;
import com.mysql.cj.result.ValueFactory;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.TimeZone;

public class RowImpl implements Row {
  private Row row;
  
  private ColumnDefinition metadata;
  
  private TimeZone defaultTimeZone;
  
  private PropertySet pset;
  
  public RowImpl(Row row, ColumnDefinition metadata, TimeZone defaultTimeZone, PropertySet pset) {
    this.row = row;
    this.metadata = metadata;
    this.defaultTimeZone = defaultTimeZone;
    this.pset = pset;
  }
  
  private int fieldNameToIndex(String fieldName) {
    int idx = this.metadata.findColumn(fieldName, true, 0);
    if (idx == -1)
      throw new DataReadException("Invalid column"); 
    return idx;
  }
  
  public BigDecimal getBigDecimal(String fieldName) {
    return getBigDecimal(fieldNameToIndex(fieldName));
  }
  
  public BigDecimal getBigDecimal(int pos) {
    return (BigDecimal)this.row.getValue(pos, (ValueFactory)new BigDecimalValueFactory(this.pset));
  }
  
  public boolean getBoolean(String fieldName) {
    return getBoolean(fieldNameToIndex(fieldName));
  }
  
  public boolean getBoolean(int pos) {
    Boolean res = (Boolean)this.row.getValue(pos, (ValueFactory)new BooleanValueFactory(this.pset));
    return (res == null) ? false : res.booleanValue();
  }
  
  public byte getByte(String fieldName) {
    return getByte(fieldNameToIndex(fieldName));
  }
  
  public byte getByte(int pos) {
    Byte res = (Byte)this.row.getValue(pos, (ValueFactory)new ByteValueFactory(this.pset));
    return (res == null) ? 0 : res.byteValue();
  }
  
  public Date getDate(String fieldName) {
    return getDate(fieldNameToIndex(fieldName));
  }
  
  public Date getDate(int pos) {
    return (Date)this.row.getValue(pos, (ValueFactory)new SqlDateValueFactory(this.pset, null, this.defaultTimeZone));
  }
  
  public DbDoc getDbDoc(String fieldName) {
    return getDbDoc(fieldNameToIndex(fieldName));
  }
  
  public DbDoc getDbDoc(int pos) {
    return (DbDoc)this.row.getValue(pos, (ValueFactory)new DbDocValueFactory(this.pset));
  }
  
  public double getDouble(String fieldName) {
    return getDouble(fieldNameToIndex(fieldName));
  }
  
  public double getDouble(int pos) {
    Double res = (Double)this.row.getValue(pos, (ValueFactory)new DoubleValueFactory(this.pset));
    return (res == null) ? 0.0D : res.doubleValue();
  }
  
  public int getInt(String fieldName) {
    return getInt(fieldNameToIndex(fieldName));
  }
  
  public int getInt(int pos) {
    Integer res = (Integer)this.row.getValue(pos, (ValueFactory)new IntegerValueFactory(this.pset));
    return (res == null) ? 0 : res.intValue();
  }
  
  public long getLong(String fieldName) {
    return getLong(fieldNameToIndex(fieldName));
  }
  
  public long getLong(int pos) {
    Long res = (Long)this.row.getValue(pos, (ValueFactory)new LongValueFactory(this.pset));
    return (res == null) ? 0L : res.longValue();
  }
  
  public String getString(String fieldName) {
    return getString(fieldNameToIndex(fieldName));
  }
  
  public String getString(int pos) {
    return (String)this.row.getValue(pos, (ValueFactory)new StringValueFactory(this.pset));
  }
  
  public Time getTime(String fieldName) {
    return getTime(fieldNameToIndex(fieldName));
  }
  
  public Time getTime(int pos) {
    return (Time)this.row.getValue(pos, (ValueFactory)new SqlTimeValueFactory(this.pset, null, this.defaultTimeZone));
  }
  
  public Timestamp getTimestamp(String fieldName) {
    return getTimestamp(fieldNameToIndex(fieldName));
  }
  
  public Timestamp getTimestamp(int pos) {
    return (Timestamp)this.row.getValue(pos, (ValueFactory)new SqlTimestampValueFactory(this.pset, null, this.defaultTimeZone, this.defaultTimeZone));
  }
}
