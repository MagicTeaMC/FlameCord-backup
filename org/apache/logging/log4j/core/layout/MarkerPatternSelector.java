package org.apache.logging.log4j.core.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.impl.LocationAware;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(name = "MarkerPatternSelector", category = "Core", elementType = "patternSelector", printObject = true)
public class MarkerPatternSelector implements PatternSelector, LocationAware {
  public static class Builder implements org.apache.logging.log4j.core.util.Builder<MarkerPatternSelector> {
    @PluginElement("PatternMatch")
    private PatternMatch[] properties;
    
    @PluginBuilderAttribute("defaultPattern")
    private String defaultPattern;
    
    @PluginBuilderAttribute("alwaysWriteExceptions")
    private boolean alwaysWriteExceptions = true;
    
    @PluginBuilderAttribute("disableAnsi")
    private boolean disableAnsi;
    
    @PluginBuilderAttribute("noConsoleNoAnsi")
    private boolean noConsoleNoAnsi;
    
    @PluginConfiguration
    private Configuration configuration;
    
    public MarkerPatternSelector build() {
      if (this.defaultPattern == null)
        this.defaultPattern = "%m%n"; 
      if (this.properties == null || this.properties.length == 0) {
        MarkerPatternSelector.LOGGER.warn("No marker patterns were provided with PatternMatch");
        return null;
      } 
      return new MarkerPatternSelector(this.properties, this.defaultPattern, this.alwaysWriteExceptions, this.disableAnsi, this.noConsoleNoAnsi, this.configuration);
    }
    
    public Builder setProperties(PatternMatch[] properties) {
      this.properties = properties;
      return this;
    }
    
    public Builder setDefaultPattern(String defaultPattern) {
      this.defaultPattern = defaultPattern;
      return this;
    }
    
    public Builder setAlwaysWriteExceptions(boolean alwaysWriteExceptions) {
      this.alwaysWriteExceptions = alwaysWriteExceptions;
      return this;
    }
    
    public Builder setDisableAnsi(boolean disableAnsi) {
      this.disableAnsi = disableAnsi;
      return this;
    }
    
    public Builder setNoConsoleNoAnsi(boolean noConsoleNoAnsi) {
      this.noConsoleNoAnsi = noConsoleNoAnsi;
      return this;
    }
    
    public Builder setConfiguration(Configuration configuration) {
      this.configuration = configuration;
      return this;
    }
  }
  
  private final Map<String, PatternFormatter[]> formatterMap = (Map)new HashMap<>();
  
  private final Map<String, String> patternMap = new HashMap<>();
  
  private final PatternFormatter[] defaultFormatters;
  
  private final String defaultPattern;
  
  private static Logger LOGGER = (Logger)StatusLogger.getLogger();
  
  private final boolean requiresLocation;
  
  @Deprecated
  public MarkerPatternSelector(PatternMatch[] properties, String defaultPattern, boolean alwaysWriteExceptions, boolean noConsoleNoAnsi, Configuration config) {
    this(properties, defaultPattern, alwaysWriteExceptions, false, noConsoleNoAnsi, config);
  }
  
  private MarkerPatternSelector(PatternMatch[] properties, String defaultPattern, boolean alwaysWriteExceptions, boolean disableAnsi, boolean noConsoleNoAnsi, Configuration config) {
    boolean needsLocation = false;
    PatternParser parser = PatternLayout.createPatternParser(config);
    for (PatternMatch property : properties) {
      try {
        List<PatternFormatter> list = parser.parse(property.getPattern(), alwaysWriteExceptions, disableAnsi, noConsoleNoAnsi);
        PatternFormatter[] formatters = list.<PatternFormatter>toArray(PatternFormatter.EMPTY_ARRAY);
        this.formatterMap.put(property.getKey(), formatters);
        for (int i = 0; !needsLocation && i < formatters.length; i++)
          needsLocation = formatters[i].requiresLocation(); 
        this.patternMap.put(property.getKey(), property.getPattern());
      } catch (RuntimeException ex) {
        throw new IllegalArgumentException("Cannot parse pattern '" + property.getPattern() + "'", ex);
      } 
    } 
    try {
      List<PatternFormatter> list = parser.parse(defaultPattern, alwaysWriteExceptions, disableAnsi, noConsoleNoAnsi);
      this.defaultFormatters = list.<PatternFormatter>toArray(PatternFormatter.EMPTY_ARRAY);
      this.defaultPattern = defaultPattern;
      for (int i = 0; !needsLocation && i < this.defaultFormatters.length; i++)
        needsLocation = this.defaultFormatters[i].requiresLocation(); 
    } catch (RuntimeException ex) {
      throw new IllegalArgumentException("Cannot parse pattern '" + defaultPattern + "'", ex);
    } 
    this.requiresLocation = needsLocation;
  }
  
  public boolean requiresLocation() {
    return this.requiresLocation;
  }
  
  public PatternFormatter[] getFormatters(LogEvent event) {
    Marker marker = event.getMarker();
    if (marker == null)
      return this.defaultFormatters; 
    for (String key : this.formatterMap.keySet()) {
      if (marker.isInstanceOf(key))
        return this.formatterMap.get(key); 
    } 
    return this.defaultFormatters;
  }
  
  @PluginBuilderFactory
  public static Builder newBuilder() {
    return new Builder();
  }
  
  @Deprecated
  public static MarkerPatternSelector createSelector(PatternMatch[] properties, String defaultPattern, boolean alwaysWriteExceptions, boolean noConsoleNoAnsi, Configuration configuration) {
    Builder builder = newBuilder();
    builder.setProperties(properties);
    builder.setDefaultPattern(defaultPattern);
    builder.setAlwaysWriteExceptions(alwaysWriteExceptions);
    builder.setNoConsoleNoAnsi(noConsoleNoAnsi);
    builder.setConfiguration(configuration);
    return builder.build();
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (Map.Entry<String, String> entry : this.patternMap.entrySet()) {
      if (!first)
        sb.append(", "); 
      sb.append("key=\"").append(entry.getKey()).append("\", pattern=\"").append(entry.getValue()).append("\"");
      first = false;
    } 
    if (!first)
      sb.append(", "); 
    sb.append("default=\"").append(this.defaultPattern).append("\"");
    return sb.toString();
  }
}
