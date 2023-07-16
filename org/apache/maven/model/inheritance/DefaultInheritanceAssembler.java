package org.apache.maven.model.inheritance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginContainer;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.merge.MavenModelMerger;
import org.codehaus.plexus.util.StringUtils;

@Named
@Singleton
public class DefaultInheritanceAssembler implements InheritanceAssembler {
  private InheritanceModelMerger merger = new InheritanceModelMerger();
  
  private static final String CHILD_DIRECTORY = "child-directory";
  
  private static final String CHILD_DIRECTORY_PROPERTY = "project.directory";
  
  public void assembleModelInheritance(Model child, Model parent, ModelBuildingRequest request, ModelProblemCollector problems) {
    Map<Object, Object> hints = new HashMap<>();
    String childPath = child.getProperties().getProperty("project.directory", child.getArtifactId());
    hints.put("child-directory", childPath);
    hints.put("child-path-adjustment", getChildPathAdjustment(child, parent, childPath));
    this.merger.merge(child, parent, false, hints);
  }
  
  private String getChildPathAdjustment(Model child, Model parent, String childDirectory) {
    String adjustment = "";
    if (parent != null) {
      String childName = child.getArtifactId();
      if (child.getProjectDirectory() != null)
        childName = child.getProjectDirectory().getName(); 
      for (String module : parent.getModules()) {
        module = module.replace('\\', '/');
        if (module.regionMatches(true, module.length() - 4, ".xml", 0, 4))
          module = module.substring(0, module.lastIndexOf('/') + 1); 
        String moduleName = module;
        if (moduleName.endsWith("/"))
          moduleName = moduleName.substring(0, moduleName.length() - 1); 
        int lastSlash = moduleName.lastIndexOf('/');
        moduleName = moduleName.substring(lastSlash + 1);
        if ((moduleName.equals(childName) || moduleName.equals(childDirectory)) && lastSlash >= 0) {
          adjustment = module.substring(0, lastSlash);
          break;
        } 
      } 
    } 
    return adjustment;
  }
  
  protected static class InheritanceModelMerger extends MavenModelMerger {
    protected String extrapolateChildUrl(String parentUrl, boolean appendPath, Map<Object, Object> context) {
      Object childDirectory = context.get("child-directory");
      Object childPathAdjustment = context.get("child-path-adjustment");
      if (StringUtils.isBlank(parentUrl) || childDirectory == null || childPathAdjustment == null || !appendPath)
        return parentUrl; 
      return appendPath(parentUrl, childDirectory.toString(), childPathAdjustment.toString());
    }
    
    private String appendPath(String parentUrl, String childPath, String pathAdjustment) {
      StringBuilder url = new StringBuilder(parentUrl.length() + pathAdjustment.length() + childPath.length() + ((pathAdjustment.length() == 0) ? 1 : 2));
      url.append(parentUrl);
      concatPath(url, pathAdjustment);
      concatPath(url, childPath);
      return url.toString();
    }
    
    private void concatPath(StringBuilder url, String path) {
      if (path.length() > 0) {
        boolean initialUrlEndsWithSlash = (url.charAt(url.length() - 1) == '/');
        boolean pathStartsWithSlash = (path.charAt(0) == '/');
        if (pathStartsWithSlash) {
          if (initialUrlEndsWithSlash)
            url.setLength(url.length() - 1); 
        } else if (!initialUrlEndsWithSlash) {
          url.append('/');
        } 
        url.append(path);
        if (initialUrlEndsWithSlash && !path.endsWith("/"))
          url.append('/'); 
      } 
    }
    
    protected void mergeModelBase_Properties(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
      Properties merged = new Properties();
      if (sourceDominant) {
        merged.putAll(target.getProperties());
        putAll(merged, source.getProperties(), "project.directory");
      } else {
        putAll(merged, source.getProperties(), "project.directory");
        merged.putAll(target.getProperties());
      } 
      target.setProperties(merged);
      target.setLocation("properties", 
          InputLocation.merge(target.getLocation("properties"), source
            .getLocation("properties"), sourceDominant));
    }
    
    private void putAll(Map<Object, Object> s, Map<Object, Object> t, Object excludeKey) {
      for (Map.Entry<Object, Object> e : t.entrySet()) {
        if (!e.getKey().equals(excludeKey))
          s.put(e.getKey(), e.getValue()); 
      } 
    }
    
    protected void mergePluginContainer_Plugins(PluginContainer target, PluginContainer source, boolean sourceDominant, Map<Object, Object> context) {
      List<Plugin> src = source.getPlugins();
      if (!src.isEmpty()) {
        List<Plugin> tgt = target.getPlugins();
        Map<Object, Plugin> master = new LinkedHashMap<>(src.size() * 2);
        for (Plugin element : src) {
          if (element.isInherited() || !element.getExecutions().isEmpty()) {
            Plugin plugin = new Plugin();
            plugin.setLocation("", element.getLocation(""));
            plugin.setGroupId(null);
            mergePlugin(plugin, element, sourceDominant, context);
            Object key = getPluginKey(element);
            master.put(key, plugin);
          } 
        } 
        Map<Object, List<Plugin>> predecessors = new LinkedHashMap<>();
        List<Plugin> pending = new ArrayList<>();
        for (Plugin element : tgt) {
          Object key = getPluginKey(element);
          Plugin existing = master.get(key);
          if (existing != null) {
            mergePlugin(element, existing, sourceDominant, context);
            master.put(key, element);
            if (!pending.isEmpty()) {
              predecessors.put(key, pending);
              pending = new ArrayList<>();
            } 
            continue;
          } 
          pending.add(element);
        } 
        List<Plugin> result = new ArrayList<>(src.size() + tgt.size());
        for (Map.Entry<Object, Plugin> entry : master.entrySet()) {
          List<Plugin> pre = predecessors.get(entry.getKey());
          if (pre != null)
            result.addAll(pre); 
          result.add(entry.getValue());
        } 
        result.addAll(pending);
        target.setPlugins(result);
      } 
    }
    
    protected void mergePlugin(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
      if (source.isInherited())
        mergeConfigurationContainer((ConfigurationContainer)target, (ConfigurationContainer)source, sourceDominant, context); 
      mergePlugin_GroupId(target, source, sourceDominant, context);
      mergePlugin_ArtifactId(target, source, sourceDominant, context);
      mergePlugin_Version(target, source, sourceDominant, context);
      mergePlugin_Extensions(target, source, sourceDominant, context);
      mergePlugin_Dependencies(target, source, sourceDominant, context);
      mergePlugin_Executions(target, source, sourceDominant, context);
    }
    
    protected void mergeReporting_Plugins(Reporting target, Reporting source, boolean sourceDominant, Map<Object, Object> context) {
      List<ReportPlugin> src = source.getPlugins();
      if (!src.isEmpty()) {
        List<ReportPlugin> tgt = target.getPlugins();
        Map<Object, ReportPlugin> merged = new LinkedHashMap<>((src.size() + tgt.size()) * 2);
        for (ReportPlugin element : src) {
          Object key = getReportPluginKey(element);
          if (element.isInherited()) {
            ReportPlugin plugin = new ReportPlugin();
            plugin.setLocation("", element.getLocation(""));
            plugin.setGroupId(null);
            mergeReportPlugin(plugin, element, sourceDominant, context);
            merged.put(key, plugin);
          } 
        } 
        for (ReportPlugin element : tgt) {
          Object key = getReportPluginKey(element);
          ReportPlugin existing = merged.get(key);
          if (existing != null)
            mergeReportPlugin(element, existing, sourceDominant, context); 
          merged.put(key, element);
        } 
        target.setPlugins(new ArrayList(merged.values()));
      } 
    }
  }
}
