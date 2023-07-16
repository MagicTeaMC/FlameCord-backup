package com.mysql.cj.jdbc.jmx;

import java.sql.SQLException;

public interface ReplicationGroupManagerMBean {
  void addReplicaHost(String paramString1, String paramString2) throws SQLException;
  
  @Deprecated
  default void addSlaveHost(String groupFilter, String host) throws SQLException {
    addReplicaHost(groupFilter, host);
  }
  
  void removeReplicaHost(String paramString1, String paramString2) throws SQLException;
  
  @Deprecated
  default void removeSlaveHost(String groupFilter, String host) throws SQLException {
    removeReplicaHost(groupFilter, host);
  }
  
  void promoteReplicaToSource(String paramString1, String paramString2) throws SQLException;
  
  @Deprecated
  default void promoteSlaveToMaster(String groupFilter, String host) throws SQLException {
    promoteReplicaToSource(groupFilter, host);
  }
  
  void removeSourceHost(String paramString1, String paramString2) throws SQLException;
  
  @Deprecated
  default void removeMasterHost(String groupFilter, String host) throws SQLException {
    removeSourceHost(groupFilter, host);
  }
  
  String getSourceHostsList(String paramString);
  
  @Deprecated
  default String getMasterHostsList(String group) {
    return getSourceHostsList(group);
  }
  
  String getReplicaHostsList(String paramString);
  
  @Deprecated
  default String getSlaveHostsList(String group) {
    return getReplicaHostsList(group);
  }
  
  String getRegisteredConnectionGroups();
  
  int getActiveSourceHostCount(String paramString);
  
  @Deprecated
  default int getActiveMasterHostCount(String group) {
    return getActiveSourceHostCount(group);
  }
  
  int getActiveReplicaHostCount(String paramString);
  
  @Deprecated
  default int getActiveSlaveHostCount(String group) {
    return getActiveReplicaHostCount(group);
  }
  
  int getReplicaPromotionCount(String paramString);
  
  @Deprecated
  default int getSlavePromotionCount(String group) {
    return getReplicaPromotionCount(group);
  }
  
  long getTotalLogicalConnectionCount(String paramString);
  
  long getActiveLogicalConnectionCount(String paramString);
}
