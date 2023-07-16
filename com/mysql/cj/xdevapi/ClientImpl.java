package com.mysql.cj.xdevapi;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.BooleanPropertyDefinition;
import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.DefaultPropertySet;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.IntegerPropertyDefinition;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.Protocol;
import com.mysql.cj.protocol.x.XProtocol;
import com.mysql.cj.util.StringUtils;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientImpl implements Client, Protocol.ProtocolEventListener {
  boolean isClosed = false;
  
  private ConnectionUrl connUrl = null;
  
  private boolean poolingEnabled = true;
  
  private int maxSize = 25;
  
  int maxIdleTime = 0;
  
  private int queueTimeout = 0;
  
  private int demotedTimeout = 120000;
  
  Map<HostInfo, Long> demotedHosts = null;
  
  BlockingQueue<PooledXProtocol> idleProtocols = null;
  
  Set<WeakReference<PooledXProtocol>> activeProtocols = null;
  
  Set<WeakReference<Session>> nonPooledSessions = null;
  
  SessionFactory sessionFactory = new SessionFactory();
  
  public ClientImpl(String url, String clientPropsJson) {
    Properties clientProps = StringUtils.isNullOrEmpty(clientPropsJson) ? new Properties() : clientPropsFromJson(clientPropsJson);
    init(url, clientProps);
  }
  
  public ClientImpl(String url, Properties clientProps) {
    init(url, (clientProps != null) ? clientProps : new Properties());
  }
  
  private Properties clientPropsFromJson(String clientPropsJson) {
    Properties props = new Properties();
    DbDoc clientPropsDoc = JsonParser.parseDoc(clientPropsJson);
    JsonValue pooling = clientPropsDoc.remove("pooling");
    if (pooling != null) {
      if (!DbDoc.class.isAssignableFrom(pooling.getClass()))
        throw new XDevAPIError(String.format("Client option 'pooling' does not support value '%s'.", new Object[] { pooling.toFormattedString() })); 
      DbDoc poolingDoc = (DbDoc)pooling;
      JsonValue jsonVal = poolingDoc.remove("enabled");
      if (jsonVal != null)
        if (JsonLiteral.class.isAssignableFrom(jsonVal.getClass())) {
          JsonLiteral pe = (JsonLiteral)jsonVal;
          if (pe != JsonLiteral.FALSE && pe != JsonLiteral.TRUE)
            throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { Client.ClientProperty.POOLING_ENABLED.getKeyName(), jsonVal
                    .toFormattedString() })); 
          props.setProperty(Client.ClientProperty.POOLING_ENABLED.getKeyName(), pe.value);
        } else {
          if (JsonString.class.isAssignableFrom(jsonVal.getClass()))
            throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { Client.ClientProperty.POOLING_ENABLED.getKeyName(), ((JsonString)jsonVal)
                    .getString() })); 
          throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { Client.ClientProperty.POOLING_ENABLED.getKeyName(), jsonVal
                  .toFormattedString() }));
        }  
      jsonVal = poolingDoc.remove("maxSize");
      if (jsonVal != null)
        if (JsonNumber.class.isAssignableFrom(jsonVal.getClass())) {
          props.setProperty(Client.ClientProperty.POOLING_MAX_SIZE.getKeyName(), ((JsonNumber)jsonVal).toString());
        } else {
          if (JsonString.class.isAssignableFrom(jsonVal.getClass()))
            throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { Client.ClientProperty.POOLING_MAX_SIZE.getKeyName(), ((JsonString)jsonVal)
                    .getString() })); 
          throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { Client.ClientProperty.POOLING_MAX_SIZE.getKeyName(), jsonVal
                  .toFormattedString() }));
        }  
      jsonVal = poolingDoc.remove("maxIdleTime");
      if (jsonVal != null)
        if (JsonNumber.class.isAssignableFrom(jsonVal.getClass())) {
          props.setProperty(Client.ClientProperty.POOLING_MAX_IDLE_TIME.getKeyName(), ((JsonNumber)jsonVal).toString());
        } else {
          if (JsonString.class.isAssignableFrom(jsonVal.getClass()))
            throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { Client.ClientProperty.POOLING_MAX_IDLE_TIME.getKeyName(), ((JsonString)jsonVal)
                    .getString() })); 
          throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { Client.ClientProperty.POOLING_MAX_IDLE_TIME.getKeyName(), jsonVal
                  .toFormattedString() }));
        }  
      jsonVal = poolingDoc.remove("queueTimeout");
      if (jsonVal != null)
        if (JsonNumber.class.isAssignableFrom(jsonVal.getClass())) {
          props.setProperty(Client.ClientProperty.POOLING_QUEUE_TIMEOUT.getKeyName(), ((JsonNumber)jsonVal).toString());
        } else {
          if (JsonString.class.isAssignableFrom(jsonVal.getClass()))
            throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { Client.ClientProperty.POOLING_QUEUE_TIMEOUT.getKeyName(), ((JsonString)jsonVal)
                    .getString() })); 
          throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { Client.ClientProperty.POOLING_QUEUE_TIMEOUT.getKeyName(), jsonVal
                  .toFormattedString() }));
        }  
      if (poolingDoc.size() > 0) {
        String key = poolingDoc.keySet().stream().findFirst().get();
        throw new XDevAPIError(String.format("Client option 'pooling.%s' is not recognized as valid.", new Object[] { key }));
      } 
    } 
    if (!clientPropsDoc.isEmpty()) {
      String key = clientPropsDoc.keySet().stream().findFirst().get();
      throw new XDevAPIError(String.format("Client option '%s' is not recognized as valid.", new Object[] { key }));
    } 
    return props;
  }
  
  private void validateAndInitializeClientProps(Properties clientProps) {
    String propKey = "";
    String propValue = "";
    propKey = Client.ClientProperty.POOLING_ENABLED.getKeyName();
    if (clientProps.containsKey(propKey)) {
      propValue = clientProps.getProperty(propKey);
      try {
        this.poolingEnabled = BooleanPropertyDefinition.booleanFrom(propKey, propValue, null).booleanValue();
      } catch (CJException e) {
        throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { propKey, propValue }), e);
      } 
    } 
    propKey = Client.ClientProperty.POOLING_MAX_SIZE.getKeyName();
    if (clientProps.containsKey(propKey)) {
      propValue = clientProps.getProperty(propKey);
      try {
        this.maxSize = IntegerPropertyDefinition.integerFrom(propKey, propValue, 1, null).intValue();
      } catch (WrongArgumentException e) {
        throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { propKey, propValue }), e);
      } 
      if (this.maxSize <= 0)
        throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { propKey, propValue })); 
    } 
    propKey = Client.ClientProperty.POOLING_MAX_IDLE_TIME.getKeyName();
    if (clientProps.containsKey(propKey)) {
      propValue = clientProps.getProperty(propKey);
      try {
        this.maxIdleTime = IntegerPropertyDefinition.integerFrom(propKey, propValue, 1, null).intValue();
      } catch (WrongArgumentException e) {
        throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { propKey, propValue }), e);
      } 
      if (this.maxIdleTime < 0)
        throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { propKey, propValue })); 
    } 
    propKey = Client.ClientProperty.POOLING_QUEUE_TIMEOUT.getKeyName();
    if (clientProps.containsKey(propKey)) {
      propValue = clientProps.getProperty(propKey);
      try {
        this.queueTimeout = IntegerPropertyDefinition.integerFrom(propKey, propValue, 1, null).intValue();
      } catch (WrongArgumentException e) {
        throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { propKey, propValue }), e);
      } 
      if (this.queueTimeout < 0)
        throw new XDevAPIError(String.format("Client option '%s' does not support value '%s'.", new Object[] { propKey, propValue })); 
    } 
    List<String> clientPropsAsString = (List<String>)Stream.<Client.ClientProperty>of(Client.ClientProperty.values()).map(Client.ClientProperty::getKeyName).collect(Collectors.toList());
    propKey = clientProps.keySet().stream().filter(k -> !clientPropsAsString.contains(k)).findFirst().orElse(null);
    if (propKey != null)
      throw new XDevAPIError(String.format("Client option '%s' is not recognized as valid.", new Object[] { propKey })); 
  }
  
  private void init(String url, Properties clientProps) {
    this.connUrl = this.sessionFactory.parseUrl(url);
    validateAndInitializeClientProps(clientProps);
    if (this.poolingEnabled) {
      this.demotedHosts = new HashMap<>();
      this.idleProtocols = new LinkedBlockingQueue<>(this.maxSize);
      this.activeProtocols = new HashSet<>(this.maxSize);
    } else {
      this.nonPooledSessions = new HashSet<>();
    } 
  }
  
  public Session getSession() {
    if (this.isClosed)
      throw new XDevAPIError("Client is closed."); 
    if (!this.poolingEnabled)
      synchronized (this) {
        List<WeakReference<Session>> obsoletedSessions = new ArrayList<>();
        for (WeakReference<Session> ws : this.nonPooledSessions) {
          if (ws != null) {
            Session s = ws.get();
            if (s == null || !s.isOpen())
              obsoletedSessions.add(ws); 
          } 
        } 
        for (WeakReference<Session> ws : obsoletedSessions)
          this.nonPooledSessions.remove(ws); 
        Session session = this.sessionFactory.getSession(this.connUrl);
        this.nonPooledSessions.add(new WeakReference<>(session));
        return session;
      }  
    PooledXProtocol prot = null;
    List<HostInfo> hostsList = this.connUrl.getHostsList();
    synchronized (this) {
      List<PooledXProtocol> toCloseAndRemove = (List<PooledXProtocol>)this.idleProtocols.stream().filter(p -> !p.isHostInfoValid(hostsList)).collect(Collectors.toList());
      toCloseAndRemove.stream().peek(PooledXProtocol::realClose).peek(this.idleProtocols::remove).map(PooledXProtocol::getHostInfo).sequential()
        .forEach(this.demotedHosts::remove);
    } 
    long start = System.currentTimeMillis();
    while (prot == null && (this.queueTimeout == 0 || System.currentTimeMillis() < start + this.queueTimeout)) {
      synchronized (this.idleProtocols) {
        if (this.idleProtocols.peek() != null) {
          PooledXProtocol tryProt = this.idleProtocols.poll();
          if (tryProt.isOpen())
            if (tryProt.isIdleTimeoutReached()) {
              tryProt.realClose();
            } else {
              try {
                tryProt.reset();
                prot = tryProt;
              } catch (CJCommunicationsException|com.mysql.cj.protocol.x.XProtocolError cJCommunicationsException) {}
            }  
        } else if (this.idleProtocols.size() + this.activeProtocols.size() < this.maxSize) {
          CJCommunicationsException cJCommunicationsException;
          CJException latestException = null;
          List<HostInfo> hostsToRevisit = new ArrayList<>();
          for (HostInfo hi : hostsList) {
            if (this.demotedHosts.containsKey(hi))
              if (start - ((Long)this.demotedHosts.get(hi)).longValue() > this.demotedTimeout) {
                this.demotedHosts.remove(hi);
              } else {
                hostsToRevisit.add(hi);
                continue;
              }  
            try {
              prot = newPooledXProtocol(hi);
              break;
            } catch (CJCommunicationsException e) {
              if (e.getCause() == null)
                throw e; 
              cJCommunicationsException = e;
              this.demotedHosts.put(hi, Long.valueOf(System.currentTimeMillis()));
            } 
          } 
          if (prot == null)
            for (HostInfo hi : hostsToRevisit) {
              try {
                prot = newPooledXProtocol(hi);
                this.demotedHosts.remove(hi);
                break;
              } catch (CJCommunicationsException e) {
                if (e.getCause() == null)
                  throw e; 
                cJCommunicationsException = e;
                this.demotedHosts.put(hi, Long.valueOf(System.currentTimeMillis()));
              } 
            }  
          if (prot == null && cJCommunicationsException != null)
            throw (CJCommunicationsException)ExceptionFactory.createException(CJCommunicationsException.class, Messages.getString("Session.Create.Failover.0"), cJCommunicationsException); 
        } else if (this.queueTimeout > 0) {
          long currentTimeout = this.queueTimeout - System.currentTimeMillis() - start;
          try {
            if (currentTimeout > 0L)
              prot = this.idleProtocols.poll(currentTimeout, TimeUnit.MILLISECONDS); 
          } catch (InterruptedException e) {
            throw new XDevAPIError("Session can not be obtained within " + this.queueTimeout + " milliseconds.", e);
          } 
        } else {
          prot = this.idleProtocols.poll();
        } 
      } 
    } 
    if (prot == null)
      throw new XDevAPIError("Session can not be obtained within " + this.queueTimeout + " milliseconds."); 
    synchronized (this) {
      this.activeProtocols.add(new WeakReference<>(prot));
    } 
    SessionImpl sess = new SessionImpl(prot);
    return sess;
  }
  
  private PooledXProtocol newPooledXProtocol(HostInfo hi) {
    DefaultPropertySet defaultPropertySet = new DefaultPropertySet();
    defaultPropertySet.initializeProperties(hi.exposeAsProperties());
    PooledXProtocol tryProt = new PooledXProtocol(hi, (PropertySet)defaultPropertySet);
    tryProt.addListener(this);
    tryProt.connect(hi.getUser(), hi.getPassword(), hi.getDatabase());
    return tryProt;
  }
  
  public void close() {
    synchronized (this) {
      if (this.poolingEnabled) {
        if (!this.isClosed) {
          this.isClosed = true;
          this.idleProtocols.forEach(s -> s.realClose());
          this.idleProtocols.clear();
          this.activeProtocols.stream().map(Reference::get).filter(Objects::nonNull).forEach(s -> s.realClose());
          this.activeProtocols.clear();
        } 
      } else {
        this.nonPooledSessions.stream().map(Reference::get).filter(Objects::nonNull).filter(Session::isOpen).forEach(s -> s.close());
      } 
    } 
  }
  
  void idleProtocol(PooledXProtocol prot) {
    synchronized (this) {
      if (!this.isClosed) {
        List<WeakReference<PooledXProtocol>> removeThem = new ArrayList<>();
        for (WeakReference<PooledXProtocol> wps : this.activeProtocols) {
          if (wps != null) {
            PooledXProtocol as = wps.get();
            if (as == null) {
              removeThem.add(wps);
              continue;
            } 
            if (as == prot) {
              removeThem.add(wps);
              this.idleProtocols.add(as);
            } 
          } 
        } 
        for (WeakReference<PooledXProtocol> wr : removeThem)
          this.activeProtocols.remove(wr); 
      } 
    } 
  }
  
  public class PooledXProtocol extends XProtocol {
    long idleSince = -1L;
    
    HostInfo hostInfo = null;
    
    public PooledXProtocol(HostInfo hostInfo, PropertySet propertySet) {
      super(hostInfo, propertySet);
      this.hostInfo = hostInfo;
    }
    
    public void close() {
      reset();
      this.idleSince = System.currentTimeMillis();
      ClientImpl.this.idleProtocol(this);
    }
    
    public HostInfo getHostInfo() {
      return this.hostInfo;
    }
    
    boolean isIdleTimeoutReached() {
      return (ClientImpl.this.maxIdleTime > 0 && this.idleSince > 0L && System.currentTimeMillis() > this.idleSince + ClientImpl.this.maxIdleTime);
    }
    
    boolean isHostInfoValid(List<HostInfo> hostsList) {
      return hostsList.stream().filter(h -> h.equalHostPortPair(this.hostInfo)).findFirst().isPresent();
    }
    
    void realClose() {
      try {
        super.close();
      } catch (IOException iOException) {}
    }
  }
  
  public void handleEvent(Protocol.ProtocolEventListener.EventType type, Object info, Throwable reason) {
    HostInfo hi;
    switch (type) {
      case SERVER_SHUTDOWN:
        hi = ((PooledXProtocol)info).getHostInfo();
        synchronized (this) {
          List<PooledXProtocol> toCloseAndRemove = (List<PooledXProtocol>)this.idleProtocols.stream().filter(p -> p.getHostInfo().equalHostPortPair(hi)).collect(Collectors.toList());
          toCloseAndRemove.stream().peek(PooledXProtocol::realClose).peek(this.idleProtocols::remove).map(PooledXProtocol::getHostInfo).sequential()
            .forEach(this.demotedHosts::remove);
          removeActivePooledXProtocol((PooledXProtocol)info);
        } 
        break;
      case SERVER_CLOSED_SESSION:
        synchronized (this) {
          removeActivePooledXProtocol((PooledXProtocol)info);
        } 
        break;
    } 
  }
  
  private void removeActivePooledXProtocol(PooledXProtocol prot) {
    WeakReference<PooledXProtocol> wprot = null;
    for (WeakReference<PooledXProtocol> wps : this.activeProtocols) {
      if (wps != null) {
        PooledXProtocol as = wps.get();
        if (as == prot) {
          wprot = wps;
          break;
        } 
      } 
    } 
    this.activeProtocols.remove(wprot);
    prot.realClose();
  }
}
