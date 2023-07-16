package com.mysql.cj.jdbc.ha;

import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.util.StringUtils;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

public class StandardLoadBalanceExceptionChecker implements LoadBalanceExceptionChecker {
  private List<String> sqlStateList;
  
  private List<Class<?>> sqlExClassList;
  
  public boolean shouldExceptionTriggerFailover(Throwable ex) {
    String sqlState = (ex instanceof SQLException) ? ((SQLException)ex).getSQLState() : null;
    if (sqlState != null) {
      if (sqlState.startsWith("08"))
        return true; 
      if (this.sqlStateList != null)
        for (Iterator<String> i = this.sqlStateList.iterator(); i.hasNext();) {
          if (sqlState.startsWith(((String)i.next()).toString()))
            return true; 
        }  
    } 
    if (ex instanceof com.mysql.cj.jdbc.exceptions.CommunicationsException || ex instanceof com.mysql.cj.exceptions.CJCommunicationsException)
      return true; 
    if (this.sqlExClassList != null)
      for (Iterator<Class<?>> i = this.sqlExClassList.iterator(); i.hasNext();) {
        if (((Class)i.next()).isInstance(ex))
          return true; 
      }  
    return false;
  }
  
  public void destroy() {}
  
  public void init(Properties props) {
    configureSQLStateList(props.getProperty(PropertyKey.loadBalanceSQLStateFailover.getKeyName(), null));
    configureSQLExceptionSubclassList(props.getProperty(PropertyKey.loadBalanceSQLExceptionSubclassFailover.getKeyName(), null));
  }
  
  private void configureSQLStateList(String sqlStates) {
    if (sqlStates == null || "".equals(sqlStates))
      return; 
    this.sqlStateList = (List<String>)StringUtils.split(sqlStates, ",", true).stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
  }
  
  private void configureSQLExceptionSubclassList(String sqlExClasses) {
    if (sqlExClasses == null || "".equals(sqlExClasses))
      return; 
    this
      
      .sqlExClassList = (List<Class<?>>)StringUtils.split(sqlExClasses, ",", true).stream().filter(s -> !s.isEmpty()).map(s -> {
          try {
            return Class.forName(s, false, getClass().getClassLoader());
          } catch (ClassNotFoundException classNotFoundException) {
            return null;
          } 
        }).filter(Objects::nonNull).collect(Collectors.toList());
  }
}
