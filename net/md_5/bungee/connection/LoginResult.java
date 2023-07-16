package net.md_5.bungee.connection;

import java.util.Arrays;
import net.md_5.bungee.protocol.Property;

public class LoginResult {
  private String id;
  
  private String name;
  
  private Property[] properties;
  
  public void setId(String id) {
    this.id = id;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setProperties(Property[] properties) {
    this.properties = properties;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof LoginResult))
      return false; 
    LoginResult other = (LoginResult)o;
    if (!other.canEqual(this))
      return false; 
    Object this$id = getId(), other$id = other.getId();
    if ((this$id == null) ? (other$id != null) : !this$id.equals(other$id))
      return false; 
    Object this$name = getName(), other$name = other.getName();
    return ((this$name == null) ? (other$name != null) : !this$name.equals(other$name)) ? false : (!!Arrays.deepEquals((Object[])getProperties(), (Object[])other.getProperties()));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof LoginResult;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $id = getId();
    result = result * 59 + (($id == null) ? 43 : $id.hashCode());
    Object $name = getName();
    result = result * 59 + (($name == null) ? 43 : $name.hashCode());
    return result * 59 + Arrays.deepHashCode((Object[])getProperties());
  }
  
  public String toString() {
    return "LoginResult(id=" + getId() + ", name=" + getName() + ", properties=" + Arrays.deepToString((Object[])getProperties()) + ")";
  }
  
  public LoginResult(String id, String name, Property[] properties) {
    this.id = id;
    this.name = name;
    this.properties = properties;
  }
  
  public String getId() {
    return this.id;
  }
  
  public String getName() {
    return this.name;
  }
  
  public Property[] getProperties() {
    return this.properties;
  }
}
