package org.eclipse.aether.util.repository;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.resolution.ArtifactDescriptorPolicy;
import org.eclipse.aether.resolution.ArtifactDescriptorPolicyRequest;

public final class SimpleArtifactDescriptorPolicy implements ArtifactDescriptorPolicy {
  private final int policy;
  
  public SimpleArtifactDescriptorPolicy(boolean ignoreMissing, boolean ignoreInvalid) {
    this((ignoreMissing ? 1 : 0) | (ignoreInvalid ? 2 : 0));
  }
  
  public SimpleArtifactDescriptorPolicy(int policy) {
    this.policy = policy;
  }
  
  public int getPolicy(RepositorySystemSession session, ArtifactDescriptorPolicyRequest request) {
    return this.policy;
  }
}
