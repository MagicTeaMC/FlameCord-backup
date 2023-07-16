package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class BoolArgumentType implements ArgumentType<Boolean> {
  private static final Collection<String> EXAMPLES = Arrays.asList(new String[] { "true", "false" });
  
  public static BoolArgumentType bool() {
    return new BoolArgumentType();
  }
  
  public static boolean getBool(CommandContext<?> context, String name) {
    return ((Boolean)context.getArgument(name, Boolean.class)).booleanValue();
  }
  
  public Boolean parse(StringReader reader) throws CommandSyntaxException {
    return Boolean.valueOf(reader.readBoolean());
  }
  
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    if ("true".startsWith(builder.getRemaining().toLowerCase()))
      builder.suggest("true"); 
    if ("false".startsWith(builder.getRemaining().toLowerCase()))
      builder.suggest("false"); 
    return builder.buildFuture();
  }
  
  public Collection<String> getExamples() {
    return EXAMPLES;
  }
}
