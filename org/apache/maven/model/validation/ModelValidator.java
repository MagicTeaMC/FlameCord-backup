package org.apache.maven.model.validation;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;

public interface ModelValidator {
  void validateRawModel(Model paramModel, ModelBuildingRequest paramModelBuildingRequest, ModelProblemCollector paramModelProblemCollector);
  
  void validateEffectiveModel(Model paramModel, ModelBuildingRequest paramModelBuildingRequest, ModelProblemCollector paramModelProblemCollector);
}
