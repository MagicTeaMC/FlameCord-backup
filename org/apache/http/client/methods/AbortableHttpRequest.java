package org.apache.http.client.methods;

import java.io.IOException;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionReleaseTrigger;

@Deprecated
public interface AbortableHttpRequest {
  void setConnectionRequest(ClientConnectionRequest paramClientConnectionRequest) throws IOException;
  
  void setReleaseTrigger(ConnectionReleaseTrigger paramConnectionReleaseTrigger) throws IOException;
  
  void abort();
}
