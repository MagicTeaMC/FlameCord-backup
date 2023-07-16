package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;

public class FloatArgumentType implements ArgumentType<Float> {
  private static final Collection<String> EXAMPLES = Arrays.asList(new String[] { "0", "1.2", ".5", "-1", "-.5", "-1234.56" });
  
  private final float minimum;
  
  private final float maximum;
  
  private FloatArgumentType(float minimum, float maximum) {
    this.minimum = minimum;
    this.maximum = maximum;
  }
  
  public static FloatArgumentType floatArg() {
    return floatArg(-3.4028235E38F);
  }
  
  public static FloatArgumentType floatArg(float min) {
    return floatArg(min, Float.MAX_VALUE);
  }
  
  public static FloatArgumentType floatArg(float min, float max) {
    return new FloatArgumentType(min, max);
  }
  
  public static float getFloat(CommandContext<?> context, String name) {
    return ((Float)context.getArgument(name, Float.class)).floatValue();
  }
  
  public float getMinimum() {
    return this.minimum;
  }
  
  public float getMaximum() {
    return this.maximum;
  }
  
  public Float parse(StringReader reader) throws CommandSyntaxException {
    int start = reader.getCursor();
    float result = reader.readFloat();
    if (result < this.minimum) {
      reader.setCursor(start);
      throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.floatTooLow().createWithContext(reader, Float.valueOf(result), Float.valueOf(this.minimum));
    } 
    if (result > this.maximum) {
      reader.setCursor(start);
      throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.floatTooHigh().createWithContext(reader, Float.valueOf(result), Float.valueOf(this.maximum));
    } 
    return Float.valueOf(result);
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (!(o instanceof FloatArgumentType))
      return false; 
    FloatArgumentType that = (FloatArgumentType)o;
    return (this.maximum == that.maximum && this.minimum == that.minimum);
  }
  
  public int hashCode() {
    return (int)(31.0F * this.minimum + this.maximum);
  }
  
  public String toString() {
    if (this.minimum == -3.4028235E38F && this.maximum == Float.MAX_VALUE)
      return "float()"; 
    if (this.maximum == Float.MAX_VALUE)
      return "float(" + this.minimum + ")"; 
    return "float(" + this.minimum + ", " + this.maximum + ")";
  }
  
  public Collection<String> getExamples() {
    return EXAMPLES;
  }
}
