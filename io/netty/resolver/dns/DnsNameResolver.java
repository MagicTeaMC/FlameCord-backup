package io.netty.resolver.dns;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.dns.DatagramDnsQueryEncoder;
import io.netty.handler.codec.dns.DatagramDnsResponse;
import io.netty.handler.codec.dns.DatagramDnsResponseDecoder;
import io.netty.handler.codec.dns.DefaultDnsRawRecord;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.TcpDnsQueryEncoder;
import io.netty.handler.codec.dns.TcpDnsResponseDecoder;
import io.netty.resolver.DefaultHostsFileEntriesResolver;
import io.netty.resolver.HostsFileEntriesResolver;
import io.netty.resolver.InetNameResolver;
import io.netty.resolver.ResolvedAddressTypes;
import io.netty.util.NetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import java.net.IDN;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DnsNameResolver extends InetNameResolver {
  private static final InternalLogger logger;
  
  private static final String LOCALHOST = "localhost";
  
  private static final String WINDOWS_HOST_NAME;
  
  private static final InetAddress LOCALHOST_ADDRESS;
  
  private static final DnsRecord[] EMPTY_ADDITIONALS;
  
  private static final DnsRecordType[] IPV4_ONLY_RESOLVED_RECORD_TYPES;
  
  private static final InternetProtocolFamily[] IPV4_ONLY_RESOLVED_PROTOCOL_FAMILIES;
  
  private static final DnsRecordType[] IPV4_PREFERRED_RESOLVED_RECORD_TYPES;
  
  private static final InternetProtocolFamily[] IPV4_PREFERRED_RESOLVED_PROTOCOL_FAMILIES;
  
  private static final DnsRecordType[] IPV6_ONLY_RESOLVED_RECORD_TYPES;
  
  private static final InternetProtocolFamily[] IPV6_ONLY_RESOLVED_PROTOCOL_FAMILIES;
  
  private static final DnsRecordType[] IPV6_PREFERRED_RESOLVED_RECORD_TYPES;
  
  private static final InternetProtocolFamily[] IPV6_PREFERRED_RESOLVED_PROTOCOL_FAMILIES;
  
  static final ResolvedAddressTypes DEFAULT_RESOLVE_ADDRESS_TYPES;
  
  static final String[] DEFAULT_SEARCH_DOMAINS;
  
  private static final UnixResolverOptions DEFAULT_OPTIONS;
  
  private static final DatagramDnsResponseDecoder DATAGRAM_DECODER;
  
  private static final DatagramDnsQueryEncoder DATAGRAM_ENCODER;
  
  private static final TcpDnsQueryEncoder TCP_ENCODER;
  
  private final Promise<Channel> channelReadyPromise;
  
  private final Channel ch;
  
  private final Comparator<InetSocketAddress> nameServerComparator;
  
  private final DnsQueryContextManager queryContextManager;
  
  private final DnsCache resolveCache;
  
  private final AuthoritativeDnsServerCache authoritativeDnsServerCache;
  
  private final DnsCnameCache cnameCache;
  
  private final FastThreadLocal<DnsServerAddressStream> nameServerAddrStream;
  
  private final long queryTimeoutMillis;
  
  private final int maxQueriesPerResolve;
  
  private final ResolvedAddressTypes resolvedAddressTypes;
  
  private final InternetProtocolFamily[] resolvedInternetProtocolFamilies;
  
  private final boolean recursionDesired;
  
  private final int maxPayloadSize;
  
  private final boolean optResourceEnabled;
  
  private final HostsFileEntriesResolver hostsFileEntriesResolver;
  
  private final DnsServerAddressStreamProvider dnsServerAddressStreamProvider;
  
  private final String[] searchDomains;
  
  private final int ndots;
  
  private final boolean supportsAAAARecords;
  
  private final boolean supportsARecords;
  
  private final InternetProtocolFamily preferredAddressType;
  
  private final DnsRecordType[] resolveRecordTypes;
  
  private final boolean decodeIdn;
  
  private final DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory;
  
  private final boolean completeOncePreferredResolved;
  
  private final Bootstrap socketBootstrap;
  
  private final int maxNumConsolidation;
  
  private final Map<String, Future<List<InetAddress>>> inflightLookups;
  
  static {
    String hostName, searchDomains[];
    UnixResolverOptions options;
  }
  
  static {
    logger = InternalLoggerFactory.getInstance(DnsNameResolver.class);
    EMPTY_ADDITIONALS = new DnsRecord[0];
    IPV4_ONLY_RESOLVED_RECORD_TYPES = new DnsRecordType[] { DnsRecordType.A };
    IPV4_ONLY_RESOLVED_PROTOCOL_FAMILIES = new InternetProtocolFamily[] { InternetProtocolFamily.IPv4 };
    IPV4_PREFERRED_RESOLVED_RECORD_TYPES = new DnsRecordType[] { DnsRecordType.A, DnsRecordType.AAAA };
    IPV4_PREFERRED_RESOLVED_PROTOCOL_FAMILIES = new InternetProtocolFamily[] { InternetProtocolFamily.IPv4, InternetProtocolFamily.IPv6 };
    IPV6_ONLY_RESOLVED_RECORD_TYPES = new DnsRecordType[] { DnsRecordType.AAAA };
    IPV6_ONLY_RESOLVED_PROTOCOL_FAMILIES = new InternetProtocolFamily[] { InternetProtocolFamily.IPv6 };
    IPV6_PREFERRED_RESOLVED_RECORD_TYPES = new DnsRecordType[] { DnsRecordType.AAAA, DnsRecordType.A };
    IPV6_PREFERRED_RESOLVED_PROTOCOL_FAMILIES = new InternetProtocolFamily[] { InternetProtocolFamily.IPv6, InternetProtocolFamily.IPv4 };
    if (NetUtil.isIpV4StackPreferred() || !anyInterfaceSupportsIpV6()) {
      DEFAULT_RESOLVE_ADDRESS_TYPES = ResolvedAddressTypes.IPV4_ONLY;
      LOCALHOST_ADDRESS = NetUtil.LOCALHOST4;
    } else if (NetUtil.isIpV6AddressesPreferred()) {
      DEFAULT_RESOLVE_ADDRESS_TYPES = ResolvedAddressTypes.IPV6_PREFERRED;
      LOCALHOST_ADDRESS = NetUtil.LOCALHOST6;
    } else {
      DEFAULT_RESOLVE_ADDRESS_TYPES = ResolvedAddressTypes.IPV4_PREFERRED;
      LOCALHOST_ADDRESS = NetUtil.LOCALHOST4;
    } 
    logger.debug("Default ResolvedAddressTypes: {}", DEFAULT_RESOLVE_ADDRESS_TYPES);
    logger.debug("Localhost address: {}", LOCALHOST_ADDRESS);
    try {
      hostName = PlatformDependent.isWindows() ? InetAddress.getLocalHost().getHostName() : null;
    } catch (Exception ignore) {
      hostName = null;
    } 
    WINDOWS_HOST_NAME = hostName;
    logger.debug("Windows hostname: {}", WINDOWS_HOST_NAME);
    try {
      List<String> list = PlatformDependent.isWindows() ? getSearchDomainsHack() : UnixResolverDnsServerAddressStreamProvider.parseEtcResolverSearchDomains();
      searchDomains = list.<String>toArray(new String[0]);
    } catch (Exception ignore) {
      searchDomains = EmptyArrays.EMPTY_STRINGS;
    } 
    DEFAULT_SEARCH_DOMAINS = searchDomains;
    logger.debug("Default search domains: {}", Arrays.toString((Object[])DEFAULT_SEARCH_DOMAINS));
    try {
      options = UnixResolverDnsServerAddressStreamProvider.parseEtcResolverOptions();
    } catch (Exception ignore) {
      options = UnixResolverOptions.newBuilder().build();
    } 
    DEFAULT_OPTIONS = options;
    logger.debug("Default {}", DEFAULT_OPTIONS);
    DATAGRAM_DECODER = new DatagramDnsResponseDecoder() {
        protected DnsResponse decodeResponse(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
          DnsResponse response = super.decodeResponse(ctx, packet);
          if (((ByteBuf)packet.content()).isReadable()) {
            response.setTruncated(true);
            if (DnsNameResolver.logger.isDebugEnabled())
              DnsNameResolver.logger.debug("{} RECEIVED: UDP [{}: {}] truncated packet received, consider adjusting maxPayloadSize for the {}.", new Object[] { ctx
                    .channel(), Integer.valueOf(response.id()), packet.sender(), 
                    StringUtil.simpleClassName(DnsNameResolver.class) }); 
          } 
          return response;
        }
      };
    DATAGRAM_ENCODER = new DatagramDnsQueryEncoder();
    TCP_ENCODER = new TcpDnsQueryEncoder();
  }
  
  private static boolean anyInterfaceSupportsIpV6() {
    for (NetworkInterface iface : NetUtil.NETWORK_INTERFACES) {
      Enumeration<InetAddress> addresses = iface.getInetAddresses();
      while (addresses.hasMoreElements()) {
        InetAddress inetAddress = addresses.nextElement();
        if (inetAddress instanceof java.net.Inet6Address && !inetAddress.isAnyLocalAddress() && !inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress())
          return true; 
      } 
    } 
    return false;
  }
  
  private static List<String> getSearchDomainsHack() throws Exception {
    if (PlatformDependent.javaVersion() < 9) {
      Class<?> configClass = Class.forName("sun.net.dns.ResolverConfiguration");
      Method open = configClass.getMethod("open", new Class[0]);
      Method nameservers = configClass.getMethod("searchlist", new Class[0]);
      Object instance = open.invoke(null, new Object[0]);
      return (List<String>)nameservers.invoke(instance, new Object[0]);
    } 
    return Collections.emptyList();
  }
  
  @Deprecated
  public DnsNameResolver(EventLoop eventLoop, ChannelFactory<? extends DatagramChannel> channelFactory, DnsCache resolveCache, DnsCache authoritativeDnsServerCache, DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory, long queryTimeoutMillis, ResolvedAddressTypes resolvedAddressTypes, boolean recursionDesired, int maxQueriesPerResolve, boolean traceEnabled, int maxPayloadSize, boolean optResourceEnabled, HostsFileEntriesResolver hostsFileEntriesResolver, DnsServerAddressStreamProvider dnsServerAddressStreamProvider, String[] searchDomains, int ndots, boolean decodeIdn) {
    this(eventLoop, channelFactory, resolveCache, new AuthoritativeDnsServerCacheAdapter(authoritativeDnsServerCache), dnsQueryLifecycleObserverFactory, queryTimeoutMillis, resolvedAddressTypes, recursionDesired, maxQueriesPerResolve, traceEnabled, maxPayloadSize, optResourceEnabled, hostsFileEntriesResolver, dnsServerAddressStreamProvider, searchDomains, ndots, decodeIdn);
  }
  
  @Deprecated
  public DnsNameResolver(EventLoop eventLoop, ChannelFactory<? extends DatagramChannel> channelFactory, DnsCache resolveCache, AuthoritativeDnsServerCache authoritativeDnsServerCache, DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory, long queryTimeoutMillis, ResolvedAddressTypes resolvedAddressTypes, boolean recursionDesired, int maxQueriesPerResolve, boolean traceEnabled, int maxPayloadSize, boolean optResourceEnabled, HostsFileEntriesResolver hostsFileEntriesResolver, DnsServerAddressStreamProvider dnsServerAddressStreamProvider, String[] searchDomains, int ndots, boolean decodeIdn) {
    this(eventLoop, channelFactory, null, resolveCache, NoopDnsCnameCache.INSTANCE, authoritativeDnsServerCache, dnsQueryLifecycleObserverFactory, queryTimeoutMillis, resolvedAddressTypes, recursionDesired, maxQueriesPerResolve, traceEnabled, maxPayloadSize, optResourceEnabled, hostsFileEntriesResolver, dnsServerAddressStreamProvider, searchDomains, ndots, decodeIdn, false);
  }
  
  DnsNameResolver(EventLoop eventLoop, ChannelFactory<? extends DatagramChannel> channelFactory, ChannelFactory<? extends SocketChannel> socketChannelFactory, DnsCache resolveCache, DnsCnameCache cnameCache, AuthoritativeDnsServerCache authoritativeDnsServerCache, DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory, long queryTimeoutMillis, ResolvedAddressTypes resolvedAddressTypes, boolean recursionDesired, int maxQueriesPerResolve, boolean traceEnabled, int maxPayloadSize, boolean optResourceEnabled, HostsFileEntriesResolver hostsFileEntriesResolver, DnsServerAddressStreamProvider dnsServerAddressStreamProvider, String[] searchDomains, int ndots, boolean decodeIdn, boolean completeOncePreferredResolved) {
    this(eventLoop, channelFactory, socketChannelFactory, resolveCache, cnameCache, authoritativeDnsServerCache, null, dnsQueryLifecycleObserverFactory, queryTimeoutMillis, resolvedAddressTypes, recursionDesired, maxQueriesPerResolve, traceEnabled, maxPayloadSize, optResourceEnabled, hostsFileEntriesResolver, dnsServerAddressStreamProvider, searchDomains, ndots, decodeIdn, completeOncePreferredResolved, 0);
  }
  
  DnsNameResolver(EventLoop eventLoop, ChannelFactory<? extends DatagramChannel> channelFactory, ChannelFactory<? extends SocketChannel> socketChannelFactory, final DnsCache resolveCache, final DnsCnameCache cnameCache, final AuthoritativeDnsServerCache authoritativeDnsServerCache, SocketAddress localAddress, DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory, long queryTimeoutMillis, ResolvedAddressTypes resolvedAddressTypes, boolean recursionDesired, int maxQueriesPerResolve, boolean traceEnabled, int maxPayloadSize, boolean optResourceEnabled, HostsFileEntriesResolver hostsFileEntriesResolver, DnsServerAddressStreamProvider dnsServerAddressStreamProvider, String[] searchDomains, int ndots, boolean decodeIdn, boolean completeOncePreferredResolved, int maxNumConsolidation) {
    super((EventExecutor)eventLoop);
    ChannelFuture future;
    this.queryContextManager = new DnsQueryContextManager();
    this.nameServerAddrStream = new FastThreadLocal<DnsServerAddressStream>() {
        protected DnsServerAddressStream initialValue() {
          return DnsNameResolver.this.dnsServerAddressStreamProvider.nameServerAddressStream("");
        }
      };
    this
      
      .queryTimeoutMillis = (queryTimeoutMillis > 0L) ? queryTimeoutMillis : TimeUnit.SECONDS.toMillis(DEFAULT_OPTIONS.timeout());
    this.resolvedAddressTypes = (resolvedAddressTypes != null) ? resolvedAddressTypes : DEFAULT_RESOLVE_ADDRESS_TYPES;
    this.recursionDesired = recursionDesired;
    this.maxQueriesPerResolve = (maxQueriesPerResolve > 0) ? maxQueriesPerResolve : DEFAULT_OPTIONS.attempts();
    this.maxPayloadSize = ObjectUtil.checkPositive(maxPayloadSize, "maxPayloadSize");
    this.optResourceEnabled = optResourceEnabled;
    this.hostsFileEntriesResolver = (HostsFileEntriesResolver)ObjectUtil.checkNotNull(hostsFileEntriesResolver, "hostsFileEntriesResolver");
    this
      .dnsServerAddressStreamProvider = (DnsServerAddressStreamProvider)ObjectUtil.checkNotNull(dnsServerAddressStreamProvider, "dnsServerAddressStreamProvider");
    this.resolveCache = (DnsCache)ObjectUtil.checkNotNull(resolveCache, "resolveCache");
    this.cnameCache = (DnsCnameCache)ObjectUtil.checkNotNull(cnameCache, "cnameCache");
    this
      
      .dnsQueryLifecycleObserverFactory = traceEnabled ? ((dnsQueryLifecycleObserverFactory instanceof NoopDnsQueryLifecycleObserverFactory) ? new LoggingDnsQueryLifeCycleObserverFactory() : new BiDnsQueryLifecycleObserverFactory(new LoggingDnsQueryLifeCycleObserverFactory(), dnsQueryLifecycleObserverFactory)) : (DnsQueryLifecycleObserverFactory)ObjectUtil.checkNotNull(dnsQueryLifecycleObserverFactory, "dnsQueryLifecycleObserverFactory");
    this.searchDomains = (searchDomains != null) ? (String[])searchDomains.clone() : DEFAULT_SEARCH_DOMAINS;
    this.ndots = (ndots >= 0) ? ndots : DEFAULT_OPTIONS.ndots();
    this.decodeIdn = decodeIdn;
    this.completeOncePreferredResolved = completeOncePreferredResolved;
    if (socketChannelFactory == null) {
      this.socketBootstrap = null;
    } else {
      this.socketBootstrap = new Bootstrap();
      ((Bootstrap)((Bootstrap)((Bootstrap)this.socketBootstrap.option(ChannelOption.SO_REUSEADDR, Boolean.valueOf(true)))
        .group((EventLoopGroup)executor()))
        .channelFactory(socketChannelFactory))
        .handler((ChannelHandler)TCP_ENCODER);
    } 
    switch (this.resolvedAddressTypes) {
      case IPV4_ONLY:
        this.supportsAAAARecords = false;
        this.supportsARecords = true;
        this.resolveRecordTypes = IPV4_ONLY_RESOLVED_RECORD_TYPES;
        this.resolvedInternetProtocolFamilies = IPV4_ONLY_RESOLVED_PROTOCOL_FAMILIES;
        break;
      case IPV4_PREFERRED:
        this.supportsAAAARecords = true;
        this.supportsARecords = true;
        this.resolveRecordTypes = IPV4_PREFERRED_RESOLVED_RECORD_TYPES;
        this.resolvedInternetProtocolFamilies = IPV4_PREFERRED_RESOLVED_PROTOCOL_FAMILIES;
        break;
      case IPV6_ONLY:
        this.supportsAAAARecords = true;
        this.supportsARecords = false;
        this.resolveRecordTypes = IPV6_ONLY_RESOLVED_RECORD_TYPES;
        this.resolvedInternetProtocolFamilies = IPV6_ONLY_RESOLVED_PROTOCOL_FAMILIES;
        break;
      case IPV6_PREFERRED:
        this.supportsAAAARecords = true;
        this.supportsARecords = true;
        this.resolveRecordTypes = IPV6_PREFERRED_RESOLVED_RECORD_TYPES;
        this.resolvedInternetProtocolFamilies = IPV6_PREFERRED_RESOLVED_PROTOCOL_FAMILIES;
        break;
      default:
        throw new IllegalArgumentException("Unknown ResolvedAddressTypes " + resolvedAddressTypes);
    } 
    this.preferredAddressType = preferredAddressType(this.resolvedAddressTypes);
    this.authoritativeDnsServerCache = (AuthoritativeDnsServerCache)ObjectUtil.checkNotNull(authoritativeDnsServerCache, "authoritativeDnsServerCache");
    this.nameServerComparator = new NameServerComparator(this.preferredAddressType.addressType());
    this.maxNumConsolidation = maxNumConsolidation;
    if (maxNumConsolidation > 0) {
      this.inflightLookups = new HashMap<String, Future<List<InetAddress>>>();
    } else {
      this.inflightLookups = null;
    } 
    Bootstrap b = new Bootstrap();
    b.group((EventLoopGroup)executor());
    b.channelFactory(channelFactory);
    this.channelReadyPromise = executor().newPromise();
    final DnsResponseHandler responseHandler = new DnsResponseHandler(this.channelReadyPromise);
    b.handler((ChannelHandler)new ChannelInitializer<DatagramChannel>() {
          protected void initChannel(DatagramChannel ch) {
            ch.pipeline().addLast(new ChannelHandler[] { (ChannelHandler)DnsNameResolver.access$200(), (ChannelHandler)DnsNameResolver.access$300(), (ChannelHandler)this.val$responseHandler });
          }
        });
    if (localAddress == null) {
      b.option(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION, Boolean.valueOf(true));
      future = b.register();
    } else {
      future = b.bind(localAddress);
    } 
    if (future.isDone()) {
      Throwable cause = future.cause();
      if (cause != null) {
        if (cause instanceof RuntimeException)
          throw (RuntimeException)cause; 
        if (cause instanceof Error)
          throw (Error)cause; 
        throw new IllegalStateException("Unable to create / register Channel", cause);
      } 
    } else {
      future.addListener((GenericFutureListener)new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
              Throwable cause = future.cause();
              if (cause != null)
                DnsNameResolver.this.channelReadyPromise.tryFailure(cause); 
            }
          });
    } 
    this.ch = future.channel();
    this.ch.config().setRecvByteBufAllocator((RecvByteBufAllocator)new FixedRecvByteBufAllocator(maxPayloadSize));
    this.ch.closeFuture().addListener((GenericFutureListener)new ChannelFutureListener() {
          public void operationComplete(ChannelFuture future) {
            resolveCache.clear();
            cnameCache.clear();
            authoritativeDnsServerCache.clear();
          }
        });
  }
  
  static InternetProtocolFamily preferredAddressType(ResolvedAddressTypes resolvedAddressTypes) {
    switch (resolvedAddressTypes) {
      case IPV4_ONLY:
      case IPV4_PREFERRED:
        return InternetProtocolFamily.IPv4;
      case IPV6_ONLY:
      case IPV6_PREFERRED:
        return InternetProtocolFamily.IPv6;
    } 
    throw new IllegalArgumentException("Unknown ResolvedAddressTypes " + resolvedAddressTypes);
  }
  
  InetSocketAddress newRedirectServerAddress(InetAddress server) {
    return new InetSocketAddress(server, 53);
  }
  
  final DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory() {
    return this.dnsQueryLifecycleObserverFactory;
  }
  
  protected DnsServerAddressStream newRedirectDnsServerStream(String hostname, List<InetSocketAddress> nameservers) {
    DnsServerAddressStream cached = authoritativeDnsServerCache().get(hostname);
    if (cached == null || cached.size() == 0) {
      Collections.sort(nameservers, this.nameServerComparator);
      return new SequentialDnsServerAddressStream(nameservers, 0);
    } 
    return cached;
  }
  
  public DnsCache resolveCache() {
    return this.resolveCache;
  }
  
  public DnsCnameCache cnameCache() {
    return this.cnameCache;
  }
  
  public AuthoritativeDnsServerCache authoritativeDnsServerCache() {
    return this.authoritativeDnsServerCache;
  }
  
  public long queryTimeoutMillis() {
    return this.queryTimeoutMillis;
  }
  
  public ResolvedAddressTypes resolvedAddressTypes() {
    return this.resolvedAddressTypes;
  }
  
  InternetProtocolFamily[] resolvedInternetProtocolFamiliesUnsafe() {
    return this.resolvedInternetProtocolFamilies;
  }
  
  final String[] searchDomains() {
    return this.searchDomains;
  }
  
  final int ndots() {
    return this.ndots;
  }
  
  final boolean supportsAAAARecords() {
    return this.supportsAAAARecords;
  }
  
  final boolean supportsARecords() {
    return this.supportsARecords;
  }
  
  final InternetProtocolFamily preferredAddressType() {
    return this.preferredAddressType;
  }
  
  final DnsRecordType[] resolveRecordTypes() {
    return this.resolveRecordTypes;
  }
  
  final boolean isDecodeIdn() {
    return this.decodeIdn;
  }
  
  public boolean isRecursionDesired() {
    return this.recursionDesired;
  }
  
  public int maxQueriesPerResolve() {
    return this.maxQueriesPerResolve;
  }
  
  public int maxPayloadSize() {
    return this.maxPayloadSize;
  }
  
  public boolean isOptResourceEnabled() {
    return this.optResourceEnabled;
  }
  
  public HostsFileEntriesResolver hostsFileEntriesResolver() {
    return this.hostsFileEntriesResolver;
  }
  
  public void close() {
    if (this.ch.isOpen())
      this.ch.close(); 
  }
  
  protected EventLoop executor() {
    return (EventLoop)super.executor();
  }
  
  private InetAddress resolveHostsFileEntry(String hostname) {
    if (this.hostsFileEntriesResolver == null)
      return null; 
    InetAddress address = this.hostsFileEntriesResolver.address(hostname, this.resolvedAddressTypes);
    return (address == null && isLocalWindowsHost(hostname)) ? LOCALHOST_ADDRESS : address;
  }
  
  private List<InetAddress> resolveHostsFileEntries(String hostname) {
    List<InetAddress> addresses;
    if (this.hostsFileEntriesResolver == null)
      return null; 
    if (this.hostsFileEntriesResolver instanceof DefaultHostsFileEntriesResolver) {
      addresses = ((DefaultHostsFileEntriesResolver)this.hostsFileEntriesResolver).addresses(hostname, this.resolvedAddressTypes);
    } else {
      InetAddress address = this.hostsFileEntriesResolver.address(hostname, this.resolvedAddressTypes);
      addresses = (address != null) ? Collections.<InetAddress>singletonList(address) : null;
    } 
    return (addresses == null && isLocalWindowsHost(hostname)) ? 
      Collections.<InetAddress>singletonList(LOCALHOST_ADDRESS) : addresses;
  }
  
  private static boolean isLocalWindowsHost(String hostname) {
    return (PlatformDependent.isWindows() && ("localhost"
      .equalsIgnoreCase(hostname) || (WINDOWS_HOST_NAME != null && WINDOWS_HOST_NAME
      .equalsIgnoreCase(hostname))));
  }
  
  public final Future<InetAddress> resolve(String inetHost, Iterable<DnsRecord> additionals) {
    return resolve(inetHost, additionals, executor().newPromise());
  }
  
  public final Future<InetAddress> resolve(String inetHost, Iterable<DnsRecord> additionals, Promise<InetAddress> promise) {
    ObjectUtil.checkNotNull(promise, "promise");
    DnsRecord[] additionalsArray = toArray(additionals, true);
    try {
      doResolve(inetHost, additionalsArray, promise, this.resolveCache);
      return (Future<InetAddress>)promise;
    } catch (Exception e) {
      return (Future<InetAddress>)promise.setFailure(e);
    } 
  }
  
  public final Future<List<InetAddress>> resolveAll(String inetHost, Iterable<DnsRecord> additionals) {
    return resolveAll(inetHost, additionals, executor().newPromise());
  }
  
  public final Future<List<InetAddress>> resolveAll(String inetHost, Iterable<DnsRecord> additionals, Promise<List<InetAddress>> promise) {
    ObjectUtil.checkNotNull(promise, "promise");
    DnsRecord[] additionalsArray = toArray(additionals, true);
    try {
      doResolveAll(inetHost, additionalsArray, promise, this.resolveCache);
      return (Future<List<InetAddress>>)promise;
    } catch (Exception e) {
      return (Future<List<InetAddress>>)promise.setFailure(e);
    } 
  }
  
  protected void doResolve(String inetHost, Promise<InetAddress> promise) throws Exception {
    doResolve(inetHost, EMPTY_ADDITIONALS, promise, this.resolveCache);
  }
  
  public final Future<List<DnsRecord>> resolveAll(DnsQuestion question) {
    return resolveAll(question, EMPTY_ADDITIONALS, executor().newPromise());
  }
  
  public final Future<List<DnsRecord>> resolveAll(DnsQuestion question, Iterable<DnsRecord> additionals) {
    return resolveAll(question, additionals, executor().newPromise());
  }
  
  public final Future<List<DnsRecord>> resolveAll(DnsQuestion question, Iterable<DnsRecord> additionals, Promise<List<DnsRecord>> promise) {
    DnsRecord[] additionalsArray = toArray(additionals, true);
    return resolveAll(question, additionalsArray, promise);
  }
  
  private Future<List<DnsRecord>> resolveAll(DnsQuestion question, DnsRecord[] additionals, Promise<List<DnsRecord>> promise) {
    ObjectUtil.checkNotNull(question, "question");
    ObjectUtil.checkNotNull(promise, "promise");
    DnsRecordType type = question.type();
    String hostname = question.name();
    if (type == DnsRecordType.A || type == DnsRecordType.AAAA) {
      List<InetAddress> hostsFileEntries = resolveHostsFileEntries(hostname);
      if (hostsFileEntries != null) {
        List<DnsRecord> result = new ArrayList<DnsRecord>();
        for (InetAddress hostsFileEntry : hostsFileEntries) {
          ByteBuf content = null;
          if (hostsFileEntry instanceof java.net.Inet4Address) {
            if (type == DnsRecordType.A)
              content = Unpooled.wrappedBuffer(hostsFileEntry.getAddress()); 
          } else if (hostsFileEntry instanceof java.net.Inet6Address && 
            type == DnsRecordType.AAAA) {
            content = Unpooled.wrappedBuffer(hostsFileEntry.getAddress());
          } 
          if (content != null)
            result.add(new DefaultDnsRawRecord(hostname, type, 86400L, content)); 
        } 
        if (!result.isEmpty()) {
          if (!trySuccess(promise, result))
            for (DnsRecord r : result)
              ReferenceCountUtil.safeRelease(r);  
          return (Future<List<DnsRecord>>)promise;
        } 
      } 
    } 
    DnsServerAddressStream nameServerAddrs = this.dnsServerAddressStreamProvider.nameServerAddressStream(hostname);
    (new DnsRecordResolveContext(this, this.ch, promise, question, additionals, nameServerAddrs, this.maxQueriesPerResolve))
      .resolve(promise);
    return (Future<List<DnsRecord>>)promise;
  }
  
  private static DnsRecord[] toArray(Iterable<DnsRecord> additionals, boolean validateType) {
    ObjectUtil.checkNotNull(additionals, "additionals");
    if (additionals instanceof Collection) {
      Collection<DnsRecord> collection = (Collection<DnsRecord>)additionals;
      for (DnsRecord r : additionals)
        validateAdditional(r, validateType); 
      return collection.<DnsRecord>toArray(new DnsRecord[collection.size()]);
    } 
    Iterator<DnsRecord> additionalsIt = additionals.iterator();
    if (!additionalsIt.hasNext())
      return EMPTY_ADDITIONALS; 
    List<DnsRecord> records = new ArrayList<DnsRecord>();
    do {
      DnsRecord r = additionalsIt.next();
      validateAdditional(r, validateType);
      records.add(r);
    } while (additionalsIt.hasNext());
    return records.<DnsRecord>toArray(new DnsRecord[records.size()]);
  }
  
  private static void validateAdditional(DnsRecord record, boolean validateType) {
    ObjectUtil.checkNotNull(record, "record");
    if (validateType && record instanceof io.netty.handler.codec.dns.DnsRawRecord)
      throw new IllegalArgumentException("DnsRawRecord implementations not allowed: " + record); 
  }
  
  private InetAddress loopbackAddress() {
    return preferredAddressType().localhost();
  }
  
  protected void doResolve(String inetHost, DnsRecord[] additionals, Promise<InetAddress> promise, DnsCache resolveCache) throws Exception {
    if (inetHost == null || inetHost.isEmpty()) {
      promise.setSuccess(loopbackAddress());
      return;
    } 
    InetAddress address = NetUtil.createInetAddressFromIpAddressString(inetHost);
    if (address != null) {
      promise.setSuccess(address);
      return;
    } 
    String hostname = hostname(inetHost);
    InetAddress hostsFileEntry = resolveHostsFileEntry(hostname);
    if (hostsFileEntry != null) {
      promise.setSuccess(hostsFileEntry);
      return;
    } 
    if (!doResolveCached(hostname, additionals, promise, resolveCache))
      doResolveUncached(hostname, additionals, promise, resolveCache, this.completeOncePreferredResolved); 
  }
  
  private boolean doResolveCached(String hostname, DnsRecord[] additionals, Promise<InetAddress> promise, DnsCache resolveCache) {
    List<? extends DnsCacheEntry> cachedEntries = resolveCache.get(hostname, additionals);
    if (cachedEntries == null || cachedEntries.isEmpty())
      return false; 
    Throwable cause = ((DnsCacheEntry)cachedEntries.get(0)).cause();
    if (cause == null) {
      int numEntries = cachedEntries.size();
      for (InternetProtocolFamily f : this.resolvedInternetProtocolFamilies) {
        for (int i = 0; i < numEntries; i++) {
          DnsCacheEntry e = cachedEntries.get(i);
          if (f.addressType().isInstance(e.address())) {
            trySuccess(promise, e.address());
            return true;
          } 
        } 
      } 
      return false;
    } 
    tryFailure(promise, cause);
    return true;
  }
  
  static <T> boolean trySuccess(Promise<T> promise, T result) {
    boolean notifiedRecords = promise.trySuccess(result);
    if (!notifiedRecords)
      logger.trace("Failed to notify success ({}) to a promise: {}", result, promise); 
    return notifiedRecords;
  }
  
  private static void tryFailure(Promise<?> promise, Throwable cause) {
    if (!promise.tryFailure(cause))
      logger.trace("Failed to notify failure to a promise: {}", promise, cause); 
  }
  
  private void doResolveUncached(String hostname, DnsRecord[] additionals, final Promise<InetAddress> promise, DnsCache resolveCache, boolean completeEarlyIfPossible) {
    Promise<List<InetAddress>> allPromise = executor().newPromise();
    doResolveAllUncached(hostname, additionals, promise, allPromise, resolveCache, completeEarlyIfPossible);
    allPromise.addListener((GenericFutureListener)new FutureListener<List<InetAddress>>() {
          public void operationComplete(Future<List<InetAddress>> future) {
            if (future.isSuccess()) {
              DnsNameResolver.trySuccess(promise, ((List)future.getNow()).get(0));
            } else {
              DnsNameResolver.tryFailure(promise, future.cause());
            } 
          }
        });
  }
  
  protected void doResolveAll(String inetHost, Promise<List<InetAddress>> promise) throws Exception {
    doResolveAll(inetHost, EMPTY_ADDITIONALS, promise, this.resolveCache);
  }
  
  protected void doResolveAll(String inetHost, DnsRecord[] additionals, Promise<List<InetAddress>> promise, DnsCache resolveCache) throws Exception {
    if (inetHost == null || inetHost.isEmpty()) {
      promise.setSuccess(Collections.singletonList(loopbackAddress()));
      return;
    } 
    InetAddress address = NetUtil.createInetAddressFromIpAddressString(inetHost);
    if (address != null) {
      promise.setSuccess(Collections.singletonList(address));
      return;
    } 
    String hostname = hostname(inetHost);
    List<InetAddress> hostsFileEntries = resolveHostsFileEntries(hostname);
    if (hostsFileEntries != null) {
      promise.setSuccess(hostsFileEntries);
      return;
    } 
    if (!doResolveAllCached(hostname, additionals, promise, resolveCache, this.resolvedInternetProtocolFamilies))
      doResolveAllUncached(hostname, additionals, promise, promise, resolveCache, this.completeOncePreferredResolved); 
  }
  
  static boolean doResolveAllCached(String hostname, DnsRecord[] additionals, Promise<List<InetAddress>> promise, DnsCache resolveCache, InternetProtocolFamily[] resolvedInternetProtocolFamilies) {
    List<? extends DnsCacheEntry> cachedEntries = resolveCache.get(hostname, additionals);
    if (cachedEntries == null || cachedEntries.isEmpty())
      return false; 
    Throwable cause = ((DnsCacheEntry)cachedEntries.get(0)).cause();
    if (cause == null) {
      List<InetAddress> result = null;
      int numEntries = cachedEntries.size();
      for (InternetProtocolFamily f : resolvedInternetProtocolFamilies) {
        for (int i = 0; i < numEntries; i++) {
          DnsCacheEntry e = cachedEntries.get(i);
          if (f.addressType().isInstance(e.address())) {
            if (result == null)
              result = new ArrayList<InetAddress>(numEntries); 
            result.add(e.address());
          } 
        } 
      } 
      if (result != null) {
        trySuccess(promise, result);
        return true;
      } 
      return false;
    } 
    tryFailure(promise, cause);
    return true;
  }
  
  private void doResolveAllUncached(final String hostname, final DnsRecord[] additionals, final Promise<?> originalPromise, final Promise<List<InetAddress>> promise, final DnsCache resolveCache, final boolean completeEarlyIfPossible) {
    EventLoop eventLoop = executor();
    if (eventLoop.inEventLoop()) {
      doResolveAllUncached0(hostname, additionals, originalPromise, promise, resolveCache, completeEarlyIfPossible);
    } else {
      eventLoop.execute(new Runnable() {
            public void run() {
              DnsNameResolver.this.doResolveAllUncached0(hostname, additionals, originalPromise, promise, resolveCache, completeEarlyIfPossible);
            }
          });
    } 
  }
  
  private void doResolveAllUncached0(final String hostname, final DnsRecord[] additionals, final Promise<?> originalPromise, final Promise<List<InetAddress>> promise, final DnsCache resolveCache, final boolean completeEarlyIfPossible) {
    assert executor().inEventLoop();
    if (this.inflightLookups != null && (additionals == null || additionals.length == 0)) {
      Future<List<InetAddress>> inflightFuture = this.inflightLookups.get(hostname);
      if (inflightFuture != null) {
        inflightFuture.addListener(new GenericFutureListener<Future<? super List<InetAddress>>>() {
              public void operationComplete(Future<? super List<InetAddress>> future) {
                if (future.isSuccess()) {
                  promise.setSuccess(future.getNow());
                } else {
                  Throwable cause = future.cause();
                  if (DnsNameResolver.isTimeoutError(cause)) {
                    DnsNameResolver.this.resolveNow(hostname, additionals, originalPromise, promise, resolveCache, completeEarlyIfPossible);
                  } else {
                    promise.setFailure(cause);
                  } 
                } 
              }
            });
        return;
      } 
      if (this.inflightLookups.size() < this.maxNumConsolidation) {
        this.inflightLookups.put(hostname, promise);
        promise.addListener(new GenericFutureListener<Future<? super List<InetAddress>>>() {
              public void operationComplete(Future<? super List<InetAddress>> future) {
                DnsNameResolver.this.inflightLookups.remove(hostname);
              }
            });
      } 
    } 
    resolveNow(hostname, additionals, originalPromise, promise, resolveCache, completeEarlyIfPossible);
  }
  
  private void resolveNow(String hostname, DnsRecord[] additionals, Promise<?> originalPromise, Promise<List<InetAddress>> promise, DnsCache resolveCache, boolean completeEarlyIfPossible) {
    DnsServerAddressStream nameServerAddrs = this.dnsServerAddressStreamProvider.nameServerAddressStream(hostname);
    DnsAddressResolveContext ctx = new DnsAddressResolveContext(this, this.ch, originalPromise, hostname, additionals, nameServerAddrs, this.maxQueriesPerResolve, resolveCache, this.authoritativeDnsServerCache, completeEarlyIfPossible);
    ctx.resolve(promise);
  }
  
  private static String hostname(String inetHost) {
    String hostname = IDN.toASCII(inetHost);
    if (StringUtil.endsWith(inetHost, '.') && !StringUtil.endsWith(hostname, '.'))
      hostname = hostname + "."; 
    return hostname;
  }
  
  public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(DnsQuestion question) {
    return query(nextNameServerAddress(), question);
  }
  
  public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(DnsQuestion question, Iterable<DnsRecord> additionals) {
    return query(nextNameServerAddress(), question, additionals);
  }
  
  public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(DnsQuestion question, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> promise) {
    return query(nextNameServerAddress(), question, Collections.emptyList(), promise);
  }
  
  private InetSocketAddress nextNameServerAddress() {
    return ((DnsServerAddressStream)this.nameServerAddrStream.get()).next();
  }
  
  public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(InetSocketAddress nameServerAddr, DnsQuestion question) {
    return query0(nameServerAddr, question, NoopDnsQueryLifecycleObserver.INSTANCE, EMPTY_ADDITIONALS, true, this.ch
        .eventLoop().newPromise());
  }
  
  public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(InetSocketAddress nameServerAddr, DnsQuestion question, Iterable<DnsRecord> additionals) {
    return query0(nameServerAddr, question, NoopDnsQueryLifecycleObserver.INSTANCE, 
        toArray(additionals, false), true, this.ch
        .eventLoop().newPromise());
  }
  
  public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(InetSocketAddress nameServerAddr, DnsQuestion question, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> promise) {
    return query0(nameServerAddr, question, NoopDnsQueryLifecycleObserver.INSTANCE, EMPTY_ADDITIONALS, true, promise);
  }
  
  public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(InetSocketAddress nameServerAddr, DnsQuestion question, Iterable<DnsRecord> additionals, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> promise) {
    return query0(nameServerAddr, question, NoopDnsQueryLifecycleObserver.INSTANCE, 
        toArray(additionals, false), true, promise);
  }
  
  public static boolean isTransportOrTimeoutError(Throwable cause) {
    return (cause != null && cause.getCause() instanceof DnsNameResolverException);
  }
  
  public static boolean isTimeoutError(Throwable cause) {
    return (cause != null && cause.getCause() instanceof DnsNameResolverTimeoutException);
  }
  
  final void flushQueries() {
    this.ch.flush();
  }
  
  final Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query0(InetSocketAddress nameServerAddr, DnsQuestion question, DnsQueryLifecycleObserver queryLifecycleObserver, DnsRecord[] additionals, boolean flush, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> promise) {
    Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> castPromise = cast(
        (Promise)ObjectUtil.checkNotNull(promise, "promise"));
    int payloadSize = isOptResourceEnabled() ? maxPayloadSize() : 0;
    try {
      DnsQueryContext queryContext = new DatagramDnsQueryContext(this.ch, (Future<? extends Channel>)this.channelReadyPromise, nameServerAddr, this.queryContextManager, payloadSize, isRecursionDesired(), question, additionals, castPromise);
      ChannelFuture future = queryContext.writeQuery(queryTimeoutMillis(), flush);
      queryLifecycleObserver.queryWritten(nameServerAddr, future);
      return (Future<AddressedEnvelope<DnsResponse, InetSocketAddress>>)castPromise;
    } catch (Exception e) {
      return (Future<AddressedEnvelope<DnsResponse, InetSocketAddress>>)castPromise.setFailure(e);
    } 
  }
  
  private static Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> cast(Promise<?> promise) {
    return (Promise)promise;
  }
  
  final DnsServerAddressStream newNameServerAddressStream(String hostname) {
    return this.dnsServerAddressStreamProvider.nameServerAddressStream(hostname);
  }
  
  private final class DnsResponseHandler extends ChannelInboundHandlerAdapter {
    private final Promise<Channel> channelActivePromise;
    
    DnsResponseHandler(Promise<Channel> channelActivePromise) {
      this.channelActivePromise = channelActivePromise;
    }
    
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
      Channel qCh = ctx.channel();
      final DatagramDnsResponse res = (DatagramDnsResponse)msg;
      final int queryId = res.id();
      DnsNameResolver.logger.debug("{} RECEIVED: UDP [{}: {}], {}", new Object[] { qCh, Integer.valueOf(queryId), res.sender(), res });
      final DnsQueryContext qCtx = DnsNameResolver.this.queryContextManager.get(res.sender(), queryId);
      if (qCtx == null) {
        DnsNameResolver.logger.debug("{} Received a DNS response with an unknown ID: UDP [{}: {}]", new Object[] { qCh, 
              Integer.valueOf(queryId), res.sender() });
        res.release();
        return;
      } 
      if (qCtx.isDone()) {
        DnsNameResolver.logger.debug("{} Received a DNS response for a query that was timed out or cancelled: UDP [{}: {}]", new Object[] { qCh, 
              Integer.valueOf(queryId), res.sender() });
        res.release();
        return;
      } 
      if (!res.isTruncated() || DnsNameResolver.this.socketBootstrap == null) {
        qCtx.finishSuccess((AddressedEnvelope<? extends DnsResponse, InetSocketAddress>)res);
        return;
      } 
      DnsNameResolver.this.socketBootstrap.connect(res.sender()).addListener((GenericFutureListener)new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
              if (!future.isSuccess()) {
                DnsNameResolver.logger.debug("{} Unable to fallback to TCP [{}: {}]", new Object[] { DnsNameResolver.access$1100(this.this$1.this$0), Integer.valueOf(this.val$queryId), this.val$res.sender(), future.cause() });
                qCtx.finishSuccess((AddressedEnvelope<? extends DnsResponse, InetSocketAddress>)res);
                return;
              } 
              final Channel tcpCh = future.channel();
              Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> promise = tcpCh.eventLoop().newPromise();
              int payloadSize = DnsNameResolver.this.isOptResourceEnabled() ? DnsNameResolver.this.maxPayloadSize() : 0;
              final TcpDnsQueryContext tcpCtx = new TcpDnsQueryContext(tcpCh, (Future<? extends Channel>)DnsNameResolver.this.channelReadyPromise, (InetSocketAddress)tcpCh.remoteAddress(), DnsNameResolver.this.queryContextManager, payloadSize, DnsNameResolver.this.isRecursionDesired(), qCtx.question(), DnsNameResolver.EMPTY_ADDITIONALS, promise);
              tcpCh.pipeline().addLast(new ChannelHandler[] { (ChannelHandler)new TcpDnsResponseDecoder() });
              tcpCh.pipeline().addLast(new ChannelHandler[] { (ChannelHandler)new ChannelInboundHandlerAdapter() {
                      public void channelRead(ChannelHandlerContext ctx, Object msg) {
                        Channel tcpCh = ctx.channel();
                        DnsResponse response = (DnsResponse)msg;
                        int queryId = response.id();
                        if (DnsNameResolver.logger.isDebugEnabled())
                          DnsNameResolver.logger.debug("{} RECEIVED: TCP [{}: {}], {}", new Object[] { tcpCh, Integer.valueOf(queryId), tcpCh
                                .remoteAddress(), response }); 
                        DnsQueryContext foundCtx = DnsNameResolver.this.queryContextManager.get(res.sender(), queryId);
                        if (foundCtx != null && foundCtx.isDone()) {
                          DnsNameResolver.logger.debug("{} Received a DNS response for a query that was timed out or cancelled : TCP [{}: {}]", new Object[] { tcpCh, 
                                Integer.valueOf(queryId), this.this$2.val$res.sender() });
                          response.release();
                        } else if (foundCtx == tcpCtx) {
                          tcpCtx.finishSuccess(new DnsNameResolver.AddressedEnvelopeAdapter((InetSocketAddress)ctx
                                .channel().remoteAddress(), (InetSocketAddress)ctx
                                .channel().localAddress(), response));
                        } else {
                          response.release();
                          tcpCtx.finishFailure("Received TCP DNS response with unexpected ID", null, false);
                          if (DnsNameResolver.logger.isDebugEnabled())
                            DnsNameResolver.logger.debug("{} Received a DNS response with an unexpected ID: TCP [{}: {}]", new Object[] { tcpCh, 
                                  Integer.valueOf(queryId), tcpCh.remoteAddress() }); 
                        } 
                      }
                      
                      public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                        if (tcpCtx.finishFailure("TCP fallback error", cause, false) && DnsNameResolver
                          .logger.isDebugEnabled())
                          DnsNameResolver.logger.debug("{} Error during processing response: TCP [{}: {}]", new Object[] { ctx
                                .channel(), Integer.valueOf(this.this$2.val$queryId), ctx
                                .channel().remoteAddress(), cause }); 
                      }
                    } });
              promise.addListener((GenericFutureListener)new FutureListener<AddressedEnvelope<DnsResponse, InetSocketAddress>>() {
                    public void operationComplete(Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> future) {
                      if (future.isSuccess()) {
                        qCtx.finishSuccess((AddressedEnvelope<? extends DnsResponse, InetSocketAddress>)future.getNow());
                        res.release();
                      } else {
                        qCtx.finishSuccess((AddressedEnvelope<? extends DnsResponse, InetSocketAddress>)res);
                      } 
                      tcpCh.close();
                    }
                  });
              tcpCtx.writeQuery(DnsNameResolver.this.queryTimeoutMillis(), true);
            }
          });
    }
    
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
      super.channelActive(ctx);
      this.channelActivePromise.trySuccess(ctx.channel());
    }
    
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      if (cause instanceof io.netty.handler.codec.CorruptedFrameException) {
        DnsNameResolver.logger.debug("{} Unable to decode DNS response: UDP", ctx.channel(), cause);
      } else {
        DnsNameResolver.logger.warn("{} Unexpected exception: UDP", ctx.channel(), cause);
      } 
    }
  }
  
  private static final class AddressedEnvelopeAdapter implements AddressedEnvelope<DnsResponse, InetSocketAddress> {
    private final InetSocketAddress sender;
    
    private final InetSocketAddress recipient;
    
    private final DnsResponse response;
    
    AddressedEnvelopeAdapter(InetSocketAddress sender, InetSocketAddress recipient, DnsResponse response) {
      this.sender = sender;
      this.recipient = recipient;
      this.response = response;
    }
    
    public DnsResponse content() {
      return this.response;
    }
    
    public InetSocketAddress sender() {
      return this.sender;
    }
    
    public InetSocketAddress recipient() {
      return this.recipient;
    }
    
    public AddressedEnvelope<DnsResponse, InetSocketAddress> retain() {
      this.response.retain();
      return this;
    }
    
    public AddressedEnvelope<DnsResponse, InetSocketAddress> retain(int increment) {
      this.response.retain(increment);
      return this;
    }
    
    public AddressedEnvelope<DnsResponse, InetSocketAddress> touch() {
      this.response.touch();
      return this;
    }
    
    public AddressedEnvelope<DnsResponse, InetSocketAddress> touch(Object hint) {
      this.response.touch(hint);
      return this;
    }
    
    public int refCnt() {
      return this.response.refCnt();
    }
    
    public boolean release() {
      return this.response.release();
    }
    
    public boolean release(int decrement) {
      return this.response.release(decrement);
    }
    
    public boolean equals(Object obj) {
      if (this == obj)
        return true; 
      if (!(obj instanceof AddressedEnvelope))
        return false; 
      AddressedEnvelope<?, SocketAddress> that = (AddressedEnvelope<?, SocketAddress>)obj;
      if (sender() == null) {
        if (that.sender() != null)
          return false; 
      } else if (!sender().equals(that.sender())) {
        return false;
      } 
      if (recipient() == null) {
        if (that.recipient() != null)
          return false; 
      } else if (!recipient().equals(that.recipient())) {
        return false;
      } 
      return this.response.equals(obj);
    }
    
    public int hashCode() {
      int hashCode = this.response.hashCode();
      if (sender() != null)
        hashCode = hashCode * 31 + sender().hashCode(); 
      if (recipient() != null)
        hashCode = hashCode * 31 + recipient().hashCode(); 
      return hashCode;
    }
  }
}
