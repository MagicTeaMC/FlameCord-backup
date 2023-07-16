package org.apache.maven.model.resolution;

public class UnresolvableModelException extends Exception {
  private final String groupId;
  
  private final String artifactId;
  
  private final String version;
  
  public UnresolvableModelException(String message, String groupId, String artifactId, String version, Throwable cause) {
    super(message, cause);
    this.groupId = (groupId != null) ? groupId : "";
    this.artifactId = (artifactId != null) ? artifactId : "";
    this.version = (version != null) ? version : "";
  }
  
  public UnresolvableModelException(String message, String groupId, String artifactId, String version) {
    super(message);
    this.groupId = (groupId != null) ? groupId : "";
    this.artifactId = (artifactId != null) ? artifactId : "";
    this.version = (version != null) ? version : "";
  }
  
  public UnresolvableModelException(Throwable cause, String groupId, String artifactId, String version) {
    super(cause);
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
  }
  
  public String getGroupId() {
    return this.groupId;
  }
  
  public String getArtifactId() {
    return this.artifactId;
  }
  
  public String getVersion() {
    return this.version;
  }
}
