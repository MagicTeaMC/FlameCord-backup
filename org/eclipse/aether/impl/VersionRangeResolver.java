package org.eclipse.aether.impl;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;

public interface VersionRangeResolver {
  VersionRangeResult resolveVersionRange(RepositorySystemSession paramRepositorySystemSession, VersionRangeRequest paramVersionRangeRequest) throws VersionRangeResolutionException;
}
