package org.codehaus.plexus.interpolation.fixed;

import org.codehaus.plexus.interpolation.reflection.ReflectionValueExtractor;

public class ObjectBasedValueSource implements FixedValueSource {
  private final Object root;
  
  public ObjectBasedValueSource(Object root) {
    this.root = root;
  }
  
  public Object getValue(String expression, InterpolationState interpolationState) {
    if (expression == null || expression.trim().length() < 1)
      return null; 
    try {
      return ReflectionValueExtractor.evaluate(expression, this.root, false);
    } catch (Exception e) {
      interpolationState.addFeedback("Failed to extract '" + expression + "' from: " + this.root, e);
      return null;
    } 
  }
}
