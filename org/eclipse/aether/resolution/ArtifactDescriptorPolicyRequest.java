package org.eclipse.aether.resolution;

import org.eclipse.aether.artifact.Artifact;

public final class ArtifactDescriptorPolicyRequest {
  private Artifact artifact;
  
  private String context = "";
  
  public ArtifactDescriptorPolicyRequest() {}
  
  public ArtifactDescriptorPolicyRequest(Artifact artifact, String context) {
    setArtifact(artifact);
    setRequestContext(context);
  }
  
  public Artifact getArtifact() {
    return this.artifact;
  }
  
  public ArtifactDescriptorPolicyRequest setArtifact(Artifact artifact) {
    this.artifact = artifact;
    return this;
  }
  
  public String getRequestContext() {
    return this.context;
  }
  
  public ArtifactDescriptorPolicyRequest setRequestContext(String context) {
    this.context = (context != null) ? context : "";
    return this;
  }
  
  public String toString() {
    return String.valueOf(getArtifact());
  }
}
