package org.apache.maven.model.building;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import org.apache.maven.model.Model;

public class ModelBuildingException extends Exception {
  private final ModelBuildingResult result;
  
  @Deprecated
  public ModelBuildingException(Model model, String modelId, List<ModelProblem> problems) {
    super(toMessage(modelId, problems));
    if (model != null) {
      DefaultModelBuildingResult tmp = new DefaultModelBuildingResult();
      if (modelId == null)
        modelId = ""; 
      tmp.addModelId(modelId);
      tmp.setRawModel(modelId, model);
      tmp.setProblems(problems);
      this.result = tmp;
    } else {
      this.result = null;
    } 
  }
  
  public ModelBuildingException(ModelBuildingResult result) {
    super(toMessage(result));
    this.result = result;
  }
  
  public ModelBuildingResult getResult() {
    return this.result;
  }
  
  public Model getModel() {
    if (this.result == null)
      return null; 
    if (this.result.getEffectiveModel() != null)
      return this.result.getEffectiveModel(); 
    return this.result.getRawModel();
  }
  
  public String getModelId() {
    if (this.result == null || this.result.getModelIds().isEmpty())
      return ""; 
    return this.result.getModelIds().get(0);
  }
  
  public List<ModelProblem> getProblems() {
    if (this.result == null)
      return Collections.emptyList(); 
    return Collections.unmodifiableList(this.result.getProblems());
  }
  
  private static String toMessage(ModelBuildingResult result) {
    if (result != null && !result.getModelIds().isEmpty())
      return toMessage(result.getModelIds().get(0), result.getProblems()); 
    return null;
  }
  
  private static String toMessage(String modelId, List<ModelProblem> problems) {
    StringWriter buffer = new StringWriter(1024);
    PrintWriter writer = new PrintWriter(buffer);
    writer.print(problems.size());
    writer.print((problems.size() == 1) ? " problem was " : " problems were ");
    writer.print("encountered while building the effective model");
    if (modelId != null && modelId.length() > 0) {
      writer.print(" for ");
      writer.print(modelId);
    } 
    writer.println();
    for (ModelProblem problem : problems) {
      writer.print("[");
      writer.print(problem.getSeverity());
      writer.print("] ");
      writer.print(problem.getMessage());
      writer.print(" @ ");
      writer.println(ModelProblemUtils.formatLocation(problem, modelId));
    } 
    return buffer.toString();
  }
}
