package org.apache.maven.repository.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.License;
import org.apache.maven.model.Model;
import org.apache.maven.model.Prerequisites;
import org.apache.maven.model.Repository;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.ArtifactType;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.artifact.DefaultArtifactType;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.Exclusion;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;

public class ArtifactDescriptorReaderDelegate {
  public void populateResult(RepositorySystemSession session, ArtifactDescriptorResult result, Model model) {
    ArtifactTypeRegistry stereotypes = session.getArtifactTypeRegistry();
    for (Repository r : model.getRepositories())
      result.addRepository(ArtifactDescriptorUtils.toRemoteRepository(r)); 
    for (Dependency dependency : model.getDependencies())
      result.addDependency(convert(dependency, stereotypes)); 
    DependencyManagement mgmt = model.getDependencyManagement();
    if (mgmt != null)
      for (Dependency dependency : mgmt.getDependencies())
        result.addManagedDependency(convert(dependency, stereotypes));  
    Map<String, Object> properties = new LinkedHashMap<>();
    Prerequisites prerequisites = model.getPrerequisites();
    if (prerequisites != null)
      properties.put("prerequisites.maven", prerequisites.getMaven()); 
    List<License> licenses = model.getLicenses();
    properties.put("license.count", Integer.valueOf(licenses.size()));
    for (int i = 0; i < licenses.size(); i++) {
      License license = licenses.get(i);
      properties.put("license." + i + ".name", license.getName());
      properties.put("license." + i + ".url", license.getUrl());
      properties.put("license." + i + ".comments", license.getComments());
      properties.put("license." + i + ".distribution", license.getDistribution());
    } 
    result.setProperties(properties);
    setArtifactProperties(result, model);
  }
  
  private Dependency convert(Dependency dependency, ArtifactTypeRegistry stereotypes) {
    DefaultArtifactType defaultArtifactType;
    ArtifactType stereotype = stereotypes.get(dependency.getType());
    if (stereotype == null)
      defaultArtifactType = new DefaultArtifactType(dependency.getType()); 
    boolean system = (dependency.getSystemPath() != null && dependency.getSystemPath().length() > 0);
    Map<String, String> props = null;
    if (system)
      props = Collections.singletonMap("localPath", dependency.getSystemPath()); 
    DefaultArtifact defaultArtifact = new DefaultArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getClassifier(), null, dependency.getVersion(), props, (ArtifactType)defaultArtifactType);
    List<Exclusion> exclusions = new ArrayList<>(dependency.getExclusions().size());
    for (Exclusion exclusion : dependency.getExclusions())
      exclusions.add(convert(exclusion)); 
    Dependency result = new Dependency((Artifact)defaultArtifact, dependency.getScope(), (dependency.getOptional() != null) ? Boolean.valueOf(dependency.isOptional()) : null, exclusions);
    return result;
  }
  
  private Exclusion convert(Exclusion exclusion) {
    return new Exclusion(exclusion.getGroupId(), exclusion.getArtifactId(), "*", "*");
  }
  
  private void setArtifactProperties(ArtifactDescriptorResult result, Model model) {
    String downloadUrl = null;
    DistributionManagement distMgmt = model.getDistributionManagement();
    if (distMgmt != null)
      downloadUrl = distMgmt.getDownloadUrl(); 
    if (downloadUrl != null && downloadUrl.length() > 0) {
      Artifact artifact = result.getArtifact();
      Map<String, String> props = new HashMap<>(artifact.getProperties());
      props.put("downloadUrl", downloadUrl);
      result.setArtifact(artifact.setProperties(props));
    } 
  }
}
