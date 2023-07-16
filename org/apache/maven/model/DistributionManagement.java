package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class DistributionManagement implements Serializable, Cloneable, InputLocationTracker {
  private DeploymentRepository repository;
  
  private DeploymentRepository snapshotRepository;
  
  private Site site;
  
  private String downloadUrl;
  
  private Relocation relocation;
  
  private String status;
  
  private Map<Object, InputLocation> locations;
  
  private InputLocation location;
  
  private InputLocation repositoryLocation;
  
  private InputLocation snapshotRepositoryLocation;
  
  private InputLocation siteLocation;
  
  private InputLocation downloadUrlLocation;
  
  private InputLocation relocationLocation;
  
  private InputLocation statusLocation;
  
  public DistributionManagement clone() {
    try {
      DistributionManagement copy = (DistributionManagement)super.clone();
      if (this.repository != null)
        copy.repository = this.repository.clone(); 
      if (this.snapshotRepository != null)
        copy.snapshotRepository = this.snapshotRepository.clone(); 
      if (this.site != null)
        copy.site = this.site.clone(); 
      if (this.relocation != null)
        copy.relocation = this.relocation.clone(); 
      if (copy.locations != null)
        copy.locations = new LinkedHashMap<>(copy.locations); 
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public String getDownloadUrl() {
    return this.downloadUrl;
  }
  
  public InputLocation getLocation(Object key) {
    if (key instanceof String) {
      switch ((String)key) {
        case "":
          return this.location;
        case "repository":
          return this.repositoryLocation;
        case "snapshotRepository":
          return this.snapshotRepositoryLocation;
        case "site":
          return this.siteLocation;
        case "downloadUrl":
          return this.downloadUrlLocation;
        case "relocation":
          return this.relocationLocation;
        case "status":
          return this.statusLocation;
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
        case "repository":
          this.repositoryLocation = location;
          return;
        case "snapshotRepository":
          this.snapshotRepositoryLocation = location;
          return;
        case "site":
          this.siteLocation = location;
          return;
        case "downloadUrl":
          this.downloadUrlLocation = location;
          return;
        case "relocation":
          this.relocationLocation = location;
          return;
        case "status":
          this.statusLocation = location;
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
  
  public Relocation getRelocation() {
    return this.relocation;
  }
  
  public DeploymentRepository getRepository() {
    return this.repository;
  }
  
  public Site getSite() {
    return this.site;
  }
  
  public DeploymentRepository getSnapshotRepository() {
    return this.snapshotRepository;
  }
  
  public String getStatus() {
    return this.status;
  }
  
  public void setDownloadUrl(String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }
  
  public void setRelocation(Relocation relocation) {
    this.relocation = relocation;
  }
  
  public void setRepository(DeploymentRepository repository) {
    this.repository = repository;
  }
  
  public void setSite(Site site) {
    this.site = site;
  }
  
  public void setSnapshotRepository(DeploymentRepository snapshotRepository) {
    this.snapshotRepository = snapshotRepository;
  }
  
  public void setStatus(String status) {
    this.status = status;
  }
}
