package com.mysql.cj.protocol.a.authentication;

import com.mysql.cj.Messages;
import com.mysql.cj.callback.MysqlCallback;
import com.mysql.cj.callback.MysqlCallbackHandler;
import com.mysql.cj.callback.UsernameCallback;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.RSAException;
import com.mysql.cj.protocol.AuthenticationPlugin;
import com.mysql.cj.protocol.ExportControlled;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.Protocol;
import com.mysql.cj.protocol.a.NativeConstants;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.util.StringUtils;
import com.oracle.bmc.ConfigFileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;
import java.util.List;

public class AuthenticationOciClient implements AuthenticationPlugin<NativePacketPayload> {
  public static String PLUGIN_NAME = "authentication_oci_client";
  
  private String sourceOfAuthData = PLUGIN_NAME;
  
  protected Protocol<NativePacketPayload> protocol = null;
  
  private MysqlCallbackHandler usernameCallbackHandler = null;
  
  private String configFingerprint = null;
  
  private String configKeyFile = null;
  
  private String configSecurityTokenFile = null;
  
  private RSAPrivateKey privateKey = null;
  
  private byte[] token = null;
  
  public void init(Protocol<NativePacketPayload> prot, MysqlCallbackHandler cbh) {
    this.protocol = prot;
    this.usernameCallbackHandler = cbh;
  }
  
  public void reset() {
    this.configFingerprint = null;
    this.privateKey = null;
  }
  
  public void destroy() {
    reset();
    this.protocol = null;
    this.usernameCallbackHandler = null;
  }
  
  public String getProtocolPluginName() {
    return PLUGIN_NAME;
  }
  
  public boolean requiresConfidentiality() {
    return false;
  }
  
  public boolean isReusable() {
    return false;
  }
  
  public void setAuthenticationParameters(String user, String password) {
    if (user == null && this.usernameCallbackHandler != null)
      this.usernameCallbackHandler.handle((MysqlCallback)new UsernameCallback(System.getProperty("user.name"))); 
  }
  
  public void setSourceOfAuthData(String sourceOfAuthData) {
    this.sourceOfAuthData = sourceOfAuthData;
  }
  
  public boolean nextAuthenticationStep(NativePacketPayload fromServer, List<NativePacketPayload> toServer) {
    toServer.clear();
    if (!this.sourceOfAuthData.equals(PLUGIN_NAME) || fromServer.getPayloadLength() == 0) {
      toServer.add(new NativePacketPayload(0));
      return true;
    } 
    loadOciConfig();
    initializePrivateKey();
    initializeToken();
    byte[] nonce = fromServer.readBytes(NativeConstants.StringSelfDataType.STRING_EOF);
    byte[] signature = ExportControlled.sign(nonce, this.privateKey);
    if (signature == null)
      signature = new byte[0]; 
    String payload = String.format("{\"fingerprint\":\"%s\", \"signature\":\"%s\", \"token\":\"%s\"}", new Object[] { this.configFingerprint, 
          Base64.getEncoder().encodeToString(signature), new String(this.token) });
    toServer.add(new NativePacketPayload(payload.getBytes(Charset.defaultCharset())));
    return true;
  }
  
  private void loadOciConfig() {
    ConfigFileReader.ConfigFile configFile;
    try {
      String configFilePath = this.protocol.getPropertySet().getStringProperty(PropertyKey.ociConfigFile.getKeyName()).getStringValue();
      String configProfile = this.protocol.getPropertySet().getStringProperty(PropertyKey.ociConfigProfile.getKeyName()).getStringValue();
      if (StringUtils.isNullOrEmpty(configFilePath)) {
        configFile = ConfigFileReader.parseDefault(configProfile);
      } else if (Files.exists(Paths.get(configFilePath, new String[0]), new java.nio.file.LinkOption[0])) {
        configFile = ConfigFileReader.parse(configFilePath, configProfile);
      } else {
        throw ExceptionFactory.createException(Messages.getString("AuthenticationOciClientPlugin.ConfigFileNotFound"));
      } 
    } catch (NoClassDefFoundError e) {
      throw ExceptionFactory.createException(Messages.getString("AuthenticationOciClientPlugin.OciSdkNotFound"), e);
    } catch (IOException e) {
      throw ExceptionFactory.createException(Messages.getString("AuthenticationOciClientPlugin.OciConfigFileError"), e);
    } catch (IllegalArgumentException e) {
      throw ExceptionFactory.createException(Messages.getString("AuthenticationOciClientPlugin.ProfileNotFound"), e);
    } 
    this.configFingerprint = configFile.get("fingerprint");
    if (StringUtils.isNullOrEmpty(this.configFingerprint))
      throw ExceptionFactory.createException(Messages.getString("AuthenticationOciClientPlugin.OciConfigFileMissingEntry")); 
    this.configKeyFile = configFile.get("key_file");
    if (StringUtils.isNullOrEmpty(this.configKeyFile))
      throw ExceptionFactory.createException(Messages.getString("AuthenticationOciClientPlugin.OciConfigFileMissingEntry")); 
    this.configSecurityTokenFile = configFile.get("security_token_file");
  }
  
  private void initializePrivateKey() {
    if (this.privateKey != null)
      return; 
    try {
      Path keyFilePath = Paths.get(this.configKeyFile, new String[0]);
      if (Files.notExists(keyFilePath, new java.nio.file.LinkOption[0]))
        throw ExceptionFactory.createException(Messages.getString("AuthenticationOciClientPlugin.PrivateKeyNotFound")); 
      String key = new String(Files.readAllBytes(keyFilePath));
      this.privateKey = ExportControlled.decodeRSAPrivateKey(key);
    } catch (IOException e) {
      throw ExceptionFactory.createException(Messages.getString("AuthenticationOciClientPlugin.FailedReadingPrivateKey"), e);
    } catch (RSAException|IllegalArgumentException e) {
      throw ExceptionFactory.createException(Messages.getString("AuthenticationOciClientPlugin.PrivateKeyNotValid"), e);
    } 
  }
  
  private void initializeToken() {
    if (this.token != null)
      return; 
    if (StringUtils.isNullOrEmpty(this.configSecurityTokenFile)) {
      this.token = new byte[0];
      return;
    } 
    try {
      Path securityTokenFilePath = Paths.get(this.configSecurityTokenFile, new String[0]);
      if (Files.notExists(securityTokenFilePath, new java.nio.file.LinkOption[0]))
        throw ExceptionFactory.createException(Messages.getString("AuthenticationOciClientPlugin.SecurityTokenFileNotFound")); 
      long size = Files.size(securityTokenFilePath);
      if (size > 10240L)
        throw ExceptionFactory.createException(Messages.getString("AuthenticationOciClientPlugin.SecurityTokenTooBig")); 
      this.token = Files.readAllBytes(Paths.get(this.configSecurityTokenFile, new String[0]));
    } catch (IOException e) {
      throw ExceptionFactory.createException(Messages.getString("AuthenticationOciClientPlugin.FailedReadingSecurityTokenFile"), e);
    } 
  }
}
