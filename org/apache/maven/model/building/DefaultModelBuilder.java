package org.apache.maven.model.building;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputSource;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Repository;
import org.apache.maven.model.composition.DependencyManagementImporter;
import org.apache.maven.model.inheritance.InheritanceAssembler;
import org.apache.maven.model.interpolation.ModelInterpolator;
import org.apache.maven.model.io.ModelParseException;
import org.apache.maven.model.management.DependencyManagementInjector;
import org.apache.maven.model.management.PluginManagementInjector;
import org.apache.maven.model.normalization.ModelNormalizer;
import org.apache.maven.model.path.ModelPathTranslator;
import org.apache.maven.model.path.ModelUrlNormalizer;
import org.apache.maven.model.path.ProfileActivationFilePathInterpolator;
import org.apache.maven.model.plugin.LifecycleBindingsInjector;
import org.apache.maven.model.plugin.PluginConfigurationExpander;
import org.apache.maven.model.plugin.ReportConfigurationExpander;
import org.apache.maven.model.plugin.ReportingConverter;
import org.apache.maven.model.profile.DefaultProfileActivationContext;
import org.apache.maven.model.profile.ProfileActivationContext;
import org.apache.maven.model.profile.ProfileInjector;
import org.apache.maven.model.profile.ProfileSelector;
import org.apache.maven.model.resolution.InvalidRepositoryException;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.model.resolution.UnresolvableModelException;
import org.apache.maven.model.resolution.WorkspaceModelResolver;
import org.apache.maven.model.superpom.SuperPomProvider;
import org.apache.maven.model.validation.ModelValidator;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.MapBasedValueSource;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.codehaus.plexus.interpolation.ValueSource;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.sisu.Nullable;

@Named
@Singleton
public class DefaultModelBuilder implements ModelBuilder {
  @Inject
  private ModelProcessor modelProcessor;
  
  @Inject
  private ModelValidator modelValidator;
  
  @Inject
  private ModelNormalizer modelNormalizer;
  
  @Inject
  private ModelInterpolator modelInterpolator;
  
  @Inject
  private ModelPathTranslator modelPathTranslator;
  
  @Inject
  private ModelUrlNormalizer modelUrlNormalizer;
  
  @Inject
  private SuperPomProvider superPomProvider;
  
  @Inject
  private InheritanceAssembler inheritanceAssembler;
  
  @Inject
  private ProfileSelector profileSelector;
  
  @Inject
  private ProfileInjector profileInjector;
  
  @Inject
  private PluginManagementInjector pluginManagementInjector;
  
  @Inject
  private DependencyManagementInjector dependencyManagementInjector;
  
  @Inject
  private DependencyManagementImporter dependencyManagementImporter;
  
  @Inject
  @Nullable
  private LifecycleBindingsInjector lifecycleBindingsInjector;
  
  @Inject
  private PluginConfigurationExpander pluginConfigurationExpander;
  
  @Inject
  private ReportConfigurationExpander reportConfigurationExpander;
  
  @Inject
  private ReportingConverter reportingConverter;
  
  @Inject
  private ProfileActivationFilePathInterpolator profileActivationFilePathInterpolator;
  
  public DefaultModelBuilder setModelProcessor(ModelProcessor modelProcessor) {
    this.modelProcessor = modelProcessor;
    return this;
  }
  
  public DefaultModelBuilder setModelValidator(ModelValidator modelValidator) {
    this.modelValidator = modelValidator;
    return this;
  }
  
  public DefaultModelBuilder setModelNormalizer(ModelNormalizer modelNormalizer) {
    this.modelNormalizer = modelNormalizer;
    return this;
  }
  
  public DefaultModelBuilder setModelInterpolator(ModelInterpolator modelInterpolator) {
    this.modelInterpolator = modelInterpolator;
    return this;
  }
  
  public DefaultModelBuilder setModelPathTranslator(ModelPathTranslator modelPathTranslator) {
    this.modelPathTranslator = modelPathTranslator;
    return this;
  }
  
  public DefaultModelBuilder setModelUrlNormalizer(ModelUrlNormalizer modelUrlNormalizer) {
    this.modelUrlNormalizer = modelUrlNormalizer;
    return this;
  }
  
  public DefaultModelBuilder setSuperPomProvider(SuperPomProvider superPomProvider) {
    this.superPomProvider = superPomProvider;
    return this;
  }
  
  public DefaultModelBuilder setProfileSelector(ProfileSelector profileSelector) {
    this.profileSelector = profileSelector;
    return this;
  }
  
  public DefaultModelBuilder setProfileInjector(ProfileInjector profileInjector) {
    this.profileInjector = profileInjector;
    return this;
  }
  
  public DefaultModelBuilder setInheritanceAssembler(InheritanceAssembler inheritanceAssembler) {
    this.inheritanceAssembler = inheritanceAssembler;
    return this;
  }
  
  public DefaultModelBuilder setDependencyManagementImporter(DependencyManagementImporter depMgmtImporter) {
    this.dependencyManagementImporter = depMgmtImporter;
    return this;
  }
  
  public DefaultModelBuilder setDependencyManagementInjector(DependencyManagementInjector depMgmtInjector) {
    this.dependencyManagementInjector = depMgmtInjector;
    return this;
  }
  
  public DefaultModelBuilder setLifecycleBindingsInjector(LifecycleBindingsInjector lifecycleBindingsInjector) {
    this.lifecycleBindingsInjector = lifecycleBindingsInjector;
    return this;
  }
  
  public DefaultModelBuilder setPluginConfigurationExpander(PluginConfigurationExpander pluginConfigurationExpander) {
    this.pluginConfigurationExpander = pluginConfigurationExpander;
    return this;
  }
  
  public DefaultModelBuilder setPluginManagementInjector(PluginManagementInjector pluginManagementInjector) {
    this.pluginManagementInjector = pluginManagementInjector;
    return this;
  }
  
  public DefaultModelBuilder setReportConfigurationExpander(ReportConfigurationExpander reportConfigurationExpander) {
    this.reportConfigurationExpander = reportConfigurationExpander;
    return this;
  }
  
  public DefaultModelBuilder setReportingConverter(ReportingConverter reportingConverter) {
    this.reportingConverter = reportingConverter;
    return this;
  }
  
  public DefaultModelBuilder setProfileActivationFilePathInterpolator(ProfileActivationFilePathInterpolator profileActivationFilePathInterpolator) {
    this.profileActivationFilePathInterpolator = profileActivationFilePathInterpolator;
    return this;
  }
  
  public ModelBuildingResult build(ModelBuildingRequest request) throws ModelBuildingException {
    return build(request, new LinkedHashSet<>());
  }
  
  protected ModelBuildingResult build(ModelBuildingRequest request, Collection<String> importIds) throws ModelBuildingException {
    DefaultModelBuildingResult result = new DefaultModelBuildingResult();
    DefaultModelProblemCollector problems = new DefaultModelProblemCollector(result);
    DefaultProfileActivationContext profileActivationContext = getProfileActivationContext(request);
    problems.setSource("(external profiles)");
    List<Profile> activeExternalProfiles = this.profileSelector.getActiveProfiles(request.getProfiles(), (ProfileActivationContext)profileActivationContext, problems);
    result.setActiveExternalProfiles(activeExternalProfiles);
    if (!activeExternalProfiles.isEmpty()) {
      Properties profileProps = new Properties();
      for (Profile profile : activeExternalProfiles)
        profileProps.putAll(profile.getProperties()); 
      profileProps.putAll(profileActivationContext.getUserProperties());
      profileActivationContext.setUserProperties(profileProps);
    } 
    Model inputModel = request.getRawModel();
    if (inputModel == null)
      inputModel = readModel(request.getModelSource(), request.getPomFile(), request, problems); 
    problems.setRootModel(inputModel);
    ModelData resultData = new ModelData(request.getModelSource(), inputModel);
    ModelData superData = new ModelData(null, getSuperModel());
    Collection<String> parentIds = new LinkedHashSet<>();
    List<ModelData> lineage = new ArrayList<>();
    for (ModelData currentData = resultData; currentData != null; ) {
      lineage.add(currentData);
      Model rawModel = currentData.getModel();
      currentData.setRawModel(rawModel);
      Model tmpModel = rawModel.clone();
      currentData.setModel(tmpModel);
      problems.setSource(tmpModel);
      this.modelNormalizer.mergeDuplicates(tmpModel, request, problems);
      profileActivationContext.setProjectProperties(tmpModel.getProperties());
      List<Profile> activePomProfiles = this.profileSelector.getActiveProfiles(rawModel.getProfiles(), (ProfileActivationContext)profileActivationContext, problems);
      currentData.setActiveProfiles(activePomProfiles);
      Map<String, Activation> interpolatedActivations = getInterpolatedActivations(rawModel, profileActivationContext, problems);
      injectProfileActivations(tmpModel, interpolatedActivations);
      for (Profile activeProfile : activePomProfiles)
        this.profileInjector.injectProfile(tmpModel, activeProfile, request, problems); 
      if (currentData == resultData)
        for (Profile activeProfile : activeExternalProfiles)
          this.profileInjector.injectProfile(tmpModel, activeProfile, request, problems);  
      if (currentData == superData)
        break; 
      configureResolver(request.getModelResolver(), tmpModel, problems);
      ModelData parentData = readParent(tmpModel, currentData.getSource(), request, problems);
      if (parentData == null) {
        currentData = superData;
        continue;
      } 
      if (currentData == resultData) {
        currentData.setGroupId((currentData.getRawModel().getGroupId() == null) ? parentData.getGroupId() : currentData
            .getRawModel()
            .getGroupId());
        currentData.setVersion((currentData.getRawModel().getVersion() == null) ? parentData.getVersion() : currentData
            .getRawModel()
            .getVersion());
        currentData.setArtifactId(currentData.getRawModel().getArtifactId());
        parentIds.add(currentData.getId());
        currentData.setGroupId(null);
        currentData.setArtifactId(null);
        currentData.setVersion(null);
        currentData = parentData;
        continue;
      } 
      if (!parentIds.add(parentData.getId())) {
        String message = "The parents form a cycle: ";
        for (String modelId : parentIds)
          message = message + modelId + " -> "; 
        message = message + parentData.getId();
        problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.FATAL, ModelProblem.Version.BASE))
            .setMessage(message));
        throw problems.newModelBuildingException();
      } 
      currentData = parentData;
    } 
    problems.setSource(inputModel);
    checkPluginVersions(lineage, request, problems);
    assembleInheritance(lineage, request, problems);
    Model resultModel = resultData.getModel();
    problems.setSource(resultModel);
    problems.setRootModel(resultModel);
    resultModel = interpolateModel(resultModel, request, problems);
    resultData.setModel(resultModel);
    if (resultModel.getParent() != null) {
      ModelData parentData = lineage.get(1);
      if (parentData.getVersion() == null || parentData.getVersion().contains("${")) {
        Model interpolatedParent = interpolateModel(parentData.getModel(), request, problems);
        parentData.setVersion(interpolatedParent.getVersion());
      } 
    } 
    this.modelUrlNormalizer.normalize(resultModel, request);
    configureResolver(request.getModelResolver(), resultModel, problems, true);
    resultData.setGroupId(resultModel.getGroupId());
    resultData.setArtifactId(resultModel.getArtifactId());
    resultData.setVersion(resultModel.getVersion());
    result.setEffectiveModel(resultModel);
    for (ModelData modelData : lineage) {
      String modelId = (modelData != superData) ? modelData.getId() : "";
      result.addModelId(modelId);
      result.setActivePomProfiles(modelId, modelData.getActiveProfiles());
      result.setRawModel(modelId, modelData.getRawModel());
    } 
    if (!request.isTwoPhaseBuilding())
      build(request, result, importIds); 
    return result;
  }
  
  private Map<String, Activation> getInterpolatedActivations(Model rawModel, DefaultProfileActivationContext context, DefaultModelProblemCollector problems) {
    Map<String, Activation> interpolatedActivations = getProfileActivations(rawModel, true);
    for (Activation activation : interpolatedActivations.values()) {
      if (activation.getFile() != null)
        replaceWithInterpolatedValue(activation.getFile(), (ProfileActivationContext)context, problems); 
    } 
    return interpolatedActivations;
  }
  
  private void replaceWithInterpolatedValue(ActivationFile activationFile, ProfileActivationContext context, DefaultModelProblemCollector problems) {
    try {
      if (StringUtils.isNotEmpty(activationFile.getExists())) {
        String path = activationFile.getExists();
        String absolutePath = this.profileActivationFilePathInterpolator.interpolate(path, context);
        activationFile.setExists(absolutePath);
      } else if (StringUtils.isNotEmpty(activationFile.getMissing())) {
        String path = activationFile.getMissing();
        String absolutePath = this.profileActivationFilePathInterpolator.interpolate(path, context);
        activationFile.setMissing(absolutePath);
      } 
    } catch (InterpolationException e) {
      String path = StringUtils.isNotEmpty(activationFile.getExists()) ? activationFile.getExists() : activationFile.getMissing();
      problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE)).setMessage("Failed to interpolate file location " + path + ": " + e
            .getMessage()).setLocation(activationFile
            .getLocation(StringUtils.isNotEmpty(activationFile.getExists()) ? "exists" : "missing"))
          
          .setException((Exception)e));
    } 
  }
  
  public ModelBuildingResult build(ModelBuildingRequest request, ModelBuildingResult result) throws ModelBuildingException {
    return build(request, result, new LinkedHashSet<>());
  }
  
  private ModelBuildingResult build(ModelBuildingRequest request, ModelBuildingResult result, Collection<String> imports) throws ModelBuildingException {
    Model resultModel = result.getEffectiveModel();
    DefaultModelProblemCollector problems = new DefaultModelProblemCollector(result);
    problems.setSource(resultModel);
    problems.setRootModel(resultModel);
    this.modelPathTranslator.alignToBaseDirectory(resultModel, resultModel.getProjectDirectory(), request);
    this.pluginManagementInjector.injectManagement(resultModel, request, problems);
    fireEvent(resultModel, request, problems, ModelBuildingEventCatapult.BUILD_EXTENSIONS_ASSEMBLED);
    if (request.isProcessPlugins()) {
      if (this.lifecycleBindingsInjector == null)
        throw new IllegalStateException("lifecycle bindings injector is missing"); 
      this.lifecycleBindingsInjector.injectLifecycleBindings(resultModel, request, problems);
    } 
    importDependencyManagement(resultModel, request, problems, imports);
    this.dependencyManagementInjector.injectManagement(resultModel, request, problems);
    this.modelNormalizer.injectDefaultValues(resultModel, request, problems);
    if (request.isProcessPlugins()) {
      this.reportConfigurationExpander.expandPluginConfiguration(resultModel, request, problems);
      this.reportingConverter.convertReporting(resultModel, request, problems);
      this.pluginConfigurationExpander.expandPluginConfiguration(resultModel, request, problems);
    } 
    this.modelValidator.validateEffectiveModel(resultModel, request, problems);
    if (hasModelErrors(problems))
      throw problems.newModelBuildingException(); 
    return result;
  }
  
  public Result<? extends Model> buildRawModel(File pomFile, int validationLevel, boolean locationTracking) {
    ModelBuildingRequest request = (new DefaultModelBuildingRequest()).setValidationLevel(validationLevel).setLocationTracking(locationTracking);
    DefaultModelProblemCollector collector = new DefaultModelProblemCollector(new DefaultModelBuildingResult());
    try {
      return Result.newResult(readModel(null, pomFile, request, collector), collector.getProblems());
    } catch (ModelBuildingException e) {
      return Result.error(collector.getProblems());
    } 
  }
  
  private Model readModel(ModelSource modelSource, File pomFile, ModelBuildingRequest request, DefaultModelProblemCollector problems) throws ModelBuildingException {
    Model model;
    if (modelSource == null)
      if (pomFile != null) {
        modelSource = new FileModelSource(pomFile);
      } else {
        throw new NullPointerException("neither pomFile nor modelSource can be null");
      }  
    problems.setSource(modelSource.getLocation());
    try {
      boolean strict = (request.getValidationLevel() >= 20);
      InputSource source = request.isLocationTracking() ? new InputSource() : null;
      Map<String, Object> options = new HashMap<>();
      options.put("org.apache.maven.model.io.isStrict", Boolean.valueOf(strict));
      options.put("org.apache.maven.model.io.inputSource", source);
      options.put("org.apache.maven.model.building.source", modelSource);
      try {
        model = this.modelProcessor.read(modelSource.getInputStream(), options);
      } catch (ModelParseException e) {
        if (!strict)
          throw e; 
        options.put("org.apache.maven.model.io.isStrict", Boolean.FALSE);
        try {
          model = this.modelProcessor.read(modelSource.getInputStream(), options);
        } catch (ModelParseException ne) {
          throw e;
        } 
        if (pomFile != null) {
          problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.V20))
              .setMessage("Malformed POM " + modelSource.getLocation() + ": " + e.getMessage())
              .setException((Exception)e));
        } else {
          problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.WARNING, ModelProblem.Version.V20))
              .setMessage("Malformed POM " + modelSource.getLocation() + ": " + e.getMessage())
              .setException((Exception)e));
        } 
      } 
      if (source != null) {
        source.setModelId(ModelProblemUtils.toId(model));
        source.setLocation(modelSource.getLocation());
      } 
    } catch (ModelParseException e) {
      problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.FATAL, ModelProblem.Version.BASE))
          .setMessage("Non-parseable POM " + modelSource.getLocation() + ": " + e.getMessage())
          .setException((Exception)e));
      throw problems.newModelBuildingException();
    } catch (IOException e) {
      String msg = e.getMessage();
      if (msg == null || msg.length() <= 0)
        if (e.getClass().getName().endsWith("MalformedInputException")) {
          msg = "Some input bytes do not match the file encoding.";
        } else {
          msg = e.getClass().getSimpleName();
        }  
      problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.FATAL, ModelProblem.Version.BASE))
          .setMessage("Non-readable POM " + modelSource.getLocation() + ": " + msg).setException(e));
      throw problems.newModelBuildingException();
    } 
    model.setPomFile(pomFile);
    problems.setSource(model);
    this.modelValidator.validateRawModel(model, request, problems);
    if (hasFatalErrors(problems))
      throw problems.newModelBuildingException(); 
    return model;
  }
  
  private DefaultProfileActivationContext getProfileActivationContext(ModelBuildingRequest request) {
    DefaultProfileActivationContext context = new DefaultProfileActivationContext();
    context.setActiveProfileIds(request.getActiveProfileIds());
    context.setInactiveProfileIds(request.getInactiveProfileIds());
    context.setSystemProperties(request.getSystemProperties());
    context.setUserProperties(request.getUserProperties());
    context.setProjectDirectory((request.getPomFile() != null) ? request.getPomFile().getParentFile() : null);
    return context;
  }
  
  private void configureResolver(ModelResolver modelResolver, Model model, DefaultModelProblemCollector problems) {
    configureResolver(modelResolver, model, problems, false);
  }
  
  private void configureResolver(ModelResolver modelResolver, Model model, DefaultModelProblemCollector problems, boolean replaceRepositories) {
    if (modelResolver == null)
      return; 
    problems.setSource(model);
    List<Repository> repositories = model.getRepositories();
    for (Repository repository : repositories) {
      try {
        modelResolver.addRepository(repository, replaceRepositories);
      } catch (InvalidRepositoryException e) {
        problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE))
            .setMessage("Invalid repository " + repository.getId() + ": " + e.getMessage())
            .setLocation(repository.getLocation("")).setException((Exception)e));
      } 
    } 
  }
  
  private void checkPluginVersions(List<ModelData> lineage, ModelBuildingRequest request, ModelProblemCollector problems) {
    if (request.getValidationLevel() < 20)
      return; 
    Map<String, Plugin> plugins = new HashMap<>();
    Map<String, String> versions = new HashMap<>();
    Map<String, String> managedVersions = new HashMap<>();
    for (int i = lineage.size() - 1; i >= 0; i--) {
      Model model = ((ModelData)lineage.get(i)).getModel();
      Build build = model.getBuild();
      if (build != null) {
        for (Plugin plugin : build.getPlugins()) {
          String key = plugin.getKey();
          if (versions.get(key) == null) {
            versions.put(key, plugin.getVersion());
            plugins.put(key, plugin);
          } 
        } 
        PluginManagement mgmt = build.getPluginManagement();
        if (mgmt != null)
          for (Plugin plugin : mgmt.getPlugins()) {
            String key = plugin.getKey();
            if (managedVersions.get(key) == null)
              managedVersions.put(key, plugin.getVersion()); 
          }  
      } 
    } 
    for (String key : versions.keySet()) {
      if (versions.get(key) == null && managedVersions.get(key) == null) {
        InputLocation location = ((Plugin)plugins.get(key)).getLocation("");
        problems
          .add((new ModelProblemCollectorRequest(ModelProblem.Severity.WARNING, ModelProblem.Version.V20))
            .setMessage("'build.plugins.plugin.version' for " + key + " is missing.")
            .setLocation(location));
      } 
    } 
  }
  
  private void assembleInheritance(List<ModelData> lineage, ModelBuildingRequest request, ModelProblemCollector problems) {
    for (int i = lineage.size() - 2; i >= 0; i--) {
      Model parent = ((ModelData)lineage.get(i + 1)).getModel();
      Model child = ((ModelData)lineage.get(i)).getModel();
      this.inheritanceAssembler.assembleModelInheritance(child, parent, request, problems);
    } 
  }
  
  private Map<String, Activation> getProfileActivations(Model model, boolean clone) {
    Map<String, Activation> activations = new HashMap<>();
    for (Profile profile : model.getProfiles()) {
      Activation activation = profile.getActivation();
      if (activation == null)
        continue; 
      if (clone)
        activation = activation.clone(); 
      activations.put(profile.getId(), activation);
    } 
    return activations;
  }
  
  private void injectProfileActivations(Model model, Map<String, Activation> activations) {
    for (Profile profile : model.getProfiles()) {
      Activation activation = profile.getActivation();
      if (activation == null)
        continue; 
      profile.setActivation(activations.get(profile.getId()));
    } 
  }
  
  private Model interpolateModel(Model model, ModelBuildingRequest request, ModelProblemCollector problems) {
    Map<String, Activation> originalActivations = getProfileActivations(model, true);
    Model interpolatedModel = this.modelInterpolator.interpolateModel(model, model.getProjectDirectory(), request, problems);
    if (interpolatedModel.getParent() != null) {
      StringSearchInterpolator ssi = new StringSearchInterpolator();
      ssi.addValueSource((ValueSource)new MapBasedValueSource(request.getUserProperties()));
      ssi.addValueSource((ValueSource)new MapBasedValueSource(model.getProperties()));
      ssi.addValueSource((ValueSource)new MapBasedValueSource(request.getSystemProperties()));
      try {
        String interpolated = ssi.interpolate(interpolatedModel.getParent().getVersion());
        interpolatedModel.getParent().setVersion(interpolated);
      } catch (Exception e) {
        ModelProblemCollectorRequest mpcr = (new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE)).setMessage("Failed to interpolate field: " + interpolatedModel.getParent().getVersion() + " on class: ").setException(e);
        problems.add(mpcr);
      } 
    } 
    interpolatedModel.setPomFile(model.getPomFile());
    injectProfileActivations(model, originalActivations);
    return interpolatedModel;
  }
  
  private ModelData readParent(Model childModel, ModelSource childSource, ModelBuildingRequest request, DefaultModelProblemCollector problems) throws ModelBuildingException {
    ModelData parentData;
    Parent parent = childModel.getParent();
    if (parent != null) {
      String groupId = parent.getGroupId();
      String artifactId = parent.getArtifactId();
      String version = parent.getVersion();
      parentData = getCache(request.getModelCache(), groupId, artifactId, version, ModelCacheTag.RAW);
      if (parentData == null) {
        parentData = readParentLocally(childModel, childSource, request, problems);
        if (parentData == null)
          parentData = readParentExternally(childModel, request, problems); 
        putCache(request.getModelCache(), groupId, artifactId, version, ModelCacheTag.RAW, parentData);
      } else {
        File pomFile = parentData.getModel().getPomFile();
        if (pomFile != null) {
          FileModelSource pomSource = new FileModelSource(pomFile);
          ModelSource expectedParentSource = getParentPomFile(childModel, childSource);
          if (expectedParentSource == null || (expectedParentSource instanceof ModelSource2 && 
            !pomSource.equals(expectedParentSource)))
            parentData = readParentExternally(childModel, request, problems); 
        } 
      } 
      Model parentModel = parentData.getModel();
      if (!"pom".equals(parentModel.getPackaging()))
        problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE))
            .setMessage("Invalid packaging for parent POM " + ModelProblemUtils.toSourceHint(parentModel) + ", must be \"pom\" but is \"" + parentModel
              .getPackaging() + "\"")
            .setLocation(parentModel.getLocation("packaging"))); 
    } else {
      parentData = null;
    } 
    return parentData;
  }
  
  private ModelData readParentLocally(Model childModel, ModelSource childSource, ModelBuildingRequest request, DefaultModelProblemCollector problems) throws ModelBuildingException {
    ModelSource candidateSource;
    Model candidateModel;
    Parent parent = childModel.getParent();
    WorkspaceModelResolver resolver = request.getWorkspaceModelResolver();
    if (resolver == null) {
      candidateSource = getParentPomFile(childModel, childSource);
      if (candidateSource == null)
        return null; 
      File pomFile = null;
      if (candidateSource instanceof FileModelSource)
        pomFile = ((FileModelSource)candidateSource).getPomFile(); 
      candidateModel = readModel(candidateSource, pomFile, request, problems);
    } else {
      try {
        candidateModel = resolver.resolveRawModel(parent.getGroupId(), parent.getArtifactId(), parent.getVersion());
      } catch (UnresolvableModelException e) {
        problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.FATAL, ModelProblem.Version.BASE))
            .setMessage(e.getMessage().toString()).setLocation(parent.getLocation("")).setException((Exception)e));
        throw problems.newModelBuildingException();
      } 
      if (candidateModel == null)
        return null; 
      candidateSource = new FileModelSource(candidateModel.getPomFile());
    } 
    String groupId = candidateModel.getGroupId();
    if (groupId == null && candidateModel.getParent() != null)
      groupId = candidateModel.getParent().getGroupId(); 
    String artifactId = candidateModel.getArtifactId();
    String version = candidateModel.getVersion();
    if (version == null && candidateModel.getParent() != null)
      version = candidateModel.getParent().getVersion(); 
    if (groupId == null || !groupId.equals(parent.getGroupId()) || artifactId == null || 
      !artifactId.equals(parent.getArtifactId())) {
      StringBuilder buffer = new StringBuilder(256);
      buffer.append("'parent.relativePath'");
      if (childModel != problems.getRootModel())
        buffer.append(" of POM ").append(ModelProblemUtils.toSourceHint(childModel)); 
      buffer.append(" points at ").append(groupId).append(':').append(artifactId);
      buffer.append(" instead of ").append(parent.getGroupId()).append(':');
      buffer.append(parent.getArtifactId()).append(", please verify your project structure");
      problems.setSource(childModel);
      problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.WARNING, ModelProblem.Version.BASE))
          .setMessage(buffer.toString()).setLocation(parent.getLocation("")));
      return null;
    } 
    if (version != null && parent.getVersion() != null && !version.equals(parent.getVersion()))
      try {
        VersionRange parentRange = VersionRange.createFromVersionSpec(parent.getVersion());
        if (!parentRange.hasRestrictions())
          return null; 
        if (!parentRange.containsVersion((ArtifactVersion)new DefaultArtifactVersion(version)))
          return null; 
        String rawChildModelVersion = childModel.getVersion();
        if (rawChildModelVersion == null) {
          problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.FATAL, ModelProblem.Version.V31))
              .setMessage("Version must be a constant").setLocation(childModel.getLocation("")));
        } else if (rawChildVersionReferencesParent(rawChildModelVersion)) {
          problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.FATAL, ModelProblem.Version.V31))
              .setMessage("Version must be a constant")
              .setLocation(childModel.getLocation("version")));
        } 
      } catch (InvalidVersionSpecificationException e) {
        return null;
      }  
    ModelData parentData = new ModelData(candidateSource, candidateModel, groupId, artifactId, version);
    return parentData;
  }
  
  private boolean rawChildVersionReferencesParent(String rawChildModelVersion) {
    return (rawChildModelVersion.equals("${pom.version}") || rawChildModelVersion
      .equals("${project.version}") || rawChildModelVersion
      .equals("${pom.parent.version}") || rawChildModelVersion
      .equals("${project.parent.version}"));
  }
  
  private ModelSource getParentPomFile(Model childModel, ModelSource source) {
    if (!(source instanceof ModelSource2))
      return null; 
    String parentPath = childModel.getParent().getRelativePath();
    if (parentPath == null || parentPath.length() <= 0)
      return null; 
    return ((ModelSource2)source).getRelatedSource(parentPath);
  }
  
  private ModelData readParentExternally(Model childModel, ModelBuildingRequest request, DefaultModelProblemCollector problems) throws ModelBuildingException {
    ModelSource modelSource;
    problems.setSource(childModel);
    Parent parent = childModel.getParent().clone();
    String groupId = parent.getGroupId();
    String artifactId = parent.getArtifactId();
    String version = parent.getVersion();
    ModelResolver modelResolver = request.getModelResolver();
    Objects.requireNonNull(modelResolver, 
        String.format("request.modelResolver cannot be null (parent POM %s and POM %s)", new Object[] { ModelProblemUtils.toId(groupId, artifactId, version), 
            ModelProblemUtils.toSourceHint(childModel) }));
    try {
      modelSource = modelResolver.resolveModel(parent);
    } catch (UnresolvableModelException e) {
      StringBuilder buffer = new StringBuilder(256);
      buffer.append("Non-resolvable parent POM");
      if (!containsCoordinates(e.getMessage(), groupId, artifactId, version))
        buffer.append(' ').append(ModelProblemUtils.toId(groupId, artifactId, version)); 
      if (childModel != problems.getRootModel())
        buffer.append(" for ").append(ModelProblemUtils.toId(childModel)); 
      buffer.append(": ").append(e.getMessage());
      if (childModel.getProjectDirectory() != null)
        if (parent.getRelativePath() == null || parent.getRelativePath().length() <= 0) {
          buffer.append(" and 'parent.relativePath' points at no local POM");
        } else {
          buffer.append(" and 'parent.relativePath' points at wrong local POM");
        }  
      problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.FATAL, ModelProblem.Version.BASE))
          .setMessage(buffer.toString()).setLocation(parent.getLocation("")).setException((Exception)e));
      throw problems.newModelBuildingException();
    } 
    ModelBuildingRequest lenientRequest = request;
    if (request.getValidationLevel() > 20)
      lenientRequest = new FilterModelBuildingRequest(request) {
          public int getValidationLevel() {
            return 20;
          }
        }; 
    Model parentModel = readModel(modelSource, null, lenientRequest, problems);
    if (!parent.getVersion().equals(version)) {
      String rawChildModelVersion = childModel.getVersion();
      if (rawChildModelVersion == null) {
        problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.FATAL, ModelProblem.Version.V31))
            .setMessage("Version must be a constant").setLocation(childModel.getLocation("")));
      } else if (rawChildVersionReferencesParent(rawChildModelVersion)) {
        problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.FATAL, ModelProblem.Version.V31))
            .setMessage("Version must be a constant")
            .setLocation(childModel.getLocation("version")));
      } 
    } 
    ModelData parentData = new ModelData(modelSource, parentModel, parent.getGroupId(), parent.getArtifactId(), parent.getVersion());
    return parentData;
  }
  
  private Model getSuperModel() {
    return this.superPomProvider.getSuperModel("4.0.0").clone();
  }
  
  private void importDependencyManagement(Model model, ModelBuildingRequest request, DefaultModelProblemCollector problems, Collection<String> importIds) {
    DependencyManagement depMgmt = model.getDependencyManagement();
    if (depMgmt == null)
      return; 
    String importing = model.getGroupId() + ':' + model.getArtifactId() + ':' + model.getVersion();
    importIds.add(importing);
    WorkspaceModelResolver workspaceResolver = request.getWorkspaceModelResolver();
    ModelResolver modelResolver = request.getModelResolver();
    ModelBuildingRequest importRequest = null;
    List<DependencyManagement> importMgmts = null;
    for (Iterator<Dependency> it = depMgmt.getDependencies().iterator(); it.hasNext(); ) {
      Dependency dependency = it.next();
      if (!"pom".equals(dependency.getType()) || !"import".equals(dependency.getScope()))
        continue; 
      it.remove();
      String groupId = dependency.getGroupId();
      String artifactId = dependency.getArtifactId();
      String version = dependency.getVersion();
      if (groupId == null || groupId.length() <= 0) {
        problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE))
            .setMessage("'dependencyManagement.dependencies.dependency.groupId' for " + dependency
              .getManagementKey() + " is missing.")
            .setLocation(dependency.getLocation("")));
        continue;
      } 
      if (artifactId == null || artifactId.length() <= 0) {
        problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE))
            .setMessage("'dependencyManagement.dependencies.dependency.artifactId' for " + dependency
              .getManagementKey() + " is missing.")
            .setLocation(dependency.getLocation("")));
        continue;
      } 
      if (version == null || version.length() <= 0) {
        problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE))
            .setMessage("'dependencyManagement.dependencies.dependency.version' for " + dependency
              .getManagementKey() + " is missing.")
            .setLocation(dependency.getLocation("")));
        continue;
      } 
      String imported = groupId + ':' + artifactId + ':' + version;
      if (importIds.contains(imported)) {
        String message = "The dependencies of type=pom and with scope=import form a cycle: ";
        for (String modelId : importIds)
          message = message + modelId + " -> "; 
        message = message + imported;
        problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE)).setMessage(message));
        continue;
      } 
      DependencyManagement importMgmt = getCache(request.getModelCache(), groupId, artifactId, version, ModelCacheTag.IMPORT);
      if (importMgmt == null) {
        if (workspaceResolver == null && modelResolver == null)
          throw new NullPointerException(String.format("request.workspaceModelResolver and request.modelResolver cannot be null (parent POM %s and POM %s)", new Object[] { ModelProblemUtils.toId(groupId, artifactId, version), 
                  ModelProblemUtils.toSourceHint(model) })); 
        Model importModel = null;
        if (workspaceResolver != null)
          try {
            importModel = workspaceResolver.resolveEffectiveModel(groupId, artifactId, version);
          } catch (UnresolvableModelException e) {
            problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.FATAL, ModelProblem.Version.BASE))
                .setMessage(e.getMessage().toString()).setException((Exception)e));
            continue;
          }  
        if (importModel == null) {
          ModelSource importSource;
          ModelBuildingResult importResult;
          try {
            importSource = modelResolver.resolveModel(groupId, artifactId, version);
          } catch (UnresolvableModelException e) {
            StringBuilder buffer = new StringBuilder(256);
            buffer.append("Non-resolvable import POM");
            if (!containsCoordinates(e.getMessage(), groupId, artifactId, version))
              buffer.append(' ').append(ModelProblemUtils.toId(groupId, artifactId, version)); 
            buffer.append(": ").append(e.getMessage());
            problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE))
                .setMessage(buffer.toString()).setLocation(dependency.getLocation(""))
                .setException((Exception)e));
            continue;
          } 
          if (importRequest == null) {
            importRequest = new DefaultModelBuildingRequest();
            importRequest.setValidationLevel(0);
            importRequest.setModelCache(request.getModelCache());
            importRequest.setSystemProperties(request.getSystemProperties());
            importRequest.setUserProperties(request.getUserProperties());
            importRequest.setLocationTracking(request.isLocationTracking());
          } 
          importRequest.setModelSource(importSource);
          importRequest.setModelResolver(modelResolver.newCopy());
          try {
            importResult = build(importRequest, importIds);
          } catch (ModelBuildingException e) {
            problems.addAll(e.getProblems());
            continue;
          } 
          problems.addAll(importResult.getProblems());
          importModel = importResult.getEffectiveModel();
        } 
        importMgmt = importModel.getDependencyManagement();
        if (importMgmt == null)
          importMgmt = new DependencyManagement(); 
        putCache(request.getModelCache(), groupId, artifactId, version, ModelCacheTag.IMPORT, importMgmt);
      } 
      if (importMgmts == null)
        importMgmts = new ArrayList<>(); 
      importMgmts.add(importMgmt);
    } 
    importIds.remove(importing);
    this.dependencyManagementImporter.importManagement(model, importMgmts, request, problems);
  }
  
  private <T> void putCache(ModelCache modelCache, String groupId, String artifactId, String version, ModelCacheTag<T> tag, T data) {
    if (modelCache != null)
      modelCache.put(groupId, artifactId, version, tag.getName(), tag.intoCache(data)); 
  }
  
  private <T> T getCache(ModelCache modelCache, String groupId, String artifactId, String version, ModelCacheTag<T> tag) {
    if (modelCache != null) {
      Object data = modelCache.get(groupId, artifactId, version, tag.getName());
      if (data != null)
        return tag.fromCache(tag.getType().cast(data)); 
    } 
    return null;
  }
  
  private void fireEvent(Model model, ModelBuildingRequest request, ModelProblemCollector problems, ModelBuildingEventCatapult catapult) throws ModelBuildingException {
    ModelBuildingListener listener = request.getModelBuildingListener();
    if (listener != null) {
      ModelBuildingEvent event = new DefaultModelBuildingEvent(model, request, problems);
      catapult.fire(listener, event);
    } 
  }
  
  private boolean containsCoordinates(String message, String groupId, String artifactId, String version) {
    return (message != null && (groupId == null || message.contains(groupId)) && (artifactId == null || message
      .contains(artifactId)) && (version == null || message
      .contains(version)));
  }
  
  protected boolean hasModelErrors(ModelProblemCollectorExt problems) {
    if (problems instanceof DefaultModelProblemCollector)
      return ((DefaultModelProblemCollector)problems).hasErrors(); 
    throw new IllegalStateException();
  }
  
  protected boolean hasFatalErrors(ModelProblemCollectorExt problems) {
    if (problems instanceof DefaultModelProblemCollector)
      return ((DefaultModelProblemCollector)problems).hasFatalErrors(); 
    throw new IllegalStateException();
  }
}
