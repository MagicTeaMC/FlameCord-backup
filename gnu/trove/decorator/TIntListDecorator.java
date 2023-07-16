package gnu.trove.decorator;

import gnu.trove.list.TIntList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

public class TIntListDecorator extends AbstractList<Integer> implements List<Integer>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TIntList list;
  
  public TIntListDecorator() {}
  
  public TIntListDecorator(TIntList list) {
    Objects.requireNonNull(list);
    this.list = list;
  }
  
  public TIntList getList() {
    return this.list;
  }
  
  public int size() {
    return this.list.size();
  }
  
  public Integer get(int index) {
    int value = this.list.get(index);
    if (value == this.list.getNoEntryValue())
      return null; 
    return Integer.valueOf(value);
  }
  
  public Integer set(int index, Integer value) {
    int previous_value = this.list.set(index, value.intValue());
    if (previous_value == this.list.getNoEntryValue())
      return null; 
    return Integer.valueOf(previous_value);
  }
  
  public void add(int index, Integer value) {
    this.list.insert(index, value.intValue());
  }
  
  public Integer remove(int index) {
    int previous_value = this.list.removeAt(index);
    if (previous_value == this.list.getNoEntryValue())
      return null; 
    return Integer.valueOf(previous_value);
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this.list = (TIntList)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this.list);
  }
}
