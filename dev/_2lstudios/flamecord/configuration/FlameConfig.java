package dev._2lstudios.flamecord.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class FlameConfig {
  Configuration load(File file) {
    ConfigurationProvider configurationProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    if (file.exists() && file.isFile())
      try {
        return configurationProvider.load(file);
      } catch (IOException iOException) {} 
    return new Configuration();
  }
  
  void save(Configuration config, File file) {
    ConfigurationProvider configurationProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    try {
      configurationProvider.save(config, file);
    } catch (IOException iOException) {}
  }
  
  double setIfUnexistant(String arg1, double arg2, Configuration configuration) {
    return ((Double)setIfUnexistant(arg1, Double.valueOf(arg2), configuration)).doubleValue();
  }
  
  int setIfUnexistant(String arg1, int arg2, Configuration configuration) {
    return ((Integer)setIfUnexistant(arg1, Integer.valueOf(arg2), configuration)).intValue();
  }
  
  String setIfUnexistant(String arg1, String arg2, Configuration configuration) {
    return (String)setIfUnexistant(arg1, arg2, configuration);
  }
  
  boolean setIfUnexistant(String arg1, boolean arg2, Configuration configuration) {
    return ((Boolean)setIfUnexistant(arg1, Boolean.valueOf(arg2), configuration)).booleanValue();
  }
  
  Object setIfUnexistant(String arg1, Object arg2, Configuration configuration) {
    if (!configuration.contains(arg1)) {
      configuration.set(arg1, arg2);
      return arg2;
    } 
    return configuration.get(arg1);
  }
  
  Collection<String> setIfUnexistant(String arg1, Collection<String> arg2, Configuration configuration) {
    if (!configuration.contains(arg1)) {
      configuration.set(arg1, new ArrayList<>(arg2));
      return arg2;
    } 
    return new HashSet<>(configuration.getStringList(arg1));
  }
}
