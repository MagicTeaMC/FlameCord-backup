package net.md_5.bungee.api.chat.hover.content;

import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;

public class Entity extends Content {
  private String type;
  
  @NonNull
  private String id;
  
  private BaseComponent name;
  
  public void setType(String type) {
    this.type = type;
  }
  
  public void setId(@NonNull String id) {
    if (id == null)
      throw new NullPointerException("id is marked non-null but is null"); 
    this.id = id;
  }
  
  public void setName(BaseComponent name) {
    this.name = name;
  }
  
  public Entity(String type, @NonNull String id, BaseComponent name) {
    if (id == null)
      throw new NullPointerException("id is marked non-null but is null"); 
    this.type = type;
    this.id = id;
    this.name = name;
  }
  
  public String toString() {
    return "Entity(type=" + getType() + ", id=" + getId() + ", name=" + getName() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Entity))
      return false; 
    Entity other = (Entity)o;
    if (!other.canEqual(this))
      return false; 
    if (!super.equals(o))
      return false; 
    Object this$type = getType(), other$type = other.getType();
    if ((this$type == null) ? (other$type != null) : !this$type.equals(other$type))
      return false; 
    Object this$id = getId(), other$id = other.getId();
    if ((this$id == null) ? (other$id != null) : !this$id.equals(other$id))
      return false; 
    Object this$name = getName(), other$name = other.getName();
    return !((this$name == null) ? (other$name != null) : !this$name.equals(other$name));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Entity;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = super.hashCode();
    Object $type = getType();
    result = result * 59 + (($type == null) ? 43 : $type.hashCode());
    Object $id = getId();
    result = result * 59 + (($id == null) ? 43 : $id.hashCode());
    Object $name = getName();
    return result * 59 + (($name == null) ? 43 : $name.hashCode());
  }
  
  public String getType() {
    return this.type;
  }
  
  @NonNull
  public String getId() {
    return this.id;
  }
  
  public BaseComponent getName() {
    return this.name;
  }
  
  public HoverEvent.Action requiredAction() {
    return HoverEvent.Action.SHOW_ENTITY;
  }
}
