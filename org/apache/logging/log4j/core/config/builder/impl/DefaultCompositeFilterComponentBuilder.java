package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.CompositeFilterComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.FilterComponentBuilder;

class DefaultCompositeFilterComponentBuilder extends DefaultComponentAndConfigurationBuilder<CompositeFilterComponentBuilder> implements CompositeFilterComponentBuilder {
  public DefaultCompositeFilterComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> builder, String onMatch, String onMismatch) {
    super(builder, "Filters");
    addAttribute("onMatch", onMatch);
    addAttribute("onMismatch", onMismatch);
  }
  
  public CompositeFilterComponentBuilder add(FilterComponentBuilder builder) {
    return addComponent((ComponentBuilder<?>)builder);
  }
}
