package gnu.trove.strategy;

import java.io.Serializable;

public interface HashingStrategy<T> extends Serializable {
  public static final long serialVersionUID = 5674097166776615540L;
  
  int computeHashCode(T paramT);
  
  boolean equals(T paramT1, T paramT2);
}
