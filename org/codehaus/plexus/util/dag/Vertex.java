package org.codehaus.plexus.util.dag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Vertex implements Cloneable, Serializable {
  private String label = null;
  
  List<Vertex> children = new ArrayList<Vertex>();
  
  List<Vertex> parents = new ArrayList<Vertex>();
  
  public Vertex(String label) {
    this.label = label;
  }
  
  public String getLabel() {
    return this.label;
  }
  
  public void addEdgeTo(Vertex vertex) {
    this.children.add(vertex);
  }
  
  public void removeEdgeTo(Vertex vertex) {
    this.children.remove(vertex);
  }
  
  public void addEdgeFrom(Vertex vertex) {
    this.parents.add(vertex);
  }
  
  public void removeEdgeFrom(Vertex vertex) {
    this.parents.remove(vertex);
  }
  
  public List<Vertex> getChildren() {
    return this.children;
  }
  
  public List<String> getChildLabels() {
    List<String> retValue = new ArrayList<String>(this.children.size());
    for (Vertex vertex : this.children)
      retValue.add(vertex.getLabel()); 
    return retValue;
  }
  
  public List<Vertex> getParents() {
    return this.parents;
  }
  
  public List<String> getParentLabels() {
    List<String> retValue = new ArrayList<String>(this.parents.size());
    for (Vertex vertex : this.parents)
      retValue.add(vertex.getLabel()); 
    return retValue;
  }
  
  public boolean isLeaf() {
    return (this.children.size() == 0);
  }
  
  public boolean isRoot() {
    return (this.parents.size() == 0);
  }
  
  public boolean isConnected() {
    return (isRoot() || isLeaf());
  }
  
  public Object clone() throws CloneNotSupportedException {
    Object retValue = super.clone();
    return retValue;
  }
  
  public String toString() {
    return "Vertex{label='" + this.label + "'" + "}";
  }
}
