package gnu.trove.decorator;

import gnu.trove.list.TShortList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

public class TShortListDecorator extends AbstractList<Short> implements List<Short>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TShortList list;
  
  public TShortListDecorator() {}
  
  public TShortListDecorator(TShortList list) {
    Objects.requireNonNull(list);
    this.list = list;
  }
  
  public TShortList getList() {
    return this.list;
  }
  
  public int size() {
    return this.list.size();
  }
  
  public Short get(int index) {
    short value = this.list.get(index);
    if (value == this.list.getNoEntryValue())
      return null; 
    return Short.valueOf(value);
  }
  
  public Short set(int index, Short value) {
    short previous_value = this.list.set(index, value.shortValue());
    if (previous_value == this.list.getNoEntryValue())
      return null; 
    return Short.valueOf(previous_value);
  }
  
  public void add(int index, Short value) {
    this.list.insert(index, value.shortValue());
  }
  
  public Short remove(int index) {
    short previous_value = this.list.removeAt(index);
    if (previous_value == this.list.getNoEntryValue())
      return null; 
    return Short.valueOf(previous_value);
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this.list = (TShortList)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this.list);
  }
}
