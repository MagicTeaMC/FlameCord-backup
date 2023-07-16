package org.apache.maven.model.interpolation;

import java.util.HashSet;
import java.util.Set;
import org.apache.maven.model.path.UrlNormalizer;
import org.codehaus.plexus.interpolation.InterpolationPostProcessor;

class UrlNormalizingPostProcessor implements InterpolationPostProcessor {
  private static final Set<String> URL_EXPRESSIONS;
  
  private UrlNormalizer normalizer;
  
  static {
    Set<String> expressions = new HashSet<>();
    expressions.add("project.url");
    expressions.add("project.scm.url");
    expressions.add("project.scm.connection");
    expressions.add("project.scm.developerConnection");
    expressions.add("project.distributionManagement.site.url");
    URL_EXPRESSIONS = expressions;
  }
  
  UrlNormalizingPostProcessor(UrlNormalizer normalizer) {
    this.normalizer = normalizer;
  }
  
  public Object execute(String expression, Object value) {
    if (value != null && URL_EXPRESSIONS.contains(expression))
      return this.normalizer.normalize(value.toString()); 
    return null;
  }
}
