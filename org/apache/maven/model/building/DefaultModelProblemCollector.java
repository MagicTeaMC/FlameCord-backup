package org.apache.maven.model.building;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.ModelParseException;

class DefaultModelProblemCollector implements ModelProblemCollectorExt {
  private final ModelBuildingResult result;
  
  private List<ModelProblem> problems;
  
  private String source;
  
  private Model sourceModel;
  
  private Model rootModel;
  
  private Set<ModelProblem.Severity> severities = EnumSet.noneOf(ModelProblem.Severity.class);
  
  DefaultModelProblemCollector(ModelBuildingResult result) {
    this.result = result;
    this.problems = result.getProblems();
    for (ModelProblem problem : this.problems)
      this.severities.add(problem.getSeverity()); 
  }
  
  public boolean hasFatalErrors() {
    return this.severities.contains(ModelProblem.Severity.FATAL);
  }
  
  public boolean hasErrors() {
    return (this.severities.contains(ModelProblem.Severity.ERROR) || this.severities.contains(ModelProblem.Severity.FATAL));
  }
  
  public List<ModelProblem> getProblems() {
    return this.problems;
  }
  
  public void setSource(String source) {
    this.source = source;
    this.sourceModel = null;
  }
  
  public void setSource(Model source) {
    this.sourceModel = source;
    this.source = null;
    if (this.rootModel == null)
      this.rootModel = source; 
  }
  
  private String getSource() {
    if (this.source == null && this.sourceModel != null)
      this.source = ModelProblemUtils.toPath(this.sourceModel); 
    return this.source;
  }
  
  private String getModelId() {
    return ModelProblemUtils.toId(this.sourceModel);
  }
  
  public void setRootModel(Model rootModel) {
    this.rootModel = rootModel;
  }
  
  public Model getRootModel() {
    return this.rootModel;
  }
  
  public String getRootModelId() {
    return ModelProblemUtils.toId(this.rootModel);
  }
  
  public void add(ModelProblem problem) {
    this.problems.add(problem);
    this.severities.add(problem.getSeverity());
  }
  
  public void addAll(List<ModelProblem> problems) {
    this.problems.addAll(problems);
    for (ModelProblem problem : problems)
      this.severities.add(problem.getSeverity()); 
  }
  
  public void add(ModelProblemCollectorRequest req) {
    int line = -1;
    int column = -1;
    String source = null;
    String modelId = null;
    if (req.getLocation() != null) {
      line = req.getLocation().getLineNumber();
      column = req.getLocation().getColumnNumber();
      if (req.getLocation().getSource() != null) {
        modelId = req.getLocation().getSource().getModelId();
        source = req.getLocation().getSource().getLocation();
      } 
    } 
    if (modelId == null) {
      modelId = getModelId();
      source = getSource();
    } 
    if (line <= 0 && column <= 0 && req.getException() instanceof ModelParseException) {
      ModelParseException e = (ModelParseException)req.getException();
      line = e.getLineNumber();
      column = e.getColumnNumber();
    } 
    ModelProblem problem = new DefaultModelProblem(req.getMessage(), req.getSeverity(), req.getVersion(), source, line, column, modelId, req.getException());
    add(problem);
  }
  
  public ModelBuildingException newModelBuildingException() {
    ModelBuildingResult result = this.result;
    if (result.getModelIds().isEmpty()) {
      DefaultModelBuildingResult tmp = new DefaultModelBuildingResult();
      tmp.setEffectiveModel(result.getEffectiveModel());
      tmp.setProblems(getProblems());
      tmp.setActiveExternalProfiles(result.getActiveExternalProfiles());
      String id = getRootModelId();
      tmp.addModelId(id);
      tmp.setRawModel(id, getRootModel());
      result = tmp;
    } 
    return new ModelBuildingException(result);
  }
}
