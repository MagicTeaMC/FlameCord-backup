package org.apache.logging.log4j.core.config;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.lookup.Interpolator;
import org.apache.logging.log4j.core.lookup.LookupResult;
import org.apache.logging.log4j.core.lookup.PropertiesLookup;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;

@Plugin(name = "properties", category = "Core", printObject = true)
public final class PropertiesPlugin {
  private static final StrSubstitutor UNESCAPING_SUBSTITUTOR = createUnescapingSubstitutor();
  
  @PluginFactory
  public static StrLookup configureSubstitutor(@PluginElement("Properties") Property[] properties, @PluginConfiguration Configuration config) {
    Property[] unescapedProperties = new Property[(properties == null) ? 0 : properties.length];
    for (int i = 0; i < unescapedProperties.length; i++)
      unescapedProperties[i] = unescape(properties[i]); 
    return (StrLookup)new Interpolator((StrLookup)new PropertiesLookup(unescapedProperties, config
          .getProperties()), config
        .getPluginPackages());
  }
  
  private static Property unescape(Property input) {
    return Property.createProperty(input
        .getName(), 
        unescape(input.getRawValue()), input
        .getValue());
  }
  
  static String unescape(String input) {
    return UNESCAPING_SUBSTITUTOR.replace(input);
  }
  
  private static StrSubstitutor createUnescapingSubstitutor() {
    StrSubstitutor substitutor = new StrSubstitutor(NullLookup.INSTANCE);
    substitutor.setValueDelimiter(null);
    substitutor.setValueDelimiterMatcher(null);
    return substitutor;
  }
  
  private enum NullLookup implements StrLookup {
    INSTANCE;
    
    public String lookup(String key) {
      return null;
    }
    
    public String lookup(LogEvent event, String key) {
      return null;
    }
    
    public LookupResult evaluate(String key) {
      return null;
    }
    
    public LookupResult evaluate(LogEvent event, String key) {
      return null;
    }
  }
}
