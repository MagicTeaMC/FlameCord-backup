package org.apache.maven.model.plugin;

import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Build;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputSource;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.ReportSet;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.building.ModelProblemCollectorRequest;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;

@Named
@Singleton
public class DefaultReportingConverter implements ReportingConverter {
  private final InputLocation location;
  
  public DefaultReportingConverter() {
    String modelId = "org.apache.maven:maven-model-builder:" + getClass().getPackage().getImplementationVersion() + ":reporting-converter";
    InputSource inputSource = new InputSource();
    inputSource.setModelId(modelId);
    this.location = new InputLocation(-1, -1, inputSource);
    this.location.setLocation(Integer.valueOf(0), this.location);
  }
  
  public void convertReporting(Model model, ModelBuildingRequest request, ModelProblemCollector problems) {
    Reporting reporting = model.getReporting();
    if (reporting == null)
      return; 
    Build build = model.getBuild();
    if (build == null) {
      build = new Build();
      model.setBuild(build);
      model.setLocation("build", this.location);
    } 
    Plugin sitePlugin = findSitePlugin(build);
    if (sitePlugin == null) {
      sitePlugin = new Plugin();
      sitePlugin.setArtifactId("maven-site-plugin");
      sitePlugin.setLocation("artifactId", this.location);
      PluginManagement pluginManagement = build.getPluginManagement();
      if (pluginManagement == null) {
        pluginManagement = new PluginManagement();
        build.setPluginManagement(pluginManagement);
      } 
      pluginManagement.addPlugin(sitePlugin);
    } 
    Xpp3Dom configuration = (Xpp3Dom)sitePlugin.getConfiguration();
    if (configuration == null) {
      configuration = new Xpp3Dom("configuration", this.location);
      sitePlugin.setConfiguration(configuration);
    } 
    Xpp3Dom reportPlugins = configuration.getChild("reportPlugins");
    if (reportPlugins != null) {
      problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.WARNING, ModelProblem.Version.BASE))
          .setMessage("Reporting configuration should be done in <reporting> section, not in maven-site-plugin <configuration> as reportPlugins parameter.")
          
          .setLocation(sitePlugin.getLocation("configuration")));
      return;
    } 
    if (configuration.getChild("outputDirectory") == null)
      addDom(configuration, "outputDirectory", reporting.getOutputDirectory(), reporting
          .getLocation("outputDirectory")); 
    reportPlugins = new Xpp3Dom("reportPlugins", this.location);
    configuration.addChild(reportPlugins);
    boolean hasMavenProjectInfoReportsPlugin = false;
    for (ReportPlugin plugin : reporting.getPlugins()) {
      Xpp3Dom reportPlugin = convert(plugin);
      reportPlugins.addChild(reportPlugin);
      if (!reporting.isExcludeDefaults() && !hasMavenProjectInfoReportsPlugin && "org.apache.maven.plugins"
        .equals(plugin.getGroupId()) && "maven-project-info-reports-plugin"
        .equals(plugin.getArtifactId()))
        hasMavenProjectInfoReportsPlugin = true; 
    } 
    if (!reporting.isExcludeDefaults() && !hasMavenProjectInfoReportsPlugin) {
      Xpp3Dom dom = new Xpp3Dom("reportPlugin", this.location);
      addDom(dom, "groupId", "org.apache.maven.plugins");
      addDom(dom, "artifactId", "maven-project-info-reports-plugin");
      reportPlugins.addChild(dom);
    } 
  }
  
  private Plugin findSitePlugin(Build build) {
    for (Plugin plugin : build.getPlugins()) {
      if (isSitePlugin(plugin))
        return plugin; 
    } 
    PluginManagement pluginManagement = build.getPluginManagement();
    if (pluginManagement != null)
      for (Plugin plugin : pluginManagement.getPlugins()) {
        if (isSitePlugin(plugin))
          return plugin; 
      }  
    return null;
  }
  
  private boolean isSitePlugin(Plugin plugin) {
    return ("maven-site-plugin".equals(plugin.getArtifactId()) && "org.apache.maven.plugins"
      .equals(plugin.getGroupId()));
  }
  
  private Xpp3Dom convert(ReportPlugin plugin) {
    Xpp3Dom dom = new Xpp3Dom("reportPlugin", plugin.getLocation(""));
    addDom(dom, "groupId", plugin.getGroupId(), plugin.getLocation("groupId"));
    addDom(dom, "artifactId", plugin.getArtifactId(), plugin.getLocation("artifactId"));
    addDom(dom, "version", plugin.getVersion(), plugin.getLocation("version"));
    Xpp3Dom configuration = (Xpp3Dom)plugin.getConfiguration();
    if (configuration != null) {
      configuration = new Xpp3Dom(configuration);
      dom.addChild(configuration);
    } 
    if (!plugin.getReportSets().isEmpty()) {
      Xpp3Dom reportSets = new Xpp3Dom("reportSets", plugin.getLocation("reportSets"));
      for (ReportSet reportSet : plugin.getReportSets()) {
        Xpp3Dom rs = convert(reportSet);
        reportSets.addChild(rs);
      } 
      dom.addChild(reportSets);
    } 
    return dom;
  }
  
  private Xpp3Dom convert(ReportSet reportSet) {
    Xpp3Dom dom = new Xpp3Dom("reportSet", reportSet.getLocation(""));
    InputLocation idLocation = reportSet.getLocation("id");
    addDom(dom, "id", reportSet.getId(), (idLocation == null) ? this.location : idLocation);
    Xpp3Dom configuration = (Xpp3Dom)reportSet.getConfiguration();
    if (configuration != null) {
      configuration = new Xpp3Dom(configuration);
      dom.addChild(configuration);
    } 
    if (!reportSet.getReports().isEmpty()) {
      InputLocation location = reportSet.getLocation("reports");
      Xpp3Dom reports = new Xpp3Dom("reports", location);
      int n = 0;
      for (String report : reportSet.getReports())
        addDom(reports, "report", report, (location == null) ? null : location.getLocation(Integer.valueOf(n++))); 
      dom.addChild(reports);
    } 
    return dom;
  }
  
  private void addDom(Xpp3Dom parent, String childName, String childValue) {
    addDom(parent, childName, childValue, this.location);
  }
  
  private void addDom(Xpp3Dom parent, String childName, String childValue, InputLocation location) {
    if (StringUtils.isNotEmpty(childValue))
      parent.addChild(newDom(childName, childValue, location)); 
  }
  
  private Xpp3Dom newDom(String name, String value, InputLocation location) {
    Xpp3Dom dom = new Xpp3Dom(name, location);
    dom.setValue(value);
    return dom;
  }
}
