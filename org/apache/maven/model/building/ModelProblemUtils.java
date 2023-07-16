package org.apache.maven.model.building;

import java.io.File;
import org.apache.maven.model.Model;

public class ModelProblemUtils {
  static String toSourceHint(Model model) {
    if (model == null)
      return ""; 
    StringBuilder buffer = new StringBuilder(128);
    buffer.append(toId(model));
    File pomFile = model.getPomFile();
    if (pomFile != null)
      buffer.append(" (").append(pomFile).append(')'); 
    return buffer.toString();
  }
  
  static String toPath(Model model) {
    String path = "";
    if (model != null) {
      File pomFile = model.getPomFile();
      if (pomFile != null)
        path = pomFile.getAbsolutePath(); 
    } 
    return path;
  }
  
  static String toId(Model model) {
    if (model == null)
      return ""; 
    String groupId = model.getGroupId();
    if (groupId == null && model.getParent() != null)
      groupId = model.getParent().getGroupId(); 
    String artifactId = model.getArtifactId();
    String version = model.getVersion();
    if (version == null && model.getParent() != null)
      version = model.getParent().getVersion(); 
    if (version == null)
      version = "[unknown-version]"; 
    return toId(groupId, artifactId, version);
  }
  
  static String toId(String groupId, String artifactId, String version) {
    StringBuilder buffer = new StringBuilder(128);
    buffer.append((groupId != null && groupId.length() > 0) ? groupId : "[unknown-group-id]");
    buffer.append(':');
    buffer.append((artifactId != null && artifactId.length() > 0) ? artifactId : "[unknown-artifact-id]");
    buffer.append(':');
    buffer.append((version != null && version.length() > 0) ? version : "[unknown-version]");
    return buffer.toString();
  }
  
  public static String formatLocation(ModelProblem problem, String projectId) {
    StringBuilder buffer = new StringBuilder(256);
    if (!problem.getModelId().equals(projectId)) {
      buffer.append(problem.getModelId());
      if (problem.getSource().length() > 0) {
        if (buffer.length() > 0)
          buffer.append(", "); 
        buffer.append(problem.getSource());
      } 
    } 
    if (problem.getLineNumber() > 0) {
      if (buffer.length() > 0)
        buffer.append(", "); 
      buffer.append("line ").append(problem.getLineNumber());
    } 
    if (problem.getColumnNumber() > 0) {
      if (buffer.length() > 0)
        buffer.append(", "); 
      buffer.append("column ").append(problem.getColumnNumber());
    } 
    return buffer.toString();
  }
}
