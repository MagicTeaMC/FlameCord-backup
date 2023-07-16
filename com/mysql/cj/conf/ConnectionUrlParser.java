package com.mysql.cj.conf;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.UnsupportedConnectionStringException;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.util.SearchMode;
import com.mysql.cj.util.StringUtils;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectionUrlParser implements DatabaseUrlContainer {
  private static final String DUMMY_SCHEMA = "cj://";
  
  private static final String USER_PASS_SEPARATOR = ":";
  
  private static final String USER_HOST_SEPARATOR = "@";
  
  private static final String HOSTS_SEPARATOR = ",";
  
  private static final String KEY_VALUE_HOST_INFO_OPENING_MARKER = "(";
  
  private static final String KEY_VALUE_HOST_INFO_CLOSING_MARKER = ")";
  
  private static final String HOSTS_LIST_OPENING_MARKERS = "[(";
  
  private static final String HOSTS_LIST_CLOSING_MARKERS = "])";
  
  private static final String ADDRESS_EQUALS_HOST_INFO_PREFIX = "ADDRESS=";
  
  private static final Pattern CONNECTION_STRING_PTRN = Pattern.compile("(?<scheme>[\\w\\+:%]+)\\s*(?://(?<authority>[^/?#]*))?\\s*(?:/(?!\\s*/)(?<path>[^?#]*))?(?:\\?(?!\\s*\\?)(?<query>[^#]*))?(?:\\s*#(?<fragment>.*))?");
  
  private static final Pattern SCHEME_PTRN = Pattern.compile("(?<scheme>[\\w\\+:%]+).*");
  
  private static final Pattern HOST_LIST_PTRN = Pattern.compile("^\\[(?<hosts>.*)\\]$");
  
  private static final Pattern GENERIC_HOST_PTRN = Pattern.compile("^(?<host>.*?)(?::(?<port>[^:]*))?$");
  
  private static final Pattern KEY_VALUE_HOST_PTRN = Pattern.compile("[,\\s]*(?<key>[\\w\\.\\-\\s%]*)(?:=(?<value>[^,]*))?");
  
  private static final Pattern ADDRESS_EQUALS_HOST_PTRN = Pattern.compile("\\s*\\(\\s*(?<key>[\\w\\.\\-%]+)?\\s*(?:=(?<value>[^)]*))?\\)\\s*");
  
  private static final Pattern PROPERTIES_PTRN = Pattern.compile("[&\\s]*(?<key>[\\w\\.\\-\\s%]*)(?:=(?<value>[^&]*))?");
  
  private final String baseConnectionString;
  
  private String scheme;
  
  private String authority;
  
  private String path;
  
  private String query;
  
  private List<HostInfo> parsedHosts = null;
  
  private Map<String, String> parsedProperties = null;
  
  public static ConnectionUrlParser parseConnectionString(String connString) {
    return new ConnectionUrlParser(connString);
  }
  
  private ConnectionUrlParser(String connString) {
    if (connString == null)
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ConnectionString.0")); 
    if (!isConnectionStringSupported(connString))
      throw (UnsupportedConnectionStringException)ExceptionFactory.createException(UnsupportedConnectionStringException.class, 
          Messages.getString("ConnectionString.17", new String[] { connString })); 
    this.baseConnectionString = connString;
    parseConnectionString();
  }
  
  public static boolean isConnectionStringSupported(String connString) {
    if (connString == null)
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ConnectionString.0")); 
    Matcher matcher = SCHEME_PTRN.matcher(connString);
    return (matcher.matches() && ConnectionUrl.Type.isSupported(decodeSkippingPlusSign(matcher.group("scheme"))));
  }
  
  private void parseConnectionString() {
    String connString = this.baseConnectionString;
    Matcher matcher = CONNECTION_STRING_PTRN.matcher(connString);
    if (!matcher.matches())
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ConnectionString.1")); 
    this.scheme = decodeSkippingPlusSign(matcher.group("scheme"));
    this.authority = matcher.group("authority");
    this.path = (matcher.group("path") == null) ? null : decode(matcher.group("path")).trim();
    this.query = matcher.group("query");
  }
  
  private void parseAuthoritySection() {
    if (StringUtils.isNullOrEmpty(this.authority)) {
      this.parsedHosts.add(new HostInfo());
      return;
    } 
    List<String> authoritySegments = StringUtils.split(this.authority, ",", "[(", "])", true, SearchMode.__MRK_WS);
    for (String hi : authoritySegments)
      parseAuthoritySegment(hi); 
  }
  
  private void parseAuthoritySegment(String authSegment) {
    Pair<String, String> userHostInfoSplit = splitByUserInfoAndHostInfo(authSegment);
    String userInfo = StringUtils.safeTrim((String)userHostInfoSplit.left);
    String user = null;
    String password = null;
    if (!StringUtils.isNullOrEmpty(userInfo)) {
      Pair<String, String> userInfoPair = parseUserInfo(userInfo);
      user = decode(StringUtils.safeTrim((String)userInfoPair.left));
      password = decode(StringUtils.safeTrim((String)userInfoPair.right));
    } 
    String hostInfo = StringUtils.safeTrim((String)userHostInfoSplit.right);
    HostInfo hi = buildHostInfoForEmptyHost(user, password, hostInfo);
    if (hi != null) {
      this.parsedHosts.add(hi);
      return;
    } 
    hi = buildHostInfoResortingToUriParser(user, password, authSegment);
    if (hi != null) {
      this.parsedHosts.add(hi);
      return;
    } 
    List<HostInfo> hiList = buildHostInfoResortingToSubHostsListParser(user, password, hostInfo);
    if (hiList != null) {
      this.parsedHosts.addAll(hiList);
      return;
    } 
    hi = buildHostInfoResortingToKeyValueSyntaxParser(user, password, hostInfo);
    if (hi != null) {
      this.parsedHosts.add(hi);
      return;
    } 
    hi = buildHostInfoResortingToAddressEqualsSyntaxParser(user, password, hostInfo);
    if (hi != null) {
      this.parsedHosts.add(hi);
      return;
    } 
    hi = buildHostInfoResortingToGenericSyntaxParser(user, password, hostInfo);
    if (hi != null) {
      this.parsedHosts.add(hi);
      return;
    } 
    throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ConnectionString.2", new Object[] { authSegment }));
  }
  
  private HostInfo buildHostInfoForEmptyHost(String user, String password, String hostInfo) {
    if (StringUtils.isNullOrEmpty(hostInfo))
      return new HostInfo(this, null, -1, user, password); 
    return null;
  }
  
  private HostInfo buildHostInfoResortingToUriParser(String user, String password, String hostInfo) {
    String host = null;
    int port = -1;
    try {
      URI uri = URI.create("cj://" + hostInfo);
      if (uri.getHost() != null)
        host = decode(uri.getHost()); 
      if (uri.getPort() != -1)
        port = uri.getPort(); 
      if (uri.getUserInfo() != null)
        return null; 
    } catch (IllegalArgumentException e) {
      return null;
    } 
    if (host != null || port != -1)
      return new HostInfo(this, host, port, user, password); 
    return null;
  }
  
  private List<HostInfo> buildHostInfoResortingToSubHostsListParser(String user, String password, String hostInfo) {
    Matcher matcher = HOST_LIST_PTRN.matcher(hostInfo);
    if (matcher.matches()) {
      String hosts = matcher.group("hosts");
      List<String> hostsList = StringUtils.split(hosts, ",", "[(", "])", true, SearchMode.__MRK_WS);
      boolean maybeIPv6 = (hostsList.size() == 1 && ((String)hostsList.get(0)).matches("(?i)^[\\dabcdef:]+$"));
      List<HostInfo> hostInfoList = new ArrayList<>();
      for (String h : hostsList) {
        HostInfo hi;
        if ((hi = buildHostInfoForEmptyHost(user, password, h)) != null) {
          hostInfoList.add(hi);
          continue;
        } 
        if ((hi = buildHostInfoResortingToUriParser(user, password, h)) != null || (maybeIPv6 && (
          hi = buildHostInfoResortingToUriParser(user, password, "[" + h + "]")) != null)) {
          hostInfoList.add(hi);
          continue;
        } 
        if ((hi = buildHostInfoResortingToKeyValueSyntaxParser(user, password, h)) != null) {
          hostInfoList.add(hi);
          continue;
        } 
        if ((hi = buildHostInfoResortingToAddressEqualsSyntaxParser(user, password, h)) != null) {
          hostInfoList.add(hi);
          continue;
        } 
        if ((hi = buildHostInfoResortingToGenericSyntaxParser(user, password, h)) != null) {
          hostInfoList.add(hi);
          continue;
        } 
        return null;
      } 
      return hostInfoList;
    } 
    return null;
  }
  
  private HostInfo buildHostInfoResortingToKeyValueSyntaxParser(String user, String password, String hostInfo) {
    if (!hostInfo.startsWith("(") || !hostInfo.endsWith(")"))
      return null; 
    hostInfo = hostInfo.substring("(".length(), hostInfo.length() - ")".length());
    return new HostInfo(this, null, -1, user, password, processKeyValuePattern(KEY_VALUE_HOST_PTRN, hostInfo));
  }
  
  private HostInfo buildHostInfoResortingToAddressEqualsSyntaxParser(String user, String password, String hostInfo) {
    int p = StringUtils.indexOfIgnoreCase(hostInfo, "ADDRESS=");
    if (p != 0)
      return null; 
    hostInfo = hostInfo.substring(p + "ADDRESS=".length()).trim();
    return new HostInfo(this, null, -1, user, password, processKeyValuePattern(ADDRESS_EQUALS_HOST_PTRN, hostInfo));
  }
  
  private HostInfo buildHostInfoResortingToGenericSyntaxParser(String user, String password, String hostInfo) {
    if ((splitByUserInfoAndHostInfo(hostInfo)).left != null)
      return null; 
    Pair<String, Integer> hostPortPair = parseHostPortPair(hostInfo);
    String host = decode(StringUtils.safeTrim((String)hostPortPair.left));
    Integer port = (Integer)hostPortPair.right;
    return new HostInfo(this, StringUtils.isNullOrEmpty(host) ? null : host, port.intValue(), user, password);
  }
  
  private Pair<String, String> splitByUserInfoAndHostInfo(String authSegment) {
    String userInfoPart = null;
    String hostInfoPart = authSegment;
    int p = authSegment.indexOf("@");
    if (p >= 0) {
      userInfoPart = authSegment.substring(0, p);
      hostInfoPart = authSegment.substring(p + "@".length());
    } 
    return new Pair<>(userInfoPart, hostInfoPart);
  }
  
  public static Pair<String, String> parseUserInfo(String userInfo) {
    if (StringUtils.isNullOrEmpty(userInfo))
      return null; 
    String[] userInfoParts = userInfo.split(":", 2);
    String userName = userInfoParts[0];
    String password = (userInfoParts.length > 1) ? userInfoParts[1] : null;
    return new Pair<>(userName, password);
  }
  
  public static Pair<String, Integer> parseHostPortPair(String hostInfo) {
    if (StringUtils.isNullOrEmpty(hostInfo))
      return null; 
    Matcher matcher = GENERIC_HOST_PTRN.matcher(hostInfo);
    if (matcher.matches()) {
      String host = matcher.group("host");
      String portAsString = decode(StringUtils.safeTrim(matcher.group("port")));
      Integer portAsInteger = Integer.valueOf(-1);
      if (!StringUtils.isNullOrEmpty(portAsString))
        try {
          portAsInteger = Integer.valueOf(Integer.parseInt(portAsString));
        } catch (NumberFormatException e) {
          throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ConnectionString.3", new Object[] { hostInfo }), e);
        }  
      return new Pair<>(host, portAsInteger);
    } 
    throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ConnectionString.3", new Object[] { hostInfo }));
  }
  
  private void parseQuerySection() {
    if (StringUtils.isNullOrEmpty(this.query)) {
      this.parsedProperties = new HashMap<>();
      return;
    } 
    this.parsedProperties = processKeyValuePattern(PROPERTIES_PTRN, this.query);
  }
  
  private Map<String, String> processKeyValuePattern(Pattern pattern, String input) {
    Matcher matcher = pattern.matcher(input);
    int p = 0;
    Map<String, String> kvMap = new HashMap<>();
    while (matcher.find()) {
      if (matcher.start() != p)
        throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
            Messages.getString("ConnectionString.4", new Object[] { input.substring(p) })); 
      String key = decode(StringUtils.safeTrim(matcher.group("key")));
      String value = decode(StringUtils.safeTrim(matcher.group("value")));
      if (!StringUtils.isNullOrEmpty(key)) {
        kvMap.put(key, value);
      } else if (!StringUtils.isNullOrEmpty(value)) {
        throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
            Messages.getString("ConnectionString.4", new Object[] { input.substring(p) }));
      } 
      p = matcher.end();
    } 
    if (p != input.length())
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ConnectionString.4", new Object[] { input.substring(p) })); 
    return kvMap;
  }
  
  private static String decode(String text) {
    if (StringUtils.isNullOrEmpty(text))
      return text; 
    try {
      return URLDecoder.decode(text, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      return "";
    } 
  }
  
  private static String decodeSkippingPlusSign(String text) {
    if (StringUtils.isNullOrEmpty(text))
      return text; 
    text = text.replace("+", "%2B");
    try {
      return URLDecoder.decode(text, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      return "";
    } 
  }
  
  public String getDatabaseUrl() {
    return this.baseConnectionString;
  }
  
  public String getScheme() {
    return this.scheme;
  }
  
  public String getAuthority() {
    return this.authority;
  }
  
  public String getPath() {
    return this.path;
  }
  
  public String getQuery() {
    return this.query;
  }
  
  public List<HostInfo> getHosts() {
    if (this.parsedHosts == null) {
      this.parsedHosts = new ArrayList<>();
      parseAuthoritySection();
    } 
    return this.parsedHosts;
  }
  
  public Map<String, String> getProperties() {
    if (this.parsedProperties == null)
      parseQuerySection(); 
    return Collections.unmodifiableMap(this.parsedProperties);
  }
  
  public String toString() {
    StringBuilder asStr = new StringBuilder(super.toString());
    asStr.append(String.format(" :: {scheme: \"%s\", authority: \"%s\", path: \"%s\", query: \"%s\", parsedHosts: %s, parsedProperties: %s}", new Object[] { this.scheme, this.authority, this.path, this.query, this.parsedHosts, this.parsedProperties }));
    return asStr.toString();
  }
  
  public static class Pair<T, U> {
    public final T left;
    
    public final U right;
    
    public Pair(T left, U right) {
      this.left = left;
      this.right = right;
    }
    
    public String toString() {
      StringBuilder asStr = new StringBuilder(super.toString());
      asStr.append(String.format(" :: { left: %s, right: %s }", new Object[] { this.left, this.right }));
      return asStr.toString();
    }
  }
}
