package net.md_5.bungee.api.event;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.PendingConnection;

public class ProxyPingEvent extends AsyncEvent<ProxyPingEvent> {
  private final PendingConnection connection;
  
  private ServerPing response;
  
  public void setResponse(ServerPing response) {
    this.response = response;
  }
  
  public String toString() {
    return "ProxyPingEvent(connection=" + getConnection() + ", response=" + getResponse() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ProxyPingEvent))
      return false; 
    ProxyPingEvent other = (ProxyPingEvent)o;
    if (!other.canEqual(this))
      return false; 
    Object this$connection = getConnection(), other$connection = other.getConnection();
    if ((this$connection == null) ? (other$connection != null) : !this$connection.equals(other$connection))
      return false; 
    Object this$response = getResponse(), other$response = other.getResponse();
    return !((this$response == null) ? (other$response != null) : !this$response.equals(other$response));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ProxyPingEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $connection = getConnection();
    result = result * 59 + (($connection == null) ? 43 : $connection.hashCode());
    Object $response = getResponse();
    return result * 59 + (($response == null) ? 43 : $response.hashCode());
  }
  
  public PendingConnection getConnection() {
    return this.connection;
  }
  
  public ServerPing getResponse() {
    return this.response;
  }
  
  public ProxyPingEvent(PendingConnection connection, ServerPing response, Callback<ProxyPingEvent> done) {
    super(done);
    this.connection = connection;
    this.response = response;
  }
}
