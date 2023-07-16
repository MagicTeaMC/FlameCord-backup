package net.md_5.bungee.api.plugin;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.Subscribe;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import io.github.waterfallmc.waterfall.event.ProxyExceptionEvent;
import io.github.waterfallmc.waterfall.exception.ProxyCommandException;
import io.github.waterfallmc.waterfall.exception.ProxyEventException;
import io.github.waterfallmc.waterfall.exception.ProxyException;
import io.github.waterfallmc.waterfall.exception.ProxyPluginEnableDisableException;
import io.github.waterfallmc.waterfall.exception.ProxyTabCompleteException;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Handler;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.event.EventBus;
import net.md_5.bungee.event.EventHandlerMethod;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

public final class PluginManager {
  private final ProxyServer proxy;
  
  private final Yaml yaml;
  
  private final EventBus eventBus;
  
  public PluginManager(ProxyServer proxy, Yaml yaml, EventBus eventBus, LibraryLoader libraryLoader) {
    this.proxy = proxy;
    this.yaml = yaml;
    this.eventBus = eventBus;
    this.libraryLoader = libraryLoader;
  }
  
  private final Map<String, Plugin> plugins = new LinkedHashMap<>();
  
  private final MutableGraph<String> dependencyGraph = GraphBuilder.directed().build();
  
  private final LibraryLoader libraryLoader;
  
  private final Map<String, Command> commandMap = new HashMap<>();
  
  private Map<String, PluginDescription> toLoad = new HashMap<>();
  
  private final Multimap<Plugin, Command> commandsByPlugin = (Multimap<Plugin, Command>)ArrayListMultimap.create();
  
  private final Multimap<Plugin, Listener> listenersByPlugin = (Multimap<Plugin, Listener>)ArrayListMultimap.create();
  
  private final HashMap<String, URLClassLoader> pluginloaders = new HashMap<>();
  
  public PluginManager(ProxyServer proxy) {
    this.proxy = proxy;
    Constructor yamlConstructor = new Constructor(new LoaderOptions());
    PropertyUtils propertyUtils = yamlConstructor.getPropertyUtils();
    propertyUtils.setSkipMissingProperties(true);
    yamlConstructor.setPropertyUtils(propertyUtils);
    this.yaml = new Yaml((BaseConstructor)yamlConstructor);
    this.eventBus = new EventBus(proxy.getLogger());
    LibraryLoader libraryLoader = null;
    try {
      libraryLoader = new LibraryLoader(proxy.getLogger());
    } catch (NoClassDefFoundError ex) {
      proxy.getLogger().warning("Could not initialize LibraryLoader (missing dependencies?)");
    } 
    this.libraryLoader = libraryLoader;
  }
  
  public void registerCommand(Plugin plugin, Command command) {
    this.commandMap.put(command.getName().toLowerCase(Locale.ROOT), command);
    for (String alias : command.getAliases())
      this.commandMap.put(alias.toLowerCase(Locale.ROOT), command); 
    this.commandsByPlugin.put(plugin, command);
  }
  
  public void unregisterCommand(Command command) {
    while (this.commandMap.values().remove(command));
    this.commandsByPlugin.values().remove(command);
  }
  
  public void unregisterCommands(Plugin plugin) {
    for (Iterator<Command> it = this.commandsByPlugin.get(plugin).iterator(); it.hasNext(); ) {
      Command command = it.next();
      while (this.commandMap.values().remove(command));
      it.remove();
    } 
  }
  
  private Command getCommandIfEnabled(String commandName, CommandSender sender) {
    String commandLower = commandName.toLowerCase(Locale.ROOT);
    if (sender instanceof net.md_5.bungee.api.connection.ProxiedPlayer && this.proxy.getDisabledCommands().contains(commandLower))
      return null; 
    return this.commandMap.get(commandLower);
  }
  
  public boolean isExecutableCommand(String commandName, CommandSender sender) {
    return (getCommandIfEnabled(commandName, sender) != null);
  }
  
  public boolean dispatchCommand(CommandSender sender, String commandLine) {
    return dispatchCommand(sender, commandLine, null);
  }
  
  public boolean dispatchCommand(CommandSender sender, String commandLine, List<String> tabResults) {
    String[] split = commandLine.split(" ", -1);
    if (split.length == 0 || split[0].isEmpty())
      return false; 
    Command command = getCommandIfEnabled(split[0], sender);
    if (command == null)
      return false; 
    if (!command.hasPermission(sender)) {
      if (tabResults == null)
        sender.sendMessage((command.getPermissionMessage() == null) ? this.proxy.getTranslation("no_permission", new Object[0]) : command.getPermissionMessage()); 
      return true;
    } 
    String[] args = Arrays.<String>copyOfRange(split, 1, split.length);
    if (tabResults == null) {
      try {
        if (this.proxy.getConfig().isLogCommands())
          this.proxy.getLogger().log(Level.INFO, "{0} executed command: /{1}", new Object[] { sender
                
                .getName(), commandLine }); 
        command.execute(sender, args);
      } catch (Exception ex) {
        sender.sendMessage(ChatColor.RED + "An internal error occurred whilst executing this command, please check the console log for details.");
        ProxyServer.getInstance().getLogger().log(Level.WARNING, "Error in dispatching command", ex);
        callEvent(new ProxyExceptionEvent((ProxyException)new ProxyCommandException(ex, command, sender, args)));
      } 
    } else if (commandLine.contains(" ") && command instanceof TabExecutor) {
      try {
        for (String s : ((TabExecutor)command).onTabComplete(sender, args))
          tabResults.add(s); 
      } catch (Exception ex) {
        sender.sendMessage(ChatColor.RED + "An internal error occurred whilst executing this command, please check the console log for details.");
        ProxyServer.getInstance().getLogger().log(Level.WARNING, "Error in dispatching command", ex);
        callEvent(new ProxyExceptionEvent((ProxyException)new ProxyTabCompleteException(ex, command, sender, args)));
      } 
    } 
    return true;
  }
  
  public List<String> tabCompleteCommand(CommandSender sender, String commandLine) {
    List<String> suggestions = new ArrayList<>();
    if (commandLine.indexOf(' ') == -1) {
      for (Command command : this.commandMap.values()) {
        if (command.getName().startsWith(commandLine)) {
          String permission = command.getPermission();
          if (permission == null || permission.isEmpty() || sender.hasPermission(permission))
            suggestions.add(command.getName()); 
        } 
      } 
    } else {
      dispatchCommand(sender, commandLine, suggestions);
    } 
    return suggestions;
  }
  
  public Collection<Plugin> getPlugins() {
    return this.plugins.values();
  }
  
  public Plugin getPlugin(String name) {
    return this.plugins.get(name);
  }
  
  public void unloadPlugin(Plugin plugin) {
    plugin.onDisable();
    unregisterListeners(plugin);
    unregisterCommands(plugin);
    this.proxy.getScheduler().cancel(plugin);
    for (Handler handler : plugin.getLogger().getHandlers())
      handler.close(); 
    try {
      ((URLClassLoader)this.pluginloaders.get(plugin.getDescription().getName())).close();
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    if (this.plugins.containsKey(plugin.getDescription().getName()))
      this.plugins.remove(plugin.getDescription().getName()); 
    if (this.pluginloaders.containsKey(plugin.getDescription().getName()))
      this.pluginloaders.remove(plugin.getDescription().getName()); 
  }
  
  public void loadPlugins() {
    Map<PluginDescription, Boolean> pluginStatuses = new HashMap<>();
    for (Map.Entry<String, PluginDescription> entry : this.toLoad.entrySet()) {
      PluginDescription plugin = entry.getValue();
      if (!enablePlugin(pluginStatuses, new Stack<>(), plugin))
        ProxyServer.getInstance().getLogger().log(Level.WARNING, "Failed to enable {0}", entry.getKey()); 
    } 
    this.toLoad.clear();
    this.toLoad = null;
  }
  
  public void enablePlugins() {
    for (Plugin plugin : this.plugins.values()) {
      try {
        plugin.onEnable();
        ProxyServer.getInstance().getLogger().log(Level.INFO, "Enabled plugin {0} version {1} by {2}", new Object[] { plugin
              
              .getDescription().getName(), plugin.getDescription().getVersion(), plugin.getDescription().getAuthor() });
      } catch (Throwable t) {
        String msg = "Exception encountered when loading plugin: " + plugin.getDescription().getName();
        ProxyServer.getInstance().getLogger().log(Level.WARNING, msg, t);
        callEvent(new ProxyExceptionEvent((ProxyException)new ProxyPluginEnableDisableException(msg, t, plugin)));
      } 
    } 
  }
  
  private boolean enablePlugin(Map<PluginDescription, Boolean> pluginStatuses, Stack<PluginDescription> dependStack, PluginDescription plugin) {
    if (pluginStatuses.containsKey(plugin))
      return ((Boolean)pluginStatuses.get(plugin)).booleanValue(); 
    Set<String> dependencies = new HashSet<>();
    dependencies.addAll(plugin.getDepends());
    dependencies.addAll(plugin.getSoftDepends());
    boolean status = true;
    for (String dependName : dependencies) {
      PluginDescription depend = this.toLoad.get(dependName);
      Boolean dependStatus = (depend != null) ? pluginStatuses.get(depend) : Boolean.FALSE;
      if (dependStatus == null)
        if (dependStack.contains(depend)) {
          StringBuilder dependencyGraph = new StringBuilder();
          for (PluginDescription element : dependStack)
            dependencyGraph.append(element.getName()).append(" -> "); 
          dependencyGraph.append(plugin.getName()).append(" -> ").append(dependName);
          ProxyServer.getInstance().getLogger().log(Level.WARNING, "Circular dependency detected: {0}", dependencyGraph);
          status = false;
        } else {
          dependStack.push(plugin);
          dependStatus = Boolean.valueOf(enablePlugin(pluginStatuses, dependStack, depend));
          dependStack.pop();
        }  
      if (dependStatus == Boolean.FALSE && plugin.getDepends().contains(dependName)) {
        ProxyServer.getInstance().getLogger().log(Level.WARNING, "{0} (required by {1}) is unavailable", new Object[] { String.valueOf(dependName), plugin.getName() });
        status = false;
      } 
      this.dependencyGraph.putEdge(plugin.getName(), dependName);
      if (!status)
        break; 
    } 
    if (status)
      try {
        URLClassLoader loader = new PluginClassloader(this.proxy, plugin, plugin.getFile(), (this.libraryLoader != null) ? this.libraryLoader.createLoader(plugin) : null);
        Class<?> main = loader.loadClass(plugin.getMain());
        Plugin clazz = main.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        this.plugins.put(plugin.getName(), clazz);
        this.pluginloaders.put(plugin.getName(), loader);
        clazz.onLoad();
        ProxyServer.getInstance().getLogger().log(Level.INFO, "Loaded plugin {0} version {1} by {2}", new Object[] { plugin
              
              .getName(), plugin.getVersion(), plugin.getAuthor() });
      } catch (Throwable t) {
        this.proxy.getLogger().log(Level.WARNING, "Error loading plugin " + plugin.getName(), t);
      }  
    pluginStatuses.put(plugin, Boolean.valueOf(status));
    return status;
  }
  
  public void detectPlugins(File folder) {
    Preconditions.checkNotNull(folder, "folder");
    Preconditions.checkArgument(folder.isDirectory(), "Must load from a directory");
    for (File file : folder.listFiles()) {
      if (file.isFile() && file.getName().endsWith(".jar"))
        try (JarFile jar = new JarFile(file)) {
          JarEntry pdf = jar.getJarEntry("bungee.yml");
          if (pdf == null)
            pdf = jar.getJarEntry("plugin.yml"); 
          Preconditions.checkNotNull(pdf, "Plugin must have a plugin.yml or bungee.yml");
          try (InputStream in = jar.getInputStream(pdf)) {
            PluginDescription desc = (PluginDescription)this.yaml.loadAs(in, PluginDescription.class);
            Preconditions.checkNotNull(desc.getName(), "Plugin from %s has no name", file);
            Preconditions.checkNotNull(desc.getMain(), "Plugin from %s has no main", file);
            desc.setFile(file);
            this.toLoad.put(desc.getName(), desc);
          } 
        } catch (Exception ex) {
          ProxyServer.getInstance().getLogger().log(Level.WARNING, "Could not load plugin from file " + file, ex);
        }  
    } 
  }
  
  public <T extends Event> T callEvent(T event) {
    Preconditions.checkNotNull(event, "event");
    long start = System.nanoTime();
    this.eventBus.post(event, this::handleEventException);
    event.postCall();
    long elapsed = System.nanoTime() - start;
    if (elapsed > 250000000L)
      ProxyServer.getInstance().getLogger().log(Level.WARNING, "Event {0} took {1}ms to process!", new Object[] { event, 
            
            Long.valueOf(elapsed / 1000000L) }); 
    return event;
  }
  
  private <T extends Event> void handleEventException(String msg, T event, EventHandlerMethod method, Throwable ex) {
    if (!(event instanceof ProxyExceptionEvent))
      callEvent(new ProxyExceptionEvent((ProxyException)new ProxyEventException(msg, ex, (Listener)method.getListener(), (Event)event))); 
  }
  
  public void registerListener(Plugin plugin, Listener listener) {
    for (Method method : listener.getClass().getDeclaredMethods())
      Preconditions.checkArgument(!method.isAnnotationPresent((Class)Subscribe.class), "Listener %s has registered using deprecated subscribe annotation! Please update to @EventHandler.", listener); 
    this.eventBus.register(listener);
    this.listenersByPlugin.put(plugin, listener);
  }
  
  public void unregisterListener(Listener listener) {
    this.eventBus.unregister(listener);
    this.listenersByPlugin.values().remove(listener);
  }
  
  public void unregisterListeners(Plugin plugin) {
    for (Iterator<Listener> it = this.listenersByPlugin.get(plugin).iterator(); it.hasNext(); ) {
      this.eventBus.unregister(it.next());
      it.remove();
    } 
  }
  
  public Collection<Map.Entry<String, Command>> getCommands() {
    return Collections.unmodifiableCollection(this.commandMap.entrySet());
  }
  
  boolean isTransitiveDepend(PluginDescription plugin, PluginDescription depend) {
    Preconditions.checkArgument((plugin != null), "plugin");
    Preconditions.checkArgument((depend != null), "depend");
    if (this.dependencyGraph.nodes().contains(plugin.getName()))
      if (Graphs.reachableNodes((Graph)this.dependencyGraph, plugin.getName()).contains(depend.getName()))
        return true;  
    return false;
  }
}
