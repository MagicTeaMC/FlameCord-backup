package dev._2lstudios.flamecord.antibot;

import dev._2lstudios.flamecord.FlameCord;
import dev._2lstudios.flamecord.configuration.FlameCordConfiguration;
import java.net.SocketAddress;
import java.util.logging.Level;

public class NicknameCheck {
  private FlameCordConfiguration config;
  
  private LoggerWrapper logger;
  
  private AddressDataManager addressDataManager;
  
  public NicknameCheck(AddressDataManager addressDataManager) {
    this.config = FlameCord.getInstance().getFlameCordConfiguration();
    this.logger = FlameCord.getInstance().getLoggerWrapper();
    this.addressDataManager = addressDataManager;
  }
  
  private boolean isBlacklisted(FlameCordConfiguration config, String nickname) {
    String lowerNickname = nickname.toLowerCase();
    for (String blacklisted : config.getAntibotNicknameBlacklist()) {
      if (lowerNickname.contains(blacklisted))
        return true; 
    } 
    return false;
  }
  
  public boolean check(SocketAddress remoteAddress) {
    if (this.config.isAntibotNicknameEnabled()) {
      AddressData addressData = this.addressDataManager.getAddressData(remoteAddress);
      String nickname = addressData.getLastNickname();
      if (isBlacklisted(this.config, nickname)) {
        if (this.config.isAntibotNicknameLog())
          this.logger.log(Level.INFO, "[FlameCord] [{0}] has a blacklisted nickname (" + nickname + ")", new Object[] { remoteAddress }); 
        if (this.config.isAntibotNicknameFirewall())
          addressData.firewall("Blacklisted nickname [" + nickname + "]"); 
        return true;
      } 
    } 
    return false;
  }
}
