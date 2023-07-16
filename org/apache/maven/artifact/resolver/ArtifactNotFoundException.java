package org.apache.maven.artifact.resolver;

import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;

public class ArtifactNotFoundException extends AbstractArtifactResolutionException {
  private String downloadUrl;
  
  protected ArtifactNotFoundException(String message, Artifact artifact, List<ArtifactRepository> remoteRepositories) {
    super(message, artifact, remoteRepositories);
  }
  
  public ArtifactNotFoundException(String message, Artifact artifact) {
    this(message, artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), artifact.getType(), artifact
        .getClassifier(), null, artifact.getDownloadUrl(), artifact.getDependencyTrail());
  }
  
  protected ArtifactNotFoundException(String message, Artifact artifact, List<ArtifactRepository> remoteRepositories, Throwable cause) {
    this(message, artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), artifact.getType(), artifact
        .getClassifier(), remoteRepositories, artifact.getDownloadUrl(), artifact.getDependencyTrail(), cause);
  }
  
  public ArtifactNotFoundException(String message, String groupId, String artifactId, String version, String type, String classifier, List<ArtifactRepository> remoteRepositories, String downloadUrl, List<String> path, Throwable cause) {
    super(constructMissingArtifactMessage(message, "", groupId, artifactId, version, type, classifier, downloadUrl, path), groupId, artifactId, version, type, classifier, remoteRepositories, null, cause);
    this.downloadUrl = downloadUrl;
  }
  
  private ArtifactNotFoundException(String message, String groupId, String artifactId, String version, String type, String classifier, List<ArtifactRepository> remoteRepositories, String downloadUrl, List<String> path) {
    super(constructMissingArtifactMessage(message, "", groupId, artifactId, version, type, classifier, downloadUrl, path), groupId, artifactId, version, type, classifier, remoteRepositories, null);
    this.downloadUrl = downloadUrl;
  }
  
  public String getDownloadUrl() {
    return this.downloadUrl;
  }
}
