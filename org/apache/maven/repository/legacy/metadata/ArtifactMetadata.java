package org.apache.maven.repository.legacy.metadata;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataStoreException;

public interface ArtifactMetadata {
  boolean storedInArtifactVersionDirectory();
  
  boolean storedInGroupDirectory();
  
  String getGroupId();
  
  String getArtifactId();
  
  String getBaseVersion();
  
  Object getKey();
  
  String getLocalFilename(ArtifactRepository paramArtifactRepository);
  
  String getRemoteFilename();
  
  void merge(ArtifactMetadata paramArtifactMetadata);
  
  void storeInLocalRepository(ArtifactRepository paramArtifactRepository1, ArtifactRepository paramArtifactRepository2) throws RepositoryMetadataStoreException;
  
  String extendedToString();
}
