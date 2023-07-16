package org.apache.logging.log4j.core.lookup;

import java.util.Map;
import java.util.Properties;

public final class RuntimeStrSubstitutor extends StrSubstitutor {
  public RuntimeStrSubstitutor() {}
  
  public RuntimeStrSubstitutor(Map<String, String> valueMap) {
    super(valueMap);
  }
  
  public RuntimeStrSubstitutor(Properties properties) {
    super(properties);
  }
  
  public RuntimeStrSubstitutor(StrLookup lookup) {
    super(lookup);
  }
  
  public RuntimeStrSubstitutor(StrSubstitutor other) {
    super(other);
  }
  
  public String toString() {
    return "RuntimeStrSubstitutor{" + super.toString() + "}";
  }
}
