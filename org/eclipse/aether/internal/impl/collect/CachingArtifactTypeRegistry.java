package org.eclipse.aether.internal.impl.collect;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.ArtifactType;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;

class CachingArtifactTypeRegistry implements ArtifactTypeRegistry {
  private final ArtifactTypeRegistry delegate;
  
  private final Map<String, ArtifactType> types;
  
  public static ArtifactTypeRegistry newInstance(RepositorySystemSession session) {
    return newInstance(session.getArtifactTypeRegistry());
  }
  
  public static ArtifactTypeRegistry newInstance(ArtifactTypeRegistry delegate) {
    return (delegate != null) ? new CachingArtifactTypeRegistry(delegate) : null;
  }
  
  private CachingArtifactTypeRegistry(ArtifactTypeRegistry delegate) {
    this.delegate = delegate;
    this.types = new HashMap<>();
  }
  
  public ArtifactType get(String typeId) {
    ArtifactType type = this.types.get(typeId);
    if (type == null) {
      type = this.delegate.get(typeId);
      this.types.put(typeId, type);
    } 
    return type;
  }
}
