package net.md_5.bungee.command;

import java.util.Collection;
import java.util.Collections;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;

public final class ConsoleCommandSender implements CommandSender {
  public static ConsoleCommandSender getInstance() {
    return instance;
  }
  
  private static final ConsoleCommandSender instance = new ConsoleCommandSender();
  
  public void sendMessage(String message) {
    ProxyServer.getInstance().getLogger().info(message);
  }
  
  public void sendMessages(String... messages) {
    for (String message : messages)
      sendMessage(message); 
  }
  
  public void sendMessage(BaseComponent... message) {
    sendMessage(BaseComponent.toLegacyText(message));
  }
  
  public void sendMessage(BaseComponent message) {
    sendMessage(message.toLegacyText());
  }
  
  public String getName() {
    return "CONSOLE";
  }
  
  public Collection<String> getGroups() {
    return Collections.emptySet();
  }
  
  public void addGroups(String... groups) {
    throw new UnsupportedOperationException("Console may not have groups");
  }
  
  public void removeGroups(String... groups) {
    throw new UnsupportedOperationException("Console may not have groups");
  }
  
  public boolean hasPermission(String permission) {
    return true;
  }
  
  public void setPermission(String permission, boolean value) {
    throw new UnsupportedOperationException("Console has all permissions");
  }
  
  public Collection<String> getPermissions() {
    return Collections.emptySet();
  }
}
