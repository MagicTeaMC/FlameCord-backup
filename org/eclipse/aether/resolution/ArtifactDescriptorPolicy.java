package org.eclipse.aether.resolution;

import org.eclipse.aether.RepositorySystemSession;

public interface ArtifactDescriptorPolicy {
  public static final int STRICT = 0;
  
  public static final int IGNORE_MISSING = 1;
  
  public static final int IGNORE_INVALID = 2;
  
  public static final int IGNORE_ERRORS = 3;
  
  int getPolicy(RepositorySystemSession paramRepositorySystemSession, ArtifactDescriptorPolicyRequest paramArtifactDescriptorPolicyRequest);
}
