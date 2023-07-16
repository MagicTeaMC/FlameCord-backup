package org.eclipse.aether.metadata;

import java.io.File;
import java.util.Map;

public interface Metadata {
  String getGroupId();
  
  String getArtifactId();
  
  String getVersion();
  
  String getType();
  
  Nature getNature();
  
  File getFile();
  
  Metadata setFile(File paramFile);
  
  String getProperty(String paramString1, String paramString2);
  
  Map<String, String> getProperties();
  
  Metadata setProperties(Map<String, String> paramMap);
  
  public enum Nature {
    RELEASE, SNAPSHOT, RELEASE_OR_SNAPSHOT;
  }
}
