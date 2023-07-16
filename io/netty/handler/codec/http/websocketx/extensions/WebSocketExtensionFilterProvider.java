package io.netty.handler.codec.http.websocketx.extensions;

public interface WebSocketExtensionFilterProvider {
  public static final WebSocketExtensionFilterProvider DEFAULT = new WebSocketExtensionFilterProvider() {
      public WebSocketExtensionFilter encoderFilter() {
        return WebSocketExtensionFilter.NEVER_SKIP;
      }
      
      public WebSocketExtensionFilter decoderFilter() {
        return WebSocketExtensionFilter.NEVER_SKIP;
      }
    };
  
  WebSocketExtensionFilter encoderFilter();
  
  WebSocketExtensionFilter decoderFilter();
}
