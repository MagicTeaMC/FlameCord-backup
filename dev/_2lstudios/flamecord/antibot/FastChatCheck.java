package dev._2lstudios.flamecord.antibot;

import dev._2lstudios.flamecord.FlameCord;
import dev._2lstudios.flamecord.configuration.FlameCordConfiguration;
import java.net.SocketAddress;
import java.util.logging.Level;

public class FastChatCheck {
  private FlameCordConfiguration config;
  
  private LoggerWrapper logger;
  
  private final AddressDataManager addressDataManager;
  
  public FastChatCheck(AddressDataManager addressDataManager) {
    this.config = FlameCord.getInstance().getFlameCordConfiguration();
    this.logger = FlameCord.getInstance().getLoggerWrapper();
    this.addressDataManager = addressDataManager;
  }
  
  public boolean check(SocketAddress remoteAddress) {
    if (this.config.isAntibotFastChatEnabled()) {
      AddressData addressData = this.addressDataManager.getAddressData(remoteAddress);
      if (addressData.getTimeSinceLastConnection() <= this.config.getAntibotFastChatTime()) {
        if (this.config.isAntibotFastChatLog())
          this.logger.log(Level.INFO, "[FlameCord] [{0}] is chatting too fast", new Object[] { remoteAddress }); 
        if (this.config.isAntibotFastChatFirewall())
          addressData.firewall("Too fast chatting"); 
        return true;
      } 
    } 
    return false;
  }
}
