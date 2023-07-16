package com.mojang.brigadier.tree;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class ArgumentCommandNode<S, T> extends CommandNode<S> {
  private static final String USAGE_ARGUMENT_OPEN = "<";
  
  private static final String USAGE_ARGUMENT_CLOSE = ">";
  
  private final String name;
  
  private final ArgumentType<T> type;
  
  private final SuggestionProvider<S> customSuggestions;
  
  public ArgumentCommandNode(String name, ArgumentType<T> type, Command<S> command, Predicate<S> requirement, CommandNode<S> redirect, RedirectModifier<S> modifier, boolean forks, SuggestionProvider<S> customSuggestions) {
    super(command, requirement, redirect, modifier, forks);
    this.name = name;
    this.type = type;
    this.customSuggestions = customSuggestions;
  }
  
  public ArgumentType<T> getType() {
    return this.type;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getUsageText() {
    return "<" + this.name + ">";
  }
  
  public SuggestionProvider<S> getCustomSuggestions() {
    return this.customSuggestions;
  }
  
  public void parse(StringReader reader, CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException {
    int start = reader.getCursor();
    T result = (T)this.type.parse(reader);
    ParsedArgument<S, T> parsed = new ParsedArgument(start, reader.getCursor(), result);
    contextBuilder.withArgument(this.name, parsed);
    contextBuilder.withNode(this, parsed.getRange());
  }
  
  public CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) throws CommandSyntaxException {
    if (this.customSuggestions == null)
      return this.type.listSuggestions(context, builder); 
    return this.customSuggestions.getSuggestions(context, builder);
  }
  
  public RequiredArgumentBuilder<S, T> createBuilder() {
    RequiredArgumentBuilder<S, T> builder = RequiredArgumentBuilder.argument(this.name, this.type);
    builder.requires(getRequirement());
    builder.forward(getRedirect(), getRedirectModifier(), isFork());
    builder.suggests(this.customSuggestions);
    if (getCommand() != null)
      builder.executes(getCommand()); 
    return builder;
  }
  
  public boolean isValidInput(String input) {
    try {
      StringReader reader = new StringReader(input);
      this.type.parse(reader);
      return (!reader.canRead() || reader.peek() == ' ');
    } catch (CommandSyntaxException ignored) {
      return false;
    } 
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (!(o instanceof ArgumentCommandNode))
      return false; 
    ArgumentCommandNode that = (ArgumentCommandNode)o;
    if (!this.name.equals(that.name))
      return false; 
    if (!this.type.equals(that.type))
      return false; 
    return super.equals(o);
  }
  
  public int hashCode() {
    int result = this.name.hashCode();
    result = 31 * result + this.type.hashCode();
    return result;
  }
  
  protected String getSortedKey() {
    return this.name;
  }
  
  public Collection<String> getExamples() {
    return this.type.getExamples();
  }
  
  public String toString() {
    return "<argument " + this.name + ":" + this.type + ">";
  }
}
