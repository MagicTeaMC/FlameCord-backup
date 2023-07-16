package org.apache.http.conn.util;

import java.net.IDN;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.SAFE)
public final class PublicSuffixMatcher {
  private final Map<String, DomainType> rules;
  
  private final Map<String, DomainType> exceptions;
  
  public PublicSuffixMatcher(Collection<String> rules, Collection<String> exceptions) {
    this(DomainType.UNKNOWN, rules, exceptions);
  }
  
  public PublicSuffixMatcher(DomainType domainType, Collection<String> rules, Collection<String> exceptions) {
    Args.notNull(domainType, "Domain type");
    Args.notNull(rules, "Domain suffix rules");
    this.rules = new ConcurrentHashMap<String, DomainType>(rules.size());
    for (String rule : rules)
      this.rules.put(rule, domainType); 
    this.exceptions = new ConcurrentHashMap<String, DomainType>();
    if (exceptions != null)
      for (String exception : exceptions)
        this.exceptions.put(exception, domainType);  
  }
  
  public PublicSuffixMatcher(Collection<PublicSuffixList> lists) {
    Args.notNull(lists, "Domain suffix lists");
    this.rules = new ConcurrentHashMap<String, DomainType>();
    this.exceptions = new ConcurrentHashMap<String, DomainType>();
    for (PublicSuffixList list : lists) {
      DomainType domainType = list.getType();
      List<String> rules = list.getRules();
      for (String rule : rules)
        this.rules.put(rule, domainType); 
      List<String> exceptions = list.getExceptions();
      if (exceptions != null)
        for (String exception : exceptions)
          this.exceptions.put(exception, domainType);  
    } 
  }
  
  private static DomainType findEntry(Map<String, DomainType> map, String rule) {
    if (map == null)
      return null; 
    return map.get(rule);
  }
  
  private static boolean match(DomainType domainType, DomainType expectedType) {
    return (domainType != null && (expectedType == null || domainType.equals(expectedType)));
  }
  
  public String getDomainRoot(String domain) {
    return getDomainRoot(domain, null);
  }
  
  public String getDomainRoot(String domain, DomainType expectedType) {
    if (domain == null)
      return null; 
    if (domain.startsWith("."))
      return null; 
    String normalized = DnsUtils.normalize(domain);
    String segment = normalized;
    String result = null;
    while (segment != null) {
      String key = IDN.toUnicode(segment);
      DomainType exceptionRule = findEntry(this.exceptions, key);
      if (match(exceptionRule, expectedType))
        return segment; 
      DomainType domainRule = findEntry(this.rules, key);
      if (match(domainRule, expectedType)) {
        if (domainRule == DomainType.PRIVATE)
          return segment; 
        return result;
      } 
      int nextdot = segment.indexOf('.');
      String nextSegment = (nextdot != -1) ? segment.substring(nextdot + 1) : null;
      if (nextSegment != null) {
        DomainType wildcardDomainRule = findEntry(this.rules, "*." + IDN.toUnicode(nextSegment));
        if (match(wildcardDomainRule, expectedType)) {
          if (wildcardDomainRule == DomainType.PRIVATE)
            return segment; 
          return result;
        } 
      } 
      result = segment;
      segment = nextSegment;
    } 
    if (expectedType == null || expectedType == DomainType.UNKNOWN)
      return result; 
    return null;
  }
  
  public boolean matches(String domain) {
    return matches(domain, null);
  }
  
  public boolean matches(String domain, DomainType expectedType) {
    if (domain == null)
      return false; 
    String domainRoot = getDomainRoot(domain.startsWith(".") ? domain.substring(1) : domain, expectedType);
    return (domainRoot == null);
  }
}
