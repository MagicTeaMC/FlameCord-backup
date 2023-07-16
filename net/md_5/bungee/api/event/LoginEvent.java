package net.md_5.bungee.api.event;

import java.util.Arrays;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.connection.LoginResult;

public class LoginEvent extends AsyncEvent<LoginEvent> implements Cancellable {
  private boolean cancelled;
  
  private BaseComponent[] cancelReasonComponents;
  
  private LoginResult loginResult;
  
  private final PendingConnection connection;
  
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
  
  public void setLoginResult(LoginResult loginResult) {
    this.loginResult = loginResult;
  }
  
  public String toString() {
    return "LoginEvent(cancelled=" + isCancelled() + ", cancelReasonComponents=" + Arrays.deepToString((Object[])getCancelReasonComponents()) + ", loginResult=" + getLoginResult() + ", connection=" + getConnection() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof LoginEvent))
      return false; 
    LoginEvent other = (LoginEvent)o;
    if (!other.canEqual(this))
      return false; 
    if (isCancelled() != other.isCancelled())
      return false; 
    if (!Arrays.deepEquals((Object[])getCancelReasonComponents(), (Object[])other.getCancelReasonComponents()))
      return false; 
    Object this$loginResult = getLoginResult(), other$loginResult = other.getLoginResult();
    if ((this$loginResult == null) ? (other$loginResult != null) : !this$loginResult.equals(other$loginResult))
      return false; 
    Object this$connection = getConnection(), other$connection = other.getConnection();
    return !((this$connection == null) ? (other$connection != null) : !this$connection.equals(other$connection));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof LoginEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + (isCancelled() ? 79 : 97);
    result = result * 59 + Arrays.deepHashCode((Object[])getCancelReasonComponents());
    Object $loginResult = getLoginResult();
    result = result * 59 + (($loginResult == null) ? 43 : $loginResult.hashCode());
    Object $connection = getConnection();
    return result * 59 + (($connection == null) ? 43 : $connection.hashCode());
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public BaseComponent[] getCancelReasonComponents() {
    return this.cancelReasonComponents;
  }
  
  public LoginResult getLoginResult() {
    return this.loginResult;
  }
  
  public PendingConnection getConnection() {
    return this.connection;
  }
  
  public LoginEvent(PendingConnection connection, Callback<LoginEvent> done) {
    super(done);
    this.connection = connection;
  }
  
  public LoginEvent(PendingConnection connection, Callback<LoginEvent> done, LoginResult loginResult) {
    super(done);
    this.connection = connection;
    this.loginResult = loginResult;
  }
  
  @Deprecated
  public String getCancelReason() {
    return BaseComponent.toLegacyText(getCancelReasonComponents());
  }
  
  @Deprecated
  public void setCancelReason(String cancelReason) {
    setCancelReason(TextComponent.fromLegacyText(cancelReason));
  }
  
  public void setCancelReason(BaseComponent... cancelReason) {
    this.cancelReasonComponents = cancelReason;
  }
}
