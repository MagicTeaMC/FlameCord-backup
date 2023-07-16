package org.apache.maven.artifact.metadata;

import org.apache.maven.repository.legacy.metadata.ArtifactMetadata;

@Deprecated
public interface ArtifactMetadata extends ArtifactMetadata {
  void merge(ArtifactMetadata paramArtifactMetadata);
}
