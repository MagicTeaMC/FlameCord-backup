package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.set.TDoubleSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class TDoubleSetDecorator extends AbstractSet<Double> implements Set<Double>, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected TDoubleSet _set;
  
  public TDoubleSetDecorator() {}
  
  public TDoubleSetDecorator(TDoubleSet set) {
    Objects.requireNonNull(set);
    this._set = set;
  }
  
  public TDoubleSet getSet() {
    return this._set;
  }
  
  public boolean add(Double value) {
    return (value != null && this._set.add(value.doubleValue()));
  }
  
  public boolean equals(Object other) {
    if (this._set.equals(other))
      return true; 
    if (other instanceof Set) {
      Set that = (Set)other;
      if (that.size() != this._set.size())
        return false; 
      Iterator it = that.iterator();
      for (int i = that.size(); i-- > 0; ) {
        Object val = it.next();
        if (val instanceof Double) {
          double v = ((Double)val).doubleValue();
          if (this._set.contains(v))
            continue; 
          return false;
        } 
        return false;
      } 
      return true;
    } 
    return false;
  }
  
  public void clear() {
    this._set.clear();
  }
  
  public boolean remove(Object value) {
    return (value instanceof Double && this._set.remove(((Double)value).doubleValue()));
  }
  
  public Iterator<Double> iterator() {
    return new Iterator<Double>() {
        private final TDoubleIterator it = TDoubleSetDecorator.this._set.iterator();
        
        public Double next() {
          return Double.valueOf(this.it.next());
        }
        
        public boolean hasNext() {
          return this.it.hasNext();
        }
        
        public void remove() {
          this.it.remove();
        }
      };
  }
  
  public int size() {
    return this._set.size();
  }
  
  public boolean isEmpty() {
    return (this._set.size() == 0);
  }
  
  public boolean contains(Object o) {
    if (!(o instanceof Double))
      return false; 
    return this._set.contains(((Double)o).doubleValue());
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._set = (TDoubleSet)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._set);
  }
}
