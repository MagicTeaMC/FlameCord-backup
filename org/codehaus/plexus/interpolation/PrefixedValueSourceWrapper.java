package org.codehaus.plexus.interpolation;

import java.util.Collections;
import java.util.List;
import org.codehaus.plexus.interpolation.util.ValueSourceUtils;

public class PrefixedValueSourceWrapper implements FeedbackEnabledValueSource, QueryEnabledValueSource {
  private final ValueSource valueSource;
  
  private final String[] possiblePrefixes;
  
  private boolean allowUnprefixedExpressions;
  
  private String lastExpression;
  
  public PrefixedValueSourceWrapper(ValueSource valueSource, String prefix) {
    this.valueSource = valueSource;
    this.possiblePrefixes = new String[] { prefix };
  }
  
  public PrefixedValueSourceWrapper(ValueSource valueSource, String prefix, boolean allowUnprefixedExpressions) {
    this.valueSource = valueSource;
    this.possiblePrefixes = new String[] { prefix };
    this.allowUnprefixedExpressions = allowUnprefixedExpressions;
  }
  
  public PrefixedValueSourceWrapper(ValueSource valueSource, List<String> possiblePrefixes) {
    this.valueSource = valueSource;
    this.possiblePrefixes = possiblePrefixes.<String>toArray(new String[possiblePrefixes.size()]);
  }
  
  public PrefixedValueSourceWrapper(ValueSource valueSource, List<String> possiblePrefixes, boolean allowUnprefixedExpressions) {
    this.valueSource = valueSource;
    this.possiblePrefixes = possiblePrefixes.<String>toArray(new String[possiblePrefixes.size()]);
    this.allowUnprefixedExpressions = allowUnprefixedExpressions;
  }
  
  public Object getValue(String expression) {
    this.lastExpression = ValueSourceUtils.trimPrefix(expression, this.possiblePrefixes, this.allowUnprefixedExpressions);
    if (this.lastExpression == null)
      return null; 
    return this.valueSource.getValue(this.lastExpression);
  }
  
  public List getFeedback() {
    return (this.valueSource instanceof FeedbackEnabledValueSource) ? this.valueSource
      .getFeedback() : Collections.EMPTY_LIST;
  }
  
  public String getLastExpression() {
    return (this.valueSource instanceof QueryEnabledValueSource) ? ((QueryEnabledValueSource)this.valueSource)
      .getLastExpression() : this.lastExpression;
  }
  
  public void clearFeedback() {
    this.valueSource.clearFeedback();
  }
}
