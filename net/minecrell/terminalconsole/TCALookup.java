package net.minecrell.terminalconsole;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.AbstractLookup;

@Plugin(name = "tca", category = "Lookup")
public final class TCALookup extends AbstractLookup {
  public static final String KEY_DISABLE_ANSI = "disableAnsi";
  
  public String lookup(LogEvent event, String key) {
    if ("disableAnsi".equals(key))
      return String.valueOf(!TerminalConsoleAppender.isAnsiSupported()); 
    return null;
  }
}
