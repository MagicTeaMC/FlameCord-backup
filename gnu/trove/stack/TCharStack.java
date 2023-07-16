package gnu.trove.stack;

public interface TCharStack {
  char getNoEntryValue();
  
  void push(char paramChar);
  
  char pop();
  
  char peek();
  
  int size();
  
  void clear();
  
  char[] toArray();
  
  void toArray(char[] paramArrayOfchar);
}
