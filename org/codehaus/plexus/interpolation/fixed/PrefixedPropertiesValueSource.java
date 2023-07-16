package org.codehaus.plexus.interpolation.fixed;

import java.util.List;
import java.util.Properties;

public class PrefixedPropertiesValueSource extends AbstractDelegatingValueSource {
  public PrefixedPropertiesValueSource(String prefix, Properties properties) {
    super(new PrefixedValueSourceWrapper(new PropertiesBasedValueSource(properties), prefix));
  }
  
  public PrefixedPropertiesValueSource(List<String> possiblePrefixes, Properties properties, boolean allowUnprefixedExpressions) {
    super(new PrefixedValueSourceWrapper(new PropertiesBasedValueSource(properties), possiblePrefixes, allowUnprefixedExpressions));
  }
}
