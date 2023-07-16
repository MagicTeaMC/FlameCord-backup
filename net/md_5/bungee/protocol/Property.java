package net.md_5.bungee.protocol;

public class Property {
  private String name;
  
  private String value;
  
  private String signature;
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setValue(String value) {
    this.value = value;
  }
  
  public void setSignature(String signature) {
    this.signature = signature;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Property))
      return false; 
    Property other = (Property)o;
    if (!other.canEqual(this))
      return false; 
    Object this$name = getName(), other$name = other.getName();
    if ((this$name == null) ? (other$name != null) : !this$name.equals(other$name))
      return false; 
    Object this$value = getValue(), other$value = other.getValue();
    if ((this$value == null) ? (other$value != null) : !this$value.equals(other$value))
      return false; 
    Object this$signature = getSignature(), other$signature = other.getSignature();
    return !((this$signature == null) ? (other$signature != null) : !this$signature.equals(other$signature));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Property;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $name = getName();
    result = result * 59 + (($name == null) ? 43 : $name.hashCode());
    Object $value = getValue();
    result = result * 59 + (($value == null) ? 43 : $value.hashCode());
    Object $signature = getSignature();
    return result * 59 + (($signature == null) ? 43 : $signature.hashCode());
  }
  
  public String toString() {
    return "Property(name=" + getName() + ", value=" + getValue() + ", signature=" + getSignature() + ")";
  }
  
  public Property(String name, String value, String signature) {
    this.name = name;
    this.value = value;
    this.signature = signature;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public String getSignature() {
    return this.signature;
  }
  
  public Property(String name, String value) {
    this(name, value, null);
  }
}
