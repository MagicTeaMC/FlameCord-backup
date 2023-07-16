package org.eclipse.aether.repository;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.aether.metadata.Metadata;

public final class LocalMetadataRegistration {
  private Metadata metadata;
  
  private RemoteRepository repository;
  
  private Collection<String> contexts = Collections.emptyList();
  
  public LocalMetadataRegistration() {}
  
  public LocalMetadataRegistration(Metadata metadata) {
    setMetadata(metadata);
  }
  
  public LocalMetadataRegistration(Metadata metadata, RemoteRepository repository, Collection<String> contexts) {
    setMetadata(metadata);
    setRepository(repository);
    setContexts(contexts);
  }
  
  public Metadata getMetadata() {
    return this.metadata;
  }
  
  public LocalMetadataRegistration setMetadata(Metadata metadata) {
    this.metadata = metadata;
    return this;
  }
  
  public RemoteRepository getRepository() {
    return this.repository;
  }
  
  public LocalMetadataRegistration setRepository(RemoteRepository repository) {
    this.repository = repository;
    return this;
  }
  
  public Collection<String> getContexts() {
    return this.contexts;
  }
  
  public LocalMetadataRegistration setContexts(Collection<String> contexts) {
    if (contexts != null) {
      this.contexts = contexts;
    } else {
      this.contexts = Collections.emptyList();
    } 
    return this;
  }
}
