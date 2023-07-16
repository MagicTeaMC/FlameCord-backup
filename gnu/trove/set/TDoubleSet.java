package gnu.trove.set;

import gnu.trove.TDoubleCollection;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import java.util.Collection;

public interface TDoubleSet extends TDoubleCollection {
  double getNoEntryValue();
  
  int size();
  
  boolean isEmpty();
  
  boolean contains(double paramDouble);
  
  TDoubleIterator iterator();
  
  double[] toArray();
  
  double[] toArray(double[] paramArrayOfdouble);
  
  boolean add(double paramDouble);
  
  boolean remove(double paramDouble);
  
  boolean containsAll(Collection<?> paramCollection);
  
  boolean containsAll(TDoubleCollection paramTDoubleCollection);
  
  boolean containsAll(double[] paramArrayOfdouble);
  
  boolean addAll(Collection<? extends Double> paramCollection);
  
  boolean addAll(TDoubleCollection paramTDoubleCollection);
  
  boolean addAll(double[] paramArrayOfdouble);
  
  boolean retainAll(Collection<?> paramCollection);
  
  boolean retainAll(TDoubleCollection paramTDoubleCollection);
  
  boolean retainAll(double[] paramArrayOfdouble);
  
  boolean removeAll(Collection<?> paramCollection);
  
  boolean removeAll(TDoubleCollection paramTDoubleCollection);
  
  boolean removeAll(double[] paramArrayOfdouble);
  
  void clear();
  
  boolean forEach(TDoubleProcedure paramTDoubleProcedure);
  
  boolean equals(Object paramObject);
  
  int hashCode();
}
