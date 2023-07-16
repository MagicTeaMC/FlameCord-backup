package org.apache.maven.model.composition;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;

@Named
@Singleton
public class DefaultDependencyManagementImporter implements DependencyManagementImporter {
  public void importManagement(Model target, List<? extends DependencyManagement> sources, ModelBuildingRequest request, ModelProblemCollector problems) {
    if (sources != null && !sources.isEmpty()) {
      Map<String, Dependency> dependencies = new LinkedHashMap<>();
      DependencyManagement depMgmt = target.getDependencyManagement();
      if (depMgmt != null) {
        for (Dependency dependency : depMgmt.getDependencies())
          dependencies.put(dependency.getManagementKey(), dependency); 
      } else {
        depMgmt = new DependencyManagement();
        target.setDependencyManagement(depMgmt);
      } 
      for (DependencyManagement source : sources) {
        for (Dependency dependency : source.getDependencies()) {
          String key = dependency.getManagementKey();
          if (!dependencies.containsKey(key))
            dependencies.put(key, dependency); 
        } 
      } 
      depMgmt.setDependencies(new ArrayList(dependencies.values()));
    } 
  }
}
