package net.md_5.bungee.api.event;

import java.util.Arrays;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.plugin.Cancellable;

public class PreLoginEvent extends AsyncEvent<PreLoginEvent> implements Cancellable {
  private boolean cancelled;
  
  private BaseComponent[] cancelReasonComponents;
  
  private final PendingConnection connection;
  
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
  
  public String toString() {
    return "PreLoginEvent(cancelled=" + isCancelled() + ", cancelReasonComponents=" + Arrays.deepToString((Object[])getCancelReasonComponents()) + ", connection=" + getConnection() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof PreLoginEvent))
      return false; 
    PreLoginEvent other = (PreLoginEvent)o;
    if (!other.canEqual(this))
      return false; 
    if (isCancelled() != other.isCancelled())
      return false; 
    if (!Arrays.deepEquals((Object[])getCancelReasonComponents(), (Object[])other.getCancelReasonComponents()))
      return false; 
    Object this$connection = getConnection(), other$connection = other.getConnection();
    return !((this$connection == null) ? (other$connection != null) : !this$connection.equals(other$connection));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof PreLoginEvent;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + (isCancelled() ? 79 : 97);
    result = result * 59 + Arrays.deepHashCode((Object[])getCancelReasonComponents());
    Object $connection = getConnection();
    return result * 59 + (($connection == null) ? 43 : $connection.hashCode());
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public BaseComponent[] getCancelReasonComponents() {
    return this.cancelReasonComponents;
  }
  
  public PendingConnection getConnection() {
    return this.connection;
  }
  
  public PreLoginEvent(PendingConnection connection, Callback<PreLoginEvent> done) {
    super(done);
    this.connection = connection;
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
