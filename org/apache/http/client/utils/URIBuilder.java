package org.apache.http.client.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.TextUtils;

public class URIBuilder {
  private String scheme;
  
  private String encodedSchemeSpecificPart;
  
  private String encodedAuthority;
  
  private String userInfo;
  
  private String encodedUserInfo;
  
  private String host;
  
  private int port;
  
  private String encodedPath;
  
  private List<String> pathSegments;
  
  private String encodedQuery;
  
  private List<NameValuePair> queryParams;
  
  private String query;
  
  private Charset charset;
  
  private String fragment;
  
  private String encodedFragment;
  
  public URIBuilder() {
    this.port = -1;
  }
  
  public URIBuilder(String string) throws URISyntaxException {
    this(new URI(string), (Charset)null);
  }
  
  public URIBuilder(URI uri) {
    this(uri, (Charset)null);
  }
  
  public URIBuilder(String string, Charset charset) throws URISyntaxException {
    this(new URI(string), charset);
  }
  
  public URIBuilder(URI uri, Charset charset) {
    setCharset(charset);
    digestURI(uri);
  }
  
  public URIBuilder setCharset(Charset charset) {
    this.charset = charset;
    return this;
  }
  
  public Charset getCharset() {
    return this.charset;
  }
  
  private List<NameValuePair> parseQuery(String query, Charset charset) {
    if (query != null && !query.isEmpty())
      return URLEncodedUtils.parse(query, charset); 
    return null;
  }
  
  private List<String> parsePath(String path, Charset charset) {
    if (path != null && !path.isEmpty())
      return URLEncodedUtils.parsePathSegments(path, charset); 
    return null;
  }
  
  public URI build() throws URISyntaxException {
    return new URI(buildString());
  }
  
  private String buildString() {
    StringBuilder sb = new StringBuilder();
    if (this.scheme != null)
      sb.append(this.scheme).append(':'); 
    if (this.encodedSchemeSpecificPart != null) {
      sb.append(this.encodedSchemeSpecificPart);
    } else {
      if (this.encodedAuthority != null) {
        sb.append("//").append(this.encodedAuthority);
      } else if (this.host != null) {
        sb.append("//");
        if (this.encodedUserInfo != null) {
          sb.append(this.encodedUserInfo).append("@");
        } else if (this.userInfo != null) {
          sb.append(encodeUserInfo(this.userInfo)).append("@");
        } 
        if (InetAddressUtils.isIPv6Address(this.host)) {
          sb.append("[").append(this.host).append("]");
        } else {
          sb.append(this.host);
        } 
        if (this.port >= 0)
          sb.append(":").append(this.port); 
      } 
      if (this.encodedPath != null) {
        sb.append(normalizePath(this.encodedPath, (sb.length() == 0)));
      } else if (this.pathSegments != null) {
        sb.append(encodePath(this.pathSegments));
      } 
      if (this.encodedQuery != null) {
        sb.append("?").append(this.encodedQuery);
      } else if (this.queryParams != null && !this.queryParams.isEmpty()) {
        sb.append("?").append(encodeUrlForm(this.queryParams));
      } else if (this.query != null) {
        sb.append("?").append(encodeUric(this.query));
      } 
    } 
    if (this.encodedFragment != null) {
      sb.append("#").append(this.encodedFragment);
    } else if (this.fragment != null) {
      sb.append("#").append(encodeUric(this.fragment));
    } 
    return sb.toString();
  }
  
  private static String normalizePath(String path, boolean relative) {
    String s = path;
    if (TextUtils.isBlank(s))
      return ""; 
    if (!relative && !s.startsWith("/"))
      s = "/" + s; 
    return s;
  }
  
  private void digestURI(URI uri) {
    this.scheme = uri.getScheme();
    this.encodedSchemeSpecificPart = uri.getRawSchemeSpecificPart();
    this.encodedAuthority = uri.getRawAuthority();
    this.host = uri.getHost();
    this.port = uri.getPort();
    this.encodedUserInfo = uri.getRawUserInfo();
    this.userInfo = uri.getUserInfo();
    this.encodedPath = uri.getRawPath();
    this.pathSegments = parsePath(uri.getRawPath(), (this.charset != null) ? this.charset : Consts.UTF_8);
    this.encodedQuery = uri.getRawQuery();
    this.queryParams = parseQuery(uri.getRawQuery(), (this.charset != null) ? this.charset : Consts.UTF_8);
    this.encodedFragment = uri.getRawFragment();
    this.fragment = uri.getFragment();
  }
  
  private String encodeUserInfo(String userInfo) {
    return URLEncodedUtils.encUserInfo(userInfo, (this.charset != null) ? this.charset : Consts.UTF_8);
  }
  
  private String encodePath(List<String> pathSegments) {
    return URLEncodedUtils.formatSegments(pathSegments, (this.charset != null) ? this.charset : Consts.UTF_8);
  }
  
  private String encodeUrlForm(List<NameValuePair> params) {
    return URLEncodedUtils.format(params, (this.charset != null) ? this.charset : Consts.UTF_8);
  }
  
  private String encodeUric(String fragment) {
    return URLEncodedUtils.encUric(fragment, (this.charset != null) ? this.charset : Consts.UTF_8);
  }
  
  public URIBuilder setScheme(String scheme) {
    this.scheme = scheme;
    return this;
  }
  
  public URIBuilder setUserInfo(String userInfo) {
    this.userInfo = userInfo;
    this.encodedSchemeSpecificPart = null;
    this.encodedAuthority = null;
    this.encodedUserInfo = null;
    return this;
  }
  
  public URIBuilder setUserInfo(String username, String password) {
    return setUserInfo(username + ':' + password);
  }
  
  public URIBuilder setHost(String host) {
    this.host = host;
    this.encodedSchemeSpecificPart = null;
    this.encodedAuthority = null;
    return this;
  }
  
  public URIBuilder setPort(int port) {
    this.port = (port < 0) ? -1 : port;
    this.encodedSchemeSpecificPart = null;
    this.encodedAuthority = null;
    return this;
  }
  
  public URIBuilder setPath(String path) {
    return setPathSegments((path != null) ? URLEncodedUtils.splitPathSegments(path) : null);
  }
  
  public URIBuilder setPathSegments(String... pathSegments) {
    this.pathSegments = (pathSegments.length > 0) ? Arrays.<String>asList(pathSegments) : null;
    this.encodedSchemeSpecificPart = null;
    this.encodedPath = null;
    return this;
  }
  
  public URIBuilder setPathSegments(List<String> pathSegments) {
    this.pathSegments = (pathSegments != null && pathSegments.size() > 0) ? new ArrayList<String>(pathSegments) : null;
    this.encodedSchemeSpecificPart = null;
    this.encodedPath = null;
    return this;
  }
  
  public URIBuilder removeQuery() {
    this.queryParams = null;
    this.query = null;
    this.encodedQuery = null;
    this.encodedSchemeSpecificPart = null;
    return this;
  }
  
  @Deprecated
  public URIBuilder setQuery(String query) {
    this.queryParams = parseQuery(query, (this.charset != null) ? this.charset : Consts.UTF_8);
    this.query = null;
    this.encodedQuery = null;
    this.encodedSchemeSpecificPart = null;
    return this;
  }
  
  public URIBuilder setParameters(List<NameValuePair> nvps) {
    if (this.queryParams == null) {
      this.queryParams = new ArrayList<NameValuePair>();
    } else {
      this.queryParams.clear();
    } 
    this.queryParams.addAll(nvps);
    this.encodedQuery = null;
    this.encodedSchemeSpecificPart = null;
    this.query = null;
    return this;
  }
  
  public URIBuilder addParameters(List<NameValuePair> nvps) {
    if (this.queryParams == null)
      this.queryParams = new ArrayList<NameValuePair>(); 
    this.queryParams.addAll(nvps);
    this.encodedQuery = null;
    this.encodedSchemeSpecificPart = null;
    this.query = null;
    return this;
  }
  
  public URIBuilder setParameters(NameValuePair... nvps) {
    if (this.queryParams == null) {
      this.queryParams = new ArrayList<NameValuePair>();
    } else {
      this.queryParams.clear();
    } 
    for (NameValuePair nvp : nvps)
      this.queryParams.add(nvp); 
    this.encodedQuery = null;
    this.encodedSchemeSpecificPart = null;
    this.query = null;
    return this;
  }
  
  public URIBuilder addParameter(String param, String value) {
    if (this.queryParams == null)
      this.queryParams = new ArrayList<NameValuePair>(); 
    this.queryParams.add(new BasicNameValuePair(param, value));
    this.encodedQuery = null;
    this.encodedSchemeSpecificPart = null;
    this.query = null;
    return this;
  }
  
  public URIBuilder setParameter(String param, String value) {
    if (this.queryParams == null)
      this.queryParams = new ArrayList<NameValuePair>(); 
    if (!this.queryParams.isEmpty())
      for (Iterator<NameValuePair> it = this.queryParams.iterator(); it.hasNext(); ) {
        NameValuePair nvp = it.next();
        if (nvp.getName().equals(param))
          it.remove(); 
      }  
    this.queryParams.add(new BasicNameValuePair(param, value));
    this.encodedQuery = null;
    this.encodedSchemeSpecificPart = null;
    this.query = null;
    return this;
  }
  
  public URIBuilder clearParameters() {
    this.queryParams = null;
    this.encodedQuery = null;
    this.encodedSchemeSpecificPart = null;
    return this;
  }
  
  public URIBuilder setCustomQuery(String query) {
    this.query = query;
    this.encodedQuery = null;
    this.encodedSchemeSpecificPart = null;
    this.queryParams = null;
    return this;
  }
  
  public URIBuilder setFragment(String fragment) {
    this.fragment = fragment;
    this.encodedFragment = null;
    return this;
  }
  
  public boolean isAbsolute() {
    return (this.scheme != null);
  }
  
  public boolean isOpaque() {
    return (this.pathSegments == null && this.encodedPath == null);
  }
  
  public String getScheme() {
    return this.scheme;
  }
  
  public String getUserInfo() {
    return this.userInfo;
  }
  
  public String getHost() {
    return this.host;
  }
  
  public int getPort() {
    return this.port;
  }
  
  public boolean isPathEmpty() {
    return ((this.pathSegments == null || this.pathSegments.isEmpty()) && (this.encodedPath == null || this.encodedPath.isEmpty()));
  }
  
  public List<String> getPathSegments() {
    return (this.pathSegments != null) ? new ArrayList<String>(this.pathSegments) : Collections.<String>emptyList();
  }
  
  public String getPath() {
    if (this.pathSegments == null)
      return null; 
    StringBuilder result = new StringBuilder();
    for (String segment : this.pathSegments)
      result.append('/').append(segment); 
    return result.toString();
  }
  
  public boolean isQueryEmpty() {
    return ((this.queryParams == null || this.queryParams.isEmpty()) && this.encodedQuery == null);
  }
  
  public List<NameValuePair> getQueryParams() {
    return (this.queryParams != null) ? new ArrayList<NameValuePair>(this.queryParams) : Collections.<NameValuePair>emptyList();
  }
  
  public String getFragment() {
    return this.fragment;
  }
  
  public String toString() {
    return buildString();
  }
}
