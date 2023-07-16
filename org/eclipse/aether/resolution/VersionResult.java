package org.eclipse.aether.resolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.eclipse.aether.repository.ArtifactRepository;

public final class VersionResult {
  private final VersionRequest request;
  
  private List<Exception> exceptions;
  
  private String version;
  
  private ArtifactRepository repository;
  
  public VersionResult(VersionRequest request) {
    this.request = Objects.<VersionRequest>requireNonNull(request, "version request cannot be null");
    this.exceptions = Collections.emptyList();
  }
  
  public VersionRequest getRequest() {
    return this.request;
  }
  
  public List<Exception> getExceptions() {
    return this.exceptions;
  }
  
  public VersionResult addException(Exception exception) {
    if (exception != null) {
      if (this.exceptions.isEmpty())
        this.exceptions = new ArrayList<>(); 
      this.exceptions.add(exception);
    } 
    return this;
  }
  
  public String getVersion() {
    return this.version;
  }
  
  public VersionResult setVersion(String version) {
    this.version = version;
    return this;
  }
  
  public ArtifactRepository getRepository() {
    return this.repository;
  }
  
  public VersionResult setRepository(ArtifactRepository repository) {
    this.repository = repository;
    return this;
  }
  
  public String toString() {
    return getVersion() + " @ " + getRepository();
  }
}
