package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContext;

@FunctionalInterface
public interface ResultConsumer<S> {
  void onCommandComplete(CommandContext<S> paramCommandContext, boolean paramBoolean, int paramInt);
}
