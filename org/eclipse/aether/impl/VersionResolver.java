package org.eclipse.aether.impl;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.resolution.VersionRequest;
import org.eclipse.aether.resolution.VersionResolutionException;
import org.eclipse.aether.resolution.VersionResult;

public interface VersionResolver {
  VersionResult resolveVersion(RepositorySystemSession paramRepositorySystemSession, VersionRequest paramVersionRequest) throws VersionResolutionException;
}
