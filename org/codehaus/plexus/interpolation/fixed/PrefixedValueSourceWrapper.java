package org.codehaus.plexus.interpolation.fixed;

import java.util.List;
import org.codehaus.plexus.interpolation.util.ValueSourceUtils;

public class PrefixedValueSourceWrapper implements FixedValueSource {
  private final FixedValueSource valueSource;
  
  private final String[] possiblePrefixes;
  
  private boolean allowUnprefixedExpressions;
  
  private String lastExpression;
  
  public PrefixedValueSourceWrapper(FixedValueSource valueSource, String prefix) {
    this.valueSource = valueSource;
    this.possiblePrefixes = new String[] { prefix };
  }
  
  public PrefixedValueSourceWrapper(FixedValueSource valueSource, String prefix, boolean allowUnprefixedExpressions) {
    this.valueSource = valueSource;
    this.possiblePrefixes = new String[] { prefix };
    this.allowUnprefixedExpressions = allowUnprefixedExpressions;
  }
  
  public PrefixedValueSourceWrapper(FixedValueSource valueSource, List<String> possiblePrefixes) {
    this.valueSource = valueSource;
    this.possiblePrefixes = possiblePrefixes.<String>toArray(new String[possiblePrefixes.size()]);
  }
  
  public PrefixedValueSourceWrapper(FixedValueSource valueSource, List<String> possiblePrefixes, boolean allowUnprefixedExpressions) {
    this.valueSource = valueSource;
    this.possiblePrefixes = possiblePrefixes.<String>toArray(new String[possiblePrefixes.size()]);
    this.allowUnprefixedExpressions = allowUnprefixedExpressions;
  }
  
  public Object getValue(String expression, InterpolationState interpolationState) {
    expression = ValueSourceUtils.trimPrefix(expression, this.possiblePrefixes, this.allowUnprefixedExpressions);
    if (expression == null)
      return null; 
    return this.valueSource.getValue(expression, interpolationState);
  }
}
