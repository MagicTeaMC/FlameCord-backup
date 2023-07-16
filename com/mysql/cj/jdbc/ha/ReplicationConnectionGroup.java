package com.mysql.cj.jdbc.ha;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ReplicationConnectionGroup {
  private String groupName;
  
  private long connections = 0L;
  
  private long replicasAdded = 0L;
  
  private long replicasRemoved = 0L;
  
  private long replicasPromoted = 0L;
  
  private long activeConnections = 0L;
  
  private HashMap<Long, ReplicationConnection> replicationConnections = new HashMap<>();
  
  private Set<String> replicaHostList = new CopyOnWriteArraySet<>();
  
  private boolean isInitialized = false;
  
  private Set<String> sourceHostList = new CopyOnWriteArraySet<>();
  
  ReplicationConnectionGroup(String groupName) {
    this.groupName = groupName;
  }
  
  public long getConnectionCount() {
    return this.connections;
  }
  
  public long registerReplicationConnection(ReplicationConnection conn, List<String> localSourceList, List<String> localReplicaList) {
    long currentConnectionId;
    synchronized (this) {
      if (!this.isInitialized) {
        if (localSourceList != null)
          this.sourceHostList.addAll(localSourceList); 
        if (localReplicaList != null)
          this.replicaHostList.addAll(localReplicaList); 
        this.isInitialized = true;
      } 
      currentConnectionId = ++this.connections;
      this.replicationConnections.put(Long.valueOf(currentConnectionId), conn);
    } 
    this.activeConnections++;
    return currentConnectionId;
  }
  
  public String getGroupName() {
    return this.groupName;
  }
  
  public Collection<String> getSourceHosts() {
    return this.sourceHostList;
  }
  
  @Deprecated
  public Collection<String> getMasterHosts() {
    return getSourceHosts();
  }
  
  public Collection<String> getReplicaHosts() {
    return this.replicaHostList;
  }
  
  @Deprecated
  public Collection<String> getSlaveHosts() {
    return getReplicaHosts();
  }
  
  public void addReplicaHost(String hostPortPair) throws SQLException {
    if (this.replicaHostList.add(hostPortPair)) {
      this.replicasAdded++;
      for (ReplicationConnection c : this.replicationConnections.values())
        c.addReplicaHost(hostPortPair); 
    } 
  }
  
  @Deprecated
  public void addSlaveHost(String hostPortPair) throws SQLException {
    addReplicaHost(hostPortPair);
  }
  
  public void handleCloseConnection(ReplicationConnection conn) {
    this.replicationConnections.remove(Long.valueOf(conn.getConnectionGroupId()));
    this.activeConnections--;
  }
  
  public void removeReplicaHost(String hostPortPair, boolean closeGently) throws SQLException {
    if (this.replicaHostList.remove(hostPortPair)) {
      this.replicasRemoved++;
      for (ReplicationConnection c : this.replicationConnections.values())
        c.removeReplica(hostPortPair, closeGently); 
    } 
  }
  
  @Deprecated
  public void removeSlaveHost(String hostPortPair, boolean closeGently) throws SQLException {
    removeReplicaHost(hostPortPair, closeGently);
  }
  
  public void promoteReplicaToSource(String hostPortPair) throws SQLException {
    if ((this.replicaHostList.remove(hostPortPair) | this.sourceHostList.add(hostPortPair)) != 0) {
      this.replicasPromoted++;
      for (ReplicationConnection c : this.replicationConnections.values())
        c.promoteReplicaToSource(hostPortPair); 
    } 
  }
  
  @Deprecated
  public void promoteSlaveToMaster(String hostPortPair) throws SQLException {
    promoteReplicaToSource(hostPortPair);
  }
  
  public void removeSourceHost(String hostPortPair) throws SQLException {
    removeSourceHost(hostPortPair, true);
  }
  
  @Deprecated
  public void removeMasterHost(String hostPortPair) throws SQLException {
    removeSourceHost(hostPortPair);
  }
  
  public void removeSourceHost(String hostPortPair, boolean closeGently) throws SQLException {
    if (this.sourceHostList.remove(hostPortPair))
      for (ReplicationConnection c : this.replicationConnections.values())
        c.removeSourceHost(hostPortPair, closeGently);  
  }
  
  @Deprecated
  public void removeMasterHost(String hostPortPair, boolean closeGently) throws SQLException {
    removeSourceHost(hostPortPair, closeGently);
  }
  
  public int getConnectionCountWithHostAsReplica(String hostPortPair) {
    int matched = 0;
    for (ReplicationConnection c : this.replicationConnections.values()) {
      if (c.isHostReplica(hostPortPair))
        matched++; 
    } 
    return matched;
  }
  
  @Deprecated
  public int getConnectionCountWithHostAsSlave(String hostPortPair) {
    return getConnectionCountWithHostAsReplica(hostPortPair);
  }
  
  public int getConnectionCountWithHostAsSource(String hostPortPair) {
    int matched = 0;
    for (ReplicationConnection c : this.replicationConnections.values()) {
      if (c.isHostSource(hostPortPair))
        matched++; 
    } 
    return matched;
  }
  
  @Deprecated
  public int getConnectionCountWithHostAsMaster(String hostPortPair) {
    return getConnectionCountWithHostAsSource(hostPortPair);
  }
  
  public long getNumberOfReplicasAdded() {
    return this.replicasAdded;
  }
  
  @Deprecated
  public long getNumberOfSlavesAdded() {
    return getNumberOfReplicasAdded();
  }
  
  public long getNumberOfReplicasRemoved() {
    return this.replicasRemoved;
  }
  
  @Deprecated
  public long getNumberOfSlavesRemoved() {
    return getNumberOfReplicasRemoved();
  }
  
  public long getNumberOfReplicaPromotions() {
    return this.replicasPromoted;
  }
  
  @Deprecated
  public long getNumberOfSlavePromotions() {
    return getNumberOfReplicaPromotions();
  }
  
  public long getTotalConnectionCount() {
    return this.connections;
  }
  
  public long getActiveConnectionCount() {
    return this.activeConnections;
  }
  
  public String toString() {
    return "ReplicationConnectionGroup[groupName=" + this.groupName + ",sourceHostList=" + this.sourceHostList + ",replicaHostList=" + this.replicaHostList + "]";
  }
}
