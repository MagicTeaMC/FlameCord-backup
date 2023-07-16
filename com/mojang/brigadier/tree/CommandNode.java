package com.mojang.brigadier.tree;

import com.mojang.brigadier.AmbiguityConsumer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class CommandNode<S> implements Comparable<CommandNode<S>> {
  private Map<String, CommandNode<S>> children = new LinkedHashMap<>();
  
  private Map<String, LiteralCommandNode<S>> literals = new LinkedHashMap<>();
  
  private Map<String, ArgumentCommandNode<S, ?>> arguments = new LinkedHashMap<>();
  
  private final Predicate<S> requirement;
  
  private final CommandNode<S> redirect;
  
  private final RedirectModifier<S> modifier;
  
  private final boolean forks;
  
  private Command<S> command;
  
  protected CommandNode(Command<S> command, Predicate<S> requirement, CommandNode<S> redirect, RedirectModifier<S> modifier, boolean forks) {
    this.command = command;
    this.requirement = requirement;
    this.redirect = redirect;
    this.modifier = modifier;
    this.forks = forks;
  }
  
  public Command<S> getCommand() {
    return this.command;
  }
  
  public Collection<CommandNode<S>> getChildren() {
    return this.children.values();
  }
  
  public CommandNode<S> getChild(String name) {
    return this.children.get(name);
  }
  
  public CommandNode<S> getRedirect() {
    return this.redirect;
  }
  
  public RedirectModifier<S> getRedirectModifier() {
    return this.modifier;
  }
  
  public boolean canUse(S source) {
    return this.requirement.test(source);
  }
  
  public void addChild(CommandNode<S> node) {
    if (node instanceof RootCommandNode)
      throw new UnsupportedOperationException("Cannot add a RootCommandNode as a child to any other CommandNode"); 
    CommandNode<S> child = this.children.get(node.getName());
    if (child != null) {
      if (node.getCommand() != null)
        child.command = node.getCommand(); 
      for (CommandNode<S> grandchild : node.getChildren())
        child.addChild(grandchild); 
    } else {
      this.children.put(node.getName(), node);
      if (node instanceof LiteralCommandNode) {
        this.literals.put(node.getName(), (LiteralCommandNode<S>)node);
      } else if (node instanceof ArgumentCommandNode) {
        this.arguments.put(node.getName(), (ArgumentCommandNode)node);
      } 
    } 
    this.children = (Map<String, CommandNode<S>>)this.children.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }
  
  public void findAmbiguities(AmbiguityConsumer<S> consumer) {
    Set<String> matches = new HashSet<>();
    for (CommandNode<S> child : this.children.values()) {
      for (CommandNode<S> sibling : this.children.values()) {
        if (child == sibling)
          continue; 
        for (String input : child.getExamples()) {
          if (sibling.isValidInput(input))
            matches.add(input); 
        } 
        if (matches.size() > 0) {
          consumer.ambiguous(this, child, sibling, matches);
          matches = new HashSet<>();
        } 
      } 
      child.findAmbiguities(consumer);
    } 
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (!(o instanceof CommandNode))
      return false; 
    CommandNode<S> that = (CommandNode<S>)o;
    if (!this.children.equals(that.children))
      return false; 
    if ((this.command != null) ? !this.command.equals(that.command) : (that.command != null))
      return false; 
    return true;
  }
  
  public int hashCode() {
    return 31 * this.children.hashCode() + ((this.command != null) ? this.command.hashCode() : 0);
  }
  
  public Predicate<S> getRequirement() {
    return this.requirement;
  }
  
  public Collection<? extends CommandNode<S>> getRelevantNodes(StringReader input) {
    if (this.literals.size() > 0) {
      int cursor = input.getCursor();
      while (input.canRead() && input.peek() != ' ')
        input.skip(); 
      String text = input.getString().substring(cursor, input.getCursor());
      input.setCursor(cursor);
      LiteralCommandNode<S> literal = this.literals.get(text);
      if (literal != null)
        return Collections.singleton(literal); 
      return this.arguments.values();
    } 
    return this.arguments.values();
  }
  
  public int compareTo(CommandNode<S> o) {
    if (this instanceof LiteralCommandNode == o instanceof LiteralCommandNode)
      return getSortedKey().compareTo(o.getSortedKey()); 
    return (o instanceof LiteralCommandNode) ? 1 : -1;
  }
  
  public boolean isFork() {
    return this.forks;
  }
  
  protected abstract boolean isValidInput(String paramString);
  
  public abstract String getName();
  
  public abstract String getUsageText();
  
  public abstract void parse(StringReader paramStringReader, CommandContextBuilder<S> paramCommandContextBuilder) throws CommandSyntaxException;
  
  public abstract CompletableFuture<Suggestions> listSuggestions(CommandContext<S> paramCommandContext, SuggestionsBuilder paramSuggestionsBuilder) throws CommandSyntaxException;
  
  public abstract ArgumentBuilder<S, ?> createBuilder();
  
  protected abstract String getSortedKey();
  
  public abstract Collection<String> getExamples();
}
