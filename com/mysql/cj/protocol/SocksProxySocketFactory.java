package com.mysql.cj.protocol;

import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketException;

public class SocksProxySocketFactory extends StandardSocketFactory {
  protected Socket createSocket(PropertySet props) {
    String socksProxyHost = (String)props.getStringProperty(PropertyKey.socksProxyHost).getValue();
    int socksProxyPort = ((Integer)props.getIntegerProperty(PropertyKey.socksProxyPort).getValue()).intValue();
    return new Socket(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(socksProxyHost, socksProxyPort)));
  }
  
  public <T extends java.io.Closeable> T connect(String hostname, int portNumber, PropertySet pset, int loginTimeout) throws IOException {
    if (!((Boolean)pset.getBooleanProperty(PropertyKey.socksProxyRemoteDns).getValue()).booleanValue())
      return super.connect(hostname, portNumber, pset, loginTimeout); 
    this.loginTimeoutCountdown = loginTimeout;
    if (pset != null && hostname != null) {
      this.host = hostname;
      this.port = portNumber;
      String localSocketHostname = (String)pset.getStringProperty(PropertyKey.localSocketAddress).getValue();
      InetSocketAddress localSockAddr = (localSocketHostname != null && localSocketHostname.length() > 0) ? new InetSocketAddress(InetAddress.getByName(localSocketHostname), 0) : null;
      int connectTimeout = ((Integer)pset.getIntegerProperty(PropertyKey.connectTimeout).getValue()).intValue();
      try {
        this.rawSocket = createSocket(pset);
        configureSocket(this.rawSocket, pset);
        if (localSockAddr != null)
          this.rawSocket.bind(localSockAddr); 
        this.rawSocket.connect(InetSocketAddress.createUnresolved(this.host, this.port), getRealTimeout(connectTimeout));
      } catch (SocketException ex) {
        this.rawSocket = null;
        throw ex;
      } 
      resetLoginTimeCountdown();
      this.sslSocket = this.rawSocket;
      return (T)this.rawSocket;
    } 
    throw new SocketException("Unable to create socket");
  }
}
