package dev._2lstudios.flamecord.configuration;

import java.io.File;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;

public class ModulesConfiguration extends FlameConfig {
  public boolean reconnectEnabled = false;
  
  public boolean alertEnabled = true;
  
  public boolean findEnabled = true;
  
  public boolean ipEnabled = true;
  
  public boolean listEnabled = true;
  
  public boolean permsEnabled = true;
  
  public boolean reloadEnabled = true;
  
  public boolean sendEnabled = true;
  
  public boolean serverEnabled = true;
  
  public ModulesConfiguration(ConfigurationProvider configurationProvider) {
    File configurationFile = new File("./modules.yml");
    Configuration configuration = load(configurationFile);
    this.alertEnabled = setIfUnexistant("alert.enabled", this.alertEnabled, configuration);
    this.findEnabled = setIfUnexistant("find.enabled", this.findEnabled, configuration);
    this.ipEnabled = setIfUnexistant("ip.enabled", this.ipEnabled, configuration);
    this.listEnabled = setIfUnexistant("list.enabled", this.listEnabled, configuration);
    this.permsEnabled = setIfUnexistant("perms.enabled", this.permsEnabled, configuration);
    this.reloadEnabled = setIfUnexistant("reload.enabled", this.reloadEnabled, configuration);
    this.sendEnabled = setIfUnexistant("send.enabled", this.sendEnabled, configuration);
    this.serverEnabled = setIfUnexistant("server.enabled", this.serverEnabled, configuration);
    this.reconnectEnabled = setIfUnexistant("reconnect.enabled", this.reconnectEnabled, configuration);
    save(configuration, configurationFile);
  }
}
