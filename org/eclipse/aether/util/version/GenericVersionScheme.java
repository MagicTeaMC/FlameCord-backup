package org.eclipse.aether.util.version;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionConstraint;
import org.eclipse.aether.version.VersionRange;
import org.eclipse.aether.version.VersionScheme;

public final class GenericVersionScheme implements VersionScheme {
  public Version parseVersion(String version) throws InvalidVersionSpecificationException {
    return new GenericVersion(version);
  }
  
  public VersionRange parseVersionRange(String range) throws InvalidVersionSpecificationException {
    return new GenericVersionRange(range);
  }
  
  public VersionConstraint parseVersionConstraint(String constraint) throws InvalidVersionSpecificationException {
    VersionConstraint result;
    Collection<VersionRange> ranges = new ArrayList<>();
    String process = constraint;
    while (process.startsWith("[") || process.startsWith("(")) {
      int index1 = process.indexOf(')');
      int index2 = process.indexOf(']');
      int index = index2;
      if (index2 < 0 || (index1 >= 0 && index1 < index2))
        index = index1; 
      if (index < 0)
        throw new InvalidVersionSpecificationException(constraint, "Unbounded version range " + constraint); 
      VersionRange range = parseVersionRange(process.substring(0, index + 1));
      ranges.add(range);
      process = process.substring(index + 1).trim();
      if (process.length() > 0 && process.startsWith(","))
        process = process.substring(1).trim(); 
    } 
    if (process.length() > 0 && !ranges.isEmpty())
      throw new InvalidVersionSpecificationException(constraint, "Invalid version range " + constraint + ", expected [ or ( but got " + process); 
    if (ranges.isEmpty()) {
      result = new GenericVersionConstraint(parseVersion(constraint));
    } else {
      result = new GenericVersionConstraint(UnionVersionRange.from(ranges));
    } 
    return result;
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    return (obj != null && getClass().equals(obj.getClass()));
  }
  
  public int hashCode() {
    return getClass().hashCode();
  }
}
