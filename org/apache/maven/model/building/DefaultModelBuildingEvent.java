package org.apache.maven.model.building;

import org.apache.maven.model.Model;

class DefaultModelBuildingEvent implements ModelBuildingEvent {
  private final Model model;
  
  private final ModelBuildingRequest request;
  
  private final ModelProblemCollector problems;
  
  DefaultModelBuildingEvent(Model model, ModelBuildingRequest request, ModelProblemCollector problems) {
    this.model = model;
    this.request = request;
    this.problems = problems;
  }
  
  public Model getModel() {
    return this.model;
  }
  
  public ModelBuildingRequest getRequest() {
    return this.request;
  }
  
  public ModelProblemCollector getProblems() {
    return this.problems;
  }
}
