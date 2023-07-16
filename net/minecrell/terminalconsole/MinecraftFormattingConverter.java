package net.minecrell.terminalconsole;

import java.util.List;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.PropertiesUtil;

@Plugin(name = "minecraftFormatting", category = "Converter")
@ConverterKeys({"minecraftFormatting"})
@Deprecated
@PerformanceSensitive({"allocation"})
public final class MinecraftFormattingConverter extends LogEventPatternConverter {
  public static final String KEEP_FORMATTING_PROPERTY = "terminal.keepMinecraftFormatting";
  
  private static final boolean KEEP_FORMATTING = PropertiesUtil.getProperties().getBooleanProperty("terminal.keepMinecraftFormatting");
  
  static final String ANSI_RESET = "\033[m";
  
  private static final char COLOR_CHAR = '§';
  
  private static final String LOOKUP = "0123456789abcdefklmnor";
  
  private static final String[] ansiCodes = new String[] { 
      "\033[0;30m", "\033[0;34m", "\033[0;32m", "\033[0;36m", "\033[0;31m", "\033[0;35m", "\033[0;33m", "\033[0;37m", "\033[0;30;1m", "\033[0;34;1m", 
      "\033[0;32;1m", "\033[0;36;1m", "\033[0;31;1m", "\033[0;35;1m", "\033[0;33;1m", "\033[0;37;1m", "\033[5m", "\033[21m", "\033[9m", "\033[4m", 
      "\033[3m", "\033[m" };
  
  private final boolean ansi;
  
  private final List<PatternFormatter> formatters;
  
  protected MinecraftFormattingConverter(List<PatternFormatter> formatters, boolean strip) {
    super("minecraftFormatting", null);
    this.formatters = formatters;
    this.ansi = !strip;
  }
  
  public void format(LogEvent event, StringBuilder toAppendTo) {
    int start = toAppendTo.length();
    for (int i = 0, size = this.formatters.size(); i < size; i++)
      ((PatternFormatter)this.formatters.get(i)).format(event, toAppendTo); 
    if (KEEP_FORMATTING || toAppendTo.length() == start)
      return; 
    String content = toAppendTo.substring(start);
    format(content, toAppendTo, start, (this.ansi && TerminalConsoleAppender.isAnsiSupported()));
  }
  
  static void format(String s, StringBuilder result, int start, boolean ansi) {
    int next = s.indexOf('§');
    int last = s.length() - 1;
    if (next == -1 || next == last)
      return; 
    result.setLength(start + next);
    int pos = next;
    do {
      int format = "0123456789abcdefklmnor".indexOf(Character.toLowerCase(s.charAt(next + 1)));
      if (format != -1) {
        if (pos != next)
          result.append(s, pos, next); 
        if (ansi)
          result.append(ansiCodes[format]); 
        pos = next += 2;
      } else {
        next++;
      } 
      next = s.indexOf('§', next);
    } while (next != -1 && next < last);
    result.append(s, pos, s.length());
    if (ansi)
      result.append("\033[m"); 
  }
  
  public static MinecraftFormattingConverter newInstance(Configuration config, String[] options) {
    if (options.length < 1 || options.length > 2) {
      LOGGER.error("Incorrect number of options on minecraftFormatting. Expected at least 1, max 2 received " + options.length);
      return null;
    } 
    if (options[0] == null) {
      LOGGER.error("No pattern supplied on minecraftFormatting");
      return null;
    } 
    PatternParser parser = PatternLayout.createPatternParser(config);
    List<PatternFormatter> formatters = parser.parse(options[0]);
    boolean strip = (options.length > 1 && "strip".equals(options[1]));
    return new MinecraftFormattingConverter(formatters, strip);
  }
}
