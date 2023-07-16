package org.eclipse.aether.impl;

import java.util.Collection;
import java.util.List;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

public interface ArtifactResolver {
  ArtifactResult resolveArtifact(RepositorySystemSession paramRepositorySystemSession, ArtifactRequest paramArtifactRequest) throws ArtifactResolutionException;
  
  List<ArtifactResult> resolveArtifacts(RepositorySystemSession paramRepositorySystemSession, Collection<? extends ArtifactRequest> paramCollection) throws ArtifactResolutionException;
}
