package dev._2lstudios.flamecord.antibot;

public class StatsData {
  private long lastSecond = System.currentTimeMillis();
  
  private int totalPings = 0;
  
  private int totalConnections = 0;
  
  private int currentPings = 0;
  
  private int currentConnections = 0;
  
  private int lastPings = 0;
  
  private int lastConnections = 0;
  
  public void resetData() {
    long currentTime = System.currentTimeMillis();
    if (currentTime - this.lastSecond > 1000L) {
      this.lastSecond = currentTime;
      this.lastPings = this.currentPings;
      this.lastConnections = this.currentConnections;
      this.currentPings = 0;
      this.currentConnections = 0;
    } 
  }
  
  public void addPing() {
    resetData();
    this.currentPings++;
    this.totalPings++;
  }
  
  public void addConnection() {
    resetData();
    this.currentConnections++;
    this.totalConnections++;
  }
  
  public int getCurrentPings() {
    resetData();
    return this.currentPings;
  }
  
  public int getCurrentConnections() {
    resetData();
    return this.currentConnections;
  }
  
  public int getLastPings() {
    resetData();
    return this.lastPings;
  }
  
  public int getLastConnections() {
    resetData();
    return this.lastConnections;
  }
  
  public int getTotalPings() {
    return this.totalPings;
  }
  
  public int getTotalConnections() {
    return this.totalConnections;
  }
}
