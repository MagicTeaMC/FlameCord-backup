package com.mysql.cj.protocol.a.authentication;

import com.mysql.cj.Messages;
import com.mysql.cj.callback.MysqlCallback;
import com.mysql.cj.callback.MysqlCallbackHandler;
import com.mysql.cj.callback.UsernameCallback;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.UnableToConnectException;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.AuthenticationPlugin;
import com.mysql.cj.protocol.ExportControlled;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.Protocol;
import com.mysql.cj.protocol.Security;
import com.mysql.cj.protocol.a.NativeConstants;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.util.StringUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class Sha256PasswordPlugin implements AuthenticationPlugin<NativePacketPayload> {
  public static String PLUGIN_NAME = "sha256_password";
  
  protected Protocol<NativePacketPayload> protocol = null;
  
  protected MysqlCallbackHandler usernameCallbackHandler = null;
  
  protected String password = null;
  
  protected String seed = null;
  
  protected boolean publicKeyRequested = false;
  
  protected String publicKeyString = null;
  
  protected RuntimeProperty<String> serverRSAPublicKeyFile = null;
  
  public void init(Protocol<NativePacketPayload> prot, MysqlCallbackHandler cbh) {
    this.protocol = prot;
    this.usernameCallbackHandler = cbh;
    this.serverRSAPublicKeyFile = this.protocol.getPropertySet().getStringProperty(PropertyKey.serverRSAPublicKeyFile);
    String pkURL = (String)this.serverRSAPublicKeyFile.getValue();
    if (pkURL != null)
      this.publicKeyString = readRSAKey(pkURL, this.protocol.getPropertySet(), this.protocol.getExceptionInterceptor()); 
  }
  
  public void destroy() {
    reset();
    this.protocol = null;
    this.usernameCallbackHandler = null;
    this.password = null;
    this.seed = null;
    this.publicKeyRequested = false;
    this.publicKeyString = null;
    this.serverRSAPublicKeyFile = null;
  }
  
  public String getProtocolPluginName() {
    return PLUGIN_NAME;
  }
  
  public boolean requiresConfidentiality() {
    return false;
  }
  
  public boolean isReusable() {
    return true;
  }
  
  public void setAuthenticationParameters(String user, String password) {
    this.password = password;
    if (user == null && this.usernameCallbackHandler != null)
      this.usernameCallbackHandler.handle((MysqlCallback)new UsernameCallback(System.getProperty("user.name"))); 
  }
  
  public boolean nextAuthenticationStep(NativePacketPayload fromServer, List<NativePacketPayload> toServer) {
    toServer.clear();
    if (this.password == null || this.password.length() == 0 || fromServer == null) {
      NativePacketPayload packet = new NativePacketPayload(new byte[] { 0 });
      toServer.add(packet);
    } else {
      try {
        if (this.protocol.getSocketConnection().isSSLEstablished()) {
          NativePacketPayload packet = new NativePacketPayload(StringUtils.getBytes(this.password, this.protocol.getServerSession().getCharsetSettings().getPasswordCharacterEncoding()));
          packet.setPosition(packet.getPayloadLength());
          packet.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
          packet.setPosition(0);
          toServer.add(packet);
        } else if (this.serverRSAPublicKeyFile.getValue() != null) {
          this.seed = fromServer.readString(NativeConstants.StringSelfDataType.STRING_TERM, null);
          NativePacketPayload packet = new NativePacketPayload(encryptPassword());
          toServer.add(packet);
        } else {
          if (!((Boolean)this.protocol.getPropertySet().getBooleanProperty(PropertyKey.allowPublicKeyRetrieval).getValue()).booleanValue())
            throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, Messages.getString("Sha256PasswordPlugin.2"), this.protocol
                .getExceptionInterceptor()); 
          if (this.publicKeyRequested && fromServer.getPayloadLength() > 21) {
            this.publicKeyString = fromServer.readString(NativeConstants.StringSelfDataType.STRING_TERM, null);
            NativePacketPayload packet = new NativePacketPayload(encryptPassword());
            toServer.add(packet);
            this.publicKeyRequested = false;
          } else {
            this.seed = fromServer.readString(NativeConstants.StringSelfDataType.STRING_TERM, null);
            NativePacketPayload packet = new NativePacketPayload(new byte[] { 1 });
            toServer.add(packet);
            this.publicKeyRequested = true;
          } 
        } 
      } catch (CJException e) {
        throw ExceptionFactory.createException(e.getMessage(), e, this.protocol.getExceptionInterceptor());
      } 
    } 
    return true;
  }
  
  protected byte[] encryptPassword() {
    return encryptPassword("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
  }
  
  protected byte[] encryptPassword(String transformation) {
    byte[] input = null;
    (new byte[1])[0] = 0;
    input = (this.password != null) ? StringUtils.getBytesNullTerminated(this.password, this.protocol.getServerSession().getCharsetSettings().getPasswordCharacterEncoding()) : new byte[1];
    byte[] mysqlScrambleBuff = new byte[input.length];
    Security.xorString(input, mysqlScrambleBuff, this.seed.getBytes(), input.length);
    return ExportControlled.encryptWithRSAPublicKey(mysqlScrambleBuff, ExportControlled.decodeRSAPublicKey(this.publicKeyString), transformation);
  }
  
  protected static String readRSAKey(String pkPath, PropertySet propertySet, ExceptionInterceptor exceptionInterceptor) {
    String res = null;
    byte[] fileBuf = new byte[2048];
    BufferedInputStream fileIn = null;
    try {
      File f = new File(pkPath);
      String canonicalPath = f.getCanonicalPath();
      fileIn = new BufferedInputStream(new FileInputStream(canonicalPath));
      int bytesRead = 0;
      StringBuilder sb = new StringBuilder();
      while ((bytesRead = fileIn.read(fileBuf)) != -1)
        sb.append(StringUtils.toAsciiString(fileBuf, 0, bytesRead)); 
      res = sb.toString();
    } catch (IOException ioEx) {
      (new Object[1])[0] = "";
      (new Object[1])[0] = "'" + pkPath + "'";
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Sha256PasswordPlugin.0", ((Boolean)propertySet.getBooleanProperty(PropertyKey.paranoid).getValue()).booleanValue() ? new Object[1] : new Object[1]), exceptionInterceptor);
    } finally {
      if (fileIn != null)
        try {
          fileIn.close();
        } catch (IOException e) {
          throw ExceptionFactory.createException(Messages.getString("Sha256PasswordPlugin.1"), e, exceptionInterceptor);
        }  
    } 
    return res;
  }
}
