package org.apache.maven.model.building;

import java.util.List;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;

class ModelData {
  private final ModelSource source;
  
  private Model model;
  
  private Model rawModel;
  
  private List<Profile> activeProfiles;
  
  private String groupId;
  
  private String artifactId;
  
  private String version;
  
  ModelData(ModelSource source, Model model) {
    this.source = source;
    this.model = model;
  }
  
  ModelData(ModelSource source, Model model, String groupId, String artifactId, String version) {
    this.source = source;
    this.model = model;
    setGroupId(groupId);
    setArtifactId(artifactId);
    setVersion(version);
  }
  
  public ModelSource getSource() {
    return this.source;
  }
  
  public Model getModel() {
    return this.model;
  }
  
  public void setModel(Model model) {
    this.model = model;
  }
  
  public Model getRawModel() {
    return this.rawModel;
  }
  
  public void setRawModel(Model rawModel) {
    this.rawModel = rawModel;
  }
  
  public List<Profile> getActiveProfiles() {
    return this.activeProfiles;
  }
  
  public void setActiveProfiles(List<Profile> activeProfiles) {
    this.activeProfiles = activeProfiles;
  }
  
  public String getGroupId() {
    return (this.groupId != null) ? this.groupId : "";
  }
  
  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }
  
  public String getArtifactId() {
    return (this.artifactId != null) ? this.artifactId : "";
  }
  
  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }
  
  public String getVersion() {
    return (this.version != null) ? this.version : "";
  }
  
  public void setVersion(String version) {
    this.version = version;
  }
  
  public String getId() {
    StringBuilder buffer = new StringBuilder(128);
    buffer.append(getGroupId()).append(':').append(getArtifactId()).append(':').append(getVersion());
    return buffer.toString();
  }
  
  public String toString() {
    return String.valueOf(this.model);
  }
}
