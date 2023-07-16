package gnu.trove.decorator;

import gnu.trove.iterator.TCharIterator;
import gnu.trove.set.TCharSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class TCharSetDecorator extends AbstractSet<Character> implements Set<Character>, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected TCharSet _set;
  
  public TCharSetDecorator() {}
  
  public TCharSetDecorator(TCharSet set) {
    Objects.requireNonNull(set);
    this._set = set;
  }
  
  public TCharSet getSet() {
    return this._set;
  }
  
  public boolean add(Character value) {
    return (value != null && this._set.add(value.charValue()));
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
        if (val instanceof Character) {
          char v = ((Character)val).charValue();
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
    return (value instanceof Character && this._set.remove(((Character)value).charValue()));
  }
  
  public Iterator<Character> iterator() {
    return new Iterator<Character>() {
        private final TCharIterator it = TCharSetDecorator.this._set.iterator();
        
        public Character next() {
          return Character.valueOf(this.it.next());
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
    if (!(o instanceof Character))
      return false; 
    return this._set.contains(((Character)o).charValue());
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._set = (TCharSet)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._set);
  }
}
