package org.eclipse.aether.transfer;

import org.eclipse.aether.RepositoryException;

public class ChecksumFailureException extends RepositoryException {
  private final String expected;
  
  private final String actual;
  
  private final boolean retryWorthy;
  
  public ChecksumFailureException(String expected, String actual) {
    super("Checksum validation failed, expected " + expected + " but is " + actual);
    this.expected = expected;
    this.actual = actual;
    this.retryWorthy = true;
  }
  
  public ChecksumFailureException(String message) {
    this(false, message, null);
  }
  
  public ChecksumFailureException(Throwable cause) {
    this("Checksum validation failed" + getMessage(": ", cause), cause);
  }
  
  public ChecksumFailureException(String message, Throwable cause) {
    this(false, message, cause);
  }
  
  public ChecksumFailureException(boolean retryWorthy, String message, Throwable cause) {
    super(message, cause);
    this.expected = "";
    this.actual = "";
    this.retryWorthy = retryWorthy;
  }
  
  public String getExpected() {
    return this.expected;
  }
  
  public String getActual() {
    return this.actual;
  }
  
  public boolean isRetryWorthy() {
    return this.retryWorthy;
  }
}
