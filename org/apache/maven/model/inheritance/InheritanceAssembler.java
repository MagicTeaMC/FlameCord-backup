package org.apache.maven.model.inheritance;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;

public interface InheritanceAssembler {
  void assembleModelInheritance(Model paramModel1, Model paramModel2, ModelBuildingRequest paramModelBuildingRequest, ModelProblemCollector paramModelProblemCollector);
}
