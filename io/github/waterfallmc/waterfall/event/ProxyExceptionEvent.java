package io.github.waterfallmc.waterfall.event;

import com.google.common.base.Preconditions;
import io.github.waterfallmc.waterfall.exception.ProxyException;
import net.md_5.bungee.api.plugin.Event;

public class ProxyExceptionEvent extends Event {
  private ProxyException exception;
  
  public ProxyExceptionEvent(ProxyException exception) {
    this.exception = (ProxyException)Preconditions.checkNotNull(exception, "exception");
  }
  
  public ProxyException getException() {
    return this.exception;
  }
}
