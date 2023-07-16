package net.md_5.bungee.api;

import lombok.NonNull;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;

public class ServerConnectRequest {
  @NonNull
  private final ServerInfo target;
  
  @NonNull
  private final ServerConnectEvent.Reason reason;
  
  private final Callback<Result> callback;
  
  private int connectTimeout;
  
  private boolean retry;
  
  private boolean sendFeedback;
  
  ServerConnectRequest(@NonNull ServerInfo target, @NonNull ServerConnectEvent.Reason reason, Callback<Result> callback, int connectTimeout, boolean retry, boolean sendFeedback) {
    if (target == null)
      throw new NullPointerException("target is marked non-null but is null"); 
    if (reason == null)
      throw new NullPointerException("reason is marked non-null but is null"); 
    this.target = target;
    this.reason = reason;
    this.callback = callback;
    this.connectTimeout = connectTimeout;
    this.retry = retry;
    this.sendFeedback = sendFeedback;
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    private ServerInfo target;
    
    private ServerConnectEvent.Reason reason;
    
    private Callback<ServerConnectRequest.Result> callback;
    
    private boolean retry;
    
    private boolean sendFeedback;
    
    public Builder target(@NonNull ServerInfo target) {
      if (target == null)
        throw new NullPointerException("target is marked non-null but is null"); 
      this.target = target;
      return this;
    }
    
    public Builder reason(@NonNull ServerConnectEvent.Reason reason) {
      if (reason == null)
        throw new NullPointerException("reason is marked non-null but is null"); 
      this.reason = reason;
      return this;
    }
    
    public Builder callback(Callback<ServerConnectRequest.Result> callback) {
      this.callback = callback;
      return this;
    }
    
    public Builder connectTimeout(int connectTimeout) {
      this.connectTimeout = connectTimeout;
      return this;
    }
    
    public Builder retry(boolean retry) {
      this.retry = retry;
      return this;
    }
    
    public Builder sendFeedback(boolean sendFeedback) {
      this.sendFeedback = sendFeedback;
      return this;
    }
    
    public ServerConnectRequest build() {
      return new ServerConnectRequest(this.target, this.reason, this.callback, this.connectTimeout, this.retry, this.sendFeedback);
    }
    
    public String toString() {
      return "ServerConnectRequest.Builder(target=" + this.target + ", reason=" + this.reason + ", callback=" + this.callback + ", connectTimeout=" + this.connectTimeout + ", retry=" + this.retry + ", sendFeedback=" + this.sendFeedback + ")";
    }
    
    private int connectTimeout = ProxyServer.getInstance().getConfig().getServerConnectTimeout();
    
    private boolean isSendFeedback = true;
  }
  
  public enum Result {
    EVENT_CANCEL, ALREADY_CONNECTED, ALREADY_CONNECTING, SUCCESS, FAIL;
  }
  
  @NonNull
  public ServerInfo getTarget() {
    return this.target;
  }
  
  @NonNull
  public ServerConnectEvent.Reason getReason() {
    return this.reason;
  }
  
  public Callback<Result> getCallback() {
    return this.callback;
  }
  
  public void setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
  }
  
  public int getConnectTimeout() {
    return this.connectTimeout;
  }
  
  public void setRetry(boolean retry) {
    this.retry = retry;
  }
  
  public boolean isRetry() {
    return this.retry;
  }
  
  public void setSendFeedback(boolean sendFeedback) {
    this.sendFeedback = sendFeedback;
  }
  
  public boolean isSendFeedback() {
    return this.sendFeedback;
  }
}
