package com.mysql.cj;

public class ServerVersion implements Comparable<ServerVersion> {
  private String completeVersion;
  
  private Integer major;
  
  private Integer minor;
  
  private Integer subminor;
  
  public ServerVersion(String completeVersion, int major, int minor, int subminor) {
    this.completeVersion = completeVersion;
    this.major = Integer.valueOf(major);
    this.minor = Integer.valueOf(minor);
    this.subminor = Integer.valueOf(subminor);
  }
  
  public ServerVersion(int major, int minor, int subminor) {
    this(null, major, minor, subminor);
  }
  
  public int getMajor() {
    return this.major.intValue();
  }
  
  public int getMinor() {
    return this.minor.intValue();
  }
  
  public int getSubminor() {
    return this.subminor.intValue();
  }
  
  public String toString() {
    if (this.completeVersion != null)
      return this.completeVersion; 
    return String.format("%d.%d.%d", new Object[] { this.major, this.minor, this.subminor });
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null || !ServerVersion.class.isAssignableFrom(obj.getClass()))
      return false; 
    ServerVersion another = (ServerVersion)obj;
    if (getMajor() != another.getMajor() || getMinor() != another.getMinor() || getSubminor() != another.getSubminor())
      return false; 
    return true;
  }
  
  public int hashCode() {
    int hash = 23;
    hash += 19 * hash + this.major.intValue();
    hash += 19 * hash + this.minor.intValue();
    hash += 19 * hash + this.subminor.intValue();
    return hash;
  }
  
  public int compareTo(ServerVersion other) {
    int c;
    if ((c = this.major.compareTo(Integer.valueOf(other.getMajor()))) != 0)
      return c; 
    if ((c = this.minor.compareTo(Integer.valueOf(other.getMinor()))) != 0)
      return c; 
    return this.subminor.compareTo(Integer.valueOf(other.getSubminor()));
  }
  
  public boolean meetsMinimum(ServerVersion min) {
    return (compareTo(min) >= 0);
  }
  
  public static ServerVersion parseVersion(String versionString) {
    int point = versionString.indexOf('.');
    if (point != -1)
      try {
        int serverMajorVersion = Integer.parseInt(versionString.substring(0, point));
        String remaining = versionString.substring(point + 1, versionString.length());
        point = remaining.indexOf('.');
        if (point != -1) {
          int serverMinorVersion = Integer.parseInt(remaining.substring(0, point));
          remaining = remaining.substring(point + 1, remaining.length());
          int pos = 0;
          while (pos < remaining.length() && 
            remaining.charAt(pos) >= '0' && remaining.charAt(pos) <= '9')
            pos++; 
          int serverSubminorVersion = Integer.parseInt(remaining.substring(0, pos));
          return new ServerVersion(versionString, serverMajorVersion, serverMinorVersion, serverSubminorVersion);
        } 
      } catch (NumberFormatException numberFormatException) {} 
    return new ServerVersion(0, 0, 0);
  }
}
