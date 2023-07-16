package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public class UncheckedExecutionException extends RuntimeException {
  private static final long serialVersionUID = 0L;
  
  protected UncheckedExecutionException() {}
  
  protected UncheckedExecutionException(@CheckForNull String message) {
    super(message);
  }
  
  public UncheckedExecutionException(@CheckForNull String message, @CheckForNull Throwable cause) {
    super(message, cause);
  }
  
  public UncheckedExecutionException(@CheckForNull Throwable cause) {
    super(cause);
  }
}
