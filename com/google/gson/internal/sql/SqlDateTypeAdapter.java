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
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

final class SqlDateTypeAdapter extends TypeAdapter<Date> {
  static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        return (typeToken.getRawType() == Date.class) ? 
          new SqlDateTypeAdapter() : null;
      }
    };
  
  private final DateFormat format = new SimpleDateFormat("MMM d, yyyy");
  
  public Date read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    } 
    String s = in.nextString();
    try {
      Date utilDate;
      synchronized (this) {
        utilDate = this.format.parse(s);
      } 
      return new Date(utilDate.getTime());
    } catch (ParseException e) {
      Date utilDate;
      throw new JsonSyntaxException("Failed parsing '" + s + "' as SQL Date; at path " + in.getPreviousPath(), utilDate);
    } 
  }
  
  public void write(JsonWriter out, Date value) throws IOException {
    String dateString;
    if (value == null) {
      out.nullValue();
      return;
    } 
    synchronized (this) {
      dateString = this.format.format(value);
    } 
    out.value(dateString);
  }
  
  private SqlDateTypeAdapter() {}
}
