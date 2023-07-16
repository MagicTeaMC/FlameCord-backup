package com.mysql.cj.jdbc.ha;

import com.mysql.cj.jdbc.ConnectionImpl;
import com.mysql.cj.jdbc.JdbcConnection;
import java.lang.reflect.InvocationHandler;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class BestResponseTimeBalanceStrategy implements BalanceStrategy {
  public ConnectionImpl pickConnection(InvocationHandler proxy, List<String> configuredHosts, Map<String, JdbcConnection> liveConnections, long[] responseTimes, int numRetries) throws SQLException {
    Map<String, Long> blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
    SQLException ex = null;
    for (int attempts = 0; attempts < numRetries; ) {
      long minResponseTime = Long.MAX_VALUE;
      int bestHostIndex = 0;
      if (blockList.size() == configuredHosts.size())
        blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist(); 
      for (int i = 0; i < responseTimes.length; i++) {
        long candidateResponseTime = responseTimes[i];
        if (candidateResponseTime < minResponseTime && !blockList.containsKey(configuredHosts.get(i))) {
          if (candidateResponseTime == 0L) {
            bestHostIndex = i;
            break;
          } 
          bestHostIndex = i;
          minResponseTime = candidateResponseTime;
        } 
      } 
      String bestHost = configuredHosts.get(bestHostIndex);
      ConnectionImpl conn = (ConnectionImpl)liveConnections.get(bestHost);
      if (conn == null)
        try {
          conn = ((LoadBalancedConnectionProxy)proxy).createConnectionForHost(bestHost);
        } catch (SQLException sqlEx) {
          ex = sqlEx;
          if (((LoadBalancedConnectionProxy)proxy).shouldExceptionTriggerConnectionSwitch(sqlEx)) {
            ((LoadBalancedConnectionProxy)proxy).addToGlobalBlocklist(bestHost);
            blockList.put(bestHost, null);
            if (blockList.size() == configuredHosts.size()) {
              attempts++;
              try {
                Thread.sleep(250L);
              } catch (InterruptedException interruptedException) {}
              blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
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
}
