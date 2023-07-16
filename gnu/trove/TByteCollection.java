package gnu.trove;

import gnu.trove.iterator.TByteIterator;
import gnu.trove.procedure.TByteProcedure;
import java.util.Collection;

public interface TByteCollection {
  public static final long serialVersionUID = 1L;
  
  byte getNoEntryValue();
  
  int size();
  
  boolean isEmpty();
  
  boolean contains(byte paramByte);
  
  TByteIterator iterator();
  
  byte[] toArray();
  
  byte[] toArray(byte[] paramArrayOfbyte);
  
  boolean add(byte paramByte);
  
  boolean remove(byte paramByte);
  
  boolean containsAll(Collection<?> paramCollection);
  
  boolean containsAll(TByteCollection paramTByteCollection);
  
  boolean containsAll(byte[] paramArrayOfbyte);
  
  boolean addAll(Collection<? extends Byte> paramCollection);
  
  boolean addAll(TByteCollection paramTByteCollection);
  
  boolean addAll(byte[] paramArrayOfbyte);
  
  boolean retainAll(Collection<?> paramCollection);
  
  boolean retainAll(TByteCollection paramTByteCollection);
  
  boolean retainAll(byte[] paramArrayOfbyte);
  
  boolean removeAll(Collection<?> paramCollection);
  
  boolean removeAll(TByteCollection paramTByteCollection);
  
  boolean removeAll(byte[] paramArrayOfbyte);
  
  void clear();
  
  boolean forEach(TByteProcedure paramTByteProcedure);
  
  boolean equals(Object paramObject);
  
  int hashCode();
}
