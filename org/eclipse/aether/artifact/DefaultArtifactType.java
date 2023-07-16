package org.eclipse.aether.artifact;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class DefaultArtifactType implements ArtifactType {
  private final String id;
  
  private final String extension;
  
  private final String classifier;
  
  private final Map<String, String> properties;
  
  public DefaultArtifactType(String id) {
    this(id, id, "", "none", false, false);
  }
  
  public DefaultArtifactType(String id, String extension, String classifier, String language) {
    this(id, extension, classifier, language, true, false);
  }
  
  public DefaultArtifactType(String id, String extension, String classifier, String language, boolean constitutesBuildPath, boolean includesDependencies) {
    this.id = Objects.<String>requireNonNull(id, "type id cannot be null");
    if (id.length() == 0)
      throw new IllegalArgumentException("type id cannot be empty"); 
    this.extension = emptify(extension);
    this.classifier = emptify(classifier);
    Map<String, String> props = new HashMap<>();
    props.put("type", id);
    props.put("language", (language != null && language.length() > 0) ? language : "none");
    props.put("includesDependencies", Boolean.toString(includesDependencies));
    props.put("constitutesBuildPath", Boolean.toString(constitutesBuildPath));
    this.properties = Collections.unmodifiableMap(props);
  }
  
  public DefaultArtifactType(String id, String extension, String classifier, Map<String, String> properties) {
    this.id = Objects.<String>requireNonNull(id, "type id cannot be null");
    if (id.length() == 0)
      throw new IllegalArgumentException("type id cannot be empty"); 
    this.extension = emptify(extension);
    this.classifier = emptify(classifier);
    this.properties = AbstractArtifact.copyProperties(properties);
  }
  
  private static String emptify(String str) {
    return (str == null) ? "" : str;
  }
  
  public String getId() {
    return this.id;
  }
  
  public String getExtension() {
    return this.extension;
  }
  
  public String getClassifier() {
    return this.classifier;
  }
  
  public Map<String, String> getProperties() {
    return this.properties;
  }
}
