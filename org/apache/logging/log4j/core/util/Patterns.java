package org.apache.logging.log4j.core.util;

public final class Patterns {
  public static final String COMMA_SEPARATOR = toWhitespaceSeparator(",");
  
  public static final String COMMA_SPACE_SEPARATOR = toWhitespaceSeparator("[,\\s]");
  
  public static final String WHITESPACE = "\\s*";
  
  public static String toWhitespaceSeparator(String separator) {
    return "\\s*" + separator + "\\s*";
  }
}
