package org.apache.maven.artifact.resolver.filter;

import org.apache.maven.artifact.Artifact;

public interface ArtifactFilter {
  boolean include(Artifact paramArtifact);
}
