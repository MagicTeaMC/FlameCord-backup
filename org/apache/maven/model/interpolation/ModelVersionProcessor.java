package org.apache.maven.model.interpolation;

import java.util.Properties;
import org.apache.maven.model.building.ModelBuildingRequest;

public interface ModelVersionProcessor {
  boolean isValidProperty(String paramString);
  
  void overwriteModelProperties(Properties paramProperties, ModelBuildingRequest paramModelBuildingRequest);
}
