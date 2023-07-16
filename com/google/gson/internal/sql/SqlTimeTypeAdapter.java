package com.google.gson.internal.sql;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

final class SqlTimeTypeAdapter extends TypeAdapter<Time> {
  static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        return (typeToken.getRawType() == Time.class) ? new SqlTimeTypeAdapter() : null;
      }
    };
  
  private final DateFormat format = new SimpleDateFormat("hh:mm:ss a");
  
  public Time read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    } 
    String s = in.nextString();
    try {
      synchronized (this) {
        Date date = this.format.parse(s);
        return new Time(date.getTime());
      } 
    } catch (ParseException e) {
      throw new JsonSyntaxException("Failed parsing '" + s + "' as SQL Time; at path " + in.getPreviousPath(), e);
    } 
  }
  
  public void write(JsonWriter out, Time value) throws IOException {
    String timeString;
    if (value == null) {
      out.nullValue();
      return;
    } 
    synchronized (this) {
      timeString = this.format.format(value);
    } 
    out.value(timeString);
  }
  
  private SqlTimeTypeAdapter() {}
}
