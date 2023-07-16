package org.eclipse.aether.util.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.eclipse.aether.graph.DependencyFilter;

public final class DependencyFilterUtils {
  public static DependencyFilter notFilter(DependencyFilter filter) {
    return new NotDependencyFilter(filter);
  }
  
  public static DependencyFilter andFilter(DependencyFilter... filters) {
    if (filters != null && filters.length == 1)
      return filters[0]; 
    return new AndDependencyFilter(filters);
  }
  
  public static DependencyFilter andFilter(Collection<DependencyFilter> filters) {
    if (filters != null && filters.size() == 1)
      return filters.iterator().next(); 
    return new AndDependencyFilter(filters);
  }
  
  public static DependencyFilter orFilter(DependencyFilter... filters) {
    if (filters != null && filters.length == 1)
      return filters[0]; 
    return new OrDependencyFilter(filters);
  }
  
  public static DependencyFilter orFilter(Collection<DependencyFilter> filters) {
    if (filters != null && filters.size() == 1)
      return filters.iterator().next(); 
    return new OrDependencyFilter(filters);
  }
  
  public static DependencyFilter classpathFilter(String... classpathTypes) {
    return classpathFilter((classpathTypes != null) ? Arrays.<String>asList(classpathTypes) : null);
  }
  
  public static DependencyFilter classpathFilter(Collection<String> classpathTypes) {
    Collection<String> types = new HashSet<>();
    if (classpathTypes != null)
      for (String classpathType : classpathTypes) {
        String[] tokens = classpathType.split("[+,]");
        for (String token : tokens) {
          token = token.trim();
          if (token.length() > 0)
            types.add(token); 
        } 
      }  
    Collection<String> included = new HashSet<>();
    for (String type : types) {
      if ("compile".equals(type)) {
        Collections.addAll(included, new String[] { "compile", "provided", "system" });
        continue;
      } 
      if ("runtime".equals(type)) {
        Collections.addAll(included, new String[] { "compile", "runtime" });
        continue;
      } 
      if ("test".equals(type)) {
        Collections.addAll(included, new String[] { "compile", "provided", "system", "runtime", "test" });
        continue;
      } 
      included.add(type);
    } 
    Collection<String> excluded = new HashSet<>();
    Collections.addAll(excluded, new String[] { "compile", "provided", "system", "runtime", "test" });
    excluded.removeAll(included);
    return new ScopeDependencyFilter(null, excluded);
  }
}
