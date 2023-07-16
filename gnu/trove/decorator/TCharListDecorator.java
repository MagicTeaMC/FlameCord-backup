package gnu.trove.decorator;

import gnu.trove.list.TCharList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

public class TCharListDecorator extends AbstractList<Character> implements List<Character>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TCharList list;
  
  public TCharListDecorator() {}
  
  public TCharListDecorator(TCharList list) {
    Objects.requireNonNull(list);
    this.list = list;
  }
  
  public TCharList getList() {
    return this.list;
  }
  
  public int size() {
    return this.list.size();
  }
  
  public Character get(int index) {
    char value = this.list.get(index);
    if (value == this.list.getNoEntryValue())
      return null; 
    return Character.valueOf(value);
  }
  
  public Character set(int index, Character value) {
    char previous_value = this.list.set(index, value.charValue());
    if (previous_value == this.list.getNoEntryValue())
      return null; 
    return Character.valueOf(previous_value);
  }
  
  public void add(int index, Character value) {
    this.list.insert(index, value.charValue());
  }
  
  public Character remove(int index) {
    char previous_value = this.list.removeAt(index);
    if (previous_value == this.list.getNoEntryValue())
      return null; 
    return Character.valueOf(previous_value);
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this.list = (TCharList)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this.list);
  }
}
