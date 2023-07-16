package org.eclipse.aether.util.version;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionRange;

final class UnionVersionRange implements VersionRange {
  private final Set<VersionRange> ranges;
  
  private final VersionRange.Bound lowerBound;
  
  private final VersionRange.Bound upperBound;
  
  public static VersionRange from(VersionRange... ranges) {
    if (ranges == null)
      return from(Collections.emptySet()); 
    return from(Arrays.asList(ranges));
  }
  
  public static VersionRange from(Collection<? extends VersionRange> ranges) {
    if (ranges != null && ranges.size() == 1)
      return ranges.iterator().next(); 
    return new UnionVersionRange(ranges);
  }
  
  private UnionVersionRange(Collection<? extends VersionRange> ranges) {
    if (ranges == null || ranges.isEmpty()) {
      this.ranges = Collections.emptySet();
      this.lowerBound = null;
      this.upperBound = null;
    } else {
      this.ranges = new HashSet<>(ranges);
      VersionRange.Bound lowerBound = null, upperBound = null;
      for (VersionRange range : this.ranges) {
        VersionRange.Bound lb = range.getLowerBound();
        if (lb == null) {
          lowerBound = null;
          break;
        } 
        if (lowerBound == null) {
          lowerBound = lb;
          continue;
        } 
        int c = lb.getVersion().compareTo(lowerBound.getVersion());
        if (c < 0 || (c == 0 && !lowerBound.isInclusive()))
          lowerBound = lb; 
      } 
      for (VersionRange range : this.ranges) {
        VersionRange.Bound ub = range.getUpperBound();
        if (ub == null) {
          upperBound = null;
          break;
        } 
        if (upperBound == null) {
          upperBound = ub;
          continue;
        } 
        int c = ub.getVersion().compareTo(upperBound.getVersion());
        if (c > 0 || (c == 0 && !upperBound.isInclusive()))
          upperBound = ub; 
      } 
      this.lowerBound = lowerBound;
      this.upperBound = upperBound;
    } 
  }
  
  public boolean containsVersion(Version version) {
    for (VersionRange range : this.ranges) {
      if (range.containsVersion(version))
        return true; 
    } 
    return false;
  }
  
  public VersionRange.Bound getLowerBound() {
    return this.lowerBound;
  }
  
  public VersionRange.Bound getUpperBound() {
    return this.upperBound;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (obj == null || !getClass().equals(obj.getClass()))
      return false; 
    UnionVersionRange that = (UnionVersionRange)obj;
    return this.ranges.equals(that.ranges);
  }
  
  public int hashCode() {
    return 97 * this.ranges.hashCode();
  }
  
  public String toString() {
    StringBuilder buffer = new StringBuilder(128);
    for (VersionRange range : this.ranges) {
      if (buffer.length() > 0)
        buffer.append(", "); 
      buffer.append(range);
    } 
    return buffer.toString();
  }
}
