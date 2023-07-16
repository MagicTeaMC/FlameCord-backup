package dev._2lstudios.flamecord.antibot;

import dev._2lstudios.flamecord.FlameCord;
import dev._2lstudios.flamecord.configuration.FlameCordConfiguration;

public class CheckManager {
  private final AccountsCheck accountsCheck;
  
  private final CountryCheck countryCheck;
  
  private final FastChatCheck fastChatCheck;
  
  private final NicknameCheck nicknameCheck;
  
  private final PasswordCheck passwordCheck;
  
  private final RatelimitCheck ratelimitCheck;
  
  private final ReconnectCheck reconnectCheck;
  
  private final PacketsCheck packetsCheck;
  
  private final ProxyCheck proxyCheck;
  
  public AccountsCheck getAccountsCheck() {
    return this.accountsCheck;
  }
  
  public CountryCheck getCountryCheck() {
    return this.countryCheck;
  }
  
  public FastChatCheck getFastChatCheck() {
    return this.fastChatCheck;
  }
  
  public NicknameCheck getNicknameCheck() {
    return this.nicknameCheck;
  }
  
  public PasswordCheck getPasswordCheck() {
    return this.passwordCheck;
  }
  
  public RatelimitCheck getRatelimitCheck() {
    return this.ratelimitCheck;
  }
  
  public ReconnectCheck getReconnectCheck() {
    return this.reconnectCheck;
  }
  
  public PacketsCheck getPacketsCheck() {
    return this.packetsCheck;
  }
  
  public ProxyCheck getProxyCheck() {
    return this.proxyCheck;
  }
  
  public CheckManager(AddressDataManager addressDataManager, FlameCordConfiguration flameCordConfiguration) {
    this.accountsCheck = new AccountsCheck(addressDataManager);
    this.countryCheck = new CountryCheck(addressDataManager);
    this.fastChatCheck = new FastChatCheck(addressDataManager);
    this.nicknameCheck = new NicknameCheck(addressDataManager);
    this.passwordCheck = new PasswordCheck(addressDataManager);
    this.ratelimitCheck = new RatelimitCheck(addressDataManager);
    this.reconnectCheck = new ReconnectCheck(addressDataManager);
    this.packetsCheck = new PacketsCheck();
    this.proxyCheck = new ProxyCheck(addressDataManager);
    this.countryCheck.load();
    if (FlameCord.getInstance().getFlameCordConfiguration().isAntibotProxyEnabled())
      (new Thread(() -> this.proxyCheck.updateProxies())).start(); 
  }
  
  public void unload() {
    this.countryCheck.unload();
  }
}
