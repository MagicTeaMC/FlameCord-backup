package net.md_5.bungee.module.reconnect.yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.util.CaseInsensitiveMap;
import org.yaml.snakeyaml.Yaml;

public class YamlReconnectHandler extends AbstractReconnectHandler {
  private final Yaml yaml = new Yaml();
  
  private final File file = new File("locations.yml");
  
  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  
  private CaseInsensitiveMap<String> data;
  
  public YamlReconnectHandler() {
    try {
      this.file.createNewFile();
      try (FileReader rd = new FileReader(this.file)) {
        Map map = (Map)this.yaml.loadAs(rd, Map.class);
        if (map != null)
          this.data = new CaseInsensitiveMap(map); 
      } 
    } catch (Exception ex) {
      this.file.renameTo(new File("locations.yml.old"));
      ProxyServer.getInstance().getLogger().log(Level.WARNING, "Could not load reconnect locations, resetting them");
    } 
    if (this.data == null)
      this.data = new CaseInsensitiveMap(); 
  }
  
  protected ServerInfo getStoredServer(ProxiedPlayer player) {
    ServerInfo server = null;
    this.lock.readLock().lock();
    try {
      server = ProxyServer.getInstance().getServerInfo((String)this.data.get(key(player)));
    } finally {
      this.lock.readLock().unlock();
    } 
    return server;
  }
  
  public void setServer(ProxiedPlayer player) {
    this.lock.writeLock().lock();
    try {
      this.data.put(key(player), (player.getReconnectServer() != null) ? player.getReconnectServer().getName() : player.getServer().getInfo().getName());
    } finally {
      this.lock.writeLock().unlock();
    } 
  }
  
  private String key(ProxiedPlayer player) {
    InetSocketAddress host = player.getPendingConnection().getVirtualHost();
    return player.getName() + ";" + host.getHostString() + ":" + host.getPort();
  }
  
  public void save() {
    Map<String, String> copy = new HashMap<>();
    this.lock.readLock().lock();
    try {
      copy.putAll((Map)this.data);
    } finally {
      this.lock.readLock().unlock();
    } 
    try (FileWriter wr = new FileWriter(this.file)) {
      this.yaml.dump(copy, wr);
    } catch (IOException ex) {
      ProxyServer.getInstance().getLogger().log(Level.WARNING, "Could not save reconnect locations", ex);
    } 
  }
  
  public void close() {}
}
