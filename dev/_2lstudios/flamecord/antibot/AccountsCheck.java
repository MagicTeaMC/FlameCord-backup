package dev._2lstudios.flamecord.antibot;

import dev._2lstudios.flamecord.FlameCord;
import dev._2lstudios.flamecord.configuration.FlameCordConfiguration;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.logging.Level;

public class AccountsCheck {
  private FlameCordConfiguration config;
  
  private LoggerWrapper logger;
  
  private AddressDataManager addressDataManager;
  
  public AccountsCheck(AddressDataManager addressDataManager) {
    this.config = FlameCord.getInstance().getFlameCordConfiguration();
    this.logger = FlameCord.getInstance().getLoggerWrapper();
    this.addressDataManager = addressDataManager;
  }
  
  public boolean check(SocketAddress remoteAddress, String nickname) {
    if (this.config.getAntibotAccountsWhitelist().contains(nickname))
      return false; 
    if (this.config.isAntibotAccountsEnabled()) {
      AddressData addressData = this.addressDataManager.getAddressData(remoteAddress);
      Collection<String> nicknames = addressData.getNicknames();
      if (nicknames.size() > this.config.getAntibotAccountsLimit()) {
        nicknames.remove(nickname);
        if (this.config.isAntibotAccountsLog())
          this.logger.log(Level.INFO, "[FlameCord] [{0}] has too many accounts", new Object[] { remoteAddress }); 
        if (this.config.isAntibotAccountsFirewall())
          addressData.firewall("Too many accounts"); 
        return true;
      } 
    } 
    return false;
  }
}
