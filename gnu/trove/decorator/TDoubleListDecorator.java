package gnu.trove.decorator;

import gnu.trove.list.TDoubleList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

public class TDoubleListDecorator extends AbstractList<Double> implements List<Double>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TDoubleList list;
  
  public TDoubleListDecorator() {}
  
  public TDoubleListDecorator(TDoubleList list) {
    Objects.requireNonNull(list);
    this.list = list;
  }
  
  public TDoubleList getList() {
    return this.list;
  }
  
  public int size() {
    return this.list.size();
  }
  
  public Double get(int index) {
    double value = this.list.get(index);
    if (value == this.list.getNoEntryValue())
      return null; 
    return Double.valueOf(value);
  }
  
  public Double set(int index, Double value) {
    double previous_value = this.list.set(index, value.doubleValue());
    if (previous_value == this.list.getNoEntryValue())
      return null; 
    return Double.valueOf(previous_value);
  }
  
  public void add(int index, Double value) {
    this.list.insert(index, value.doubleValue());
  }
  
  public Double remove(int index) {
    double previous_value = this.list.removeAt(index);
    if (previous_value == this.list.getNoEntryValue())
      return null; 
    return Double.valueOf(previous_value);
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this.list = (TDoubleList)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this.list);
  }
}
