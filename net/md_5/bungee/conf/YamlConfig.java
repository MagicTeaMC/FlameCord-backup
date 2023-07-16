package net.md_5.bungee.conf;

import com.google.common.base.Charsets;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.util.CaseInsensitiveMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

public class YamlConfig implements ConfigurationAdapter {
  private final Yaml yaml;
  
  private Map<String, Object> config;
  
  private final File file;
  
  private enum DefaultTabList {
    GLOBAL, GLOBAL_PING, SERVER;
  }
  
  public YamlConfig() {
    this(new File("config.yml"));
  }
  
  public YamlConfig(File file) {
    this.file = file;
    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    this.yaml = new Yaml(options);
  }
  
  public void load() {
    load(true);
  }
  
  public void load(boolean doPermissions) {
    try {
      this.file.createNewFile();
      try (InputStream is = new FileInputStream(this.file)) {
        try {
          this.config = (Map<String, Object>)this.yaml.load(is);
        } catch (YAMLException ex) {
          throw new RuntimeException("Invalid configuration encountered - this is a configuration error and NOT a bug! Please attempt to fix the error or see https://www.spigotmc.org/ for help.", ex);
        } 
      } 
      if (this.config == null) {
        this.config = (Map<String, Object>)new CaseInsensitiveMap();
      } else {
        this.config = (Map<String, Object>)new CaseInsensitiveMap(this.config);
      } 
    } catch (IOException ex) {
      throw new RuntimeException("Could not load configuration!", ex);
    } 
    if (!doPermissions)
      return; 
    Map<String, Object> permissions = get("permissions", null);
    if (permissions == null) {
      set("permissions.default", Arrays.asList(new String[] { "bungeecord.command.server", "bungeecord.command.list" }));
      set("permissions.admin", Arrays.asList(new String[] { "bungeecord.command.alert", "bungeecord.command.end", "bungeecord.command.ip", "bungeecord.command.reload", "bungeecord.command.kick" }));
    } 
    Map<String, Object> groups = get("groups", null);
    if (groups == null)
      set("groups.md_5", Collections.singletonList("admin")); 
  }
  
  private <T> T get(String path, T def) {
    return get(path, def, this.config);
  }
  
  private <T> T get(String path, T def, Map<String, T> submap) {
    int index = path.indexOf('.');
    if (index == -1) {
      Object val = submap.get(path);
      if (val == null && def != null) {
        val = def;
        submap.put(path, def);
        save();
      } 
      return (T)val;
    } 
    String first = path.substring(0, index);
    String second = path.substring(index + 1, path.length());
    Map<Object, Object> sub = (Map)submap.get(first);
    if (sub == null) {
      sub = new LinkedHashMap<>();
      submap.put(first, (T)sub);
    } 
    return get(second, def, sub);
  }
  
  private void set(String path, Object val) {
    set(path, val, this.config);
  }
  
  private void set(String path, Object val, Map<String, Object> submap) {
    int index = path.indexOf('.');
    if (index == -1) {
      if (val == null) {
        submap.remove(path);
      } else {
        submap.put(path, val);
      } 
      save();
    } else {
      String first = path.substring(0, index);
      String second = path.substring(index + 1, path.length());
      Map<Object, Object> sub = (Map)submap.get(first);
      if (sub == null) {
        sub = new LinkedHashMap<>();
        submap.put(first, sub);
      } 
      set(second, val, sub);
    } 
  }
  
  private void save() {
    try (Writer wr = new OutputStreamWriter(new FileOutputStream(this.file), Charsets.UTF_8)) {
      this.yaml.dump(this.config, wr);
    } catch (IOException ex) {
      ProxyServer.getInstance().getLogger().log(Level.WARNING, "Could not save config", ex);
    } 
  }
  
  public int getInt(String path, int def) {
    return ((Integer)get(path, Integer.valueOf(def))).intValue();
  }
  
  public String getString(String path, String def) {
    return get(path, def);
  }
  
  public boolean getBoolean(String path, boolean def) {
    return ((Boolean)get(path, Boolean.valueOf(def))).booleanValue();
  }
  
  public Map<String, ServerInfo> getServers() {
    Map<String, Map<String, Object>> base = get("servers", Collections.singletonMap("lobby", new HashMap<>()));
    Map<String, ServerInfo> ret = new HashMap<>();
    for (Map.Entry<String, Map<String, Object>> entry : base.entrySet()) {
      Map<String, Object> val = entry.getValue();
      String name = entry.getKey();
      String addr = get("address", "localhost:25565", val);
      String motd = ChatColor.translateAlternateColorCodes('&', get("motd", "&1Just another " + BungeeCord.getInstance().getName() + " - Forced Host", val));
      boolean restricted = ((Boolean)get("restricted", Boolean.valueOf(false), val)).booleanValue();
      SocketAddress address = Util.getAddr(addr);
      ServerInfo info = ProxyServer.getInstance().constructServerInfo(name, address, motd, restricted);
      ret.put(name, info);
    } 
    return ret;
  }
  
  @SuppressFBWarnings({"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"})
  public Collection<ListenerInfo> getListeners() {
    Collection<Map<String, Object>> base = get("listeners", Arrays.asList((Map<String, Object>[])new Map[] { new HashMap<>() }));
    Map<String, String> forcedDef = new HashMap<>();
    forcedDef.put("pvp.md-5.net", "pvp");
    Collection<ListenerInfo> ret = new HashSet<>();
    for (Map<String, Object> val : base) {
      String motd = get("motd", "&1Another " + BungeeCord.getInstance().getName() + " server", val);
      motd = ChatColor.translateAlternateColorCodes('&', motd);
      int maxPlayers = ((Integer)get("max_players", Integer.valueOf(1), val)).intValue();
      boolean forceDefault = ((Boolean)get("force_default_server", Boolean.valueOf(false), val)).booleanValue();
      String host = get("host", "0.0.0.0:25577", val);
      int tabListSize = ((Integer)get("tab_size", Integer.valueOf(60), val)).intValue();
      SocketAddress address = Util.getAddr(host);
      CaseInsensitiveMap caseInsensitiveMap = new CaseInsensitiveMap(get("forced_hosts", forcedDef, val));
      String tabListName = get("tab_list", "GLOBAL_PING", val);
      DefaultTabList value = DefaultTabList.valueOf(tabListName.toUpperCase(Locale.ROOT));
      if (value == null)
        value = DefaultTabList.GLOBAL_PING; 
      boolean setLocalAddress = ((Boolean)get("bind_local_address", Boolean.valueOf(true), val)).booleanValue();
      boolean pingPassthrough = ((Boolean)get("ping_passthrough", Boolean.valueOf(false), val)).booleanValue();
      boolean query = ((Boolean)get("query_enabled", Boolean.valueOf(false), val)).booleanValue();
      int queryPort = ((Integer)get("query_port", Integer.valueOf(25577), val)).intValue();
      boolean proxyProtocol = ((Boolean)get("proxy_protocol", Boolean.valueOf(false), val)).booleanValue();
      List<String> serverPriority = new ArrayList<>(get("priorities", Collections.EMPTY_LIST, val));
      String defaultServer = get("default_server", null, val);
      String fallbackServer = get("fallback_server", null, val);
      if (defaultServer != null) {
        serverPriority.add(defaultServer);
        set("default_server", null, val);
      } 
      if (fallbackServer != null) {
        serverPriority.add(fallbackServer);
        set("fallback_server", null, val);
      } 
      if (serverPriority.isEmpty())
        serverPriority.add("lobby"); 
      set("priorities", serverPriority, val);
      ListenerInfo info = new ListenerInfo(address, motd, maxPlayers, tabListSize, serverPriority, forceDefault, (Map)caseInsensitiveMap, value.toString(), setLocalAddress, pingPassthrough, queryPort, query, proxyProtocol);
      ret.add(info);
    } 
    return ret;
  }
  
  public Collection<String> getGroups(String player) {
    Map<String, Collection<String>> raw = get("groups", Collections.emptyMap());
    Collection<String> groups = raw.get(player);
    Collection<String> ret = (groups == null) ? new HashSet<>() : new HashSet<>(groups);
    ret.add("default");
    return ret;
  }
  
  public Collection<?> getList(String path, Collection<?> def) {
    return get(path, def);
  }
  
  public Collection<String> getPermissions(String group) {
    Collection<String> permissions = get("permissions." + group, null);
    return (permissions == null) ? Collections.EMPTY_SET : permissions;
  }
}
