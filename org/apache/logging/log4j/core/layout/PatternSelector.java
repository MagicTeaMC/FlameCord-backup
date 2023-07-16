package org.apache.logging.log4j.core.layout;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.pattern.PatternFormatter;

public interface PatternSelector {
  public static final String ELEMENT_TYPE = "patternSelector";
  
  PatternFormatter[] getFormatters(LogEvent paramLogEvent);
}
