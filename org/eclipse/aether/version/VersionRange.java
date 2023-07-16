package org.eclipse.aether.version;

import java.util.Objects;

public interface VersionRange {
  boolean containsVersion(Version paramVersion);
  
  Bound getLowerBound();
  
  Bound getUpperBound();
  
  public static final class Bound {
    private final Version version;
    
    private final boolean inclusive;
    
    public Bound(Version version, boolean inclusive) {
      this.version = Objects.<Version>requireNonNull(version, "version cannot be null");
      this.inclusive = inclusive;
    }
    
    public Version getVersion() {
      return this.version;
    }
    
    public boolean isInclusive() {
      return this.inclusive;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (obj == null || !getClass().equals(obj.getClass()))
        return false; 
      Bound that = (Bound)obj;
      return (this.inclusive == that.inclusive && this.version.equals(that.version));
    }
    
    public int hashCode() {
      int hash = 17;
      hash = hash * 31 + this.version.hashCode();
      hash = hash * 31 + (this.inclusive ? 1 : 0);
      return hash;
    }
    
    public String toString() {
      return String.valueOf(this.version);
    }
  }
}
