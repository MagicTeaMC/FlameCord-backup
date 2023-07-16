package org.apache.maven.model.building;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.model.resolution.WorkspaceModelResolver;

class FilterModelBuildingRequest implements ModelBuildingRequest {
  protected ModelBuildingRequest request;
  
  FilterModelBuildingRequest(ModelBuildingRequest request) {
    this.request = request;
  }
  
  public File getPomFile() {
    return this.request.getPomFile();
  }
  
  public FilterModelBuildingRequest setPomFile(File pomFile) {
    this.request.setPomFile(pomFile);
    return this;
  }
  
  public ModelSource getModelSource() {
    return this.request.getModelSource();
  }
  
  public FilterModelBuildingRequest setModelSource(ModelSource modelSource) {
    this.request.setModelSource(modelSource);
    return this;
  }
  
  public int getValidationLevel() {
    return this.request.getValidationLevel();
  }
  
  public FilterModelBuildingRequest setValidationLevel(int validationLevel) {
    this.request.setValidationLevel(validationLevel);
    return this;
  }
  
  public boolean isProcessPlugins() {
    return this.request.isProcessPlugins();
  }
  
  public FilterModelBuildingRequest setProcessPlugins(boolean processPlugins) {
    this.request.setProcessPlugins(processPlugins);
    return this;
  }
  
  public boolean isTwoPhaseBuilding() {
    return this.request.isTwoPhaseBuilding();
  }
  
  public FilterModelBuildingRequest setTwoPhaseBuilding(boolean twoPhaseBuilding) {
    this.request.setTwoPhaseBuilding(twoPhaseBuilding);
    return this;
  }
  
  public boolean isLocationTracking() {
    return this.request.isLocationTracking();
  }
  
  public FilterModelBuildingRequest setLocationTracking(boolean locationTracking) {
    this.request.setLocationTracking(locationTracking);
    return this;
  }
  
  public List<Profile> getProfiles() {
    return this.request.getProfiles();
  }
  
  public FilterModelBuildingRequest setProfiles(List<Profile> profiles) {
    this.request.setProfiles(profiles);
    return this;
  }
  
  public List<String> getActiveProfileIds() {
    return this.request.getActiveProfileIds();
  }
  
  public FilterModelBuildingRequest setActiveProfileIds(List<String> activeProfileIds) {
    this.request.setActiveProfileIds(activeProfileIds);
    return this;
  }
  
  public List<String> getInactiveProfileIds() {
    return this.request.getInactiveProfileIds();
  }
  
  public FilterModelBuildingRequest setInactiveProfileIds(List<String> inactiveProfileIds) {
    this.request.setInactiveProfileIds(inactiveProfileIds);
    return this;
  }
  
  public Properties getSystemProperties() {
    return this.request.getSystemProperties();
  }
  
  public FilterModelBuildingRequest setSystemProperties(Properties systemProperties) {
    this.request.setSystemProperties(systemProperties);
    return this;
  }
  
  public Properties getUserProperties() {
    return this.request.getUserProperties();
  }
  
  public FilterModelBuildingRequest setUserProperties(Properties userProperties) {
    this.request.setUserProperties(userProperties);
    return this;
  }
  
  public Date getBuildStartTime() {
    return this.request.getBuildStartTime();
  }
  
  public ModelBuildingRequest setBuildStartTime(Date buildStartTime) {
    this.request.setBuildStartTime(buildStartTime);
    return this;
  }
  
  public ModelResolver getModelResolver() {
    return this.request.getModelResolver();
  }
  
  public FilterModelBuildingRequest setModelResolver(ModelResolver modelResolver) {
    this.request.setModelResolver(modelResolver);
    return this;
  }
  
  public ModelBuildingListener getModelBuildingListener() {
    return this.request.getModelBuildingListener();
  }
  
  public ModelBuildingRequest setModelBuildingListener(ModelBuildingListener modelBuildingListener) {
    this.request.setModelBuildingListener(modelBuildingListener);
    return this;
  }
  
  public ModelCache getModelCache() {
    return this.request.getModelCache();
  }
  
  public FilterModelBuildingRequest setModelCache(ModelCache modelCache) {
    this.request.setModelCache(modelCache);
    return this;
  }
  
  public Model getRawModel() {
    return this.request.getRawModel();
  }
  
  public ModelBuildingRequest setRawModel(Model rawModel) {
    this.request.setRawModel(rawModel);
    return this;
  }
  
  public WorkspaceModelResolver getWorkspaceModelResolver() {
    return this.request.getWorkspaceModelResolver();
  }
  
  public ModelBuildingRequest setWorkspaceModelResolver(WorkspaceModelResolver workspaceResolver) {
    this.request.setWorkspaceModelResolver(workspaceResolver);
    return this;
  }
}
