package net.md_5.bungee.api.score;

public class Objective {
  private final String name;
  
  private String value;
  
  private String type;
  
  public void setValue(String value) {
    this.value = value;
  }
  
  public void setType(String type) {
    this.type = type;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Objective))
      return false; 
    Objective other = (Objective)o;
    if (!other.canEqual(this))
      return false; 
    Object this$name = getName(), other$name = other.getName();
    if ((this$name == null) ? (other$name != null) : !this$name.equals(other$name))
      return false; 
    Object this$value = getValue(), other$value = other.getValue();
    if ((this$value == null) ? (other$value != null) : !this$value.equals(other$value))
      return false; 
    Object this$type = getType(), other$type = other.getType();
    return !((this$type == null) ? (other$type != null) : !this$type.equals(other$type));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Objective;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $name = getName();
    result = result * 59 + (($name == null) ? 43 : $name.hashCode());
    Object $value = getValue();
    result = result * 59 + (($value == null) ? 43 : $value.hashCode());
    Object $type = getType();
    return result * 59 + (($type == null) ? 43 : $type.hashCode());
  }
  
  public String toString() {
    return "Objective(name=" + getName() + ", value=" + getValue() + ", type=" + getType() + ")";
  }
  
  public Objective(String name, String value, String type) {
    this.name = name;
    this.value = value;
    this.type = type;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public String getType() {
    return this.type;
  }
}
