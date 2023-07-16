package net.md_5.bungee.api.plugin;

public interface Cancellable {
  boolean isCancelled();
  
  void setCancelled(boolean paramBoolean);
}
