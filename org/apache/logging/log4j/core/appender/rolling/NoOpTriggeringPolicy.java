package org.apache.logging.log4j.core.appender.rolling;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(name = "NoOpTriggeringPolicy", category = "Core", printObject = true)
public class NoOpTriggeringPolicy extends AbstractTriggeringPolicy {
  public static final NoOpTriggeringPolicy INSTANCE = new NoOpTriggeringPolicy();
  
  @PluginFactory
  public static NoOpTriggeringPolicy createPolicy() {
    return INSTANCE;
  }
  
  public void initialize(RollingFileManager manager) {}
  
  public boolean isTriggeringEvent(LogEvent logEvent) {
    return false;
  }
}
