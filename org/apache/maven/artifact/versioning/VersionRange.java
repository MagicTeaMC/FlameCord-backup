package org.apache.maven.artifact.versioning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import org.apache.maven.artifact.Artifact;

public class VersionRange {
  private static final Map<String, VersionRange> CACHE_SPEC = Collections.synchronizedMap(new WeakHashMap<>());
  
  private static final Map<String, VersionRange> CACHE_VERSION = Collections.synchronizedMap(new WeakHashMap<>());
  
  private final ArtifactVersion recommendedVersion;
  
  private final List<Restriction> restrictions;
  
  private VersionRange(ArtifactVersion recommendedVersion, List<Restriction> restrictions) {
    this.recommendedVersion = recommendedVersion;
    this.restrictions = restrictions;
  }
  
  public ArtifactVersion getRecommendedVersion() {
    return this.recommendedVersion;
  }
  
  public List<Restriction> getRestrictions() {
    return this.restrictions;
  }
  
  @Deprecated
  public VersionRange cloneOf() {
    List<Restriction> copiedRestrictions = null;
    if (this.restrictions != null) {
      copiedRestrictions = new ArrayList<>();
      if (!this.restrictions.isEmpty())
        copiedRestrictions.addAll(this.restrictions); 
    } 
    return new VersionRange(this.recommendedVersion, copiedRestrictions);
  }
  
  public static VersionRange createFromVersionSpec(String spec) throws InvalidVersionSpecificationException {
    if (spec == null)
      return null; 
    VersionRange cached = CACHE_SPEC.get(spec);
    if (cached != null)
      return cached; 
    List<Restriction> restrictions = new ArrayList<>();
    String process = spec;
    ArtifactVersion version = null;
    ArtifactVersion upperBound = null;
    ArtifactVersion lowerBound = null;
    while (process.startsWith("[") || process.startsWith("(")) {
      int index1 = process.indexOf(')');
      int index2 = process.indexOf(']');
      int index = index2;
      if (index2 < 0 || index1 < index2)
        if (index1 >= 0)
          index = index1;  
      if (index < 0)
        throw new InvalidVersionSpecificationException("Unbounded range: " + spec); 
      Restriction restriction = parseRestriction(process.substring(0, index + 1));
      if (lowerBound == null)
        lowerBound = restriction.getLowerBound(); 
      if (upperBound != null)
        if (restriction.getLowerBound() == null || restriction.getLowerBound().compareTo(upperBound) < 0)
          throw new InvalidVersionSpecificationException("Ranges overlap: " + spec);  
      restrictions.add(restriction);
      upperBound = restriction.getUpperBound();
      process = process.substring(index + 1).trim();
      if (process.length() > 0 && process.startsWith(","))
        process = process.substring(1).trim(); 
    } 
    if (process.length() > 0) {
      if (restrictions.size() > 0)
        throw new InvalidVersionSpecificationException("Only fully-qualified sets allowed in multiple set scenario: " + spec); 
      version = new DefaultArtifactVersion(process);
      restrictions.add(Restriction.EVERYTHING);
    } 
    cached = new VersionRange(version, restrictions);
    CACHE_SPEC.put(spec, cached);
    return cached;
  }
  
  private static Restriction parseRestriction(String spec) throws InvalidVersionSpecificationException {
    Restriction restriction;
    boolean lowerBoundInclusive = spec.startsWith("[");
    boolean upperBoundInclusive = spec.endsWith("]");
    String process = spec.substring(1, spec.length() - 1).trim();
    int index = process.indexOf(',');
    if (index < 0) {
      if (!lowerBoundInclusive || !upperBoundInclusive)
        throw new InvalidVersionSpecificationException("Single version must be surrounded by []: " + spec); 
      ArtifactVersion version = new DefaultArtifactVersion(process);
      restriction = new Restriction(version, lowerBoundInclusive, version, upperBoundInclusive);
    } else {
      String lowerBound = process.substring(0, index).trim();
      String upperBound = process.substring(index + 1).trim();
      if (lowerBound.equals(upperBound))
        throw new InvalidVersionSpecificationException("Range cannot have identical boundaries: " + spec); 
      ArtifactVersion lowerVersion = null;
      if (lowerBound.length() > 0)
        lowerVersion = new DefaultArtifactVersion(lowerBound); 
      ArtifactVersion upperVersion = null;
      if (upperBound.length() > 0)
        upperVersion = new DefaultArtifactVersion(upperBound); 
      if (upperVersion != null && lowerVersion != null && upperVersion.compareTo(lowerVersion) < 0)
        throw new InvalidVersionSpecificationException("Range defies version ordering: " + spec); 
      restriction = new Restriction(lowerVersion, lowerBoundInclusive, upperVersion, upperBoundInclusive);
    } 
    return restriction;
  }
  
  public static VersionRange createFromVersion(String version) {
    VersionRange cached = CACHE_VERSION.get(version);
    if (cached == null) {
      List<Restriction> restrictions = Collections.emptyList();
      cached = new VersionRange(new DefaultArtifactVersion(version), restrictions);
      CACHE_VERSION.put(version, cached);
    } 
    return cached;
  }
  
  public VersionRange restrict(VersionRange restriction) {
    List<Restriction> restrictions, r1 = this.restrictions;
    List<Restriction> r2 = restriction.restrictions;
    if (r1.isEmpty() || r2.isEmpty()) {
      restrictions = Collections.emptyList();
    } else {
      restrictions = Collections.unmodifiableList(intersection(r1, r2));
    } 
    ArtifactVersion version = null;
    if (restrictions.size() > 0) {
      for (Restriction r : restrictions) {
        if (this.recommendedVersion != null && r.containsVersion(this.recommendedVersion)) {
          version = this.recommendedVersion;
          break;
        } 
        if (version == null && restriction.getRecommendedVersion() != null && r
          .containsVersion(restriction.getRecommendedVersion()))
          version = restriction.getRecommendedVersion(); 
      } 
    } else if (this.recommendedVersion != null) {
      version = this.recommendedVersion;
    } else if (restriction.recommendedVersion != null) {
      version = restriction.recommendedVersion;
    } 
    return new VersionRange(version, restrictions);
  }
  
  private List<Restriction> intersection(List<Restriction> r1, List<Restriction> r2) {
    List<Restriction> restrictions = new ArrayList<>(r1.size() + r2.size());
    Iterator<Restriction> i1 = r1.iterator();
    Iterator<Restriction> i2 = r2.iterator();
    Restriction res1 = i1.next();
    Restriction res2 = i2.next();
    boolean done = false;
    while (!done) {
      if (res1.getLowerBound() == null || res2.getUpperBound() == null || res1
        .getLowerBound().compareTo(res2.getUpperBound()) <= 0) {
        if (res1.getUpperBound() == null || res2.getLowerBound() == null || res1
          .getUpperBound().compareTo(res2.getLowerBound()) >= 0) {
          ArtifactVersion lower, upper;
          boolean lowerInclusive, upperInclusive;
          if (res1.getLowerBound() == null) {
            lower = res2.getLowerBound();
            lowerInclusive = res2.isLowerBoundInclusive();
          } else if (res2.getLowerBound() == null) {
            lower = res1.getLowerBound();
            lowerInclusive = res1.isLowerBoundInclusive();
          } else {
            int comparison = res1.getLowerBound().compareTo(res2.getLowerBound());
            if (comparison < 0) {
              lower = res2.getLowerBound();
              lowerInclusive = res2.isLowerBoundInclusive();
            } else if (comparison == 0) {
              lower = res1.getLowerBound();
              lowerInclusive = (res1.isLowerBoundInclusive() && res2.isLowerBoundInclusive());
            } else {
              lower = res1.getLowerBound();
              lowerInclusive = res1.isLowerBoundInclusive();
            } 
          } 
          if (res1.getUpperBound() == null) {
            upper = res2.getUpperBound();
            upperInclusive = res2.isUpperBoundInclusive();
          } else if (res2.getUpperBound() == null) {
            upper = res1.getUpperBound();
            upperInclusive = res1.isUpperBoundInclusive();
          } else {
            int comparison = res1.getUpperBound().compareTo(res2.getUpperBound());
            if (comparison < 0) {
              upper = res1.getUpperBound();
              upperInclusive = res1.isUpperBoundInclusive();
            } else if (comparison == 0) {
              upper = res1.getUpperBound();
              upperInclusive = (res1.isUpperBoundInclusive() && res2.isUpperBoundInclusive());
            } else {
              upper = res2.getUpperBound();
              upperInclusive = res2.isUpperBoundInclusive();
            } 
          } 
          if (lower == null || upper == null || lower.compareTo(upper) != 0) {
            restrictions.add(new Restriction(lower, lowerInclusive, upper, upperInclusive));
          } else if (lowerInclusive && upperInclusive) {
            restrictions.add(new Restriction(lower, lowerInclusive, upper, upperInclusive));
          } 
          if (upper == res2.getUpperBound()) {
            if (i2.hasNext()) {
              res2 = i2.next();
              continue;
            } 
            done = true;
            continue;
          } 
          if (i1.hasNext()) {
            res1 = i1.next();
            continue;
          } 
          done = true;
          continue;
        } 
        if (i1.hasNext()) {
          res1 = i1.next();
          continue;
        } 
        done = true;
        continue;
      } 
      if (i2.hasNext()) {
        res2 = i2.next();
        continue;
      } 
      done = true;
    } 
    return restrictions;
  }
  
  public ArtifactVersion getSelectedVersion(Artifact artifact) throws OverConstrainedVersionException {
    ArtifactVersion version;
    if (this.recommendedVersion != null) {
      version = this.recommendedVersion;
    } else {
      if (this.restrictions.size() == 0)
        throw new OverConstrainedVersionException("The artifact has no valid ranges", artifact); 
      version = null;
    } 
    return version;
  }
  
  public boolean isSelectedVersionKnown(Artifact artifact) throws OverConstrainedVersionException {
    boolean value = false;
    if (this.recommendedVersion != null) {
      value = true;
    } else if (this.restrictions.size() == 0) {
      throw new OverConstrainedVersionException("The artifact has no valid ranges", artifact);
    } 
    return value;
  }
  
  public String toString() {
    if (this.recommendedVersion != null)
      return this.recommendedVersion.toString(); 
    StringBuilder buf = new StringBuilder();
    for (Iterator<Restriction> i = this.restrictions.iterator(); i.hasNext(); ) {
      Restriction r = i.next();
      buf.append(r.toString());
      if (i.hasNext())
        buf.append(','); 
    } 
    return buf.toString();
  }
  
  public ArtifactVersion matchVersion(List<ArtifactVersion> versions) {
    ArtifactVersion matched = null;
    for (ArtifactVersion version : versions) {
      if (containsVersion(version))
        if (matched == null || version.compareTo(matched) > 0)
          matched = version;  
    } 
    return matched;
  }
  
  public boolean containsVersion(ArtifactVersion version) {
    for (Restriction restriction : this.restrictions) {
      if (restriction.containsVersion(version))
        return true; 
    } 
    return false;
  }
  
  public boolean hasRestrictions() {
    return (!this.restrictions.isEmpty() && this.recommendedVersion == null);
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (!(obj instanceof VersionRange))
      return false; 
    VersionRange other = (VersionRange)obj;
    return (Objects.equals(this.recommendedVersion, other.recommendedVersion) && 
      Objects.equals(this.restrictions, other.restrictions));
  }
  
  public int hashCode() {
    int hash = 7;
    hash = 31 * hash + ((this.recommendedVersion == null) ? 0 : this.recommendedVersion.hashCode());
    hash = 31 * hash + ((this.restrictions == null) ? 0 : this.restrictions.hashCode());
    return hash;
  }
}
