package org.apache.maven.model.interpolation;

import java.util.Properties;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.building.ModelBuildingRequest;

@Named
@Singleton
public class DefaultModelVersionProcessor implements ModelVersionProcessor {
  private static final String SHA1_PROPERTY = "sha1";
  
  private static final String CHANGELIST_PROPERTY = "changelist";
  
  private static final String REVISION_PROPERTY = "revision";
  
  public boolean isValidProperty(String property) {
    return ("revision".equals(property) || "changelist".equals(property) || "sha1"
      .equals(property));
  }
  
  public void overwriteModelProperties(Properties modelProperties, ModelBuildingRequest request) {
    if (request.getSystemProperties().containsKey("revision"))
      modelProperties.put("revision", request.getSystemProperties().get("revision")); 
    if (request.getSystemProperties().containsKey("changelist"))
      modelProperties.put("changelist", request.getSystemProperties().get("changelist")); 
    if (request.getSystemProperties().containsKey("sha1"))
      modelProperties.put("sha1", request.getSystemProperties().get("sha1")); 
  }
}
