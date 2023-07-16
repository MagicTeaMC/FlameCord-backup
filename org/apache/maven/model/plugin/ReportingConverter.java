package org.apache.maven.model.plugin;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;

public interface ReportingConverter {
  void convertReporting(Model paramModel, ModelBuildingRequest paramModelBuildingRequest, ModelProblemCollector paramModelProblemCollector);
}
