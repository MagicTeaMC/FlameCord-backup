package com.mojang.brigadier.suggestion;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import java.util.Objects;

public class IntegerSuggestion extends Suggestion {
  private int value;
  
  public IntegerSuggestion(StringRange range, int value) {
    this(range, value, (Message)null);
  }
  
  public IntegerSuggestion(StringRange range, int value, Message tooltip) {
    super(range, Integer.toString(value), tooltip);
    this.value = value;
  }
  
  public int getValue() {
    return this.value;
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (!(o instanceof IntegerSuggestion))
      return false; 
    IntegerSuggestion that = (IntegerSuggestion)o;
    return (this.value == that.value && super.equals(o));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { Integer.valueOf(super.hashCode()), Integer.valueOf(this.value) });
  }
  
  public String toString() {
    return "IntegerSuggestion{value=" + this.value + ", range=" + 
      
      getRange() + ", text='" + 
      getText() + '\'' + ", tooltip='" + 
      getTooltip() + '\'' + '}';
  }
  
  public int compareTo(Suggestion o) {
    if (o instanceof IntegerSuggestion)
      return Integer.compare(this.value, ((IntegerSuggestion)o).value); 
    return super.compareTo(o);
  }
  
  public int compareToIgnoreCase(Suggestion b) {
    return compareTo(b);
  }
}
