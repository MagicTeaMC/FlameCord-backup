package org.eclipse.aether.spi.connector;

import java.io.File;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.transfer.ArtifactTransferException;

public abstract class ArtifactTransfer extends Transfer {
  private Artifact artifact;
  
  private File file;
  
  private ArtifactTransferException exception;
  
  public Artifact getArtifact() {
    return this.artifact;
  }
  
  public ArtifactTransfer setArtifact(Artifact artifact) {
    this.artifact = artifact;
    return this;
  }
  
  public File getFile() {
    return this.file;
  }
  
  public ArtifactTransfer setFile(File file) {
    this.file = file;
    return this;
  }
  
  public ArtifactTransferException getException() {
    return this.exception;
  }
  
  public ArtifactTransfer setException(ArtifactTransferException exception) {
    this.exception = exception;
    return this;
  }
}
