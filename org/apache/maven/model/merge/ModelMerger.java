package org.apache.maven.model.merge;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import org.apache.maven.model.Activation;
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

public class ModelMerger {
  public void merge(Model target, Model source, boolean sourceDominant, Map<?, ?> hints) {
    Objects.requireNonNull(target, "target cannot be null");
    if (source == null)
      return; 
    Map<Object, Object> context = new HashMap<>();
    if (hints != null)
      context.putAll(hints); 
    mergeModel(target, source, sourceDominant, context);
  }
  
  protected void mergeModel(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    mergeModelBase((ModelBase)target, (ModelBase)source, sourceDominant, context);
    mergeModel_ChildProjectUrlInheritAppendPath(target, source, sourceDominant, context);
    mergeModel_ModelVersion(target, source, sourceDominant, context);
    mergeModel_Parent(target, source, sourceDominant, context);
    mergeModel_GroupId(target, source, sourceDominant, context);
    mergeModel_ArtifactId(target, source, sourceDominant, context);
    mergeModel_Version(target, source, sourceDominant, context);
    mergeModel_Packaging(target, source, sourceDominant, context);
    mergeModel_Name(target, source, sourceDominant, context);
    mergeModel_Description(target, source, sourceDominant, context);
    mergeModel_Url(target, source, sourceDominant, context);
    mergeModel_InceptionYear(target, source, sourceDominant, context);
    mergeModel_Organization(target, source, sourceDominant, context);
    mergeModel_Licenses(target, source, sourceDominant, context);
    mergeModel_MailingLists(target, source, sourceDominant, context);
    mergeModel_Developers(target, source, sourceDominant, context);
    mergeModel_Contributors(target, source, sourceDominant, context);
    mergeModel_IssueManagement(target, source, sourceDominant, context);
    mergeModel_Scm(target, source, sourceDominant, context);
    mergeModel_CiManagement(target, source, sourceDominant, context);
    mergeModel_Prerequisites(target, source, sourceDominant, context);
    mergeModel_Build(target, source, sourceDominant, context);
    mergeModel_Profiles(target, source, sourceDominant, context);
  }
  
  protected void mergeModel_ModelVersion(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getModelVersion();
    if (src != null)
      if (sourceDominant || target.getModelVersion() == null) {
        target.setModelVersion(src);
        target.setLocation("modelVersion", source.getLocation("modelVersion"));
      }  
  }
  
  protected void mergeModel_Parent(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    Parent src = source.getParent();
    if (src != null) {
      Parent tgt = target.getParent();
      if (tgt == null) {
        tgt = new Parent();
        tgt.setRelativePath(null);
        target.setParent(tgt);
      } 
      mergeParent(tgt, src, sourceDominant, context);
    } 
  }
  
  protected void mergeModel_GroupId(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getGroupId();
    if (src != null)
      if (sourceDominant || target.getGroupId() == null) {
        target.setGroupId(src);
        target.setLocation("groupId", source.getLocation("groupId"));
      }  
  }
  
  protected void mergeModel_ArtifactId(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getArtifactId();
    if (src != null)
      if (sourceDominant || target.getArtifactId() == null) {
        target.setArtifactId(src);
        target.setLocation("artifactId", source.getLocation("artifactId"));
      }  
  }
  
  protected void mergeModel_ChildProjectUrlInheritAppendPath(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getChildProjectUrlInheritAppendPath();
    if (src != null)
      if (sourceDominant || target.getChildProjectUrlInheritAppendPath() == null) {
        target.setChildProjectUrlInheritAppendPath(src);
        target.setLocation("child.project.url.inherit.append.path", source
            .getLocation("child.project.url.inherit.append.path"));
      }  
  }
  
  protected void mergeModel_Version(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getVersion();
    if (src != null)
      if (sourceDominant || target.getVersion() == null) {
        target.setVersion(src);
        target.setLocation("version", source.getLocation("version"));
      }  
  }
  
  protected void mergeModel_Packaging(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getPackaging();
    if (src != null)
      if (sourceDominant || target.getPackaging() == null) {
        target.setPackaging(src);
        target.setLocation("packaging", source.getLocation("packaging"));
      }  
  }
  
  protected void mergeModel_Name(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getName();
    if (src != null)
      if (sourceDominant || target.getName() == null) {
        target.setName(src);
        target.setLocation("name", source.getLocation("name"));
      }  
  }
  
  protected void mergeModel_Description(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getDescription();
    if (src != null)
      if (sourceDominant || target.getDescription() == null) {
        target.setDescription(src);
        target.setLocation("description", source.getLocation("description"));
      }  
  }
  
  protected void mergeModel_Url(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getUrl();
    if (src != null)
      if (sourceDominant || target.getUrl() == null) {
        target.setUrl(src);
        target.setLocation("url", source.getLocation("url"));
      }  
  }
  
  protected void mergeModel_InceptionYear(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getInceptionYear();
    if (src != null)
      if (sourceDominant || target.getInceptionYear() == null) {
        target.setInceptionYear(src);
        target.setLocation("inceptionYear", source.getLocation("inceptionYear"));
      }  
  }
  
  protected void mergeModel_Organization(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    Organization src = source.getOrganization();
    if (src != null) {
      Organization tgt = target.getOrganization();
      if (tgt == null) {
        tgt = new Organization();
        target.setOrganization(tgt);
      } 
      mergeOrganization(tgt, src, sourceDominant, context);
    } 
  }
  
  protected void mergeModel_Licenses(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    target.setLicenses(merge(target.getLicenses(), source.getLicenses(), sourceDominant, new LicenseKeyComputer()));
  }
  
  protected void mergeModel_MailingLists(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    target.setMailingLists(merge(target.getMailingLists(), source.getMailingLists(), sourceDominant, new MailingListKeyComputer()));
  }
  
  protected void mergeModel_Developers(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    target.setDevelopers(merge(target.getDevelopers(), source.getDevelopers(), sourceDominant, new DeveloperKeyComputer()));
  }
  
  protected void mergeModel_Contributors(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    target.setContributors(merge(target.getContributors(), source.getContributors(), sourceDominant, new ContributorKeyComputer()));
  }
  
  protected void mergeModel_IssueManagement(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    IssueManagement src = source.getIssueManagement();
    if (src != null) {
      IssueManagement tgt = target.getIssueManagement();
      if (tgt == null) {
        tgt = new IssueManagement();
        target.setIssueManagement(tgt);
      } 
      mergeIssueManagement(tgt, src, sourceDominant, context);
    } 
  }
  
  protected void mergeModel_Scm(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    Scm src = source.getScm();
    if (src != null) {
      Scm tgt = target.getScm();
      if (tgt == null) {
        tgt = new Scm();
        tgt.setTag(null);
        target.setScm(tgt);
      } 
      mergeScm(tgt, src, sourceDominant, context);
    } 
  }
  
  protected void mergeModel_CiManagement(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    CiManagement src = source.getCiManagement();
    if (src != null) {
      CiManagement tgt = target.getCiManagement();
      if (tgt == null) {
        tgt = new CiManagement();
        target.setCiManagement(tgt);
      } 
      mergeCiManagement(tgt, src, sourceDominant, context);
    } 
  }
  
  protected void mergeModel_Prerequisites(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    Prerequisites src = source.getPrerequisites();
    if (src != null) {
      Prerequisites tgt = target.getPrerequisites();
      if (tgt == null) {
        tgt = new Prerequisites();
        tgt.setMaven(null);
        target.setPrerequisites(tgt);
      } 
      mergePrerequisites(tgt, src, sourceDominant, context);
    } 
  }
  
  protected void mergeModel_Build(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    Build src = source.getBuild();
    if (src != null) {
      Build tgt = target.getBuild();
      if (tgt == null) {
        tgt = new Build();
        target.setBuild(tgt);
      } 
      mergeBuild(tgt, src, sourceDominant, context);
    } 
  }
  
  protected void mergeModel_Profiles(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
    target.setProfiles(merge(target.getProfiles(), source.getProfiles(), sourceDominant, new ProfileKeyComputer()));
  }
  
  protected void mergeModelBase(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
    mergeModelBase_DistributionManagement(target, source, sourceDominant, context);
    mergeModelBase_Modules(target, source, sourceDominant, context);
    mergeModelBase_Repositories(target, source, sourceDominant, context);
    mergeModelBase_PluginRepositories(target, source, sourceDominant, context);
    mergeModelBase_Dependencies(target, source, sourceDominant, context);
    mergeModelBase_Reporting(target, source, sourceDominant, context);
    mergeModelBase_DependencyManagement(target, source, sourceDominant, context);
    mergeModelBase_Properties(target, source, sourceDominant, context);
  }
  
  protected void mergeModelBase_Modules(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
    List<String> src = source.getModules();
    if (!src.isEmpty()) {
      List<String> tgt = target.getModules();
      List<String> merged = new ArrayList<>(tgt.size() + src.size());
      merged.addAll(tgt);
      merged.addAll(src);
      target.setModules(merged);
    } 
  }
  
  protected void mergeModelBase_Dependencies(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
    target.setDependencies(merge(target.getDependencies(), source.getDependencies(), sourceDominant, new DependencyKeyComputer()));
  }
  
  protected void mergeModelBase_Repositories(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
    target.setRepositories(merge(target.getRepositories(), source.getRepositories(), sourceDominant, new RepositoryKeyComputer()));
  }
  
  protected void mergeModelBase_PluginRepositories(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
    target.setPluginRepositories(merge(target.getPluginRepositories(), source.getPluginRepositories(), sourceDominant, new RepositoryKeyComputer()));
  }
  
  protected void mergeModelBase_DistributionManagement(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
    DistributionManagement src = source.getDistributionManagement();
    if (src != null) {
      DistributionManagement tgt = target.getDistributionManagement();
      if (tgt == null) {
        tgt = new DistributionManagement();
        target.setDistributionManagement(tgt);
      } 
      mergeDistributionManagement(tgt, src, sourceDominant, context);
    } 
  }
  
  protected void mergeModelBase_Reporting(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
    Reporting src = source.getReporting();
    if (src != null) {
      Reporting tgt = target.getReporting();
      if (tgt == null) {
        tgt = new Reporting();
        target.setReporting(tgt);
      } 
      mergeReporting(tgt, src, sourceDominant, context);
    } 
  }
  
  protected void mergeModelBase_DependencyManagement(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
    DependencyManagement src = source.getDependencyManagement();
    if (src != null) {
      DependencyManagement tgt = target.getDependencyManagement();
      if (tgt == null) {
        tgt = new DependencyManagement();
        target.setDependencyManagement(tgt);
      } 
      mergeDependencyManagement(tgt, src, sourceDominant, context);
    } 
  }
  
  protected void mergeModelBase_Properties(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
    Properties merged = new Properties();
    if (sourceDominant) {
      merged.putAll(target.getProperties());
      merged.putAll(source.getProperties());
    } else {
      merged.putAll(source.getProperties());
      merged.putAll(target.getProperties());
    } 
    target.setProperties(merged);
    target.setLocation("properties", InputLocation.merge(target.getLocation("properties"), source
          .getLocation("properties"), sourceDominant));
  }
  
  protected void mergeDistributionManagement(DistributionManagement target, DistributionManagement source, boolean sourceDominant, Map<Object, Object> context) {
    mergeDistributionManagement_Repository(target, source, sourceDominant, context);
    mergeDistributionManagement_SnapshotRepository(target, source, sourceDominant, context);
    mergeDistributionManagement_Site(target, source, sourceDominant, context);
    mergeDistributionManagement_Status(target, source, sourceDominant, context);
    mergeDistributionManagement_DownloadUrl(target, source, sourceDominant, context);
  }
  
  protected void mergeDistributionManagement_Repository(DistributionManagement target, DistributionManagement source, boolean sourceDominant, Map<Object, Object> context) {
    DeploymentRepository src = source.getRepository();
    if (src != null) {
      DeploymentRepository tgt = target.getRepository();
      if (tgt == null) {
        tgt = new DeploymentRepository();
        target.setRepository(tgt);
      } 
      mergeDeploymentRepository(tgt, src, sourceDominant, context);
    } 
  }
  
  protected void mergeDistributionManagement_SnapshotRepository(DistributionManagement target, DistributionManagement source, boolean sourceDominant, Map<Object, Object> context) {
    DeploymentRepository src = source.getSnapshotRepository();
    if (src != null) {
      DeploymentRepository tgt = target.getSnapshotRepository();
      if (tgt == null) {
        tgt = new DeploymentRepository();
        target.setSnapshotRepository(tgt);
      } 
      mergeDeploymentRepository(tgt, src, sourceDominant, context);
    } 
  }
  
  protected void mergeDistributionManagement_Site(DistributionManagement target, DistributionManagement source, boolean sourceDominant, Map<Object, Object> context) {
    Site src = source.getSite();
    if (src != null) {
      Site tgt = target.getSite();
      if (tgt == null) {
        tgt = new Site();
        target.setSite(tgt);
      } 
      mergeSite(tgt, src, sourceDominant, context);
    } 
  }
  
  protected void mergeDistributionManagement_Status(DistributionManagement target, DistributionManagement source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getStatus();
    if (src != null)
      if (sourceDominant || target.getStatus() == null) {
        target.setStatus(src);
        target.setLocation("status", source.getLocation("status"));
      }  
  }
  
  protected void mergeDistributionManagement_DownloadUrl(DistributionManagement target, DistributionManagement source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getDownloadUrl();
    if (src != null)
      if (sourceDominant || target.getDownloadUrl() == null) {
        target.setDownloadUrl(src);
        target.setLocation("downloadUrl", source.getLocation("downloadUrl"));
      }  
  }
  
  protected void mergeRelocation(Relocation target, Relocation source, boolean sourceDominant, Map<Object, Object> context) {
    mergeRelocation_GroupId(target, source, sourceDominant, context);
    mergeRelocation_ArtifactId(target, source, sourceDominant, context);
    mergeRelocation_Version(target, source, sourceDominant, context);
    mergeRelocation_Message(target, source, sourceDominant, context);
  }
  
  protected void mergeRelocation_GroupId(Relocation target, Relocation source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getGroupId();
    if (src != null)
      if (sourceDominant || target.getGroupId() == null) {
        target.setGroupId(src);
        target.setLocation("groupId", source.getLocation("groupId"));
      }  
  }
  
  protected void mergeRelocation_ArtifactId(Relocation target, Relocation source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getArtifactId();
    if (src != null)
      if (sourceDominant || target.getArtifactId() == null) {
        target.setArtifactId(src);
        target.setLocation("artifactId", source.getLocation("artifactId"));
      }  
  }
  
  protected void mergeRelocation_Version(Relocation target, Relocation source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getVersion();
    if (src != null)
      if (sourceDominant || target.getVersion() == null) {
        target.setVersion(src);
        target.setLocation("version", source.getLocation("version"));
      }  
  }
  
  protected void mergeRelocation_Message(Relocation target, Relocation source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getMessage();
    if (src != null)
      if (sourceDominant || target.getMessage() == null) {
        target.setMessage(src);
        target.setLocation("message", source.getLocation("message"));
      }  
  }
  
  protected void mergeDeploymentRepository(DeploymentRepository target, DeploymentRepository source, boolean sourceDominant, Map<Object, Object> context) {
    mergeRepository((Repository)target, (Repository)source, sourceDominant, context);
    mergeDeploymentRepository_UniqueVersion(target, source, sourceDominant, context);
  }
  
  protected void mergeDeploymentRepository_UniqueVersion(DeploymentRepository target, DeploymentRepository source, boolean sourceDominant, Map<Object, Object> context) {
    if (sourceDominant) {
      target.setUniqueVersion(source.isUniqueVersion());
      target.setLocation("uniqueVersion", source.getLocation("uniqueVersion"));
    } 
  }
  
  protected void mergeSite(Site target, Site source, boolean sourceDominant, Map<Object, Object> context) {
    mergeSite_ChildSiteUrlInheritAppendPath(target, source, sourceDominant, context);
    mergeSite_Id(target, source, sourceDominant, context);
    mergeSite_Name(target, source, sourceDominant, context);
    mergeSite_Url(target, source, sourceDominant, context);
  }
  
  protected void mergeSite_ChildSiteUrlInheritAppendPath(Site target, Site source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getChildSiteUrlInheritAppendPath();
    if (src != null)
      if (sourceDominant || target.getChildSiteUrlInheritAppendPath() == null) {
        target.setChildSiteUrlInheritAppendPath(src);
        target.setLocation("child.site.url.inherit.append.path", source
            .getLocation("child.site.url.inherit.append.path"));
      }  
  }
  
  protected void mergeSite_Id(Site target, Site source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getId();
    if (src != null)
      if (sourceDominant || target.getId() == null) {
        target.setId(src);
        target.setLocation("id", source.getLocation("id"));
      }  
  }
  
  protected void mergeSite_Name(Site target, Site source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getName();
    if (src != null)
      if (sourceDominant || target.getName() == null) {
        target.setName(src);
        target.setLocation("name", source.getLocation("name"));
      }  
  }
  
  protected void mergeSite_Url(Site target, Site source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getUrl();
    if (src != null)
      if (sourceDominant || target.getUrl() == null) {
        target.setUrl(src);
        target.setLocation("url", source.getLocation("url"));
      }  
  }
  
  protected void mergeRepository(Repository target, Repository source, boolean sourceDominant, Map<Object, Object> context) {
    mergeRepositoryBase((RepositoryBase)target, (RepositoryBase)source, sourceDominant, context);
    mergeRepository_Releases(target, source, sourceDominant, context);
    mergeRepository_Snapshots(target, source, sourceDominant, context);
  }
  
  protected void mergeRepository_Releases(Repository target, Repository source, boolean sourceDominant, Map<Object, Object> context) {
    RepositoryPolicy src = source.getReleases();
    if (src != null) {
      RepositoryPolicy tgt = target.getReleases();
      if (tgt == null) {
        tgt = new RepositoryPolicy();
        target.setReleases(tgt);
      } 
      mergeRepositoryPolicy(tgt, src, sourceDominant, context);
    } 
  }
  
  protected void mergeRepository_Snapshots(Repository target, Repository source, boolean sourceDominant, Map<Object, Object> context) {
    RepositoryPolicy src = source.getSnapshots();
    if (src != null) {
      RepositoryPolicy tgt = target.getSnapshots();
      if (tgt == null) {
        tgt = new RepositoryPolicy();
        target.setSnapshots(tgt);
      } 
      mergeRepositoryPolicy(tgt, src, sourceDominant, context);
    } 
  }
  
  protected void mergeRepositoryBase(RepositoryBase target, RepositoryBase source, boolean sourceDominant, Map<Object, Object> context) {
    mergeRepositoryBase_Id(target, source, sourceDominant, context);
    mergeRepositoryBase_Name(target, source, sourceDominant, context);
    mergeRepositoryBase_Url(target, source, sourceDominant, context);
    mergeRepositoryBase_Layout(target, source, sourceDominant, context);
  }
  
  protected void mergeRepositoryBase_Id(RepositoryBase target, RepositoryBase source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getId();
    if (src != null)
      if (sourceDominant || target.getId() == null) {
        target.setId(src);
        target.setLocation("id", source.getLocation("id"));
      }  
  }
  
  protected void mergeRepositoryBase_Url(RepositoryBase target, RepositoryBase source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getUrl();
    if (src != null)
      if (sourceDominant || target.getUrl() == null) {
        target.setUrl(src);
        target.setLocation("url", source.getLocation("url"));
      }  
  }
  
  protected void mergeRepositoryBase_Name(RepositoryBase target, RepositoryBase source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getName();
    if (src != null)
      if (sourceDominant || target.getName() == null) {
        target.setName(src);
        target.setLocation("name", source.getLocation("name"));
      }  
  }
  
  protected void mergeRepositoryBase_Layout(RepositoryBase target, RepositoryBase source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getLayout();
    if (src != null)
      if (sourceDominant || target.getLayout() == null) {
        target.setLayout(src);
        target.setLocation("layout", source.getLocation("layout"));
      }  
  }
  
  protected void mergeRepositoryPolicy(RepositoryPolicy target, RepositoryPolicy source, boolean sourceDominant, Map<Object, Object> context) {
    mergeRepositoryPolicy_Enabled(target, source, sourceDominant, context);
    mergeRepositoryPolicy_UpdatePolicy(target, source, sourceDominant, context);
    mergeRepositoryPolicy_ChecksumPolicy(target, source, sourceDominant, context);
  }
  
  protected void mergeRepositoryPolicy_Enabled(RepositoryPolicy target, RepositoryPolicy source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getEnabled();
    if (src != null)
      if (sourceDominant || target.getEnabled() == null) {
        target.setEnabled(src);
        target.setLocation("enabled", source.getLocation("enabled"));
      }  
  }
  
  protected void mergeRepositoryPolicy_UpdatePolicy(RepositoryPolicy target, RepositoryPolicy source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getUpdatePolicy();
    if (src != null)
      if (sourceDominant || target.getUpdatePolicy() == null) {
        target.setUpdatePolicy(src);
        target.setLocation("updatePolicy", source.getLocation("updatePolicy"));
      }  
  }
  
  protected void mergeRepositoryPolicy_ChecksumPolicy(RepositoryPolicy target, RepositoryPolicy source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getChecksumPolicy();
    if (src != null)
      if (sourceDominant || target.getChecksumPolicy() == null) {
        target.setChecksumPolicy(src);
        target.setLocation("checksumPolicy", source.getLocation("checksumPolicy"));
      }  
  }
  
  protected void mergeDependency(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
    mergeDependency_GroupId(target, source, sourceDominant, context);
    mergeDependency_ArtifactId(target, source, sourceDominant, context);
    mergeDependency_Version(target, source, sourceDominant, context);
    mergeDependency_Type(target, source, sourceDominant, context);
    mergeDependency_Classifier(target, source, sourceDominant, context);
    mergeDependency_Scope(target, source, sourceDominant, context);
    mergeDependency_SystemPath(target, source, sourceDominant, context);
    mergeDependency_Optional(target, source, sourceDominant, context);
    mergeDependency_Exclusions(target, source, sourceDominant, context);
  }
  
  protected void mergeDependency_GroupId(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getGroupId();
    if (src != null)
      if (sourceDominant || target.getGroupId() == null) {
        target.setGroupId(src);
        target.setLocation("groupId", source.getLocation("groupId"));
      }  
  }
  
  protected void mergeDependency_ArtifactId(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getArtifactId();
    if (src != null)
      if (sourceDominant || target.getArtifactId() == null) {
        target.setArtifactId(src);
        target.setLocation("artifactId", source.getLocation("artifactId"));
      }  
  }
  
  protected void mergeDependency_Version(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getVersion();
    if (src != null)
      if (sourceDominant || target.getVersion() == null) {
        target.setVersion(src);
        target.setLocation("version", source.getLocation("version"));
      }  
  }
  
  protected void mergeDependency_Type(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getType();
    if (src != null)
      if (sourceDominant || target.getType() == null) {
        target.setType(src);
        target.setLocation("type", source.getLocation("type"));
      }  
  }
  
  protected void mergeDependency_Classifier(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getClassifier();
    if (src != null)
      if (sourceDominant || target.getClassifier() == null) {
        target.setClassifier(src);
        target.setLocation("classifier", source.getLocation("classifier"));
      }  
  }
  
  protected void mergeDependency_Scope(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getScope();
    if (src != null)
      if (sourceDominant || target.getScope() == null) {
        target.setScope(src);
        target.setLocation("scope", source.getLocation("scope"));
      }  
  }
  
  protected void mergeDependency_SystemPath(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getSystemPath();
    if (src != null)
      if (sourceDominant || target.getSystemPath() == null) {
        target.setSystemPath(src);
        target.setLocation("systemPath", source.getLocation("systemPath"));
      }  
  }
  
  protected void mergeDependency_Optional(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getOptional();
    if (src != null)
      if (sourceDominant || target.getOptional() == null) {
        target.setOptional(src);
        target.setLocation("optional", source.getLocation("optional"));
      }  
  }
  
  protected void mergeDependency_Exclusions(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
    target.setExclusions(merge(target.getExclusions(), source.getExclusions(), sourceDominant, new ExclusionKeyComputer()));
  }
  
  protected void mergeExclusion(Exclusion target, Exclusion source, boolean sourceDominant, Map<Object, Object> context) {
    mergeExclusion_GroupId(target, source, sourceDominant, context);
    mergeExclusion_ArtifactId(target, source, sourceDominant, context);
  }
  
  protected void mergeExclusion_GroupId(Exclusion target, Exclusion source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getGroupId();
    if (src != null)
      if (sourceDominant || target.getGroupId() == null) {
        target.setGroupId(src);
        target.setLocation("groupId", source.getLocation("groupId"));
      }  
  }
  
  protected void mergeExclusion_ArtifactId(Exclusion target, Exclusion source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getArtifactId();
    if (src != null)
      if (sourceDominant || target.getArtifactId() == null) {
        target.setArtifactId(src);
        target.setLocation("artifactId", source.getLocation("artifactId"));
      }  
  }
  
  protected void mergeReporting(Reporting target, Reporting source, boolean sourceDominant, Map<Object, Object> context) {
    mergeReporting_OutputDirectory(target, source, sourceDominant, context);
    mergeReporting_ExcludeDefaults(target, source, sourceDominant, context);
    mergeReporting_Plugins(target, source, sourceDominant, context);
  }
  
  protected void mergeReporting_OutputDirectory(Reporting target, Reporting source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getOutputDirectory();
    if (src != null)
      if (sourceDominant || target.getOutputDirectory() == null) {
        target.setOutputDirectory(src);
        target.setLocation("outputDirectory", source.getLocation("outputDirectory"));
      }  
  }
  
  protected void mergeReporting_ExcludeDefaults(Reporting target, Reporting source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getExcludeDefaults();
    if (src != null)
      if (sourceDominant || target.getExcludeDefaults() == null) {
        target.setExcludeDefaults(src);
        target.setLocation("excludeDefaults", source.getLocation("excludeDefaults"));
      }  
  }
  
  protected void mergeReporting_Plugins(Reporting target, Reporting source, boolean sourceDominant, Map<Object, Object> context) {
    target.setPlugins(merge(target.getPlugins(), source.getPlugins(), sourceDominant, new ReportPluginKeyComputer()));
  }
  
  protected void mergeReportPlugin(ReportPlugin target, ReportPlugin source, boolean sourceDominant, Map<Object, Object> context) {
    mergeConfigurationContainer((ConfigurationContainer)target, (ConfigurationContainer)source, sourceDominant, context);
    mergeReportPlugin_GroupId(target, source, sourceDominant, context);
    mergeReportPlugin_ArtifactId(target, source, sourceDominant, context);
    mergeReportPlugin_Version(target, source, sourceDominant, context);
    mergeReportPlugin_ReportSets(target, source, sourceDominant, context);
  }
  
  protected void mergeReportPlugin_GroupId(ReportPlugin target, ReportPlugin source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getGroupId();
    if (src != null)
      if (sourceDominant || target.getGroupId() == null) {
        target.setGroupId(src);
        target.setLocation("groupId", source.getLocation("groupId"));
      }  
  }
  
  protected void mergeReportPlugin_ArtifactId(ReportPlugin target, ReportPlugin source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getArtifactId();
    if (src != null)
      if (sourceDominant || target.getArtifactId() == null) {
        target.setArtifactId(src);
        target.setLocation("artifactId", source.getLocation("artifactId"));
      }  
  }
  
  protected void mergeReportPlugin_Version(ReportPlugin target, ReportPlugin source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getVersion();
    if (src != null)
      if (sourceDominant || target.getVersion() == null) {
        target.setVersion(src);
        target.setLocation("version", source.getLocation("version"));
      }  
  }
  
  protected void mergeReportPlugin_ReportSets(ReportPlugin target, ReportPlugin source, boolean sourceDominant, Map<Object, Object> context) {
    target.setReportSets(merge(target.getReportSets(), source.getReportSets(), sourceDominant, new ReportSetKeyComputer()));
  }
  
  protected void mergeReportSet(ReportSet target, ReportSet source, boolean sourceDominant, Map<Object, Object> context) {
    mergeConfigurationContainer((ConfigurationContainer)target, (ConfigurationContainer)source, sourceDominant, context);
    mergeReportSet_Id(target, source, sourceDominant, context);
    mergeReportSet_Reports(target, source, sourceDominant, context);
  }
  
  protected void mergeReportSet_Id(ReportSet target, ReportSet source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getId();
    if (src != null)
      if (sourceDominant || target.getId() == null) {
        target.setId(src);
        target.setLocation("id", source.getLocation("id"));
      }  
  }
  
  protected void mergeReportSet_Reports(ReportSet target, ReportSet source, boolean sourceDominant, Map<Object, Object> context) {
    List<String> src = source.getReports();
    if (!src.isEmpty()) {
      List<String> tgt = target.getReports();
      List<String> merged = new ArrayList<>(tgt.size() + src.size());
      merged.addAll(tgt);
      merged.addAll(src);
      target.setReports(merged);
      InputLocation sourceLocation = source.getLocation("reports");
      if (sourceLocation != null) {
        InputLocation targetLocation = target.getLocation("reports");
        if (targetLocation == null) {
          target.setLocation("reports", sourceLocation);
        } else {
          for (int i = 0; i < src.size(); i++)
            targetLocation.setLocation(Integer.valueOf(tgt.size() + i), sourceLocation
                .getLocation(Integer.valueOf(i))); 
        } 
      } 
    } 
  }
  
  protected void mergeDependencyManagement(DependencyManagement target, DependencyManagement source, boolean sourceDominant, Map<Object, Object> context) {
    mergeDependencyManagement_Dependencies(target, source, sourceDominant, context);
  }
  
  protected void mergeDependencyManagement_Dependencies(DependencyManagement target, DependencyManagement source, boolean sourceDominant, Map<Object, Object> context) {
    target.setDependencies(merge(target.getDependencies(), source.getDependencies(), sourceDominant, new DependencyKeyComputer()));
  }
  
  protected void mergeParent(Parent target, Parent source, boolean sourceDominant, Map<Object, Object> context) {
    mergeParent_GroupId(target, source, sourceDominant, context);
    mergeParent_ArtifactId(target, source, sourceDominant, context);
    mergeParent_Version(target, source, sourceDominant, context);
    mergeParent_RelativePath(target, source, sourceDominant, context);
  }
  
  protected void mergeParent_GroupId(Parent target, Parent source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getGroupId();
    if (src != null)
      if (sourceDominant || target.getGroupId() == null) {
        target.setGroupId(src);
        target.setLocation("groupId", source.getLocation("groupId"));
      }  
  }
  
  protected void mergeParent_ArtifactId(Parent target, Parent source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getArtifactId();
    if (src != null)
      if (sourceDominant || target.getArtifactId() == null) {
        target.setArtifactId(src);
        target.setLocation("artifactId", source.getLocation("artifactId"));
      }  
  }
  
  protected void mergeParent_Version(Parent target, Parent source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getVersion();
    if (src != null)
      if (sourceDominant || target.getVersion() == null) {
        target.setVersion(src);
        target.setLocation("version", source.getLocation("version"));
      }  
  }
  
  protected void mergeParent_RelativePath(Parent target, Parent source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getRelativePath();
    if (src != null)
      if (sourceDominant || target.getRelativePath() == null) {
        target.setRelativePath(src);
        target.setLocation("relativePath", source.getLocation("relativePath"));
      }  
  }
  
  protected void mergeOrganization(Organization target, Organization source, boolean sourceDominant, Map<Object, Object> context) {
    mergeOrganization_Name(target, source, sourceDominant, context);
    mergeOrganization_Url(target, source, sourceDominant, context);
  }
  
  protected void mergeOrganization_Name(Organization target, Organization source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getName();
    if (src != null)
      if (sourceDominant || target.getName() == null) {
        target.setName(src);
        target.setLocation("name", source.getLocation("name"));
      }  
  }
  
  protected void mergeOrganization_Url(Organization target, Organization source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getUrl();
    if (src != null)
      if (sourceDominant || target.getUrl() == null) {
        target.setUrl(src);
        target.setLocation("url", source.getLocation("url"));
      }  
  }
  
  protected void mergeLicense(License target, License source, boolean sourceDominant, Map<Object, Object> context) {
    mergeLicense_Name(target, source, sourceDominant, context);
    mergeLicense_Url(target, source, sourceDominant, context);
    mergeLicense_Distribution(target, source, sourceDominant, context);
    mergeLicense_Comments(target, source, sourceDominant, context);
  }
  
  protected void mergeLicense_Name(License target, License source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getName();
    if (src != null)
      if (sourceDominant || target.getName() == null) {
        target.setName(src);
        target.setLocation("name", source.getLocation("name"));
      }  
  }
  
  protected void mergeLicense_Url(License target, License source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getUrl();
    if (src != null)
      if (sourceDominant || target.getUrl() == null) {
        target.setUrl(src);
        target.setLocation("url", source.getLocation("url"));
      }  
  }
  
  protected void mergeLicense_Distribution(License target, License source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getDistribution();
    if (src != null)
      if (sourceDominant || target.getDistribution() == null) {
        target.setDistribution(src);
        target.setLocation("distribution", source.getLocation("distribution"));
      }  
  }
  
  protected void mergeLicense_Comments(License target, License source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getComments();
    if (src != null)
      if (sourceDominant || target.getComments() == null) {
        target.setComments(src);
        target.setLocation("comments", source.getLocation("comments"));
      }  
  }
  
  protected void mergeMailingList(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
    mergeMailingList_Name(target, source, sourceDominant, context);
    mergeMailingList_Subscribe(target, source, sourceDominant, context);
    mergeMailingList_Unsubscribe(target, source, sourceDominant, context);
    mergeMailingList_Post(target, source, sourceDominant, context);
    mergeMailingList_OtherArchives(target, source, sourceDominant, context);
  }
  
  protected void mergeMailingList_Name(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getName();
    if (src != null)
      if (sourceDominant || target.getName() == null) {
        target.setName(src);
        target.setLocation("name", source.getLocation("name"));
      }  
  }
  
  protected void mergeMailingList_Subscribe(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getSubscribe();
    if (src != null)
      if (sourceDominant || target.getSubscribe() == null) {
        target.setSubscribe(src);
        target.setLocation("subscribe", source.getLocation("subscribe"));
      }  
  }
  
  protected void mergeMailingList_Unsubscribe(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getUnsubscribe();
    if (src != null)
      if (sourceDominant || target.getUnsubscribe() == null) {
        target.setUnsubscribe(src);
        target.setLocation("unsubscribe", source.getLocation("unsubscribe"));
      }  
  }
  
  protected void mergeMailingList_Post(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getPost();
    if (src != null)
      if (sourceDominant || target.getPost() == null) {
        target.setPost(src);
        target.setLocation("post", source.getLocation("post"));
      }  
  }
  
  protected void mergeMailingList_Archive(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getArchive();
    if (src != null)
      if (sourceDominant || target.getArchive() == null) {
        target.setArchive(src);
        target.setLocation("archive", source.getLocation("archive"));
      }  
  }
  
  protected void mergeMailingList_OtherArchives(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
    List<String> src = source.getOtherArchives();
    if (!src.isEmpty()) {
      List<String> tgt = target.getOtherArchives();
      List<String> merged = new ArrayList<>(tgt.size() + src.size());
      merged.addAll(tgt);
      merged.addAll(src);
      target.setOtherArchives(merged);
    } 
  }
  
  protected void mergeDeveloper(Developer target, Developer source, boolean sourceDominant, Map<Object, Object> context) {
    mergeContributor((Contributor)target, (Contributor)source, sourceDominant, context);
    mergeDeveloper_Id(target, source, sourceDominant, context);
  }
  
  protected void mergeDeveloper_Id(Developer target, Developer source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getId();
    if (src != null)
      if (sourceDominant || target.getId() == null) {
        target.setId(src);
        target.setLocation("id", source.getLocation("id"));
      }  
  }
  
  protected void mergeContributor(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
    mergeContributor_Name(target, source, sourceDominant, context);
    mergeContributor_Email(target, source, sourceDominant, context);
    mergeContributor_Url(target, source, sourceDominant, context);
    mergeContributor_Organization(target, source, sourceDominant, context);
    mergeContributor_OrganizationUrl(target, source, sourceDominant, context);
    mergeContributor_Timezone(target, source, sourceDominant, context);
    mergeContributor_Roles(target, source, sourceDominant, context);
    mergeContributor_Properties(target, source, sourceDominant, context);
  }
  
  protected void mergeContributor_Name(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getName();
    if (src != null)
      if (sourceDominant || target.getName() == null) {
        target.setName(src);
        target.setLocation("name", source.getLocation("name"));
      }  
  }
  
  protected void mergeContributor_Email(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getEmail();
    if (src != null)
      if (sourceDominant || target.getEmail() == null) {
        target.setEmail(src);
        target.setLocation("email", source.getLocation("email"));
      }  
  }
  
  protected void mergeContributor_Url(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getUrl();
    if (src != null)
      if (sourceDominant || target.getUrl() == null) {
        target.setUrl(src);
        target.setLocation("url", source.getLocation("url"));
      }  
  }
  
  protected void mergeContributor_Organization(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getOrganization();
    if (src != null)
      if (sourceDominant || target.getOrganization() == null) {
        target.setOrganization(src);
        target.setLocation("organization", source.getLocation("organization"));
      }  
  }
  
  protected void mergeContributor_OrganizationUrl(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getOrganizationUrl();
    if (src != null)
      if (sourceDominant || target.getOrganizationUrl() == null) {
        target.setOrganizationUrl(src);
        target.setLocation("organizationUrl", source.getLocation("organizationUrl"));
      }  
  }
  
  protected void mergeContributor_Timezone(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getTimezone();
    if (src != null)
      if (sourceDominant || target.getTimezone() == null) {
        target.setTimezone(src);
        target.setLocation("timezone", source.getLocation("timezone"));
      }  
  }
  
  protected void mergeContributor_Roles(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
    List<String> src = source.getRoles();
    if (!src.isEmpty()) {
      List<String> tgt = target.getRoles();
      List<String> merged = new ArrayList<>(tgt.size() + src.size());
      merged.addAll(tgt);
      merged.addAll(src);
      target.setRoles(merged);
    } 
  }
  
  protected void mergeContributor_Properties(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
    Properties merged = new Properties();
    if (sourceDominant) {
      merged.putAll(target.getProperties());
      merged.putAll(source.getProperties());
    } else {
      merged.putAll(source.getProperties());
      merged.putAll(target.getProperties());
    } 
    target.setProperties(merged);
    target.setLocation("properties", InputLocation.merge(target.getLocation("properties"), source
          .getLocation("properties"), sourceDominant));
  }
  
  protected void mergeIssueManagement(IssueManagement target, IssueManagement source, boolean sourceDominant, Map<Object, Object> context) {
    mergeIssueManagement_Url(target, source, sourceDominant, context);
    mergeIssueManagement_System(target, source, sourceDominant, context);
  }
  
  protected void mergeIssueManagement_System(IssueManagement target, IssueManagement source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getSystem();
    if (src != null)
      if (sourceDominant || target.getSystem() == null) {
        target.setSystem(src);
        target.setLocation("system", source.getLocation("system"));
      }  
  }
  
  protected void mergeIssueManagement_Url(IssueManagement target, IssueManagement source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getUrl();
    if (src != null)
      if (sourceDominant || target.getUrl() == null) {
        target.setUrl(src);
        target.setLocation("url", source.getLocation("url"));
      }  
  }
  
  protected void mergeScm(Scm target, Scm source, boolean sourceDominant, Map<Object, Object> context) {
    mergeScm_ChildScmConnectionInheritAppendPath(target, source, sourceDominant, context);
    mergeScm_ChildScmDeveloperConnectionInheritAppendPath(target, source, sourceDominant, context);
    mergeScm_ChildScmUrlInheritAppendPath(target, source, sourceDominant, context);
    mergeScm_Url(target, source, sourceDominant, context);
    mergeScm_Connection(target, source, sourceDominant, context);
    mergeScm_DeveloperConnection(target, source, sourceDominant, context);
    mergeScm_Tag(target, source, sourceDominant, context);
  }
  
  protected void mergeScm_ChildScmConnectionInheritAppendPath(Scm target, Scm source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getChildScmConnectionInheritAppendPath();
    if (src != null)
      if (sourceDominant || target.getChildScmConnectionInheritAppendPath() == null) {
        target.setChildScmConnectionInheritAppendPath(src);
        target.setLocation("child.scm.connection.inherit.append.path", source
            .getLocation("child.scm.connection.inherit.append.path"));
      }  
  }
  
  protected void mergeScm_ChildScmDeveloperConnectionInheritAppendPath(Scm target, Scm source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getChildScmDeveloperConnectionInheritAppendPath();
    if (src != null)
      if (sourceDominant || target.getChildScmDeveloperConnectionInheritAppendPath() == null) {
        target.setChildScmDeveloperConnectionInheritAppendPath(src);
        target.setLocation("child.scm.developerConnection.inherit.append.path", source
            .getLocation("child.scm.developerConnection.inherit.append.path"));
      }  
  }
  
  protected void mergeScm_ChildScmUrlInheritAppendPath(Scm target, Scm source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getChildScmUrlInheritAppendPath();
    if (src != null)
      if (sourceDominant || target.getChildScmUrlInheritAppendPath() == null) {
        target.setChildScmUrlInheritAppendPath(src);
        target.setLocation("child.scm.url.inherit.append.path", source
            .getLocation("child.scm.url.inherit.append.path"));
      }  
  }
  
  protected void mergeScm_Url(Scm target, Scm source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getUrl();
    if (src != null)
      if (sourceDominant || target.getUrl() == null) {
        target.setUrl(src);
        target.setLocation("url", source.getLocation("url"));
      }  
  }
  
  protected void mergeScm_Connection(Scm target, Scm source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getConnection();
    if (src != null)
      if (sourceDominant || target.getConnection() == null) {
        target.setConnection(src);
        target.setLocation("connection", source.getLocation("connection"));
      }  
  }
  
  protected void mergeScm_DeveloperConnection(Scm target, Scm source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getDeveloperConnection();
    if (src != null)
      if (sourceDominant || target.getDeveloperConnection() == null) {
        target.setDeveloperConnection(src);
        target.setLocation("developerConnection", source.getLocation("developerConnection"));
      }  
  }
  
  protected void mergeScm_Tag(Scm target, Scm source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getTag();
    if (src != null)
      if (sourceDominant || target.getTag() == null) {
        target.setTag(src);
        target.setLocation("tag", source.getLocation("tag"));
      }  
  }
  
  protected void mergeCiManagement(CiManagement target, CiManagement source, boolean sourceDominant, Map<Object, Object> context) {
    mergeCiManagement_System(target, source, sourceDominant, context);
    mergeCiManagement_Url(target, source, sourceDominant, context);
    mergeCiManagement_Notifiers(target, source, sourceDominant, context);
  }
  
  protected void mergeCiManagement_System(CiManagement target, CiManagement source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getSystem();
    if (src != null)
      if (sourceDominant || target.getSystem() == null) {
        target.setSystem(src);
        target.setLocation("system", source.getLocation("system"));
      }  
  }
  
  protected void mergeCiManagement_Url(CiManagement target, CiManagement source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getUrl();
    if (src != null)
      if (sourceDominant || target.getUrl() == null) {
        target.setUrl(src);
        target.setLocation("url", source.getLocation("url"));
      }  
  }
  
  protected void mergeCiManagement_Notifiers(CiManagement target, CiManagement source, boolean sourceDominant, Map<Object, Object> context) {
    target.setNotifiers(merge(target.getNotifiers(), source.getNotifiers(), sourceDominant, new NotifierKeyComputer()));
  }
  
  protected void mergeNotifier(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
    mergeNotifier_Type(target, source, sourceDominant, context);
    mergeNotifier_Address(target, source, sourceDominant, context);
    mergeNotifier_Configuration(target, source, sourceDominant, context);
    mergeNotifier_SendOnError(target, source, sourceDominant, context);
    mergeNotifier_SendOnFailure(target, source, sourceDominant, context);
    mergeNotifier_SendOnSuccess(target, source, sourceDominant, context);
    mergeNotifier_SendOnWarning(target, source, sourceDominant, context);
  }
  
  protected void mergeNotifier_Type(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getType();
    if (src != null)
      if (sourceDominant || target.getType() == null)
        target.setType(src);  
  }
  
  protected void mergeNotifier_Address(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getAddress();
    if (src != null)
      if (sourceDominant || target.getAddress() == null)
        target.setAddress(src);  
  }
  
  protected void mergeNotifier_Configuration(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
    Properties merged = new Properties();
    if (sourceDominant) {
      merged.putAll(target.getConfiguration());
      merged.putAll(source.getConfiguration());
    } else {
      merged.putAll(source.getConfiguration());
      merged.putAll(target.getConfiguration());
    } 
    target.setConfiguration(merged);
  }
  
  protected void mergeNotifier_SendOnError(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
    if (sourceDominant)
      target.setSendOnError(source.isSendOnError()); 
  }
  
  protected void mergeNotifier_SendOnFailure(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
    if (sourceDominant)
      target.setSendOnFailure(source.isSendOnFailure()); 
  }
  
  protected void mergeNotifier_SendOnSuccess(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
    if (sourceDominant)
      target.setSendOnSuccess(source.isSendOnSuccess()); 
  }
  
  protected void mergeNotifier_SendOnWarning(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
    if (sourceDominant)
      target.setSendOnWarning(source.isSendOnWarning()); 
  }
  
  protected void mergePrerequisites(Prerequisites target, Prerequisites source, boolean sourceDominant, Map<Object, Object> context) {
    mergePrerequisites_Maven(target, source, sourceDominant, context);
  }
  
  protected void mergePrerequisites_Maven(Prerequisites target, Prerequisites source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getMaven();
    if (src != null)
      if (sourceDominant || target.getMaven() == null) {
        target.setMaven(src);
        target.setLocation("maven", source.getLocation("maven"));
      }  
  }
  
  protected void mergeBuild(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
    mergeBuildBase((BuildBase)target, (BuildBase)source, sourceDominant, context);
    mergeBuild_SourceDirectory(target, source, sourceDominant, context);
    mergeBuild_ScriptSourceDirectory(target, source, sourceDominant, context);
    mergeBuild_TestSourceDirectory(target, source, sourceDominant, context);
    mergeBuild_OutputDirectory(target, source, sourceDominant, context);
    mergeBuild_TestOutputDirectory(target, source, sourceDominant, context);
    mergeBuild_Extensions(target, source, sourceDominant, context);
  }
  
  protected void mergeBuild_SourceDirectory(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getSourceDirectory();
    if (src != null)
      if (sourceDominant || target.getSourceDirectory() == null) {
        target.setSourceDirectory(src);
        target.setLocation("sourceDirectory", source.getLocation("sourceDirectory"));
      }  
  }
  
  protected void mergeBuild_ScriptSourceDirectory(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getScriptSourceDirectory();
    if (src != null)
      if (sourceDominant || target.getScriptSourceDirectory() == null) {
        target.setScriptSourceDirectory(src);
        target.setLocation("scriptSourceDirectory", source.getLocation("scriptSourceDirectory"));
      }  
  }
  
  protected void mergeBuild_TestSourceDirectory(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getTestSourceDirectory();
    if (src != null)
      if (sourceDominant || target.getTestSourceDirectory() == null) {
        target.setTestSourceDirectory(src);
        target.setLocation("testSourceDirectory", source.getLocation("testSourceDirectory"));
      }  
  }
  
  protected void mergeBuild_OutputDirectory(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getOutputDirectory();
    if (src != null)
      if (sourceDominant || target.getOutputDirectory() == null) {
        target.setOutputDirectory(src);
        target.setLocation("outputDirectory", source.getLocation("outputDirectory"));
      }  
  }
  
  protected void mergeBuild_TestOutputDirectory(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getTestOutputDirectory();
    if (src != null)
      if (sourceDominant || target.getTestOutputDirectory() == null) {
        target.setTestOutputDirectory(src);
        target.setLocation("testOutputDirectory", source.getLocation("testOutputDirectory"));
      }  
  }
  
  protected void mergeBuild_Extensions(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
    target.setExtensions(merge(target.getExtensions(), source.getExtensions(), sourceDominant, new ExtensionKeyComputer()));
  }
  
  protected void mergeExtension(Extension target, Extension source, boolean sourceDominant, Map<Object, Object> context) {
    mergeExtension_GroupId(target, source, sourceDominant, context);
    mergeExtension_ArtifactId(target, source, sourceDominant, context);
    mergeExtension_Version(target, source, sourceDominant, context);
  }
  
  protected void mergeExtension_GroupId(Extension target, Extension source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getGroupId();
    if (src != null)
      if (sourceDominant || target.getGroupId() == null) {
        target.setGroupId(src);
        target.setLocation("groupId", source.getLocation("groupId"));
      }  
  }
  
  protected void mergeExtension_ArtifactId(Extension target, Extension source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getArtifactId();
    if (src != null)
      if (sourceDominant || target.getArtifactId() == null) {
        target.setArtifactId(src);
        target.setLocation("artifactId", source.getLocation("artifactId"));
      }  
  }
  
  protected void mergeExtension_Version(Extension target, Extension source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getVersion();
    if (src != null)
      if (sourceDominant || target.getVersion() == null) {
        target.setVersion(src);
        target.setLocation("version", source.getLocation("version"));
      }  
  }
  
  protected void mergeBuildBase(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
    mergePluginConfiguration((PluginConfiguration)target, (PluginConfiguration)source, sourceDominant, context);
    mergeBuildBase_DefaultGoal(target, source, sourceDominant, context);
    mergeBuildBase_FinalName(target, source, sourceDominant, context);
    mergeBuildBase_Directory(target, source, sourceDominant, context);
    mergeBuildBase_Resources(target, source, sourceDominant, context);
    mergeBuildBase_TestResources(target, source, sourceDominant, context);
    mergeBuildBase_Filters(target, source, sourceDominant, context);
  }
  
  protected void mergeBuildBase_DefaultGoal(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getDefaultGoal();
    if (src != null)
      if (sourceDominant || target.getDefaultGoal() == null) {
        target.setDefaultGoal(src);
        target.setLocation("defaultGoal", source.getLocation("defaultGoal"));
      }  
  }
  
  protected void mergeBuildBase_Directory(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getDirectory();
    if (src != null)
      if (sourceDominant || target.getDirectory() == null) {
        target.setDirectory(src);
        target.setLocation("directory", source.getLocation("directory"));
      }  
  }
  
  protected void mergeBuildBase_FinalName(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getFinalName();
    if (src != null)
      if (sourceDominant || target.getFinalName() == null) {
        target.setFinalName(src);
        target.setLocation("finalName", source.getLocation("finalName"));
      }  
  }
  
  protected void mergeBuildBase_Filters(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
    List<String> src = source.getFilters();
    if (!src.isEmpty()) {
      List<String> tgt = target.getFilters();
      List<String> merged = new ArrayList<>(tgt.size() + src.size());
      merged.addAll(tgt);
      merged.addAll(src);
      target.setFilters(merged);
    } 
  }
  
  protected void mergeBuildBase_Resources(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
    target.setResources(merge(target.getResources(), source.getResources(), sourceDominant, new ResourceKeyComputer()));
  }
  
  protected void mergeBuildBase_TestResources(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
    target.setTestResources(merge(target.getTestResources(), source.getTestResources(), sourceDominant, new ResourceKeyComputer()));
  }
  
  protected void mergePluginConfiguration(PluginConfiguration target, PluginConfiguration source, boolean sourceDominant, Map<Object, Object> context) {
    mergePluginContainer((PluginContainer)target, (PluginContainer)source, sourceDominant, context);
    mergePluginConfiguration_PluginManagement(target, source, sourceDominant, context);
  }
  
  protected void mergePluginConfiguration_PluginManagement(PluginConfiguration target, PluginConfiguration source, boolean sourceDominant, Map<Object, Object> context) {
    PluginManagement src = source.getPluginManagement();
    if (src != null) {
      PluginManagement tgt = target.getPluginManagement();
      if (tgt == null) {
        tgt = new PluginManagement();
        target.setPluginManagement(tgt);
      } 
      mergePluginManagement(tgt, src, sourceDominant, context);
    } 
  }
  
  protected void mergePluginContainer(PluginContainer target, PluginContainer source, boolean sourceDominant, Map<Object, Object> context) {
    mergePluginContainer_Plugins(target, source, sourceDominant, context);
  }
  
  protected void mergePluginContainer_Plugins(PluginContainer target, PluginContainer source, boolean sourceDominant, Map<Object, Object> context) {
    target.setPlugins(merge(target.getPlugins(), source.getPlugins(), sourceDominant, new PluginKeyComputer()));
  }
  
  protected void mergePluginManagement(PluginManagement target, PluginManagement source, boolean sourceDominant, Map<Object, Object> context) {
    mergePluginContainer((PluginContainer)target, (PluginContainer)source, sourceDominant, context);
  }
  
  protected void mergePlugin(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
    mergeConfigurationContainer((ConfigurationContainer)target, (ConfigurationContainer)source, sourceDominant, context);
    mergePlugin_GroupId(target, source, sourceDominant, context);
    mergePlugin_ArtifactId(target, source, sourceDominant, context);
    mergePlugin_Version(target, source, sourceDominant, context);
    mergePlugin_Extensions(target, source, sourceDominant, context);
    mergePlugin_Dependencies(target, source, sourceDominant, context);
    mergePlugin_Executions(target, source, sourceDominant, context);
  }
  
  protected void mergePlugin_GroupId(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getGroupId();
    if (src != null)
      if (sourceDominant || target.getGroupId() == null) {
        target.setGroupId(src);
        target.setLocation("groupId", source.getLocation("groupId"));
      }  
  }
  
  protected void mergePlugin_ArtifactId(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getArtifactId();
    if (src != null)
      if (sourceDominant || target.getArtifactId() == null) {
        target.setArtifactId(src);
        target.setLocation("artifactId", source.getLocation("artifactId"));
      }  
  }
  
  protected void mergePlugin_Version(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getVersion();
    if (src != null)
      if (sourceDominant || target.getVersion() == null) {
        target.setVersion(src);
        target.setLocation("version", source.getLocation("version"));
      }  
  }
  
  protected void mergePlugin_Extensions(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getExtensions();
    if (src != null)
      if (sourceDominant || target.getExtensions() == null) {
        target.setExtensions(src);
        target.setLocation("extensions", source.getLocation("extensions"));
      }  
  }
  
  protected void mergePlugin_Dependencies(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
    target.setDependencies(merge(target.getDependencies(), source.getDependencies(), sourceDominant, new DependencyKeyComputer()));
  }
  
  protected void mergePlugin_Executions(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
    target.setExecutions(merge(target.getExecutions(), source.getExecutions(), sourceDominant, new ExecutionKeyComputer()));
  }
  
  protected void mergeConfigurationContainer(ConfigurationContainer target, ConfigurationContainer source, boolean sourceDominant, Map<Object, Object> context) {
    mergeConfigurationContainer_Inherited(target, source, sourceDominant, context);
    mergeConfigurationContainer_Configuration(target, source, sourceDominant, context);
  }
  
  protected void mergeConfigurationContainer_Inherited(ConfigurationContainer target, ConfigurationContainer source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getInherited();
    if (src != null)
      if (sourceDominant || target.getInherited() == null) {
        target.setInherited(src);
        target.setLocation("inherited", source.getLocation("inherited"));
      }  
  }
  
  protected void mergeConfigurationContainer_Configuration(ConfigurationContainer target, ConfigurationContainer source, boolean sourceDominant, Map<Object, Object> context) {
    Xpp3Dom src = (Xpp3Dom)source.getConfiguration();
    if (src != null) {
      Xpp3Dom tgt = (Xpp3Dom)target.getConfiguration();
      if (sourceDominant || tgt == null) {
        tgt = Xpp3Dom.mergeXpp3Dom(new Xpp3Dom(src), tgt);
      } else {
        tgt = Xpp3Dom.mergeXpp3Dom(tgt, src);
      } 
      target.setConfiguration(tgt);
    } 
  }
  
  protected void mergePluginExecution(PluginExecution target, PluginExecution source, boolean sourceDominant, Map<Object, Object> context) {
    mergeConfigurationContainer((ConfigurationContainer)target, (ConfigurationContainer)source, sourceDominant, context);
    mergePluginExecution_Id(target, source, sourceDominant, context);
    mergePluginExecution_Phase(target, source, sourceDominant, context);
    mergePluginExecution_Goals(target, source, sourceDominant, context);
  }
  
  protected void mergePluginExecution_Id(PluginExecution target, PluginExecution source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getId();
    if (src != null)
      if (sourceDominant || target.getId() == null) {
        target.setId(src);
        target.setLocation("id", source.getLocation("id"));
      }  
  }
  
  protected void mergePluginExecution_Phase(PluginExecution target, PluginExecution source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getPhase();
    if (src != null)
      if (sourceDominant || target.getPhase() == null) {
        target.setPhase(src);
        target.setLocation("phase", source.getLocation("phase"));
      }  
  }
  
  protected void mergePluginExecution_Goals(PluginExecution target, PluginExecution source, boolean sourceDominant, Map<Object, Object> context) {
    List<String> src = source.getGoals();
    if (!src.isEmpty()) {
      List<String> tgt = target.getGoals();
      List<String> merged = new ArrayList<>(tgt.size() + src.size());
      merged.addAll(tgt);
      merged.addAll(src);
      target.setGoals(merged);
    } 
  }
  
  protected void mergeResource(Resource target, Resource source, boolean sourceDominant, Map<Object, Object> context) {
    mergeFileSet((FileSet)target, (FileSet)source, sourceDominant, context);
    mergeResource_TargetPath(target, source, sourceDominant, context);
    mergeResource_Filtering(target, source, sourceDominant, context);
    mergeResource_MergeId(target, source, sourceDominant, context);
  }
  
  protected void mergeResource_TargetPath(Resource target, Resource source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getTargetPath();
    if (src != null)
      if (sourceDominant || target.getTargetPath() == null) {
        target.setTargetPath(src);
        target.setLocation("targetPath", source.getLocation("targetPath"));
      }  
  }
  
  protected void mergeResource_Filtering(Resource target, Resource source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getFiltering();
    if (src != null)
      if (sourceDominant || target.getFiltering() == null) {
        target.setFiltering(src);
        target.setLocation("filtering", source.getLocation("filtering"));
      }  
  }
  
  protected void mergeResource_MergeId(Resource target, Resource source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getMergeId();
    if (src != null)
      if (sourceDominant || target.getMergeId() == null)
        target.setMergeId(src);  
  }
  
  protected void mergeFileSet(FileSet target, FileSet source, boolean sourceDominant, Map<Object, Object> context) {
    mergePatternSet((PatternSet)target, (PatternSet)source, sourceDominant, context);
    mergeFileSet_Directory(target, source, sourceDominant, context);
  }
  
  protected void mergeFileSet_Directory(FileSet target, FileSet source, boolean sourceDominant, Map<Object, Object> context) {
    String src = source.getDirectory();
    if (src != null)
      if (sourceDominant || target.getDirectory() == null) {
        target.setDirectory(src);
        target.setLocation("directory", source.getLocation("directory"));
      }  
  }
  
  protected void mergePatternSet(PatternSet target, PatternSet source, boolean sourceDominant, Map<Object, Object> context) {
    mergePatternSet_Includes(target, source, sourceDominant, context);
    mergePatternSet_Excludes(target, source, sourceDominant, context);
  }
  
  protected void mergePatternSet_Includes(PatternSet target, PatternSet source, boolean sourceDominant, Map<Object, Object> context) {
    List<String> src = source.getIncludes();
    if (!src.isEmpty()) {
      List<String> tgt = target.getIncludes();
      List<String> merged = new ArrayList<>(tgt.size() + src.size());
      merged.addAll(tgt);
      merged.addAll(src);
      target.setIncludes(merged);
    } 
  }
  
  protected void mergePatternSet_Excludes(PatternSet target, PatternSet source, boolean sourceDominant, Map<Object, Object> context) {
    List<String> src = source.getExcludes();
    if (!src.isEmpty()) {
      List<String> tgt = target.getExcludes();
      List<String> merged = new ArrayList<>(tgt.size() + src.size());
      merged.addAll(tgt);
      merged.addAll(src);
      target.setExcludes(merged);
    } 
  }
  
  protected void mergeProfile(Profile target, Profile source, boolean sourceDominant, Map<Object, Object> context) {
    mergeModelBase((ModelBase)target, (ModelBase)source, sourceDominant, context);
  }
  
  protected void mergeActivation(Activation target, Activation source, boolean sourceDominant, Map<Object, Object> context) {}
  
  protected Object getDependencyKey(Dependency dependency) {
    return dependency;
  }
  
  protected Object getPluginKey(Plugin plugin) {
    return plugin;
  }
  
  protected Object getPluginExecutionKey(PluginExecution pluginExecution) {
    return pluginExecution;
  }
  
  protected Object getReportPluginKey(ReportPlugin reportPlugin) {
    return reportPlugin;
  }
  
  protected Object getReportSetKey(ReportSet reportSet) {
    return reportSet;
  }
  
  protected Object getLicenseKey(License license) {
    return license;
  }
  
  protected Object getMailingListKey(MailingList mailingList) {
    return mailingList;
  }
  
  protected Object getDeveloperKey(Developer developer) {
    return developer;
  }
  
  protected Object getContributorKey(Contributor contributor) {
    return contributor;
  }
  
  protected Object getProfileKey(Profile profile) {
    return profile;
  }
  
  protected Object getRepositoryKey(Repository repository) {
    return getRepositoryBaseKey((RepositoryBase)repository);
  }
  
  protected Object getRepositoryBaseKey(RepositoryBase repositoryBase) {
    return repositoryBase;
  }
  
  protected Object getNotifierKey(Notifier notifier) {
    return notifier;
  }
  
  protected Object getResourceKey(Resource resource) {
    return resource;
  }
  
  protected Object getExtensionKey(Extension extension) {
    return extension;
  }
  
  protected Object getExclusionKey(Exclusion exclusion) {
    return exclusion;
  }
  
  private static interface KeyComputer<T> {
    Object key(T param1T);
  }
  
  private static interface Remapping<T> {
    T merge(T param1T1, T param1T2);
  }
  
  private final class DependencyKeyComputer implements KeyComputer<Dependency> {
    private DependencyKeyComputer() {}
    
    public Object key(Dependency dependency) {
      return ModelMerger.this.getDependencyKey(dependency);
    }
  }
  
  private class LicenseKeyComputer implements KeyComputer<License> {
    private LicenseKeyComputer() {}
    
    public Object key(License license) {
      return ModelMerger.this.getLicenseKey(license);
    }
  }
  
  private class MailingListKeyComputer implements KeyComputer<MailingList> {
    private MailingListKeyComputer() {}
    
    public Object key(MailingList mailingList) {
      return ModelMerger.this.getMailingListKey(mailingList);
    }
  }
  
  private class DeveloperKeyComputer implements KeyComputer<Developer> {
    private DeveloperKeyComputer() {}
    
    public Object key(Developer developer) {
      return ModelMerger.this.getDeveloperKey(developer);
    }
  }
  
  private class ContributorKeyComputer implements KeyComputer<Contributor> {
    private ContributorKeyComputer() {}
    
    public Object key(Contributor contributor) {
      return ModelMerger.this.getContributorKey(contributor);
    }
  }
  
  private class ProfileKeyComputer implements KeyComputer<Profile> {
    private ProfileKeyComputer() {}
    
    public Object key(Profile profile) {
      return ModelMerger.this.getProfileKey(profile);
    }
  }
  
  private class RepositoryKeyComputer implements KeyComputer<Repository> {
    private RepositoryKeyComputer() {}
    
    public Object key(Repository repository) {
      return ModelMerger.this.getRepositoryKey(repository);
    }
  }
  
  private class ReportPluginKeyComputer implements KeyComputer<ReportPlugin> {
    private ReportPluginKeyComputer() {}
    
    public Object key(ReportPlugin plugin) {
      return ModelMerger.this.getReportPluginKey(plugin);
    }
  }
  
  private class PluginKeyComputer implements KeyComputer<Plugin> {
    private PluginKeyComputer() {}
    
    public Object key(Plugin plugin) {
      return ModelMerger.this.getPluginKey(plugin);
    }
  }
  
  private class ReportSetKeyComputer implements KeyComputer<ReportSet> {
    private ReportSetKeyComputer() {}
    
    public Object key(ReportSet reportSet) {
      return ModelMerger.this.getReportSetKey(reportSet);
    }
  }
  
  private class NotifierKeyComputer implements KeyComputer<Notifier> {
    private NotifierKeyComputer() {}
    
    public Object key(Notifier notifier) {
      return ModelMerger.this.getNotifierKey(notifier);
    }
  }
  
  private class ExtensionKeyComputer implements KeyComputer<Extension> {
    private ExtensionKeyComputer() {}
    
    public Object key(Extension extension) {
      return ModelMerger.this.getExtensionKey(extension);
    }
  }
  
  private class ResourceKeyComputer implements KeyComputer<Resource> {
    private ResourceKeyComputer() {}
    
    public Object key(Resource resource) {
      return ModelMerger.this.getResourceKey(resource);
    }
  }
  
  private class ExecutionKeyComputer implements KeyComputer<PluginExecution> {
    private ExecutionKeyComputer() {}
    
    public Object key(PluginExecution pluginExecution) {
      return ModelMerger.this.getPluginExecutionKey(pluginExecution);
    }
  }
  
  private class ExclusionKeyComputer implements KeyComputer<Exclusion> {
    private ExclusionKeyComputer() {}
    
    public Object key(Exclusion exclusion) {
      return ModelMerger.this.getExclusionKey(exclusion);
    }
  }
  
  private static class SourceDominant<T> implements Remapping<T> {
    private final boolean sourceDominant;
    
    SourceDominant(boolean sourceDominant) {
      this.sourceDominant = sourceDominant;
    }
    
    public T merge(T u, T v) {
      return this.sourceDominant ? v : u;
    }
  }
  
  private static <T> List<T> merge(List<T> tgt, List<T> src, boolean sourceDominant, KeyComputer<T> computer) {
    return merge(tgt, src, computer, new SourceDominant<>(sourceDominant));
  }
  
  private static <T> List<T> merge(List<T> tgt, List<T> src, KeyComputer<T> computer, Remapping<T> remapping) {
    MergingList<T> list;
    if (src.isEmpty())
      return tgt; 
    if (tgt instanceof MergingList) {
      list = (MergingList<T>)tgt;
    } else {
      list = new MergingList<>(computer, src.size() + tgt.size());
      list.mergeAll(tgt, new SourceDominant<>(true));
    } 
    list.mergeAll(src, remapping);
    return list;
  }
  
  private static class MergingList<V> extends AbstractList<V> implements Serializable {
    private final ModelMerger.KeyComputer<V> keyComputer;
    
    private Map<Object, V> map;
    
    private List<V> list;
    
    MergingList(ModelMerger.KeyComputer<V> keyComputer, int initialCapacity) {
      this.map = new LinkedHashMap<>(initialCapacity);
      this.keyComputer = keyComputer;
    }
    
    Object writeReplace() throws ObjectStreamException {
      return new ArrayList<>(this);
    }
    
    public Iterator<V> iterator() {
      if (this.map != null)
        return this.map.values().iterator(); 
      return this.list.iterator();
    }
    
    void mergeAll(Collection<V> vs, ModelMerger.Remapping<V> remapping) {
      if (this.map == null) {
        this.map = new LinkedHashMap<>(this.list.size() + vs.size());
        for (V v : this.list)
          this.map.put(this.keyComputer.key(v), v); 
        this.list = null;
      } 
      if (vs instanceof MergingList && ((MergingList)vs).map != null) {
        for (Map.Entry<Object, V> e : ((MergingList)vs).map.entrySet()) {
          Object key = e.getKey();
          V oldValue = this.map.get(key);
          V newValue = (oldValue == null) ? e.getValue() : remapping.merge(oldValue, e.getValue());
          if (newValue == null) {
            remove(key);
            continue;
          } 
          if (newValue != oldValue)
            this.map.put(key, newValue); 
        } 
      } else {
        for (V v : vs) {
          Object key = this.keyComputer.key(v);
          V oldValue = this.map.get(key);
          V newValue = (oldValue == null) ? v : remapping.merge(oldValue, v);
          if (newValue == null) {
            remove(key);
            continue;
          } 
          this.map.put(key, newValue);
        } 
      } 
    }
    
    public boolean contains(Object o) {
      if (this.map != null)
        return this.map.containsValue(o); 
      return this.list.contains(o);
    }
    
    private List<V> asList() {
      if (this.list == null) {
        this.list = new ArrayList<>(this.map.values());
        this.map = null;
      } 
      return this.list;
    }
    
    public void add(int index, V element) {
      asList().add(index, element);
    }
    
    public V remove(int index) {
      return asList().remove(index);
    }
    
    public V get(int index) {
      return asList().get(index);
    }
    
    public int size() {
      if (this.map != null)
        return this.map.size(); 
      return this.list.size();
    }
  }
}
