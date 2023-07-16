package dev._2lstudios.flamecord.configuration;

import dev._2lstudios.flamecord.utils.ColorUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;

public class FlameCordConfiguration extends FlameConfig {
  private boolean allowInvalidNames = false;
  
  public boolean isAllowInvalidNames() {
    return this.allowInvalidNames;
  }
  
  private boolean antibotAccountsEnabled = true;
  
  public boolean isAntibotAccountsEnabled() {
    return this.antibotAccountsEnabled;
  }
  
  private boolean antibotAccountsFirewall = true;
  
  public boolean isAntibotAccountsFirewall() {
    return this.antibotAccountsFirewall;
  }
  
  private int antibotAccountsLimit = 3;
  
  public int getAntibotAccountsLimit() {
    return this.antibotAccountsLimit;
  }
  
  private boolean antibotAccountsLog = true;
  
  public boolean isAntibotAccountsLog() {
    return this.antibotAccountsLog;
  }
  
  public Collection<String> getAntibotAccountsWhitelist() {
    return this.antibotAccountsWhitelist;
  }
  
  private Collection<String> antibotAccountsWhitelist = Arrays.asList(new String[] { "Nickname" });
  
  private boolean antibotCountryEnabled = true;
  
  public boolean isAntibotCountryEnabled() {
    return this.antibotCountryEnabled;
  }
  
  private boolean antibotCountryFirewall = true;
  
  public boolean isAntibotCountryFirewall() {
    return this.antibotCountryFirewall;
  }
  
  public Collection<String> getAntibotCountryBlacklist() {
    return this.antibotCountryBlacklist;
  }
  
  private Collection<String> antibotCountryBlacklist = Arrays.asList(new String[] { "CN", "HK", "RU", "IN", "TH", "ID", "DZ", "VN", "IR", "PK" });
  
  public Collection<String> getAntibotFirewalledExceptions() {
    return this.antibotFirewalledExceptions;
  }
  
  private Collection<String> antibotFirewalledExceptions = Arrays.asList(new String[] { "BadPacketException", "QuietException", "IllegalStateConfig", "FastException" });
  
  private boolean antibotCountryLog = true;
  
  public boolean isAntibotCountryLog() {
    return this.antibotCountryLog;
  }
  
  private boolean antibotFastChatEnabled = true;
  
  public boolean isAntibotFastChatEnabled() {
    return this.antibotFastChatEnabled;
  }
  
  private boolean antibotFastChatFirewall = true;
  
  public boolean isAntibotFastChatFirewall() {
    return this.antibotFastChatFirewall;
  }
  
  private int antibotFastChatTime = 1000;
  
  public int getAntibotFastChatTime() {
    return this.antibotFastChatTime;
  }
  
  private boolean antibotFastChatLog = true;
  
  public boolean isAntibotFastChatLog() {
    return this.antibotFastChatLog;
  }
  
  private boolean antibotFirewallEnabled = true;
  
  public boolean isAntibotFirewallEnabled() {
    return this.antibotFirewallEnabled;
  }
  
  private int antibotFirewallExpire = 60;
  
  public int getAntibotFirewallExpire() {
    return this.antibotFirewallExpire;
  }
  
  private int compressionLevel = 6;
  
  public int getCompressionLevel() {
    return this.compressionLevel;
  }
  
  private boolean antibotFirewallLog = true;
  
  public boolean isAntibotFirewallLog() {
    return this.antibotFirewallLog;
  }
  
  private boolean antibotFirewallIpset = true;
  
  public boolean isAntibotFirewallIpset() {
    return this.antibotFirewallIpset;
  }
  
  public Collection<String> getAntibotFirewallWhitelist() {
    return this.antibotFirewallWhitelist;
  }
  
  private Collection<String> antibotFirewallWhitelist = Arrays.asList(new String[] { "127.0.0.1" });
  
  private boolean antibotNicknameEnabled = true;
  
  public boolean isAntibotNicknameEnabled() {
    return this.antibotNicknameEnabled;
  }
  
  private boolean antibotNicknameFirewall = true;
  
  public boolean isAntibotNicknameFirewall() {
    return this.antibotNicknameFirewall;
  }
  
  public Collection<String> getAntibotNicknameBlacklist() {
    return this.antibotNicknameBlacklist;
  }
  
  private Collection<String> antibotNicknameBlacklist = Arrays.asList(new String[] { "mcstorm", "mcdown", "mcbot", "theresa_bot", "dropbot", "kingbot" });
  
  private boolean antibotNicknameLog = true;
  
  public boolean isAntibotNicknameLog() {
    return this.antibotNicknameLog;
  }
  
  private boolean antibotPasswordEnabled = true;
  
  public boolean isAntibotPasswordEnabled() {
    return this.antibotPasswordEnabled;
  }
  
  private boolean antibotPasswordFirewall = true;
  
  public boolean isAntibotPasswordFirewall() {
    return this.antibotPasswordFirewall;
  }
  
  private int antibotPasswordLimit = 3;
  
  public int getAntibotPasswordLimit() {
    return this.antibotPasswordLimit;
  }
  
  private boolean antibotPasswordLog = true;
  
  public boolean isAntibotPasswordLog() {
    return this.antibotPasswordLog;
  }
  
  private boolean antibotRatelimitEnabled = true;
  
  public boolean isAntibotRatelimitEnabled() {
    return this.antibotRatelimitEnabled;
  }
  
  private boolean antibotRatelimitFirewall = true;
  
  public boolean isAntibotRatelimitFirewall() {
    return this.antibotRatelimitFirewall;
  }
  
  private int antibotRatelimitConnectionsPerSecond = 3;
  
  public int getAntibotRatelimitConnectionsPerSecond() {
    return this.antibotRatelimitConnectionsPerSecond;
  }
  
  private int antibotRatelimitPingsPerSecond = 8;
  
  public int getAntibotRatelimitPingsPerSecond() {
    return this.antibotRatelimitPingsPerSecond;
  }
  
  private boolean antibotRatelimitLog = true;
  
  public boolean isAntibotRatelimitLog() {
    return this.antibotRatelimitLog;
  }
  
  public Collection<String> getAntibotRatelimitWhitelist() {
    return this.antibotRatelimitWhitelist;
  }
  
  private Collection<String> antibotRatelimitWhitelist = Arrays.asList(new String[] { "127.0.0.1" });
  
  private boolean antibotReconnectEnabled = true;
  
  public boolean isAntibotReconnectEnabled() {
    return this.antibotReconnectEnabled;
  }
  
  private int antibotReconnectAttempts = 2;
  
  public int getAntibotReconnectAttempts() {
    return this.antibotReconnectAttempts;
  }
  
  private int antibotReconnectPings = 1;
  
  public int getAntibotReconnectPings() {
    return this.antibotReconnectPings;
  }
  
  private int antibotReconnectMaxTime = 10000;
  
  public int getAntibotReconnectMaxTime() {
    return this.antibotReconnectMaxTime;
  }
  
  private int antibotReconnectConnectionThreshold = 1;
  
  public int getAntibotReconnectConnectionThreshold() {
    return this.antibotReconnectConnectionThreshold;
  }
  
  private int antibotReconnectConnectionThresholdLimit = 8000;
  
  public int getAntibotReconnectConnectionThresholdLimit() {
    return this.antibotReconnectConnectionThresholdLimit;
  }
  
  private boolean antibotReconnectLog = true;
  
  public boolean isAntibotReconnectLog() {
    return this.antibotReconnectLog;
  }
  
  private boolean antibotPacketsEnabled = true;
  
  public boolean isAntibotPacketsEnabled() {
    return this.antibotPacketsEnabled;
  }
  
  private boolean antibotPacketsLog = true;
  
  public boolean isAntibotPacketsLog() {
    return this.antibotPacketsLog;
  }
  
  private boolean antibotPacketsDebug = false;
  
  public boolean isAntibotPacketsDebug() {
    return this.antibotPacketsDebug;
  }
  
  private double antibotPacketsVlsPerByte = 0.0017D;
  
  public double getAntibotPacketsVlsPerByte() {
    return this.antibotPacketsVlsPerByte;
  }
  
  private double antibotPacketsVlsPerPacket = 0.1D;
  
  public double getAntibotPacketsVlsPerPacket() {
    return this.antibotPacketsVlsPerPacket;
  }
  
  private double antibotPacketsVlsToKick = 100.0D;
  
  public double getAntibotPacketsVlsToKick() {
    return this.antibotPacketsVlsToKick;
  }
  
  private double antibotPacketsVlsToCancel = 25.0D;
  
  public double getAntibotPacketsVlsToCancel() {
    return this.antibotPacketsVlsToCancel;
  }
  
  private boolean antibotProxyEnabled = true;
  
  public boolean isAntibotProxyEnabled() {
    return this.antibotProxyEnabled;
  }
  
  private boolean antibotProxyLog = true;
  
  public boolean isAntibotProxyLog() {
    return this.antibotProxyLog;
  }
  
  private boolean antibotProxyFirewall = true;
  
  public boolean isAntibotProxyFirewall() {
    return this.antibotProxyFirewall;
  }
  
  private boolean antibotProxyOnlineCheck = false;
  
  public boolean isAntibotProxyOnlineCheck() {
    return this.antibotProxyOnlineCheck;
  }
  
  private String antibotProxyEmail = "flamecord@gmail.com";
  
  public String getAntibotProxyEmail() {
    return this.antibotProxyEmail;
  }
  
  public Collection<String> getAntibotProxyWhitelist() {
    return this.antibotProxyWhitelist;
  }
  
  private Collection<String> antibotProxyWhitelist = Arrays.asList(new String[] { "127.0.0.1" });
  
  public Collection<String> getAntibotProxyLists() {
    return this.antibotProxyLists;
  }
  
  private Collection<String> antibotProxyLists = Arrays.asList(new String[] { "https://raw.githubusercontent.com/TheSpeedX/PROXY-List/master/http.txt", "https://raw.githubusercontent.com/clarketm/proxy-list/master/proxy-list-raw.txt", "https://raw.githubusercontent.com/mertguvencli/http-proxy-list/main/proxy-list/data.txt", "https://raw.githubusercontent.com/scriptzteam/ProtonVPN-VPN-IPs/main/exit_ips.txt", "https://raw.githubusercontent.com/mmpx12/proxy-list/master/ips-list.txt", "https://check.torproject.org/torbulkexitlist?ip=1.1.1.1", "https://cinsscore.com/list/ci-badguys.txt", "https://lists.blocklist.de/lists/all.txt", "https://blocklist.greensnow.co/greensnow.txt", "https://raw.githubusercontent.com/firehol/blocklist-ipsets/master/stopforumspam_7d.ipset" });
  
  public void loadAntibot(Configuration config, Collection<String> whitelistedAddresses) {
    this.antibotAccountsEnabled = setIfUnexistant("antibot.accounts.enabled", this.antibotAccountsEnabled, config);
    this.antibotAccountsFirewall = setIfUnexistant("antibot.accounts.firewall", this.antibotAccountsFirewall, config);
    this.antibotAccountsLimit = setIfUnexistant("antibot.accounts.limit", this.antibotAccountsLimit, config);
    this.antibotAccountsLog = setIfUnexistant("antibot.accounts.log", this.antibotAccountsLog, config);
    this.antibotAccountsWhitelist = setIfUnexistant("antibot.accounts.whitelist", this.antibotAccountsWhitelist, config);
    this.antibotCountryEnabled = setIfUnexistant("antibot.country.enabled", this.antibotCountryEnabled, config);
    this.antibotCountryFirewall = setIfUnexistant("antibot.country.firewall", this.antibotCountryFirewall, config);
    this.antibotCountryBlacklist = setIfUnexistant("antibot.country.blacklist", this.antibotCountryBlacklist, config);
    this.antibotCountryLog = setIfUnexistant("antibot.country.log", this.antibotCountryLog, config);
    this.antibotFastChatEnabled = setIfUnexistant("antibot.fastchat.enabled", this.antibotFastChatEnabled, config);
    this.antibotFastChatFirewall = setIfUnexistant("antibot.fastchat.firewall", this.antibotFastChatFirewall, config);
    this.antibotFastChatTime = setIfUnexistant("antibot.fastchat.time", this.antibotFastChatTime, config);
    this.antibotFastChatLog = setIfUnexistant("antibot.fastchat.log", this.antibotFastChatLog, config);
    this.antibotFirewallEnabled = setIfUnexistant("antibot.firewall.enabled", this.antibotFirewallEnabled, config);
    this.antibotFirewalledExceptions = setIfUnexistant("antibot.firewall.exceptions", this.antibotFirewalledExceptions, config);
    this.antibotFirewallExpire = setIfUnexistant("antibot.firewall.time", this.antibotFirewallExpire, config);
    this.antibotFirewallLog = setIfUnexistant("antibot.firewall.log", this.antibotFirewallLog, config);
    this.antibotFirewallWhitelist = new HashSet<>(setIfUnexistant("antibot.firewall.whitelist", this.antibotFirewallWhitelist, config));
    this.antibotFirewallIpset = setIfUnexistant("antibot.firewall.ipset", this.antibotFirewallIpset, config);
    this.antibotFirewallWhitelist.addAll(whitelistedAddresses);
    this.antibotNicknameEnabled = setIfUnexistant("antibot.nickname.enabled", this.antibotNicknameEnabled, config);
    this.antibotNicknameFirewall = setIfUnexistant("antibot.nickname.firewall", this.antibotNicknameFirewall, config);
    this.antibotNicknameBlacklist = setIfUnexistant("antibot.nickname.blacklist", this.antibotNicknameBlacklist, config);
    this.antibotNicknameLog = setIfUnexistant("antibot.nickname.log", this.antibotNicknameLog, config);
    this.antibotPasswordEnabled = setIfUnexistant("antibot.password.enabled", this.antibotPasswordEnabled, config);
    this.antibotPasswordFirewall = setIfUnexistant("antibot.password.firewall", this.antibotPasswordFirewall, config);
    this.antibotPasswordLimit = setIfUnexistant("antibot.password.limit", this.antibotPasswordLimit, config);
    this.antibotPasswordLog = setIfUnexistant("antibot.password.log", this.antibotPasswordLog, config);
    this.antibotRatelimitEnabled = setIfUnexistant("antibot.ratelimit.enabled", this.antibotRatelimitEnabled, config);
    this.antibotRatelimitFirewall = setIfUnexistant("antibot.ratelimit.firewall", this.antibotRatelimitFirewall, config);
    this.antibotRatelimitConnectionsPerSecond = setIfUnexistant("antibot.ratelimit.connections-per-second", this.antibotRatelimitConnectionsPerSecond, config);
    this.antibotRatelimitPingsPerSecond = setIfUnexistant("antibot.ratelimit.pings-per-second", this.antibotRatelimitPingsPerSecond, config);
    this.antibotRatelimitLog = setIfUnexistant("antibot.ratelimit.log", this.antibotRatelimitLog, config);
    this.antibotRatelimitWhitelist = new HashSet<>(setIfUnexistant("antibot.ratelimit.whitelist", this.antibotRatelimitWhitelist, config));
    this.antibotReconnectEnabled = setIfUnexistant("antibot.reconnect.enabled", this.antibotReconnectEnabled, config);
    this.antibotReconnectAttempts = setIfUnexistant("antibot.reconnect.attempts", this.antibotReconnectAttempts, config);
    this.antibotReconnectPings = setIfUnexistant("antibot.reconnect.pings", this.antibotReconnectPings, config);
    this.antibotReconnectMaxTime = setIfUnexistant("antibot.reconnect.max-time", this.antibotReconnectMaxTime, config);
    this.antibotReconnectConnectionThreshold = setIfUnexistant("antibot.reconnect.connection-threshold", this.antibotReconnectConnectionThreshold, config);
    this.antibotReconnectConnectionThresholdLimit = setIfUnexistant("antibot.reconnect.connection-threshold-limit", this.antibotReconnectConnectionThresholdLimit, config);
    this.antibotReconnectLog = setIfUnexistant("antibot.reconnect.log", this.antibotReconnectLog, config);
    this.antibotPacketsEnabled = setIfUnexistant("antibot.packets.enabled", this.antibotPacketsEnabled, config);
    this.antibotPacketsLog = setIfUnexistant("antibot.packets.log", this.antibotPacketsLog, config);
    this.antibotPacketsDebug = setIfUnexistant("antibot.packets.debug", this.antibotPacketsDebug, config);
    this.antibotPacketsVlsPerByte = setIfUnexistant("antibot.packets.vls-per-byte", this.antibotPacketsVlsPerByte, config);
    this.antibotPacketsVlsPerPacket = setIfUnexistant("antibot.packets.vls-per-packet", this.antibotPacketsVlsPerPacket, config);
    this.antibotPacketsVlsToKick = setIfUnexistant("antibot.packets.vls-to-kick", this.antibotPacketsVlsToKick, config);
    this.antibotProxyEnabled = setIfUnexistant("antibot.proxy.enabled", this.antibotProxyEnabled, config);
    this.antibotProxyLog = setIfUnexistant("antibot.proxy.log", this.antibotProxyLog, config);
    this.antibotProxyFirewall = setIfUnexistant("antibot.proxy.firewall", this.antibotProxyFirewall, config);
    this.antibotProxyOnlineCheck = setIfUnexistant("antibot.proxy.online-check", this.antibotProxyOnlineCheck, config);
    this.antibotProxyEmail = setIfUnexistant("antibot.proxy.email", this.antibotProxyEmail, config);
    this.antibotProxyWhitelist = setIfUnexistant("antibot.proxy.whitelist", this.antibotProxyWhitelist, config);
    this.antibotProxyLists = setIfUnexistant("antibot.proxy.lists", this.antibotProxyLists, config);
  }
  
  private int tcpFastOpen = 3;
  
  public int getTcpFastOpen() {
    return this.tcpFastOpen;
  }
  
  public String getMOTD(int maxPlayers, int onlinePlayers, int protocol) {
    if (protocol >= 735) {
      motd = this.hexMotds.get((new Random()).nextInt(this.hexMotds.size()));
    } else {
      motd = this.motds.get((new Random()).nextInt(this.motds.size()));
    } 
    String motd = motd.replace("%maxplayers%", String.valueOf(maxPlayers)).replace("%onlineplayers%", String.valueOf(onlinePlayers));
    return motd;
  }
  
  public String[] getSample(int maxPlayers, int onlinePlayers, int protocol) {
    if (protocol >= 735) {
      sample = this.hexSamples.get((new Random()).nextInt(this.hexSamples.size()));
    } else {
      sample = this.samples.get((new Random()).nextInt(this.samples.size()));
    } 
    String sample = sample.replace("%maxplayers%", String.valueOf(maxPlayers)).replace("%onlineplayers%", String.valueOf(onlinePlayers));
    return sample.split("\n");
  }
  
  public String getProtocolName(int maxPlayers, int onlinePlayers) {
    return this.protocolName.replace("%maxplayers%", String.valueOf(maxPlayers)).replace("%onlineplayers%", String.valueOf(onlinePlayers));
  }
  
  public int getFakePlayersAmount(int players) {
    switch (this.fakePlayersMode) {
      case "STATIC":
        return this.fakePlayersAmount;
      case "RANDOM":
        return (int)(Math.floor(Math.random() * this.fakePlayersAmount) + 1.0D);
      case "DIVISION":
        return players / this.fakePlayersAmount;
    } 
    return 0;
  }
  
  private boolean motdEnabled = false;
  
  public boolean isMotdEnabled() {
    return this.motdEnabled;
  }
  
  private List<String> motds = Collections.singletonList("&eDefault &cFlameCord&e server &7(%onlineplayers%/%maxplayers%)\n&eEdit on &cflamecord.yml&7 (IridiumColorAPI HEX)");
  
  private List<String> hexMotds;
  
  private boolean sampleEnabled = false;
  
  public boolean isSampleEnabled() {
    return this.sampleEnabled;
  }
  
  private List<String> samples = Collections.singletonList("&eDefault &cFlameCord&e server &7(%onlineplayers%/%maxplayers%)\n&eEdit on &cflamecord.yml&7 (IridiumColorAPI HEX)");
  
  private List<String> hexSamples;
  
  private boolean protocolEnabled = false;
  
  public boolean isProtocolEnabled() {
    return this.protocolEnabled;
  }
  
  private String protocolName = "&c&lMaintenance";
  
  public String getProtocolName() {
    return this.protocolName;
  }
  
  private boolean protocolAlwaysShow = false;
  
  public boolean isProtocolAlwaysShow() {
    return this.protocolAlwaysShow;
  }
  
  private boolean maxPlayersEnabled = false;
  
  public boolean isMaxPlayersEnabled() {
    return this.maxPlayersEnabled;
  }
  
  private int maxPlayersAmount = 1000;
  
  public int getMaxPlayersAmount() {
    return this.maxPlayersAmount;
  }
  
  private boolean maxPlayersOneMore = false;
  
  public boolean isMaxPlayersOneMore() {
    return this.maxPlayersOneMore;
  }
  
  private boolean fakePlayersEnabled = false;
  
  public boolean isFakePlayersEnabled() {
    return this.fakePlayersEnabled;
  }
  
  private int fakePlayersAmount = 3;
  
  public int getFakePlayersAmount() {
    return this.fakePlayersAmount;
  }
  
  private String fakePlayersMode = "DIVISION";
  
  private boolean loggerInitialhandler = false;
  
  public boolean isLoggerInitialhandler() {
    return this.loggerInitialhandler;
  }
  
  private boolean loggerExceptions = false;
  
  public boolean isLoggerExceptions() {
    return this.loggerExceptions;
  }
  
  private boolean loggerDump = false;
  
  public boolean isLoggerDump() {
    return this.loggerDump;
  }
  
  private boolean loggerHaProxy = false;
  
  public boolean isLoggerHaProxy() {
    return this.loggerHaProxy;
  }
  
  private boolean loggerDetailedConnection = true;
  
  public boolean isLoggerDetailedConnection() {
    return this.loggerDetailedConnection;
  }
  
  public FlameCordConfiguration(ConfigurationProvider configurationProvider, Collection<String> whitelistedAddresses) {
    File configurationFile = new File("./flamecord.yml");
    Configuration configuration = load(configurationFile);
    this.loggerInitialhandler = setIfUnexistant("logger.initialhandler", this.loggerInitialhandler, configuration);
    this.loggerExceptions = setIfUnexistant("logger.exceptions", this.loggerExceptions, configuration);
    this.loggerDump = setIfUnexistant("logger.dump", this.loggerDump, configuration);
    this.loggerHaProxy = setIfUnexistant("logger.haproxy", this.loggerHaProxy, configuration);
    this.loggerDetailedConnection = setIfUnexistant("logger.detailed-connect-errors", this.loggerDetailedConnection, configuration);
    this.motdEnabled = setIfUnexistant("custom-motd.motd.enabled", this.motdEnabled, configuration);
    this.hexMotds = ColorUtil.hexColor(new ArrayList<>(setIfUnexistant("custom-motd.motd.motds", this.motds, configuration)), 735);
    this.motds = ColorUtil.hexColor(new ArrayList<>(setIfUnexistant("custom-motd.motd.motds", this.motds, configuration)), 734);
    this.sampleEnabled = setIfUnexistant("custom-motd.sample.enabled", this.sampleEnabled, configuration);
    this.hexSamples = ColorUtil.hexColor(new ArrayList<>(setIfUnexistant("custom-motd.sample.samples", this.samples, configuration)), 735);
    this.samples = ColorUtil.hexColor(new ArrayList<>(setIfUnexistant("custom-motd.sample.samples", this.samples, configuration)), 734);
    this.protocolEnabled = setIfUnexistant("custom-motd.protocol.enabled", this.protocolEnabled, configuration);
    this.protocolName = ColorUtil.hexColor(setIfUnexistant("custom-motd.protocol.name", this.protocolName, configuration), 735);
    this.protocolAlwaysShow = setIfUnexistant("custom-motd.protocol.always-show", this.protocolAlwaysShow, configuration);
    this.maxPlayersEnabled = setIfUnexistant("custom-motd.maxplayers.enabled", this.maxPlayersEnabled, configuration);
    this.maxPlayersAmount = setIfUnexistant("custom-motd.maxplayers.amount", this.maxPlayersAmount, configuration);
    this.maxPlayersOneMore = setIfUnexistant("custom-motd.maxplayers.justonemore", this.maxPlayersOneMore, configuration);
    this.fakePlayersEnabled = setIfUnexistant("custom-motd.fakeplayers.enabled", this.fakePlayersEnabled, configuration);
    this.fakePlayersAmount = setIfUnexistant("custom-motd.fakeplayers.amount", this.fakePlayersAmount, configuration);
    this.fakePlayersMode = setIfUnexistant("custom-motd.fakeplayers.mode", this.fakePlayersMode, configuration);
    this.tcpFastOpen = setIfUnexistant("tcp-fast-open", this.tcpFastOpen, configuration);
    loadAntibot(configuration, whitelistedAddresses);
    this.allowInvalidNames = setIfUnexistant("allow-invalid-names", this.allowInvalidNames, configuration);
    this.compressionLevel = setIfUnexistant("compression-level", this.compressionLevel, configuration);
    save(configuration, configurationFile);
  }
}
