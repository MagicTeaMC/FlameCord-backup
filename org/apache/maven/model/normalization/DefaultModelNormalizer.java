package org.apache.maven.model.normalization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.merge.MavenModelMerger;
import org.codehaus.plexus.util.StringUtils;

@Named
@Singleton
public class DefaultModelNormalizer implements ModelNormalizer {
  private DuplicateMerger merger = new DuplicateMerger();
  
  public void mergeDuplicates(Model model, ModelBuildingRequest request, ModelProblemCollector problems) {
    Build build = model.getBuild();
    if (build != null) {
      List<Plugin> plugins = build.getPlugins();
      Map<Object, Plugin> map = new LinkedHashMap<>(plugins.size() * 2);
      for (Plugin plugin : plugins) {
        Object key = plugin.getKey();
        Plugin first = map.get(key);
        if (first != null)
          this.merger.mergePlugin(plugin, first); 
        map.put(key, plugin);
      } 
      if (plugins.size() != map.size())
        build.setPlugins(new ArrayList(map.values())); 
    } 
    List<Dependency> dependencies = model.getDependencies();
    Map<String, Dependency> normalized = new LinkedHashMap<>(dependencies.size() * 2);
    for (Dependency dependency : dependencies)
      normalized.put(dependency.getManagementKey(), dependency); 
    if (dependencies.size() != normalized.size())
      model.setDependencies(new ArrayList(normalized.values())); 
  }
  
  protected static class DuplicateMerger extends MavenModelMerger {
    public void mergePlugin(Plugin target, Plugin source) {
      mergePlugin(target, source, false, Collections.emptyMap());
    }
  }
  
  public void injectDefaultValues(Model model, ModelBuildingRequest request, ModelProblemCollector problems) {
    injectDependencyDefaults(model.getDependencies());
    Build build = model.getBuild();
    if (build != null)
      for (Plugin plugin : build.getPlugins())
        injectDependencyDefaults(plugin.getDependencies());  
  }
  
  private void injectDependencyDefaults(List<Dependency> dependencies) {
    for (Dependency dependency : dependencies) {
      if (StringUtils.isEmpty(dependency.getScope()))
        dependency.setScope("compile"); 
    } 
  }
}
