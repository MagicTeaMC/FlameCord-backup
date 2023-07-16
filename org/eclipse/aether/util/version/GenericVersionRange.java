package org.eclipse.aether.util.version;

import java.util.Objects;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionRange;

final class GenericVersionRange implements VersionRange {
  private final VersionRange.Bound lowerBound;
  
  private final VersionRange.Bound upperBound;
  
  GenericVersionRange(String range) throws InvalidVersionSpecificationException {
    boolean lowerBoundInclusive, upperBoundInclusive;
    Version lowerBound, upperBound;
    String process = range;
    if (range.startsWith("[")) {
      lowerBoundInclusive = true;
    } else if (range.startsWith("(")) {
      lowerBoundInclusive = false;
    } else {
      throw new InvalidVersionSpecificationException(range, "Invalid version range " + range + ", a range must start with either [ or (");
    } 
    if (range.endsWith("]")) {
      upperBoundInclusive = true;
    } else if (range.endsWith(")")) {
      upperBoundInclusive = false;
    } else {
      throw new InvalidVersionSpecificationException(range, "Invalid version range " + range + ", a range must end with either [ or (");
    } 
    process = process.substring(1, process.length() - 1);
    int index = process.indexOf(",");
    if (index < 0) {
      if (!lowerBoundInclusive || !upperBoundInclusive)
        throw new InvalidVersionSpecificationException(range, "Invalid version range " + range + ", single version must be surrounded by []"); 
      String version = process.trim();
      if (version.endsWith(".*")) {
        String prefix = version.substring(0, version.length() - 1);
        lowerBound = parse(prefix + "min");
        upperBound = parse(prefix + "max");
      } else {
        lowerBound = parse(version);
        upperBound = lowerBound;
      } 
    } else {
      String parsedLowerBound = process.substring(0, index).trim();
      String parsedUpperBound = process.substring(index + 1).trim();
      if (parsedUpperBound.contains(","))
        throw new InvalidVersionSpecificationException(range, "Invalid version range " + range + ", bounds may not contain additional ','"); 
      lowerBound = (parsedLowerBound.length() > 0) ? parse(parsedLowerBound) : null;
      upperBound = (parsedUpperBound.length() > 0) ? parse(parsedUpperBound) : null;
      if (upperBound != null && lowerBound != null)
        if (upperBound.compareTo(lowerBound) < 0)
          throw new InvalidVersionSpecificationException(range, "Invalid version range " + range + ", lower bound must not be greater than upper bound");  
    } 
    this.lowerBound = (lowerBound != null) ? new VersionRange.Bound(lowerBound, lowerBoundInclusive) : null;
    this.upperBound = (upperBound != null) ? new VersionRange.Bound(upperBound, upperBoundInclusive) : null;
  }
  
  private Version parse(String version) {
    return new GenericVersion(version);
  }
  
  public VersionRange.Bound getLowerBound() {
    return this.lowerBound;
  }
  
  public VersionRange.Bound getUpperBound() {
    return this.upperBound;
  }
  
  public boolean containsVersion(Version version) {
    if (this.lowerBound != null) {
      int comparison = this.lowerBound.getVersion().compareTo(version);
      if (comparison == 0 && !this.lowerBound.isInclusive())
        return false; 
      if (comparison > 0)
        return false; 
    } 
    if (this.upperBound != null) {
      int comparison = this.upperBound.getVersion().compareTo(version);
      if (comparison == 0 && !this.upperBound.isInclusive())
        return false; 
      if (comparison < 0)
        return false; 
    } 
    return true;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (obj == null || !getClass().equals(obj.getClass()))
      return false; 
    GenericVersionRange that = (GenericVersionRange)obj;
    return (Objects.equals(this.upperBound, that.upperBound) && 
      Objects.equals(this.lowerBound, that.lowerBound));
  }
  
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + hash(this.upperBound);
    hash = hash * 31 + hash(this.lowerBound);
    return hash;
  }
  
  private static int hash(Object obj) {
    return (obj != null) ? obj.hashCode() : 0;
  }
  
  public String toString() {
    StringBuilder buffer = new StringBuilder(64);
    if (this.lowerBound != null) {
      buffer.append(this.lowerBound.isInclusive() ? 91 : 40);
      buffer.append(this.lowerBound.getVersion());
    } else {
      buffer.append('(');
    } 
    buffer.append(',');
    if (this.upperBound != null) {
      buffer.append(this.upperBound.getVersion());
      buffer.append(this.upperBound.isInclusive() ? 93 : 41);
    } else {
      buffer.append(')');
    } 
    return buffer.toString();
  }
}
