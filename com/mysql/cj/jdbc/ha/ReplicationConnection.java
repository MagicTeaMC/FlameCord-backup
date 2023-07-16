package com.mysql.cj.jdbc.ha;

import com.mysql.cj.jdbc.JdbcConnection;
import java.sql.SQLException;

public interface ReplicationConnection extends JdbcConnection {
  long getConnectionGroupId();
  
  JdbcConnection getCurrentConnection();
  
  JdbcConnection getSourceConnection();
  
  @Deprecated
  default JdbcConnection getMasterConnection() {
    return getSourceConnection();
  }
  
  void promoteReplicaToSource(String paramString) throws SQLException;
  
  @Deprecated
  default void promoteSlaveToMaster(String host) throws SQLException {
    promoteReplicaToSource(host);
  }
  
  void removeSourceHost(String paramString) throws SQLException;
  
  @Deprecated
  default void removeMasterHost(String host) throws SQLException {
    removeSourceHost(host);
  }
  
  void removeSourceHost(String paramString, boolean paramBoolean) throws SQLException;
  
  @Deprecated
  default void removeMasterHost(String host, boolean waitUntilNotInUse) throws SQLException {
    removeSourceHost(host, waitUntilNotInUse);
  }
  
  boolean isHostSource(String paramString);
  
  @Deprecated
  default boolean isHostMaster(String host) {
    return isHostSource(host);
  }
  
  JdbcConnection getReplicaConnection();
  
  @Deprecated
  default JdbcConnection getSlavesConnection() {
    return getReplicaConnection();
  }
  
  void addReplicaHost(String paramString) throws SQLException;
  
  @Deprecated
  default void addSlaveHost(String host) throws SQLException {
    addReplicaHost(host);
  }
  
  void removeReplica(String paramString) throws SQLException;
  
  @Deprecated
  default void removeSlave(String host) throws SQLException {
    removeReplica(host);
  }
  
  void removeReplica(String paramString, boolean paramBoolean) throws SQLException;
  
  @Deprecated
  default void removeSlave(String host, boolean closeGently) throws SQLException {
    removeReplica(host, closeGently);
  }
  
  boolean isHostReplica(String paramString);
  
  @Deprecated
  default boolean isHostSlave(String host) {
    return isHostReplica(host);
  }
}
