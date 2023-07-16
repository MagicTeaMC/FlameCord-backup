package org.apache.logging.log4j.core.config.builder.api;

public interface ScriptFileComponentBuilder extends ComponentBuilder<ScriptFileComponentBuilder> {
  ScriptFileComponentBuilder addLanguage(String paramString);
  
  ScriptFileComponentBuilder addIsWatched(boolean paramBoolean);
  
  ScriptFileComponentBuilder addIsWatched(String paramString);
  
  ScriptFileComponentBuilder addCharset(String paramString);
}
