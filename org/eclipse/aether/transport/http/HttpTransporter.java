package org.eclipse.aether.transport.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.config.Lookup;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.AuthenticationContext;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.transport.AbstractTransporter;
import org.eclipse.aether.spi.connector.transport.GetTask;
import org.eclipse.aether.spi.connector.transport.PeekTask;
import org.eclipse.aether.spi.connector.transport.PutTask;
import org.eclipse.aether.spi.connector.transport.TransportTask;
import org.eclipse.aether.transfer.NoTransporterException;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.util.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class HttpTransporter extends AbstractTransporter {
  private static final Pattern CONTENT_RANGE_PATTERN = Pattern.compile("\\s*bytes\\s+([0-9]+)\\s*-\\s*([0-9]+)\\s*/.*");
  
  private static final Logger LOGGER = LoggerFactory.getLogger(HttpTransporter.class);
  
  private final AuthenticationContext repoAuthContext;
  
  private final AuthenticationContext proxyAuthContext;
  
  private final URI baseUri;
  
  private final HttpHost server;
  
  private final HttpHost proxy;
  
  private final CloseableHttpClient client;
  
  private final Map<?, ?> headers;
  
  private final LocalState state;
  
  HttpTransporter(RemoteRepository repository, RepositorySystemSession session) throws NoTransporterException {
    if (!"http".equalsIgnoreCase(repository.getProtocol()) && 
      !"https".equalsIgnoreCase(repository.getProtocol()))
      throw new NoTransporterException(repository); 
    try {
      this.baseUri = (new URI(repository.getUrl())).parseServerAuthority();
      if (this.baseUri.isOpaque())
        throw new URISyntaxException(repository.getUrl(), "URL must not be opaque"); 
      this.server = URIUtils.extractHost(this.baseUri);
      if (this.server == null)
        throw new URISyntaxException(repository.getUrl(), "URL lacks host name"); 
    } catch (URISyntaxException e) {
      throw new NoTransporterException(repository, e.getMessage(), e);
    } 
    this.proxy = toHost(repository.getProxy());
    this.repoAuthContext = AuthenticationContext.forRepository(session, repository);
    this.proxyAuthContext = AuthenticationContext.forProxy(session, repository);
    this.state = new LocalState(session, repository, new SslConfig(session, this.repoAuthContext));
    this.headers = ConfigUtils.getMap(session, Collections.emptyMap(), new String[] { "aether.connector.http.headers." + repository
          .getId(), "aether.connector.http.headers" });
    String credentialEncoding = ConfigUtils.getString(session, "ISO-8859-1", new String[] { "aether.connector.http.credentialEncoding." + repository
          
          .getId(), "aether.connector.http.credentialEncoding" });
    int connectTimeout = ConfigUtils.getInteger(session, 10000, new String[] { "aether.connector.connectTimeout." + repository
          
          .getId(), "aether.connector.connectTimeout" });
    int requestTimeout = ConfigUtils.getInteger(session, 1800000, new String[] { "aether.connector.requestTimeout." + repository
          
          .getId(), "aether.connector.requestTimeout" });
    String userAgent = ConfigUtils.getString(session, "Aether", new String[] { "aether.connector.userAgent" });
    Charset credentialsCharset = Charset.forName(credentialEncoding);
    Registry<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.create().register("Basic", new BasicSchemeFactory(credentialsCharset)).register("Digest", new DigestSchemeFactory(credentialsCharset)).register("NTLM", new NTLMSchemeFactory()).register("Negotiate", new SPNegoSchemeFactory()).register("Kerberos", new KerberosSchemeFactory()).build();
    SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(requestTimeout).build();
    RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).setSocketTimeout(requestTimeout).build();
    this
      
      .client = HttpClientBuilder.create().setUserAgent(userAgent).setDefaultSocketConfig(socketConfig).setDefaultRequestConfig(requestConfig).setDefaultAuthSchemeRegistry((Lookup)authSchemeRegistry).setConnectionManager(this.state.getConnectionManager()).setConnectionManagerShared(true).setDefaultCredentialsProvider(toCredentialsProvider(this.server, this.repoAuthContext, this.proxy, this.proxyAuthContext)).setProxy(this.proxy).build();
  }
  
  private static HttpHost toHost(Proxy proxy) {
    HttpHost host = null;
    if (proxy != null)
      host = new HttpHost(proxy.getHost(), proxy.getPort()); 
    return host;
  }
  
  private static CredentialsProvider toCredentialsProvider(HttpHost server, AuthenticationContext serverAuthCtx, HttpHost proxy, AuthenticationContext proxyAuthCtx) {
    CredentialsProvider provider = toCredentialsProvider(server.getHostName(), -1, serverAuthCtx);
    if (proxy != null) {
      CredentialsProvider p = toCredentialsProvider(proxy.getHostName(), proxy.getPort(), proxyAuthCtx);
      provider = new DemuxCredentialsProvider(provider, p, proxy);
    } 
    return provider;
  }
  
  private static CredentialsProvider toCredentialsProvider(String host, int port, AuthenticationContext ctx) {
    DeferredCredentialsProvider provider = new DeferredCredentialsProvider();
    if (ctx != null) {
      AuthScope basicScope = new AuthScope(host, port);
      provider.setCredentials(basicScope, new DeferredCredentialsProvider.BasicFactory(ctx));
      AuthScope ntlmScope = new AuthScope(host, port, AuthScope.ANY_REALM, "ntlm");
      provider.setCredentials(ntlmScope, new DeferredCredentialsProvider.NtlmFactory(ctx));
    } 
    return provider;
  }
  
  LocalState getState() {
    return this.state;
  }
  
  private URI resolve(TransportTask task) {
    return UriUtils.resolve(this.baseUri, task.getLocation());
  }
  
  public int classify(Throwable error) {
    if (error instanceof HttpResponseException && ((HttpResponseException)error)
      .getStatusCode() == 404)
      return 1; 
    return 0;
  }
  
  protected void implPeek(PeekTask task) throws Exception {
    HttpHead request = commonHeaders(new HttpHead(resolve((TransportTask)task)));
    execute((HttpUriRequest)request, (EntityGetter)null);
  }
  
  protected void implGet(GetTask task) throws Exception {
    EntityGetter getter = new EntityGetter(task);
    HttpGet request = commonHeaders(new HttpGet(resolve((TransportTask)task)));
    resume(request, task);
    try {
      execute((HttpUriRequest)request, getter);
    } catch (HttpResponseException e) {
      if (e.getStatusCode() == 412 && request.containsHeader("Range")) {
        request = commonHeaders(new HttpGet(request.getURI()));
        execute((HttpUriRequest)request, getter);
        return;
      } 
      throw e;
    } 
  }
  
  protected void implPut(PutTask task) throws Exception {
    PutTaskEntity entity = new PutTaskEntity(task);
    HttpPut request = (HttpPut)commonHeaders(entity(new HttpPut(resolve((TransportTask)task)), (HttpEntity)entity));
    try {
      execute((HttpUriRequest)request, (EntityGetter)null);
    } catch (HttpResponseException e) {
      if (e.getStatusCode() == 417 && request.containsHeader("Expect")) {
        this.state.setExpectContinue(false);
        request = (HttpPut)commonHeaders(entity(new HttpPut(request.getURI()), (HttpEntity)entity));
        execute((HttpUriRequest)request, (EntityGetter)null);
        return;
      } 
      throw e;
    } 
  }
  
  private void execute(HttpUriRequest request, EntityGetter getter) throws Exception {
    try {
      SharingHttpContext context = new SharingHttpContext(this.state);
      prepare(request, context);
      CloseableHttpResponse closeableHttpResponse = this.client.execute(this.server, (HttpRequest)request, (HttpContext)context);
      try {
        context.close();
        handleStatus((HttpResponse)closeableHttpResponse);
        if (getter != null)
          getter.handle((HttpResponse)closeableHttpResponse); 
      } finally {
        EntityUtils.consumeQuietly(closeableHttpResponse.getEntity());
      } 
    } catch (IOException e) {
      if (e.getCause() instanceof TransferCancelledException)
        throw (Exception)e.getCause(); 
      throw e;
    } 
  }
  
  private void prepare(HttpUriRequest request, SharingHttpContext context) {
    boolean put = "PUT".equalsIgnoreCase(request.getMethod());
    if (this.state.getWebDav() == null && (put || isPayloadPresent(request)))
      try {
        HttpOptions req = commonHeaders(new HttpOptions(request.getURI()));
        CloseableHttpResponse closeableHttpResponse = this.client.execute(this.server, (HttpRequest)req, (HttpContext)context);
        this.state.setWebDav(isWebDav((HttpResponse)closeableHttpResponse));
        EntityUtils.consumeQuietly(closeableHttpResponse.getEntity());
      } catch (IOException e) {
        LOGGER.debug("Failed to prepare HTTP context", e);
      }  
    if (put && Boolean.TRUE.equals(this.state.getWebDav()))
      mkdirs(request.getURI(), context); 
  }
  
  private boolean isWebDav(HttpResponse response) {
    return response.containsHeader("Dav");
  }
  
  private void mkdirs(URI uri, SharingHttpContext context) {
    List<URI> dirs = UriUtils.getDirectories(this.baseUri, uri);
    int index = 0;
    for (; index < dirs.size(); index++) {
      try {
        CloseableHttpResponse closeableHttpResponse = this.client.execute(this.server, (HttpRequest)commonHeaders(new HttpMkCol(dirs.get(index))), (HttpContext)context);
      } catch (IOException e) {
        LOGGER.debug("Failed to create parent directory {}", dirs.get(index), e);
        return;
      } 
    } 
    for (; --index >= 0; index--) {
      try {
        CloseableHttpResponse closeableHttpResponse = this.client.execute(this.server, (HttpRequest)commonHeaders(new HttpMkCol(dirs.get(index))), (HttpContext)context);
      } catch (IOException e) {
        LOGGER.debug("Failed to create parent directory {}", dirs.get(index), e);
        return;
      } 
    } 
  }
  
  private <T extends HttpEntityEnclosingRequest> T entity(T request, HttpEntity entity) {
    request.setEntity(entity);
    return request;
  }
  
  private boolean isPayloadPresent(HttpUriRequest request) {
    if (request instanceof HttpEntityEnclosingRequest) {
      HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
      return (entity != null && entity.getContentLength() != 0L);
    } 
    return false;
  }
  
  private <T extends HttpUriRequest> T commonHeaders(T request) {
    request.setHeader("Cache-Control", "no-cache, no-store");
    request.setHeader("Pragma", "no-cache");
    if (this.state.isExpectContinue() && isPayloadPresent((HttpUriRequest)request))
      request.setHeader("Expect", "100-continue"); 
    for (Map.Entry<?, ?> entry : this.headers.entrySet()) {
      if (!(entry.getKey() instanceof String))
        continue; 
      if (entry.getValue() instanceof String) {
        request.setHeader(entry.getKey().toString(), entry.getValue().toString());
        continue;
      } 
      request.removeHeaders(entry.getKey().toString());
    } 
    if (!this.state.isExpectContinue())
      request.removeHeaders("Expect"); 
    return request;
  }
  
  private <T extends HttpUriRequest> T resume(T request, GetTask task) {
    long resumeOffset = task.getResumeOffset();
    if (resumeOffset > 0L && task.getDataFile() != null) {
      request.setHeader("Range", "bytes=" + resumeOffset + '-');
      request.setHeader("If-Unmodified-Since", 
          DateUtils.formatDate(new Date(task.getDataFile().lastModified() - 60000L)));
      request.setHeader("Accept-Encoding", "identity");
    } 
    return request;
  }
  
  private void handleStatus(HttpResponse response) throws HttpResponseException {
    int status = response.getStatusLine().getStatusCode();
    if (status >= 300)
      throw new HttpResponseException(status, response.getStatusLine().getReasonPhrase() + " (" + status + ")"); 
  }
  
  protected void implClose() {
    try {
      this.client.close();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } 
    AuthenticationContext.close(this.repoAuthContext);
    AuthenticationContext.close(this.proxyAuthContext);
    this.state.close();
  }
  
  private class EntityGetter {
    private final GetTask task;
    
    EntityGetter(GetTask task) {
      this.task = task;
    }
    
    public void handle(HttpResponse response) throws IOException, TransferCancelledException {
      ByteArrayEntity byteArrayEntity;
      HttpEntity entity = response.getEntity();
      if (entity == null)
        byteArrayEntity = new ByteArrayEntity(new byte[0]); 
      long offset = 0L, length = byteArrayEntity.getContentLength();
      String range = getHeader(response, "Content-Range");
      if (range != null) {
        Matcher m = HttpTransporter.CONTENT_RANGE_PATTERN.matcher(range);
        if (!m.matches())
          throw new IOException("Invalid Content-Range header for partial download: " + range); 
        offset = Long.parseLong(m.group(1));
        length = Long.parseLong(m.group(2)) + 1L;
        if (offset < 0L || offset >= length || (offset > 0L && offset != this.task.getResumeOffset()))
          throw new IOException("Invalid Content-Range header for partial download from offset " + this.task
              .getResumeOffset() + ": " + range); 
      } 
      InputStream is = byteArrayEntity.getContent();
      HttpTransporter.this.utilGet(this.task, is, true, length, (offset > 0L));
      extractChecksums(response);
    }
    
    private void extractChecksums(HttpResponse response) {
      String etag = getHeader(response, "ETag");
      if (etag != null) {
        int start = etag.indexOf("SHA1{"), end = etag.indexOf("}", start + 5);
        if (start >= 0 && end > start)
          this.task.setChecksum("SHA-1", etag.substring(start + 5, end)); 
      } 
    }
    
    private String getHeader(HttpResponse response, String name) {
      Header header = response.getFirstHeader(name);
      return (header != null) ? header.getValue() : null;
    }
  }
  
  private class PutTaskEntity extends AbstractHttpEntity {
    private final PutTask task;
    
    PutTaskEntity(PutTask task) {
      this.task = task;
    }
    
    public boolean isRepeatable() {
      return true;
    }
    
    public boolean isStreaming() {
      return false;
    }
    
    public long getContentLength() {
      return this.task.getDataLength();
    }
    
    public InputStream getContent() throws IOException {
      return this.task.newInputStream();
    }
    
    public void writeTo(OutputStream os) throws IOException {
      try {
        HttpTransporter.this.utilPut(this.task, os, false);
      } catch (TransferCancelledException e) {
        throw (IOException)(new InterruptedIOException()).initCause(e);
      } 
    }
  }
}
