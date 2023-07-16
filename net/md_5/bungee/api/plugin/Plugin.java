package net.md_5.bungee.api.plugin;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.GroupedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Plugin {
  private PluginDescription description;
  
  private ProxyServer proxy;
  
  private File file;
  
  private Logger logger;
  
  private ExecutorService service;
  
  public PluginDescription getDescription() {
    return this.description;
  }
  
  public ProxyServer getProxy() {
    return this.proxy;
  }
  
  public File getFile() {
    return this.file;
  }
  
  public Logger getLogger() {
    return this.logger;
  }
  
  public Plugin() {
    ClassLoader classLoader = getClass().getClassLoader();
    Preconditions.checkState(classLoader instanceof PluginClassloader, "Plugin requires " + PluginClassloader.class.getName());
    ((PluginClassloader)classLoader).init(this);
  }
  
  protected Plugin(ProxyServer proxy, PluginDescription description) {
    ClassLoader classLoader = getClass().getClassLoader();
    Preconditions.checkState(!(classLoader instanceof PluginClassloader), "Cannot use initialization constructor at runtime");
  }
  
  public Logger getSLF4JLogger() {
    return LoggerFactory.getLogger(this.logger.getName());
  }
  
  public void onLoad() {}
  
  public void onEnable() {}
  
  public void onDisable() {}
  
  public final File getDataFolder() {
    return new File(getProxy().getPluginsFolder(), getDescription().getName());
  }
  
  public final InputStream getResourceAsStream(String name) {
    return getClass().getClassLoader().getResourceAsStream(name);
  }
  
  final void init(ProxyServer proxy, PluginDescription description) {
    this.proxy = proxy;
    this.description = description;
    this.file = description.getFile();
    this.logger = Logger.getLogger(description.getName());
  }
  
  @Deprecated
  public ExecutorService getExecutorService() {
    if (this.service == null) {
      String name = (getDescription() == null) ? "unknown" : getDescription().getName();
      this.service = Executors.newCachedThreadPool((new ThreadFactoryBuilder()).setNameFormat(name + " Pool Thread #%1$d")
          .setThreadFactory((ThreadFactory)new GroupedThreadFactory(this, name)).build());
    } 
    return this.service;
  }
}
