package gnu.trove.decorator;

import gnu.trove.list.TFloatList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

public class TFloatListDecorator extends AbstractList<Float> implements List<Float>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TFloatList list;
  
  public TFloatListDecorator() {}
  
  public TFloatListDecorator(TFloatList list) {
    Objects.requireNonNull(list);
    this.list = list;
  }
  
  public TFloatList getList() {
    return this.list;
  }
  
  public int size() {
    return this.list.size();
  }
  
  public Float get(int index) {
    float value = this.list.get(index);
    if (value == this.list.getNoEntryValue())
      return null; 
    return Float.valueOf(value);
  }
  
  public Float set(int index, Float value) {
    float previous_value = this.list.set(index, value.floatValue());
    if (previous_value == this.list.getNoEntryValue())
      return null; 
    return Float.valueOf(previous_value);
  }
  
  public void add(int index, Float value) {
    this.list.insert(index, value.floatValue());
  }
  
  public Float remove(int index) {
    float previous_value = this.list.removeAt(index);
    if (previous_value == this.list.getNoEntryValue())
      return null; 
    return Float.valueOf(previous_value);
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this.list = (TFloatList)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this.list);
  }
}
