package org.eclipse.aether.graph;

public final class Exclusion {
  private final String groupId;
  
  private final String artifactId;
  
  private final String classifier;
  
  private final String extension;
  
  public Exclusion(String groupId, String artifactId, String classifier, String extension) {
    this.groupId = (groupId != null) ? groupId : "";
    this.artifactId = (artifactId != null) ? artifactId : "";
    this.classifier = (classifier != null) ? classifier : "";
    this.extension = (extension != null) ? extension : "";
  }
  
  public String getGroupId() {
    return this.groupId;
  }
  
  public String getArtifactId() {
    return this.artifactId;
  }
  
  public String getClassifier() {
    return this.classifier;
  }
  
  public String getExtension() {
    return this.extension;
  }
  
  public String toString() {
    return getGroupId() + ':' + getArtifactId() + ':' + getExtension() + (
      (getClassifier().length() > 0) ? (':' + getClassifier()) : "");
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (obj == null || !getClass().equals(obj.getClass()))
      return false; 
    Exclusion that = (Exclusion)obj;
    return (this.artifactId.equals(that.artifactId) && this.groupId.equals(that.groupId) && this.extension
      .equals(that.extension) && this.classifier.equals(that.classifier));
  }
  
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + this.artifactId.hashCode();
    hash = hash * 31 + this.groupId.hashCode();
    hash = hash * 31 + this.classifier.hashCode();
    hash = hash * 31 + this.extension.hashCode();
    return hash;
  }
}
