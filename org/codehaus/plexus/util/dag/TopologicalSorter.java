package org.codehaus.plexus.util.dag;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TopologicalSorter {
  private static final Integer NOT_VISITED = Integer.valueOf(0);
  
  private static final Integer VISITING = Integer.valueOf(1);
  
  private static final Integer VISITED = Integer.valueOf(2);
  
  public static List<String> sort(DAG graph) {
    return dfs(graph);
  }
  
  public static List<String> sort(Vertex vertex) {
    List<String> retValue = new LinkedList<String>();
    dfsVisit(vertex, new HashMap<Vertex, Integer>(), retValue);
    return retValue;
  }
  
  private static List<String> dfs(DAG graph) {
    List<String> retValue = new LinkedList<String>();
    Map<Vertex, Integer> vertexStateMap = new HashMap<Vertex, Integer>();
    for (Vertex vertex : graph.getVertices()) {
      if (isNotVisited(vertex, vertexStateMap))
        dfsVisit(vertex, vertexStateMap, retValue); 
    } 
    return retValue;
  }
  
  private static boolean isNotVisited(Vertex vertex, Map<Vertex, Integer> vertexStateMap) {
    Integer state = vertexStateMap.get(vertex);
    return (state == null || NOT_VISITED.equals(state));
  }
  
  private static void dfsVisit(Vertex vertex, Map<Vertex, Integer> vertexStateMap, List<String> list) {
    vertexStateMap.put(vertex, VISITING);
    for (Vertex v : vertex.getChildren()) {
      if (isNotVisited(v, vertexStateMap))
        dfsVisit(v, vertexStateMap, list); 
    } 
    vertexStateMap.put(vertex, VISITED);
    list.add(vertex.getLabel());
  }
}
