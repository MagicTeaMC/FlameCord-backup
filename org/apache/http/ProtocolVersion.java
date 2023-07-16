package org.apache.http;

import java.io.Serializable;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class ProtocolVersion implements Serializable, Cloneable {
  private static final long serialVersionUID = 8950662842175091068L;
  
  protected final String protocol;
  
  protected final int major;
  
  protected final int minor;
  
  public ProtocolVersion(String protocol, int major, int minor) {
    this.protocol = (String)Args.notNull(protocol, "Protocol name");
    this.major = Args.notNegative(major, "Protocol major version");
    this.minor = Args.notNegative(minor, "Protocol minor version");
  }
  
  public final String getProtocol() {
    return this.protocol;
  }
  
  public final int getMajor() {
    return this.major;
  }
  
  public final int getMinor() {
    return this.minor;
  }
  
  public ProtocolVersion forVersion(int major, int minor) {
    if (major == this.major && minor == this.minor)
      return this; 
    return new ProtocolVersion(this.protocol, major, minor);
  }
  
  public final int hashCode() {
    return this.protocol.hashCode() ^ this.major * 100000 ^ this.minor;
  }
  
  public final boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (!(obj instanceof ProtocolVersion))
      return false; 
    ProtocolVersion that = (ProtocolVersion)obj;
    return (this.protocol.equals(that.protocol) && this.major == that.major && this.minor == that.minor);
  }
  
  public boolean isComparable(ProtocolVersion that) {
    return (that != null && this.protocol.equals(that.protocol));
  }
  
  public int compareToVersion(ProtocolVersion that) {
    Args.notNull(that, "Protocol version");
    Args.check(this.protocol.equals(that.protocol), "Versions for different protocols cannot be compared: %s %s", new Object[] { this, that });
    int delta = getMajor() - that.getMajor();
    if (delta == 0)
      delta = getMinor() - that.getMinor(); 
    return delta;
  }
  
  public final boolean greaterEquals(ProtocolVersion version) {
    return (isComparable(version) && compareToVersion(version) >= 0);
  }
  
  public final boolean lessEquals(ProtocolVersion version) {
    return (isComparable(version) && compareToVersion(version) <= 0);
  }
  
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    buffer.append(this.protocol);
    buffer.append('/');
    buffer.append(Integer.toString(this.major));
    buffer.append('.');
    buffer.append(Integer.toString(this.minor));
    return buffer.toString();
  }
  
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
