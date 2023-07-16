package org.apache.maven.model.interpolation;

import java.util.Date;
import java.util.Properties;
import org.codehaus.plexus.interpolation.AbstractValueSource;

class BuildTimestampValueSource extends AbstractValueSource {
  private final MavenBuildTimestamp mavenBuildTimestamp;
  
  BuildTimestampValueSource(Date startTime, Properties properties) {
    super(false);
    this.mavenBuildTimestamp = new MavenBuildTimestamp(startTime, properties);
  }
  
  public Object getValue(String expression) {
    if ("build.timestamp".equals(expression) || "maven.build.timestamp".equals(expression))
      return this.mavenBuildTimestamp.formattedTimestamp(); 
    return null;
  }
}
