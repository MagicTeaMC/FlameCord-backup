package io.github.waterfallmc.waterfall.exception;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.plugin.Plugin;

public class ProxyPluginException extends ProxyException {
  private final Plugin responsiblePlugin;
  
  public ProxyPluginException(String message, Throwable cause, Plugin responsiblePlugin) {
    super(message, cause);
    this.responsiblePlugin = (Plugin)Preconditions.checkNotNull(responsiblePlugin, "responsiblePlugin");
  }
  
  public ProxyPluginException(Throwable cause, Plugin responsiblePlugin) {
    super(cause);
    this.responsiblePlugin = (Plugin)Preconditions.checkNotNull(responsiblePlugin, "responsiblePlugin");
  }
  
  protected ProxyPluginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Plugin responsiblePlugin) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.responsiblePlugin = (Plugin)Preconditions.checkNotNull(responsiblePlugin, "responsiblePlugin");
  }
  
  public Plugin getResponsiblePlugin() {
    return this.responsiblePlugin;
  }
}
