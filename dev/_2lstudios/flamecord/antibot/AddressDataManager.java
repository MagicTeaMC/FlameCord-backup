package dev._2lstudios.flamecord.antibot;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class AddressDataManager {
  private Map<String, AddressData> addressData = new HashMap<>();
  
  public String sanitizeAddress(String text) {
    int indexOfPort = text.indexOf(":");
    if (indexOfPort != -1)
      text = text.substring(0, indexOfPort); 
    if (text.startsWith("/"))
      text = text.substring(1); 
    return text;
  }
  
  public AddressData getAddressData(String addressString) {
    addressString = sanitizeAddress(addressString);
    if (this.addressData.containsKey(addressString))
      return this.addressData.get(addressString); 
    AddressData data = new AddressData(addressString);
    this.addressData.put(addressString, data);
    return data;
  }
  
  public AddressData getAddressData(SocketAddress address) {
    InetSocketAddress iNetSocketAddress = (InetSocketAddress)address;
    String addressString = iNetSocketAddress.getHostString();
    return getAddressData(addressString);
  }
  
  public int getAddresCount() {
    return this.addressData.size();
  }
}
