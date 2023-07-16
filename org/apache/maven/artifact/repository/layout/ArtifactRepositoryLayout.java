package org.apache.maven.artifact.repository.layout;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;

public interface ArtifactRepositoryLayout {
  public static final String ROLE = ArtifactRepositoryLayout.class.getName();
  
  String getId();
  
  String pathOf(Artifact paramArtifact);
  
  String pathOfLocalRepositoryMetadata(ArtifactMetadata paramArtifactMetadata, ArtifactRepository paramArtifactRepository);
  
  String pathOfRemoteRepositoryMetadata(ArtifactMetadata paramArtifactMetadata);
}
