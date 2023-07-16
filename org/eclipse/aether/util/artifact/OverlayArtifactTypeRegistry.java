package org.eclipse.aether.util.artifact;

import org.eclipse.aether.artifact.ArtifactType;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;

public final class OverlayArtifactTypeRegistry extends SimpleArtifactTypeRegistry {
  private final ArtifactTypeRegistry delegate;
  
  public OverlayArtifactTypeRegistry(ArtifactTypeRegistry delegate) {
    this.delegate = delegate;
  }
  
  public OverlayArtifactTypeRegistry add(ArtifactType type) {
    super.add(type);
    return this;
  }
  
  public ArtifactType get(String typeId) {
    ArtifactType type = super.get(typeId);
    if (type == null && this.delegate != null)
      type = this.delegate.get(typeId); 
    return type;
  }
}
