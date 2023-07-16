package org.eclipse.aether.util.filter;

import java.util.Collection;
import java.util.List;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.version.VersionScheme;

public final class PatternInclusionsDependencyFilter extends AbstractPatternDependencyFilter {
  public PatternInclusionsDependencyFilter(String... patterns) {
    super(patterns);
  }
  
  public PatternInclusionsDependencyFilter(VersionScheme versionScheme, String... patterns) {
    super(versionScheme, patterns);
  }
  
  public PatternInclusionsDependencyFilter(Collection<String> patterns) {
    super(patterns);
  }
  
  public PatternInclusionsDependencyFilter(VersionScheme versionScheme, Collection<String> patterns) {
    super(versionScheme, patterns);
  }
}
