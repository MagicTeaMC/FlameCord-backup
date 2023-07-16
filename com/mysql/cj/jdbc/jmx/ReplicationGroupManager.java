package com.mysql.cj.jdbc.jmx;

import com.mysql.cj.Messages;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.ha.ReplicationConnectionGroup;
import com.mysql.cj.jdbc.ha.ReplicationConnectionGroupManager;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class ReplicationGroupManager implements ReplicationGroupManagerMBean {
  private boolean isJmxRegistered = false;
  
  public synchronized void registerJmx() throws SQLException {
    if (this.isJmxRegistered)
      return; 
    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    try {
      ObjectName name = new ObjectName("com.mysql.cj.jdbc.jmx:type=ReplicationGroupManager");
      mbs.registerMBean(this, name);
      this.isJmxRegistered = true;
    } catch (Exception e) {
      throw SQLError.createSQLException(Messages.getString("ReplicationGroupManager.0"), null, e, null);
    } 
  }
  
  public void addReplicaHost(String groupFilter, String host) throws SQLException {
    ReplicationConnectionGroupManager.addReplicaHost(groupFilter, host);
  }
  
  public void removeReplicaHost(String groupFilter, String host) throws SQLException {
    ReplicationConnectionGroupManager.removeReplicaHost(groupFilter, host);
  }
  
  public void promoteReplicaToSource(String groupFilter, String host) throws SQLException {
    ReplicationConnectionGroupManager.promoteReplicaToSource(groupFilter, host);
  }
  
  public void removeSourceHost(String groupFilter, String host) throws SQLException {
    ReplicationConnectionGroupManager.removeSourceHost(groupFilter, host);
  }
  
  public String getSourceHostsList(String group) {
    StringBuilder sb = new StringBuilder("");
    boolean found = false;
    for (String host : ReplicationConnectionGroupManager.getSourceHosts(group)) {
      if (found)
        sb.append(","); 
      found = true;
      sb.append(host);
    } 
    return sb.toString();
  }
  
  public String getReplicaHostsList(String group) {
    StringBuilder sb = new StringBuilder("");
    boolean found = false;
    for (String host : ReplicationConnectionGroupManager.getReplicaHosts(group)) {
      if (found)
        sb.append(","); 
      found = true;
      sb.append(host);
    } 
    return sb.toString();
  }
  
  public String getRegisteredConnectionGroups() {
    StringBuilder sb = new StringBuilder("");
    boolean found = false;
    for (ReplicationConnectionGroup group : ReplicationConnectionGroupManager.getGroupsMatching(null)) {
      if (found)
        sb.append(","); 
      found = true;
      sb.append(group.getGroupName());
    } 
    return sb.toString();
  }
  
  public int getActiveSourceHostCount(String group) {
    return ReplicationConnectionGroupManager.getSourceHosts(group).size();
  }
  
  public int getActiveReplicaHostCount(String group) {
    return ReplicationConnectionGroupManager.getReplicaHosts(group).size();
  }
  
  public int getReplicaPromotionCount(String group) {
    return ReplicationConnectionGroupManager.getNumberOfSourcePromotion(group);
  }
  
  public long getTotalLogicalConnectionCount(String group) {
    return ReplicationConnectionGroupManager.getTotalConnectionCount(group);
  }
  
  public long getActiveLogicalConnectionCount(String group) {
    return ReplicationConnectionGroupManager.getActiveConnectionCount(group);
  }
}
