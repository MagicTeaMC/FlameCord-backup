package org.eclipse.aether.impl;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.transfer.ArtifactTransferException;
import org.eclipse.aether.transfer.MetadataTransferException;

public interface UpdateCheckManager {
  void checkArtifact(RepositorySystemSession paramRepositorySystemSession, UpdateCheck<Artifact, ArtifactTransferException> paramUpdateCheck);
  
  void touchArtifact(RepositorySystemSession paramRepositorySystemSession, UpdateCheck<Artifact, ArtifactTransferException> paramUpdateCheck);
  
  void checkMetadata(RepositorySystemSession paramRepositorySystemSession, UpdateCheck<Metadata, MetadataTransferException> paramUpdateCheck);
  
  void touchMetadata(RepositorySystemSession paramRepositorySystemSession, UpdateCheck<Metadata, MetadataTransferException> paramUpdateCheck);
}
