package org.eclipse.aether.util.filter;

import java.util.Collection;
import java.util.List;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.version.VersionScheme;

public final class PatternExclusionsDependencyFilter extends AbstractPatternDependencyFilter {
  public PatternExclusionsDependencyFilter(String... patterns) {
    super(patterns);
  }
  
  public PatternExclusionsDependencyFilter(VersionScheme versionScheme, String... patterns) {
    super(versionScheme, patterns);
  }
  
  public PatternExclusionsDependencyFilter(Collection<String> patterns) {
    super(patterns);
  }
  
  public PatternExclusionsDependencyFilter(VersionScheme versionScheme, Collection<String> patterns) {
    super(versionScheme, patterns);
  }
  
  protected boolean accept(Artifact artifact) {
    return !super.accept(artifact);
  }
}
