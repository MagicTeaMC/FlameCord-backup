package dev._2lstudios.flamecord.antibot;

import com.maxmind.db.CHMCache;
import com.maxmind.db.MaxMindDbConstructor;
import com.maxmind.db.MaxMindDbParameter;
import com.maxmind.db.NodeCache;
import com.maxmind.db.Reader;
import dev._2lstudios.flamecord.FlameCord;
import dev._2lstudios.flamecord.configuration.FlameCordConfiguration;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.file.Files;
import java.util.logging.Level;

public class CountryCheck {
  private FlameCordConfiguration config;
  
  private LoggerWrapper logger;
  
  private AddressDataManager addressDataManager;
  
  private Reader maxMindReader;
  
  public CountryCheck(AddressDataManager addressDataManager) {
    this.config = FlameCord.getInstance().getFlameCordConfiguration();
    this.logger = FlameCord.getInstance().getLoggerWrapper();
    this.addressDataManager = addressDataManager;
  }
  
  public void download(URL url, File file) throws Exception {
    try (InputStream in = url.openStream()) {
      Files.copy(in, file.toPath(), new java.nio.file.CopyOption[0]);
    } 
  }
  
  public void load() {
    if (!this.config.isAntibotCountryEnabled())
      return; 
    File file = new File("GeoLite2-Country.mmdb");
    try {
      if (!file.exists()) {
        System.out.println("Starting download of MaxMindDB (This will take some seconds...)");
        download(new URL("https://git.io/GeoLite2-Country.mmdb"), file);
      } 
      this.maxMindReader = new Reader(file, (NodeCache)new CHMCache());
    } catch (Exception exception) {
      System.out.println("MaxMindDB was not able to download!");
    } 
  }
  
  public void unload() {
    try {
      if (this.maxMindReader != null)
        this.maxMindReader.close(); 
    } catch (IOException iOException) {}
  }
  
  private boolean isBlacklisted(FlameCordConfiguration config, String isoCode) {
    for (String blacklisted : config.getAntibotCountryBlacklist()) {
      if (isoCode.contains(blacklisted))
        return true; 
    } 
    return false;
  }
  
  public static class LookupResult {
    private final CountryCheck.Country country;
    
    @MaxMindDbConstructor
    public LookupResult(@MaxMindDbParameter(name = "country") CountryCheck.Country country) {
      this.country = country;
    }
    
    public CountryCheck.Country getCountry() {
      return this.country;
    }
  }
  
  public static class Country {
    private final String isoCode;
    
    @MaxMindDbConstructor
    public Country(@MaxMindDbParameter(name = "iso_code") String isoCode) {
      this.isoCode = isoCode;
    }
    
    public String getIsoCode() {
      return this.isoCode;
    }
  }
  
  public String getIsoCode(InetAddress address) {
    try {
      LookupResult lookupResult = (LookupResult)this.maxMindReader.get(address, LookupResult.class);
      if (lookupResult == null)
        return "LOCAL"; 
      Country country = lookupResult.getCountry();
      String isoCode = country.getIsoCode();
      return isoCode;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public boolean check(SocketAddress remoteAddress) {
    if (this.config.isAntibotCountryEnabled()) {
      String country;
      AddressData addressData = this.addressDataManager.getAddressData(remoteAddress);
      String addressCountry = addressData.getCountry();
      if (addressCountry != null) {
        country = addressCountry;
      } else {
        country = getIsoCode(((InetSocketAddress)remoteAddress).getAddress());
        addressData.setCountry(country);
      } 
      if (country != null && isBlacklisted(this.config, country)) {
        if (this.config.isAntibotCountryLog())
          this.logger.log(Level.INFO, "[FlameCord] [{0}] has his country blocked from the server", new Object[] { remoteAddress }); 
        if (this.config.isAntibotCountryFirewall())
          addressData.firewall("Blacklisted country"); 
        return true;
      } 
    } 
    return false;
  }
}
