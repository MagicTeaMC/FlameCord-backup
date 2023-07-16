package org.eclipse.aether.util.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionRange;
import org.eclipse.aether.version.VersionScheme;

class AbstractPatternDependencyFilter implements DependencyFilter {
  private final Set<String> patterns = new HashSet<>();
  
  private final VersionScheme versionScheme;
  
  AbstractPatternDependencyFilter(String... patterns) {
    this((VersionScheme)null, patterns);
  }
  
  AbstractPatternDependencyFilter(VersionScheme versionScheme, String... patterns) {
    this(versionScheme, (patterns == null) ? null : Arrays.<String>asList(patterns));
  }
  
  AbstractPatternDependencyFilter(Collection<String> patterns) {
    this((VersionScheme)null, patterns);
  }
  
  AbstractPatternDependencyFilter(VersionScheme versionScheme, Collection<String> patterns) {
    if (patterns != null)
      this.patterns.addAll(patterns); 
    this.versionScheme = versionScheme;
  }
  
  public boolean accept(DependencyNode node, List<DependencyNode> parents) {
    Dependency dependency = node.getDependency();
    if (dependency == null)
      return true; 
    return accept(dependency.getArtifact());
  }
  
  protected boolean accept(Artifact artifact) {
    for (String pattern : this.patterns) {
      boolean matched = accept(artifact, pattern);
      if (matched)
        return true; 
    } 
    return false;
  }
  
  private boolean accept(Artifact artifact, String pattern) {
    String[] tokens = { artifact.getGroupId(), artifact.getArtifactId(), artifact.getExtension(), artifact.getBaseVersion() };
    String[] patternTokens = pattern.split(":");
    boolean matched = (patternTokens.length <= tokens.length);
    for (int i = 0; matched && i < patternTokens.length; i++)
      matched = matches(tokens[i], patternTokens[i]); 
    return matched;
  }
  
  private boolean matches(String token, String pattern) {
    boolean matches;
    if ("*".equals(pattern) || pattern.length() == 0) {
      matches = true;
    } else if (pattern.startsWith("*") && pattern.endsWith("*")) {
      String contains = pattern.substring(1, pattern.length() - 1);
      matches = token.contains(contains);
    } else if (pattern.startsWith("*")) {
      String suffix = pattern.substring(1);
      matches = token.endsWith(suffix);
    } else if (pattern.endsWith("*")) {
      String prefix = pattern.substring(0, pattern.length() - 1);
      matches = token.startsWith(prefix);
    } else if (pattern.startsWith("[") || pattern.startsWith("(")) {
      matches = isVersionIncludedInRange(token, pattern);
    } else {
      matches = token.equals(pattern);
    } 
    return matches;
  }
  
  private boolean isVersionIncludedInRange(String version, String range) {
    if (this.versionScheme == null)
      return false; 
    try {
      Version parsedVersion = this.versionScheme.parseVersion(version);
      VersionRange parsedRange = this.versionScheme.parseVersionRange(range);
      return parsedRange.containsVersion(parsedVersion);
    } catch (InvalidVersionSpecificationException e) {
      return false;
    } 
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null || !getClass().equals(obj.getClass()))
      return false; 
    AbstractPatternDependencyFilter that = (AbstractPatternDependencyFilter)obj;
    return (Objects.equals(this.patterns, that.patterns) && 
      Objects.equals(this.versionScheme, that.versionScheme));
  }
  
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + this.patterns.hashCode();
    hash = hash * 31 + ((this.versionScheme == null) ? 0 : this.versionScheme.hashCode());
    return hash;
  }
}
