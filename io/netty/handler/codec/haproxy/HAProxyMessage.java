package io.netty.handler.codec.haproxy;

import io.netty.buffer.ByteBuf;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HAProxyMessage extends AbstractReferenceCounted {
  private static final int MAX_NESTING_LEVEL = 128;
  
  private static final ResourceLeakDetector<HAProxyMessage> leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(HAProxyMessage.class);
  
  private final ResourceLeakTracker<HAProxyMessage> leak;
  
  private final HAProxyProtocolVersion protocolVersion;
  
  private final HAProxyCommand command;
  
  private final HAProxyProxiedProtocol proxiedProtocol;
  
  private final String sourceAddress;
  
  private final String destinationAddress;
  
  private final int sourcePort;
  
  private final int destinationPort;
  
  private final List<HAProxyTLV> tlvs;
  
  private HAProxyMessage(HAProxyProtocolVersion protocolVersion, HAProxyCommand command, HAProxyProxiedProtocol proxiedProtocol, String sourceAddress, String destinationAddress, String sourcePort, String destinationPort) {
    this(protocolVersion, command, proxiedProtocol, sourceAddress, destinationAddress, 
        
        portStringToInt(sourcePort), portStringToInt(destinationPort));
  }
  
  public HAProxyMessage(HAProxyProtocolVersion protocolVersion, HAProxyCommand command, HAProxyProxiedProtocol proxiedProtocol, String sourceAddress, String destinationAddress, int sourcePort, int destinationPort) {
    this(protocolVersion, command, proxiedProtocol, sourceAddress, destinationAddress, sourcePort, destinationPort, 
        Collections.emptyList());
  }
  
  public HAProxyMessage(HAProxyProtocolVersion protocolVersion, HAProxyCommand command, HAProxyProxiedProtocol proxiedProtocol, String sourceAddress, String destinationAddress, int sourcePort, int destinationPort, List<? extends HAProxyTLV> tlvs) {
    ObjectUtil.checkNotNull(protocolVersion, "protocolVersion");
    ObjectUtil.checkNotNull(proxiedProtocol, "proxiedProtocol");
    ObjectUtil.checkNotNull(tlvs, "tlvs");
    HAProxyProxiedProtocol.AddressFamily addrFamily = proxiedProtocol.addressFamily();
    checkAddress(sourceAddress, addrFamily);
    checkAddress(destinationAddress, addrFamily);
    checkPort(sourcePort, addrFamily);
    checkPort(destinationPort, addrFamily);
    this.protocolVersion = protocolVersion;
    this.command = command;
    this.proxiedProtocol = proxiedProtocol;
    this.sourceAddress = sourceAddress;
    this.destinationAddress = destinationAddress;
    this.sourcePort = sourcePort;
    this.destinationPort = destinationPort;
    this.tlvs = Collections.unmodifiableList(tlvs);
    this.leak = leakDetector.track(this);
  }
  
  static HAProxyMessage decodeHeader(ByteBuf header) {
    HAProxyProtocolVersion ver;
    HAProxyCommand cmd;
    HAProxyProxiedProtocol protAndFam;
    String srcAddress, dstAddress;
    ObjectUtil.checkNotNull(header, "header");
    if (header.readableBytes() < 16)
      throw new HAProxyProtocolException("incomplete header: " + header
          .readableBytes() + " bytes (expected: 16+ bytes)"); 
    header.skipBytes(12);
    byte verCmdByte = header.readByte();
    try {
      ver = HAProxyProtocolVersion.valueOf(verCmdByte);
    } catch (IllegalArgumentException e) {
      throw new HAProxyProtocolException(e);
    } 
    if (ver != HAProxyProtocolVersion.V2)
      throw new HAProxyProtocolException("version 1 unsupported: 0x" + Integer.toHexString(verCmdByte)); 
    try {
      cmd = HAProxyCommand.valueOf(verCmdByte);
    } catch (IllegalArgumentException e) {
      throw new HAProxyProtocolException(e);
    } 
    if (cmd == HAProxyCommand.LOCAL)
      return unknownMsg(HAProxyProtocolVersion.V2, HAProxyCommand.LOCAL); 
    try {
      protAndFam = HAProxyProxiedProtocol.valueOf(header.readByte());
    } catch (IllegalArgumentException e) {
      throw new HAProxyProtocolException(e);
    } 
    if (protAndFam == HAProxyProxiedProtocol.UNKNOWN)
      return unknownMsg(HAProxyProtocolVersion.V2, HAProxyCommand.PROXY); 
    int addressInfoLen = header.readUnsignedShort();
    int srcPort = 0;
    int dstPort = 0;
    HAProxyProxiedProtocol.AddressFamily addressFamily = protAndFam.addressFamily();
    if (addressFamily == HAProxyProxiedProtocol.AddressFamily.AF_UNIX) {
      int addressLen;
      if (addressInfoLen < 216 || header.readableBytes() < 216)
        throw new HAProxyProtocolException("incomplete UNIX socket address information: " + 
            
            Math.min(addressInfoLen, header.readableBytes()) + " bytes (expected: 216+ bytes)"); 
      int startIdx = header.readerIndex();
      int addressEnd = header.forEachByte(startIdx, 108, ByteProcessor.FIND_NUL);
      if (addressEnd == -1) {
        addressLen = 108;
      } else {
        addressLen = addressEnd - startIdx;
      } 
      srcAddress = header.toString(startIdx, addressLen, CharsetUtil.US_ASCII);
      startIdx += 108;
      addressEnd = header.forEachByte(startIdx, 108, ByteProcessor.FIND_NUL);
      if (addressEnd == -1) {
        addressLen = 108;
      } else {
        addressLen = addressEnd - startIdx;
      } 
      dstAddress = header.toString(startIdx, addressLen, CharsetUtil.US_ASCII);
      header.readerIndex(startIdx + 108);
    } else {
      int addressLen;
      if (addressFamily == HAProxyProxiedProtocol.AddressFamily.AF_IPv4) {
        if (addressInfoLen < 12 || header.readableBytes() < 12)
          throw new HAProxyProtocolException("incomplete IPv4 address information: " + 
              
              Math.min(addressInfoLen, header.readableBytes()) + " bytes (expected: 12+ bytes)"); 
        addressLen = 4;
      } else if (addressFamily == HAProxyProxiedProtocol.AddressFamily.AF_IPv6) {
        if (addressInfoLen < 36 || header.readableBytes() < 36)
          throw new HAProxyProtocolException("incomplete IPv6 address information: " + 
              
              Math.min(addressInfoLen, header.readableBytes()) + " bytes (expected: 36+ bytes)"); 
        addressLen = 16;
      } else {
        throw new HAProxyProtocolException("unable to parse address information (unknown address family: " + addressFamily + ')');
      } 
      srcAddress = ipBytesToString(header, addressLen);
      dstAddress = ipBytesToString(header, addressLen);
      srcPort = header.readUnsignedShort();
      dstPort = header.readUnsignedShort();
    } 
    List<HAProxyTLV> tlvs = readTlvs(header);
    return new HAProxyMessage(ver, cmd, protAndFam, srcAddress, dstAddress, srcPort, dstPort, tlvs);
  }
  
  private static List<HAProxyTLV> readTlvs(ByteBuf header) {
    HAProxyTLV haProxyTLV = readNextTLV(header, 0);
    if (haProxyTLV == null)
      return Collections.emptyList(); 
    List<HAProxyTLV> haProxyTLVs = new ArrayList<HAProxyTLV>(4);
    while (true) {
      haProxyTLVs.add(haProxyTLV);
      if (haProxyTLV instanceof HAProxySSLTLV)
        haProxyTLVs.addAll(((HAProxySSLTLV)haProxyTLV).encapsulatedTLVs()); 
      if ((haProxyTLV = readNextTLV(header, 0)) == null)
        return haProxyTLVs; 
    } 
  }
  
  private static HAProxyTLV readNextTLV(ByteBuf header, int nestingLevel) {
    ByteBuf rawContent, byteBuf;
    byte client;
    int verify;
    if (nestingLevel > 128)
      throw new HAProxyProtocolException("Maximum TLV nesting level reached: " + nestingLevel + " (expected: < " + '' + ')'); 
    if (header.readableBytes() < 4)
      return null; 
    byte typeAsByte = header.readByte();
    HAProxyTLV.Type type = HAProxyTLV.Type.typeForByteValue(typeAsByte);
    int length = header.readUnsignedShort();
    switch (type) {
      case AF_UNSPEC:
        rawContent = header.retainedSlice(header.readerIndex(), length);
        byteBuf = header.readSlice(length);
        client = byteBuf.readByte();
        verify = byteBuf.readInt();
        if (byteBuf.readableBytes() >= 4) {
          List<HAProxyTLV> encapsulatedTlvs = new ArrayList<HAProxyTLV>(4);
          do {
            HAProxyTLV haProxyTLV = readNextTLV(byteBuf, nestingLevel + 1);
            if (haProxyTLV == null)
              break; 
            encapsulatedTlvs.add(haProxyTLV);
          } while (byteBuf.readableBytes() >= 4);
          return new HAProxySSLTLV(verify, client, encapsulatedTlvs, rawContent);
        } 
        return new HAProxySSLTLV(verify, client, Collections.emptyList(), rawContent);
      case AF_UNIX:
      case AF_IPv4:
      case AF_IPv6:
      case null:
      case null:
      case null:
        return new HAProxyTLV(type, typeAsByte, header.readRetainedSlice(length));
    } 
    return null;
  }
  
  static HAProxyMessage decodeHeader(String header) {
    HAProxyProxiedProtocol protAndFam;
    if (header == null)
      throw new HAProxyProtocolException("header"); 
    String[] parts = header.split(" ");
    int numParts = parts.length;
    if (numParts < 2)
      throw new HAProxyProtocolException("invalid header: " + header + " (expected: 'PROXY' and proxied protocol values)"); 
    if (!"PROXY".equals(parts[0]))
      throw new HAProxyProtocolException("unknown identifier: " + parts[0]); 
    try {
      protAndFam = HAProxyProxiedProtocol.valueOf(parts[1]);
    } catch (IllegalArgumentException e) {
      throw new HAProxyProtocolException(e);
    } 
    if (protAndFam != HAProxyProxiedProtocol.TCP4 && protAndFam != HAProxyProxiedProtocol.TCP6 && protAndFam != HAProxyProxiedProtocol.UNKNOWN)
      throw new HAProxyProtocolException("unsupported v1 proxied protocol: " + parts[1]); 
    if (protAndFam == HAProxyProxiedProtocol.UNKNOWN)
      return unknownMsg(HAProxyProtocolVersion.V1, HAProxyCommand.PROXY); 
    if (numParts != 6)
      throw new HAProxyProtocolException("invalid TCP4/6 header: " + header + " (expected: 6 parts)"); 
    try {
      return new HAProxyMessage(HAProxyProtocolVersion.V1, HAProxyCommand.PROXY, protAndFam, parts[2], parts[3], parts[4], parts[5]);
    } catch (RuntimeException e) {
      throw new HAProxyProtocolException("invalid HAProxy message", e);
    } 
  }
  
  private static HAProxyMessage unknownMsg(HAProxyProtocolVersion version, HAProxyCommand command) {
    return new HAProxyMessage(version, command, HAProxyProxiedProtocol.UNKNOWN, null, null, 0, 0);
  }
  
  private static String ipBytesToString(ByteBuf header, int addressLen) {
    StringBuilder sb = new StringBuilder();
    int ipv4Len = 4;
    int ipv6Len = 8;
    if (addressLen == 4) {
      for (int i = 0; i < 4; i++) {
        sb.append(header.readByte() & 0xFF);
        sb.append('.');
      } 
    } else {
      for (int i = 0; i < 8; i++) {
        sb.append(Integer.toHexString(header.readUnsignedShort()));
        sb.append(':');
      } 
    } 
    sb.setLength(sb.length() - 1);
    return sb.toString();
  }
  
  private static int portStringToInt(String value) {
    int port;
    try {
      port = Integer.parseInt(value);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("invalid port: " + value, e);
    } 
    if (port <= 0 || port > 65535)
      throw new IllegalArgumentException("invalid port: " + value + " (expected: 1 ~ 65535)"); 
    return port;
  }
  
  private static void checkAddress(String address, HAProxyProxiedProtocol.AddressFamily addrFamily) {
    ObjectUtil.checkNotNull(addrFamily, "addrFamily");
    switch (addrFamily) {
      case AF_UNSPEC:
        if (address != null)
          throw new IllegalArgumentException("unable to validate an AF_UNSPEC address: " + address); 
        return;
      case AF_UNIX:
        ObjectUtil.checkNotNull(address, "address");
        if ((address.getBytes(CharsetUtil.US_ASCII)).length > 108)
          throw new IllegalArgumentException("invalid AF_UNIX address: " + address); 
        return;
    } 
    ObjectUtil.checkNotNull(address, "address");
    switch (addrFamily) {
      case AF_IPv4:
        if (!NetUtil.isValidIpV4Address(address))
          throw new IllegalArgumentException("invalid IPv4 address: " + address); 
        return;
      case AF_IPv6:
        if (!NetUtil.isValidIpV6Address(address))
          throw new IllegalArgumentException("invalid IPv6 address: " + address); 
        return;
    } 
    throw new IllegalArgumentException("unexpected addrFamily: " + addrFamily);
  }
  
  private static void checkPort(int port, HAProxyProxiedProtocol.AddressFamily addrFamily) {
    switch (addrFamily) {
      case AF_IPv4:
      case AF_IPv6:
        if (port < 0 || port > 65535)
          throw new IllegalArgumentException("invalid port: " + port + " (expected: 0 ~ 65535)"); 
        return;
      case AF_UNSPEC:
      case AF_UNIX:
        if (port != 0)
          throw new IllegalArgumentException("port cannot be specified with addrFamily: " + addrFamily); 
        return;
    } 
    throw new IllegalArgumentException("unexpected addrFamily: " + addrFamily);
  }
  
  public HAProxyProtocolVersion protocolVersion() {
    return this.protocolVersion;
  }
  
  public HAProxyCommand command() {
    return this.command;
  }
  
  public HAProxyProxiedProtocol proxiedProtocol() {
    return this.proxiedProtocol;
  }
  
  public String sourceAddress() {
    return this.sourceAddress;
  }
  
  public String destinationAddress() {
    return this.destinationAddress;
  }
  
  public int sourcePort() {
    return this.sourcePort;
  }
  
  public int destinationPort() {
    return this.destinationPort;
  }
  
  public List<HAProxyTLV> tlvs() {
    return this.tlvs;
  }
  
  int tlvNumBytes() {
    int tlvNumBytes = 0;
    for (int i = 0; i < this.tlvs.size(); i++)
      tlvNumBytes += ((HAProxyTLV)this.tlvs.get(i)).totalNumBytes(); 
    return tlvNumBytes;
  }
  
  public HAProxyMessage touch() {
    tryRecord();
    return (HAProxyMessage)super.touch();
  }
  
  public HAProxyMessage touch(Object hint) {
    if (this.leak != null)
      this.leak.record(hint); 
    return this;
  }
  
  public HAProxyMessage retain() {
    tryRecord();
    return (HAProxyMessage)super.retain();
  }
  
  public HAProxyMessage retain(int increment) {
    tryRecord();
    return (HAProxyMessage)super.retain(increment);
  }
  
  public boolean release() {
    tryRecord();
    return super.release();
  }
  
  public boolean release(int decrement) {
    tryRecord();
    return super.release(decrement);
  }
  
  private void tryRecord() {
    if (this.leak != null)
      this.leak.record(); 
  }
  
  protected void deallocate() {
    try {
      for (HAProxyTLV tlv : this.tlvs)
        tlv.release(); 
    } finally {
      ResourceLeakTracker<HAProxyMessage> leak = this.leak;
      if (leak != null) {
        boolean closed = leak.close(this);
        assert closed;
      } 
    } 
  }
  
  public String toString() {
    StringBuilder sb = (new StringBuilder(256)).append(StringUtil.simpleClassName(this)).append("(protocolVersion: ").append(this.protocolVersion).append(", command: ").append(this.command).append(", proxiedProtocol: ").append(this.proxiedProtocol).append(", sourceAddress: ").append(this.sourceAddress).append(", destinationAddress: ").append(this.destinationAddress).append(", sourcePort: ").append(this.sourcePort).append(", destinationPort: ").append(this.destinationPort).append(", tlvs: [");
    if (!this.tlvs.isEmpty()) {
      for (HAProxyTLV tlv : this.tlvs)
        sb.append(tlv).append(", "); 
      sb.setLength(sb.length() - 2);
    } 
    sb.append("])");
    return sb.toString();
  }
}
