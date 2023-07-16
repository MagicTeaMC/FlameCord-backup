package org.eclipse.aether.artifact;

import java.io.File;
import java.util.Map;

public interface Artifact {
  String getGroupId();
  
  String getArtifactId();
  
  String getVersion();
  
  Artifact setVersion(String paramString);
  
  String getBaseVersion();
  
  boolean isSnapshot();
  
  String getClassifier();
  
  String getExtension();
  
  File getFile();
  
  Artifact setFile(File paramFile);
  
  String getProperty(String paramString1, String paramString2);
  
  Map<String, String> getProperties();
  
  Artifact setProperties(Map<String, String> paramMap);
}
