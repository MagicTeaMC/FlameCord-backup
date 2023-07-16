package com.mysql.cj.jdbc;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.log.Log;
import com.mysql.cj.util.StringUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class MysqlXAConnection extends MysqlPooledConnection implements XAConnection, XAResource {
  private static final int MAX_COMMAND_LENGTH = 300;
  
  private JdbcConnection underlyingConnection;
  
  private static final Map<Integer, Integer> MYSQL_ERROR_CODES_TO_XA_ERROR_CODES;
  
  private Log log;
  
  protected boolean logXaCommands;
  
  static {
    HashMap<Integer, Integer> temp = new HashMap<>();
    temp.put(Integer.valueOf(1397), Integer.valueOf(-4));
    temp.put(Integer.valueOf(1398), Integer.valueOf(-5));
    temp.put(Integer.valueOf(1399), Integer.valueOf(-7));
    temp.put(Integer.valueOf(1400), Integer.valueOf(-9));
    temp.put(Integer.valueOf(1401), Integer.valueOf(-3));
    temp.put(Integer.valueOf(1402), Integer.valueOf(100));
    temp.put(Integer.valueOf(1440), Integer.valueOf(-8));
    temp.put(Integer.valueOf(1613), Integer.valueOf(106));
    temp.put(Integer.valueOf(1614), Integer.valueOf(102));
    MYSQL_ERROR_CODES_TO_XA_ERROR_CODES = Collections.unmodifiableMap(temp);
  }
  
  protected static MysqlXAConnection getInstance(JdbcConnection mysqlConnection, boolean logXaCommands) throws SQLException {
    return new MysqlXAConnection(mysqlConnection, logXaCommands);
  }
  
  public MysqlXAConnection(JdbcConnection connection, boolean logXaCommands) {
    super(connection);
    this.underlyingConnection = connection;
    this.log = connection.getSession().getLog();
    this.logXaCommands = logXaCommands;
  }
  
  public XAResource getXAResource() throws SQLException {
    try {
      return this;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
  
  public int getTransactionTimeout() throws XAException {
    return 0;
  }
  
  public boolean setTransactionTimeout(int arg0) throws XAException {
    return false;
  }
  
  public boolean isSameRM(XAResource xares) throws XAException {
    if (xares instanceof MysqlXAConnection)
      return this.underlyingConnection.isSameResource(((MysqlXAConnection)xares).underlyingConnection); 
    return false;
  }
  
  public Xid[] recover(int flag) throws XAException {
    return recover(this.underlyingConnection, flag);
  }
  
  protected static Xid[] recover(Connection c, int flag) throws XAException {
    boolean startRscan = ((flag & 0x1000000) > 0);
    boolean endRscan = ((flag & 0x800000) > 0);
    if (!startRscan && !endRscan && flag != 0)
      throw new MysqlXAException(-5, Messages.getString("MysqlXAConnection.001"), null); 
    if (!startRscan)
      return new Xid[0]; 
    ResultSet rs = null;
    Statement stmt = null;
    List<MysqlXid> recoveredXidList = new ArrayList<>();
    try {
      stmt = c.createStatement();
      rs = stmt.executeQuery("XA RECOVER");
      while (rs.next()) {
        int formatId = rs.getInt(1);
        int gtridLength = rs.getInt(2);
        int bqualLength = rs.getInt(3);
        byte[] gtridAndBqual = rs.getBytes(4);
        byte[] gtrid = new byte[gtridLength];
        byte[] bqual = new byte[bqualLength];
        if (gtridAndBqual.length != gtridLength + bqualLength)
          throw new MysqlXAException(105, Messages.getString("MysqlXAConnection.002"), null); 
        System.arraycopy(gtridAndBqual, 0, gtrid, 0, gtridLength);
        System.arraycopy(gtridAndBqual, gtridLength, bqual, 0, bqualLength);
        recoveredXidList.add(new MysqlXid(gtrid, bqual, formatId));
      } 
    } catch (SQLException sqlEx) {
      throw mapXAExceptionFromSQLException(sqlEx);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException sqlEx) {
          throw mapXAExceptionFromSQLException(sqlEx);
        }  
      if (stmt != null)
        try {
          stmt.close();
        } catch (SQLException sqlEx) {
          throw mapXAExceptionFromSQLException(sqlEx);
        }  
    } 
    int numXids = recoveredXidList.size();
    Xid[] asXids = new Xid[numXids];
    Object[] asObjects = recoveredXidList.toArray();
    for (int i = 0; i < numXids; i++)
      asXids[i] = (Xid)asObjects[i]; 
    return asXids;
  }
  
  public int prepare(Xid xid) throws XAException {
    StringBuilder commandBuf = new StringBuilder(300);
    commandBuf.append("XA PREPARE ");
    appendXid(commandBuf, xid);
    dispatchCommand(commandBuf.toString());
    return 0;
  }
  
  public void forget(Xid xid) throws XAException {}
  
  public void rollback(Xid xid) throws XAException {
    StringBuilder commandBuf = new StringBuilder(300);
    commandBuf.append("XA ROLLBACK ");
    appendXid(commandBuf, xid);
    try {
      dispatchCommand(commandBuf.toString());
    } finally {
      this.underlyingConnection.setInGlobalTx(false);
    } 
  }
  
  public void end(Xid xid, int flags) throws XAException {
    StringBuilder commandBuf = new StringBuilder(300);
    commandBuf.append("XA END ");
    appendXid(commandBuf, xid);
    switch (flags) {
      case 67108864:
        break;
      case 33554432:
        commandBuf.append(" SUSPEND");
        break;
      case 536870912:
        break;
      default:
        throw new XAException(-5);
    } 
    dispatchCommand(commandBuf.toString());
  }
  
  public void start(Xid xid, int flags) throws XAException {
    StringBuilder commandBuf = new StringBuilder(300);
    commandBuf.append("XA START ");
    appendXid(commandBuf, xid);
    switch (flags) {
      case 2097152:
        commandBuf.append(" JOIN");
        break;
      case 134217728:
        commandBuf.append(" RESUME");
        break;
      case 0:
        break;
      default:
        throw new XAException(-5);
    } 
    dispatchCommand(commandBuf.toString());
    this.underlyingConnection.setInGlobalTx(true);
  }
  
  public void commit(Xid xid, boolean onePhase) throws XAException {
    StringBuilder commandBuf = new StringBuilder(300);
    commandBuf.append("XA COMMIT ");
    appendXid(commandBuf, xid);
    if (onePhase)
      commandBuf.append(" ONE PHASE"); 
    try {
      dispatchCommand(commandBuf.toString());
    } finally {
      this.underlyingConnection.setInGlobalTx(false);
    } 
  }
  
  private ResultSet dispatchCommand(String command) throws XAException {
    Statement stmt = null;
    try {
      if (this.logXaCommands)
        this.log.logDebug("Executing XA statement: " + command); 
      stmt = this.underlyingConnection.createStatement();
      stmt.execute(command);
      ResultSet rs = stmt.getResultSet();
      return rs;
    } catch (SQLException sqlEx) {
      throw mapXAExceptionFromSQLException(sqlEx);
    } finally {
      if (stmt != null)
        try {
          stmt.close();
        } catch (SQLException sQLException) {} 
    } 
  }
  
  protected static XAException mapXAExceptionFromSQLException(SQLException sqlEx) {
    Integer xaCode = MYSQL_ERROR_CODES_TO_XA_ERROR_CODES.get(Integer.valueOf(sqlEx.getErrorCode()));
    if (xaCode != null)
      return (XAException)(new MysqlXAException(xaCode.intValue(), sqlEx.getMessage(), null)).initCause(sqlEx); 
    return (XAException)(new MysqlXAException(-7, Messages.getString("MysqlXAConnection.003"), null)).initCause(sqlEx);
  }
  
  private static void appendXid(StringBuilder builder, Xid xid) {
    byte[] gtrid = xid.getGlobalTransactionId();
    byte[] btrid = xid.getBranchQualifier();
    if (gtrid != null)
      StringUtils.appendAsHex(builder, gtrid); 
    builder.append(',');
    if (btrid != null)
      StringUtils.appendAsHex(builder, btrid); 
    builder.append(',');
    StringUtils.appendAsHex(builder, xid.getFormatId());
  }
  
  public synchronized Connection getConnection() throws SQLException {
    try {
      Connection connToWrap = getConnection(false, true);
      return connToWrap;
    } catch (CJException cJException) {
      throw SQLExceptionsMapping.translateException(cJException);
    } 
  }
}
