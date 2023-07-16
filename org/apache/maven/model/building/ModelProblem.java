package org.apache.maven.model.building;

public interface ModelProblem {
  String getSource();
  
  int getLineNumber();
  
  int getColumnNumber();
  
  String getModelId();
  
  Exception getException();
  
  String getMessage();
  
  Severity getSeverity();
  
  Version getVersion();
  
  public enum Severity {
    FATAL, ERROR, WARNING;
  }
  
  public enum Version {
    BASE, V20, V30, V31;
  }
}
