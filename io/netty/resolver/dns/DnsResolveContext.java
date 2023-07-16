package io.netty.resolver.dns;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.dns.DefaultDnsQuestion;
import io.netty.handler.codec.dns.DefaultDnsRecordDecoder;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.DnsResponseCode;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

abstract class DnsResolveContext<T> {
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(DnsResolveContext.class);
  
  private static final RuntimeException NXDOMAIN_QUERY_FAILED_EXCEPTION = DnsResolveContextException.newStatic("No answer found and NXDOMAIN response code returned", DnsResolveContext.class, "onResponse(..)");
  
  private static final RuntimeException CNAME_NOT_FOUND_QUERY_FAILED_EXCEPTION = DnsResolveContextException.newStatic("No matching CNAME record found", DnsResolveContext.class, "onResponseCNAME(..)");
  
  private static final RuntimeException NO_MATCHING_RECORD_QUERY_FAILED_EXCEPTION = DnsResolveContextException.newStatic("No matching record type found", DnsResolveContext.class, "onResponseAorAAAA(..)");
  
  private static final RuntimeException UNRECOGNIZED_TYPE_QUERY_FAILED_EXCEPTION = DnsResolveContextException.newStatic("Response type was unrecognized", DnsResolveContext.class, "onResponse(..)");
  
  private static final RuntimeException NAME_SERVERS_EXHAUSTED_EXCEPTION = DnsResolveContextException.newStatic("No name servers returned an answer", DnsResolveContext.class, "tryToFinishResolve(..)");
  
  final DnsNameResolver parent;
  
  private final Channel channel;
  
  private final Promise<?> originalPromise;
  
  private final DnsServerAddressStream nameServerAddrs;
  
  private final String hostname;
  
  private final int dnsClass;
  
  private final DnsRecordType[] expectedTypes;
  
  final DnsRecord[] additionals;
  
  private final Set<Future<AddressedEnvelope<DnsResponse, InetSocketAddress>>> queriesInProgress = Collections.newSetFromMap(new IdentityHashMap<Future<AddressedEnvelope<DnsResponse, InetSocketAddress>>, Boolean>());
  
  private List<T> finalResult;
  
  private int allowedQueries;
  
  private boolean triedCNAME;
  
  private boolean completeEarly;
  
  DnsResolveContext(DnsNameResolver parent, Channel channel, Promise<?> originalPromise, String hostname, int dnsClass, DnsRecordType[] expectedTypes, DnsRecord[] additionals, DnsServerAddressStream nameServerAddrs, int allowedQueries) {
    assert expectedTypes.length > 0;
    this.parent = parent;
    this.channel = channel;
    this.originalPromise = originalPromise;
    this.hostname = hostname;
    this.dnsClass = dnsClass;
    this.expectedTypes = expectedTypes;
    this.additionals = additionals;
    this.nameServerAddrs = (DnsServerAddressStream)ObjectUtil.checkNotNull(nameServerAddrs, "nameServerAddrs");
    this.allowedQueries = allowedQueries;
  }
  
  static final class DnsResolveContextException extends RuntimeException {
    private static final long serialVersionUID = 1209303419266433003L;
    
    private DnsResolveContextException(String message) {
      super(message);
    }
    
    @SuppressJava6Requirement(reason = "uses Java 7+ Exception.<init>(String, Throwable, boolean, boolean) but is guarded by version checks")
    private DnsResolveContextException(String message, boolean shared) {
      super(message, null, false, true);
      assert shared;
    }
    
    public Throwable fillInStackTrace() {
      return this;
    }
    
    static DnsResolveContextException newStatic(String message, Class<?> clazz, String method) {
      DnsResolveContextException exception;
      if (PlatformDependent.javaVersion() >= 7) {
        exception = new DnsResolveContextException(message, true);
      } else {
        exception = new DnsResolveContextException(message);
      } 
      return (DnsResolveContextException)ThrowableUtil.unknownStackTrace(exception, clazz, method);
    }
  }
  
  Channel channel() {
    return this.channel;
  }
  
  DnsCache resolveCache() {
    return this.parent.resolveCache();
  }
  
  DnsCnameCache cnameCache() {
    return this.parent.cnameCache();
  }
  
  AuthoritativeDnsServerCache authoritativeDnsServerCache() {
    return this.parent.authoritativeDnsServerCache();
  }
  
  void resolve(final Promise<List<T>> promise) {
    final String[] searchDomains = this.parent.searchDomains();
    if (searchDomains.length == 0 || this.parent.ndots() == 0 || StringUtil.endsWith(this.hostname, '.')) {
      internalResolve(this.hostname, promise);
    } else {
      final boolean startWithoutSearchDomain = hasNDots();
      String initialHostname = startWithoutSearchDomain ? this.hostname : (this.hostname + '.' + searchDomains[0]);
      final int initialSearchDomainIdx = startWithoutSearchDomain ? 0 : 1;
      Promise<List<T>> searchDomainPromise = this.parent.executor().newPromise();
      searchDomainPromise.addListener((GenericFutureListener)new FutureListener<List<T>>() {
            private int searchDomainIdx = initialSearchDomainIdx;
            
            public void operationComplete(Future<List<T>> future) {
              Throwable cause = future.cause();
              if (cause == null) {
                List<T> result = (List<T>)future.getNow();
                if (!promise.trySuccess(result))
                  for (T item : result)
                    ReferenceCountUtil.safeRelease(item);  
              } else if (DnsNameResolver.isTransportOrTimeoutError(cause)) {
                promise.tryFailure(new DnsResolveContext.SearchDomainUnknownHostException(cause, DnsResolveContext.this.hostname, DnsResolveContext.this.expectedTypes, searchDomains));
              } else if (this.searchDomainIdx < searchDomains.length) {
                Promise<List<T>> newPromise = DnsResolveContext.this.parent.executor().newPromise();
                newPromise.addListener((GenericFutureListener)this);
                DnsResolveContext.this.doSearchDomainQuery(DnsResolveContext.this.hostname + '.' + searchDomains[this.searchDomainIdx++], newPromise);
              } else if (!startWithoutSearchDomain) {
                DnsResolveContext.this.internalResolve(DnsResolveContext.this.hostname, promise);
              } else {
                promise.tryFailure(new DnsResolveContext.SearchDomainUnknownHostException(cause, DnsResolveContext.this.hostname, DnsResolveContext.this.expectedTypes, searchDomains));
              } 
            }
          });
      doSearchDomainQuery(initialHostname, searchDomainPromise);
    } 
  }
  
  private boolean hasNDots() {
    for (int idx = this.hostname.length() - 1, dots = 0; idx >= 0; idx--) {
      if (this.hostname.charAt(idx) == '.' && ++dots >= this.parent.ndots())
        return true; 
    } 
    return false;
  }
  
  private static final class SearchDomainUnknownHostException extends UnknownHostException {
    private static final long serialVersionUID = -8573510133644997085L;
    
    SearchDomainUnknownHostException(Throwable cause, String originalHostname, DnsRecordType[] queryTypes, String[] searchDomains) {
      super("Failed to resolve '" + originalHostname + "' " + Arrays.toString((Object[])queryTypes) + " and search domain query for configured domains failed as well: " + 
          
          Arrays.toString((Object[])searchDomains));
      setStackTrace(cause.getStackTrace());
      initCause(cause.getCause());
    }
    
    public Throwable fillInStackTrace() {
      return this;
    }
  }
  
  void doSearchDomainQuery(String hostname, Promise<List<T>> nextPromise) {
    DnsResolveContext<T> nextContext = newResolverContext(this.parent, this.channel, this.originalPromise, hostname, this.dnsClass, this.expectedTypes, this.additionals, this.nameServerAddrs, this.parent
        
        .maxQueriesPerResolve());
    nextContext.internalResolve(hostname, nextPromise);
  }
  
  private static String hostnameWithDot(String name) {
    if (StringUtil.endsWith(name, '.'))
      return name; 
    return name + '.';
  }
  
  static String cnameResolveFromCache(DnsCnameCache cnameCache, String name) throws UnknownHostException {
    String first = cnameCache.get(hostnameWithDot(name));
    if (first == null)
      return name; 
    String second = cnameCache.get(hostnameWithDot(first));
    if (second == null)
      return first; 
    checkCnameLoop(name, first, second);
    return cnameResolveFromCacheLoop(cnameCache, name, first, second);
  }
  
  private static String cnameResolveFromCacheLoop(DnsCnameCache cnameCache, String hostname, String first, String mapping) throws UnknownHostException {
    boolean advance = false;
    String name = mapping;
    while ((mapping = cnameCache.get(hostnameWithDot(name))) != null) {
      checkCnameLoop(hostname, first, mapping);
      name = mapping;
      if (advance)
        first = cnameCache.get(first); 
      advance = !advance;
    } 
    return name;
  }
  
  private static void checkCnameLoop(String hostname, String first, String second) throws UnknownHostException {
    if (first.equals(second))
      throw new UnknownHostException("CNAME loop detected for '" + hostname + '\''); 
  }
  
  private void internalResolve(String name, Promise<List<T>> promise) {
    try {
      name = cnameResolveFromCache(cnameCache(), name);
    } catch (Throwable cause) {
      promise.tryFailure(cause);
      return;
    } 
    try {
      DnsServerAddressStream nameServerAddressStream = getNameServers(name);
      int end = this.expectedTypes.length - 1;
      for (int i = 0; i < end; i++) {
        if (!query(name, this.expectedTypes[i], nameServerAddressStream.duplicate(), false, promise))
          return; 
      } 
      query(name, this.expectedTypes[end], nameServerAddressStream, false, promise);
    } finally {
      this.parent.flushQueries();
    } 
  }
  
  private DnsServerAddressStream getNameServersFromCache(String hostname) {
    DnsServerAddressStream entries;
    int len = hostname.length();
    if (len == 0)
      return null; 
    if (hostname.charAt(len - 1) != '.')
      hostname = hostname + "."; 
    int idx = hostname.indexOf('.');
    if (idx == hostname.length() - 1)
      return null; 
    do {
      hostname = hostname.substring(idx + 1);
      int idx2 = hostname.indexOf('.');
      if (idx2 <= 0 || idx2 == hostname.length() - 1)
        return null; 
      idx = idx2;
      entries = authoritativeDnsServerCache().get(hostname);
    } while (entries == null);
    return entries;
  }
  
  private void query(final DnsServerAddressStream nameServerAddrStream, final int nameServerAddrStreamIndex, final DnsQuestion question, final DnsQueryLifecycleObserver queryLifecycleObserver, boolean flush, final Promise<List<T>> promise, Throwable cause) {
    final long queryStartTimeNanos;
    final boolean isFeedbackAddressStream;
    if (this.completeEarly || nameServerAddrStreamIndex >= nameServerAddrStream.size() || this.allowedQueries == 0 || this.originalPromise
      .isCancelled() || promise.isCancelled()) {
      tryToFinishResolve(nameServerAddrStream, nameServerAddrStreamIndex, question, queryLifecycleObserver, promise, cause);
      return;
    } 
    this.allowedQueries--;
    final InetSocketAddress nameServerAddr = nameServerAddrStream.next();
    if (nameServerAddr.isUnresolved()) {
      queryUnresolvedNameServer(nameServerAddr, nameServerAddrStream, nameServerAddrStreamIndex, question, queryLifecycleObserver, promise, cause);
      return;
    } 
    Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> queryPromise = this.channel.eventLoop().newPromise();
    if (nameServerAddrStream instanceof DnsServerResponseFeedbackAddressStream) {
      queryStartTimeNanos = System.nanoTime();
      isFeedbackAddressStream = true;
    } else {
      queryStartTimeNanos = -1L;
      isFeedbackAddressStream = false;
    } 
    Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> f = this.parent.query0(nameServerAddr, question, queryLifecycleObserver, this.additionals, flush, queryPromise);
    this.queriesInProgress.add(f);
    f.addListener((GenericFutureListener)new FutureListener<AddressedEnvelope<DnsResponse, InetSocketAddress>>() {
          public void operationComplete(Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> future) {
            DnsResolveContext.this.queriesInProgress.remove(future);
            if (promise.isDone() || future.isCancelled()) {
              queryLifecycleObserver.queryCancelled(DnsResolveContext.this.allowedQueries);
              AddressedEnvelope<DnsResponse, InetSocketAddress> result = (AddressedEnvelope<DnsResponse, InetSocketAddress>)future.getNow();
              if (result != null)
                result.release(); 
              return;
            } 
            Throwable queryCause = future.cause();
            try {
              if (queryCause == null) {
                if (isFeedbackAddressStream) {
                  DnsServerResponseFeedbackAddressStream feedbackNameServerAddrStream = (DnsServerResponseFeedbackAddressStream)nameServerAddrStream;
                  feedbackNameServerAddrStream.feedbackSuccess(nameServerAddr, 
                      System.nanoTime() - queryStartTimeNanos);
                } 
                DnsResolveContext.this.onResponse(nameServerAddrStream, nameServerAddrStreamIndex, question, (AddressedEnvelope)future.getNow(), queryLifecycleObserver, promise);
              } else {
                if (isFeedbackAddressStream) {
                  DnsServerResponseFeedbackAddressStream feedbackNameServerAddrStream = (DnsServerResponseFeedbackAddressStream)nameServerAddrStream;
                  feedbackNameServerAddrStream.feedbackFailure(nameServerAddr, queryCause, 
                      System.nanoTime() - queryStartTimeNanos);
                } 
                queryLifecycleObserver.queryFailed(queryCause);
                DnsResolveContext.this.query(nameServerAddrStream, nameServerAddrStreamIndex + 1, question, DnsResolveContext.this
                    .newDnsQueryLifecycleObserver(question), true, promise, queryCause);
              } 
            } finally {
              DnsResolveContext.this.tryToFinishResolve(nameServerAddrStream, nameServerAddrStreamIndex, question, NoopDnsQueryLifecycleObserver.INSTANCE, promise, queryCause);
            } 
          }
        });
  }
  
  private void queryUnresolvedNameServer(final InetSocketAddress nameServerAddr, final DnsServerAddressStream nameServerAddrStream, final int nameServerAddrStreamIndex, final DnsQuestion question, final DnsQueryLifecycleObserver queryLifecycleObserver, final Promise<List<T>> promise, final Throwable cause) {
    String nameServerName = (PlatformDependent.javaVersion() >= 7) ? nameServerAddr.getHostString() : nameServerAddr.getHostName();
    assert nameServerName != null;
    final Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> resolveFuture = this.parent.executor().newSucceededFuture(null);
    this.queriesInProgress.add(resolveFuture);
    Promise<List<InetAddress>> resolverPromise = this.parent.executor().newPromise();
    resolverPromise.addListener((GenericFutureListener)new FutureListener<List<InetAddress>>() {
          public void operationComplete(Future<List<InetAddress>> future) {
            DnsResolveContext.this.queriesInProgress.remove(resolveFuture);
            if (future.isSuccess()) {
              List<InetAddress> resolvedAddresses = (List<InetAddress>)future.getNow();
              DnsServerAddressStream addressStream = new DnsResolveContext.CombinedDnsServerAddressStream(nameServerAddr, resolvedAddresses, nameServerAddrStream);
              DnsResolveContext.this.query(addressStream, nameServerAddrStreamIndex, question, queryLifecycleObserver, true, promise, cause);
            } else {
              DnsResolveContext.this.query(nameServerAddrStream, nameServerAddrStreamIndex + 1, question, queryLifecycleObserver, true, promise, cause);
            } 
          }
        });
    DnsCache resolveCache = resolveCache();
    if (!DnsNameResolver.doResolveAllCached(nameServerName, this.additionals, resolverPromise, resolveCache, this.parent
        .resolvedInternetProtocolFamiliesUnsafe()))
      (new DnsAddressResolveContext(this.parent, this.channel, this.originalPromise, nameServerName, this.additionals, this.parent
          .newNameServerAddressStream(nameServerName), this.allowedQueries, resolveCache, 
          
          redirectAuthoritativeDnsServerCache(authoritativeDnsServerCache()), false))
        .resolve(resolverPromise); 
  }
  
  private static AuthoritativeDnsServerCache redirectAuthoritativeDnsServerCache(AuthoritativeDnsServerCache authoritativeDnsServerCache) {
    if (authoritativeDnsServerCache instanceof RedirectAuthoritativeDnsServerCache)
      return authoritativeDnsServerCache; 
    return new RedirectAuthoritativeDnsServerCache(authoritativeDnsServerCache);
  }
  
  private static final class RedirectAuthoritativeDnsServerCache implements AuthoritativeDnsServerCache {
    private final AuthoritativeDnsServerCache wrapped;
    
    RedirectAuthoritativeDnsServerCache(AuthoritativeDnsServerCache authoritativeDnsServerCache) {
      this.wrapped = authoritativeDnsServerCache;
    }
    
    public DnsServerAddressStream get(String hostname) {
      return null;
    }
    
    public void cache(String hostname, InetSocketAddress address, long originalTtl, EventLoop loop) {
      this.wrapped.cache(hostname, address, originalTtl, loop);
    }
    
    public void clear() {
      this.wrapped.clear();
    }
    
    public boolean clear(String hostname) {
      return this.wrapped.clear(hostname);
    }
  }
  
  private void onResponse(DnsServerAddressStream nameServerAddrStream, int nameServerAddrStreamIndex, DnsQuestion question, AddressedEnvelope<DnsResponse, InetSocketAddress> envelope, DnsQueryLifecycleObserver queryLifecycleObserver, Promise<List<T>> promise) {
    try {
      DnsResponse res = (DnsResponse)envelope.content();
      DnsResponseCode code = res.code();
      if (code == DnsResponseCode.NOERROR) {
        if (handleRedirect(question, envelope, queryLifecycleObserver, promise))
          return; 
        DnsRecordType type = question.type();
        if (type == DnsRecordType.CNAME) {
          onResponseCNAME(question, buildAliasMap((DnsResponse)envelope.content(), cnameCache(), this.parent.executor()), queryLifecycleObserver, promise);
          return;
        } 
        for (DnsRecordType expectedType : this.expectedTypes) {
          if (type == expectedType) {
            onExpectedResponse(question, envelope, queryLifecycleObserver, promise);
            return;
          } 
        } 
        queryLifecycleObserver.queryFailed(UNRECOGNIZED_TYPE_QUERY_FAILED_EXCEPTION);
        return;
      } 
      if (code != DnsResponseCode.NXDOMAIN) {
        query(nameServerAddrStream, nameServerAddrStreamIndex + 1, question, queryLifecycleObserver
            .queryNoAnswer(code), true, promise, null);
      } else {
        queryLifecycleObserver.queryFailed(NXDOMAIN_QUERY_FAILED_EXCEPTION);
        if (!res.isAuthoritativeAnswer())
          query(nameServerAddrStream, nameServerAddrStreamIndex + 1, question, 
              newDnsQueryLifecycleObserver(question), true, promise, null); 
      } 
    } finally {
      ReferenceCountUtil.safeRelease(envelope);
    } 
  }
  
  private boolean handleRedirect(DnsQuestion question, AddressedEnvelope<DnsResponse, InetSocketAddress> envelope, DnsQueryLifecycleObserver queryLifecycleObserver, Promise<List<T>> promise) {
    DnsResponse res = (DnsResponse)envelope.content();
    if (res.count(DnsSection.ANSWER) == 0) {
      AuthoritativeNameServerList serverNames = extractAuthoritativeNameServers(question.name(), res);
      if (serverNames != null) {
        int additionalCount = res.count(DnsSection.ADDITIONAL);
        AuthoritativeDnsServerCache authoritativeDnsServerCache = authoritativeDnsServerCache();
        for (int i = 0; i < additionalCount; i++) {
          DnsRecord r = res.recordAt(DnsSection.ADDITIONAL, i);
          if ((r.type() != DnsRecordType.A || this.parent.supportsARecords()) && (r
            .type() != DnsRecordType.AAAA || this.parent.supportsAAAARecords()))
            serverNames.handleWithAdditional(this.parent, r, authoritativeDnsServerCache); 
        } 
        serverNames.handleWithoutAdditionals(this.parent, resolveCache(), authoritativeDnsServerCache);
        List<InetSocketAddress> addresses = serverNames.addressList();
        DnsServerAddressStream serverStream = this.parent.newRedirectDnsServerStream(question
            .name(), addresses);
        if (serverStream != null) {
          query(serverStream, 0, question, queryLifecycleObserver
              .queryRedirected(new DnsAddressStreamList(serverStream)), true, promise, null);
          return true;
        } 
      } 
    } 
    return false;
  }
  
  private static final class DnsAddressStreamList extends AbstractList<InetSocketAddress> {
    private final DnsServerAddressStream duplicate;
    
    private List<InetSocketAddress> addresses;
    
    DnsAddressStreamList(DnsServerAddressStream stream) {
      this.duplicate = stream.duplicate();
    }
    
    public InetSocketAddress get(int index) {
      if (this.addresses == null) {
        DnsServerAddressStream stream = this.duplicate.duplicate();
        this.addresses = new ArrayList<InetSocketAddress>(size());
        for (int i = 0; i < stream.size(); i++)
          this.addresses.add(stream.next()); 
      } 
      return this.addresses.get(index);
    }
    
    public int size() {
      return this.duplicate.size();
    }
    
    public Iterator<InetSocketAddress> iterator() {
      return new Iterator<InetSocketAddress>() {
          private final DnsServerAddressStream stream = DnsResolveContext.DnsAddressStreamList.this.duplicate.duplicate();
          
          private int i;
          
          public boolean hasNext() {
            return (this.i < this.stream.size());
          }
          
          public InetSocketAddress next() {
            if (!hasNext())
              throw new NoSuchElementException(); 
            this.i++;
            return this.stream.next();
          }
          
          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
    }
  }
  
  private static AuthoritativeNameServerList extractAuthoritativeNameServers(String questionName, DnsResponse res) {
    int authorityCount = res.count(DnsSection.AUTHORITY);
    if (authorityCount == 0)
      return null; 
    AuthoritativeNameServerList serverNames = new AuthoritativeNameServerList(questionName);
    for (int i = 0; i < authorityCount; i++)
      serverNames.add(res.recordAt(DnsSection.AUTHORITY, i)); 
    return serverNames.isEmpty() ? null : serverNames;
  }
  
  private void onExpectedResponse(DnsQuestion question, AddressedEnvelope<DnsResponse, InetSocketAddress> envelope, DnsQueryLifecycleObserver queryLifecycleObserver, Promise<List<T>> promise) {
    DnsResponse response = (DnsResponse)envelope.content();
    Map<String, String> cnames = buildAliasMap(response, cnameCache(), this.parent.executor());
    int answerCount = response.count(DnsSection.ANSWER);
    boolean found = false;
    boolean completeEarly = this.completeEarly;
    boolean cnameNeedsFollow = !cnames.isEmpty();
    for (int i = 0; i < answerCount; i++) {
      DnsRecord r = response.recordAt(DnsSection.ANSWER, i);
      DnsRecordType type = r.type();
      boolean matches = false;
      for (DnsRecordType expectedType : this.expectedTypes) {
        if (type == expectedType) {
          matches = true;
          break;
        } 
      } 
      if (!matches)
        continue; 
      String questionName = question.name().toLowerCase(Locale.US);
      String recordName = r.name().toLowerCase(Locale.US);
      if (!recordName.equals(questionName)) {
        Map<String, String> cnamesCopy = new HashMap<String, String>(cnames);
        String resolved = questionName;
        do {
          resolved = cnamesCopy.remove(resolved);
          if (recordName.equals(resolved)) {
            cnameNeedsFollow = false;
            break;
          } 
        } while (resolved != null);
        if (resolved == null) {
          assert questionName.isEmpty() || questionName.charAt(questionName.length() - 1) == '.';
          for (String searchDomain : this.parent.searchDomains()) {
            if (!searchDomain.isEmpty()) {
              String fqdn;
              if (searchDomain.charAt(searchDomain.length() - 1) == '.') {
                fqdn = questionName + searchDomain;
              } else {
                fqdn = questionName + searchDomain + '.';
              } 
              if (recordName.equals(fqdn)) {
                resolved = recordName;
                break;
              } 
            } 
          } 
          if (resolved == null) {
            if (logger.isDebugEnabled())
              logger.debug("{} Ignoring record {} for [{}: {}] as it contains a different name than the question name [{}]. Cnames: {}, Search domains: {}", new Object[] { this.channel, r
                    
                    .toString(), Integer.valueOf(response.id()), envelope.sender(), questionName, cnames, this.parent
                    .searchDomains() }); 
            continue;
          } 
        } 
      } 
      T converted = convertRecord(r, this.hostname, this.additionals, this.parent.executor());
      if (converted == null) {
        if (logger.isDebugEnabled())
          logger.debug("{} Ignoring record {} for [{}: {}] as the converted record is null. Hostname [{}], Additionals: {}", new Object[] { this.channel, r
                
                .toString(), Integer.valueOf(response.id()), envelope.sender(), this.hostname, this.additionals }); 
      } else {
        boolean shouldRelease = false;
        if (!completeEarly)
          completeEarly = isCompleteEarly(converted); 
        if (!promise.isDone()) {
          if (this.finalResult == null) {
            this.finalResult = new ArrayList<T>(8);
            this.finalResult.add(converted);
          } else if (isDuplicateAllowed() || !this.finalResult.contains(converted)) {
            this.finalResult.add(converted);
          } else {
            shouldRelease = true;
          } 
        } else {
          shouldRelease = true;
        } 
        cache(this.hostname, this.additionals, r, converted);
        found = true;
        if (shouldRelease)
          ReferenceCountUtil.release(converted); 
      } 
      continue;
    } 
    if (found && !cnameNeedsFollow) {
      if (completeEarly)
        this.completeEarly = true; 
      queryLifecycleObserver.querySucceed();
    } else if (cnames.isEmpty()) {
      queryLifecycleObserver.queryFailed(NO_MATCHING_RECORD_QUERY_FAILED_EXCEPTION);
    } else {
      queryLifecycleObserver.querySucceed();
      onResponseCNAME(question, cnames, newDnsQueryLifecycleObserver(question), promise);
    } 
  }
  
  private void onResponseCNAME(DnsQuestion question, Map<String, String> cnames, DnsQueryLifecycleObserver queryLifecycleObserver, Promise<List<T>> promise) {
    String resolved = question.name().toLowerCase(Locale.US);
    boolean found = false;
    while (!cnames.isEmpty()) {
      String next = cnames.remove(resolved);
      if (next != null) {
        found = true;
        resolved = next;
      } 
    } 
    if (found) {
      followCname(question, resolved, queryLifecycleObserver, promise);
    } else {
      queryLifecycleObserver.queryFailed(CNAME_NOT_FOUND_QUERY_FAILED_EXCEPTION);
    } 
  }
  
  private static Map<String, String> buildAliasMap(DnsResponse response, DnsCnameCache cache, EventLoop loop) {
    int answerCount = response.count(DnsSection.ANSWER);
    Map<String, String> cnames = null;
    for (int i = 0; i < answerCount; i++) {
      DnsRecord r = response.recordAt(DnsSection.ANSWER, i);
      DnsRecordType type = r.type();
      if (type == DnsRecordType.CNAME)
        if (r instanceof io.netty.handler.codec.dns.DnsRawRecord) {
          ByteBuf recordContent = ((ByteBufHolder)r).content();
          String domainName = decodeDomainName(recordContent);
          if (domainName != null) {
            if (cnames == null)
              cnames = new HashMap<String, String>(Math.min(8, answerCount)); 
            String name = r.name().toLowerCase(Locale.US);
            String mapping = domainName.toLowerCase(Locale.US);
            String nameWithDot = hostnameWithDot(name);
            String mappingWithDot = hostnameWithDot(mapping);
            if (!nameWithDot.equalsIgnoreCase(mappingWithDot)) {
              cache.cache(nameWithDot, mappingWithDot, r.timeToLive(), loop);
              cnames.put(name, mapping);
            } 
          } 
        }  
    } 
    return (cnames != null) ? cnames : Collections.<String, String>emptyMap();
  }
  
  private void tryToFinishResolve(DnsServerAddressStream nameServerAddrStream, int nameServerAddrStreamIndex, DnsQuestion question, DnsQueryLifecycleObserver queryLifecycleObserver, Promise<List<T>> promise, Throwable cause) {
    if (!this.completeEarly && !this.queriesInProgress.isEmpty()) {
      queryLifecycleObserver.queryCancelled(this.allowedQueries);
      return;
    } 
    if (this.finalResult == null) {
      if (nameServerAddrStreamIndex < nameServerAddrStream.size()) {
        if (queryLifecycleObserver == NoopDnsQueryLifecycleObserver.INSTANCE) {
          query(nameServerAddrStream, nameServerAddrStreamIndex + 1, question, 
              newDnsQueryLifecycleObserver(question), true, promise, cause);
        } else {
          query(nameServerAddrStream, nameServerAddrStreamIndex + 1, question, queryLifecycleObserver, true, promise, cause);
        } 
        return;
      } 
      queryLifecycleObserver.queryFailed(NAME_SERVERS_EXHAUSTED_EXCEPTION);
      if (cause == null && !this.triedCNAME && (question
        .type() == DnsRecordType.A || question.type() == DnsRecordType.AAAA)) {
        this.triedCNAME = true;
        query(this.hostname, DnsRecordType.CNAME, getNameServers(this.hostname), true, promise);
        return;
      } 
    } else {
      queryLifecycleObserver.queryCancelled(this.allowedQueries);
    } 
    finishResolve(promise, cause);
  }
  
  private void finishResolve(Promise<List<T>> promise, Throwable cause) {
    if (!this.completeEarly && !this.queriesInProgress.isEmpty()) {
      Iterator<Future<AddressedEnvelope<DnsResponse, InetSocketAddress>>> i = this.queriesInProgress.iterator();
      while (i.hasNext()) {
        Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> f = i.next();
        i.remove();
        f.cancel(false);
      } 
    } 
    if (this.finalResult != null) {
      if (!promise.isDone()) {
        List<T> result = filterResults(this.finalResult);
        this.finalResult = Collections.emptyList();
        if (!DnsNameResolver.trySuccess(promise, result))
          for (T item : result)
            ReferenceCountUtil.safeRelease(item);  
      } else {
        assert this.finalResult.isEmpty();
      } 
      return;
    } 
    int maxAllowedQueries = this.parent.maxQueriesPerResolve();
    int tries = maxAllowedQueries - this.allowedQueries;
    StringBuilder buf = new StringBuilder(64);
    buf.append("Failed to resolve '").append(this.hostname).append("' ").append(Arrays.toString((Object[])this.expectedTypes));
    if (tries > 1)
      if (tries < maxAllowedQueries) {
        buf.append(" after ")
          .append(tries)
          .append(" queries ");
      } else {
        buf.append(". Exceeded max queries per resolve ")
          .append(maxAllowedQueries)
          .append(' ');
      }  
    UnknownHostException unknownHostException = new UnknownHostException(buf.toString());
    if (cause == null) {
      cache(this.hostname, this.additionals, unknownHostException);
    } else {
      unknownHostException.initCause(cause);
    } 
    promise.tryFailure(unknownHostException);
  }
  
  static String decodeDomainName(ByteBuf in) {
    in.markReaderIndex();
    try {
      return DefaultDnsRecordDecoder.decodeName(in);
    } catch (CorruptedFrameException e) {
      return null;
    } finally {
      in.resetReaderIndex();
    } 
  }
  
  private DnsServerAddressStream getNameServers(String name) {
    DnsServerAddressStream stream = getNameServersFromCache(name);
    if (stream == null) {
      if (name.equals(this.hostname))
        return this.nameServerAddrs.duplicate(); 
      return this.parent.newNameServerAddressStream(name);
    } 
    return stream;
  }
  
  private void followCname(DnsQuestion question, String cname, DnsQueryLifecycleObserver queryLifecycleObserver, Promise<List<T>> promise) {
    DefaultDnsQuestion defaultDnsQuestion;
    DnsServerAddressStream stream;
    try {
      cname = cnameResolveFromCache(cnameCache(), cname);
      stream = getNameServers(cname);
      defaultDnsQuestion = new DefaultDnsQuestion(cname, question.type(), this.dnsClass);
    } catch (Throwable cause) {
      queryLifecycleObserver.queryFailed(cause);
      PlatformDependent.throwException(cause);
      return;
    } 
    query(stream, 0, (DnsQuestion)defaultDnsQuestion, queryLifecycleObserver.queryCNAMEd((DnsQuestion)defaultDnsQuestion), true, promise, null);
  }
  
  private boolean query(String hostname, DnsRecordType type, DnsServerAddressStream dnsServerAddressStream, boolean flush, Promise<List<T>> promise) {
    DefaultDnsQuestion defaultDnsQuestion;
    try {
      defaultDnsQuestion = new DefaultDnsQuestion(hostname, type, this.dnsClass);
    } catch (Throwable cause) {
      promise.tryFailure(new IllegalArgumentException("Unable to create DNS Question for: [" + hostname + ", " + type + ']', cause));
      return false;
    } 
    query(dnsServerAddressStream, 0, (DnsQuestion)defaultDnsQuestion, newDnsQueryLifecycleObserver((DnsQuestion)defaultDnsQuestion), flush, promise, null);
    return true;
  }
  
  private DnsQueryLifecycleObserver newDnsQueryLifecycleObserver(DnsQuestion question) {
    return this.parent.dnsQueryLifecycleObserverFactory().newDnsQueryLifecycleObserver(question);
  }
  
  abstract DnsResolveContext<T> newResolverContext(DnsNameResolver paramDnsNameResolver, Channel paramChannel, Promise<?> paramPromise, String paramString, int paramInt1, DnsRecordType[] paramArrayOfDnsRecordType, DnsRecord[] paramArrayOfDnsRecord, DnsServerAddressStream paramDnsServerAddressStream, int paramInt2);
  
  abstract T convertRecord(DnsRecord paramDnsRecord, String paramString, DnsRecord[] paramArrayOfDnsRecord, EventLoop paramEventLoop);
  
  abstract List<T> filterResults(List<T> paramList);
  
  abstract boolean isCompleteEarly(T paramT);
  
  abstract boolean isDuplicateAllowed();
  
  abstract void cache(String paramString, DnsRecord[] paramArrayOfDnsRecord, DnsRecord paramDnsRecord, T paramT);
  
  abstract void cache(String paramString, DnsRecord[] paramArrayOfDnsRecord, UnknownHostException paramUnknownHostException);
  
  private final class CombinedDnsServerAddressStream implements DnsServerAddressStream {
    private final InetSocketAddress replaced;
    
    private final DnsServerAddressStream originalStream;
    
    private final List<InetAddress> resolvedAddresses;
    
    private Iterator<InetAddress> resolved;
    
    CombinedDnsServerAddressStream(InetSocketAddress replaced, List<InetAddress> resolvedAddresses, DnsServerAddressStream originalStream) {
      this.replaced = replaced;
      this.resolvedAddresses = resolvedAddresses;
      this.originalStream = originalStream;
      this.resolved = resolvedAddresses.iterator();
    }
    
    public InetSocketAddress next() {
      if (this.resolved.hasNext())
        return nextResolved0(); 
      InetSocketAddress address = this.originalStream.next();
      if (address.equals(this.replaced)) {
        this.resolved = this.resolvedAddresses.iterator();
        return nextResolved0();
      } 
      return address;
    }
    
    private InetSocketAddress nextResolved0() {
      return DnsResolveContext.this.parent.newRedirectServerAddress(this.resolved.next());
    }
    
    public int size() {
      return this.originalStream.size() + this.resolvedAddresses.size() - 1;
    }
    
    public DnsServerAddressStream duplicate() {
      return new CombinedDnsServerAddressStream(this.replaced, this.resolvedAddresses, this.originalStream.duplicate());
    }
  }
  
  private static final class AuthoritativeNameServerList {
    private final String questionName;
    
    private DnsResolveContext.AuthoritativeNameServer head;
    
    private int nameServerCount;
    
    AuthoritativeNameServerList(String questionName) {
      this.questionName = questionName.toLowerCase(Locale.US);
    }
    
    void add(DnsRecord r) {
      if (r.type() != DnsRecordType.NS || !(r instanceof io.netty.handler.codec.dns.DnsRawRecord))
        return; 
      if (this.questionName.length() < r.name().length())
        return; 
      String recordName = r.name().toLowerCase(Locale.US);
      int dots = 0;
      for (int a = recordName.length() - 1, b = this.questionName.length() - 1; a >= 0; a--, b--) {
        char c = recordName.charAt(a);
        if (this.questionName.charAt(b) != c)
          return; 
        if (c == '.')
          dots++; 
      } 
      if (this.head != null && this.head.dots > dots)
        return; 
      ByteBuf recordContent = ((ByteBufHolder)r).content();
      String domainName = DnsResolveContext.decodeDomainName(recordContent);
      if (domainName == null)
        return; 
      if (this.head == null || this.head.dots < dots) {
        this.nameServerCount = 1;
        this.head = new DnsResolveContext.AuthoritativeNameServer(dots, r.timeToLive(), recordName, domainName);
      } else if (this.head.dots == dots) {
        DnsResolveContext.AuthoritativeNameServer serverName = this.head;
        while (serverName.next != null)
          serverName = serverName.next; 
        serverName.next = new DnsResolveContext.AuthoritativeNameServer(dots, r.timeToLive(), recordName, domainName);
        this.nameServerCount++;
      } 
    }
    
    void handleWithAdditional(DnsNameResolver parent, DnsRecord r, AuthoritativeDnsServerCache authoritativeCache) {
      DnsResolveContext.AuthoritativeNameServer serverName = this.head;
      String nsName = r.name();
      InetAddress resolved = DnsAddressDecoder.decodeAddress(r, nsName, parent.isDecodeIdn());
      if (resolved == null)
        return; 
      while (serverName != null) {
        if (serverName.nsName.equalsIgnoreCase(nsName)) {
          if (serverName.address != null) {
            while (serverName.next != null && serverName.next.isCopy)
              serverName = serverName.next; 
            DnsResolveContext.AuthoritativeNameServer server = new DnsResolveContext.AuthoritativeNameServer(serverName);
            server.next = serverName.next;
            serverName.next = server;
            serverName = server;
            this.nameServerCount++;
          } 
          serverName.update(parent.newRedirectServerAddress(resolved), r.timeToLive());
          cache(serverName, authoritativeCache, parent.executor());
          return;
        } 
        serverName = serverName.next;
      } 
    }
    
    void handleWithoutAdditionals(DnsNameResolver parent, DnsCache cache, AuthoritativeDnsServerCache authoritativeCache) {
      DnsResolveContext.AuthoritativeNameServer serverName = this.head;
      while (serverName != null) {
        if (serverName.address == null) {
          cacheUnresolved(serverName, authoritativeCache, parent.executor());
          List<? extends DnsCacheEntry> entries = cache.get(serverName.nsName, null);
          if (entries != null && !entries.isEmpty()) {
            InetAddress address = ((DnsCacheEntry)entries.get(0)).address();
            if (address != null) {
              serverName.update(parent.newRedirectServerAddress(address));
              for (int i = 1; i < entries.size(); i++) {
                address = ((DnsCacheEntry)entries.get(i)).address();
                assert address != null : "Cache returned a cached failure, should never return anything else";
                DnsResolveContext.AuthoritativeNameServer server = new DnsResolveContext.AuthoritativeNameServer(serverName);
                server.next = serverName.next;
                serverName.next = server;
                serverName = server;
                serverName.update(parent.newRedirectServerAddress(address));
                this.nameServerCount++;
              } 
            } 
          } 
        } 
        serverName = serverName.next;
      } 
    }
    
    private static void cacheUnresolved(DnsResolveContext.AuthoritativeNameServer server, AuthoritativeDnsServerCache authoritativeCache, EventLoop loop) {
      server.address = InetSocketAddress.createUnresolved(server.nsName, 53);
      cache(server, authoritativeCache, loop);
    }
    
    private static void cache(DnsResolveContext.AuthoritativeNameServer server, AuthoritativeDnsServerCache cache, EventLoop loop) {
      if (!server.isRootServer())
        cache.cache(server.domainName, server.address, server.ttl, loop); 
    }
    
    boolean isEmpty() {
      return (this.nameServerCount == 0);
    }
    
    List<InetSocketAddress> addressList() {
      List<InetSocketAddress> addressList = new ArrayList<InetSocketAddress>(this.nameServerCount);
      DnsResolveContext.AuthoritativeNameServer server = this.head;
      while (server != null) {
        if (server.address != null)
          addressList.add(server.address); 
        server = server.next;
      } 
      return addressList;
    }
  }
  
  private static final class AuthoritativeNameServer {
    private final int dots;
    
    private final String domainName;
    
    final boolean isCopy;
    
    final String nsName;
    
    private long ttl;
    
    private InetSocketAddress address;
    
    AuthoritativeNameServer next;
    
    AuthoritativeNameServer(int dots, long ttl, String domainName, String nsName) {
      this.dots = dots;
      this.ttl = ttl;
      this.nsName = nsName;
      this.domainName = domainName;
      this.isCopy = false;
    }
    
    AuthoritativeNameServer(AuthoritativeNameServer server) {
      this.dots = server.dots;
      this.ttl = server.ttl;
      this.nsName = server.nsName;
      this.domainName = server.domainName;
      this.isCopy = true;
    }
    
    boolean isRootServer() {
      return (this.dots == 1);
    }
    
    void update(InetSocketAddress address, long ttl) {
      assert this.address == null || this.address.isUnresolved();
      this.address = address;
      this.ttl = Math.min(this.ttl, ttl);
    }
    
    void update(InetSocketAddress address) {
      update(address, Long.MAX_VALUE);
    }
  }
}
