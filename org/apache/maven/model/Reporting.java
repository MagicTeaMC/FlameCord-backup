package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Reporting implements Serializable, Cloneable, InputLocationTracker {
  private String excludeDefaults;
  
  private String outputDirectory;
  
  private List<ReportPlugin> plugins;
  
  private Map<Object, InputLocation> locations;
  
  private InputLocation location;
  
  private InputLocation excludeDefaultsLocation;
  
  private InputLocation outputDirectoryLocation;
  
  private InputLocation pluginsLocation;
  
  Map<String, ReportPlugin> reportPluginMap;
  
  public void addPlugin(ReportPlugin reportPlugin) {
    getPlugins().add(reportPlugin);
  }
  
  public Reporting clone() {
    try {
      Reporting copy = (Reporting)super.clone();
      if (this.plugins != null) {
        copy.plugins = new ArrayList<>();
        for (ReportPlugin item : this.plugins)
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
  
  public String getExcludeDefaults() {
    return this.excludeDefaults;
  }
  
  public InputLocation getLocation(Object key) {
    if (key instanceof String) {
      switch ((String)key) {
        case "":
          return this.location;
        case "excludeDefaults":
          return this.excludeDefaultsLocation;
        case "outputDirectory":
          return this.outputDirectoryLocation;
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
        case "excludeDefaults":
          this.excludeDefaultsLocation = location;
          return;
        case "outputDirectory":
          this.outputDirectoryLocation = location;
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
  
  public String getOutputDirectory() {
    return this.outputDirectory;
  }
  
  public List<ReportPlugin> getPlugins() {
    if (this.plugins == null)
      this.plugins = new ArrayList<>(); 
    return this.plugins;
  }
  
  public void removePlugin(ReportPlugin reportPlugin) {
    getPlugins().remove(reportPlugin);
  }
  
  public void setExcludeDefaults(String excludeDefaults) {
    this.excludeDefaults = excludeDefaults;
  }
  
  public void setOutputDirectory(String outputDirectory) {
    this.outputDirectory = outputDirectory;
  }
  
  public void setPlugins(List<ReportPlugin> plugins) {
    this.plugins = plugins;
  }
  
  public boolean isExcludeDefaults() {
    return (this.excludeDefaults != null) ? Boolean.parseBoolean(this.excludeDefaults) : false;
  }
  
  public void setExcludeDefaults(boolean excludeDefaults) {
    this.excludeDefaults = String.valueOf(excludeDefaults);
  }
  
  public synchronized void flushReportPluginMap() {
    this.reportPluginMap = null;
  }
  
  public synchronized Map<String, ReportPlugin> getReportPluginsAsMap() {
    if (this.reportPluginMap == null) {
      this.reportPluginMap = new LinkedHashMap<>();
      if (getPlugins() != null)
        for (Iterator<ReportPlugin> it = getPlugins().iterator(); it.hasNext(); ) {
          ReportPlugin reportPlugin = it.next();
          this.reportPluginMap.put(reportPlugin.getKey(), reportPlugin);
        }  
    } 
    return this.reportPluginMap;
  }
}
