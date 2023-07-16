package com.mysql.cj.exceptions;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.protocol.PacketReceivedTimeHolder;
import com.mysql.cj.protocol.PacketSentTimeHolder;
import com.mysql.cj.protocol.ServerSession;
import java.net.NetworkInterface;
import java.net.SocketException;

public class ExceptionFactory {
  private static final long DEFAULT_WAIT_TIMEOUT_SECONDS = 28800L;
  
  private static final int DUE_TO_TIMEOUT_FALSE = 0;
  
  private static final int DUE_TO_TIMEOUT_MAYBE = 2;
  
  private static final int DUE_TO_TIMEOUT_TRUE = 1;
  
  public static CJException createException(String message) {
    return createException(CJException.class, message);
  }
  
  public static <T extends CJException> T createException(Class<T> clazz, String message) {
    CJException cJException;
    try {
      cJException = clazz.getConstructor(new Class[] { String.class }).newInstance(new Object[] { message });
    } catch (Throwable e) {
      cJException = new CJException(message);
    } 
    return (T)cJException;
  }
  
  public static CJException createException(String message, ExceptionInterceptor interceptor) {
    return createException(CJException.class, message, interceptor);
  }
  
  public static <T extends CJException> T createException(Class<T> clazz, String message, ExceptionInterceptor interceptor) {
    T sqlEx = createException(clazz, message);
    return sqlEx;
  }
  
  public static CJException createException(String message, Throwable cause) {
    return createException(CJException.class, message, cause);
  }
  
  public static <T extends CJException> T createException(Class<T> clazz, String message, Throwable cause) {
    T sqlEx = createException(clazz, message);
    if (cause != null) {
      try {
        sqlEx.initCause(cause);
      } catch (Throwable throwable) {}
      if (cause instanceof CJException) {
        sqlEx.setSQLState(((CJException)cause).getSQLState());
        sqlEx.setVendorCode(((CJException)cause).getVendorCode());
        sqlEx.setTransient(((CJException)cause).isTransient());
      } 
    } 
    return sqlEx;
  }
  
  public static CJException createException(String message, Throwable cause, ExceptionInterceptor interceptor) {
    return createException(CJException.class, message, cause, interceptor);
  }
  
  public static CJException createException(String message, String sqlState, int vendorErrorCode, boolean isTransient, Throwable cause, ExceptionInterceptor interceptor) {
    CJException ex = createException(CJException.class, message, cause, interceptor);
    ex.setSQLState(sqlState);
    ex.setVendorCode(vendorErrorCode);
    ex.setTransient(isTransient);
    return ex;
  }
  
  public static <T extends CJException> T createException(Class<T> clazz, String message, Throwable cause, ExceptionInterceptor interceptor) {
    T sqlEx = createException(clazz, message, cause);
    return sqlEx;
  }
  
  public static CJCommunicationsException createCommunicationsException(PropertySet propertySet, ServerSession serverSession, PacketSentTimeHolder packetSentTimeHolder, PacketReceivedTimeHolder packetReceivedTimeHolder, Throwable cause, ExceptionInterceptor interceptor) {
    CJCommunicationsException sqlEx = createException(CJCommunicationsException.class, null, cause, interceptor);
    sqlEx.init(propertySet, serverSession, packetSentTimeHolder, packetReceivedTimeHolder);
    return sqlEx;
  }
  
  public static String createLinkFailureMessageBasedOnHeuristics(PropertySet propertySet, ServerSession serverSession, PacketSentTimeHolder packetSentTimeHolder, PacketReceivedTimeHolder packetReceivedTimeHolder, Throwable underlyingException) {
    long serverTimeoutSeconds = 0L;
    boolean isInteractiveClient = false;
    long lastPacketReceivedTimeMs = (packetReceivedTimeHolder == null) ? 0L : packetReceivedTimeHolder.getLastPacketReceivedTime();
    long lastPacketSentTimeMs = packetSentTimeHolder.getLastPacketSentTime();
    if (lastPacketSentTimeMs > lastPacketReceivedTimeMs)
      lastPacketSentTimeMs = packetSentTimeHolder.getPreviousPacketSentTime(); 
    if (propertySet != null) {
      isInteractiveClient = ((Boolean)propertySet.getBooleanProperty(PropertyKey.interactiveClient).getValue()).booleanValue();
      String serverTimeoutSecondsStr = null;
      if (serverSession != null)
        serverTimeoutSecondsStr = isInteractiveClient ? serverSession.getServerVariable("interactive_timeout") : serverSession.getServerVariable("wait_timeout"); 
      if (serverTimeoutSecondsStr != null)
        try {
          serverTimeoutSeconds = Long.parseLong(serverTimeoutSecondsStr);
        } catch (NumberFormatException nfe) {
          serverTimeoutSeconds = 0L;
        }  
    } 
    StringBuilder exceptionMessageBuf = new StringBuilder();
    long nowMs = System.currentTimeMillis();
    if (lastPacketSentTimeMs == 0L)
      lastPacketSentTimeMs = nowMs; 
    long timeSinceLastPacketSentMs = nowMs - lastPacketSentTimeMs;
    long timeSinceLastPacketSeconds = timeSinceLastPacketSentMs / 1000L;
    long timeSinceLastPacketReceivedMs = nowMs - lastPacketReceivedTimeMs;
    int dueToTimeout = 0;
    StringBuilder timeoutMessageBuf = null;
    if (serverTimeoutSeconds != 0L) {
      if (timeSinceLastPacketSeconds > serverTimeoutSeconds) {
        dueToTimeout = 1;
        timeoutMessageBuf = new StringBuilder();
        timeoutMessageBuf.append(Messages.getString("CommunicationsException.2"));
        timeoutMessageBuf.append(Messages.getString(isInteractiveClient ? "CommunicationsException.4" : "CommunicationsException.3"));
      } 
    } else if (timeSinceLastPacketSeconds > 28800L) {
      dueToTimeout = 2;
      timeoutMessageBuf = new StringBuilder();
      timeoutMessageBuf.append(Messages.getString("CommunicationsException.5"));
      timeoutMessageBuf.append(Messages.getString("CommunicationsException.6"));
      timeoutMessageBuf.append(Messages.getString("CommunicationsException.7"));
      timeoutMessageBuf.append(Messages.getString("CommunicationsException.8"));
    } 
    if (dueToTimeout == 1 || dueToTimeout == 2) {
      exceptionMessageBuf.append((lastPacketReceivedTimeMs != 0L) ? 
          Messages.getString("CommunicationsException.ServerPacketTimingInfo", new Object[] { Long.valueOf(timeSinceLastPacketReceivedMs), Long.valueOf(timeSinceLastPacketSentMs) }) : Messages.getString("CommunicationsException.ServerPacketTimingInfoNoRecv", new Object[] { Long.valueOf(timeSinceLastPacketSentMs) }));
      if (timeoutMessageBuf != null)
        exceptionMessageBuf.append(timeoutMessageBuf); 
      exceptionMessageBuf.append(Messages.getString("CommunicationsException.11"));
      exceptionMessageBuf.append(Messages.getString("CommunicationsException.12"));
      exceptionMessageBuf.append(Messages.getString("CommunicationsException.13"));
    } else if (underlyingException instanceof java.net.BindException) {
      boolean interfaceNotAvaliable;
      String localSocketAddress = (String)propertySet.getStringProperty(PropertyKey.localSocketAddress).getValue();
      try {
        interfaceNotAvaliable = (localSocketAddress != null && NetworkInterface.getByName(localSocketAddress) == null);
      } catch (SocketException e1) {
        interfaceNotAvaliable = false;
      } 
      exceptionMessageBuf.append(interfaceNotAvaliable ? Messages.getString("CommunicationsException.LocalSocketAddressNotAvailable") : 
          Messages.getString("CommunicationsException.TooManyClientConnections"));
    } 
    if (exceptionMessageBuf.length() == 0) {
      exceptionMessageBuf.append(Messages.getString("CommunicationsException.20"));
      if (((Boolean)propertySet.getBooleanProperty(PropertyKey.maintainTimeStats).getValue()).booleanValue() && !((Boolean)propertySet.getBooleanProperty(PropertyKey.paranoid).getValue()).booleanValue()) {
        exceptionMessageBuf.append("\n\n");
        exceptionMessageBuf.append((lastPacketReceivedTimeMs != 0L) ? 
            Messages.getString("CommunicationsException.ServerPacketTimingInfo", new Object[] { Long.valueOf(timeSinceLastPacketReceivedMs), Long.valueOf(timeSinceLastPacketSentMs) }) : Messages.getString("CommunicationsException.ServerPacketTimingInfoNoRecv", new Object[] { Long.valueOf(timeSinceLastPacketSentMs) }));
      } 
    } 
    return exceptionMessageBuf.toString();
  }
}
