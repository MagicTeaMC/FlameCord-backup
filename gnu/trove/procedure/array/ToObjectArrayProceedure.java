package gnu.trove.procedure.array;

import gnu.trove.procedure.TObjectProcedure;

public final class ToObjectArrayProceedure<T> implements TObjectProcedure<T> {
  private final T[] target;
  
  private int pos = 0;
  
  public ToObjectArrayProceedure(T[] target) {
    this.target = target;
  }
  
  public final boolean execute(T value) {
    this.target[this.pos++] = value;
    return true;
  }
}
