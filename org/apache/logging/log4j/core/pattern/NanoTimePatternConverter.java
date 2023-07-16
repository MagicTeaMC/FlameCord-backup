package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(name = "NanoTimePatternConverter", category = "Converter")
@ConverterKeys({"N", "nano"})
@PerformanceSensitive({"allocation"})
public final class NanoTimePatternConverter extends LogEventPatternConverter {
  private NanoTimePatternConverter(String[] options) {
    super("Nanotime", "nanotime");
  }
  
  public static NanoTimePatternConverter newInstance(String[] options) {
    return new NanoTimePatternConverter(options);
  }
  
  public void format(LogEvent event, StringBuilder output) {
    output.append(event.getNanoTime());
  }
}
