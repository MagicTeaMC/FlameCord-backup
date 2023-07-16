package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

@FunctionalInterface
public interface Command<S> {
  public static final int SINGLE_SUCCESS = 1;
  
  int run(CommandContext<S> paramCommandContext) throws CommandSyntaxException;
}
