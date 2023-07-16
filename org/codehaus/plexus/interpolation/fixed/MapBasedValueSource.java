package org.codehaus.plexus.interpolation.fixed;

import java.util.Map;

public class MapBasedValueSource implements FixedValueSource {
  private final Map values;
  
  public MapBasedValueSource(Map values) {
    this.values = values;
  }
  
  public Object getValue(String expression, InterpolationState interpolationState) {
    return (this.values == null) ? null : this.values.get(expression);
  }
}
