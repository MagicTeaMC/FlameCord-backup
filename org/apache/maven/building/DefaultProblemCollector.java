package org.apache.maven.building;

import java.util.ArrayList;
import java.util.List;

class DefaultProblemCollector implements ProblemCollector {
  private List<Problem> problems;
  
  private String source;
  
  DefaultProblemCollector(List<Problem> problems) {
    this.problems = (problems != null) ? problems : new ArrayList<>();
  }
  
  public List<Problem> getProblems() {
    return this.problems;
  }
  
  public void setSource(String source) {
    this.source = source;
  }
  
  public void add(Problem.Severity severity, String message, int line, int column, Exception cause) {
    Problem problem = new DefaultProblem(message, severity, this.source, line, column, cause);
    this.problems.add(problem);
  }
}
