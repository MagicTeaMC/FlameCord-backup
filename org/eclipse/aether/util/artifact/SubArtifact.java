package org.eclipse.aether.util.artifact;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.artifact.AbstractArtifact;
import org.eclipse.aether.artifact.Artifact;

public final class SubArtifact extends AbstractArtifact {
  private final Artifact mainArtifact;
  
  private final String classifier;
  
  private final String extension;
  
  private final File file;
  
  private final Map<String, String> properties;
  
  public SubArtifact(Artifact mainArtifact, String classifier, String extension) {
    this(mainArtifact, classifier, extension, (File)null);
  }
  
  public SubArtifact(Artifact mainArtifact, String classifier, String extension, File file) {
    this(mainArtifact, classifier, extension, (Map<String, String>)null, file);
  }
  
  public SubArtifact(Artifact mainArtifact, String classifier, String extension, Map<String, String> properties) {
    this(mainArtifact, classifier, extension, properties, (File)null);
  }
  
  public SubArtifact(Artifact mainArtifact, String classifier, String extension, Map<String, String> properties, File file) {
    this.mainArtifact = Objects.<Artifact>requireNonNull(mainArtifact, "main artifact cannot be null");
    this.classifier = classifier;
    this.extension = extension;
    this.file = file;
    this.properties = copyProperties(properties);
  }
  
  private SubArtifact(Artifact mainArtifact, String classifier, String extension, File file, Map<String, String> properties) {
    this.mainArtifact = mainArtifact;
    this.classifier = classifier;
    this.extension = extension;
    this.file = file;
    this.properties = properties;
  }
  
  public String getGroupId() {
    return this.mainArtifact.getGroupId();
  }
  
  public String getArtifactId() {
    return this.mainArtifact.getArtifactId();
  }
  
  public String getVersion() {
    return this.mainArtifact.getVersion();
  }
  
  public String getBaseVersion() {
    return this.mainArtifact.getBaseVersion();
  }
  
  public boolean isSnapshot() {
    return this.mainArtifact.isSnapshot();
  }
  
  public String getClassifier() {
    return expand(this.classifier, this.mainArtifact.getClassifier());
  }
  
  public String getExtension() {
    return expand(this.extension, this.mainArtifact.getExtension());
  }
  
  public File getFile() {
    return this.file;
  }
  
  public Artifact setFile(File file) {
    if (Objects.equals(this.file, file))
      return (Artifact)this; 
    return (Artifact)new SubArtifact(this.mainArtifact, this.classifier, this.extension, file, this.properties);
  }
  
  public Map<String, String> getProperties() {
    return this.properties;
  }
  
  public Artifact setProperties(Map<String, String> properties) {
    if (this.properties.equals(properties) || (properties == null && this.properties.isEmpty()))
      return (Artifact)this; 
    return (Artifact)new SubArtifact(this.mainArtifact, this.classifier, this.extension, properties, this.file);
  }
  
  private static String expand(String pattern, String replacement) {
    String result = "";
    if (pattern != null) {
      result = pattern.replace("*", replacement);
      if (replacement.length() <= 0) {
        if (pattern.startsWith("*")) {
          int i = 0;
          for (; i < result.length(); i++) {
            char c = result.charAt(i);
            if (c != '-' && c != '.')
              break; 
          } 
          result = result.substring(i);
        } 
        if (pattern.endsWith("*")) {
          int i = result.length() - 1;
          for (; i >= 0; i--) {
            char c = result.charAt(i);
            if (c != '-' && c != '.')
              break; 
          } 
          result = result.substring(0, i + 1);
        } 
      } 
    } 
    return result;
  }
}
