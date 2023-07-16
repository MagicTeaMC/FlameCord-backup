package gnu.trove.decorator;

import gnu.trove.list.TByteList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

public class TByteListDecorator extends AbstractList<Byte> implements List<Byte>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TByteList list;
  
  public TByteListDecorator() {}
  
  public TByteListDecorator(TByteList list) {
    Objects.requireNonNull(list);
    this.list = list;
  }
  
  public TByteList getList() {
    return this.list;
  }
  
  public int size() {
    return this.list.size();
  }
  
  public Byte get(int index) {
    byte value = this.list.get(index);
    if (value == this.list.getNoEntryValue())
      return null; 
    return Byte.valueOf(value);
  }
  
  public Byte set(int index, Byte value) {
    byte previous_value = this.list.set(index, value.byteValue());
    if (previous_value == this.list.getNoEntryValue())
      return null; 
    return Byte.valueOf(previous_value);
  }
  
  public void add(int index, Byte value) {
    this.list.insert(index, value.byteValue());
  }
  
  public Byte remove(int index) {
    byte previous_value = this.list.removeAt(index);
    if (previous_value == this.list.getNoEntryValue())
      return null; 
    return Byte.valueOf(previous_value);
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this.list = (TByteList)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this.list);
  }
}
