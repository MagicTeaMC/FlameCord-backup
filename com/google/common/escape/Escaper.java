package com.google.common.escape;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.errorprone.annotations.DoNotMock;

@DoNotMock("Use Escapers.nullEscaper() or another methods from the *Escapers classes")
@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class Escaper {
  private final Function<String, String> asFunction = this::escape;
  
  public abstract String escape(String paramString);
  
  public final Function<String, String> asFunction() {
    return this.asFunction;
  }
}
