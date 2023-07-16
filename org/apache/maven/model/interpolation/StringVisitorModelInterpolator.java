package org.apache.maven.model.interpolation;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.Contributor;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Developer;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Extension;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.License;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Model;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.Notifier;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
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
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.building.ModelProblemCollectorRequest;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.InterpolationPostProcessor;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.codehaus.plexus.interpolation.ValueSource;
import org.codehaus.plexus.util.xml.Xpp3Dom;

@Named
@Singleton
public class StringVisitorModelInterpolator extends AbstractStringBasedModelInterpolator {
  public Model interpolateModel(Model model, File projectDir, ModelBuildingRequest config, ModelProblemCollector problems) {
    List<? extends ValueSource> valueSources = createValueSources(model, projectDir, config, problems);
    List<? extends InterpolationPostProcessor> postProcessors = createPostProcessors(model, projectDir, config);
    InnerInterpolator innerInterpolator = createInterpolator(valueSources, postProcessors, problems);
    (new ModelVisitor(innerInterpolator)).visit(model);
    return model;
  }
  
  private InnerInterpolator createInterpolator(List<? extends ValueSource> valueSources, List<? extends InterpolationPostProcessor> postProcessors, final ModelProblemCollector problems) {
    final Map<String, String> cache = new HashMap<>();
    final StringSearchInterpolator interpolator = new StringSearchInterpolator();
    interpolator.setCacheAnswers(true);
    for (ValueSource vs : valueSources)
      interpolator.addValueSource(vs); 
    for (InterpolationPostProcessor postProcessor : postProcessors)
      interpolator.addPostProcessor(postProcessor); 
    final RecursionInterceptor recursionInterceptor = createRecursionInterceptor();
    return new InnerInterpolator() {
        public String interpolate(String value) {
          if (value != null && value.contains("${")) {
            String c = (String)cache.get(value);
            if (c == null) {
              try {
                c = interpolator.interpolate(value, recursionInterceptor);
              } catch (InterpolationException e) {
                problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE))
                    .setMessage(e.getMessage()).setException((Exception)e));
              } 
              cache.put(value, c);
            } 
            return c;
          } 
          return value;
        }
      };
  }
  
  static interface InnerInterpolator {
    String interpolate(String param1String);
  }
  
  private static final class ModelVisitor {
    private final StringVisitorModelInterpolator.InnerInterpolator interpolator;
    
    ModelVisitor(StringVisitorModelInterpolator.InnerInterpolator interpolator) {
      this.interpolator = interpolator;
    }
    
    void visit(Model model) {
      if (model != null) {
        visit((ModelBase)model);
        String orgModelVersion = model.getModelVersion();
        String intModelVersion = interpolate(orgModelVersion);
        if (orgModelVersion != intModelVersion)
          model.setModelVersion(intModelVersion); 
        visit(model.getParent());
        String orgGroupId = model.getGroupId();
        String intGroupId = interpolate(orgGroupId);
        if (orgGroupId != intGroupId)
          model.setGroupId(intGroupId); 
        String orgArtifactId = model.getArtifactId();
        String intArtifactId = interpolate(orgArtifactId);
        if (orgArtifactId != intArtifactId)
          model.setArtifactId(intArtifactId); 
        String orgVersion = model.getVersion();
        String intVersion = interpolate(orgVersion);
        if (orgVersion != intVersion)
          model.setVersion(intVersion); 
        String orgPackaging = model.getPackaging();
        String intPackaging = interpolate(orgPackaging);
        if (orgPackaging != intPackaging)
          model.setPackaging(intPackaging); 
        String orgName = model.getName();
        String intName = interpolate(orgName);
        if (orgName != intName)
          model.setName(intName); 
        String orgDescription = model.getDescription();
        String intDescription = interpolate(orgDescription);
        if (orgDescription != intDescription)
          model.setDescription(intDescription); 
        String orgUrl = model.getUrl();
        String intUrl = interpolate(orgUrl);
        if (orgUrl != intUrl)
          model.setUrl(intUrl); 
        String orgChildProjectUrlInheritAppendPath = model.getChildProjectUrlInheritAppendPath();
        String intChildProjectUrlInheritAppendPath = interpolate(orgChildProjectUrlInheritAppendPath);
        if (orgChildProjectUrlInheritAppendPath != intChildProjectUrlInheritAppendPath)
          model.setChildProjectUrlInheritAppendPath(intChildProjectUrlInheritAppendPath); 
        String orgInceptionYear = model.getInceptionYear();
        String intInceptionYear = interpolate(orgInceptionYear);
        if (orgInceptionYear != intInceptionYear)
          model.setInceptionYear(intInceptionYear); 
        visit(model.getOrganization());
        for (License license : model.getLicenses())
          visit(license); 
        for (Developer developer : model.getDevelopers())
          visit(developer); 
        for (Contributor contributor : model.getContributors())
          visit(contributor); 
        for (MailingList mailingList : model.getMailingLists())
          visit(mailingList); 
        visit(model.getPrerequisites());
        visit(model.getScm());
        visit(model.getIssueManagement());
        visit(model.getCiManagement());
        visit(model.getBuild());
        for (Profile profile : model.getProfiles())
          visit(profile); 
      } 
    }
    
    private void visit(Parent parent) {
      if (parent != null) {
        String org = parent.getGroupId();
        String val = interpolate(org);
        if (org != val)
          parent.setGroupId(val); 
        org = parent.getArtifactId();
        val = interpolate(org);
        if (org != val)
          parent.setArtifactId(val); 
        org = parent.getVersion();
        val = interpolate(org);
        if (org != val)
          parent.setVersion(val); 
        org = parent.getRelativePath();
        val = interpolate(org);
        if (org != val)
          parent.setRelativePath(val); 
      } 
    }
    
    private void visit(Organization organization) {
      if (organization != null) {
        String org = organization.getName();
        String val = interpolate(org);
        if (org != val)
          organization.setName(val); 
        org = organization.getUrl();
        val = interpolate(org);
        if (org != val)
          organization.setUrl(val); 
      } 
    }
    
    private void visit(License license) {
      if (license != null) {
        String org = license.getName();
        String val = interpolate(org);
        if (org != val)
          license.setName(val); 
        org = license.getUrl();
        val = interpolate(org);
        if (org != val)
          license.setUrl(val); 
        org = license.getDistribution();
        val = interpolate(org);
        if (org != val)
          license.setDistribution(val); 
        org = license.getComments();
        val = interpolate(org);
        if (org != val)
          license.setComments(val); 
      } 
    }
    
    private void visit(Developer developer) {
      if (developer != null) {
        visit((Contributor)developer);
        String org = developer.getId();
        String val = interpolate(org);
        if (org != val)
          developer.setId(val); 
      } 
    }
    
    private void visit(Contributor contributor) {
      if (contributor != null) {
        String org = contributor.getName();
        String val = interpolate(org);
        if (org != val)
          contributor.setName(val); 
        org = contributor.getEmail();
        val = interpolate(org);
        if (org != val)
          contributor.setEmail(val); 
        org = contributor.getUrl();
        val = interpolate(org);
        if (org != val)
          contributor.setUrl(val); 
        org = contributor.getOrganization();
        val = interpolate(org);
        if (org != val)
          contributor.setOrganization(val); 
        org = contributor.getOrganizationUrl();
        val = interpolate(org);
        if (org != val)
          contributor.setOrganizationUrl(val); 
        visit(contributor.getRoles());
      } 
    }
    
    private void visit(MailingList mailingList) {
      if (mailingList != null) {
        String org = mailingList.getName();
        String val = interpolate(org);
        if (org != val)
          mailingList.setName(val); 
        org = mailingList.getSubscribe();
        val = interpolate(org);
        if (org != val)
          mailingList.setSubscribe(val); 
        org = mailingList.getUnsubscribe();
        val = interpolate(org);
        if (org != val)
          mailingList.setUnsubscribe(val); 
        org = mailingList.getPost();
        val = interpolate(org);
        if (org != val)
          mailingList.setPost(val); 
        org = mailingList.getArchive();
        val = interpolate(org);
        if (org != val)
          mailingList.setArchive(val); 
      } 
    }
    
    private void visit(Prerequisites prerequisites) {
      if (prerequisites != null) {
        String org = prerequisites.getMaven();
        String val = interpolate(org);
        if (org != val)
          prerequisites.setMaven(val); 
      } 
    }
    
    private void visit(Scm scm) {
      if (scm != null) {
        String org = scm.getConnection();
        String val = interpolate(org);
        if (org != val)
          scm.setConnection(val); 
        org = scm.getDeveloperConnection();
        val = interpolate(org);
        if (org != val)
          scm.setDeveloperConnection(val); 
        org = scm.getTag();
        val = interpolate(org);
        if (org != val)
          scm.setTag(val); 
        org = scm.getUrl();
        val = interpolate(org);
        if (org != val)
          scm.setUrl(val); 
        org = scm.getChildScmConnectionInheritAppendPath();
        val = interpolate(org);
        if (org != val)
          scm.setChildScmConnectionInheritAppendPath(val); 
        org = scm.getChildScmDeveloperConnectionInheritAppendPath();
        val = interpolate(org);
        if (org != val)
          scm.setChildScmDeveloperConnectionInheritAppendPath(val); 
        org = scm.getChildScmUrlInheritAppendPath();
        val = interpolate(org);
        if (org != val)
          scm.setChildScmUrlInheritAppendPath(val); 
      } 
    }
    
    private void visit(IssueManagement issueManagement) {
      if (issueManagement != null) {
        String org = issueManagement.getSystem();
        String val = interpolate(org);
        if (org != val)
          issueManagement.setSystem(val); 
        org = issueManagement.getUrl();
        val = interpolate(org);
        if (org != val)
          issueManagement.setUrl(val); 
      } 
    }
    
    private void visit(CiManagement ciManagement) {
      if (ciManagement != null) {
        String org = ciManagement.getSystem();
        String val = interpolate(org);
        if (org != val)
          ciManagement.setSystem(val); 
        org = ciManagement.getUrl();
        val = interpolate(org);
        if (org != val)
          ciManagement.setUrl(val); 
        for (Notifier notifier : ciManagement.getNotifiers())
          visit(notifier); 
      } 
    }
    
    private void visit(Notifier notifier) {
      if (notifier != null) {
        String org = notifier.getType();
        String val = interpolate(org);
        if (org != val)
          notifier.setType(val); 
        visit(notifier.getConfiguration());
      } 
    }
    
    private void visit(BuildBase build) {
      if (build != null) {
        for (Plugin plugin : build.getPlugins())
          visit(plugin); 
        visit(build.getPluginManagement());
        String org = build.getDefaultGoal();
        String val = interpolate(org);
        if (org != val)
          build.setDefaultGoal(val); 
        for (Resource resource : build.getResources())
          visit(resource); 
        for (Resource resource : build.getTestResources())
          visit(resource); 
        org = build.getDirectory();
        val = interpolate(org);
        if (org != val)
          build.setDirectory(val); 
        org = build.getFinalName();
        val = interpolate(org);
        if (org != val)
          build.setFinalName(val); 
        visit(build.getFilters());
      } 
    }
    
    private void visit(PluginManagement pluginManagement) {
      if (pluginManagement != null)
        for (Plugin plugin : pluginManagement.getPlugins())
          visit(plugin);  
    }
    
    private void visit(Build build) {
      if (build != null) {
        visit((BuildBase)build);
        String org = build.getSourceDirectory();
        String val = interpolate(org);
        if (org != val)
          build.setSourceDirectory(val); 
        org = build.getScriptSourceDirectory();
        val = interpolate(org);
        if (org != val)
          build.setScriptSourceDirectory(val); 
        org = build.getTestSourceDirectory();
        val = interpolate(org);
        if (org != val)
          build.setTestSourceDirectory(val); 
        org = build.getOutputDirectory();
        val = interpolate(org);
        if (org != val)
          build.setOutputDirectory(val); 
        org = build.getTestOutputDirectory();
        val = interpolate(org);
        if (org != val)
          build.setTestOutputDirectory(val); 
        for (Extension extension : build.getExtensions())
          visit(extension); 
      } 
    }
    
    private void visit(Resource resource) {
      if (resource != null) {
        visit(resource.getIncludes());
        visit(resource.getExcludes());
        String org = resource.getDirectory();
        String val = interpolate(org);
        if (org != val)
          resource.setDirectory(val); 
        org = resource.getTargetPath();
        val = interpolate(org);
        if (org != val)
          resource.setTargetPath(val); 
        org = resource.getFiltering();
        val = interpolate(org);
        if (org != val)
          resource.setFiltering(val); 
      } 
    }
    
    private void visit(Plugin plugin) {
      if (plugin != null) {
        String org = plugin.getInherited();
        String val = interpolate(org);
        if (org != val)
          plugin.setInherited(val); 
        visit((Xpp3Dom)plugin.getConfiguration());
        org = plugin.getGroupId();
        val = interpolate(org);
        if (org != val)
          plugin.setGroupId(val); 
        org = plugin.getArtifactId();
        val = interpolate(org);
        if (org != val)
          plugin.setArtifactId(val); 
        org = plugin.getVersion();
        val = interpolate(org);
        if (org != val)
          plugin.setVersion(val); 
        org = plugin.getExtensions();
        val = interpolate(org);
        if (org != val)
          plugin.setExtensions(val); 
        for (PluginExecution execution : plugin.getExecutions())
          visit(execution); 
        for (Dependency dependency : plugin.getDependencies())
          visit(dependency); 
      } 
    }
    
    private void visit(PluginExecution execution) {
      if (execution != null) {
        String org = execution.getInherited();
        String val = interpolate(org);
        if (org != val)
          execution.setInherited(val); 
        visit((Xpp3Dom)execution.getConfiguration());
        org = execution.getId();
        val = interpolate(org);
        if (org != val)
          execution.setId(val); 
        org = execution.getPhase();
        val = interpolate(org);
        if (org != val)
          execution.setPhase(val); 
        visit(execution.getGoals());
      } 
    }
    
    private void visit(Xpp3Dom dom) {
      if (dom != null) {
        String org = dom.getValue();
        String val = interpolate(org);
        if (org != val)
          dom.setValue(val); 
        for (String attr : dom.getAttributeNames()) {
          org = dom.getAttribute(attr);
          val = interpolate(org);
          if (org != val)
            dom.setAttribute(attr, val); 
        } 
        for (int i = 0, l = dom.getChildCount(); i < l; i++)
          visit(dom.getChild(i)); 
      } 
    }
    
    private void visit(Extension extension) {
      if (extension != null) {
        String org = extension.getGroupId();
        String val = interpolate(org);
        if (org != val)
          extension.setGroupId(val); 
        org = extension.getArtifactId();
        val = interpolate(org);
        if (org != val)
          extension.setArtifactId(val); 
        org = extension.getVersion();
        val = interpolate(org);
        if (org != val)
          extension.setVersion(val); 
      } 
    }
    
    private void visit(Profile profile) {
      if (profile != null) {
        visit((ModelBase)profile);
        String org = profile.getId();
        String val = interpolate(org);
        if (org != val)
          profile.setId(val); 
        visit(profile.getActivation());
        visit(profile.getBuild());
      } 
    }
    
    private void visit(Activation activation) {
      if (activation != null) {
        String org = activation.getJdk();
        String val = interpolate(org);
        if (org != val)
          activation.setJdk(val); 
        visit(activation.getOs());
        visit(activation.getProperty());
        visit(activation.getFile());
      } 
    }
    
    private void visit(ActivationOS activationOS) {
      if (activationOS != null) {
        String org = activationOS.getName();
        String val = interpolate(org);
        if (org != val)
          activationOS.setName(val); 
        org = activationOS.getFamily();
        val = interpolate(org);
        if (org != val)
          activationOS.setFamily(val); 
        org = activationOS.getArch();
        val = interpolate(org);
        if (org != val)
          activationOS.setArch(val); 
        org = activationOS.getVersion();
        val = interpolate(org);
        if (org != val)
          activationOS.setVersion(val); 
      } 
    }
    
    private void visit(ActivationProperty activationProperty) {
      if (activationProperty != null) {
        String org = activationProperty.getName();
        String val = interpolate(org);
        if (org != val)
          activationProperty.setName(val); 
        org = activationProperty.getValue();
        val = interpolate(org);
        if (org != val)
          activationProperty.setValue(val); 
      } 
    }
    
    private void visit(ActivationFile activationFile) {
      if (activationFile != null) {
        String org = activationFile.getMissing();
        String val = interpolate(org);
        if (org != val)
          activationFile.setMissing(val); 
        org = activationFile.getExists();
        val = interpolate(org);
        if (org != val)
          activationFile.setExists(val); 
      } 
    }
    
    private void visit(ModelBase modelBase) {
      if (modelBase != null) {
        visit(modelBase.getModules());
        visit(modelBase.getDistributionManagement());
        visit(modelBase.getProperties());
        visit(modelBase.getDependencyManagement());
        for (Dependency dependency : modelBase.getDependencies())
          visit(dependency); 
        for (Repository repository : modelBase.getRepositories())
          visit(repository); 
        for (Repository repository : modelBase.getPluginRepositories())
          visit(repository); 
        visit(modelBase.getReporting());
      } 
    }
    
    private void visit(DistributionManagement distributionManagement) {
      if (distributionManagement != null) {
        visit((Repository)distributionManagement.getRepository());
        visit((Repository)distributionManagement.getSnapshotRepository());
        visit(distributionManagement.getSite());
        String org = distributionManagement.getDownloadUrl();
        String val = interpolate(org);
        if (org != val)
          distributionManagement.setDownloadUrl(val); 
        visit(distributionManagement.getRelocation());
      } 
    }
    
    private void visit(Site site) {
      if (site != null) {
        String org = site.getId();
        String val = interpolate(org);
        if (org != val)
          site.setId(val); 
        org = site.getName();
        val = interpolate(org);
        if (org != val)
          site.setName(val); 
        org = site.getUrl();
        val = interpolate(org);
        if (org != val)
          site.setUrl(val); 
        org = site.getChildSiteUrlInheritAppendPath();
        val = interpolate(org);
        if (org != val)
          site.setChildSiteUrlInheritAppendPath(val); 
      } 
    }
    
    private void visit(Relocation relocation) {
      if (relocation != null) {
        String org = relocation.getGroupId();
        String val = interpolate(org);
        if (org != val)
          relocation.setGroupId(val); 
        org = relocation.getArtifactId();
        val = interpolate(org);
        if (org != val)
          relocation.setArtifactId(val); 
        org = relocation.getVersion();
        val = interpolate(org);
        if (org != val)
          relocation.setVersion(val); 
        org = relocation.getMessage();
        val = interpolate(org);
        if (org != val)
          relocation.setMessage(val); 
      } 
    }
    
    private void visit(DependencyManagement dependencyManagement) {
      if (dependencyManagement != null)
        for (Dependency dependency : dependencyManagement.getDependencies())
          visit(dependency);  
    }
    
    private void visit(Repository repository) {
      if (repository != null) {
        visit((RepositoryBase)repository);
        visit(repository.getReleases());
        visit(repository.getSnapshots());
      } 
    }
    
    private void visit(RepositoryBase repositoryBase) {
      if (repositoryBase != null) {
        String orgId = repositoryBase.getId();
        String intId = interpolate(orgId);
        if (orgId != intId)
          repositoryBase.setId(intId); 
        String orgName = repositoryBase.getName();
        String intName = interpolate(orgName);
        if (orgName != intName)
          repositoryBase.setName(intName); 
        String orgUrl = repositoryBase.getUrl();
        String intUrl = interpolate(orgUrl);
        if (orgUrl != intUrl)
          repositoryBase.setUrl(intUrl); 
        String orgLayout = repositoryBase.getLayout();
        String intLayout = interpolate(orgLayout);
        if (orgLayout != intLayout)
          repositoryBase.setLayout(intLayout); 
      } 
    }
    
    private void visit(RepositoryPolicy repositoryPolicy) {
      if (repositoryPolicy != null) {
        String orgEnabled = repositoryPolicy.getEnabled();
        String intEnabled = interpolate(orgEnabled);
        if (orgEnabled != intEnabled)
          repositoryPolicy.setEnabled(intEnabled); 
        String orgUpdatePolicy = repositoryPolicy.getUpdatePolicy();
        String intUpdatePolicy = interpolate(orgUpdatePolicy);
        if (orgUpdatePolicy != intUpdatePolicy)
          repositoryPolicy.setUpdatePolicy(intUpdatePolicy); 
        String orgChecksumPolicy = repositoryPolicy.getChecksumPolicy();
        String intChecksumPolicy = interpolate(orgChecksumPolicy);
        if (orgChecksumPolicy != intChecksumPolicy)
          repositoryPolicy.setChecksumPolicy(intChecksumPolicy); 
      } 
    }
    
    private void visit(Dependency dependency) {
      if (dependency != null) {
        String org = dependency.getGroupId();
        String val = interpolate(org);
        if (org != val) {
          dependency.setGroupId(val);
          dependency.clearManagementKey();
        } 
        org = dependency.getArtifactId();
        val = interpolate(org);
        if (org != val) {
          dependency.setArtifactId(val);
          dependency.clearManagementKey();
        } 
        org = dependency.getVersion();
        val = interpolate(org);
        if (org != val)
          dependency.setVersion(val); 
        org = dependency.getType();
        val = interpolate(org);
        if (org != val) {
          dependency.setType(val);
          dependency.clearManagementKey();
        } 
        org = dependency.getClassifier();
        val = interpolate(org);
        if (org != val) {
          dependency.setClassifier(val);
          dependency.clearManagementKey();
        } 
        org = dependency.getScope();
        val = interpolate(org);
        if (org != val)
          dependency.setScope(val); 
        org = dependency.getSystemPath();
        val = interpolate(org);
        if (org != val)
          dependency.setSystemPath(val); 
        for (Exclusion exclusion : dependency.getExclusions())
          visit(exclusion); 
        org = dependency.getOptional();
        val = interpolate(org);
        if (org != val)
          dependency.setOptional(val); 
      } 
    }
    
    private void visit(Exclusion exclusion) {
      if (exclusion != null) {
        String org = exclusion.getGroupId();
        String val = interpolate(org);
        if (org != val)
          exclusion.setGroupId(val); 
        org = exclusion.getArtifactId();
        val = interpolate(org);
        if (org != val)
          exclusion.setArtifactId(val); 
      } 
    }
    
    private void visit(Reporting reporting) {
      if (reporting != null) {
        String org = reporting.getExcludeDefaults();
        String val = interpolate(org);
        if (org != val)
          reporting.setExcludeDefaults(val); 
        org = reporting.getOutputDirectory();
        val = interpolate(org);
        if (org != val)
          reporting.setOutputDirectory(val); 
        for (ReportPlugin plugin : reporting.getPlugins())
          visit(plugin); 
      } 
    }
    
    private void visit(ReportPlugin plugin) {
      if (plugin != null) {
        String org = plugin.getInherited();
        String val = interpolate(org);
        if (org != val)
          plugin.setInherited(val); 
        visit((Xpp3Dom)plugin.getConfiguration());
        org = plugin.getGroupId();
        val = interpolate(org);
        if (org != val)
          plugin.setGroupId(val); 
        org = plugin.getArtifactId();
        val = interpolate(org);
        if (org != val)
          plugin.setArtifactId(val); 
        org = plugin.getVersion();
        val = interpolate(org);
        if (org != val)
          plugin.setVersion(val); 
        for (ReportSet reportSet : plugin.getReportSets())
          visit(reportSet); 
      } 
    }
    
    private void visit(ReportSet reportSet) {
      if (reportSet != null) {
        String org = reportSet.getInherited();
        String val = interpolate(org);
        if (org != val)
          reportSet.setInherited(val); 
        visit((Xpp3Dom)reportSet.getConfiguration());
        org = reportSet.getId();
        val = interpolate(org);
        if (org != val)
          reportSet.setId(val); 
        visit(reportSet.getReports());
      } 
    }
    
    private void visit(Properties properties) {
      if (properties != null)
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
          Object v = entry.getValue();
          if (v instanceof String) {
            String value = (String)v;
            String inter = interpolate(value);
            if (value != inter && inter != null)
              entry.setValue(inter); 
          } 
        }  
    }
    
    private void visit(List<String> list) {
      if (list != null) {
        ListIterator<String> it = list.listIterator();
        while (it.hasNext()) {
          String value = it.next();
          String inter = interpolate(value);
          if (value != inter)
            it.set(inter); 
        } 
      } 
    }
    
    private String interpolate(String value) {
      return this.interpolator.interpolate(value);
    }
  }
}
