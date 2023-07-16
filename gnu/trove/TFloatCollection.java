package gnu.trove;

import gnu.trove.iterator.TFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import java.util.Collection;

public interface TFloatCollection {
  public static final long serialVersionUID = 1L;
  
  float getNoEntryValue();
  
  int size();
  
  boolean isEmpty();
  
  boolean contains(float paramFloat);
  
  TFloatIterator iterator();
  
  float[] toArray();
  
  float[] toArray(float[] paramArrayOffloat);
  
  boolean add(float paramFloat);
  
  boolean remove(float paramFloat);
  
  boolean containsAll(Collection<?> paramCollection);
  
  boolean containsAll(TFloatCollection paramTFloatCollection);
  
  boolean containsAll(float[] paramArrayOffloat);
  
  boolean addAll(Collection<? extends Float> paramCollection);
  
  boolean addAll(TFloatCollection paramTFloatCollection);
  
  boolean addAll(float[] paramArrayOffloat);
  
  boolean retainAll(Collection<?> paramCollection);
  
  boolean retainAll(TFloatCollection paramTFloatCollection);
  
  boolean retainAll(float[] paramArrayOffloat);
  
  boolean removeAll(Collection<?> paramCollection);
  
  boolean removeAll(TFloatCollection paramTFloatCollection);
  
  boolean removeAll(float[] paramArrayOffloat);
  
  void clear();
  
  boolean forEach(TFloatProcedure paramTFloatProcedure);
  
  boolean equals(Object paramObject);
  
  int hashCode();
}
