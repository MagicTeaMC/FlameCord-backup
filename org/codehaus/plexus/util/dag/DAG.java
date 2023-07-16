package org.codehaus.plexus.util.dag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DAG implements Cloneable, Serializable {
  private Map<String, Vertex> vertexMap = new HashMap<String, Vertex>();
  
  private List<Vertex> vertexList = new ArrayList<Vertex>();
  
  public List<Vertex> getVertices() {
    return this.vertexList;
  }
  
  @Deprecated
  public List<Vertex> getVerticies() {
    return getVertices();
  }
  
  public Set<String> getLabels() {
    return this.vertexMap.keySet();
  }
  
  public Vertex addVertex(String label) {
    Vertex retValue = null;
    if (this.vertexMap.containsKey(label)) {
      retValue = this.vertexMap.get(label);
    } else {
      retValue = new Vertex(label);
      this.vertexMap.put(label, retValue);
      this.vertexList.add(retValue);
    } 
    return retValue;
  }
  
  public void addEdge(String from, String to) throws CycleDetectedException {
    Vertex v1 = addVertex(from);
    Vertex v2 = addVertex(to);
    addEdge(v1, v2);
  }
  
  public void addEdge(Vertex from, Vertex to) throws CycleDetectedException {
    from.addEdgeTo(to);
    to.addEdgeFrom(from);
    List<String> cycle = CycleDetector.introducesCycle(to);
    if (cycle != null) {
      removeEdge(from, to);
      String msg = "Edge between '" + from + "' and '" + to + "' introduces to cycle in the graph";
      throw new CycleDetectedException(msg, cycle);
    } 
  }
  
  public void removeEdge(String from, String to) {
    Vertex v1 = addVertex(from);
    Vertex v2 = addVertex(to);
    removeEdge(v1, v2);
  }
  
  public void removeEdge(Vertex from, Vertex to) {
    from.removeEdgeTo(to);
    to.removeEdgeFrom(from);
  }
  
  public Vertex getVertex(String label) {
    Vertex retValue = this.vertexMap.get(label);
    return retValue;
  }
  
  public boolean hasEdge(String label1, String label2) {
    Vertex v1 = getVertex(label1);
    Vertex v2 = getVertex(label2);
    boolean retValue = v1.getChildren().contains(v2);
    return retValue;
  }
  
  public List<String> getChildLabels(String label) {
    Vertex vertex = getVertex(label);
    return vertex.getChildLabels();
  }
  
  public List<String> getParentLabels(String label) {
    Vertex vertex = getVertex(label);
    return vertex.getParentLabels();
  }
  
  public Object clone() throws CloneNotSupportedException {
    Object retValue = super.clone();
    return retValue;
  }
  
  public boolean isConnected(String label) {
    Vertex vertex = getVertex(label);
    boolean retValue = vertex.isConnected();
    return retValue;
  }
  
  public List<String> getSuccessorLabels(String label) {
    List<String> retValue;
    Vertex vertex = getVertex(label);
    if (vertex.isLeaf()) {
      retValue = new ArrayList<String>(1);
      retValue.add(label);
    } else {
      retValue = TopologicalSorter.sort(vertex);
    } 
    return retValue;
  }
}
