package org.apache.maven.model.building;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.model.resolution.WorkspaceModelResolver;

public interface ModelBuildingRequest {
  public static final int VALIDATION_LEVEL_MINIMAL = 0;
  
  public static final int VALIDATION_LEVEL_MAVEN_2_0 = 20;
  
  public static final int VALIDATION_LEVEL_MAVEN_3_0 = 30;
  
  public static final int VALIDATION_LEVEL_MAVEN_3_1 = 31;
  
  public static final int VALIDATION_LEVEL_STRICT = 30;
  
  Model getRawModel();
  
  ModelBuildingRequest setRawModel(Model paramModel);
  
  ModelSource getModelSource();
  
  ModelBuildingRequest setModelSource(ModelSource paramModelSource);
  
  File getPomFile();
  
  ModelBuildingRequest setPomFile(File paramFile);
  
  int getValidationLevel();
  
  ModelBuildingRequest setValidationLevel(int paramInt);
  
  boolean isProcessPlugins();
  
  ModelBuildingRequest setProcessPlugins(boolean paramBoolean);
  
  boolean isTwoPhaseBuilding();
  
  ModelBuildingRequest setTwoPhaseBuilding(boolean paramBoolean);
  
  boolean isLocationTracking();
  
  ModelBuildingRequest setLocationTracking(boolean paramBoolean);
  
  List<Profile> getProfiles();
  
  ModelBuildingRequest setProfiles(List<Profile> paramList);
  
  List<String> getActiveProfileIds();
  
  ModelBuildingRequest setActiveProfileIds(List<String> paramList);
  
  List<String> getInactiveProfileIds();
  
  ModelBuildingRequest setInactiveProfileIds(List<String> paramList);
  
  Properties getSystemProperties();
  
  ModelBuildingRequest setSystemProperties(Properties paramProperties);
  
  Properties getUserProperties();
  
  ModelBuildingRequest setUserProperties(Properties paramProperties);
  
  Date getBuildStartTime();
  
  ModelBuildingRequest setBuildStartTime(Date paramDate);
  
  ModelResolver getModelResolver();
  
  ModelBuildingRequest setModelResolver(ModelResolver paramModelResolver);
  
  ModelBuildingListener getModelBuildingListener();
  
  ModelBuildingRequest setModelBuildingListener(ModelBuildingListener paramModelBuildingListener);
  
  ModelCache getModelCache();
  
  ModelBuildingRequest setModelCache(ModelCache paramModelCache);
  
  WorkspaceModelResolver getWorkspaceModelResolver();
  
  ModelBuildingRequest setWorkspaceModelResolver(WorkspaceModelResolver paramWorkspaceModelResolver);
}
