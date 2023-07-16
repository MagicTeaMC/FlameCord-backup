package org.apache.maven.artifact.versioning;

import java.util.StringTokenizer;
import org.apache.commons.lang3.math.NumberUtils;

public class DefaultArtifactVersion implements ArtifactVersion {
  private Integer majorVersion;
  
  private Integer minorVersion;
  
  private Integer incrementalVersion;
  
  private Integer buildNumber;
  
  private String qualifier;
  
  private ComparableVersion comparable;
  
  public DefaultArtifactVersion(String version) {
    parseVersion(version);
  }
  
  public int hashCode() {
    return 11 + this.comparable.hashCode();
  }
  
  public boolean equals(Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof ArtifactVersion))
      return false; 
    return (compareTo((ArtifactVersion)other) == 0);
  }
  
  public int compareTo(ArtifactVersion otherVersion) {
    if (otherVersion instanceof DefaultArtifactVersion)
      return this.comparable.compareTo(((DefaultArtifactVersion)otherVersion).comparable); 
    return compareTo(new DefaultArtifactVersion(otherVersion.toString()));
  }
  
  public int getMajorVersion() {
    return (this.majorVersion != null) ? this.majorVersion.intValue() : 0;
  }
  
  public int getMinorVersion() {
    return (this.minorVersion != null) ? this.minorVersion.intValue() : 0;
  }
  
  public int getIncrementalVersion() {
    return (this.incrementalVersion != null) ? this.incrementalVersion.intValue() : 0;
  }
  
  public int getBuildNumber() {
    return (this.buildNumber != null) ? this.buildNumber.intValue() : 0;
  }
  
  public String getQualifier() {
    return this.qualifier;
  }
  
  public final void parseVersion(String version) {
    String part1;
    this.comparable = new ComparableVersion(version);
    int index = version.indexOf('-');
    String part2 = null;
    if (index < 0) {
      part1 = version;
    } else {
      part1 = version.substring(0, index);
      part2 = version.substring(index + 1);
    } 
    if (part2 != null)
      if (part2.length() == 1 || !part2.startsWith("0")) {
        this.buildNumber = tryParseInt(part2);
        if (this.buildNumber == null)
          this.qualifier = part2; 
      } else {
        this.qualifier = part2;
      }  
    if (!part1.contains(".") && !part1.startsWith("0")) {
      this.majorVersion = tryParseInt(part1);
      if (this.majorVersion == null) {
        this.qualifier = version;
        this.buildNumber = null;
      } 
    } else {
      boolean fallback = false;
      StringTokenizer tok = new StringTokenizer(part1, ".");
      if (tok.hasMoreTokens()) {
        this.majorVersion = getNextIntegerToken(tok);
        if (this.majorVersion == null)
          fallback = true; 
      } else {
        fallback = true;
      } 
      if (tok.hasMoreTokens()) {
        this.minorVersion = getNextIntegerToken(tok);
        if (this.minorVersion == null)
          fallback = true; 
      } 
      if (tok.hasMoreTokens()) {
        this.incrementalVersion = getNextIntegerToken(tok);
        if (this.incrementalVersion == null)
          fallback = true; 
      } 
      if (tok.hasMoreTokens()) {
        this.qualifier = tok.nextToken();
        fallback = NumberUtils.isDigits(this.qualifier);
      } 
      if (part1.contains("..") || part1.startsWith(".") || part1.endsWith("."))
        fallback = true; 
      if (fallback) {
        this.qualifier = version;
        this.majorVersion = null;
        this.minorVersion = null;
        this.incrementalVersion = null;
        this.buildNumber = null;
      } 
    } 
  }
  
  private static Integer getNextIntegerToken(StringTokenizer tok) {
    String s = tok.nextToken();
    if (s.length() > 1 && s.startsWith("0"))
      return null; 
    return tryParseInt(s);
  }
  
  private static Integer tryParseInt(String s) {
    if (!NumberUtils.isDigits(s))
      return null; 
    try {
      long longValue = Long.parseLong(s);
      if (longValue > 2147483647L)
        return null; 
      return Integer.valueOf((int)longValue);
    } catch (NumberFormatException e) {
      return null;
    } 
  }
  
  public String toString() {
    return this.comparable.toString();
  }
}
