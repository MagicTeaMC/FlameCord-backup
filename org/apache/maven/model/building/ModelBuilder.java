package org.apache.maven.model.building;

import java.io.File;
import org.apache.maven.model.Model;

public interface ModelBuilder {
  ModelBuildingResult build(ModelBuildingRequest paramModelBuildingRequest) throws ModelBuildingException;
  
  ModelBuildingResult build(ModelBuildingRequest paramModelBuildingRequest, ModelBuildingResult paramModelBuildingResult) throws ModelBuildingException;
  
  Result<? extends Model> buildRawModel(File paramFile, int paramInt, boolean paramBoolean);
}
