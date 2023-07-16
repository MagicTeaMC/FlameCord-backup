package com.mysql.cj.protocol.x;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.mysql.cj.Constants;
import com.mysql.cj.Messages;
import com.mysql.cj.Session;
import com.mysql.cj.TransactionEventHandler;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.AssertionFailedException;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.CJConnectionFeatureNotAvailableException;
import com.mysql.cj.exceptions.CJOperationNotSupportedException;
import com.mysql.cj.exceptions.ConnectionIsClosedException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.SSLParamsException;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.log.LogFactory;
import com.mysql.cj.protocol.AbstractProtocol;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.ExportControlled;
import com.mysql.cj.protocol.FullReadInputStream;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.MessageListener;
import com.mysql.cj.protocol.MessageReader;
import com.mysql.cj.protocol.MessageSender;
import com.mysql.cj.protocol.Protocol;
import com.mysql.cj.protocol.ProtocolEntity;
import com.mysql.cj.protocol.ProtocolEntityFactory;
import com.mysql.cj.protocol.ResultBuilder;
import com.mysql.cj.protocol.ResultStreamer;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.ServerCapabilities;
import com.mysql.cj.protocol.ServerSession;
import com.mysql.cj.protocol.SocketConnection;
import com.mysql.cj.protocol.ValueEncoder;
import com.mysql.cj.protocol.a.NativeSocketConnection;
import com.mysql.cj.result.DefaultColumnDefinition;
import com.mysql.cj.result.Field;
import com.mysql.cj.result.LongValueFactory;
import com.mysql.cj.result.ValueFactory;
import com.mysql.cj.util.SequentialIdLease;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.x.protobuf.Mysqlx;
import com.mysql.cj.x.protobuf.MysqlxConnection;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import com.mysql.cj.x.protobuf.MysqlxNotice;
import com.mysql.cj.x.protobuf.MysqlxResultset;
import com.mysql.cj.x.protobuf.MysqlxSession;
import com.mysql.cj.x.protobuf.MysqlxSql;
import com.mysql.cj.xdevapi.PreparableStatement;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class XProtocol extends AbstractProtocol<XMessage> implements Protocol<XMessage> {
  private static int RETRY_PREPARE_STATEMENT_COUNTDOWN = 100;
  
  private MessageReader<XMessageHeader, XMessage> reader;
  
  private MessageSender<XMessage> sender;
  
  private Closeable managedResource;
  
  private ResultStreamer currentResultStreamer;
  
  XServerSession serverSession = null;
  
  Boolean useSessionResetKeepOpen = null;
  
  public String defaultSchemaName;
  
  private Map<String, Object> clientCapabilities = new HashMap<>();
  
  private boolean supportsPreparedStatements = true;
  
  private int retryPrepareStatementCountdown = 0;
  
  private SequentialIdLease preparedStatementIds = new SequentialIdLease();
  
  private ReferenceQueue<PreparableStatement<?>> preparableStatementRefQueue = new ReferenceQueue<>();
  
  private Map<Integer, PreparableStatement.PreparableStatementFinalizer> preparableStatementFinalizerReferences = new TreeMap<>();
  
  private boolean compressionEnabled = false;
  
  private CompressionAlgorithm compressionAlgorithm;
  
  private Map<Class<? extends GeneratedMessageV3>, ProtocolEntityFactory<? extends ProtocolEntity, XMessage>> messageToProtocolEntityFactory = new HashMap<>();
  
  private String currUser;
  
  private String currPassword;
  
  private String currDatabase;
  
  public void init(Session sess, SocketConnection socketConn, PropertySet propSet, TransactionEventHandler trManager) {
    super.init(sess, socketConn, propSet, trManager);
    this.log = LogFactory.getLogger(getPropertySet().getStringProperty(PropertyKey.logger).getStringValue(), "MySQL");
    this.messageBuilder = new XMessageBuilder();
    this.authProvider = new XAuthenticationProvider();
    this.authProvider.init(this, propSet, null);
    this.useSessionResetKeepOpen = null;
    this.messageToProtocolEntityFactory.put(MysqlxResultset.ColumnMetaData.class, new FieldFactory("latin1"));
    this.messageToProtocolEntityFactory.put(MysqlxNotice.Frame.class, new NoticeFactory());
    this.messageToProtocolEntityFactory.put(MysqlxResultset.Row.class, new XProtocolRowFactory());
    this.messageToProtocolEntityFactory.put(MysqlxResultset.FetchDoneMoreResultsets.class, new FetchDoneMoreResultsFactory());
    this.messageToProtocolEntityFactory.put(MysqlxResultset.FetchDone.class, new FetchDoneEntityFactory());
    this.messageToProtocolEntityFactory.put(MysqlxSql.StmtExecuteOk.class, new StatementExecuteOkFactory());
    this.messageToProtocolEntityFactory.put(Mysqlx.Ok.class, new OkFactory());
  }
  
  public ServerSession getServerSession() {
    return this.serverSession;
  }
  
  public void sendCapabilities(Map<String, Object> keyValuePair) {
    keyValuePair.forEach((k, v) -> ((XServerCapabilities)getServerSession().getCapabilities()).setCapability(k, v));
    this.sender.send(((XMessageBuilder)this.messageBuilder).buildCapabilitiesSet(keyValuePair));
    readQueryResult(new OkBuilder());
  }
  
  public void negotiateSSLConnection() {
    if (!ExportControlled.enabled())
      throw new CJConnectionFeatureNotAvailableException(); 
    if (!((XServerCapabilities)this.serverSession.getCapabilities()).hasCapability(XServerCapabilities.KEY_TLS))
      throw new CJCommunicationsException("A secure connection is required but the server is not configured with SSL."); 
    this.reader.stopAfterNextMessage();
    Map<String, Object> tlsCapabilities = new HashMap<>();
    tlsCapabilities.put(XServerCapabilities.KEY_TLS, Boolean.valueOf(true));
    sendCapabilities(tlsCapabilities);
    try {
      this.socketConnection.performTlsHandshake(null, this.log);
    } catch (SSLParamsException|com.mysql.cj.exceptions.FeatureNotAvailableException|IOException e) {
      throw new CJCommunicationsException(e);
    } 
    try {
      this.sender = new SyncMessageSender(this.socketConnection.getMysqlOutput());
      this.reader = new SyncMessageReader(this.socketConnection.getMysqlInput(), (Protocol.ProtocolEventHandler)this);
    } catch (IOException e) {
      throw new XProtocolError(e.getMessage(), e);
    } 
  }
  
  public void negotiateCompression() {
    PropertyDefinitions.Compression compression = (PropertyDefinitions.Compression)this.propertySet.getEnumProperty(PropertyKey.xdevapiCompression.getKeyName()).getValue();
    if (compression == PropertyDefinitions.Compression.DISABLED)
      return; 
    Map<String, List<String>> compressionCapabilities = this.serverSession.serverCapabilities.getCompression();
    if (compressionCapabilities.isEmpty() || !compressionCapabilities.containsKey(XServerCapabilities.SUBKEY_COMPRESSION_ALGORITHM) || ((List)compressionCapabilities
      .get(XServerCapabilities.SUBKEY_COMPRESSION_ALGORITHM)).isEmpty()) {
      if (compression == PropertyDefinitions.Compression.REQUIRED)
        throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Protocol.Compression.0")); 
      return;
    } 
    RuntimeProperty<String> compressionAlgorithmsProp = this.propertySet.getStringProperty(PropertyKey.xdevapiCompressionAlgorithms.getKeyName());
    String compressionAlgorithmsList = (String)compressionAlgorithmsProp.getValue();
    compressionAlgorithmsList = (compressionAlgorithmsList == null) ? "" : compressionAlgorithmsList.trim();
    String[] compressionAlgsOrder = compressionAlgorithmsList.split("\\s*,\\s*");
    String[] compressionAlgorithmsOrder = (String[])Arrays.<String>stream(compressionAlgsOrder).sequential().filter(n -> (n != null && !n.isEmpty())).map(String::toLowerCase).map(CompressionAlgorithm::getNormalizedAlgorithmName).toArray(x$0 -> new String[x$0]);
    String compressionExtensions = (String)this.propertySet.getStringProperty(PropertyKey.xdevapiCompressionExtensions.getKeyName()).getValue();
    compressionExtensions = (compressionExtensions == null) ? "" : compressionExtensions.trim();
    Map<String, CompressionAlgorithm> compressionAlgorithms = getCompressionExtensions(compressionExtensions);
    Optional<String> algorithmOpt = Arrays.<String>stream(compressionAlgorithmsOrder).sequential().filter((List)compressionCapabilities.get(XServerCapabilities.SUBKEY_COMPRESSION_ALGORITHM)::contains).filter(compressionAlgorithms::containsKey).findFirst();
    if (!algorithmOpt.isPresent()) {
      if (compression == PropertyDefinitions.Compression.REQUIRED)
        throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Protocol.Compression.2")); 
      return;
    } 
    String algorithm = algorithmOpt.get();
    this.compressionAlgorithm = compressionAlgorithms.get(algorithm);
    Map<String, Object> compressionCap = new HashMap<>();
    compressionCap.put(XServerCapabilities.SUBKEY_COMPRESSION_ALGORITHM, algorithm);
    compressionCap.put(XServerCapabilities.SUBKEY_COMPRESSION_SERVER_COMBINE_MIXED_MESSAGES, Boolean.valueOf(true));
    sendCapabilities(Collections.singletonMap(XServerCapabilities.KEY_COMPRESSION, compressionCap));
    this.compressionEnabled = true;
  }
  
  public void beforeHandshake() {
    this.serverSession = new XServerSession();
    try {
      this.sender = new SyncMessageSender(this.socketConnection.getMysqlOutput());
      this.reader = new SyncMessageReader(this.socketConnection.getMysqlInput(), (Protocol.ProtocolEventHandler)this);
      this.managedResource = this.socketConnection.getMysqlSocket();
    } catch (IOException e) {
      throw new XProtocolError(e.getMessage(), e);
    } 
    this.serverSession.setCapabilities(readServerCapabilities());
    String attributes = (String)this.propertySet.getStringProperty(PropertyKey.xdevapiConnectionAttributes).getValue();
    if (attributes == null || !attributes.equalsIgnoreCase("false")) {
      Map<String, String> attMap = getConnectionAttributesMap("true".equalsIgnoreCase(attributes) ? "" : attributes);
      this.clientCapabilities.put(XServerCapabilities.KEY_SESSION_CONNECT_ATTRS, attMap);
    } 
    RuntimeProperty<PropertyDefinitions.XdevapiSslMode> xdevapiSslMode = this.propertySet.getEnumProperty(PropertyKey.xdevapiSslMode);
    RuntimeProperty<PropertyDefinitions.SslMode> jdbcSslMode = this.propertySet.getEnumProperty(PropertyKey.sslMode);
    if (xdevapiSslMode.isExplicitlySet() || !jdbcSslMode.isExplicitlySet())
      jdbcSslMode.setValue(PropertyDefinitions.SslMode.valueOf(((PropertyDefinitions.XdevapiSslMode)xdevapiSslMode.getValue()).toString())); 
    RuntimeProperty<String> xdevapiSslKeyStoreUrl = this.propertySet.getStringProperty(PropertyKey.xdevapiSslKeyStoreUrl);
    RuntimeProperty<String> jdbcClientCertKeyStoreUrl = this.propertySet.getStringProperty(PropertyKey.clientCertificateKeyStoreUrl);
    if (xdevapiSslKeyStoreUrl.isExplicitlySet() || !jdbcClientCertKeyStoreUrl.isExplicitlySet())
      jdbcClientCertKeyStoreUrl.setValue(xdevapiSslKeyStoreUrl.getValue()); 
    RuntimeProperty<String> xdevapiSslKeyStoreType = this.propertySet.getStringProperty(PropertyKey.xdevapiSslKeyStoreType);
    RuntimeProperty<String> jdbcClientCertKeyStoreType = this.propertySet.getStringProperty(PropertyKey.clientCertificateKeyStoreType);
    if (xdevapiSslKeyStoreType.isExplicitlySet() || !jdbcClientCertKeyStoreType.isExplicitlySet())
      jdbcClientCertKeyStoreType.setValue(xdevapiSslKeyStoreType.getValue()); 
    RuntimeProperty<String> xdevapiSslKeyStorePassword = this.propertySet.getStringProperty(PropertyKey.xdevapiSslKeyStorePassword);
    RuntimeProperty<String> jdbcClientCertKeyStorePassword = this.propertySet.getStringProperty(PropertyKey.clientCertificateKeyStorePassword);
    if (xdevapiSslKeyStorePassword.isExplicitlySet() || !jdbcClientCertKeyStorePassword.isExplicitlySet())
      jdbcClientCertKeyStorePassword.setValue(xdevapiSslKeyStorePassword.getValue()); 
    RuntimeProperty<Boolean> xdevapiFallbackToSystemKeyStore = this.propertySet.getBooleanProperty(PropertyKey.xdevapiFallbackToSystemKeyStore);
    RuntimeProperty<Boolean> jdbcFallbackToSystemKeyStore = this.propertySet.getBooleanProperty(PropertyKey.fallbackToSystemKeyStore);
    if (xdevapiFallbackToSystemKeyStore.isExplicitlySet() || !jdbcFallbackToSystemKeyStore.isExplicitlySet())
      jdbcFallbackToSystemKeyStore.setValue(xdevapiFallbackToSystemKeyStore.getValue()); 
    RuntimeProperty<String> xdevapiSslTrustStoreUrl = this.propertySet.getStringProperty(PropertyKey.xdevapiSslTrustStoreUrl);
    RuntimeProperty<String> jdbcTrustCertKeyStoreUrl = this.propertySet.getStringProperty(PropertyKey.trustCertificateKeyStoreUrl);
    if (xdevapiSslTrustStoreUrl.isExplicitlySet() || !jdbcTrustCertKeyStoreUrl.isExplicitlySet())
      jdbcTrustCertKeyStoreUrl.setValue(xdevapiSslTrustStoreUrl.getValue()); 
    RuntimeProperty<String> xdevapiSslTrustStoreType = this.propertySet.getStringProperty(PropertyKey.xdevapiSslTrustStoreType);
    RuntimeProperty<String> jdbcTrustCertKeyStoreType = this.propertySet.getStringProperty(PropertyKey.trustCertificateKeyStoreType);
    if (xdevapiSslTrustStoreType.isExplicitlySet() || !jdbcTrustCertKeyStoreType.isExplicitlySet())
      jdbcTrustCertKeyStoreType.setValue(xdevapiSslTrustStoreType.getValue()); 
    RuntimeProperty<String> xdevapiSslTrustStorePassword = this.propertySet.getStringProperty(PropertyKey.xdevapiSslTrustStorePassword);
    RuntimeProperty<String> jdbcTrustCertKeyStorePassword = this.propertySet.getStringProperty(PropertyKey.trustCertificateKeyStorePassword);
    if (xdevapiSslTrustStorePassword.isExplicitlySet() || !jdbcTrustCertKeyStorePassword.isExplicitlySet())
      jdbcTrustCertKeyStorePassword.setValue(xdevapiSslTrustStorePassword.getValue()); 
    RuntimeProperty<Boolean> xdevapiFallbackToSystemTrustStore = this.propertySet.getBooleanProperty(PropertyKey.xdevapiFallbackToSystemTrustStore);
    RuntimeProperty<Boolean> jdbcFallbackToSystemTrustStore = this.propertySet.getBooleanProperty(PropertyKey.fallbackToSystemTrustStore);
    if (xdevapiFallbackToSystemTrustStore.isExplicitlySet() || !jdbcFallbackToSystemTrustStore.isExplicitlySet())
      jdbcFallbackToSystemTrustStore.setValue(xdevapiFallbackToSystemTrustStore.getValue()); 
    RuntimeProperty<PropertyDefinitions.SslMode> sslMode = jdbcSslMode;
    if (sslMode.getValue() == PropertyDefinitions.SslMode.PREFERRED)
      sslMode.setValue(PropertyDefinitions.SslMode.REQUIRED); 
    if (sslMode.getValue() != PropertyDefinitions.SslMode.DISABLED) {
      RuntimeProperty<String> xdevapiTlsVersions = this.propertySet.getStringProperty(PropertyKey.xdevapiTlsVersions);
      RuntimeProperty<String> jdbcEnabledTlsProtocols = this.propertySet.getStringProperty(PropertyKey.tlsVersions);
      if (xdevapiTlsVersions.isExplicitlySet()) {
        String[] tlsVersions = ((String)xdevapiTlsVersions.getValue()).split("\\s*,\\s*");
        List<String> tryProtocols = Arrays.asList(tlsVersions);
        ExportControlled.checkValidProtocols(tryProtocols);
        jdbcEnabledTlsProtocols.setValue(xdevapiTlsVersions.getValue());
      } 
      RuntimeProperty<String> xdevapiTlsCiphersuites = this.propertySet.getStringProperty(PropertyKey.xdevapiTlsCiphersuites);
      RuntimeProperty<String> jdbcEnabledSslCipherSuites = this.propertySet.getStringProperty(PropertyKey.tlsCiphersuites);
      if (xdevapiTlsCiphersuites.isExplicitlySet())
        jdbcEnabledSslCipherSuites.setValue(xdevapiTlsCiphersuites.getValue()); 
    } 
    if (this.clientCapabilities.size() > 0)
      try {
        sendCapabilities(this.clientCapabilities);
      } catch (XProtocolError e) {
        if (e.getErrorCode() != 5002 && 
          !e.getMessage().contains(XServerCapabilities.KEY_SESSION_CONNECT_ATTRS))
          throw e; 
        this.clientCapabilities.remove(XServerCapabilities.KEY_SESSION_CONNECT_ATTRS);
      }  
    if (jdbcSslMode.getValue() != PropertyDefinitions.SslMode.DISABLED)
      negotiateSSLConnection(); 
    negotiateCompression();
  }
  
  private Map<String, String> getConnectionAttributesMap(String attStr) {
    Map<String, String> attMap = new HashMap<>();
    if (attStr != null) {
      if (attStr.startsWith("[") && attStr.endsWith("]"))
        attStr = attStr.substring(1, attStr.length() - 1); 
      if (!StringUtils.isNullOrEmpty(attStr)) {
        String[] pairs = attStr.split(",");
        for (String pair : pairs) {
          String[] kv = pair.split("=");
          String key = kv[0].trim();
          String value = (kv.length > 1) ? kv[1].trim() : "";
          if (key.startsWith("_"))
            throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Protocol.WrongAttributeName")); 
          if (attMap.put(key, value) != null)
            throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
                Messages.getString("Protocol.DuplicateAttribute", new Object[] { key })); 
        } 
      } 
    } 
    attMap.put("_platform", Constants.OS_ARCH);
    attMap.put("_os", Constants.OS_NAME + "-" + Constants.OS_VERSION);
    attMap.put("_client_name", "MySQL Connector/J");
    attMap.put("_client_version", "8.0.33");
    attMap.put("_client_license", "GPL");
    attMap.put("_runtime_version", Constants.JVM_VERSION);
    attMap.put("_runtime_vendor", Constants.JVM_VENDOR);
    return attMap;
  }
  
  private Map<String, CompressionAlgorithm> getCompressionExtensions(String compressionExtensions) {
    Map<String, CompressionAlgorithm> compressionExtensionsMap = CompressionAlgorithm.getDefaultInstances();
    if (compressionExtensions.length() == 0)
      return compressionExtensionsMap; 
    String[] compressionExtAlgs = compressionExtensions.split(",");
    for (String compressionExtAlg : compressionExtAlgs) {
      String[] compressionExtAlgParts = compressionExtAlg.split(":");
      if (compressionExtAlgParts.length != 3)
        throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Protocol.Compression.1")); 
      String algorithmName = compressionExtAlgParts[0].toLowerCase();
      String inputStreamClassName = compressionExtAlgParts[1];
      String outputStreamClassName = compressionExtAlgParts[2];
      CompressionAlgorithm compressionAlg = new CompressionAlgorithm(algorithmName, inputStreamClassName, outputStreamClassName);
      compressionExtensionsMap.put(compressionAlg.getAlgorithmIdentifier(), compressionAlg);
    } 
    return compressionExtensionsMap;
  }
  
  public XProtocol(HostInfo hostInfo, PropertySet propertySet) {
    this.currUser = null;
    this.currPassword = null;
    this.currDatabase = null;
    String host = hostInfo.getHost();
    if (host == null || StringUtils.isEmptyOrWhitespaceOnly(host))
      host = "localhost"; 
    int port = hostInfo.getPort();
    if (port < 0)
      port = 33060; 
    this.defaultSchemaName = hostInfo.getDatabase();
    RuntimeProperty<Integer> connectTimeout = propertySet.getIntegerProperty(PropertyKey.connectTimeout);
    RuntimeProperty<Integer> xdevapiConnectTimeout = propertySet.getIntegerProperty(PropertyKey.xdevapiConnectTimeout);
    if (xdevapiConnectTimeout.isExplicitlySet() || !connectTimeout.isExplicitlySet())
      connectTimeout.setValue(xdevapiConnectTimeout.getValue()); 
    NativeSocketConnection nativeSocketConnection = new NativeSocketConnection();
    nativeSocketConnection.connect(host, port, propertySet, null, null, 0);
    init((Session)null, (SocketConnection)nativeSocketConnection, propertySet, (TransactionEventHandler)null);
  }
  
  public void connect(String user, String password, String database) {
    this.currUser = user;
    this.currPassword = password;
    this.currDatabase = database;
    beforeHandshake();
    this.authProvider.connect(user, password, database);
  }
  
  public void changeUser(String user, String password, String database) {
    this.currUser = user;
    this.currPassword = password;
    this.currDatabase = database;
    this.authProvider.changeUser(user, password, database);
  }
  
  public void afterHandshake() {
    if (this.compressionEnabled) {
      try {
        this
          .reader = new SyncMessageReader(new FullReadInputStream(new CompressionSplittedInputStream((InputStream)this.socketConnection.getMysqlInput(), new CompressorStreamsFactory(this.compressionAlgorithm))), (Protocol.ProtocolEventHandler)this);
      } catch (IOException e) {
        ExceptionFactory.createException(Messages.getString("Protocol.Compression.6"), e);
      } 
      try {
        this
          .sender = new SyncMessageSender(new CompressionSplittedOutputStream(this.socketConnection.getMysqlOutput(), new CompressorStreamsFactory(this.compressionAlgorithm)));
      } catch (IOException e) {
        ExceptionFactory.createException(Messages.getString("Protocol.Compression.7"), e);
      } 
    } 
    initServerSession();
  }
  
  public void configureTimeZone() {}
  
  public void initServerSession() {
    configureTimeZone();
    send(this.messageBuilder.buildSqlStatement("select @@mysqlx_max_allowed_packet"), 0);
    ColumnDefinition metadata = readMetadata();
    long count = ((Long)(new XProtocolRowInputStream(metadata, this, null)).next().getValue(0, (ValueFactory)new LongValueFactory(this.propertySet))).longValue();
    readQueryResult(new StatementExecuteOkBuilder());
    setMaxAllowedPacket((int)count);
  }
  
  public void readAuthenticateOk() {
    try {
      XMessage mess = (XMessage)this.reader.readMessage(null, 4);
      if (mess != null && mess.getNotices() != null)
        for (Notice notice : mess.getNotices()) {
          if (notice instanceof Notice.XSessionStateChanged)
            switch (((Notice.XSessionStateChanged)notice).getParamType().intValue()) {
              case 11:
                getServerSession().getCapabilities().setThreadId(((Notice.XSessionStateChanged)notice).getValue().getVUnsignedInt());
            }  
        }  
    } catch (IOException e) {
      throw new XProtocolError(e.getMessage(), e);
    } 
  }
  
  public byte[] readAuthenticateContinue() {
    try {
      MysqlxSession.AuthenticateContinue msg = (MysqlxSession.AuthenticateContinue)((XMessage)this.reader.readMessage(null, 3)).getMessage();
      byte[] data = msg.getAuthData().toByteArray();
      if (data.length != 20)
        throw AssertionFailedException.shouldNotHappen("Salt length should be 20, but is " + data.length); 
      return data;
    } catch (IOException e) {
      throw new XProtocolError(e.getMessage(), e);
    } 
  }
  
  public boolean hasMoreResults() {
    try {
      if (((SyncMessageReader)this.reader).getNextNonNoticeMessageType() == 16) {
        this.reader.readMessage(null, 16);
        if (((SyncMessageReader)this.reader).getNextNonNoticeMessageType() == 14)
          return false; 
        return true;
      } 
      return false;
    } catch (IOException e) {
      throw new XProtocolError(e.getMessage(), e);
    } 
  }
  
  public <T extends com.mysql.cj.QueryResult> T readQueryResult(ResultBuilder<T> resultBuilder) {
    try {
      boolean done = false;
      while (!done) {
        XMessageHeader header = (XMessageHeader)this.reader.readHeader();
        XMessage mess = (XMessage)this.reader.readMessage(null, header);
        Class<? extends GeneratedMessageV3> msgClass = (Class)mess.getMessage().getClass();
        if (Mysqlx.Error.class.equals(msgClass))
          throw new XProtocolError((Mysqlx.Error)Mysqlx.Error.class.cast(mess.getMessage())); 
        if (!this.messageToProtocolEntityFactory.containsKey(msgClass))
          throw new WrongArgumentException("Unhandled msg class (" + msgClass + ") + msg=" + mess.getMessage()); 
        List<Notice> notices;
        if ((notices = mess.getNotices()) != null)
          notices.stream().forEach(resultBuilder::addProtocolEntity); 
        done = resultBuilder.addProtocolEntity((ProtocolEntity)((ProtocolEntityFactory)this.messageToProtocolEntityFactory.get(msgClass)).createFromMessage(mess));
      } 
      return (T)resultBuilder.build();
    } catch (IOException e) {
      throw new XProtocolError(e.getMessage(), e);
    } 
  }
  
  public boolean hasResults() {
    try {
      return (((SyncMessageReader)this.reader).getNextNonNoticeMessageType() == 12);
    } catch (IOException e) {
      throw new XProtocolError(e.getMessage(), e);
    } 
  }
  
  public void drainRows() {
    try {
      while (((SyncMessageReader)this.reader).getNextNonNoticeMessageType() == 13)
        this.reader.readMessage(null, 13); 
    } catch (XProtocolError e) {
      this.currentResultStreamer = null;
      throw e;
    } catch (IOException e) {
      this.currentResultStreamer = null;
      throw new XProtocolError(e.getMessage(), e);
    } 
  }
  
  public ColumnDefinition readMetadata() {
    return readMetadata((Consumer<Notice>)null);
  }
  
  public ColumnDefinition readMetadata(Consumer<Notice> noticeConsumer) {
    try {
      List<MysqlxResultset.ColumnMetaData> fromServer = new LinkedList<>();
      while (true) {
        XMessage mess = (XMessage)this.reader.readMessage(null, 12);
        List<Notice> notices;
        if (noticeConsumer != null && (notices = mess.getNotices()) != null)
          notices.stream().forEach(noticeConsumer::accept); 
        fromServer.add((MysqlxResultset.ColumnMetaData)mess.getMessage());
        if (((SyncMessageReader)this.reader).getNextNonNoticeMessageType() != 12) {
          ArrayList<Field> metadata = new ArrayList<>(fromServer.size());
          ProtocolEntityFactory<Field, XMessage> fieldFactory = (ProtocolEntityFactory<Field, XMessage>)this.messageToProtocolEntityFactory.get(MysqlxResultset.ColumnMetaData.class);
          fromServer.forEach(col -> metadata.add(fieldFactory.createFromMessage(new XMessage((Message)col))));
          return (ColumnDefinition)new DefaultColumnDefinition(metadata.<Field>toArray(new Field[0]));
        } 
      } 
    } catch (IOException e) {
      throw new XProtocolError(e.getMessage(), e);
    } 
  }
  
  public ColumnDefinition readMetadata(Field f, Consumer<Notice> noticeConsumer) {
    try {
      List<MysqlxResultset.ColumnMetaData> fromServer = new LinkedList<>();
      while (((SyncMessageReader)this.reader).getNextNonNoticeMessageType() == 12) {
        XMessage mess = (XMessage)this.reader.readMessage(null, 12);
        List<Notice> notices;
        if (noticeConsumer != null && (notices = mess.getNotices()) != null)
          notices.stream().forEach(noticeConsumer::accept); 
        fromServer.add((MysqlxResultset.ColumnMetaData)mess.getMessage());
      } 
      ArrayList<Field> metadata = new ArrayList<>(fromServer.size());
      metadata.add(f);
      ProtocolEntityFactory<Field, XMessage> fieldFactory = (ProtocolEntityFactory<Field, XMessage>)this.messageToProtocolEntityFactory.get(MysqlxResultset.ColumnMetaData.class);
      fromServer.forEach(col -> metadata.add(fieldFactory.createFromMessage(new XMessage((Message)col))));
      return (ColumnDefinition)new DefaultColumnDefinition(metadata.<Field>toArray(new Field[0]));
    } catch (IOException e) {
      throw new XProtocolError(e.getMessage(), e);
    } 
  }
  
  public XProtocolRow readRowOrNull(ColumnDefinition metadata, Consumer<Notice> noticeConsumer) {
    try {
      if (((SyncMessageReader)this.reader).getNextNonNoticeMessageType() == 13) {
        XMessage mess = (XMessage)this.reader.readMessage(null, 13);
        List<Notice> notices;
        if (noticeConsumer != null && (notices = mess.getNotices()) != null)
          notices.stream().forEach(noticeConsumer::accept); 
        XProtocolRow res = new XProtocolRow((MysqlxResultset.Row)mess.getMessage());
        res.setMetadata(metadata);
        return res;
      } 
      return null;
    } catch (XProtocolError e) {
      this.currentResultStreamer = null;
      throw e;
    } catch (IOException e) {
      this.currentResultStreamer = null;
      throw new XProtocolError(e.getMessage(), e);
    } 
  }
  
  public boolean supportsPreparedStatements() {
    return this.supportsPreparedStatements;
  }
  
  public boolean readyForPreparingStatements() {
    if (this.retryPrepareStatementCountdown == 0)
      return true; 
    this.retryPrepareStatementCountdown--;
    return false;
  }
  
  public int getNewPreparedStatementId(PreparableStatement<?> preparableStatement) {
    if (!this.supportsPreparedStatements)
      throw new XProtocolError("The connected MySQL server does not support prepared statements."); 
    int preparedStatementId = this.preparedStatementIds.allocateSequentialId();
    this.preparableStatementFinalizerReferences.put(Integer.valueOf(preparedStatementId), new PreparableStatement.PreparableStatementFinalizer(preparableStatement, this.preparableStatementRefQueue, preparedStatementId));
    return preparedStatementId;
  }
  
  public void freePreparedStatementId(int preparedStatementId) {
    if (!this.supportsPreparedStatements)
      throw new XProtocolError("The connected MySQL server does not support prepared statements."); 
    this.preparedStatementIds.releaseSequentialId(preparedStatementId);
    this.preparableStatementFinalizerReferences.remove(Integer.valueOf(preparedStatementId));
  }
  
  public boolean failedPreparingStatement(int preparedStatementId, XProtocolError e) {
    freePreparedStatementId(preparedStatementId);
    if (e.getErrorCode() == 1461) {
      this.retryPrepareStatementCountdown = RETRY_PREPARE_STATEMENT_COUNTDOWN;
      return true;
    } 
    if (e.getErrorCode() == 1047 && this.preparableStatementFinalizerReferences.isEmpty()) {
      this.supportsPreparedStatements = false;
      this.retryPrepareStatementCountdown = 0;
      this.preparedStatementIds = null;
      this.preparableStatementRefQueue = null;
      this.preparableStatementFinalizerReferences = null;
      return true;
    } 
    return false;
  }
  
  protected void newCommand() {
    if (this.currentResultStreamer != null)
      try {
        this.currentResultStreamer.finishStreaming();
      } finally {
        this.currentResultStreamer = null;
      }  
    if (this.supportsPreparedStatements) {
      Reference<? extends PreparableStatement<?>> ref;
      while ((ref = this.preparableStatementRefQueue.poll()) != null) {
        PreparableStatement.PreparableStatementFinalizer psf = (PreparableStatement.PreparableStatementFinalizer)ref;
        psf.clear();
        try {
          this.sender.send(((XMessageBuilder)this.messageBuilder).buildPrepareDeallocate(psf.getPreparedStatementId()));
          readQueryResult(new OkBuilder());
        } catch (XProtocolError e) {
          if (e.getErrorCode() != 5110)
            throw e; 
        } finally {
          freePreparedStatementId(psf.getPreparedStatementId());
        } 
      } 
    } 
  }
  
  public <M extends Message, R extends com.mysql.cj.QueryResult> R query(M message, ResultBuilder<R> resultBuilder) {
    send((Message)message, 0);
    R res = readQueryResult(resultBuilder);
    if (ResultStreamer.class.isAssignableFrom(res.getClass()))
      this.currentResultStreamer = (ResultStreamer)res; 
    return res;
  }
  
  public <M extends Message, R extends com.mysql.cj.QueryResult> CompletableFuture<R> queryAsync(M message, ResultBuilder<R> resultBuilder) {
    newCommand();
    CompletableFuture<R> f = new CompletableFuture<>();
    MessageListener<XMessage> l = new ResultMessageListener<>(this.messageToProtocolEntityFactory, resultBuilder, f);
    this.sender.send((XMessage)message, f, () -> this.reader.pushMessageListener(l));
    return f;
  }
  
  public boolean isOpen() {
    return (this.managedResource != null);
  }
  
  public void close() throws IOException {
    try {
      send(this.messageBuilder.buildClose(), 0);
      readQueryResult(new OkBuilder());
    } catch (Exception exception) {
      try {
        if (this.managedResource == null)
          throw new ConnectionIsClosedException(); 
        this.managedResource.close();
        this.managedResource = null;
      } catch (IOException ex) {
        throw new CJCommunicationsException(ex);
      } 
    } finally {
      try {
        if (this.managedResource == null)
          throw new ConnectionIsClosedException(); 
        this.managedResource.close();
        this.managedResource = null;
      } catch (IOException ex) {
        throw new CJCommunicationsException(ex);
      } 
    } 
  }
  
  public boolean isSqlResultPending() {
    try {
      switch (((SyncMessageReader)this.reader).getNextNonNoticeMessageType()) {
        case 12:
          return true;
        case 16:
          this.reader.readMessage(null, 16);
          break;
      } 
      return false;
    } catch (IOException e) {
      throw new XProtocolError(e.getMessage(), e);
    } 
  }
  
  public void setMaxAllowedPacket(int maxAllowedPacket) {
    this.sender.setMaxAllowedPacket(maxAllowedPacket);
  }
  
  public void send(Message message, int packetLen) {
    newCommand();
    this.sender.send(message);
  }
  
  public ServerCapabilities readServerCapabilities() {
    try {
      this.sender.send(((XMessageBuilder)this.messageBuilder).buildCapabilitiesGet());
      return new XServerCapabilities((Map<String, MysqlxDatatypes.Any>)((MysqlxConnection.Capabilities)((XMessage)this.reader.readMessage(null, 2)).getMessage())
          .getCapabilitiesList().stream().collect(Collectors.toMap(MysqlxConnection.Capability::getName, MysqlxConnection.Capability::getValue)));
    } catch (IOException|AssertionFailedException e) {
      throw new XProtocolError(e.getMessage(), e);
    } 
  }
  
  public void reset() {
    newCommand();
    this.propertySet.reset();
    if (this.useSessionResetKeepOpen == null)
      try {
        send(((XMessageBuilder)this.messageBuilder).buildExpectOpen(), 0);
        readQueryResult(new OkBuilder());
        this.useSessionResetKeepOpen = Boolean.valueOf(true);
      } catch (XProtocolError e) {
        if (e.getErrorCode() != 5168 && e
          .getErrorCode() != 5160)
          throw e; 
        this.useSessionResetKeepOpen = Boolean.valueOf(false);
      }  
    if (this.useSessionResetKeepOpen.booleanValue()) {
      send(((XMessageBuilder)this.messageBuilder).buildSessionResetKeepOpen(), 0);
      readQueryResult(new OkBuilder());
    } else {
      send(((XMessageBuilder)this.messageBuilder).buildSessionResetAndClose(), 0);
      readQueryResult(new OkBuilder());
      if (this.clientCapabilities.containsKey(XServerCapabilities.KEY_SESSION_CONNECT_ATTRS)) {
        Map<String, Object> reducedClientCapabilities = new HashMap<>();
        reducedClientCapabilities.put(XServerCapabilities.KEY_SESSION_CONNECT_ATTRS, this.clientCapabilities
            .get(XServerCapabilities.KEY_SESSION_CONNECT_ATTRS));
        if (reducedClientCapabilities.size() > 0)
          sendCapabilities(reducedClientCapabilities); 
      } 
      this.authProvider.changeUser(this.currUser, this.currPassword, this.currDatabase);
    } 
    if (this.supportsPreparedStatements) {
      this.retryPrepareStatementCountdown = 0;
      this.preparedStatementIds = new SequentialIdLease();
      this.preparableStatementRefQueue = new ReferenceQueue<>();
      this.preparableStatementFinalizerReferences = new TreeMap<>();
    } 
  }
  
  public ExceptionInterceptor getExceptionInterceptor() {
    throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
  }
  
  public void changeDatabase(String database) {
    throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
  }
  
  public boolean versionMeetsMinimum(int major, int minor, int subminor) {
    throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
  }
  
  public XMessage readMessage(XMessage reuse) {
    throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
  }
  
  public XMessage checkErrorMessage() {
    throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
  }
  
  public XMessage sendCommand(Message queryPacket, boolean skipCheck, int timeoutMillis) {
    throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
  }
  
  public <T extends ProtocolEntity> T read(Class<T> requiredClass, ProtocolEntityFactory<T, XMessage> protocolEntityFactory) throws IOException {
    throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
  }
  
  public <T extends ProtocolEntity> T read(Class<Resultset> requiredClass, int maxRows, boolean streamResults, XMessage resultPacket, boolean isBinaryEncoded, ColumnDefinition metadata, ProtocolEntityFactory<T, XMessage> protocolEntityFactory) throws IOException {
    throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
  }
  
  public void setLocalInfileInputStream(InputStream stream) {
    throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
  }
  
  public InputStream getLocalInfileInputStream() {
    throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
  }
  
  public String getQueryComment() {
    throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
  }
  
  public void setQueryComment(String comment) {
    throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
  }
  
  public Supplier<ValueEncoder> getValueEncoderSupplier(Object obj) {
    throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
  }
}
