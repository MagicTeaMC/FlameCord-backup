package com.mysql.cj.protocol;

import com.mysql.cj.ServerVersion;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.FeatureNotAvailableException;
import com.mysql.cj.exceptions.RSAException;
import com.mysql.cj.exceptions.SSLParamsException;
import com.mysql.cj.log.Log;
import com.mysql.cj.util.Base64Decoder;
import com.mysql.cj.util.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.crypto.Cipher;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class ExportControlled {
  private static final String TLSv1 = "TLSv1";
  
  private static final String TLSv1_1 = "TLSv1.1";
  
  private static final String TLSv1_2 = "TLSv1.2";
  
  private static final String TLSv1_3 = "TLSv1.3";
  
  private static final String[] KNOWN_TLS_PROTOCOLS = new String[] { "TLSv1.3", "TLSv1.2", "TLSv1.1", "TLSv1" };
  
  private static final String[] VALID_TLS_PROTOCOLS = new String[] { "TLSv1.3", "TLSv1.2" };
  
  private static final String TLS_SETTINGS_RESOURCE = "/com/mysql/cj/TlsSettings.properties";
  
  private static final List<String> ALLOWED_CIPHERS = new ArrayList<>();
  
  private static final List<String> RESTRICTED_CIPHER_SUBSTR = new ArrayList<>();
  
  static {
    try {
      Properties tlsSettings = new Properties();
      tlsSettings.load(ExportControlled.class.getResourceAsStream("/com/mysql/cj/TlsSettings.properties"));
      Arrays.<String>stream(tlsSettings.getProperty("TLSCiphers.Mandatory").split("\\s*,\\s*")).forEach(s -> {
            ALLOWED_CIPHERS.add("TLS_" + s.trim());
            ALLOWED_CIPHERS.add("SSL_" + s.trim());
          });
      Arrays.<String>stream(tlsSettings.getProperty("TLSCiphers.Approved").split("\\s*,\\s*")).forEach(s -> {
            ALLOWED_CIPHERS.add("TLS_" + s.trim());
            ALLOWED_CIPHERS.add("SSL_" + s.trim());
          });
      Arrays.<String>stream(tlsSettings.getProperty("TLSCiphers.Deprecated").split("\\s*,\\s*")).forEach(s -> {
            ALLOWED_CIPHERS.add("TLS_" + s.trim());
            ALLOWED_CIPHERS.add("SSL_" + s.trim());
          });
      Arrays.<String>stream(tlsSettings.getProperty("TLSCiphers.Unacceptable.Mask").split("\\s*,\\s*")).forEach(s -> RESTRICTED_CIPHER_SUBSTR.add(s.trim()));
    } catch (IOException e) {
      throw ExceptionFactory.createException("Unable to load TlsSettings.properties");
    } 
  }
  
  public static boolean enabled() {
    return true;
  }
  
  private static String[] getAllowedCiphers(PropertySet pset, List<String> socketCipherSuites) {
    String enabledSSLCipherSuites = (String)pset.getStringProperty(PropertyKey.tlsCiphersuites).getValue();
    Stream<String> filterStream = StringUtils.isNullOrEmpty(enabledSSLCipherSuites) ? socketCipherSuites.stream() : Arrays.<String>stream(enabledSSLCipherSuites.split("\\s*,\\s*")).filter(socketCipherSuites::contains);
    List<String> allowedCiphers = (List<String>)filterStream.filter(ALLOWED_CIPHERS::contains).filter(c -> !RESTRICTED_CIPHER_SUBSTR.stream().filter(()).findFirst().isPresent()).collect(Collectors.toList());
    return allowedCiphers.<String>toArray(new String[0]);
  }
  
  private static String[] getAllowedProtocols(PropertySet pset, ServerVersion serverVersion, String[] socketProtocols) {
    List<String> tryProtocols = null;
    RuntimeProperty<String> tlsVersions = pset.getStringProperty(PropertyKey.tlsVersions);
    if (tlsVersions != null && tlsVersions.isExplicitlySet()) {
      if (tlsVersions.getValue() == null)
        throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Specified list of TLS versions is empty. Accepted values are TLSv1.2 and TLSv1.3."); 
      tryProtocols = getValidProtocols(((String)tlsVersions.getValue()).split("\\s*,\\s*"));
    } else {
      tryProtocols = new ArrayList<>(Arrays.asList(VALID_TLS_PROTOCOLS));
    } 
    List<String> jvmSupportedProtocols = Arrays.asList(socketProtocols);
    List<String> allowedProtocols = new ArrayList<>();
    for (String protocol : tryProtocols) {
      if (jvmSupportedProtocols.contains(protocol))
        allowedProtocols.add(protocol); 
    } 
    return allowedProtocols.<String>toArray(new String[0]);
  }
  
  private static List<String> getValidProtocols(String[] protocols) {
    List<String> requestedProtocols = (List<String>)Arrays.<String>stream(protocols).filter(p -> !StringUtils.isNullOrEmpty(p.trim())).collect(Collectors.toList());
    if (requestedProtocols.size() == 0)
      throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Specified list of TLS versions is empty. Accepted values are TLSv1.2 and TLSv1.3."); 
    List<String> sanitizedProtocols = new ArrayList<>();
    for (String protocol : KNOWN_TLS_PROTOCOLS) {
      if (requestedProtocols.contains(protocol))
        sanitizedProtocols.add(protocol); 
    } 
    if (sanitizedProtocols.size() == 0)
      throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Specified list of TLS versions only contains non valid TLS protocols. Accepted values are TLSv1.2 and TLSv1.3."); 
    List<String> validProtocols = new ArrayList<>();
    for (String protocol : VALID_TLS_PROTOCOLS) {
      if (sanitizedProtocols.contains(protocol))
        validProtocols.add(protocol); 
    } 
    if (validProtocols.size() == 0)
      throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "TLS protocols TLSv1 and TLSv1.1 are not supported. Accepted values are TLSv1.2 and TLSv1.3."); 
    return validProtocols;
  }
  
  public static void checkValidProtocols(List<String> protocols) {
    getValidProtocols(protocols.<String>toArray(new String[0]));
  }
  
  private static class KeyStoreConf {
    public String keyStoreUrl = null;
    
    public String keyStorePassword = null;
    
    public String keyStoreType = "JKS";
    
    public KeyStoreConf() {}
    
    public KeyStoreConf(String keyStoreUrl, String keyStorePassword, String keyStoreType) {
      this.keyStoreUrl = keyStoreUrl;
      this.keyStorePassword = keyStorePassword;
      this.keyStoreType = keyStoreType;
    }
  }
  
  private static KeyStoreConf getTrustStoreConf(PropertySet propertySet, boolean required) {
    String trustStoreUrl = (String)propertySet.getStringProperty(PropertyKey.trustCertificateKeyStoreUrl).getValue();
    String trustStorePassword = (String)propertySet.getStringProperty(PropertyKey.trustCertificateKeyStorePassword).getValue();
    String trustStoreType = (String)propertySet.getStringProperty(PropertyKey.trustCertificateKeyStoreType).getValue();
    boolean fallbackToSystemTrustStore = ((Boolean)propertySet.getBooleanProperty(PropertyKey.fallbackToSystemTrustStore).getValue()).booleanValue();
    if (fallbackToSystemTrustStore && StringUtils.isNullOrEmpty(trustStoreUrl)) {
      trustStoreUrl = System.getProperty("javax.net.ssl.trustStore");
      trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
      trustStoreType = System.getProperty("javax.net.ssl.trustStoreType");
      if (StringUtils.isNullOrEmpty(trustStoreType))
        trustStoreType = (String)propertySet.getStringProperty(PropertyKey.trustCertificateKeyStoreType).getInitialValue(); 
      if (!StringUtils.isNullOrEmpty(trustStoreUrl))
        try {
          new URL(trustStoreUrl);
        } catch (MalformedURLException e) {
          trustStoreUrl = "file:" + trustStoreUrl;
        }  
    } 
    if (required && StringUtils.isNullOrEmpty(trustStoreUrl))
      throw new CJCommunicationsException("No truststore provided to verify the Server certificate."); 
    return new KeyStoreConf(trustStoreUrl, trustStorePassword, trustStoreType);
  }
  
  private static KeyStoreConf getKeyStoreConf(PropertySet propertySet) {
    String keyStoreUrl = (String)propertySet.getStringProperty(PropertyKey.clientCertificateKeyStoreUrl).getValue();
    String keyStorePassword = (String)propertySet.getStringProperty(PropertyKey.clientCertificateKeyStorePassword).getValue();
    String keyStoreType = (String)propertySet.getStringProperty(PropertyKey.clientCertificateKeyStoreType).getValue();
    boolean fallbackToSystemKeyStore = ((Boolean)propertySet.getBooleanProperty(PropertyKey.fallbackToSystemKeyStore).getValue()).booleanValue();
    if (fallbackToSystemKeyStore && StringUtils.isNullOrEmpty(keyStoreUrl)) {
      keyStoreUrl = System.getProperty("javax.net.ssl.keyStore");
      keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
      keyStoreType = System.getProperty("javax.net.ssl.keyStoreType");
      if (StringUtils.isNullOrEmpty(keyStoreType))
        keyStoreType = (String)propertySet.getStringProperty(PropertyKey.clientCertificateKeyStoreType).getInitialValue(); 
      if (!StringUtils.isNullOrEmpty(keyStoreUrl))
        try {
          new URL(keyStoreUrl);
        } catch (MalformedURLException e) {
          keyStoreUrl = "file:" + keyStoreUrl;
        }  
    } 
    return new KeyStoreConf(keyStoreUrl, keyStorePassword, keyStoreType);
  }
  
  public static Socket performTlsHandshake(Socket rawSocket, SocketConnection socketConnection, ServerVersion serverVersion, Log log) throws IOException, SSLParamsException, FeatureNotAvailableException {
    PropertySet pset = socketConnection.getPropertySet();
    PropertyDefinitions.SslMode sslMode = (PropertyDefinitions.SslMode)pset.getEnumProperty(PropertyKey.sslMode).getValue();
    boolean verifyServerCert = (sslMode == PropertyDefinitions.SslMode.VERIFY_CA || sslMode == PropertyDefinitions.SslMode.VERIFY_IDENTITY);
    boolean fallbackToSystemTrustStore = ((Boolean)pset.getBooleanProperty(PropertyKey.fallbackToSystemTrustStore).getValue()).booleanValue();
    KeyStoreConf trustStore = !verifyServerCert ? new KeyStoreConf() : getTrustStoreConf(pset, (serverVersion == null && verifyServerCert && !fallbackToSystemTrustStore));
    KeyStoreConf keyStore = getKeyStoreConf(pset);
    SSLSocketFactory socketFactory = getSSLContext(keyStore, trustStore, fallbackToSystemTrustStore, verifyServerCert, (sslMode == PropertyDefinitions.SslMode.VERIFY_IDENTITY) ? socketConnection.getHost() : null, socketConnection.getExceptionInterceptor()).getSocketFactory();
    SSLSocket sslSocket = (SSLSocket)socketFactory.createSocket(rawSocket, socketConnection.getHost(), socketConnection.getPort(), true);
    String[] allowedProtocols = getAllowedProtocols(pset, serverVersion, sslSocket.getSupportedProtocols());
    sslSocket.setEnabledProtocols(allowedProtocols);
    String[] allowedCiphers = getAllowedCiphers(pset, Arrays.asList(sslSocket.getEnabledCipherSuites()));
    if (allowedCiphers != null)
      sslSocket.setEnabledCipherSuites(allowedCiphers); 
    sslSocket.startHandshake();
    return sslSocket;
  }
  
  public static class X509TrustManagerWrapper implements X509TrustManager {
    private X509TrustManager origTm = null;
    
    private boolean verifyServerCert = false;
    
    private String hostName = null;
    
    private CertificateFactory certFactory = null;
    
    private PKIXParameters validatorParams = null;
    
    private CertPathValidator validator = null;
    
    public X509TrustManagerWrapper(X509TrustManager tm, boolean verifyServerCertificate, String hostName) throws CertificateException {
      this.origTm = tm;
      this.verifyServerCert = verifyServerCertificate;
      this.hostName = hostName;
      if (verifyServerCertificate)
        try {
          Set<TrustAnchor> anch = (Set<TrustAnchor>)Arrays.<X509Certificate>stream(tm.getAcceptedIssuers()).map(c -> new TrustAnchor(c, null)).collect(Collectors.toSet());
          this.validatorParams = new PKIXParameters(anch);
          this.validatorParams.setRevocationEnabled(false);
          this.validator = CertPathValidator.getInstance("PKIX");
          this.certFactory = CertificateFactory.getInstance("X.509");
        } catch (Exception e) {
          throw new CertificateException(e);
        }  
    }
    
    public X509TrustManagerWrapper(boolean verifyServerCertificate, String hostName) {
      this.verifyServerCert = verifyServerCertificate;
      this.hostName = hostName;
    }
    
    public X509Certificate[] getAcceptedIssuers() {
      return (this.origTm != null) ? this.origTm.getAcceptedIssuers() : new X509Certificate[0];
    }
    
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
      for (int i = 0; i < chain.length; i++)
        chain[i].checkValidity(); 
      if (this.validatorParams != null) {
        X509CertSelector certSelect = new X509CertSelector();
        certSelect.setSerialNumber(chain[0].getSerialNumber());
        try {
          CertPath certPath = this.certFactory.generateCertPath(Arrays.asList((Certificate[])chain));
          CertPathValidatorResult result = this.validator.validate(certPath, this.validatorParams);
          ((PKIXCertPathValidatorResult)result).getTrustAnchor().getTrustedCert().checkValidity();
        } catch (InvalidAlgorithmParameterException e) {
          throw new CertificateException(e);
        } catch (CertPathValidatorException e) {
          throw new CertificateException(e);
        } 
      } 
      if (this.verifyServerCert) {
        if (this.origTm != null) {
          this.origTm.checkServerTrusted(chain, authType);
        } else {
          throw new CertificateException("Can't verify server certificate because no trust manager is found.");
        } 
        if (this.hostName != null) {
          boolean hostNameVerified = false;
          Collection<List<?>> subjectAltNames = chain[0].getSubjectAlternativeNames();
          if (subjectAltNames != null) {
            boolean sanVerification = false;
            for (List<?> san : subjectAltNames) {
              Integer nameType = (Integer)san.get(0);
              if (nameType.intValue() == 2) {
                sanVerification = true;
                if (verifyHostName((String)san.get(1))) {
                  hostNameVerified = true;
                  break;
                } 
                continue;
              } 
              if (nameType.intValue() == 7) {
                sanVerification = true;
                if (this.hostName.equalsIgnoreCase((String)san.get(1))) {
                  hostNameVerified = true;
                  break;
                } 
              } 
            } 
            if (sanVerification && !hostNameVerified)
              throw new CertificateException("Server identity verification failed. None of the DNS or IP Subject Alternative Name entries matched the server hostname/IP '" + this.hostName + "'."); 
          } 
          if (!hostNameVerified) {
            String dn = chain[0].getSubjectX500Principal().getName("RFC2253");
            String cn = null;
            try {
              LdapName ldapDN = new LdapName(dn);
              for (Rdn rdn : ldapDN.getRdns()) {
                if (rdn.getType().equalsIgnoreCase("CN")) {
                  cn = rdn.getValue().toString();
                  break;
                } 
              } 
            } catch (InvalidNameException e) {
              throw new CertificateException("Failed to retrieve the Common Name (CN) from the server certificate.");
            } 
            if (!verifyHostName(cn))
              throw new CertificateException("Server identity verification failed. The certificate Common Name '" + cn + "' does not match '" + this.hostName + "'."); 
          } 
        } 
      } 
    }
    
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
      this.origTm.checkClientTrusted(chain, authType);
    }
    
    private boolean verifyHostName(String ptn) {
      int indexOfStar = ptn.indexOf('*');
      if (indexOfStar >= 0 && indexOfStar < ptn.indexOf('.')) {
        String head = ptn.substring(0, indexOfStar);
        String tail = ptn.substring(indexOfStar + 1);
        return (StringUtils.startsWithIgnoreCase(this.hostName, head) && StringUtils.endsWithIgnoreCase(this.hostName, tail) && this.hostName
          .substring(head.length(), this.hostName.length() - tail.length()).indexOf('.') == -1);
      } 
      return this.hostName.equalsIgnoreCase(ptn);
    }
  }
  
  public static SSLContext getSSLContext(KeyStoreConf clientCertificateKeyStore, KeyStoreConf trustCertificateKeyStore, boolean fallbackToDefaultTrustStore, boolean verifyServerCert, String hostName, ExceptionInterceptor exceptionInterceptor) throws SSLParamsException {
    String clientCertificateKeyStoreUrl = clientCertificateKeyStore.keyStoreUrl;
    String clientCertificateKeyStoreType = clientCertificateKeyStore.keyStoreType;
    String clientCertificateKeyStorePassword = clientCertificateKeyStore.keyStorePassword;
    String trustCertificateKeyStoreUrl = trustCertificateKeyStore.keyStoreUrl;
    String trustCertificateKeyStoreType = trustCertificateKeyStore.keyStoreType;
    String trustCertificateKeyStorePassword = trustCertificateKeyStore.keyStorePassword;
    TrustManagerFactory tmf = null;
    KeyManagerFactory kmf = null;
    KeyManager[] kms = null;
    List<TrustManager> tms = new ArrayList<>();
    try {
      tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    } catch (NoSuchAlgorithmException nsae) {
      throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Default algorithm definitions for TrustManager and/or KeyManager are invalid.  Check java security properties file.", nsae, exceptionInterceptor);
    } 
    if (!StringUtils.isNullOrEmpty(clientCertificateKeyStoreUrl)) {
      InputStream ksIS = null;
      try {
        if (!StringUtils.isNullOrEmpty(clientCertificateKeyStoreType)) {
          KeyStore clientKeyStore = KeyStore.getInstance(clientCertificateKeyStoreType);
          URL ksURL = new URL(clientCertificateKeyStoreUrl);
          char[] password = (clientCertificateKeyStorePassword == null) ? new char[0] : clientCertificateKeyStorePassword.toCharArray();
          ksIS = ksURL.openStream();
          clientKeyStore.load(ksIS, password);
          kmf.init(clientKeyStore, password);
          kms = kmf.getKeyManagers();
        } 
      } catch (UnrecoverableKeyException uke) {
        throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Could not recover keys from client keystore.  Check password?", uke, exceptionInterceptor);
      } catch (NoSuchAlgorithmException nsae) {
        throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Unsupported keystore algorithm [" + nsae.getMessage() + "]", nsae, exceptionInterceptor);
      } catch (KeyStoreException kse) {
        throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Could not create KeyStore instance [" + kse.getMessage() + "]", kse, exceptionInterceptor);
      } catch (CertificateException nsae) {
        throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Could not load client" + clientCertificateKeyStoreType + " keystore from " + clientCertificateKeyStoreUrl, nsae, exceptionInterceptor);
      } catch (MalformedURLException mue) {
        throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, clientCertificateKeyStoreUrl + " does not appear to be a valid URL.", mue, exceptionInterceptor);
      } catch (IOException ioe) {
        throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Cannot open " + clientCertificateKeyStoreUrl + " [" + ioe.getMessage() + "]", ioe, exceptionInterceptor);
      } finally {
        if (ksIS != null)
          try {
            ksIS.close();
          } catch (IOException iOException) {} 
      } 
    } 
    InputStream trustStoreIS = null;
    try {
      String trustStoreType = "";
      char[] trustStorePassword = null;
      KeyStore trustKeyStore = null;
      if (!StringUtils.isNullOrEmpty(trustCertificateKeyStoreUrl) && !StringUtils.isNullOrEmpty(trustCertificateKeyStoreType)) {
        trustStoreType = trustCertificateKeyStoreType;
        trustStorePassword = (trustCertificateKeyStorePassword == null) ? new char[0] : trustCertificateKeyStorePassword.toCharArray();
        trustStoreIS = (new URL(trustCertificateKeyStoreUrl)).openStream();
        trustKeyStore = KeyStore.getInstance(trustStoreType);
        trustKeyStore.load(trustStoreIS, trustStorePassword);
      } 
      if (trustKeyStore != null || (verifyServerCert && fallbackToDefaultTrustStore)) {
        tmf.init(trustKeyStore);
        TrustManager[] origTms = tmf.getTrustManagers();
        for (TrustManager tm : origTms)
          tms.add((tm instanceof X509TrustManager) ? new X509TrustManagerWrapper((X509TrustManager)tm, verifyServerCert, hostName) : tm); 
      } 
    } catch (MalformedURLException e) {
      throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, trustCertificateKeyStoreUrl + " does not appear to be a valid URL.", e, exceptionInterceptor);
    } catch (NoSuchAlgorithmException e) {
      throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Unsupported keystore algorithm [" + e.getMessage() + "]", e, exceptionInterceptor);
    } catch (KeyStoreException e) {
      throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Could not create KeyStore instance [" + e.getMessage() + "]", e, exceptionInterceptor);
    } catch (CertificateException e) {
      throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Could not load trust" + trustCertificateKeyStoreType + " keystore from " + trustCertificateKeyStoreUrl, e, exceptionInterceptor);
    } catch (IOException e) {
      throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Cannot open " + trustCertificateKeyStoreUrl + " [" + e.getMessage() + "]", e, exceptionInterceptor);
    } finally {
      if (trustStoreIS != null)
        try {
          trustStoreIS.close();
        } catch (IOException iOException) {} 
    } 
    if (tms.size() == 0)
      tms.add(new X509TrustManagerWrapper(verifyServerCert, hostName)); 
    try {
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(kms, tms.<TrustManager>toArray(new TrustManager[tms.size()]), null);
      return sslContext;
    } catch (NoSuchAlgorithmException nsae) {
      throw new SSLParamsException("TLS is not a valid SSL protocol.", nsae);
    } catch (KeyManagementException kme) {
      throw new SSLParamsException("KeyManagementException: " + kme.getMessage(), kme);
    } 
  }
  
  public static boolean isSSLEstablished(Socket socket) {
    return (socket == null) ? false : SSLSocket.class.isAssignableFrom(socket.getClass());
  }
  
  public static RSAPublicKey decodeRSAPublicKey(String key) throws RSAException {
    if (key == null)
      throw (RSAException)ExceptionFactory.createException(RSAException.class, "Key parameter is null"); 
    int offset = key.indexOf("\n") + 1;
    int len = key.indexOf("-----END PUBLIC KEY-----") - offset;
    byte[] certificateData = Base64Decoder.decode(key.getBytes(), offset, len);
    X509EncodedKeySpec spec = new X509EncodedKeySpec(certificateData);
    try {
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return (RSAPublicKey)kf.generatePublic(spec);
    } catch (NoSuchAlgorithmException|java.security.spec.InvalidKeySpecException e) {
      throw (RSAException)ExceptionFactory.createException(RSAException.class, "Unable to decode public key", e);
    } 
  }
  
  public static byte[] encryptWithRSAPublicKey(byte[] source, RSAPublicKey key, String transformation) throws RSAException {
    try {
      Cipher cipher = Cipher.getInstance(transformation);
      cipher.init(1, key);
      return cipher.doFinal(source);
    } catch (NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException|java.security.InvalidKeyException|javax.crypto.IllegalBlockSizeException|javax.crypto.BadPaddingException e) {
      throw (RSAException)ExceptionFactory.createException(RSAException.class, e.getMessage(), e);
    } 
  }
  
  public static byte[] encryptWithRSAPublicKey(byte[] source, RSAPublicKey key) throws RSAException {
    return encryptWithRSAPublicKey(source, key, "RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
  }
  
  public static RSAPrivateKey decodeRSAPrivateKey(String key) throws RSAException {
    if (key == null)
      throw (RSAException)ExceptionFactory.createException(RSAException.class, "Key parameter is null"); 
    String keyData = key.replace("-----BEGIN PRIVATE KEY-----", "").replaceAll("\\R", "").replace("-----END PRIVATE KEY-----", "");
    byte[] decodedKeyData = Base64.getDecoder().decode(keyData);
    try {
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return (RSAPrivateKey)keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodedKeyData));
    } catch (NoSuchAlgorithmException|java.security.spec.InvalidKeySpecException e) {
      throw (RSAException)ExceptionFactory.createException(RSAException.class, "Unable to decode private key", e);
    } 
  }
  
  public static byte[] sign(byte[] source, RSAPrivateKey privateKey) throws RSAException {
    try {
      Signature signature = Signature.getInstance("SHA256withRSA");
      signature.initSign(privateKey);
      signature.update(source);
      return signature.sign();
    } catch (NoSuchAlgorithmException|java.security.InvalidKeyException|java.security.SignatureException e) {
      throw (RSAException)ExceptionFactory.createException(RSAException.class, e.getMessage(), e);
    } 
  }
}
