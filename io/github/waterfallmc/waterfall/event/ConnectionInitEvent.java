package io.github.waterfallmc.waterfall.event;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.event.AsyncEvent;
import net.md_5.bungee.api.plugin.Cancellable;

public class ConnectionInitEvent extends AsyncEvent<ConnectionInitEvent> implements Cancellable {
  private final SocketAddress remoteAddress;
  
  private final ListenerInfo listener;
  
  public String toString() {
    return "ConnectionInitEvent(remoteAddress=" + getRemoteAddress() + ", listener=" + this.listener + ", isCancelled=" + isCancelled() + ")";
  }
  
  private boolean isCancelled = false;
  
  public ConnectionInitEvent(SocketAddress remoteAddress, ListenerInfo listener, Callback<ConnectionInitEvent> done) {
    super(done);
    this.remoteAddress = remoteAddress;
    this.listener = listener;
  }
  
  public boolean isCancelled() {
    return this.isCancelled;
  }
  
  public void setCancelled(boolean cancel) {
    this.isCancelled = cancel;
  }
  
  @Deprecated
  public InetSocketAddress getRemoteAddress() {
    return (InetSocketAddress)this.remoteAddress;
  }
  
  public SocketAddress getRemoteSocketAddress() {
    return this.remoteAddress;
  }
}
