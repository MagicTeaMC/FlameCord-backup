package org.apache.maven.artifact.resolver;

import java.util.ArrayList;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;

public class MultipleArtifactsNotFoundException extends ArtifactResolutionException {
  private final List<Artifact> resolvedArtifacts;
  
  private final List<Artifact> missingArtifacts;
  
  @Deprecated
  public MultipleArtifactsNotFoundException(Artifact originatingArtifact, List<Artifact> missingArtifacts, List<ArtifactRepository> remoteRepositories) {
    this(originatingArtifact, new ArrayList<>(), missingArtifacts, remoteRepositories);
  }
  
  public MultipleArtifactsNotFoundException(Artifact originatingArtifact, List<Artifact> resolvedArtifacts, List<Artifact> missingArtifacts, List<ArtifactRepository> remoteRepositories) {
    super(constructMessage(missingArtifacts), originatingArtifact, remoteRepositories);
    this.resolvedArtifacts = resolvedArtifacts;
    this.missingArtifacts = missingArtifacts;
  }
  
  public List<Artifact> getResolvedArtifacts() {
    return this.resolvedArtifacts;
  }
  
  public List<Artifact> getMissingArtifacts() {
    return this.missingArtifacts;
  }
  
  private static String constructMessage(List<Artifact> artifacts) {
    StringBuilder buffer = new StringBuilder(256);
    buffer.append("Missing:\n");
    buffer.append("----------\n");
    int counter = 0;
    for (Artifact artifact : artifacts) {
      String message = ++counter + ") " + artifact.getId();
      buffer.append(constructMissingArtifactMessage(message, "  ", artifact.getGroupId(), artifact
            .getArtifactId(), artifact.getVersion(), artifact.getType(), artifact.getClassifier(), artifact
            .getDownloadUrl(), artifact.getDependencyTrail()));
    } 
    buffer.append("----------\n");
    int size = artifacts.size();
    buffer.append(size).append(" required artifact");
    if (size > 1) {
      buffer.append("s are");
    } else {
      buffer.append(" is");
    } 
    buffer.append(" missing.\n\nfor artifact: ");
    return buffer.toString();
  }
}
