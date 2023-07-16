package org.apache.maven.building;

import java.util.List;

public interface ProblemCollector {
  void add(Problem.Severity paramSeverity, String paramString, int paramInt1, int paramInt2, Exception paramException);
  
  void setSource(String paramString);
  
  List<Problem> getProblems();
}
