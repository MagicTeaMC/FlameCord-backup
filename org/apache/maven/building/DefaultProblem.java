package org.apache.maven.building;

class DefaultProblem implements Problem {
  private final String source;
  
  private final int lineNumber;
  
  private final int columnNumber;
  
  private final String message;
  
  private final Exception exception;
  
  private final Problem.Severity severity;
  
  DefaultProblem(String message, Problem.Severity severity, String source, int lineNumber, int columnNumber, Exception exception) {
    this.message = message;
    this.severity = (severity != null) ? severity : Problem.Severity.ERROR;
    this.source = (source != null) ? source : "";
    this.lineNumber = lineNumber;
    this.columnNumber = columnNumber;
    this.exception = exception;
  }
  
  public String getSource() {
    return this.source;
  }
  
  public int getLineNumber() {
    return this.lineNumber;
  }
  
  public int getColumnNumber() {
    return this.columnNumber;
  }
  
  public String getLocation() {
    StringBuilder buffer = new StringBuilder(256);
    if (getSource().length() > 0) {
      if (buffer.length() > 0)
        buffer.append(", "); 
      buffer.append(getSource());
    } 
    if (getLineNumber() > 0) {
      if (buffer.length() > 0)
        buffer.append(", "); 
      buffer.append("line ").append(getLineNumber());
    } 
    if (getColumnNumber() > 0) {
      if (buffer.length() > 0)
        buffer.append(", "); 
      buffer.append("column ").append(getColumnNumber());
    } 
    return buffer.toString();
  }
  
  public Exception getException() {
    return this.exception;
  }
  
  public String getMessage() {
    String msg;
    if (this.message != null && this.message.length() > 0) {
      msg = this.message;
    } else {
      msg = this.exception.getMessage();
      if (msg == null)
        msg = ""; 
    } 
    return msg;
  }
  
  public Problem.Severity getSeverity() {
    return this.severity;
  }
  
  public String toString() {
    StringBuilder buffer = new StringBuilder(128);
    buffer.append('[').append(getSeverity()).append("] ");
    buffer.append(getMessage());
    buffer.append(" @ ").append(getLocation());
    return buffer.toString();
  }
}
