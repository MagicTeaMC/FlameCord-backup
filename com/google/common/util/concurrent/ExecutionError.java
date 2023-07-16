package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public class ExecutionError extends Error {
  private static final long serialVersionUID = 0L;
  
  protected ExecutionError() {}
  
  protected ExecutionError(@CheckForNull String message) {
    super(message);
  }
  
  public ExecutionError(@CheckForNull String message, @CheckForNull Error cause) {
    super(message, cause);
  }
  
  public ExecutionError(@CheckForNull Error cause) {
    super(cause);
  }
}