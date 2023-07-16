package org.codehaus.plexus.interpolation.fixed;

import java.io.IOException;
import java.util.Properties;
import org.codehaus.plexus.interpolation.os.OperatingSystemUtils;

public class EnvarBasedValueSource implements FixedValueSource {
  private static Properties envarsCaseSensitive;
  
  private static Properties envarsCaseInsensitive;
  
  private final Properties envars;
  
  private final boolean caseSensitive;
  
  public EnvarBasedValueSource() throws IOException {
    this(true);
  }
  
  public EnvarBasedValueSource(boolean caseSensitive) throws IOException {
    this.caseSensitive = caseSensitive;
    this.envars = getEnvars(caseSensitive);
  }
  
  private static synchronized Properties getEnvars(boolean caseSensitive) throws IOException {
    if (caseSensitive) {
      if (envarsCaseSensitive == null)
        envarsCaseSensitive = OperatingSystemUtils.getSystemEnvVars(caseSensitive); 
      return envarsCaseSensitive;
    } 
    if (envarsCaseInsensitive == null)
      envarsCaseInsensitive = OperatingSystemUtils.getSystemEnvVars(caseSensitive); 
    return envarsCaseInsensitive;
  }
  
  public Object getValue(String expression, InterpolationState interpolationState) {
    String expr = expression;
    if (expr.startsWith("env."))
      expr = expr.substring("env.".length()); 
    if (!this.caseSensitive)
      expr = expr.toUpperCase(); 
    return this.envars.getProperty(expr);
  }
  
  static void resetStatics() {
    envarsCaseSensitive = null;
    envarsCaseInsensitive = null;
  }
}
