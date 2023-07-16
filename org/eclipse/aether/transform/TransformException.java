package org.eclipse.aether.transform;

public class TransformException extends Exception {
  public TransformException() {
    super("Transformation failed");
  }
  
  public TransformException(String message) {
    super(message);
  }
  
  public TransformException(Throwable cause) {
    super(cause);
  }
  
  public TransformException(String message, Throwable cause) {
    super(message, cause);
  }
}
