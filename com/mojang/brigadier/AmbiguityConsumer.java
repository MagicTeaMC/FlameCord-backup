package com.mojang.brigadier;

import com.mojang.brigadier.tree.CommandNode;
import java.util.Collection;

@FunctionalInterface
public interface AmbiguityConsumer<S> {
  void ambiguous(CommandNode<S> paramCommandNode1, CommandNode<S> paramCommandNode2, CommandNode<S> paramCommandNode3, Collection<String> paramCollection);
}
