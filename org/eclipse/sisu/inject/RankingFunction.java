package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.ImplementedBy;

@ImplementedBy(DefaultRankingFunction.class)
public interface RankingFunction {
  int maxRank();
  
  <T> int rank(Binding<T> paramBinding);
}
