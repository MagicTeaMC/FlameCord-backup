package org.codehaus.plexus.util.dag;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CycleDetector {
  private static final Integer NOT_VISITED = Integer.valueOf(0);
  
  private static final Integer VISITING = Integer.valueOf(1);
  
  private static final Integer VISITED = Integer.valueOf(2);
  
  public static List<String> hasCycle(DAG graph) {
    List<Vertex> vertices = graph.getVertices();
    Map<Vertex, Integer> vertexStateMap = new HashMap<Vertex, Integer>();
    List<String> retValue = null;
    for (Vertex vertex : vertices) {
      if (isNotVisited(vertex, vertexStateMap)) {
        retValue = introducesCycle(vertex, vertexStateMap);
        if (retValue != null)
          break; 
      } 
    } 
    return retValue;
  }
  
  public static List<String> introducesCycle(Vertex vertex, Map<Vertex, Integer> vertexStateMap) {
    LinkedList<String> cycleStack = new LinkedList<String>();
    boolean hasCycle = dfsVisit(vertex, cycleStack, vertexStateMap);
    if (hasCycle) {
      String label = cycleStack.getFirst();
      int pos = cycleStack.lastIndexOf(label);
      List<String> cycle = cycleStack.subList(0, pos + 1);
      Collections.reverse(cycle);
      return cycle;
    } 
    return null;
  }
  
  public static List<String> introducesCycle(Vertex vertex) {
    Map<Vertex, Integer> vertexStateMap = new HashMap<Vertex, Integer>();
    return introducesCycle(vertex, vertexStateMap);
  }
  
  private static boolean isNotVisited(Vertex vertex, Map<Vertex, Integer> vertexStateMap) {
    Integer state = vertexStateMap.get(vertex);
    return (state == null || NOT_VISITED.equals(state));
  }
  
  private static boolean isVisiting(Vertex vertex, Map<Vertex, Integer> vertexStateMap) {
    Integer state = vertexStateMap.get(vertex);
    return VISITING.equals(state);
  }
  
  private static boolean dfsVisit(Vertex vertex, LinkedList<String> cycle, Map<Vertex, Integer> vertexStateMap) {
    cycle.addFirst(vertex.getLabel());
    vertexStateMap.put(vertex, VISITING);
    for (Vertex v : vertex.getChildren()) {
      if (isNotVisited(v, vertexStateMap)) {
        boolean hasCycle = dfsVisit(v, cycle, vertexStateMap);
        if (hasCycle)
          return true; 
        continue;
      } 
      if (isVisiting(v, vertexStateMap)) {
        cycle.addFirst(v.getLabel());
        return true;
      } 
    } 
    vertexStateMap.put(vertex, VISITED);
    cycle.removeFirst();
    return false;
  }
}
