package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PluginContainer implements Serializable, Cloneable, InputLocationTracker {
  private List<Plugin> plugins;
  
  private Map<Object, InputLocation> locations;
  
  private InputLocation location;
  
  private InputLocation pluginsLocation;
  
  Map<String, Plugin> pluginMap;
  
  public void addPlugin(Plugin plugin) {
    getPlugins().add(plugin);
  }
  
  public PluginContainer clone() {
    try {
      PluginContainer copy = (PluginContainer)super.clone();
      if (this.plugins != null) {
        copy.plugins = new ArrayList<>();
        for (Plugin item : this.plugins)
          copy.plugins.add(item.clone()); 
      } 
      if (copy.locations != null)
        copy.locations = new LinkedHashMap<>(copy.locations); 
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public InputLocation getLocation(Object key) {
    if (key instanceof String) {
      switch ((String)key) {
        case "":
          return this.location;
        case "plugins":
          return this.pluginsLocation;
      } 
      return getOtherLocation(key);
    } 
    return getOtherLocation(key);
  }
  
  public void setLocation(Object key, InputLocation location) {
    if (key instanceof String) {
      switch ((String)key) {
        case "":
          this.location = location;
          return;
        case "plugins":
          this.pluginsLocation = location;
          return;
      } 
      setOtherLocation(key, location);
      return;
    } 
    setOtherLocation(key, location);
  }
  
  public void setOtherLocation(Object key, InputLocation location) {
    if (location != null) {
      if (this.locations == null)
        this.locations = new LinkedHashMap<>(); 
      this.locations.put(key, location);
    } 
  }
  
  private InputLocation getOtherLocation(Object key) {
    return (this.locations != null) ? this.locations.get(key) : null;
  }
  
  public List<Plugin> getPlugins() {
    if (this.plugins == null)
      this.plugins = new ArrayList<>(); 
    return this.plugins;
  }
  
  public void removePlugin(Plugin plugin) {
    getPlugins().remove(plugin);
  }
  
  public void setPlugins(List<Plugin> plugins) {
    this.plugins = plugins;
  }
  
  public synchronized void flushPluginMap() {
    this.pluginMap = null;
  }
  
  public synchronized Map<String, Plugin> getPluginsAsMap() {
    if (this.pluginMap == null) {
      this.pluginMap = new LinkedHashMap<>();
      if (this.plugins != null)
        for (Iterator<Plugin> it = this.plugins.iterator(); it.hasNext(); ) {
          Plugin plugin = it.next();
          this.pluginMap.put(plugin.getKey(), plugin);
        }  
    } 
    return this.pluginMap;
  }
}
