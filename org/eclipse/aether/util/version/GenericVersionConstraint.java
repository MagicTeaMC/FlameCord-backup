package org.eclipse.aether.util.version;

import java.util.Objects;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionConstraint;
import org.eclipse.aether.version.VersionRange;

final class GenericVersionConstraint implements VersionConstraint {
  private final VersionRange range;
  
  private final Version version;
  
  GenericVersionConstraint(VersionRange range) {
    this.range = Objects.<VersionRange>requireNonNull(range, "version range cannot be null");
    this.version = null;
  }
  
  GenericVersionConstraint(Version version) {
    this.version = Objects.<Version>requireNonNull(version, "version cannot be null");
    this.range = null;
  }
  
  public VersionRange getRange() {
    return this.range;
  }
  
  public Version getVersion() {
    return this.version;
  }
  
  public boolean containsVersion(Version version) {
    if (this.range == null)
      return version.equals(this.version); 
    return this.range.containsVersion(version);
  }
  
  public String toString() {
    return String.valueOf((this.range == null) ? this.version : this.range);
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null || !getClass().equals(obj.getClass()))
      return false; 
    GenericVersionConstraint that = (GenericVersionConstraint)obj;
    return (Objects.equals(this.range, that.range) && Objects.equals(this.version, that.getVersion()));
  }
  
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + hash(getRange());
    hash = hash * 31 + hash(getVersion());
    return hash;
  }
  
  private static int hash(Object obj) {
    return (obj != null) ? obj.hashCode() : 0;
  }
}
