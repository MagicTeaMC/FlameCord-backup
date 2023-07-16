package org.apache.maven.model.building;

import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;

interface ModelCacheTag<T> {
  public static final ModelCacheTag<ModelData> RAW = new ModelCacheTag<ModelData>() {
      public String getName() {
        return "raw";
      }
      
      public Class<ModelData> getType() {
        return ModelData.class;
      }
      
      public ModelData intoCache(ModelData data) {
        Model model = (data.getModel() != null) ? data.getModel().clone() : null;
        return new ModelData(data.getSource(), model, data.getGroupId(), data.getArtifactId(), data.getVersion());
      }
      
      public ModelData fromCache(ModelData data) {
        return intoCache(data);
      }
    };
  
  public static final ModelCacheTag<DependencyManagement> IMPORT = new ModelCacheTag<DependencyManagement>() {
      public String getName() {
        return "import";
      }
      
      public Class<DependencyManagement> getType() {
        return DependencyManagement.class;
      }
      
      public DependencyManagement intoCache(DependencyManagement data) {
        return (data != null) ? data.clone() : null;
      }
      
      public DependencyManagement fromCache(DependencyManagement data) {
        return intoCache(data);
      }
    };
  
  String getName();
  
  Class<T> getType();
  
  T intoCache(T paramT);
  
  T fromCache(T paramT);
}
