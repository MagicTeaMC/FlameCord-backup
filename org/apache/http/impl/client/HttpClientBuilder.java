package org.apache.http.impl.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.BackoffManager;
import org.apache.http.client.ConnectionBackoffStrategy;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.InputStreamFactory;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.protocol.RequestAuthCache;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.apache.http.client.protocol.RequestExpectContinue;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.impl.execchain.BackoffStrategyExec;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.impl.execchain.MainClientExec;
import org.apache.http.impl.execchain.ProtocolExec;
import org.apache.http.impl.execchain.RedirectExec;
import org.apache.http.impl.execchain.RetryExec;
import org.apache.http.impl.execchain.ServiceUnavailableRetryExec;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.TextUtils;
import org.apache.http.util.VersionInfo;

public class HttpClientBuilder {
  private HttpRequestExecutor requestExec;
  
  private HostnameVerifier hostnameVerifier;
  
  private LayeredConnectionSocketFactory sslSocketFactory;
  
  private SSLContext sslContext;
  
  private HttpClientConnectionManager connManager;
  
  private boolean connManagerShared;
  
  private SchemePortResolver schemePortResolver;
  
  private ConnectionReuseStrategy reuseStrategy;
  
  private ConnectionKeepAliveStrategy keepAliveStrategy;
  
  private AuthenticationStrategy targetAuthStrategy;
  
  private AuthenticationStrategy proxyAuthStrategy;
  
  private UserTokenHandler userTokenHandler;
  
  private HttpProcessor httpprocessor;
  
  private DnsResolver dnsResolver;
  
  private LinkedList<HttpRequestInterceptor> requestFirst;
  
  private LinkedList<HttpRequestInterceptor> requestLast;
  
  private LinkedList<HttpResponseInterceptor> responseFirst;
  
  private LinkedList<HttpResponseInterceptor> responseLast;
  
  private HttpRequestRetryHandler retryHandler;
  
  private HttpRoutePlanner routePlanner;
  
  private RedirectStrategy redirectStrategy;
  
  private ConnectionBackoffStrategy connectionBackoffStrategy;
  
  private BackoffManager backoffManager;
  
  private ServiceUnavailableRetryStrategy serviceUnavailStrategy;
  
  private Lookup<AuthSchemeProvider> authSchemeRegistry;
  
  private Lookup<CookieSpecProvider> cookieSpecRegistry;
  
  private Map<String, InputStreamFactory> contentDecoderMap;
  
  private CookieStore cookieStore;
  
  private CredentialsProvider credentialsProvider;
  
  private String userAgent;
  
  private HttpHost proxy;
  
  private Collection<? extends Header> defaultHeaders;
  
  private SocketConfig defaultSocketConfig;
  
  private ConnectionConfig defaultConnectionConfig;
  
  private RequestConfig defaultRequestConfig;
  
  private boolean evictExpiredConnections;
  
  private boolean evictIdleConnections;
  
  private long maxIdleTime;
  
  private TimeUnit maxIdleTimeUnit;
  
  private boolean systemProperties;
  
  private boolean redirectHandlingDisabled;
  
  private boolean automaticRetriesDisabled;
  
  private boolean contentCompressionDisabled;
  
  private boolean cookieManagementDisabled;
  
  private boolean authCachingDisabled;
  
  private boolean connectionStateDisabled;
  
  private boolean defaultUserAgentDisabled;
  
  private int maxConnTotal = 0;
  
  private int maxConnPerRoute = 0;
  
  private long connTimeToLive = -1L;
  
  private TimeUnit connTimeToLiveTimeUnit = TimeUnit.MILLISECONDS;
  
  private List<Closeable> closeables;
  
  private PublicSuffixMatcher publicSuffixMatcher;
  
  public static HttpClientBuilder create() {
    return new HttpClientBuilder();
  }
  
  public final HttpClientBuilder setRequestExecutor(HttpRequestExecutor requestExec) {
    this.requestExec = requestExec;
    return this;
  }
  
  @Deprecated
  public final HttpClientBuilder setHostnameVerifier(X509HostnameVerifier hostnameVerifier) {
    this.hostnameVerifier = (HostnameVerifier)hostnameVerifier;
    return this;
  }
  
  public final HttpClientBuilder setSSLHostnameVerifier(HostnameVerifier hostnameVerifier) {
    this.hostnameVerifier = hostnameVerifier;
    return this;
  }
  
  public final HttpClientBuilder setPublicSuffixMatcher(PublicSuffixMatcher publicSuffixMatcher) {
    this.publicSuffixMatcher = publicSuffixMatcher;
    return this;
  }
  
  @Deprecated
  public final HttpClientBuilder setSslcontext(SSLContext sslcontext) {
    return setSSLContext(sslcontext);
  }
  
  public final HttpClientBuilder setSSLContext(SSLContext sslContext) {
    this.sslContext = sslContext;
    return this;
  }
  
  public final HttpClientBuilder setSSLSocketFactory(LayeredConnectionSocketFactory sslSocketFactory) {
    this.sslSocketFactory = sslSocketFactory;
    return this;
  }
  
  public final HttpClientBuilder setMaxConnTotal(int maxConnTotal) {
    this.maxConnTotal = maxConnTotal;
    return this;
  }
  
  public final HttpClientBuilder setMaxConnPerRoute(int maxConnPerRoute) {
    this.maxConnPerRoute = maxConnPerRoute;
    return this;
  }
  
  public final HttpClientBuilder setDefaultSocketConfig(SocketConfig config) {
    this.defaultSocketConfig = config;
    return this;
  }
  
  public final HttpClientBuilder setDefaultConnectionConfig(ConnectionConfig config) {
    this.defaultConnectionConfig = config;
    return this;
  }
  
  public final HttpClientBuilder setConnectionTimeToLive(long connTimeToLive, TimeUnit connTimeToLiveTimeUnit) {
    this.connTimeToLive = connTimeToLive;
    this.connTimeToLiveTimeUnit = connTimeToLiveTimeUnit;
    return this;
  }
  
  public final HttpClientBuilder setConnectionManager(HttpClientConnectionManager connManager) {
    this.connManager = connManager;
    return this;
  }
  
  public final HttpClientBuilder setConnectionManagerShared(boolean shared) {
    this.connManagerShared = shared;
    return this;
  }
  
  public final HttpClientBuilder setConnectionReuseStrategy(ConnectionReuseStrategy reuseStrategy) {
    this.reuseStrategy = reuseStrategy;
    return this;
  }
  
  public final HttpClientBuilder setKeepAliveStrategy(ConnectionKeepAliveStrategy keepAliveStrategy) {
    this.keepAliveStrategy = keepAliveStrategy;
    return this;
  }
  
  public final HttpClientBuilder setTargetAuthenticationStrategy(AuthenticationStrategy targetAuthStrategy) {
    this.targetAuthStrategy = targetAuthStrategy;
    return this;
  }
  
  public final HttpClientBuilder setProxyAuthenticationStrategy(AuthenticationStrategy proxyAuthStrategy) {
    this.proxyAuthStrategy = proxyAuthStrategy;
    return this;
  }
  
  public final HttpClientBuilder setUserTokenHandler(UserTokenHandler userTokenHandler) {
    this.userTokenHandler = userTokenHandler;
    return this;
  }
  
  public final HttpClientBuilder disableConnectionState() {
    this.connectionStateDisabled = true;
    return this;
  }
  
  public final HttpClientBuilder setSchemePortResolver(SchemePortResolver schemePortResolver) {
    this.schemePortResolver = schemePortResolver;
    return this;
  }
  
  public final HttpClientBuilder setUserAgent(String userAgent) {
    this.userAgent = userAgent;
    return this;
  }
  
  public final HttpClientBuilder setDefaultHeaders(Collection<? extends Header> defaultHeaders) {
    this.defaultHeaders = defaultHeaders;
    return this;
  }
  
  public final HttpClientBuilder addInterceptorFirst(HttpResponseInterceptor itcp) {
    if (itcp == null)
      return this; 
    if (this.responseFirst == null)
      this.responseFirst = new LinkedList<HttpResponseInterceptor>(); 
    this.responseFirst.addFirst(itcp);
    return this;
  }
  
  public final HttpClientBuilder addInterceptorLast(HttpResponseInterceptor itcp) {
    if (itcp == null)
      return this; 
    if (this.responseLast == null)
      this.responseLast = new LinkedList<HttpResponseInterceptor>(); 
    this.responseLast.addLast(itcp);
    return this;
  }
  
  public final HttpClientBuilder addInterceptorFirst(HttpRequestInterceptor itcp) {
    if (itcp == null)
      return this; 
    if (this.requestFirst == null)
      this.requestFirst = new LinkedList<HttpRequestInterceptor>(); 
    this.requestFirst.addFirst(itcp);
    return this;
  }
  
  public final HttpClientBuilder addInterceptorLast(HttpRequestInterceptor itcp) {
    if (itcp == null)
      return this; 
    if (this.requestLast == null)
      this.requestLast = new LinkedList<HttpRequestInterceptor>(); 
    this.requestLast.addLast(itcp);
    return this;
  }
  
  public final HttpClientBuilder disableCookieManagement() {
    this.cookieManagementDisabled = true;
    return this;
  }
  
  public final HttpClientBuilder disableContentCompression() {
    this.contentCompressionDisabled = true;
    return this;
  }
  
  public final HttpClientBuilder disableAuthCaching() {
    this.authCachingDisabled = true;
    return this;
  }
  
  public final HttpClientBuilder setHttpProcessor(HttpProcessor httpprocessor) {
    this.httpprocessor = httpprocessor;
    return this;
  }
  
  public final HttpClientBuilder setDnsResolver(DnsResolver dnsResolver) {
    this.dnsResolver = dnsResolver;
    return this;
  }
  
  public final HttpClientBuilder setRetryHandler(HttpRequestRetryHandler retryHandler) {
    this.retryHandler = retryHandler;
    return this;
  }
  
  public final HttpClientBuilder disableAutomaticRetries() {
    this.automaticRetriesDisabled = true;
    return this;
  }
  
  public final HttpClientBuilder setProxy(HttpHost proxy) {
    this.proxy = proxy;
    return this;
  }
  
  public final HttpClientBuilder setRoutePlanner(HttpRoutePlanner routePlanner) {
    this.routePlanner = routePlanner;
    return this;
  }
  
  public final HttpClientBuilder setRedirectStrategy(RedirectStrategy redirectStrategy) {
    this.redirectStrategy = redirectStrategy;
    return this;
  }
  
  public final HttpClientBuilder disableRedirectHandling() {
    this.redirectHandlingDisabled = true;
    return this;
  }
  
  public final HttpClientBuilder setConnectionBackoffStrategy(ConnectionBackoffStrategy connectionBackoffStrategy) {
    this.connectionBackoffStrategy = connectionBackoffStrategy;
    return this;
  }
  
  public final HttpClientBuilder setBackoffManager(BackoffManager backoffManager) {
    this.backoffManager = backoffManager;
    return this;
  }
  
  public final HttpClientBuilder setServiceUnavailableRetryStrategy(ServiceUnavailableRetryStrategy serviceUnavailStrategy) {
    this.serviceUnavailStrategy = serviceUnavailStrategy;
    return this;
  }
  
  public final HttpClientBuilder setDefaultCookieStore(CookieStore cookieStore) {
    this.cookieStore = cookieStore;
    return this;
  }
  
  public final HttpClientBuilder setDefaultCredentialsProvider(CredentialsProvider credentialsProvider) {
    this.credentialsProvider = credentialsProvider;
    return this;
  }
  
  public final HttpClientBuilder setDefaultAuthSchemeRegistry(Lookup<AuthSchemeProvider> authSchemeRegistry) {
    this.authSchemeRegistry = authSchemeRegistry;
    return this;
  }
  
  public final HttpClientBuilder setDefaultCookieSpecRegistry(Lookup<CookieSpecProvider> cookieSpecRegistry) {
    this.cookieSpecRegistry = cookieSpecRegistry;
    return this;
  }
  
  public final HttpClientBuilder setContentDecoderRegistry(Map<String, InputStreamFactory> contentDecoderMap) {
    this.contentDecoderMap = contentDecoderMap;
    return this;
  }
  
  public final HttpClientBuilder setDefaultRequestConfig(RequestConfig config) {
    this.defaultRequestConfig = config;
    return this;
  }
  
  public final HttpClientBuilder useSystemProperties() {
    this.systemProperties = true;
    return this;
  }
  
  public final HttpClientBuilder evictExpiredConnections() {
    this.evictExpiredConnections = true;
    return this;
  }
  
  @Deprecated
  public final HttpClientBuilder evictIdleConnections(Long maxIdleTime, TimeUnit maxIdleTimeUnit) {
    return evictIdleConnections(maxIdleTime.longValue(), maxIdleTimeUnit);
  }
  
  public final HttpClientBuilder evictIdleConnections(long maxIdleTime, TimeUnit maxIdleTimeUnit) {
    this.evictIdleConnections = true;
    this.maxIdleTime = maxIdleTime;
    this.maxIdleTimeUnit = maxIdleTimeUnit;
    return this;
  }
  
  public final HttpClientBuilder disableDefaultUserAgent() {
    this.defaultUserAgentDisabled = true;
    return this;
  }
  
  protected ClientExecChain createMainExec(HttpRequestExecutor requestExec, HttpClientConnectionManager connManager, ConnectionReuseStrategy reuseStrategy, ConnectionKeepAliveStrategy keepAliveStrategy, HttpProcessor proxyHttpProcessor, AuthenticationStrategy targetAuthStrategy, AuthenticationStrategy proxyAuthStrategy, UserTokenHandler userTokenHandler) {
    return (ClientExecChain)new MainClientExec(requestExec, connManager, reuseStrategy, keepAliveStrategy, proxyHttpProcessor, targetAuthStrategy, proxyAuthStrategy, userTokenHandler);
  }
  
  protected ClientExecChain decorateMainExec(ClientExecChain mainExec) {
    return mainExec;
  }
  
  protected ClientExecChain decorateProtocolExec(ClientExecChain protocolExec) {
    return protocolExec;
  }
  
  protected void addCloseable(Closeable closeable) {
    if (closeable == null)
      return; 
    if (this.closeables == null)
      this.closeables = new ArrayList<Closeable>(); 
    this.closeables.add(closeable);
  }
  
  private static String[] split(String s) {
    if (TextUtils.isBlank(s))
      return null; 
    return s.split(" *, *");
  }
  
  public CloseableHttpClient build() {
    PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;
    DefaultClientConnectionReuseStrategy defaultClientConnectionReuseStrategy;
    RetryExec retryExec;
    ServiceUnavailableRetryExec serviceUnavailableRetryExec;
    RedirectExec redirectExec;
    BackoffStrategyExec backoffStrategyExec;
    DefaultRoutePlanner defaultRoutePlanner;
    Registry registry;
    PublicSuffixMatcher publicSuffixMatcherCopy = this.publicSuffixMatcher;
    if (publicSuffixMatcherCopy == null)
      publicSuffixMatcherCopy = PublicSuffixMatcherLoader.getDefault(); 
    HttpRequestExecutor requestExecCopy = this.requestExec;
    if (requestExecCopy == null)
      requestExecCopy = new HttpRequestExecutor(); 
    HttpClientConnectionManager connManagerCopy = this.connManager;
    if (connManagerCopy == null) {
      SSLConnectionSocketFactory sSLConnectionSocketFactory;
      LayeredConnectionSocketFactory sslSocketFactoryCopy = this.sslSocketFactory;
      if (sslSocketFactoryCopy == null) {
        DefaultHostnameVerifier defaultHostnameVerifier;
        String[] supportedProtocols = this.systemProperties ? split(System.getProperty("https.protocols")) : null;
        String[] supportedCipherSuites = this.systemProperties ? split(System.getProperty("https.cipherSuites")) : null;
        HostnameVerifier hostnameVerifierCopy = this.hostnameVerifier;
        if (hostnameVerifierCopy == null)
          defaultHostnameVerifier = new DefaultHostnameVerifier(publicSuffixMatcherCopy); 
        if (this.sslContext != null) {
          sSLConnectionSocketFactory = new SSLConnectionSocketFactory(this.sslContext, supportedProtocols, supportedCipherSuites, (HostnameVerifier)defaultHostnameVerifier);
        } else if (this.systemProperties) {
          sSLConnectionSocketFactory = new SSLConnectionSocketFactory((SSLSocketFactory)SSLSocketFactory.getDefault(), supportedProtocols, supportedCipherSuites, (HostnameVerifier)defaultHostnameVerifier);
        } else {
          sSLConnectionSocketFactory = new SSLConnectionSocketFactory(SSLContexts.createDefault(), (HostnameVerifier)defaultHostnameVerifier);
        } 
      } 
      PoolingHttpClientConnectionManager poolingmgr = new PoolingHttpClientConnectionManager(RegistryBuilder.create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sSLConnectionSocketFactory).build(), null, null, this.dnsResolver, this.connTimeToLive, (this.connTimeToLiveTimeUnit != null) ? this.connTimeToLiveTimeUnit : TimeUnit.MILLISECONDS);
      if (this.defaultSocketConfig != null)
        poolingmgr.setDefaultSocketConfig(this.defaultSocketConfig); 
      if (this.defaultConnectionConfig != null)
        poolingmgr.setDefaultConnectionConfig(this.defaultConnectionConfig); 
      if (this.systemProperties) {
        String s = System.getProperty("http.keepAlive", "true");
        if ("true".equalsIgnoreCase(s)) {
          s = System.getProperty("http.maxConnections", "5");
          int max = Integer.parseInt(s);
          poolingmgr.setDefaultMaxPerRoute(max);
          poolingmgr.setMaxTotal(2 * max);
        } 
      } 
      if (this.maxConnTotal > 0)
        poolingmgr.setMaxTotal(this.maxConnTotal); 
      if (this.maxConnPerRoute > 0)
        poolingmgr.setDefaultMaxPerRoute(this.maxConnPerRoute); 
      poolingHttpClientConnectionManager = poolingmgr;
    } 
    ConnectionReuseStrategy reuseStrategyCopy = this.reuseStrategy;
    if (reuseStrategyCopy == null)
      if (this.systemProperties) {
        String s = System.getProperty("http.keepAlive", "true");
        if ("true".equalsIgnoreCase(s)) {
          defaultClientConnectionReuseStrategy = DefaultClientConnectionReuseStrategy.INSTANCE;
        } else {
          NoConnectionReuseStrategy noConnectionReuseStrategy = NoConnectionReuseStrategy.INSTANCE;
        } 
      } else {
        defaultClientConnectionReuseStrategy = DefaultClientConnectionReuseStrategy.INSTANCE;
      }  
    ConnectionKeepAliveStrategy keepAliveStrategyCopy = this.keepAliveStrategy;
    if (keepAliveStrategyCopy == null)
      keepAliveStrategyCopy = DefaultConnectionKeepAliveStrategy.INSTANCE; 
    AuthenticationStrategy targetAuthStrategyCopy = this.targetAuthStrategy;
    if (targetAuthStrategyCopy == null)
      targetAuthStrategyCopy = TargetAuthenticationStrategy.INSTANCE; 
    AuthenticationStrategy proxyAuthStrategyCopy = this.proxyAuthStrategy;
    if (proxyAuthStrategyCopy == null)
      proxyAuthStrategyCopy = ProxyAuthenticationStrategy.INSTANCE; 
    UserTokenHandler userTokenHandlerCopy = this.userTokenHandler;
    if (userTokenHandlerCopy == null)
      if (!this.connectionStateDisabled) {
        userTokenHandlerCopy = DefaultUserTokenHandler.INSTANCE;
      } else {
        userTokenHandlerCopy = NoopUserTokenHandler.INSTANCE;
      }  
    String userAgentCopy = this.userAgent;
    if (userAgentCopy == null) {
      if (this.systemProperties)
        userAgentCopy = System.getProperty("http.agent"); 
      if (userAgentCopy == null && !this.defaultUserAgentDisabled)
        userAgentCopy = VersionInfo.getUserAgent("Apache-HttpClient", "org.apache.http.client", getClass()); 
    } 
    ClientExecChain execChain = createMainExec(requestExecCopy, (HttpClientConnectionManager)poolingHttpClientConnectionManager, (ConnectionReuseStrategy)defaultClientConnectionReuseStrategy, keepAliveStrategyCopy, (HttpProcessor)new ImmutableHttpProcessor(new HttpRequestInterceptor[] { (HttpRequestInterceptor)new RequestTargetHost(), (HttpRequestInterceptor)new RequestUserAgent(userAgentCopy) }, ), targetAuthStrategyCopy, proxyAuthStrategyCopy, userTokenHandlerCopy);
    execChain = decorateMainExec(execChain);
    HttpProcessor httpprocessorCopy = this.httpprocessor;
    if (httpprocessorCopy == null) {
      HttpProcessorBuilder b = HttpProcessorBuilder.create();
      if (this.requestFirst != null)
        for (HttpRequestInterceptor i : this.requestFirst)
          b.addFirst(i);  
      if (this.responseFirst != null)
        for (HttpResponseInterceptor i : this.responseFirst)
          b.addFirst(i);  
      b.addAll(new HttpRequestInterceptor[] { (HttpRequestInterceptor)new RequestDefaultHeaders(this.defaultHeaders), (HttpRequestInterceptor)new RequestContent(), (HttpRequestInterceptor)new RequestTargetHost(), (HttpRequestInterceptor)new RequestClientConnControl(), (HttpRequestInterceptor)new RequestUserAgent(userAgentCopy), (HttpRequestInterceptor)new RequestExpectContinue() });
      if (!this.cookieManagementDisabled)
        b.add((HttpRequestInterceptor)new RequestAddCookies()); 
      if (!this.contentCompressionDisabled)
        if (this.contentDecoderMap != null) {
          List<String> encodings = new ArrayList<String>(this.contentDecoderMap.keySet());
          Collections.sort(encodings);
          b.add((HttpRequestInterceptor)new RequestAcceptEncoding(encodings));
        } else {
          b.add((HttpRequestInterceptor)new RequestAcceptEncoding());
        }  
      if (!this.authCachingDisabled)
        b.add((HttpRequestInterceptor)new RequestAuthCache()); 
      if (!this.cookieManagementDisabled)
        b.add((HttpResponseInterceptor)new ResponseProcessCookies()); 
      if (!this.contentCompressionDisabled)
        if (this.contentDecoderMap != null) {
          RegistryBuilder<InputStreamFactory> b2 = RegistryBuilder.create();
          for (Map.Entry<String, InputStreamFactory> entry : this.contentDecoderMap.entrySet())
            b2.register(entry.getKey(), entry.getValue()); 
          b.add((HttpResponseInterceptor)new ResponseContentEncoding((Lookup)b2.build()));
        } else {
          b.add((HttpResponseInterceptor)new ResponseContentEncoding());
        }  
      if (this.requestLast != null)
        for (HttpRequestInterceptor i : this.requestLast)
          b.addLast(i);  
      if (this.responseLast != null)
        for (HttpResponseInterceptor i : this.responseLast)
          b.addLast(i);  
      httpprocessorCopy = b.build();
    } 
    ProtocolExec protocolExec = new ProtocolExec(execChain, httpprocessorCopy);
    ClientExecChain clientExecChain1 = decorateProtocolExec((ClientExecChain)protocolExec);
    if (!this.automaticRetriesDisabled) {
      HttpRequestRetryHandler retryHandlerCopy = this.retryHandler;
      if (retryHandlerCopy == null)
        retryHandlerCopy = DefaultHttpRequestRetryHandler.INSTANCE; 
      retryExec = new RetryExec(clientExecChain1, retryHandlerCopy);
    } 
    HttpRoutePlanner routePlannerCopy = this.routePlanner;
    if (routePlannerCopy == null) {
      DefaultSchemePortResolver defaultSchemePortResolver;
      SchemePortResolver schemePortResolverCopy = this.schemePortResolver;
      if (schemePortResolverCopy == null)
        defaultSchemePortResolver = DefaultSchemePortResolver.INSTANCE; 
      if (this.proxy != null) {
        DefaultProxyRoutePlanner defaultProxyRoutePlanner = new DefaultProxyRoutePlanner(this.proxy, (SchemePortResolver)defaultSchemePortResolver);
      } else if (this.systemProperties) {
        SystemDefaultRoutePlanner systemDefaultRoutePlanner = new SystemDefaultRoutePlanner((SchemePortResolver)defaultSchemePortResolver, ProxySelector.getDefault());
      } else {
        defaultRoutePlanner = new DefaultRoutePlanner((SchemePortResolver)defaultSchemePortResolver);
      } 
    } 
    ServiceUnavailableRetryStrategy serviceUnavailStrategyCopy = this.serviceUnavailStrategy;
    if (serviceUnavailStrategyCopy != null)
      serviceUnavailableRetryExec = new ServiceUnavailableRetryExec((ClientExecChain)retryExec, serviceUnavailStrategyCopy); 
    if (!this.redirectHandlingDisabled) {
      RedirectStrategy redirectStrategyCopy = this.redirectStrategy;
      if (redirectStrategyCopy == null)
        redirectStrategyCopy = DefaultRedirectStrategy.INSTANCE; 
      redirectExec = new RedirectExec((ClientExecChain)serviceUnavailableRetryExec, (HttpRoutePlanner)defaultRoutePlanner, redirectStrategyCopy);
    } 
    if (this.backoffManager != null && this.connectionBackoffStrategy != null)
      backoffStrategyExec = new BackoffStrategyExec((ClientExecChain)redirectExec, this.connectionBackoffStrategy, this.backoffManager); 
    Lookup<AuthSchemeProvider> authSchemeRegistryCopy = this.authSchemeRegistry;
    if (authSchemeRegistryCopy == null)
      registry = RegistryBuilder.create().register("Basic", new BasicSchemeFactory()).register("Digest", new DigestSchemeFactory()).register("NTLM", new NTLMSchemeFactory()).register("Negotiate", new SPNegoSchemeFactory()).register("Kerberos", new KerberosSchemeFactory()).build(); 
    Lookup<CookieSpecProvider> cookieSpecRegistryCopy = this.cookieSpecRegistry;
    if (cookieSpecRegistryCopy == null)
      cookieSpecRegistryCopy = CookieSpecRegistries.createDefault(publicSuffixMatcherCopy); 
    CookieStore defaultCookieStore = this.cookieStore;
    if (defaultCookieStore == null)
      defaultCookieStore = new BasicCookieStore(); 
    CredentialsProvider defaultCredentialsProvider = this.credentialsProvider;
    if (defaultCredentialsProvider == null)
      if (this.systemProperties) {
        defaultCredentialsProvider = new SystemDefaultCredentialsProvider();
      } else {
        defaultCredentialsProvider = new BasicCredentialsProvider();
      }  
    List<Closeable> closeablesCopy = (this.closeables != null) ? new ArrayList<Closeable>(this.closeables) : null;
    if (!this.connManagerShared) {
      if (closeablesCopy == null)
        closeablesCopy = new ArrayList<Closeable>(1); 
      final PoolingHttpClientConnectionManager cm = poolingHttpClientConnectionManager;
      if (this.evictExpiredConnections || this.evictIdleConnections) {
        final IdleConnectionEvictor connectionEvictor = new IdleConnectionEvictor((HttpClientConnectionManager)poolingHttpClientConnectionManager1, (this.maxIdleTime > 0L) ? this.maxIdleTime : 10L, (this.maxIdleTimeUnit != null) ? this.maxIdleTimeUnit : TimeUnit.SECONDS, this.maxIdleTime, this.maxIdleTimeUnit);
        closeablesCopy.add(new Closeable() {
              public void close() throws IOException {
                connectionEvictor.shutdown();
                try {
                  connectionEvictor.awaitTermination(1L, TimeUnit.SECONDS);
                } catch (InterruptedException interrupted) {
                  Thread.currentThread().interrupt();
                } 
              }
            });
        connectionEvictor.start();
      } 
      closeablesCopy.add(new Closeable() {
            public void close() throws IOException {
              cm.shutdown();
            }
          });
    } 
    return new InternalHttpClient((ClientExecChain)backoffStrategyExec, (HttpClientConnectionManager)poolingHttpClientConnectionManager, (HttpRoutePlanner)defaultRoutePlanner, cookieSpecRegistryCopy, (Lookup<AuthSchemeProvider>)registry, defaultCookieStore, defaultCredentialsProvider, (this.defaultRequestConfig != null) ? this.defaultRequestConfig : RequestConfig.DEFAULT, closeablesCopy);
  }
}
