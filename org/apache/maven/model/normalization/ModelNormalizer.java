package org.apache.maven.model.normalization;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;

public interface ModelNormalizer {
  void mergeDuplicates(Model paramModel, ModelBuildingRequest paramModelBuildingRequest, ModelProblemCollector paramModelProblemCollector);
  
  void injectDefaultValues(Model paramModel, ModelBuildingRequest paramModelBuildingRequest, ModelProblemCollector paramModelProblemCollector);
}
