package org.apache.maven.model.building;

import java.util.Objects;
import org.apache.maven.model.InputLocation;

public final class ModelProblemCollectorRequest {
  private final ModelProblem.Severity severity;
  
  private final ModelProblem.Version version;
  
  private Exception exception;
  
  private String message;
  
  private InputLocation location;
  
  public ModelProblemCollectorRequest(ModelProblem.Severity severity, ModelProblem.Version version) {
    this.severity = Objects.<ModelProblem.Severity>requireNonNull(severity, "severity cannot be null");
    this.version = Objects.<ModelProblem.Version>requireNonNull(version, "version cannot be null");
  }
  
  public ModelProblem.Severity getSeverity() {
    return this.severity;
  }
  
  public ModelProblem.Version getVersion() {
    return this.version;
  }
  
  public Exception getException() {
    return this.exception;
  }
  
  public ModelProblemCollectorRequest setException(Exception exception) {
    this.exception = exception;
    return this;
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public ModelProblemCollectorRequest setMessage(String message) {
    this.message = message;
    return this;
  }
  
  public InputLocation getLocation() {
    return this.location;
  }
  
  public ModelProblemCollectorRequest setLocation(InputLocation location) {
    this.location = location;
    return this;
  }
}
