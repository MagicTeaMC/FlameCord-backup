package org.apache.maven.model.plugin;

import java.util.List;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;
import org.codehaus.plexus.util.xml.Xpp3Dom;

@Named
@Singleton
public class DefaultPluginConfigurationExpander implements PluginConfigurationExpander {
  public void expandPluginConfiguration(Model model, ModelBuildingRequest request, ModelProblemCollector problems) {
    Build build = model.getBuild();
    if (build != null) {
      expand(build.getPlugins());
      PluginManagement pluginManagement = build.getPluginManagement();
      if (pluginManagement != null)
        expand(pluginManagement.getPlugins()); 
    } 
  }
  
  private void expand(List<Plugin> plugins) {
    for (Plugin plugin : plugins) {
      Xpp3Dom pluginConfiguration = (Xpp3Dom)plugin.getConfiguration();
      if (pluginConfiguration != null)
        for (PluginExecution execution : plugin.getExecutions()) {
          Xpp3Dom executionConfiguration = (Xpp3Dom)execution.getConfiguration();
          executionConfiguration = Xpp3Dom.mergeXpp3Dom(executionConfiguration, new Xpp3Dom(pluginConfiguration));
          execution.setConfiguration(executionConfiguration);
        }  
    } 
  }
}
