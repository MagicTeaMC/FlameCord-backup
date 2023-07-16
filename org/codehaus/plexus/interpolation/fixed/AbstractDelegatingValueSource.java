package org.codehaus.plexus.interpolation.fixed;

public abstract class AbstractDelegatingValueSource implements FixedValueSource {
  private final FixedValueSource delegate;
  
  protected AbstractDelegatingValueSource(FixedValueSource delegate) {
    if (delegate == null)
      throw new NullPointerException("Delegate ValueSource cannot be null."); 
    this.delegate = delegate;
  }
  
  protected FixedValueSource getDelegate() {
    return this.delegate;
  }
  
  public Object getValue(String expression, InterpolationState interpolationState) {
    return getDelegate().getValue(expression, interpolationState);
  }
}
