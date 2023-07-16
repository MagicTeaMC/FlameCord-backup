package org.apache.maven.repository.internal;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.artifact.AbstractArtifact;
import org.eclipse.aether.artifact.Artifact;

public final class RelocatedArtifact extends AbstractArtifact {
  private final Artifact artifact;
  
  private final String groupId;
  
  private final String artifactId;
  
  private final String version;
  
  private final String message;
  
  RelocatedArtifact(Artifact artifact, String groupId, String artifactId, String version, String message) {
    this.artifact = Objects.<Artifact>requireNonNull(artifact, "artifact cannot be null");
    this.groupId = (groupId != null && groupId.length() > 0) ? groupId : null;
    this.artifactId = (artifactId != null && artifactId.length() > 0) ? artifactId : null;
    this.version = (version != null && version.length() > 0) ? version : null;
    this.message = (message != null && message.length() > 0) ? message : null;
  }
  
  public String getGroupId() {
    if (this.groupId != null)
      return this.groupId; 
    return this.artifact.getGroupId();
  }
  
  public String getArtifactId() {
    if (this.artifactId != null)
      return this.artifactId; 
    return this.artifact.getArtifactId();
  }
  
  public String getVersion() {
    if (this.version != null)
      return this.version; 
    return this.artifact.getVersion();
  }
  
  public Artifact setVersion(String version) {
    String current = getVersion();
    if (current.equals(version) || (version == null && current.length() <= 0))
      return (Artifact)this; 
    return (Artifact)new RelocatedArtifact(this.artifact, this.groupId, this.artifactId, version, this.message);
  }
  
  public Artifact setFile(File file) {
    File current = getFile();
    if (Objects.equals(current, file))
      return (Artifact)this; 
    return (Artifact)new RelocatedArtifact(this.artifact.setFile(file), this.groupId, this.artifactId, this.version, this.message);
  }
  
  public Artifact setProperties(Map<String, String> properties) {
    Map<String, String> current = getProperties();
    if (current.equals(properties) || (properties == null && current.isEmpty()))
      return (Artifact)this; 
    return (Artifact)new RelocatedArtifact(this.artifact.setProperties(properties), this.groupId, this.artifactId, this.version, this.message);
  }
  
  public String getClassifier() {
    return this.artifact.getClassifier();
  }
  
  public String getExtension() {
    return this.artifact.getExtension();
  }
  
  public File getFile() {
    return this.artifact.getFile();
  }
  
  public String getProperty(String key, String defaultValue) {
    return this.artifact.getProperty(key, defaultValue);
  }
  
  public Map<String, String> getProperties() {
    return this.artifact.getProperties();
  }
  
  public String getMessage() {
    return this.message;
  }
}
