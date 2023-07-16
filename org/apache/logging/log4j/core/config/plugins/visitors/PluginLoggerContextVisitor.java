package org.apache.logging.log4j.core.config.plugins.visitors;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;

public class PluginLoggerContextVisitor extends AbstractPluginVisitor<PluginConfiguration> {
  public PluginLoggerContextVisitor() {
    super(PluginConfiguration.class);
  }
  
  public Object visit(Configuration configuration, Node node, LogEvent event, StringBuilder log) {
    if (this.conversionType.isAssignableFrom(LoggerContext.class)) {
      if (configuration.getLoggerContext() != null)
        return configuration.getLoggerContext(); 
      LOGGER.warn("Configuration {} is not assigned a LoggerContext", configuration.getName());
    } 
    LOGGER.warn("Variable annotated with @PluginLoggerContext does not reference a LoggerContext");
    return null;
  }
}
