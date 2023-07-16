package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;

public class IntegerArgumentType implements ArgumentType<Integer> {
  private static final Collection<String> EXAMPLES = Arrays.asList(new String[] { "0", "123", "-123" });
  
  private final int minimum;
  
  private final int maximum;
  
  private IntegerArgumentType(int minimum, int maximum) {
    this.minimum = minimum;
    this.maximum = maximum;
  }
  
  public static IntegerArgumentType integer() {
    return integer(-2147483648);
  }
  
  public static IntegerArgumentType integer(int min) {
    return integer(min, 2147483647);
  }
  
  public static IntegerArgumentType integer(int min, int max) {
    return new IntegerArgumentType(min, max);
  }
  
  public static int getInteger(CommandContext<?> context, String name) {
    return ((Integer)context.getArgument(name, int.class)).intValue();
  }
  
  public int getMinimum() {
    return this.minimum;
  }
  
  public int getMaximum() {
    return this.maximum;
  }
  
  public Integer parse(StringReader reader) throws CommandSyntaxException {
    int start = reader.getCursor();
    int result = reader.readInt();
    if (result < this.minimum) {
      reader.setCursor(start);
      throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow().createWithContext(reader, Integer.valueOf(result), Integer.valueOf(this.minimum));
    } 
    if (result > this.maximum) {
      reader.setCursor(start);
      throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooHigh().createWithContext(reader, Integer.valueOf(result), Integer.valueOf(this.maximum));
    } 
    return Integer.valueOf(result);
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (!(o instanceof IntegerArgumentType))
      return false; 
    IntegerArgumentType that = (IntegerArgumentType)o;
    return (this.maximum == that.maximum && this.minimum == that.minimum);
  }
  
  public int hashCode() {
    return 31 * this.minimum + this.maximum;
  }
  
  public String toString() {
    if (this.minimum == Integer.MIN_VALUE && this.maximum == Integer.MAX_VALUE)
      return "integer()"; 
    if (this.maximum == Integer.MAX_VALUE)
      return "integer(" + this.minimum + ")"; 
    return "integer(" + this.minimum + ", " + this.maximum + ")";
  }
  
  public Collection<String> getExamples() {
    return EXAMPLES;
  }
}
