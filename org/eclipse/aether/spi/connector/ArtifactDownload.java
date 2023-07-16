package org.eclipse.aether.spi.connector;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.transfer.ArtifactTransferException;
import org.eclipse.aether.transfer.TransferListener;

public final class ArtifactDownload extends ArtifactTransfer {
  private boolean existenceCheck;
  
  private String checksumPolicy = "";
  
  private String context = "";
  
  private Collection<String> contexts;
  
  private List<RemoteRepository> repositories = Collections.emptyList();
  
  public ArtifactDownload(Artifact artifact, String context, File file, String checksumPolicy) {
    setArtifact(artifact);
    setRequestContext(context);
    setFile(file);
    setChecksumPolicy(checksumPolicy);
  }
  
  public ArtifactDownload setArtifact(Artifact artifact) {
    super.setArtifact(artifact);
    return this;
  }
  
  public ArtifactDownload setFile(File file) {
    super.setFile(file);
    return this;
  }
  
  public boolean isExistenceCheck() {
    return this.existenceCheck;
  }
  
  public ArtifactDownload setExistenceCheck(boolean existenceCheck) {
    this.existenceCheck = existenceCheck;
    return this;
  }
  
  public String getChecksumPolicy() {
    return this.checksumPolicy;
  }
  
  public ArtifactDownload setChecksumPolicy(String checksumPolicy) {
    this.checksumPolicy = (checksumPolicy != null) ? checksumPolicy : "";
    return this;
  }
  
  public String getRequestContext() {
    return this.context;
  }
  
  public ArtifactDownload setRequestContext(String context) {
    this.context = (context != null) ? context : "";
    return this;
  }
  
  public Collection<String> getSupportedContexts() {
    return (this.contexts != null) ? this.contexts : Collections.<String>singleton(this.context);
  }
  
  public ArtifactDownload setSupportedContexts(Collection<String> contexts) {
    if (contexts == null || contexts.isEmpty()) {
      this.contexts = Collections.singleton(this.context);
    } else {
      this.contexts = contexts;
    } 
    return this;
  }
  
  public List<RemoteRepository> getRepositories() {
    return this.repositories;
  }
  
  public ArtifactDownload setRepositories(List<RemoteRepository> repositories) {
    if (repositories == null) {
      this.repositories = Collections.emptyList();
    } else {
      this.repositories = repositories;
    } 
    return this;
  }
  
  public ArtifactDownload setException(ArtifactTransferException exception) {
    super.setException(exception);
    return this;
  }
  
  public ArtifactDownload setListener(TransferListener listener) {
    super.setListener(listener);
    return this;
  }
  
  public ArtifactDownload setTrace(RequestTrace trace) {
    super.setTrace(trace);
    return this;
  }
  
  public String toString() {
    return getArtifact() + " - " + (isExistenceCheck() ? "?" : "") + getFile();
  }
  
  public ArtifactDownload() {}
}
