package gnu.trove.impl.unmodifiable;

import gnu.trove.TFloatCollection;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;

public class TUnmodifiableFloatSet extends TUnmodifiableFloatCollection implements TFloatSet, Serializable {
  private static final long serialVersionUID = -9215047833775013803L;
  
  public TUnmodifiableFloatSet(TFloatSet s) {
    super((TFloatCollection)s);
  }
  
  public boolean equals(Object o) {
    return (o == this || this.c.equals(o));
  }
  
  public int hashCode() {
    return this.c.hashCode();
  }
}
