package org.apache.logging.log4j.core.lookup;

import java.util.Map;
import java.util.Properties;

public final class ConfigurationStrSubstitutor extends StrSubstitutor {
  public ConfigurationStrSubstitutor() {}
  
  public ConfigurationStrSubstitutor(Map<String, String> valueMap) {
    super(valueMap);
  }
  
  public ConfigurationStrSubstitutor(Properties properties) {
    super(properties);
  }
  
  public ConfigurationStrSubstitutor(StrLookup lookup) {
    super(lookup);
  }
  
  public ConfigurationStrSubstitutor(StrSubstitutor other) {
    super(other);
  }
  
  public String toString() {
    return "ConfigurationStrSubstitutor{" + super.toString() + "}";
  }
}
