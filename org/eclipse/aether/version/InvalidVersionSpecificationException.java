package org.eclipse.aether.version;

import org.eclipse.aether.RepositoryException;

public class InvalidVersionSpecificationException extends RepositoryException {
  private final String version;
  
  public InvalidVersionSpecificationException(String version, String message) {
    super(message);
    this.version = version;
  }
  
  public InvalidVersionSpecificationException(String version, Throwable cause) {
    super("Could not parse version specification " + version + getMessage(": ", cause), cause);
    this.version = version;
  }
  
  public InvalidVersionSpecificationException(String version, String message, Throwable cause) {
    super(message, cause);
    this.version = version;
  }
  
  public String getVersion() {
    return this.version;
  }
}
