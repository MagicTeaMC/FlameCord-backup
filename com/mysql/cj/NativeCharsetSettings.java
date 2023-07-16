package com.mysql.cj;

import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.ProtocolEntityFactory;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.ServerCapabilities;
import com.mysql.cj.protocol.ServerSession;
import com.mysql.cj.protocol.a.NativeMessageBuilder;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.protocol.a.ResultsetFactory;
import com.mysql.cj.result.IntegerValueFactory;
import com.mysql.cj.result.Row;
import com.mysql.cj.result.StringValueFactory;
import com.mysql.cj.result.ValueFactory;
import com.mysql.cj.util.StringUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class NativeCharsetSettings extends CharsetMapping implements CharsetSettings {
  private NativeSession session;
  
  private ServerSession serverSession;
  
  public Map<Integer, String> collationIndexToCollationName = null;
  
  public Map<String, Integer> collationNameToCollationIndex = null;
  
  public Map<Integer, String> collationIndexToCharsetName = null;
  
  public Map<String, Integer> charsetNameToMblen = null;
  
  public Map<String, String> charsetNameToJavaEncoding = null;
  
  public Map<String, Integer> charsetNameToCollationIndex = null;
  
  public Map<String, String> javaEncodingUcToCharsetName = null;
  
  public Set<String> multibyteEncodings = null;
  
  private Integer sessionCollationIndex = null;
  
  private String metadataEncoding = null;
  
  private int metadataCollationIndex;
  
  private String errorMessageEncoding = "Cp1252";
  
  protected RuntimeProperty<String> characterEncoding;
  
  protected RuntimeProperty<String> passwordCharacterEncoding;
  
  protected RuntimeProperty<String> characterSetResults;
  
  protected RuntimeProperty<String> connectionCollation;
  
  protected RuntimeProperty<Boolean> cacheServerConfiguration;
  
  private boolean requiresEscapingEncoder;
  
  private NativeMessageBuilder commandBuilder = null;
  
  private static final Map<String, Map<Integer, String>> customCollationIndexToCollationNameByUrl = new HashMap<>();
  
  private static final Map<String, Map<String, Integer>> customCollationNameToCollationIndexByUrl = new HashMap<>();
  
  private static final Map<String, Map<Integer, String>> customCollationIndexToCharsetNameByUrl = new HashMap<>();
  
  private static final Map<String, Map<String, Integer>> customCharsetNameToMblenByUrl = new HashMap<>();
  
  private static final Map<String, Map<String, String>> customCharsetNameToJavaEncodingByUrl = new HashMap<>();
  
  private static final Map<String, Map<String, Integer>> customCharsetNameToCollationIndexByUrl = new HashMap<>();
  
  private static final Map<String, Map<String, String>> customJavaEncodingUcToCharsetNameByUrl = new HashMap<>();
  
  private static final Map<String, Set<String>> customMultibyteEncodingsByUrl = new HashMap<>();
  
  private boolean platformDbCharsetMatches = true;
  
  private NativeMessageBuilder getCommandBuilder() {
    if (this.commandBuilder == null)
      this.commandBuilder = new NativeMessageBuilder(this.serverSession.supportsQueryAttributes()); 
    return this.commandBuilder;
  }
  
  private void checkForCharsetMismatch() {
    String characterEncodingValue = (String)this.characterEncoding.getValue();
    if (characterEncodingValue != null) {
      Charset characterEncodingCs = Charset.forName(characterEncodingValue);
      Charset encodingToCheck = Charset.defaultCharset();
      this.platformDbCharsetMatches = encodingToCheck.equals(characterEncodingCs);
    } 
  }
  
  public boolean doesPlatformDbCharsetMatches() {
    return this.platformDbCharsetMatches;
  }
  
  public NativeCharsetSettings(NativeSession sess) {
    this.session = sess;
    this.serverSession = this.session.getServerSession();
    this.characterEncoding = sess.getPropertySet().getStringProperty(PropertyKey.characterEncoding);
    this.characterSetResults = this.session.getPropertySet().getProperty(PropertyKey.characterSetResults);
    this.passwordCharacterEncoding = this.session.getPropertySet().getStringProperty(PropertyKey.passwordCharacterEncoding);
    this.connectionCollation = this.session.getPropertySet().getStringProperty(PropertyKey.connectionCollation);
    this.cacheServerConfiguration = sess.getPropertySet().getBooleanProperty(PropertyKey.cacheServerConfiguration);
    tryAndFixEncoding(this.characterEncoding, true);
    tryAndFixEncoding(this.passwordCharacterEncoding, true);
    if (!"null".equalsIgnoreCase((String)this.characterSetResults.getValue()))
      tryAndFixEncoding(this.characterSetResults, false); 
  }
  
  private void tryAndFixEncoding(RuntimeProperty<String> encodingProperty, boolean replaceImpermissibleEncodings) {
    String oldEncoding = (String)encodingProperty.getValue();
    if (oldEncoding != null)
      if (replaceImpermissibleEncodings && ("UnicodeBig".equalsIgnoreCase(oldEncoding) || "UTF-16".equalsIgnoreCase(oldEncoding) || "UTF-16LE"
        .equalsIgnoreCase(oldEncoding) || "UTF-32".equalsIgnoreCase(oldEncoding))) {
        encodingProperty.setValue("UTF-8");
      } else {
        try {
          StringUtils.getBytes("abc", oldEncoding);
        } catch (WrongArgumentException waEx) {
          String newEncoding = getStaticJavaEncodingForMysqlCharset(oldEncoding);
          if (newEncoding == null)
            throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("StringUtils.0", new Object[] { oldEncoding }), this.session
                .getExceptionInterceptor()); 
          StringUtils.getBytes("abc", newEncoding);
          encodingProperty.setValue(newEncoding);
        } 
      }  
  }
  
  public int configurePreHandshake(boolean reset) {
    if (reset)
      this.sessionCollationIndex = null; 
    if (this.sessionCollationIndex != null)
      return this.sessionCollationIndex.intValue(); 
    ServerCapabilities capabilities = this.serverSession.getCapabilities();
    String encoding = this.passwordCharacterEncoding.getStringValue();
    if (encoding == null) {
      String connectionColl = this.connectionCollation.getStringValue();
      if ((connectionColl == null || (this.sessionCollationIndex = getStaticCollationIndexForCollationName(connectionColl)) == null) && (
        encoding = (String)this.characterEncoding.getValue()) == null)
        this.sessionCollationIndex = Integer.valueOf(255); 
    } 
    if (this.sessionCollationIndex == null && (
      this.sessionCollationIndex = Integer.valueOf(getStaticCollationIndexForJavaEncoding(encoding, capabilities.getServerVersion()))).intValue() == 0)
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("StringUtils.0", new Object[] { encoding })); 
    if (this.sessionCollationIndex.intValue() > 255 || 
      isStaticImpermissibleCollation(this.sessionCollationIndex.intValue()))
      this.sessionCollationIndex = Integer.valueOf(255); 
    if (this.sessionCollationIndex.intValue() == 255 && 
      !capabilities.getServerVersion().meetsMinimum(new ServerVersion(8, 0, 1)))
      this.sessionCollationIndex = Integer.valueOf(45); 
    this.errorMessageEncoding = getStaticJavaEncodingForCollationIndex(this.sessionCollationIndex);
    String csName = getStaticMysqlCharsetNameForCollationIndex(this.sessionCollationIndex);
    this.serverSession.getServerVariables().put("character_set_results", csName);
    this.serverSession.getServerVariables().put("character_set_client", csName);
    this.serverSession.getServerVariables().put("character_set_connection", csName);
    this.serverSession.getServerVariables().put("collation_connection", getStaticCollationNameForCollationIndex(this.sessionCollationIndex));
    return this.sessionCollationIndex.intValue();
  }
  
  public void configurePostHandshake(boolean dontCheckServerMatch) {
    buildCollationMapping();
    String requiredCollation = this.connectionCollation.getStringValue();
    String requiredEncoding = (String)this.characterEncoding.getValue();
    String passwordEncoding = (String)this.passwordCharacterEncoding.getValue();
    String sessionCharsetName = getServerDefaultCharset();
    String sessionCollationClause = "";
    try {
      Integer requiredCollationIndex;
      if (requiredCollation != null && (requiredCollationIndex = getCollationIndexForCollationName(requiredCollation)) != null) {
        if (isImpermissibleCollation(requiredCollationIndex.intValue()))
          if (this.serverSession.getCapabilities().getServerVersion().meetsMinimum(new ServerVersion(8, 0, 1))) {
            requiredCollationIndex = Integer.valueOf(255);
            requiredCollation = "utf8mb4_0900_ai_ci";
          } else {
            requiredCollationIndex = Integer.valueOf(45);
            requiredCollation = "utf8mb4_general_ci";
          }  
        sessionCollationClause = " COLLATE " + requiredCollation;
        sessionCharsetName = getMysqlCharsetNameForCollationIndex(requiredCollationIndex);
        requiredEncoding = getJavaEncodingForCollationIndex(requiredCollationIndex, requiredEncoding);
        this.sessionCollationIndex = requiredCollationIndex;
      } 
      if (requiredEncoding != null) {
        if (sessionCollationClause.length() == 0)
          sessionCharsetName = getMysqlCharsetForJavaEncoding(requiredEncoding.toUpperCase(Locale.ENGLISH), this.serverSession.getServerVersion()); 
      } else {
        if (!StringUtils.isNullOrEmpty(passwordEncoding)) {
          if (this.serverSession.getCapabilities().getServerVersion().meetsMinimum(new ServerVersion(8, 0, 1))) {
            this.sessionCollationIndex = Integer.valueOf(255);
            requiredCollation = "utf8mb4_0900_ai_ci";
          } else {
            this.sessionCollationIndex = Integer.valueOf(45);
            requiredCollation = "utf8mb4_general_ci";
          } 
          sessionCollationClause = " COLLATE " + getCollationNameForCollationIndex(this.sessionCollationIndex);
        } 
        if ((requiredEncoding = getJavaEncodingForCollationIndex(this.sessionCollationIndex, requiredEncoding)) == null)
          throw ExceptionFactory.createException(Messages.getString("Connection.5", new Object[] { this.sessionCollationIndex.toString() }), this.session
              .getExceptionInterceptor()); 
        sessionCharsetName = getMysqlCharsetNameForCollationIndex(this.sessionCollationIndex);
      } 
    } catch (ArrayIndexOutOfBoundsException outOfBoundsEx) {
      throw ExceptionFactory.createException(Messages.getString("Connection.6", new Object[] { this.sessionCollationIndex }), this.session
          .getExceptionInterceptor());
    } 
    this.characterEncoding.setValue(requiredEncoding);
    if (sessionCharsetName != null) {
      boolean isCharsetDifferent = !characterSetNamesMatches(sessionCharsetName);
      boolean isCollationDifferent = (sessionCollationClause.length() > 0 && !requiredCollation.equalsIgnoreCase(this.serverSession.getServerVariable("collation_connection")));
      if (dontCheckServerMatch || isCharsetDifferent || isCollationDifferent) {
        this.session.getProtocol().sendCommand((Message)getCommandBuilder().buildComQuery(null, "SET NAMES " + sessionCharsetName + sessionCollationClause), false, 0);
        this.serverSession.getServerVariables().put("character_set_client", sessionCharsetName);
        this.serverSession.getServerVariables().put("character_set_connection", sessionCharsetName);
        if (sessionCollationClause.length() > 0) {
          this.serverSession.getServerVariables().put("collation_connection", requiredCollation);
        } else {
          int idx = getCollationIndexForMysqlCharsetName(sessionCharsetName);
          if (idx == 255 && 
            !this.serverSession.getCapabilities().getServerVersion().meetsMinimum(new ServerVersion(8, 0, 1)))
            idx = 45; 
          this.serverSession.getServerVariables().put("collation_connection", getCollationNameForCollationIndex(Integer.valueOf(idx)));
        } 
      } 
    } 
    String sessionResultsCharset = this.serverSession.getServerVariable("character_set_results");
    String characterSetResultsValue = (String)this.characterSetResults.getValue();
    if (StringUtils.isNullOrEmpty(characterSetResultsValue) || "null".equalsIgnoreCase(characterSetResultsValue)) {
      if (!StringUtils.isNullOrEmpty(sessionResultsCharset) && !"NULL".equalsIgnoreCase(sessionResultsCharset)) {
        this.session.getProtocol().sendCommand((Message)getCommandBuilder().buildComQuery(null, "SET character_set_results = NULL"), false, 0);
        this.serverSession.getServerVariables().put("character_set_results", null);
      } 
      String defaultMetadataCharsetMysql = this.serverSession.getServerVariable("character_set_system");
      this.metadataEncoding = (defaultMetadataCharsetMysql != null) ? getJavaEncodingForMysqlCharset(defaultMetadataCharsetMysql) : "UTF-8";
      this.errorMessageEncoding = "UTF-8";
    } else {
      String resultsCharsetName = getMysqlCharsetForJavaEncoding(characterSetResultsValue.toUpperCase(Locale.ENGLISH), this.serverSession
          .getServerVersion());
      if (resultsCharsetName == null)
        throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
            Messages.getString("Connection.7", new Object[] { characterSetResultsValue }), this.session.getExceptionInterceptor()); 
      if (!resultsCharsetName.equalsIgnoreCase(sessionResultsCharset)) {
        this.session.getProtocol().sendCommand((Message)getCommandBuilder().buildComQuery(null, "SET character_set_results = " + resultsCharsetName), false, 0);
        this.serverSession.getServerVariables().put("character_set_results", resultsCharsetName);
      } 
      this.metadataEncoding = characterSetResultsValue;
      this.errorMessageEncoding = characterSetResultsValue;
    } 
    this.metadataCollationIndex = getCollationIndexForJavaEncoding(this.metadataEncoding, this.serverSession.getServerVersion());
    checkForCharsetMismatch();
    try {
      CharsetEncoder enc = Charset.forName((String)this.characterEncoding.getValue()).newEncoder();
      CharBuffer cbuf = CharBuffer.allocate(1);
      ByteBuffer bbuf = ByteBuffer.allocate(1);
      cbuf.put("¥");
      cbuf.position(0);
      enc.encode(cbuf, bbuf, true);
      if (bbuf.get(0) == 92) {
        this.requiresEscapingEncoder = true;
      } else {
        cbuf.clear();
        bbuf.clear();
        cbuf.put("₩");
        cbuf.position(0);
        enc.encode(cbuf, bbuf, true);
        if (bbuf.get(0) == 92)
          this.requiresEscapingEncoder = true; 
      } 
    } catch (UnsupportedCharsetException ucex) {
      byte[] bbuf = StringUtils.getBytes("¥", (String)this.characterEncoding.getValue());
      if (bbuf[0] == 92) {
        this.requiresEscapingEncoder = true;
      } else {
        bbuf = StringUtils.getBytes("₩", (String)this.characterEncoding.getValue());
        if (bbuf[0] == 92)
          this.requiresEscapingEncoder = true; 
      } 
    } 
  }
  
  private boolean characterSetNamesMatches(String mysqlEncodingName) {
    boolean res = false;
    if (mysqlEncodingName != null) {
      res = (mysqlEncodingName.equalsIgnoreCase(this.serverSession.getServerVariable("character_set_client")) && mysqlEncodingName.equalsIgnoreCase(this.serverSession.getServerVariable("character_set_connection")));
      List<String> aliases;
      if (!res && (aliases = CharsetMapping.getStaticMysqlCharsetAliasesByName(mysqlEncodingName)) != null)
        for (String alias : aliases) {
          if (res = (alias.equalsIgnoreCase(this.serverSession.getServerVariable("character_set_client")) && alias.equalsIgnoreCase(this.serverSession.getServerVariable("character_set_connection"))))
            break; 
        }  
    } 
    return res;
  }
  
  public String getServerDefaultCharset() {
    String charset = getStaticMysqlCharsetNameForCollationIndex(this.sessionCollationIndex);
    return (charset != null) ? charset : this.serverSession.getServerVariable("character_set_server");
  }
  
  public String getErrorMessageEncoding() {
    return this.errorMessageEncoding;
  }
  
  public String getMetadataEncoding() {
    return this.metadataEncoding;
  }
  
  public int getMetadataCollationIndex() {
    return this.metadataCollationIndex;
  }
  
  public boolean getRequiresEscapingEncoder() {
    return this.requiresEscapingEncoder;
  }
  
  public String getPasswordCharacterEncoding() {
    return getStaticJavaEncodingForCollationIndex(this.sessionCollationIndex);
  }
  
  private void buildCollationMapping() {
    Map<Integer, String> customCollationIndexToCollationName = null;
    Map<String, Integer> customCollationNameToCollationIndex = null;
    Map<Integer, String> customCollationIndexToCharsetName = null;
    Map<String, Integer> customCharsetNameToMblen = null;
    Map<String, String> customCharsetNameToJavaEncoding = new HashMap<>();
    Map<String, String> customJavaEncodingUcToCharsetName = new HashMap<>();
    Map<String, Integer> customCharsetNameToCollationIndex = new HashMap<>();
    Set<String> customMultibyteEncodings = new HashSet<>();
    String databaseURL = this.session.getHostInfo().getDatabaseUrl();
    if (((Boolean)this.cacheServerConfiguration.getValue()).booleanValue())
      synchronized (customCollationIndexToCharsetNameByUrl) {
        customCollationIndexToCollationName = customCollationIndexToCollationNameByUrl.get(databaseURL);
        customCollationNameToCollationIndex = customCollationNameToCollationIndexByUrl.get(databaseURL);
        customCollationIndexToCharsetName = customCollationIndexToCharsetNameByUrl.get(databaseURL);
        customCharsetNameToMblen = customCharsetNameToMblenByUrl.get(databaseURL);
        customCharsetNameToJavaEncoding = customCharsetNameToJavaEncodingByUrl.get(databaseURL);
        customJavaEncodingUcToCharsetName = customJavaEncodingUcToCharsetNameByUrl.get(databaseURL);
        customCharsetNameToCollationIndex = customCharsetNameToCollationIndexByUrl.get(databaseURL);
        customMultibyteEncodings = customMultibyteEncodingsByUrl.get(databaseURL);
      }  
    if (customCollationIndexToCharsetName == null && ((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.detectCustomCollations).getValue()).booleanValue()) {
      customCollationIndexToCollationName = new HashMap<>();
      customCollationNameToCollationIndex = new HashMap<>();
      customCollationIndexToCharsetName = new HashMap<>();
      customCharsetNameToMblen = new HashMap<>();
      customCharsetNameToJavaEncoding = new HashMap<>();
      customJavaEncodingUcToCharsetName = new HashMap<>();
      customCharsetNameToCollationIndex = new HashMap<>();
      customMultibyteEncodings = new HashSet<>();
      String customCharsetMapping = (String)this.session.getPropertySet().getStringProperty(PropertyKey.customCharsetMapping).getValue();
      if (customCharsetMapping != null) {
        String[] pairs = customCharsetMapping.split(",");
        for (String pair : pairs) {
          int keyEnd = pair.indexOf(":");
          if (keyEnd > 0 && keyEnd + 1 < pair.length()) {
            String charset = pair.substring(0, keyEnd);
            String encoding = pair.substring(keyEnd + 1);
            customCharsetNameToJavaEncoding.put(charset, encoding);
            customJavaEncodingUcToCharsetName.put(encoding.toUpperCase(Locale.ENGLISH), charset);
          } 
        } 
      } 
      IntegerValueFactory integerValueFactory = new IntegerValueFactory(this.session.getPropertySet());
      try {
        NativePacketPayload resultPacket = this.session.getProtocol().sendCommand((Message)getCommandBuilder().buildComQuery(null, "SELECT c.COLLATION_NAME, c.CHARACTER_SET_NAME, c.ID, cs.MAXLEN, c.IS_DEFAULT='Yes' from INFORMATION_SCHEMA.COLLATIONS AS c LEFT JOIN INFORMATION_SCHEMA.CHARACTER_SETS AS cs ON cs.CHARACTER_SET_NAME=c.CHARACTER_SET_NAME"), false, 0);
        Resultset rs = this.session.getProtocol().readAllResults(-1, false, resultPacket, false, null, (ProtocolEntityFactory)new ResultsetFactory(Resultset.Type.FORWARD_ONLY, null));
        StringValueFactory stringValueFactory = new StringValueFactory(this.session.getPropertySet());
        Row r;
        while ((r = (Row)rs.getRows().next()) != null) {
          String collationName = (String)r.getValue(0, (ValueFactory)stringValueFactory);
          String charsetName = (String)r.getValue(1, (ValueFactory)stringValueFactory);
          int collationIndex = ((Number)r.getValue(2, (ValueFactory)integerValueFactory)).intValue();
          int maxlen = ((Number)r.getValue(3, (ValueFactory)integerValueFactory)).intValue();
          boolean isDefault = (((Number)r.getValue(4, (ValueFactory)integerValueFactory)).intValue() > 0);
          if (collationIndex >= 1024 || collationIndex != 
            getStaticCollationIndexForCollationName(collationName).intValue() || 
            !charsetName.equals(getStaticMysqlCharsetNameForCollationIndex(Integer.valueOf(collationIndex)))) {
            customCollationIndexToCollationName.put(Integer.valueOf(collationIndex), collationName);
            customCollationNameToCollationIndex.put(collationName, Integer.valueOf(collationIndex));
            customCollationIndexToCharsetName.put(Integer.valueOf(collationIndex), charsetName);
            if (isDefault || (!customCharsetNameToCollationIndex.containsKey(charsetName) && 
              CharsetMapping.getStaticCollationIndexForMysqlCharsetName(charsetName) == 0))
              customCharsetNameToCollationIndex.put(charsetName, Integer.valueOf(collationIndex)); 
          } 
          if (getStaticMysqlCharsetByName(charsetName) == null) {
            customCharsetNameToMblen.put(charsetName, Integer.valueOf(maxlen));
            if (maxlen > 1) {
              String enc = customCharsetNameToJavaEncoding.get(charsetName);
              if (enc != null)
                customMultibyteEncodings.add(enc.toUpperCase(Locale.ENGLISH)); 
            } 
          } 
        } 
      } catch (IOException e) {
        throw ExceptionFactory.createException(e.getMessage(), e, this.session.getExceptionInterceptor());
      } 
      if (((Boolean)this.cacheServerConfiguration.getValue()).booleanValue())
        synchronized (customCollationIndexToCharsetNameByUrl) {
          customCollationIndexToCollationNameByUrl.put(databaseURL, Collections.unmodifiableMap(customCollationIndexToCollationName));
          customCollationNameToCollationIndexByUrl.put(databaseURL, Collections.unmodifiableMap(customCollationNameToCollationIndex));
          customCollationIndexToCharsetNameByUrl.put(databaseURL, Collections.unmodifiableMap(customCollationIndexToCharsetName));
          customCharsetNameToMblenByUrl.put(databaseURL, Collections.unmodifiableMap(customCharsetNameToMblen));
          customCharsetNameToJavaEncodingByUrl.put(databaseURL, Collections.unmodifiableMap(customCharsetNameToJavaEncoding));
          customJavaEncodingUcToCharsetNameByUrl.put(databaseURL, Collections.unmodifiableMap(customJavaEncodingUcToCharsetName));
          customCharsetNameToCollationIndexByUrl.put(databaseURL, Collections.unmodifiableMap(customCharsetNameToCollationIndex));
          customMultibyteEncodingsByUrl.put(databaseURL, Collections.unmodifiableSet(customMultibyteEncodings));
        }  
    } 
    if (customCollationIndexToCharsetName != null) {
      this.collationIndexToCollationName = customCollationIndexToCollationName;
      this.collationNameToCollationIndex = customCollationNameToCollationIndex;
      this.collationIndexToCharsetName = customCollationIndexToCharsetName;
      this.charsetNameToMblen = customCharsetNameToMblen;
      this.charsetNameToJavaEncoding = customCharsetNameToJavaEncoding;
      this.javaEncodingUcToCharsetName = customJavaEncodingUcToCharsetName;
      this.charsetNameToCollationIndex = customCharsetNameToCollationIndex;
      this.multibyteEncodings = customMultibyteEncodings;
    } 
  }
  
  public Integer getCollationIndexForCollationName(String collationName) {
    Integer collationIndex = null;
    if (this.collationNameToCollationIndex == null || (collationIndex = this.collationNameToCollationIndex.get(collationName)) == null)
      collationIndex = getStaticCollationIndexForCollationName(collationName); 
    return collationIndex;
  }
  
  public String getCollationNameForCollationIndex(Integer collationIndex) {
    String collationName = null;
    if (collationIndex != null && (this.collationIndexToCollationName == null || (
      collationName = this.collationIndexToCollationName.get(collationIndex)) == null))
      collationName = getStaticCollationNameForCollationIndex(collationIndex); 
    return collationName;
  }
  
  public String getMysqlCharsetNameForCollationIndex(Integer collationIndex) {
    String charset = null;
    if (this.collationIndexToCharsetName == null || (charset = this.collationIndexToCharsetName.get(collationIndex)) == null)
      charset = getStaticMysqlCharsetNameForCollationIndex(collationIndex); 
    return charset;
  }
  
  public String getJavaEncodingForCollationIndex(int collationIndex) {
    return getJavaEncodingForCollationIndex(Integer.valueOf(collationIndex), (String)this.characterEncoding.getValue());
  }
  
  public String getJavaEncodingForCollationIndex(Integer collationIndex, String fallBackJavaEncoding) {
    String encoding = null;
    String charset = null;
    if (collationIndex.intValue() != -1) {
      if (this.collationIndexToCharsetName != null && (charset = this.collationIndexToCharsetName.get(collationIndex)) != null)
        encoding = getJavaEncodingForMysqlCharset(charset, fallBackJavaEncoding); 
      if (encoding == null)
        encoding = getStaticJavaEncodingForCollationIndex(collationIndex, fallBackJavaEncoding); 
    } 
    return (encoding != null) ? encoding : fallBackJavaEncoding;
  }
  
  public int getCollationIndexForJavaEncoding(String javaEncoding, ServerVersion version) {
    return getCollationIndexForMysqlCharsetName(getMysqlCharsetForJavaEncoding(javaEncoding, version));
  }
  
  public int getCollationIndexForMysqlCharsetName(String charsetName) {
    Integer index = null;
    if (this.charsetNameToCollationIndex == null || (index = this.charsetNameToCollationIndex.get(charsetName)) == null)
      index = Integer.valueOf(getStaticCollationIndexForMysqlCharsetName(charsetName)); 
    return index.intValue();
  }
  
  public String getJavaEncodingForMysqlCharset(String mysqlCharsetName) {
    String encoding = null;
    if (this.charsetNameToJavaEncoding == null || (encoding = this.charsetNameToJavaEncoding.get(mysqlCharsetName)) == null)
      encoding = getStaticJavaEncodingForMysqlCharset(mysqlCharsetName); 
    return encoding;
  }
  
  public String getJavaEncodingForMysqlCharset(String mysqlCharsetName, String javaEncoding) {
    String encoding = null;
    if (this.charsetNameToJavaEncoding == null || (encoding = this.charsetNameToJavaEncoding.get(mysqlCharsetName)) == null)
      encoding = getStaticJavaEncodingForMysqlCharset(mysqlCharsetName, javaEncoding); 
    return encoding;
  }
  
  public String getMysqlCharsetForJavaEncoding(String javaEncoding, ServerVersion version) {
    String charset = null;
    if (this.javaEncodingUcToCharsetName == null || (charset = this.javaEncodingUcToCharsetName.get(javaEncoding.toUpperCase(Locale.ENGLISH))) == null)
      charset = getStaticMysqlCharsetForJavaEncoding(javaEncoding, version); 
    return charset;
  }
  
  public boolean isImpermissibleCollation(int collationIndex) {
    String charsetName = null;
    if (this.collationIndexToCharsetName != null && (charsetName = this.collationIndexToCharsetName.get(Integer.valueOf(collationIndex))) != null && (
      charsetName.equals("ucs2") || charsetName.equals("utf16") || charsetName.equals("utf16le") || charsetName
      .equals("utf32")))
      return true; 
    return isStaticImpermissibleCollation(collationIndex);
  }
  
  public boolean isMultibyteCharset(String javaEncodingName) {
    if (this.multibyteEncodings != null && this.multibyteEncodings.contains(javaEncodingName.toUpperCase(Locale.ENGLISH)))
      return true; 
    return isStaticMultibyteCharset(javaEncodingName);
  }
  
  public int getMaxBytesPerChar(String javaCharsetName) {
    return getMaxBytesPerChar((Integer)null, javaCharsetName);
  }
  
  public int getMaxBytesPerChar(Integer charsetIndex, String javaCharsetName) {
    String charset = null;
    if ((charset = getMysqlCharsetNameForCollationIndex(charsetIndex)) == null)
      charset = getStaticMysqlCharsetForJavaEncoding(javaCharsetName, this.serverSession.getServerVersion()); 
    Integer mblen = null;
    if (this.charsetNameToMblen == null || (mblen = this.charsetNameToMblen.get(charset)) == null)
      mblen = Integer.valueOf(getStaticMblen(charset)); 
    return (mblen != null) ? mblen.intValue() : 1;
  }
}
