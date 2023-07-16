package gnu.trove.decorator;

import gnu.trove.iterator.TFloatIterator;
import gnu.trove.set.TFloatSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class TFloatSetDecorator extends AbstractSet<Float> implements Set<Float>, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected TFloatSet _set;
  
  public TFloatSetDecorator() {}
  
  public TFloatSetDecorator(TFloatSet set) {
    Objects.requireNonNull(set);
    this._set = set;
  }
  
  public TFloatSet getSet() {
    return this._set;
  }
  
  public boolean add(Float value) {
    return (value != null && this._set.add(value.floatValue()));
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
        if (val instanceof Float) {
          float v = ((Float)val).floatValue();
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
    return (value instanceof Float && this._set.remove(((Float)value).floatValue()));
  }
  
  public Iterator<Float> iterator() {
    return new Iterator<Float>() {
        private final TFloatIterator it = TFloatSetDecorator.this._set.iterator();
        
        public Float next() {
          return Float.valueOf(this.it.next());
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
    if (!(o instanceof Float))
      return false; 
    return this._set.contains(((Float)o).floatValue());
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._set = (TFloatSet)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._set);
  }
}
