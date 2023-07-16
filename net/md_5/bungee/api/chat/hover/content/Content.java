package net.md_5.bungee.api.chat.hover.content;

import net.md_5.bungee.api.chat.HoverEvent;

public abstract class Content {
  public String toString() {
    return "Content()";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Content))
      return false; 
    Content other = (Content)o;
    return !!other.canEqual(this);
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Content;
  }
  
  public int hashCode() {
    int result = 1;
    return 1;
  }
  
  public void assertAction(HoverEvent.Action input) throws UnsupportedOperationException {
    if (input != requiredAction())
      throw new UnsupportedOperationException("Action " + input + " not compatible! Expected " + requiredAction()); 
  }
  
  public abstract HoverEvent.Action requiredAction();
}
