package org.apache.maven.model.building;

import java.util.List;

public interface ModelProblemCollectorExt extends ModelProblemCollector {
  List<ModelProblem> getProblems();
}
