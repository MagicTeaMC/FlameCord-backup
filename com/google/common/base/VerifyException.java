package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public class VerifyException extends RuntimeException {
  public VerifyException() {}
  
  public VerifyException(@CheckForNull String message) {
    super(message);
  }
  
  public VerifyException(@CheckForNull Throwable cause) {
    super(cause);
  }
  
  public VerifyException(@CheckForNull String message, @CheckForNull Throwable cause) {
    super(message, cause);
  }
}