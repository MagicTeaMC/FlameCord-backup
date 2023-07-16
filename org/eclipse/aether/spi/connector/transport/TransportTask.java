package org.eclipse.aether.spi.connector.transport;

import java.net.URI;
import java.util.Objects;

public abstract class TransportTask {
  static final TransportListener NOOP = new TransportListener() {
    
    };
  
  static final byte[] EMPTY = new byte[0];
  
  private URI location;
  
  private TransportListener listener = NOOP;
  
  public URI getLocation() {
    return this.location;
  }
  
  TransportTask setLocation(URI location) {
    this.location = Objects.<URI>requireNonNull(location, "location type cannot be null");
    return this;
  }
  
  public TransportListener getListener() {
    return this.listener;
  }
  
  TransportTask setListener(TransportListener listener) {
    this.listener = (listener != null) ? listener : NOOP;
    return this;
  }
}
