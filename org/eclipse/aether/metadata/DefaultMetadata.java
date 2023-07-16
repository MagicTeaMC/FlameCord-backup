package org.eclipse.aether.metadata;

import java.io.File;
import java.util.Map;
import java.util.Objects;

public final class DefaultMetadata extends AbstractMetadata {
  private final String groupId;
  
  private final String artifactId;
  
  private final String version;
  
  private final String type;
  
  private final Metadata.Nature nature;
  
  private final File file;
  
  private final Map<String, String> properties;
  
  public DefaultMetadata(String type, Metadata.Nature nature) {
    this("", "", "", type, nature, (Map<String, String>)null, (File)null);
  }
  
  public DefaultMetadata(String groupId, String type, Metadata.Nature nature) {
    this(groupId, "", "", type, nature, (Map<String, String>)null, (File)null);
  }
  
  public DefaultMetadata(String groupId, String artifactId, String type, Metadata.Nature nature) {
    this(groupId, artifactId, "", type, nature, (Map<String, String>)null, (File)null);
  }
  
  public DefaultMetadata(String groupId, String artifactId, String version, String type, Metadata.Nature nature) {
    this(groupId, artifactId, version, type, nature, (Map<String, String>)null, (File)null);
  }
  
  public DefaultMetadata(String groupId, String artifactId, String version, String type, Metadata.Nature nature, File file) {
    this(groupId, artifactId, version, type, nature, (Map<String, String>)null, file);
  }
  
  public DefaultMetadata(String groupId, String artifactId, String version, String type, Metadata.Nature nature, Map<String, String> properties, File file) {
    this.groupId = emptify(groupId);
    this.artifactId = emptify(artifactId);
    this.version = emptify(version);
    this.type = emptify(type);
    this.nature = Objects.<Metadata.Nature>requireNonNull(nature, "metadata nature cannot be null");
    this.file = file;
    this.properties = copyProperties(properties);
  }
  
  DefaultMetadata(String groupId, String artifactId, String version, String type, Metadata.Nature nature, File file, Map<String, String> properties) {
    this.groupId = emptify(groupId);
    this.artifactId = emptify(artifactId);
    this.version = emptify(version);
    this.type = emptify(type);
    this.nature = nature;
    this.file = file;
    this.properties = properties;
  }
  
  private static String emptify(String str) {
    return (str == null) ? "" : str;
  }
  
  public String getGroupId() {
    return this.groupId;
  }
  
  public String getArtifactId() {
    return this.artifactId;
  }
  
  public String getVersion() {
    return this.version;
  }
  
  public String getType() {
    return this.type;
  }
  
  public Metadata.Nature getNature() {
    return this.nature;
  }
  
  public File getFile() {
    return this.file;
  }
  
  public Map<String, String> getProperties() {
    return this.properties;
  }
}
