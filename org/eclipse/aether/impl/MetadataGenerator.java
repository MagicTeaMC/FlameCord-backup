package org.eclipse.aether.impl;

import java.util.Collection;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;

public interface MetadataGenerator {
  Collection<? extends Metadata> prepare(Collection<? extends Artifact> paramCollection);
  
  Artifact transformArtifact(Artifact paramArtifact);
  
  Collection<? extends Metadata> finish(Collection<? extends Artifact> paramCollection);
}
