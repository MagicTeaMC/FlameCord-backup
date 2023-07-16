package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;

public class DoubleArgumentType implements ArgumentType<Double> {
  private static final Collection<String> EXAMPLES = Arrays.asList(new String[] { "0", "1.2", ".5", "-1", "-.5", "-1234.56" });
  
  private final double minimum;
  
  private final double maximum;
  
  private DoubleArgumentType(double minimum, double maximum) {
    this.minimum = minimum;
    this.maximum = maximum;
  }
  
  public static DoubleArgumentType doubleArg() {
    return doubleArg(-1.7976931348623157E308D);
  }
  
  public static DoubleArgumentType doubleArg(double min) {
    return doubleArg(min, Double.MAX_VALUE);
  }
  
  public static DoubleArgumentType doubleArg(double min, double max) {
    return new DoubleArgumentType(min, max);
  }
  
  public static double getDouble(CommandContext<?> context, String name) {
    return ((Double)context.getArgument(name, Double.class)).doubleValue();
  }
  
  public double getMinimum() {
    return this.minimum;
  }
  
  public double getMaximum() {
    return this.maximum;
  }
  
  public Double parse(StringReader reader) throws CommandSyntaxException {
    int start = reader.getCursor();
    double result = reader.readDouble();
    if (result < this.minimum) {
      reader.setCursor(start);
      throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.doubleTooLow().createWithContext(reader, Double.valueOf(result), Double.valueOf(this.minimum));
    } 
    if (result > this.maximum) {
      reader.setCursor(start);
      throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.doubleTooHigh().createWithContext(reader, Double.valueOf(result), Double.valueOf(this.maximum));
    } 
    return Double.valueOf(result);
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (!(o instanceof DoubleArgumentType))
      return false; 
    DoubleArgumentType that = (DoubleArgumentType)o;
    return (this.maximum == that.maximum && this.minimum == that.minimum);
  }
  
  public int hashCode() {
    return (int)(31.0D * this.minimum + this.maximum);
  }
  
  public String toString() {
    if (this.minimum == -1.7976931348623157E308D && this.maximum == Double.MAX_VALUE)
      return "double()"; 
    if (this.maximum == Double.MAX_VALUE)
      return "double(" + this.minimum + ")"; 
    return "double(" + this.minimum + ", " + this.maximum + ")";
  }
  
  public Collection<String> getExamples() {
    return EXAMPLES;
  }
}
