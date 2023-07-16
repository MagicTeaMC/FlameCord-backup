package org.apache.maven.model.composition;

import java.util.List;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;

public interface DependencyManagementImporter {
  void importManagement(Model paramModel, List<? extends DependencyManagement> paramList, ModelBuildingRequest paramModelBuildingRequest, ModelProblemCollector paramModelProblemCollector);
}
