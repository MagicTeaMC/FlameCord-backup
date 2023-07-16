package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.PropertyComponentBuilder;

class DefaultPropertyComponentBuilder extends DefaultComponentAndConfigurationBuilder<PropertyComponentBuilder> implements PropertyComponentBuilder {
  public DefaultPropertyComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> builder, String name, String value) {
    super(builder, name, "Property", value);
  }
}
