package gnu.trove.strategy;

public class IdentityHashingStrategy<K> implements HashingStrategy<K> {
  static final long serialVersionUID = -5188534454583764904L;
  
  public static final IdentityHashingStrategy<Object> INSTANCE = new IdentityHashingStrategy();
  
  public int computeHashCode(K object) {
    return System.identityHashCode(object);
  }
  
  public boolean equals(K o1, K o2) {
    return (o1 == o2);
  }
}
