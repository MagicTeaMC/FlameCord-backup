package org.codehaus.plexus.interpolation.object;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ObjectInterpolationWarning {
  private final String message;
  
  private Throwable cause;
  
  private final String path;
  
  public ObjectInterpolationWarning(String path, String message) {
    this.path = path;
    this.message = message;
  }
  
  public ObjectInterpolationWarning(String path, String message, Throwable cause) {
    this.path = path;
    this.message = message;
    this.cause = cause;
  }
  
  public String getPath() {
    return this.path;
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
  
  public String toString() {
    if (this.cause == null)
      return this.path + ": " + this.message; 
    StringWriter w = new StringWriter();
    PrintWriter pw = new PrintWriter(w);
    pw.print(this.path);
    pw.print(": ");
    pw.println(this.message);
    pw.println("Cause: ");
    this.cause.printStackTrace(pw);
    return w.toString();
  }
}
