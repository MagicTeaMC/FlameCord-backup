package io.netty.handler.codec;

import io.netty.util.Signal;
import io.netty.util.internal.ObjectUtil;

public class DecoderResult {
  protected static final Signal SIGNAL_UNFINISHED = Signal.valueOf(DecoderResult.class, "UNFINISHED");
  
  protected static final Signal SIGNAL_SUCCESS = Signal.valueOf(DecoderResult.class, "SUCCESS");
  
  public static final DecoderResult UNFINISHED = new DecoderResult((Throwable)SIGNAL_UNFINISHED);
  
  public static final DecoderResult SUCCESS = new DecoderResult((Throwable)SIGNAL_SUCCESS);
  
  private final Throwable cause;
  
  public static DecoderResult failure(Throwable cause) {
    return new DecoderResult((Throwable)ObjectUtil.checkNotNull(cause, "cause"));
  }
  
  protected DecoderResult(Throwable cause) {
    this.cause = (Throwable)ObjectUtil.checkNotNull(cause, "cause");
  }
  
  public boolean isFinished() {
    return (this.cause != SIGNAL_UNFINISHED);
  }
  
  public boolean isSuccess() {
    return (this.cause == SIGNAL_SUCCESS);
  }
  
  public boolean isFailure() {
    return (this.cause != SIGNAL_SUCCESS && this.cause != SIGNAL_UNFINISHED);
  }
  
  public Throwable cause() {
    if (isFailure())
      return this.cause; 
    return null;
  }
  
  public String toString() {
    if (isFinished()) {
      if (isSuccess())
        return "success"; 
      String cause = cause().toString();
      return (new StringBuilder(cause.length() + 17))
        .append("failure(")
        .append(cause)
        .append(')')
        .toString();
    } 
    return "unfinished";
  }
}
