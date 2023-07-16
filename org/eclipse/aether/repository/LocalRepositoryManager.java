package org.eclipse.aether.repository;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;

public interface LocalRepositoryManager {
  LocalRepository getRepository();
  
  String getPathForLocalArtifact(Artifact paramArtifact);
  
  String getPathForRemoteArtifact(Artifact paramArtifact, RemoteRepository paramRemoteRepository, String paramString);
  
  String getPathForLocalMetadata(Metadata paramMetadata);
  
  String getPathForRemoteMetadata(Metadata paramMetadata, RemoteRepository paramRemoteRepository, String paramString);
  
  LocalArtifactResult find(RepositorySystemSession paramRepositorySystemSession, LocalArtifactRequest paramLocalArtifactRequest);
  
  void add(RepositorySystemSession paramRepositorySystemSession, LocalArtifactRegistration paramLocalArtifactRegistration);
  
  LocalMetadataResult find(RepositorySystemSession paramRepositorySystemSession, LocalMetadataRequest paramLocalMetadataRequest);
  
  void add(RepositorySystemSession paramRepositorySystemSession, LocalMetadataRegistration paramLocalMetadataRegistration);
}
