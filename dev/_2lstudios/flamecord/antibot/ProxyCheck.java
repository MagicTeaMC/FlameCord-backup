package dev._2lstudios.flamecord.antibot;

import dev._2lstudios.flamecord.FlameCord;
import dev._2lstudios.flamecord.configuration.FlameCordConfiguration;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Collection;
import java.util.logging.Level;

public class ProxyCheck {
  private FlameCordConfiguration config;
  
  private LoggerWrapper logger;
  
  private AddressDataManager addressDataManager;
  
  private String proxies = "";
  
  public ProxyCheck(AddressDataManager addressDataManager) {
    this.addressDataManager = addressDataManager;
    this.config = FlameCord.getInstance().getFlameCordConfiguration();
    this.logger = FlameCord.getInstance().getLoggerWrapper();
  }
  
  public void updateProxies() {
    Collection<String> lists = this.config.getAntibotProxyLists();
    FlameCord.getInstance().getLoggerWrapper().getLogger()
      .info("[FlameCord] Updating proxy database from " + lists.size() + " websites... (It can take a while!)");
    StringBuilder content = new StringBuilder();
    int proxyCount = 0;
    long timeTook = System.currentTimeMillis();
    for (String website : lists) {
      try {
        URL url = new URL(website);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        if (responseCode == 200) {
          BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
          String inputLine;
          while ((inputLine = in.readLine()) != null) {
            if (!inputLine.startsWith("#") && content.indexOf(inputLine) == -1) {
              content.append(inputLine);
              content.append("\n");
              proxyCount++;
            } 
          } 
          in.close();
          continue;
        } 
        FlameCord.getInstance().getLoggerWrapper().log(Level.INFO, "GET request failed for " + website, new Object[0]);
      } catch (Exception ex) {
        FlameCord.getInstance().getLoggerWrapper().log(Level.INFO, "GET request failed for " + website, new Object[0]);
      } 
    } 
    timeTook = (System.currentTimeMillis() - timeTook) / 1000L;
    FlameCord.getInstance().getLoggerWrapper().getLogger()
      .info("[FlameCord] Loaded a total of " + proxyCount + " proxies to the database! (Took " + timeTook + " seconds)");
    this.proxies = content.toString();
  }
  
  public boolean check(String ip) {
    return (!this.proxies.equals("") && this.proxies.contains(ip));
  }
  
  public boolean check(SocketAddress address) {
    if (!this.config.isAntibotProxyEnabled())
      return false; 
    AddressData addressData = this.addressDataManager.getAddressData(address);
    String ip = addressData.getHostString();
    Collection<String> whitelist = this.config.getAntibotProxyWhitelist();
    if (whitelist.contains(ip))
      return false; 
    if ((!this.proxies.equals("") && this.proxies.contains(ip)) || isVPN(ip)) {
      if (this.config.isAntibotProxyLog())
        this.logger.log(Level.INFO, "[FlameCord] [{0}] was blocked for using a VPN/Proxy service", new Object[] { address }); 
      if (this.config.isAntibotProxyFirewall())
        addressData.firewall("Using VPN/proxy services"); 
      return true;
    } 
    return false;
  }
  
  public boolean isVPN(String ip) {
    if (!this.config.isAntibotProxyOnlineCheck())
      return false; 
    try {
      URL url = new URL("https://check.getipintel.net/check.php?ip=" + ip + "&contact=" + FlameCord.getInstance().getFlameCordConfiguration().getAntibotProxyEmail());
      HttpURLConnection con = (HttpURLConnection)url.openConnection();
      con.setRequestMethod("GET");
      int responseCode = con.getResponseCode();
      if (responseCode == 200) {
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null)
          response.append(line); 
        in.close();
        double result = Double.parseDouble(response.toString());
        if (result > 0.99D)
          return true; 
        return false;
      } 
      return false;
    } catch (Exception e) {
      return false;
    } 
  }
}
