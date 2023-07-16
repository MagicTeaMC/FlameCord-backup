package org.eclipse.aether.artifact;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractArtifact implements Artifact {
  private static final String SNAPSHOT = "SNAPSHOT";
  
  private static final Pattern SNAPSHOT_TIMESTAMP = Pattern.compile("^(.*-)?([0-9]{8}\\.[0-9]{6}-[0-9]+)$");
  
  public boolean isSnapshot() {
    return isSnapshot(getVersion());
  }
  
  private static boolean isSnapshot(String version) {
    return (version.endsWith("SNAPSHOT") || SNAPSHOT_TIMESTAMP.matcher(version).matches());
  }
  
  public String getBaseVersion() {
    return toBaseVersion(getVersion());
  }
  
  private static String toBaseVersion(String version) {
    String baseVersion;
    if (version == null) {
      baseVersion = version;
    } else if (version.startsWith("[") || version.startsWith("(")) {
      baseVersion = version;
    } else {
      Matcher m = SNAPSHOT_TIMESTAMP.matcher(version);
      if (m.matches()) {
        if (m.group(1) != null) {
          baseVersion = m.group(1) + "SNAPSHOT";
        } else {
          baseVersion = "SNAPSHOT";
        } 
      } else {
        baseVersion = version;
      } 
    } 
    return baseVersion;
  }
  
  private Artifact newInstance(String version, Map<String, String> properties, File file) {
    return new DefaultArtifact(getGroupId(), getArtifactId(), getClassifier(), getExtension(), version, file, properties);
  }
  
  public Artifact setVersion(String version) {
    String current = getVersion();
    if (current.equals(version) || (version == null && current.length() <= 0))
      return this; 
    return newInstance(version, getProperties(), getFile());
  }
  
  public Artifact setFile(File file) {
    File current = getFile();
    if (Objects.equals(current, file))
      return this; 
    return newInstance(getVersion(), getProperties(), file);
  }
  
  public Artifact setProperties(Map<String, String> properties) {
    Map<String, String> current = getProperties();
    if (current.equals(properties) || (properties == null && current.isEmpty()))
      return this; 
    return newInstance(getVersion(), copyProperties(properties), getFile());
  }
  
  public String getProperty(String key, String defaultValue) {
    String value = getProperties().get(key);
    return (value != null) ? value : defaultValue;
  }
  
  protected static Map<String, String> copyProperties(Map<String, String> properties) {
    if (properties != null && !properties.isEmpty())
      return Collections.unmodifiableMap(new HashMap<>(properties)); 
    return Collections.emptyMap();
  }
  
  public String toString() {
    StringBuilder buffer = new StringBuilder(128);
    buffer.append(getGroupId());
    buffer.append(':').append(getArtifactId());
    buffer.append(':').append(getExtension());
    if (getClassifier().length() > 0)
      buffer.append(':').append(getClassifier()); 
    buffer.append(':').append(getVersion());
    return buffer.toString();
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof Artifact))
      return false; 
    Artifact that = (Artifact)obj;
    return (Objects.equals(getArtifactId(), that.getArtifactId()) && 
      Objects.equals(getGroupId(), that.getGroupId()) && 
      Objects.equals(getVersion(), that.getVersion()) && 
      Objects.equals(getExtension(), that.getExtension()) && 
      Objects.equals(getClassifier(), that.getClassifier()) && 
      Objects.equals(getFile(), that.getFile()) && 
      Objects.equals(getProperties(), that.getProperties()));
  }
  
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + getGroupId().hashCode();
    hash = hash * 31 + getArtifactId().hashCode();
    hash = hash * 31 + getExtension().hashCode();
    hash = hash * 31 + getClassifier().hashCode();
    hash = hash * 31 + getVersion().hashCode();
    hash = hash * 31 + hash(getFile());
    return hash;
  }
  
  private static int hash(Object obj) {
    return (obj != null) ? obj.hashCode() : 0;
  }
}
