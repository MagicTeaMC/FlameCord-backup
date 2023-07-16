package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.LogEvent;

public interface StrLookup {
  public static final String CATEGORY = "Lookup";
  
  String lookup(String paramString);
  
  String lookup(LogEvent paramLogEvent, String paramString);
  
  default LookupResult evaluate(String key) {
    String value = lookup(key);
    return (value == null) ? null : new DefaultLookupResult(value);
  }
  
  default LookupResult evaluate(LogEvent event, String key) {
    String value = lookup(event, key);
    return (value == null) ? null : new DefaultLookupResult(value);
  }
}
