package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;

class DefaultLayoutComponentBuilder extends DefaultComponentAndConfigurationBuilder<LayoutComponentBuilder> implements LayoutComponentBuilder {
  public DefaultLayoutComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> builder, String type) {
    super(builder, type);
  }
}
