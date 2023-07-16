package io.github.waterfallmc.waterfall.exception;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ProxyCommandException extends ProxyException {
  private final Command command;
  
  private final CommandSender commandSender;
  
  private final String[] arguments;
  
  public ProxyCommandException(String message, Throwable cause, Command command, CommandSender commandSender, String[] arguments) {
    super(message, cause);
    this.commandSender = (CommandSender)Preconditions.checkNotNull(commandSender, "commandSender");
    this.arguments = (String[])Preconditions.checkNotNull(arguments, "arguments");
    this.command = (Command)Preconditions.checkNotNull(command, "command");
  }
  
  public ProxyCommandException(Throwable cause, Command command, CommandSender commandSender, String[] arguments) {
    super(cause);
    this.commandSender = (CommandSender)Preconditions.checkNotNull(commandSender, "commandSender");
    this.arguments = (String[])Preconditions.checkNotNull(arguments, "arguments");
    this.command = (Command)Preconditions.checkNotNull(command, "command");
  }
  
  protected ProxyCommandException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Command command, CommandSender commandSender, String[] arguments) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.commandSender = (CommandSender)Preconditions.checkNotNull(commandSender, "commandSender");
    this.arguments = (String[])Preconditions.checkNotNull(arguments, "arguments");
    this.command = (Command)Preconditions.checkNotNull(command, "command");
  }
  
  public Command getCommand() {
    return this.command;
  }
  
  public CommandSender getCommandSender() {
    return this.commandSender;
  }
  
  public String[] getArguments() {
    return this.arguments;
  }
}
