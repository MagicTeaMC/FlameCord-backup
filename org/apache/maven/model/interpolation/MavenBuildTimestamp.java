package org.apache.maven.model.interpolation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.TimeZone;

public class MavenBuildTimestamp {
  public static final String DEFAULT_BUILD_TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
  
  public static final String BUILD_TIMESTAMP_FORMAT_PROPERTY = "maven.build.timestamp.format";
  
  public static final TimeZone DEFAULT_BUILD_TIME_ZONE = TimeZone.getTimeZone("Etc/UTC");
  
  private String formattedTimestamp;
  
  public MavenBuildTimestamp() {
    this(new Date());
  }
  
  public MavenBuildTimestamp(Date time) {
    this(time, "yyyy-MM-dd'T'HH:mm:ss'Z'");
  }
  
  public MavenBuildTimestamp(Date time, Properties properties) {
    this(time, (properties != null) ? properties.getProperty("maven.build.timestamp.format") : null);
  }
  
  public MavenBuildTimestamp(Date time, String timestampFormat) {
    if (timestampFormat == null)
      timestampFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'"; 
    if (time == null)
      time = new Date(); 
    SimpleDateFormat dateFormat = new SimpleDateFormat(timestampFormat);
    dateFormat.setCalendar(new GregorianCalendar());
    dateFormat.setTimeZone(DEFAULT_BUILD_TIME_ZONE);
    this.formattedTimestamp = dateFormat.format(time);
  }
  
  public String formattedTimestamp() {
    return this.formattedTimestamp;
  }
}
