package gnu.trove;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.procedure.TIntProcedure;
import java.util.Collection;

public interface TIntCollection {
  public static final long serialVersionUID = 1L;
  
  int getNoEntryValue();
  
  int size();
  
  boolean isEmpty();
  
  boolean contains(int paramInt);
  
  TIntIterator iterator();
  
  int[] toArray();
  
  int[] toArray(int[] paramArrayOfint);
  
  boolean add(int paramInt);
  
  boolean remove(int paramInt);
  
  boolean containsAll(Collection<?> paramCollection);
  
  boolean containsAll(TIntCollection paramTIntCollection);
  
  boolean containsAll(int[] paramArrayOfint);
  
  boolean addAll(Collection<? extends Integer> paramCollection);
  
  boolean addAll(TIntCollection paramTIntCollection);
  
  boolean addAll(int[] paramArrayOfint);
  
  boolean retainAll(Collection<?> paramCollection);
  
  boolean retainAll(TIntCollection paramTIntCollection);
  
  boolean retainAll(int[] paramArrayOfint);
  
  boolean removeAll(Collection<?> paramCollection);
  
  boolean removeAll(TIntCollection paramTIntCollection);
  
  boolean removeAll(int[] paramArrayOfint);
  
  void clear();
  
  boolean forEach(TIntProcedure paramTIntProcedure);
  
  boolean equals(Object paramObject);
  
  int hashCode();
}
