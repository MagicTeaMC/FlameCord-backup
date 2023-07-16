package io.github.waterfallmc.waterfall.exception;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;

public class ProxyEventException extends ProxyException {
  private final Listener listener;
  
  private final Event event;
  
  public ProxyEventException(String message, Throwable cause, Listener listener, Event event) {
    super(message, cause);
    this.listener = (Listener)Preconditions.checkNotNull(listener, "listener");
    this.event = (Event)Preconditions.checkNotNull(event, "event");
  }
  
  public ProxyEventException(Throwable cause, Listener listener, Event event) {
    super(cause);
    this.listener = (Listener)Preconditions.checkNotNull(listener, "listener");
    this.event = (Event)Preconditions.checkNotNull(event, "event");
  }
  
  protected ProxyEventException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Listener listener, Event event) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.listener = (Listener)Preconditions.checkNotNull(listener, "listener");
    this.event = (Event)Preconditions.checkNotNull(event, "event");
  }
  
  public Listener getListener() {
    return this.listener;
  }
  
  public Event getEvent() {
    return this.event;
  }
}
