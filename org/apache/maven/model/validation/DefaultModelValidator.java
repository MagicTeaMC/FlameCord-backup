package org.apache.maven.model.validation;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputLocationTracker;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Profile;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Repository;
import org.apache.maven.model.Resource;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.building.ModelProblemCollectorRequest;
import org.apache.maven.model.interpolation.ModelVersionProcessor;
import org.codehaus.plexus.util.StringUtils;

@Named
@Singleton
public class DefaultModelValidator implements ModelValidator {
  private static final Pattern CI_FRIENDLY_EXPRESSION = Pattern.compile("\\$\\{(.+?)\\}");
  
  private static final String ILLEGAL_FS_CHARS = "\\/:\"<>|?*";
  
  private static final String ILLEGAL_VERSION_CHARS = "\\/:\"<>|?*";
  
  private static final String ILLEGAL_REPO_ID_CHARS = "\\/:\"<>|?*";
  
  private static final String EMPTY = "";
  
  private final Set<String> validIds = new HashSet<>();
  
  private ModelVersionProcessor versionProcessor;
  
  @Inject
  public DefaultModelValidator(ModelVersionProcessor versionProcessor) {
    this.versionProcessor = versionProcessor;
  }
  
  public void validateRawModel(Model m, ModelBuildingRequest request, ModelProblemCollector problems) {
    Parent parent = m.getParent();
    if (parent != null) {
      validateStringNotEmpty("parent.groupId", problems, ModelProblem.Severity.FATAL, ModelProblem.Version.BASE, parent.getGroupId(), (InputLocationTracker)parent);
      validateStringNotEmpty("parent.artifactId", problems, ModelProblem.Severity.FATAL, ModelProblem.Version.BASE, parent.getArtifactId(), (InputLocationTracker)parent);
      validateStringNotEmpty("parent.version", problems, ModelProblem.Severity.FATAL, ModelProblem.Version.BASE, parent.getVersion(), (InputLocationTracker)parent);
      if (equals(parent.getGroupId(), m.getGroupId()) && equals(parent.getArtifactId(), m.getArtifactId()))
        addViolation(problems, ModelProblem.Severity.FATAL, ModelProblem.Version.BASE, "parent.artifactId", null, "must be changed, the parent element cannot have the same groupId:artifactId as the project.", (InputLocationTracker)parent); 
      if (equals("LATEST", parent.getVersion()) || equals("RELEASE", parent.getVersion()))
        addViolation(problems, ModelProblem.Severity.WARNING, ModelProblem.Version.BASE, "parent.version", null, "is either LATEST or RELEASE (both of them are being deprecated)", (InputLocationTracker)parent); 
    } 
    if (request.getValidationLevel() >= 20) {
      ModelProblem.Severity errOn30 = getSeverity(request, 30);
      validateStringNotEmpty("modelVersion", problems, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, m.getModelVersion(), (InputLocationTracker)m);
      validateModelVersion(problems, m.getModelVersion(), (InputLocationTracker)m, new String[] { "4.0.0" });
      validateStringNoExpression("groupId", problems, ModelProblem.Severity.WARNING, ModelProblem.Version.V20, m.getGroupId(), (InputLocationTracker)m);
      if (parent == null)
        validateStringNotEmpty("groupId", problems, ModelProblem.Severity.FATAL, ModelProblem.Version.V20, m.getGroupId(), (InputLocationTracker)m); 
      validateStringNoExpression("artifactId", problems, ModelProblem.Severity.WARNING, ModelProblem.Version.V20, m.getArtifactId(), (InputLocationTracker)m);
      validateStringNotEmpty("artifactId", problems, ModelProblem.Severity.FATAL, ModelProblem.Version.V20, m.getArtifactId(), (InputLocationTracker)m);
      validateVersionNoExpression("version", problems, ModelProblem.Severity.WARNING, ModelProblem.Version.V20, m.getVersion(), (InputLocationTracker)m);
      if (parent == null)
        validateStringNotEmpty("version", problems, ModelProblem.Severity.FATAL, ModelProblem.Version.V20, m.getVersion(), (InputLocationTracker)m); 
      validate20RawDependencies(problems, m.getDependencies(), "dependencies.dependency.", "", request);
      validate20RawDependenciesSelfReferencing(problems, m, m.getDependencies(), "dependencies.dependency", request);
      if (m.getDependencyManagement() != null)
        validate20RawDependencies(problems, m.getDependencyManagement().getDependencies(), "dependencyManagement.dependencies.dependency.", "", request); 
      validateRawRepositories(problems, m.getRepositories(), "repositories.repository.", "", request);
      validateRawRepositories(problems, m.getPluginRepositories(), "pluginRepositories.pluginRepository.", "", request);
      Build build = m.getBuild();
      if (build != null) {
        validate20RawPlugins(problems, build.getPlugins(), "build.plugins.plugin.", "", request);
        PluginManagement mgmt = build.getPluginManagement();
        if (mgmt != null)
          validate20RawPlugins(problems, mgmt.getPlugins(), "build.pluginManagement.plugins.plugin.", "", request); 
      } 
      Set<String> profileIds = new HashSet<>();
      for (Profile profile : m.getProfiles()) {
        String prefix = "profiles.profile[" + profile.getId() + "].";
        if (!profileIds.add(profile.getId()))
          addViolation(problems, errOn30, ModelProblem.Version.V20, "profiles.profile.id", null, "must be unique but found duplicate profile with id " + profile
              .getId(), (InputLocationTracker)profile); 
        validate30RawProfileActivation(problems, profile.getActivation(), profile.getId(), prefix, "activation", request);
        validate20RawDependencies(problems, profile.getDependencies(), prefix, "dependencies.dependency.", request);
        if (profile.getDependencyManagement() != null)
          validate20RawDependencies(problems, profile.getDependencyManagement().getDependencies(), prefix, "dependencyManagement.dependencies.dependency.", request); 
        validateRawRepositories(problems, profile.getRepositories(), prefix, "repositories.repository.", request);
        validateRawRepositories(problems, profile.getPluginRepositories(), prefix, "pluginRepositories.pluginRepository.", request);
        BuildBase buildBase = profile.getBuild();
        if (buildBase != null) {
          validate20RawPlugins(problems, buildBase.getPlugins(), prefix, "plugins.plugin.", request);
          PluginManagement mgmt = buildBase.getPluginManagement();
          if (mgmt != null)
            validate20RawPlugins(problems, mgmt.getPlugins(), prefix, "pluginManagement.plugins.plugin.", request); 
        } 
      } 
    } 
  }
  
  private void validate30RawProfileActivation(ModelProblemCollector problems, Activation activation, String sourceHint, String prefix, String fieldName, ModelBuildingRequest request) {
    if (activation == null)
      return; 
    ActivationFile file = activation.getFile();
    if (file != null) {
      String path;
      boolean missing;
      if (StringUtils.isNotEmpty(file.getExists())) {
        path = file.getExists();
        missing = false;
      } else if (StringUtils.isNotEmpty(file.getMissing())) {
        path = file.getMissing();
        missing = true;
      } else {
        return;
      } 
      if (path.contains("${project.basedir}")) {
        addViolation(problems, ModelProblem.Severity.WARNING, ModelProblem.Version.V30, prefix + fieldName + (missing ? ".file.missing" : ".file.exists"), null, "Failed to interpolate file location " + path + " for profile " + sourceHint + ": ${project.basedir} expression not supported during profile activation, use ${basedir} instead", (InputLocationTracker)file
            
            .getLocation(missing ? "missing" : "exists"));
      } else if (hasProjectExpression(path)) {
        addViolation(problems, ModelProblem.Severity.WARNING, ModelProblem.Version.V30, prefix + fieldName + (missing ? ".file.missing" : ".file.exists"), null, "Failed to interpolate file location " + path + " for profile " + sourceHint + ": ${project.*} expressions are not supported during profile activation", (InputLocationTracker)file
            
            .getLocation(missing ? "missing" : "exists"));
      } 
    } 
  }
  
  private void validate20RawPlugins(ModelProblemCollector problems, List<Plugin> plugins, String prefix, String prefix2, ModelBuildingRequest request) {
    ModelProblem.Severity errOn31 = getSeverity(request, 31);
    Map<String, Plugin> index = new HashMap<>();
    for (Plugin plugin : plugins) {
      if (plugin.getGroupId() == null || (plugin
        .getGroupId() != null && plugin.getGroupId().trim().isEmpty()))
        addViolation(problems, ModelProblem.Severity.FATAL, ModelProblem.Version.V20, prefix + prefix2 + "(groupId:artifactId)", null, "groupId of a plugin must be defined. ", (InputLocationTracker)plugin); 
      if (plugin.getArtifactId() == null || (plugin
        .getArtifactId() != null && plugin.getArtifactId().trim().isEmpty()))
        addViolation(problems, ModelProblem.Severity.FATAL, ModelProblem.Version.V20, prefix + prefix2 + "(groupId:artifactId)", null, "artifactId of a plugin must be defined. ", (InputLocationTracker)plugin); 
      if (plugin.getVersion() != null && plugin.getVersion().trim().isEmpty())
        addViolation(problems, ModelProblem.Severity.FATAL, ModelProblem.Version.V20, prefix + prefix2 + "(groupId:artifactId)", null, "version of a plugin must be defined. ", (InputLocationTracker)plugin); 
      String key = plugin.getKey();
      Plugin existing = index.get(key);
      if (existing != null) {
        addViolation(problems, errOn31, ModelProblem.Version.V20, prefix + prefix2 + "(groupId:artifactId)", null, "must be unique but found duplicate declaration of plugin " + key, (InputLocationTracker)plugin);
      } else {
        index.put(key, plugin);
      } 
      Set<String> executionIds = new HashSet<>();
      for (PluginExecution exec : plugin.getExecutions()) {
        if (!executionIds.add(exec.getId()))
          addViolation(problems, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, prefix + prefix2 + "[" + plugin
              .getKey() + "].executions.execution.id", null, "must be unique but found duplicate execution with id " + exec
              .getId(), (InputLocationTracker)exec); 
      } 
    } 
  }
  
  public void validateEffectiveModel(Model m, ModelBuildingRequest request, ModelProblemCollector problems) {
    validateStringNotEmpty("modelVersion", problems, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, m.getModelVersion(), (InputLocationTracker)m);
    validateId("groupId", problems, m.getGroupId(), (InputLocationTracker)m);
    validateId("artifactId", problems, m.getArtifactId(), (InputLocationTracker)m);
    validateStringNotEmpty("packaging", problems, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, m.getPackaging(), (InputLocationTracker)m);
    if (!m.getModules().isEmpty()) {
      if (!"pom".equals(m.getPackaging()))
        addViolation(problems, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, "packaging", null, "with value '" + m
            .getPackaging() + "' is invalid. Aggregator projects require 'pom' as packaging.", (InputLocationTracker)m); 
      for (int i = 0, n = m.getModules().size(); i < n; i++) {
        String module = m.getModules().get(i);
        if (StringUtils.isBlank(module))
          addViolation(problems, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, "modules.module[" + i + "]", null, "has been specified without a path to the project directory.", (InputLocationTracker)m
              
              .getLocation("modules")); 
      } 
    } 
    validateStringNotEmpty("version", problems, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, m.getVersion(), (InputLocationTracker)m);
    ModelProblem.Severity errOn30 = getSeverity(request, 30);
    validateEffectiveDependencies(problems, m, m.getDependencies(), false, request);
    DependencyManagement mgmt = m.getDependencyManagement();
    if (mgmt != null)
      validateEffectiveDependencies(problems, m, mgmt.getDependencies(), true, request); 
    if (request.getValidationLevel() >= 20) {
      Set<String> modules = new HashSet<>();
      for (int i = 0, n = m.getModules().size(); i < n; i++) {
        String module = m.getModules().get(i);
        if (!modules.add(module))
          addViolation(problems, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, "modules.module[" + i + "]", null, "specifies duplicate child module " + module, (InputLocationTracker)m
              .getLocation("modules")); 
      } 
      ModelProblem.Severity errOn31 = getSeverity(request, 31);
      validateBannedCharacters("", "version", problems, errOn31, ModelProblem.Version.V20, m.getVersion(), null, (InputLocationTracker)m, "\\/:\"<>|?*");
      validate20ProperSnapshotVersion("version", problems, errOn31, ModelProblem.Version.V20, m.getVersion(), null, (InputLocationTracker)m);
      Build build = m.getBuild();
      if (build != null) {
        for (Plugin p : build.getPlugins()) {
          validateStringNotEmpty("build.plugins.plugin.artifactId", problems, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, p
              .getArtifactId(), (InputLocationTracker)p);
          validateStringNotEmpty("build.plugins.plugin.groupId", problems, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, p
              .getGroupId(), (InputLocationTracker)p);
          validate20PluginVersion("build.plugins.plugin.version", problems, p.getVersion(), p.getKey(), (InputLocationTracker)p, request);
          validateBoolean("build.plugins.plugin.inherited", "", problems, errOn30, ModelProblem.Version.V20, p
              .getInherited(), p.getKey(), (InputLocationTracker)p);
          validateBoolean("build.plugins.plugin.extensions", "", problems, errOn30, ModelProblem.Version.V20, p
              .getExtensions(), p.getKey(), (InputLocationTracker)p);
          validate20EffectivePluginDependencies(problems, p, request);
        } 
        validate20RawResources(problems, build.getResources(), "build.resources.resource.", request);
        validate20RawResources(problems, build.getTestResources(), "build.testResources.testResource.", request);
      } 
      Reporting reporting = m.getReporting();
      if (reporting != null)
        for (ReportPlugin p : reporting.getPlugins()) {
          validateStringNotEmpty("reporting.plugins.plugin.artifactId", problems, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, p
              .getArtifactId(), (InputLocationTracker)p);
          validateStringNotEmpty("reporting.plugins.plugin.groupId", problems, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, p
              .getGroupId(), (InputLocationTracker)p);
        }  
      for (Repository repository : m.getRepositories())
        validate20EffectiveRepository(problems, repository, "repositories.repository.", request); 
      for (Repository repository : m.getPluginRepositories())
        validate20EffectiveRepository(problems, repository, "pluginRepositories.pluginRepository.", request); 
      DistributionManagement distMgmt = m.getDistributionManagement();
      if (distMgmt != null) {
        if (distMgmt.getStatus() != null)
          addViolation(problems, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, "distributionManagement.status", null, "must not be specified.", (InputLocationTracker)distMgmt); 
        validate20EffectiveRepository(problems, (Repository)distMgmt.getRepository(), "distributionManagement.repository.", request);
        validate20EffectiveRepository(problems, (Repository)distMgmt.getSnapshotRepository(), "distributionManagement.snapshotRepository.", request);
      } 
    } 
  }
  
  private void validate20RawDependencies(ModelProblemCollector problems, List<Dependency> dependencies, String prefix, String prefix2, ModelBuildingRequest request) {
    ModelProblem.Severity errOn30 = getSeverity(request, 30);
    ModelProblem.Severity errOn31 = getSeverity(request, 31);
    Map<String, Dependency> index = new HashMap<>();
    for (Dependency dependency : dependencies) {
      String key = dependency.getManagementKey();
      if ("import".equals(dependency.getScope())) {
        if (!"pom".equals(dependency.getType())) {
          addViolation(problems, ModelProblem.Severity.WARNING, ModelProblem.Version.V20, prefix + prefix2 + "type", key, "must be 'pom' to import the managed dependencies.", (InputLocationTracker)dependency);
        } else if (StringUtils.isNotEmpty(dependency.getClassifier())) {
          addViolation(problems, errOn30, ModelProblem.Version.V20, prefix + prefix2 + "classifier", key, "must be empty, imported POM cannot have a classifier.", (InputLocationTracker)dependency);
        } 
      } else if ("system".equals(dependency.getScope())) {
        if (request.getValidationLevel() >= 31)
          addViolation(problems, ModelProblem.Severity.WARNING, ModelProblem.Version.V31, prefix + prefix2 + "scope", key, "declares usage of deprecated 'system' scope ", (InputLocationTracker)dependency); 
        String sysPath = dependency.getSystemPath();
        if (StringUtils.isNotEmpty(sysPath))
          if (!hasExpression(sysPath)) {
            addViolation(problems, ModelProblem.Severity.WARNING, ModelProblem.Version.V20, prefix + prefix2 + "systemPath", key, "should use a variable instead of a hard-coded path " + sysPath, (InputLocationTracker)dependency);
          } else if (sysPath.contains("${basedir}") || sysPath.contains("${project.basedir}")) {
            addViolation(problems, ModelProblem.Severity.WARNING, ModelProblem.Version.V20, prefix + prefix2 + "systemPath", key, "should not point at files within the project directory, " + sysPath + " will be unresolvable by dependent projects", (InputLocationTracker)dependency);
          }  
      } 
      if (equals("LATEST", dependency.getVersion()) || equals("RELEASE", dependency.getVersion()))
        addViolation(problems, ModelProblem.Severity.WARNING, ModelProblem.Version.BASE, prefix + prefix2 + "version", key, "is either LATEST or RELEASE (both of them are being deprecated)", (InputLocationTracker)dependency); 
      Dependency existing = index.get(key);
      if (existing != null) {
        String msg;
        if (equals(existing.getVersion(), dependency.getVersion())) {
          msg = "duplicate declaration of version " + Objects.toString(dependency.getVersion(), "(?)");
        } else {
          msg = "version " + Objects.toString(existing.getVersion(), "(?)") + " vs " + Objects.toString(dependency.getVersion(), "(?)");
        } 
        addViolation(problems, errOn31, ModelProblem.Version.V20, prefix + prefix2 + "(groupId:artifactId:type:classifier)", null, "must be unique: " + key + " -> " + msg, (InputLocationTracker)dependency);
        continue;
      } 
      index.put(key, dependency);
    } 
  }
  
  private void validate20RawDependenciesSelfReferencing(ModelProblemCollector problems, Model m, List<Dependency> dependencies, String prefix, ModelBuildingRequest request) {
    for (Dependency dependency : dependencies) {
      String key = dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion() + ((dependency.getClassifier() != null) ? (":" + dependency.getClassifier()) : "");
      String mKey = m.getGroupId() + ":" + m.getArtifactId() + ":" + m.getVersion();
      if (key.equals(mKey))
        addViolation(problems, ModelProblem.Severity.FATAL, ModelProblem.Version.V31, prefix + "[" + key + "]", key, "is referencing itself.", (InputLocationTracker)dependency); 
    } 
  }
  
  private void validateEffectiveDependencies(ModelProblemCollector problems, Model m, List<Dependency> dependencies, boolean management, ModelBuildingRequest request) {
    ModelProblem.Severity errOn30 = getSeverity(request, 30);
    String prefix = management ? "dependencyManagement.dependencies.dependency." : "dependencies.dependency.";
    for (Dependency d : dependencies) {
      validateEffectiveDependency(problems, d, management, prefix, request);
      if (request.getValidationLevel() >= 20) {
        validateBoolean(prefix, "optional", problems, errOn30, ModelProblem.Version.V20, d.getOptional(), d
            .getManagementKey(), (InputLocationTracker)d);
        if (!management) {
          validateVersion(prefix, "version", problems, errOn30, ModelProblem.Version.V20, d.getVersion(), d
              .getManagementKey(), (InputLocationTracker)d);
          validateEnum(prefix, "scope", problems, ModelProblem.Severity.WARNING, ModelProblem.Version.V20, d.getScope(), d
              .getManagementKey(), (InputLocationTracker)d, new String[] { "provided", "compile", "runtime", "test", "system" });
          validateEffectiveModelAgainstDependency(prefix, problems, m, d, request);
          continue;
        } 
        validateEnum(prefix, "scope", problems, ModelProblem.Severity.WARNING, ModelProblem.Version.V20, d.getScope(), d
            .getManagementKey(), (InputLocationTracker)d, new String[] { "provided", "compile", "runtime", "test", "system", "import" });
      } 
    } 
  }
  
  private void validateEffectiveModelAgainstDependency(String prefix, ModelProblemCollector problems, Model m, Dependency d, ModelBuildingRequest request) {
    String key = d.getGroupId() + ":" + d.getArtifactId() + ":" + d.getVersion() + ((d.getClassifier() != null) ? (":" + d.getClassifier()) : "");
    String mKey = m.getGroupId() + ":" + m.getArtifactId() + ":" + m.getVersion();
    if (key.equals(mKey))
      addViolation(problems, ModelProblem.Severity.FATAL, ModelProblem.Version.V31, prefix + "[" + key + "]", key, "is referencing itself.", (InputLocationTracker)d); 
  }
  
  private void validate20EffectivePluginDependencies(ModelProblemCollector problems, Plugin plugin, ModelBuildingRequest request) {
    List<Dependency> dependencies = plugin.getDependencies();
    if (!dependencies.isEmpty()) {
      String prefix = "build.plugins.plugin[" + plugin.getKey() + "].dependencies.dependency.";
      ModelProblem.Severity errOn30 = getSeverity(request, 30);
      for (Dependency d : dependencies) {
        validateEffectiveDependency(problems, d, false, prefix, request);
        validateVersion(prefix, "version", problems, errOn30, ModelProblem.Version.BASE, d.getVersion(), d
            .getManagementKey(), (InputLocationTracker)d);
        validateEnum(prefix, "scope", problems, errOn30, ModelProblem.Version.BASE, d.getScope(), d.getManagementKey(), (InputLocationTracker)d, new String[] { "compile", "runtime", "system" });
      } 
    } 
  }
  
  private void validateEffectiveDependency(ModelProblemCollector problems, Dependency d, boolean management, String prefix, ModelBuildingRequest request) {
    validateId(prefix, "artifactId", problems, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, d.getArtifactId(), d
        .getManagementKey(), (InputLocationTracker)d);
    validateId(prefix, "groupId", problems, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, d.getGroupId(), d
        .getManagementKey(), (InputLocationTracker)d);
    if (!management) {
      validateStringNotEmpty(prefix, "type", problems, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, d.getType(), d
          .getManagementKey(), (InputLocationTracker)d);
      validateDependencyVersion(problems, d, prefix);
    } 
    if ("system".equals(d.getScope())) {
      String systemPath = d.getSystemPath();
      if (StringUtils.isEmpty(systemPath)) {
        addViolation(problems, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, prefix + "systemPath", d.getManagementKey(), "is missing.", (InputLocationTracker)d);
      } else {
        File sysFile = new File(systemPath);
        if (!sysFile.isAbsolute()) {
          addViolation(problems, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, prefix + "systemPath", d.getManagementKey(), "must specify an absolute path but is " + systemPath, (InputLocationTracker)d);
        } else if (!sysFile.isFile()) {
          String msg = "refers to a non-existing file " + sysFile.getAbsolutePath();
          systemPath = systemPath.replace('/', File.separatorChar).replace('\\', File.separatorChar);
          String jdkHome = request.getSystemProperties().getProperty("java.home", "") + File.separator + "..";
          if (systemPath.startsWith(jdkHome))
            msg = msg + ". Please verify that you run Maven using a JDK and not just a JRE."; 
          addViolation(problems, ModelProblem.Severity.WARNING, ModelProblem.Version.BASE, prefix + "systemPath", d.getManagementKey(), msg, (InputLocationTracker)d);
        } 
      } 
    } else if (StringUtils.isNotEmpty(d.getSystemPath())) {
      addViolation(problems, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, prefix + "systemPath", d.getManagementKey(), "must be omitted. This field may only be specified for a dependency with system scope.", (InputLocationTracker)d);
    } 
    if (request.getValidationLevel() >= 20)
      for (Exclusion exclusion : d.getExclusions()) {
        if (request.getValidationLevel() < 30) {
          validateId(prefix, "exclusions.exclusion.groupId", problems, ModelProblem.Severity.WARNING, ModelProblem.Version.V20, exclusion
              .getGroupId(), d.getManagementKey(), (InputLocationTracker)exclusion);
          validateId(prefix, "exclusions.exclusion.artifactId", problems, ModelProblem.Severity.WARNING, ModelProblem.Version.V20, exclusion
              .getArtifactId(), d.getManagementKey(), (InputLocationTracker)exclusion);
          continue;
        } 
        validateIdWithWildcards(prefix, "exclusions.exclusion.groupId", problems, ModelProblem.Severity.WARNING, ModelProblem.Version.V30, exclusion
            .getGroupId(), d.getManagementKey(), (InputLocationTracker)exclusion);
        validateIdWithWildcards(prefix, "exclusions.exclusion.artifactId", problems, ModelProblem.Severity.WARNING, ModelProblem.Version.V30, exclusion
            .getArtifactId(), d.getManagementKey(), (InputLocationTracker)exclusion);
      }  
  }
  
  protected void validateDependencyVersion(ModelProblemCollector problems, Dependency d, String prefix) {
    validateStringNotEmpty(prefix, "version", problems, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, d.getVersion(), d
        .getManagementKey(), (InputLocationTracker)d);
  }
  
  private void validateRawRepositories(ModelProblemCollector problems, List<Repository> repositories, String prefix, String prefix2, ModelBuildingRequest request) {
    Map<String, Repository> index = new HashMap<>();
    for (Repository repository : repositories) {
      validateStringNotEmpty(prefix, prefix2, "id", problems, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, repository.getId(), null, (InputLocationTracker)repository);
      validateStringNotEmpty(prefix, prefix2, "[" + repository.getId() + "].url", problems, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, repository
          .getUrl(), null, (InputLocationTracker)repository);
      String key = repository.getId();
      Repository existing = index.get(key);
      if (existing != null) {
        ModelProblem.Severity errOn30 = getSeverity(request, 30);
        addViolation(problems, errOn30, ModelProblem.Version.V20, prefix + prefix2 + "id", null, "must be unique: " + repository
            .getId() + " -> " + existing.getUrl() + " vs " + repository.getUrl(), (InputLocationTracker)repository);
        continue;
      } 
      index.put(key, repository);
    } 
  }
  
  private void validate20EffectiveRepository(ModelProblemCollector problems, Repository repository, String prefix, ModelBuildingRequest request) {
    if (repository != null) {
      ModelProblem.Severity errOn31 = getSeverity(request, 31);
      validateBannedCharacters(prefix, "id", problems, errOn31, ModelProblem.Version.V20, repository.getId(), null, (InputLocationTracker)repository, "\\/:\"<>|?*");
      if ("local".equals(repository.getId()))
        addViolation(problems, errOn31, ModelProblem.Version.V20, prefix + "id", null, "must not be 'local', this identifier is reserved for the local repository, using it for other repositories will corrupt your repository metadata.", (InputLocationTracker)repository); 
      if ("legacy".equals(repository.getLayout()))
        addViolation(problems, ModelProblem.Severity.WARNING, ModelProblem.Version.V20, prefix + "layout", repository.getId(), "uses the unsupported value 'legacy', artifact resolution might fail.", (InputLocationTracker)repository); 
    } 
  }
  
  private void validate20RawResources(ModelProblemCollector problems, List<Resource> resources, String prefix, ModelBuildingRequest request) {
    ModelProblem.Severity errOn30 = getSeverity(request, 30);
    for (Resource resource : resources) {
      validateStringNotEmpty(prefix, "directory", problems, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, resource
          .getDirectory(), null, (InputLocationTracker)resource);
      validateBoolean(prefix, "filtering", problems, errOn30, ModelProblem.Version.V20, resource.getFiltering(), resource
          .getDirectory(), (InputLocationTracker)resource);
    } 
  }
  
  private boolean validateId(String fieldName, ModelProblemCollector problems, String id, InputLocationTracker tracker) {
    return validateId("", fieldName, problems, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, id, null, tracker);
  }
  
  private boolean validateId(String prefix, String fieldName, ModelProblemCollector problems, ModelProblem.Severity severity, ModelProblem.Version version, String id, String sourceHint, InputLocationTracker tracker) {
    if (this.validIds.contains(id))
      return true; 
    if (!validateStringNotEmpty(prefix, fieldName, problems, severity, version, id, sourceHint, tracker))
      return false; 
    if (!isValidId(id)) {
      addViolation(problems, severity, version, prefix + fieldName, sourceHint, "with value '" + id + "' does not match a valid id pattern.", tracker);
      return false;
    } 
    this.validIds.add(id);
    return true;
  }
  
  private boolean isValidId(String id) {
    for (int i = 0; i < id.length(); i++) {
      char c = id.charAt(i);
      if (!isValidIdCharacter(c))
        return false; 
    } 
    return true;
  }
  
  private boolean isValidIdCharacter(char c) {
    return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '-' || c == '_' || c == '.');
  }
  
  private boolean validateIdWithWildcards(String prefix, String fieldName, ModelProblemCollector problems, ModelProblem.Severity severity, ModelProblem.Version version, String id, String sourceHint, InputLocationTracker tracker) {
    if (!validateStringNotEmpty(prefix, fieldName, problems, severity, version, id, sourceHint, tracker))
      return false; 
    if (!isValidIdWithWildCards(id)) {
      addViolation(problems, severity, version, prefix + fieldName, sourceHint, "with value '" + id + "' does not match a valid id pattern.", tracker);
      return false;
    } 
    return true;
  }
  
  private boolean isValidIdWithWildCards(String id) {
    for (int i = 0; i < id.length(); i++) {
      char c = id.charAt(i);
      if (!isValidIdWithWildCardCharacter(c))
        return false; 
    } 
    return true;
  }
  
  private boolean isValidIdWithWildCardCharacter(char c) {
    return (isValidIdCharacter(c) || c == '?' || c == '*');
  }
  
  private boolean validateStringNoExpression(String fieldName, ModelProblemCollector problems, ModelProblem.Severity severity, ModelProblem.Version version, String string, InputLocationTracker tracker) {
    if (!hasExpression(string))
      return true; 
    addViolation(problems, severity, version, fieldName, null, "contains an expression but should be a constant.", tracker);
    return false;
  }
  
  private boolean validateVersionNoExpression(String fieldName, ModelProblemCollector problems, ModelProblem.Severity severity, ModelProblem.Version version, String string, InputLocationTracker tracker) {
    if (!hasExpression(string))
      return true; 
    Matcher m = CI_FRIENDLY_EXPRESSION.matcher(string.trim());
    while (m.find()) {
      String property = m.group(1);
      if (!this.versionProcessor.isValidProperty(property)) {
        addViolation(problems, severity, version, fieldName, null, "contains an expression but should be a constant.", tracker);
        return false;
      } 
    } 
    return true;
  }
  
  private boolean hasExpression(String value) {
    return (value != null && value.contains("${"));
  }
  
  private boolean hasProjectExpression(String value) {
    return (value != null && value.contains("${project."));
  }
  
  private boolean validateStringNotEmpty(String fieldName, ModelProblemCollector problems, ModelProblem.Severity severity, ModelProblem.Version version, String string, InputLocationTracker tracker) {
    return validateStringNotEmpty("", fieldName, problems, severity, version, string, null, tracker);
  }
  
  private boolean validateStringNotEmpty(String prefix, String prefix2, String fieldName, ModelProblemCollector problems, ModelProblem.Severity severity, ModelProblem.Version version, String string, String sourceHint, InputLocationTracker tracker) {
    if (!validateNotNull(prefix, prefix2, fieldName, problems, severity, version, string, sourceHint, tracker))
      return false; 
    if (!string.isEmpty())
      return true; 
    addViolation(problems, severity, version, prefix + prefix2 + fieldName, sourceHint, "is missing.", tracker);
    return false;
  }
  
  private boolean validateStringNotEmpty(String prefix, String fieldName, ModelProblemCollector problems, ModelProblem.Severity severity, ModelProblem.Version version, String string, String sourceHint, InputLocationTracker tracker) {
    if (!validateNotNull(prefix, fieldName, problems, severity, version, string, sourceHint, tracker))
      return false; 
    if (string.length() > 0)
      return true; 
    addViolation(problems, severity, version, prefix + fieldName, sourceHint, "is missing.", tracker);
    return false;
  }
  
  private boolean validateNotNull(String prefix, String fieldName, ModelProblemCollector problems, ModelProblem.Severity severity, ModelProblem.Version version, Object object, String sourceHint, InputLocationTracker tracker) {
    if (object != null)
      return true; 
    addViolation(problems, severity, version, prefix + fieldName, sourceHint, "is missing.", tracker);
    return false;
  }
  
  private boolean validateNotNull(String prefix, String prefix2, String fieldName, ModelProblemCollector problems, ModelProblem.Severity severity, ModelProblem.Version version, Object object, String sourceHint, InputLocationTracker tracker) {
    if (object != null)
      return true; 
    addViolation(problems, severity, version, prefix + prefix2 + fieldName, sourceHint, "is missing.", tracker);
    return false;
  }
  
  private boolean validateBoolean(String prefix, String fieldName, ModelProblemCollector problems, ModelProblem.Severity severity, ModelProblem.Version version, String string, String sourceHint, InputLocationTracker tracker) {
    if (string == null || string.length() <= 0)
      return true; 
    if ("true".equalsIgnoreCase(string) || "false".equalsIgnoreCase(string))
      return true; 
    addViolation(problems, severity, version, prefix + fieldName, sourceHint, "must be 'true' or 'false' but is '" + string + "'.", tracker);
    return false;
  }
  
  private boolean validateEnum(String prefix, String fieldName, ModelProblemCollector problems, ModelProblem.Severity severity, ModelProblem.Version version, String string, String sourceHint, InputLocationTracker tracker, String... validValues) {
    if (string == null || string.length() <= 0)
      return true; 
    List<String> values = Arrays.asList(validValues);
    if (values.contains(string))
      return true; 
    addViolation(problems, severity, version, prefix + fieldName, sourceHint, "must be one of " + values + " but is '" + string + "'.", tracker);
    return false;
  }
  
  private boolean validateModelVersion(ModelProblemCollector problems, String string, InputLocationTracker tracker, String... validVersions) {
    if (string == null || string.length() <= 0)
      return true; 
    List<String> values = Arrays.asList(validVersions);
    if (values.contains(string))
      return true; 
    boolean newerThanAll = true;
    boolean olderThanAll = true;
    for (String validValue : validVersions) {
      int comparison = compareModelVersions(validValue, string);
      newerThanAll = (newerThanAll && comparison < 0);
      olderThanAll = (olderThanAll && comparison > 0);
    } 
    if (newerThanAll) {
      addViolation(problems, ModelProblem.Severity.FATAL, ModelProblem.Version.V20, "modelVersion", null, "of '" + string + "' is newer than the versions supported by this version of Maven: " + values + ". Building this project requires a newer version of Maven.", tracker);
    } else if (olderThanAll) {
      addViolation(problems, ModelProblem.Severity.FATAL, ModelProblem.Version.V20, "modelVersion", null, "of '" + string + "' is older than the versions supported by this version of Maven: " + values + ". Building this project requires an older version of Maven.", tracker);
    } else {
      addViolation(problems, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, "modelVersion", null, "must be one of " + values + " but is '" + string + "'.", tracker);
    } 
    return false;
  }
  
  private static int compareModelVersions(String first, String second) {
    String[] firstSegments = StringUtils.split(first, ".");
    String[] secondSegments = StringUtils.split(second, ".");
    for (int i = 0; i < Math.min(firstSegments.length, secondSegments.length); i++) {
      int result = Long.valueOf(firstSegments[i]).compareTo(Long.valueOf(secondSegments[i]));
      if (result != 0)
        return result; 
    } 
    if (firstSegments.length == secondSegments.length)
      return 0; 
    return (firstSegments.length > secondSegments.length) ? -1 : 1;
  }
  
  private boolean validateBannedCharacters(String prefix, String fieldName, ModelProblemCollector problems, ModelProblem.Severity severity, ModelProblem.Version version, String string, String sourceHint, InputLocationTracker tracker, String banned) {
    if (string != null)
      for (int i = string.length() - 1; i >= 0; i--) {
        if (banned.indexOf(string.charAt(i)) >= 0) {
          addViolation(problems, severity, version, prefix + fieldName, sourceHint, "must not contain any of these characters " + banned + " but found " + string
              
              .charAt(i), tracker);
          return false;
        } 
      }  
    return true;
  }
  
  private boolean validateVersion(String prefix, String fieldName, ModelProblemCollector problems, ModelProblem.Severity severity, ModelProblem.Version version, String string, String sourceHint, InputLocationTracker tracker) {
    if (string == null || string.length() <= 0)
      return true; 
    if (hasExpression(string)) {
      addViolation(problems, severity, version, prefix + fieldName, sourceHint, "must be a valid version but is '" + string + "'.", tracker);
      return false;
    } 
    return validateBannedCharacters(prefix, fieldName, problems, severity, version, string, sourceHint, tracker, "\\/:\"<>|?*");
  }
  
  private boolean validate20ProperSnapshotVersion(String fieldName, ModelProblemCollector problems, ModelProblem.Severity severity, ModelProblem.Version version, String string, String sourceHint, InputLocationTracker tracker) {
    if (string == null || string.length() <= 0)
      return true; 
    if (string.endsWith("SNAPSHOT") && !string.endsWith("-SNAPSHOT")) {
      addViolation(problems, severity, version, fieldName, sourceHint, "uses an unsupported snapshot version format, should be '*-SNAPSHOT' instead.", tracker);
      return false;
    } 
    return true;
  }
  
  private boolean validate20PluginVersion(String fieldName, ModelProblemCollector problems, String string, String sourceHint, InputLocationTracker tracker, ModelBuildingRequest request) {
    if (string == null)
      return true; 
    ModelProblem.Severity errOn30 = getSeverity(request, 30);
    if (!validateVersion("", fieldName, problems, errOn30, ModelProblem.Version.V20, string, sourceHint, tracker))
      return false; 
    if (string.length() <= 0 || "RELEASE".equals(string) || "LATEST".equals(string)) {
      addViolation(problems, errOn30, ModelProblem.Version.V20, fieldName, sourceHint, "must be a valid version but is '" + string + "'.", tracker);
      return false;
    } 
    return true;
  }
  
  private static void addViolation(ModelProblemCollector problems, ModelProblem.Severity severity, ModelProblem.Version version, String fieldName, String sourceHint, String message, InputLocationTracker tracker) {
    StringBuilder buffer = new StringBuilder(256);
    buffer.append('\'').append(fieldName).append('\'');
    if (sourceHint != null)
      buffer.append(" for ").append(sourceHint); 
    buffer.append(' ').append(message);
    problems.add((new ModelProblemCollectorRequest(severity, version)).setMessage(buffer
          .toString()).setLocation(getLocation(fieldName, tracker)));
  }
  
  private static InputLocation getLocation(String fieldName, InputLocationTracker tracker) {
    InputLocation location = null;
    if (tracker != null) {
      if (fieldName != null) {
        Object key = fieldName;
        int idx = fieldName.lastIndexOf('.');
        if (idx >= 0) {
          fieldName = fieldName.substring(idx + 1);
          key = fieldName;
        } 
        if (fieldName.endsWith("]")) {
          key = fieldName.substring(fieldName.lastIndexOf('[') + 1, fieldName.length() - 1);
          try {
            key = Integer.valueOf(key.toString());
          } catch (NumberFormatException numberFormatException) {}
        } 
        location = tracker.getLocation(key);
      } 
      if (location == null)
        location = tracker.getLocation(""); 
    } 
    return location;
  }
  
  private static boolean equals(String s1, String s2) {
    return StringUtils.clean(s1).equals(StringUtils.clean(s2));
  }
  
  private static ModelProblem.Severity getSeverity(ModelBuildingRequest request, int errorThreshold) {
    return getSeverity(request.getValidationLevel(), errorThreshold);
  }
  
  private static ModelProblem.Severity getSeverity(int validationLevel, int errorThreshold) {
    if (validationLevel < errorThreshold)
      return ModelProblem.Severity.WARNING; 
    return ModelProblem.Severity.ERROR;
  }
}
