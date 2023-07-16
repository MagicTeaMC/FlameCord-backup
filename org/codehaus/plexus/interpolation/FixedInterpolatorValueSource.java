package org.codehaus.plexus.interpolation;

import java.util.List;
import org.codehaus.plexus.interpolation.fixed.FixedStringSearchInterpolator;
import org.codehaus.plexus.interpolation.fixed.InterpolationState;

public class FixedInterpolatorValueSource implements ValueSource {
  private final FixedStringSearchInterpolator fixedStringSearchInterpolator;
  
  private final InterpolationState errorCollector = new InterpolationState();
  
  public FixedInterpolatorValueSource(FixedStringSearchInterpolator fixedStringSearchInterpolator) {
    this.fixedStringSearchInterpolator = fixedStringSearchInterpolator;
  }
  
  public Object getValue(String expression) {
    return this.fixedStringSearchInterpolator.getValue(expression, this.errorCollector);
  }
  
  public List getFeedback() {
    return this.errorCollector.asList();
  }
  
  public void clearFeedback() {
    this.errorCollector.clear();
  }
}
