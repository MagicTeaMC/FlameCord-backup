package org.eclipse.aether.util.artifact;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.artifact.AbstractArtifact;
import org.eclipse.aether.artifact.Artifact;

public abstract class DelegatingArtifact extends AbstractArtifact {
  private final Artifact delegate;
  
  protected DelegatingArtifact(Artifact delegate) {
    this.delegate = Objects.<Artifact>requireNonNull(delegate, "delegate artifact cannot be null");
  }
  
  protected abstract DelegatingArtifact newInstance(Artifact paramArtifact);
  
  public String getGroupId() {
    return this.delegate.getGroupId();
  }
  
  public String getArtifactId() {
    return this.delegate.getArtifactId();
  }
  
  public String getVersion() {
    return this.delegate.getVersion();
  }
  
  public Artifact setVersion(String version) {
    Artifact artifact = this.delegate.setVersion(version);
    if (artifact != this.delegate)
      return (Artifact)newInstance(artifact); 
    return (Artifact)this;
  }
  
  public String getBaseVersion() {
    return this.delegate.getBaseVersion();
  }
  
  public boolean isSnapshot() {
    return this.delegate.isSnapshot();
  }
  
  public String getClassifier() {
    return this.delegate.getClassifier();
  }
  
  public String getExtension() {
    return this.delegate.getExtension();
  }
  
  public File getFile() {
    return this.delegate.getFile();
  }
  
  public Artifact setFile(File file) {
    Artifact artifact = this.delegate.setFile(file);
    if (artifact != this.delegate)
      return (Artifact)newInstance(artifact); 
    return (Artifact)this;
  }
  
  public String getProperty(String key, String defaultValue) {
    return this.delegate.getProperty(key, defaultValue);
  }
  
  public Map<String, String> getProperties() {
    return this.delegate.getProperties();
  }
  
  public Artifact setProperties(Map<String, String> properties) {
    Artifact artifact = this.delegate.setProperties(properties);
    if (artifact != this.delegate)
      return (Artifact)newInstance(artifact); 
    return (Artifact)this;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (obj instanceof DelegatingArtifact)
      return this.delegate.equals(((DelegatingArtifact)obj).delegate); 
    return this.delegate.equals(obj);
  }
  
  public int hashCode() {
    return this.delegate.hashCode();
  }
  
  public String toString() {
    return this.delegate.toString();
  }
}
