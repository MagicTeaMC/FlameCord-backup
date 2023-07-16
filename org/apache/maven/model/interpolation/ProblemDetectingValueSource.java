package org.apache.maven.model.interpolation;

import java.util.List;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.building.ModelProblemCollectorRequest;
import org.codehaus.plexus.interpolation.ValueSource;

class ProblemDetectingValueSource implements ValueSource {
  private final ValueSource valueSource;
  
  private final String bannedPrefix;
  
  private final String newPrefix;
  
  private final ModelProblemCollector problems;
  
  ProblemDetectingValueSource(ValueSource valueSource, String bannedPrefix, String newPrefix, ModelProblemCollector problems) {
    this.valueSource = valueSource;
    this.bannedPrefix = bannedPrefix;
    this.newPrefix = newPrefix;
    this.problems = problems;
  }
  
  public Object getValue(String expression) {
    Object value = this.valueSource.getValue(expression);
    if (value != null && expression.startsWith(this.bannedPrefix)) {
      String msg = "The expression ${" + expression + "} is deprecated.";
      if (this.newPrefix != null && this.newPrefix.length() > 0)
        msg = msg + " Please use ${" + this.newPrefix + expression.substring(this.bannedPrefix.length()) + "} instead."; 
      this.problems.add((new ModelProblemCollectorRequest(ModelProblem.Severity.WARNING, ModelProblem.Version.V20)).setMessage(msg));
    } 
    return value;
  }
  
  public List getFeedback() {
    return this.valueSource.getFeedback();
  }
  
  public void clearFeedback() {
    this.valueSource.clearFeedback();
  }
}
