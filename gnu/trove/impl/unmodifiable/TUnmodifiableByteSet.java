package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.set.TByteSet;
import java.io.Serializable;

public class TUnmodifiableByteSet extends TUnmodifiableByteCollection implements TByteSet, Serializable {
  private static final long serialVersionUID = -9215047833775013803L;
  
  public TUnmodifiableByteSet(TByteSet s) {
    super((TByteCollection)s);
  }
  
  public boolean equals(Object o) {
    return (o == this || this.c.equals(o));
  }
  
  public int hashCode() {
    return this.c.hashCode();
  }
}
