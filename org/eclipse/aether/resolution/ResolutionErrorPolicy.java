package org.eclipse.aether.resolution;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;

public interface ResolutionErrorPolicy {
  public static final int CACHE_DISABLED = 0;
  
  public static final int CACHE_NOT_FOUND = 1;
  
  public static final int CACHE_TRANSFER_ERROR = 2;
  
  public static final int CACHE_ALL = 3;
  
  int getArtifactPolicy(RepositorySystemSession paramRepositorySystemSession, ResolutionErrorPolicyRequest<Artifact> paramResolutionErrorPolicyRequest);
  
  int getMetadataPolicy(RepositorySystemSession paramRepositorySystemSession, ResolutionErrorPolicyRequest<Metadata> paramResolutionErrorPolicyRequest);
}
