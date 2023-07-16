package org.eclipse.aether.artifact;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DefaultArtifact extends AbstractArtifact {
  private static final Pattern COORDINATE_PATTERN = Pattern.compile("([^: ]+):([^: ]+)(:([^: ]*)(:([^: ]+))?)?:([^: ]+)");
  
  private final String groupId;
  
  private final String artifactId;
  
  private final String version;
  
  private final String classifier;
  
  private final String extension;
  
  private final File file;
  
  private final Map<String, String> properties;
  
  public DefaultArtifact(String coords) {
    this(coords, Collections.emptyMap());
  }
  
  public DefaultArtifact(String coords, Map<String, String> properties) {
    Matcher m = COORDINATE_PATTERN.matcher(coords);
    if (!m.matches())
      throw new IllegalArgumentException("Bad artifact coordinates " + coords + ", expected format is <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>"); 
    this.groupId = m.group(1);
    this.artifactId = m.group(2);
    this.extension = get(m.group(4), "jar");
    this.classifier = get(m.group(6), "");
    this.version = m.group(7);
    this.file = null;
    this.properties = copyProperties(properties);
  }
  
  private static String get(String value, String defaultValue) {
    return (value == null || value.length() <= 0) ? defaultValue : value;
  }
  
  public DefaultArtifact(String groupId, String artifactId, String extension, String version) {
    this(groupId, artifactId, "", extension, version);
  }
  
  public DefaultArtifact(String groupId, String artifactId, String classifier, String extension, String version) {
    this(groupId, artifactId, classifier, extension, version, (Map<String, String>)null, (File)null);
  }
  
  public DefaultArtifact(String groupId, String artifactId, String classifier, String extension, String version, ArtifactType type) {
    this(groupId, artifactId, classifier, extension, version, (Map<String, String>)null, type);
  }
  
  public DefaultArtifact(String groupId, String artifactId, String classifier, String extension, String version, Map<String, String> properties, ArtifactType type) {
    this.groupId = emptify(groupId);
    this.artifactId = emptify(artifactId);
    if (classifier != null || type == null) {
      this.classifier = emptify(classifier);
    } else {
      this.classifier = emptify(type.getClassifier());
    } 
    if (extension != null || type == null) {
      this.extension = emptify(extension);
    } else {
      this.extension = emptify(type.getExtension());
    } 
    this.version = emptify(version);
    this.file = null;
    this.properties = merge(properties, (type != null) ? type.getProperties() : null);
  }
  
  private static Map<String, String> merge(Map<String, String> dominant, Map<String, String> recessive) {
    Map<String, String> properties;
    if ((dominant == null || dominant.isEmpty()) && (recessive == null || recessive.isEmpty())) {
      properties = Collections.emptyMap();
    } else {
      properties = new HashMap<>();
      if (recessive != null)
        properties.putAll(recessive); 
      if (dominant != null)
        properties.putAll(dominant); 
      properties = Collections.unmodifiableMap(properties);
    } 
    return properties;
  }
  
  public DefaultArtifact(String groupId, String artifactId, String classifier, String extension, String version, Map<String, String> properties, File file) {
    this.groupId = emptify(groupId);
    this.artifactId = emptify(artifactId);
    this.classifier = emptify(classifier);
    this.extension = emptify(extension);
    this.version = emptify(version);
    this.file = file;
    this.properties = copyProperties(properties);
  }
  
  DefaultArtifact(String groupId, String artifactId, String classifier, String extension, String version, File file, Map<String, String> properties) {
    this.groupId = emptify(groupId);
    this.artifactId = emptify(artifactId);
    this.classifier = emptify(classifier);
    this.extension = emptify(extension);
    this.version = emptify(version);
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
  
  public String getClassifier() {
    return this.classifier;
  }
  
  public String getExtension() {
    return this.extension;
  }
  
  public File getFile() {
    return this.file;
  }
  
  public Map<String, String> getProperties() {
    return this.properties;
  }
}
