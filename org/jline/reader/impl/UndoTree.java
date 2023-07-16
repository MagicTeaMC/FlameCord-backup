package org.jline.reader.impl;

import java.util.function.Consumer;

public class UndoTree<T> {
  private final Consumer<T> state;
  
  private final Node parent;
  
  private Node current;
  
  public UndoTree(Consumer<T> s) {
    this.state = s;
    this.parent = new Node(null);
    this.parent.left = this.parent;
    clear();
  }
  
  public void clear() {
    this.current = this.parent;
  }
  
  public void newState(T state) {
    Node node = new Node(state);
    this.current.right = node;
    node.left = this.current;
    this.current = node;
  }
  
  public boolean canUndo() {
    return (this.current.left != this.parent);
  }
  
  public boolean canRedo() {
    return (this.current.right != null);
  }
  
  public void undo() {
    if (!canUndo())
      throw new IllegalStateException("Cannot undo."); 
    this.current = this.current.left;
    this.state.accept(this.current.state);
  }
  
  public void redo() {
    if (!canRedo())
      throw new IllegalStateException("Cannot redo."); 
    this.current = this.current.right;
    this.state.accept(this.current.state);
  }
  
  private class Node {
    private final T state;
    
    private Node left = null;
    
    private Node right = null;
    
    public Node(T s) {
      this.state = s;
    }
  }
}
