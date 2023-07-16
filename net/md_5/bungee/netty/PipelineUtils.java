package net.md_5.bungee.netty;

import com.google.common.base.Preconditions;
import dev._2lstudios.flamecord.FlameCord;
import io.github.waterfallmc.waterfall.event.ConnectionInitEvent;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import io.netty.util.internal.PlatformDependent;
import java.net.SocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.event.ClientConnectEvent;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.protocol.KickStringWriter;
import net.md_5.bungee.protocol.LegacyDecoder;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.MinecraftEncoder;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.Varint21FrameDecoder;
import net.md_5.bungee.protocol.Varint21LengthFieldExtraBufPrepender;
import net.md_5.bungee.protocol.Varint21LengthFieldPrepender;

public class PipelineUtils {
  public static final AttributeKey<ListenerInfo> LISTENER = AttributeKey.newInstance("ListerInfo");
  
  public static final ChannelInitializer<Channel> SERVER_CHILD = new ChannelInitializer<Channel>() {
      protected void initChannel(Channel ch) throws Exception {
        SocketAddress remoteAddress = (ch.remoteAddress() == null) ? ch.parent().localAddress() : ch.remoteAddress();
        FlameCord flameCord = FlameCord.getInstance();
        String firewallReason = flameCord.getAddressDataManager().getAddressData(remoteAddress).getFirewallReason();
        if (firewallReason != null) {
          if (flameCord.getFlameCordConfiguration().isAntibotFirewallLog())
            flameCord.getLoggerWrapper().log(Level.INFO, "[FlameCord] [{0}] is firewalled from the server. ({1})", new Object[] { remoteAddress, firewallReason }); 
          ch.close();
          return;
        } 
        if (BungeeCord.getInstance().getConnectionThrottle() != null && BungeeCord.getInstance().getConnectionThrottle().throttle(remoteAddress)) {
          ch.close();
          return;
        } 
        ListenerInfo listener = (ListenerInfo)ch.attr(PipelineUtils.LISTENER).get();
        if (((ClientConnectEvent)BungeeCord.getInstance().getPluginManager().callEvent((Event)new ClientConnectEvent(remoteAddress, listener))).isCancelled()) {
          ch.close();
          return;
        } 
        ConnectionInitEvent connectionInitEvent = new ConnectionInitEvent(ch.remoteAddress(), listener, (result, throwable) -> {
              if (result.isCancelled()) {
                ch.close();
                return;
              } 
              try {
                PipelineUtils.BASE.initChannel(ch);
              } catch (Exception e) {
                e.printStackTrace();
                ch.close();
                return;
              } 
              ch.pipeline().addBefore("frame-decoder", "legacy-decoder", (ChannelHandler)new LegacyDecoder());
              ch.pipeline().addAfter("frame-decoder", "packet-decoder", (ChannelHandler)new MinecraftDecoder(Protocol.HANDSHAKE, true, ProxyServer.getInstance().getProtocolVersion()));
              ch.pipeline().addAfter("frame-prepender", "packet-encoder", (ChannelHandler)new MinecraftEncoder(Protocol.HANDSHAKE, true, ProxyServer.getInstance().getProtocolVersion()));
              ch.pipeline().addBefore("frame-prepender", "legacy-kick", (ChannelHandler)PipelineUtils.legacyKicker);
              ((HandlerBoss)ch.pipeline().get(HandlerBoss.class)).setHandler((PacketHandler)new InitialHandler(BungeeCord.getInstance(), listener));
              if (listener.isProxyProtocol())
                ch.pipeline().addFirst(new ChannelHandler[] { (ChannelHandler)new HAProxyMessageDecoder() }); 
            });
        BungeeCord.getInstance().getPluginManager().callEvent((Event)connectionInitEvent);
      }
      
      public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
      }
    };
  
  public static final Base BASE = new Base(false);
  
  public static final Base BASE_SERVERSIDE = new Base(true);
  
  private static final KickStringWriter legacyKicker = new KickStringWriter();
  
  public static final Varint21LengthFieldPrepender framePrepender = new Varint21LengthFieldPrepender();
  
  private static final Varint21LengthFieldExtraBufPrepender serverFramePrepender = new Varint21LengthFieldExtraBufPrepender();
  
  public static final String TIMEOUT_HANDLER = "timeout";
  
  public static final String PACKET_DECODER = "packet-decoder";
  
  public static final String PACKET_ENCODER = "packet-encoder";
  
  public static final String BOSS_HANDLER = "inbound-boss";
  
  public static final String ENCRYPT_HANDLER = "encrypt";
  
  public static final String DECRYPT_HANDLER = "decrypt";
  
  public static final String FRAME_DECODER = "frame-decoder";
  
  public static final String FRAME_PREPENDER = "frame-prepender";
  
  public static final String LEGACY_DECODER = "legacy-decoder";
  
  public static final String LEGACY_KICKER = "legacy-kick";
  
  private static boolean epoll;
  
  static {
    if (!PlatformDependent.isWindows() && Boolean.parseBoolean(System.getProperty("bungee.epoll", "true"))) {
      ProxyServer.getInstance().getLogger().info("Not on Windows, attempting to use enhanced EpollEventLoop");
      if (epoll = Epoll.isAvailable()) {
        ProxyServer.getInstance().getLogger().info("Epoll is working, utilising it!");
      } else {
        ProxyServer.getInstance().getLogger().log(Level.WARNING, "Epoll is not working, falling back to NIO: {0}", Util.exception(Epoll.unavailabilityCause()));
      } 
    } 
  }
  
  private static final ChannelFactory<? extends ServerChannel> serverChannelFactory = epoll ? EpollServerSocketChannel::new : NioServerSocketChannel::new;
  
  private static final ChannelFactory<? extends ServerChannel> serverChannelDomainFactory = epoll ? EpollServerDomainSocketChannel::new : null;
  
  private static final ChannelFactory<? extends Channel> channelFactory = epoll ? EpollSocketChannel::new : NioSocketChannel::new;
  
  private static final ChannelFactory<? extends Channel> channelDomainFactory = epoll ? EpollDomainSocketChannel::new : null;
  
  public static EventLoopGroup newEventLoopGroup(int threads, ThreadFactory factory) {
    return epoll ? (EventLoopGroup)new EpollEventLoopGroup(threads, factory) : (EventLoopGroup)new NioEventLoopGroup(threads, factory);
  }
  
  public static Class<? extends ServerChannel> getServerChannel(SocketAddress address) {
    if (address instanceof io.netty.channel.unix.DomainSocketAddress) {
      Preconditions.checkState(epoll, "Epoll required to have UNIX sockets");
      return (Class)EpollServerDomainSocketChannel.class;
    } 
    return epoll ? (Class)EpollServerSocketChannel.class : (Class)NioServerSocketChannel.class;
  }
  
  public static Class<? extends Channel> getChannel(SocketAddress address) {
    if (address instanceof io.netty.channel.unix.DomainSocketAddress) {
      Preconditions.checkState(epoll, "Epoll required to have UNIX sockets");
      return (Class)EpollDomainSocketChannel.class;
    } 
    return epoll ? (Class)EpollSocketChannel.class : (Class)NioSocketChannel.class;
  }
  
  public static ChannelFactory<? extends ServerChannel> getServerChannelFactory(SocketAddress address) {
    if (address instanceof io.netty.channel.unix.DomainSocketAddress) {
      ChannelFactory<? extends ServerChannel> factory = serverChannelDomainFactory;
      Preconditions.checkState((factory != null), "Epoll required to have UNIX sockets");
      return factory;
    } 
    return serverChannelFactory;
  }
  
  public static ChannelFactory<? extends Channel> getChannelFactory(SocketAddress address) {
    if (address instanceof io.netty.channel.unix.DomainSocketAddress) {
      ChannelFactory<? extends Channel> factory = channelDomainFactory;
      Preconditions.checkState((factory != null), "Epoll required to have UNIX sockets");
      return factory;
    } 
    return channelFactory;
  }
  
  public static Class<? extends DatagramChannel> getDatagramChannel() {
    return epoll ? (Class)EpollDatagramChannel.class : (Class)NioDatagramChannel.class;
  }
  
  private static final int LOW_MARK = Integer.getInteger("net.md_5.bungee.low_mark", 524288).intValue();
  
  private static final int HIGH_MARK = Integer.getInteger("net.md_5.bungee.high_mark", 2097152).intValue();
  
  private static final WriteBufferWaterMark MARK = new WriteBufferWaterMark(LOW_MARK, HIGH_MARK);
  
  public static final class Base extends ChannelInitializer<Channel> {
    public Base(boolean toServer) {
      this.toServer = toServer;
    }
    
    private boolean toServer = false;
    
    public void initChannel(Channel ch) throws Exception {
      ChannelPipeline pipeline = ch.pipeline();
      ChannelConfig channelConfig = ch.config();
      try {
        channelConfig.setOption(ChannelOption.IP_TOS, Integer.valueOf(24));
      } catch (ChannelException channelException) {}
      channelConfig.setOption(ChannelOption.TCP_NODELAY, Boolean.valueOf(true));
      channelConfig.setOption(ChannelOption.TCP_FASTOPEN, Integer.valueOf(FlameCord.getInstance().getFlameCordConfiguration().getTcpFastOpen()));
      channelConfig.setAllocator((ByteBufAllocator)PooledByteBufAllocator.DEFAULT);
      channelConfig.setWriteBufferWaterMark(PipelineUtils.MARK);
      pipeline.addLast("frame-decoder", (ChannelHandler)new Varint21FrameDecoder());
      pipeline.addLast("timeout", (ChannelHandler)new ReadTimeoutHandler((BungeeCord.getInstance()).config.getTimeout(), TimeUnit.MILLISECONDS));
      pipeline.addLast("frame-prepender", this.toServer ? (ChannelHandler)PipelineUtils.serverFramePrepender : (ChannelHandler)PipelineUtils.framePrepender);
      pipeline.addLast("inbound-boss", (ChannelHandler)new HandlerBoss());
    }
    
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      cause.printStackTrace();
      ctx.close();
    }
    
    public Base() {}
  }
}
