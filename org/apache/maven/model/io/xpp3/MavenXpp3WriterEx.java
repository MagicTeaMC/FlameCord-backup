package org.apache.maven.model.io.xpp3;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.Contributor;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.Developer;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Extension;
import org.apache.maven.model.FileSet;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputLocationTracker;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.License;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Model;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.Notifier;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Parent;
import org.apache.maven.model.PatternSet;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginConfiguration;
import org.apache.maven.model.PluginContainer;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Prerequisites;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Relocation;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.ReportSet;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryBase;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.model.Resource;
import org.apache.maven.model.Scm;
import org.apache.maven.model.Site;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.MXSerializer;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;

public class MavenXpp3WriterEx {
  private static final String NAMESPACE = null;
  
  private String fileComment = null;
  
  protected InputLocation.StringFormatter stringFormatter;
  
  public void setFileComment(String fileComment) {
    this.fileComment = fileComment;
  }
  
  public void setStringFormatter(InputLocation.StringFormatter stringFormatter) {
    this.stringFormatter = stringFormatter;
  }
  
  protected String toString(InputLocation location) {
    if (this.stringFormatter != null)
      return this.stringFormatter.toString(location); 
    return ' ' + location.getSource().toString() + ':' + location.getLineNumber() + ' ';
  }
  
  public void write(Writer writer, Model model) throws IOException {
    MXSerializer mXSerializer = new MXSerializer();
    mXSerializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
    mXSerializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
    mXSerializer.setOutput(writer);
    mXSerializer.startDocument(model.getModelEncoding(), null);
    writeModel(model, "project", (XmlSerializer)mXSerializer);
    mXSerializer.endDocument();
  }
  
  public void write(OutputStream stream, Model model) throws IOException {
    MXSerializer mXSerializer = new MXSerializer();
    mXSerializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
    mXSerializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
    mXSerializer.setOutput(stream, model.getModelEncoding());
    mXSerializer.startDocument(model.getModelEncoding(), null);
    writeModel(model, "project", (XmlSerializer)mXSerializer);
    mXSerializer.endDocument();
  }
  
  protected void writeXpp3DomToSerializer(Xpp3Dom dom, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, dom.getName());
    String[] attributeNames = dom.getAttributeNames();
    for (String attributeName : attributeNames)
      serializer.attribute(NAMESPACE, attributeName, dom.getAttribute(attributeName)); 
    for (Xpp3Dom aChild : dom.getChildren())
      writeXpp3DomToSerializer(aChild, serializer); 
    String value = dom.getValue();
    if (value != null)
      serializer.text(value); 
    serializer.endTag(NAMESPACE, dom.getName());
    if (dom.getInputLocation() != null && dom.getChildCount() == 0)
      serializer.comment(toString((InputLocation)dom.getInputLocation())); 
  }
  
  private void writeActivation(Activation activation, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (activation.isActiveByDefault()) {
      serializer.startTag(NAMESPACE, "activeByDefault").text(String.valueOf(activation.isActiveByDefault())).endTag(NAMESPACE, "activeByDefault");
      writeLocationTracking((InputLocationTracker)activation, "activeByDefault", serializer);
    } 
    if (activation.getJdk() != null) {
      serializer.startTag(NAMESPACE, "jdk").text(activation.getJdk()).endTag(NAMESPACE, "jdk");
      writeLocationTracking((InputLocationTracker)activation, "jdk", serializer);
    } 
    if (activation.getOs() != null)
      writeActivationOS(activation.getOs(), "os", serializer); 
    if (activation.getProperty() != null)
      writeActivationProperty(activation.getProperty(), "property", serializer); 
    if (activation.getFile() != null)
      writeActivationFile(activation.getFile(), "file", serializer); 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeActivationFile(ActivationFile activationFile, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (activationFile.getMissing() != null) {
      serializer.startTag(NAMESPACE, "missing").text(activationFile.getMissing()).endTag(NAMESPACE, "missing");
      writeLocationTracking((InputLocationTracker)activationFile, "missing", serializer);
    } 
    if (activationFile.getExists() != null) {
      serializer.startTag(NAMESPACE, "exists").text(activationFile.getExists()).endTag(NAMESPACE, "exists");
      writeLocationTracking((InputLocationTracker)activationFile, "exists", serializer);
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeActivationOS(ActivationOS activationOS, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (activationOS.getName() != null) {
      serializer.startTag(NAMESPACE, "name").text(activationOS.getName()).endTag(NAMESPACE, "name");
      writeLocationTracking((InputLocationTracker)activationOS, "name", serializer);
    } 
    if (activationOS.getFamily() != null) {
      serializer.startTag(NAMESPACE, "family").text(activationOS.getFamily()).endTag(NAMESPACE, "family");
      writeLocationTracking((InputLocationTracker)activationOS, "family", serializer);
    } 
    if (activationOS.getArch() != null) {
      serializer.startTag(NAMESPACE, "arch").text(activationOS.getArch()).endTag(NAMESPACE, "arch");
      writeLocationTracking((InputLocationTracker)activationOS, "arch", serializer);
    } 
    if (activationOS.getVersion() != null) {
      serializer.startTag(NAMESPACE, "version").text(activationOS.getVersion()).endTag(NAMESPACE, "version");
      writeLocationTracking((InputLocationTracker)activationOS, "version", serializer);
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeActivationProperty(ActivationProperty activationProperty, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (activationProperty.getName() != null) {
      serializer.startTag(NAMESPACE, "name").text(activationProperty.getName()).endTag(NAMESPACE, "name");
      writeLocationTracking((InputLocationTracker)activationProperty, "name", serializer);
    } 
    if (activationProperty.getValue() != null) {
      serializer.startTag(NAMESPACE, "value").text(activationProperty.getValue()).endTag(NAMESPACE, "value");
      writeLocationTracking((InputLocationTracker)activationProperty, "value", serializer);
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeBuild(Build build, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (build.getSourceDirectory() != null) {
      serializer.startTag(NAMESPACE, "sourceDirectory").text(build.getSourceDirectory()).endTag(NAMESPACE, "sourceDirectory");
      writeLocationTracking((InputLocationTracker)build, "sourceDirectory", serializer);
    } 
    if (build.getScriptSourceDirectory() != null) {
      serializer.startTag(NAMESPACE, "scriptSourceDirectory").text(build.getScriptSourceDirectory()).endTag(NAMESPACE, "scriptSourceDirectory");
      writeLocationTracking((InputLocationTracker)build, "scriptSourceDirectory", serializer);
    } 
    if (build.getTestSourceDirectory() != null) {
      serializer.startTag(NAMESPACE, "testSourceDirectory").text(build.getTestSourceDirectory()).endTag(NAMESPACE, "testSourceDirectory");
      writeLocationTracking((InputLocationTracker)build, "testSourceDirectory", serializer);
    } 
    if (build.getOutputDirectory() != null) {
      serializer.startTag(NAMESPACE, "outputDirectory").text(build.getOutputDirectory()).endTag(NAMESPACE, "outputDirectory");
      writeLocationTracking((InputLocationTracker)build, "outputDirectory", serializer);
    } 
    if (build.getTestOutputDirectory() != null) {
      serializer.startTag(NAMESPACE, "testOutputDirectory").text(build.getTestOutputDirectory()).endTag(NAMESPACE, "testOutputDirectory");
      writeLocationTracking((InputLocationTracker)build, "testOutputDirectory", serializer);
    } 
    if (build.getExtensions() != null && build.getExtensions().size() > 0) {
      serializer.startTag(NAMESPACE, "extensions");
      for (Iterator<Extension> iter = build.getExtensions().iterator(); iter.hasNext(); ) {
        Extension o = iter.next();
        writeExtension(o, "extension", serializer);
      } 
      serializer.endTag(NAMESPACE, "extensions");
    } 
    if (build.getDefaultGoal() != null) {
      serializer.startTag(NAMESPACE, "defaultGoal").text(build.getDefaultGoal()).endTag(NAMESPACE, "defaultGoal");
      writeLocationTracking((InputLocationTracker)build, "defaultGoal", serializer);
    } 
    if (build.getResources() != null && build.getResources().size() > 0) {
      serializer.startTag(NAMESPACE, "resources");
      for (Iterator<Resource> iter = build.getResources().iterator(); iter.hasNext(); ) {
        Resource o = iter.next();
        writeResource(o, "resource", serializer);
      } 
      serializer.endTag(NAMESPACE, "resources");
    } 
    if (build.getTestResources() != null && build.getTestResources().size() > 0) {
      serializer.startTag(NAMESPACE, "testResources");
      for (Iterator<Resource> iter = build.getTestResources().iterator(); iter.hasNext(); ) {
        Resource o = iter.next();
        writeResource(o, "testResource", serializer);
      } 
      serializer.endTag(NAMESPACE, "testResources");
    } 
    if (build.getDirectory() != null) {
      serializer.startTag(NAMESPACE, "directory").text(build.getDirectory()).endTag(NAMESPACE, "directory");
      writeLocationTracking((InputLocationTracker)build, "directory", serializer);
    } 
    if (build.getFinalName() != null) {
      serializer.startTag(NAMESPACE, "finalName").text(build.getFinalName()).endTag(NAMESPACE, "finalName");
      writeLocationTracking((InputLocationTracker)build, "finalName", serializer);
    } 
    if (build.getFilters() != null && build.getFilters().size() > 0) {
      serializer.startTag(NAMESPACE, "filters");
      InputLocation location = build.getLocation("filters");
      int n = 0;
      for (Iterator<String> iter = build.getFilters().iterator(); iter.hasNext(); ) {
        String filter = iter.next();
        serializer.startTag(NAMESPACE, "filter").text(filter).endTag(NAMESPACE, "filter");
        writeLocationTracking((InputLocationTracker)location, Integer.valueOf(n++), serializer);
      } 
      serializer.endTag(NAMESPACE, "filters");
    } 
    if (build.getPluginManagement() != null)
      writePluginManagement(build.getPluginManagement(), "pluginManagement", serializer); 
    if (build.getPlugins() != null && build.getPlugins().size() > 0) {
      serializer.startTag(NAMESPACE, "plugins");
      for (Iterator<Plugin> iter = build.getPlugins().iterator(); iter.hasNext(); ) {
        Plugin o = iter.next();
        writePlugin(o, "plugin", serializer);
      } 
      serializer.endTag(NAMESPACE, "plugins");
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeBuildBase(BuildBase buildBase, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (buildBase.getDefaultGoal() != null) {
      serializer.startTag(NAMESPACE, "defaultGoal").text(buildBase.getDefaultGoal()).endTag(NAMESPACE, "defaultGoal");
      writeLocationTracking((InputLocationTracker)buildBase, "defaultGoal", serializer);
    } 
    if (buildBase.getResources() != null && buildBase.getResources().size() > 0) {
      serializer.startTag(NAMESPACE, "resources");
      for (Iterator<Resource> iter = buildBase.getResources().iterator(); iter.hasNext(); ) {
        Resource o = iter.next();
        writeResource(o, "resource", serializer);
      } 
      serializer.endTag(NAMESPACE, "resources");
    } 
    if (buildBase.getTestResources() != null && buildBase.getTestResources().size() > 0) {
      serializer.startTag(NAMESPACE, "testResources");
      for (Iterator<Resource> iter = buildBase.getTestResources().iterator(); iter.hasNext(); ) {
        Resource o = iter.next();
        writeResource(o, "testResource", serializer);
      } 
      serializer.endTag(NAMESPACE, "testResources");
    } 
    if (buildBase.getDirectory() != null) {
      serializer.startTag(NAMESPACE, "directory").text(buildBase.getDirectory()).endTag(NAMESPACE, "directory");
      writeLocationTracking((InputLocationTracker)buildBase, "directory", serializer);
    } 
    if (buildBase.getFinalName() != null) {
      serializer.startTag(NAMESPACE, "finalName").text(buildBase.getFinalName()).endTag(NAMESPACE, "finalName");
      writeLocationTracking((InputLocationTracker)buildBase, "finalName", serializer);
    } 
    if (buildBase.getFilters() != null && buildBase.getFilters().size() > 0) {
      serializer.startTag(NAMESPACE, "filters");
      InputLocation location = buildBase.getLocation("filters");
      int n = 0;
      for (Iterator<String> iter = buildBase.getFilters().iterator(); iter.hasNext(); ) {
        String filter = iter.next();
        serializer.startTag(NAMESPACE, "filter").text(filter).endTag(NAMESPACE, "filter");
        writeLocationTracking((InputLocationTracker)location, Integer.valueOf(n++), serializer);
      } 
      serializer.endTag(NAMESPACE, "filters");
    } 
    if (buildBase.getPluginManagement() != null)
      writePluginManagement(buildBase.getPluginManagement(), "pluginManagement", serializer); 
    if (buildBase.getPlugins() != null && buildBase.getPlugins().size() > 0) {
      serializer.startTag(NAMESPACE, "plugins");
      for (Iterator<Plugin> iter = buildBase.getPlugins().iterator(); iter.hasNext(); ) {
        Plugin o = iter.next();
        writePlugin(o, "plugin", serializer);
      } 
      serializer.endTag(NAMESPACE, "plugins");
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeCiManagement(CiManagement ciManagement, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (ciManagement.getSystem() != null) {
      serializer.startTag(NAMESPACE, "system").text(ciManagement.getSystem()).endTag(NAMESPACE, "system");
      writeLocationTracking((InputLocationTracker)ciManagement, "system", serializer);
    } 
    if (ciManagement.getUrl() != null) {
      serializer.startTag(NAMESPACE, "url").text(ciManagement.getUrl()).endTag(NAMESPACE, "url");
      writeLocationTracking((InputLocationTracker)ciManagement, "url", serializer);
    } 
    if (ciManagement.getNotifiers() != null && ciManagement.getNotifiers().size() > 0) {
      serializer.startTag(NAMESPACE, "notifiers");
      for (Iterator<Notifier> iter = ciManagement.getNotifiers().iterator(); iter.hasNext(); ) {
        Notifier o = iter.next();
        writeNotifier(o, "notifier", serializer);
      } 
      serializer.endTag(NAMESPACE, "notifiers");
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeConfigurationContainer(ConfigurationContainer configurationContainer, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (configurationContainer.getInherited() != null) {
      serializer.startTag(NAMESPACE, "inherited").text(configurationContainer.getInherited()).endTag(NAMESPACE, "inherited");
      writeLocationTracking((InputLocationTracker)configurationContainer, "inherited", serializer);
    } 
    if (configurationContainer.getConfiguration() != null)
      writeXpp3DomToSerializer((Xpp3Dom)configurationContainer.getConfiguration(), serializer); 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeContributor(Contributor contributor, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (contributor.getName() != null) {
      serializer.startTag(NAMESPACE, "name").text(contributor.getName()).endTag(NAMESPACE, "name");
      writeLocationTracking((InputLocationTracker)contributor, "name", serializer);
    } 
    if (contributor.getEmail() != null) {
      serializer.startTag(NAMESPACE, "email").text(contributor.getEmail()).endTag(NAMESPACE, "email");
      writeLocationTracking((InputLocationTracker)contributor, "email", serializer);
    } 
    if (contributor.getUrl() != null) {
      serializer.startTag(NAMESPACE, "url").text(contributor.getUrl()).endTag(NAMESPACE, "url");
      writeLocationTracking((InputLocationTracker)contributor, "url", serializer);
    } 
    if (contributor.getOrganization() != null) {
      serializer.startTag(NAMESPACE, "organization").text(contributor.getOrganization()).endTag(NAMESPACE, "organization");
      writeLocationTracking((InputLocationTracker)contributor, "organization", serializer);
    } 
    if (contributor.getOrganizationUrl() != null) {
      serializer.startTag(NAMESPACE, "organizationUrl").text(contributor.getOrganizationUrl()).endTag(NAMESPACE, "organizationUrl");
      writeLocationTracking((InputLocationTracker)contributor, "organizationUrl", serializer);
    } 
    if (contributor.getRoles() != null && contributor.getRoles().size() > 0) {
      serializer.startTag(NAMESPACE, "roles");
      InputLocation location = contributor.getLocation("roles");
      int n = 0;
      for (Iterator<String> iter = contributor.getRoles().iterator(); iter.hasNext(); ) {
        String role = iter.next();
        serializer.startTag(NAMESPACE, "role").text(role).endTag(NAMESPACE, "role");
        writeLocationTracking((InputLocationTracker)location, Integer.valueOf(n++), serializer);
      } 
      serializer.endTag(NAMESPACE, "roles");
    } 
    if (contributor.getTimezone() != null) {
      serializer.startTag(NAMESPACE, "timezone").text(contributor.getTimezone()).endTag(NAMESPACE, "timezone");
      writeLocationTracking((InputLocationTracker)contributor, "timezone", serializer);
    } 
    if (contributor.getProperties() != null && contributor.getProperties().size() > 0) {
      serializer.startTag(NAMESPACE, "properties");
      InputLocation location = contributor.getLocation("properties");
      for (Iterator<String> iter = contributor.getProperties().keySet().iterator(); iter.hasNext(); ) {
        String key = iter.next();
        String value = (String)contributor.getProperties().get(key);
        serializer.startTag(NAMESPACE, key).text(value).endTag(NAMESPACE, key);
        writeLocationTracking((InputLocationTracker)location, key, serializer);
      } 
      serializer.endTag(NAMESPACE, "properties");
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeDependency(Dependency dependency, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (dependency.getGroupId() != null) {
      serializer.startTag(NAMESPACE, "groupId").text(dependency.getGroupId()).endTag(NAMESPACE, "groupId");
      writeLocationTracking((InputLocationTracker)dependency, "groupId", serializer);
    } 
    if (dependency.getArtifactId() != null) {
      serializer.startTag(NAMESPACE, "artifactId").text(dependency.getArtifactId()).endTag(NAMESPACE, "artifactId");
      writeLocationTracking((InputLocationTracker)dependency, "artifactId", serializer);
    } 
    if (dependency.getVersion() != null) {
      serializer.startTag(NAMESPACE, "version").text(dependency.getVersion()).endTag(NAMESPACE, "version");
      writeLocationTracking((InputLocationTracker)dependency, "version", serializer);
    } 
    if (dependency.getType() != null && !dependency.getType().equals("jar")) {
      serializer.startTag(NAMESPACE, "type").text(dependency.getType()).endTag(NAMESPACE, "type");
      writeLocationTracking((InputLocationTracker)dependency, "type", serializer);
    } 
    if (dependency.getClassifier() != null) {
      serializer.startTag(NAMESPACE, "classifier").text(dependency.getClassifier()).endTag(NAMESPACE, "classifier");
      writeLocationTracking((InputLocationTracker)dependency, "classifier", serializer);
    } 
    if (dependency.getScope() != null) {
      serializer.startTag(NAMESPACE, "scope").text(dependency.getScope()).endTag(NAMESPACE, "scope");
      writeLocationTracking((InputLocationTracker)dependency, "scope", serializer);
    } 
    if (dependency.getSystemPath() != null) {
      serializer.startTag(NAMESPACE, "systemPath").text(dependency.getSystemPath()).endTag(NAMESPACE, "systemPath");
      writeLocationTracking((InputLocationTracker)dependency, "systemPath", serializer);
    } 
    if (dependency.getExclusions() != null && dependency.getExclusions().size() > 0) {
      serializer.startTag(NAMESPACE, "exclusions");
      for (Iterator<Exclusion> iter = dependency.getExclusions().iterator(); iter.hasNext(); ) {
        Exclusion o = iter.next();
        writeExclusion(o, "exclusion", serializer);
      } 
      serializer.endTag(NAMESPACE, "exclusions");
    } 
    if (dependency.getOptional() != null) {
      serializer.startTag(NAMESPACE, "optional").text(dependency.getOptional()).endTag(NAMESPACE, "optional");
      writeLocationTracking((InputLocationTracker)dependency, "optional", serializer);
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeDependencyManagement(DependencyManagement dependencyManagement, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (dependencyManagement.getDependencies() != null && dependencyManagement.getDependencies().size() > 0) {
      serializer.startTag(NAMESPACE, "dependencies");
      for (Iterator<Dependency> iter = dependencyManagement.getDependencies().iterator(); iter.hasNext(); ) {
        Dependency o = iter.next();
        writeDependency(o, "dependency", serializer);
      } 
      serializer.endTag(NAMESPACE, "dependencies");
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeDeploymentRepository(DeploymentRepository deploymentRepository, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (deploymentRepository.isUniqueVersion() != true) {
      serializer.startTag(NAMESPACE, "uniqueVersion").text(String.valueOf(deploymentRepository.isUniqueVersion())).endTag(NAMESPACE, "uniqueVersion");
      writeLocationTracking((InputLocationTracker)deploymentRepository, "uniqueVersion", serializer);
    } 
    if (deploymentRepository.getReleases() != null)
      writeRepositoryPolicy(deploymentRepository.getReleases(), "releases", serializer); 
    if (deploymentRepository.getSnapshots() != null)
      writeRepositoryPolicy(deploymentRepository.getSnapshots(), "snapshots", serializer); 
    if (deploymentRepository.getId() != null) {
      serializer.startTag(NAMESPACE, "id").text(deploymentRepository.getId()).endTag(NAMESPACE, "id");
      writeLocationTracking((InputLocationTracker)deploymentRepository, "id", serializer);
    } 
    if (deploymentRepository.getName() != null) {
      serializer.startTag(NAMESPACE, "name").text(deploymentRepository.getName()).endTag(NAMESPACE, "name");
      writeLocationTracking((InputLocationTracker)deploymentRepository, "name", serializer);
    } 
    if (deploymentRepository.getUrl() != null) {
      serializer.startTag(NAMESPACE, "url").text(deploymentRepository.getUrl()).endTag(NAMESPACE, "url");
      writeLocationTracking((InputLocationTracker)deploymentRepository, "url", serializer);
    } 
    if (deploymentRepository.getLayout() != null && !deploymentRepository.getLayout().equals("default")) {
      serializer.startTag(NAMESPACE, "layout").text(deploymentRepository.getLayout()).endTag(NAMESPACE, "layout");
      writeLocationTracking((InputLocationTracker)deploymentRepository, "layout", serializer);
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeDeveloper(Developer developer, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (developer.getId() != null) {
      serializer.startTag(NAMESPACE, "id").text(developer.getId()).endTag(NAMESPACE, "id");
      writeLocationTracking((InputLocationTracker)developer, "id", serializer);
    } 
    if (developer.getName() != null) {
      serializer.startTag(NAMESPACE, "name").text(developer.getName()).endTag(NAMESPACE, "name");
      writeLocationTracking((InputLocationTracker)developer, "name", serializer);
    } 
    if (developer.getEmail() != null) {
      serializer.startTag(NAMESPACE, "email").text(developer.getEmail()).endTag(NAMESPACE, "email");
      writeLocationTracking((InputLocationTracker)developer, "email", serializer);
    } 
    if (developer.getUrl() != null) {
      serializer.startTag(NAMESPACE, "url").text(developer.getUrl()).endTag(NAMESPACE, "url");
      writeLocationTracking((InputLocationTracker)developer, "url", serializer);
    } 
    if (developer.getOrganization() != null) {
      serializer.startTag(NAMESPACE, "organization").text(developer.getOrganization()).endTag(NAMESPACE, "organization");
      writeLocationTracking((InputLocationTracker)developer, "organization", serializer);
    } 
    if (developer.getOrganizationUrl() != null) {
      serializer.startTag(NAMESPACE, "organizationUrl").text(developer.getOrganizationUrl()).endTag(NAMESPACE, "organizationUrl");
      writeLocationTracking((InputLocationTracker)developer, "organizationUrl", serializer);
    } 
    if (developer.getRoles() != null && developer.getRoles().size() > 0) {
      serializer.startTag(NAMESPACE, "roles");
      InputLocation location = developer.getLocation("roles");
      int n = 0;
      for (Iterator<String> iter = developer.getRoles().iterator(); iter.hasNext(); ) {
        String role = iter.next();
        serializer.startTag(NAMESPACE, "role").text(role).endTag(NAMESPACE, "role");
        writeLocationTracking((InputLocationTracker)location, Integer.valueOf(n++), serializer);
      } 
      serializer.endTag(NAMESPACE, "roles");
    } 
    if (developer.getTimezone() != null) {
      serializer.startTag(NAMESPACE, "timezone").text(developer.getTimezone()).endTag(NAMESPACE, "timezone");
      writeLocationTracking((InputLocationTracker)developer, "timezone", serializer);
    } 
    if (developer.getProperties() != null && developer.getProperties().size() > 0) {
      serializer.startTag(NAMESPACE, "properties");
      InputLocation location = developer.getLocation("properties");
      for (Iterator<String> iter = developer.getProperties().keySet().iterator(); iter.hasNext(); ) {
        String key = iter.next();
        String value = (String)developer.getProperties().get(key);
        serializer.startTag(NAMESPACE, key).text(value).endTag(NAMESPACE, key);
        writeLocationTracking((InputLocationTracker)location, key, serializer);
      } 
      serializer.endTag(NAMESPACE, "properties");
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeDistributionManagement(DistributionManagement distributionManagement, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (distributionManagement.getRepository() != null)
      writeDeploymentRepository(distributionManagement.getRepository(), "repository", serializer); 
    if (distributionManagement.getSnapshotRepository() != null)
      writeDeploymentRepository(distributionManagement.getSnapshotRepository(), "snapshotRepository", serializer); 
    if (distributionManagement.getSite() != null)
      writeSite(distributionManagement.getSite(), "site", serializer); 
    if (distributionManagement.getDownloadUrl() != null) {
      serializer.startTag(NAMESPACE, "downloadUrl").text(distributionManagement.getDownloadUrl()).endTag(NAMESPACE, "downloadUrl");
      writeLocationTracking((InputLocationTracker)distributionManagement, "downloadUrl", serializer);
    } 
    if (distributionManagement.getRelocation() != null)
      writeRelocation(distributionManagement.getRelocation(), "relocation", serializer); 
    if (distributionManagement.getStatus() != null) {
      serializer.startTag(NAMESPACE, "status").text(distributionManagement.getStatus()).endTag(NAMESPACE, "status");
      writeLocationTracking((InputLocationTracker)distributionManagement, "status", serializer);
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeExclusion(Exclusion exclusion, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (exclusion.getGroupId() != null) {
      serializer.startTag(NAMESPACE, "groupId").text(exclusion.getGroupId()).endTag(NAMESPACE, "groupId");
      writeLocationTracking((InputLocationTracker)exclusion, "groupId", serializer);
    } 
    if (exclusion.getArtifactId() != null) {
      serializer.startTag(NAMESPACE, "artifactId").text(exclusion.getArtifactId()).endTag(NAMESPACE, "artifactId");
      writeLocationTracking((InputLocationTracker)exclusion, "artifactId", serializer);
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeExtension(Extension extension, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (extension.getGroupId() != null) {
      serializer.startTag(NAMESPACE, "groupId").text(extension.getGroupId()).endTag(NAMESPACE, "groupId");
      writeLocationTracking((InputLocationTracker)extension, "groupId", serializer);
    } 
    if (extension.getArtifactId() != null) {
      serializer.startTag(NAMESPACE, "artifactId").text(extension.getArtifactId()).endTag(NAMESPACE, "artifactId");
      writeLocationTracking((InputLocationTracker)extension, "artifactId", serializer);
    } 
    if (extension.getVersion() != null) {
      serializer.startTag(NAMESPACE, "version").text(extension.getVersion()).endTag(NAMESPACE, "version");
      writeLocationTracking((InputLocationTracker)extension, "version", serializer);
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeFileSet(FileSet fileSet, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (fileSet.getDirectory() != null) {
      serializer.startTag(NAMESPACE, "directory").text(fileSet.getDirectory()).endTag(NAMESPACE, "directory");
      writeLocationTracking((InputLocationTracker)fileSet, "directory", serializer);
    } 
    if (fileSet.getIncludes() != null && fileSet.getIncludes().size() > 0) {
      serializer.startTag(NAMESPACE, "includes");
      InputLocation location = fileSet.getLocation("includes");
      int n = 0;
      for (Iterator<String> iter = fileSet.getIncludes().iterator(); iter.hasNext(); ) {
        String include = iter.next();
        serializer.startTag(NAMESPACE, "include").text(include).endTag(NAMESPACE, "include");
        writeLocationTracking((InputLocationTracker)location, Integer.valueOf(n++), serializer);
      } 
      serializer.endTag(NAMESPACE, "includes");
    } 
    if (fileSet.getExcludes() != null && fileSet.getExcludes().size() > 0) {
      serializer.startTag(NAMESPACE, "excludes");
      InputLocation location = fileSet.getLocation("excludes");
      int n = 0;
      for (Iterator<String> iter = fileSet.getExcludes().iterator(); iter.hasNext(); ) {
        String exclude = iter.next();
        serializer.startTag(NAMESPACE, "exclude").text(exclude).endTag(NAMESPACE, "exclude");
        writeLocationTracking((InputLocationTracker)location, Integer.valueOf(n++), serializer);
      } 
      serializer.endTag(NAMESPACE, "excludes");
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeIssueManagement(IssueManagement issueManagement, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (issueManagement.getSystem() != null) {
      serializer.startTag(NAMESPACE, "system").text(issueManagement.getSystem()).endTag(NAMESPACE, "system");
      writeLocationTracking((InputLocationTracker)issueManagement, "system", serializer);
    } 
    if (issueManagement.getUrl() != null) {
      serializer.startTag(NAMESPACE, "url").text(issueManagement.getUrl()).endTag(NAMESPACE, "url");
      writeLocationTracking((InputLocationTracker)issueManagement, "url", serializer);
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeLicense(License license, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (license.getName() != null) {
      serializer.startTag(NAMESPACE, "name").text(license.getName()).endTag(NAMESPACE, "name");
      writeLocationTracking((InputLocationTracker)license, "name", serializer);
    } 
    if (license.getUrl() != null) {
      serializer.startTag(NAMESPACE, "url").text(license.getUrl()).endTag(NAMESPACE, "url");
      writeLocationTracking((InputLocationTracker)license, "url", serializer);
    } 
    if (license.getDistribution() != null) {
      serializer.startTag(NAMESPACE, "distribution").text(license.getDistribution()).endTag(NAMESPACE, "distribution");
      writeLocationTracking((InputLocationTracker)license, "distribution", serializer);
    } 
    if (license.getComments() != null) {
      serializer.startTag(NAMESPACE, "comments").text(license.getComments()).endTag(NAMESPACE, "comments");
      writeLocationTracking((InputLocationTracker)license, "comments", serializer);
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeLocationTracking(InputLocationTracker locationTracker, Object key, XmlSerializer serializer) throws IOException {
    InputLocation location = (locationTracker == null) ? null : locationTracker.getLocation(key);
    if (location != null)
      serializer.comment(toString(location)); 
  }
  
  private void writeMailingList(MailingList mailingList, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (mailingList.getName() != null) {
      serializer.startTag(NAMESPACE, "name").text(mailingList.getName()).endTag(NAMESPACE, "name");
      writeLocationTracking((InputLocationTracker)mailingList, "name", serializer);
    } 
    if (mailingList.getSubscribe() != null) {
      serializer.startTag(NAMESPACE, "subscribe").text(mailingList.getSubscribe()).endTag(NAMESPACE, "subscribe");
      writeLocationTracking((InputLocationTracker)mailingList, "subscribe", serializer);
    } 
    if (mailingList.getUnsubscribe() != null) {
      serializer.startTag(NAMESPACE, "unsubscribe").text(mailingList.getUnsubscribe()).endTag(NAMESPACE, "unsubscribe");
      writeLocationTracking((InputLocationTracker)mailingList, "unsubscribe", serializer);
    } 
    if (mailingList.getPost() != null) {
      serializer.startTag(NAMESPACE, "post").text(mailingList.getPost()).endTag(NAMESPACE, "post");
      writeLocationTracking((InputLocationTracker)mailingList, "post", serializer);
    } 
    if (mailingList.getArchive() != null) {
      serializer.startTag(NAMESPACE, "archive").text(mailingList.getArchive()).endTag(NAMESPACE, "archive");
      writeLocationTracking((InputLocationTracker)mailingList, "archive", serializer);
    } 
    if (mailingList.getOtherArchives() != null && mailingList.getOtherArchives().size() > 0) {
      serializer.startTag(NAMESPACE, "otherArchives");
      InputLocation location = mailingList.getLocation("otherArchives");
      int n = 0;
      for (Iterator<String> iter = mailingList.getOtherArchives().iterator(); iter.hasNext(); ) {
        String otherArchive = iter.next();
        serializer.startTag(NAMESPACE, "otherArchive").text(otherArchive).endTag(NAMESPACE, "otherArchive");
        writeLocationTracking((InputLocationTracker)location, Integer.valueOf(n++), serializer);
      } 
      serializer.endTag(NAMESPACE, "otherArchives");
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeModel(Model model, String tagName, XmlSerializer serializer) throws IOException {
    if (this.fileComment != null)
      serializer.comment(this.fileComment); 
    serializer.setPrefix("", "http://maven.apache.org/POM/4.0.0");
    serializer.setPrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    serializer.startTag(NAMESPACE, tagName);
    serializer.attribute("", "xsi:schemaLocation", "http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd");
    if (model.getChildProjectUrlInheritAppendPath() != null)
      serializer.attribute(NAMESPACE, "child.project.url.inherit.append.path", model.getChildProjectUrlInheritAppendPath()); 
    if (model.getModelVersion() != null) {
      serializer.startTag(NAMESPACE, "modelVersion").text(model.getModelVersion()).endTag(NAMESPACE, "modelVersion");
      writeLocationTracking((InputLocationTracker)model, "modelVersion", serializer);
    } 
    if (model.getParent() != null)
      writeParent(model.getParent(), "parent", serializer); 
    if (model.getGroupId() != null) {
      serializer.startTag(NAMESPACE, "groupId").text(model.getGroupId()).endTag(NAMESPACE, "groupId");
      writeLocationTracking((InputLocationTracker)model, "groupId", serializer);
    } 
    if (model.getArtifactId() != null) {
      serializer.startTag(NAMESPACE, "artifactId").text(model.getArtifactId()).endTag(NAMESPACE, "artifactId");
      writeLocationTracking((InputLocationTracker)model, "artifactId", serializer);
    } 
    if (model.getVersion() != null) {
      serializer.startTag(NAMESPACE, "version").text(model.getVersion()).endTag(NAMESPACE, "version");
      writeLocationTracking((InputLocationTracker)model, "version", serializer);
    } 
    if (model.getPackaging() != null && !model.getPackaging().equals("jar")) {
      serializer.startTag(NAMESPACE, "packaging").text(model.getPackaging()).endTag(NAMESPACE, "packaging");
      writeLocationTracking((InputLocationTracker)model, "packaging", serializer);
    } 
    if (model.getName() != null) {
      serializer.startTag(NAMESPACE, "name").text(model.getName()).endTag(NAMESPACE, "name");
      writeLocationTracking((InputLocationTracker)model, "name", serializer);
    } 
    if (model.getDescription() != null) {
      serializer.startTag(NAMESPACE, "description").text(model.getDescription()).endTag(NAMESPACE, "description");
      writeLocationTracking((InputLocationTracker)model, "description", serializer);
    } 
    if (model.getUrl() != null) {
      serializer.startTag(NAMESPACE, "url").text(model.getUrl()).endTag(NAMESPACE, "url");
      writeLocationTracking((InputLocationTracker)model, "url", serializer);
    } 
    if (model.getInceptionYear() != null) {
      serializer.startTag(NAMESPACE, "inceptionYear").text(model.getInceptionYear()).endTag(NAMESPACE, "inceptionYear");
      writeLocationTracking((InputLocationTracker)model, "inceptionYear", serializer);
    } 
    if (model.getOrganization() != null)
      writeOrganization(model.getOrganization(), "organization", serializer); 
    if (model.getLicenses() != null && model.getLicenses().size() > 0) {
      serializer.startTag(NAMESPACE, "licenses");
      for (Iterator<License> iter = model.getLicenses().iterator(); iter.hasNext(); ) {
        License o = iter.next();
        writeLicense(o, "license", serializer);
      } 
      serializer.endTag(NAMESPACE, "licenses");
    } 
    if (model.getDevelopers() != null && model.getDevelopers().size() > 0) {
      serializer.startTag(NAMESPACE, "developers");
      for (Iterator<Developer> iter = model.getDevelopers().iterator(); iter.hasNext(); ) {
        Developer o = iter.next();
        writeDeveloper(o, "developer", serializer);
      } 
      serializer.endTag(NAMESPACE, "developers");
    } 
    if (model.getContributors() != null && model.getContributors().size() > 0) {
      serializer.startTag(NAMESPACE, "contributors");
      for (Iterator<Contributor> iter = model.getContributors().iterator(); iter.hasNext(); ) {
        Contributor o = iter.next();
        writeContributor(o, "contributor", serializer);
      } 
      serializer.endTag(NAMESPACE, "contributors");
    } 
    if (model.getMailingLists() != null && model.getMailingLists().size() > 0) {
      serializer.startTag(NAMESPACE, "mailingLists");
      for (Iterator<MailingList> iter = model.getMailingLists().iterator(); iter.hasNext(); ) {
        MailingList o = iter.next();
        writeMailingList(o, "mailingList", serializer);
      } 
      serializer.endTag(NAMESPACE, "mailingLists");
    } 
    if (model.getPrerequisites() != null)
      writePrerequisites(model.getPrerequisites(), "prerequisites", serializer); 
    if (model.getModules() != null && model.getModules().size() > 0) {
      serializer.startTag(NAMESPACE, "modules");
      InputLocation location = model.getLocation("modules");
      int n = 0;
      for (Iterator<String> iter = model.getModules().iterator(); iter.hasNext(); ) {
        String module = iter.next();
        serializer.startTag(NAMESPACE, "module").text(module).endTag(NAMESPACE, "module");
        writeLocationTracking((InputLocationTracker)location, Integer.valueOf(n++), serializer);
      } 
      serializer.endTag(NAMESPACE, "modules");
    } 
    if (model.getScm() != null)
      writeScm(model.getScm(), "scm", serializer); 
    if (model.getIssueManagement() != null)
      writeIssueManagement(model.getIssueManagement(), "issueManagement", serializer); 
    if (model.getCiManagement() != null)
      writeCiManagement(model.getCiManagement(), "ciManagement", serializer); 
    if (model.getDistributionManagement() != null)
      writeDistributionManagement(model.getDistributionManagement(), "distributionManagement", serializer); 
    if (model.getProperties() != null && model.getProperties().size() > 0) {
      serializer.startTag(NAMESPACE, "properties");
      InputLocation location = model.getLocation("properties");
      for (Iterator<String> iter = model.getProperties().keySet().iterator(); iter.hasNext(); ) {
        String key = iter.next();
        String value = (String)model.getProperties().get(key);
        serializer.startTag(NAMESPACE, key).text(value).endTag(NAMESPACE, key);
        writeLocationTracking((InputLocationTracker)location, key, serializer);
      } 
      serializer.endTag(NAMESPACE, "properties");
    } 
    if (model.getDependencyManagement() != null)
      writeDependencyManagement(model.getDependencyManagement(), "dependencyManagement", serializer); 
    if (model.getDependencies() != null && model.getDependencies().size() > 0) {
      serializer.startTag(NAMESPACE, "dependencies");
      for (Iterator<Dependency> iter = model.getDependencies().iterator(); iter.hasNext(); ) {
        Dependency o = iter.next();
        writeDependency(o, "dependency", serializer);
      } 
      serializer.endTag(NAMESPACE, "dependencies");
    } 
    if (model.getRepositories() != null && model.getRepositories().size() > 0) {
      serializer.startTag(NAMESPACE, "repositories");
      for (Iterator<Repository> iter = model.getRepositories().iterator(); iter.hasNext(); ) {
        Repository o = iter.next();
        writeRepository(o, "repository", serializer);
      } 
      serializer.endTag(NAMESPACE, "repositories");
    } 
    if (model.getPluginRepositories() != null && model.getPluginRepositories().size() > 0) {
      serializer.startTag(NAMESPACE, "pluginRepositories");
      for (Iterator<Repository> iter = model.getPluginRepositories().iterator(); iter.hasNext(); ) {
        Repository o = iter.next();
        writeRepository(o, "pluginRepository", serializer);
      } 
      serializer.endTag(NAMESPACE, "pluginRepositories");
    } 
    if (model.getBuild() != null)
      writeBuild(model.getBuild(), "build", serializer); 
    if (model.getReports() != null)
      writeXpp3DomToSerializer((Xpp3Dom)model.getReports(), serializer); 
    if (model.getReporting() != null)
      writeReporting(model.getReporting(), "reporting", serializer); 
    if (model.getProfiles() != null && model.getProfiles().size() > 0) {
      serializer.startTag(NAMESPACE, "profiles");
      for (Iterator<Profile> iter = model.getProfiles().iterator(); iter.hasNext(); ) {
        Profile o = iter.next();
        writeProfile(o, "profile", serializer);
      } 
      serializer.endTag(NAMESPACE, "profiles");
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeModelBase(ModelBase modelBase, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (modelBase.getModules() != null && modelBase.getModules().size() > 0) {
      serializer.startTag(NAMESPACE, "modules");
      InputLocation location = modelBase.getLocation("modules");
      int n = 0;
      for (Iterator<String> iter = modelBase.getModules().iterator(); iter.hasNext(); ) {
        String module = iter.next();
        serializer.startTag(NAMESPACE, "module").text(module).endTag(NAMESPACE, "module");
        writeLocationTracking((InputLocationTracker)location, Integer.valueOf(n++), serializer);
      } 
      serializer.endTag(NAMESPACE, "modules");
    } 
    if (modelBase.getDistributionManagement() != null)
      writeDistributionManagement(modelBase.getDistributionManagement(), "distributionManagement", serializer); 
    if (modelBase.getProperties() != null && modelBase.getProperties().size() > 0) {
      serializer.startTag(NAMESPACE, "properties");
      InputLocation location = modelBase.getLocation("properties");
      for (Iterator<String> iter = modelBase.getProperties().keySet().iterator(); iter.hasNext(); ) {
        String key = iter.next();
        String value = (String)modelBase.getProperties().get(key);
        serializer.startTag(NAMESPACE, key).text(value).endTag(NAMESPACE, key);
        writeLocationTracking((InputLocationTracker)location, key, serializer);
      } 
      serializer.endTag(NAMESPACE, "properties");
    } 
    if (modelBase.getDependencyManagement() != null)
      writeDependencyManagement(modelBase.getDependencyManagement(), "dependencyManagement", serializer); 
    if (modelBase.getDependencies() != null && modelBase.getDependencies().size() > 0) {
      serializer.startTag(NAMESPACE, "dependencies");
      for (Iterator<Dependency> iter = modelBase.getDependencies().iterator(); iter.hasNext(); ) {
        Dependency o = iter.next();
        writeDependency(o, "dependency", serializer);
      } 
      serializer.endTag(NAMESPACE, "dependencies");
    } 
    if (modelBase.getRepositories() != null && modelBase.getRepositories().size() > 0) {
      serializer.startTag(NAMESPACE, "repositories");
      for (Iterator<Repository> iter = modelBase.getRepositories().iterator(); iter.hasNext(); ) {
        Repository o = iter.next();
        writeRepository(o, "repository", serializer);
      } 
      serializer.endTag(NAMESPACE, "repositories");
    } 
    if (modelBase.getPluginRepositories() != null && modelBase.getPluginRepositories().size() > 0) {
      serializer.startTag(NAMESPACE, "pluginRepositories");
      for (Iterator<Repository> iter = modelBase.getPluginRepositories().iterator(); iter.hasNext(); ) {
        Repository o = iter.next();
        writeRepository(o, "pluginRepository", serializer);
      } 
      serializer.endTag(NAMESPACE, "pluginRepositories");
    } 
    if (modelBase.getReports() != null)
      writeXpp3DomToSerializer((Xpp3Dom)modelBase.getReports(), serializer); 
    if (modelBase.getReporting() != null)
      writeReporting(modelBase.getReporting(), "reporting", serializer); 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeNotifier(Notifier notifier, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (notifier.getType() != null && !notifier.getType().equals("mail")) {
      serializer.startTag(NAMESPACE, "type").text(notifier.getType()).endTag(NAMESPACE, "type");
      writeLocationTracking((InputLocationTracker)notifier, "type", serializer);
    } 
    if (notifier.isSendOnError() != true) {
      serializer.startTag(NAMESPACE, "sendOnError").text(String.valueOf(notifier.isSendOnError())).endTag(NAMESPACE, "sendOnError");
      writeLocationTracking((InputLocationTracker)notifier, "sendOnError", serializer);
    } 
    if (notifier.isSendOnFailure() != true) {
      serializer.startTag(NAMESPACE, "sendOnFailure").text(String.valueOf(notifier.isSendOnFailure())).endTag(NAMESPACE, "sendOnFailure");
      writeLocationTracking((InputLocationTracker)notifier, "sendOnFailure", serializer);
    } 
    if (notifier.isSendOnSuccess() != true) {
      serializer.startTag(NAMESPACE, "sendOnSuccess").text(String.valueOf(notifier.isSendOnSuccess())).endTag(NAMESPACE, "sendOnSuccess");
      writeLocationTracking((InputLocationTracker)notifier, "sendOnSuccess", serializer);
    } 
    if (notifier.isSendOnWarning() != true) {
      serializer.startTag(NAMESPACE, "sendOnWarning").text(String.valueOf(notifier.isSendOnWarning())).endTag(NAMESPACE, "sendOnWarning");
      writeLocationTracking((InputLocationTracker)notifier, "sendOnWarning", serializer);
    } 
    if (notifier.getAddress() != null) {
      serializer.startTag(NAMESPACE, "address").text(notifier.getAddress()).endTag(NAMESPACE, "address");
      writeLocationTracking((InputLocationTracker)notifier, "address", serializer);
    } 
    if (notifier.getConfiguration() != null && notifier.getConfiguration().size() > 0) {
      serializer.startTag(NAMESPACE, "configuration");
      InputLocation location = notifier.getLocation("configuration");
      for (Iterator<String> iter = notifier.getConfiguration().keySet().iterator(); iter.hasNext(); ) {
        String key = iter.next();
        String value = (String)notifier.getConfiguration().get(key);
        serializer.startTag(NAMESPACE, key).text(value).endTag(NAMESPACE, key);
        writeLocationTracking((InputLocationTracker)location, key, serializer);
      } 
      serializer.endTag(NAMESPACE, "configuration");
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeOrganization(Organization organization, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (organization.getName() != null) {
      serializer.startTag(NAMESPACE, "name").text(organization.getName()).endTag(NAMESPACE, "name");
      writeLocationTracking((InputLocationTracker)organization, "name", serializer);
    } 
    if (organization.getUrl() != null) {
      serializer.startTag(NAMESPACE, "url").text(organization.getUrl()).endTag(NAMESPACE, "url");
      writeLocationTracking((InputLocationTracker)organization, "url", serializer);
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeParent(Parent parent, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (parent.getGroupId() != null) {
      serializer.startTag(NAMESPACE, "groupId").text(parent.getGroupId()).endTag(NAMESPACE, "groupId");
      writeLocationTracking((InputLocationTracker)parent, "groupId", serializer);
    } 
    if (parent.getArtifactId() != null) {
      serializer.startTag(NAMESPACE, "artifactId").text(parent.getArtifactId()).endTag(NAMESPACE, "artifactId");
      writeLocationTracking((InputLocationTracker)parent, "artifactId", serializer);
    } 
    if (parent.getVersion() != null) {
      serializer.startTag(NAMESPACE, "version").text(parent.getVersion()).endTag(NAMESPACE, "version");
      writeLocationTracking((InputLocationTracker)parent, "version", serializer);
    } 
    if (parent.getRelativePath() != null && !parent.getRelativePath().equals("../pom.xml")) {
      serializer.startTag(NAMESPACE, "relativePath").text(parent.getRelativePath()).endTag(NAMESPACE, "relativePath");
      writeLocationTracking((InputLocationTracker)parent, "relativePath", serializer);
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writePatternSet(PatternSet patternSet, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (patternSet.getIncludes() != null && patternSet.getIncludes().size() > 0) {
      serializer.startTag(NAMESPACE, "includes");
      InputLocation location = patternSet.getLocation("includes");
      int n = 0;
      for (Iterator<String> iter = patternSet.getIncludes().iterator(); iter.hasNext(); ) {
        String include = iter.next();
        serializer.startTag(NAMESPACE, "include").text(include).endTag(NAMESPACE, "include");
        writeLocationTracking((InputLocationTracker)location, Integer.valueOf(n++), serializer);
      } 
      serializer.endTag(NAMESPACE, "includes");
    } 
    if (patternSet.getExcludes() != null && patternSet.getExcludes().size() > 0) {
      serializer.startTag(NAMESPACE, "excludes");
      InputLocation location = patternSet.getLocation("excludes");
      int n = 0;
      for (Iterator<String> iter = patternSet.getExcludes().iterator(); iter.hasNext(); ) {
        String exclude = iter.next();
        serializer.startTag(NAMESPACE, "exclude").text(exclude).endTag(NAMESPACE, "exclude");
        writeLocationTracking((InputLocationTracker)location, Integer.valueOf(n++), serializer);
      } 
      serializer.endTag(NAMESPACE, "excludes");
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writePlugin(Plugin plugin, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (plugin.getGroupId() != null && !plugin.getGroupId().equals("org.apache.maven.plugins")) {
      serializer.startTag(NAMESPACE, "groupId").text(plugin.getGroupId()).endTag(NAMESPACE, "groupId");
      writeLocationTracking((InputLocationTracker)plugin, "groupId", serializer);
    } 
    if (plugin.getArtifactId() != null) {
      serializer.startTag(NAMESPACE, "artifactId").text(plugin.getArtifactId()).endTag(NAMESPACE, "artifactId");
      writeLocationTracking((InputLocationTracker)plugin, "artifactId", serializer);
    } 
    if (plugin.getVersion() != null) {
      serializer.startTag(NAMESPACE, "version").text(plugin.getVersion()).endTag(NAMESPACE, "version");
      writeLocationTracking((InputLocationTracker)plugin, "version", serializer);
    } 
    if (plugin.getExtensions() != null) {
      serializer.startTag(NAMESPACE, "extensions").text(plugin.getExtensions()).endTag(NAMESPACE, "extensions");
      writeLocationTracking((InputLocationTracker)plugin, "extensions", serializer);
    } 
    if (plugin.getExecutions() != null && plugin.getExecutions().size() > 0) {
      serializer.startTag(NAMESPACE, "executions");
      for (Iterator<PluginExecution> iter = plugin.getExecutions().iterator(); iter.hasNext(); ) {
        PluginExecution o = iter.next();
        writePluginExecution(o, "execution", serializer);
      } 
      serializer.endTag(NAMESPACE, "executions");
    } 
    if (plugin.getDependencies() != null && plugin.getDependencies().size() > 0) {
      serializer.startTag(NAMESPACE, "dependencies");
      for (Iterator<Dependency> iter = plugin.getDependencies().iterator(); iter.hasNext(); ) {
        Dependency o = iter.next();
        writeDependency(o, "dependency", serializer);
      } 
      serializer.endTag(NAMESPACE, "dependencies");
    } 
    if (plugin.getGoals() != null)
      writeXpp3DomToSerializer((Xpp3Dom)plugin.getGoals(), serializer); 
    if (plugin.getInherited() != null) {
      serializer.startTag(NAMESPACE, "inherited").text(plugin.getInherited()).endTag(NAMESPACE, "inherited");
      writeLocationTracking((InputLocationTracker)plugin, "inherited", serializer);
    } 
    if (plugin.getConfiguration() != null)
      writeXpp3DomToSerializer((Xpp3Dom)plugin.getConfiguration(), serializer); 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writePluginConfiguration(PluginConfiguration pluginConfiguration, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (pluginConfiguration.getPluginManagement() != null)
      writePluginManagement(pluginConfiguration.getPluginManagement(), "pluginManagement", serializer); 
    if (pluginConfiguration.getPlugins() != null && pluginConfiguration.getPlugins().size() > 0) {
      serializer.startTag(NAMESPACE, "plugins");
      for (Iterator<Plugin> iter = pluginConfiguration.getPlugins().iterator(); iter.hasNext(); ) {
        Plugin o = iter.next();
        writePlugin(o, "plugin", serializer);
      } 
      serializer.endTag(NAMESPACE, "plugins");
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writePluginContainer(PluginContainer pluginContainer, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (pluginContainer.getPlugins() != null && pluginContainer.getPlugins().size() > 0) {
      serializer.startTag(NAMESPACE, "plugins");
      for (Iterator<Plugin> iter = pluginContainer.getPlugins().iterator(); iter.hasNext(); ) {
        Plugin o = iter.next();
        writePlugin(o, "plugin", serializer);
      } 
      serializer.endTag(NAMESPACE, "plugins");
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writePluginExecution(PluginExecution pluginExecution, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (pluginExecution.getId() != null && !pluginExecution.getId().equals("default")) {
      serializer.startTag(NAMESPACE, "id").text(pluginExecution.getId()).endTag(NAMESPACE, "id");
      writeLocationTracking((InputLocationTracker)pluginExecution, "id", serializer);
    } 
    if (pluginExecution.getPhase() != null) {
      serializer.startTag(NAMESPACE, "phase").text(pluginExecution.getPhase()).endTag(NAMESPACE, "phase");
      writeLocationTracking((InputLocationTracker)pluginExecution, "phase", serializer);
    } 
    if (pluginExecution.getGoals() != null && pluginExecution.getGoals().size() > 0) {
      serializer.startTag(NAMESPACE, "goals");
      InputLocation location = pluginExecution.getLocation("goals");
      int n = 0;
      for (Iterator<String> iter = pluginExecution.getGoals().iterator(); iter.hasNext(); ) {
        String goal = iter.next();
        serializer.startTag(NAMESPACE, "goal").text(goal).endTag(NAMESPACE, "goal");
        writeLocationTracking((InputLocationTracker)location, Integer.valueOf(n++), serializer);
      } 
      serializer.endTag(NAMESPACE, "goals");
    } 
    if (pluginExecution.getInherited() != null) {
      serializer.startTag(NAMESPACE, "inherited").text(pluginExecution.getInherited()).endTag(NAMESPACE, "inherited");
      writeLocationTracking((InputLocationTracker)pluginExecution, "inherited", serializer);
    } 
    if (pluginExecution.getConfiguration() != null)
      writeXpp3DomToSerializer((Xpp3Dom)pluginExecution.getConfiguration(), serializer); 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writePluginManagement(PluginManagement pluginManagement, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (pluginManagement.getPlugins() != null && pluginManagement.getPlugins().size() > 0) {
      serializer.startTag(NAMESPACE, "plugins");
      for (Iterator<Plugin> iter = pluginManagement.getPlugins().iterator(); iter.hasNext(); ) {
        Plugin o = iter.next();
        writePlugin(o, "plugin", serializer);
      } 
      serializer.endTag(NAMESPACE, "plugins");
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writePrerequisites(Prerequisites prerequisites, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (prerequisites.getMaven() != null && !prerequisites.getMaven().equals("2.0")) {
      serializer.startTag(NAMESPACE, "maven").text(prerequisites.getMaven()).endTag(NAMESPACE, "maven");
      writeLocationTracking((InputLocationTracker)prerequisites, "maven", serializer);
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeProfile(Profile profile, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (profile.getId() != null && !profile.getId().equals("default")) {
      serializer.startTag(NAMESPACE, "id").text(profile.getId()).endTag(NAMESPACE, "id");
      writeLocationTracking((InputLocationTracker)profile, "id", serializer);
    } 
    if (profile.getActivation() != null)
      writeActivation(profile.getActivation(), "activation", serializer); 
    if (profile.getBuild() != null)
      writeBuildBase(profile.getBuild(), "build", serializer); 
    if (profile.getModules() != null && profile.getModules().size() > 0) {
      serializer.startTag(NAMESPACE, "modules");
      InputLocation location = profile.getLocation("modules");
      int n = 0;
      for (Iterator<String> iter = profile.getModules().iterator(); iter.hasNext(); ) {
        String module = iter.next();
        serializer.startTag(NAMESPACE, "module").text(module).endTag(NAMESPACE, "module");
        writeLocationTracking((InputLocationTracker)location, Integer.valueOf(n++), serializer);
      } 
      serializer.endTag(NAMESPACE, "modules");
    } 
    if (profile.getDistributionManagement() != null)
      writeDistributionManagement(profile.getDistributionManagement(), "distributionManagement", serializer); 
    if (profile.getProperties() != null && profile.getProperties().size() > 0) {
      serializer.startTag(NAMESPACE, "properties");
      InputLocation location = profile.getLocation("properties");
      for (Iterator<String> iter = profile.getProperties().keySet().iterator(); iter.hasNext(); ) {
        String key = iter.next();
        String value = (String)profile.getProperties().get(key);
        serializer.startTag(NAMESPACE, key).text(value).endTag(NAMESPACE, key);
        writeLocationTracking((InputLocationTracker)location, key, serializer);
      } 
      serializer.endTag(NAMESPACE, "properties");
    } 
    if (profile.getDependencyManagement() != null)
      writeDependencyManagement(profile.getDependencyManagement(), "dependencyManagement", serializer); 
    if (profile.getDependencies() != null && profile.getDependencies().size() > 0) {
      serializer.startTag(NAMESPACE, "dependencies");
      for (Iterator<Dependency> iter = profile.getDependencies().iterator(); iter.hasNext(); ) {
        Dependency o = iter.next();
        writeDependency(o, "dependency", serializer);
      } 
      serializer.endTag(NAMESPACE, "dependencies");
    } 
    if (profile.getRepositories() != null && profile.getRepositories().size() > 0) {
      serializer.startTag(NAMESPACE, "repositories");
      for (Iterator<Repository> iter = profile.getRepositories().iterator(); iter.hasNext(); ) {
        Repository o = iter.next();
        writeRepository(o, "repository", serializer);
      } 
      serializer.endTag(NAMESPACE, "repositories");
    } 
    if (profile.getPluginRepositories() != null && profile.getPluginRepositories().size() > 0) {
      serializer.startTag(NAMESPACE, "pluginRepositories");
      for (Iterator<Repository> iter = profile.getPluginRepositories().iterator(); iter.hasNext(); ) {
        Repository o = iter.next();
        writeRepository(o, "pluginRepository", serializer);
      } 
      serializer.endTag(NAMESPACE, "pluginRepositories");
    } 
    if (profile.getReports() != null)
      writeXpp3DomToSerializer((Xpp3Dom)profile.getReports(), serializer); 
    if (profile.getReporting() != null)
      writeReporting(profile.getReporting(), "reporting", serializer); 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeRelocation(Relocation relocation, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (relocation.getGroupId() != null) {
      serializer.startTag(NAMESPACE, "groupId").text(relocation.getGroupId()).endTag(NAMESPACE, "groupId");
      writeLocationTracking((InputLocationTracker)relocation, "groupId", serializer);
    } 
    if (relocation.getArtifactId() != null) {
      serializer.startTag(NAMESPACE, "artifactId").text(relocation.getArtifactId()).endTag(NAMESPACE, "artifactId");
      writeLocationTracking((InputLocationTracker)relocation, "artifactId", serializer);
    } 
    if (relocation.getVersion() != null) {
      serializer.startTag(NAMESPACE, "version").text(relocation.getVersion()).endTag(NAMESPACE, "version");
      writeLocationTracking((InputLocationTracker)relocation, "version", serializer);
    } 
    if (relocation.getMessage() != null) {
      serializer.startTag(NAMESPACE, "message").text(relocation.getMessage()).endTag(NAMESPACE, "message");
      writeLocationTracking((InputLocationTracker)relocation, "message", serializer);
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeReportPlugin(ReportPlugin reportPlugin, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (reportPlugin.getGroupId() != null && !reportPlugin.getGroupId().equals("org.apache.maven.plugins")) {
      serializer.startTag(NAMESPACE, "groupId").text(reportPlugin.getGroupId()).endTag(NAMESPACE, "groupId");
      writeLocationTracking((InputLocationTracker)reportPlugin, "groupId", serializer);
    } 
    if (reportPlugin.getArtifactId() != null) {
      serializer.startTag(NAMESPACE, "artifactId").text(reportPlugin.getArtifactId()).endTag(NAMESPACE, "artifactId");
      writeLocationTracking((InputLocationTracker)reportPlugin, "artifactId", serializer);
    } 
    if (reportPlugin.getVersion() != null) {
      serializer.startTag(NAMESPACE, "version").text(reportPlugin.getVersion()).endTag(NAMESPACE, "version");
      writeLocationTracking((InputLocationTracker)reportPlugin, "version", serializer);
    } 
    if (reportPlugin.getReportSets() != null && reportPlugin.getReportSets().size() > 0) {
      serializer.startTag(NAMESPACE, "reportSets");
      for (Iterator<ReportSet> iter = reportPlugin.getReportSets().iterator(); iter.hasNext(); ) {
        ReportSet o = iter.next();
        writeReportSet(o, "reportSet", serializer);
      } 
      serializer.endTag(NAMESPACE, "reportSets");
    } 
    if (reportPlugin.getInherited() != null) {
      serializer.startTag(NAMESPACE, "inherited").text(reportPlugin.getInherited()).endTag(NAMESPACE, "inherited");
      writeLocationTracking((InputLocationTracker)reportPlugin, "inherited", serializer);
    } 
    if (reportPlugin.getConfiguration() != null)
      writeXpp3DomToSerializer((Xpp3Dom)reportPlugin.getConfiguration(), serializer); 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeReportSet(ReportSet reportSet, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (reportSet.getId() != null && !reportSet.getId().equals("default")) {
      serializer.startTag(NAMESPACE, "id").text(reportSet.getId()).endTag(NAMESPACE, "id");
      writeLocationTracking((InputLocationTracker)reportSet, "id", serializer);
    } 
    if (reportSet.getReports() != null && reportSet.getReports().size() > 0) {
      serializer.startTag(NAMESPACE, "reports");
      InputLocation location = reportSet.getLocation("reports");
      int n = 0;
      for (Iterator<String> iter = reportSet.getReports().iterator(); iter.hasNext(); ) {
        String report = iter.next();
        serializer.startTag(NAMESPACE, "report").text(report).endTag(NAMESPACE, "report");
        writeLocationTracking((InputLocationTracker)location, Integer.valueOf(n++), serializer);
      } 
      serializer.endTag(NAMESPACE, "reports");
    } 
    if (reportSet.getInherited() != null) {
      serializer.startTag(NAMESPACE, "inherited").text(reportSet.getInherited()).endTag(NAMESPACE, "inherited");
      writeLocationTracking((InputLocationTracker)reportSet, "inherited", serializer);
    } 
    if (reportSet.getConfiguration() != null)
      writeXpp3DomToSerializer((Xpp3Dom)reportSet.getConfiguration(), serializer); 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeReporting(Reporting reporting, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (reporting.getExcludeDefaults() != null) {
      serializer.startTag(NAMESPACE, "excludeDefaults").text(reporting.getExcludeDefaults()).endTag(NAMESPACE, "excludeDefaults");
      writeLocationTracking((InputLocationTracker)reporting, "excludeDefaults", serializer);
    } 
    if (reporting.getOutputDirectory() != null) {
      serializer.startTag(NAMESPACE, "outputDirectory").text(reporting.getOutputDirectory()).endTag(NAMESPACE, "outputDirectory");
      writeLocationTracking((InputLocationTracker)reporting, "outputDirectory", serializer);
    } 
    if (reporting.getPlugins() != null && reporting.getPlugins().size() > 0) {
      serializer.startTag(NAMESPACE, "plugins");
      for (Iterator<ReportPlugin> iter = reporting.getPlugins().iterator(); iter.hasNext(); ) {
        ReportPlugin o = iter.next();
        writeReportPlugin(o, "plugin", serializer);
      } 
      serializer.endTag(NAMESPACE, "plugins");
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeRepository(Repository repository, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (repository.getReleases() != null)
      writeRepositoryPolicy(repository.getReleases(), "releases", serializer); 
    if (repository.getSnapshots() != null)
      writeRepositoryPolicy(repository.getSnapshots(), "snapshots", serializer); 
    if (repository.getId() != null) {
      serializer.startTag(NAMESPACE, "id").text(repository.getId()).endTag(NAMESPACE, "id");
      writeLocationTracking((InputLocationTracker)repository, "id", serializer);
    } 
    if (repository.getName() != null) {
      serializer.startTag(NAMESPACE, "name").text(repository.getName()).endTag(NAMESPACE, "name");
      writeLocationTracking((InputLocationTracker)repository, "name", serializer);
    } 
    if (repository.getUrl() != null) {
      serializer.startTag(NAMESPACE, "url").text(repository.getUrl()).endTag(NAMESPACE, "url");
      writeLocationTracking((InputLocationTracker)repository, "url", serializer);
    } 
    if (repository.getLayout() != null && !repository.getLayout().equals("default")) {
      serializer.startTag(NAMESPACE, "layout").text(repository.getLayout()).endTag(NAMESPACE, "layout");
      writeLocationTracking((InputLocationTracker)repository, "layout", serializer);
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeRepositoryBase(RepositoryBase repositoryBase, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (repositoryBase.getId() != null) {
      serializer.startTag(NAMESPACE, "id").text(repositoryBase.getId()).endTag(NAMESPACE, "id");
      writeLocationTracking((InputLocationTracker)repositoryBase, "id", serializer);
    } 
    if (repositoryBase.getName() != null) {
      serializer.startTag(NAMESPACE, "name").text(repositoryBase.getName()).endTag(NAMESPACE, "name");
      writeLocationTracking((InputLocationTracker)repositoryBase, "name", serializer);
    } 
    if (repositoryBase.getUrl() != null) {
      serializer.startTag(NAMESPACE, "url").text(repositoryBase.getUrl()).endTag(NAMESPACE, "url");
      writeLocationTracking((InputLocationTracker)repositoryBase, "url", serializer);
    } 
    if (repositoryBase.getLayout() != null && !repositoryBase.getLayout().equals("default")) {
      serializer.startTag(NAMESPACE, "layout").text(repositoryBase.getLayout()).endTag(NAMESPACE, "layout");
      writeLocationTracking((InputLocationTracker)repositoryBase, "layout", serializer);
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeRepositoryPolicy(RepositoryPolicy repositoryPolicy, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (repositoryPolicy.getEnabled() != null) {
      serializer.startTag(NAMESPACE, "enabled").text(repositoryPolicy.getEnabled()).endTag(NAMESPACE, "enabled");
      writeLocationTracking((InputLocationTracker)repositoryPolicy, "enabled", serializer);
    } 
    if (repositoryPolicy.getUpdatePolicy() != null) {
      serializer.startTag(NAMESPACE, "updatePolicy").text(repositoryPolicy.getUpdatePolicy()).endTag(NAMESPACE, "updatePolicy");
      writeLocationTracking((InputLocationTracker)repositoryPolicy, "updatePolicy", serializer);
    } 
    if (repositoryPolicy.getChecksumPolicy() != null) {
      serializer.startTag(NAMESPACE, "checksumPolicy").text(repositoryPolicy.getChecksumPolicy()).endTag(NAMESPACE, "checksumPolicy");
      writeLocationTracking((InputLocationTracker)repositoryPolicy, "checksumPolicy", serializer);
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeResource(Resource resource, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (resource.getTargetPath() != null) {
      serializer.startTag(NAMESPACE, "targetPath").text(resource.getTargetPath()).endTag(NAMESPACE, "targetPath");
      writeLocationTracking((InputLocationTracker)resource, "targetPath", serializer);
    } 
    if (resource.getFiltering() != null) {
      serializer.startTag(NAMESPACE, "filtering").text(resource.getFiltering()).endTag(NAMESPACE, "filtering");
      writeLocationTracking((InputLocationTracker)resource, "filtering", serializer);
    } 
    if (resource.getDirectory() != null) {
      serializer.startTag(NAMESPACE, "directory").text(resource.getDirectory()).endTag(NAMESPACE, "directory");
      writeLocationTracking((InputLocationTracker)resource, "directory", serializer);
    } 
    if (resource.getIncludes() != null && resource.getIncludes().size() > 0) {
      serializer.startTag(NAMESPACE, "includes");
      InputLocation location = resource.getLocation("includes");
      int n = 0;
      for (Iterator<String> iter = resource.getIncludes().iterator(); iter.hasNext(); ) {
        String include = iter.next();
        serializer.startTag(NAMESPACE, "include").text(include).endTag(NAMESPACE, "include");
        writeLocationTracking((InputLocationTracker)location, Integer.valueOf(n++), serializer);
      } 
      serializer.endTag(NAMESPACE, "includes");
    } 
    if (resource.getExcludes() != null && resource.getExcludes().size() > 0) {
      serializer.startTag(NAMESPACE, "excludes");
      InputLocation location = resource.getLocation("excludes");
      int n = 0;
      for (Iterator<String> iter = resource.getExcludes().iterator(); iter.hasNext(); ) {
        String exclude = iter.next();
        serializer.startTag(NAMESPACE, "exclude").text(exclude).endTag(NAMESPACE, "exclude");
        writeLocationTracking((InputLocationTracker)location, Integer.valueOf(n++), serializer);
      } 
      serializer.endTag(NAMESPACE, "excludes");
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeScm(Scm scm, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (scm.getChildScmConnectionInheritAppendPath() != null)
      serializer.attribute(NAMESPACE, "child.scm.connection.inherit.append.path", scm.getChildScmConnectionInheritAppendPath()); 
    if (scm.getChildScmDeveloperConnectionInheritAppendPath() != null)
      serializer.attribute(NAMESPACE, "child.scm.developerConnection.inherit.append.path", scm.getChildScmDeveloperConnectionInheritAppendPath()); 
    if (scm.getChildScmUrlInheritAppendPath() != null)
      serializer.attribute(NAMESPACE, "child.scm.url.inherit.append.path", scm.getChildScmUrlInheritAppendPath()); 
    if (scm.getConnection() != null) {
      serializer.startTag(NAMESPACE, "connection").text(scm.getConnection()).endTag(NAMESPACE, "connection");
      writeLocationTracking((InputLocationTracker)scm, "connection", serializer);
    } 
    if (scm.getDeveloperConnection() != null) {
      serializer.startTag(NAMESPACE, "developerConnection").text(scm.getDeveloperConnection()).endTag(NAMESPACE, "developerConnection");
      writeLocationTracking((InputLocationTracker)scm, "developerConnection", serializer);
    } 
    if (scm.getTag() != null && !scm.getTag().equals("HEAD")) {
      serializer.startTag(NAMESPACE, "tag").text(scm.getTag()).endTag(NAMESPACE, "tag");
      writeLocationTracking((InputLocationTracker)scm, "tag", serializer);
    } 
    if (scm.getUrl() != null) {
      serializer.startTag(NAMESPACE, "url").text(scm.getUrl()).endTag(NAMESPACE, "url");
      writeLocationTracking((InputLocationTracker)scm, "url", serializer);
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeSite(Site site, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (site.getChildSiteUrlInheritAppendPath() != null)
      serializer.attribute(NAMESPACE, "child.site.url.inherit.append.path", site.getChildSiteUrlInheritAppendPath()); 
    if (site.getId() != null) {
      serializer.startTag(NAMESPACE, "id").text(site.getId()).endTag(NAMESPACE, "id");
      writeLocationTracking((InputLocationTracker)site, "id", serializer);
    } 
    if (site.getName() != null) {
      serializer.startTag(NAMESPACE, "name").text(site.getName()).endTag(NAMESPACE, "name");
      writeLocationTracking((InputLocationTracker)site, "name", serializer);
    } 
    if (site.getUrl() != null) {
      serializer.startTag(NAMESPACE, "url").text(site.getUrl()).endTag(NAMESPACE, "url");
      writeLocationTracking((InputLocationTracker)site, "url", serializer);
    } 
    serializer.endTag(NAMESPACE, tagName);
  }
}
