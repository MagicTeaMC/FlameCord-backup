package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;

class DefaultComponentAndConfigurationBuilder<T extends ComponentBuilder<T>> extends DefaultComponentBuilder<T, DefaultConfigurationBuilder<? extends Configuration>> {
  DefaultComponentAndConfigurationBuilder(DefaultConfigurationBuilder<? extends Configuration> builder, String name, String type, String value) {
    super(builder, name, type, value);
  }
  
  DefaultComponentAndConfigurationBuilder(DefaultConfigurationBuilder<? extends Configuration> builder, String name, String type) {
    super(builder, name, type);
  }
  
  public DefaultComponentAndConfigurationBuilder(DefaultConfigurationBuilder<? extends Configuration> builder, String type) {
    super(builder, type);
  }
}
