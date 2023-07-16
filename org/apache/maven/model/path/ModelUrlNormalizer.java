package org.apache.maven.model.path;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;

public interface ModelUrlNormalizer {
  void normalize(Model paramModel, ModelBuildingRequest paramModelBuildingRequest);
}
