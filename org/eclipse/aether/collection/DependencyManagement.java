package org.eclipse.aether.collection;

import java.util.Collection;
import java.util.Map;
import org.eclipse.aether.graph.Exclusion;

public final class DependencyManagement {
  private String version;
  
  private String scope;
  
  private Boolean optional;
  
  private Collection<Exclusion> exclusions;
  
  private Map<String, String> properties;
  
  public String getVersion() {
    return this.version;
  }
  
  public DependencyManagement setVersion(String version) {
    this.version = version;
    return this;
  }
  
  public String getScope() {
    return this.scope;
  }
  
  public DependencyManagement setScope(String scope) {
    this.scope = scope;
    return this;
  }
  
  public Boolean getOptional() {
    return this.optional;
  }
  
  public DependencyManagement setOptional(Boolean optional) {
    this.optional = optional;
    return this;
  }
  
  public Collection<Exclusion> getExclusions() {
    return this.exclusions;
  }
  
  public DependencyManagement setExclusions(Collection<Exclusion> exclusions) {
    this.exclusions = exclusions;
    return this;
  }
  
  public Map<String, String> getProperties() {
    return this.properties;
  }
  
  public DependencyManagement setProperties(Map<String, String> properties) {
    this.properties = properties;
    return this;
  }
}
