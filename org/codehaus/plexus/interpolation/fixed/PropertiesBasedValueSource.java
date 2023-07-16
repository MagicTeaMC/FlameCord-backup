package org.codehaus.plexus.interpolation.fixed;

import java.util.Properties;

public class PropertiesBasedValueSource implements FixedValueSource {
  private final Properties properties;
  
  public PropertiesBasedValueSource(Properties properties) {
    this.properties = properties;
  }
  
  public Object getValue(String expression, InterpolationState interpolationState) {
    return (this.properties == null) ? null : this.properties.getProperty(expression);
  }
}
