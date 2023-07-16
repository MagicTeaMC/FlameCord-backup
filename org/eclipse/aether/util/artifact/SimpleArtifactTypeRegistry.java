package org.eclipse.aether.util.artifact;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.aether.artifact.ArtifactType;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;

class SimpleArtifactTypeRegistry implements ArtifactTypeRegistry {
  private final Map<String, ArtifactType> types = new HashMap<>();
  
  public SimpleArtifactTypeRegistry add(ArtifactType type) {
    this.types.put(type.getId(), type);
    return this;
  }
  
  public ArtifactType get(String typeId) {
    return this.types.get(typeId);
  }
  
  public String toString() {
    return this.types.toString();
  }
}
