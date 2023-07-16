package org.eclipse.aether.impl;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;

public interface ArtifactDescriptorReader {
  ArtifactDescriptorResult readArtifactDescriptor(RepositorySystemSession paramRepositorySystemSession, ArtifactDescriptorRequest paramArtifactDescriptorRequest) throws ArtifactDescriptorException;
}
