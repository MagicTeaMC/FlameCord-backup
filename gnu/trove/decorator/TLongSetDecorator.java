package gnu.trove.decorator;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.set.TLongSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class TLongSetDecorator extends AbstractSet<Long> implements Set<Long>, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected TLongSet _set;
  
  public TLongSetDecorator() {}
  
  public TLongSetDecorator(TLongSet set) {
    Objects.requireNonNull(set);
    this._set = set;
  }
  
  public TLongSet getSet() {
    return this._set;
  }
  
  public boolean add(Long value) {
    return (value != null && this._set.add(value.longValue()));
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
        if (val instanceof Long) {
          long v = ((Long)val).longValue();
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
    return (value instanceof Long && this._set.remove(((Long)value).longValue()));
  }
  
  public Iterator<Long> iterator() {
    return new Iterator<Long>() {
        private final TLongIterator it = TLongSetDecorator.this._set.iterator();
        
        public Long next() {
          return Long.valueOf(this.it.next());
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
    if (!(o instanceof Long))
      return false; 
    return this._set.contains(((Long)o).longValue());
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._set = (TLongSet)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._set);
  }
}
