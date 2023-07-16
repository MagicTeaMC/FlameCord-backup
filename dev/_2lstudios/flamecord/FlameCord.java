package dev._2lstudios.flamecord;

import dev._2lstudios.flamecord.antibot.AddressDataManager;
import dev._2lstudios.flamecord.antibot.CheckManager;
import dev._2lstudios.flamecord.antibot.LoggerWrapper;
import dev._2lstudios.flamecord.antibot.StatsData;
import dev._2lstudios.flamecord.configuration.FlameCordConfiguration;
import dev._2lstudios.flamecord.configuration.MessagesConfiguration;
import dev._2lstudios.flamecord.configuration.ModulesConfiguration;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class FlameCord {
  private static FlameCord instance;
  
  private static Runtime runtime;
  
  private FlameCordConfiguration flameCordConfiguration;
  
  private ModulesConfiguration modulesConfiguration;
  
  private MessagesConfiguration messagesConfiguration;
  
  private AddressDataManager addressDataManager;
  
  private CheckManager checkManager;
  
  private StatsData statsData;
  
  private LoggerWrapper loggerWrapper;
  
  public static FlameCord getInstance() {
    return instance;
  }
  
  public static void initialize(Logger logger, Collection<String> whitelistedAddresses) {
    if (runtime == null)
      runtime = Runtime.getRuntime(); 
    if (instance == null)
      instance = new FlameCord(); 
    instance.reload(logger, whitelistedAddresses);
  }
  
  public FlameCordConfiguration getFlameCordConfiguration() {
    return this.flameCordConfiguration;
  }
  
  public ModulesConfiguration getModulesConfiguration() {
    return this.modulesConfiguration;
  }
  
  public MessagesConfiguration getMessagesConfiguration() {
    return this.messagesConfiguration;
  }
  
  public AddressDataManager getAddressDataManager() {
    return this.addressDataManager;
  }
  
  public CheckManager getCheckManager() {
    return this.checkManager;
  }
  
  public StatsData getStatsData() {
    return this.statsData;
  }
  
  public LoggerWrapper getLoggerWrapper() {
    return this.loggerWrapper;
  }
  
  public void reload(Logger logger, Collection<String> whitelistedAddresses) {
    ConfigurationProvider configurationProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    this.flameCordConfiguration = new FlameCordConfiguration(configurationProvider, whitelistedAddresses);
    this.modulesConfiguration = new ModulesConfiguration(configurationProvider);
    this.messagesConfiguration = new MessagesConfiguration(logger, configurationProvider);
    if (this.checkManager != null)
      this.checkManager.unload(); 
    this.loggerWrapper = new LoggerWrapper(Logger.getLogger("BungeeCord"));
    this.addressDataManager = new AddressDataManager();
    this.checkManager = new CheckManager(this.addressDataManager, this.flameCordConfiguration);
    this.statsData = new StatsData();
    if (this.flameCordConfiguration.isAntibotFirewallIpset()) {
      runLinuxCommand("apt install iptables -y");
      runLinuxCommand("apt install ipset -y");
      runLinuxCommand("ipset destroy flamecord-firewall");
      runLinuxCommand("ipset create flamecord-firewall hash:ip timeout 60");
      runLinuxCommand("iptables -I INPUT -m set --match-set flamecord-firewall src -j DROP");
      (new Thread(() -> {
            FlameCord flameCord = getInstance();
            while (!flameCord.isShuttingDown()) {
              flameCord.runQueuedLinuxCommands();
              try {
                Thread.sleep(1000L);
              } catch (InterruptedException interruptedException) {}
            } 
          })).start();
    } 
  }
  
  private boolean shutdown = false;
  
  public boolean isShuttingDown() {
    return this.shutdown;
  }
  
  public void shutdown() {
    this.shutdown = true;
  }
  
  public void runLinuxCommand(String command) {
    try {
      runtime.exec("/bin/sh -c '" + command + "'");
    } catch (IOException iOException) {}
  }
  
  private Collection<String> commandQueue = ConcurrentHashMap.newKeySet();
  
  private boolean processing = false;
  
  public void queueLinuxCommand(String command) {
    this.commandQueue.add(command);
  }
  
  public void runQueuedLinuxCommands() {
    if (!this.commandQueue.isEmpty() && !this.processing) {
      this.processing = true;
      try {
        Iterator<String> iterator = this.commandQueue.iterator();
        StringBuilder commands = new StringBuilder();
        int ranCommands = 0;
        while (iterator.hasNext()) {
          String command = iterator.next();
          if (ranCommands++ > 0)
            commands.append(" && "); 
          commands.append(command);
          iterator.remove();
        } 
        runLinuxCommand(commands.toString());
        getLoggerWrapper().log(Level.INFO, "Blacklisted " + ranCommands + " ips from the kernel with IPSet", new Object[0]);
      } finally {
        this.processing = false;
      } 
    } 
  }
}
