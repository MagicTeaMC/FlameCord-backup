package io.netty.channel.unix;

import io.netty.channel.ChannelException;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

public class Socket extends FileDescriptor {
  private static volatile boolean isIpv6Preferred;
  
  @Deprecated
  public static final int UDS_SUN_PATH_SIZE = 100;
  
  protected final boolean ipv6;
  
  public Socket(int fd) {
    super(fd);
    this.ipv6 = isIPv6(fd);
  }
  
  private boolean useIpv6(InetAddress address) {
    return useIpv6(this, address);
  }
  
  protected static boolean useIpv6(Socket socket, InetAddress address) {
    return (socket.ipv6 || address instanceof Inet6Address);
  }
  
  public final void shutdown() throws IOException {
    shutdown(true, true);
  }
  
  public final void shutdown(boolean read, boolean write) throws IOException {
    int oldState, newState;
    do {
      oldState = this.state;
      if (isClosed(oldState))
        throw new ClosedChannelException(); 
      newState = oldState;
      if (read && !isInputShutdown(newState))
        newState = inputShutdown(newState); 
      if (write && !isOutputShutdown(newState))
        newState = outputShutdown(newState); 
      if (newState == oldState)
        return; 
    } while (!casState(oldState, newState));
    int res = shutdown(this.fd, read, write);
    if (res < 0)
      Errors.ioResult("shutdown", res); 
  }
  
  public final boolean isShutdown() {
    int state = this.state;
    return (isInputShutdown(state) && isOutputShutdown(state));
  }
  
  public final boolean isInputShutdown() {
    return isInputShutdown(this.state);
  }
  
  public final boolean isOutputShutdown() {
    return isOutputShutdown(this.state);
  }
  
  public final int sendTo(ByteBuffer buf, int pos, int limit, InetAddress addr, int port) throws IOException {
    return sendTo(buf, pos, limit, addr, port, false);
  }
  
  public final int sendTo(ByteBuffer buf, int pos, int limit, InetAddress addr, int port, boolean fastOpen) throws IOException {
    byte[] address;
    int scopeId;
    if (addr instanceof Inet6Address) {
      address = addr.getAddress();
      scopeId = ((Inet6Address)addr).getScopeId();
    } else {
      scopeId = 0;
      address = NativeInetAddress.ipv4MappedIpv6Address(addr.getAddress());
    } 
    int flags = fastOpen ? msgFastopen() : 0;
    int res = sendTo(this.fd, useIpv6(addr), buf, pos, limit, address, scopeId, port, flags);
    if (res >= 0)
      return res; 
    if (res == Errors.ERRNO_EINPROGRESS_NEGATIVE && fastOpen)
      return 0; 
    if (res == Errors.ERROR_ECONNREFUSED_NEGATIVE)
      throw new PortUnreachableException("sendTo failed"); 
    return Errors.ioResult("sendTo", res);
  }
  
  public final int sendToDomainSocket(ByteBuffer buf, int pos, int limit, byte[] path) throws IOException {
    int res = sendToDomainSocket(this.fd, buf, pos, limit, path);
    if (res >= 0)
      return res; 
    return Errors.ioResult("sendToDomainSocket", res);
  }
  
  public final int sendToAddress(long memoryAddress, int pos, int limit, InetAddress addr, int port) throws IOException {
    return sendToAddress(memoryAddress, pos, limit, addr, port, false);
  }
  
  public final int sendToAddress(long memoryAddress, int pos, int limit, InetAddress addr, int port, boolean fastOpen) throws IOException {
    byte[] address;
    int scopeId;
    if (addr instanceof Inet6Address) {
      address = addr.getAddress();
      scopeId = ((Inet6Address)addr).getScopeId();
    } else {
      scopeId = 0;
      address = NativeInetAddress.ipv4MappedIpv6Address(addr.getAddress());
    } 
    int flags = fastOpen ? msgFastopen() : 0;
    int res = sendToAddress(this.fd, useIpv6(addr), memoryAddress, pos, limit, address, scopeId, port, flags);
    if (res >= 0)
      return res; 
    if (res == Errors.ERRNO_EINPROGRESS_NEGATIVE && fastOpen)
      return 0; 
    if (res == Errors.ERROR_ECONNREFUSED_NEGATIVE)
      throw new PortUnreachableException("sendToAddress failed"); 
    return Errors.ioResult("sendToAddress", res);
  }
  
  public final int sendToAddressDomainSocket(long memoryAddress, int pos, int limit, byte[] path) throws IOException {
    int res = sendToAddressDomainSocket(this.fd, memoryAddress, pos, limit, path);
    if (res >= 0)
      return res; 
    return Errors.ioResult("sendToAddressDomainSocket", res);
  }
  
  public final int sendToAddresses(long memoryAddress, int length, InetAddress addr, int port) throws IOException {
    return sendToAddresses(memoryAddress, length, addr, port, false);
  }
  
  public final int sendToAddresses(long memoryAddress, int length, InetAddress addr, int port, boolean fastOpen) throws IOException {
    byte[] address;
    int scopeId;
    if (addr instanceof Inet6Address) {
      address = addr.getAddress();
      scopeId = ((Inet6Address)addr).getScopeId();
    } else {
      scopeId = 0;
      address = NativeInetAddress.ipv4MappedIpv6Address(addr.getAddress());
    } 
    int flags = fastOpen ? msgFastopen() : 0;
    int res = sendToAddresses(this.fd, useIpv6(addr), memoryAddress, length, address, scopeId, port, flags);
    if (res >= 0)
      return res; 
    if (res == Errors.ERRNO_EINPROGRESS_NEGATIVE && fastOpen)
      return 0; 
    if (res == Errors.ERROR_ECONNREFUSED_NEGATIVE)
      throw new PortUnreachableException("sendToAddresses failed"); 
    return Errors.ioResult("sendToAddresses", res);
  }
  
  public final int sendToAddressesDomainSocket(long memoryAddress, int length, byte[] path) throws IOException {
    int res = sendToAddressesDomainSocket(this.fd, memoryAddress, length, path);
    if (res >= 0)
      return res; 
    return Errors.ioResult("sendToAddressesDomainSocket", res);
  }
  
  public final DatagramSocketAddress recvFrom(ByteBuffer buf, int pos, int limit) throws IOException {
    return recvFrom(this.fd, buf, pos, limit);
  }
  
  public final DatagramSocketAddress recvFromAddress(long memoryAddress, int pos, int limit) throws IOException {
    return recvFromAddress(this.fd, memoryAddress, pos, limit);
  }
  
  public final DomainDatagramSocketAddress recvFromDomainSocket(ByteBuffer buf, int pos, int limit) throws IOException {
    return recvFromDomainSocket(this.fd, buf, pos, limit);
  }
  
  public final DomainDatagramSocketAddress recvFromAddressDomainSocket(long memoryAddress, int pos, int limit) throws IOException {
    return recvFromAddressDomainSocket(this.fd, memoryAddress, pos, limit);
  }
  
  public int recv(ByteBuffer buf, int pos, int limit) throws IOException {
    int res = recv(intValue(), buf, pos, limit);
    if (res > 0)
      return res; 
    if (res == 0)
      return -1; 
    return Errors.ioResult("recv", res);
  }
  
  public int recvAddress(long address, int pos, int limit) throws IOException {
    int res = recvAddress(intValue(), address, pos, limit);
    if (res > 0)
      return res; 
    if (res == 0)
      return -1; 
    return Errors.ioResult("recvAddress", res);
  }
  
  public int send(ByteBuffer buf, int pos, int limit) throws IOException {
    int res = send(intValue(), buf, pos, limit);
    if (res >= 0)
      return res; 
    return Errors.ioResult("send", res);
  }
  
  public int sendAddress(long address, int pos, int limit) throws IOException {
    int res = sendAddress(intValue(), address, pos, limit);
    if (res >= 0)
      return res; 
    return Errors.ioResult("sendAddress", res);
  }
  
  public final int recvFd() throws IOException {
    int res = recvFd(this.fd);
    if (res > 0)
      return res; 
    if (res == 0)
      return -1; 
    if (res == Errors.ERRNO_EAGAIN_NEGATIVE || res == Errors.ERRNO_EWOULDBLOCK_NEGATIVE)
      return 0; 
    throw Errors.newIOException("recvFd", res);
  }
  
  public final int sendFd(int fdToSend) throws IOException {
    int res = sendFd(this.fd, fdToSend);
    if (res >= 0)
      return res; 
    if (res == Errors.ERRNO_EAGAIN_NEGATIVE || res == Errors.ERRNO_EWOULDBLOCK_NEGATIVE)
      return -1; 
    throw Errors.newIOException("sendFd", res);
  }
  
  public final boolean connect(SocketAddress socketAddress) throws IOException {
    int res;
    if (socketAddress instanceof InetSocketAddress) {
      InetSocketAddress inetSocketAddress = (InetSocketAddress)socketAddress;
      InetAddress inetAddress = inetSocketAddress.getAddress();
      NativeInetAddress address = NativeInetAddress.newInstance(inetAddress);
      res = connect(this.fd, useIpv6(inetAddress), address.address, address.scopeId, inetSocketAddress.getPort());
    } else if (socketAddress instanceof DomainSocketAddress) {
      DomainSocketAddress unixDomainSocketAddress = (DomainSocketAddress)socketAddress;
      res = connectDomainSocket(this.fd, unixDomainSocketAddress.path().getBytes(CharsetUtil.UTF_8));
    } else {
      throw new Error("Unexpected SocketAddress implementation " + socketAddress);
    } 
    if (res < 0)
      return Errors.handleConnectErrno("connect", res); 
    return true;
  }
  
  public final boolean finishConnect() throws IOException {
    int res = finishConnect(this.fd);
    if (res < 0)
      return Errors.handleConnectErrno("finishConnect", res); 
    return true;
  }
  
  public final void disconnect() throws IOException {
    int res = disconnect(this.fd, this.ipv6);
    if (res < 0)
      Errors.handleConnectErrno("disconnect", res); 
  }
  
  public final void bind(SocketAddress socketAddress) throws IOException {
    if (socketAddress instanceof InetSocketAddress) {
      InetSocketAddress addr = (InetSocketAddress)socketAddress;
      InetAddress inetAddress = addr.getAddress();
      NativeInetAddress address = NativeInetAddress.newInstance(inetAddress);
      int res = bind(this.fd, useIpv6(inetAddress), address.address, address.scopeId, addr.getPort());
      if (res < 0)
        throw Errors.newIOException("bind", res); 
    } else if (socketAddress instanceof DomainSocketAddress) {
      DomainSocketAddress addr = (DomainSocketAddress)socketAddress;
      int res = bindDomainSocket(this.fd, addr.path().getBytes(CharsetUtil.UTF_8));
      if (res < 0)
        throw Errors.newIOException("bind", res); 
    } else {
      throw new Error("Unexpected SocketAddress implementation " + socketAddress);
    } 
  }
  
  public final void listen(int backlog) throws IOException {
    int res = listen(this.fd, backlog);
    if (res < 0)
      throw Errors.newIOException("listen", res); 
  }
  
  public final int accept(byte[] addr) throws IOException {
    int res = accept(this.fd, addr);
    if (res >= 0)
      return res; 
    if (res == Errors.ERRNO_EAGAIN_NEGATIVE || res == Errors.ERRNO_EWOULDBLOCK_NEGATIVE)
      return -1; 
    throw Errors.newIOException("accept", res);
  }
  
  public final InetSocketAddress remoteAddress() {
    byte[] addr = remoteAddress(this.fd);
    return (addr == null) ? null : NativeInetAddress.address(addr, 0, addr.length);
  }
  
  public final DomainSocketAddress remoteDomainSocketAddress() {
    byte[] addr = remoteDomainSocketAddress(this.fd);
    return (addr == null) ? null : new DomainSocketAddress(new String(addr));
  }
  
  public final InetSocketAddress localAddress() {
    byte[] addr = localAddress(this.fd);
    return (addr == null) ? null : NativeInetAddress.address(addr, 0, addr.length);
  }
  
  public final DomainSocketAddress localDomainSocketAddress() {
    byte[] addr = localDomainSocketAddress(this.fd);
    return (addr == null) ? null : new DomainSocketAddress(new String(addr));
  }
  
  public final int getReceiveBufferSize() throws IOException {
    return getReceiveBufferSize(this.fd);
  }
  
  public final int getSendBufferSize() throws IOException {
    return getSendBufferSize(this.fd);
  }
  
  public final boolean isKeepAlive() throws IOException {
    return (isKeepAlive(this.fd) != 0);
  }
  
  public final boolean isTcpNoDelay() throws IOException {
    return (isTcpNoDelay(this.fd) != 0);
  }
  
  public final boolean isReuseAddress() throws IOException {
    return (isReuseAddress(this.fd) != 0);
  }
  
  public final boolean isReusePort() throws IOException {
    return (isReusePort(this.fd) != 0);
  }
  
  public final boolean isBroadcast() throws IOException {
    return (isBroadcast(this.fd) != 0);
  }
  
  public final int getSoLinger() throws IOException {
    return getSoLinger(this.fd);
  }
  
  public final int getSoError() throws IOException {
    return getSoError(this.fd);
  }
  
  public final int getTrafficClass() throws IOException {
    return getTrafficClass(this.fd, this.ipv6);
  }
  
  public final void setKeepAlive(boolean keepAlive) throws IOException {
    setKeepAlive(this.fd, keepAlive ? 1 : 0);
  }
  
  public final void setReceiveBufferSize(int receiveBufferSize) throws IOException {
    setReceiveBufferSize(this.fd, receiveBufferSize);
  }
  
  public final void setSendBufferSize(int sendBufferSize) throws IOException {
    setSendBufferSize(this.fd, sendBufferSize);
  }
  
  public final void setTcpNoDelay(boolean tcpNoDelay) throws IOException {
    setTcpNoDelay(this.fd, tcpNoDelay ? 1 : 0);
  }
  
  public final void setSoLinger(int soLinger) throws IOException {
    setSoLinger(this.fd, soLinger);
  }
  
  public final void setReuseAddress(boolean reuseAddress) throws IOException {
    setReuseAddress(this.fd, reuseAddress ? 1 : 0);
  }
  
  public final void setReusePort(boolean reusePort) throws IOException {
    setReusePort(this.fd, reusePort ? 1 : 0);
  }
  
  public final void setBroadcast(boolean broadcast) throws IOException {
    setBroadcast(this.fd, broadcast ? 1 : 0);
  }
  
  public final void setTrafficClass(int trafficClass) throws IOException {
    setTrafficClass(this.fd, this.ipv6, trafficClass);
  }
  
  public void setIntOpt(int level, int optname, int optvalue) throws IOException {
    setIntOpt(this.fd, level, optname, optvalue);
  }
  
  public void setRawOpt(int level, int optname, ByteBuffer optvalue) throws IOException {
    int limit = optvalue.limit();
    if (optvalue.isDirect()) {
      setRawOptAddress(this.fd, level, optname, 
          Buffer.memoryAddress(optvalue) + optvalue.position(), optvalue.remaining());
    } else if (optvalue.hasArray()) {
      setRawOptArray(this.fd, level, optname, optvalue
          .array(), optvalue.arrayOffset() + optvalue.position(), optvalue.remaining());
    } else {
      byte[] bytes = new byte[optvalue.remaining()];
      optvalue.duplicate().get(bytes);
      setRawOptArray(this.fd, level, optname, bytes, 0, bytes.length);
    } 
    optvalue.position(limit);
  }
  
  public int getIntOpt(int level, int optname) throws IOException {
    return getIntOpt(this.fd, level, optname);
  }
  
  public void getRawOpt(int level, int optname, ByteBuffer out) throws IOException {
    if (out.isDirect()) {
      getRawOptAddress(this.fd, level, optname, Buffer.memoryAddress(out) + out.position(), out.remaining());
    } else if (out.hasArray()) {
      getRawOptArray(this.fd, level, optname, out.array(), out.position() + out.arrayOffset(), out.remaining());
    } else {
      byte[] outArray = new byte[out.remaining()];
      getRawOptArray(this.fd, level, optname, outArray, 0, outArray.length);
      out.put(outArray);
    } 
    out.position(out.limit());
  }
  
  public static boolean isIPv6Preferred() {
    return isIpv6Preferred;
  }
  
  public static boolean shouldUseIpv6(InternetProtocolFamily family) {
    return (family == null) ? isIPv6Preferred() : ((family == InternetProtocolFamily.IPv6));
  }
  
  private static native boolean isIPv6Preferred0(boolean paramBoolean);
  
  private static native boolean isIPv6(int paramInt);
  
  public String toString() {
    return "Socket{fd=" + this.fd + '}';
  }
  
  public static Socket newSocketStream() {
    return new Socket(newSocketStream0());
  }
  
  public static Socket newSocketDgram() {
    return new Socket(newSocketDgram0());
  }
  
  public static Socket newSocketDomain() {
    return new Socket(newSocketDomain0());
  }
  
  public static Socket newSocketDomainDgram() {
    return new Socket(newSocketDomainDgram0());
  }
  
  public static void initialize() {
    isIpv6Preferred = isIPv6Preferred0(NetUtil.isIpV4StackPreferred());
  }
  
  protected static int newSocketStream0() {
    return newSocketStream0(isIPv6Preferred());
  }
  
  protected static int newSocketStream0(InternetProtocolFamily protocol) {
    return newSocketStream0(shouldUseIpv6(protocol));
  }
  
  protected static int newSocketStream0(boolean ipv6) {
    int res = newSocketStreamFd(ipv6);
    if (res < 0)
      throw new ChannelException(Errors.newIOException("newSocketStream", res)); 
    return res;
  }
  
  protected static int newSocketDgram0() {
    return newSocketDgram0(isIPv6Preferred());
  }
  
  protected static int newSocketDgram0(InternetProtocolFamily family) {
    return newSocketDgram0(shouldUseIpv6(family));
  }
  
  protected static int newSocketDgram0(boolean ipv6) {
    int res = newSocketDgramFd(ipv6);
    if (res < 0)
      throw new ChannelException(Errors.newIOException("newSocketDgram", res)); 
    return res;
  }
  
  protected static int newSocketDomain0() {
    int res = newSocketDomainFd();
    if (res < 0)
      throw new ChannelException(Errors.newIOException("newSocketDomain", res)); 
    return res;
  }
  
  protected static int newSocketDomainDgram0() {
    int res = newSocketDomainDgramFd();
    if (res < 0)
      throw new ChannelException(Errors.newIOException("newSocketDomainDgram", res)); 
    return res;
  }
  
  private static native int shutdown(int paramInt, boolean paramBoolean1, boolean paramBoolean2);
  
  private static native int connect(int paramInt1, boolean paramBoolean, byte[] paramArrayOfbyte, int paramInt2, int paramInt3);
  
  private static native int connectDomainSocket(int paramInt, byte[] paramArrayOfbyte);
  
  private static native int finishConnect(int paramInt);
  
  private static native int disconnect(int paramInt, boolean paramBoolean);
  
  private static native int bind(int paramInt1, boolean paramBoolean, byte[] paramArrayOfbyte, int paramInt2, int paramInt3);
  
  private static native int bindDomainSocket(int paramInt, byte[] paramArrayOfbyte);
  
  private static native int listen(int paramInt1, int paramInt2);
  
  private static native int accept(int paramInt, byte[] paramArrayOfbyte);
  
  private static native byte[] remoteAddress(int paramInt);
  
  private static native byte[] remoteDomainSocketAddress(int paramInt);
  
  private static native byte[] localAddress(int paramInt);
  
  private static native byte[] localDomainSocketAddress(int paramInt);
  
  private static native int send(int paramInt1, ByteBuffer paramByteBuffer, int paramInt2, int paramInt3);
  
  private static native int sendAddress(int paramInt1, long paramLong, int paramInt2, int paramInt3);
  
  private static native int recv(int paramInt1, ByteBuffer paramByteBuffer, int paramInt2, int paramInt3);
  
  private static native int recvAddress(int paramInt1, long paramLong, int paramInt2, int paramInt3);
  
  private static native int sendTo(int paramInt1, boolean paramBoolean, ByteBuffer paramByteBuffer, int paramInt2, int paramInt3, byte[] paramArrayOfbyte, int paramInt4, int paramInt5, int paramInt6);
  
  private static native int sendToAddress(int paramInt1, boolean paramBoolean, long paramLong, int paramInt2, int paramInt3, byte[] paramArrayOfbyte, int paramInt4, int paramInt5, int paramInt6);
  
  private static native int sendToAddresses(int paramInt1, boolean paramBoolean, long paramLong, int paramInt2, byte[] paramArrayOfbyte, int paramInt3, int paramInt4, int paramInt5);
  
  private static native int sendToDomainSocket(int paramInt1, ByteBuffer paramByteBuffer, int paramInt2, int paramInt3, byte[] paramArrayOfbyte);
  
  private static native int sendToAddressDomainSocket(int paramInt1, long paramLong, int paramInt2, int paramInt3, byte[] paramArrayOfbyte);
  
  private static native int sendToAddressesDomainSocket(int paramInt1, long paramLong, int paramInt2, byte[] paramArrayOfbyte);
  
  private static native DatagramSocketAddress recvFrom(int paramInt1, ByteBuffer paramByteBuffer, int paramInt2, int paramInt3) throws IOException;
  
  private static native DatagramSocketAddress recvFromAddress(int paramInt1, long paramLong, int paramInt2, int paramInt3) throws IOException;
  
  private static native DomainDatagramSocketAddress recvFromDomainSocket(int paramInt1, ByteBuffer paramByteBuffer, int paramInt2, int paramInt3) throws IOException;
  
  private static native DomainDatagramSocketAddress recvFromAddressDomainSocket(int paramInt1, long paramLong, int paramInt2, int paramInt3) throws IOException;
  
  private static native int recvFd(int paramInt);
  
  private static native int sendFd(int paramInt1, int paramInt2);
  
  private static native int msgFastopen();
  
  private static native int newSocketStreamFd(boolean paramBoolean);
  
  private static native int newSocketDgramFd(boolean paramBoolean);
  
  private static native int newSocketDomainFd();
  
  private static native int newSocketDomainDgramFd();
  
  private static native int isReuseAddress(int paramInt) throws IOException;
  
  private static native int isReusePort(int paramInt) throws IOException;
  
  private static native int getReceiveBufferSize(int paramInt) throws IOException;
  
  private static native int getSendBufferSize(int paramInt) throws IOException;
  
  private static native int isKeepAlive(int paramInt) throws IOException;
  
  private static native int isTcpNoDelay(int paramInt) throws IOException;
  
  private static native int isBroadcast(int paramInt) throws IOException;
  
  private static native int getSoLinger(int paramInt) throws IOException;
  
  private static native int getSoError(int paramInt) throws IOException;
  
  private static native int getTrafficClass(int paramInt, boolean paramBoolean) throws IOException;
  
  private static native void setReuseAddress(int paramInt1, int paramInt2) throws IOException;
  
  private static native void setReusePort(int paramInt1, int paramInt2) throws IOException;
  
  private static native void setKeepAlive(int paramInt1, int paramInt2) throws IOException;
  
  private static native void setReceiveBufferSize(int paramInt1, int paramInt2) throws IOException;
  
  private static native void setSendBufferSize(int paramInt1, int paramInt2) throws IOException;
  
  private static native void setTcpNoDelay(int paramInt1, int paramInt2) throws IOException;
  
  private static native void setSoLinger(int paramInt1, int paramInt2) throws IOException;
  
  private static native void setBroadcast(int paramInt1, int paramInt2) throws IOException;
  
  private static native void setTrafficClass(int paramInt1, boolean paramBoolean, int paramInt2) throws IOException;
  
  private static native void setIntOpt(int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws IOException;
  
  private static native void setRawOptArray(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfbyte, int paramInt4, int paramInt5) throws IOException;
  
  private static native void setRawOptAddress(int paramInt1, int paramInt2, int paramInt3, long paramLong, int paramInt4) throws IOException;
  
  private static native int getIntOpt(int paramInt1, int paramInt2, int paramInt3) throws IOException;
  
  private static native void getRawOptArray(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfbyte, int paramInt4, int paramInt5) throws IOException;
  
  private static native void getRawOptAddress(int paramInt1, int paramInt2, int paramInt3, long paramLong, int paramInt4) throws IOException;
}
