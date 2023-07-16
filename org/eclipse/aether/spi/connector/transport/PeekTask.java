package org.eclipse.aether.spi.connector.transport;

import java.net.URI;

public final class PeekTask extends TransportTask {
  public PeekTask(URI location) {
    setLocation(location);
  }
  
  public String toString() {
    return "?? " + getLocation();
  }
}
