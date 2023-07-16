package dev._2lstudios.flamecord.antibot;

import dev._2lstudios.flamecord.FlameCord;
import dev._2lstudios.flamecord.configuration.FlameCordConfiguration;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;

public class AddressData {
  private FlameCordConfiguration config;
  
  private StatsData statsData;
  
  private Collection<String> nicknames = null;
  
  private final String hostString;
  
  private String lastNickname = "";
  
  private String country = null;
  
  private String firewallReason = null;
  
  private long lastPing = 0L;
  
  private long penultimateConnection = 0L;
  
  private long lastConnection = 0L;
  
  private long lastFirewall = 0L;
  
  private int pingsSecond = 0;
  
  private int totalPings = 0;
  
  private int connectionsSecond = 0;
  
  private int totalConnections = 0;
  
  public AddressData(String hostString) {
    this.config = FlameCord.getInstance().getFlameCordConfiguration();
    this.statsData = FlameCord.getInstance().getStatsData();
    this.hostString = hostString;
  }
  
  public Collection<String> getNicknames() {
    if (this.nicknames == null)
      this.nicknames = new HashSet<>(); 
    return this.nicknames;
  }
  
  public String getLastNickname() {
    return this.lastNickname;
  }
  
  public void addNickname(String nickname) {
    if (this.nicknames == null)
      this.nicknames = new HashSet<>(); 
    if (!this.lastNickname.equals(nickname)) {
      this.lastNickname = nickname;
      this.totalConnections = 1;
    } 
    this.nicknames.add(nickname);
  }
  
  public long getPenultimateConnection() {
    return this.penultimateConnection;
  }
  
  public long getTimeSincePenultimateConnection() {
    return System.currentTimeMillis() - this.penultimateConnection;
  }
  
  public long getLastConnection() {
    return this.lastConnection;
  }
  
  public long getTimeSinceLastConnection() {
    return System.currentTimeMillis() - this.lastConnection;
  }
  
  private void updatePingsSecond() {
    if (System.currentTimeMillis() - this.lastPing >= 1000L)
      this.pingsSecond = 0; 
  }
  
  public int getPingsSecond() {
    updatePingsSecond();
    return this.pingsSecond;
  }
  
  public void addPing() {
    this.statsData.addPing();
    updatePingsSecond();
    this.lastPing = System.currentTimeMillis();
    this.pingsSecond++;
    this.totalPings++;
  }
  
  public int getTotalPings() {
    return this.totalPings;
  }
  
  private void updateConnectionsSecond() {
    if (System.currentTimeMillis() - this.lastConnection >= 1000L)
      this.connectionsSecond = 0; 
  }
  
  public int getConnectionsSecond() {
    updateConnectionsSecond();
    return this.connectionsSecond;
  }
  
  public void addConnection() {
    long currentTime = System.currentTimeMillis();
    this.statsData.addConnection();
    updateConnectionsSecond();
    this.penultimateConnection = (this.lastConnection == 0L) ? currentTime : this.lastConnection;
    this.lastConnection = currentTime;
    this.connectionsSecond++;
    this.totalConnections++;
  }
  
  public int getTotalConnections() {
    return this.totalConnections;
  }
  
  public String getHostString() {
    return this.hostString;
  }
  
  public boolean isFirewalled() {
    return 
      (System.currentTimeMillis() - this.lastFirewall < (this.config.getAntibotFirewallExpire() * 1000));
  }
  
  public void firewall(String reason) {
    if (!FlameCord.getInstance().getFlameCordConfiguration().getAntibotFirewallWhitelist().contains(this.hostString)) {
      this.lastFirewall = System.currentTimeMillis();
      this.firewallReason = reason;
      if (FlameCord.getInstance().getFlameCordConfiguration().isAntibotFirewallLog())
        FlameCord.getInstance().getLoggerWrapper().log(Level.INFO, "[FlameCord] [{0}] was firewalled because of " + reason, new Object[] { this.hostString }); 
      FlameCord.getInstance().queueLinuxCommand("ipset add flamecord-firewall " + this.hostString);
    } 
  }
  
  public void unfirewall() {
    this.lastFirewall = 0L;
    this.firewallReason = null;
  }
  
  public String getFirewallReason() {
    if (isFirewalled())
      return this.firewallReason; 
    return null;
  }
  
  public void setTotalConnections(int totalConnections) {
    this.totalConnections = totalConnections;
  }
  
  public String setCountry(String country) {
    return this.country = country;
  }
  
  public String getCountry() {
    return this.country;
  }
  
  public boolean hasNickname(String nickname) {
    return this.nicknames.contains(nickname);
  }
}
