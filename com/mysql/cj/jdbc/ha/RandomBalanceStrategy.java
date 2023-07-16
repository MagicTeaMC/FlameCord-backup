package com.mysql.cj.jdbc.ha;

import com.mysql.cj.Messages;
import com.mysql.cj.jdbc.ConnectionImpl;
import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.exceptions.SQLError;
import java.lang.reflect.InvocationHandler;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RandomBalanceStrategy implements BalanceStrategy {
  public ConnectionImpl pickConnection(InvocationHandler proxy, List<String> configuredHosts, Map<String, JdbcConnection> liveConnections, long[] responseTimes, int numRetries) throws SQLException {
    int numHosts = configuredHosts.size();
    SQLException ex = null;
    List<String> allowList = new ArrayList<>(numHosts);
    allowList.addAll(configuredHosts);
    Map<String, Long> blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
    allowList.removeAll(blockList.keySet());
    Map<String, Integer> allowListMap = getArrayIndexMap(allowList);
    for (int attempts = 0; attempts < numRetries; ) {
      int random = (int)Math.floor(Math.random() * allowList.size());
      if (allowList.size() == 0)
        throw SQLError.createSQLException(Messages.getString("RandomBalanceStrategy.0"), null); 
      String hostPortSpec = allowList.get(random);
      ConnectionImpl conn = (ConnectionImpl)liveConnections.get(hostPortSpec);
      if (conn == null)
        try {
          conn = ((LoadBalancedConnectionProxy)proxy).createConnectionForHost(hostPortSpec);
        } catch (SQLException sqlEx) {
          ex = sqlEx;
          if (((LoadBalancedConnectionProxy)proxy).shouldExceptionTriggerConnectionSwitch(sqlEx)) {
            Integer allowListIndex = allowListMap.get(hostPortSpec);
            if (allowListIndex != null) {
              allowList.remove(allowListIndex.intValue());
              allowListMap = getArrayIndexMap(allowList);
            } 
            ((LoadBalancedConnectionProxy)proxy).addToGlobalBlocklist(hostPortSpec);
            if (allowList.size() == 0) {
              attempts++;
              try {
                Thread.sleep(250L);
              } catch (InterruptedException interruptedException) {}
              allowListMap = new HashMap<>(numHosts);
              allowList.addAll(configuredHosts);
              blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
              allowList.removeAll(blockList.keySet());
              allowListMap = getArrayIndexMap(allowList);
            } 
            continue;
          } 
          throw sqlEx;
        }  
      return conn;
    } 
    if (ex != null)
      throw ex; 
    return null;
  }
  
  private Map<String, Integer> getArrayIndexMap(List<String> l) {
    Map<String, Integer> m = new HashMap<>(l.size());
    for (int i = 0; i < l.size(); i++)
      m.put(l.get(i), Integer.valueOf(i)); 
    return m;
  }
}
