package org.apache.maven.model.management;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.merge.MavenModelMerger;

@Named
@Singleton
public class DefaultDependencyManagementInjector implements DependencyManagementInjector {
  private ManagementModelMerger merger = new ManagementModelMerger();
  
  public void injectManagement(Model model, ModelBuildingRequest request, ModelProblemCollector problems) {
    this.merger.mergeManagedDependencies(model);
  }
  
  protected static class ManagementModelMerger extends MavenModelMerger {
    public void mergeManagedDependencies(Model model) {
      DependencyManagement dependencyManagement = model.getDependencyManagement();
      if (dependencyManagement != null) {
        Map<Object, Dependency> dependencies = new HashMap<>();
        Map<Object, Object> context = Collections.emptyMap();
        for (Dependency dependency : model.getDependencies()) {
          Object key = getDependencyKey(dependency);
          dependencies.put(key, dependency);
        } 
        for (Dependency managedDependency : dependencyManagement.getDependencies()) {
          Object key = getDependencyKey(managedDependency);
          Dependency dependency = dependencies.get(key);
          if (dependency != null)
            mergeDependency(dependency, managedDependency, false, context); 
        } 
      } 
    }
    
    protected void mergeDependency_Optional(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {}
    
    protected void mergeDependency_Exclusions(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
      List<Exclusion> tgt = target.getExclusions();
      if (tgt.isEmpty()) {
        List<Exclusion> src = source.getExclusions();
        for (Exclusion element : src) {
          Exclusion clone = element.clone();
          target.addExclusion(clone);
        } 
      } 
    }
  }
}
