package net.md_5.bungee.api.chat;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.chat.ComponentSerializer;

public final class HoverEvent {
  private final Action action;
  
  private final List<Content> contents;
  
  public String toString() {
    return "HoverEvent(action=" + getAction() + ", contents=" + getContents() + ", legacy=" + isLegacy() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof HoverEvent))
      return false; 
    HoverEvent other = (HoverEvent)o;
    if (isLegacy() != other.isLegacy())
      return false; 
    Object this$action = getAction(), other$action = other.getAction();
    if ((this$action == null) ? (other$action != null) : !this$action.equals(other$action))
      return false; 
    Object<Content> this$contents = (Object<Content>)getContents(), other$contents = (Object<Content>)other.getContents();
    return !((this$contents == null) ? (other$contents != null) : !this$contents.equals(other$contents));
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + (isLegacy() ? 79 : 97);
    Object $action = getAction();
    result = result * 59 + (($action == null) ? 43 : $action.hashCode());
    Object<Content> $contents = (Object<Content>)getContents();
    return result * 59 + (($contents == null) ? 43 : $contents.hashCode());
  }
  
  public HoverEvent(Action action, List<Content> contents) {
    this.action = action;
    this.contents = contents;
  }
  
  public Action getAction() {
    return this.action;
  }
  
  public List<Content> getContents() {
    return this.contents;
  }
  
  private boolean legacy = false;
  
  public void setLegacy(boolean legacy) {
    this.legacy = legacy;
  }
  
  public boolean isLegacy() {
    return this.legacy;
  }
  
  public HoverEvent(Action action, Content... contents) {
    Preconditions.checkArgument((contents.length != 0), "Must contain at least one content");
    this.action = action;
    this.contents = new ArrayList<>();
    for (Content it : contents)
      addContent(it); 
  }
  
  @Deprecated
  public HoverEvent(Action action, BaseComponent[] value) {
    this.action = action;
    this.contents = new ArrayList<>((Collection)Collections.singletonList(new Text(value)));
    this.legacy = true;
  }
  
  @Deprecated
  public BaseComponent[] getValue() {
    Content content = this.contents.get(0);
    if (content instanceof Text && ((Text)content).getValue() instanceof BaseComponent[])
      return (BaseComponent[])((Text)content).getValue(); 
    TextComponent component = new TextComponent(ComponentSerializer.toString(content));
    return new BaseComponent[] { component };
  }
  
  public void addContent(Content content) throws UnsupportedOperationException {
    Preconditions.checkArgument((!this.legacy || this.contents.size() == 0), "Legacy HoverEvent may not have more than one content");
    content.assertAction(this.action);
    this.contents.add(content);
  }
  
  public enum Action {
    SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY, SHOW_ACHIEVEMENT;
  }
  
  public static Class<?> getClass(Action action, boolean array) {
    Preconditions.checkArgument((action != null), "action");
    switch (action) {
      case SHOW_TEXT:
        return array ? Text[].class : Text.class;
      case SHOW_ENTITY:
        return array ? Entity[].class : Entity.class;
      case SHOW_ITEM:
        return array ? Item[].class : Item.class;
    } 
    throw new UnsupportedOperationException("Action '" + action.name() + " not supported");
  }
}
