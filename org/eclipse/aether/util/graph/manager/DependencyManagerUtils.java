package org.eclipse.aether.util.graph.manager;

import java.util.Collection;
import java.util.Map;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.Exclusion;

public final class DependencyManagerUtils {
  public static final String CONFIG_PROP_VERBOSE = "aether.dependencyManager.verbose";
  
  public static final String NODE_DATA_PREMANAGED_VERSION = "premanaged.version";
  
  public static final String NODE_DATA_PREMANAGED_SCOPE = "premanaged.scope";
  
  public static final String NODE_DATA_PREMANAGED_OPTIONAL = "premanaged.optional";
  
  public static final String NODE_DATA_PREMANAGED_EXCLUSIONS = "premanaged.exclusions";
  
  public static final String NODE_DATA_PREMANAGED_PROPERTIES = "premanaged.properties";
  
  public static String getPremanagedVersion(DependencyNode node) {
    if ((node.getManagedBits() & 0x1) == 0)
      return null; 
    return cast(node.getData().get("premanaged.version"), String.class);
  }
  
  public static String getPremanagedScope(DependencyNode node) {
    if ((node.getManagedBits() & 0x2) == 0)
      return null; 
    return cast(node.getData().get("premanaged.scope"), String.class);
  }
  
  public static Boolean getPremanagedOptional(DependencyNode node) {
    if ((node.getManagedBits() & 0x4) == 0)
      return null; 
    return cast(node.getData().get("premanaged.optional"), Boolean.class);
  }
  
  public static Collection<Exclusion> getPremanagedExclusions(DependencyNode node) {
    if ((node.getManagedBits() & 0x10) == 0)
      return null; 
    return cast(node.getData().get("premanaged.exclusions"), (Class)Collection.class);
  }
  
  public static Map<String, String> getPremanagedProperties(DependencyNode node) {
    if ((node.getManagedBits() & 0x8) == 0)
      return null; 
    return cast(node.getData().get("premanaged.properties"), (Class)Map.class);
  }
  
  private static <T> T cast(Object obj, Class<T> type) {
    return type.isInstance(obj) ? type.cast(obj) : null;
  }
}
