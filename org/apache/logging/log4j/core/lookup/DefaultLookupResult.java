package org.apache.logging.log4j.core.lookup;

import java.util.Objects;

final class DefaultLookupResult implements LookupResult {
  private final String value;
  
  DefaultLookupResult(String value) {
    this.value = Objects.<String>requireNonNull(value, "value is required");
  }
  
  public String value() {
    return this.value;
  }
  
  public boolean isLookupEvaluationAllowedInValue() {
    return false;
  }
  
  public String toString() {
    return "DefaultLookupResult{value='" + this.value + "'}";
  }
}
