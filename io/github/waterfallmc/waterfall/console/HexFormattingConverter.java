package io.github.waterfallmc.waterfall.console;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecrell.terminalconsole.TerminalConsoleAppender;
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

@Plugin(name = "paperMinecraftFormatting", category = "Converter")
@ConverterKeys({"paperMinecraftFormatting"})
@PerformanceSensitive({"allocation"})
public final class HexFormattingConverter extends LogEventPatternConverter {
  private static final boolean KEEP_FORMATTING = PropertiesUtil.getProperties().getBooleanProperty("terminal.keepMinecraftFormatting");
  
  private static final String ANSI_RESET = "\033[m";
  
  private static final char COLOR_CHAR = '§';
  
  private static final String LOOKUP = "0123456789abcdefklmnor";
  
  private static final String RGB_ANSI = "\033[38;2;%d;%d;%dm";
  
  private static final Pattern NAMED_PATTERN = Pattern.compile("§[0-9a-fk-orA-FK-OR]");
  
  private static final Pattern RGB_PATTERN = Pattern.compile("§x(§[0-9a-fA-F]){6}");
  
  private static final String[] ansiCodes = new String[] { 
      "\033[0;30m", "\033[0;34m", "\033[0;32m", "\033[0;36m", "\033[0;31m", "\033[0;35m", "\033[0;33m", "\033[0;37m", "\033[0;30;1m", "\033[0;34;1m", 
      "\033[0;32;1m", "\033[0;36;1m", "\033[0;31;1m", "\033[0;35;1m", "\033[0;33;1m", "\033[0;37;1m", "\033[5m", "\033[21m", "\033[9m", "\033[4m", 
      "\033[3m", "\033[m" };
  
  private final boolean ansi;
  
  private final List<PatternFormatter> formatters;
  
  protected HexFormattingConverter(List<PatternFormatter> formatters, boolean strip) {
    super("paperMinecraftFormatting", null);
    this.formatters = formatters;
    this.ansi = !strip;
  }
  
  public void format(LogEvent event, StringBuilder toAppendTo) {
    int start = toAppendTo.length();
    for (int i = 0, size = this.formatters.size(); i < size; i++)
      ((PatternFormatter)this.formatters.get(i)).format(event, toAppendTo); 
    if (KEEP_FORMATTING || toAppendTo.length() == start)
      return; 
    boolean useAnsi = (this.ansi && TerminalConsoleAppender.isAnsiSupported());
    String content = useAnsi ? convertRGBColors(toAppendTo.substring(start)) : stripRGBColors(toAppendTo.substring(start));
    format(content, toAppendTo, start, useAnsi);
  }
  
  private static String convertRGBColors(String input) {
    Matcher matcher = RGB_PATTERN.matcher(input);
    StringBuffer buffer = new StringBuffer();
    while (matcher.find()) {
      String s = matcher.group().replace(String.valueOf('§'), "").replace('x', '#');
      int hex = Integer.decode(s).intValue();
      int red = hex >> 16 & 0xFF;
      int green = hex >> 8 & 0xFF;
      int blue = hex & 0xFF;
      String replacement = String.format("\033[38;2;%d;%d;%dm", new Object[] { Integer.valueOf(red), Integer.valueOf(green), Integer.valueOf(blue) });
      matcher.appendReplacement(buffer, replacement);
    } 
    matcher.appendTail(buffer);
    return buffer.toString();
  }
  
  private static String stripRGBColors(String input) {
    Matcher matcher = RGB_PATTERN.matcher(input);
    StringBuffer buffer = new StringBuffer();
    while (matcher.find())
      matcher.appendReplacement(buffer, ""); 
    matcher.appendTail(buffer);
    return buffer.toString();
  }
  
  static void format(String content, StringBuilder result, int start, boolean ansi) {
    int next = content.indexOf('§');
    int last = content.length() - 1;
    if (next == -1 || next == last) {
      result.setLength(start);
      result.append(content);
      if (ansi)
        result.append("\033[m"); 
      return;
    } 
    Matcher matcher = NAMED_PATTERN.matcher(content);
    StringBuffer buffer = new StringBuffer();
    while (matcher.find()) {
      int format = "0123456789abcdefklmnor".indexOf(Character.toLowerCase(matcher.group().charAt(1)));
      if (format != -1)
        matcher.appendReplacement(buffer, ansi ? ansiCodes[format] : ""); 
    } 
    matcher.appendTail(buffer);
    result.setLength(start);
    result.append(buffer.toString());
    if (ansi)
      result.append("\033[m"); 
  }
  
  public static HexFormattingConverter newInstance(Configuration config, String[] options) {
    if (options.length < 1 || options.length > 2) {
      LOGGER.error("Incorrect number of options on paperMinecraftFormatting. Expected at least 1, max 2 received " + options.length);
      return null;
    } 
    if (options[0] == null) {
      LOGGER.error("No pattern supplied on paperMinecraftFormatting");
      return null;
    } 
    PatternParser parser = PatternLayout.createPatternParser(config);
    List<PatternFormatter> formatters = parser.parse(options[0]);
    boolean strip = (options.length > 1 && "strip".equals(options[1]));
    return new HexFormattingConverter(formatters, strip);
  }
}
