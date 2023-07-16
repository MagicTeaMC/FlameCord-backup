package org.eclipse.aether;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.ArtifactRepository;

public final class RepositoryEvent {
  private final EventType type;
  
  private final RepositorySystemSession session;
  
  private final Artifact artifact;
  
  private final Metadata metadata;
  
  private final ArtifactRepository repository;
  
  private final File file;
  
  private final List<Exception> exceptions;
  
  private final RequestTrace trace;
  
  public enum EventType {
    ARTIFACT_DESCRIPTOR_INVALID, ARTIFACT_DESCRIPTOR_MISSING, METADATA_INVALID, ARTIFACT_RESOLVING, ARTIFACT_RESOLVED, METADATA_RESOLVING, METADATA_RESOLVED, ARTIFACT_DOWNLOADING, ARTIFACT_DOWNLOADED, METADATA_DOWNLOADING, METADATA_DOWNLOADED, ARTIFACT_INSTALLING, ARTIFACT_INSTALLED, METADATA_INSTALLING, METADATA_INSTALLED, ARTIFACT_DEPLOYING, ARTIFACT_DEPLOYED, METADATA_DEPLOYING, METADATA_DEPLOYED;
  }
  
  RepositoryEvent(Builder builder) {
    this.type = builder.type;
    this.session = builder.session;
    this.artifact = builder.artifact;
    this.metadata = builder.metadata;
    this.repository = builder.repository;
    this.file = builder.file;
    this.exceptions = builder.exceptions;
    this.trace = builder.trace;
  }
  
  public EventType getType() {
    return this.type;
  }
  
  public RepositorySystemSession getSession() {
    return this.session;
  }
  
  public Artifact getArtifact() {
    return this.artifact;
  }
  
  public Metadata getMetadata() {
    return this.metadata;
  }
  
  public File getFile() {
    return this.file;
  }
  
  public ArtifactRepository getRepository() {
    return this.repository;
  }
  
  public Exception getException() {
    return this.exceptions.isEmpty() ? null : this.exceptions.get(0);
  }
  
  public List<Exception> getExceptions() {
    return this.exceptions;
  }
  
  public RequestTrace getTrace() {
    return this.trace;
  }
  
  public String toString() {
    StringBuilder buffer = new StringBuilder(256);
    buffer.append(getType());
    if (getArtifact() != null)
      buffer.append(" ").append(getArtifact()); 
    if (getMetadata() != null)
      buffer.append(" ").append(getMetadata()); 
    if (getFile() != null)
      buffer.append(" (").append(getFile()).append(")"); 
    if (getRepository() != null)
      buffer.append(" @ ").append(getRepository()); 
    return buffer.toString();
  }
  
  public static final class Builder {
    RepositoryEvent.EventType type;
    
    RepositorySystemSession session;
    
    Artifact artifact;
    
    Metadata metadata;
    
    ArtifactRepository repository;
    
    File file;
    
    List<Exception> exceptions = Collections.emptyList();
    
    RequestTrace trace;
    
    public Builder(RepositorySystemSession session, RepositoryEvent.EventType type) {
      this.session = Objects.<RepositorySystemSession>requireNonNull(session, "session cannot be null");
      this.type = Objects.<RepositoryEvent.EventType>requireNonNull(type, "event type cannot be null");
    }
    
    public Builder setArtifact(Artifact artifact) {
      this.artifact = artifact;
      return this;
    }
    
    public Builder setMetadata(Metadata metadata) {
      this.metadata = metadata;
      return this;
    }
    
    public Builder setRepository(ArtifactRepository repository) {
      this.repository = repository;
      return this;
    }
    
    public Builder setFile(File file) {
      this.file = file;
      return this;
    }
    
    public Builder setException(Exception exception) {
      if (exception != null) {
        this.exceptions = Collections.singletonList(exception);
      } else {
        this.exceptions = Collections.emptyList();
      } 
      return this;
    }
    
    public Builder setExceptions(List<Exception> exceptions) {
      if (exceptions != null) {
        this.exceptions = exceptions;
      } else {
        this.exceptions = Collections.emptyList();
      } 
      return this;
    }
    
    public Builder setTrace(RequestTrace trace) {
      this.trace = trace;
      return this;
    }
    
    public RepositoryEvent build() {
      return new RepositoryEvent(this);
    }
  }
}
