package org.apache.maven.model.building;

import org.apache.maven.model.Model;

public interface ModelBuildingEvent {
  Model getModel();
  
  ModelBuildingRequest getRequest();
  
  ModelProblemCollector getProblems();
}
