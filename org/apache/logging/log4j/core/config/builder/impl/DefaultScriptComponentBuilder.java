package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.ScriptComponentBuilder;

class DefaultScriptComponentBuilder extends DefaultComponentAndConfigurationBuilder<ScriptComponentBuilder> implements ScriptComponentBuilder {
  public DefaultScriptComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> builder, String name, String language, String text) {
    super(builder, name, "Script");
    if (language != null)
      addAttribute("language", language); 
    if (text != null)
      addAttribute("text", text); 
  }
}
