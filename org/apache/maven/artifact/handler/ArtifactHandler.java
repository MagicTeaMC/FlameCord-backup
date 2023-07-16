package org.apache.maven.artifact.handler;

public interface ArtifactHandler {
  public static final String ROLE = ArtifactHandler.class.getName();
  
  String getExtension();
  
  String getDirectory();
  
  String getClassifier();
  
  String getPackaging();
  
  boolean isIncludesDependencies();
  
  String getLanguage();
  
  boolean isAddedToClasspath();
}
