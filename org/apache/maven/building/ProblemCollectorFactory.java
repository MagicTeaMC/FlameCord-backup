package org.apache.maven.building;

import java.util.List;

public class ProblemCollectorFactory {
  public static ProblemCollector newInstance(List<Problem> problems) {
    return new DefaultProblemCollector(problems);
  }
}
