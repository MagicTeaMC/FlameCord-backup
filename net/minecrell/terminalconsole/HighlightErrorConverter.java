package net.minecrell.terminalconsole;

import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(name = "highlightError", category = "Converter")
@ConverterKeys({"highlightError"})
@PerformanceSensitive({"allocation"})
public final class HighlightErrorConverter extends LogEventPatternConverter {
  private static final String ANSI_RESET = "\033[m";
  
  private static final String ANSI_ERROR = "\033[31;1m";
  
  private static final String ANSI_WARN = "\033[33;1m";
  
  private final List<PatternFormatter> formatters;
  
  protected HighlightErrorConverter(List<PatternFormatter> formatters) {
    super("highlightError", null);
    this.formatters = formatters;
  }
  
  public void format(LogEvent event, StringBuilder toAppendTo) {
    if (TerminalConsoleAppender.isAnsiSupported()) {
      Level level = event.getLevel();
      if (level.isMoreSpecificThan(Level.ERROR)) {
        format("\033[31;1m", event, toAppendTo);
        return;
      } 
      if (level.isMoreSpecificThan(Level.WARN)) {
        format("\033[33;1m", event, toAppendTo);
        return;
      } 
    } 
    for (int i = 0, size = this.formatters.size(); i < size; i++)
      ((PatternFormatter)this.formatters.get(i)).format(event, toAppendTo); 
  }
  
  private void format(String style, LogEvent event, StringBuilder toAppendTo) {
    int start = toAppendTo.length();
    toAppendTo.append(style);
    int end = toAppendTo.length();
    for (int i = 0, size = this.formatters.size(); i < size; i++)
      ((PatternFormatter)this.formatters.get(i)).format(event, toAppendTo); 
    if (toAppendTo.length() == end) {
      toAppendTo.setLength(start);
    } else {
      toAppendTo.append("\033[m");
    } 
  }
  
  public boolean handlesThrowable() {
    for (PatternFormatter formatter : this.formatters) {
      if (formatter.handlesThrowable())
        return true; 
    } 
    return false;
  }
  
  public static HighlightErrorConverter newInstance(Configuration config, String[] options) {
    if (options.length != 1) {
      LOGGER.error("Incorrect number of options on highlightError. Expected 1 received " + options.length);
      return null;
    } 
    if (options[0] == null) {
      LOGGER.error("No pattern supplied on highlightError");
      return null;
    } 
    PatternParser parser = PatternLayout.createPatternParser(config);
    List<PatternFormatter> formatters = parser.parse(options[0]);
    return new HighlightErrorConverter(formatters);
  }
}
