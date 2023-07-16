package io.github.waterfallmc.waterfall.conf;

import java.io.File;
import net.md_5.bungee.conf.Configuration;
import net.md_5.bungee.conf.YamlConfig;
import net.md_5.bungee.protocol.ProtocolConstants;

public class WaterfallConfiguration extends Configuration {
  private boolean logInitialHandlerConnections = true;
  
  private String gameVersion;
  
  private boolean useNettyDnsResolver = true;
  
  private int tabThrottle = 1000;
  
  private boolean disableModernTabLimiter = true;
  
  private boolean disableTabListRewrite = true;
  
  private int pluginChannelLimit = 128;
  
  private int pluginChannelNameLimit = 128;
  
  public void load() {
    super.load();
    YamlConfig config = new YamlConfig(new File("waterfall.yml"));
    config.load(false);
    this.logInitialHandlerConnections = config.getBoolean("log_initial_handler_connections", this.logInitialHandlerConnections);
    this.gameVersion = config.getString("game_version", "").isEmpty() ? ((String)ProtocolConstants.SUPPORTED_VERSIONS.get(0) + "-" + (String)ProtocolConstants.SUPPORTED_VERSIONS.get(ProtocolConstants.SUPPORTED_VERSIONS.size() - 1)) : config.getString("game_version", "");
    this.useNettyDnsResolver = config.getBoolean("use_netty_dns_resolver", this.useNettyDnsResolver);
    this.tabThrottle = config.getInt("throttling.tab_complete", this.tabThrottle);
    this.disableModernTabLimiter = config.getBoolean("disable_modern_tab_limiter", this.disableModernTabLimiter);
    this.disableTabListRewrite = config.getBoolean("disable_tab_list_rewrite", this.disableTabListRewrite);
    this.pluginChannelLimit = config.getInt("registered_plugin_channels_limit", this.pluginChannelLimit);
    this.pluginChannelNameLimit = config.getInt("plugin_channel_name_limit", this.pluginChannelNameLimit);
  }
  
  public boolean isLogInitialHandlerConnections() {
    return this.logInitialHandlerConnections;
  }
  
  public String getGameVersion() {
    return this.gameVersion;
  }
  
  public boolean isUseNettyDnsResolver() {
    return this.useNettyDnsResolver;
  }
  
  public int getTabThrottle() {
    return this.tabThrottle;
  }
  
  public boolean isDisableModernTabLimiter() {
    return this.disableModernTabLimiter;
  }
  
  public boolean isDisableTabListRewrite() {
    return this.disableTabListRewrite;
  }
  
  public int getPluginChannelLimit() {
    return this.pluginChannelLimit;
  }
  
  public int getPluginChannelNameLimit() {
    return this.pluginChannelNameLimit;
  }
}
