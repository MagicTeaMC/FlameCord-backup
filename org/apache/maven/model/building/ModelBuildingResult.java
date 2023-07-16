package org.apache.maven.model.building;

import java.util.List;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;

public interface ModelBuildingResult {
  List<String> getModelIds();
  
  Model getEffectiveModel();
  
  Model getRawModel();
  
  Model getRawModel(String paramString);
  
  List<Profile> getActivePomProfiles(String paramString);
  
  List<Profile> getActiveExternalProfiles();
  
  List<ModelProblem> getProblems();
}
