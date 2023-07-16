package org.apache.maven.model.path;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public class DefaultUrlNormalizer implements UrlNormalizer {
  public String normalize(String url) {
    String result = url;
    if (result != null)
      while (true) {
        int idx = result.indexOf("/../");
        if (idx < 0)
          break; 
        if (idx == 0) {
          result = result.substring(3);
          continue;
        } 
        int parent = idx - 1;
        while (parent >= 0 && result.charAt(parent) == '/')
          parent--; 
        parent = result.lastIndexOf('/', parent);
        if (parent < 0) {
          result = result.substring(idx + 4);
          continue;
        } 
        result = result.substring(0, parent) + result.substring(idx + 3);
      }  
    return result;
  }
}
