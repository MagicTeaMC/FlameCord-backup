package com.mysql.cj.protocol.a.authentication;

import com.mysql.cj.callback.MysqlCallback;
import com.mysql.cj.callback.MysqlCallbackHandler;
import com.mysql.cj.callback.UsernameCallback;
import com.mysql.cj.protocol.AuthenticationPlugin;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.Protocol;
import com.mysql.cj.protocol.a.NativeConstants;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.util.StringUtils;
import java.util.List;

public class MysqlClearPasswordPlugin implements AuthenticationPlugin<NativePacketPayload> {
  public static String PLUGIN_NAME = "mysql_clear_password";
  
  private Protocol<NativePacketPayload> protocol = null;
  
  private MysqlCallbackHandler usernameCallbackHandler = null;
  
  private String password = null;
  
  public void init(Protocol<NativePacketPayload> prot, MysqlCallbackHandler cbh) {
    this.protocol = prot;
    this.usernameCallbackHandler = cbh;
  }
  
  public void destroy() {
    reset();
    this.protocol = null;
    this.usernameCallbackHandler = null;
    this.password = null;
  }
  
  public String getProtocolPluginName() {
    return PLUGIN_NAME;
  }
  
  public boolean requiresConfidentiality() {
    return true;
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
    String encoding = this.protocol.getServerSession().getCharsetSettings().getPasswordCharacterEncoding();
    NativePacketPayload packet = new NativePacketPayload(StringUtils.getBytes((this.password != null) ? this.password : "", encoding));
    packet.setPosition(packet.getPayloadLength());
    packet.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
    packet.setPosition(0);
    toServer.add(packet);
    return true;
  }
}
