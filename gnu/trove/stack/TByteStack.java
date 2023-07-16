package gnu.trove.stack;

public interface TByteStack {
  byte getNoEntryValue();
  
  void push(byte paramByte);
  
  byte pop();
  
  byte peek();
  
  int size();
  
  void clear();
  
  byte[] toArray();
  
  void toArray(byte[] paramArrayOfbyte);
}
