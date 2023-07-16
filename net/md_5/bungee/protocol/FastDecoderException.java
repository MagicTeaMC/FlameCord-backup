package net.md_5.bungee.protocol;

import io.netty.handler.codec.DecoderException;

public class FastDecoderException extends DecoderException {
  private static final boolean PROCESS_TRACES = Boolean.getBoolean("waterfall.decoder-traces");
  
  public FastDecoderException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public FastDecoderException(String message) {
    super(message);
  }
  
  public Throwable initCause(Throwable cause) {
    if (PROCESS_TRACES)
      return super.initCause(cause); 
    return (Throwable)this;
  }
  
  public Throwable fillInStackTrace() {
    if (PROCESS_TRACES)
      return super.fillInStackTrace(); 
    return (Throwable)this;
  }
}
