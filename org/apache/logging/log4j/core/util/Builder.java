package org.apache.logging.log4j.core.util;

import org.apache.logging.log4j.core.config.plugins.util.PluginBuilder;

public interface Builder<T> {
  T build();
  
  default boolean isValid() {
    return PluginBuilder.validateFields(this, getErrorPrefix());
  }
  
  default String getErrorPrefix() {
    return "Component";
  }
}
