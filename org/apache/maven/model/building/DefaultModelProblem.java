package org.apache.maven.model.building;

import org.apache.maven.model.Model;

public class DefaultModelProblem implements ModelProblem {
  private final String source;
  
  private final int lineNumber;
  
  private final int columnNumber;
  
  private final String modelId;
  
  private final String message;
  
  private final Exception exception;
  
  private final ModelProblem.Severity severity;
  
  private final ModelProblem.Version version;
  
  public DefaultModelProblem(String message, ModelProblem.Severity severity, ModelProblem.Version version, Model source, int lineNumber, int columnNumber, Exception exception) {
    this(message, severity, version, ModelProblemUtils.toPath(source), lineNumber, columnNumber, 
        ModelProblemUtils.toId(source), exception);
  }
  
  public DefaultModelProblem(String message, ModelProblem.Severity severity, ModelProblem.Version version, String source, int lineNumber, int columnNumber, String modelId, Exception exception) {
    this.message = message;
    this.severity = (severity != null) ? severity : ModelProblem.Severity.ERROR;
    this.source = (source != null) ? source : "";
    this.lineNumber = lineNumber;
    this.columnNumber = columnNumber;
    this.modelId = (modelId != null) ? modelId : "";
    this.exception = exception;
    this.version = version;
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
  
  public String getModelId() {
    return this.modelId;
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
  
  public ModelProblem.Severity getSeverity() {
    return this.severity;
  }
  
  public ModelProblem.Version getVersion() {
    return this.version;
  }
  
  public String toString() {
    StringBuilder buffer = new StringBuilder(128);
    buffer.append('[').append(getSeverity()).append("] ");
    buffer.append(getMessage());
    buffer.append(" @ ").append(ModelProblemUtils.formatLocation(this, null));
    return buffer.toString();
  }
}
