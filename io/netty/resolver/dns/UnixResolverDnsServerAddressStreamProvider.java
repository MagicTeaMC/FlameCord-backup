package io.netty.resolver.dns;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class UnixResolverDnsServerAddressStreamProvider implements DnsServerAddressStreamProvider {
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(UnixResolverDnsServerAddressStreamProvider.class);
  
  private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
  
  private static final String RES_OPTIONS = System.getenv("RES_OPTIONS");
  
  private static final String ETC_RESOLV_CONF_FILE = "/etc/resolv.conf";
  
  private static final String ETC_RESOLVER_DIR = "/etc/resolver";
  
  private static final String NAMESERVER_ROW_LABEL = "nameserver";
  
  private static final String SORTLIST_ROW_LABEL = "sortlist";
  
  private static final String OPTIONS_ROW_LABEL = "options ";
  
  private static final String OPTIONS_ROTATE_FLAG = "rotate";
  
  private static final String DOMAIN_ROW_LABEL = "domain";
  
  private static final String SEARCH_ROW_LABEL = "search";
  
  private static final String PORT_ROW_LABEL = "port";
  
  private final DnsServerAddresses defaultNameServerAddresses;
  
  private final Map<String, DnsServerAddresses> domainToNameServerStreamMap;
  
  static DnsServerAddressStreamProvider parseSilently() {
    try {
      UnixResolverDnsServerAddressStreamProvider nameServerCache = new UnixResolverDnsServerAddressStreamProvider("/etc/resolv.conf", "/etc/resolver");
      return nameServerCache.mayOverrideNameServers() ? nameServerCache : DefaultDnsServerAddressStreamProvider.INSTANCE;
    } catch (Exception e) {
      if (logger.isDebugEnabled())
        logger.debug("failed to parse {} and/or {}", new Object[] { "/etc/resolv.conf", "/etc/resolver", e }); 
      return DefaultDnsServerAddressStreamProvider.INSTANCE;
    } 
  }
  
  public UnixResolverDnsServerAddressStreamProvider(File etcResolvConf, File... etcResolverFiles) throws IOException {
    Map<String, DnsServerAddresses> etcResolvConfMap = parse(new File[] { (File)ObjectUtil.checkNotNull(etcResolvConf, "etcResolvConf") });
    boolean useEtcResolverFiles = (etcResolverFiles != null && etcResolverFiles.length != 0);
    this.domainToNameServerStreamMap = useEtcResolverFiles ? parse(etcResolverFiles) : etcResolvConfMap;
    DnsServerAddresses defaultNameServerAddresses = etcResolvConfMap.get(etcResolvConf.getName());
    if (defaultNameServerAddresses == null) {
      Collection<DnsServerAddresses> values = etcResolvConfMap.values();
      if (values.isEmpty())
        throw new IllegalArgumentException(etcResolvConf + " didn't provide any name servers"); 
      this.defaultNameServerAddresses = values.iterator().next();
    } else {
      this.defaultNameServerAddresses = defaultNameServerAddresses;
    } 
    if (useEtcResolverFiles)
      this.domainToNameServerStreamMap.putAll(etcResolvConfMap); 
  }
  
  public UnixResolverDnsServerAddressStreamProvider(String etcResolvConf, String etcResolverDir) throws IOException {
    this((etcResolvConf == null) ? null : new File(etcResolvConf), (etcResolverDir == null) ? null : (new File(etcResolverDir))
        .listFiles());
  }
  
  public DnsServerAddressStream nameServerAddressStream(String hostname) {
    while (true) {
      int i = hostname.indexOf('.', 1);
      if (i < 0 || i == hostname.length() - 1)
        return this.defaultNameServerAddresses.stream(); 
      DnsServerAddresses addresses = this.domainToNameServerStreamMap.get(hostname);
      if (addresses != null)
        return addresses.stream(); 
      hostname = hostname.substring(i + 1);
    } 
  }
  
  private boolean mayOverrideNameServers() {
    return (!this.domainToNameServerStreamMap.isEmpty() || this.defaultNameServerAddresses.stream().next() != null);
  }
  
  private static Map<String, DnsServerAddresses> parse(File... etcResolverFiles) throws IOException {
    Map<String, DnsServerAddresses> domainToNameServerStreamMap = new HashMap<String, DnsServerAddresses>(etcResolverFiles.length << 1);
    boolean rotateGlobal = (RES_OPTIONS != null && RES_OPTIONS.contains("rotate"));
    for (File etcResolverFile : etcResolverFiles) {
      if (etcResolverFile.isFile()) {
        FileReader fr = new FileReader(etcResolverFile);
        BufferedReader br = null;
      } 
    } 
    return domainToNameServerStreamMap;
  }
  
  private static void putIfAbsent(Map<String, DnsServerAddresses> domainToNameServerStreamMap, String domainName, List<InetSocketAddress> addresses, boolean rotate) {
    DnsServerAddresses addrs = rotate ? DnsServerAddresses.rotational(addresses) : DnsServerAddresses.sequential(addresses);
    putIfAbsent(domainToNameServerStreamMap, domainName, addrs);
  }
  
  private static void putIfAbsent(Map<String, DnsServerAddresses> domainToNameServerStreamMap, String domainName, DnsServerAddresses addresses) {
    DnsServerAddresses existingAddresses = domainToNameServerStreamMap.put(domainName, addresses);
    if (existingAddresses != null) {
      domainToNameServerStreamMap.put(domainName, existingAddresses);
      if (logger.isDebugEnabled())
        logger.debug("Domain name {} already maps to addresses {} so new addresses {} will be discarded", new Object[] { domainName, existingAddresses, addresses }); 
    } 
  }
  
  static UnixResolverOptions parseEtcResolverOptions() throws IOException {
    return parseEtcResolverOptions(new File("/etc/resolv.conf"));
  }
  
  static UnixResolverOptions parseEtcResolverOptions(File etcResolvConf) throws IOException {
    UnixResolverOptions.Builder optionsBuilder = UnixResolverOptions.newBuilder();
    FileReader fr = new FileReader(etcResolvConf);
    BufferedReader br = null;
    try {
      br = new BufferedReader(fr);
      String line;
      while ((line = br.readLine()) != null) {
        if (line.startsWith("options ")) {
          parseResOptions(line.substring("options ".length()), optionsBuilder);
          break;
        } 
      } 
    } finally {
      if (br == null) {
        fr.close();
      } else {
        br.close();
      } 
    } 
    if (RES_OPTIONS != null)
      parseResOptions(RES_OPTIONS, optionsBuilder); 
    return optionsBuilder.build();
  }
  
  private static void parseResOptions(String line, UnixResolverOptions.Builder builder) {
    String[] opts = WHITESPACE_PATTERN.split(line);
    for (String opt : opts) {
      try {
        if (opt.startsWith("ndots:")) {
          builder.setNdots(parseResIntOption(opt, "ndots:"));
        } else if (opt.startsWith("attempts:")) {
          builder.setAttempts(parseResIntOption(opt, "attempts:"));
        } else if (opt.startsWith("timeout:")) {
          builder.setTimeout(parseResIntOption(opt, "timeout:"));
        } 
      } catch (NumberFormatException numberFormatException) {}
    } 
  }
  
  private static int parseResIntOption(String opt, String fullLabel) {
    String optValue = opt.substring(fullLabel.length());
    return Integer.parseInt(optValue);
  }
  
  static List<String> parseEtcResolverSearchDomains() throws IOException {
    return parseEtcResolverSearchDomains(new File("/etc/resolv.conf"));
  }
  
  static List<String> parseEtcResolverSearchDomains(File etcResolvConf) throws IOException {
    String localDomain = null;
    List<String> searchDomains = new ArrayList<String>();
    FileReader fr = new FileReader(etcResolvConf);
    BufferedReader br = null;
    try {
      br = new BufferedReader(fr);
      String line;
      while ((line = br.readLine()) != null) {
        if (localDomain == null && line.startsWith("domain")) {
          int i = StringUtil.indexOfNonWhiteSpace(line, "domain".length());
          if (i >= 0)
            localDomain = line.substring(i); 
          continue;
        } 
        if (line.startsWith("search")) {
          int i = StringUtil.indexOfNonWhiteSpace(line, "search".length());
          if (i >= 0) {
            String[] domains = WHITESPACE_PATTERN.split(line.substring(i));
            Collections.addAll(searchDomains, domains);
          } 
        } 
      } 
    } finally {
      if (br == null) {
        fr.close();
      } else {
        br.close();
      } 
    } 
    return (localDomain != null && searchDomains.isEmpty()) ? 
      Collections.<String>singletonList(localDomain) : searchDomains;
  }
}