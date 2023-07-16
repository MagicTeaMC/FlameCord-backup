package org.jline.reader;

public class Reference implements Binding {
  private final String name;
  
  public Reference(String name) {
    this.name = name;
  }
  
  public String name() {
    return this.name;
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    Reference func = (Reference)o;
    return this.name.equals(func.name);
  }
  
  public int hashCode() {
    return this.name.hashCode();
  }
  
  public String toString() {
    return "Reference[" + this.name + ']';
  }
}
