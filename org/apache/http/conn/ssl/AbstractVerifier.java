package org.apache.http.conn.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.security.auth.x500.X500Principal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.util.Args;

@Deprecated
public abstract class AbstractVerifier implements X509HostnameVerifier {
  private final Log log = LogFactory.getLog(getClass());
  
  static final String[] BAD_COUNTRY_2LDS = new String[] { 
      "ac", "co", "com", "ed", "edu", "go", "gouv", "gov", "info", "lg", 
      "ne", "net", "or", "org" };
  
  static {
    Arrays.sort((Object[])BAD_COUNTRY_2LDS);
  }
  
  public final void verify(String host, SSLSocket ssl) throws IOException {
    Args.notNull(host, "Host");
    SSLSession session = ssl.getSession();
    if (session == null) {
      InputStream in = ssl.getInputStream();
      in.available();
      session = ssl.getSession();
      if (session == null) {
        ssl.startHandshake();
        session = ssl.getSession();
      } 
    } 
    Certificate[] certs = session.getPeerCertificates();
    X509Certificate x509 = (X509Certificate)certs[0];
    verify(host, x509);
  }
  
  public final boolean verify(String host, SSLSession session) {
    try {
      Certificate[] certs = session.getPeerCertificates();
      X509Certificate x509 = (X509Certificate)certs[0];
      verify(host, x509);
      return true;
    } catch (SSLException ex) {
      if (this.log.isDebugEnabled())
        this.log.debug(ex.getMessage(), ex); 
      return false;
    } 
  }
  
  public final void verify(String host, X509Certificate cert) throws SSLException {
    List<SubjectName> allSubjectAltNames = DefaultHostnameVerifier.getSubjectAltNames(cert);
    List<String> subjectAlts = new ArrayList<String>();
    if (InetAddressUtils.isIPv4Address(host) || InetAddressUtils.isIPv6Address(host)) {
      for (SubjectName subjectName : allSubjectAltNames) {
        if (subjectName.getType() == 7)
          subjectAlts.add(subjectName.getValue()); 
      } 
    } else {
      for (SubjectName subjectName : allSubjectAltNames) {
        if (subjectName.getType() == 2)
          subjectAlts.add(subjectName.getValue()); 
      } 
    } 
    X500Principal subjectPrincipal = cert.getSubjectX500Principal();
    String cn = DefaultHostnameVerifier.extractCN(subjectPrincipal.getName("RFC2253"));
    (new String[1])[0] = cn;
    verify(host, (cn != null) ? new String[1] : null, (subjectAlts != null && !subjectAlts.isEmpty()) ? subjectAlts.<String>toArray(new String[subjectAlts.size()]) : null);
  }
  
  public final void verify(String host, String[] cns, String[] subjectAlts, boolean strictWithSubDomains) throws SSLException {
    String cn = (cns != null && cns.length > 0) ? cns[0] : null;
    List<String> subjectAltList = (subjectAlts != null && subjectAlts.length > 0) ? Arrays.<String>asList(subjectAlts) : null;
    String normalizedHost = InetAddressUtils.isIPv6Address(host) ? DefaultHostnameVerifier.normaliseAddress(host.toLowerCase(Locale.ROOT)) : host;
    if (subjectAltList != null) {
      for (String subjectAlt : subjectAltList) {
        String normalizedAltSubject = InetAddressUtils.isIPv6Address(subjectAlt) ? DefaultHostnameVerifier.normaliseAddress(subjectAlt) : subjectAlt;
        if (matchIdentity(normalizedHost, normalizedAltSubject, strictWithSubDomains))
          return; 
      } 
      throw new SSLException("Certificate for <" + host + "> doesn't match any " + "of the subject alternative names: " + subjectAltList);
    } 
    if (cn != null) {
      String normalizedCN = InetAddressUtils.isIPv6Address(cn) ? DefaultHostnameVerifier.normaliseAddress(cn) : cn;
      if (matchIdentity(normalizedHost, normalizedCN, strictWithSubDomains))
        return; 
      throw new SSLException("Certificate for <" + host + "> doesn't match " + "common name of the certificate subject: " + cn);
    } 
    throw new SSLException("Certificate subject for <" + host + "> doesn't contain " + "a common name and does not have alternative names");
  }
  
  private static boolean matchIdentity(String host, String identity, boolean strict) {
    if (host == null)
      return false; 
    String normalizedHost = host.toLowerCase(Locale.ROOT);
    String normalizedIdentity = identity.toLowerCase(Locale.ROOT);
    String[] parts = normalizedIdentity.split("\\.");
    boolean doWildcard = (parts.length >= 3 && parts[0].endsWith("*") && (!strict || validCountryWildcard(parts)));
    if (doWildcard) {
      boolean match;
      String firstpart = parts[0];
      if (firstpart.length() > 1) {
        String prefix = firstpart.substring(0, firstpart.length() - 1);
        String suffix = normalizedIdentity.substring(firstpart.length());
        String hostSuffix = normalizedHost.substring(prefix.length());
        match = (normalizedHost.startsWith(prefix) && hostSuffix.endsWith(suffix));
      } else {
        match = normalizedHost.endsWith(normalizedIdentity.substring(1));
      } 
      return (match && (!strict || countDots(normalizedHost) == countDots(normalizedIdentity)));
    } 
    return normalizedHost.equals(normalizedIdentity);
  }
  
  private static boolean validCountryWildcard(String[] parts) {
    if (parts.length != 3 || parts[2].length() != 2)
      return true; 
    return (Arrays.binarySearch((Object[])BAD_COUNTRY_2LDS, parts[1]) < 0);
  }
  
  public static boolean acceptableCountryWildcard(String cn) {
    return validCountryWildcard(cn.split("\\."));
  }
  
  public static String[] getCNs(X509Certificate cert) {
    String subjectPrincipal = cert.getSubjectX500Principal().toString();
    try {
      String cn = DefaultHostnameVerifier.extractCN(subjectPrincipal);
      (new String[1])[0] = cn;
      return (cn != null) ? new String[1] : null;
    } catch (SSLException ex) {
      return null;
    } 
  }
  
  public static String[] getDNSSubjectAlts(X509Certificate cert) {
    List<SubjectName> subjectAltNames = DefaultHostnameVerifier.getSubjectAltNames(cert);
    if (subjectAltNames == null)
      return null; 
    List<String> dnsAlts = new ArrayList<String>();
    for (SubjectName subjectName : subjectAltNames) {
      if (subjectName.getType() == 2)
        dnsAlts.add(subjectName.getValue()); 
    } 
    return dnsAlts.isEmpty() ? dnsAlts.<String>toArray(new String[dnsAlts.size()]) : null;
  }
  
  public static int countDots(String s) {
    int count = 0;
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == '.')
        count++; 
    } 
    return count;
  }
}
