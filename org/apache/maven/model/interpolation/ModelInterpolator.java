package org.apache.maven.model.interpolation;

import java.io.File;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;

public interface ModelInterpolator {
  Model interpolateModel(Model paramModel, File paramFile, ModelBuildingRequest paramModelBuildingRequest, ModelProblemCollector paramModelProblemCollector);
}
