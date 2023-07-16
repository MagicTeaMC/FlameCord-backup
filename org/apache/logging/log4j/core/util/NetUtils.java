package org.apache.logging.log4j.core.util;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public final class NetUtils {
  private static final Logger LOGGER = (Logger)StatusLogger.getLogger();
  
  private static final String UNKNOWN_LOCALHOST = "UNKNOWN_LOCALHOST";
  
  public static String getLocalHostname() {
    try {
      InetAddress addr = InetAddress.getLocalHost();
      return (addr == null) ? "UNKNOWN_LOCALHOST" : addr.getHostName();
    } catch (UnknownHostException uhe) {
      try {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        if (interfaces != null)
          while (interfaces.hasMoreElements()) {
            NetworkInterface nic = interfaces.nextElement();
            Enumeration<InetAddress> addresses = nic.getInetAddresses();
            while (addresses.hasMoreElements()) {
              InetAddress address = addresses.nextElement();
              if (!address.isLoopbackAddress()) {
                String hostname = address.getHostName();
                if (hostname != null)
                  return hostname; 
              } 
            } 
          }  
      } catch (SocketException socketException) {}
      LOGGER.error("Could not determine local host name", uhe);
      return "UNKNOWN_LOCALHOST";
    } 
  }
  
  public static byte[] getMacAddress() {
    byte[] mac = null;
    try {
      InetAddress localHost = InetAddress.getLocalHost();
      try {
        NetworkInterface localInterface = NetworkInterface.getByInetAddress(localHost);
        if (isUpAndNotLoopback(localInterface))
          mac = localInterface.getHardwareAddress(); 
        if (mac == null) {
          Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
          if (networkInterfaces != null)
            while (networkInterfaces.hasMoreElements() && mac == null) {
              NetworkInterface nic = networkInterfaces.nextElement();
              if (isUpAndNotLoopback(nic))
                mac = nic.getHardwareAddress(); 
            }  
        } 
      } catch (SocketException e) {
        LOGGER.catching(e);
      } 
      if (ArrayUtils.isEmpty(mac) && localHost != null) {
        byte[] address = localHost.getAddress();
        mac = Arrays.copyOf(address, 6);
      } 
    } catch (UnknownHostException unknownHostException) {}
    return mac;
  }
  
  public static String getMacAddressString() {
    byte[] macAddr = getMacAddress();
    if (!ArrayUtils.isEmpty(macAddr)) {
      StringBuilder sb = new StringBuilder(String.format("%02x", new Object[] { Byte.valueOf(macAddr[0]) }));
      for (int i = 1; i < macAddr.length; i++) {
        sb.append(":").append(String.format("%02x", new Object[] { Byte.valueOf(macAddr[i]) }));
      } 
      return sb.toString();
    } 
    return null;
  }
  
  private static boolean isUpAndNotLoopback(NetworkInterface ni) throws SocketException {
    return (ni != null && !ni.isLoopback() && ni.isUp());
  }
  
  public static URI toURI(String path) {
    try {
      return new URI(path);
    } catch (URISyntaxException e) {
      try {
        URL url = new URL(path);
        return new URI(url.getProtocol(), url.getHost(), url.getPath(), null);
      } catch (MalformedURLException|URISyntaxException nestedEx) {
        return (new File(path)).toURI();
      } 
    } 
  }
  
  public static List<URI> toURIs(String path) {
    String[] parts = path.split(",");
    String scheme = null;
    List<URI> uris = new ArrayList<>(parts.length);
    for (String part : parts) {
      URI uri = toURI((scheme != null) ? (scheme + ":" + part.trim()) : part.trim());
      if (scheme == null && uri.getScheme() != null)
        scheme = uri.getScheme(); 
      uris.add(uri);
    } 
    return uris;
  }
}
