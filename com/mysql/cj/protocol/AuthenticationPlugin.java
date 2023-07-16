package com.mysql.cj.protocol;

import com.mysql.cj.callback.MysqlCallbackHandler;
import java.util.List;

public interface AuthenticationPlugin<M extends Message> {
  default void init(Protocol<M> protocol) {}
  
  default void init(Protocol<M> protocol, MysqlCallbackHandler callbackHandler) {
    init(protocol);
  }
  
  default void reset() {}
  
  default void destroy() {}
  
  String getProtocolPluginName();
  
  boolean requiresConfidentiality();
  
  boolean isReusable();
  
  void setAuthenticationParameters(String paramString1, String paramString2);
  
  default void setSourceOfAuthData(String sourceOfAuthData) {}
  
  boolean nextAuthenticationStep(M paramM, List<M> paramList);
}
