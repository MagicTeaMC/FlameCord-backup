package dev._2lstudios.flamecord.antibot;

import dev._2lstudios.flamecord.FlameCord;
import dev._2lstudios.flamecord.configuration.FlameCordConfiguration;
import java.net.SocketAddress;
import java.util.logging.Level;

public class ReconnectCheck {
  private FlameCordConfiguration config;
  
  private LoggerWrapper logger;
  
  private AddressDataManager addressDataManager;
  
  private int connections = 0;
  
  private long lastConnection = 0L;
  
  public ReconnectCheck(AddressDataManager addressDataManager) {
    this.config = FlameCord.getInstance().getFlameCordConfiguration();
    this.logger = FlameCord.getInstance().getLoggerWrapper();
    this.addressDataManager = addressDataManager;
  }
  
  public boolean check(SocketAddress remoteAddress) {
    if (this.config.isAntibotReconnectEnabled()) {
      long currentTime = System.currentTimeMillis();
      if (currentTime - this.lastConnection > this.config.getAntibotReconnectConnectionThresholdLimit()) {
        this.lastConnection = currentTime;
        this.connections = 0;
      } 
      if (++this.connections > this.config.getAntibotReconnectConnectionThreshold()) {
        AddressData addressData = this.addressDataManager.getAddressData(remoteAddress);
        boolean needsAttempts = (addressData.getTotalConnections() < this.config.getAntibotReconnectAttempts() || addressData.getTotalPings() < this.config.getAntibotReconnectPings());
        boolean tooSlow = (addressData.getTimeSincePenultimateConnection() > this.config.getAntibotReconnectMaxTime());
        if (tooSlow) {
          if (this.config.isAntibotReconnectLog())
            this.logger.log(Level.INFO, "[FlameCord] [{0}] has to reconnect to join", new Object[] { remoteAddress }); 
          addressData.setTotalConnections(0);
          return true;
        } 
        return needsAttempts;
      } 
    } 
    return false;
  }
}
