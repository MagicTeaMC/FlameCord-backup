package org.codehaus.plexus.interpolation;

import java.util.Properties;

public class PropertiesBasedValueSource extends AbstractValueSource {
  private final Properties properties;
  
  public PropertiesBasedValueSource(Properties properties) {
    super(false);
    this.properties = properties;
  }
  
  public Object getValue(String expression) {
    return (this.properties == null) ? null : this.properties.getProperty(expression);
  }
}
