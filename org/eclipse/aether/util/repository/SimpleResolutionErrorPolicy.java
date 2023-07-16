package org.eclipse.aether.util.repository;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.resolution.ResolutionErrorPolicy;
import org.eclipse.aether.resolution.ResolutionErrorPolicyRequest;

public final class SimpleResolutionErrorPolicy implements ResolutionErrorPolicy {
  private final int artifactPolicy;
  
  private final int metadataPolicy;
  
  public SimpleResolutionErrorPolicy(boolean cacheNotFound, boolean cacheTransferErrors) {
    this((cacheNotFound ? 1 : 0) | (cacheTransferErrors ? 2 : 0));
  }
  
  public SimpleResolutionErrorPolicy(int policy) {
    this(policy, policy);
  }
  
  public SimpleResolutionErrorPolicy(int artifactPolicy, int metadataPolicy) {
    this.artifactPolicy = artifactPolicy;
    this.metadataPolicy = metadataPolicy;
  }
  
  public int getArtifactPolicy(RepositorySystemSession session, ResolutionErrorPolicyRequest<Artifact> request) {
    return this.artifactPolicy;
  }
  
  public int getMetadataPolicy(RepositorySystemSession session, ResolutionErrorPolicyRequest<Metadata> request) {
    return this.metadataPolicy;
  }
}
