package net.minecrell.terminalconsole.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.layout.PatternMatch;
import org.apache.logging.log4j.core.layout.PatternSelector;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(name = "LoggerNamePatternSelector", category = "Core", elementType = "patternSelector")
@PerformanceSensitive({"allocation"})
public final class LoggerNamePatternSelector implements PatternSelector {
  private final PatternFormatter[] defaultFormatters;
  
  private static class LoggerNameSelector {
    private final String name;
    
    private final boolean isPackage;
    
    private final PatternFormatter[] formatters;
    
    LoggerNameSelector(String name, PatternFormatter[] formatters) {
      this.name = name;
      this.isPackage = name.endsWith(".");
      this.formatters = formatters;
    }
    
    PatternFormatter[] get() {
      return this.formatters;
    }
    
    boolean test(String s) {
      return this.isPackage ? s.startsWith(this.name) : s.equals(this.name);
    }
  }
  
  private final List<LoggerNameSelector> formatters = new ArrayList<>();
  
  protected LoggerNamePatternSelector(String defaultPattern, PatternMatch[] properties, boolean alwaysWriteExceptions, boolean disableAnsi, boolean noConsoleNoAnsi, Configuration config) {
    PatternParser parser = PatternLayout.createPatternParser(config);
    PatternFormatter[] emptyFormatters = new PatternFormatter[0];
    this
      .defaultFormatters = (PatternFormatter[])parser.parse(defaultPattern, alwaysWriteExceptions, disableAnsi, noConsoleNoAnsi).toArray((Object[])emptyFormatters);
    for (PatternMatch property : properties) {
      PatternFormatter[] formatters = (PatternFormatter[])parser.parse(property.getPattern(), alwaysWriteExceptions, disableAnsi, noConsoleNoAnsi).toArray((Object[])emptyFormatters);
      for (String name : property.getKey().split(","))
        this.formatters.add(new LoggerNameSelector(name, formatters)); 
    } 
  }
  
  public PatternFormatter[] getFormatters(LogEvent event) {
    String loggerName = event.getLoggerName();
    if (loggerName != null)
      for (int i = 0; i < this.formatters.size(); i++) {
        LoggerNameSelector selector = this.formatters.get(i);
        if (selector.test(loggerName))
          return selector.get(); 
      }  
    return this.defaultFormatters;
  }
  
  @PluginFactory
  public static LoggerNamePatternSelector createSelector(@Required(message = "Default pattern is required") @PluginAttribute("defaultPattern") String defaultPattern, @PluginElement("PatternMatch") PatternMatch[] properties, @PluginAttribute(value = "alwaysWriteExceptions", defaultBoolean = true) boolean alwaysWriteExceptions, @PluginAttribute("disableAnsi") boolean disableAnsi, @PluginAttribute("noConsoleNoAnsi") boolean noConsoleNoAnsi, @PluginConfiguration Configuration config) {
    return new LoggerNamePatternSelector(defaultPattern, properties, alwaysWriteExceptions, disableAnsi, noConsoleNoAnsi, config);
  }
}
