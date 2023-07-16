package org.codehaus.plexus.interpolation;

public class InterpolationException extends Exception {
  private static final long serialVersionUID = 1L;
  
  private final String expression;
  
  public InterpolationException(String message, String expression, Throwable cause) {
    super(buildMessage(message, expression), cause);
    this.expression = expression;
  }
  
  public InterpolationException(String message, String expression) {
    super(buildMessage(message, expression));
    this.expression = expression;
  }
  
  private static String buildMessage(String message, String expression) {
    return "Resolving expression: '" + expression + "': " + message;
  }
  
  public String getExpression() {
    return this.expression;
  }
}
