package org.eclipse.aether.util.artifact;

import java.util.Objects;
import org.eclipse.aether.artifact.Artifact;

public final class ArtifactIdUtils {
  private static final char SEP = ':';
  
  public static String toId(Artifact artifact) {
    String id = null;
    if (artifact != null)
      id = toId(artifact.getGroupId(), artifact.getArtifactId(), artifact.getExtension(), artifact
          .getClassifier(), artifact.getVersion()); 
    return id;
  }
  
  public static String toId(String groupId, String artifactId, String extension, String classifier, String version) {
    StringBuilder buffer = concat(groupId, artifactId, extension, classifier);
    buffer.append(':');
    if (version != null)
      buffer.append(version); 
    return buffer.toString();
  }
  
  public static String toBaseId(Artifact artifact) {
    String id = null;
    if (artifact != null)
      id = toId(artifact.getGroupId(), artifact.getArtifactId(), artifact.getExtension(), artifact
          .getClassifier(), artifact.getBaseVersion()); 
    return id;
  }
  
  public static String toVersionlessId(Artifact artifact) {
    String id = null;
    if (artifact != null)
      id = toVersionlessId(artifact.getGroupId(), artifact.getArtifactId(), artifact.getExtension(), artifact
          .getClassifier()); 
    return id;
  }
  
  public static String toVersionlessId(String groupId, String artifactId, String extension, String classifier) {
    return concat(groupId, artifactId, extension, classifier).toString();
  }
  
  private static StringBuilder concat(String groupId, String artifactId, String extension, String classifier) {
    StringBuilder buffer = new StringBuilder(128);
    if (groupId != null)
      buffer.append(groupId); 
    buffer.append(':');
    if (artifactId != null)
      buffer.append(artifactId); 
    buffer.append(':');
    if (extension != null)
      buffer.append(extension); 
    if (classifier != null && classifier.length() > 0)
      buffer.append(':').append(classifier); 
    return buffer;
  }
  
  public static boolean equalsId(Artifact artifact1, Artifact artifact2) {
    if (artifact1 == null || artifact2 == null)
      return false; 
    if (!Objects.equals(artifact1.getArtifactId(), artifact2.getArtifactId()))
      return false; 
    if (!Objects.equals(artifact1.getGroupId(), artifact2.getGroupId()))
      return false; 
    if (!Objects.equals(artifact1.getExtension(), artifact2.getExtension()))
      return false; 
    if (!Objects.equals(artifact1.getClassifier(), artifact2.getClassifier()))
      return false; 
    if (!Objects.equals(artifact1.getVersion(), artifact2.getVersion()))
      return false; 
    return true;
  }
  
  public static boolean equalsBaseId(Artifact artifact1, Artifact artifact2) {
    if (artifact1 == null || artifact2 == null)
      return false; 
    if (!Objects.equals(artifact1.getArtifactId(), artifact2.getArtifactId()))
      return false; 
    if (!Objects.equals(artifact1.getGroupId(), artifact2.getGroupId()))
      return false; 
    if (!Objects.equals(artifact1.getExtension(), artifact2.getExtension()))
      return false; 
    if (!Objects.equals(artifact1.getClassifier(), artifact2.getClassifier()))
      return false; 
    if (!Objects.equals(artifact1.getBaseVersion(), artifact2.getBaseVersion()))
      return false; 
    return true;
  }
  
  public static boolean equalsVersionlessId(Artifact artifact1, Artifact artifact2) {
    if (artifact1 == null || artifact2 == null)
      return false; 
    if (!Objects.equals(artifact1.getArtifactId(), artifact2.getArtifactId()))
      return false; 
    if (!Objects.equals(artifact1.getGroupId(), artifact2.getGroupId()))
      return false; 
    if (!Objects.equals(artifact1.getExtension(), artifact2.getExtension()))
      return false; 
    if (!Objects.equals(artifact1.getClassifier(), artifact2.getClassifier()))
      return false; 
    return true;
  }
}
