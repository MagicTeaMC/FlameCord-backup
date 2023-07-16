package gnu.trove.stack;

public interface TShortStack {
  short getNoEntryValue();
  
  void push(short paramShort);
  
  short pop();
  
  short peek();
  
  int size();
  
  void clear();
  
  short[] toArray();
  
  void toArray(short[] paramArrayOfshort);
}
