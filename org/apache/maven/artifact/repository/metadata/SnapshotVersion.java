package org.apache.maven.artifact.repository.metadata;

import java.io.Serializable;

public class SnapshotVersion implements Serializable, Cloneable {
  private String classifier = "";
  
  private String extension;
  
  private String version;
  
  private String updated;
  
  public SnapshotVersion clone() {
    try {
      SnapshotVersion copy = (SnapshotVersion)super.clone();
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public boolean equals(Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof SnapshotVersion))
      return false; 
    SnapshotVersion that = (SnapshotVersion)other;
    boolean result = true;
    result = (result && ((getClassifier() == null) ? (that.getClassifier() == null) : getClassifier().equals(that.getClassifier())));
    result = (result && ((getExtension() == null) ? (that.getExtension() == null) : getExtension().equals(that.getExtension())));
    result = (result && ((getVersion() == null) ? (that.getVersion() == null) : getVersion().equals(that.getVersion())));
    result = (result && ((getUpdated() == null) ? (that.getUpdated() == null) : getUpdated().equals(that.getUpdated())));
    return result;
  }
  
  public String getClassifier() {
    return this.classifier;
  }
  
  public String getExtension() {
    return this.extension;
  }
  
  public String getUpdated() {
    return this.updated;
  }
  
  public String getVersion() {
    return this.version;
  }
  
  public int hashCode() {
    int result = 17;
    result = 37 * result + ((this.classifier != null) ? this.classifier.hashCode() : 0);
    result = 37 * result + ((this.extension != null) ? this.extension.hashCode() : 0);
    result = 37 * result + ((this.version != null) ? this.version.hashCode() : 0);
    result = 37 * result + ((this.updated != null) ? this.updated.hashCode() : 0);
    return result;
  }
  
  public void setClassifier(String classifier) {
    this.classifier = classifier;
  }
  
  public void setExtension(String extension) {
    this.extension = extension;
  }
  
  public void setUpdated(String updated) {
    this.updated = updated;
  }
  
  public void setVersion(String version) {
    this.version = version;
  }
  
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("classifier = '");
    buf.append(getClassifier());
    buf.append("'");
    buf.append("\n");
    buf.append("extension = '");
    buf.append(getExtension());
    buf.append("'");
    buf.append("\n");
    buf.append("version = '");
    buf.append(getVersion());
    buf.append("'");
    buf.append("\n");
    buf.append("updated = '");
    buf.append(getUpdated());
    buf.append("'");
    return buf.toString();
  }
}
