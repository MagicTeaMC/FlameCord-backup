package org.eclipse.aether.transform;

import java.util.Collection;
import org.eclipse.aether.artifact.Artifact;

public interface FileTransformerManager {
  Collection<FileTransformer> getTransformersForArtifact(Artifact paramArtifact);
}
