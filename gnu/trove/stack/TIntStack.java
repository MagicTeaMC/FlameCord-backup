package gnu.trove.stack;

public interface TIntStack {
  int getNoEntryValue();
  
  void push(int paramInt);
  
  int pop();
  
  int peek();
  
  int size();
  
  void clear();
  
  int[] toArray();
  
  void toArray(int[] paramArrayOfint);
}
