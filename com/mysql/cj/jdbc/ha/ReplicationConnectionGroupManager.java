package com.mysql.cj.jdbc.ha;

import com.mysql.cj.jdbc.jmx.ReplicationGroupManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ReplicationConnectionGroupManager {
  private static HashMap<String, ReplicationConnectionGroup> GROUP_MAP = new HashMap<>();
  
  private static ReplicationGroupManager mbean = new ReplicationGroupManager();
  
  private static boolean hasRegisteredJmx = false;
  
  public static synchronized ReplicationConnectionGroup getConnectionGroupInstance(String groupName) {
    if (GROUP_MAP.containsKey(groupName))
      return GROUP_MAP.get(groupName); 
    ReplicationConnectionGroup group = new ReplicationConnectionGroup(groupName);
    GROUP_MAP.put(groupName, group);
    return group;
  }
  
  public static void registerJmx() throws SQLException {
    if (hasRegisteredJmx)
      return; 
    mbean.registerJmx();
    hasRegisteredJmx = true;
  }
  
  public static ReplicationConnectionGroup getConnectionGroup(String groupName) {
    return GROUP_MAP.get(groupName);
  }
  
  public static Collection<ReplicationConnectionGroup> getGroupsMatching(String group) {
    if (group == null || group.equals("")) {
      Set<ReplicationConnectionGroup> set = new HashSet<>();
      set.addAll(GROUP_MAP.values());
      return set;
    } 
    Set<ReplicationConnectionGroup> s = new HashSet<>();
    ReplicationConnectionGroup o = GROUP_MAP.get(group);
    if (o != null)
      s.add(o); 
    return s;
  }
  
  public static void addReplicaHost(String group, String hostPortPair) throws SQLException {
    Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
    for (ReplicationConnectionGroup cg : s)
      cg.addReplicaHost(hostPortPair); 
  }
  
  @Deprecated
  public static void addSlaveHost(String group, String hostPortPair) throws SQLException {
    addReplicaHost(group, hostPortPair);
  }
  
  public static void removeReplicaHost(String group, String hostPortPair) throws SQLException {
    removeReplicaHost(group, hostPortPair, true);
  }
  
  @Deprecated
  public static void removeSlaveHost(String group, String hostPortPair) throws SQLException {
    removeReplicaHost(group, hostPortPair);
  }
  
  public static void removeReplicaHost(String group, String hostPortPair, boolean closeGently) throws SQLException {
    Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
    for (ReplicationConnectionGroup cg : s)
      cg.removeReplicaHost(hostPortPair, closeGently); 
  }
  
  @Deprecated
  public static void removeSlaveHost(String group, String hostPortPair, boolean closeGently) throws SQLException {
    removeReplicaHost(group, hostPortPair, closeGently);
  }
  
  public static void promoteReplicaToSource(String group, String hostPortPair) throws SQLException {
    Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
    for (ReplicationConnectionGroup cg : s)
      cg.promoteReplicaToSource(hostPortPair); 
  }
  
  @Deprecated
  public static void promoteSlaveToMaster(String group, String hostPortPair) throws SQLException {
    promoteReplicaToSource(group, hostPortPair);
  }
  
  public static long getReplicaPromotionCount(String group) throws SQLException {
    Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
    long promoted = 0L;
    for (ReplicationConnectionGroup cg : s) {
      long tmp = cg.getNumberOfReplicaPromotions();
      if (tmp > promoted)
        promoted = tmp; 
    } 
    return promoted;
  }
  
  @Deprecated
  public static long getSlavePromotionCount(String group) throws SQLException {
    return getReplicaPromotionCount(group);
  }
  
  public static void removeSourceHost(String group, String hostPortPair) throws SQLException {
    removeSourceHost(group, hostPortPair, true);
  }
  
  @Deprecated
  public static void removeMasterHost(String group, String hostPortPair) throws SQLException {
    removeSourceHost(group, hostPortPair);
  }
  
  public static void removeSourceHost(String group, String hostPortPair, boolean closeGently) throws SQLException {
    Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
    for (ReplicationConnectionGroup cg : s)
      cg.removeSourceHost(hostPortPair, closeGently); 
  }
  
  @Deprecated
  public static void removeMasterHost(String group, String hostPortPair, boolean closeGently) throws SQLException {
    removeSourceHost(group, hostPortPair, closeGently);
  }
  
  public static String getRegisteredReplicationConnectionGroups() {
    Collection<ReplicationConnectionGroup> s = getGroupsMatching(null);
    StringBuilder sb = new StringBuilder();
    String sep = "";
    for (ReplicationConnectionGroup cg : s) {
      String group = cg.getGroupName();
      sb.append(sep);
      sb.append(group);
      sep = ",";
    } 
    return sb.toString();
  }
  
  public static int getNumberOfSourcePromotion(String groupFilter) {
    int total = 0;
    Collection<ReplicationConnectionGroup> s = getGroupsMatching(groupFilter);
    for (ReplicationConnectionGroup cg : s)
      total = (int)(total + cg.getNumberOfReplicaPromotions()); 
    return total;
  }
  
  @Deprecated
  public static int getNumberOfMasterPromotion(String groupFilter) {
    return getNumberOfSourcePromotion(groupFilter);
  }
  
  public static int getConnectionCountWithHostAsReplica(String groupFilter, String hostPortPair) {
    int total = 0;
    Collection<ReplicationConnectionGroup> s = getGroupsMatching(groupFilter);
    for (ReplicationConnectionGroup cg : s)
      total += cg.getConnectionCountWithHostAsReplica(hostPortPair); 
    return total;
  }
  
  @Deprecated
  public static int getConnectionCountWithHostAsSlave(String groupFilter, String hostPortPair) {
    return getConnectionCountWithHostAsReplica(groupFilter, hostPortPair);
  }
  
  public static int getConnectionCountWithHostAsSource(String groupFilter, String hostPortPair) {
    int total = 0;
    Collection<ReplicationConnectionGroup> s = getGroupsMatching(groupFilter);
    for (ReplicationConnectionGroup cg : s)
      total += cg.getConnectionCountWithHostAsSource(hostPortPair); 
    return total;
  }
  
  @Deprecated
  public static int getConnectionCountWithHostAsMaster(String groupFilter, String hostPortPair) {
    return getConnectionCountWithHostAsSource(groupFilter, hostPortPair);
  }
  
  public static Collection<String> getReplicaHosts(String groupFilter) {
    Collection<ReplicationConnectionGroup> s = getGroupsMatching(groupFilter);
    Collection<String> hosts = new ArrayList<>();
    for (ReplicationConnectionGroup cg : s)
      hosts.addAll(cg.getReplicaHosts()); 
    return hosts;
  }
  
  @Deprecated
  public static Collection<String> getSlaveHosts(String groupFilter) {
    return getReplicaHosts(groupFilter);
  }
  
  public static Collection<String> getSourceHosts(String groupFilter) {
    Collection<ReplicationConnectionGroup> s = getGroupsMatching(groupFilter);
    Collection<String> hosts = new ArrayList<>();
    for (ReplicationConnectionGroup cg : s)
      hosts.addAll(cg.getSourceHosts()); 
    return hosts;
  }
  
  @Deprecated
  public static Collection<String> getMasterHosts(String groupFilter) {
    return getSourceHosts(groupFilter);
  }
  
  public static long getTotalConnectionCount(String group) {
    long connections = 0L;
    Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
    for (ReplicationConnectionGroup cg : s)
      connections += cg.getTotalConnectionCount(); 
    return connections;
  }
  
  public static long getActiveConnectionCount(String group) {
    long connections = 0L;
    Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
    for (ReplicationConnectionGroup cg : s)
      connections += cg.getActiveConnectionCount(); 
    return connections;
  }
}
