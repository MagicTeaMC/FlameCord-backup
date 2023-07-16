package com.mysql.cj.jdbc.ha;

import com.mysql.cj.jdbc.JdbcConnection;
import java.lang.reflect.InvocationHandler;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface BalanceStrategy {
  JdbcConnection pickConnection(InvocationHandler paramInvocationHandler, List<String> paramList, Map<String, JdbcConnection> paramMap, long[] paramArrayOflong, int paramInt) throws SQLException;
}
