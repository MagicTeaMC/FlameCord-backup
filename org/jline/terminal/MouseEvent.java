package org.jline.terminal;

import java.util.EnumSet;

public class MouseEvent {
  private final Type type;
  
  private final Button button;
  
  private final EnumSet<Modifier> modifiers;
  
  private final int x;
  
  private final int y;
  
  public enum Type {
    Released, Pressed, Wheel, Moved, Dragged;
  }
  
  public enum Button {
    NoButton, Button1, Button2, Button3, WheelUp, WheelDown;
  }
  
  public enum Modifier {
    Shift, Alt, Control;
  }
  
  public MouseEvent(Type type, Button button, EnumSet<Modifier> modifiers, int x, int y) {
    this.type = type;
    this.button = button;
    this.modifiers = modifiers;
    this.x = x;
    this.y = y;
  }
  
  public Type getType() {
    return this.type;
  }
  
  public Button getButton() {
    return this.button;
  }
  
  public EnumSet<Modifier> getModifiers() {
    return this.modifiers;
  }
  
  public int getX() {
    return this.x;
  }
  
  public int getY() {
    return this.y;
  }
  
  public String toString() {
    return "MouseEvent[type=" + this.type + ", button=" + this.button + ", modifiers=" + this.modifiers + ", x=" + this.x + ", y=" + this.y + ']';
  }
}
