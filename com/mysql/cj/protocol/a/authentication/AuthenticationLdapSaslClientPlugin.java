package com.mysql.cj.protocol.a.authentication;

import com.mysql.cj.Messages;
import com.mysql.cj.callback.MysqlCallback;
import com.mysql.cj.callback.MysqlCallbackHandler;
import com.mysql.cj.callback.UsernameCallback;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.protocol.AuthenticationPlugin;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.Protocol;
import com.mysql.cj.protocol.a.NativeConstants;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.sasl.ScramShaSaslProvider;
import com.mysql.cj.util.StringUtils;
import java.io.IOException;
import java.security.PrivilegedActionException;
import java.security.Provider;
import java.security.Security;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

public class AuthenticationLdapSaslClientPlugin implements AuthenticationPlugin<NativePacketPayload> {
  public AuthenticationLdapSaslClientPlugin() {
    this.protocol = null;
    this.usernameCallbackHandler = null;
    this.user = null;
    this.password = null;
    this.authMech = null;
    this.saslClient = null;
    this.subject = null;
    this.firstPass = true;
    this.credentialsCallbackHandler = (cbs -> {
        for (Callback cb : cbs) {
          if (NameCallback.class.isAssignableFrom(cb.getClass())) {
            ((NameCallback)cb).setName(this.user);
          } else if (PasswordCallback.class.isAssignableFrom(cb.getClass())) {
            char[] passwordChars = (this.password == null) ? new char[0] : this.password.toCharArray();
            ((PasswordCallback)cb).setPassword(passwordChars);
          } else {
            throw new UnsupportedCallbackException(cb, cb.getClass().getName());
          } 
        } 
      });
  }
  
  public static String PLUGIN_NAME = "authentication_ldap_sasl_client";
  
  private static final String LOGIN_CONFIG_ENTRY = "MySQLConnectorJ";
  
  private static final String LDAP_SERVICE_NAME = "ldap";
  
  private Protocol<?> protocol;
  
  private MysqlCallbackHandler usernameCallbackHandler;
  
  private String user;
  
  private String password;
  
  private AuthenticationMechanisms authMech;
  
  private SaslClient saslClient;
  
  private Subject subject;
  
  private boolean firstPass;
  
  private CallbackHandler credentialsCallbackHandler;
  
  private enum AuthenticationMechanisms {
    SCRAM_SHA_1("SCRAM-SHA-1", "MYSQLCJ-SCRAM-SHA-1"),
    SCRAM_SHA_256("SCRAM-SHA-256", "MYSQLCJ-SCRAM-SHA-256"),
    GSSAPI("GSSAPI", "GSSAPI");
    
    private String mechName = null;
    
    private String saslServiceName = null;
    
    AuthenticationMechanisms(String mechName, String serviceName) {
      this.mechName = mechName;
      this.saslServiceName = serviceName;
    }
    
    static AuthenticationMechanisms fromValue(String mechName) {
      for (AuthenticationMechanisms am : values()) {
        if (am.mechName.equalsIgnoreCase(mechName))
          return am; 
      } 
      throw ExceptionFactory.createException(Messages.getString("AuthenticationLdapSaslClientPlugin.UnsupportedAuthMech", new String[] { mechName }));
    }
    
    String getMechName() {
      return this.mechName;
    }
    
    String getSaslServiceName() {
      return this.saslServiceName;
    }
  }
  
  public void init(Protocol<NativePacketPayload> prot) {
    this.protocol = prot;
    Security.addProvider((Provider)new ScramShaSaslProvider());
  }
  
  public void init(Protocol<NativePacketPayload> prot, MysqlCallbackHandler cbh) {
    init(prot);
    this.usernameCallbackHandler = cbh;
  }
  
  public void reset() {
    if (this.saslClient != null)
      try {
        this.saslClient.dispose();
      } catch (SaslException saslException) {} 
    this.user = null;
    this.password = null;
    this.authMech = null;
    this.saslClient = null;
    this.subject = null;
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
    this.user = user;
    this.password = password;
    if (this.user == null) {
      this.user = System.getProperty("user.name");
      if (this.usernameCallbackHandler != null)
        this.usernameCallbackHandler.handle((MysqlCallback)new UsernameCallback(this.user)); 
    } 
  }
  
  public boolean nextAuthenticationStep(NativePacketPayload fromServer, List<NativePacketPayload> toServer) {
    toServer.clear();
    if (this.saslClient == null) {
      String authMechId = fromServer.readString(NativeConstants.StringSelfDataType.STRING_EOF, "ASCII");
      try {
        this.authMech = AuthenticationMechanisms.fromValue(authMechId);
      } catch (CJException e) {
        if (this.firstPass) {
          this.firstPass = false;
          return true;
        } 
        throw e;
      } 
      this.firstPass = false;
      try {
        String ldapServerHostname;
        String loginConfigFile;
        Configuration loginConfig;
        LoginContext loginContext;
        switch (this.authMech) {
          case GSSAPI:
            ldapServerHostname = (String)this.protocol.getPropertySet().getStringProperty(PropertyKey.ldapServerHostname).getValue();
            if (StringUtils.isNullOrEmpty(ldapServerHostname)) {
              String krb5Kdc = System.getProperty("java.security.krb5.kdc");
              if (!StringUtils.isNullOrEmpty(krb5Kdc)) {
                ldapServerHostname = krb5Kdc;
                int dotIndex = krb5Kdc.indexOf('.');
                if (dotIndex > 0)
                  ldapServerHostname = krb5Kdc.substring(0, dotIndex).toLowerCase(Locale.ENGLISH); 
              } 
            } 
            if (StringUtils.isNullOrEmpty(ldapServerHostname))
              throw ExceptionFactory.createException(Messages.getString("AuthenticationLdapSaslClientPlugin.MissingLdapServerHostname")); 
            loginConfigFile = System.getProperty("java.security.auth.login.config");
            loginConfig = null;
            if (StringUtils.isNullOrEmpty(loginConfigFile)) {
              final String localUser = this.user;
              final boolean debug = Boolean.getBoolean("sun.security.jgss.debug");
              loginConfig = new Configuration() {
                  public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
                    Map<String, String> options = new HashMap<>();
                    options.put("useTicketCache", "true");
                    options.put("renewTGT", "false");
                    options.put("principal", localUser);
                    options.put("debug", Boolean.toString(debug));
                    return new AppConfigurationEntry[] { new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options) };
                  }
                };
            } 
            loginContext = new LoginContext("MySQLConnectorJ", null, this.credentialsCallbackHandler, loginConfig);
            loginContext.login();
            this.subject = loginContext.getSubject();
            try {
              String localLdapServerHostname = ldapServerHostname;
              this.saslClient = Subject.<SaslClient>doAs(this.subject, () -> Sasl.createSaslClient(new String[] { this.authMech.getSaslServiceName() }, null, "ldap", localLdapServerHostname, null, null));
            } catch (PrivilegedActionException e) {
              throw (SaslException)e.getException();
            } 
            break;
          case SCRAM_SHA_1:
          case SCRAM_SHA_256:
            this.saslClient = Sasl.createSaslClient(new String[] { this.authMech.getSaslServiceName() }, null, null, null, null, this.credentialsCallbackHandler);
            break;
        } 
      } catch (LoginException|SaslException e) {
        throw ExceptionFactory.createException(
            Messages.getString("AuthenticationLdapSaslClientPlugin.FailCreateSaslClient", new Object[] { this.authMech.getMechName() }), e);
      } 
      if (this.saslClient == null)
        throw ExceptionFactory.createException(
            Messages.getString("AuthenticationLdapSaslClientPlugin.FailCreateSaslClient", new Object[] { this.authMech.getMechName() })); 
    } 
    if (!this.saslClient.isComplete())
      try {
        Subject.doAs(this.subject, () -> {
              byte[] response = this.saslClient.evaluateChallenge(fromServer.readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
              if (response != null) {
                NativePacketPayload packet = new NativePacketPayload(response);
                packet.setPosition(0);
                toServer.add(packet);
              } 
              return null;
            });
      } catch (PrivilegedActionException e) {
        throw ExceptionFactory.createException(
            Messages.getString("AuthenticationLdapSaslClientPlugin.ErrProcessingAuthIter", new Object[] { this.authMech.getMechName() }), e
            .getException());
      }  
    return true;
  }
}
