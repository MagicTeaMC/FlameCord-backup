package org.apache.maven.building;

public interface Problem {
  String getSource();
  
  int getLineNumber();
  
  int getColumnNumber();
  
  String getLocation();
  
  Exception getException();
  
  String getMessage();
  
  Severity getSeverity();
  
  public enum Severity {
    FATAL, ERROR, WARNING;
  }
}
