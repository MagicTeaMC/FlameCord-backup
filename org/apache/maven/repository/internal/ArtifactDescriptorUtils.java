package org.apache.maven.repository.internal;

import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryPolicy;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;

public class ArtifactDescriptorUtils {
  public static Artifact toPomArtifact(Artifact artifact) {
    DefaultArtifact defaultArtifact;
    Artifact pomArtifact = artifact;
    if (pomArtifact.getClassifier().length() > 0 || !"pom".equals(pomArtifact.getExtension()))
      defaultArtifact = new DefaultArtifact(artifact.getGroupId(), artifact.getArtifactId(), "pom", artifact.getVersion()); 
    return (Artifact)defaultArtifact;
  }
  
  public static RemoteRepository toRemoteRepository(Repository repository) {
    RemoteRepository.Builder builder = new RemoteRepository.Builder(repository.getId(), repository.getLayout(), repository.getUrl());
    builder.setSnapshotPolicy(toRepositoryPolicy(repository.getSnapshots()));
    builder.setReleasePolicy(toRepositoryPolicy(repository.getReleases()));
    return builder.build();
  }
  
  public static RepositoryPolicy toRepositoryPolicy(RepositoryPolicy policy) {
    boolean enabled = true;
    String checksums = "warn";
    String updates = "daily";
    if (policy != null) {
      enabled = policy.isEnabled();
      if (policy.getUpdatePolicy() != null)
        updates = policy.getUpdatePolicy(); 
      if (policy.getChecksumPolicy() != null)
        checksums = policy.getChecksumPolicy(); 
    } 
    return new RepositoryPolicy(enabled, updates, checksums);
  }
}
