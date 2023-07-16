package com.mojang.brigadier.tree;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class LiteralCommandNode<S> extends CommandNode<S> {
  private final String literal;
  
  public LiteralCommandNode(String literal, Command<S> command, Predicate<S> requirement, CommandNode<S> redirect, RedirectModifier<S> modifier, boolean forks) {
    super(command, requirement, redirect, modifier, forks);
    this.literal = literal;
  }
  
  public String getLiteral() {
    return this.literal;
  }
  
  public String getName() {
    return this.literal;
  }
  
  public void parse(StringReader reader, CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException {
    int start = reader.getCursor();
    int end = parse(reader);
    if (end > -1) {
      contextBuilder.withNode(this, StringRange.between(start, end));
      return;
    } 
    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().createWithContext(reader, this.literal);
  }
  
  private int parse(StringReader reader) {
    int start = reader.getCursor();
    if (reader.canRead(this.literal.length())) {
      int end = start + this.literal.length();
      if (reader.getString().substring(start, end).equals(this.literal)) {
        reader.setCursor(end);
        if (!reader.canRead() || reader.peek() == ' ')
          return end; 
        reader.setCursor(start);
      } 
    } 
    return -1;
  }
  
  public CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    if (this.literal.toLowerCase().startsWith(builder.getRemaining().toLowerCase()))
      return builder.suggest(this.literal).buildFuture(); 
    return Suggestions.empty();
  }
  
  public boolean isValidInput(String input) {
    return (parse(new StringReader(input)) > -1);
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (!(o instanceof LiteralCommandNode))
      return false; 
    LiteralCommandNode that = (LiteralCommandNode)o;
    if (!this.literal.equals(that.literal))
      return false; 
    return super.equals(o);
  }
  
  public String getUsageText() {
    return this.literal;
  }
  
  public int hashCode() {
    int result = this.literal.hashCode();
    result = 31 * result + super.hashCode();
    return result;
  }
  
  public LiteralArgumentBuilder<S> createBuilder() {
    LiteralArgumentBuilder<S> builder = LiteralArgumentBuilder.literal(this.literal);
    builder.requires(getRequirement());
    builder.forward(getRedirect(), getRedirectModifier(), isFork());
    if (getCommand() != null)
      builder.executes(getCommand()); 
    return builder;
  }
  
  protected String getSortedKey() {
    return this.literal;
  }
  
  public Collection<String> getExamples() {
    return Collections.singleton(this.literal);
  }
  
  public String toString() {
    return "<literal " + this.literal + ">";
  }
}
