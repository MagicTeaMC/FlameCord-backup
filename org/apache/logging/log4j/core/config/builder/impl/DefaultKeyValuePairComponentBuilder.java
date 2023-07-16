package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.KeyValuePairComponentBuilder;

class DefaultKeyValuePairComponentBuilder extends DefaultComponentAndConfigurationBuilder<KeyValuePairComponentBuilder> implements KeyValuePairComponentBuilder {
  public DefaultKeyValuePairComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> builder, String key, String value) {
    super(builder, "KeyValuePair");
    addAttribute("key", key);
    addAttribute("value", value);
  }
}
