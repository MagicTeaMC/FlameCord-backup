package org.codehaus.plexus.interpolation;

public abstract class AbstractFunctionValueSourceWrapper implements ValueSource {
  private final ValueSource valueSource;
  
  protected AbstractFunctionValueSourceWrapper(ValueSource valueSource) {
    this.valueSource = valueSource;
  }
  
  public Object getValue(String expression) {
    Object value = this.valueSource.getValue(expression);
    String expr = expression;
    if (this.valueSource instanceof QueryEnabledValueSource)
      expr = ((QueryEnabledValueSource)this.valueSource).getLastExpression(); 
    return executeFunction(expr, value);
  }
  
  protected ValueSource getValueSource() {
    return this.valueSource;
  }
  
  protected abstract Object executeFunction(String paramString, Object paramObject);
}
