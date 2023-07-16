package org.eclipse.aether.metadata;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractMetadata implements Metadata {
  private Metadata newInstance(Map<String, String> properties, File file) {
    return new DefaultMetadata(getGroupId(), getArtifactId(), getVersion(), getType(), getNature(), file, properties);
  }
  
  public Metadata setFile(File file) {
    File current = getFile();
    if (Objects.equals(current, file))
      return this; 
    return newInstance(getProperties(), file);
  }
  
  public Metadata setProperties(Map<String, String> properties) {
    Map<String, String> current = getProperties();
    if (current.equals(properties) || (properties == null && current.isEmpty()))
      return this; 
    return newInstance(copyProperties(properties), getFile());
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
    if (getGroupId().length() > 0)
      buffer.append(getGroupId()); 
    if (getArtifactId().length() > 0)
      buffer.append(':').append(getArtifactId()); 
    if (getVersion().length() > 0)
      buffer.append(':').append(getVersion()); 
    buffer.append('/').append(getType());
    return buffer.toString();
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof Metadata))
      return false; 
    Metadata that = (Metadata)obj;
    return (Objects.equals(getArtifactId(), that.getArtifactId()) && 
      Objects.equals(getGroupId(), that.getGroupId()) && 
      Objects.equals(getVersion(), that.getVersion()) && 
      Objects.equals(getType(), that.getType()) && 
      Objects.equals(getNature(), that.getNature()) && 
      Objects.equals(getFile(), that.getFile()) && 
      Objects.equals(getProperties(), that.getProperties()));
  }
  
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + getGroupId().hashCode();
    hash = hash * 31 + getArtifactId().hashCode();
    hash = hash * 31 + getType().hashCode();
    hash = hash * 31 + getNature().hashCode();
    hash = hash * 31 + getVersion().hashCode();
    hash = hash * 31 + hash(getFile());
    return hash;
  }
  
  private static int hash(Object obj) {
    return (obj != null) ? obj.hashCode() : 0;
  }
}
