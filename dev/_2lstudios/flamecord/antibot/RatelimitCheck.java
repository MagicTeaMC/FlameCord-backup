package dev._2lstudios.flamecord.antibot;

import dev._2lstudios.flamecord.FlameCord;
import dev._2lstudios.flamecord.configuration.FlameCordConfiguration;
import java.net.SocketAddress;
import java.util.logging.Level;

public class RatelimitCheck {
  private FlameCordConfiguration config;
  
  private LoggerWrapper logger;
  
  private AddressDataManager addressDataManager;
  
  public RatelimitCheck(AddressDataManager addressDataManager) {
    this.config = FlameCord.getInstance().getFlameCordConfiguration();
    this.logger = FlameCord.getInstance().getLoggerWrapper();
    this.addressDataManager = addressDataManager;
  }
  
  public boolean check(SocketAddress remoteAddress, int protocol) {
    if (this.config.isAntibotRatelimitEnabled()) {
      AddressData addressData = this.addressDataManager.getAddressData(remoteAddress);
      if (this.config.getAntibotRatelimitWhitelist().contains(addressData.getHostString()))
        return false; 
      if (addressData.getConnectionsSecond() >= this.config.getAntibotRatelimitConnectionsPerSecond() || addressData
        .getPingsSecond() >= this.config.getAntibotRatelimitPingsPerSecond()) {
        if (this.config.isAntibotRatelimitLog())
          if (protocol == 1) {
            this.logger.log(Level.INFO, "[FlameCord] [{0}] is pinging too fast", new Object[] { remoteAddress });
          } else {
            this.logger.log(Level.INFO, "[FlameCord] [{0}] is connecting too fast", new Object[] { remoteAddress });
          }  
        if (this.config.isAntibotRatelimitFirewall())
          if (protocol == 1) {
            addressData.firewall("Too many pings");
          } else {
            addressData.firewall("Too many connections");
          }  
        return true;
      } 
    } 
    return false;
  }
}
