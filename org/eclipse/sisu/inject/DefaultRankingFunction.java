package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import javax.inject.Inject;
import org.eclipse.sisu.Priority;

public final class DefaultRankingFunction implements RankingFunction {
  private final int primaryRank;
  
  public DefaultRankingFunction(int primaryRank) {
    if (primaryRank < 0)
      throw new IllegalArgumentException("Primary rank must be zero or more"); 
    this.primaryRank = primaryRank;
  }
  
  @Inject
  public DefaultRankingFunction() {
    this(0);
  }
  
  public int maxRank() {
    return Integer.MAX_VALUE;
  }
  
  public <T> int rank(Binding<T> binding) {
    Priority priority = Sources.<Priority>getAnnotation(binding, Priority.class);
    if (priority != null)
      return priority.value(); 
    if (QualifyingStrategy.DEFAULT_QUALIFIER.equals(QualifyingStrategy.qualify(binding.getKey())))
      return this.primaryRank; 
    return this.primaryRank + Integer.MIN_VALUE;
  }
}
