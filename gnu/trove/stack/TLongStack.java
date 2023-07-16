package gnu.trove.stack;

public interface TLongStack {
  long getNoEntryValue();
  
  void push(long paramLong);
  
  long pop();
  
  long peek();
  
  int size();
  
  void clear();
  
  long[] toArray();
  
  void toArray(long[] paramArrayOflong);
}
