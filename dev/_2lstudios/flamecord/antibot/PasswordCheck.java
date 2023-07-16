package dev._2lstudios.flamecord.antibot;

import dev._2lstudios.flamecord.FlameCord;
import dev._2lstudios.flamecord.configuration.FlameCordConfiguration;
import java.net.SocketAddress;
import java.util.logging.Level;

public class PasswordCheck {
  private FlameCordConfiguration config;
  
  private AddressDataManager addressDataManager;
  
  private LoggerWrapper logger;
  
  private String lastNickname = "";
  
  private String lastPassword = "";
  
  private int repeatCount = 0;
  
  public PasswordCheck(AddressDataManager addressDataManager) {
    this.config = FlameCord.getInstance().getFlameCordConfiguration();
    this.logger = FlameCord.getInstance().getLoggerWrapper();
    this.addressDataManager = addressDataManager;
  }
  
  private void updatePassword(FlameCordConfiguration config, String nickname, String password) {
    if (!nickname.equals(this.lastNickname))
      if (password.equals(this.lastPassword)) {
        if (this.repeatCount < config.getAntibotPasswordLimit())
          this.repeatCount++; 
      } else if (this.repeatCount > 0) {
        this.repeatCount--;
      }  
    this.lastNickname = nickname;
    this.lastPassword = password;
  }
  
  public boolean check(SocketAddress remoteAddress, String passwordMessage) {
    if (this.config.isAntibotPasswordEnabled() && (
      passwordMessage.contains("/login ") || passwordMessage.contains("/l ") || passwordMessage
      .contains("/register ") || passwordMessage
      .contains("/reg "))) {
      AddressData addressData = this.addressDataManager.getAddressData(remoteAddress);
      String nickname = addressData.getLastNickname();
      String password = passwordMessage.split(" ")[1];
      updatePassword(this.config, nickname, password);
      if (this.repeatCount >= this.config.getAntibotPasswordLimit()) {
        if (this.config.isAntibotPasswordLog())
          this.logger.log(Level.INFO, "[FlameCord] [{0}] has entered a repeated password", new Object[] { remoteAddress }); 
        if (this.config.isAntibotPasswordFirewall())
          addressData.firewall("Repeated password"); 
        return true;
      } 
    } 
    return false;
  }
  
  public int getRepeatCount() {
    return this.repeatCount;
  }
}
