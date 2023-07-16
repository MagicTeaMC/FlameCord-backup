package org.eclipse.aether.resolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionConstraint;

public final class VersionRangeResult {
  private final VersionRangeRequest request;
  
  private List<Exception> exceptions;
  
  private List<Version> versions;
  
  private Map<Version, ArtifactRepository> repositories;
  
  private VersionConstraint versionConstraint;
  
  public VersionRangeResult(VersionRangeRequest request) {
    this.request = Objects.<VersionRangeRequest>requireNonNull(request, "version range request cannot be null");
    this.exceptions = Collections.emptyList();
    this.versions = Collections.emptyList();
    this.repositories = Collections.emptyMap();
  }
  
  public VersionRangeRequest getRequest() {
    return this.request;
  }
  
  public List<Exception> getExceptions() {
    return this.exceptions;
  }
  
  public VersionRangeResult addException(Exception exception) {
    if (exception != null) {
      if (this.exceptions.isEmpty())
        this.exceptions = new ArrayList<>(); 
      this.exceptions.add(exception);
    } 
    return this;
  }
  
  public List<Version> getVersions() {
    return this.versions;
  }
  
  public VersionRangeResult addVersion(Version version) {
    if (this.versions.isEmpty())
      this.versions = new ArrayList<>(); 
    this.versions.add(version);
    return this;
  }
  
  public VersionRangeResult setVersions(List<Version> versions) {
    if (versions == null) {
      this.versions = Collections.emptyList();
    } else {
      this.versions = versions;
    } 
    return this;
  }
  
  public Version getLowestVersion() {
    if (this.versions.isEmpty())
      return null; 
    return this.versions.get(0);
  }
  
  public Version getHighestVersion() {
    if (this.versions.isEmpty())
      return null; 
    return this.versions.get(this.versions.size() - 1);
  }
  
  public ArtifactRepository getRepository(Version version) {
    return this.repositories.get(version);
  }
  
  public VersionRangeResult setRepository(Version version, ArtifactRepository repository) {
    if (repository != null) {
      if (this.repositories.isEmpty())
        this.repositories = new HashMap<>(); 
      this.repositories.put(version, repository);
    } 
    return this;
  }
  
  public VersionConstraint getVersionConstraint() {
    return this.versionConstraint;
  }
  
  public VersionRangeResult setVersionConstraint(VersionConstraint versionConstraint) {
    this.versionConstraint = versionConstraint;
    return this;
  }
  
  public String toString() {
    return String.valueOf(this.repositories);
  }
}
