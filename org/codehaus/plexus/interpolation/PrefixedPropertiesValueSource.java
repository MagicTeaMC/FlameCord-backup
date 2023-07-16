package org.codehaus.plexus.interpolation;

import java.util.List;
import java.util.Properties;

public class PrefixedPropertiesValueSource extends AbstractDelegatingValueSource implements QueryEnabledValueSource {
  public PrefixedPropertiesValueSource(String prefix, Properties properties) {
    super(new PrefixedValueSourceWrapper(new PropertiesBasedValueSource(properties), prefix));
  }
  
  public PrefixedPropertiesValueSource(List<String> possiblePrefixes, Properties properties, boolean allowUnprefixedExpressions) {
    super(new PrefixedValueSourceWrapper(new PropertiesBasedValueSource(properties), possiblePrefixes, allowUnprefixedExpressions));
  }
  
  public String getLastExpression() {
    return ((QueryEnabledValueSource)getDelegate()).getLastExpression();
  }
}
