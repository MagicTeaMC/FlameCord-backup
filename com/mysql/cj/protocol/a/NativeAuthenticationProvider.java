package com.mysql.cj.protocol.a;

import com.mysql.cj.Constants;
import com.mysql.cj.Messages;
import com.mysql.cj.callback.MysqlCallback;
import com.mysql.cj.callback.MysqlCallbackHandler;
import com.mysql.cj.callback.UsernameCallback;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.UnableToConnectException;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.AuthenticationPlugin;
import com.mysql.cj.protocol.AuthenticationProvider;
import com.mysql.cj.protocol.Protocol;
import com.mysql.cj.protocol.ServerSession;
import com.mysql.cj.protocol.a.authentication.AuthenticationFidoClient;
import com.mysql.cj.protocol.a.authentication.AuthenticationKerberosClient;
import com.mysql.cj.protocol.a.authentication.AuthenticationLdapSaslClientPlugin;
import com.mysql.cj.protocol.a.authentication.AuthenticationOciClient;
import com.mysql.cj.protocol.a.authentication.CachingSha2PasswordPlugin;
import com.mysql.cj.protocol.a.authentication.MysqlClearPasswordPlugin;
import com.mysql.cj.protocol.a.authentication.MysqlNativePasswordPlugin;
import com.mysql.cj.protocol.a.authentication.MysqlOldPasswordPlugin;
import com.mysql.cj.protocol.a.authentication.Sha256PasswordPlugin;
import com.mysql.cj.protocol.a.result.OkPacket;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NativeAuthenticationProvider implements AuthenticationProvider<NativePacketPayload> {
  private static final int AUTH_411_OVERHEAD = 33;
  
  private static final String NONE = "none";
  
  private String seed;
  
  private String username;
  
  private String password;
  
  private String database;
  
  private boolean useConnectWithDb;
  
  private ExceptionInterceptor exceptionInterceptor;
  
  private PropertySet propertySet;
  
  private Protocol<NativePacketPayload> protocol;
  
  private Map<String, AuthenticationPlugin<NativePacketPayload>> authenticationPlugins = null;
  
  private String clientDefaultAuthenticationPluginName = null;
  
  private boolean clientDefaultAuthenticationPluginExplicitelySet = false;
  
  private String serverDefaultAuthenticationPluginName = null;
  
  private MysqlCallbackHandler callbackHandler;
  
  public NativeAuthenticationProvider() {
    this.callbackHandler = (cb -> {
        if (cb instanceof UsernameCallback)
          this.username = ((UsernameCallback)cb).getUsername(); 
      });
  }
  
  public void init(Protocol<NativePacketPayload> prot, PropertySet propSet, ExceptionInterceptor excInterceptor) {
    this.protocol = prot;
    this.propertySet = propSet;
    this.exceptionInterceptor = excInterceptor;
  }
  
  public void connect(String user, String pass, String db) {
    ServerSession sessState = this.protocol.getServerSession();
    this.username = user;
    this.password = pass;
    this.database = db;
    NativeCapabilities capabilities = (NativeCapabilities)sessState.getCapabilities();
    NativePacketPayload buf = capabilities.getInitialHandshakePacket();
    PropertyDefinitions.SslMode sslMode = (PropertyDefinitions.SslMode)this.propertySet.getEnumProperty(PropertyKey.sslMode).getValue();
    int capabilityFlags = capabilities.getCapabilityFlags();
    if ((capabilityFlags & 0x800) == 0 && sslMode != PropertyDefinitions.SslMode.DISABLED && sslMode != PropertyDefinitions.SslMode.PREFERRED)
      throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, Messages.getString("MysqlIO.15"), getExceptionInterceptor()); 
    if ((capabilityFlags & 0x8000) == 0)
      throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, "CLIENT_SECURE_CONNECTION is required", getExceptionInterceptor()); 
    if ((capabilityFlags & 0x80000) == 0)
      throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, "CLIENT_PLUGIN_AUTH is required", getExceptionInterceptor()); 
    sessState.setStatusFlags(capabilities.getStatusFlags());
    int authPluginDataLength = capabilities.getAuthPluginDataLength();
    StringBuilder fullSeed = new StringBuilder((authPluginDataLength > 0) ? authPluginDataLength : 20);
    fullSeed.append(capabilities.getSeed());
    fullSeed.append((authPluginDataLength > 0) ? buf
        .readString(NativeConstants.StringLengthDataType.STRING_FIXED, "ASCII", authPluginDataLength - 8) : buf.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII"));
    this.seed = fullSeed.toString();
    this
      .useConnectWithDb = (this.database != null && this.database.length() > 0 && !((Boolean)this.propertySet.getBooleanProperty(PropertyKey.createDatabaseIfNotExist).getValue()).booleanValue());
    long clientParam = (capabilityFlags & 0x1 | (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.useAffectedRows).getValue()).booleanValue() ? 0 : (capabilityFlags & 0x2)) | capabilityFlags & 0x4 | (this.useConnectWithDb ? (capabilityFlags & 0x8) : 0) | (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.useCompression).getValue()).booleanValue() ? (capabilityFlags & 0x20) : 0) | ((((Boolean)this.propertySet.getBooleanProperty(PropertyKey.allowLoadLocalInfile).getValue()).booleanValue() || this.propertySet.getStringProperty(PropertyKey.allowLoadLocalInfileInPath).isExplicitlySet()) ? (capabilityFlags & 0x80) : 0) | capabilityFlags & 0x200 | (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.interactiveClient).getValue()).booleanValue() ? (capabilityFlags & 0x400) : 0) | ((this.propertySet.getEnumProperty(PropertyKey.sslMode).getValue() != PropertyDefinitions.SslMode.DISABLED) ? (capabilityFlags & 0x800) : 0) | capabilityFlags & 0x2000 | 0x8000 | (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.allowMultiQueries).getValue()).booleanValue() ? (capabilityFlags & 0x10000) : 0) | capabilityFlags & 0x20000 | capabilityFlags & 0x40000 | 0x80000 | ("none".equals(this.propertySet.getStringProperty(PropertyKey.connectionAttributes).getValue()) ? 0 : (capabilityFlags & 0x100000)) | capabilityFlags & 0x200000 | (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.disconnectOnExpiredPasswords).getValue()).booleanValue() ? 0 : (capabilityFlags & 0x400000)) | (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.trackSessionState).getValue()).booleanValue() ? (capabilityFlags & 0x800000) : 0) | capabilityFlags & 0x1000000 | capabilityFlags & 0x8000000 | capabilityFlags & 0x10000000);
    sessState.setClientParam(clientParam);
    if ((clientParam & 0x800L) != 0L)
      this.protocol.negotiateSSLConnection(); 
    if (buf.isOKPacket())
      throw ExceptionFactory.createException(Messages.getString("AuthenticationProvider.UnexpectedAuthenticationApproval"), getExceptionInterceptor()); 
    proceedHandshakeWithPluggableAuthentication(buf);
    this.password = null;
  }
  
  private void loadAuthenticationPlugins() {
    List<String> disabledAuthenticationPlugins;
    RuntimeProperty<String> defaultAuthenticationPluginProp = this.propertySet.getStringProperty(PropertyKey.defaultAuthenticationPlugin);
    String defaultAuthenticationPluginValue = (String)defaultAuthenticationPluginProp.getValue();
    if (defaultAuthenticationPluginValue == null || "".equals(defaultAuthenticationPluginValue.trim()))
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
          Messages.getString("AuthenticationProvider.BadDefaultAuthenticationPlugin", new Object[] { defaultAuthenticationPluginValue }), getExceptionInterceptor()); 
    String disabledPlugins = (String)this.propertySet.getStringProperty(PropertyKey.disabledAuthenticationPlugins).getValue();
    if (disabledPlugins != null && !"".equals(disabledPlugins)) {
      disabledAuthenticationPlugins = StringUtils.split(disabledPlugins, ",", true);
    } else {
      disabledAuthenticationPlugins = Collections.EMPTY_LIST;
    } 
    this.authenticationPlugins = new HashMap<>();
    List<AuthenticationPlugin<NativePacketPayload>> pluginsToInit = new LinkedList<>();
    pluginsToInit.add(new MysqlNativePasswordPlugin());
    pluginsToInit.add(new MysqlClearPasswordPlugin());
    pluginsToInit.add(new Sha256PasswordPlugin());
    pluginsToInit.add(new CachingSha2PasswordPlugin());
    pluginsToInit.add(new MysqlOldPasswordPlugin());
    pluginsToInit.add(new AuthenticationLdapSaslClientPlugin());
    pluginsToInit.add(new AuthenticationKerberosClient());
    pluginsToInit.add(new AuthenticationOciClient());
    pluginsToInit.add(new AuthenticationFidoClient());
    String authenticationPluginClasses = (String)this.propertySet.getStringProperty(PropertyKey.authenticationPlugins).getValue();
    if (authenticationPluginClasses != null && !"".equals(authenticationPluginClasses.trim())) {
      List<String> pluginsToCreate = StringUtils.split(authenticationPluginClasses, ",", true);
      for (String className : pluginsToCreate)
        pluginsToInit.add(Util.getInstance(AuthenticationPlugin.class, className, null, null, getExceptionInterceptor())); 
    } 
    boolean defaultFound = false;
    for (AuthenticationPlugin<NativePacketPayload> plugin : pluginsToInit) {
      String pluginProtocolName = plugin.getProtocolPluginName();
      String pluginClassName = plugin.getClass().getName();
      boolean disabledByProtocolName = disabledAuthenticationPlugins.contains(pluginProtocolName);
      boolean disabledByClassName = disabledAuthenticationPlugins.contains(pluginClassName);
      if (disabledByProtocolName || disabledByClassName) {
        if (!defaultFound && (defaultAuthenticationPluginValue
          .equals(pluginProtocolName) || defaultAuthenticationPluginValue.equals(pluginClassName)))
          throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
              Messages.getString("AuthenticationProvider.BadDisabledAuthenticationPlugin", new Object[] { disabledByClassName ? pluginClassName : pluginProtocolName }), getExceptionInterceptor()); 
        continue;
      } 
      this.authenticationPlugins.put(pluginProtocolName, plugin);
      if (!defaultFound && (defaultAuthenticationPluginValue
        .equals(pluginProtocolName) || defaultAuthenticationPluginValue.equals(pluginClassName))) {
        this.clientDefaultAuthenticationPluginName = pluginProtocolName;
        this.clientDefaultAuthenticationPluginExplicitelySet = defaultAuthenticationPluginProp.isExplicitlySet();
        defaultFound = true;
      } 
    } 
    if (!defaultFound)
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
          Messages.getString("AuthenticationProvider.DefaultAuthenticationPluginIsNotListed", new Object[] { defaultAuthenticationPluginValue }), getExceptionInterceptor()); 
  }
  
  private AuthenticationPlugin<NativePacketPayload> getAuthenticationPlugin(String pluginName) {
    AuthenticationPlugin<NativePacketPayload> plugin = this.authenticationPlugins.get(pluginName);
    if (plugin == null)
      return null; 
    if (!plugin.isReusable())
      try {
        plugin = (AuthenticationPlugin<NativePacketPayload>)plugin.getClass().newInstance();
      } catch (Throwable t) {
        throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
            Messages.getString("AuthenticationProvider.BadAuthenticationPlugin", new Object[] { plugin.getClass().getName() }), t, 
            getExceptionInterceptor());
      }  
    plugin.init(this.protocol, this.callbackHandler);
    return plugin;
  }
  
  private void checkConfidentiality(AuthenticationPlugin<?> plugin) {
    if (plugin.requiresConfidentiality() && !this.protocol.getSocketConnection().isSSLEstablished())
      throw ExceptionFactory.createException(
          Messages.getString("AuthenticationProvider.AuthenticationPluginRequiresSSL", new Object[] { plugin.getProtocolPluginName() }), getExceptionInterceptor()); 
  }
  
  private void proceedHandshakeWithPluggableAuthentication(NativePacketPayload challenge) {
    String pluginName;
    ServerSession serverSession = this.protocol.getServerSession();
    if (this.authenticationPlugins == null)
      loadAuthenticationPlugins(); 
    boolean forChangeUser = true;
    if (challenge != null) {
      this.serverDefaultAuthenticationPluginName = challenge.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII");
      forChangeUser = false;
    } 
    serverSession.getCharsetSettings().configurePreHandshake(forChangeUser);
    if (this.clientDefaultAuthenticationPluginExplicitelySet) {
      pluginName = this.clientDefaultAuthenticationPluginName;
    } else {
      pluginName = (this.serverDefaultAuthenticationPluginName != null) ? this.serverDefaultAuthenticationPluginName : this.clientDefaultAuthenticationPluginName;
    } 
    AuthenticationPlugin<NativePacketPayload> plugin = getAuthenticationPlugin(pluginName);
    if (plugin == null) {
      pluginName = this.clientDefaultAuthenticationPluginName;
      plugin = getAuthenticationPlugin(pluginName);
    } 
    boolean skipPassword = false;
    if (pluginName.equals(Sha256PasswordPlugin.PLUGIN_NAME) && !pluginName.equals(this.clientDefaultAuthenticationPluginName) && 
      !this.protocol.getSocketConnection().isSSLEstablished() && this.propertySet
      .getStringProperty(PropertyKey.serverRSAPublicKeyFile).getValue() == null && 
      !((Boolean)this.propertySet.getBooleanProperty(PropertyKey.allowPublicKeyRetrieval).getValue()).booleanValue()) {
      plugin = getAuthenticationPlugin(this.clientDefaultAuthenticationPluginName);
      skipPassword = true;
    } 
    checkConfidentiality(plugin);
    NativePacketPayload fromServer = new NativePacketPayload(StringUtils.getBytes(this.seed));
    String sourceOfAuthData = this.serverDefaultAuthenticationPluginName;
    NativePacketPayload lastSent = null;
    NativePacketPayload lastReceived = challenge;
    ArrayList<NativePacketPayload> toServer = new ArrayList<>();
    boolean firstPacket = true;
    int mfaNthFactor = 1;
    int counter = 100;
    while (0 < counter--) {
      plugin.setAuthenticationParameters(this.username, skipPassword ? null : getNthFactorPassword(mfaNthFactor));
      plugin.setSourceOfAuthData(sourceOfAuthData);
      plugin.nextAuthenticationStep(fromServer, toServer);
      if (firstPacket) {
        NativePacketPayload authData = toServer.isEmpty() ? new NativePacketPayload(0) : toServer.get(0);
        if (forChangeUser) {
          lastSent = createChangeUserPacket(serverSession, plugin.getProtocolPluginName(), authData);
          this.protocol.send(lastSent, lastSent.getPosition());
        } else {
          lastSent = createHandshakeResponsePacket(serverSession, plugin.getProtocolPluginName(), authData);
          this.protocol.send(lastSent, lastSent.getPosition());
        } 
        firstPacket = false;
      } else if (!toServer.isEmpty()) {
        toServer.forEach(b -> this.protocol.send(b, b.getPayloadLength()));
      } 
      lastReceived = (NativePacketPayload)this.protocol.checkErrorMessage();
      if (lastReceived.isOKPacket()) {
        OkPacket ok = OkPacket.parse(lastReceived, null);
        serverSession.setStatusFlags(ok.getStatusFlags(), true);
        serverSession.getServerSessionStateController().setSessionStateChanges(ok.getSessionStateChanges());
        plugin.destroy();
        break;
      } 
      if (lastReceived.isAuthMethodSwitchRequestPacket()) {
        skipPassword = false;
        pluginName = lastReceived.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII");
        if (plugin.getProtocolPluginName().equals(pluginName)) {
          plugin.reset();
        } else {
          plugin.destroy();
          plugin = getAuthenticationPlugin(pluginName);
          if (plugin == null)
            throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
                Messages.getString("AuthenticationProvider.BadAuthenticationPlugin", new Object[] { pluginName }), getExceptionInterceptor()); 
        } 
        checkConfidentiality(plugin);
        fromServer = new NativePacketPayload(lastReceived.readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
      } else if (lastReceived.isAuthNextFactorPacket()) {
        mfaNthFactor++;
        skipPassword = false;
        pluginName = lastReceived.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII");
        if (plugin.getProtocolPluginName().equals(pluginName)) {
          plugin.reset();
        } else {
          plugin.destroy();
          plugin = getAuthenticationPlugin(pluginName);
          if (plugin == null)
            throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
                Messages.getString("AuthenticationProvider.BadAuthenticationPlugin", new Object[] { pluginName }), getExceptionInterceptor()); 
        } 
        checkConfidentiality(plugin);
        fromServer = new NativePacketPayload(lastReceived.readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
      } else {
        if (!this.protocol.versionMeetsMinimum(5, 5, 16))
          lastReceived.setPosition(lastReceived.getPosition() - 1); 
        fromServer = new NativePacketPayload(lastReceived.readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
      } 
      sourceOfAuthData = pluginName;
    } 
    if (counter == 0)
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
          Messages.getString("CommunicationsException.TooManyAuthenticationPluginNegotiations"), getExceptionInterceptor()); 
    this.protocol.afterHandshake();
    if (!this.useConnectWithDb)
      this.protocol.changeDatabase(this.database); 
  }
  
  private String getNthFactorPassword(int nthFactor) {
    switch (nthFactor) {
      case 1:
        return (this.password == null) ? (String)this.protocol.getPropertySet().getStringProperty(PropertyKey.password1).getValue() : this.password;
      case 2:
        return (String)this.protocol.getPropertySet().getStringProperty(PropertyKey.password2).getValue();
      case 3:
        return (String)this.protocol.getPropertySet().getStringProperty(PropertyKey.password3).getValue();
    } 
    return null;
  }
  
  private Map<String, String> getConnectionAttributesMap(String attStr) {
    Map<String, String> attMap = new HashMap<>();
    if (attStr != null) {
      String[] pairs = attStr.split(",");
      for (String pair : pairs) {
        int keyEnd = pair.indexOf(":");
        if (keyEnd > 0 && keyEnd + 1 < pair.length())
          attMap.put(pair.substring(0, keyEnd), pair.substring(keyEnd + 1)); 
      } 
    } 
    attMap.put("_client_name", "MySQL Connector/J");
    attMap.put("_client_version", "8.0.33");
    attMap.put("_runtime_vendor", Constants.JVM_VENDOR);
    attMap.put("_runtime_version", Constants.JVM_VERSION);
    attMap.put("_client_license", "GPL");
    return attMap;
  }
  
  private void appendConnectionAttributes(NativePacketPayload buf, String attributes, String enc) {
    NativePacketPayload lb = new NativePacketPayload(100);
    Map<String, String> attMap = getConnectionAttributesMap(attributes);
    for (String key : attMap.keySet()) {
      lb.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, StringUtils.getBytes(key, enc));
      lb.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, StringUtils.getBytes(attMap.get(key), enc));
    } 
    buf.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, lb.getPosition());
    buf.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, lb.getByteBuffer(), 0, lb.getPosition());
  }
  
  public ExceptionInterceptor getExceptionInterceptor() {
    return this.exceptionInterceptor;
  }
  
  public void changeUser(String user, String pass, String db) {
    this.username = user;
    this.password = pass;
    this.database = db;
    proceedHandshakeWithPluggableAuthentication(null);
    this.password = null;
  }
  
  private NativePacketPayload createHandshakeResponsePacket(ServerSession serverSession, String pluginName, NativePacketPayload authData) {
    long clientParam = serverSession.getClientParam();
    int collationIndex = serverSession.getCharsetSettings().configurePreHandshake(false);
    String enc = serverSession.getCharsetSettings().getPasswordCharacterEncoding();
    int userLength = (this.username == null) ? 0 : this.username.length();
    NativePacketPayload last_sent = new NativePacketPayload(88 + 3 * userLength + (this.useConnectWithDb ? (3 * this.database.length()) : 0));
    last_sent.writeInteger(NativeConstants.IntegerDataType.INT4, clientParam);
    last_sent.writeInteger(NativeConstants.IntegerDataType.INT4, 16777215L);
    last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, collationIndex);
    last_sent.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, new byte[23]);
    last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(this.username, enc));
    if ((clientParam & 0x200000L) != 0L) {
      last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, authData.readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
    } else {
      last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, authData.getPayloadLength());
      last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_EOF, authData.getByteBuffer());
    } 
    if (this.useConnectWithDb)
      last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(this.database, enc)); 
    last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(pluginName, enc));
    if ((clientParam & 0x100000L) != 0L)
      appendConnectionAttributes(last_sent, (String)this.propertySet.getStringProperty(PropertyKey.connectionAttributes).getValue(), enc); 
    return last_sent;
  }
  
  private NativePacketPayload createChangeUserPacket(ServerSession serverSession, String pluginName, NativePacketPayload authData) {
    long clientParam = serverSession.getClientParam();
    int collationIndex = serverSession.getCharsetSettings().configurePreHandshake(false);
    String enc = serverSession.getCharsetSettings().getPasswordCharacterEncoding();
    NativePacketPayload last_sent = new NativePacketPayload(88 + 3 * this.username.length() + (this.useConnectWithDb ? (3 * this.database.length()) : 1) + 1);
    last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, 17L);
    last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(this.username, enc));
    if (authData.getPayloadLength() < 256) {
      last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, authData.getPayloadLength());
      last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_EOF, authData.getByteBuffer(), 0, authData.getPayloadLength());
    } else {
      last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
    } 
    if (this.useConnectWithDb) {
      last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(this.database, enc));
    } else {
      last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
    } 
    last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, collationIndex);
    last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
    last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(pluginName, enc));
    if ((clientParam & 0x100000L) != 0L)
      appendConnectionAttributes(last_sent, (String)this.propertySet.getStringProperty(PropertyKey.connectionAttributes).getValue(), enc); 
    return last_sent;
  }
}
