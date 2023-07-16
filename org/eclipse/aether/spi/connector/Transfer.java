package org.eclipse.aether.spi.connector;

import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.transfer.TransferListener;

public abstract class Transfer {
  private TransferListener listener;
  
  private RequestTrace trace;
  
  public abstract Exception getException();
  
  public TransferListener getListener() {
    return this.listener;
  }
  
  Transfer setListener(TransferListener listener) {
    this.listener = listener;
    return this;
  }
  
  public RequestTrace getTrace() {
    return this.trace;
  }
  
  Transfer setTrace(RequestTrace trace) {
    this.trace = trace;
    return this;
  }
}
