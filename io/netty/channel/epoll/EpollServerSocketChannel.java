package io.netty.channel.epoll;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.channel.unix.NativeInetAddress;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class EpollServerSocketChannel extends AbstractEpollServerChannel implements ServerSocketChannel {
  private final EpollServerSocketChannelConfig config;
  
  private volatile Collection<InetAddress> tcpMd5SigAddresses = Collections.emptyList();
  
  public EpollServerSocketChannel() {
    this((InternetProtocolFamily)null);
  }
  
  public EpollServerSocketChannel(InternetProtocolFamily protocol) {
    super(LinuxSocket.newSocketStream(protocol), false);
    this.config = new EpollServerSocketChannelConfig(this);
  }
  
  public EpollServerSocketChannel(int fd) {
    this(new LinuxSocket(fd));
  }
  
  EpollServerSocketChannel(LinuxSocket fd) {
    super(fd);
    this.config = new EpollServerSocketChannelConfig(this);
  }
  
  EpollServerSocketChannel(LinuxSocket fd, boolean active) {
    super(fd, active);
    this.config = new EpollServerSocketChannelConfig(this);
  }
  
  protected boolean isCompatible(EventLoop loop) {
    return loop instanceof EpollEventLoop;
  }
  
  protected void doBind(SocketAddress localAddress) throws Exception {
    super.doBind(localAddress);
    int tcpFastopen;
    if (Native.IS_SUPPORTING_TCP_FASTOPEN_SERVER && (tcpFastopen = this.config.getTcpFastopen()) > 0)
      this.socket.setTcpFastOpen(tcpFastopen); 
    this.socket.listen(this.config.getBacklog());
    this.active = true;
  }
  
  public InetSocketAddress remoteAddress() {
    return (InetSocketAddress)super.remoteAddress();
  }
  
  public InetSocketAddress localAddress() {
    return (InetSocketAddress)super.localAddress();
  }
  
  public EpollServerSocketChannelConfig config() {
    return this.config;
  }
  
  protected Channel newChildChannel(int fd, byte[] address, int offset, int len) throws Exception {
    return (Channel)new EpollSocketChannel((Channel)this, new LinuxSocket(fd), NativeInetAddress.address(address, offset, len));
  }
  
  Collection<InetAddress> tcpMd5SigAddresses() {
    return this.tcpMd5SigAddresses;
  }
  
  void setTcpMd5Sig(Map<InetAddress, byte[]> keys) throws IOException {
    synchronized (this) {
      this.tcpMd5SigAddresses = TcpMd5Util.newTcpMd5Sigs(this, this.tcpMd5SigAddresses, keys);
    } 
  }
}
